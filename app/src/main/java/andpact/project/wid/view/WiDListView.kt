package andpact.project.wid.view

import andpact.project.wid.destinations.MainViewDestinations
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WiDListView(onWiDClicked: () -> Unit) {
    val TAG = "WiDListView"

    // 화면
    val pageList = listOf("일별 조회", "주별 조회", "월별 조회")
    val pagerState = rememberPagerState(pageCount = { pageList.size })
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
                title = {
                    Text(text = MainViewDestinations.WiDListViewDestination.title)
                }
            )
        },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                ScrollableTabRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface, // 색상 지정안하니 기본 색상이 지정됨.
                    selectedTabIndex = pagerState.currentPage,
                    divider = {},
                    edgePadding = 0.dp
                ) {
                    pageList.forEachIndexed { index: Int, pageName: String ->
                        Tab(
                            text = {
                                Text(text = pageName)
                            },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            }
                        )
                    }
                }

                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = pagerState,
//                    pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
//                        state = pagerState,
//                        orientation = Orientation.Horizontal
//                    )
                ) { page: Int ->
                    when (page) {
                        0 -> DailyWiDListView(
                            onWiDClicked = {
                                onWiDClicked()
                            },
                        )
                        1 -> WeeklyWiDListView()
                        2 -> MonthlyWiDListView()
                    }
                }
            }
        }
    )
}