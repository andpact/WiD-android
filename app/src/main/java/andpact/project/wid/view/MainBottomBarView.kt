package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.Tool
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.MainBottomBarViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainBottomBarView(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    currentRoute: String?,
    onDestinationChanged: (destination: String) -> Unit,
    onCurrentToolClicked: (currentTool: Tool) -> Unit,
    mainBottomBarViewModel: MainBottomBarViewModel = hiltViewModel()
) {
    val currentWiD = mainBottomBarViewModel.currentWiD.value

    val totalDuration = mainBottomBarViewModel.totalDuration.value
    val selectedTime = mainBottomBarViewModel.selectedTime.value
    val remainingTime = mainBottomBarViewModel.remainingTime.value

//    val user = mainBottomBarViewModel.user.value
//    val wiDMaxLimit = user?.wiDMaxLimit ?: Duration.ZERO

    val playerState = mainBottomBarViewModel.playerState.value

    val destinationList = mainBottomBarViewModel.destinationList

//    val density = LocalDensity.current
//    val statusBarHeight = WindowInsets.statusBars.getTop(density).dp
//    val navigatorBarHeight = WindowInsets.navigationBars.getBottom(density).dp

//    val view = LocalView.current
//    val density = LocalDensity.current
//    var statusBarHeight by remember { mutableStateOf(0.dp) }
//
//    // WindowInsetsCompat을 사용하여 상태 표시줄 높이 가져오기
//    LaunchedEffect(view) {
//        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
//            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
//            statusBarHeight = with(density) { topInset.toDp() } // px → dp 변환
//            insets
//        }
//    }

    DisposableEffect(Unit) {
        val TAG = "MainBottomBarView"
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // 네비게이션 지웠을 때의 색상
    ) {
        if (currentWiD.tool != Tool.NONE && playerState != PlayerState.STOPPED) {
            ListItem(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            onCurrentToolClicked(currentWiD.tool)
                        }
                    ),
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = MaterialTheme.shapes.medium
                            )
                    )

                    // TODO: 이미지 만들면 복구
//                    Image(
//                        modifier = Modifier
//                            .size(56.dp)
//                            .clip(MaterialTheme.shapes.medium),
//                        painter = painterResource(id = firstCurrentWiD.title.smallImage),
//                        contentDescription = "현재 제목"
//                    )
                },
                headlineContent = {
                    Text(text = currentWiD.subTitle.kr + ", " + currentWiD.title.kr,)
                },
                supportingContent = {
                    Text(
                        text = when (currentWiD.tool) {
                            Tool.STOPWATCH -> mainBottomBarViewModel.getDurationTimeString(totalDuration) + " / ${Tool.STOPWATCH.kr}" // 스톱 워치
                            else -> mainBottomBarViewModel.getDurationTimeString(remainingTime) + " / ${Tool.TIMER.kr}" // 타이머
                        },
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-1).sp
                    )
                },
                trailingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedIconButton(
                            onClick = {
                                when (playerState) {
                                    PlayerState.STARTED -> {
                                        when (currentWiD.tool) {
                                            Tool.STOPWATCH -> mainBottomBarViewModel.pauseStopwatch() // 스톱 워치
                                            else -> mainBottomBarViewModel.pauseTimer() // 타이머
                                        }
                                    }
                                    else -> {
                                        when (currentWiD.tool) {
                                            Tool.STOPWATCH -> mainBottomBarViewModel.startStopwatch() // 스톱 워치
                                            else -> mainBottomBarViewModel.startTimer() // 타이머
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (playerState) {
                                        PlayerState.STARTED -> R.drawable.baseline_pause_24 // 스톱 워치
                                        else -> R.drawable.baseline_play_arrow_24 // 타이머
                                    }
                                ),
                                contentDescription = "스톱 워치 또는 타이머 시작 및 중지",
                            )
                        }

                        OutlinedIconButton(
                            onClick = {
                                when (currentWiD.tool) {
                                    Tool.STOPWATCH -> mainBottomBarViewModel.stopStopwatch() // 스톱 워치
                                    else -> mainBottomBarViewModel.stopTimer() // 타이머
                                }
                            },
                            enabled = playerState == PlayerState.PAUSED
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "스톱워치 또는 타이머 초기화",
                            )
                        }
                    }
                }
            )

//            val progress = if (currentWiD.tool == Tool.STOPWATCH) { // 스톱 워치 소요 시간
//                if (wiDMaxLimit.seconds > 0) { // 0으로 나누는 오류 방지
//                    totalDuration.seconds / wiDMaxLimit.seconds.toFloat()
//                } else {
//                    0f
//                }
//            } else { // 타이머 남은 시간
//                val elapsedTime = selectedTime - remainingTime
//                if (selectedTime.seconds > 0) { // 0으로 나누는 오류 방지
//                    elapsedTime.seconds / selectedTime.seconds.toFloat()
//                } else {
//                    0f
//                }
//            }

            val elapsedTime = selectedTime - remainingTime
            val timerProgress = if (selectedTime.seconds > 0) { // 0으로 나누는 오류 방지
                elapsedTime.seconds / selectedTime.seconds.toFloat()
            } else {
                0f
            }

            if (currentWiD.tool == Tool.TIMER) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    progress = timerProgress.coerceIn(0f, 1f)
                )
            }
        }

        NavigationBar(
            containerColor = Transparent, // surface가 적용이 안됨.
        ) {
            destinationList.forEach { destination: MainViewDestinations ->
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == destination.route) destination.selectedIcon else destination.unselectedIcon),
                            contentDescription = "네비게이션 이동"
                        )
                    },
                    label = {
                        Text(text = destination.title)
                    },
                    selected = currentRoute == destination.route,
                    onClick = {
                        onDestinationChanged(destination.route)
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
}