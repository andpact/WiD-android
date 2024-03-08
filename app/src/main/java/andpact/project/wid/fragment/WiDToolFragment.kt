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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    val datePickerExpanded = wiDListViewModel.expandDatePicker.value

    // 화면
    val pages = listOf("스톱 워치", "타이머", "WiD 리스트")
    val pagerState = rememberPagerState(
        initialPage = if (timerViewModel.timerState.value != PlayerState.Stopped) 1 else 0,
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        Log.d("WiDToolFragment", "WiDToolFragment is being composed")

        onDispose {
            Log.d("WiDToolFragment", "WiDToolFragment is being disposed")
        }
    }

    BackHandler(
        enabled = datePickerExpanded,
        onBack = {
            wiDListViewModel.setExpandDatePicker(expand = false)
        }
    )

//    val alpha by animateFloatAsState(
//        targetValue = if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped) {
//            1f
//        } else {
//            0f
//        }
//    )

//    // Animatable을 사용하여 불투명도 애니메이션을 만듭니다.
//    val alphaValue = remember { Animatable(if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped) 1f else 0f) }
//
//    // 불투명도 애니메이션을 정의합니다.
//    val alphaAnimationSpec = tween<Float>(
//        durationMillis = 500, // 애니메이션 지속 시간 (밀리초)
//        easing = LinearEasing // 애니메이션 이징 함수
//    )
//
//    // 불투명도를 서서히 변경하는 애니메이션을 시작합니다.
//    LaunchedEffect(stopwatchViewModel.stopwatchState.value, timerViewModel.timerState.value) {
//        alphaValue.animateTo(
//            targetValue = if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped) 1f else 0f,
//            animationSpec = alphaAnimationSpec
//        )
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.secondary)
                    .alpha(
                        if (stopwatchViewModel.stopwatchTopBottomBarVisible.value && timerViewModel.timerTopBottomBarVisible.value) {
                            1f
                        } else {
                            0f
                        }
                    )
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

            // 대화상자 제거용
            if (datePickerExpanded) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
//                            enabled = datePickerExpanded,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            wiDListViewModel.setExpandDatePicker(expand = false)
                        }
                )
            }
        }

        /**
         * 상단 탭
         */
        Box {
            ScrollableTabRow(
                modifier = Modifier
    //                .alpha(alphaValue.value),
    //                .alpha(alpha),
                    .alpha(
                        if (stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped) {
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
                            color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else DarkGray
                        ) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
    //                            pagerState.animateScrollToPage(index) // 애니메이션 스크롤은 중간의 모든 프래그먼트를 불필요하게 만들게 됨.
                                pagerState.scrollToPage(index)
                            }
                        },
                        enabled = stopwatchViewModel.stopwatchState.value == PlayerState.Stopped && timerViewModel.timerState.value == PlayerState.Stopped
                    )
                }
            }

            // 대화상자 제거용
            if (datePickerExpanded) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
//                            enabled = datePickerExpanded,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            wiDListViewModel.setExpandDatePicker(expand = false)
                        }
                )
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

//@Preview(showBackground = true)
//@Composable
//fun WiDToolFragmentPreview() {
//    WiDToolFragment()
//}