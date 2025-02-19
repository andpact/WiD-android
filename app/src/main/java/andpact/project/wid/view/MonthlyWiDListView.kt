package andpact.project.wid.view

import andpact.project.wid.chartView.MonthlyWiDListChartView
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.MonthlyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.Year

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyWiDListView(monthlyWiDListViewModel: MonthlyWiDListViewModel = hiltViewModel()) {
    val TAG = "MonthlyWiDListView"

    val now = monthlyWiDListViewModel.now.value
    val today = now.toLocalDate()
    val startDate = monthlyWiDListViewModel.startDate.value // 조회 시작 날짜
    val finishDate = monthlyWiDListViewModel.finishDate.value // 조회 종료 날짜

    val monthPickerExpanded = monthlyWiDListViewModel.monthPickerExpanded.value
    val monthPickerCurrentYear = monthlyWiDListViewModel.monthPickerCurrentYear.value
    val monthPickerStartDateOfCurrentMonth = monthlyWiDListViewModel.monthPickerStartDateOfCurrentMonth.value
    val monthPickerFinishDateOfCurrentMonth = monthlyWiDListViewModel.monthPickerFinishDateOfCurrentMonth.value

    val wiDList = monthlyWiDListViewModel.wiDList.value

    val titleDurationMapList = monthlyWiDListViewModel.titleDurationMapList
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
                    FilledIconButton(
                        onClick = {
                            val newStartDate = monthlyWiDListViewModel.getFirstDateOfMonth(startDate.minusDays(15))
                            val newFinishDate = monthlyWiDListViewModel.getLastDateOfMonth(finishDate.minusDays(45))

                            monthlyWiDListViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 날짜",
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            val newStartDate = monthlyWiDListViewModel.getFirstDateOfMonth(startDate.plusDays(45))
                            val newFinishDate = monthlyWiDListViewModel.getLastDateOfMonth(finishDate.plusDays(15))

                            monthlyWiDListViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
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
                    key = "monthly-wid-list-chart-view",
                    contentType = "monthly-wid-list-chart-view"
                ) {
                    MonthlyWiDListChartView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        startDate = startDate,
                        finishDate = finishDate,
                        wiDList = wiDList
                    )
                }

                item(
                    key = "title-duration-map-list-filter-chip-row",
                    contentType = "title-duration-map-list-filter-chip-row"
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
                            key = { index, _ -> "title-duration-map-list-filter-chip-$index" },
                            contentType = { _, _ -> "filterChip" }
                        ) { _, itemMapType: TitleDurationMap ->
                            FilterChip(
                                selected = currentMapType == itemMapType,
                                onClick = {
                                    monthlyWiDListViewModel.setCurrentMapType(itemMapType)
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
                    key = { index, _ -> "current-map-list-item-$index" }, // 고유한 키를 생성
                    contentType = { _, _ -> "list-item" } // 컨텐츠 타입 지정
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
                }
            }
        }

        if (monthPickerExpanded) {
            DatePickerDialog(
                shape = MaterialTheme.shapes.medium,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                onDismissRequest = {
                    // 대화 상자 날짜를 조회 날짜로 초기화
                    monthlyWiDListViewModel.setMonthPickerCurrentYear(newYear = Year.of(startDate.year))
                    monthlyWiDListViewModel.setMonthPickerStartDateOfCurrentMonthAndMonthPickerFinishDateOfCurrentMonth(
                        newMonthPickerStartDateOfCurrentMonth = startDate,
                        newMonthPickerFinishDateOfCurrentMonth = finishDate
                    )

                    monthlyWiDListViewModel.setMonthPickerExpanded(expand = false)
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            // 대화 상자 날짜를 조회 날짜로 초기화
                            monthlyWiDListViewModel.setMonthPickerCurrentYear(newYear = Year.of(startDate.year))
                            monthlyWiDListViewModel.setMonthPickerStartDateOfCurrentMonthAndMonthPickerFinishDateOfCurrentMonth(
                                newMonthPickerStartDateOfCurrentMonth = startDate,
                                newMonthPickerFinishDateOfCurrentMonth = finishDate
                            )

                            monthlyWiDListViewModel.setMonthPickerExpanded(expand = false)
                        }
                    ) {
                        Text(text = "취소")
                    }
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            // 대화 상자의 날짜를 대화 상자의 날짜로 초기화(선택 후 변경될 수 있기 때문에)
                            monthlyWiDListViewModel.setMonthPickerCurrentYear(newYear = Year.of(monthPickerStartDateOfCurrentMonth.year))

                            // 조회 날짜를 대화 상자 날짜로 초기화
                            monthlyWiDListViewModel.setStartDateAndFinishDate(
                                newStartDate = monthPickerStartDateOfCurrentMonth,
                                newFinishDate = monthPickerFinishDateOfCurrentMonth
                            )

                            monthlyWiDListViewModel.setMonthPickerExpanded(expand = false)
                        }
                    ) {
                        Text(text = "확인")
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "월 선택",
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
                            val newYear = monthPickerCurrentYear.minusYears(1)
                            monthlyWiDListViewModel.setMonthPickerCurrentYear(newYear = newYear)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Year"
                        )
                    }

                    Text(
                        text = "${monthPickerCurrentYear}년",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    OutlinedIconButton(
                        enabled = monthPickerCurrentYear.value < today.year,
                        onClick = {
                            val newYear = monthPickerCurrentYear.plusYears(1)
                            monthlyWiDListViewModel.setMonthPickerCurrentYear(newYear = newYear)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Year"
                        )
                    }
                }

                // 월 리스트
                val months = (1..12).map { month: Int ->
                    LocalDate.of(monthPickerCurrentYear.value, month, 1)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp), // 대화 상자가 잘려서 높이를 지정해줌
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = months.chunked(3), // 한 행에 3개의 월을 포함하는 청크로 나눔
                        key = { index, _ -> "row-$index" }, // 각 행의 고유 키
                        contentType = { _, quarter -> quarter } // 행의 컨텐츠 타입
                    ) { _, quarter -> // quarter는 LocalDate의 리스트 (한 행에 3개의 월)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quarter.forEach { monthDate: LocalDate ->
                                val isStartAndFinishDate = monthDate == startDate // 조회 날짜
                                val isCurrentMonth = monthDate == monthPickerStartDateOfCurrentMonth // 대화 상자 날짜

                                val monthEnabled = monthDate <= monthlyWiDListViewModel.getFirstDateOfMonth(today)

                                OutlinedCard(
                                    modifier = Modifier
                                        .weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrentMonth) MaterialTheme.colorScheme.primary else Transparent,
                                        disabledContainerColor = Transparent
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isStartAndFinishDate) MaterialTheme.colorScheme.outline else Transparent
                                    ),
                                    enabled = monthEnabled,
                                    onClick = {
                                        val firstDayOfMonth = monthDate.withDayOfMonth(1)
                                        val lastDayOfMonth = monthDate.withDayOfMonth(monthDate.lengthOfMonth())

                                        monthlyWiDListViewModel.setMonthPickerStartDateOfCurrentMonthAndMonthPickerFinishDateOfCurrentMonth(
                                            newMonthPickerStartDateOfCurrentMonth = firstDayOfMonth,
                                            newMonthPickerFinishDateOfCurrentMonth = lastDayOfMonth
                                        )
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${monthDate.monthValue}월",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            color = if (isCurrentMonth) {
                                                MaterialTheme.colorScheme.onPrimary
                                            } else if (!monthEnabled) {
                                                MaterialTheme.colorScheme.outline
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )

                                        val firstDayOfWeek = monthDate.withDayOfMonth(1).dayOfWeek.value % 7 // 0: Sunday, ..., 6: Saturday
                                        val totalDaysInMonth = monthDate.lengthOfMonth()

                                        val days = buildList {
                                            repeat(firstDayOfWeek) { add("") } // 시작 요일 전에 빈칸 추가
                                            addAll((1..totalDaysInMonth).map { it.toString() }) // 실제 날짜 추가
                                            repeat(42 - size) { add("") } // 남은 칸 빈칸으로 채우기
                                        }

                                        LazyVerticalGrid(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 700.dp),
                                            columns = GridCells.Fixed(7)
                                        ) {
                                            itemsIndexed(
                                                items = days,
                                                key = { index, day -> "$monthDate-$index-$day" }, // 고유 키
                                                contentType = { _, day -> if (day.isNotEmpty()) "date" else "empty" } // 컨텐츠 타입
                                            ) { index, day ->
                                                if (day.isNotEmpty()) {
                                                    val textColor = if (isCurrentMonth) {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    } else if (!monthEnabled) {
                                                        MaterialTheme.colorScheme.outline
                                                    } else {
                                                        when (index % 7) {
                                                            0 -> MaterialTheme.colorScheme.onErrorContainer
                                                            6 -> MaterialTheme.colorScheme.onTertiaryContainer
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .aspectRatio(1f / 1f),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = day,
                                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                            color = textColor,
                                                        )
                                                    }
                                                } else {
                                                    Spacer(modifier = Modifier.aspectRatio(1f / 1f))
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
    }
}