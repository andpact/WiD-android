package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.DayDiaryViewModel
import andpact.project.wid.viewModel.SearchViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryDisplayFragment(mainActivityNavController: NavController) {
    // 화면
    val pages = listOf("일별 조회", "랜덤 조회", "검색 조회")
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    // 뷰모델
    val dayDiaryViewModel: DayDiaryViewModel = viewModel()
    val datePickerExpanded = dayDiaryViewModel.expandDatePicker.value
    val searchViewModel: SearchViewModel = viewModel()

    // 검색
//    val searchFilter = searchViewModel.searchFilter.value
    var searchFilterExpanded by remember { mutableStateOf(false) }

    BackHandler(
        enabled = datePickerExpanded,
        onBack = {
            dayDiaryViewModel.setExpandDatePicker(expand = false)
        }
    )

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
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다이어리",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (pagerState.currentPage == 2) {
                    Row(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                searchFilterExpanded = true
                            },
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        searchFilterExpanded = true
                                    },
                                painter = painterResource(R.drawable.baseline_filter_alt_24),
                                contentDescription = "검색 필터 설정",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            DropdownMenu(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.tertiary),
                                expanded = searchFilterExpanded,
                                onDismissRequest = { searchFilterExpanded = false }
                            ) {
                                searchFilterList.forEach { filter ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = searchFilterMap[filter] ?: "",
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        onClick = {
                                            searchViewModel.setSearchFilter(filter)
                                            searchFilterExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
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
                            dayDiaryViewModel.setExpandDatePicker(expand = false)
                        }
                )
            }
        }

        /**
         * 상단 탭
         */
        Box{
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
                            color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else DarkGray
                        ) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
    //                                pagerState.animateScrollToPage(index)
                                pagerState.scrollToPage(index)
                            }
                        }
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
                            dayDiaryViewModel.setExpandDatePicker(expand = false)
                        }
                )
            }
        }

        /**
         * 컨텐츠
         */
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> DayDiaryFragment(
                    mainActivityNavController = mainActivityNavController,
                    dayDiaryViewModel = dayDiaryViewModel
                )
                1 -> RandomDiaryFragment(mainActivityNavController = mainActivityNavController)
                2 -> SearchFragment(
                    mainActivityNavController = mainActivityNavController,
                    searchViewModel = searchViewModel
                )
            }
        }
    }
}