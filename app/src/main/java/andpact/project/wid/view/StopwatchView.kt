package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.StopwatchViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StopwatchView(
    onBackButtonPressed: () -> Unit,
    stopwatchViewModel: StopwatchViewModel = hiltViewModel()
) {
    val TAG = "StopwatchView"

    // currentWiD
    val firstCurrentWiD = stopwatchViewModel.firstCurrentWiD.value
    val secondCurrentWiD = stopwatchViewModel.secondCurrentWiD.value

    // 제목
    val titlePageIndex = Title.values().drop(1).indexOf(firstCurrentWiD.title).coerceAtLeast(0)

    // 화면
    val stopwatchViewBarVisible = stopwatchViewModel.stopwatchViewBarVisible.value
    val pagerState = rememberPagerState(
        initialPage = titlePageIndex,
        pageCount = { Title.values().drop(1).size}
    )
    val coroutineScope = rememberCoroutineScope()

    // 도구
    val currentToolState = stopwatchViewModel.currentToolState.value
    val totalDuration = stopwatchViewModel.totalDuration.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(enabled = stopwatchViewBarVisible) {
        stopwatchViewModel.setStopwatchViewBarVisible(!stopwatchViewBarVisible)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // 설정 해줘야 함.
        topBar = {
            if (stopwatchViewBarVisible) {
                CenterAlignedTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(shape = MaterialTheme.shapes.medium),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onBackButtonPressed()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = "뒤로 가기",
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "스톱워치",
                            style = Typography.titleLarge,
                        )
                    },
                    actions = {
                        /** 현재 기록 보기 */
                        IconButton(
                            onClick = {
                                onBackButtonPressed()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_keyboard_double_arrow_right_24),
                                contentDescription = "현재 기록 보기",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        },
        content = { contentPadding: PaddingValues ->
            if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = {
                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp),
                                text = "제목 선택",
                                style = Typography.titleLarge,
                                textAlign = TextAlign.Center
                            )

                            HorizontalPager(
                                state = pagerState,
                                pageSpacing = (-48).dp
                            ) { page: Int ->
                                val currentTitle = Title.values()[page + 1] // 인덱스 보정

                                FilledIconButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 32.dp)
                                        .aspectRatio(1f),
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(page)
                                        }

                                        stopwatchViewModel.setTitle(newTitle = currentTitle)
                                    },
                                    shape = MaterialTheme.shapes.extraLarge
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        painter = painterResource(id = currentTitle.image), // Title enum의 image 사용
                                        contentDescription = currentTitle.kr // 한글 설명 사용
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = firstCurrentWiD.title.kr,
                                    style = Typography.titleLarge,
                                )

//                                Spacer(
//                                    modifier = Modifier
//                                        .height(height = 8.dp)
//                                )
//
//                                Text(
//                                    text = firstCurrentWiD.title.description,
//                                    style = Typography.bodyMedium,
//                                    overflow = TextOverflow.Ellipsis,
//                                    maxLines = 1
//                                )
                            }
                        }
                    }
                )
            } else { // 스톱 워치 시작, 중지 상태
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stopwatchViewModel.getStopwatchDurationString(totalDuration),
                        style = TextStyle(textAlign = TextAlign.End)
                    )
                }
            }
        },
        bottomBar = {
            if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            val firstPage = pagerState.initialPage // 0
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(firstPage)
                            }
                            stopwatchViewModel.setTitle(newTitle = Title.values().drop(1)[firstPage])
                        },
                        enabled = 0 < pagerState.currentPage
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(R.drawable.baseline_keyboard_double_arrow_left_24),
                            contentDescription = "첫 제목"
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            val prevPage = pagerState.currentPage - 1
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(prevPage)
                            }
                            stopwatchViewModel.setTitle(newTitle = Title.values().drop(1)[prevPage])
                        },
                        enabled = 0 < pagerState.currentPage
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 제목"
                        )
                    }

                    FilledIconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            coroutineScope.launch {
                                stopwatchViewModel.startStopwatch()
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = LimeGreen)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(R.drawable.baseline_play_arrow_24),
                            contentDescription = "스톱 워치 시작",
                            tint = White
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            val nextPage = pagerState.currentPage + 1
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(nextPage)
                            }
                            stopwatchViewModel.setTitle(newTitle = Title.values().drop(1)[nextPage])
                        },
                        enabled = pagerState.currentPage < pagerState.pageCount - 1
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 제목",
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            val lastPage = pagerState.pageCount - 1
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(lastPage)
                            }
                            stopwatchViewModel.setTitle(newTitle = Title.values().drop(1)[lastPage])
                        },
                        enabled = pagerState.currentPage < pagerState.pageCount - 1
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(R.drawable.baseline_keyboard_double_arrow_right_24),
                            contentDescription = "마지막 제목",
                        )
                    }
                }
            } else if (stopwatchViewBarVisible) { // 스톱 워치 중지, 정지
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.medium),
                        painter = painterResource(id = firstCurrentWiD.title.smallImage),
                        contentDescription = "앱 아이콘"
                    )

                    IconButton(
                        modifier = Modifier
                            .size(48.dp)
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
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            stopwatchViewModel.setStopwatchViewBarVisible(false)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.baseline_fullscreen_24),
                            contentDescription = "탑 바텀 바 숨기기",
                        )
                    }

                    FilledIconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            stopwatchViewModel.stopStopwatch()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = OrangeRed)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.baseline_stop_24),
                            contentDescription = "스톱워치 정지",
                            tint = White
                        )
                    }

                    FilledIconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            if (currentToolState == CurrentToolState.STARTED) { // 스톱 워치 시작 상태
                                stopwatchViewModel.pauseStopwatch()
                            } else { // 스톱 워치 중지, 정지 상태
                                stopwatchViewModel.startStopwatch()
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (currentToolState == CurrentToolState.STARTED) { // 스톱 워치 시작 상태
                                DeepSkyBlue
                            } else { // 스톱 워치 중지 상태
                                LimeGreen
                            }
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(
                                id = if (currentToolState == CurrentToolState.STARTED) {
                                    R.drawable.baseline_pause_24
                                } else {
                                    R.drawable.baseline_play_arrow_24
                                }
                            ),
                            contentDescription = "스톱 워치 중지 및 시작",
                            tint = White
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
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            stopwatchViewModel.setStopwatchViewBarVisible(true)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.baseline_fullscreen_exit_24),
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun StopwatchPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.large.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0))),
            navigationIcon = {
                IconButton(
                    onClick = {
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "뒤로 가기",
                    )
                }
            },
            title = {
                Text(
                    text = "새로운 기록",
                    style = Typography.titleLarge,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        )
    }
}