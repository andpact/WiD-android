package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.LimeGreen
import andpact.project.wid.ui.theme.OrangeRed
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryDisplayFragment(navController: NavController, stopwatchPlayer: StopwatchPlayer, timerPlayer: TimerPlayer) {
    // 화면
    val pages = listOf("일별 조회", "랜덤 조회", "검색 조회")
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
//                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.primary
            )

            if (stopwatchPlayer.stopwatchState.value != PlayerState.Stopped) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.Center)
                        .background(
                            color = if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                                LimeGreen
                            } else {
                                OrangeRed
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = titleMap[stopwatchPlayer.title.value] ?: "공부",
                        style = Typography.labelMedium,
                        color = White
                    )

                    Text(
                        text = getDurationString(stopwatchPlayer.duration.value, 0),
                        style = Typography.labelMedium,
                        color = White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else if (timerPlayer.timerState.value != PlayerState.Stopped) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = if (timerPlayer.timerState.value == PlayerState.Started) {
                                LimeGreen
                            } else {
                                OrangeRed
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = titleMap[timerPlayer.title.value] ?: "공부",
                        style = Typography.labelMedium,
                        color = White
                    )

                    Text(
                        text = getDurationString(timerPlayer.remainingTime.value, 0),
                        style = Typography.labelMedium,
                        color = White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "다이어리 조회",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        /**
         * 상단 탭
         */
//        Row( // 좌, 우 상단 코너 만들기 위해 선언함.
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.background)
//                .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
//        ) {
            ScrollableTabRow(
                containerColor = MaterialTheme.colorScheme.secondary,
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
                        )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
//        }

        /**
         * 컨텐츠
         */
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> DayDiaryFragment(navController = navController)
                1 -> RandomDiaryFragment()
                2 -> SearchFragment(navController = navController)
            }
        }
    }
}