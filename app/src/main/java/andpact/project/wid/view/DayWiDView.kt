package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DailyWiDListPieChartView
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.DayWiDViewModel
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
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
fun DayWiDView(
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    dayWiDViewModel: DayWiDViewModel = hiltViewModel()
) {
    val TAG = "DayWiDView"

    val titleColorMap = dayWiDViewModel.titleColorMap

    // 날짜
    val today = LocalDate.now() // DayWiDView가 화면에 나타난 시점의 날, "오늘"의 기준
    val currentDate = dayWiDViewModel.currentDate.value // 조회하려는 날짜
    val showDatePicker = dayWiDViewModel.showDatePicker.value
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
    val currentTool = dayWiDViewModel.currentTool.value

    // WiD List
    val fullWiDListLoaded = dayWiDViewModel.fullWiDListLoaded.value
    val fullWiDList = dayWiDViewModel.fullWiDList.value

    // 합계
    val totalDurationMap = dayWiDViewModel.totalDurationMap.value

    // Current WiD
    val date = dayWiDViewModel.date.value
    val start = dayWiDViewModel.start.value
    val finish = dayWiDViewModel.finish.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        //  Day WiD View의 Today를 기준으로 today를 통일함.
        dayWiDViewModel.setToday(newDate = today)

        // 리스트 수정 후, 돌아 왔을 때, 갱신된 리스트를 반영하기 위함.
        dayWiDViewModel.setCurrentDate(
            today = today,
            newDate = currentDate
        )

        onDispose {
            Log.d(TAG, "disposed")

            dayWiDViewModel.stopLastNewWiDTimer()
        }
    }

    /** 위드가 실시간 갱신되는 화면만 갱신하면 됨(단일 위드 생성 -> date, 다중 위드 생성 -> date + 1) */
    LaunchedEffect(finish) { // Current WiD, finish가 갱신되고 있다는 것은 도구가 시작 상태라는 것임.
        Log.d(TAG, "LaunchedEffect: finish update")

        if ((currentDate == date && start.isBefore(finish)) || (currentDate == date.plusDays(1) && start.isAfter(finish))) {
            // currentDate가 date이거나, currentDate가 date + 1일이고, 각 조건에서 start와 finish의 관계에 맞을 때
            dayWiDViewModel.setCurrentDate(
                today = today,
                newDate = currentDate
            )
        }
    }

    BackHandler(
        enabled = showDatePicker,
        onBack = {
            dayWiDViewModel.setShowDatePicker(show = false)
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
                        dayWiDViewModel.setShowDatePicker(show = true)
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
                        dayWiDViewModel.setCurrentDate(
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
                        dayWiDViewModel.setCurrentDate(
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
                            date = dayWiDViewModel.currentDate.value,
                            title = "기록 없음",
                            start = LocalTime.MIN,
                            finish = LocalTime.MIN,
                            duration = Duration.ZERO,
                            createdBy = CurrentTool.LIST
                        )

                        dayWiDViewModel.setNewWiD(newWiD = newWiD)
                        dayWiDViewModel.setUpdatedNewWiD(updatedNewWiD = newWiD)

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
                                    dayWiDViewModel.setNewWiD(newWiD = newWiD)
                                    dayWiDViewModel.setUpdatedNewWiD(updatedNewWiD = newWiD)

                                    onEmptyWiDClicked()
                                },
                                onWiDClicked = { wiD: WiD ->
                                    dayWiDViewModel.setExistingWiD(existingWiD = wiD)
                                    dayWiDViewModel.setUpdatedWiD(updatedWiD = wiD)

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
                                /** 빈 위드, 아닌 위드 하나의 코드로 표시하기 */
                                if (wiD.id == "newWiD" || wiD.id == "lastNewWiD") { // 빈 WiD
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
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
                                                        dayWiDViewModel.setNewWiD(newWiD = wiD)
                                                        dayWiDViewModel.setUpdatedNewWiD(updatedNewWiD = wiD)

                                                        onEmptyWiDClicked()
                                                    }
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(40.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                            )

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = wiD.title,
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                Text(
                                                    text = getDurationString(wiD.duration, mode = 3),
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
                                                    dayWiDViewModel.setExistingWiD(existingWiD = wiD)
                                                    dayWiDViewModel.setUpdatedWiD(updatedWiD = wiD)

                                                    onWiDClicked() // 화면 전환 용
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
//                                            Image(
//                                                modifier = Modifier
//                                                    .fillMaxHeight()
//                                                    .aspectRatio(1f / 1f),
//                                                painter = painterResource(id = R.drawable.ic_launcher_background),
//                                                contentDescription = "앱 아이콘"
//                                            )

                                            Box(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(40.dp)
                                                    .background(
                                                        color = titleColorMap[wiD.title] ?: MaterialTheme.colorScheme.secondaryContainer,
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                            )

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = titleToKRMap[wiD.title] ?: "",
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                Text(
                                                    text = getDurationString(wiD.duration, mode = 3),
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
                                                        .padding(8.dp),
                                                    text = currentTool.name,
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
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                        .height(intrinsicSize = IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1f / 1f)
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
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

                                    Text(
                                        text = titleToKRMap[title] ?: "기록 없음",
                                        style = Typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )

                                    Text(
                                        text = getDurationString(duration = totalDuration, mode = 3),
                                        style = Typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "(${ "%.1f".format(getTitlePercentageOfDay(totalDuration))}%)",
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
                        dayWiDViewModel.setShowDatePicker(show = false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newDate = Instant
                                    .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                dayWiDViewModel.setCurrentDate(
                                    today = today,
                                    newDate = newDate
                                )
                                dayWiDViewModel.setShowDatePicker(show = false)
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
                                dayWiDViewModel.setShowDatePicker(show = false)
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
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f / 1f)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )

            Text(
                text = "공부",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Text(
                text = "3시간 30분 30초",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "(20%)",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f / 1f)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )

            Text(
                text = "공부",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Text(
                text = "3시간 30분 30초",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "(20%)",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}