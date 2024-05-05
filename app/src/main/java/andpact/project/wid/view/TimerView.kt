package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.StopwatchViewModel
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerView(
    onTimerStateChanged: (PlayerState) -> Unit,
    onHideTimerViewBarChanged: (Boolean) -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val TAG = "TimerView"

    // 화면
    val hideTimerViewBar = timerViewModel.hideTimerViewBar.value

    // 제목
    val title = timerViewModel.title.value

    val titleColorMap = timerViewModel.titleColorMap.value

    val titlePagerState = rememberPagerState(
        initialPage = titleColorMap.size,
        pageCount = { titleColorMap.size * 2 }
    )
    val hourSelectionPagerState = rememberPagerState(
        initialPage = 24,
        pageCount = { 48 }
    )
    val minuteSelectionPagerState = rememberPagerState(
        initialPage = 60,
        pageCount = { 120 }
    )
    val coroutineScope = rememberCoroutineScope()


    val timerState = timerViewModel.timerState.value
    val remainingTime = timerViewModel.remainingTime.value
    val selectedTime = timerViewModel.selectedTime.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    LaunchedEffect(hourSelectionPagerState.currentPage, minuteSelectionPagerState.currentPage) {
        val adjustedHour = hourSelectionPagerState.currentPage % 24
        val adjustedMinute = minuteSelectionPagerState.currentPage % 60
        val newRemainingTime = Duration.ofHours(adjustedHour.toLong()) + Duration.ofMinutes(adjustedMinute.toLong())
        timerViewModel.setSelectedTime(newRemainingTime)
    }

    LaunchedEffect(titlePagerState.currentPage) {
        // 페이저 안에 선언하니까, 아래 메서드가 반복적으로 실행됨.
        timerViewModel.setTitle(newTitle = titleColorMap.keys.elementAt(titlePagerState.currentPage % titleColorMap.size))
    }

    LaunchedEffect(timerState) {
        onTimerStateChanged(timerState)
    }

    LaunchedEffect(hideTimerViewBar) {
        onHideTimerViewBarChanged(hideTimerViewBar)
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
//    BackHandler(enabled = titleMenuExpanded) {
//        titleMenuExpanded = false
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(
                enabled = timerState == PlayerState.Started,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { // 스톱 워치가 시작된 상태에서만 상, 하단 바 숨길 수 있도록
                timerViewModel.setHideTimerViewBar(!hideTimerViewBar)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (timerState == PlayerState.Stopped) { // 스톱 워치 정지 상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(16.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "제목",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = 0 < titlePagerState.currentPage,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    titlePagerState.animateScrollToPage(titlePagerState.currentPage - 1)
                                }
                            }
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (0 < titlePagerState.currentPage) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    DarkGray
                                }
                            ),
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "이전 제목",
                        tint = White
                    )

                    VerticalPager(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        state = titlePagerState,
                        pageSpacing = (-260).dp
                    ) { page ->
                        val menuTitle = titleColorMap.keys.elementAt(page % titleColorMap.size)
                        val color = titleColorMap[menuTitle] ?: Transparent

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (titlePagerState.currentPage == page && !titlePagerState.isScrollInProgress) {
                                            color
                                        } else {
                                            Transparent
                                        }
                                    )
                                    .padding(vertical = 16.dp)
                                    .clickable(
                                        enabled = !titlePagerState.isScrollInProgress,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            titlePagerState.animateScrollToPage(page)
                                        }
                                    },
                                text = menuTitle,
                                style = if (titlePagerState.currentPage == page && !titlePagerState.isScrollInProgress) {
                                    Typography.titleLarge
                                } else {
                                    Typography.bodyMedium
                                },
                                color = titleColorMap[menuTitle]?.let { textColor ->
                                    if (titlePagerState.isScrollInProgress) {
                                        MaterialTheme.colorScheme.primary
                                    } else if (titlePagerState.currentPage == page) {
                                        if (textColor.luminance() > 0.5f) {
                                            Black
                                        } else {
                                            White
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }

                                } ?: MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Icon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = titlePagerState.currentPage < titlePagerState.pageCount - 1,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    titlePagerState.animateScrollToPage(titlePagerState.currentPage + 1)
                                }
                            }
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (titlePagerState.currentPage < titlePagerState.pageCount - 1) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    DarkGray
                                }
                            ),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "다음 제목",
                        tint = White
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "시간",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = 0 < hourSelectionPagerState.currentPage,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    hourSelectionPagerState.animateScrollToPage(
                                        hourSelectionPagerState.currentPage - 1
                                    )
                                }
                            }
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (0 < hourSelectionPagerState.currentPage) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    DarkGray
                                }
                            ),
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "타이머 -1시간",
                        tint = White
                    )

                    VerticalPager(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        state = hourSelectionPagerState,
                        pageSpacing = (-260).dp
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (hourSelectionPagerState.currentPage == page && !hourSelectionPagerState.isScrollInProgress) {
                                            LimeGreen.copy(alpha = 0.2f)
                                        } else {
                                            Transparent
                                        }
                                    )
                                    .padding(vertical = 16.dp)
                                    .clickable(
                                        enabled = !hourSelectionPagerState.isScrollInProgress,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            hourSelectionPagerState.animateScrollToPage(page)
                                        }
                                    },
                                text = (page % 24).toString(),
                                style = if (hourSelectionPagerState.currentPage == page && !hourSelectionPagerState.isScrollInProgress) {
                                    Typography.titleLarge
                                } else {
                                    Typography.bodyMedium
                                },
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Icon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = hourSelectionPagerState.currentPage < hourSelectionPagerState.pageCount - 1,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    hourSelectionPagerState.animateScrollToPage(
                                        hourSelectionPagerState.currentPage + 1
                                    )
                                }
                            }
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (hourSelectionPagerState.currentPage < hourSelectionPagerState.pageCount - 1) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    DarkGray
                                }
                            ),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "타이머 +1시간",
                        tint = White
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "분",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = 0 < minuteSelectionPagerState.currentPage,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    minuteSelectionPagerState.animateScrollToPage(
                                        minuteSelectionPagerState.currentPage - 1
                                    )
                                }
                            }
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (0 < minuteSelectionPagerState.currentPage) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    DarkGray
                                }
                            ),
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "타이머 -1분",
                        tint = White
                    )

                    VerticalPager(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        state = minuteSelectionPagerState,
                        pageSpacing = (-260).dp
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (minuteSelectionPagerState.currentPage == page && !minuteSelectionPagerState.isScrollInProgress) {
                                            DeepSkyBlue.copy(alpha = 0.2f)
                                        } else {
                                            Transparent
                                        }
                                    )
                                    .padding(vertical = 16.dp)
                                    .clickable(
                                        enabled = !minuteSelectionPagerState.isScrollInProgress,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            minuteSelectionPagerState.animateScrollToPage(page)
                                        }
                                    },
                                text = (page % 60).toString(),
                                style = if (minuteSelectionPagerState.currentPage == page && !minuteSelectionPagerState.isScrollInProgress) {
                                    Typography.titleLarge
                                } else {
                                    Typography.bodyMedium
                                },
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Icon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = minuteSelectionPagerState.currentPage < minuteSelectionPagerState.pageCount - 1,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    minuteSelectionPagerState.animateScrollToPage(
                                        minuteSelectionPagerState.currentPage + 1
                                    )
                                }
                            }
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (minuteSelectionPagerState.currentPage < minuteSelectionPagerState.pageCount - 1) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    DarkGray
                                }
                            ),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "타이머 +1분",
                        tint = White
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getTimerTimeString(duration = remainingTime),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .alpha(
                    if (hideTimerViewBar) {
                        0f
                    } else {
                        1f
                    }
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (timerState == PlayerState.Stopped) { // 스톱 워치 정지 상태
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            enabled = Duration.ZERO < selectedTime,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            timerViewModel.startTimer()
                        }
                        .background(
                            color = if (Duration.ZERO < selectedTime) {
                                LimeGreen
                            } else {
                                DarkGray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp),
                        painter = painterResource(R.drawable.baseline_play_arrow_24),
                        contentDescription = "타이머 시작",
                        tint = White
                    )
                }
            } else { // 스톰 워치 시작 및 정지 상태
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(titleColorMap[title] ?: MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    text = title,
                    style = Typography.titleLarge,
                    color = titleColorMap[title]?.let { textColor ->
                        if (textColor.luminance() > 0.5f) {
                            Black
                        } else {
                            White
                        }
                    } ?: MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (timerState == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
//                                enabled = timerState == PlayerState.Paused,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                timerViewModel.stopTimer()
                            }
                            .background(color = DeepSkyBlue)
                            .padding(16.dp)
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "타이머 정지",
                        tint = White
                    )
                }

                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (hideTimerViewBar) {
                                timerViewModel.setHideTimerViewBar(hideTimerViewBar = false)
                            } else if (timerState == PlayerState.Started) {
                                timerViewModel.pauseTimer()
                            } else {
                                timerViewModel.startTimer()
                            }
                        }
                        .background(
                            color = if (timerState == PlayerState.Started) OrangeRed
                            else LimeGreen
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(
                        id = if (timerState == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "타이머 중지 및 시작",
                    tint = White
                )
            }
        }
    }
}