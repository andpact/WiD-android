package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.WeeklyWiDListChartView
import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.WeeklyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
    val wiDList = weeklyWiDListViewModel.wiDList.value

    // 맵
    val currentMapType = weeklyWiDListViewModel.currentMapType.value
    val currentMap = weeklyWiDListViewModel.currentMap.value
    val titleDateCountMap = weeklyWiDListViewModel.titleDateCountMap.value
    val titleMaxDateMap = weeklyWiDListViewModel.titleMaxDateMap.value
    val titleMinDateMap = weeklyWiDListViewModel.titleMinDateMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(
        enabled = weekPickerExpanded,
        onBack = { weeklyWiDListViewModel.setWeekPickerExpanded(false) }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            weeklyWiDListViewModel.setWeekPickerExpanded(true)
                        }
                    ) {
                        Text(
                            text = weeklyWiDListViewModel.getWeekString(
                                firstDayOfWeek = startDate,
                                lastDayOfWeek = finishDate
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                actions = {
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
                    WeeklyWiDListChartView(
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
                                        weeklyWiDListViewModel.setCurrentMapType(mapType)
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
//                                        style = Typography.titleLarge,
//                                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                                    )
//                                }

                                Text(text = "${index + 1}")
                            },
                            headlineContent = {
                                Text(text = title.kr)
                            },
                            supportingContent = {
                                Text(text = weeklyWiDListViewModel.getDurationString(duration = duration))
                            },
                            trailingContent = {
                                Text(
                                    text = when (currentMapType) {
                                        TitleDurationMap.TOTAL -> weeklyWiDListViewModel.getDurationPercentageStringOfWeek(duration = duration)
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
                        style = MaterialTheme.typography.bodyLarge
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
                                    text = weeklyWiDListViewModel.getWeekString(firstDayOfWeek = firstDayOfWeek, lastDayOfWeek = lastDayOfWeek),
                                    style = MaterialTheme.typography.bodyLarge
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