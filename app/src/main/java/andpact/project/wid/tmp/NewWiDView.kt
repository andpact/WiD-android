package andpact.project.wid.tmp

//import andpact.project.wid.R
//import andpact.project.wid.chartView.TimeSelectorView
//import andpact.project.wid.chartView.TitleSelectorView
//import andpact.project.wid.model.Title
//import andpact.project.wid.model.WiD
//import andpact.project.wid.ui.theme.Typography
//import andpact.project.wid.viewModel.NewWiDViewModel
//import android.util.Log
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import kotlinx.coroutines.launch
//import java.time.Duration
//import java.time.LocalTime
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
//@Composable
//fun NewWiDView(
//    onBackButtonPressed: () -> Unit,
//    newWiDViewModel: NewWiDViewModel = hiltViewModel()
//) {
//    val TAG = "NewWiDView"
//
//    // WiD
//    val newWiD = newWiDViewModel.newWiD.value // 변경 전
//    val updatedNewWiD = newWiDViewModel.updatedNewWiD.value // 변경 후
//    val isLastUpdatedNewWiDTimerRunning = newWiDViewModel.isLastUpdatedNewWiDTimerRunning.value
//
//    // 화면
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//    val coroutineScope = rememberCoroutineScope()
//
//    // 제목
//    val showTitleMenu = newWiDViewModel.showTitleMenu.value
//    val titleExist = newWiDViewModel.titleExist.value
//
//    /** 시작 시간을 변경하지 않으면, 기존의 State를 유지하도록 해야 함. */
//    // 시작 시간
//    val showStartPicker = newWiDViewModel.showStartPicker.value
//    val startHourPagerState = rememberPagerState(
//        initialPage = newWiD.start.hour,
//        pageCount = { 24 }
//    )
//    val startMinutePagerState = rememberPagerState(
//        initialPage = newWiD.start.minute,
//        pageCount = { 60 }
//    )
//    val startSecondPagerState = rememberPagerState(
//        initialPage = newWiD.start.second,
//        pageCount = { 60 }
//    )
//    val startOverlap = newWiDViewModel.startOverlap.value
//    val startModified = newWiDViewModel.startModified.value
//
//    // 종료 시간
//    val showFinishPicker = newWiDViewModel.showFinishPicker.value
//    val finishHourPagerState = rememberPagerState(
//        initialPage = newWiD.finish.hour,
//        pageCount = { 24 }
//    )
//    val finishMinutePagerState = rememberPagerState(
//        initialPage = newWiD.finish.minute,
//        pageCount = { 60 }
//    )
//    val finishSecondPagerState = rememberPagerState(
//        initialPage = newWiD.finish.second,
//        pageCount = { 60 }
//    )
//    val finishOverlap = newWiDViewModel.finishOverlap.value
//    val finishModified = newWiDViewModel.finishModified.value
//
//    // 소요 시간
//    val durationExist = newWiDViewModel.durationExist.value
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
//        val newWiDDate = newWiD.date
//        newWiDViewModel.getWiDListOfDate(collectionDate = newWiDDate)
//
//        if (newWiD.id == "lastNewWiD") {
//            newWiDViewModel.startLastNewWiDTimer()
//            newWiDViewModel.startLastUpdatedNewWiDTimer()
//        }
//
//        onDispose {
//            Log.d(TAG, "disposed")
//
//            newWiDViewModel.stopLastNewWiDTimer()
//            newWiDViewModel.stopLastUpdatedNewWiDTimer()
//        }
//    }
//
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize(),
//        containerColor = MaterialTheme.colorScheme.surface,
//        topBar = {
//            CenterAlignedTopAppBar(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                navigationIcon = {
//                    IconButton(
//                        onClick = {
//                            onBackButtonPressed()
//                        }
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                            contentDescription = "뒤로 가기",
//                        )
//                    }
//                },
//                title = {
//                    Text(
//                        text = "새로운 WiD",
//                        style = Typography.titleLarge,
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer
//                )
//            )
//        },
//        content = { contentPadding: PaddingValues -> // Scaffold 내부는 박스로 되어 있음.
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(contentPadding)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(vertical = 16.dp)
//                            .weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Text(
//                            text = "날짜",
//                            style = Typography.titleMedium,
//                        )
//
//                        Text(
//                            text = newWiDViewModel.getDateString(date = updatedNewWiD.date),
//                            style = Typography.bodyMedium,
//                        )
//                    }
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            newWiDViewModel.setShowTitleMenu(show = true)
//                        },
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(vertical = 16.dp)
//                            .weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Text(
//                            text = "제목",
//                            style = Typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Text(
//                            text = updatedNewWiD.title.kr,
//                            style = Typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//
//                    Icon(
//                        modifier = Modifier
//                            .padding(16.dp),
//                        painter = painterResource(R.drawable.baseline_edit_24),
//                        contentDescription = "제목 수정",
//                        tint = MaterialTheme.colorScheme.onSurface
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            newWiDViewModel.setShowStartPicker(show = true)
//                        },
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(vertical = 16.dp)
//                            .weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(4.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "시작",
//                                style = Typography.titleMedium,
//                            )
//
//                            if (startModified && startOverlap) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(12.dp),
//                                    painter = painterResource(R.drawable.baseline_error_outline_24),
//                                    contentDescription = "시작 시간 사용 불가",
//                                    tint = MaterialTheme.colorScheme.error
//                                )
//
//                                Text(
//                                    text = "사용 불가",
//                                    style = Typography.labelSmall,
//                                    color = MaterialTheme.colorScheme.error
//                                )
//                            } else if (startModified) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(12.dp),
//                                    painter = painterResource(R.drawable.baseline_check_circle_outline_24),
//                                    contentDescription = "시작 시간 사용 가능",
//                                )
//
//                                Text(
//                                    text = "사용 가능",
//                                    style = Typography.labelSmall,
//                                )
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .weight(1f)
//                            )
//                        }
//
//                        Text(
//                            text = newWiDViewModel.getTimeString(time = updatedNewWiD.start),
//                            style = Typography.bodyMedium,
//                        )
//                    }
//
//                    Icon(
//                        modifier = Modifier
//                            .padding(16.dp),
//                        painter = painterResource(R.drawable.baseline_edit_24),
//                        contentDescription = "시작 시간 수정",
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            newWiDViewModel.setShowFinishPicker(show = true)
//                        },
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(vertical = 16.dp)
//                            .weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(4.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "종료",
//                                style = Typography.titleMedium,
//                            )
//
//                            if (finishModified && finishOverlap) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(12.dp),
//                                    painter = painterResource(R.drawable.baseline_error_outline_24),
//                                    contentDescription = "종료 시간 사용 불가",
//                                    tint = MaterialTheme.colorScheme.error
//                                )
//
//                                Text(
//                                    text = "사용 불가",
//                                    style = Typography.labelSmall,
//                                    color = MaterialTheme.colorScheme.error
//                                )
//                            } else if (finishModified) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(12.dp),
//                                    painter = painterResource(R.drawable.baseline_check_circle_outline_24),
//                                    contentDescription = "종료 시간 사용 가능",
//                                )
//
//                                Text(
//                                    text = "사용 가능",
//                                    style = Typography.labelSmall,
//                                )
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .weight(1f)
//                            )
//                        }
//
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            Text(
//                                text = newWiDViewModel.getTimeString(time = updatedNewWiD.finish),
//                                style = Typography.bodyMedium,
//                            )
//
//                            if (isLastUpdatedNewWiDTimerRunning) {
//                                Text(
//                                    modifier = Modifier
//                                        .background(
//                                            color = MaterialTheme.colorScheme.errorContainer,
//                                            shape = MaterialTheme.shapes.medium
//                                        ),
//                                    text = "LIVE",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.onErrorContainer
//                                )
//                            }
//                        }
//                    }
//
//                    Icon(
//                        modifier = Modifier
//                            .padding(16.dp),
//                        painter = painterResource(R.drawable.baseline_edit_24),
//                        contentDescription = "종료 시간 수정",
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(vertical = 16.dp)
//                            .weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(4.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "소요",
//                                style = Typography.titleMedium,
//                            )
//
//                            if (startModified && finishModified && !durationExist) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(12.dp),
//                                    painter = painterResource(R.drawable.baseline_error_outline_24),
//                                    contentDescription = "소요 시간 부족",
//                                    tint = MaterialTheme.colorScheme.error
//                                )
//
//                                Text(
//                                    text = "소요 시간 부족",
//                                    style = Typography.labelSmall,
//                                    color = MaterialTheme.colorScheme.error
//                                )
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .weight(1f)
//                            )
//                        }
//
//                        Text(
//                            text = newWiDViewModel.getDurationString(updatedNewWiD.duration),
//                            style = Typography.bodyMedium,
//                        )
//                    }
//                }
//
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                )
//
//                FilledIconButton(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    onClick = {
//                        newWiDViewModel.createWiD() { createWiDSuccess: Boolean ->
//                            if (createWiDSuccess) {
//                                onBackButtonPressed()
//                            }
//                        }
//                    },
//                    enabled = titleExist && !startOverlap && !finishOverlap && durationExist,
//                    shape = RectangleShape
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(
//                            space = 8.dp,
//                            alignment = Alignment.CenterHorizontally
//                        ),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.outline_add_box_24),
//                            contentDescription = "새로운 WiD 만들기",
//                        )
//
//                        Text(
//                            text = "새로운 WiD 만들기",
//                            style = Typography.bodyMedium
//                        )
//                    }
//                }
//            }
//
//            if (showTitleMenu) {
//                AlertDialog(
//                    modifier = Modifier
//                        .height(height = screenHeight / 2)
//                        .background(
//                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
//                            shape = MaterialTheme.shapes.extraLarge
//                        ),
//                    onDismissRequest = {
//                        newWiDViewModel.setShowTitleMenu(show = false)
//                    },
//                ) {
//                    TitleSelectorView(
//                        currentTitle = updatedNewWiD.title,
//                        onTitleSelected = { title: Title ->
//                            val newUpdatedNewWiD = updatedNewWiD.copy(title = title)
//                            newWiDViewModel.setTitleExist(exist = true)
//                            newWiDViewModel.setUpdateNewWiD(updatedNewWiD = newUpdatedNewWiD)
//                            newWiDViewModel.setShowTitleMenu(show = false)
//                        }
//                    )
//                }
//            }
//
//            if (showStartPicker) {
//                AlertDialog(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.surface,
//                            shape = MaterialTheme.shapes.extraLarge
//                        ),
//                    onDismissRequest = {
//                        newWiDViewModel.setShowStartPicker(show = false)
//
//                        coroutineScope.launch {
//                            startHourPagerState.scrollToPage(page = updatedNewWiD.start.hour)
//                            startMinutePagerState.scrollToPage(page = updatedNewWiD.start.minute)
//                            startSecondPagerState.scrollToPage(page = updatedNewWiD.start.second)
//                        }
//                    },
//                    content = {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .padding(16.dp),
//                                text = "시작 시간 선택",
//                                style = Typography.titleLarge
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
//                            Row(
//                                modifier = Modifier
//                                    .padding(16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable {
//                                        /** 수정할 일 없도록. */
//                                        coroutineScope.launch {
//                                            launch { startHourPagerState.animateScrollToPage(page = newWiD.start.hour) }
//                                            launch { startMinutePagerState.animateScrollToPage(page = newWiD.start.minute) }
//                                            launch { startSecondPagerState.animateScrollToPage(page = newWiD.start.second) }
//                                        }
//                                    }
//                                    .background(
//                                        color = MaterialTheme.colorScheme.primaryContainer,
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(16.dp), // 안쪽 패딩
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
//                                    )
//
//                                    Text(
//                                        text = newWiDViewModel.getTimeString(time = newWiD.start),
//                                        style = Typography.bodyMedium,
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                    contentDescription = "최소 시간 사용",
//                                )
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .padding(16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable {
//                                        coroutineScope.launch {
//                                            launch { startHourPagerState.animateScrollToPage(page = updatedNewWiD.finish.hour) }
//                                            launch { startMinutePagerState.animateScrollToPage(page = updatedNewWiD.finish.minute) }
//                                            launch { startSecondPagerState.animateScrollToPage(page = updatedNewWiD.finish.second) }
//                                        }
//                                    }
//                                    .background(
//                                        color = MaterialTheme.colorScheme.primaryContainer,
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(16.dp), // 안쪽 패딩
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
//                                    )
//
//                                    Text(
//                                        text = newWiDViewModel.getTimeString(time = updatedNewWiD.finish),
//                                        style = Typography.bodyMedium,
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24), /** 아이콘 변경 */
//                                    contentDescription = "최대 시간 사용",
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
//                                FilledTonalButton(
//                                    onClick = {
//                                        newWiDViewModel.setShowStartPicker(show = false)
//
//                                        coroutineScope.launch {
//                                            startHourPagerState.scrollToPage(page = updatedNewWiD.start.hour)
//                                            startMinutePagerState.scrollToPage(page = updatedNewWiD.start.minute)
//                                            startSecondPagerState.scrollToPage(page = updatedNewWiD.start.second)
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
//                                        val newStart = LocalTime.of(
//                                            startHourPagerState.currentPage,
//                                            startMinutePagerState.currentPage,
//                                            startSecondPagerState.currentPage
//                                        )
//
//                                        val updatedNewWiD = WiD(
//                                            id = updatedNewWiD.id,
//                                            date = updatedNewWiD.date,
//                                            title = updatedNewWiD.title,
//                                            start = newStart,
//                                            finish = updatedNewWiD.finish,
//                                            duration = Duration.between(newStart, updatedNewWiD.finish),
//                                            createdBy = updatedNewWiD.createdBy
//                                        )
//
//                                        newWiDViewModel.setUpdateNewWiD(updatedNewWiD = updatedNewWiD)
//                                        newWiDViewModel.setStartModified(modified = true)
//                                        newWiDViewModel.setShowStartPicker(show = false)
//                                    }
//                                ) {
//                                    Text(
//                                        text = "확인",
//                                        style = Typography.bodyMedium
//                                    )
//                                }
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .height(16.dp)
//                            )
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
//                        newWiDViewModel.setShowFinishPicker(show = false)
//
//                        coroutineScope.launch {
//                            finishHourPagerState.scrollToPage(page = updatedNewWiD.finish.hour)
//                            finishMinutePagerState.scrollToPage(page = updatedNewWiD.finish.minute)
//                            finishSecondPagerState.scrollToPage(page = updatedNewWiD.finish.second)
//                        }
//                    },
//                    content = {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .padding(16.dp),
//                                text = "종료 시간 선택",
//                                style = Typography.titleLarge
//                            )
//
//                            TimeSelectorView(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .height(screenHeight / 3),
//                                hourPagerState = finishHourPagerState,
//                                minutePagerState = finishMinutePagerState,
//                                secondPagerState = finishSecondPagerState,
//                                coroutineScope = coroutineScope
//                            )
//
//                            Row(
//                                modifier = Modifier
//                                    .padding(16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable {
//                                        /** 수정할 일 없도록. */
//                                        coroutineScope.launch {
//                                            launch { finishHourPagerState.animateScrollToPage(page = updatedNewWiD.start.hour) }
//                                            launch { finishMinutePagerState.animateScrollToPage(page = updatedNewWiD.start.minute) }
//                                            launch { finishSecondPagerState.animateScrollToPage(page = updatedNewWiD.start.second) }
//                                        }
//                                    }
//                                    .background(
//                                        color = MaterialTheme.colorScheme.primaryContainer,
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(16.dp), // 안쪽 패딩
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
//                                    )
//
//                                    Text(
//                                        text = newWiDViewModel.getTimeString(time = updatedNewWiD.start),
//                                        style = Typography.bodyMedium,
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                    contentDescription = "최소 시간 사용",
//                                )
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .padding(16.dp) // 바깥 패딩
//                                    .clip(shape = MaterialTheme.shapes.medium)
//                                    .clickable {
//                                        coroutineScope.launch {
//                                            launch { finishHourPagerState.animateScrollToPage(page = newWiD.finish.hour) }
//                                            launch { finishMinutePagerState.animateScrollToPage(page = newWiD.finish.minute) }
//                                            launch { finishSecondPagerState.animateScrollToPage(page = newWiD.finish.second) }
//                                        }
//                                    }
//                                    .background(
//                                        color = MaterialTheme.colorScheme.primaryContainer,
//                                        shape = MaterialTheme.shapes.medium
//                                    )
//                                    .padding(16.dp), // 안쪽 패딩
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
//                                    )
//
//                                    Text(
//                                        text = newWiDViewModel.getTimeString(time = newWiD.finish),
//                                        style = Typography.bodyMedium,
//                                    )
//                                }
//
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24), /** 아이콘 변경 */
//                                    contentDescription = "최대 시간 사용",
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
//                                FilledTonalButton(
//                                    onClick = {
//                                        newWiDViewModel.setShowFinishPicker(show = false)
//
//                                        coroutineScope.launch {
//                                            finishHourPagerState.scrollToPage(page = updatedNewWiD.finish.hour)
//                                            finishMinutePagerState.scrollToPage(page = updatedNewWiD.finish.minute)
//                                            finishSecondPagerState.scrollToPage(page = updatedNewWiD.finish.second)
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
//                                        val newFinish = LocalTime.of(
//                                            finishHourPagerState.currentPage,
//                                            finishMinutePagerState.currentPage,
//                                            finishSecondPagerState.currentPage
//                                        )
//
//                                        val updatedNewWiD = WiD(
//                                            id = updatedNewWiD.id,
//                                            date = updatedNewWiD.date,
//                                            title = updatedNewWiD.title,
//                                            start = updatedNewWiD.start,
//                                            finish = newFinish,
//                                            duration = Duration.between(updatedNewWiD.start, newFinish),
//                                            createdBy = updatedNewWiD.createdBy
//                                        )
//
//                                        newWiDViewModel.setUpdateNewWiD(updatedNewWiD = updatedNewWiD)
//                                        newWiDViewModel.setFinishModified(modified = true)
//                                        newWiDViewModel.setShowFinishPicker(show = false)
//                                    }
//                                ) {
//                                    Text(
//                                        text = "확인",
//                                        style = Typography.bodyMedium
//                                    )
//                                }
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .height(16.dp)
//                            )
//                        }
//                    }
//                )
//            }
//        }
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
//@Composable
//fun NewWiDPreview() {
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize(),
//        containerColor = MaterialTheme.colorScheme.surface,
//        topBar = {
//            CenterAlignedTopAppBar(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                navigationIcon = {
//                    IconButton(
//                        onClick = {
//                        }
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                            contentDescription = "뒤로 가기",
//                        )
//                    }
//                },
//                title = {
//                    Text(
//                        text = "새로운 기록",
//                        style = Typography.titleLarge,
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer
//                )
//            )
//        },
//        content = { contentPadding: PaddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(contentPadding)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(
//                        space = 4.dp,
//                        alignment = Alignment.CenterVertically
//                    )
//                ) {
//                    Text(
//                        text = "날짜",
//                        style = Typography.bodyMedium,
//                    )
//
//                    Text(
//                        text = "2024년 10월 10일(수)",
//                        style = Typography.labelMedium,
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(
//                        space = 8.dp,
//                        alignment = Alignment.CenterVertically
//                    )
//                ) {
//                    Text(
//                        text = "제목",
//                        style = Typography.bodyMedium,
//                    )
//
//                    Text(
//                        text = "공부",
//                        style = Typography.labelMedium,
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(
//                        space = 8.dp,
//                        alignment = Alignment.CenterVertically
//                    )
//                ) {
//                    Text(
//                        text = "시작",
//                        style = Typography.bodyMedium,
//                    )
//
//                    Text(
//                        text = "00시 00분 00초",
//                        style = Typography.labelMedium,
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(
//                        space = 8.dp,
//                        alignment = Alignment.CenterVertically
//                    )
//                ) {
//                    Text(
//                        text = "종료",
//                        style = Typography.bodyMedium,
//                    )
//
//                    Text(
//                        text = "00시 00분 00초",
//                        style = Typography.labelMedium,
//                    )
//                }
//
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .padding(horizontal = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(
//                        space = 8.dp,
//                        alignment = Alignment.CenterVertically
//                    )
//                ) {
//                    Text(
//                        text = "소요",
//                        style = Typography.bodyMedium,
//                    )
//
//                    Text(
//                        text = "1시간 10분 10초",
//                        style = Typography.labelMedium,
//                    )
//                }
//            }
//        },
//        bottomBar = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically,
//                content = {
//                    IconButton(
//                        onClick = {
//
//                        },
//                        content = {
//                            Icon(
//                                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                contentDescription = "초기화"
//                            )
//                        }
//                    )
//
//                    VerticalDivider(
//                        modifier = Modifier
//                            .height(intrinsicSize = IntrinsicSize.Min)
//                    )
//
//                    TextButton(
//                        onClick = {
//
//                        },
//                        content = {
//                            Icon(
//                                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
//                                contentDescription = "초기화",
//                            )
//
//                            Text(
//                                text = "생성 완료",
//                                style = Typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                        }
//                    )
//                }
//            )
//        }
//    )
//}