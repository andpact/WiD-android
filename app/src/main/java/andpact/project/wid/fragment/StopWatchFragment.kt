package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    var stopWatchStarted by remember { mutableStateOf(false) }
    var stopWatchPaused by remember { mutableStateOf(false) }
    var stopWatchReset by remember { mutableStateOf(true) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    
    // 화면
    var buttonText by remember { mutableStateOf("시작") }
    var stopWatchTopBottomBarVisible by remember { mutableStateOf(true) }

    fun startStopWatch() {
        stopWatchStarted = true
        stopWatchPaused = false
        stopWatchReset = false

        date = LocalDate.now()
        start = LocalTime.now()

        startTime = System.currentTimeMillis() - elapsedTime

        buttonText = "중지"
    }

    fun pauseStopWatch() {
        stopWatchStarted = false
        stopWatchPaused = true

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
        stopWatchPaused = false
        stopWatchReset = true

        elapsedTime = 0

        buttonText = "시작"
    }

    LaunchedEffect(stopWatchStarted) {
        while (stopWatchStarted) {
//            val today = LocalDate.now()
//
//            // 날짜가 변경되면 갱신해줌.
//            if (date != today) {
//                date = today
//            }

            currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime

            // 12시간이 넘어가면 자동으로 WiD가 등록되도록 함.
            if (60 * 60 * 12 * 1000 <= elapsedTime) {
                pauseStopWatch()
                resetStopWatch()
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true

        if (stopWatchStarted) {
            pauseStopWatch()
        }
    }

    // 전체 화면
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
            .clickable(enabled = stopWatchStarted) {
                stopWatchTopBottomBarVisible = !stopWatchTopBottomBarVisible
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
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "스탑워치",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }

        // 컨텐츠
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AnimatedVisibility(
                    visible = titleMenuExpanded,
                    enter = expandVertically{ 0 },
                    exit = shrinkVertically{ 0 },
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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

                // 하단 바
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(stopWatchReset) {
                                titleMenuExpanded = !titleMenuExpanded
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_title_16),
                            contentDescription = "Reset stopwatch",
                            tint = Color.Black
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

                        Text(titleMap[title] ?: "공부")

                        Icon(
                            imageVector = if (titleMenuExpanded) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = "Expand title menu",
                            tint = if (stopWatchReset) {
                                Color.Black
                            } else {
                                Color.LightGray
                            }
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (stopWatchPaused) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        resetStopWatch()
                                    },
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_refresh_16),
                                    contentDescription = "Reset stopwatch",
                                    tint = Color.Black
                                )

                                Text(text = "초기화")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clickable {
                                    if (stopWatchStarted) {
                                        pauseStopWatch()
                                    } else {
                                        startStopWatch()
                                        titleMenuExpanded = false
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
                                contentDescription = "Start & pause stopwatch",
                                tint = when (buttonText) {
                                    "중지" -> colorResource(id = R.color.orange_red)
                                    "계속" -> colorResource(id = R.color.lime_green)
                                    else -> colorResource(id = R.color.deep_sky_blue)
                                }
                            )

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
}

@Preview(showBackground = true)
@Composable
fun StopWatchFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    StopWatchFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}