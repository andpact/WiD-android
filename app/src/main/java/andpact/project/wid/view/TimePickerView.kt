package andpact.project.wid.view

import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.TimePickerViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
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

//    val previousView: PreviousView = PreviousView.valueOf(previousViewString)
    
//    val START = timePickerViewModel.START
//    val FINISH = timePickerViewModel.FINISH

    val isLastNewWiD = timePickerViewModel.isLastNewWiD.value

    val updateClickedWiDCopyToNow = timePickerViewModel.updateClickedWiDCopyToNow.value

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val coroutineScope = rememberCoroutineScope()

    val clickedWiD = timePickerViewModel.clickedWiD.value
    val clickedWiDCopy = timePickerViewModel.clickedWiDCopy.value

    val startHourPagerState = rememberPagerState(initialPage = clickedWiD.start.hour, pageCount = { 24 })
    val startMinutePagerState = rememberPagerState(initialPage = clickedWiD.start.minute, pageCount = { 60 })
    val startSecondPagerState = rememberPagerState(initialPage = clickedWiD.start.second, pageCount = { 60 })
    val isStartOutOfRange = timePickerViewModel.isStartOutOfRange.value
    val minStart = timePickerViewModel.minStart.value
    val maxStart = timePickerViewModel.maxStart.value

    val finishHourPagerState = rememberPagerState(initialPage = clickedWiD.finish.hour, pageCount = { 24 })
    val finishMinutePagerState = rememberPagerState(initialPage = clickedWiD.finish.minute, pageCount = { 60 })
    val finishSecondPagerState = rememberPagerState(initialPage = clickedWiD.finish.second, pageCount = { 60 })
    val isFinishOutOfRange = timePickerViewModel.isFinishOutOfRange.value
    val minFinish = timePickerViewModel.minFinish.value
    val maxFinish = timePickerViewModel.maxFinish.value

    LaunchedEffect(clickedWiDCopy) {
        finishHourPagerState.scrollToPage(clickedWiDCopy.finish.hour)
        finishMinutePagerState.scrollToPage(clickedWiDCopy.finish.minute)
        finishSecondPagerState.scrollToPage(clickedWiDCopy.finish.second)
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    // TODO: 백 버튼 누르면 초기화되도록 
    
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
                            onBackButtonPressed()
                            // TODO: 초기화 되도록 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기",
                        )
                    }
                },
                title = {
                    Text(text = "${PreviousView.CLICKED_WID.kr} > ${previousView.kr} 시간 선택")
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .height(48.dp),
                    shape = RectangleShape, // TODO: 버튼 색상 뭐로할거?
                    onClick = {
                        coroutineScope.launch {
                            if (previousView == PreviousView.CLICKED_WID_START) { // 시작 시간 변경
                                startHourPagerState.scrollToPage(page = clickedWiDCopy.start.hour)
                                startMinutePagerState.scrollToPage(page = clickedWiDCopy.start.minute)
                                startSecondPagerState.scrollToPage(page = clickedWiDCopy.start.second)
                            } else { // 종료 시간 변경
                                finishHourPagerState.scrollToPage(page = clickedWiDCopy.finish.hour)
                                finishMinutePagerState.scrollToPage(page = clickedWiDCopy.finish.minute)
                                finishSecondPagerState.scrollToPage(page = clickedWiDCopy.finish.second)
                            }
                        }
                    }
                ) {
                    Text(text = "취소")
                }

                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RectangleShape,
                    onClick = {
                        if (previousView == PreviousView.CLICKED_WID_START) { // 시작 시간 수정
                            val newStart = LocalTime.of(startHourPagerState.currentPage, startMinutePagerState.currentPage, startSecondPagerState.currentPage)
                            timePickerViewModel.setClickedWiDCopyStart(newStart = newStart)
                        } else { // 종료 시간 수정
                            val newFinish = LocalTime.of(finishHourPagerState.currentPage, finishMinutePagerState.currentPage, finishSecondPagerState.currentPage)

                            timePickerViewModel.setClickedWiDCopyFinish(newFinish = newFinish)
                            timePickerViewModel.setUpdateClickedWiDCopyToNow(update = false)
                        }
                    },
                    enabled = if (previousView == PreviousView.CLICKED_WID_START) { !isStartOutOfRange } else { !isFinishOutOfRange },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = LimeGreen,
                        contentColor = White
                    )
                ) {
                    Text(text = "확인")
                }
            }
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (previousView == PreviousView.CLICKED_WID_START) { // 시작 시간 수정
                TimeSelectorView(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(screenHeight / 3), // TODO: 높이 고정
                    hourPagerState = startHourPagerState,
                    minutePagerState = startMinutePagerState,
                    secondPagerState = startSecondPagerState,
                    coroutineScope = coroutineScope
                )

                val isMinStartEnabled = !(startHourPagerState.currentPage == minStart.hour &&
                        startMinutePagerState.currentPage == minStart.minute &&
                        startSecondPagerState.currentPage == minStart.second)

                // TODO: 비활성화 때 컨텐츠 색상 올바르게 수정해야 함.
                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 바깥 패딩
                        .clip(shape = MaterialTheme.shapes.medium)
                        .clickable(
                            enabled = isMinStartEnabled,
                            onClick = {
                                coroutineScope.launch {
                                    launch {
                                        startHourPagerState.animateScrollToPage(
                                            page = minStart.hour
                                        )
                                    }
                                    launch {
                                        startMinutePagerState.animateScrollToPage(
                                            page = minStart.minute
                                        )
                                    }
                                    launch {
                                        startSecondPagerState.animateScrollToPage(
                                            page = minStart.second
                                        )
                                    }
                                }
                            }
                        )
                        .background(
                            color = if (isMinStartEnabled) MaterialTheme.colorScheme.primaryContainer // 활성화 색상
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
                            shape = MaterialTheme.shapes.medium
                        ),
                    headlineContent = {
                        Text(
                            text = "선택 가능한 최소 시간",
                            color = if (isMinStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    supportingContent = {
                        Text(
                            text = timePickerViewModel.getTimeString(time = minStart),
                            color = if (isMinStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "최소 시간 사용",
                            tint = if (isMinStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
                        )
                    }
                )

                val isMaxStartEnabled = !(startHourPagerState.currentPage == maxStart.hour &&
                        startMinutePagerState.currentPage == maxStart.minute &&
                        startSecondPagerState.currentPage == maxStart.second)

                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 바깥 패딩
                        .clip(shape = MaterialTheme.shapes.medium)
                        .clickable(
                            enabled = isMaxStartEnabled,
                            onClick = {
                                coroutineScope.launch {
                                    launch {
                                        startHourPagerState.animateScrollToPage(
                                            page = maxStart.hour
                                        )
                                    }
                                    launch {
                                        startMinutePagerState.animateScrollToPage(
                                            page = maxStart.minute
                                        )
                                    }
                                    launch {
                                        startSecondPagerState.animateScrollToPage(
                                            page = maxStart.second
                                        )
                                    }
                                }
                            }
                        )
                        .background(
                            color = if (isMaxStartEnabled) MaterialTheme.colorScheme.primaryContainer // 활성화 색상
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
                            shape = MaterialTheme.shapes.medium
                        ),
                    headlineContent = {
                        Text(
                            text = "선택 가능한 최대 시간",
                            color = if (isMaxStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    supportingContent = {
                        Text(
                            text = timePickerViewModel.getTimeString(time = maxStart),
                            color = if (isMaxStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "최대 시간 사용",
                            tint = if (isMaxStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
                        )
                    }
                )
            } else { // 종료 시간 수정
                TimeSelectorView(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(screenHeight / 3), // TODO: 높이 고정
                    hourPagerState = finishHourPagerState,
                    minutePagerState = finishMinutePagerState,
                    secondPagerState = finishSecondPagerState,
                    coroutineScope = coroutineScope,
                    onTimeChanged = { // 종료 시간 변경 시 갱신을 멈춤
                        if (isLastNewWiD) timePickerViewModel.setUpdateClickedWiDCopyToNow(update = false)
                    }
                )

                val isMinFinishEnabled = !(finishHourPagerState.currentPage == minFinish.hour &&
                        finishMinutePagerState.currentPage == minFinish.minute &&
                        finishSecondPagerState.currentPage == minFinish.second)

                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 바깥 패딩
                        .clip(shape = MaterialTheme.shapes.medium)
                        .clickable(
                            enabled = isMinFinishEnabled,
                            onClick = {
                                coroutineScope.launch {
                                    launch {
                                        finishHourPagerState.animateScrollToPage(
                                            page = minFinish.hour
                                        )
                                    }
                                    launch {
                                        finishMinutePagerState.animateScrollToPage(
                                            page = minFinish.minute
                                        )
                                    }
                                    launch {
                                        finishSecondPagerState.animateScrollToPage(
                                            page = minFinish.second
                                        )
                                    }
                                }
                            }
                        )
                        .background(
                            color = if (isMinFinishEnabled) MaterialTheme.colorScheme.primaryContainer // 활성화 색상
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
                            shape = MaterialTheme.shapes.medium
                        ),
                    headlineContent = {
                        Text(
                            text = "선택 가능한 최소 시간",
                            color = if (isMinFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    supportingContent = {
                        Text(
                            text = timePickerViewModel.getTimeString(time = minFinish),
                            color = if (isMinFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "최소 시간 사용",
                            tint = if (isMinFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
                        )
                    }
                )

                val isMaxFinishEnabled = if (isLastNewWiD) {
                    !updateClickedWiDCopyToNow
                } else {
                    !(finishHourPagerState.currentPage == maxFinish.hour &&
                            finishMinutePagerState.currentPage == maxFinish.minute &&
                            finishSecondPagerState.currentPage == maxFinish.second)
                }

                ListItem(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 바깥 패딩
                        .clip(shape = MaterialTheme.shapes.medium)
                        .clickable(
                            enabled = isMaxFinishEnabled,
                            onClick = {
                                coroutineScope.launch {
                                    launch {
                                        finishHourPagerState.animateScrollToPage(
                                            page = maxFinish.hour
                                        )
                                    }
                                    launch {
                                        finishMinutePagerState.animateScrollToPage(
                                            page = maxFinish.minute
                                        )
                                    }
                                    launch {
                                        finishSecondPagerState.animateScrollToPage(
                                            page = maxFinish.second
                                        )
                                    }
                                }
                            }
                        )
                        .background(
                            color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
                            shape = MaterialTheme.shapes.medium
                        ),
                    headlineContent = {
                        Text(
                            text = "선택 가능한 최대 시간",
                            color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )
                    },
                    supportingContent = {
                        Text(
                            text = timePickerViewModel.getTimeString(time = maxFinish),
                            color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
                        )

                        if (updateClickedWiDCopyToNow) {
                            Text(
                                modifier = Modifier
                                    .background(
                                        color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.errorContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = 4.dp),
                                text = "Now",
                                color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onError
                                else MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "최대 시간 사용",
                            tint = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun TimePickerPreview() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기",
                        )
                    }
                },
                title = {
                    Text(text = "기록 > 시간 선택")
                },
                actions = {
                    TextButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Text(text = "확인")
                    }
                }
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // TODO: 초기화 버튼?
//                Spacer(modifier = Modifier.weight(1f))

                FilledTonalButton(
                    modifier = Modifier
                        .height(48.dp),
                    shape = RectangleShape,
                    onClick = {
                    }
                ) {
                    Text(text = "취소")
                }

                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RectangleShape,
                    onClick = {
                    },
                    enabled = true,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = LimeGreen,
                        contentColor = White
                    )
                ) {
                    Text(text = "확인")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                onClick = { /*TODO*/ },
                enabled = true
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    leadingContent = {
                        Icon(imageVector = Icons.Default.AccountBox, contentDescription = "")
                    },
//                    overlineContent = {
//                        Text(text = "overlineContent")
//                    },
                    headlineContent = {
                        Text(text = "headlineContent")
                    },
                    supportingContent = {
                        Text(text = "supportingContent")
                    },
                    trailingContent = {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "")
                    }
                )
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                onClick = { /*TODO*/ },
                enabled = false
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
//                        leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        leadingIconColor = if (true) { ListItemDefaults.colors().leadingIconColor } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) },
//                        overlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
//                        headlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
//                        supportingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
//                        trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
//                    overlineContent = {
//                        Text(text = "overlineContent")
//                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "")
                    },
                    headlineContent = {
                        Text(text = "headlineContent2")
                    },
                    supportingContent = {
                        Text(text = "supportingContent2")
                    },
                    trailingContent = {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                )
            }
        }
    }
}