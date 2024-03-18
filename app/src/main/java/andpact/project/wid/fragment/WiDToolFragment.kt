package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Black
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.StopwatchViewModel
import andpact.project.wid.viewModel.TimerViewModel
import andpact.project.wid.viewModel.WiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WiDToolFragment(mainActivityNavController: NavController, stopwatchViewModel: StopwatchViewModel, timerViewModel: TimerViewModel) {
    // 뷰모델
    val wiDListViewModel: WiDListViewModel = viewModel()

    // 화면
    val pages = listOf("스톱 워치", "타이머", "WiD 리스트")
    val pagerState = rememberPagerState(
        initialPage = if (timerViewModel.timerState.value != PlayerState.Stopped) 1 else 0, // 스톱 워치가 0 페이지니까, 타이머 실행 중일 때만 1페이지로 초기화함.
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        Log.d("WiDToolFragment", "WiDToolFragment is being composed")

        onDispose {
            Log.d("WiDToolFragment", "WiDToolFragment is being disposed")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .alpha(
                    if (stopwatchViewModel.stopwatchTopBottomBarVisible.value && timerViewModel.timerTopBottomBarVisible.value) {
                        1f
                    } else {
                        0f
                    }
                )
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

        /**
         * 상단 탭
         */
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            ScrollableTabRow(
                modifier = Modifier
                    .alpha(
                        if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped) {
                            1f
                        } else {
                            0f
                        }
                    )
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)),
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
                        enabled = stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped
                    )
                }
            }
        }

        /**
         * 컨텐츠
         */
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped
        ) { page ->
            when (page) {
                0 -> StopWatchFragment(stopwatchViewModel = stopwatchViewModel)
                1 -> TimerFragment(timerViewModel = timerViewModel)
                2 -> WiDListFragment(mainActivityNavController = mainActivityNavController, wiDListViewModel = wiDListViewModel)
            }
        }
    }
}