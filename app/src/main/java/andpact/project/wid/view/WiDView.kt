package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.model.City
import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.WiDViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDView(
    onBackButtonPressed: () -> Unit,
    onTitlePickerClicked: () -> Unit,
    onTimePickerClicked: (previousView: PreviousView) -> Unit,
    onCityPickerClicked: (currentCity: City) -> Unit,
    wiDViewModel: WiDViewModel = hiltViewModel()
) {
    val TAG = "WiDView"

//    val now = wiDViewModel.now.value

    // WiD
    val clickedWiD = wiDViewModel.clickedWiD.value
    val clickedWiDCopy = wiDViewModel.clickedWiDCopy.value
    val updateClickedWiDToNow = wiDViewModel.updateClickedWiDToNow.value
    val updateClickedWiDCopyToNow = wiDViewModel.updateClickedWiDCopyToNow.value
    val isNewWiD = wiDViewModel.isNewWiD.value
    val isLastNewWiD = wiDViewModel.isLastNewWiD.value

    // 화면
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//    val coroutineScope = rememberCoroutineScope()

    // 제목
    val titleExist = wiDViewModel.titleExist.value
    val titleModified = wiDViewModel.titleModified.value

    // 시작 시간
//    val showStartPicker = wiDViewModel.showStartPicker.value
//    val startHourPagerState = rememberPagerState(initialPage = clickedWiD.start.hour, pageCount = { 24 })
//    val startMinutePagerState = rememberPagerState(initialPage = clickedWiD.start.minute, pageCount = { 60 })
//    val startSecondPagerState = rememberPagerState(initialPage = clickedWiD.start.second, pageCount = { 60 })
//    val isStartOutOfRange = wiDViewModel.isStartOutOfRange.value
    val startModified = wiDViewModel.startModified.value
//    val minStart = wiDViewModel.minStart.value
//    val maxStart = wiDViewModel.maxStart.value

    // 종료 시간
//    val showFinishPicker = wiDViewModel.showFinishPicker.value
//    val finishHourPagerState = rememberPagerState(initialPage = clickedWiD.finish.hour, pageCount = { 24 })
//    val finishMinutePagerState = rememberPagerState(initialPage = clickedWiD.finish.minute, pageCount = { 60 })
//    val finishSecondPagerState = rememberPagerState(initialPage = clickedWiD.finish.second, pageCount = { 60 })
//    val isFinishOutOfRange = wiDViewModel.isFinishOutOfRange.value
    val finishModified = wiDViewModel.finishModified.value
//    val minFinish = wiDViewModel.minFinish.value
//    val maxFinish = wiDViewModel.maxFinish.value

    val cityModified = wiDViewModel.cityModified.value

    val showDeleteWiDDialog = wiDViewModel.showDeleteWiDDialog.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

//    LaunchedEffect(clickedWiDCopy) {
//        finishHourPagerState.scrollToPage(clickedWiDCopy.finish.hour)
//        finishMinutePagerState.scrollToPage(clickedWiDCopy.finish.minute)
//        finishSecondPagerState.scrollToPage(clickedWiDCopy.finish.second)
//    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isLastNewWiD) { // 나갈 때도 확인
                                wiDViewModel.setUpdateClickedWiDToNow(update = false)
                                wiDViewModel.setUpdateClickedWiDCopyToNow(update = false)
                            }

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
                    Text(text = if (isNewWiD) "새로운 기록" else "기록")
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledIconButton(
                    modifier = Modifier
                        .size(56.dp),
                    onClick = {
                        wiDViewModel.setClickedWiDCopy(newClickedWiDCopy = clickedWiD) // 제목, 시작, 종료 초기화
                        wiDViewModel.setUpdateClickedWiDCopyToNow(update = isLastNewWiD)
                    },
                    enabled = clickedWiD != clickedWiDCopy, // TODO: 이미 초기 상태면 클릭할 수 없도록.
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "초기화",
                    )
                }

                if (!isNewWiD) {
                    FilledIconButton(
                        modifier = Modifier
                            .size(56.dp),
                        onClick = {
                            wiDViewModel.setShowDeleteWiDDialog(show = true)
                        },
                        shape = RectangleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제"
                        )
                    }
                }

                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        if (isNewWiD) {
                            wiDViewModel.createWiD(
                                onWiDCreated = {
                                    if (it) {
                                        onBackButtonPressed()
                                    }
                                }
                            )
                        } else {
                            wiDViewModel.updateWiD(
                                onWiDUpdated = {
                                    if (it) {
                                        onBackButtonPressed()
                                    }
                                }
                            )
                        }
                    },
                    enabled = if (isNewWiD) { titleExist } else { titleModified || startModified || finishModified || cityModified },
                    shape = RectangleShape
                ) {
                    Text(text = if (isNewWiD) { "새로운 기록 생성" } else { "수정 완료" })
                }
            }
        },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "날짜",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = wiDViewModel.getDateString(date = clickedWiDCopy.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onTitlePickerClicked() // 이미 데이터 소스의 클릭된 위드는 갱신된 상태
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "제목",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = clickedWiDCopy.title.kr,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (titleModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Text(
                                text = clickedWiD.title.kr,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "제목 수정 뷰로 이동"
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onTimePickerClicked(PreviousView.CLICKED_WID_START)
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "시작",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = wiDViewModel.getTimeString(time = clickedWiDCopy.start),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (startModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = wiDViewModel.getTimeString(time = clickedWiD.start),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "시작 종료 뷰로 이동"
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onTimePickerClicked(PreviousView.CLICKED_WID_FINISH)
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "종료",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = wiDViewModel.getTimeString(time = clickedWiDCopy.finish),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (updateClickedWiDCopyToNow) {
                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.error,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .padding(horizontal = 4.dp),
                                    text = "Now",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }

                    if (finishModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = wiDViewModel.getTimeString(time = clickedWiD.finish),
                                    style = MaterialTheme.typography.bodyMedium,
                                )

                                if (updateClickedWiDToNow) {
                                    Text(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.error,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(horizontal = 4.dp),
                                        text = "Now",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "종료 선택 뷰로 이동",
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "소요",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = wiDViewModel.getDurationString(clickedWiDCopy.duration),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (startModified || finishModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = wiDViewModel.getDurationString(clickedWiD.duration),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Icon(
                        modifier = Modifier
                            .alpha(0f),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "도구",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = clickedWiDCopy.createdBy.kr,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "경험치",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "${clickedWiD.exp}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalDimensions.current.listItemHeight)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onCityPickerClicked(clickedWiDCopy.city)
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "위치",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = clickedWiDCopy.city.kr + ", " + clickedWiDCopy.city.country.kr,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (cityModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = clickedWiD.city.kr + ", " + clickedWiD.city.country.kr,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "도시 선택 뷰로 이동"
                    )
                }
            }

//            if (showStartPicker) {
//                AlertDialog(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.surface,
//                            shape = MaterialTheme.shapes.extraLarge
//                        ),
//                    onDismissRequest = { // 취소와 동일
//                        wiDViewModel.setShowStartPicker(show = false)
//
//                        coroutineScope.launch { // 초기 상태가 아니라 해당 변경 이전으로 돌림
//                            startHourPagerState.scrollToPage(page = clickedWiDCopy.start.hour)
//                            startMinutePagerState.scrollToPage(page = clickedWiDCopy.start.minute)
//                            startSecondPagerState.scrollToPage(page = clickedWiDCopy.start.second)
//                        }
//                    },
//                    content = {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .height(56.dp),
//                                text = "시작 시간 선택",
//                                style = Typography.titleLarge,
//                                textAlign = TextAlign.Center
//                            )
//
//                            TimeSelectorView(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .height(screenHeight / 3),
//                                hourPagerState = startHourPagerState,
//                                minutePagerState = startMinutePagerState,
//                                secondPagerState = startSecondPagerState,
//                                coroutineScope = coroutineScope
//                            )
//
//                            val isMinStartEnabled = !(startHourPagerState.currentPage == minStart.hour &&
//                                    startMinutePagerState.currentPage == minStart.minute &&
//                                    startSecondPagerState.currentPage == minStart.second)
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(56.dp)
//                                    .padding(horizontal = 16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable(
//                                        enabled = isMinStartEnabled,
//                                        onClick = {
//                                            coroutineScope.launch {
//                                                launch {
//                                                    startHourPagerState.animateScrollToPage(
//                                                        page = minStart.hour
//                                                    )
//                                                }
//                                                launch {
//                                                    startMinutePagerState.animateScrollToPage(
//                                                        page = minStart.minute
//                                                    )
//                                                }
//                                                launch {
//                                                    startSecondPagerState.animateScrollToPage(
//                                                        page = minStart.second
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    )
//                                    .background(
//                                        color = if (isMinStartEnabled) MaterialTheme.colorScheme.primaryContainer // 활성화 색상
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(horizontal = 8.dp), // 안쪽 패딩
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .weight(1f),
//                                    verticalArrangement = Arrangement.spacedBy(4.dp)
//                                ) {
//                                    Text(
//                                        text = "선택 가능한 최소 시간",
//                                        style = Typography.bodyMedium,
//                                        color = if (isMinStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//
//                                    Text(
//                                        text = wiDViewModel.getTimeString(time = minStart),
//                                        style = Typography.bodyMedium,
//                                        color = if (isMinStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                    contentDescription = "최소 시간 사용",
//                                    tint = if (isMinStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
//                                )
//                            }
//
//                            val isMaxStartEnabled = !(startHourPagerState.currentPage == maxStart.hour &&
//                                    startMinutePagerState.currentPage == maxStart.minute &&
//                                    startSecondPagerState.currentPage == maxStart.second)
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(56.dp)
//                                    .padding(horizontal = 16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable(
//                                        enabled = isMaxStartEnabled,
//                                        onClick = {
//                                            coroutineScope.launch {
//                                                launch {
//                                                    startHourPagerState.animateScrollToPage(
//                                                        page = maxStart.hour
//                                                    )
//                                                }
//                                                launch {
//                                                    startMinutePagerState.animateScrollToPage(
//                                                        page = maxStart.minute
//                                                    )
//                                                }
//                                                launch {
//                                                    startSecondPagerState.animateScrollToPage(
//                                                        page = maxStart.second
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    )
//                                    .background(
//                                        color = if (isMaxStartEnabled) MaterialTheme.colorScheme.primaryContainer // 활성화 색상
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(horizontal = 8.dp), // 안쪽 패딩
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .weight(1f),
//                                    verticalArrangement = Arrangement.spacedBy(4.dp)
//                                ) {
//                                    Text(
//                                        text = "선택 가능한 최대 시간",
//                                        style = Typography.bodyMedium,
//                                        color = if (isMaxStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//
//                                    Text(
//                                        text = wiDViewModel.getTimeString(time = maxStart),
//                                        style = Typography.bodyMedium,
//                                        color = if (isMaxStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                    contentDescription = "최대 시간 사용",
//                                    tint = if (isMaxStartEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
//                                )
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp),
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                TextButton(
//                                    onClick = {
//                                        wiDViewModel.setShowStartPicker(show = false)
//
//                                        coroutineScope.launch {
//                                            startHourPagerState.scrollToPage(page = clickedWiDCopy.start.hour)
//                                            startMinutePagerState.scrollToPage(page = clickedWiDCopy.start.minute)
//                                            startSecondPagerState.scrollToPage(page = clickedWiDCopy.start.second)
//                                        }
//                                    }
//                                ) {
//                                    Text(
//                                        text = "취소",
//                                        style = Typography.bodyMedium
//                                    )
//                                }
//
//                                FilledTonalButton(
//                                    onClick = {
//                                        val newStart = LocalTime.of(startHourPagerState.currentPage, startMinutePagerState.currentPage, startSecondPagerState.currentPage)
//
//                                        val newClickedWiDCopy = clickedWiDCopy.copy(
//                                            start = newStart,
//                                            duration = Duration.between(newStart, clickedWiDCopy.finish)
//                                        )
//
//                                        wiDViewModel.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
//                                        wiDViewModel.setShowStartPicker(show = false)
//                                    },
//                                    enabled = !isStartOutOfRange
//                                ) {
//                                    Text(
//                                        text = "확인",
//                                        style = Typography.bodyMedium
//                                    )
//                                }
//                            }
//                        }
//                    }
//                )
//            }
//
//            if (showFinishPicker) {
//                AlertDialog(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.surface,
//                            shape = MaterialTheme.shapes.extraLarge
//                        ),
//                    onDismissRequest = {
//                        wiDViewModel.setShowFinishPicker(show = false)
//
//                        coroutineScope.launch {
//                            finishHourPagerState.scrollToPage(page = clickedWiDCopy.finish.hour)
//                            finishMinutePagerState.scrollToPage(page = clickedWiDCopy.finish.minute)
//                            finishSecondPagerState.scrollToPage(page = clickedWiDCopy.finish.second)
//                        }
//                    },
//                    content = {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .height(56.dp),
//                                text = "종료 시간 선택",
//                                style = Typography.titleLarge,
//                                textAlign = TextAlign.Center
//                            )
//
//                            TimeSelectorView(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .height(screenHeight / 3),
//                                hourPagerState = finishHourPagerState,
//                                minutePagerState = finishMinutePagerState,
//                                secondPagerState = finishSecondPagerState,
//                                coroutineScope = coroutineScope,
//                                onTimeChanged = { // 종료 시간 변경 시 갱신을 멈춤
//                                    if (isLastNewWiD) wiDViewModel.setUpdateClickedWiDCopyToNow(update = false)
//                                }
//                            )
//
//                            val isMinFinishEnabled = !(finishHourPagerState.currentPage == minFinish.hour &&
//                                    finishMinutePagerState.currentPage == minFinish.minute &&
//                                    finishSecondPagerState.currentPage == minFinish.second)
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(56.dp)
//                                    .padding(horizontal = 16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable(
//                                        enabled = isMinFinishEnabled,
//                                        onClick = {
//                                            coroutineScope.launch {
//                                                launch {
//                                                    finishHourPagerState.animateScrollToPage(
//                                                        page = minFinish.hour
//                                                    )
//                                                }
//                                                launch {
//                                                    finishMinutePagerState.animateScrollToPage(
//                                                        page = minFinish.minute
//                                                    )
//                                                }
//                                                launch {
//                                                    finishSecondPagerState.animateScrollToPage(
//                                                        page = minFinish.second
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    )
//                                    .background(
//                                        color = if (isMinFinishEnabled) MaterialTheme.colorScheme.primaryContainer // 활성화 색상
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(horizontal = 8.dp), // 안쪽 패딩
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .weight(1f),
//                                    verticalArrangement = Arrangement.spacedBy(4.dp)
//                                ) {
//                                    Text(
//                                        text = "선택 가능한 최소 시간",
//                                        style = Typography.bodyMedium,
//                                        color = if (isMinFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//
//                                    Text(
//                                        text = wiDViewModel.getTimeString(time = minFinish),
//                                        style = Typography.bodyMedium,
//                                        color = if (isMinFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                    contentDescription = "최소 시간 사용",
//                                    tint = if (isMinFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
//                                )
//                            }
//
//                            val isMaxFinishEnabled = if (isLastNewWiD) {
//                                !updateClickedWiDCopyToNow
//                            } else {
//                                !(finishHourPagerState.currentPage == maxFinish.hour &&
//                                        finishMinutePagerState.currentPage == maxFinish.minute &&
//                                        finishSecondPagerState.currentPage == maxFinish.second)
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(56.dp)
//                                    .padding(horizontal = 16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable(
//                                        enabled = isMaxFinishEnabled,
//                                        onClick = {
//                                            coroutineScope.launch {
//                                                launch {
//                                                    finishHourPagerState.animateScrollToPage(
//                                                        page = maxFinish.hour
//                                                    )
//                                                }
//                                                launch {
//                                                    finishMinutePagerState.animateScrollToPage(
//                                                        page = maxFinish.minute
//                                                    )
//                                                }
//                                                launch {
//                                                    finishSecondPagerState.animateScrollToPage(
//                                                        page = maxFinish.second
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    )
//                                    .background(
//                                        color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.primaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // 비활성화 색상
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(horizontal = 8.dp), // 안쪽 패딩
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .weight(1f),
//                                    verticalArrangement = Arrangement.spacedBy(4.dp)
//                                ) {
//                                    Text(
//                                        text = "선택 가능한 최대 시간",
//                                        style = Typography.bodyMedium,
//                                        color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                    )
//
//                                    Row(
//                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
//                                    ) {
//                                        Text(
//                                            text = wiDViewModel.getTimeString(time = maxFinish),
//                                            style = Typography.bodyMedium,
//                                            color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 텍스트 색상 설정
//                                        )
//
//                                        if (updateClickedWiDCopyToNow) {
//                                            Text(
//                                                modifier = Modifier
//                                                    .background(
//                                                        color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.error
//                                                        else MaterialTheme.colorScheme.errorContainer,
//                                                        shape = MaterialTheme.shapes.medium
//                                                    )
//                                                    .padding(horizontal = 4.dp),
//                                                text = "Now",
//                                                style = Typography.bodyMedium,
//                                                color = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onError
//                                                else MaterialTheme.colorScheme.onErrorContainer,
//                                            )
//                                        }
//                                    }
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                    contentDescription = "최대 시간 사용",
//                                    tint = if (isMaxFinishEnabled) MaterialTheme.colorScheme.onPrimaryContainer
//                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // 아이콘 색상 설정
//                                )
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp),
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                TextButton(
//                                    onClick = {
//                                        wiDViewModel.setShowFinishPicker(show = false)
//
//                                        coroutineScope.launch {
//                                            finishHourPagerState.scrollToPage(page = clickedWiDCopy.finish.hour)
//                                            finishMinutePagerState.scrollToPage(page = clickedWiDCopy.finish.minute)
//                                            finishSecondPagerState.scrollToPage(page = clickedWiDCopy.finish.second)
//                                        }
//                                    }
//                                ) {
//                                    Text(
//                                        text = "취소",
//                                        style = Typography.bodyMedium
//                                    )
//                                }
//
//                                FilledTonalButton(
//                                    onClick = {
//                                        val newFinish = LocalTime.of(finishHourPagerState.currentPage, finishMinutePagerState.currentPage, finishSecondPagerState.currentPage)
//
//                                        val newClickedWiDCopy = clickedWiDCopy.copy(
//                                            finish = newFinish,
//                                            duration = Duration.between(clickedWiDCopy.start, newFinish)
//                                        )
//
//                                        wiDViewModel.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
//                                        wiDViewModel.setUpdateClickedWiDCopyToNow(update = false)
//                                        wiDViewModel.setShowFinishPicker(show = false)
//                                    },
//                                    enabled = !isFinishOutOfRange
//                                ) {
//                                    Text(
//                                        text = "확인",
//                                        style = Typography.bodyMedium
//                                    )
//                                }
//                            }
//                        }
//                    }
//                )
//            }

            if (showDeleteWiDDialog) {
                DatePickerDialog(
                    onDismissRequest = {
                        wiDViewModel.setShowDeleteWiDDialog(false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                wiDViewModel.deleteWiD(
                                    onWiDDeleted = { deleteWiDSuccess: Boolean ->
                                        if (deleteWiDSuccess) {
                                            onBackButtonPressed()
                                        }
                                    }
                                )

                                wiDViewModel.setShowDeleteWiDDialog(false)
                            }
                        ) {
                            Text(text = "삭제")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                wiDViewModel.setShowDeleteWiDDialog(false)
                            }
                        ) {
                            Text(text = "취소")
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = "기록을 삭제하시겠습니까?"
                        )
                    }
                }
            }
        }
    )
}

//@Composable
//@Preview(showBackground = true)
//fun WiDPreview() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .height(intrinsicSize = IntrinsicSize.Min)
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "제목",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "공부",
//                    style = Typography.bodyMedium,
//                )
//            }
//
//            VerticalDivider(
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "수정 전",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "운동",
//                    style = Typography.bodyMedium,
//                )
//            }
//
//            Icon(
//                modifier = Modifier
//                    .size(24.dp),
//                imageVector = Icons.Default.KeyboardArrowDown,
//                contentDescription = "제목 수정",
//            )
//        }
//
//        HorizontalDivider(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .height(intrinsicSize = IntrinsicSize.Min)
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "시작",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "00:00:00",
//                    style = Typography.bodyMedium,
//                )
//            }
//
//            VerticalDivider(
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "수정 전",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "00:00:00",
//                    style = Typography.bodyMedium
//                )
//            }
//
//            Icon(
//                modifier = Modifier
//                    .size(24.dp),
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = "제목 수정",
//            )
//        }
//
//        HorizontalDivider(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .height(intrinsicSize = IntrinsicSize.Min)
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "종료",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "00:00:00",
//                    style = Typography.bodyMedium,
//                )
//            }
//
//            VerticalDivider(
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "수정 전",
//                    style = Typography.bodyMedium,
//                )
//
//
//                Row(
//                    horizontalArrangement = Arrangement
//                        .spacedBy(4.dp)
//                ) {
//                    Text(
//                        text = "00:00:00",
//                        style = Typography.bodyMedium
//                    )
//
//                    Text(
//                        modifier = Modifier
//                            .background(
//                                color = MaterialTheme.colorScheme.error,
//                                shape = RoundedCornerShape(16)
//                            )
//                            .padding(horizontal = 4.dp),
//                        text = "Now",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onError,
//                        textAlign = TextAlign.Right
//                    )
//                }
//            }
//
//            Icon(
//                modifier = Modifier
//                    .size(24.dp),
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = "제목 수정",
//            )
//        }
//
//        HorizontalDivider(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .height(intrinsicSize = IntrinsicSize.Min)
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "시작",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "3시간 30분 30초",
//                    style = Typography.bodyMedium,
//                )
//            }
//
//            VerticalDivider(
//                modifier = Modifier
//                    .padding(vertical = 8.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text(
//                    text = "수정 전",
//                    style = Typography.bodyMedium,
//                )
//
//                Text(
//                    text = "2시간 20분 20초",
//                    style = Typography.bodyMedium
//                )
//            }
//
//            Icon(
//                modifier = Modifier
//                    .size(24.dp)
//                    .alpha(0f),
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = null,
//            )
//        }
//
//        HorizontalDivider(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//        )
//    }
//}