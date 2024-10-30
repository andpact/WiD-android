package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.PeriodBasedPieChartFragment
import andpact.project.wid.chartView.StackedVerticalBarChartView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.WeekWiDViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Duration
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekWiDView(weekWiDViewModel: WeekWiDViewModel = hiltViewModel()) {
    val TAG = "WeekWiDView"

    // 날짜
    val today = weekWiDViewModel.today.value
    val startDate = weekWiDViewModel.startDate.value // 조회 시작 날짜
    val finishDate = weekWiDViewModel.finishDate.value // 조회 종료 날짜
    val weekPickerExpanded = weekWiDViewModel.weekPickerExpanded.value

    // WiD
    val wiDListFetched = weekWiDViewModel.wiDListFetched.value
    val wiDList = weekWiDViewModel.wiDList.value

    val titleColorMap = weekWiDViewModel.titleColorMap

    // 합계
    val totalDurationMap = weekWiDViewModel.totalDurationMap.value

    // 평균
    val averageDurationMap = weekWiDViewModel.averageDurationMap.value

    // 최고
    val minDurationMap = weekWiDViewModel.minDurationMap.value

    // 최고
    val maxDurationMap = weekWiDViewModel.maxDurationMap.value

    // WiD List 갱신용
    val date = weekWiDViewModel.date.value
    val start = weekWiDViewModel.start.value
    val finish = weekWiDViewModel.finish.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        weekWiDViewModel.setToday(newDate = today)

        weekWiDViewModel.setStartDateAndFinishDate(
            startDate = weekWiDViewModel.startDate.value,
            finishDate = weekWiDViewModel.finishDate.value
        )

        onDispose { Log.d(TAG, "disposed") }
    }

    LaunchedEffect(finish) {
        Log.d(TAG, "wid list update")

        if (start.isBefore(finish)) { // date 날짜를 조회할 때만 리스트 갱신
            if ((startDate.isBefore(date) && finishDate.isAfter(date)) || startDate.isEqual(date) || finishDate.isEqual(date)) {
                weekWiDViewModel.setStartDateAndFinishDate(
                    startDate = startDate,
                    finishDate = finishDate
                )
            }
        } else if (start.isAfter(finish)) { // date + 1 날짜 조회할 때 리스트 갱신
            if ((startDate.isBefore(date.plusDays(1)) && finishDate.isAfter(date.plusDays(1))) || startDate.isEqual(date.plusDays(1)) || finishDate.isEqual(date.plusDays(1))) {
                weekWiDViewModel.setStartDateAndFinishDate(
                    startDate = startDate,
                    finishDate = finishDate
                )
            }
        }
    }

    BackHandler(
        enabled = weekPickerExpanded,
        onBack = {
            weekWiDViewModel.setWeekPickerExpanded(false)
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
                        weekWiDViewModel.setWeekPickerExpanded(true)
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

                        weekWiDViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
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

                        weekWiDViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
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
                        StackedVerticalBarChartView(
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

                    // 파이차트
//                    item {
//                        LazyVerticalGrid(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
//                            columns = GridCells.Fixed(7)
//                        ) {
//                            items(
//                                count = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
//                            ) { index: Int ->
//                                val indexDate = startDate.plusDays(index.toLong())
//                                val filteredWiDListByDate = wiDList.filter { it.date == indexDate }
//
//                                Column(
//                                    modifier = Modifier
//                                        .weight(1f),
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                ) {
//                                    PeriodBasedPieChartFragment(
//                                        date = indexDate,
//                                        wiDList = filteredWiDListByDate
//                                    )
//
////                                    Text(
////                                        text = "${getTotalDurationPercentageFromWiDList(wiDList = filteredWiDListByDate)}%",
////                                        style = Typography.labelSmall,
////                                        color = MaterialTheme.colorScheme.primary
////                                    )
//                                }
//                            }
//                        }
//                    }

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
                                        color = MaterialTheme.colorScheme.onSurface
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
                                    text = getDurationString(duration = averageDuration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "(${ "%.1f".format(getTitlePercentageOfDay(averageDuration))}%)",
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
                                        color = MaterialTheme.colorScheme.onSurface
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
                                    text = getDurationString(duration = maxDuration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "(${"%.1f".format(getTitlePercentageOfDay(maxDuration))}%)",
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
                                        color = MaterialTheme.colorScheme.onSurface
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
                                    text = getDurationString(duration = minDuration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "(${ "%.1f".format(getTitlePercentageOfDay(minDuration))}%)",
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
                    weekWiDViewModel.setWeekPickerExpanded(false)
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
                                        weekWiDViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfWeek,
                                            finishDate = lastDayOfWeek
                                        )

                                        weekWiDViewModel.setWeekPickerExpanded(false)
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
                                        weekWiDViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfWeek,
                                            finishDate = lastDayOfWeek
                                        )

                                        weekWiDViewModel.setWeekPickerExpanded(false)
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