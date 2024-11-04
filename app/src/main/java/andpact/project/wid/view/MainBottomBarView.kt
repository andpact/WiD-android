package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.MainBottomBarViewModel
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomBarView(
    currentRoute: String?,
    mainViewBarVisible: Boolean,
    onDestinationChanged: (String) -> Unit,
    mainBottomBarViewModel: MainBottomBarViewModel = hiltViewModel()
) {
    val infiniteTransition = rememberInfiniteTransition()
//    val size by infiniteTransition.animateValue(
//        initialValue = 10.dp,
//        targetValue = 20.dp,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000),
//            repeatMode = RepeatMode.Reverse
//        )
//    )

    val title = mainBottomBarViewModel.title.value

    val badgeColor by infiniteTransition.animateColor(
        initialValue = titleColorMap[title] ?: MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        targetValue = titleColorMap[title] ?: MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )


//    val totalDuration = mainBottomBarViewModel.totalDuration.value
//    val remainingTime = mainBottomBarViewModel.remainingTime.value

//    val currentTool = mainBottomBarViewModel.currentTool.value
    val currentToolState = mainBottomBarViewModel.currentToolState.value
//    val currentTool = mainBottomBarViewModel.user.value?.currentTool
//    val currentToolState = mainBottomBarViewModel.user.value?.currentToolState

    val destinationList = mainBottomBarViewModel.destinationList

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // 네비게이션 지웠을 때의 색상
    ) {
//        if (currentTool != CurrentTool.NONE && currentToolState != CurrentToolState.STOPPED && currentRoute != MainViewDestinations.WiDToolViewDestination.route) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.primaryContainer)
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(24.dp),
//                    painter = painterResource(
//                        id = titleNumberStringToTitleIconMap[title] ?: R.drawable.baseline_title_24
//                    ),
//                    contentDescription = "도구 제목",
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//
//                Column(
//                    modifier = Modifier
//                        .weight(1f)
//                ) {
//                    Text(
//                        text = titleNumberStringToTitleKRStringMap[title] ?: "",
//                        style = Typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//
//                    Text(
//                        text = when (currentTool) {
//                            CurrentTool.STOPWATCH -> getDurationString(totalDuration, 0) // 스톱 워치
//                            else -> getDurationString(remainingTime, 0) // 타이머
//                        },
//                        style = Typography.labelMedium,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer,
//                        fontFamily = FontFamily.Monospace
//                    )
//                }
//
//                FilledIconButton(
//                    onClick = {
//                        when (currentTool) {
//                            CurrentTool.STOPWATCH -> mainBottomBarViewModel.stopStopwatch() // 스톱 워치
//                            else -> mainBottomBarViewModel.stopTimer() // 타이머
//                        }
//                    },
//                    enabled = currentToolState == CurrentToolState.PAUSED
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_stop_24),
//                        contentDescription = "스톱워치 또는 타이머 초기화",
//                    )
//                }
//
//
//                FilledIconButton(
//                    onClick = {
//                        when (currentToolState) {
//                            CurrentToolState.STARTED -> {
//                                when (currentTool) {
//                                    CurrentTool.STOPWATCH -> mainBottomBarViewModel.pauseStopwatch() // 스톱 워치
//                                    else -> mainBottomBarViewModel.pauseTimer() // 타이머
//                                }
//                            }
//                            else -> {
//                                when (currentTool) {
//                                    CurrentTool.STOPWATCH -> mainBottomBarViewModel.startStopwatch() // 스톱 워치
//                                    else -> mainBottomBarViewModel.startTimer() // 타이머
//                                }
//                            }
//                        }
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(
//                            id = when (currentToolState) {
//                                CurrentToolState.STARTED -> R.drawable.baseline_pause_24 // 스톱 워치
//                                else -> R.drawable.baseline_play_arrow_24 // 타이머
//                            }
//                        ),
//                        contentDescription = "스톱 워치 또는 타이머 시작 및 중지",
//                    )
//                }
//            }
//        }

        NavigationBar(
            modifier = Modifier
                .alpha(if (mainViewBarVisible) 1f else 0f)
        ) {
            destinationList.forEach { destination: MainViewDestinations ->
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (destination.route == MainViewDestinations.WiDToolViewDestination.route && currentRoute != destination.route) {
                                    if (currentToolState == CurrentToolState.STARTED) {
                                        Badge(
                                            modifier = Modifier
                                                .size(10.dp),
                                            containerColor = badgeColor,
                                        )
                                    } else if (currentToolState == CurrentToolState.PAUSED) {
                                        Badge(
                                            modifier = Modifier
                                                .size(10.dp),
                                            containerColor = Color.Green,
                                        )
                                    }
                                }
                            },
                            content = {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp),
                                    painter = painterResource(id = destination.icon),
                                    contentDescription = "네비게이션 이동"
                                )
                            }
                        )
                    },
                    label = {
                        Text(
                            text = destination.title ?: "",
                            style = Typography.bodySmall
                        )
                    },
                    selected = currentRoute == destination.route,
                    onClick = {
                        onDestinationChanged(destination.route)
                    },
                    enabled = mainViewBarVisible
                )
            }
        }
    }
}