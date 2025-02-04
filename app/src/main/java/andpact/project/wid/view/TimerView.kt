package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.SnackbarActionResult
import andpact.project.wid.ui.theme.White
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

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
    val pagerState = rememberPagerState(pageCount = { 3 })

    val coroutineScope = rememberCoroutineScope()
    val hourListState = rememberLazyListState()
    val minuteListState = rememberLazyListState()
    val secondListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentToolState = timerViewModel.currentToolState.value
    val remainingTime = timerViewModel.remainingTime.value
    val selectedTime = timerViewModel.selectedTime.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(enabled = !timerViewBarVisible) {
        timerViewModel.setTimerViewBarVisible(true)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // 설정 해줘야 함.
        topBar = {
            AnimatedContent(
                targetState = timerViewBarVisible,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
            ) { isVisible: Boolean ->
                if (isVisible) {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onBackButtonPressed()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "뒤로 가기",
                                )
                            }
                        },
                        title = {
                            Text(text = "타이머")
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.height(64.dp)) // 빈 공간 유지
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AnimatedContent(
                    targetState = timerViewBarVisible,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                ) { isVisible: Boolean ->
                    if (isVisible) {
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
                                        timerViewModel.pauseTimer(
                                            onResult = { snackbarActionResult: SnackbarActionResult ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = snackbarActionResult.message,
                                                        actionLabel = "확인",
                                                        withDismissAction = true,
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            }
                                        )
                                    } else { // 중지 및 정지 상태
                                        timerViewModel.startTimer(
                                            onResult = { snackbarActionResult: SnackbarActionResult ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = snackbarActionResult.message,
                                                        actionLabel = "확인",
                                                        withDismissAction = true,
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }, // TODO: 제목이 있을 때만 시작 가능하도록
                                enabled = wiDMinLimit <= selectedTime && selectedTime <= wiDMaxLimit && wiDList.size <= WID_LIST_LIMIT_PER_DAY
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
                                    enabled = currentToolState != CurrentToolState.STOPPED
                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Close,
//                                        contentDescription = "타이머 초기화"
//                                    )

                                    Text(text = "초기화")
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(64.dp)) // 빈 공간 유지
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        modifier = Modifier
                            .weight(1f),
                        visible = timerViewBarVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
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
                        enter = fadeIn(),
                        exit = fadeOut()
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
                verticalArrangement = Arrangement.Center
            ) {
                if (currentToolState == CurrentToolState.STOPPED) { // 스톱 워치 정지 상태
                    TimeSelectorView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        coroutineScope = coroutineScope,
                        hourListState = hourListState,
                        minuteListState = minuteListState,
                        secondListState = secondListState,
                        onTimeChanged = { selectedTime: Duration ->
                            timerViewModel.setTimerTime(newSelectedTime = selectedTime)
                        }
                    )
                } else { // 스톱 워치 시작, 중지 상태
                    AnimatedContent(
                        targetState = timerViewBarVisible,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                    ) { isVisible: Boolean ->
                        if (isVisible) {
                            val currentPage = pagerState.currentPage

                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                SegmentedButton(
                                    modifier = Modifier
                                        .height(40.dp),
                                    selected = currentPage == 0,
                                    shape = MaterialTheme.shapes.extraLarge.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    },
                                    icon = {}
                                ) {
                                    Text(text = "표지")
                                }

                                SegmentedButton(
                                    modifier = Modifier
                                        .height(40.dp),
                                    selected = currentPage == 1,
                                    shape = RectangleShape,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    },
                                    icon = {}
                                ) {
                                    Text(text = "시간")
                                }

                                SegmentedButton(
                                    modifier = Modifier
                                        .height(40.dp),
                                    selected = currentPage == 2,
                                    shape = MaterialTheme.shapes.extraLarge.copy(topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)),
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(2)
                                        }
                                    },
                                    icon = {}
                                ) {
                                    Text(text = "기록")
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(64.dp)) // 빈 공간 유지
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f),
                        state = pagerState,
                        verticalAlignment = Alignment.CenterVertically
                    ) { page: Int ->
                        when (page) {
                            0 -> { // 표지
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
//                                    Image(
//                                        modifier = Modifier
//                                            .padding(horizontal = 64.dp),
//                                        painter = painterResource(id = firstCurrentWiD.title.smallImage),
//                                        contentDescription = "제목 이미지"
//                                    )

                                    val elapsedTime = selectedTime - remainingTime
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

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .align(alignment = Alignment.CenterStart),
                                            text = timerViewModel.getDurationTimeString(remainingTime),
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        Text(
                                            modifier = Modifier
                                                .align(alignment = Alignment.CenterEnd),
                                            text = timerViewModel.getDurationTimeString(selectedTime),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            1 -> { // 시간
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = timerViewModel.getTimerDurationString(duration = remainingTime),
                                    style = TextStyle(
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                )
                            }
                            2 -> { // 기록
                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                ) {
                                    item(
                                        key = "date",
                                        contentType = "list-item-date"
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(72.dp)
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
                                    }

                                    item(
                                        key = "title",
                                        contentType = "list-item-title"
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
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
                                    }

                                    item(
                                        key = "start",
                                        contentType = "list-item-start"
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(72.dp)
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
                                    }

                                    item(
                                        key = "finish",
                                        contentType = "list-item-finish"
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(72.dp)
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
                                    }

                                    item(
                                        key = "duration",
                                        contentType = "list-item-duration"
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(72.dp)
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
                                                    text = timerViewModel.getDurationString(
                                                        firstCurrentWiD.duration
                                                    ),
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
                                                        text = timerViewModel.getDurationString(
                                                            secondCurrentWiD.duration
                                                        ),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    item(
                                        key = "exp",
                                        contentType = "list-item-exp"
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .height(72.dp)
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