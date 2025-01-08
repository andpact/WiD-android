package andpact.project.wid.chartView

import andpact.project.wid.R
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
    coroutineScope: CoroutineScope,
    onTimeChanged: () -> Unit = {} // TODO: 드래그로 시간 변경시에도 동작하도록
// FIXME: 초기상태 왜 캡쳐하록 헀음?, 시, 분, 초 때문에 ":" 높이 안맞음.
) {
    var isInitialPage by remember { mutableStateOf(true) }

    LaunchedEffect(
        key1 = hourPagerState.currentPage,
        key2 = minutePagerState.currentPage,
        key3 = secondPagerState.currentPage
    ) {
        if (!isInitialPage) { onTimeChanged() }
    }

    LaunchedEffect(Unit) {
        isInitialPage = false
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(hourPagerState, minutePagerState, secondPagerState).forEachIndexed { index: Int, pagerState: PagerState ->
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (index) {
                        0 -> "시"
                        1 -> "분"
                        2 -> "초"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FilledTonalIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        isInitialPage = false // 사용자 동작 시 초기 상태 플래그 해제
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
                            style = if (pagerState.currentPage == page && !pagerState.isScrollInProgress) { MaterialTheme.typography.bodyLarge }
                            else { MaterialTheme.typography.bodySmall },
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                FilledTonalIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        isInitialPage = false // 사용자 동작 시 초기 상태 플래그 해제
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
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}