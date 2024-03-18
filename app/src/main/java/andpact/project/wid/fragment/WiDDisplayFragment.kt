package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.*
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WiDDisplayFragment() {
    // 화면
    val pages = listOf("일별 조회", "주별 조회", "월별 조회", "제목별 조회")
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    // 뷰 모델(한 번에 다 선언해서 관리함)
    val dayWiDViewModel: DayWiDViewModel = viewModel()
    val weekWiDViewModel: WeekWiDViewModel = viewModel()
    val monthWiDViewModel: MonthWiDViewModel = viewModel()
    val titleWiDViewModel: TitleWiDViewModel = viewModel()

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }

    // 기간
    var periodMenuExpanded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        Log.d("WiDDisplayFragment", "WiDDisplayFragment is being composed")

        onDispose {
            Log.d("WiDDisplayFragment", "WiDDisplayFragment is being disposed")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WiD",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            if (pagerState.currentPage == 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Icon(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    titleMenuExpanded = true
                                }
                                .size(24.dp),
                            painter = painterResource(
                                titleIconMap[titleWiDViewModel.selectedTitle.value]
                                    ?: R.drawable.baseline_title_24
                            ),
                            contentDescription = "제목 선택",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        DropdownMenu(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiary),
                            expanded = titleMenuExpanded,
                            onDismissRequest = { titleMenuExpanded = false }
                        ) {
                            titles.forEach { itemTitle ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = titleMap[itemTitle] ?: "",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        titleWiDViewModel.setTitle(itemTitle)
                                        titleMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box {
                        Icon(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    periodMenuExpanded = true
                                }
                                .size(24.dp),
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = "기간 선택",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        DropdownMenu(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiary),
                            expanded = periodMenuExpanded,
                            onDismissRequest = { periodMenuExpanded = false }
                        ) {
                            periods.forEach { itemPeriod ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = periodMap[itemPeriod] ?: "",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        titleWiDViewModel.setPeriod(itemPeriod)
                                        periodMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        /**
         * 상단 탭
         */
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            ScrollableTabRow(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)),
                containerColor = MaterialTheme.colorScheme.secondary,
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
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else DarkGray
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
        }

        /**
         * 컨텐츠
         */
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> DayWiDFragment(dayWiDViewModel = dayWiDViewModel)
                1 -> WeekWiDFragment(weekWiDViewModel = weekWiDViewModel)
                2 -> MonthWiDFragment(monthWiDViewModel = monthWiDViewModel)
                3 -> TitleWiDFragment(titleWiDViewModel = titleWiDViewModel)
            }
        }
    }
}