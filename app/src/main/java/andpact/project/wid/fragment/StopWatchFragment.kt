package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.StopwatchViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StopWatchFragment(stopwatchViewModel: StopwatchViewModel) {
    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        Log.d("StopWatchFragment", "StopWatchFragment is being composed")

        onDispose {
            Log.d("StopWatchFragment", "StopWatchFragment is being disposed")
        }
    }

    BackHandler(enabled = titleMenuExpanded) {
        titleMenuExpanded = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(
                enabled = stopwatchViewModel.stopwatchState.value == PlayerState.Started,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { // 스톱 워치가 시작된 상태에서만 상, 하단 바 숨길 수 있도록
                if (stopwatchViewModel.stopwatchTopBottomBarVisible.value) {
                    stopwatchViewModel.setStopwatchTopBottomBarVisible(false)
                } else {
                    stopwatchViewModel.setStopwatchTopBottomBarVisible(true)
                }
            }
    ) {
        /**
         * 컨텐츠
         */
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = getStopWatchTimeString(stopwatchViewModel.duration.value),
            style = TextStyle(
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary
            )
        )

        /**
         * 하단 바
         */
        if (stopwatchViewModel.stopwatchTopBottomBarVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            enabled = stopwatchViewModel.stopwatchState.value == PlayerState.Stopped,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            titleMenuExpanded = true
                        }
                        .background(
                            color = if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped)
                                AppIndigo
                            else
                                DarkGray
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(titleIconMap[stopwatchViewModel.title.value] ?: R.drawable.baseline_menu_book_16),
                    contentDescription = "제목",
                    tint = White
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (stopwatchViewModel.stopwatchState.value == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                enabled = stopwatchViewModel.stopwatchState.value == PlayerState.Paused,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                stopwatchViewModel.stopStopwatch()
                            }
                            .background(color = DeepSkyBlue)
                            .padding(16.dp)
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 초기화",
                        tint = White
                    )
                }

                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { // 스톱 워치 모든 상태일 때 클릭 가능
                            if (stopwatchViewModel.stopwatchState.value == PlayerState.Started) { // 스톱 워치 시작 상태
                                stopwatchViewModel.pauseStopwatch()
                            } else { // 스톱 워치 중지, 정지 상태
                                stopwatchViewModel.startStopwatch()
                            }
                        }
                        .background(
                            color = if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.primary
                            else if (stopwatchViewModel.stopwatchState.value == PlayerState.Paused) LimeGreen
                            else OrangeRed
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(
                        id = if (stopwatchViewModel.stopwatchState.value == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "스톱 워치 시작 및 중지",
                    tint = if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.secondary else White
                )
            }
        }

        // 컴포저블 안에 기본적으로 박스가 감싸고 있는 구조인 듯.
        // 위 전체 화면 박스에 배경색을 넣어놔서 아래 박스는 배경 색을 넣을 필요 없음.
        /**
         * 제목 바텀 시트
         */
        if (titleMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = titleMenuExpanded,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        titleMenuExpanded = false
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(screenHeight / 2)
                        .padding(16.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = MaterialTheme.colorScheme.primary,
                        )
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(titles.size) { index ->
                            val itemTitle = titles[index]
                            val iconResourceId = titleIconMap[itemTitle] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        stopwatchViewModel.setTitle(itemTitle)
                                        titleMenuExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(id = iconResourceId),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = titleMap[itemTitle] ?: "공부",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )


                                RadioButton(
                                    selected = itemTitle == stopwatchViewModel.title.value,
                                    onClick = { },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}