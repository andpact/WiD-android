package andpact.project.wid.view

import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.DateTimePickerViewModel
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DateTimePickerView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    currentDate: LocalDate,
    previousView: PreviousView,
    onBackButtonPressed: () -> Unit,
    dateTimePickerViewModel: DateTimePickerViewModel = hiltViewModel()
) {
    val TAG = "DateTimePickerView"

//    val START = dateTimePickerViewModel.START
//    val FINISH = dateTimePickerViewModel.FINISH

//    val now = dateTimePickerViewModel.now.value

    val isLastNewWiD = dateTimePickerViewModel.isLastNewWiD.value

    val updateClickedWiDCopyToNow = dateTimePickerViewModel.updateClickedWiDCopyToNow.value

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = if (previousView == PreviousView.CLICKED_WID_START) 0 else 1, pageCount = { 2 })

//    val clickedWiD = dateTimePickerViewModel.clickedWiD.value
    val clickedWiDCopy = dateTimePickerViewModel.clickedWiDCopy.value

    val startCurrentDate = dateTimePickerViewModel.startCurrentDate.value
//    val startDatePickerMidDate = dateTimePickerViewModel.startDatePickerMidDate.value
    val startHourListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.start.hour)
    val startMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.start.minute)
    val startSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.start.second)
    val startEnabled = dateTimePickerViewModel.startEnabled.value
    val minStart = dateTimePickerViewModel.minStart.value
    val maxStart = dateTimePickerViewModel.maxStart.value

    val finishCurrentDate = dateTimePickerViewModel.finishCurrentDate.value
//    val finishDatePickerMidDate = dateTimePickerViewModel.finishDatePickerMidDate.value
    val finishHourListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.finish.hour)
    val finishMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.finish.minute)
    val finishSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.finish.second)
    val finishEnabled = dateTimePickerViewModel.finishEnabled.value
    val minFinish = dateTimePickerViewModel.minFinish.value
    val maxFinish = dateTimePickerViewModel.maxFinish.value

    // 마지막 새 기록 시작 시간을 실시간으로 갱신
//    LaunchedEffect(clickedWiDCopy.start) {
//        startHourListState.scrollToItem(index = clickedWiDCopy.start.hour)
//        startMinuteListState.scrollToItem(index = clickedWiDCopy.start.minute)
//        startSecondListState.scrollToItem(index = clickedWiDCopy.start.second)
//    }

    // 마지막 새 기록 종료 시간을 실시간으로 갱신
//    LaunchedEffect(clickedWiDCopy.finish) {
//        finishHourListState.scrollToItem(index = clickedWiDCopy.finish.hour)
//        finishMinuteListState.scrollToItem(index = clickedWiDCopy.finish.minute)
//        finishSecondListState.scrollToItem(index = clickedWiDCopy.finish.second)
//    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = clickedWiDCopy.start.toLocalDate())
        dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = clickedWiDCopy.finish.toLocalDate())

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
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(statusBarHeight)
                        .background(MaterialTheme.colorScheme.surface)
                )

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
                        Text(
                            text = "${previousView.kr} 시간 선택",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                )
            }
        },
        bottomBar = {
            Column {
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
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        onClick = {
                            val newStartTime = LocalTime.of(startHourListState.firstVisibleItemIndex, startMinuteListState.firstVisibleItemIndex, startSecondListState.firstVisibleItemIndex)
                            val newFinishTime = LocalTime.of(finishHourListState.firstVisibleItemIndex, finishMinuteListState.firstVisibleItemIndex, finishSecondListState.firstVisibleItemIndex)
                            val newStartDateTime = LocalDateTime.of(startCurrentDate, newStartTime)
                            val newFinishDateTime = LocalDateTime.of(finishCurrentDate, newFinishTime)

                            if (clickedWiDCopy.start != newStartDateTime) { // 직전 시작 시간 수정됨
                                dateTimePickerViewModel.setClickedWiDCopyStart(newStart = newStartDateTime)
                            } else if (clickedWiDCopy.finish != newFinishDateTime) { // 직전 종료 시간 수정됨
                                dateTimePickerViewModel.setClickedWiDCopyFinish(newFinish = newFinishDateTime)
                                dateTimePickerViewModel.setUpdateClickedWiDCopyFinishToNow(update = false)
                            }
                        },
                        enabled = !startEnabled && !finishEnabled
                    ) {
                        Text(text = "수정 완료")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navigationBarHeight)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            val currentPage = pagerState.currentPage

            TabRow(
                selectedTabIndex = currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[currentPage]),
                        width = tabPositions[currentPage].contentWidth
                    )
                }
            ) {
                Tab(
                    selected = currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "") },
                    text = { Text(text = "시작 시점") }
                )

                Tab(
                    selected = currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "") },
                    text = { Text(text = "종료 시점") }
                )
            }

            HorizontalPager(
                state = pagerState
            ) { page: Int ->
                when(page) {
                    0 -> { // 시작 시간 수정
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(
                                key = "start-current-date-picker",
                                contentType = "start-current-date-picker-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledIconButton(
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        enabled = startCurrentDate == currentDate,
                                        onClick = {
                                            val newStartCurrentDate = startCurrentDate.minusDays(1)
                                            dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = newStartCurrentDate)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowLeft,
                                            contentDescription = "Previous Start Date"
                                        )
                                    }

                                    Text(
                                        text = dateTimePickerViewModel.getDateString(date = startCurrentDate),
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    FilledIconButton(
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        enabled = startCurrentDate == currentDate.minusDays(1),
                                        onClick = {
                                            val newStartCurrentDate = startCurrentDate.plusDays(1)
                                            dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = newStartCurrentDate)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "Next Start Date"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "start-current-previous-time-picker",
                                contentType = "start-current-time-picker-button-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // 시작 시 변경
                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        enabled = startHourListState.canScrollBackward,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        onClick = {
                                            coroutineScope.launch {
                                                startHourListState.animateScrollToItem(index = startHourListState.firstVisibleItemIndex - 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Previous Start Hour"
                                        )
                                    }

                                    // 시작 분 변경
                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        enabled = startMinuteListState.canScrollBackward,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        onClick = {
                                            coroutineScope.launch {
                                                startMinuteListState.animateScrollToItem(index = startMinuteListState.firstVisibleItemIndex - 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Previous Start Minute"
                                        )
                                    }

                                    // 시작 초 변경
                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        enabled = startSecondListState.canScrollBackward,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        onClick = {
                                            coroutineScope.launch {
                                                startSecondListState.animateScrollToItem(index = startSecondListState.firstVisibleItemIndex - 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Previous Start Second"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "start-current-time-picker",
                                contentType = "start-current-time-picker-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((48 * 3).dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        state = startHourListState,
                                        flingBehavior = rememberSnapFlingBehavior(lazyListState = startHourListState)
                                    ) {
                                        item(
                                            key = "second-spacer-top",
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
                                                        startHourListState.animateScrollToItem(index = itemIndex)
                                                    }
                                                },
                                                shape = RectangleShape
                                            ) {
                                                Text(
                                                    text = "$itemIndex",
                                                    style = if (startHourListState.firstVisibleItemIndex == itemIndex && !startHourListState.isScrollInProgress) {
                                                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                    } else {
                                                        MaterialTheme.typography.bodySmall
                                                    }
                                                )
                                            }
                                        }

                                        item(
                                            key = "second-spacer-bottom",
                                            contentType = "spacer"
                                        ) {
                                            Spacer(modifier = Modifier.height(48.dp))
                                        }
                                    }

                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((48 * 3).dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        state = startMinuteListState,
                                        flingBehavior = rememberSnapFlingBehavior(lazyListState = startMinuteListState)
                                    ) {
                                        item(
                                            key = "second-spacer-top",
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
                                                        startMinuteListState.animateScrollToItem(index = itemIndex)
                                                    }
                                                },
                                                shape = RectangleShape
                                            ) {
                                                Text(
                                                    text = "$itemIndex",
                                                    style = if (startMinuteListState.firstVisibleItemIndex == itemIndex && !startMinuteListState.isScrollInProgress) {
                                                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                    } else {
                                                        MaterialTheme.typography.bodySmall
                                                    }
                                                )
                                            }
                                        }

                                        item(
                                            key = "second-spacer-bottom",
                                            contentType = "spacer"
                                        ) {
                                            Spacer(modifier = Modifier.height(48.dp))
                                        }
                                    }

                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((48 * 3).dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        state = startSecondListState,
                                        flingBehavior = rememberSnapFlingBehavior(lazyListState = startSecondListState)
                                    ) {
                                        item(
                                            key = "second-spacer-top",
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
                                                        startSecondListState.animateScrollToItem(index = itemIndex)
                                                    }
                                                },
                                                shape = RectangleShape
                                            ) {
                                                Text(
                                                    text = "$itemIndex",
                                                    style = if (startSecondListState.firstVisibleItemIndex == itemIndex && !startSecondListState.isScrollInProgress) {
                                                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                    } else {
                                                        MaterialTheme.typography.bodySmall
                                                    }
                                                )
                                            }
                                        }

                                        item(
                                            key = "second-spacer-bottom",
                                            contentType = "spacer"
                                        ) {
                                            Spacer(modifier = Modifier.height(48.dp))
                                        }
                                    }
                                }
                            }

                            item(
                                key = "start-current-next-time-picker",
                                contentType = "start-current-time-picker-button-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = startHourListState.canScrollForward,
                                        onClick = {
                                            coroutineScope.launch {
                                                startHourListState.animateScrollToItem(index = startHourListState.firstVisibleItemIndex + 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Next Start Hour"
                                        )
                                    }

                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = startMinuteListState.canScrollForward,
                                        onClick = {
                                            coroutineScope.launch {
                                                startMinuteListState.animateScrollToItem(index = startMinuteListState.firstVisibleItemIndex + 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Next Start Minute"
                                        )
                                    }

                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = startSecondListState.canScrollForward,
                                        onClick = {
                                            coroutineScope.launch {
                                                startSecondListState.animateScrollToItem(index = startSecondListState.firstVisibleItemIndex + 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Next Start Second"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "min-start-time-picker",
                                contentType = "start-time-picker-card"
                            ) {
                                val isMinStartEnabled = !(startHourListState.firstVisibleItemIndex == minStart.hour &&
                                        startMinuteListState.firstVisibleItemIndex == minStart.minute &&
                                        startSecondListState.firstVisibleItemIndex == minStart.second)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min),
                                    shape = RectangleShape,
                                    enabled = isMinStartEnabled,
                                    onClick = {
                                        dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = minStart.toLocalDate())
                                        dateTimePickerViewModel.setUpdateClickedWiDCopyStartToNowMinus12Hours(update = true)

                                        coroutineScope.launch {
                                            launch { startHourListState.animateScrollToItem(index = minStart.hour) }
                                            launch { startMinuteListState.animateScrollToItem(index = minStart.minute) }
                                            launch { startSecondListState.animateScrollToItem(index = minStart.second) }
                                        }
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Transparent)
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
                                                text = dateTimePickerViewModel.getDateTimeString(dateTime = minStart),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "최소 시간 사용"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "max-start-time-picker",
                                contentType = "start-time-picker-card"
                            ) {
                                val isMaxStartEnabled = !(startHourListState.firstVisibleItemIndex == maxStart.hour &&
                                        startMinuteListState.firstVisibleItemIndex == maxStart.minute &&
                                        startSecondListState.firstVisibleItemIndex == maxStart.second)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min),
                                    shape = RectangleShape,
                                    enabled = isMaxStartEnabled,
                                    onClick = {
                                        dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = maxStart.toLocalDate())

                                        coroutineScope.launch {
                                            launch { startHourListState.animateScrollToItem(index = maxStart.hour) }
                                            launch { startMinuteListState.animateScrollToItem(index = maxStart.minute) }
                                            launch { startSecondListState.animateScrollToItem(index = maxStart.second) }
                                        }
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Transparent)
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
                                                text = dateTimePickerViewModel.getDateTimeString(dateTime = maxStart),
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
                            contentPadding = PaddingValues(vertical = 8.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(
                                key = "finish-current-date-picker",
                                contentType = "finish-current-date-picker-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledIconButton(
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        enabled = finishCurrentDate == currentDate.plusDays(1),
                                        onClick = {
                                            val newFinishCurrentDate = finishCurrentDate.minusDays(1)
                                            dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = newFinishCurrentDate)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowLeft,
                                            contentDescription = "Previous Finish Date"
                                        )
                                    }

                                    Text(
                                        text = dateTimePickerViewModel.getDateString(date = finishCurrentDate),
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    FilledIconButton(
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        enabled = finishCurrentDate == currentDate,
                                        onClick = {
                                            val newFinishCurrentDate = finishCurrentDate.plusDays(1)
                                            dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = newFinishCurrentDate)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "Next Finish Date"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "finish-current-previous-time-picker",
                                contentType = "finish-current-time-picker-button-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = finishHourListState.canScrollBackward,
                                        onClick = {
                                            coroutineScope.launch {
                                                finishHourListState.animateScrollToItem(index = finishHourListState.firstVisibleItemIndex - 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Previous Finish Hour"
                                        )
                                    }

                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = finishMinuteListState.canScrollBackward,
                                        onClick = {
                                            coroutineScope.launch {
                                                finishMinuteListState.animateScrollToItem(index = finishMinuteListState.firstVisibleItemIndex - 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Previous Finish Minute"
                                        )
                                    }

                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = finishSecondListState.canScrollBackward,
                                        onClick = {
                                            coroutineScope.launch {
                                                finishSecondListState.animateScrollToItem(index = finishSecondListState.firstVisibleItemIndex - 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Previous Finish Second"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "finish-current-time-picker",
                                contentType = "finish-current-time-picker-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((48 * 3).dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        state = finishHourListState,
                                        flingBehavior = rememberSnapFlingBehavior(lazyListState = finishHourListState)
                                    ) {
                                        item(
                                            key = "second-spacer-top",
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
                                                        finishHourListState.animateScrollToItem(index = itemIndex)
                                                    }
                                                },
                                                shape = RectangleShape
                                            ) {
                                                Text(
                                                    text = "$itemIndex",
                                                    style = if (finishHourListState.firstVisibleItemIndex == itemIndex && !finishHourListState.isScrollInProgress) {
                                                        MaterialTheme.typography.bodyLarge
                                                    } else {
                                                        MaterialTheme.typography.bodySmall
                                                    }
                                                )
                                            }
                                        }

                                        item(
                                            key = "second-spacer-bottom",
                                            contentType = "spacer"
                                        ) {
                                            Spacer(modifier = Modifier.height(48.dp))
                                        }
                                    }

                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((48 * 3).dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        state = finishMinuteListState,
                                        flingBehavior = rememberSnapFlingBehavior(lazyListState = finishMinuteListState)
                                    ) {
                                        item(
                                            key = "second-spacer-top",
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
                                                        finishMinuteListState.animateScrollToItem(index = itemIndex)
                                                    }
                                                },
                                                shape = RectangleShape
                                            ) {
                                                Text(
                                                    text = "$itemIndex",
                                                    style = if (finishMinuteListState.firstVisibleItemIndex == itemIndex && !finishMinuteListState.isScrollInProgress) {
                                                        MaterialTheme.typography.bodyLarge
                                                    } else {
                                                        MaterialTheme.typography.bodySmall
                                                    }
                                                )
                                            }
                                        }

                                        item(
                                            key = "second-spacer-bottom",
                                            contentType = "spacer"
                                        ) {
                                            Spacer(modifier = Modifier.height(48.dp))
                                        }
                                    }

                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((48 * 3).dp)
                                            .border(
                                                width = 0.5.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.medium
                                            ),
                                        state = finishSecondListState,
                                        flingBehavior = rememberSnapFlingBehavior(lazyListState = finishSecondListState)
                                    ) {
                                        item(
                                            key = "second-spacer-top",
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
                                                        finishSecondListState.animateScrollToItem(index = itemIndex)
                                                    }
                                                },
                                                shape = RectangleShape
                                            ) {
                                                Text(
                                                    text = "$itemIndex",
                                                    style = if (finishSecondListState.firstVisibleItemIndex == itemIndex && !finishSecondListState.isScrollInProgress) {
                                                        MaterialTheme.typography.bodyLarge
                                                    } else {
                                                        MaterialTheme.typography.bodySmall
                                                    }
                                                )
                                            }
                                        }

                                        item(
                                            key = "second-spacer-bottom",
                                            contentType = "spacer"
                                        ) {
                                            Spacer(modifier = Modifier.height(48.dp))
                                        }
                                    }
                                }
                            }

                            item(
                                key = "finish-current-next-time-picker",
                                contentType = "finish-current-time-picker-button-row"
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = finishHourListState.canScrollForward,
                                        onClick = {
                                            coroutineScope.launch {
                                                finishHourListState.animateScrollToItem(index = finishHourListState.firstVisibleItemIndex + 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Next Finish Hour"
                                        )
                                    }

                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = finishMinuteListState.canScrollForward,
                                        onClick = {
                                            coroutineScope.launch {
                                                finishMinuteListState.animateScrollToItem(index = finishMinuteListState.firstVisibleItemIndex + 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Next Finish Minute"
                                        )
                                    }

                                    FilledIconButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        enabled = finishSecondListState.canScrollForward,
                                        onClick = {
                                            coroutineScope.launch {
                                                finishSecondListState.animateScrollToItem(index = finishSecondListState.firstVisibleItemIndex + 1)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Next Finish Second"
                                        )
                                    }
                                }
                            }

                            item(
                                key = "min-finish-time-picker",
                                contentType = "finish-time-picker-card"
                            ) {
                                val isMinFinishEnabled = !(finishHourListState.firstVisibleItemIndex == minFinish.hour &&
                                        finishMinuteListState.firstVisibleItemIndex == minFinish.minute &&
                                        finishSecondListState.firstVisibleItemIndex == minFinish.second)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min),
                                    shape = RectangleShape,
                                    enabled = isMinFinishEnabled,
                                    onClick = {
                                        dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = minFinish.toLocalDate())

                                        coroutineScope.launch {
                                            launch { finishHourListState.animateScrollToItem(index = minFinish.hour) }
                                            launch { finishMinuteListState.animateScrollToItem(index = minFinish.minute) }
                                            launch { finishSecondListState.animateScrollToItem(index = minFinish.second) }
                                        }
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Transparent)
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
                                                text = dateTimePickerViewModel.getDateTimeString(dateTime = minFinish),
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

                            item(
                                key = "max-finish-time-picker",
                                contentType = "finish-time-picker-card"
                            ) {
                                val isMaxFinishEnabled = if (isLastNewWiD) {
                                    !updateClickedWiDCopyToNow
                                } else {
                                    !(finishHourListState.firstVisibleItemIndex == maxFinish.hour &&
                                            finishMinuteListState.firstVisibleItemIndex == maxFinish.minute &&
                                            finishSecondListState.firstVisibleItemIndex == maxFinish.second)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min),
                                    enabled = isMaxFinishEnabled,
                                    onClick = {
                                        dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = maxFinish.toLocalDate())

                                        coroutineScope.launch {
                                            launch { finishHourListState.animateScrollToItem(index = maxFinish.hour) }
                                            launch { finishMinuteListState.animateScrollToItem(index = maxFinish.minute) }
                                            launch { finishSecondListState.animateScrollToItem(index = maxFinish.second) }
                                        }
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Transparent)
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
                                                text = dateTimePickerViewModel.getDateTimeString(dateTime = maxFinish),
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