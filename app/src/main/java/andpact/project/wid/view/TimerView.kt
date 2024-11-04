package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerView(
//    onTimerStateChanged: (ToolState) -> Unit,
    onTimerViewBarVisibleChanged: (Boolean) -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val TAG = "TimerView"

    val timerViewBarVisible = timerViewModel.timerViewBarVisible.value

    val title = timerViewModel.title.value

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

//    val timerState = timerViewModel.user.value?.currentToolState ?: CurrentToolState.STOPPED
    val currentToolState = timerViewModel.currentToolState.value
    val remainingTime = timerViewModel.remainingTime.value
    val selectedTime = timerViewModel.selectedTime.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose { Log.d(TAG, "disposed") }
    }

    LaunchedEffect(hourSelectionPagerState.currentPage, minuteSelectionPagerState.currentPage) {
        val adjustedHour = hourSelectionPagerState.currentPage % 24
        val adjustedMinute = minuteSelectionPagerState.currentPage % 60
        val newRemainingTime = Duration.ofHours(adjustedHour.toLong()) + Duration.ofMinutes(adjustedMinute.toLong())
        timerViewModel.setSelectedTime(newRemainingTime)
    }

    LaunchedEffect(timerViewBarVisible) {
        onTimerViewBarVisibleChanged(timerViewBarVisible)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    )

                    FilledTonalIconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            val prevPage = titlePagerState.currentPage - 1
                            coroutineScope.launch {
                                titlePagerState.animateScrollToPage(prevPage)
                            }
                            timerViewModel.setTitle(newTitle = "$prevPage")
                        },
                        enabled = 0 < titlePagerState.currentPage,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "이전 제목",
                        )
                    }

                    VerticalPager(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        state = titlePagerState,
                        pageSpacing = (-200).dp
                    ) { page ->
//                        val menuTitle = titleColorMap.keys.elementAt(page % titleColorMap.size)
                        val menuTitle = "${page % titleColorMap.size}"
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
                                    ) {
                                        val newPage = page % titleColorMap.size
                                        coroutineScope.launch {
                                            titlePagerState.animateScrollToPage(newPage)
                                        }
                                        timerViewModel.setTitle(newTitle = "$newPage")
                                    },
                                text = titleKRMap[menuTitle] ?: "기록 없음",
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

                    FilledTonalIconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            val nextPage = titlePagerState.currentPage + 1
                            coroutineScope.launch {
                                titlePagerState.animateScrollToPage(nextPage)
                            }
                            timerViewModel.setTitle(newTitle = "$nextPage")
                        },
                        enabled = titlePagerState.currentPage < titlePagerState.pageCount - 1,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "다음 제목",
                        )
                    }
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
                    )

                    FilledTonalIconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                hourSelectionPagerState.animateScrollToPage(hourSelectionPagerState.currentPage - 1)
                            }
                        },
                        enabled = 0 < hourSelectionPagerState.currentPage,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "타이머 -1시간",
                        )
                    }

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
                        pageSpacing = (-200).dp
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

                    FilledTonalIconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                hourSelectionPagerState.animateScrollToPage(hourSelectionPagerState.currentPage + 1)
                            }
                        },
                        enabled = hourSelectionPagerState.currentPage < hourSelectionPagerState.pageCount - 1,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "타이머 +1시간",
                        )
                    }
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
                    )

                    FilledTonalIconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                minuteSelectionPagerState.animateScrollToPage(
                                    minuteSelectionPagerState.currentPage - 1
                                )
                            }
                        },
                        enabled = 0 < minuteSelectionPagerState.currentPage,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "타이머 -1분",
                        )
                    }

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
                        pageSpacing = (-200).dp
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

                    FilledTonalIconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            coroutineScope.launch {
                                minuteSelectionPagerState.animateScrollToPage(
                                    minuteSelectionPagerState.currentPage + 1
                                )
                            }
                        },
                        enabled = minuteSelectionPagerState.currentPage < minuteSelectionPagerState.pageCount - 1,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "타이머 +1분",
                        )
                    }
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

        /** 하단 바 */
        if (currentToolState == CurrentToolState.STOPPED) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            timerViewModel.startTimer()
                        }
                    },
                    enabled = Duration.ZERO < selectedTime,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_play_arrow_24),
                            contentDescription = "타이머 시작",
                        )

                        Text(
                            text = "타이머 시작",
                            style = Typography.bodyMedium
                        )
                    }
                }
            }
        } else if (timerViewBarVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = {

                    },
                    enabled = false
                ) {
                    Image(
                        painter = painterResource(id = titleImageMap[title] ?: R.drawable.ic_launcher_background),
                        contentDescription = "앱 아이콘"
                    )
                }

                FilledTonalIconButton(
                    modifier = Modifier
                        .alpha(0f),
                    onClick = {

                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_done_24),
                        contentDescription = "현재 제목",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        timerViewModel.setTimerViewBarVisible(false)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_unfold_more_24),
                        contentDescription = "탑 바텀 바 숨기기",
                    )
                }

                FilledIconButton(
                    onClick = {
                        timerViewModel.stopTimer()
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "타이머 정지",
                    )
                }

                FilledIconButton(
                    onClick = {
                        if (currentToolState == CurrentToolState.STARTED) {
                            timerViewModel.pauseTimer()
                        } else {
                            timerViewModel.startTimer()
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (currentToolState == CurrentToolState.STARTED) {
                                R.drawable.baseline_pause_24
                            } else {
                                R.drawable.baseline_play_arrow_24
                            }
                        ),
                        contentDescription = "타이머 중지 및 시작",
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                FilledTonalIconButton(
                    onClick = {
                        timerViewModel.setTimerViewBarVisible(true)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_unfold_less_24),
                        contentDescription = "탑 바텀 바 보이기",
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}