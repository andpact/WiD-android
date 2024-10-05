package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.StopwatchViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StopwatchView(
//    onStopwatchStateChanged: (ToolState) -> Unit,
    onStopwatchViewBarVisibleChanged: (Boolean) -> Unit,
    stopwatchViewModel: StopwatchViewModel = hiltViewModel(),
) {
    val TAG = "StopwatchView"

    val stopwatchViewBarVisible = stopwatchViewModel.stopwatchViewBarVisible.value

    val title = stopwatchViewModel.title.value
    val titleColorMap = stopwatchViewModel.titleColorMap

    val pagerState = rememberPagerState(pageCount = { titleColorMap.size })
    val coroutineScope = rememberCoroutineScope()

    val stopwatchToolState = stopwatchViewModel.user.value?.currentToolState ?: CurrentToolState.STOPPED
    val totalDuration = stopwatchViewModel.totalDuration.value

    LaunchedEffect(pagerState.currentPage) {
        // 페이저 안에 선언하니까, 아래 메서드가 반복적으로 실행됨.
        stopwatchViewModel.setTitle(newTitle = titleColorMap.keys.elementAt(pagerState.currentPage))
    }

//    LaunchedEffect(toolState) {
//        onStopwatchStateChanged(toolState)
//    }

    LaunchedEffect(stopwatchViewBarVisible) {
        onStopwatchViewBarVisibleChanged(stopwatchViewBarVisible)
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    BackHandler(enabled = stopwatchViewBarVisible) {
        stopwatchViewModel.setStopwatchViewBarVisible(!stopwatchViewBarVisible)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stopwatchToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                text = "제목 선택",
                style = Typography.titleLarge,
            )

            HorizontalPager( // 페이저 안에 기본적으로 박스가 있음.
                modifier = Modifier
                    .weight(1f),
                state = pagerState,
                pageSpacing = (-48).dp,
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                val menuTitle = titleColorMap.keys.elementAt(page)
                val color = titleColorMap[menuTitle] ?: Transparent
//                val color = lightTitleColorMap[menuTitle] ?: Transparent // 기본 배경색은 투명으로 설정

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    FilledIconButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.1f)
                            .padding(horizontal = 32.dp, vertical = 16.dp),
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = color
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(100.dp),
                            painter = painterResource(id = titleNumberStringToTitleIconMap[menuTitle] ?: 0),
                            contentDescription = "현재 제목",
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = titleNumberStringToTitleKRStringMap[menuTitle] ?: "",
                            style = Typography.titleLarge,
                        )

                        Text(
                            text = titleNumberStringToTitleExampleKRStringMap[menuTitle] ?: "제목",
                            style = Typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
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
                    text = getStopWatchTimeString(totalDuration),
                    style = TextStyle(textAlign = TextAlign.End)
                )
            }
        }

        /** 하단 바 */
        if (stopwatchToolState == CurrentToolState.STOPPED) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    enabled = 0 < pagerState.currentPage
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_keyboard_double_arrow_left_24),
                        contentDescription = "첫 제목"
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    enabled = 0 < pagerState.currentPage
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 제목"
                    )
                }

                FilledIconButton(
                    onClick = {
                        coroutineScope.launch {
                            stopwatchViewModel.startStopwatch()
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_play_arrow_24),
                        contentDescription = "스톱 워치 시작",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    enabled = pagerState.currentPage < pagerState.pageCount - 1
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 제목",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.pageCount - 1)
                        }
                    },
                    enabled = pagerState.currentPage < pagerState.pageCount - 1
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_keyboard_double_arrow_right_24),
                        contentDescription = "마지막 제목",
                    )
                }
            }
        } else if (stopwatchViewBarVisible) {
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
                    Icon(
                        painter = painterResource(id = titleNumberStringToTitleIconMap[title] ?: 0),
                        contentDescription = "현재 제목",
                    )
                }

                FilledTonalIconButton(
                    modifier = Modifier
                        .alpha(0f),
                    onClick = {

                    }
                ) {
                    Icon(
                        painter = painterResource(id = titleNumberStringToTitleIconMap[title] ?: 0),
                        contentDescription = "현재 제목",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        stopwatchViewModel.setStopwatchViewBarVisible(false)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_unfold_more_24),
                        contentDescription = "탑 바텀 바 숨기기",
                    )
                }

                FilledIconButton(
                    onClick = {
                        stopwatchViewModel.stopStopwatch()
                    },
                    enabled = stopwatchToolState == CurrentToolState.PAUSED
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 정지",
                    )
                }

                FilledIconButton(
                    onClick = {
                        if (stopwatchToolState == CurrentToolState.STARTED) { // 스톱 워치 시작 상태
                            stopwatchViewModel.pauseStopwatch()
                        } else { // 스톱 워치 중지, 정지 상태
                            stopwatchViewModel.startStopwatch()
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (stopwatchToolState == CurrentToolState.STARTED)
                                R.drawable.baseline_pause_24
                            else
                                R.drawable.baseline_play_arrow_24
                        ),
                        contentDescription = "스톱 워치 중지 및 시작",
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                FilledTonalIconButton(
                    onClick = {
                        stopwatchViewModel.setStopwatchViewBarVisible(true)
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