package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

// TODO: wiDMaxLimit으로 타이머 시간 제한하기
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerView(
    onBackButtonPressed: () -> Unit,
    onTitlePickerClicked: () -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val TAG = "TimerView"

    val WID_LIST_LIMIT_PER_DAY = timerViewModel.WID_LIST_LIMIT_PER_DAY

    val isSameDateForStartAndFinish = timerViewModel.isSameDateForStartAndFinish.value
    val firstCurrentWiD = timerViewModel.firstCurrentWiD.value
    val secondCurrentWiD = timerViewModel.secondCurrentWiD.value

    val wiDList = timerViewModel.wiDList.value

    val user = timerViewModel.user.value
    val wiDMinLimit = user?.wiDMinLimit ?: Duration.ZERO
    val wiDMaxLimit = user?.wiDMaxLimit ?: Duration.ZERO

    val timerViewBarVisible = timerViewModel.timerViewBarVisible.value
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
    val pagerState = rememberPagerState(pageCount = { 3 })


    val hourPagerState = rememberPagerState(pageCount = { 12 })
    val minutePagerState = rememberPagerState(pageCount = { 60 })
    val secondPagerState = rememberPagerState(pageCount = { 60 })
    val coroutineScope = rememberCoroutineScope()

    val currentToolState = timerViewModel.currentToolState.value
    val remainingTime = timerViewModel.remainingTime.value
    val selectedTime = timerViewModel.selectedTime.value
    // TODO: 최초 선택 시간을 가지고 있어야 함.

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    LaunchedEffect(
        key1 = hourPagerState.currentPage,
        key2 = minutePagerState.currentPage,
        key3 = secondPagerState.currentPage
    ) {
        val adjustedHour = hourPagerState.currentPage
        val adjustedMinute = minutePagerState.currentPage
        val adjustedSecond = secondPagerState.currentPage
        val newRemainingTime =
            Duration.ofHours(adjustedHour.toLong()) +
            Duration.ofMinutes(adjustedMinute.toLong()) +
            Duration.ofSeconds(adjustedSecond.toLong())

        timerViewModel.setTimerTime(newRemainingTime)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // 설정 해줘야 함.
        topBar = {
            AnimatedVisibility(
                visible = timerViewBarVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it } // 위에서 아래로 슬라이드
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
                    initialAlpha = 0f // 점점 불투명하게
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it } // 아래에서 위로 슬라이드
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing),
                    targetAlpha = 0f // 점점 투명하게
                )
            ) {
                CenterAlignedTopAppBar(
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
                        Text(text = "타이머")
                    }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AnimatedVisibility(
                    visible = timerViewBarVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            TextButton(
                                modifier = Modifier
                                    .align(Alignment.CenterStart),
                                onClick = {
                                    onTitlePickerClicked()
                                },
                                enabled = currentToolState == CurrentToolState.STOPPED,
                            ) {
                                Text(
                                    text = firstCurrentWiD.subTitle.kr + ", " + firstCurrentWiD.title.kr,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        FilledIconButton(
                            onClick = {
                                if (currentToolState == CurrentToolState.STARTED) { // 시작 상태
                                    timerViewModel.pauseTimer()
                                } else { // 중지 및 정지 상태
                                    timerViewModel.startTimer()
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(1) // 표지로 전환
                                    }
                                }
                            },
                            enabled = (user?.wiDMinLimit ?: Duration.ZERO) <= selectedTime && selectedTime <= user?.wiDMaxLimit && wiDList.size <= WID_LIST_LIMIT_PER_DAY
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (currentToolState == CurrentToolState.STARTED) { R.drawable.baseline_pause_24 }
                                    else { R.drawable.baseline_play_arrow_24 }
                                ),
                                contentDescription = when (currentToolState) {
                                    CurrentToolState.STARTED -> "타이머 일시 정지"
                                    CurrentToolState.PAUSED, CurrentToolState.STOPPED -> "타이머 시작"
                                },
                                tint = White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd),
                                onClick = {
                                    timerViewModel.stopTimer()
                                },
                                enabled = currentToolState == CurrentToolState.PAUSED
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "타이머 초기화"
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    AnimatedVisibility(
                        modifier = Modifier
                            .weight(1f),
                        visible = timerViewBarVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = 4.dp),
                                    text = "최소",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Text(
                                    text = timerViewModel.getDurationString(wiDMinLimit),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = 4.dp),
                                    text = "최대",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Text(
                                    text = timerViewModel.getDurationString(wiDMaxLimit),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            if (timerViewBarVisible) {
                                timerViewModel.setTimerViewBarVisible(false)
                            } else {
                                timerViewModel.setTimerViewBarVisible(true)
                            }
                        },
                        enabled = currentToolState == CurrentToolState.STARTED
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (timerViewBarVisible) {
                                    R.drawable.baseline_fullscreen_24
                                } else {
                                    R.drawable.baseline_fullscreen_exit_24
                                }
                            ),
                            contentDescription = "탑 바텀 바 숨기기 보이기",
                        )
                    }

                    AnimatedVisibility(
                        modifier = Modifier
                            .weight(1f),
                        visible = timerViewBarVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${wiDList.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = 4.dp),
                                    text = "현재",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$WID_LIST_LIMIT_PER_DAY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = 4.dp),
                                    text = "최대",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                    }
                }
            }
        },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
                    TimeSelectorView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
//                                        .height(screenHeight / 3), // TODO: 화면높이가 아니라 고정하는 게 맞음
                        hourPagerState = hourPagerState,
                        minutePagerState = minutePagerState,
                        secondPagerState = secondPagerState,
                        coroutineScope = coroutineScope
                    )
                } else { // 스톱 워치 시작, 중지 상태
                    AnimatedVisibility(
                        visible = timerViewBarVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val currentPage = pagerState.currentPage

                            Row(
                                modifier = Modifier
                                    .height(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                            ) {
                                FilterChip(
                                    selected = currentPage == 0,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepSkyBlue),
                                    border = null,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    },
                                    label = {
                                        Text(text = "표지")
                                    }
                                )

                                FilterChip(
                                    selected = currentPage == 1,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepSkyBlue),
                                    border = null,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    },
                                    label = {
                                        Text(text = "시간")
                                    }
                                )

                                FilterChip(
                                    selected = currentPage == 2,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepSkyBlue),
                                    border = null,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(2)
                                        }
                                    },
                                    label = {
                                        Text(text = "기록")
                                    }
                                )
                            }
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f),
                        state = pagerState,
                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->
                        when (page) {
                            0 -> { // 표지
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .padding(horizontal = 64.dp),
                                        painter = painterResource(id = firstCurrentWiD.title.smallImage),
                                        contentDescription = "제목 이미지"
                                    )

                                    val elapsedTime = selectedTime - remainingTime // TODO: 최초 선택시간을 화면에 표시해야 함.
                                    val progress = if (selectedTime.seconds > 0) { // 0으로 나누는 오류 방지
                                        elapsedTime.seconds / selectedTime.seconds.toFloat()
                                    } else {
                                        0f
                                    }

                                    Slider(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp),
                                        value = progress,
                                        enabled = false,
                                        onValueChange = {},
                                        colors = SliderDefaults.colors(
                                            disabledThumbColor = MaterialTheme.colorScheme.onSurface,
                                            disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = timerViewModel.getDurationString(remainingTime),
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        Text(
                                            text = timerViewModel.getDurationString(selectedTime),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            1 -> { // 시간
                                Text(
                                    text = timerViewModel.getTimerDurationString(duration = remainingTime),
                                    style = TextStyle(
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                )
                            }
                            2 -> { // 기록
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                ) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                            ) {
                                                Text(
                                                    text = "날짜",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )

                                                Text(
                                                    text = timerViewModel.getDateString(date = firstCurrentWiD.date),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            if (!isSameDateForStartAndFinish) {
                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = 12.dp),
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                ) {
                                                    Text(
                                                        text = "날짜",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                    )

                                                    Text(
                                                        text = timerViewModel.getDateString(date = secondCurrentWiD.date),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "제목",
                                                style = MaterialTheme.typography.bodyLarge,
                                            )

                                            Text(
                                                text = firstCurrentWiD.title.kr,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                            ) {
                                                Text(
                                                    text = "시작",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )

                                                Text(
                                                    text = timerViewModel.getTimeString(time = firstCurrentWiD.start),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            if (!isSameDateForStartAndFinish) {
                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = 12.dp),
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                ) {
                                                    Text(
                                                        text = "시작",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                    )

                                                    Text(
                                                        text = timerViewModel.getTimeString(time = secondCurrentWiD.start),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                            ) {
                                                Text(
                                                    text = "종료",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )

                                                Text(
                                                    text = timerViewModel.getTimeString(time = firstCurrentWiD.finish),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            if (!isSameDateForStartAndFinish) {
                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = 12.dp),
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                ) {
                                                    Text(
                                                        text = "종료",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                    )

                                                    Text(
                                                        text = timerViewModel.getTimeString(time = secondCurrentWiD.finish),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                            ) {
                                                Text(
                                                    text = "소요",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )

                                                Text(
                                                    text = timerViewModel.getDurationString(firstCurrentWiD.duration),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            if (!isSameDateForStartAndFinish) {
                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = 12.dp),
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                ) {
                                                    Text(
                                                        text = "소요",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                    )

                                                    Text(
                                                        text = timerViewModel.getDurationString(secondCurrentWiD.duration),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f),
                                            ) {
                                                Text(
                                                    text = "경험치",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )

                                                Text(
                                                    text = "${firstCurrentWiD.exp}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            if (!isSameDateForStartAndFinish) {
                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = 12.dp),
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                ) {
                                                    Text(
                                                        text = "경험치",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                    )

                                                    Text(
                                                        text = "${secondCurrentWiD.exp}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp),
                                            thickness = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp)
                                                .padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "위치",
                                                style = MaterialTheme.typography.bodyLarge,
                                            )

                                            Text(
                                                text = firstCurrentWiD.city.kr + ", " + firstCurrentWiD.city.country.kr,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}