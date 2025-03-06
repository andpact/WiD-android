package andpact.project.wid.view

import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.DateTimePickerViewModel
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import java.time.Duration
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

//    val now = dateTimePickerViewModel.now.value

    val user = dateTimePickerViewModel.user.value
    val wiDMinLimit = user?.wiDMinLimit ?: Duration.ofMinutes(5)
    val wiDMaxLimit = user?.wiDMaxLimit ?: Duration.ofHours(12)

    val isLastNewWiD = dateTimePickerViewModel.isLastNewWiD.value

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = if (previousView == PreviousView.CLICKED_WID_START) 0 else 1, pageCount = { 2 })

    val clickedWiD = dateTimePickerViewModel.clickedWiD.value
    val clickedWiDCopy = dateTimePickerViewModel.clickedWiDCopy.value

    val minStart = dateTimePickerViewModel.minStart.value // 조회 기록의 최소 시작
    val maxFinish = dateTimePickerViewModel.maxFinish.value // 조회 기록의 최대 종료
    val updateMaxFinish = dateTimePickerViewModel.updateMaxFinish.value
    val applyMaxFinish = dateTimePickerViewModel.applyMaxFinish.value // 조회 기록의 최대 종료을 뷰에서 사용할지

    val startCurrentDate = dateTimePickerViewModel.startCurrentDate.value
    val startHourListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.start.hour)
    val startMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.start.minute)
    val startSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.start.second)
    val startCurrentTime = LocalTime.of(startHourListState.firstVisibleItemIndex, startMinuteListState.firstVisibleItemIndex, startSecondListState.firstVisibleItemIndex)
    val currentStart = LocalDateTime.of(startCurrentDate, startCurrentTime)
    val currentStartEnabled = minStart <= currentStart

    val finishCurrentDate = dateTimePickerViewModel.finishCurrentDate.value
    val finishHourListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.finish.hour)
    val finishMinuteListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.finish.minute)
    val finishSecondListState = rememberLazyListState(initialFirstVisibleItemIndex = clickedWiDCopy.finish.second)
    val finishCurrentTime = LocalTime.of(finishHourListState.firstVisibleItemIndex, finishMinuteListState.firstVisibleItemIndex, finishSecondListState.firstVisibleItemIndex)
    val currentFinish = LocalDateTime.of(finishCurrentDate, finishCurrentTime)
    val currentFinishEnabled = currentFinish <= maxFinish

    val currentDuration = Duration.between(currentStart, currentFinish)
    val currentDurationEnabled = currentDuration in wiDMinLimit..wiDMaxLimit

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        dateTimePickerViewModel.setCurrentDate(newCurrentDate = currentDate) // currentStart와 currentFinish의 제한을 구하기 위해서 설정함.

        dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = clickedWiDCopy.start.toLocalDate()) // 시작 날짜 초기화
        dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = clickedWiDCopy.finish.toLocalDate()) // 종료 날짜 초기화

        dateTimePickerViewModel.setApplyMaxFinish(apply = isLastNewWiD) // 마지막 새 기록만 maxFinish 적용되어 있음.

        onDispose { Log.d(TAG, "disposed") }
    }

    LaunchedEffect( // 최대 종료 시간 사용
        key1 = maxFinish,
        block = {
            if (applyMaxFinish) {
                if (currentFinish.toLocalDate() != maxFinish.toLocalDate()) { dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = maxFinish.toLocalDate()) }
                coroutineScope.launch {
                    if (currentFinish.hour != maxFinish.hour) {
                        finishHourListState.animateScrollToItem(index = maxFinish.hour)
                    }
                    if (currentFinish.minute != maxFinish.minute) {
                        finishMinuteListState.animateScrollToItem(index = maxFinish.minute)
                    }
                    if (currentFinish.second != maxFinish.second) {
                        finishSecondListState.animateScrollToItem(index = maxFinish.second)
                    }
                }
            }
        }
    )

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
                                onBackButtonPressed()
                                dateTimePickerViewModel.setClickedWiDCopyStart(newStart = clickedWiD.start)
                                dateTimePickerViewModel.setClickedWiDCopyFinish(newFinish = clickedWiD.finish)
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
                    }
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
                        onClick = {
                            onBackButtonPressed()
                            dateTimePickerViewModel.setClickedWiDCopyStart(newStart = clickedWiD.start)
                            dateTimePickerViewModel.setClickedWiDCopyFinish(newFinish = clickedWiD.finish)
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
                            onBackButtonPressed()

                            dateTimePickerViewModel.setClickedWiDCopyStart(newStart = currentStart) // 시작 수정

                            if (applyMaxFinish) {
                                dateTimePickerViewModel.setUpdateClickedWiDCopyFinishToMaxFinish(update = true) // 종료 시간 갱신
                            } else {
                                dateTimePickerViewModel.setClickedWiDCopyFinish(newFinish = currentFinish) // 종료 수정
                            }
                        },
                        enabled = currentStartEnabled && currentFinishEnabled && currentDurationEnabled
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
//                    icon = { Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "시작 시점") },
                    text = { Text(text = "시작 시점") }
                )

                Tab(
                    selected = currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
//                    icon = { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "종료 시점") },
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
                            contentPadding = PaddingValues(vertical = 8.dp)
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
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height((48 * 3).dp),
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
                                                            MaterialTheme.typography.labelSmall
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

                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height((48 * 3).dp),
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
                                                            MaterialTheme.typography.labelSmall
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

                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height((48 * 3).dp),
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
                                                            MaterialTheme.typography.labelSmall
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

                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )
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
                                key = "min-start",
                                contentType = "list-item"
                            ) {
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            if (startCurrentDate != minStart.toLocalDate()) {
                                                dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = minStart.toLocalDate())
                                            }

                                            if (currentStart != minStart) {
                                                coroutineScope.launch {
                                                    launch { startHourListState.animateScrollToItem(index = minStart.hour) }
                                                    launch { startMinuteListState.animateScrollToItem(index = minStart.minute) }
                                                    launch { startSecondListState.animateScrollToItem(index = minStart.second) }
                                                }
                                            }
                                        },
                                    headlineContent = {
                                        Text(text = "최소 시작 시간 제한")
                                    },
                                    supportingContent = {
                                        Text(text = dateTimePickerViewModel.getDateTimeString(dateTime = minStart))
                                    },
                                    trailingContent = {
                                        RadioButton(
                                            selected = currentStart == minStart,
                                            onClick = null
                                        )
                                    }
                                )

//                                Card(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(72.dp)
//                                        .height(intrinsicSize = IntrinsicSize.Min),
//                                    enabled = currentStart != minStart,
//                                    onClick = {
//                                        dateTimePickerViewModel.setStartCurrentDate(newStartCurrentDate = minStart.toLocalDate())
//                                        coroutineScope.launch {
//                                            launch { startHourListState.animateScrollToItem(index = minStart.hour) }
//                                            launch { startMinuteListState.animateScrollToItem(index = minStart.minute) }
//                                            launch { startSecondListState.animateScrollToItem(index = minStart.second) }
//                                        }
//                                    },
//                                    shape = RectangleShape,
//                                    colors = CardDefaults.cardColors(
//                                        containerColor = Transparent,
//                                        disabledContainerColor = Transparent
//                                    )
//                                ) {
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxHeight()
//                                            .padding(horizontal = 16.dp),
//                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Column(
//                                            modifier = Modifier
//                                                .weight(1f)
//                                        ) {
//                                            Text(
//                                                text = "최소 시작 시간 제한",
//                                                style = MaterialTheme.typography.bodyLarge
//                                            )
//
//                                            Text(
//                                                text = dateTimePickerViewModel.getDateTimeString(dateTime = minStart),
//                                                style = MaterialTheme.typography.bodyMedium
//                                            )
//                                        }
//
//                                        Icon(
//                                            imageVector = Icons.Default.KeyboardArrowDown,
//                                            contentDescription = "최소 시작 시간 사용",
//                                        )
//                                    }
//                                }
                            }

                            item(
                                key = "min-start-divider",
                                contentType = "divider"
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }

                            item(
                                key = "current-duration-with-min-start",
                                contentType = "list-item"
                            ) {
                                ListItem(
                                    overlineContent = {
                                        Text(
                                            text = "최소 ${dateTimePickerViewModel.getDurationString(duration = wiDMinLimit)} ~ 최대 ${dateTimePickerViewModel.getDurationString(duration = wiDMaxLimit)}",
                                            maxLines = 1
                                        )
                                    },
                                    headlineContent = {
                                        Text(text = "현재 소요 시간")
                                    },
                                    supportingContent = {
                                        Text(text = dateTimePickerViewModel.getDurationString(duration = currentDuration))
                                    }
                                )
                            }
                        }
                    }
                    1 -> { // 종료 시간 수정
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp),
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
                                        enabled = finishCurrentDate == currentDate.plusDays(1) && !applyMaxFinish,
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
                                        enabled = finishCurrentDate == currentDate && !applyMaxFinish,
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
                                        enabled = finishHourListState.canScrollBackward && !applyMaxFinish,
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
                                        enabled = finishMinuteListState.canScrollBackward && !applyMaxFinish,
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
                                        enabled = finishSecondListState.canScrollBackward && !applyMaxFinish,
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
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height((48 * 3).dp),
                                            state = finishHourListState,
                                            userScrollEnabled = !applyMaxFinish,
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
                                                    enabled = !applyMaxFinish,
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
                                                            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                        } else {
                                                            MaterialTheme.typography.labelSmall
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

                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height((48 * 3).dp),
                                            state = finishMinuteListState,
                                            userScrollEnabled = !applyMaxFinish,
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
                                                    enabled = !applyMaxFinish,
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
                                                            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                        } else {
                                                            MaterialTheme.typography.labelSmall
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

                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )

                                        LazyColumn(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height((48 * 3).dp),
                                            state = finishSecondListState,
                                            userScrollEnabled = !applyMaxFinish,
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
                                                    enabled = !applyMaxFinish,
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
                                                            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                        } else {
                                                            MaterialTheme.typography.labelSmall
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

                                        VerticalDivider(
                                            modifier = Modifier
                                                .height((48 * 3).dp),
                                            thickness = 0.5.dp
                                        )
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
                                        enabled = finishHourListState.canScrollForward && !applyMaxFinish,
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
                                        enabled = finishMinuteListState.canScrollForward && !applyMaxFinish,
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
                                        enabled = finishSecondListState.canScrollForward && !applyMaxFinish,
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
                                key = "max-finish",
                                contentType = "list-item"
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .height(intrinsicSize = IntrinsicSize.Min),
                                    enabled = if (updateMaxFinish) { true } else { currentFinish != maxFinish },
                                    onClick = {
                                        if (updateMaxFinish) { // 최대 종료 갱신 상태
                                            dateTimePickerViewModel.setApplyMaxFinish(!applyMaxFinish)
                                            dateTimePickerViewModel.setUpdateClickedWiDCopyFinishToMaxFinish(!applyMaxFinish)
                                        } else { // 최대 종료 정지 상태
                                            if (finishCurrentDate != maxFinish.toLocalDate()) {
                                                dateTimePickerViewModel.setFinishCurrentDate(newFinishCurrentDate = maxFinish.toLocalDate())
                                            }
                                            if (currentFinish == maxFinish) {
                                                coroutineScope.launch {
                                                    launch { finishHourListState.animateScrollToItem(index = maxFinish.hour) }
                                                    launch { finishMinuteListState.animateScrollToItem(index = maxFinish.minute) }
                                                    launch { finishSecondListState.animateScrollToItem(index = maxFinish.second) }
                                                }
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
                                                text = "최대 종료 시간 제한",
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Text(
                                                text = dateTimePickerViewModel.getDateTimeString(dateTime = maxFinish),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        if (updateMaxFinish) {
                                            Switch(
                                                checked = applyMaxFinish,
                                                onCheckedChange = {
                                                    dateTimePickerViewModel.setApplyMaxFinish(it)
                                                    dateTimePickerViewModel.setUpdateClickedWiDCopyFinishToMaxFinish(it)
                                                }
                                            )
                                        } else {
                                            RadioButton(
                                                selected = currentFinish == maxFinish,
                                                onClick = null
                                            )
                                        }
                                    }
                                }
                            }

                            item(
                                key = "max-finish-divider",
                                contentType = "divider"
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }

                            item(
                                key = "current-duration-with-max-finish",
                                contentType = "list-item"
                            ) {
                                ListItem(
                                    overlineContent = {
                                        Text(
                                            text = "최소 ${dateTimePickerViewModel.getDurationString(duration = wiDMinLimit)} ~ 최대 ${dateTimePickerViewModel.getDurationString(duration = wiDMaxLimit)}",
                                            maxLines = 1
                                        )
                                    },
                                    headlineContent = {
                                        Text(text = "현재 소요 시간")
                                    },
                                    supportingContent = {
                                        Text(text = dateTimePickerViewModel.getDurationString(duration = currentDuration))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}