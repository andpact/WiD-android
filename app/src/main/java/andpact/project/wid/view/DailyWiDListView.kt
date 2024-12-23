package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DailyWiDListPieChartView
import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
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
    onWiDClicked: () -> Unit,
    dailyWiDListViewModel: DailyWiDListViewModel = hiltViewModel()
) {
    val TAG = "DailyWiDListView"
    val NEW_WID = dailyWiDListViewModel.NEW_WID
    val LAST_NEW_WID = dailyWiDListViewModel.LAST_NEW_WID
    val CURRENT_WID = dailyWiDListViewModel.CURRENT_WID

    // 날짜
    val today = dailyWiDListViewModel.today.value
    val now = dailyWiDListViewModel.now.value
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

//    val tmpFullWiDList = yearDateWiDListMap
//        .getOrDefault(Year.of(currentDate.year), emptyMap())
//        .getOrDefault(currentDate, emptyList())

    // 도구
//    val currentToolState = dailyWiDListViewModel.currentToolState.value

    // WiD List
    val fullWiDList = dailyWiDListViewModel.fullWiDList.value

    // 합계
    val totalDurationMap = dailyWiDListViewModel.totalDurationMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        // 리스트 수정 후, 돌아 왔을 때, 갱신된 리스트를 반영하기 위함.
        dailyWiDListViewModel.setCurrentDate(newDate = currentDate)

        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(
        enabled = showDatePicker,
        onBack = { dailyWiDListViewModel.setShowDatePicker(show = false) }
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
                            text = dailyWiDListViewModel.getDateString(currentDate),
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
                        dailyWiDListViewModel.setCurrentDate(newDate = newDate)
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
                        dailyWiDListViewModel.setCurrentDate(newDate = newDate)
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
            FloatingActionButton(
                onClick = {
                    val isToday = today == currentDate
                    val minTime = LocalTime.MIN
                    val nowOrMax = if (isToday) LocalTime.now() else LocalTime.MAX.withNano(0)

                    val newClickedWiD = WiD.default().copy(
                        id = if (isToday) LAST_NEW_WID else NEW_WID,
                        date = currentDate,
                        start = minTime, // 기록이 없는 날짜기 때문에 무조건 Min에서 시작
                        finish = nowOrMax, // 갱신되기 전에 초기화
                        duration = Duration.between(minTime, nowOrMax)
                    )

                    dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = newClickedWiD)

                    onWiDClicked()
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "새로운 기록 생성"
                )
            }
        },
        content = { contentPadding: PaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    if (fullWiDList.size == 1) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                content = {
                                    Icon(
                                        modifier = Modifier
                                            .size(48.dp),
                                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                        contentDescription = "표시할 기록이 없습니다."
                                    )

                                    Text(
                                        text = "표시할 기록이 없습니다.",
                                        style = Typography.titleLarge,
                                    )
                                }
                            )
                        }
                    } else {
                        item {
                            DailyWiDListPieChartView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                fullWiDList = fullWiDList,
//                                onNewWiDClicked = { newWiD: WiD ->
//                                    dailyWiDListViewModel.setNewWiD(newWiD = newWiD)
//                                    dailyWiDListViewModel.setUpdatedNewWiD(updatedNewWiD = newWiD)
//
//                                    onEmptyWiDClicked()
//                                },
//                                onWiDClicked = { wiD: WiD ->
//                                    dailyWiDListViewModel.setExistingWiD(existingWiD = wiD)
//                                    dailyWiDListViewModel.setUpdatedWiD(updatedWiD = wiD)
//
//                                    onWiDClicked()
//                                }
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 8.dp),
                                    text = dailyWiDListViewModel.getTimeString(time = LocalTime.MIN),
                                    fontFamily = chivoMonoBlackItalic,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        fullWiDList.forEach { wiD: WiD ->
                            val isNewWiD = wiD.id == NEW_WID || wiD.id == LAST_NEW_WID // 새 WiD 조건 변수

                            // WiD Row
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .height(56.dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .clickable(enabled = wiD.id != CURRENT_WID) {
                                                dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = wiD)
                                                onWiDClicked()
                                            }
                                            .padding(horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(MaterialTheme.shapes.medium),
                                            painter = if (isNewWiD) painterResource(id = R.drawable.image_untitled) else painterResource(id = wiD.title.smallImage),
                                            contentDescription = "제목 이미지"
                                        )

                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = wiD.title.kr,
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                if (!isNewWiD) {
                                                    Text(
                                                        modifier = Modifier
                                                            .background(
                                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                                shape = MaterialTheme.shapes.medium
                                                            )
                                                            .padding(horizontal = 8.dp),
                                                        text = wiD.createdBy.kr,
                                                        style = Typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }

                                            Text(
                                                text = dailyWiDListViewModel.getDurationString(wiD.duration),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        if (wiD.id == CURRENT_WID) {
                                            Text(
                                                modifier = Modifier
                                                    .background(
                                                        color = MaterialTheme.colorScheme.error,
                                                        shape = MaterialTheme.shapes.medium
                                                    )
                                                    .padding(horizontal = 8.dp),
                                                text = "LIVE",
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onError
                                            )
                                        } else {
                                            Icon(
                                                modifier = Modifier
                                                    .size(24.dp),
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "이 WiD로 전환하기",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .background(color = if (wiD.finish == now) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(horizontal = 8.dp),
                                        text = dailyWiDListViewModel.getTimeString(time = wiD.finish),
                                        fontFamily = chivoMonoBlackItalic,
                                        color = if (wiD.finish == now) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondaryContainer
                                    )

                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                text = "합계 기록",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        item { // 요소 사이 여백 필요 없으니 하나의 아이템으로 처리
                            totalDurationMap.onEachIndexed { index: Int, (title: Title, totalDuration: Duration) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
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

                                    Column(
                                        modifier = Modifier
                                            .weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = title.kr,
                                            style = Typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            text = dailyWiDListViewModel.getDurationString(duration = totalDuration),
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Text(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(horizontal = 8.dp),
                                        text = dailyWiDListViewModel.getDurationPercentageStringOfDay(duration = totalDuration),
                                        style = Typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (index < totalDurationMap.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .padding(start = (16 + 40 + 8).dp, end = 16.dp),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                }
            )

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

                                dailyWiDListViewModel.setCurrentDate(newDate = newDate)
                                dailyWiDListViewModel.setShowDatePicker(show = false)
                            },
                            content = {
                                Text(
                                    text = "확인",
                                    style = Typography.bodyMedium
                                )
                            }
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                dailyWiDListViewModel.setShowDatePicker(show = false)
                            },
                            content = {
                                Text(
                                    text = "취소",
                                    style = Typography.bodyMedium
                                )
                            }
                        )
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

//@Preview(showBackground = true)
//@Composable
//fun DailyWiDListPreview() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = MaterialTheme.colorScheme.surface),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Text(
//                modifier = Modifier
//                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
//                    .padding(horizontal = 8.dp),
//                text = "getTimeString(time = LocalTime.MIN)",
//                fontFamily = chivoMonoBlackItalic,
//                color = MaterialTheme.colorScheme.onSecondaryContainer
//            )
//
//            HorizontalDivider(
//                thickness = 0.5.dp,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(height = 56.dp)
//                .padding(horizontal = 16.dp)
//                .border(
//                    width = 0.5.dp,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    shape = MaterialTheme.shapes.medium
//                )
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(shape = MaterialTheme.shapes.medium),
//                painter = painterResource(id = Title.STUDY.smallImage),
//                contentDescription = "앱 아이콘"
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    Text(
//                        text = "공부",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//
//                    Text(
//                        modifier = Modifier
//                            .background(
//                                color = MaterialTheme.colorScheme.secondaryContainer,
//                                shape = MaterialTheme.shapes.medium
//                            )
//                            .padding(horizontal = 8.dp),
//                        text = CurrentTool.STOPWATCH.kr,
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                }
//
//                Text(
//                    text = "3시간 30분 30초",
//                    style = Typography.labelMedium,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//            }
//
//            Icon(
//                modifier = Modifier
//                    .size(24.dp),
//                imageVector = Icons.Default.KeyboardArrowRight,
//                contentDescription = "이 WiD로 전환하기",
//                tint = MaterialTheme.colorScheme.onSurface
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Text(
//                modifier = Modifier
//                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
//                    .padding(horizontal = 8.dp),
//                text = "getTimeString(time = LocalTime.MIN)",
//                fontFamily = chivoMonoBlackItalic,
//                color = MaterialTheme.colorScheme.onSecondaryContainer
//            )
//
//            HorizontalDivider(
//                thickness = 0.5.dp,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//        }
//    }
//}