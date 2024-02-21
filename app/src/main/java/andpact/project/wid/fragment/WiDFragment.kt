package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDFragment(wiDId: Long, navController: NavController) {
    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val clickedWiD = wiDService.readWiDById(wiDId)

    if (clickedWiD == null) {
        Text(
            modifier = Modifier
                .fillMaxSize(),
            text = "WiD not found",
            textAlign = TextAlign.Center
        )

        return
    }

    // 화면
    val lazyColumnState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 날짜
    val today: LocalDate = LocalDate.now()
    val date by remember { mutableStateOf(clickedWiD.date) }
    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    // WiD
    val wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
    val clickedWiDIndex = wiDList.indexOf(clickedWiD)
    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(clickedWiD.title) }

    // 시작 시간
    var start by remember { mutableStateOf(clickedWiD.start) }
    val startLimit = if (0 < clickedWiDIndex) { wiDList[clickedWiDIndex - 1].finish } else { LocalTime.MIDNIGHT }
    var expandStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    val isStartOverlap by remember(start) { mutableStateOf(start < startLimit)}
    val isStartOverCurrentTime by remember(start) { mutableStateOf(date == today && currentTime < start) }

    // 종료 시간
    var finish by remember { mutableStateOf(clickedWiD.finish) }
    val finishLimit = if (clickedWiDIndex < wiDList.size - 1) { wiDList[clickedWiDIndex + 1].start } else if (date == today) { currentTime } else { LocalTime.MIDNIGHT.minusSeconds(1) }
    var expandFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    val isFinishOverlap by remember(finish) { mutableStateOf(finishLimit < finish) }
    val isFinishOverCurrentTime by remember(finish) { mutableStateOf(date == today && currentTime < finish) }

    // 소요 시간
    val duration by remember(start, finish) { mutableStateOf(Duration.between(start, finish)) }
    val durationExist by remember(duration) { mutableStateOf(Duration.ZERO < duration) }

    // WiD
    val isTimeOverlap = isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime

    LaunchedEffect(isDeleteButtonPressed) {
        if (isDeleteButtonPressed) {
            delay(3000L)
            isDeleteButtonPressed = false
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "WiD",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(!isTimeOverlap && durationExist) {
                        wiDService.updateWiD(
                            id = wiDId,
                            date = date,
                            title = title,
                            start = start,
                            finish = finish,
                            duration = duration
                        )

                        navController.popBackStack()
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (!isTimeOverlap && durationExist) {
                            LimeGreen
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        }
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                text = "완료",
                style = Typography.bodyMedium,
                color = White
            )
        }

//        HorizontalDivider()

        /**
         * 컨텐츠
         */
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
//                .shadow(
//                    elevation = 2.dp,
//                    shape = RoundedCornerShape(8.dp),
//                    spotColor = MaterialTheme.colorScheme.primary,
//                )
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(color = MaterialTheme.colorScheme.secondary)
        ) {
            // 날짜
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                    contentDescription = "날짜",
                    tint = MaterialTheme.colorScheme.primary
                )

                VerticalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "날짜",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getDateString(date),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )

            // 제목
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clickable {
                        titleMenuExpanded = !titleMenuExpanded
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(titleIconMap[title] ?: R.drawable.baseline_menu_book_16),
                    contentDescription = "제목",
                    tint = MaterialTheme.colorScheme.primary
                )

                VerticalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "제목",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = titleMap[title] ?: title,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp),
                    imageVector = if (titleMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "제목 메뉴 펼치기",
                )
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )

            // 시작 시간
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clickable {
                        expandStartPicker = !expandStartPicker
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_alarm_24),
                    contentDescription = "시작 시간",
                    tint = MaterialTheme.colorScheme.primary
                )

                VerticalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "시작",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getTimeString(start, "a hh:mm:ss"),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp),
                    imageVector = if (expandStartPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "시작 시간 선택 도구 펼치기",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )

            // 종료 시간
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clickable {
                        expandFinishPicker = !expandFinishPicker
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                    contentDescription = "종료 시간",
                    tint = MaterialTheme.colorScheme.primary
                )

                VerticalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "종료",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getTimeString(finish, "a hh:mm:ss"),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp),
                    imageVector = if (expandFinishPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "종료 시간 선택 도구 펼치기",
                    tint = MaterialTheme.colorScheme.primary

                )
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )

            // 소요 시간
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.baseline_timelapse_24),
                    contentDescription = "소요 시간",
                    tint = MaterialTheme.colorScheme.primary
                )

                VerticalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "소요",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getDurationString(duration, mode = 3),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 삭제 버튼
        TextButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isDeleteButtonPressed) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        OrangeRed
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = if (isDeleteButtonPressed) {
                        OrangeRed
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    shape = RoundedCornerShape(8.dp)
                ),
            onClick = {
                if (isDeleteButtonPressed) {
                    wiDService.deleteWiDById(id = wiDId)
                    navController.popBackStack()
                } else {
                    isDeleteButtonPressed = true
                }
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isDeleteButtonPressed) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(R.drawable.outline_delete_16),
                        contentDescription = "WiD 삭제",
                        tint = OrangeRed
                    )
                }

                Text(
                    text = if (isDeleteButtonPressed) {
                        "삭제 확인"
                    } else {
                        "삭제"
                    },
                    style = Typography.bodyMedium,
                    color = if (isDeleteButtonPressed) {
                        White
                    } else {
                        OrangeRed
                    }
                )
            }
        }

//        /**
//         * 컨텐츠
//         */
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            state = lazyColumnState
//        ) {
//            // 정보 입력
//            item {
//                Column(
//                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.secondary)
//                        .padding(vertical = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp),
//                        text = "WiD 정보",
//                        style = Typography.titleMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//
//                    Column(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .shadow(
//                                elevation = 2.dp,
//                                shape = RoundedCornerShape(8.dp),
//                                spotColor = MaterialTheme.colorScheme.primary,
//                            )
//                            .background(color = MaterialTheme.colorScheme.secondary)
//                    ) {
//                        // 날짜
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                modifier = Modifier
//                                    .padding(16.dp)
//                                    .size(24.dp),
//                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                                contentDescription = "날짜",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//
//                            VerticalDivider()
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = "날짜",
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = getDateString(date),
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
//
//                        HorizontalDivider()
//
//                        // 제목
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min)
//                                .clickable {
//                                    titleMenuExpanded = !titleMenuExpanded
//                                },
//                            verticalAlignment = Alignment.CenterVertically,
//                        ) {
//                            Icon(
//                                modifier = Modifier
//                                    .padding(16.dp)
//                                    .size(24.dp),
//                                painter = painterResource(titleIconMap[title] ?: R.drawable.baseline_menu_book_16),
//                                contentDescription = "제목",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//
//                            VerticalDivider()
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = "제목",
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = titleMap[title] ?: title,
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = if (titleMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "제목 메뉴 펼치기",
//                            )
//                        }
//
//                        HorizontalDivider()
//
//                        // 시작 시간
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min)
//                                .clickable {
//                                    expandStartPicker = !expandStartPicker
//                                },
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                modifier = Modifier
//                                    .padding(16.dp)
//                                    .size(24.dp),
//                                painter = painterResource(id = R.drawable.baseline_alarm_24),
//                                contentDescription = "시작 시간",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//
//                            VerticalDivider()
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = "시작",
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = getTimeString(start, "a hh:mm:ss"),
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = if (expandStartPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "시작 시간 선택 도구 펼치기",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//
//                        HorizontalDivider()
//
//                        // 종료 시간
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min)
//                                .clickable {
//                                    expandFinishPicker = !expandFinishPicker
//                                },
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                modifier = Modifier
//                                    .height(IntrinsicSize.Min)
//                                    .padding(16.dp)
//                                    .size(24.dp),
//                                painter = painterResource(id = R.drawable.baseline_alarm_on_24),
//                                contentDescription = "종료 시간",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//
//                            VerticalDivider()
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = "종료",
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = getTimeString(finish, "a hh:mm:ss"),
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = if (expandFinishPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "종료 시간 선택 도구 펼치기",
//                                tint = MaterialTheme.colorScheme.primary
//
//                            )
//                        }
//
//                        HorizontalDivider()
//
//                        // 소요 시간
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                modifier = Modifier
//                                    .padding(16.dp)
//                                    .size(24.dp),
//                                painter = painterResource(id = R.drawable.baseline_timelapse_24),
//                                contentDescription = "소요 시간",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//
//                            VerticalDivider()
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = "소요",
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = getDurationString(duration, mode = 3),
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
//                    }
//
//                    // 삭제 버튼
//                    TextButton(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .fillMaxWidth()
//                            .border(
//                                width = 1.dp,
//                                color = if (isDeleteButtonPressed) {
//                                    MaterialTheme.colorScheme.secondary
//                                } else {
//                                    OrangeRed
//                                },
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .background(
//                                color = if (isDeleteButtonPressed) {
//                                    OrangeRed
//                                } else {
//                                    MaterialTheme.colorScheme.secondary
//                                },
//                                shape = RoundedCornerShape(8.dp)
//                            ),
//                        onClick = {
//                            if (isDeleteButtonPressed) {
//                                wiDService.deleteWiDById(id = wiDId)
//                                navController.popBackStack()
//                            } else {
//                                isDeleteButtonPressed = true
//                            }
//                        },
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            if (!isDeleteButtonPressed) {
//                                Icon(
//                                    modifier = Modifier
//                                        .size(24.dp),
//                                    painter = painterResource(R.drawable.outline_delete_16),
//                                    contentDescription = "WiD 삭제",
//                                    tint = OrangeRed
//                                )
//                            }
//
//                            Text(
//                                text = if (isDeleteButtonPressed) {
//                                    "삭제 확인"
//                                } else {
//                                    "삭제"
//                                },
//                                style = Typography.bodyMedium,
//                                color = if (isDeleteButtonPressed) {
//                                    White
//                                } else {
//                                    OrangeRed
//                                }
//                            )
//                        }
//                    }
//               }
//            }
//
//
//            item {
//                HorizontalDivider(
//                    thickness = 8.dp,
//                    color = MaterialTheme.colorScheme.tertiary
//                )
//            }
//
//            // 선택 가능한 시간 범위
//            item {
//                Column(
//                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.secondary)
//                        .padding(vertical = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp),
//                        text = "선택 가능한 시간 범위",
//                        style = Typography.titleMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(IntrinsicSize.Min)
//                            .padding(horizontal = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .width(8.dp)
//                                .fillMaxHeight()
//                                .background(MaterialTheme.colorScheme.primary)
//                        )
//
//                        Row(
//                            modifier = Modifier
//                                .shadow(
//                                    // 배경 색이 반드시 있어야하고, 배경보다 shadow를 먼저 적용해야함.
//                                    elevation = 2.dp,
//                                    shape = RoundedCornerShape(8.dp),
//                                    spotColor = MaterialTheme.colorScheme.primary,
//                                )
//                                .background(MaterialTheme.colorScheme.secondary)
//                                .clickable {
//                                    start = startLimit
//                                    finish = finishLimit
//                                },
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .padding(16.dp),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = "${getTimeString(startLimit, "a hh:mm:ss")} ~ ${getTimeString(finishLimit, "a hh:mm:ss")}",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = getDurationString(Duration.between(startLimit, finishLimit), mode = 3),
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .weight(1f)
//                            )
//
//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp)
//                                    .rotate(90f),
//                                painter = painterResource(id = R.drawable.baseline_exit_to_app_16),
//                                contentDescription = "이 시간대 사용하기",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }
//        }

        /**
         * 하단 바
         */
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(Color.Blue)
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "임시 하단 바",
//                style = Typography.titleLarge
//            )
//        }
    }

    /**
     * 날짜, 제목, 시작 시간, 종료 시간 선택 대화 상자
     */
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = titleMenuExpanded || expandStartPicker || expandFinishPicker,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(titleMenuExpanded || expandStartPicker || expandFinishPicker) {
                    titleMenuExpanded = false
                    expandStartPicker = false
                    expandFinishPicker = false
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
//                    .then(if (titleMenuExpanded) Modifier.height(screenHeight / 2) else Modifier)
//                    .shadow(
//                        elevation = 2.dp,
//                        shape = RoundedCornerShape(8.dp),
//                        spotColor = MaterialTheme.colorScheme.primary,
//                    )
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (titleMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(false) {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable {
                                    titleMenuExpanded = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "제목 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "제목 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(titles.size) { index ->
                            val itemTitle = titles[index]
                            val iconResourceId = titleIconMap[itemTitle] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        title = itemTitle
                                        titleMenuExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(id = iconResourceId),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = titleMap[itemTitle] ?: "공부",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                if (title == itemTitle) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = "선택됨",
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                if (expandStartPicker) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(false) {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable {
                                    expandStartPicker = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "시작 시간 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "시작 시간 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 16.dp)
                                .clickable {
                                    expandStartPicker = false
                                    val newStart = LocalTime.of(
                                        startTimePickerState.hour,
                                        startTimePickerState.minute
                                    )

                                    start = newStart
                                },
                            text = "확인",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    TimePicker(
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        state = startTimePickerState
                    )
                }

                if (expandFinishPicker) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(false) {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable {
                                    expandFinishPicker = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "종료 시간 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "종료 시간 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 16.dp)
                                .clickable {
                                    expandFinishPicker = false
                                    val newFinish = LocalTime.of(
                                        finishTimePickerState.hour,
                                        finishTimePickerState.minute
                                    )

                                    finish = newFinish
                                },
                            text = "확인",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    TimePicker(
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        state = finishTimePickerState
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDFragmentPreview() {
    WiDFragment(0, NavController(LocalContext.current))
}