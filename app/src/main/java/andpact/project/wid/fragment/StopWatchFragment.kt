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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
fun StopWatchFragment(navController: NavController) {
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

        if (stopWatchStarted) {
            pauseStopWatch()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
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
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.popBackStack()

                            if (stopWatchStarted) {
                                pauseStopWatch()
                            }
                        },
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "뒤로 가기",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "스톱 워치",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
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
            style = TextStyle(
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary
            )
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
                    .padding(16.dp) // 바깥 패딩
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 16.dp), // 안쪽 패딩
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = titleMenuExpanded,
                    enter = expandVertically{ 0 },
                    exit = shrinkVertically{ 0 },
                ) {
                    Column( // 더미
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (stopWatchReset) "사용할 제목을 선택하세요." else "선택한 제목이 이어서 사용됩니다.",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth(),
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
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            labelColor = MaterialTheme.colorScheme.primary,
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider()
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(stopWatchReset || stopWatchStarted) {
                                titleMenuExpanded = !titleMenuExpanded
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp, 15.dp)
                                .background(color = colorMap[title] ?: DarkGray)
                        )

                        Text(
                            text = titleMap[title] ?: "공부",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (!stopWatchPaused) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_unfold_more_16),
                                contentDescription = "제목 메뉴 펼치기",
                                tint = MaterialTheme.colorScheme.primary
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
                                    .clip(CircleShape)
                                    .clickable {
                                        resetStopWatch()
                                    }
                                    .background(DeepSkyBlue)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_refresh_24),
                                    contentDescription = "스톱 워치 초기화",
                                    tint = White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape) // 박스를 원형 모양으로 잘라서 모양 및 클릭 범위를 원형으로 만듬.
                                .clickable {
                                    if (stopWatchStarted) {
                                        pauseStopWatch()
                                    } else {
                                        startStopWatch()
                                        titleMenuExpanded = false
                                    }
                                }
                                .background(
                                    color = if (stopWatchReset) MaterialTheme.colorScheme.primary
                                    else if (stopWatchPaused) LimeGreen
                                    else OrangeRed
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
                                contentDescription = "스톱 워치 시작 및 중지",
                                tint = if (stopWatchReset) MaterialTheme.colorScheme.secondary else White
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
    StopWatchFragment(NavController(LocalContext.current))
}