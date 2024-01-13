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
import androidx.navigation.compose.rememberNavController
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopWatchFragment(navController: NavController, stopwatchPlayer: StopwatchPlayer) {
    // 날짜
    var date: LocalDate = LocalDate.now()

    // WiD
    val wiDService = WiDService(context = LocalContext.current)

    // 제목
    var title by remember { mutableStateOf(titles[0]) }
    var titleMenuExpanded by remember { mutableStateOf(false) }

    // 시작 시간
    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    // 화면
    var stopwatchTopBottomBarVisible by remember { mutableStateOf(true) }

    fun startStopwatch() {
        stopwatchPlayer.startIt()
    }

    fun restartStopwatch() {
        stopwatchPlayer.restartIt()

//        val now = LocalTime.now()
//
//        if (now.isBefore(start)) {
//            val midnight = LocalTime.MIDNIGHT
//
//            val previousDate = date.minusDays(1)
//
//            val firstWiD = WiD(
//                id = 0,
//                date = previousDate,
//                title = title,
//                start = start,
//                finish = midnight.plusSeconds(-1),
//                duration = Duration.between(start, midnight.plusSeconds(-1)),
//            )
//            wiDService.createWiD(firstWiD)
//
//            val secondWiD = WiD(
//                id = 0,
//                date = date,
//                title = title,
//                start = midnight,
//                finish = now,
//                duration = Duration.between(midnight, now),
//            )
//            wiDService.createWiD(secondWiD)
//        } else {
//            val newWiD = WiD(
//                id = 0,
//                date = date,
//                title = title,
//                start = start,
//                finish = now,
//                duration = Duration.between(start, now),
//            )
//            wiDService.createWiD(newWiD)
//        }
//
//        date = LocalDate.now()
//        start = now
    }

    fun pauseStopwatch() {
        stopwatchPlayer.pauseIt()

//        val now = LocalTime.now()
//
//        if (now.isBefore(start)) {
//            val midnight = LocalTime.MIDNIGHT
//
//            val previousDate = date.minusDays(1)
//
//            val firstWiD = WiD(
//                id = 0,
//                date = previousDate,
//                title = title,
//                start = start,
//                finish = midnight.plusSeconds(-1),
//                duration = Duration.between(start, midnight.plusSeconds(-1)),
//            )
//            wiDService.createWiD(firstWiD)
//
//            val secondWiD = WiD(
//                id = 0,
//                date = date,
//                title = title,
//                start = midnight,
//                finish = now,
//                duration = Duration.between(midnight, now),
//            )
//            wiDService.createWiD(secondWiD)
//        } else {
//            val newWiD = WiD(
//                id = 0,
//                date = date,
//                title = title,
//                start = start,
//                finish = now,
//                duration = Duration.between(start, now),
//            )
//            wiDService.createWiD(newWiD)
//        }
    }

    fun stopStopwatch() {
        stopwatchPlayer.stopIt()
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()

//        if (stopWatchStarted) {
//            pauseStopwatch()
//        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(enabled = stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                stopwatchTopBottomBarVisible = !stopwatchTopBottomBarVisible
                if (titleMenuExpanded) {
                    titleMenuExpanded = false
                }
            }
    ) {
        /**
         * 상단 바
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = stopwatchTopBottomBarVisible,
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

//                            if (stopWatchStarted) {
//                                pauseStopwatch()
//                            }
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
            text = formatStopWatchTime(stopwatchPlayer.elapsedTime.value),
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
            visible = stopwatchTopBottomBarVisible,
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
                            text = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) "사용할 제목을 선택하세요." else "선택한 제목이 이어서 사용됩니다.",
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
                                            if (stopwatchPlayer.stopwatchState.value == PlayerState.Started && title != chipTitle) {
                                                restartStopwatch()
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
                            .clickable(stopwatchPlayer.stopwatchState.value == PlayerState.Stopped || stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
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

                        if (stopwatchPlayer.stopwatchState.value != PlayerState.Paused) {
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
                        if (stopwatchPlayer.stopwatchState.value == PlayerState.Paused) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        stopStopwatch()
                                    }
                                    .background(DeepSkyBlue)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_refresh_24),
                                    contentDescription = "스톱 워치 정지",
                                    tint = White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape) // 박스를 원형 모양으로 잘라서 모양 및 클릭 범위를 원형으로 만듬.
                                .clickable {
                                    if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                        pauseStopwatch()
                                    } else {
                                        startStopwatch()
                                        titleMenuExpanded = false
                                    }
                                }
                                .background(
                                    color = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.primary
                                    else if (stopwatchPlayer.stopwatchState.value == PlayerState.Paused) LimeGreen
                                    else OrangeRed
                                )
                                .padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                        R.drawable.baseline_pause_24
                                    } else {
                                        R.drawable.baseline_play_arrow_24
                                    }
                                ),
                                contentDescription = "스톱 워치 시작 및 중지",
                                tint = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.secondary else White
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
    val dummyNavController = rememberNavController()
    val stopwatchViewModel = StopwatchPlayer()

    StopWatchFragment(navController = dummyNavController, stopwatchPlayer = stopwatchViewModel)
}
