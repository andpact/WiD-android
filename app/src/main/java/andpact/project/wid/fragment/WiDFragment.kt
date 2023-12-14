package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDFragment(wiDId: Long, navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val clickedWiD = wiDService.readWiDById(wiDId)

    if (clickedWiD == null) {
        Text(
            modifier = Modifier
                .fillMaxSize(),
            text = "WiD not found",
            textAlign = TextAlign.Center
        )

        return
    }

    // 화면
    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 날짜
    val today: LocalDate = LocalDate.now()
    val date by remember { mutableStateOf(clickedWiD.date) }
    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    // WiD
    val wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
    val clickedWiDIndex = wiDList.indexOf(clickedWiD)
    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(clickedWiD.title) }

    // 시작 시간
    var start by remember { mutableStateOf(clickedWiD.start) }
    val startLimit = if (0 < clickedWiDIndex) { wiDList[clickedWiDIndex - 1].finish } else { LocalTime.MIDNIGHT }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    val isStartOverlap by remember(start) { mutableStateOf(start < startLimit)}
    val isStartOverCurrentTime by remember(start) { mutableStateOf(date == today && currentTime < start) }

    // 종료 시간
    var finish by remember { mutableStateOf(clickedWiD.finish) }
    val finishLimit = if (clickedWiDIndex < wiDList.size - 1) { wiDList[clickedWiDIndex + 1].start } else if (date == today) { currentTime } else { LocalTime.MIDNIGHT.minusSeconds(1) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    val isFinishOverlap by remember(finish) { mutableStateOf(finishLimit < finish) }
    val isFinishOverCurrentTime by remember(finish) { mutableStateOf(date == today && currentTime < finish) }

    // 소요 시간
    val duration by remember(start, finish) { mutableStateOf(Duration.between(start, finish)) }
    val durationExist by remember(duration) { mutableStateOf(Duration.ZERO < duration) }

    // WiD
    val isTimeOverlap = isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime

    LaunchedEffect(isDeleteButtonPressed) {
        if (isDeleteButtonPressed) {
            delay(3000L)
            isDeleteButtonPressed = false
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true
    }

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "WiD",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier
                    .clickable(!isTimeOverlap && durationExist) {
                        wiDService.updateWiD(
                            id = wiDId,
                            date = date,
                            title = title,
                            start = start,
                            finish = finish,
                            duration = duration
                        )
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_done_16),
                    contentDescription = "Modify & complete WiD",
                    tint = if (!(!isTimeOverlap && durationExist)) {
                        Color.LightGray
                    } else {
                        colorResource(id = R.color.lime_green)
                    }
                )

                Text(
                    text = "완료",
                    style = TextStyle(
                        color = if (!(!isTimeOverlap && durationExist)) {
                            Color.LightGray
                        } else {
                            colorResource(id = R.color.lime_green)
                        }
                    )
                )
            }
        }

        // 컨텐츠
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = lazyColumnState
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_verified_24),
                        contentDescription = "선택 가능한 시간 범위"
                    )

                    Column {
                        Text(text = "선택 가능한 시간 범위")

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(
                                    time = startLimit,
                                    patten = "a hh:mm:ss"
                                ),
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )

                            Text(" ~ ")

                            Text(
                                text = formatTime(
                                    time = finishLimit,
                                    patten = "a hh:mm:ss"
                                ),
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // item 1
//            item {
//                Spacer(
//                    modifier = Modifier
//                        .height(32.dp)
//                )
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    Text(
//                        text = "선택 가능한 시간 범위",
//                        style = TextStyle(fontSize = 20.sp)
//                    )
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = formatTime(
//                                time = startLimit,
//                                patten = "a hh:mm:ss"
//                            ),
//                            style = TextStyle(fontWeight = FontWeight.Bold)
//                        )
//
//                        Text(" ~ ")
//
//                        Text(
//                            text = formatTime(
//                                time = finishLimit,
//                                patten = "a hh:mm:ss"
//                            ),
//                            style = TextStyle(fontWeight = FontWeight.Bold)
//                        )
//                    }
//                }
//            }

            // item 2
            item {
                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 3
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "Date"
                    )

                    Column {
                        Text("날짜")

                        Text(
                            text = getDayString(date),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // item 4
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 5
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            titleMenuExpanded = !titleMenuExpanded
                            if (titleMenuExpanded) {
                                coroutineScope.launch {
                                    delay(100)
                                    lazyColumnState.animateScrollToItem(3)
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_title_24),
                        contentDescription = "Title"
                    )

                    Column {
                        Text("제목")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = titleMap[title] ?: title,
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(10.dp)
                                    .background(
                                        color = colorResource(
                                            id = colorMap[title] ?: R.color.light_gray
                                        )
                                    )
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (titleMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show title menu",
                    )
                }

                AnimatedVisibility(
                    visible = titleMenuExpanded,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        titles.forEach { chipTitle ->
                            item {
                                FilterChip(
                                    selected = title == chipTitle,
                                    onClick = {
                                        title = chipTitle
                                        titleMenuExpanded = false
                                    },
                                    label = {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            text = titleMap[chipTitle] ?: chipTitle,
                                            style = TextStyle(textAlign = TextAlign.Center)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }

            // item 6
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 7
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showStartPicker = !showStartPicker
                            if (showStartPicker) {
                                coroutineScope.launch {
                                    delay(100)
                                    lazyColumnState.animateScrollToItem(5)
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_alarm_24),
                        contentDescription = "Start"
                    )

                    Column {
                        Text("시작")

                        Text(
                            text = formatTime(start, "a hh:mm:ss"),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (showStartPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show start picker",
                    )
                }

                AnimatedVisibility(
                    visible = showStartPicker,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = startTimePickerState)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showStartPicker = false }
                            ) {
                                Text(text = "취소")
                            }

                            TextButton(
                                onClick = {
                                    showStartPicker = false
                                    val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                    start = newStart
                                }
                            ) {
                                Text(text = "확인")
                            }
                        }
                    }
                }
            }

            // item 8
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 9
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showFinishPicker = !showFinishPicker
                            if (showFinishPicker) {
                                coroutineScope.launch {
                                    delay(100)
//                                    lazyColumnState.scrollToItem(8)
                                    lazyColumnState.animateScrollToItem(7) // Finish Picker가 화면에 나타나는 도중에 스크롤이 동작하면 에러가 나는 듯.
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                        contentDescription = "Finish"
                    )

                    Column {
                        Text("종료")

                        Text(
                            text = formatTime(finish, "a hh:mm:ss"),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (showFinishPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show finish picker",
                    )
                }

                AnimatedVisibility(
                    visible = showFinishPicker,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = finishTimePickerState)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showFinishPicker = false }
                            ) {
                                Text(text = "취소")
                            }

                            TextButton(
                                onClick = {
                                    showFinishPicker = false
                                    val newFinish = LocalTime.of(
                                        finishTimePickerState.hour,
                                        finishTimePickerState.minute
                                    )

                                    finish = newFinish
                                }
                            ) {
                                Text(text = "확인")
                            }
                        }
                    }
                }
            }

            // item 10
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 11
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "Duration"
                    )

                    Column {
                        Text("소요")

                        Text(
                            text = formatDuration(duration, mode = 3),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // item 12
            item {
                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                )

                TextButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .background(
                            color = colorResource(id = R.color.orange_red),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    onClick = {
                        if (isDeleteButtonPressed) {
                            wiDService.deleteWiDById(id = wiDId)
                            navController.popBackStack()
                            mainTopBottomBarVisible.value = true
                        } else {
                            isDeleteButtonPressed = true
                        }
                    },
                ) {
                    Text(
                        text = if (isDeleteButtonPressed) {
                            "삭제 확인"
                        } else {
                            "삭제"
                        },
                        style = TextStyle(color = Color.White)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )
            }
        }

        // 하단 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "임시 하단 바")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    WiDFragment(0, NavController(LocalContext.current), mainTopBottomBarVisible)
}