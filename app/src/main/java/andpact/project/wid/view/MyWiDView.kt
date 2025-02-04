package andpact.project.wid.view

import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.viewModel.MyWiDViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWiDView(myWiDViewModel: MyWiDViewModel = hiltViewModel()) {
    val TAG = "MyWiDView"

    val coroutineScope = rememberCoroutineScope()

    val wiDMinLimit = myWiDViewModel.user.value?.wiDMinLimit ?: Duration.ZERO
    val wiDMinLimitHourListState = rememberLazyListState(initialFirstVisibleItemIndex = wiDMinLimit.toHours().toInt())
    val wiDMinLimitMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMinLimit.toMinutes() % 60).toInt())
    val wiDMinLimitSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMinLimit.seconds % 60).toInt())

    val wiDMaxLimit = myWiDViewModel.user.value?.wiDMaxLimit ?: Duration.ZERO
    val wiDMaxLimitHourListState = rememberLazyListState(initialFirstVisibleItemIndex = wiDMaxLimit.toHours().toInt())
    val wiDMaxLimitMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMaxLimit.toMinutes() % 60).toInt())
    val wiDMaxLimitSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = (wiDMaxLimit.seconds % 60).toInt())

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
                    Text(text = "생성 가능한 기록 최소 시간")
                },
                supportingContent = {
                    Text(text = myWiDViewModel.getDurationString(duration = wiDMinLimit))
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "최소 시간 수정"
                    )
                }
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
                    Text(text = "생성 가능한 기록 최대 시간")
                },
                supportingContent = {
                    Text(text = myWiDViewModel.getDurationString(duration = wiDMaxLimit))
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "최대 시간 수정"
                    )
                }
            )
        }
    }

    if (showUpdateWiDMinLimitDialog) {
        AlertDialog(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            onDismissRequest = { // 취소와 동일
                coroutineScope.launch {
                    wiDMinLimitHourListState.scrollToItem(wiDMinLimit.toHours().toInt())
                    wiDMinLimitMinuteListState.scrollToItem((wiDMinLimit.toMinutes() % 60).toInt())
                    wiDMinLimitSecondListState.scrollToItem((wiDMinLimit.seconds % 60).toInt())
                }

                myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = false)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "최소 시간 수정",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                TimeSelectorView(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    coroutineScope = coroutineScope,
                    hourListState = wiDMinLimitHourListState,
                    minuteListState = wiDMinLimitMinuteListState,
                    secondListState = wiDMinLimitSecondListState
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

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

                    FilledTonalButton(
                        onClick = {
                            val selectedHours = wiDMinLimitHourListState.firstVisibleItemIndex
                            val selectedMinutes = wiDMinLimitMinuteListState.firstVisibleItemIndex
                            val selectedSeconds = wiDMinLimitSecondListState.firstVisibleItemIndex

                            val newMinLimit = Duration.ofHours(selectedHours.toLong())
                                .plusMinutes(selectedMinutes.toLong())
                                .plusSeconds(selectedSeconds.toLong())

                            myWiDViewModel.updateWiDMinLimit(minLimit = newMinLimit)

                            myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = false)
                        },
                    ) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }

    if (showUpdateWiDMaxLimitDialog) {
        AlertDialog(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            onDismissRequest = {
                coroutineScope.launch {
                    wiDMaxLimitHourListState.scrollToItem(wiDMaxLimit.toHours().toInt())
                    wiDMaxLimitMinuteListState.scrollToItem((wiDMaxLimit.toMinutes() % 60).toInt())
                    wiDMaxLimitSecondListState.scrollToItem((wiDMaxLimit.seconds % 60).toInt())
                }

                myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = false)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "최대 시간 수정",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                TimeSelectorView(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    coroutineScope = coroutineScope,
                    hourListState = wiDMaxLimitHourListState,
                    minuteListState = wiDMaxLimitMinuteListState,
                    secondListState = wiDMaxLimitSecondListState
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

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

                    FilledTonalButton(
                        onClick = {
                            val selectedHours = wiDMaxLimitHourListState.firstVisibleItemIndex
                            val selectedMinutes = wiDMaxLimitMinuteListState.firstVisibleItemIndex
                            val selectedSeconds = wiDMaxLimitSecondListState.firstVisibleItemIndex

                            val newMaxLimit = Duration.ofHours(selectedHours.toLong())
                                .plusMinutes(selectedMinutes.toLong())
                                .plusSeconds(selectedSeconds.toLong())

                            myWiDViewModel.updateWiDMaxLimit(maxLimit = newMaxLimit)

                            myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = false)
                        },
                    ) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}