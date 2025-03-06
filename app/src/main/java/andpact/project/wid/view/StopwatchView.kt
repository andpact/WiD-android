package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.SubTitle
import andpact.project.wid.model.Title
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.StopwatchViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Duration
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StopwatchView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    onBackButtonPressed: () -> Unit,
    onTitlePickerClicked: () -> Unit,
    stopwatchViewModel: StopwatchViewModel = hiltViewModel()
) {
    val TAG = "StopwatchView"

    val WID_LIST_LIMIT_PER_DAY = stopwatchViewModel.WID_LIST_LIMIT_PER_DAY

//    val now = stopwatchViewModel.now.value

    val currentWiD = stopwatchViewModel.currentWiD.value

    val wiDList = stopwatchViewModel.wiDList.value

    val user = stopwatchViewModel.user.value
    val wiDMinLimit = user?.wiDMinLimit ?: Duration.ofMinutes(5)
    val wiDMaxLimit = user?.wiDMaxLimit ?: Duration.ofHours(12)

    // 화면
    val stopwatchViewBarVisible = stopwatchViewModel.stopwatchViewBarVisible.value
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 도구
    val playerState = stopwatchViewModel.playerState.value
    val totalDuration = stopwatchViewModel.totalDuration.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(enabled = !stopwatchViewBarVisible) {
        stopwatchViewModel.setStopwatchViewBarVisible(true)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // 설정 해줘야 함.
        topBar = {
            AnimatedContent(
                targetState = stopwatchViewBarVisible,
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
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                        )

                        CenterAlignedTopAppBar(
                            navigationIcon = {
                                IconButton(
                                    onClick = { onBackButtonPressed() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "뒤로 가기",
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = "스톱워치",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
//                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                    targetState = stopwatchViewBarVisible,
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
                                    containerColor = if (playerState == PlayerState.STARTED) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (playerState == PlayerState.STARTED) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                                onClick = {
                                    if (playerState == PlayerState.STARTED) { // 시작 상태
                                        stopwatchViewModel.pauseStopwatch()
                                    } else { // 중지 및 정지 상태
                                        stopwatchViewModel.startStopwatch()
                                    }
                                },
                                enabled = wiDList.size <= WID_LIST_LIMIT_PER_DAY
                                        && currentWiD.title != Title.UNTITLED
                                        && currentWiD.subTitle != SubTitle.UNSELECTED_UNTITLED
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (playerState == PlayerState.STARTED) R.drawable.baseline_pause_24
                                        else R.drawable.baseline_play_arrow_24
                                    ),
                                    contentDescription = when (playerState) {
                                        PlayerState.STARTED -> "스톱워치 일시 정지"
                                        PlayerState.PAUSED, PlayerState.STOPPED -> "스톱워치 시작"
                                    }
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
                                        stopwatchViewModel.stopStopwatch()
                                    },
                                    enabled = playerState != PlayerState.STOPPED
                                ) {
                                    Text(text = "초기화")
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(64.dp))
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
                            .weight(1f), // 여기 설정해야 함 하위 컴포넌트가 아니라
                        visible = stopwatchViewBarVisible,
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
                                    text = stopwatchViewModel.getDurationString(wiDMinLimit),
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
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Text(
                                    text = stopwatchViewModel.getDurationString(wiDMaxLimit),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    OutlinedIconButton(
                        onClick = {
                            if (stopwatchViewBarVisible) {
                                stopwatchViewModel.setStopwatchViewBarVisible(false)
                            } else {
                                stopwatchViewModel.setStopwatchViewBarVisible(true)
                            }

//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(1)
//                            }
                        },
                        enabled = playerState == PlayerState.STARTED
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (stopwatchViewBarVisible) {
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
                        visible = stopwatchViewBarVisible,
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
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = WID_LIST_LIMIT_PER_DAY.toString(),
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = stopwatchViewBarVisible,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                ) { isVisible: Boolean ->
                    if (isVisible) {
                        val currentPage = pagerState.currentPage

                        TabRow(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
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
                ) { page ->
                    when (page) {
                        0 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
                            ) {
                                // TODO: 이미지 준비되면 복구
//                                Image(
//                                    modifier = Modifier
//                                        .padding(horizontal = 64.dp),
//                                    painter = painterResource(id = firstCurrentWiD.title.smallImage),
//                                    contentDescription = "제목 이미지"
//                                )

                                Box(
                                    modifier = Modifier
                                        .size(240.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                )

                                Text(
                                    text = stopwatchViewModel.getDurationTimeString(totalDuration),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        1 -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val hours = totalDuration.toHours()
                                val hourTens = hours / 10
                                val hourOnes = hours % 10

                                // 십의 자리
                                AnimatedContent(
                                    targetState = hourTens,
                                    transitionSpec = {
                                        slideInVertically(animationSpec = tween(300)) { it } +
                                                fadeIn(animationSpec = tween(300)) togetherWith
                                                slideOutVertically(animationSpec = tween(300)) { -it } +
                                                fadeOut(animationSpec = tween(300))
                                    }
                                ) { tensDigit ->
                                    Text(
                                        text = tensDigit.toString(),
                                        style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // 일의 자리
                                AnimatedContent(
                                    targetState = hourOnes,
                                    transitionSpec = {
                                        slideInVertically(animationSpec = tween(300)) { it } +
                                                fadeIn(animationSpec = tween(300)) togetherWith
                                                slideOutVertically(animationSpec = tween(300)) { -it } +
                                                fadeOut(animationSpec = tween(300))
                                    }
                                ) { onesDigit ->
                                    Text(
                                        text = onesDigit.toString(),
                                        style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                val minutes = (totalDuration.toMinutes() % 60).toInt()
                                val minuteTens = minutes / 10
                                val minuteOnes = minutes % 10

                                // 십의 자리
                                AnimatedContent(
                                    targetState = minuteTens,
                                    transitionSpec = {
                                        slideInVertically(animationSpec = tween(300)) { it } +
                                                fadeIn(animationSpec = tween(300)) togetherWith
                                                slideOutVertically(animationSpec = tween(300)) { -it } +
                                                fadeOut(animationSpec = tween(300))
                                    }
                                ) { tensDigit ->
                                    Text(
                                        text = tensDigit.toString(),
                                        style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // 일의 자리
                                AnimatedContent(
                                    targetState = minuteOnes,
                                    transitionSpec = {
                                        slideInVertically(animationSpec = tween(300)) { it } +
                                                fadeIn(animationSpec = tween(300)) togetherWith
                                                slideOutVertically(animationSpec = tween(300)) { -it } +
                                                fadeOut(animationSpec = tween(300))
                                    }
                                ) { onesDigit ->
                                    Text(
                                        text = onesDigit.toString(),
                                        style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                val seconds = (totalDuration.seconds % 60).toInt()
                                val secondTens = seconds / 10
                                val secondOnes = seconds % 10

                                // 십의 자리
                                AnimatedContent(
                                    targetState = secondTens,
                                    transitionSpec = {
                                        slideInVertically(animationSpec = tween(300)) { it } +
                                                fadeIn(animationSpec = tween(300)) togetherWith
                                                slideOutVertically(animationSpec = tween(300)) { -it } +
                                                fadeOut(animationSpec = tween(300))
                                    }
                                ) { tensDigit ->
                                    Text(
                                        text = tensDigit.toString(),
                                        style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                // 일의 자리
                                AnimatedContent(
                                    targetState = secondOnes,
                                    transitionSpec = {
                                        slideInVertically(animationSpec = tween(300)) { it } +
                                                fadeIn(animationSpec = tween(300)) togetherWith
                                                slideOutVertically(animationSpec = tween(300)) { -it } +
                                                fadeOut(animationSpec = tween(300))
                                    }
                                ) { onesDigit ->
                                    Text(
                                        text = onesDigit.toString(),
                                        style = MaterialTheme.typography.displayLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                        2 -> {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                            ) {
                                item(
                                    key = "title",
                                    contentType = "list-item"
                                ) {
                                    ListItem(
                                        colors = ListItemDefaults.colors(containerColor = Transparent),
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
                                        colors = ListItemDefaults.colors(containerColor = Transparent),
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
                                        colors = ListItemDefaults.colors(containerColor = Transparent),
                                        headlineContent = {
                                            Text(text = "시작")
                                        },
                                        supportingContent = {
                                            Text(text = if (playerState == PlayerState.STOPPED) {
//                                                    stopwatchViewModel.getDateTimeString(dateTime = now)
                                                    AnnotatedString("시작 전")
                                                } else {
                                                    stopwatchViewModel.getDateTimeString(dateTime = currentWiD.start)
                                                }
                                            )
                                        }
                                    )
                                }

                                item(
                                    key = "finish",
                                    contentType = "list-item"
                                ) {
                                    ListItem(
                                        colors = ListItemDefaults.colors(containerColor = Transparent),
                                        headlineContent = {
                                            Text(text = "종료")
                                        },
                                        supportingContent = {
                                            Text(text = if (playerState == PlayerState.STOPPED) {
                                                    AnnotatedString("시작 전")
                                                } else {
                                                    stopwatchViewModel.getDateTimeString(dateTime = currentWiD.finish)
                                                }
                                            )
                                        }
                                    )
                                }

                                item(
                                    key = "duration",
                                    contentType = "list-item"
                                ) {
                                    ListItem(
                                        colors = ListItemDefaults.colors(containerColor = Transparent),
                                        headlineContent = {
                                            Text(text = "소요")
                                        },
                                        supportingContent = {
                                            Text(text = stopwatchViewModel.getDurationString(duration = currentWiD.duration))
                                        }
                                    )
                                }

                                item(
                                    key = "exp",
                                    contentType = "list-item"
                                ) {
                                    ListItem(
                                        colors = ListItemDefaults.colors(containerColor = Transparent),
                                        headlineContent = {
                                            Text(text = "경험치")
                                        },
                                        supportingContent = {
                                            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

                                            Text(text = numberFormat.format(currentWiD.exp))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}