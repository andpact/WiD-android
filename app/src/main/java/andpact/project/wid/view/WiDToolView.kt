package andpact.project.wid.view

import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import andpact.project.wid.viewModel.WiDToolViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WiDToolView(
    onWiDToolViewBarVisibleChanged: (Boolean) -> Unit,
    wiDToolViewModel: WiDToolViewModel = hiltViewModel()
) {
    val TAG = "WiDToolView"

    val currentTool = wiDToolViewModel.currentTool.value
    val currentToolState = wiDToolViewModel.currentToolState.value
    val wiDToolViewBarVisible = wiDToolViewModel.wiDToolViewBarVisible.value

    // 화면
    val pages = wiDToolViewModel.pages
    val pagerState = rememberPagerState(
        initialPage = if (currentTool == CurrentTool.TIMER && currentToolState != CurrentToolState.STOPPED) 1 else 0, // 스톱 워치가 0 페이지니까, 타이머 실행 중일 때만 1페이지로 초기화함.
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .alpha(if (wiDToolViewBarVisible) 1f else 0f),
                title = {
                    Text(
                        text = "도구",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            ScrollableTabRow(
                modifier = Modifier
                    .alpha(if (wiDToolViewBarVisible) 1f else 0f)
//                    .alpha(if (currentToolState == CurrentToolState.STOPPED) 1f else 0f)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clip(RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp)
                    ),
                containerColor = MaterialTheme.colorScheme.surface, // 색상 지정안하니 기본 색상이 지정됨.
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                edgePadding = 0.dp
            ) {
                pages.forEachIndexed { index: Int, _: String ->
                    Tab(
                        text = {
                            Text(
                                text = pages[index],
                                style = Typography.bodyMedium,
                                color = if (currentToolState == CurrentToolState.STOPPED) {
                                    MaterialTheme.colorScheme.onSurface
                                } else if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        enabled = currentToolState == CurrentToolState.STOPPED
                    )
                }
            }

            /** 컨텐츠 */
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = currentToolState == CurrentToolState.STOPPED
            ) { page: Int ->
                when (page) {
                    0 -> StopwatchView(
                        onStopwatchViewBarVisibleChanged = { visible: Boolean ->
                            wiDToolViewModel.setWiDToolViewBarVisible(visible = visible)
                            onWiDToolViewBarVisibleChanged(visible) // Main View의 바텀 네비게이션 바 제거 용 콜백
                        }
                    )
                    1 -> TimerView(
                        onTimerViewBarVisibleChanged = { visible: Boolean ->
                            wiDToolViewModel.setWiDToolViewBarVisible(visible = visible)
                            onWiDToolViewBarVisibleChanged(visible) // Main View의 바텀 네비게이션 바 제거 용 콜백
                        }
                    )
                }
            }
        }
    }
}