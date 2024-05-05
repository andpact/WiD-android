package andpact.project.wid.view

import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.WiDToolViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WiDToolView(
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    onHideWiDToolViewBarChanged: (Boolean) -> Unit,
    wiDToolViewModel: WiDToolViewModel = hiltViewModel()
) {
    val TAG = "WiDToolView"

    val stopwatchState = wiDToolViewModel.stopwatchState.value
    val timerState = wiDToolViewModel.timerState.value
    val hideWiDToolViewBar = wiDToolViewModel.hideWiDToolViewBar.value

    // 화면
    val pages = wiDToolViewModel.pages
    val pagerState = rememberPagerState(
        initialPage = if (timerState != PlayerState.Stopped) 1 else 0, // 스톱 워치가 0 페이지니까, 타이머 실행 중일 때만 1페이지로 초기화함.
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    // 여기를 눌러도 스톱워치, 타이머의 상, 하단 바 사라지고 나오도록.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /** 상단 바 */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .alpha(if (hideWiDToolViewBar) 0f else 1f)
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "도구",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
        }

        /** 상단 탭 */
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            ScrollableTabRow(
                modifier = Modifier
                    .alpha(if (stopwatchState == PlayerState.Stopped && timerState == PlayerState.Stopped) 1f else 0f)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
                    .clickable {

                    },
                containerColor = MaterialTheme.colorScheme.secondary, // 색상 지정안하니 기본 색상이 지정됨.
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                edgePadding = 0.dp
            ) {
                pages.forEachIndexed { index: Int, _: String ->
                    Tab(
                        text = { Text(
                            text = pages[index],
                            style = Typography.bodyMedium,
                            color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else DarkGray
                        ) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        enabled = stopwatchState == PlayerState.Stopped && timerState == PlayerState.Stopped
                    )
                }
            }
        }

        /** 컨텐츠 */
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = stopwatchState == PlayerState.Stopped && timerState == PlayerState.Stopped
        ) { page ->
            when (page) {
                0 -> StopwatchView(
                    onStopwatchStateChanged = { newState ->
                        wiDToolViewModel.setStopwatchState(newState = newState)
                    },
                    onHideStopwatchViewBarChanged = { hide ->
                        wiDToolViewModel.setHideWiDToolViewBar(hide = hide)
                        onHideWiDToolViewBarChanged(hide) // Main View의 바텀 네비게이션 바 제거 용 콜백
                    }
                )
                1 -> TimerView(
                    onTimerStateChanged = { newState ->
                        wiDToolViewModel.setTimerState(newState = newState)
                    },
                    onHideTimerViewBarChanged = { hide ->
                        wiDToolViewModel.setHideWiDToolViewBar(hide = hide)
                        onHideWiDToolViewBarChanged(hide) // Main View의 바텀 네비게이션 바 제거 용 콜백
                    }
                )
                2 -> WiDListView(
                    onEmptyWiDClicked = {
                        onEmptyWiDClicked()
                    },
                    onWiDClicked = {
                        onWiDClicked()
                    }
                )
            }
        }
    }
}