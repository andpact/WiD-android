package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
    var stopWatchTopBottomBarVisible by remember { mutableStateOf(true) }

    fun startStopWatch() {
        stopWatchStarted = true
        stopWatchPaused = false
        stopWatchReset = false

        date = LocalDate.now()
        start = LocalTime.now()

        startTime = System.currentTimeMillis() - elapsedTime
    }

    fun restartStopWatch() {
        val now = LocalTime.now()
        finish = now

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

        date = LocalDate.now()
        start = now

        elapsedTime = 0
        startTime = System.currentTimeMillis() - elapsedTime
    }

    fun pauseStopWatch() {
        stopWatchStarted = false
        stopWatchPaused = true

        finish = LocalTime.now()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(enabled = stopWatchStarted) {
                stopWatchTopBottomBarVisible = !stopWatchTopBottomBarVisible
            }
    ) {
        /**
         * 상단 바
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = stopWatchTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
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
                    style = Typography.titleLarge
                )
            }
        }

        /**
         * 컨텐츠
         */
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = formatStopWatchTime(elapsedTime),
            style = TextStyle(textAlign = TextAlign.End)
        )

        /**
         * 하단 바
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter), // 여기에서 정렬을 설정해야 올바르게 동작함. 아래의 열이 아니라.
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
                    Column { // 더미 컬럼
                        Text(text = if (stopWatchReset) "사용할 제목을 선택하세요." else "선택한 제목이 이어서 사용됩니다.")

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
                                            if (stopWatchStarted && title != chipTitle) {
                                                restartStopWatch()
                                            }

                                            title = chipTitle
                                            titleMenuExpanded = false
                                        },
                                        label = {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                text = titleMap[chipTitle] ?: chipTitle,
                                                style = Typography.bodySmall,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = colorResource(id = R.color.light_gray),
                                            labelColor = Color.Black,
                                            selectedContainerColor = Color.Black,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = colorResource(id = R.color.light_gray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(stopWatchReset || stopWatchStarted) {
                                titleMenuExpanded = !titleMenuExpanded
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp, 10.dp)
                                .background(
                                    color = colorResource(
                                        id = colorMap[title]
                                            ?: R.color.light_gray
                                    )
                                )
                        )

                        Text(
                            text = titleMap[title] ?: "공부",
                            style = Typography.bodyMedium
                        )

                        if (!stopWatchPaused) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_unfold_more_16),
                                contentDescription = "제목 메뉴 펼치기",
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (stopWatchPaused) {
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        resetStopWatch()
                                    }
                                    .background(
                                        color = colorResource(id = R.color.deep_sky_blue),
                                        shape = CircleShape
                                    )
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_refresh_24),
                                    contentDescription = "스탑워치 초기화",
                                    tint = Color.White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clickable {
                                    if (stopWatchStarted) {
                                        pauseStopWatch()
                                    } else {
                                        startStopWatch()
                                        titleMenuExpanded = false
                                    }
                                }
                                .background(
                                    color = colorResource(id = if (stopWatchReset) R.color.deep_sky_blue else if (stopWatchPaused) R.color.lime_green else R.color.orange_red),
                                    shape = CircleShape
                                )
                                .padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (stopWatchStarted) {
                                        R.drawable.baseline_pause_24
                                    } else {
                                        R.drawable.baseline_play_arrow_24
                                    }
                                ),
                                contentDescription = "Start & pause stopwatch",
                                tint = Color.White
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