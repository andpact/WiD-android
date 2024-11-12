package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerView(
    onTimerViewBarVisibleChanged: (Boolean) -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val TAG = "TimerView"

    val timerViewBarVisible = timerViewModel.timerViewBarVisible.value

    val title = timerViewModel.title.value
    val titlePageIndex = Title.values().indexOf(title).coerceAtLeast(0) - 1

    val titlePagerState = rememberPagerState(
        initialPage = titlePageIndex,
        pageCount = { Title.values().size - 1 }
    )
    val hourSelectionPagerState = rememberPagerState(pageCount = { 12 })
    val minuteSelectionPagerState = rememberPagerState(pageCount = { 60 })
    val coroutineScope = rememberCoroutineScope()

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // 설정 해줘야 함.
        content = { contentPadding: PaddingValues ->
            if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Center
                            )

                            /** 스톱 워치에서 제목을 수정하고, 타이머를 그냥 시작하면 타이머의 제목이 아닐 텐데? */
                            HorizontalPager(
                                state = titlePagerState,
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
                                            titlePagerState.animateScrollToPage(page)
                                        }

                                        timerViewModel.setTitle(newTitle = currentTitle)
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

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalIconButton(
                                    onClick = {
                                        val prevPage = titlePagerState.currentPage - 1
                                        coroutineScope.launch {
                                            titlePagerState.animateScrollToPage(prevPage)
                                        }
                                        timerViewModel.setTitle(newTitle = Title.values()[prevPage + 1])
                                    },
                                    enabled = 0 < titlePagerState.currentPage
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "이전 제목"
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = title.kr,
                                    style = Typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )

                                FilledTonalIconButton(
                                    onClick = {
                                        val nextPage = titlePagerState.currentPage + 1
                                        coroutineScope.launch {
                                            titlePagerState.animateScrollToPage(nextPage)
                                        }
                                        timerViewModel.setTitle(newTitle = Title.values()[nextPage + 1])
                                    },
                                    enabled = titlePagerState.currentPage < titlePagerState.pageCount - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "다음 제목"
                                    )
                                }
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp),
                                text = "시간 선택",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Center
                            )

                            HorizontalPager(
                                pageSpacing = (-288).dp,
                                state = hourSelectionPagerState,
                                verticalAlignment = Alignment.CenterVertically
                            ) { page ->
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 144.dp),
                                    enabled = !hourSelectionPagerState.isScrollInProgress,
                                    onClick = {
                                        coroutineScope.launch {
                                            hourSelectionPagerState.animateScrollToPage(page)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = page.toString(),
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
//                                    textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalIconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            hourSelectionPagerState.animateScrollToPage(hourSelectionPagerState.currentPage - 1)
                                        }
                                    },
                                    enabled = 0 < hourSelectionPagerState.currentPage
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "타이머 -1시간",
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = "${hourSelectionPagerState.currentPage}시간",
                                    style = Typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )

                                FilledTonalIconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            hourSelectionPagerState.animateScrollToPage(hourSelectionPagerState.currentPage + 1)
                                        }
                                    },
                                    enabled = hourSelectionPagerState.currentPage < hourSelectionPagerState.pageCount - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "타이머 +1시간",
                                    )
                                }
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp),
                                text = "분 선택",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Center
                            )

                            HorizontalPager(
                                pageSpacing = (-288).dp,
                                state = minuteSelectionPagerState,
                                verticalAlignment = Alignment.CenterVertically
                            ) { page ->
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 144.dp),
                                    enabled = !minuteSelectionPagerState.isScrollInProgress,
                                    onClick = {
                                        coroutineScope.launch {
                                            minuteSelectionPagerState.animateScrollToPage(page)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = page.toString(),
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
//                                    textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalIconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            minuteSelectionPagerState.animateScrollToPage(
                                                minuteSelectionPagerState.currentPage - 1
                                            )
                                        }
                                    },
                                    enabled = 0 < minuteSelectionPagerState.currentPage
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "타이머 -1분",
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = "${minuteSelectionPagerState.currentPage}분",
                                    style = Typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )

                                FilledTonalIconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            minuteSelectionPagerState.animateScrollToPage(
                                                minuteSelectionPagerState.currentPage + 1
                                            )
                                        }
                                    },
                                    enabled = minuteSelectionPagerState.currentPage < minuteSelectionPagerState.pageCount - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "타이머 +1분",
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier
                                    .height(60.dp)
                            )
                        }
                    }
                )
            } else { // 타이머 시작, 중지
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getTimerDurationString(duration = remainingTime),
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (currentToolState == CurrentToolState.STOPPED) {
                FilledTonalButton(
                    onClick = {
                        coroutineScope.launch {
                            timerViewModel.startTimer()
                        }
                    },
                    enabled = Duration.ZERO < selectedTime,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeGreen
                    ),
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                painter = painterResource(R.drawable.baseline_play_arrow_24),
                                contentDescription = "타이머 시작",
                            )

                            Spacer(
                                modifier = Modifier
                                    .width(8.dp)
                            )

                            Text(
                                text = "타이머 시작",
                                style = Typography.titleLarge
                            )
                        }
                    }
                )

//                ExtendedFloatingActionButton(
//                    text = {
//                        Text(
//                            text = "타이머 시작",
//                            style = Typography.bodyMedium
//                        )
//                    },
//                    icon = {
//                        Icon(
//                            modifier = Modifier
//                                .size(24.dp),
//                            painter = painterResource(R.drawable.baseline_play_arrow_24),
//                            contentDescription = "타이머 시작",
//                        )
//                    },
//                    onClick = {
//                        coroutineScope.launch {
//                            timerViewModel.startTimer()
//                        }
//                    },
//                    containerColor = LimeGreen,
//                    contentColor = White
//                )
            } else if (currentToolState != CurrentToolState.STOPPED && timerViewBarVisible) {
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
                        painter = painterResource(id = title.image),
                        contentDescription = "앱 아이콘"
                    )

                    FilledTonalIconButton(
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
                            timerViewModel.setTimerViewBarVisible(false)
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
                            timerViewModel.stopTimer()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = OrangeRed)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.baseline_stop_24),
                            contentDescription = "타이머 정지",
                            tint = White
                        )
                    }

                    FilledIconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            if (currentToolState == CurrentToolState.STARTED) {
                                timerViewModel.pauseTimer()
                            } else {
                                timerViewModel.startTimer()
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
                            contentDescription = "타이머 중지 및 시작",
                            tint = White
                        )
                    }
                }
            } else if (currentToolState != CurrentToolState.STOPPED) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    FilledTonalIconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            timerViewModel.setTimerViewBarVisible(true)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.baseline_fullscreen_exit_24),
                            contentDescription = "탑 바텀 바 보이기"
                        )
                    }
                }
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun TimerPreview() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Text(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.secondaryContainer,
//                    shape = MaterialTheme.shapes.medium
//                )
//                .padding(16.dp),
//            text = "제목 선택",
//            style = Typography.titleLarge,
//            color = MaterialTheme.colorScheme.onSecondaryContainer,
//            textAlign = TextAlign.Center
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            Image(
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f / 1f)
//                    .clip(MaterialTheme.shapes.medium),
//                painter = painterResource(id = titleImageMap["0"] ?: R.drawable.image_untitled),
//                contentDescription = "앱 아이콘"
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f / 1f)
//            ) {
//                Text(
//                    text = "공부",
//                    style = Typography.bodyMedium
//                )
//
//                Row() {
//                    FilledTonalIconButton(
//                        modifier = Modifier
//                            .size(48.dp),
//                        onClick = {
//                        }
//                    ) {
//                        Icon(
//                            modifier = Modifier
//                                .size(36.dp),
//                            imageVector = Icons.Default.KeyboardArrowLeft,
//                            contentDescription = "이전 제목"
//                        )
//                    }
//
//                    FilledTonalIconButton(
//                        modifier = Modifier
//                            .size(48.dp),
//                        onClick = {
//                        }
//                    ) {
//                        Icon(
//                            modifier = Modifier
//                                .size(36.dp),
//                            imageVector = Icons.Default.KeyboardArrowRight,
//                            contentDescription = "다음 제목",
//                        )
//                    }
//                }
//            }
//        }
//
//        Text(
//            modifier = Modifier
//                .padding(16.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.secondary,
//                    shape = MaterialTheme.shapes.medium
//                )
//                .padding(16.dp),
//            text = "시간 선택",
//            style = Typography.titleLarge
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            FilledTonalIconButton(
//                modifier = Modifier
//                    .size(48.dp),
//                onClick = {
//                }
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(36.dp),
//                    imageVector = Icons.Default.KeyboardArrowLeft,
//                    contentDescription = "이전 제목"
//                )
//            }
//
//            Text(
//                modifier = Modifier
//                    .weight(1f),
//                text = "0",
//                style = Typography.titleLarge
//            )
//
//            FilledTonalIconButton(
//                modifier = Modifier
//                    .size(48.dp),
//                onClick = {
//                }
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(36.dp),
//                    imageVector = Icons.Default.KeyboardArrowRight,
//                    contentDescription = "다음 제목",
//                )
//            }
//        }
//
//        Text(
//            modifier = Modifier
//                .padding(16.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.secondary,
//                    shape = MaterialTheme.shapes.medium
//                )
//                .padding(16.dp),
//            text = "분 선택",
//            style = Typography.titleLarge
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            FilledTonalIconButton(
//                modifier = Modifier
//                    .size(48.dp),
//                onClick = {
//                }
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(36.dp),
//                    imageVector = Icons.Default.KeyboardArrowLeft,
//                    contentDescription = "이전 제목"
//                )
//            }
//
//            Text(
//                modifier = Modifier
//                    .weight(1f),
//                text = "0",
//                style = Typography.titleLarge
//            )
//
//            FilledTonalIconButton(
//                modifier = Modifier
//                    .size(48.dp),
//                onClick = {
//                }
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(36.dp),
//                    imageVector = Icons.Default.KeyboardArrowRight,
//                    contentDescription = "다음 제목",
//                )
//            }
//        }
//    }
//}