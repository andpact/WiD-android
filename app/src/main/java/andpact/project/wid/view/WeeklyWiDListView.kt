package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.WeeklyWiDListStackedVerticalBarChartView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyWiDListView(weeklyWiDListViewModel: WeeklyWiDListViewModel = hiltViewModel()) {
    val TAG = "WeeklyWiDListView"

    // 날짜
    val today = weeklyWiDListViewModel.today.value
    val startDate = weeklyWiDListViewModel.startDate.value // 조회 시작 날짜
    val finishDate = weeklyWiDListViewModel.finishDate.value // 조회 종료 날짜
    val weekPickerExpanded = weeklyWiDListViewModel.weekPickerExpanded.value

    // WiD
    val wiDListFetched = weeklyWiDListViewModel.wiDListFetched.value
    val wiDList = weeklyWiDListViewModel.wiDList.value

    // 합계
    val totalDurationMap = weeklyWiDListViewModel.totalDurationMap.value

    // 평균
    val averageDurationMap = weeklyWiDListViewModel.averageDurationMap.value

    // 최고
    val minDurationMap = weeklyWiDListViewModel.minDurationMap.value

    // 최고
    val maxDurationMap = weeklyWiDListViewModel.maxDurationMap.value

    // WiD List 갱신용
    val date = weeklyWiDListViewModel.date.value
    val start = weeklyWiDListViewModel.start.value
    val finish = weeklyWiDListViewModel.finish.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        weeklyWiDListViewModel.setToday(newDate = today)

        weeklyWiDListViewModel.setStartDateAndFinishDate(
            startDate = weeklyWiDListViewModel.startDate.value,
            finishDate = weeklyWiDListViewModel.finishDate.value
        )

        onDispose { Log.d(TAG, "disposed") }
    }

    LaunchedEffect(finish) {
        Log.d(TAG, "wid list update")

        if (start.isBefore(finish)) { // date 날짜를 조회할 때만 리스트 갱신
            if ((startDate.isBefore(date) && finishDate.isAfter(date)) || startDate.isEqual(date) || finishDate.isEqual(date)) {
                weeklyWiDListViewModel.setStartDateAndFinishDate(
                    startDate = startDate,
                    finishDate = finishDate
                )
            }
        } else if (start.isAfter(finish)) { // date + 1 날짜 조회할 때 리스트 갱신
            if ((startDate.isBefore(date.plusDays(1)) && finishDate.isAfter(date.plusDays(1))) || startDate.isEqual(date.plusDays(1)) || finishDate.isEqual(date.plusDays(1))) {
                weeklyWiDListViewModel.setStartDateAndFinishDate(
                    startDate = startDate,
                    finishDate = finishDate
                )
            }
        }
    }

    BackHandler(
        enabled = weekPickerExpanded,
        onBack = {
            weeklyWiDListViewModel.setWeekPickerExpanded(false)
        }
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
                TextButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        weeklyWiDListViewModel.setWeekPickerExpanded(true)
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
                            text = getPeriodStringOfWeek(
                                firstDayOfWeek = startDate,
                                lastDayOfWeek = finishDate
                            ),
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
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
                    enabled = !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today))
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
            if (wiDListFetched && wiDList.isEmpty()) {
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
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "다음 날짜",
                    )

                    Text(
                        text = "표시할 데이터가 없습니다.",
                        style = Typography.bodyMedium,
                    )
                }
            } else if (wiDListFetched) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        HorizontalDivider(
                            thickness = 8.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                                text = "각 제목 합계",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

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
                                        .padding(horizontal = 4.dp),
                                    text = "${"%.1f".format(getDurationPercentageStringOfDay(totalDuration))}%",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "평균 기록",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "하루 단위 평균",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    averageDurationMap.onEachIndexed { index: Int, (title: String, averageDuration: Duration) ->
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
                                        text = getDurationString(duration = averageDuration),
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
                                        .padding(horizontal = 4.dp),
                                    text = "${"%.1f".format(getDurationPercentageStringOfDay(averageDuration))}%",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "최고 기록",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "하루 단위 최고",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    maxDurationMap.onEachIndexed { index: Int, (title: String, maxDuration: Duration) ->
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
                                        text = getDurationString(duration = maxDuration),
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
                                        .padding(horizontal = 4.dp),
                                    text = "${"%.1f".format(getDurationPercentageStringOfDay(maxDuration))}%",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "최저 기록",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "하루 단위 최저",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    minDurationMap.onEachIndexed { index: Int, (title: String, minDuration: Duration) ->
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
                                        text = getDurationString(duration = minDuration),
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
                                        .padding(horizontal = 4.dp),
                                    text = "${"%.1f".format(getDurationPercentageStringOfDay(minDuration))}%",
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

                            val firstDayOfWeek = getFirstDateOfWeek(today).minusWeeks(reverseIndex.toLong())
                            val lastDayOfWeek = getLastDateOfWeek(today).minusWeeks(reverseIndex.toLong())

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
                                    text = getPeriodStringOfWeek(firstDayOfWeek = firstDayOfWeek, lastDayOfWeek = lastDayOfWeek),
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