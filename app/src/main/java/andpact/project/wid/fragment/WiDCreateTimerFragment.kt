package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import android.widget.NumberPicker
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.IconCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDCreateTimerFragment(buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)

    // 날짜 관련 변수
    var date: LocalDate = LocalDate.now()

    // 제목 관련 변수
    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(titles[0]) }

    // 시작 및 종료 시간 관련 변수
    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    // 타이머 표시용 변수
    var isRunning by remember { mutableStateOf(false) }
    var finishTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var remainingTime by remember { mutableStateOf(0L) }

    // 버튼 상태용 변수
    var buttonText by remember { mutableStateOf("시작") }

    // Picker 관련 변수
    val itemHeight = 30.dp
    val pickerHeight = itemHeight * 3 + 16.dp * 2 // 아이템 사이의 여백 16을 두 번 추가해줌.
    val coroutineScope = rememberCoroutineScope()

    // 시간 선택 관련 변수
    var selectedHour by remember { mutableStateOf(0) }
    val lazyHourListState = rememberLazyListState(Int.MAX_VALUE / 2 - 16) // 정중앙의 0을 찾기 위해서 마이너스 16 해줌.
    val isHourScrollInProgress = remember { derivedStateOf { lazyHourListState.isScrollInProgress } }
    val currentHourIndex = remember { derivedStateOf { lazyHourListState.firstVisibleItemIndex } }
    val currentHourScrollOffset = remember { derivedStateOf { lazyHourListState.firstVisibleItemScrollOffset } }

    // 분 선택 관련 변수
    var selectedMinute by remember { mutableStateOf(0) }
    val lazyMinuteListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isMinuteScrollInProgress = remember { derivedStateOf { lazyMinuteListState.isScrollInProgress } }
    val currentMinuteIndex = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemIndex } }
    val currentMinuteScrollOffset = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemScrollOffset } }

    // 초 선택 관련 변수
    var selectedSecond by remember { mutableStateOf(0) }
    val lazySecondListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isSecondScrollInProgress = remember { derivedStateOf { lazySecondListState.isScrollInProgress } }
    val currentSecondIndex = remember { derivedStateOf { lazySecondListState.firstVisibleItemIndex } }
    val currentSecondScrollOffset = remember { derivedStateOf { lazySecondListState.firstVisibleItemScrollOffset } }

    fun startWiD() {
        isRunning = true

        date = LocalDate.now()
        start = LocalTime.now()

        buttonsVisible.value = false

        buttonText = "중지"

        finishTime = System.currentTimeMillis() + remainingTime
    }

    fun finishWiD() {
        isRunning = false

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
                detail = ""
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish),
                detail = ""
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
                detail = ""
            )
            wiDService.createWiD(newWiD)
        }
    }

    fun resetWiD() {
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

        buttonsVisible.value = true
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            currentTime = System.currentTimeMillis() // currentTime은 1초마다 갱신 되어야 함.
            if (currentTime < finishTime) {
                remainingTime = finishTime - currentTime
            } else { // 시간이 0초가 되면 종료 후 리셋시킴
                finishWiD()
                resetWiD()
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    // 전체 화면
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    ) {
        // 타이머 초기 화면
        if (!isRunning && buttonsVisible.value) {
            Column(
                modifier = Modifier
                    .padding(PaddingValues(bottom = 150.dp))
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

                HorizontalDivider()

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

                HorizontalDivider()
            }
        } else {
            // 타이머 남은 시간 텍스트 뷰
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(bottom = 140.dp))
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = formatTimerTime(time = remainingTime),
                    style = TextStyle(fontSize = 50.sp, textAlign = TextAlign.Center, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Icon(
                        modifier = Modifier
                            .scale(0.8f),
                        painter = painterResource(id = R.drawable.outline_timer_24),
                        contentDescription = "Finish Time")

                    Text(
                        text = formatTime(time = finishTime)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        finishTime += 5 * 60 * 1000
                    }) {
                        Text("+ 5m")
                    }

                    TextButton(onClick = {
                        finishTime += 15 * 60 * 1000
                    }) {
                        Text("+ 15m")
                    }

                    TextButton(onClick = {
                        finishTime += 30 * 60 * 1000
                    }) {
                        Text("+ 30m")
                    }

                    TextButton(onClick = {
                        finishTime += 60 * 60 * 1000
                    }) {
                        Text("+ 60m")
                    }
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .size(10.dp)
                .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
            )

            ExposedDropdownMenuBox(modifier = Modifier
                .weight(1f),
                expanded = titleMenuExpanded,
                onExpandedChange = { if (!isRunning && buttonsVisible.value) titleMenuExpanded = !titleMenuExpanded },
            ) {
                TextField(modifier = Modifier
                    .menuAnchor(),
                    readOnly = true,
                    value = titleMap[title] ?: "공부",
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    onValueChange = {},
                    trailingIcon = { if (!isRunning && buttonsVisible.value) ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                ExposedDropdownMenu(
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
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            TextButton(modifier = Modifier
                .weight(1f)
                .background(
                    if (!(!isRunning && !buttonsVisible.value)) Color.LightGray else Color.Black,
                    shape = RoundedCornerShape(8.dp)
                ),
                onClick = { if (!isRunning) { resetWiD() } },
                enabled = !isRunning && !buttonsVisible.value
            ) {
                Text(text = "초기화",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TextButton(modifier = Modifier
                .weight(1f)
                .background(
                    if (remainingTime == 0L) {
                        Color.LightGray
                    } else if (buttonText == "중지") {
                        colorResource(id = R.color.orange_red)
                    } else if (buttonText == "계속") {
                        colorResource(id = R.color.lime_green)
                    } else {
                        colorResource(id = R.color.deep_sky_blue)
                    }, shape = RoundedCornerShape(8.dp)
                ),
                onClick = {
                    if (!isRunning) startWiD() else finishWiD()
                },
                enabled = remainingTime != 0L
            ) {
                Text(text = buttonText,
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateTimerFragmentPreview() {
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDCreateTimerFragment(buttonsVisible = buttonsVisible)
}