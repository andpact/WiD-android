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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    // WiD
    val wiDService = WiDService(context = LocalContext.current)

    // 화면
    var stopwatchTopBottomBarVisible by remember { mutableStateOf(true) }

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    DisposableEffect(Unit) {
        // Fragment가 나타날 때
        stopwatchPlayer.setInStopwatchView(true)

        onDispose {
            // Fragment가 사라질 때
            stopwatchPlayer.setInStopwatchView(false)
        }
    }

    fun createWiD(now : LocalTime) {
        val date = stopwatchPlayer.date
        val title = stopwatchPlayer.title.value
        val start = stopwatchPlayer.start.withNano(0) // 밀리 세컨드를 0으로 만들어 정확한 소요시간을 구함.
        val finish = now.withNano(0)
        val duration = Duration.between(start, finish)

        if (duration <= Duration.ZERO) {
            return
        }

        if (finish.isBefore(start)) {
            val midnight = LocalTime.MIDNIGHT

            val previousDate = date.minusDays(1)

            val firstWiD = WiD(
                id = 0,
                date = previousDate,
                title = title,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = Duration.between(start, midnight.plusSeconds(-1))
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish)
            )
            wiDService.createWiD(secondWiD)
        } else {
            val newWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = start,
                finish = finish,
                duration = duration
            )
            wiDService.createWiD(newWiD)
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                if (stopwatchPlayer.stopwatchState.value == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                stopwatchPlayer.stopStopwatch()
                            },
                        painter = painterResource(id = R.drawable.baseline_refresh_24),
                        contentDescription = "스톱 워치 정지",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) {
                                titleMenuExpanded = true
                            },
                        painter = painterResource(titleIconMap[stopwatchPlayer.title.value] ?: R.drawable.baseline_menu_book_16),
                        contentDescription = "제목",
                        tint = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.primary else DarkGray
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape) // 박스를 원형 모양으로 잘라서 모양 및 클릭 범위를 원형으로 만듬.
                            .clickable {
                                if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                    stopwatchPlayer.pauseStopwatch()

                                    val now = LocalTime.now()

                                    createWiD(now)
                                } else {
                                    stopwatchPlayer.startStopwatch()

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
                            modifier = Modifier
                                .size(32.dp),
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

                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                titleMenuExpanded = true
                            },
                        painter = painterResource(R.drawable.baseline_keyboard_double_arrow_right_24),
                        contentDescription = "이어서",
                        tint = if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) MaterialTheme.colorScheme.primary else DarkGray
                    )
                }
            }
        }

        if (titleMenuExpanded) {
            ModalBottomSheet(
//                modifier = Modifier
//                    .navigationBarsPadding()
//                    .padding(16.dp),
//                shape = RoundedCornerShape(8.dp),
                containerColor = MaterialTheme.colorScheme.tertiary,
                onDismissRequest = { titleMenuExpanded = false },
                sheetState = bottomSheetState,
                dragHandle = null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .clickable {
                                titleMenuExpanded = false
                            },
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "제목 메뉴 닫기",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.Center),
                        text = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) "제목 선택" else "이어서 사용할 제목 선택",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                ) {
                    items(titles.size) { index ->
                        val title = titles[index]
                        val iconResourceId = titleIconMap[title] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(!(title == stopwatchPlayer.title.value && stopwatchPlayer.stopwatchState.value == PlayerState.Started)) {
                                    if (stopwatchPlayer.stopwatchState.value == PlayerState.Started && stopwatchPlayer.title.value != title) {
                                        stopwatchPlayer.restartStopwatch()

                                        val now = LocalTime.now()

                                        createWiD(now)

                                        stopwatchPlayer.date = LocalDate.now()
                                        stopwatchPlayer.start = now
                                    }

                                    stopwatchPlayer.setTitle(title)
                                    titleMenuExpanded = false
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = iconResourceId),
                                contentDescription = "제목",
                                tint = if (title == stopwatchPlayer.title.value && stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                    DarkGray
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )

                            Text(
                                text = titleMap[title] ?: "공부",
                                style = Typography.bodyMedium,
                                color = if (title == stopwatchPlayer.title.value && stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                    DarkGray
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            if (title == stopwatchPlayer.title.value && stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = "사용 중",
                                    style = Typography.bodyMedium,
                                    color = if (title == stopwatchPlayer.title.value && stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                        DarkGray
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            } else if (title == stopwatchPlayer.title.value) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = "선택됨",
                                    style = Typography.bodyMedium,
                                    color = if (title == stopwatchPlayer.title.value && stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                        DarkGray
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
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
