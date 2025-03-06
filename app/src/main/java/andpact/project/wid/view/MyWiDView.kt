package andpact.project.wid.view

import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.MyWiDViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyWiDView(myWiDViewModel: MyWiDViewModel = hiltViewModel()) {
    val TAG = "MyWiDView"

    val coroutineScope = rememberCoroutineScope()

    val minDurationLimit = Duration.ofMinutes(5)
    val maxDurationLimit = Duration.ofHours(12)

    val wiDMinLimit = myWiDViewModel.user.value?.wiDMinLimit ?: Duration.ZERO
    val wiDMinLimitHourListState = rememberLazyListState(initialFirstVisibleItemIndex = wiDMinLimit.toHours().toInt())
    val wiDMinLimitMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMinLimit.toMinutes() % 60).toInt())
    val wiDMinLimitSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMinLimit.seconds % 60).toInt())
    val selectedMinLimit = Duration.ofHours(wiDMinLimitHourListState.firstVisibleItemIndex.toLong())
        .plusMinutes(wiDMinLimitMinuteListState.firstVisibleItemIndex.toLong())
        .plusSeconds(wiDMinLimitSecondListState.firstVisibleItemIndex.toLong())

    val wiDMaxLimit = myWiDViewModel.user.value?.wiDMaxLimit ?: Duration.ZERO
    val wiDMaxLimitHourListState = rememberLazyListState(initialFirstVisibleItemIndex = wiDMaxLimit.toHours().toInt())
    val wiDMaxLimitMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMaxLimit.toMinutes() % 60).toInt())
    val wiDMaxLimitSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMaxLimit.seconds % 60).toInt())
    val selectedMaxLimit = Duration.ofHours(wiDMaxLimitHourListState.firstVisibleItemIndex.toLong())
        .plusMinutes(wiDMaxLimitMinuteListState.firstVisibleItemIndex.toLong())
        .plusSeconds(wiDMaxLimitSecondListState.firstVisibleItemIndex.toLong())

    val isMinLimitValid = selectedMinLimit in minDurationLimit..wiDMaxLimit
    val isMaxLimitValid = selectedMaxLimit in wiDMinLimit..maxDurationLimit

    val showUpdateWiDMinLimitDialog = myWiDViewModel.showUpdateWiDMinLimitDialog.value
    val showUpdateWiDMaxLimitDialog = myWiDViewModel.showUpdateWiDMaxLimitDialog.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item(
            key = "wid-min-limit",
            contentType = "list-item"
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = true)
                    },
                headlineContent = {
                    Text(text = "생성 가능한 기록의 최소 시간")
                },
                supportingContent = {
                    Text(text = myWiDViewModel.getDurationString(duration = wiDMinLimit))
                },
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = "최소 시간 수정"
                        )
                    }
                }
            )
        }

        item(
            contentType = "divider"
        ){
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }

        item(
            key = "wid-max-limit",
            contentType = "list-item"
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = true)
                    },
                headlineContent = {
                    Text(text = "생성 가능한 기록의 최대 시간")
                },
                supportingContent = {
                    Text(text = myWiDViewModel.getDurationString(duration = wiDMaxLimit))
                },
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = "최대 시간 수정"
                        )
                    }
                }
            )
        }
    }

    if (showUpdateWiDMinLimitDialog) {
        DatePickerDialog(
            shape = MaterialTheme.shapes.medium,
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            onDismissRequest = {
                coroutineScope.launch {
                    wiDMinLimitHourListState.scrollToItem(wiDMinLimit.toHours().toInt())
                    wiDMinLimitMinuteListState.scrollToItem((wiDMinLimit.toMinutes() % 60).toInt())
                    wiDMinLimitSecondListState.scrollToItem((wiDMinLimit.seconds % 60).toInt())
                }

                myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = false)
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            wiDMinLimitHourListState.scrollToItem(wiDMinLimit.toHours().toInt())
                            wiDMinLimitMinuteListState.scrollToItem((wiDMinLimit.toMinutes() % 60).toInt())
                            wiDMinLimitSecondListState.scrollToItem((wiDMinLimit.seconds % 60).toInt())
                        }

                        myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = false)
                    }
                ) {
                    Text(text = "취소")
                }
            },
            confirmButton = {
                OutlinedButton(
                    enabled = isMinLimitValid,
                    onClick = {
                        myWiDViewModel.updateWiDMinLimit(minLimit = selectedMinLimit)
                        myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = false)
                    }
                ) {
                    Text(text = "확인")
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "최소 시간 수정",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                VerticalDivider()

                // 시
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .height((48 * 3).dp)
                        .heightIn(max = 700.dp),
                    state = wiDMinLimitHourListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wiDMinLimitHourListState)
                ) {
                    item(
                        key = "wid-min-hour-spacer-top",
                        contentType = "spacer"
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
                                    wiDMinLimitHourListState.animateScrollToItem(index = itemIndex)
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "$itemIndex",
                                style = if (wiDMinLimitHourListState.firstVisibleItemIndex == itemIndex && !wiDMinLimitHourListState.isScrollInProgress) {
                                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                } else {
                                    MaterialTheme.typography.labelSmall
                                }
                            )
                        }
                    }

                    item(
                        key = "wid-min-hour-spacer-bottom",
                        contentType = "spacer"
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                VerticalDivider()

                // 분
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .height((48 * 3).dp),
                    state = wiDMinLimitMinuteListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wiDMinLimitMinuteListState)
                ) {
                    item(
                        key = "wid-min-minute-spacer-top",
                        contentType = "spacer"
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
                                    wiDMinLimitMinuteListState.animateScrollToItem(index = itemIndex)
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "$itemIndex",
                                style = if (wiDMinLimitMinuteListState.firstVisibleItemIndex == itemIndex && !wiDMinLimitMinuteListState.isScrollInProgress) {
                                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                } else {
                                    MaterialTheme.typography.labelSmall
                                }
                            )
                        }
                    }

                    item(
                        key = "wid-min-minute-spacer-bottom",
                        contentType = "spacer"
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                VerticalDivider()

                // 초
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .height((48 * 3).dp),
                    state = wiDMinLimitSecondListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wiDMinLimitSecondListState)
                ) {
                    item(
                        key = "wid-min-second-spacer-top",
                        contentType = "spacer"
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
                                    wiDMinLimitSecondListState.animateScrollToItem(index = itemIndex)
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "$itemIndex",
                                style = if (wiDMinLimitSecondListState.firstVisibleItemIndex == itemIndex && !wiDMinLimitSecondListState.isScrollInProgress) {
                                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                } else {
                                    MaterialTheme.typography.labelSmall
                                }
                            )
                        }
                    }

                    item(
                        key = "wid-min-second-spacer-bottom",
                        contentType = "spacer"
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                VerticalDivider()
            }

            val isMinDurationLimitEnabled = !(wiDMinLimitHourListState.firstVisibleItemIndex == minDurationLimit.toHours().toInt()
                    && wiDMinLimitMinuteListState.firstVisibleItemIndex == (minDurationLimit.toMinutes() % 60).toInt()
                    && wiDMinLimitSecondListState.firstVisibleItemIndex == (minDurationLimit.seconds % 60).toInt())

//            ListItem(
//                modifier = Modifier
//                    .clickable {
//                        if (isMinDurationLimitEnabled) {
//                            coroutineScope.launch {
//                                launch { wiDMinLimitHourListState.animateScrollToItem(minDurationLimit.toHours().toInt()) }
//                                launch { wiDMinLimitMinuteListState.animateScrollToItem((minDurationLimit.toMinutes() % 60).toInt()) }
//                                launch { wiDMinLimitSecondListState.animateScrollToItem((minDurationLimit.seconds % 60).toInt()) }
//                            }
//                        }
//                    },
//                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
//                headlineContent = {
//                    Text(text = "사용 가능한 최소 시간")
//                },
//                supportingContent = {
//                    Text(text = myWiDViewModel.getDurationString(duration = minDurationLimit))
//
//                },
//                trailingContent = {
//                    RadioButton(
//                        selected = !isMinDurationLimitEnabled,
//                        onClick = null
//                    )
//                }
//            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .height(intrinsicSize = IntrinsicSize.Min),
                shape = RectangleShape,
                enabled = isMinDurationLimitEnabled,
                onClick = {
                    if (isMinDurationLimitEnabled) {
                        coroutineScope.launch {
                            launch { wiDMinLimitHourListState.scrollToItem(minDurationLimit.toHours().toInt()) }
                            launch { wiDMinLimitMinuteListState.scrollToItem((minDurationLimit.toMinutes() % 60).toInt()) }
                            launch { wiDMinLimitSecondListState.scrollToItem((minDurationLimit.seconds % 60).toInt()) }
                        }
                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = Transparent,
                    disabledContainerColor = Transparent
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "사용 가능한 최소 시간",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = myWiDViewModel.getDurationString(duration = minDurationLimit),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    RadioButton(
                        selected = !isMinDurationLimitEnabled,
                        onClick = null
                    )
                }
            }
        }
    }

    if (showUpdateWiDMaxLimitDialog) {
        DatePickerDialog(
            shape = MaterialTheme.shapes.medium,
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            onDismissRequest = {
                coroutineScope.launch {
                    wiDMaxLimitHourListState.scrollToItem(wiDMaxLimit.toHours().toInt())
                    wiDMaxLimitMinuteListState.scrollToItem((wiDMaxLimit.toMinutes() % 60).toInt())
                    wiDMaxLimitSecondListState.scrollToItem((wiDMaxLimit.seconds % 60).toInt())
                }

                myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = false)
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            wiDMaxLimitHourListState.scrollToItem(wiDMaxLimit.toHours().toInt())
                            wiDMaxLimitMinuteListState.scrollToItem((wiDMaxLimit.toMinutes() % 60).toInt())
                            wiDMaxLimitSecondListState.scrollToItem((wiDMaxLimit.seconds % 60).toInt())
                        }

                        myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = false)
                    }
                ) {
                    Text(text = "취소")
                }
            },
            confirmButton = {
                OutlinedButton(
                    enabled = isMaxLimitValid,
                    onClick = {
                        myWiDViewModel.updateWiDMaxLimit(maxLimit = selectedMaxLimit)
                        myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = false)
                    }
                ) {
                    Text(text = "확인")
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "최대 시간 수정",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                VerticalDivider()

                // 시
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .height((48 * 3).dp),
                    state = wiDMaxLimitHourListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wiDMaxLimitHourListState)
                ) {
                    item(
                        key = "wid-max-hour-spacer-top",
                        contentType = "spacer"
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
                                    wiDMaxLimitHourListState.animateScrollToItem(index = itemIndex)
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "$itemIndex",
                                style = if (wiDMaxLimitHourListState.firstVisibleItemIndex == itemIndex && !wiDMaxLimitHourListState.isScrollInProgress) {
                                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                } else {
                                    MaterialTheme.typography.labelSmall
                                }
                            )
                        }
                    }

                    item(
                        key = "wid-max-hour-spacer-bottom",
                        contentType = "spacer"
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                VerticalDivider()

                // 분
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .height((48 * 3).dp),
                    state = wiDMaxLimitMinuteListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wiDMaxLimitMinuteListState)
                ) {
                    item(
                        key = "wid-max-minute-spacer-top",
                        contentType = "spacer"
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
                                    wiDMaxLimitMinuteListState.animateScrollToItem(index = itemIndex)
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "$itemIndex",
                                style = if (wiDMaxLimitMinuteListState.firstVisibleItemIndex == itemIndex && !wiDMaxLimitMinuteListState.isScrollInProgress) {
                                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                } else {
                                    MaterialTheme.typography.labelSmall
                                }
                            )
                        }
                    }

                    item(
                        key = "wid-max-minute-spacer-bottom",
                        contentType = "spacer"
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                VerticalDivider()

                // 초
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .height((48 * 3).dp),
                    state = wiDMaxLimitSecondListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = wiDMaxLimitSecondListState)
                ) {
                    item(
                        key = "wid-max-second-spacer-top",
                        contentType = "spacer"
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
                                    wiDMaxLimitSecondListState.animateScrollToItem(index = itemIndex)
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text(
                                text = "$itemIndex",
                                style = if (wiDMaxLimitSecondListState.firstVisibleItemIndex == itemIndex && !wiDMaxLimitSecondListState.isScrollInProgress) {
                                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                } else {
                                    MaterialTheme.typography.labelSmall
                                }
                            )
                        }
                    }

                    item(
                        key = "wid-max-second-spacer-bottom",
                        contentType = "spacer"
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                VerticalDivider()
            }

            val isMaxDurationLimitEnabled = !(wiDMaxLimitHourListState.firstVisibleItemIndex == maxDurationLimit.toHours().toInt()
                    && wiDMaxLimitMinuteListState.firstVisibleItemIndex == (maxDurationLimit.toMinutes() % 60).toInt()
                    && wiDMaxLimitSecondListState.firstVisibleItemIndex == (maxDurationLimit.seconds % 60).toInt())

//            ListItem(
//                modifier = Modifier
//                    .clickable {
//                        if (isMaxDurationLimitEnabled) {
//                            coroutineScope.launch {
//                                launch { wiDMaxLimitHourListState.animateScrollToItem(maxDurationLimit.toHours().toInt()) }
//                                launch { wiDMaxLimitMinuteListState.animateScrollToItem((maxDurationLimit.toMinutes() % 60).toInt()) }
//                                launch { wiDMaxLimitSecondListState.animateScrollToItem((maxDurationLimit.seconds % 60).toInt()) }
//                            }
//                        }
//                    },
//                headlineContent = {
//                    Text(text = "사용 가능한 최대 시간")
//                },
//                supportingContent = {
//                    Text(text = myWiDViewModel.getDurationString(duration = maxDurationLimit))
//                },
//                trailingContent = {
//                    RadioButton(
//                        selected = !isMaxDurationLimitEnabled,
//                        onClick = null
//                    )
//                }
//            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .height(intrinsicSize = IntrinsicSize.Min),
                shape = RectangleShape,
                enabled = isMaxDurationLimitEnabled,
                onClick = {
                    if (isMaxDurationLimitEnabled) {
                        coroutineScope.launch {
                            launch { wiDMaxLimitHourListState.scrollToItem(maxDurationLimit.toHours().toInt()) }
                            launch { wiDMaxLimitMinuteListState.scrollToItem((maxDurationLimit.toMinutes() % 60).toInt()) }
                            launch { wiDMaxLimitSecondListState.scrollToItem((maxDurationLimit.seconds % 60).toInt()) }
                        }
                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = Transparent,
                    disabledContainerColor = Transparent
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "사용 가능한 최대 시간",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = myWiDViewModel.getDurationString(duration = maxDurationLimit),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    RadioButton(
                        selected = !isMaxDurationLimitEnabled,
                        onClick = null
                    )
                }
            }
        }
    }
}