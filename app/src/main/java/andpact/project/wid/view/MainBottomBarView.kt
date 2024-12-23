package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.viewModel.MainBottomBarViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(
                        onClick = {
                            onCurrentToolClicked(firstCurrentWiD.createdBy)
                        }
                    )
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .size(40.dp),
                    painter = painterResource(id = firstCurrentWiD.title.smallImage),
                    contentDescription = "현재 제목"
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = firstCurrentWiD.title.kr,
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(horizontal = 8.dp),
                            text = firstCurrentWiD.createdBy.kr,
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = when (firstCurrentWiD.createdBy) {
                            CurrentTool.STOPWATCH -> mainBottomBarViewModel.getDurationString(totalDuration) // 스톱 워치
                            else -> mainBottomBarViewModel.getDurationString(remainingTime) // 타이머
                        },
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = FontFamily.Monospace
                    )
                }

                FilledIconButton(
                    onClick = {
                        when (firstCurrentWiD.createdBy) {
                            CurrentTool.STOPWATCH -> mainBottomBarViewModel.stopStopwatch() // 스톱 워치
                            else -> mainBottomBarViewModel.stopTimer() // 타이머
                        }
                    },
                    enabled = currentToolState == CurrentToolState.PAUSED
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 또는 타이머 초기화",
                    )
                }


                FilledIconButton(
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
            }
        }

        NavigationBar(
            containerColor = Color.Transparent, // surface가 적용이 안됨.
        ) {
            destinationList.forEach { destination: MainViewDestinations ->
                NavigationBarItem(
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = if (currentRoute == destination.route) destination.selectedIcon else destination.unselectedIcon),
                            contentDescription = "네비게이션 이동"
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
                    colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
}