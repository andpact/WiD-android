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
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
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
    val now = weeklyWiDListViewModel.now.value
    val today = now.toLocalDate()
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
                    FilledIconButton(
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
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 주",
                        )
                    }

                    FilledIconButton(
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
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
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
                        Column(
                            modifier = Modifier
                                .size(240.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = MaterialTheme.shapes.extraSmall
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "NO",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Text(
                                text = "DATA",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                        Text(
                            text = "표시할 데이터가 없습니다.",
                            style = MaterialTheme.typography.bodySmall,
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
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "히스토리",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
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
                        itemsIndexed(
                            items = titleDurationMapList,
                            key = { index, _ -> "titleDurationMapListFilterChip-$index" },
                            contentType = { _, _ -> "filterChip" }
                        ) { _, itemMapType: TitleDurationMap ->
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
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
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
                shape = MaterialTheme.shapes.medium,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
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
                    OutlinedButton(
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
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "주 선택",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedIconButton(
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

                    OutlinedIconButton(
                        enabled = weekPickerMidDateOfCurrentMonth < today.withDayOfMonth(15),
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
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

                    daysOfWeek.forEachIndexed { index, day ->
                        val textColor = when (index) {
                            5 -> MaterialTheme.colorScheme.onTertiaryContainer
                            6 -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = day,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = textColor
                        )
                    }
                }

                val totalDays = ChronoUnit.DAYS.between(weekPickerStartDateOfCurrentMonth, weekPickerFinishDateOfCurrentMonth).toInt() + 1
                val daysList = List(totalDays) { weekPickerStartDateOfCurrentMonth.plusDays(it.toLong()) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp), // 대화 상자가 잘려서 높이를 지정해줌
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp * 6), // 항상 6줄 표시
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        .height(64.dp),
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
                                                    DayOfWeek.SATURDAY -> MaterialTheme.colorScheme.onTertiaryContainer
                                                    DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onErrorContainer
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            }

                                            Text(
                                                modifier = Modifier
                                                    .weight(1f),
                                                text = date.dayOfMonth.toString(),
                                                style = MaterialTheme.typography.bodyLarge,
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
    }
}