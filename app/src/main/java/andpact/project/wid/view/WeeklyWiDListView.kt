package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.WeeklyWiDListStackedVerticalBarChartView
import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.WeeklyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Duration
import java.time.Year

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyWiDListView(
    mapType: TitleDurationMap,
    weeklyWiDListViewModel: WeeklyWiDListViewModel = hiltViewModel()
) {
    val TAG = "WeeklyWiDListView"

    // 날짜
    val today = weeklyWiDListViewModel.today.value
    val startDate = weeklyWiDListViewModel.startDate.value // 조회 시작 날짜
    val finishDate = weeklyWiDListViewModel.finishDate.value // 조회 종료 날짜
    val weekPickerExpanded = weeklyWiDListViewModel.weekPickerExpanded.value

    // WiD
//    val wiDListFetched = weeklyWiDListViewModel.wiDListFetched.value
//    val wiDList = weeklyWiDListViewModel.wiDList.value
    val wiDList = weeklyWiDListViewModel.wiDList.value

    // 맵
    val currentMapType = weeklyWiDListViewModel.currentMapType.value
    val currentMap = weeklyWiDListViewModel.currentMap.value
    val titleDateCountMap = weeklyWiDListViewModel.titleDateCountMap.value
    val titleMaxDateMap = weeklyWiDListViewModel.titleMaxDateMap.value
    val titleMinDateMap = weeklyWiDListViewModel.titleMinDateMap.value

    // Current WiD
//    val firstCurrentWiD = weeklyWiDListViewModel.firstCurrentWiD.value
//    val secondCurrentWiD = weeklyWiDListViewModel.secondCurrentWiD.value

    // 조회할 맵 변경
    LaunchedEffect(mapType) {
        weeklyWiDListViewModel.setCurrentMapType(mapType)
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        weeklyWiDListViewModel.setStartDateAndFinishDate(
            startDate = weeklyWiDListViewModel.startDate.value,
            finishDate = weeklyWiDListViewModel.finishDate.value
        )

        onDispose { Log.d(TAG, "disposed") }
    }

//    LaunchedEffect(
//        key1 = firstCurrentWiD,
//        block = {
//            Log.d(TAG, "LaunchedEffect: firstCurrentWiD update")
//
//            if (firstCurrentWiD.date in startDate..finishDate) {
//                weeklyWiDListViewModel.setStartDateAndFinishDate(
//                    startDate = startDate,
//                    finishDate = finishDate
//                )
//            }
//        }
//    )
//
//    LaunchedEffect(
//        key1 = secondCurrentWiD,
//        block = {
//            Log.d(TAG, "LaunchedEffect: secondCurrentWiD update")
//
//            if (secondCurrentWiD.date in startDate..finishDate) {
//                weeklyWiDListViewModel.setStartDateAndFinishDate(
//                    startDate = startDate,
//                    finishDate = finishDate
//                )
//            }
//        }
//    )

    BackHandler(
        enabled = weekPickerExpanded,
        onBack = { weeklyWiDListViewModel.setWeekPickerExpanded(false) }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    TextButton(
                        onClick = {
                            weeklyWiDListViewModel.setWeekPickerExpanded(true)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = "날짜",
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = weeklyWiDListViewModel.getPeriodStringOfWeek(
                                firstDayOfWeek = startDate,
                                lastDayOfWeek = finishDate
                            ),
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
//                        Row(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
//                        ) {
//                        }
                    }
                }

                FilledTonalIconButton(
                    onClick = {
                        val newStartDate = startDate.minusWeeks(1)
                        val newFinishDate = finishDate.minusWeeks(1)

                        weeklyWiDListViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 날짜",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        val newStartDate = startDate.plusWeeks(1)
                        val newFinishDate = finishDate.plusWeeks(1)

                        weeklyWiDListViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                    },
                    enabled = !(startDate == weeklyWiDListViewModel.getFirstDateOfWeek(today) && finishDate == weeklyWiDListViewModel.getLastDateOfWeek(today))
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 날짜",
                    )
                }
            }
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            if (wiDList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(100.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "다음 날짜",
                    )

                    Text(
                        text = "표시할 데이터가 없습니다.",
                        style = Typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // 수직 막대 차트
                    item {
                        WeeklyWiDListStackedVerticalBarChartView(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            startDate = startDate,
                            finishDate = finishDate,
                            wiDList = wiDList
                        )
                    }

                    item {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            text = when (currentMapType) {
                                TitleDurationMap.TOTAL -> TitleDurationMap.TOTAL.kr
                                TitleDurationMap.AVERAGE -> TitleDurationMap.AVERAGE.kr
                                TitleDurationMap.MAX -> TitleDurationMap.MAX.kr
                                TitleDurationMap.MIN -> TitleDurationMap.MIN.kr
                            },
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    currentMap.onEachIndexed { index: Int, (title: Title, duration: Duration) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
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

                                Spacer(
                                    modifier = Modifier
                                        .width(8.dp)
                                )

                                Column {
                                    Text(
                                        modifier = Modifier
                                            .padding(top = 8.dp, bottom = 4.dp),
                                        text = title.kr,
                                        style = Typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(top = 4.dp, bottom = 8.dp),
                                        text = weeklyWiDListViewModel.getDurationString(duration = duration),
                                        style = Typography.titleMedium,
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
                                    text = when (currentMapType) {
                                        TitleDurationMap.TOTAL -> weeklyWiDListViewModel.getDurationPercentageStringOfWeek(duration = duration)
                                        TitleDurationMap.AVERAGE -> "총 ${titleDateCountMap[title]}일 기준"
                                        TitleDurationMap.MAX -> "${titleMaxDateMap[title]?.dayOfMonth}일"
                                        TitleDurationMap.MIN -> "${titleMinDateMap[title]?.dayOfMonth}일"
                                    },
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (index < currentMap.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(start = (16 + 40 + 8).dp, end = 16.dp),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                        )
                    }
                }
            }
        }

        if (weekPickerExpanded) {
            AlertDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                onDismissRequest = {
                    weeklyWiDListViewModel.setWeekPickerExpanded(false)
                },
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
                        text = "기간 선택",
                        style = Typography.titleLarge
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(5) { index -> // 0부터 시작
                            val reverseIndex = 4 - index // 역순 인덱스 계산

                            val firstDayOfWeek = weeklyWiDListViewModel.getFirstDateOfWeek(today).minusWeeks(reverseIndex.toLong())
                            val lastDayOfWeek = weeklyWiDListViewModel.getLastDateOfWeek(today).minusWeeks(reverseIndex.toLong())

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        weeklyWiDListViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfWeek,
                                            finishDate = lastDayOfWeek
                                        )

                                        weeklyWiDListViewModel.setWeekPickerExpanded(false)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = weeklyWiDListViewModel.getPeriodStringOfWeek(firstDayOfWeek = firstDayOfWeek, lastDayOfWeek = lastDayOfWeek),
                                    style = Typography.bodyMedium,
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                RadioButton(
                                    selected = startDate == firstDayOfWeek && finishDate == lastDayOfWeek,
                                    onClick = {
                                        weeklyWiDListViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfWeek,
                                            finishDate = lastDayOfWeek
                                        )

                                        weeklyWiDListViewModel.setWeekPickerExpanded(false)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}