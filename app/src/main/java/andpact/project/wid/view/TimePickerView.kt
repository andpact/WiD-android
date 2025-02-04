package andpact.project.wid.view

import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.LimeGreen
import andpact.project.wid.ui.theme.White
import andpact.project.wid.viewModel.TimePickerViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimePickerView(
    previousView: PreviousView,
    onBackButtonPressed: () -> Unit,
    timePickerViewModel: TimePickerViewModel = hiltViewModel()
) {
    val TAG = "TimePickerView"

//    val START = timePickerViewModel.START
//    val FINISH = timePickerViewModel.FINISH

    val isLastNewWiD = timePickerViewModel.isLastNewWiD.value

    val updateClickedWiDCopyToNow = timePickerViewModel.updateClickedWiDCopyToNow.value

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = if (previousView == PreviousView.CLICKED_WID_START) 0 else 1, pageCount = { 2 })

    val clickedWiD = timePickerViewModel.clickedWiD.value
    val clickedWiDCopy = timePickerViewModel.clickedWiDCopy.value

    val startHourListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiD.start.hour)
    val startMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiD.start.minute)
    val startSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiD.start.second)
    val isStartOutOfRange = timePickerViewModel.isStartOutOfRange.value
    val minStart = timePickerViewModel.minStart.value
    val maxStart = timePickerViewModel.maxStart.value

    val finishHourListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiD.finish.hour)
    val finishMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiD.finish.minute)
    val finishSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiD.finish.second)
    val isFinishOutOfRange = timePickerViewModel.isFinishOutOfRange.value
    val minFinish = timePickerViewModel.minFinish.value
    val maxFinish = timePickerViewModel.maxFinish.value

    LaunchedEffect(clickedWiDCopy) {
        finishHourListState.scrollToItem(index = clickedWiDCopy.finish.hour)
        finishMinuteListState.scrollToItem(index = clickedWiDCopy.finish.minute)
        finishSecondListState.scrollToItem(index = clickedWiDCopy.finish.second)
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

//    BackHandler {
//        coroutineScope.launch { // clickedWiDCopy가 직전 수정 버전
//            startHourListState.scrollToItem(index = clickedWiDCopy.start.hour)
//            startMinuteListState.scrollToItem(index = clickedWiDCopy.start.minute)
//            startSecondListState.scrollToItem(index = clickedWiDCopy.start.second)
//
//            finishHourListState.scrollToItem(index = clickedWiDCopy.finish.hour)
//            finishMinuteListState.scrollToItem(index = clickedWiDCopy.finish.minute)
//            finishSecondListState.scrollToItem(index = clickedWiDCopy.finish.second)
//        }
//
//        onBackButtonPressed()
//    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // Scaffold 중첩 시 배경색을 자식 뷰도 지정해야함.
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {
//                            coroutineScope.launch { // clickedWiDCopy가 직전 수정 버전
//                                startHourListState.scrollToItem(index = clickedWiDCopy.start.hour)
//                                startMinuteListState.scrollToItem(index = clickedWiDCopy.start.minute)
//                                startSecondListState.scrollToItem(index = clickedWiDCopy.start.second)
//
//                                finishHourListState.scrollToItem(index = clickedWiDCopy.finish.hour)
//                                finishMinuteListState.scrollToItem(index = clickedWiDCopy.finish.minute)
//                                finishSecondListState.scrollToItem(index = clickedWiDCopy.finish.second)
//                            }

                            onBackButtonPressed()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기",
                        )
                    }
                },
                title = {
                    Text(text = "${previousView.kr} 시간 선택")
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RectangleShape,
                    onClick = { // 뒤로가기 + 초기화
//                        coroutineScope.launch { // clickedWiDCopy가 직전 수정 버전
//                            startHourListState.scrollToItem(index = clickedWiDCopy.start.hour)
//                            startMinuteListState.scrollToItem(index = clickedWiDCopy.start.minute)
//                            startSecondListState.scrollToItem(index = clickedWiDCopy.start.second)
//
//                            finishHourListState.scrollToItem(index = clickedWiDCopy.finish.hour)
//                            finishMinuteListState.scrollToItem(index = clickedWiDCopy.finish.minute)
//                            finishSecondListState.scrollToItem(index = clickedWiDCopy.finish.second)
//                        }

                        // 초기화할 필요는 사실 없음
                        onBackButtonPressed()
                    }
                ) {
                    Text(text = "취소")
                }

                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RectangleShape,
                    onClick = {
                        val newStart = LocalTime.of(startHourListState.firstVisibleItemIndex, startMinuteListState.firstVisibleItemIndex, startSecondListState.firstVisibleItemIndex)
                        val newFinish = LocalTime.of(finishHourListState.firstVisibleItemIndex, finishMinuteListState.firstVisibleItemIndex, finishSecondListState.firstVisibleItemIndex)
                        if (clickedWiDCopy.start != newStart) { // 직전 시작 시간 수정됨
                            timePickerViewModel.setClickedWiDCopyStart(newStart = newStart)
                        } else if (clickedWiDCopy.finish != newFinish) { // 직전 종료 시간 수정됨
                            timePickerViewModel.setClickedWiDCopyFinish(newFinish = newFinish)
                            timePickerViewModel.setUpdateClickedWiDCopyToNow(update = false)
                        }
                    },
                    enabled = !isStartOutOfRange && !isFinishOutOfRange,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = LimeGreen,
                        contentColor = White
                    )
                ) {
                    Text(text = "수정 완료")
                }
            }
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
            ) {
                SegmentedButton(
                    modifier = Modifier
                        .height(40.dp),
                    selected = pagerState.currentPage == 0,
                    shape = MaterialTheme.shapes.extraLarge.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    },
                    icon = {}
                ) {
                    Text(text = "시작 시간")
                }

                SegmentedButton(
                    modifier = Modifier
                        .height(40.dp),
                    selected = pagerState.currentPage == 1,
                    shape = MaterialTheme.shapes.extraLarge.copy(topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)),
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                    icon = {}
                ) {
                    Text(text = "종료 시간")
                }
            }

            HorizontalPager(
                state = pagerState
            ) { page: Int ->
                when(page) {
                    0 -> { // 시작 시간 수정
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            item {
                                TimeSelectorView(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    coroutineScope = coroutineScope,
                                    hourListState = startHourListState,
                                    minuteListState = startMinuteListState,
                                    secondListState = startSecondListState,
                                )
                            }

                            val isMinStartEnabled = !(startHourListState.firstVisibleItemIndex == minStart.hour &&
                                    startMinuteListState.firstVisibleItemIndex == minStart.minute &&
                                    startSecondListState.firstVisibleItemIndex == minStart.second)

                            item {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min)
                                        .padding(horizontal = 16.dp), // 바깥 패딩
                                    enabled = isMinStartEnabled,
                                    onClick = {
                                        coroutineScope.launch {
                                            launch { startHourListState.animateScrollToItem(index = minStart.hour) }
                                            launch { startMinuteListState.animateScrollToItem(index = minStart.minute) }
                                            launch { startSecondListState.animateScrollToItem(index = minStart.second) }
                                        }
                                    }
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
                                                text = "선택 가능한 최소 시간",
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Text(
                                                text = timePickerViewModel.getTimeString(time = minStart),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "최소 시간 사용",
                                        )
                                    }
                                }
                            }

                            val isMaxStartEnabled = !(startHourListState.firstVisibleItemIndex == maxStart.hour &&
                                    startMinuteListState.firstVisibleItemIndex == maxStart.minute &&
                                    startSecondListState.firstVisibleItemIndex == maxStart.second)

                            item {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min)
                                        .padding(horizontal = 16.dp), // 바깥 패딩
                                    enabled = isMaxStartEnabled,
                                    onClick = {
                                        coroutineScope.launch {
                                            launch { startHourListState.animateScrollToItem(index = maxStart.hour) }
                                            launch { startMinuteListState.animateScrollToItem(index = maxStart.minute) }
                                            launch { startSecondListState.animateScrollToItem(index = maxStart.second) }
                                        }
                                    }
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
                                                text = "선택 가능한 최대 시간",
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Text(
                                                text = timePickerViewModel.getTimeString(time = maxStart),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "최대 시간 사용",
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // 종료 시간 수정
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            item{
                                TimeSelectorView(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    coroutineScope = coroutineScope,
                                    hourListState = finishHourListState,
                                    minuteListState = finishMinuteListState,
                                    secondListState = finishSecondListState,
                                )
                            }

                            val isMinFinishEnabled = !(finishHourListState.firstVisibleItemIndex == minFinish.hour &&
                                    finishMinuteListState.firstVisibleItemIndex == minFinish.minute &&
                                    finishSecondListState.firstVisibleItemIndex == minFinish.second)

                            item{
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min)
                                        .padding(horizontal = 16.dp), // 바깥 패딩
                                    enabled = isMinFinishEnabled,
                                    onClick = {
                                        coroutineScope.launch {
                                            launch { finishHourListState.animateScrollToItem(index = minFinish.hour) }
                                            launch { finishMinuteListState.animateScrollToItem(index = minFinish.minute) }
                                            launch { finishSecondListState.animateScrollToItem(index = minFinish.second) }
                                        }
                                    }
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
                                                text = "선택 가능한 최소 시간",
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Text(
                                                text = timePickerViewModel.getTimeString(time = minFinish),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "최소 시간 사용",
                                        )
                                    }
                                }
                            }

                            val isMaxFinishEnabled = if (isLastNewWiD) {
                                !updateClickedWiDCopyToNow
                            } else {
                                !(finishHourListState.firstVisibleItemIndex == maxFinish.hour &&
                                        finishMinuteListState.firstVisibleItemIndex == maxFinish.minute &&
                                        finishSecondListState.firstVisibleItemIndex == maxFinish.second)
                            }

                            item{
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min)
                                        .padding(horizontal = 16.dp), // 바깥 패딩
                                    enabled = isMaxFinishEnabled,
                                    onClick = {
                                        coroutineScope.launch {
                                            launch { finishHourListState.animateScrollToItem(index = maxFinish.hour) }
                                            launch { finishMinuteListState.animateScrollToItem(index = maxFinish.minute) }
                                            launch { finishSecondListState.animateScrollToItem(index = maxFinish.second) }
                                        }
                                    }
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
                                                text = "선택 가능한 최대 시간",
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Text(
                                                text = timePickerViewModel.getTimeString(time = maxFinish),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "최대 시간 사용",
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}