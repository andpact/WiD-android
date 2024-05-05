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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
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
    onStopwatchStateChanged: (PlayerState) -> Unit,
    onHideStopwatchViewBarChanged: (Boolean) -> Unit,
    stopwatchViewModel: StopwatchViewModel = hiltViewModel(),
) {
    val TAG = "StopwatchView"

    // 화면
    val hideStopwatchViewBar = stopwatchViewModel.hideStopwatchViewBar.value

    // 제목
    val title = stopwatchViewModel.title.value

    val titleColorMap = stopwatchViewModel.titleColorMap.value
//    val titleColorMap = defaultTitleColorMapWithColors

    val pagerState = rememberPagerState(pageCount = { titleColorMap.size })
    val coroutineScope = rememberCoroutineScope()

    // 스톱 워치
    val stopwatchState = stopwatchViewModel.stopwatchState.value
    val duration = stopwatchViewModel.duration.value

    LaunchedEffect(pagerState.currentPage) {
        // 페이저 안에 선언하니까, 아래 메서드가 반복적으로 실행됨.
        stopwatchViewModel.setTitle(newTitle = titleColorMap.keys.elementAt(pagerState.currentPage))
    }

    LaunchedEffect(stopwatchState) {
        onStopwatchStateChanged(stopwatchState)
    }

    LaunchedEffect(hideStopwatchViewBar) {
        onHideStopwatchViewBarChanged(hideStopwatchViewBar)
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    BackHandler(enabled = hideStopwatchViewBar) {
        stopwatchViewModel.setHideStopwatchViewBar(!hideStopwatchViewBar)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .clickable(
                enabled = stopwatchState == PlayerState.Started,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { // 스톱 워치가 시작된 상태에서만 상, 하단 바 숨길 수 있도록
                stopwatchViewModel.setHideStopwatchViewBar(!hideStopwatchViewBar)
            }
    ) {
        if (stopwatchState == PlayerState.Stopped) { // 스톱 워치 정지 상태
            HorizontalPager(
                modifier = Modifier
                    .weight(1f),
                state = pagerState,
                pageSpacing = (-48).dp
            ) { page ->
                val menuTitle = titleColorMap.keys.elementAt(page)
                val color = titleColorMap[menuTitle] ?: Transparent // 기본 배경색은 투명으로 설정

                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .fillMaxWidth()
//                        .aspectRatio(1f)
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = menuTitle,
                        style = Typography.bodyMedium,
                        color = titleColorMap[menuTitle]?.let { textColor ->
                            if (0.5f < textColor.luminance()) {
                                Black
                            } else {
                                White
                            }
                        } ?: MaterialTheme.colorScheme.primary
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
                    text = getStopWatchTimeString(duration),
                    style = TextStyle(
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        /** 하단 바 */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp)
                .alpha(if (hideStopwatchViewBar) 0f else 1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (stopwatchState == PlayerState.Stopped) { // 스톱 워치 정지 상태
                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = 0 < pagerState.currentPage,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                        .size(32.dp),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "첫 제목",
                    tint = if (0 < pagerState.currentPage)
                        MaterialTheme.colorScheme.primary
                    else
                        DarkGray
                )

                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = 0 < pagerState.currentPage,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                        .size(32.dp),
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 제목",
                    tint = if (0 < pagerState.currentPage)
                        MaterialTheme.colorScheme.primary
                    else
                        DarkGray
                )

                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            stopwatchViewModel.startStopwatch()
                        }
                        .background(color = LimeGreen)
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(R.drawable.baseline_play_arrow_24),
                    contentDescription = "스톱 워치 시작",
                    tint = White
                )

                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = pagerState.currentPage < pagerState.pageCount - 1,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                        .size(32.dp),
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 제목",
                    tint = if (pagerState.currentPage < pagerState.pageCount - 1)
                        MaterialTheme.colorScheme.primary
                    else
                        DarkGray
                )

                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = pagerState.currentPage < pagerState.pageCount - 1,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.pageCount - 1)
                            }
                        }
                        .size(32.dp),
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "마지막 제목",
                    tint = if (pagerState.currentPage < pagerState.pageCount - 1)
                        MaterialTheme.colorScheme.primary
                    else
                        DarkGray
                )
            } else { // 스톰 워치 시작 및 정지 상태
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(titleColorMap[title] ?: MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    text = title,
                    style = Typography.titleLarge,
                    color = titleColorMap[title]?.let { textColor ->
                        if (textColor.luminance() > 0.5f)
                            Black
                        else
                            White
                    } ?: MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (stopwatchState == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
//                                enabled = stopwatchState == PlayerState.Paused,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                stopwatchViewModel.stopStopwatch()
                            }
                            .background(color = DeepSkyBlue)
                            .padding(16.dp)
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 정지",
                        tint = White
                    )
                }

                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { // 스톱 워치 모든 상태일 때 클릭 가능
                            if (hideStopwatchViewBar) {
                                stopwatchViewModel.setHideStopwatchViewBar(hideStopwatchViewBar = false)
                            } else if (stopwatchState == PlayerState.Started) { // 스톱 워치 시작 상태
                                stopwatchViewModel.pauseStopwatch()
                            } else { // 스톱 워치 중지, 정지 상태
                                stopwatchViewModel.startStopwatch()
                            }
                        }
                        .background(
                            color = if (stopwatchState == PlayerState.Started)
                                OrangeRed
                            else
                                LimeGreen
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(
                        id = if (stopwatchState == PlayerState.Started)
                            R.drawable.baseline_pause_24
                        else
                            R.drawable.baseline_play_arrow_24
                    ),
                    contentDescription = "스톱 워치 중지 및 시작",
                    tint = White
                )
            }
        }
    }
}