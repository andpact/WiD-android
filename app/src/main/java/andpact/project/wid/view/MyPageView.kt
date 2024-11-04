package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyPageView(
    onUserSignedOut: () -> Unit,
    onUserDeleted: (Boolean) -> Unit,
) {
    val TAG = "MyPageView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val pageList = listOf("계정", "제목", "도구")
    val pagerState = rememberPagerState(pageCount = { pageList.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "마이페이지",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
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
                    0 -> MyAccountView(
                        onUserSignedOut = {
                            onUserSignedOut()
                        },
                        onUserDeleted = { userDeleted: Boolean ->
                            if (userDeleted) {
                                onUserDeleted(true)
                            }
                        }
                    )
                    1 -> MyTitleView()
                    2 -> MyToolView()
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MyPagePreview() {
//
//}