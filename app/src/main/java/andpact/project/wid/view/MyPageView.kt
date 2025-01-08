package andpact.project.wid.view

import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.City
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyPageView(
    onCityPickerClicked: (currentCity: City) -> Unit,
    onUserSignedOut: () -> Unit,
    onUserDeleted: (Boolean) -> Unit,
) {
    val TAG = "MyPageView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val pageList = listOf("계정", "기록")
    val pagerState = rememberPagerState(pageCount = { pageList.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = MainViewDestinations.MyPageViewDestination.title)
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

                HorizontalPager(state = pagerState) { page: Int ->
                    when (page) {
                        0 -> MyAccountView(
                            onCityPickerClicked = { selectedCity: City ->
                                onCityPickerClicked(selectedCity)
                            },
                            onUserSignedOut = {
                                onUserSignedOut()
                            },
                            onUserDeleted = { userDeleted: Boolean ->
                                if (userDeleted) {
                                    onUserDeleted(true)
                                }
                            }
                        )
                        1 -> MyWiDView()
                    }
                }
            }
        }
    )
}