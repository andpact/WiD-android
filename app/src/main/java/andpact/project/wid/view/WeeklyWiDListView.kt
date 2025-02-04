package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.WeeklyWiDListChartView
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.WeeklyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyWiDListView(weeklyWiDListViewModel: WeeklyWiDListViewModel = hiltViewModel()) {
    val TAG = "WeeklyWiDListView"

    // 날짜
    val today = weeklyWiDListViewModel.today.value
    val startDate = weeklyWiDListViewModel.startDate.value // 조회 시작 날짜
    val finishDate = weeklyWiDListViewModel.finishDate.value // 조회 종료 날짜
    
    val weekPickerExpanded = weeklyWiDListViewModel.weekPickerExpanded.value
    val weekPickerMidDateOfCurrentMonth = weeklyWiDListViewModel.weekPickerMidDateOfCurrentMonth.value
    val weekPickerStartDateOfCurrentMonth = weeklyWiDListViewModel.weekPickerStartDateOfCurrentMonth.value
    val weekPickerFinishDateOfCurrentMonth = weeklyWiDListViewModel.weekPickerFinishDateOfCurrentMonth.value
    val weekPickerStartDateOfCurrentWeek = weeklyWiDListViewModel.weekPickerStartDateOfCurrentWeek.value
    val weekPickerFinishDateOfCurrentWeek = weeklyWiDListViewModel.weekPickerFinishDateOfCurrentWeek.value

    // WiD
    val wiDList = weeklyWiDListViewModel.wiDList.value

    // 맵
    val titleDurationMapList = weeklyWiDListViewModel.titleDurationMapList
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

                            weeklyWiDListViewModel.setStartDateAndFinishDate(newStartDate = newStartDate, newFinishDate = newFinishDate)

                            // 대화 상자의 시작, 중간, 종료 날짜 변경
                            weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(newWeekPickerMidDateOfCurrentMonth = newStartDate.plusDays(3).withDayOfMonth(15))
                            weeklyWiDListViewModel.setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek(
                                newWeekPickerStartDateOfCurrentWeek = newStartDate,
                                newWeekPickerFinishDateOfCurrentWeek = newFinishDate
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 주",
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            val newStartDate = startDate.plusWeeks(1)
                            val newFinishDate = finishDate.plusWeeks(1)

                            weeklyWiDListViewModel.setStartDateAndFinishDate(newStartDate = newStartDate, newFinishDate = newFinishDate)

                            // 대화 상자의 시작, 중간, 종료 날짜 변경
                            weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(newWeekPickerMidDateOfCurrentMonth = newStartDate.plusDays(3).withDayOfMonth(15))
                            weeklyWiDListViewModel.setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek(
                                newWeekPickerStartDateOfCurrentWeek = newStartDate,
                                newWeekPickerFinishDateOfCurrentWeek = newFinishDate
                            )
                        },
                        enabled = !(startDate == weeklyWiDListViewModel.getFirstDateOfWeek(today) && finishDate == weeklyWiDListViewModel.getLastDateOfWeek(today))
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 주",
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
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (wiDList.isEmpty()) {
                item(
                    key = "no-data",
                    contentType = "no-data"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(100.dp),
                            painter = painterResource(id = R.drawable.baseline_more_vert_24), // TODO: 수평 점으로 변경
                            contentDescription = "no-data-icon"
                        )

                        Text(
                            text = "표시할 데이터가 없습니다.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            } else {
                item(
                    key = "history",
                    contentType = "header"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "히스토리",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                item (
                    key = "spacer",
                    contentType = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item(
                    key = "weeklyWiDListChartView",
                    contentType = "chart"
                ) {
                    WeeklyWiDListChartView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        startDate = startDate,
                        finishDate = finishDate,
                        wiDList = wiDList
                    )
                }

                item (
                    key = "spacer",
                    contentType = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item(
                    key = "titleDurationMapListFilterChipRow",
                    contentType = "lazyRow"
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(
                            items = titleDurationMapList,
                            key = { "titleDurationMapListFilterChip" },
                            contentType = { "filterChip" }
                        ) { itemMapType: TitleDurationMap ->
                            FilterChip(
                                selected = currentMapType == itemMapType,
                                onClick = {
                                    weeklyWiDListViewModel.setCurrentMapType(itemMapType)
                                },
                                label = {
                                    Text(text = itemMapType.kr)
                                }
                            )
                        }
                    }
                }

                itemsIndexed(
                    items = currentMap.toList(),
                    key = { index, _ -> "CurrentMapListItem-$index" }, // 고유한 키 생성
                    contentType = { _, _ -> "listItem" } // 콘텐츠 타입 설정
                ) { index, (title, duration) ->
                    ListItem(
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${index + 1}")
                            }
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
                }
            }
        }

        if (weekPickerExpanded) {
            DatePickerDialog(
                onDismissRequest = {
                    // 대화 상자의 날짜를 조회 날짜로 초기화
                    weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(startDate.plusDays(3).withDayOfMonth(15))
                    weeklyWiDListViewModel.setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek(
                        newWeekPickerStartDateOfCurrentWeek = startDate,
                        newWeekPickerFinishDateOfCurrentWeek = finishDate
                    )

                    weeklyWiDListViewModel.setWeekPickerExpanded(expand = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // 대화 상자의 날짜를 대화 상자의 날짜로 초기화(선택 후 변경될 수 있기 때문에)
                            weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(weekPickerStartDateOfCurrentWeek.plusDays(3).withDayOfMonth(15))

                            // 조회 날짜를 대화 상자 날짜로 초기화
                            weeklyWiDListViewModel.setStartDateAndFinishDate(
                                newStartDate = weekPickerStartDateOfCurrentWeek,
                                newFinishDate = weekPickerFinishDateOfCurrentWeek
                            )

                            weeklyWiDListViewModel.setWeekPickerExpanded(expand = false)
                        }
                    ) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            // 대화 상자의 날짜를 조회 날짜로 초기화
                            weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(startDate.plusDays(3).withDayOfMonth(15))
                            weeklyWiDListViewModel.setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek(
                                newWeekPickerStartDateOfCurrentWeek = startDate,
                                newWeekPickerFinishDateOfCurrentWeek = finishDate
                            )

                            weeklyWiDListViewModel.setWeekPickerExpanded(expand = false)
                        }
                    ) {
                        Text(text = "취소")
                    }
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "주 선택",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newWeekPickerMidDateOfCurrentMonth = weekPickerMidDateOfCurrentMonth.minusMonths(1)
                            weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(newWeekPickerMidDateOfCurrentMonth = newWeekPickerMidDateOfCurrentMonth)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Month"
                        )
                    }

                    Text(
                        text = "${weekPickerMidDateOfCurrentMonth.year}년 ${weekPickerMidDateOfCurrentMonth.monthValue}월",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    IconButton(
                        enabled = startDate.plusDays(3).month != weekPickerMidDateOfCurrentMonth.month,
                        onClick = {
                            val newWeekPickerMidDateOfCurrentMonth = weekPickerMidDateOfCurrentMonth.plusMonths(1)
                            weeklyWiDListViewModel.setWeekPickerMidDateOfCurrentMonth(newWeekPickerMidDateOfCurrentMonth = newWeekPickerMidDateOfCurrentMonth)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Month"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

                    daysOfWeek.forEachIndexed { index, day ->
                        val textColor = when (index) {
                            5 -> DeepSkyBlue
                            6 -> OrangeRed
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = day,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = textColor
                        )
                    }
                }

                val totalDays = ChronoUnit.DAYS.between(weekPickerStartDateOfCurrentMonth, weekPickerFinishDateOfCurrentMonth).toInt() + 1
                val daysList = List(totalDays) { weekPickerStartDateOfCurrentMonth.plusDays(it.toLong()) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp * 6) // 항상 6줄 표시
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    daysList.chunked(7).forEach { week: List<LocalDate> ->
                        val isStartAndFinishDate = week.contains(startDate) && week.contains(finishDate)
                        val isCurrentWeek = week.contains(weekPickerStartDateOfCurrentWeek) && week.contains(weekPickerFinishDateOfCurrentWeek)

                        val weekFirst = week.first()
                        val weekLast = week.last()

                        val weekEnabled = weekFirst <= weeklyWiDListViewModel.getFirstDateOfWeek(today) && weekLast <= weeklyWiDListViewModel.getLastDateOfWeek(today)

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentWeek) MaterialTheme.colorScheme.primary else Transparent,
                                disabledContainerColor = Transparent
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isStartAndFinishDate) MaterialTheme.colorScheme.outline else Transparent
                            ),
                            enabled = weekEnabled,
                            onClick = {
                                weeklyWiDListViewModel.setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek(
                                    newWeekPickerStartDateOfCurrentWeek = weekFirst,
                                    newWeekPickerFinishDateOfCurrentWeek = weekLast
                                )
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                week.forEach { date: LocalDate ->
                                    val textColor = if (isCurrentWeek) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else if (!weekEnabled) {
                                        MaterialTheme.colorScheme.outline
                                    } else {
                                        when (date.dayOfWeek) {
                                            DayOfWeek.SATURDAY -> DeepSkyBlue
                                            DayOfWeek.SUNDAY -> OrangeRed
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    }

                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = date.dayOfMonth.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}