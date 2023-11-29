package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopWatchFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
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

    // 스톱워치
    var isRecording by remember { mutableStateOf(false) }
    var isRecordingStop by remember { mutableStateOf(false) }
    var isStopWatchReset by remember { mutableStateOf(true) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var buttonText by remember { mutableStateOf("시작") }
    var stopWatchTopBottomBarVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    fun startStopWatch() {
        isRecording = true
        isRecordingStop = false
        isStopWatchReset = false

        coroutineScope.launch {
            delay(3000)
            stopWatchTopBottomBarVisible = false
        }

        date = LocalDate.now()
        start = LocalTime.now()

        startTime = System.currentTimeMillis() - elapsedTime

        buttonText = "중지"
    }

    fun finishStopWatch() {
        isRecording = false
        isRecordingStop = true

        finish = LocalTime.now()
//        finish = LocalTime.now().plusHours(10).plusMinutes(33).plusSeconds(33)

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

    fun resetStopWatch() {
        isRecordingStop = false
        isStopWatchReset = true

        elapsedTime = 0

        buttonText = "시작"
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            val today = LocalDate.now()

            // 날짜가 변경되면 갱신해줌.
            if (date != today) {
                date = today
            }

            currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime

            // 12시간이 넘어가면 자동으로 WiD가 등록되도록 함.
            if (60 * 60 * 12 * 1000 <= elapsedTime) {
                finishStopWatch()
                resetStopWatch()
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true

        if (isRecording) {
            finishStopWatch()
        }
    }

    // 전체 화면
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
            .clickable(enabled = isRecording) {
                coroutineScope.launch {
                    stopWatchTopBottomBarVisible = true
                    delay(3000)
                    if (isRecording) {
                        stopWatchTopBottomBarVisible = false
                    }
                }
            }
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = stopWatchTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            // 상단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(
                    shape = RectangleShape,
                    onClick = {
                        navController.popBackStack()
                        mainTopBottomBarVisible.value = true

                        finishStopWatch()
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "스탑워치",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }

        // 스톱워치 시간 표시
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = formatStopWatchTime(elapsedTime),
            style = TextStyle(textAlign = TextAlign.End)
        )

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = stopWatchTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            // 하단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                            .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
                    )

                    ExposedDropdownMenuBox(
                        expanded = titleMenuExpanded,
                        onExpandedChange = { if (!isRecording && isStopWatchReset) titleMenuExpanded = !titleMenuExpanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .menuAnchor(),
                            readOnly = true,
                            value = titleMap[title] ?: "공부",
                            textStyle = TextStyle(textAlign = TextAlign.Center),
                            onValueChange = {},
                            trailingIcon = { if (!isRecording && isStopWatchReset) ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleMenuExpanded) },
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
                                                .background(color = colorResource(id = colorMap[menuTitle] ?: R.color.light_gray))
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    if (isRecordingStop) {
                        TextButton(
                            shape = RectangleShape,
                            onClick = { resetStopWatch() },
                        ) {
                            Text(
                                text = "초기화",
                                style = TextStyle(color = Color.Black)
                            )
                        }
                    }

                    TextButton(
                        shape = RectangleShape,
                        onClick = { if (isRecording) finishStopWatch() else startStopWatch() }
                    ) {
                        Text(
                            text = buttonText,
                            style = TextStyle(
                                color = when (buttonText) {
                                    "중지" -> colorResource(id = R.color.orange_red)
                                    "계속" -> colorResource(id = R.color.lime_green)
                                    else -> colorResource(id = R.color.deep_sky_blue)
                                },
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
fun StopWatchFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    StopWatchFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}