package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Black
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.White
import andpact.project.wid.util.PlayerState
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.defaultTitleColorMapWithColors
import andpact.project.wid.util.getDurationString
import andpact.project.wid.viewModel.MainBottomBarViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainBottomBarView(
    currentRoute: String?,
    hideMainViewBar: Boolean,
    onDestinationChanged: (String) -> Unit,
    mainBottomBarViewModel: MainBottomBarViewModel = hiltViewModel()
) {
    val titleColorMap = mainBottomBarViewModel.titleColorMap.value

    val stopwatchTitle = mainBottomBarViewModel.stopwatchTitle.value
    val stopwatchDuration = mainBottomBarViewModel.stopwatchDuration.value
    val stopwatchState = mainBottomBarViewModel.stopwatchState.value

    val timerTitle = mainBottomBarViewModel.timerTitle.value
    val timerRemainingTime = mainBottomBarViewModel.timerRemainingTime.value
    val timerState = mainBottomBarViewModel.timerState.value

    val destinationList = listOf(
        MainViewDestinations.HomeViewDestination,
        MainViewDestinations.WiDToolViewDestination,
        MainViewDestinations.WiDDisplayViewDestination,
        MainViewDestinations.DiaryDisplayViewDestination,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .alpha(if (hideMainViewBar) 0f else 1f)
    ) {
        if (stopwatchState != PlayerState.Stopped && currentRoute != MainViewDestinations.WiDToolViewDestination.route) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(titleColorMap[stopwatchTitle] ?: DarkGray)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stopwatchTitle,
                        style = Typography.labelMedium,
                        color = titleColorMap[stopwatchTitle]?.let { textColor ->
                            if (textColor.luminance() > 0.5f) {
                                Black
                            } else {
                                White
                            }
                        } ?: MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getDurationString(stopwatchDuration, 0),
                        style = Typography.labelMedium,
                        color = titleColorMap[stopwatchTitle]?.let { textColor ->
                            if (textColor.luminance() > 0.5f) {
                                Black
                            } else {
                                White
                            }
                        } ?: MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (stopwatchState == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                mainBottomBarViewModel.stopStopwatch()
                            }
//                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 초기화",
                        tint = titleColorMap[stopwatchTitle]?.let { textColor ->
                            if (textColor.luminance() > 0.5f) {
                                Black
                            } else {
                                White
                            }
                        } ?: MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { // 스톱 워치 모든 상태일 때 클릭 가능
                            if (stopwatchState == PlayerState.Started) { // 스톱 워치 시작 상태
                                mainBottomBarViewModel.pauseStopwatch()
                            } else { // 스톱 워치 중지, 정지 상태
                                mainBottomBarViewModel.startStopwatch()
                            }
                        }
//                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(
                        id = if (stopwatchState == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "스톱 워치 시작 및 중지",
                    tint = titleColorMap[stopwatchTitle]?.let { textColor ->
                        if (textColor.luminance() > 0.5f) {
                            Black
                        } else {
                            White
                        }
                    } ?: MaterialTheme.colorScheme.primary
                )
            }
        } else if (timerState != PlayerState.Stopped && currentRoute != MainViewDestinations.WiDToolViewDestination.route) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(titleColorMap[timerTitle] ?: DarkGray)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = timerTitle,
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getDurationString(timerRemainingTime, 0),
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (timerState == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                mainBottomBarViewModel.stopTimer()
                            }
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "타이머 초기화",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { // 타이머 모든 상태일 때 클릭 가능
                            if (timerState == PlayerState.Started) { // 타이머 시작 상태
                                mainBottomBarViewModel.pauseTimer()
                            } else { // 타이머 중지, 정지 상태
                                mainBottomBarViewModel.startTimer()
                            }
                        }
                        .size(24.dp),
                    painter = painterResource(
                        id = if (timerState == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "타이머 시작 및 중지",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) { // 리플 제거용
            NavigationBar(
                modifier = Modifier
                    .height(56.dp),
                //            containerColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.tertiary,
            ) {
                destinationList.forEach { destination ->
                    NavigationBarItem(
                        alwaysShowLabel = false,
                        icon = {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                painter = painterResource(id = destination.icon),
                                contentDescription = ""
                            ) },
                        selected = currentRoute == destination.route,
                        onClick = {
                            onDestinationChanged(destination.route)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedTextColor = DarkGray,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = DarkGray,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        ),
                        enabled = !hideMainViewBar
                    )
                }
            }
        }
    }
}