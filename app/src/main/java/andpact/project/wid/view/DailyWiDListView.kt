package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DailyWiDListChartView
import andpact.project.wid.model.Title
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.DailyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.*
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWiDListView(
    onWiDClicked: () -> Unit,
    dailyWiDListViewModel: DailyWiDListViewModel = hiltViewModel()
) {
    val TAG = "DailyWiDListView"

    val WID_LIST_LIMIT_PER_DAY: Int = dailyWiDListViewModel.WID_LIST_LIMIT_PER_DAY

    val NEW_WID: String = dailyWiDListViewModel.NEW_WID
    val LAST_NEW_WID: String = dailyWiDListViewModel.LAST_NEW_WID
    val CURRENT_WID: String = dailyWiDListViewModel.CURRENT_WID

    // 날짜
    val today = dailyWiDListViewModel.today.value
    val now = dailyWiDListViewModel.now.value
    val currentDate = dailyWiDListViewModel.currentDate.value // 조회하려는 날짜
    val showDatePicker = dailyWiDListViewModel.showDatePicker.value
    val dayPickerCurrentDate = dailyWiDListViewModel.dayPickerCurrentDate.value
    val dayPickerMidDateOfCurrentMonth = dailyWiDListViewModel.dayPickerMidDateOfCurrentMonth.value

    // WiD List
    val fullWiDList = dailyWiDListViewModel.fullWiDList.value
    val filteredWiDList = fullWiDList.filterNot { it.id == NEW_WID || it.id == LAST_NEW_WID }

    // 합계
    val totalDurationMap = dailyWiDListViewModel.totalDurationMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
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
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            dailyWiDListViewModel.setShowDatePicker(show = true)
                        }
                    ) {
                        Text(
                            text = dailyWiDListViewModel.getDateString(currentDate),
                            style = MaterialTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                actions = {
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
            )
        },
        floatingActionButton = {
            if (fullWiDList.size == 1) {
                FloatingActionButton(
                    onClick = {
                        val isToday = today == currentDate
                        val minTime = LocalTime.MIN
                        val nowOrMax = if (isToday) LocalTime.now() else LocalTime.MAX.withNano(0)

                        val newClickedWiD = WiD.default().copy(
                            id = if (isToday) LAST_NEW_WID else NEW_WID,
                            date = currentDate,
                            start = minTime, // 기록이 없는 날짜기 때문에 무조건 Min에서 시작
                            finish = nowOrMax, // 갱신되기 전에 최초 초기화
                            duration = Duration.between(minTime, nowOrMax)
                        )

                        dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = newClickedWiD)

                        onWiDClicked()
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새로운 기록 생성"
                    )
                }
            }
        },
    ) { contentPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (fullWiDList.size == 1) {
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
                    key = "daily-wid-list-chart-view",
                    contentType = "daily-wid-list-chart-view"
                ) {
                    DailyWiDListChartView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        fullWiDList = fullWiDList,
                        wiDListLimitPerDay = WID_LIST_LIMIT_PER_DAY.toString()
                    )
                }

                item (
                    key = "spacer",
                    contentType = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item(
                    key = "wid-list",
                    contentType = "header"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "기록 리스트",
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
                    key = "start-time",
                    contentType = "time"
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 4.dp),
                            text = dailyWiDListViewModel.getTimeString(time = LocalTime.MIN),
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            letterSpacing = (-1).sp
                        )

                        HorizontalDivider()
                    }
                }

                item (
                    key = "spacer",
                    contentType = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(
                    items = fullWiDList,
                    key = { index, _ -> "wid-$index" }, // 인덱스를 포함하여 고유 키 생성
                    contentType = { _, _ -> "wid-list-item" }
                ) { _, item ->
                    val isNewWiD = item.id == NEW_WID || item.id == LAST_NEW_WID
                    val isCurrentWiD = item.id == CURRENT_WID
                    val enableToCreateNewWiD = filteredWiDList.size <= WID_LIST_LIMIT_PER_DAY

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (isNewWiD) { // 새로운 기록
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .height(intrinsicSize = IntrinsicSize.Min),
                                onClick = {
                                    dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = item)
                                    onWiDClicked()
                                },
                                enabled = enableToCreateNewWiD
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text(
                                            text = item.title.kr,
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Text(
                                            text = dailyWiDListViewModel.getDurationString(item.duration),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "이 WiD로 전환하기",
                                    )
                                }
                            }
                        } else { // 현재 기록 또는 기존 기록
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .height(intrinsicSize = IntrinsicSize.Min),
                                onClick = {
                                    dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = item)
                                    onWiDClicked()
                                },
                                enabled = !isCurrentWiD
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
//                                    Image(
//                                        modifier = Modifier
//                                            .size(56.dp)
//                                            .clip(MaterialTheme.shapes.medium),
//                                        painter = painterResource(id = item.title.smallImage),
//                                        contentDescription = "제목 이미지"
//                                    )

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text(
                                            text = item.subTitle.kr + ", " + item.title.kr,
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Text(
                                            text = dailyWiDListViewModel.getDurationString(item.duration) + " / " + item.tool.kr,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    if (isCurrentWiD) {
                                        Text(
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.error,
                                                    shape = MaterialTheme.shapes.extraSmall
                                                )
                                                .padding(horizontal = 4.dp),
                                            text = "LIVE",
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "이 WiD로 전환하기",
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .background(color = if (item.finish == now) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 4.dp),
                            text = dailyWiDListViewModel.getTimeString(time = item.finish),
                            fontFamily = FontFamily.Monospace,
                            color = if (item.finish == now) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondaryContainer,
                            letterSpacing = (-1).sp
                        )

                        HorizontalDivider()
                    }
                }

                item (
                    key = "spacer",
                    contentType = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item(
                    key = "total-duration",
                    contentType = "header"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "합계 기록",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                item (
                    key = "spacer",
                    contentType = "spacer"
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(
                    items = totalDurationMap.toList(),
                    key = { index, _ -> "total-duration-map-$index" }, // 고유한 키를 생성
                    contentType = { _, _ -> "total-duration-map-list-item" } // 컨텐츠 타입 지정
                ) { index: Int, (title: Title, totalDuration: Duration) ->
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
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        },
                        headlineContent = {
                            Text(text = title.kr)
                        },
                        supportingContent = {
                            Text(text = dailyWiDListViewModel.getDurationString(duration = totalDuration))
                        },
                        trailingContent = {
                            Text(text = dailyWiDListViewModel.getDurationPercentageStringOfDay(duration = totalDuration))
                        }
                    )
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    dailyWiDListViewModel.setDayPickerMidDateOfCurrentMonth(newDayPickerMidDateOfCurrentMonth = currentDate.withDayOfMonth(15))
                    dailyWiDListViewModel.setDayPickerCurrentDate(newDayPickerCurrentDate = currentDate)

                    dailyWiDListViewModel.setShowDatePicker(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dailyWiDListViewModel.setDayPickerMidDateOfCurrentMonth(newDayPickerMidDateOfCurrentMonth = dayPickerCurrentDate.withDayOfMonth(15))
                            dailyWiDListViewModel.setCurrentDate(newDate = dayPickerCurrentDate)

                            dailyWiDListViewModel.setShowDatePicker(show = false)
                        }
                    ) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            dailyWiDListViewModel.setDayPickerMidDateOfCurrentMonth(newDayPickerMidDateOfCurrentMonth = currentDate.withDayOfMonth(15))
                            dailyWiDListViewModel.setDayPickerCurrentDate(newDayPickerCurrentDate = currentDate)

                            dailyWiDListViewModel.setShowDatePicker(show = false)
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
                        text = "일 선택",
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
                            val newDayPickerMidDateOfCurrentMonth = dayPickerMidDateOfCurrentMonth.minusMonths(1)
                            dailyWiDListViewModel.setDayPickerMidDateOfCurrentMonth(newDayPickerMidDateOfCurrentMonth = newDayPickerMidDateOfCurrentMonth)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Month"
                        )
                    }

                    Text(
                        text = "${dayPickerMidDateOfCurrentMonth.year}년 ${dayPickerMidDateOfCurrentMonth.monthValue}월",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    IconButton(
                        enabled = dayPickerMidDateOfCurrentMonth < today.withDayOfMonth(15),
                        onClick = {
                            val newDayPickerMidDateOfCurrentMonth = dayPickerMidDateOfCurrentMonth.plusMonths(1)
                            dailyWiDListViewModel.setDayPickerMidDateOfCurrentMonth(newDayPickerMidDateOfCurrentMonth = newDayPickerMidDateOfCurrentMonth)
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
                    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

                    daysOfWeek.forEachIndexed { index, day ->
                        val textColor = when (index) {
                            0 -> OrangeRed
                            6 -> DeepSkyBlue
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

                val firstDayOfWeek = dayPickerMidDateOfCurrentMonth.withDayOfMonth(1).dayOfWeek.value % 7
                val totalDaysInMonth = dayPickerMidDateOfCurrentMonth.lengthOfMonth()

                // `days` 리스트를 LocalDate? 타입으로 수정
                val days = buildList {
                    repeat(firstDayOfWeek) { add(null) } // 시작 요일 이전 빈 칸 (null)
                    addAll((1..totalDaysInMonth).map { day ->
                        dayPickerMidDateOfCurrentMonth.withDayOfMonth(day)
                    }) // 실제 날짜 (LocalDate)
                    repeat(42 - size) { add(null) } // 42개가 될 때까지 빈 칸 추가 (null)
                }

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    columns = GridCells.Fixed(7),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(
                        items = days,
                        key = { index, day -> "$day-$index" },
                        contentType = { _, day -> if (day != null) "date" else "empty" }
                    ) { index, day ->
                        // 날짜 유효성 확인
                        if (day != null) {
                            val isStart = currentDate == day // 조회 날짜
                            val isCurrentDate = dayPickerCurrentDate == day // 대화 상자 선택 날짜
                            val dateEnabled = day <= today // 오늘 포함 이전 날짜만 클릭 가능

                            val textColor = if (isCurrentDate) {
                                MaterialTheme.colorScheme.onPrimary
                            } else if (!dateEnabled) {
                                MaterialTheme.colorScheme.outline
                            } else {
                                when (index % 7) {
                                    0 -> OrangeRed // Sunday
                                    6 -> DeepSkyBlue // Saturday
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            }

                            OutlinedCard(
                                modifier = Modifier
                                    .aspectRatio(1f / 1f),
                                enabled = dateEnabled,
                                onClick = {
                                    dailyWiDListViewModel.setDayPickerCurrentDate(newDayPickerCurrentDate = day)
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrentDate) MaterialTheme.colorScheme.primary else Transparent,
                                    disabledContainerColor = Transparent
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isStart) MaterialTheme.colorScheme.outline else Transparent
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}