package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DailyWiDListPieChartView
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.DailyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWiDListView(
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    dailyWiDListViewModel: DailyWiDListViewModel = hiltViewModel()
) {
    val TAG = "DailyWiDListView"

    // 날짜
    val today = LocalDate.now() // DailyWiDListView 화면에 나타난 시점의 날, "오늘"의 기준
    val currentDate = dailyWiDListViewModel.currentDate.value // 조회하려는 날짜
    val showDatePicker = dailyWiDListViewModel.showDatePicker.value
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() + (9 * 60 * 60 * 1000),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // utcTimeMillis는 KST를 반환하는 듯하고,
                // System.currentTimeMillis()가 (KST -9시간)을 반환하는 듯.
                return utcTimeMillis <= System.currentTimeMillis() + (9 * 60 * 60 * 1000)
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = LocalDate.now().year
                return year <= currentYear
            }
        }
    )

    // 도구
    val currentTool = dailyWiDListViewModel.currentTool.value

    // WiD List
    val fullWiDListLoaded = dailyWiDListViewModel.fullWiDListLoaded.value
    val fullWiDList = dailyWiDListViewModel.fullWiDList.value

    // 합계
    val totalDurationMap = dailyWiDListViewModel.totalDurationMap.value

    // Current WiD
    val date = dailyWiDListViewModel.date.value
    val start = dailyWiDListViewModel.start.value
    val finish = dailyWiDListViewModel.finish.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        //  Day WiD View의 Today를 기준으로 today를 통일함.
        dailyWiDListViewModel.setToday(newDate = today)

        // 리스트 수정 후, 돌아 왔을 때, 갱신된 리스트를 반영하기 위함.
        dailyWiDListViewModel.setCurrentDate(
            today = today,
            newDate = currentDate
        )

        onDispose {
            Log.d(TAG, "disposed")

            dailyWiDListViewModel.stopLastNewWiDTimer()
        }
    }

    /** 위드가 실시간 갱신되는 화면만 갱신하면 됨(단일 위드 생성 -> date, 다중 위드 생성 -> date + 1) */
    LaunchedEffect(finish) { // Current WiD, finish가 갱신되고 있다는 것은 도구가 시작 상태라는 것임.
        Log.d(TAG, "LaunchedEffect: finish update")

        if ((currentDate == date && start.isBefore(finish)) || (currentDate == date.plusDays(1) && start.isAfter(finish))) {
            // currentDate가 date이거나, currentDate가 date + 1일이고, 각 조건에서 start와 finish의 관계에 맞을 때
            dailyWiDListViewModel.setCurrentDate(
                today = today,
                newDate = currentDate
            )
        }
    }

    BackHandler(
        enabled = showDatePicker,
        onBack = {
            dailyWiDListViewModel.setShowDatePicker(show = false)
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // Scaffold 중첩 시 배경색을 자식 뷰도 지정해야함.
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        dailyWiDListViewModel.setShowDatePicker(show = true)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = "날짜",
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = getDateString(currentDate),
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = {
                        val newDate = currentDate.minusDays(1)
                        dailyWiDListViewModel.setCurrentDate(
                            today = today,
                            newDate = newDate
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 날짜",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        val newDate = currentDate.plusDays(1)
                        dailyWiDListViewModel.setCurrentDate(
                            today = today,
                            newDate = newDate
                        )
                    },
                    enabled = currentDate != today
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 날짜",
                    )
                }
            }
        },
        floatingActionButton = {
            if (fullWiDListLoaded && fullWiDList.isEmpty()) {
                FloatingActionButton(
                    onClick = {
                        val newWiD = WiD(
                            id = "newWiD",
                            date = dailyWiDListViewModel.currentDate.value,
                            title = "기록 없음",
                            start = LocalTime.MIN,
                            finish = LocalTime.MIN,
                            duration = Duration.ZERO,
                            createdBy = CurrentTool.LIST
                        )

                        dailyWiDListViewModel.setNewWiD(newWiD = newWiD)
                        dailyWiDListViewModel.setUpdatedNewWiD(updatedNewWiD = newWiD)

                        onEmptyWiDClicked()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새로운 WiD",
                    )
                }
            }
        },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                if (fullWiDListLoaded && fullWiDList.isEmpty()) {
                    Icon(
                        modifier = Modifier
                            .size(48.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "표시할 기록이 없습니다.",
                    )

                    Text(
                        text = "표시할 기록이 없습니다.",
                        style = Typography.titleLarge,
                    )

                    Text(
                        text = "WiD를 생성하여\n하루를 어떻게 보냈는지\n기록해 보세요.",
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                } else if (fullWiDListLoaded) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 파이 차트
                        item {
                            DailyWiDListPieChartView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                fullWiDList = fullWiDList,
                                onNewWiDClicked = { newWiD: WiD ->
                                    dailyWiDListViewModel.setNewWiD(newWiD = newWiD)
                                    dailyWiDListViewModel.setUpdatedNewWiD(updatedNewWiD = newWiD)

                                    onEmptyWiDClicked()
                                },
                                onWiDClicked = { wiD: WiD ->
                                    dailyWiDListViewModel.setExistingWiD(existingWiD = wiD)
                                    dailyWiDListViewModel.setUpdatedWiD(updatedWiD = wiD)

                                    onWiDClicked()
                                }
                            )
                        }

                        // 리스트
                        item {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = getTimeString(LocalTime.MIN, "a hh:mm:ss"),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                HorizontalDivider()
                            }

                            fullWiDList.forEach { wiD: WiD ->
                                if (wiD.id == "newWiD" || wiD.id == "lastNewWiD") { // 빈 WiD
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        /** 실제 값 생성하고, 적용 시키기 */
//                                        ElevatedCard(
//                                            modifier = Modifier
//                                                .padding(horizontal = 16.dp),
//                                            colors = CardDefaults.elevatedCardColors(
//                                                containerColor = MaterialTheme.colorScheme.surface
//                                            )
//                                        ) {
//                                        }

                                        Row(
                                            modifier = Modifier // 폭을 가득 채우지 말기.
                                                .padding(horizontal = 16.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    shape = MaterialTheme.shapes.medium
                                                )
                                                .clip(shape = MaterialTheme.shapes.medium) // 클릭 반응 자르기용
                                                .clickable(
                                                    onClick = {
                                                        dailyWiDListViewModel.setNewWiD(newWiD = wiD)
                                                        dailyWiDListViewModel.setUpdatedNewWiD(
                                                            updatedNewWiD = wiD
                                                        )

                                                        onEmptyWiDClicked()
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(40.dp),
                                                painter = painterResource(id = R.drawable.image_untitled),
                                                contentDescription = "앱 아이콘"
                                            )

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = wiD.title, // "기록 없음"
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                Text(
                                                    text = getDurationString(wiD.duration),
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                            )

                                            Icon(
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp)
                                                    .size(24.dp),
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "이 WiD로 전환하기",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            if (wiD.id == "lastNewWiD") {
                                                Text(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.errorContainer,
                                                            shape = MaterialTheme.shapes.medium
                                                        )
                                                        .padding(horizontal = 8.dp),
                                                    text = "LIVE",
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }

                                            HorizontalDivider()
                                        }
                                    }
                                } else { // 기존에 있는 위드
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    shape = MaterialTheme.shapes.medium
                                                )
                                                .clip(shape = MaterialTheme.shapes.medium)
                                                .clickable(wiD.id != "currentWiD") {
                                                    dailyWiDListViewModel.setExistingWiD(existingWiD = wiD)
                                                    dailyWiDListViewModel.setUpdatedWiD(updatedWiD = wiD)

                                                    onWiDClicked() // 화면 전환 용
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(40.dp),
                                                painter = painterResource(id = titleImageMap[wiD.title] ?: R.drawable.image_untitled),
                                                contentDescription = "앱 아이콘"
                                            )

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Row {
                                                    Text(
                                                        text = titleKRMap[wiD.title] ?: "기록 없음",
                                                        style = Typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )

                                                    Spacer(
                                                        modifier = Modifier
                                                            .width(8.dp)
                                                    )

                                                    Text(
                                                        modifier = Modifier
                                                            .background(
                                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                                shape = MaterialTheme.shapes.medium
                                                            )
                                                            .padding(horizontal = 8.dp),
                                                        text = toolKRMap[wiD.createdBy] ?: "",
                                                        style = Typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                Text(
                                                    text = getDurationString(wiD.duration),
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                            )

                                            if (wiD.id == "currentWiD") {
                                                Text(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .background(
                                                            color = MaterialTheme.colorScheme.error,
                                                            shape = CircleShape
                                                        )
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    text = toolKRMap[currentTool] ?: "",
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onError
                                                )
                                            } else {
                                                Icon(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .size(24.dp),
                                                    imageVector = Icons.Default.KeyboardArrowRight,
                                                    contentDescription = "이 WiD로 전환하기",
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            if (wiD.id == "currentWiD") {
                                                Text(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.errorContainer,
                                                            shape = MaterialTheme.shapes.medium
                                                        )
                                                        .padding(horizontal = 8.dp),
                                                    text = "LIVE",
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }

                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            HorizontalDivider(
                                thickness = 8.dp,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }

                        // 합계 기록
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "합계 기록",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                Text(
                                    text = "각 제목 합계 소요 시간",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // 합계 기록
                        totalDurationMap.onEachIndexed { index: Int, (title: String, totalDuration: Duration) ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .aspectRatio(1f / 1f)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            style = Typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier
                                            .width(8.dp)
                                    )

                                    Column {
                                        Text(
                                            modifier = Modifier
                                                .padding(top = 8.dp, bottom = 4.dp),
                                            text = titleKRMap[title] ?: "기록 없음",
                                            style = Typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            modifier = Modifier
                                                .padding(top = 4.dp, bottom = 8.dp),
                                            text = getDurationString(duration = totalDuration),
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )

                                    Text(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        text = "${"%.1f".format(getDurationPercentageStringOfDay(duration = totalDuration))}%",
                                        style = Typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }
            }

            /** 날짜 선택 대화상자 */
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        dailyWiDListViewModel.setShowDatePicker(show = false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newDate = Instant
                                    .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                dailyWiDListViewModel.setCurrentDate(
                                    today = today,
                                    newDate = newDate
                                )
                                dailyWiDListViewModel.setShowDatePicker(show = false)
                            }) {
                            Text(
                                text = "확인",
                                style = Typography.bodyMedium
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                dailyWiDListViewModel.setShowDatePicker(show = false)
                            }
                        ) {
                            Text(
                                text = "취소",
                                style = Typography.bodyMedium
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = "날짜 선택",
                            style = Typography.titleLarge
                        )

                        DatePicker(
                            state = datePickerState,
                            title = null,
                            headline = null,
                            showModeToggle = false
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DayWiDPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "오전 00:00:00",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HorizontalDivider()
            }

            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
//                elevation = CardDefaults.cardElevation(
//                    defaultElevation = 1.dp
//                ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onSurface,
//                        shape = MaterialTheme.shapes.medium
//                    )
                        .clip(shape = MaterialTheme.shapes.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp),
                        painter = painterResource(id = titleImageMap["0"] ?: R.drawable.image_untitled),
                        contentDescription = "앱 아이콘"
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row {
                            Text(
                                text = "공부",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier
                                    .width(8.dp)
                            )

                            Text(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = 8.dp),
                                text = toolKRMap[CurrentTool.STOPWATCH] ?: "",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "3시간 30분 30초",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(24.dp),
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "이 WiD로 전환하기",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "오전 00:00:00",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HorizontalDivider()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .aspectRatio(1f / 1f)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "10",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )

            Column {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp),
                    text = "공부",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 8.dp),
                    text = "3시간 30분 30초",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Text(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "20%",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}