package andpact.project.wid.chartView

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeSelectorView(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    hourListState: LazyListState,
    minuteListState: LazyListState,
    secondListState: LazyListState,
    onTimeChanged: (selectedTime: Duration) -> Unit = {}
) {
    val TAG = "TimeSelectorView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    LaunchedEffect(
        key1 = hourListState.firstVisibleItemIndex,
        key2 = minuteListState.firstVisibleItemIndex,
        key3 = secondListState.firstVisibleItemIndex,
        block = {
            val newSelectedTime = Duration.ofHours(hourListState.firstVisibleItemIndex.toLong())
                .plusMinutes(minuteListState.firstVisibleItemIndex.toLong())
                .plusSeconds(secondListState.firstVisibleItemIndex.toLong())
            onTimeChanged(newSelectedTime)
        }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("시", "분", "초").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            VerticalDivider()

            // 시
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .height((48 * 7).dp),
                state = hourListState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)
            ) {
                items(
                    count = 3,
                    key = { index -> "hour-spacer-top-$index" },
                    contentType = { "spacer" }
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                items(
                    count = 24,
                    key = { itemIndex -> "hour-item-$itemIndex" },
                    contentType = { "hour-item" }
                ) { itemIndex ->
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            coroutineScope.launch {
                                hourListState.animateScrollToItem(index = itemIndex)
                            }
                        },
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "$itemIndex",
                            style = if (hourListState.firstVisibleItemIndex == itemIndex && !hourListState.isScrollInProgress) {
                                MaterialTheme.typography.bodyLarge
                            } else {
                                MaterialTheme.typography.bodySmall
                            }
                        )
                    }
                }

                items(
                    count = 3,
                    key = { index -> "hour-spacer-bottom-$index" },
                    contentType = { "spacer" }
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            VerticalDivider()

            // 분
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .height((48 * 7).dp),
                state = minuteListState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)
            ) {
                items(
                    count = 3,
                    key = { index -> "minute-spacer-top-$index" },
                    contentType = { "spacer" }
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                items(
                    count = 60,
                    key = { itemIndex -> "minute-item-$itemIndex" },
                    contentType = { "minute-item" }
                ) { itemIndex ->
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            coroutineScope.launch {
                                minuteListState.animateScrollToItem(index = itemIndex)
                            }
                        },
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "$itemIndex",
                            style = if (minuteListState.firstVisibleItemIndex == itemIndex && !minuteListState.isScrollInProgress) {
                                MaterialTheme.typography.bodyLarge
                            } else {
                                MaterialTheme.typography.bodySmall
                            }
                        )
                    }
                }

                items(
                    count = 3,
                    key = { index -> "minute-spacer-bottom-$index" },
                    contentType = { "spacer" }
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            VerticalDivider()

            // 초
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .height((48 * 7).dp),
                state = secondListState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = secondListState)
            ) {
                items(
                    count = 3,
                    key = { index -> "second-spacer-top-$index" },
                    contentType = { "spacer" }
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                items(
                    count = 60,
                    key = { itemIndex -> "second-item-$itemIndex" },
                    contentType = { "second-item" }
                ) { itemIndex ->
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            coroutineScope.launch {
                                secondListState.animateScrollToItem(index = itemIndex)
                            }
                        },
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "$itemIndex",
                            style = if (secondListState.firstVisibleItemIndex == itemIndex && !secondListState.isScrollInProgress) {
                                MaterialTheme.typography.bodyLarge
                            } else {
                                MaterialTheme.typography.bodySmall
                            }
                        )
                    }
                }

                items(
                    count = 3,
                    key = { index -> "second-spacer-bottom-$index" },
                    contentType = { "spacer" }
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            VerticalDivider()
        }
    }
}