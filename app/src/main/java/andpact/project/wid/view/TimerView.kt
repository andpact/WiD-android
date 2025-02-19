package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.SnackbarActionResult
import andpact.project.wid.model.Title
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.ui.theme.White
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    onBackButtonPressed: () -> Unit,
    onTitlePickerClicked: () -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val TAG = "TimerView"

    val WID_LIST_LIMIT_PER_DAY = timerViewModel.WID_LIST_LIMIT_PER_DAY

    val now = timerViewModel.now.value

    val currentWiD = timerViewModel.currentWiD.value

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

    val playerState = timerViewModel.playerState.value
    val remainingTime = timerViewModel.remainingTime.value
    val selectedTime = timerViewModel.selectedTime.value

    LaunchedEffect(
        key1 = hourListState.firstVisibleItemIndex,
        key2 = minuteListState.firstVisibleItemIndex,
        key3 = secondListState.firstVisibleItemIndex,
        block = {
            val newSelectedTime = Duration.ofHours(hourListState.firstVisibleItemIndex.toLong())
                .plusMinutes(minuteListState.firstVisibleItemIndex.toLong())
                .plusSeconds(secondListState.firstVisibleItemIndex.toLong())

            timerViewModel.setTimerTime(newSelectedTime = newSelectedTime)
        }
    )

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
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(statusBarHeight)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        )

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
                                Text(
                                    text = "타이머",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(statusBarHeight + 64.dp)) // 빈 공간 유지
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
                                    enabled = playerState == PlayerState.STOPPED,
                                ) {
                                    Text(
                                        text = currentWiD.subTitle.kr + ", " + currentWiD.title.kr,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            FilledIconButton(
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (playerState == PlayerState.STARTED) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = if (playerState == PlayerState.STARTED) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                                ),
                                onClick = {
                                    if (playerState == PlayerState.STARTED) { // 시작 상태
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
                                },
                                enabled = wiDMinLimit <= selectedTime && selectedTime <= wiDMaxLimit && wiDList.size <= WID_LIST_LIMIT_PER_DAY && currentWiD.title != Title.UNTITLED
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (playerState == PlayerState.STARTED) { R.drawable.baseline_pause_24 }
                                        else { R.drawable.baseline_play_arrow_24 }
                                    ),
                                    contentDescription = when (playerState) {
                                        PlayerState.STARTED -> "타이머 일시 정지"
                                        PlayerState.PAUSED, PlayerState.STOPPED -> "타이머 시작"
                                    },
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                TextButton(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd),
                                    onClick = {
                                        timerViewModel.stopTimer()
                                    },
                                    enabled = playerState != PlayerState.STOPPED
                                ) {
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
                                    style = MaterialTheme.typography.labelSmall
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
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    OutlinedIconButton(
                        onClick = {
                            if (timerViewBarVisible) {
                                timerViewModel.setTimerViewBarVisible(false)
                            } else {
                                timerViewModel.setTimerViewBarVisible(true)
                            }
                        },
                        enabled = playerState == PlayerState.STARTED
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
                                    style = MaterialTheme.typography.labelSmall
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
                                    style = MaterialTheme.typography.labelSmall
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navigationBarHeight)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center
            ) {
                if (playerState == PlayerState.STOPPED) { // 스톱 워치 정지 상태
                    // TODO: 탭 로우 추가하고 정해진 시간(기존 타이머 동작)과 정해진 시각(16:00:00까지)로 나누기
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(intrinsicSize = IntrinsicSize.Min)
                        ) {
                            VerticalDivider()

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .height((48 * 5).dp),
                                state = hourListState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)
                            ) {
                                items(
                                    count = 2,
                                    key = { index -> "hour-spacer-top-$index" },
                                    contentType = { "spacer" }
                                ) {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }

                                items(
                                    count = 24,
                                    key = { itemIndex -> "hour-item-$itemIndex" },
                                    contentType = { "hour-item" }
                                ) { itemIndex ->
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        onClick = {
                                            coroutineScope.launch {
                                                hourListState.animateScrollToItem(index = itemIndex)
                                            }
                                        },
                                        shape = RectangleShape
                                    ) {
                                        Text(
                                            text = "$itemIndex",
                                            style = if (hourListState.firstVisibleItemIndex == itemIndex && !hourListState.isScrollInProgress) {
                                                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            } else {
                                                MaterialTheme.typography.bodySmall
                                            }
                                        )
                                    }
                                }

                                items(
                                    count = 2,
                                    key = { index -> "hour-spacer-bottom-$index" },
                                    contentType = { "spacer" }
                                ) {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }
                            }

                            VerticalDivider()

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .height((48 * 5).dp),
                                state = minuteListState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)
                            ) {
                                items(
                                    count = 2,
                                    key = { index -> "minute-spacer-top-$index" },
                                    contentType = { "spacer" }
                                ) {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }

                                items(
                                    count = 60,
                                    key = { itemIndex -> "minute-item-$itemIndex" },
                                    contentType = { "minute-item" }
                                ) { itemIndex ->
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        onClick = {
                                            coroutineScope.launch {
                                                minuteListState.animateScrollToItem(index = itemIndex)
                                            }
                                        },
                                        shape = RectangleShape
                                    ) {
                                        Text(
                                            text = "$itemIndex",
                                            style = if (minuteListState.firstVisibleItemIndex == itemIndex && !minuteListState.isScrollInProgress) {
                                                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            } else {
                                                MaterialTheme.typography.bodySmall
                                            }
                                        )
                                    }
                                }

                                items(
                                    count = 2,
                                    key = { index -> "minute-spacer-bottom-$index" },
                                    contentType = { "spacer" }
                                ) {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }
                            }

                            VerticalDivider()

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .height((48 * 5).dp),
                                state = secondListState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = secondListState)
                            ) {
                                items(
                                    count = 2,
                                    key = { index -> "second-spacer-top-$index" },
                                    contentType = { "spacer" }
                                ) {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }

                                items(
                                    count = 60,
                                    key = { itemIndex -> "second-item-$itemIndex" },
                                    contentType = { "second-item" }
                                ) { itemIndex ->
                                    TextButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        onClick = {
                                            coroutineScope.launch {
                                                secondListState.animateScrollToItem(index = itemIndex)
                                            }
                                        },
                                        shape = RectangleShape
                                    ) {
                                        Text(
                                            text = "$itemIndex",
                                            style = if (secondListState.firstVisibleItemIndex == itemIndex && !secondListState.isScrollInProgress) {
                                                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            } else {
                                                MaterialTheme.typography.bodySmall
                                            }
                                        )
                                    }
                                }

                                items(
                                    count = 2,
                                    key = { index -> "second-spacer-bottom-$index" },
                                    contentType = { "spacer" }
                                ) {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }
                            }

                            VerticalDivider()
                        }
                    }
                } else { // 스톱 워치 시작, 중지 상태
                    AnimatedContent(
                        targetState = timerViewBarVisible,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                    ) { isVisible: Boolean ->
                        if (isVisible) {
                            val currentPage = pagerState.currentPage

                            TabRow(
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                                    .clip(
                                        shape = MaterialTheme.shapes.medium.copy(
                                            bottomStart = CornerSize(0),
                                            bottomEnd = CornerSize(0)
                                        )
                                    ),
                                containerColor = MaterialTheme.colorScheme.surface, // 색상 지정안하니 기본 색상이 지정됨.
                                selectedTabIndex = currentPage,
                                indicator = { tabPositions ->
                                    TabRowDefaults.PrimaryIndicator(
                                        modifier = Modifier
                                            .tabIndicatorOffset(tabPositions[currentPage]),
                                        width = tabPositions[currentPage].contentWidth
                                    )
                                }
                            ) {
                                Tab(
                                    selected = currentPage == 0,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    },
                                    text = { Text(text = "이미지")}
                                )

                                Tab(
                                    selected = currentPage == 1,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    },
                                    text = { Text(text = "시간")}
                                )

                                Tab(
                                    selected = currentPage == 2,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(2)
                                        }
                                    },
                                    text = { Text(text = "기록")}
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(48.dp))
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
                                    Box(
                                        modifier = Modifier
                                            .size(240.dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                    )

                                    // TODO: 이미지 준비되면 복구
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
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Text(
                                            modifier = Modifier
                                                .align(alignment = Alignment.CenterEnd),
                                            text = timerViewModel.getDurationTimeString(selectedTime),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                            1 -> { // 시간
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = timerViewModel.getTimerDurationString(duration = remainingTime),
                                    textAlign = TextAlign.Center
                                )
                            }
                            2 -> { // 기록
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                                        .border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                ) {
                                    item(
                                        key = "title",
                                        contentType = "list-item"
                                    ) {
                                        ListItem(
                                            headlineContent = {
                                                Text(text = "제목")
                                            },
                                            supportingContent = {
                                                Text(text = currentWiD.title.kr)
                                            }
                                        )
                                    }

                                    item(
                                        key = "sub-title",
                                        contentType = "list-item"
                                    ) {
                                        ListItem(
                                            headlineContent = {
                                                Text(text = "부제목")
                                            },
                                            supportingContent = {
                                                Text(text = currentWiD.subTitle.kr)
                                            }
                                        )
                                    }

                                    item(
                                        key = "start",
                                        contentType = "list-item"
                                    ) {
                                        ListItem(
                                            headlineContent = {
                                                Text(text = "시작")
                                            },
                                            supportingContent = {
                                                Text(text = timerViewModel.getDateTimeString(dateTime = currentWiD.start))
                                            }
                                        )
                                    }

                                    item(
                                        key = "finish",
                                        contentType = "list-item"
                                    ) {
                                        ListItem(
                                            headlineContent = {
                                                Text(text = "종료")
                                            },
                                            supportingContent = {
                                                Text(text = timerViewModel.getDateTimeString(dateTime = currentWiD.finish))
                                            }
                                        )
                                    }

                                    item(
                                        key = "duration",
                                        contentType = "list-item"
                                    ) {
                                        ListItem(
                                            headlineContent = {
                                                Text(text = "소요")
                                            },
                                            supportingContent = {
                                                Text(text = timerViewModel.getDurationString(duration = currentWiD.duration))
                                            }
                                        )
                                    }

                                    item(
                                        key = "exp",
                                        contentType = "list-item"
                                    ) {
                                        ListItem(
                                            headlineContent = {
                                                Text(text = "경험치")
                                            },
                                            supportingContent = {
                                                Text(text = "${currentWiD.exp}")
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
    )
}