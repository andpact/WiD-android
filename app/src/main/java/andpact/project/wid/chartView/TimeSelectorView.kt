package andpact.project.wid.chartView

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeSelectorView(
    modifier: Modifier = Modifier,
    hourPagerState: PagerState,
    minutePagerState: PagerState,
    secondPagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(hourPagerState, minutePagerState, secondPagerState).forEachIndexed { index, pagerState ->
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    enabled = pagerState.currentPage > 0,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_drop_up_24),
                        contentDescription = "증가"
                    )
                }

                VerticalPager(
                    modifier = Modifier
                        .weight(1f),
                    state = pagerState,
                    pageSpacing = (-80).dp
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = page.toString(),
                            style = if (pagerState.currentPage == page && !pagerState.isScrollInProgress) {
                                Typography.titleLarge
                            } else {
                                Typography.bodyMedium
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                FilledTonalIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    enabled = pagerState.currentPage < pagerState.pageCount - 1,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "감소"
                    )
                }
            }

            if (index < 2) {
                Text(
                    text = ":",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}