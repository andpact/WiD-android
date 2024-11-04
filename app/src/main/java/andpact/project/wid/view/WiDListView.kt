package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WiDListView(
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
) {
    val TAG = "WiDListView"

    // 화면
//    val pages = listOf("일별 조회", "주별 조회", "제목별 조회")
    val pageList = listOf("일별 조회", "주별 조회")
    val pagerState = rememberPagerState(pageCount = { pageList.size })
    val coroutineScope = rememberCoroutineScope()

    var titleDurationMapMenuExpanded by remember { mutableStateOf(false) }

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
                    Text(
                        text = "리스트",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    if (pagerState.currentPage == 1) { // WeeklyWiDListView 일 때
                        Box {
                            IconButton(
                                onClick = {
                                    titleDurationMapMenuExpanded = true
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp),
                                    painter = painterResource(R.drawable.baseline_more_vert_24),
                                    contentDescription = "맵 선택",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            DropdownMenu(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface),
                                expanded = titleDurationMapMenuExpanded,
                                onDismissRequest = { titleDurationMapMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "총합",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        titleDurationMapMenuExpanded = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "평균",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        titleDurationMapMenuExpanded = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "최고",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        titleDurationMapMenuExpanded = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "최저",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        titleDurationMapMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
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
                    0 -> DailyWiDListView(
                        onEmptyWiDClicked = {
                            onEmptyWiDClicked()
                        },
                        onWiDClicked = {
                            onWiDClicked()
                        }
                    )
                    1 -> WeeklyWiDListView()
    //                2 -> TitleWiDView()
                }
            }
        }
    }
}