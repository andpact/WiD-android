package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.TimeSelectorView
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.WiDViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WiDView(
    onBackButtonPressed: () -> Unit,
    wiDViewModel: WiDViewModel = hiltViewModel()
) {
    val TAG = "WiDView"

    // WiD
    val wiD = wiDViewModel.wiD.value

    // Updated WiD
    val updatedWiD = wiDViewModel.updatedWiD.value

    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val coroutineScope = rememberCoroutineScope()

    // 제목
    val showTitleMenu = wiDViewModel.showTitleMenu.value

    // 시작 시간
    val showStartPicker = wiDViewModel.showStartPicker.value
    val startHourPagerState = rememberPagerState(
        initialPage = wiD.start.hour,
        pageCount = { 24 }
    )
    val startMinutePagerState = rememberPagerState(
        initialPage = wiD.start.minute,
        pageCount = { 60 }
    )
    val startSecondPagerState = rememberPagerState(
        initialPage = wiD.start.second,
        pageCount = { 60 }
    )
//    val startTimePickerState = rememberTimePickerState(
//        initialHour = updatedWiD.start.hour,
//        initialMinute = updatedWiD.start.minute,
//        is24Hour = false
//    )
    val startOverlap = wiDViewModel.startOverlap.value
    val startModified = wiDViewModel.startModified.value

    // 종료 시간
    val showFinishPicker = wiDViewModel.showFinishPicker.value
    val finishHourPagerState = rememberPagerState(
        initialPage = wiD.finish.hour,
        pageCount = { 24 }
    )
    val finishMinutePagerState = rememberPagerState(
        initialPage = wiD.finish.minute,
        pageCount = { 60 }
    )
    val finishSecondPagerState = rememberPagerState(
        initialPage = wiD.finish.second,
        pageCount = { 60 }
    )
    val finishTimePickerState = rememberTimePickerState(
        initialHour = updatedWiD.finish.hour,
        initialMinute = updatedWiD.finish.minute,
        is24Hour = false
    )
    val finishOverlap = wiDViewModel.finishOverlap.value
    val finishModified = wiDViewModel.finishModified.value

    // 소요 시간
    val durationExist = wiDViewModel.durationExist.value

    val showDeleteWiDDialog = wiDViewModel.showDeleteWiDDialog.value
    val expandMenu = wiDViewModel.expandMenu.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        val clickedWiDDate = updatedWiD.date
        wiDViewModel.getWiDListByDate(currentDate = clickedWiDDate)

        onDispose { Log.d(TAG, "disposed") }
    }

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
                            onBackButtonPressed()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "뒤로 가기",
                        )
                    }
                },
                title = {
                    Text(
                        text = "WiD",
                        style = Typography.titleLarge,
                    )
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = {
                                wiDViewModel.setExpandMenu(expand = true)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_more_vert_24),
                                contentDescription = "더보기",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        DropdownMenu(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface),
                            expanded = expandMenu,
                            onDismissRequest = {
                                wiDViewModel.setExpandMenu(expand = false)
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "WiD 삭제",
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    wiDViewModel.setExpandMenu(expand = false)
                                    wiDViewModel.setShowDeleteWiDDialog(show = true)
                                },
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier
                                            .size(24.dp),
                                        painter = painterResource(id = R.drawable.outline_delete_16),
                                        contentDescription = "WiD 삭제",
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "날짜",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = getDateString(date = updatedWiD.date),
                            style = Typography.bodyMedium,
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            wiDViewModel.setShowTitleMenu(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "제목",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = updatedWiD.title.kr,
                            style = Typography.bodyMedium,
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(R.drawable.baseline_edit_24),
                        contentDescription = "제목 수정",
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            wiDViewModel.setShowStartPicker(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "시작",
                                style = Typography.titleMedium,
                            )

                            if (startModified && startOverlap) {
                                Icon(
                                    modifier = Modifier
                                        .size(12.dp),
                                    painter = painterResource(R.drawable.baseline_error_outline_24),
                                    contentDescription = "시작 시간 사용 불가",
                                    tint = MaterialTheme.colorScheme.error
                                )

                                Text(
                                    text = "사용 불가",
                                    style = Typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (startModified) {
                                Icon(
                                    modifier = Modifier
                                        .size(12.dp),
                                    painter = painterResource(R.drawable.baseline_check_circle_outline_24),
                                    contentDescription = "시작 시간 사용 가능",
                                )

                                Text(
                                    text = "사용 가능",
                                    style = Typography.labelSmall,
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }

                        Text(
                            text = getTimeString(time = updatedWiD.start),
                            style = Typography.bodyMedium,
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(R.drawable.baseline_edit_24),
                        contentDescription = "시작 시간 수정",
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            wiDViewModel.setShowFinishPicker(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "종료",
                                style = Typography.titleMedium,
                            )

                            if (finishModified && finishOverlap) {
                                Icon(
                                    modifier = Modifier
                                        .size(12.dp),
                                    painter = painterResource(R.drawable.baseline_error_outline_24),
                                    contentDescription = "종료 시간 사용 불가",
                                    tint = MaterialTheme.colorScheme.error
                                )

                                Text(
                                    text = "사용 불가",
                                    style = Typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (finishModified) {
                                Icon(
                                    modifier = Modifier
                                        .size(12.dp),
                                    painter = painterResource(R.drawable.baseline_check_circle_outline_24),
                                    contentDescription = "종료 시간 사용 가능",
                                )

                                Text(
                                    text = "사용 가능",
                                    style = Typography.labelSmall,
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }

                        Text(
                            text = getTimeString(time = updatedWiD.finish),
                            style = Typography.bodyMedium,
                        )

                        /** LIVE 표시 */
                    }

                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(R.drawable.baseline_edit_24),
                        contentDescription = "종료 시간 수정",
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "소요",
                                style = Typography.titleMedium,
                            )

                            if (!durationExist) {
                                Icon(
                                    modifier = Modifier
                                        .size(12.dp),
                                    painter = painterResource(R.drawable.baseline_error_outline_24),
                                    contentDescription = "소요 시간 부족",
                                    tint = MaterialTheme.colorScheme.error
                                )

                                Text(
                                    text = "소요 시간 부족",
                                    style = Typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }

                        Text(
                            text = getDurationString(updatedWiD.duration),
                            style = Typography.bodyMedium,
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "도구",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = updatedWiD.createdBy.kr,
                            style = Typography.bodyMedium,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "변경 사항",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = "WiD -> UpdatedWiD 변경 사항 표시(ui 짜기) or 수정 사항이 없습니다.",
                            style = Typography.bodyMedium,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "경험치",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = "${wiD.duration.seconds}",
                            style = Typography.bodyMedium,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                FilledIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        wiDViewModel.updateWiD(
                            onWiDUpdated = { updateWiDSuccess: Boolean ->
                                if (updateWiDSuccess) {
                                    onBackButtonPressed()
                                }
                            }
                        )
                    },
                    enabled = !startOverlap && !finishOverlap && durationExist,
                    shape = RectangleShape
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_done_24),
                            contentDescription = "수정 완료",
                        )

                        Text(
                            text = "수정 완료",
                            style = Typography.bodyMedium
                        )
                    }
                }
            }

            if (showTitleMenu) {
                AlertDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    onDismissRequest = {
                        wiDViewModel.setShowTitleMenu(show = false)
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = "제목 선택",
                            style = Typography.titleLarge
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight / 2)
                        ) {
                            // Title enum을 사용하여 목록 생성
                            items(Title.values().drop(1).size) { index -> // drop(1) 사용하여 첫 번째 요소 제외
                                val itemTitle = Title.values().drop(1)[index] // Title enum에서 첫 번째 값 제외

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // WiD 업데이트
                                            val updatedWiD = WiD(
                                                id = updatedWiD.id,
                                                date = updatedWiD.date,
                                                title = itemTitle,
                                                start = updatedWiD.start,
                                                finish = updatedWiD.finish,
                                                duration = Duration.between(
                                                    updatedWiD.start,
                                                    updatedWiD.finish
                                                ),
                                                createdBy = updatedWiD.createdBy
                                            )

                                            wiDViewModel.setUpdatedWiD(updatedWiD = updatedWiD)
                                            wiDViewModel.setShowTitleMenu(show = false)
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .width(8.dp)
                                    )

                                    Image(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(MaterialTheme.shapes.medium),
                                        painter = painterResource(id = itemTitle.smallImage),
                                        contentDescription = "앱 아이콘"
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .width(8.dp)
                                    )

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .padding(top = 8.dp, bottom = 4.dp),
                                            text = itemTitle.kr,
                                            style = Typography.bodyMedium
                                        )

                                        Text(
                                            modifier = Modifier
                                                .padding(top = 4.dp, bottom = 8.dp),
                                            text = itemTitle.description,
                                            style = Typography.labelMedium,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    }

                                    RadioButton(
                                        selected = updatedWiD.title == itemTitle,
                                        onClick = {
                                            // WiD 업데이트
                                            val updatedWiD = WiD(
                                                id = updatedWiD.id,
                                                date = updatedWiD.date,
                                                title = itemTitle,
                                                start = updatedWiD.start,
                                                finish = updatedWiD.finish,
                                                duration = Duration.between(updatedWiD.start, updatedWiD.finish),
                                                createdBy = updatedWiD.createdBy
                                            )

                                            wiDViewModel.setUpdatedWiD(updatedWiD = updatedWiD)
                                            wiDViewModel.setShowTitleMenu(show = false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showStartPicker) {
                AlertDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    onDismissRequest = {
                        wiDViewModel.setShowStartPicker(show = false)

                        coroutineScope.launch {
                            startHourPagerState.scrollToPage(page = updatedWiD.start.hour)
                            startMinutePagerState.scrollToPage(page = updatedWiD.start.minute)
                            startSecondPagerState.scrollToPage(page = updatedWiD.start.second)
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = "시작 시간 선택",
                                style = Typography.titleLarge
                            )

                            TimeSelectorView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .height(screenHeight / 3),
                                hourPagerState = startHourPagerState,
                                minutePagerState = startMinutePagerState,
                                secondPagerState = startSecondPagerState,
                                coroutineScope = coroutineScope
                            )

                            Row(
                                modifier = Modifier
                                    .padding(16.dp) // 바깥 패딩
                                    .clip(shape = MaterialTheme.shapes.medium)
                                    .clickable {
                                        /** 최소 시간 구해야함. */
                                        coroutineScope.launch {
//                                            launch { startHourPagerState.animateScrollToPage(page = newWiD.start.hour) }
//                                            launch { startMinutePagerState.animateScrollToPage(page = newWiD.start.minute) }
//                                            launch { startSecondPagerState.animateScrollToPage(page = newWiD.start.second) }
                                        }
                                    }
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp), // 안쪽 패딩
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "선택 가능한 최소 시간",
                                        style = Typography.bodyMedium,
                                    )

                                    /** 최소 시간 구해야함. */
//                                    Text(
//                                        text = getTimeString(
//                                            time = newWiD.start,
//                                            patten = "a hh:mm:ss"
//                                        ),
//                                        style = Typography.bodyMedium,
//                                    )
                                }

                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                                    contentDescription = "최소 시간 사용",
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .padding(16.dp) // 바깥 패딩
                                    .clip(shape = MaterialTheme.shapes.medium)
                                    .clickable {
                                        /** 최대 시간 구해야함. */
                                        coroutineScope.launch {
//                                            launch { startHourPagerState.animateScrollToPage(page = updatedNewWiD.finish.hour) }
//                                            launch { startMinutePagerState.animateScrollToPage(page = updatedNewWiD.finish.minute) }
//                                            launch { startSecondPagerState.animateScrollToPage(page = updatedNewWiD.finish.second) }
                                        }
                                    }
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp), // 안쪽 패딩
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "선택 가능한 최대 시간",
                                        style = Typography.bodyMedium,
                                    )

                                    /** 최대 시간 구해야함. */
//                                    Text(
//                                        text = getTimeString(
//                                            time = updatedWiD.finish,
//                                            patten = "a hh:mm:ss"
//                                        ),
//                                        style = Typography.bodyMedium,
//                                    )
                                }

                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24), /** 아이콘 변경 */
                                    contentDescription = "최대 시간 사용",
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                FilledTonalButton(
                                    onClick = {
                                        wiDViewModel.setShowStartPicker(show = false)

                                        coroutineScope.launch {
                                            startHourPagerState.scrollToPage(page = updatedWiD.start.hour)
                                            startMinutePagerState.scrollToPage(page = updatedWiD.start.minute)
                                            startSecondPagerState.scrollToPage(page = updatedWiD.start.second)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "취소",
                                        style = Typography.bodyMedium
                                    )
                                }

                                FilledTonalButton(
                                    onClick = {
                                        val newStart = LocalTime.of(
                                            startHourPagerState.currentPage,
                                            startMinutePagerState.currentPage,
                                            startSecondPagerState.currentPage
                                        )

                                        val newUpdatedWiD = WiD(
                                            id = updatedWiD.id,
                                            date = updatedWiD.date,
                                            title = updatedWiD.title,
                                            start = newStart,
                                            finish = updatedWiD.finish,
                                            duration = Duration.between(newStart, updatedWiD.finish),
                                            createdBy = updatedWiD.createdBy
                                        )

                                        wiDViewModel.setUpdatedWiD(updatedWiD = newUpdatedWiD)
                                        wiDViewModel.setStartModified(modified = true)
                                        wiDViewModel.setShowStartPicker(show = false)
                                    }
                                ) {
                                    Text(
                                        text = "확인",
                                        style = Typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier
                                    .height(16.dp)
                            )
                        }
                    }
                )
            }

            if (showFinishPicker) {
                AlertDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    onDismissRequest = {
                        wiDViewModel.setShowFinishPicker(show = false)

                        coroutineScope.launch {
                            finishHourPagerState.scrollToPage(page = updatedWiD.finish.hour)
                            finishMinutePagerState.scrollToPage(page = updatedWiD.finish.minute)
                            finishSecondPagerState.scrollToPage(page = updatedWiD.finish.second)
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = "종료 시간 선택",
                                style = Typography.titleLarge
                            )

                            TimeSelectorView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .height(screenHeight / 3),
                                hourPagerState = finishHourPagerState,
                                minutePagerState = finishMinutePagerState,
                                secondPagerState = finishSecondPagerState,
                                coroutineScope = coroutineScope
                            )

                            Row(
                                modifier = Modifier
                                    .padding(16.dp) // 바깥 패딩
                                    .clip(shape = MaterialTheme.shapes.medium)
                                    .clickable {
                                        /** 최소 시간 특정해야함. */
                                        coroutineScope.launch {
//                                            launch { finishHourPagerState.animateScrollToPage(page = updatedNewWiD.start.hour) }
//                                            launch { finishMinutePagerState.animateScrollToPage(page = updatedNewWiD.start.minute) }
//                                            launch { finishSecondPagerState.animateScrollToPage(page = updatedNewWiD.start.second) }
                                        }
                                    }
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp), // 안쪽 패딩
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "선택 가능한 최소 시간",
                                        style = Typography.bodyMedium,
                                    )

                                    /** 최소 시간 특정해야함. */
//                                    Text(
//                                        text = getTimeString(
//                                            time = updatedNewWiD.start,
//                                            patten = "a hh:mm:ss"
//                                        ),
//                                        style = Typography.bodyMedium,
//                                    )
                                }

                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                                    contentDescription = "최소 시간 사용",
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .padding(16.dp) // 바깥 패딩
                                    .clip(shape = MaterialTheme.shapes.medium)
                                    .clickable {
                                        /** 최대 시간 특정해야함. */
                                        coroutineScope.launch {
//                                            launch { finishHourPagerState.animateScrollToPage(page = newWiD.finish.hour) }
//                                            launch { finishMinutePagerState.animateScrollToPage(page = newWiD.finish.minute) }
//                                            launch { finishSecondPagerState.animateScrollToPage(page = newWiD.finish.second) }
                                        }
                                    }
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp), // 안쪽 패딩
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "선택 가능한 최대 시간",
                                        style = Typography.bodyMedium,
                                    )

                                    /** 최소 시간 특정해야함. */
//                                    Text(
//                                        text = getTimeString(
//                                            time = newWiD.finish,
//                                            patten = "a hh:mm:ss"
//                                        ),
//                                        style = Typography.bodyMedium,
//                                    )
                                }

                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24), /** 아이콘 변경 */
                                    contentDescription = "최대 시간 사용",
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                FilledTonalButton(
                                    onClick = {
                                        wiDViewModel.setShowFinishPicker(show = false)

                                        coroutineScope.launch {
                                            finishHourPagerState.scrollToPage(page = updatedWiD.finish.hour)
                                            finishMinutePagerState.scrollToPage(page = updatedWiD.finish.minute)
                                            finishSecondPagerState.scrollToPage(page = updatedWiD.finish.second)
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "취소",
                                        style = Typography.bodyMedium
                                    )
                                }

                                FilledTonalButton(
                                    onClick = {
                                        val newFinish = LocalTime.of(
                                            finishHourPagerState.currentPage,
                                            finishMinutePagerState.currentPage,
                                            finishSecondPagerState.currentPage
                                        )

                                        val newUpdatedWiD = WiD(
                                            id = updatedWiD.id,
                                            date = updatedWiD.date,
                                            title = updatedWiD.title,
                                            start = updatedWiD.start,
                                            finish = newFinish,
                                            duration = Duration.between(updatedWiD.start, newFinish),
                                            createdBy = updatedWiD.createdBy
                                        )

                                        wiDViewModel.setUpdatedWiD(updatedWiD = newUpdatedWiD)
                                        wiDViewModel.setFinishModified(modified = true)
                                        wiDViewModel.setShowFinishPicker(show = false)
                                    }
                                ) {
                                    Text(
                                        text = "확인",
                                        style = Typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier
                                    .height(16.dp)
                            )
                        }
                    }
                )
            }

            if (showDeleteWiDDialog) {
                DatePickerDialog(
                    onDismissRequest = {
                        wiDViewModel.setShowDeleteWiDDialog(false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                wiDViewModel.deleteWiD() { deleteWiDSuccess: Boolean ->
                                    if (deleteWiDSuccess) {
                                        onBackButtonPressed()
                                    }
                                }

                                wiDViewModel.setShowDeleteWiDDialog(false)
                            }
                        ) {
                            Text(
                                text = "삭제",
                                style = Typography.bodyMedium
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                wiDViewModel.setShowDeleteWiDDialog(false)
                            }
                        ) {
                            Text(
                                text = "취소",
                                style = Typography.bodyMedium
                            )
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
                            text = "WiD를 삭제하시겠습니까?",
                            style = Typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}