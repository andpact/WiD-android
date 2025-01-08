package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.viewModel.MainBottomBarViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainBottomBarView(
    currentRoute: String?,
    onDestinationChanged: (destination: String) -> Unit,
    onCurrentToolClicked: (currentTool: CurrentTool) -> Unit,
    mainBottomBarViewModel: MainBottomBarViewModel = hiltViewModel()
) {
    val firstCurrentWiD = mainBottomBarViewModel.firstCurrentWiD.value
    val secondCurrentWiD = mainBottomBarViewModel.secondCurrentWiD.value

    val totalDuration = mainBottomBarViewModel.totalDuration.value
    val remainingTime = mainBottomBarViewModel.remainingTime.value

    val currentToolState = mainBottomBarViewModel.currentToolState.value

    val destinationList = mainBottomBarViewModel.destinationList

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
        if (firstCurrentWiD.createdBy != CurrentTool.NONE && currentToolState != CurrentToolState.STOPPED) {
            ListItem(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            onCurrentToolClicked(firstCurrentWiD.createdBy)
                        }
                    ),
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                leadingContent = {
                    Image(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(MaterialTheme.shapes.medium),
                        painter = painterResource(id = firstCurrentWiD.title.smallImage),
                        contentDescription = "현재 제목"
                    )
                },
                headlineContent = {
                    Text(text = firstCurrentWiD.title.kr)
                },
                supportingContent = {
                    Text(
                        text = when (firstCurrentWiD.createdBy) {
                            CurrentTool.STOPWATCH -> mainBottomBarViewModel.getDurationString(totalDuration) + " / ${CurrentTool.STOPWATCH.kr}" // 스톱 워치
                            else -> mainBottomBarViewModel.getDurationString(remainingTime) + " / ${CurrentTool.TIMER.kr}" // 타이머
                        },
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-1).sp
                    )
                },
                trailingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                when (currentToolState) {
                                    CurrentToolState.STARTED -> {
                                        when (firstCurrentWiD.createdBy) {
                                            CurrentTool.STOPWATCH -> mainBottomBarViewModel.pauseStopwatch() // 스톱 워치
                                            else -> mainBottomBarViewModel.pauseTimer() // 타이머
                                        }
                                    }
                                    else -> {
                                        when (firstCurrentWiD.createdBy) {
                                            CurrentTool.STOPWATCH -> mainBottomBarViewModel.startStopwatch() // 스톱 워치
                                            else -> mainBottomBarViewModel.startTimer() // 타이머
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (currentToolState) {
                                        CurrentToolState.STARTED -> R.drawable.baseline_pause_24 // 스톱 워치
                                        else -> R.drawable.baseline_play_arrow_24 // 타이머
                                    }
                                ),
                                contentDescription = "스톱 워치 또는 타이머 시작 및 중지",
                            )
                        }

                        IconButton(
                            onClick = {
                                when (firstCurrentWiD.createdBy) {
                                    CurrentTool.STOPWATCH -> mainBottomBarViewModel.stopStopwatch() // 스톱 워치
                                    else -> mainBottomBarViewModel.stopTimer() // 타이머
                                }
                            },
                            enabled = currentToolState == CurrentToolState.PAUSED
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "스톱워치 또는 타이머 초기화",
                            )
                        }
                    }
                }
            )
        }

        NavigationBar(
            containerColor = Color.Transparent, // surface가 적용이 안됨.
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