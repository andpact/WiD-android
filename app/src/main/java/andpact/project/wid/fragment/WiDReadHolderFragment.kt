package andpact.project.wid.fragment

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WiDReadHolderFragment(navController: NavController, buttonsVisible: MutableState<Boolean>) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = buttonsVisible.value,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Color.Black
                    )
                },
                containerColor = Color.White
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }},
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    text = {
                        Text(text = "날짜 별 기록")
                    }
                )

                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }},
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    text = {
                        Text(text = "기간 별 기록")
                    }
                )
            }
        }

        HorizontalPager(state = pagerState) {page ->
            when (page) {
                0 -> WiDReadDayFragment(navController = navController, buttonsVisible = buttonsVisible)
//                1 -> WiDReadCalendarFragment()
                1 -> PeriodBasedFragment()
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WiDReadHolderFragmentPreview() {
//    WiDReadHolderFragment()
//}