package andpact.project.wid.view

import andpact.project.wid.ui.theme.Typography
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchView(
    onDiaryClicked: () -> Unit,
    onWiDClicked: () -> Unit
) {
    val TAG = "SearchView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val pageList = listOf("기록", "다이어리")
    val pagerState = rememberPagerState(pageCount = { pageList.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "검색",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
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
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)),
                    containerColor = MaterialTheme.colorScheme.surface, // 색상 지정안하니 기본 색상이 지정됨.
                    selectedTabIndex = pagerState.currentPage,
                    divider = {},
                    edgePadding = 0.dp
                ) {
                    pageList.forEachIndexed { index: Int, _: String ->
                        Tab(
                            text = {
                                Text(
                                    text = pageList[index],
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
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

                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> WiDSearchView()
                        1 -> DiarySearchView()
                    }
                }
            }
        }
    )
}