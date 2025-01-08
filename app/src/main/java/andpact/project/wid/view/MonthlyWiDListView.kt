package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.MonthlyWiDListChartView
import andpact.project.wid.chartView.WeeklyWiDListChartView
import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.viewModel.MonthlyWiDListViewModel
import andpact.project.wid.viewModel.WeeklyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyWiDListView(monthlyWiDListViewModel: MonthlyWiDListViewModel = hiltViewModel()) {
    val TAG = "MonthlyWiDListView"

    val today = monthlyWiDListViewModel.today.value
    val startDate = monthlyWiDListViewModel.startDate.value // 조회 시작 날짜
    val finishDate = monthlyWiDListViewModel.finishDate.value // 조회 종료 날짜
    val monthPickerExpanded = monthlyWiDListViewModel.monthPickerExpanded.value

    val wiDList = monthlyWiDListViewModel.wiDList.value

    val currentMapType = monthlyWiDListViewModel.currentMapType.value
    val currentMap = monthlyWiDListViewModel.currentMap.value
    val titleDateCountMap = monthlyWiDListViewModel.titleDateCountMap.value
    val titleMaxDateMap = monthlyWiDListViewModel.titleMaxDateMap.value
    val titleMinDateMap = monthlyWiDListViewModel.titleMinDateMap.value

    BackHandler(
        enabled = monthPickerExpanded,
        onBack = { monthlyWiDListViewModel.setMonthPickerExpanded(expand = false) }
    )

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            monthlyWiDListViewModel.setMonthPickerExpanded(expand = true)
                        }
                    ) {
                        Text(
                            text = monthlyWiDListViewModel.getMonthString(firstDayOfMonth = startDate),
                            style = MaterialTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = {
                            val newStartDate = monthlyWiDListViewModel.getFirstDateOfMonth(startDate.minusDays(15))
                            val newFinishDate = monthlyWiDListViewModel.getLastDateOfMonth(finishDate.minusDays(45))

                            monthlyWiDListViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 날짜",
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            val newStartDate = monthlyWiDListViewModel.getFirstDateOfMonth(startDate.plusDays(45))
                            val newFinishDate = monthlyWiDListViewModel.getLastDateOfMonth(finishDate.plusDays(15))

                            monthlyWiDListViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                        },
                        enabled = !(startDate == monthlyWiDListViewModel.getFirstDateOfMonth(today) && finishDate == monthlyWiDListViewModel.getLastDateOfMonth(today))
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 날짜",
                        )
                    }
                }
            )
        }
    ) { contentPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
        ) {
            if (wiDList.isEmpty()) {
                item {
                    Icon(
                        modifier = Modifier
                            .size(100.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "다음 날짜",
                    )

                    Text(
                        text = "표시할 데이터가 없습니다.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                item {
                    MonthlyWiDListChartView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        startDate = startDate,
                        finishDate = finishDate,
                        wiDList = wiDList
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        TitleDurationMap.values().forEach { mapType: TitleDurationMap ->
                            item {
                                FilterChip(
                                    selected = currentMapType == mapType,
                                    onClick = {
                                        monthlyWiDListViewModel.setCurrentMapType(mapType)
                                    },
                                    label = {
                                        Text(text = mapType.kr)
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    currentMap.onEachIndexed { index: Int, (title: Title, duration: Duration) ->
                        ListItem(
                            leadingContent = {
//                                Box(
//                                    modifier = Modifier
//                                        .size(56.dp)
//                                        .background(
//                                            color = MaterialTheme.colorScheme.secondaryContainer,
//                                            shape = MaterialTheme.shapes.medium
//                                        ),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = "${index + 1}",
//                                    )
//                                }

                                Text(text = "${index + 1}")
                            },
                            headlineContent = {
                                Text(text = title.kr)
                            },
                            supportingContent = {
                                Text(text = monthlyWiDListViewModel.getDurationString(duration = duration))
                            },
                            trailingContent = {
                                Text(
                                    text = when (currentMapType) {
                                        TitleDurationMap.TOTAL -> monthlyWiDListViewModel.getDurationPercentageStringOfMonth(date = startDate, duration = duration)
                                        TitleDurationMap.AVERAGE -> "총 ${titleDateCountMap[title]}일 기준"
                                        TitleDurationMap.MAX -> "${titleMaxDateMap[title]?.dayOfMonth}일"
                                        TitleDurationMap.MIN -> "${titleMinDateMap[title]?.dayOfMonth}일"
                                    }
                                )
                            }
                        )

//                        if (index < currentMap.size - 1) {
//                            HorizontalDivider(
//                                modifier = Modifier
//                                    .padding(start = (16 + 40 + 8).dp, end = 16.dp),
//                                thickness = 0.5.dp
//                            )
//                        }
                    }
                }
            }
        }

        if (monthPickerExpanded) {
            // TODO: 월 선택 대화상자 만들기
        }
    }
}