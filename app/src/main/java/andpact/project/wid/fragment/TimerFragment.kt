package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 날짜
    var date: LocalDate = LocalDate.now()

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
//    val wiDList = remember(date) { wiDService.readDailyWiDListByDate(date) }

    // 합계
//    val totalDurationMap = remember(wiDList) { getTotalDurationMapByTitle(wiDList = wiDList) }

    // 제목
    var title by remember { mutableStateOf(titles[0]) }
    var titleMenuExpanded by remember { mutableStateOf(false) }

    // 시작 시간
    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    // 종료 시간
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    // 타이머
    var timerStarted by remember { mutableStateOf(false) }
    var timerPaused by remember { mutableStateOf(false) }
    var timerReset by remember { mutableStateOf(true) }
    var finishTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var remainingTime by remember { mutableStateOf(0L) }
    var buttonText by remember { mutableStateOf("시작") }
    val itemHeight = 30.dp
    val pickerHeight = itemHeight * 3 + (16.dp * 2) // 아이템 사이의 여백 16을 두 번 추가해줌.
    var timerTopBottomBarVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // 타이머 시간 선택
    var selectedHour by remember { mutableStateOf(0) }
    val lazyHourListState = rememberLazyListState(Int.MAX_VALUE / 2 - 16) // 정중앙의 0을 찾기 위해서 마이너스 16 해줌.
    val isHourScrollInProgress = remember { derivedStateOf { lazyHourListState.isScrollInProgress } }
    val currentHourIndex = remember { derivedStateOf { lazyHourListState.firstVisibleItemIndex } }
    val currentHourScrollOffset = remember { derivedStateOf { lazyHourListState.firstVisibleItemScrollOffset } }

    // 타이머 분 선택
    var selectedMinute by remember { mutableStateOf(0) }
    val lazyMinuteListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isMinuteScrollInProgress = remember { derivedStateOf { lazyMinuteListState.isScrollInProgress } }
    val currentMinuteIndex = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemIndex } }
    val currentMinuteScrollOffset = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemScrollOffset } }

    // 타이머 초 선택
    var selectedSecond by remember { mutableStateOf(0) }
    val lazySecondListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isSecondScrollInProgress = remember { derivedStateOf { lazySecondListState.isScrollInProgress } }
    val currentSecondIndex = remember { derivedStateOf { lazySecondListState.firstVisibleItemIndex } }
    val currentSecondScrollOffset = remember { derivedStateOf { lazySecondListState.firstVisibleItemScrollOffset } }

    fun startTimer() {
        timerStarted = true
        timerPaused = false
        timerReset = false

        date = LocalDate.now()
        start = LocalTime.now()

        buttonText = "중지"

        finishTime = System.currentTimeMillis() + remainingTime
    }

    fun finishTimer() {
        timerStarted = false
        timerPaused = true

        finish = LocalTime.now()

        buttonText = "계속"

        if (finish.isBefore(start)) {
            val midnight = LocalTime.MIDNIGHT

            val previousDate = date.minusDays(1)

            val firstWiD = WiD(
                id = 0,
                date = previousDate,
                title = title,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = Duration.between(start, midnight.plusSeconds(-1)),
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish),
            )
            wiDService.createWiD(secondWiD)
        } else {
            val newWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = start,
                finish = finish,
                duration = Duration.between(start, finish),
            )
            wiDService.createWiD(newWiD)
        }
    }

    fun resetTimer() {
        timerPaused = false
        timerReset = true

//        finishTime = 0
//        currentTime = 0

//        selectedHour = 0
//        selectedMinute = 0
//        selectedSecond = 0

        // 초기화 버튼을 누르면 0시로 초기화 해버림.
        coroutineScope.launch {
            lazyHourListState.animateScrollToItem(Int.MAX_VALUE / 2 - 16)
            lazyMinuteListState.animateScrollToItem(Int.MAX_VALUE / 2 - 4)
            lazySecondListState.animateScrollToItem(Int.MAX_VALUE / 2 - 4)
        }
        remainingTime = 0

        buttonText = "시작"
    }

    LaunchedEffect(timerStarted) {
        val today = LocalDate.now()

        // 날짜가 변경되면 갱신해줌.
        if (date != today) {
            date = today
        }

        while (timerStarted) {
            currentTime = System.currentTimeMillis() // currentTime은 1초마다 갱신 되어야 함.
            if (currentTime < finishTime) {
                delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
                remainingTime = finishTime - currentTime
            } else { // 시간이 0초가 되면 종료
                finishTimer()
                resetTimer()
            }
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true

        if (timerStarted) {
            finishTimer()
        }
    }

    // 전체 화면
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
            .clickable(enabled = timerStarted) {
                timerTopBottomBarVisible = !timerTopBottomBarVisible
            }
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = timerTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            // 상단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "타이머",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                if (timerStarted || timerPaused) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_timer_24),
                            contentDescription = "Finish Time",
                            tint = Color.Black
                        )

                        Text(formatTime(time = finishTime, "a H시 mm분 ss초"))
                    }
                }
            }
        }

        // 타이머 초기 화면
        if (timerReset) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "시간",
                        style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray)
                    )

                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "분",
                        style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray)
                    )

                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "초",
                        style = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Gray)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row {
                    // 시간 선택
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(pickerHeight),
                        state = lazyHourListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Interactive mode를 켜야 프리뷰에서 스크롤이 동작한다.
                        items(count = Int.MAX_VALUE) {index ->
                            val adjustedIndex = index % 24 // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentHourIndex.value) {
                                selectedHour = (adjustedIndex + 1) % 24 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                remainingTime = selectedHour * 3_600_000L + selectedMinute * 60_000L + selectedSecond * 1_000L
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
                                        coroutineScope.launch {
                                            lazyHourListState.animateScrollToItem(index - 1) // 아이템 클릭 시 열의 가운데로 오도록 함.
                                        }
                                    },
                                text = "$adjustedIndex",
                                style = TextStyle(fontSize = 30.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (index == currentHourIndex.value + 1) FontWeight.Bold else null,
                                    color = if (index == currentHourIndex.value + 1) Color.Black else Color.LightGray
                                )
                            )
                        }
                    }

                    // 분 선택
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(pickerHeight),
                        state = lazyMinuteListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(count = Int.MAX_VALUE) {index ->
                            val adjustedIndex = index % 60 // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentMinuteIndex.value) {
                                selectedMinute = (adjustedIndex + 1) % 60 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                remainingTime = selectedHour * 3_600_000L + selectedMinute * 60_000L + selectedSecond * 1_000L
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
                                        coroutineScope.launch {
                                            lazyMinuteListState.animateScrollToItem(index - 1) // 아이템 클릭 시 열의 가운데로 오도록 함.
                                        }
                                    },
                                text = "$adjustedIndex",
                                style = TextStyle(fontSize = 30.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (index == currentMinuteIndex.value + 1) FontWeight.Bold else null,
                                    color = if (index == currentMinuteIndex.value + 1) Color.Black else Color.LightGray
                                )
                            )
                        }
                    }

                    // 초 선택
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(pickerHeight),
                        state = lazySecondListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(count = Int.MAX_VALUE) {index ->
                            val adjustedIndex = index % 60 // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentSecondIndex.value) {
                                selectedSecond = (adjustedIndex + 1) % 60 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                remainingTime = selectedHour * 3_600_000L + selectedMinute * 60_000L + selectedSecond * 1_000L
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
                                        coroutineScope.launch {
                                            lazySecondListState.animateScrollToItem(index - 1) // 아이템 클릭 시 열의 가운데로 오도록 함.
                                        }
                                    },
                                text = "$adjustedIndex",
                                style = TextStyle(fontSize = 30.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (index == currentSecondIndex.value + 1) FontWeight.Bold else null,
                                    color = if (index == currentSecondIndex.value + 1) Color.Black else Color.LightGray
                                )
                            )
                        }
                    }

                    if (!isHourScrollInProgress.value) {
                        coroutineScope.launch {
                            if (currentHourScrollOffset.value < pickerHeight.value / 2) {
                                if (lazyHourListState.layoutInfo.totalItemsCount == 0)
                                    return@launch
                                lazyHourListState.animateScrollToItem(index = currentHourIndex.value)
                            } else {
                                lazyHourListState.animateScrollToItem(index = currentHourIndex.value + 1)
                            }
                        }
                    }

                    if (!isMinuteScrollInProgress.value) {
                        coroutineScope.launch {
                            if (currentMinuteScrollOffset.value < pickerHeight.value / 2) {
                                if (lazyMinuteListState.layoutInfo.totalItemsCount == 0)
                                    return@launch
                                lazyMinuteListState.animateScrollToItem(index = currentMinuteIndex.value)
                            } else {
                                lazyMinuteListState.animateScrollToItem(index = currentMinuteIndex.value + 1)
                            }
                        }
                    }

                    if (!isSecondScrollInProgress.value) {
                        coroutineScope.launch {
                            if (currentSecondScrollOffset.value < pickerHeight.value / 2) {
                                if (lazySecondListState.layoutInfo.totalItemsCount == 0)
                                    return@launch
                                lazySecondListState.animateScrollToItem(index = currentSecondIndex.value)
                            } else {
                                lazySecondListState.animateScrollToItem(index = currentSecondIndex.value + 1)
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            // 타이머 남은 시간 텍스트 뷰
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = formatTimerTime(time = remainingTime),
                    style = TextStyle(
                        fontSize = 50.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.wellfleet_regular))
                    )
                )

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    TextButton(
//                        onClick = {
//                            finishTime += 5 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 5m")
//                    }
//
//                    TextButton(
//                        onClick = {
//                            finishTime += 15 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 15m")
//                    }
//
//                    TextButton(
//                        onClick = {
//                            finishTime += 30 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 30m")
//                    }
//
//                    TextButton(
//                        onClick = {
//                            finishTime += 60 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 60m")
//                    }
//                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = timerTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            // 하단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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

                    ExposedDropdownMenuBox(
                        expanded = titleMenuExpanded,
                        onExpandedChange = { if (!timerStarted && timerReset) titleMenuExpanded = !titleMenuExpanded },
                    ) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor(),
                            readOnly = true,
                            value = titleMap[title] ?: "공부",
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            onValueChange = {},
                            trailingIcon = { if (!timerStarted && timerReset) ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleMenuExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier
                                .background(Color.White),
                            expanded = titleMenuExpanded,
                            onDismissRequest = { titleMenuExpanded = false }
                        ) {
                            titles.forEach { menuTitle ->
                                DropdownMenuItem(
                                    text = { Text(text = titleMap[menuTitle] ?: "공부") },
                                    onClick = {
                                        title = menuTitle
                                        titleMenuExpanded = false
                                    },
                                    trailingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(10.dp)
                                                .background(
                                                    color = colorResource(
                                                        id = colorMap[menuTitle]
                                                            ?: R.color.light_gray
                                                    )
                                                )
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(2f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timerPaused) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    resetTimer()
                                },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_refresh_16),
                                contentDescription = "Reset timer",
                                tint = Color.Black
                            )

                            Text(text = "초기화")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clickable(enabled = remainingTime != 0L) {
                                if (timerStarted) {
                                    finishTimer()
                                } else {
                                    startTimer()
                                }
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (buttonText == "중지") {
                                    R.drawable.baseline_pause_16
                                } else {
                                    R.drawable.baseline_play_arrow_16
                                }
                            ),
                            contentDescription = "Start & pause timer",
                            tint = if (remainingTime == 0L) {
                                Color.LightGray
                            } else if (buttonText == "중지") {
                                colorResource(id = R.color.orange_red)
                            } else if (buttonText == "계속") {
                                colorResource(id = R.color.lime_green)
                            } else {
                                colorResource(id = R.color.deep_sky_blue)
                            }
                        )

                        Text(text = buttonText,
                            style = TextStyle(
                                color = if (remainingTime == 0L) {
                                    Color.LightGray
                                } else if (buttonText == "중지") {
                                    colorResource(id = R.color.orange_red)
                                } else if (buttonText == "계속") {
                                    colorResource(id = R.color.lime_green)
                                } else {
                                    colorResource(id = R.color.deep_sky_blue)
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    TimerFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}