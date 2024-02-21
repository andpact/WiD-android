package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.White
import andpact.project.wid.util.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WiDToolFragment(navController: NavController, stopwatchPlayer: StopwatchPlayer, timerPlayer: TimerPlayer) {
    // 화면
    val pages = listOf("스톱 워치", "타이머", "WiD 리스트")
    val pagerState = rememberPagerState(pageCount = { pages.size })

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(
                    if (stopwatchPlayer.stopwatchTopBottomBarVisible.value && timerPlayer.timerTopBottomBarVisible.value) {
                        1f
                    } else {
                        0f
                    }
                ) // 배경 색 전에 선언해야 배경색까지 사라짐.
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable(stopwatchPlayer.stopwatchTopBottomBarVisible.value && timerPlayer.timerTopBottomBarVisible.value) {
                        navController.popBackStack()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "WiD 도구",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        /**
         * 상단 탭
         */
        ScrollableTabRow(
            modifier = Modifier
                .alpha(
                    if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped && timerPlayer.timerState.value == PlayerState.Stopped) {
                        1f
                    } else {
                        0f
                    }
                ),
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
                        color = MaterialTheme.colorScheme.primary
                    )},
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    enabled = stopwatchPlayer.stopwatchState.value == PlayerState.Stopped && timerPlayer.timerState.value == PlayerState.Stopped
                )
            }
        }

        /**
         * 컨텐츠
         */
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = stopwatchPlayer.stopwatchState.value == PlayerState.Stopped && timerPlayer.timerState.value == PlayerState.Stopped
        ) { page ->
            when (page) {
                0 -> StopWatchFragment(navController = navController, stopwatchPlayer = stopwatchPlayer)
                1 -> TimerFragment(navController = navController, timerPlayer = timerPlayer)
                2 -> WiDListFragment(navController = navController)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WiDToolFragmentPreview() {
//    WiDToolFragment()
//}