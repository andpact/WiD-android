package andpact.project.wid.view

import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.viewModel.MyWiDViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyWiDView(myWiDViewModel: MyWiDViewModel = hiltViewModel()) {
    val TAG = "MyWiDView"

    val coroutineScope = rememberCoroutineScope()

    val wiDMinLimit = myWiDViewModel.user.value?.wiDMinLimit ?: Duration.ZERO
    val wiDMinLimitHourPagerState = rememberPagerState(initialPage = wiDMinLimit.toHours().toInt(), pageCount = { 12 })
    val wiDMinLimitMinutePagerState = rememberPagerState(initialPage = (wiDMinLimit.toMinutes() % 60).toInt(), pageCount = { 60 })
    val wiDMinLimitSecondPagerState = rememberPagerState(initialPage = (wiDMinLimit.seconds % 60).toInt(), pageCount = { 60 })

    val wiDMaxLimit = myWiDViewModel.user.value?.wiDMaxLimit ?: Duration.ZERO
    val wiDMaxLimitHourPagerState = rememberPagerState(initialPage = wiDMaxLimit.toHours().toInt(), pageCount = { 12 })
    val wiDMaxLimitMinutePagerState = rememberPagerState(initialPage = (wiDMaxLimit.toMinutes() % 60).toInt(), pageCount = { 60 })
    val wiDMaxLimitSecondPagerState = rememberPagerState(initialPage = (wiDMaxLimit.seconds % 60).toInt(), pageCount = { 60 })

    val showUpdateWiDMinLimitDialog = myWiDViewModel.showUpdateWiDMinLimitDialog.value
    val showUpdateWiDMaxLimitDialog = myWiDViewModel.showUpdateWiDMaxLimitDialog.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            ListItem(
                modifier = Modifier
                    .clickable {
                        myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = true)
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "최소 시간 수정"
                    )
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

            ListItem(
                modifier = Modifier
                    .clickable {
                        myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = true)
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "최대 시간 수정"
                    )
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
                    wiDMinLimitHourPagerState.scrollToPage(wiDMinLimit.toHours().toInt())
                    wiDMinLimitMinutePagerState.scrollToPage((wiDMinLimit.toMinutes() % 60).toInt())
                    wiDMinLimitSecondPagerState.scrollToPage((wiDMinLimit.seconds % 60).toInt())
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
                        .height(250.dp)
                        .padding(horizontal = 16.dp),
                    hourPagerState = wiDMinLimitHourPagerState,
                    minutePagerState = wiDMinLimitMinutePagerState,
                    secondPagerState = wiDMinLimitSecondPagerState,
                    coroutineScope = coroutineScope
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
                                wiDMinLimitHourPagerState.scrollToPage(wiDMinLimit.toHours().toInt())
                                wiDMinLimitMinutePagerState.scrollToPage((wiDMinLimit.toMinutes() % 60).toInt())
                                wiDMinLimitSecondPagerState.scrollToPage((wiDMinLimit.seconds % 60).toInt())
                            }

                            myWiDViewModel.setShowUpdateWiDMinLimitDialog(show = false)
                        }
                    ) {
                        Text(text = "취소")
                    }

                    FilledTonalButton(
                        onClick = {
                            val selectedHours = wiDMinLimitHourPagerState.currentPage
                            val selectedMinutes = wiDMinLimitMinutePagerState.currentPage
                            val selectedSeconds = wiDMinLimitSecondPagerState.currentPage

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
                    wiDMaxLimitHourPagerState.scrollToPage(wiDMaxLimit.toHours().toInt())
                    wiDMaxLimitMinutePagerState.scrollToPage((wiDMaxLimit.toMinutes() % 60).toInt())
                    wiDMaxLimitSecondPagerState.scrollToPage((wiDMaxLimit.seconds % 60).toInt())
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
                        .height(250.dp)
                        .padding(horizontal = 16.dp),
                    hourPagerState = wiDMaxLimitHourPagerState,
                    minutePagerState = wiDMaxLimitMinutePagerState,
                    secondPagerState = wiDMaxLimitSecondPagerState,
                    coroutineScope = coroutineScope
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
                                wiDMaxLimitHourPagerState.scrollToPage(wiDMaxLimit.toHours().toInt())
                                wiDMaxLimitMinutePagerState.scrollToPage((wiDMaxLimit.toMinutes() % 60).toInt())
                                wiDMaxLimitSecondPagerState.scrollToPage((wiDMaxLimit.seconds % 60).toInt())
                            }

                            myWiDViewModel.setShowUpdateWiDMaxLimitDialog(show = false)
                        }
                    ) {
                        Text(text = "취소")
                    }

                    FilledTonalButton(
                        onClick = {
                            val selectedHours = wiDMaxLimitHourPagerState.currentPage
                            val selectedMinutes = wiDMaxLimitMinutePagerState.currentPage
                            val selectedSeconds = wiDMaxLimitSecondPagerState.currentPage

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