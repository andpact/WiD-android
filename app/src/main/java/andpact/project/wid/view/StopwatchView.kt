package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.StopwatchViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StopwatchView(
    onStopwatchViewBarVisibleChanged: (Boolean) -> Unit,
    stopwatchViewModel: StopwatchViewModel = hiltViewModel(),
) {
    val TAG = "StopwatchView"

    // 제목
    val title = stopwatchViewModel.title.value

    // 화면
    val stopwatchViewBarVisible = stopwatchViewModel.stopwatchViewBarVisible.value
    val pagerState = rememberPagerState(pageCount = { titleColorMap.size })
    val coroutineScope = rememberCoroutineScope()

    // 도구
    val currentToolState = stopwatchViewModel.currentToolState.value
    val totalDuration = stopwatchViewModel.totalDuration.value

    LaunchedEffect(stopwatchViewBarVisible) {
        onStopwatchViewBarVisibleChanged(stopwatchViewBarVisible)
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(enabled = stopwatchViewBarVisible) {
        stopwatchViewModel.setStopwatchViewBarVisible(!stopwatchViewBarVisible)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
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
            ) { page: Int ->
                // titleDurationMap를 가져와서 내림차순으로 정렬, page를 인덱스로 제목을 표시함.
                // titleCountMap을 가져와서 titleDurationMap의 키를 사용해서 개수를 가져옴.
                // 가장 많은 시간, 가장 많은 개수 표시하기
                // 최근 사용 제목도 표시하기

                FilledIconButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                        .aspectRatio(1f / 1f),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }

                        stopwatchViewModel.setTitle(newTitle = "$page")
                    },
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = painterResource(id = titleImageMap["$page"] ?: R.drawable.ic_launcher_background),
                        contentDescription = "앱 아이콘"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
//                    text = titleKRMap["${pagerState.currentPage}"] ?: "기록 없음",
                    text = titleKRMap[title] ?: "기록 없음",
                    style = Typography.titleLarge,
                )

//                Text(
//                    text = titleNumberStringToTitleExampleKRStringMap["${pagerState.currentPage}"] ?: "",
//                    style = Typography.bodyMedium,
//                    overflow = TextOverflow.Ellipsis,
//                    maxLines = 1
//                )
            }
        } else { // 스톱 워치 시작, 중지 상태
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
        if (currentToolState == CurrentToolState.STOPPED) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = {
                        val firstPage = pagerState.initialPage
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(firstPage)
                        }
                        stopwatchViewModel.setTitle(newTitle = "$firstPage")
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
                        val prevPage = pagerState.currentPage - 1
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                        stopwatchViewModel.setTitle(newTitle = "$prevPage")
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
                        val nextPage = pagerState.currentPage + 1
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                        stopwatchViewModel.setTitle(newTitle = "$nextPage")
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
                        val lastPage = pagerState.pageCount - 1
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(lastPage)
                        }
                        stopwatchViewModel.setTitle(newTitle = "$lastPage")
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
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 정지",
                    )
                }

                FilledIconButton(
                    onClick = {
                        if (currentToolState == CurrentToolState.STARTED) { // 스톱 워치 시작 상태
                            stopwatchViewModel.pauseStopwatch()
                        } else { // 스톱 워치 중지, 정지 상태
                            stopwatchViewModel.startStopwatch()
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (currentToolState == CurrentToolState.STARTED)
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

//@Preview(showBackground = true)
//@Composable
//fun StopwatchPreview() {
//}