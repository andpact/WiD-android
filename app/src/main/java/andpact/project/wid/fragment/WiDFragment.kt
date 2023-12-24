package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDFragment(wiDId: Long, navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
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
    val coroutineScope = rememberCoroutineScope()

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
        mainTopBottomBarVisible.value = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "WiD",
                style = Typography.titleLarge
            )

            Row(
                modifier = Modifier
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
                        mainTopBottomBarVisible.value = true
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_done_16),
                    contentDescription = "Modify & complete WiD",
                    tint = if (!(!isTimeOverlap && durationExist)) {
                        Color.LightGray
                    } else {
                        colorResource(id = R.color.lime_green)
                    }
                )

                Text(
                    text = "완료",
                    style = Typography.bodyMedium,
                    color = if (!(!isTimeOverlap && durationExist)) {
                        Color.LightGray
                    } else {
                        colorResource(id = R.color.lime_green)
                    }
                )
            }
        }

        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = lazyColumnState
        ) {
            item(1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "날짜"
                    )

                    Column {
                        Text(
                            text = "날짜",
                            style = Typography.labelSmall
                        )

                        Text(
                            text = getDayString(date),
                            style = Typography.bodyMedium
                        )
                    }
                }
            }

            item(2) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            item(3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            titleMenuExpanded = !titleMenuExpanded
//                            if (titleMenuExpanded) {
//                                coroutineScope.launch {
//                                    lazyColumnState.animateScrollToItem(1)
//                                }
//                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_title_24),
                        contentDescription = "제목",
                        tint = colorResource(
                            id = colorMap[title] ?: R.color.black
                        )
                    )

                    Column {
                        Text(
                            text = "제목",
                            style = Typography.labelSmall
                        )

                        Text(
                            text = titleMap[title] ?: title,
                            style = Typography.bodyMedium
                        )
                    }


                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (titleMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "제목 메뉴 펼치기",
                    )
                }

                if (titleMenuExpanded) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        titles.forEach { chipTitle ->
                            item {
                                FilterChip(
                                    selected = title == chipTitle,
                                    onClick = {
                                        title = chipTitle
                                        titleMenuExpanded = false
                                    },
                                    label = {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            text = titleMap[chipTitle] ?: chipTitle,
                                            style = Typography.bodySmall,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorResource(id = R.color.light_gray),
                                        labelColor = Color.Black,
                                        selectedContainerColor = Color.Black,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item(4) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            item(5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandStartPicker = !expandStartPicker
//                            if (expandStartPicker) {
//                                coroutineScope.launch {
//                                    lazyColumnState.animateScrollToItem(3)
//                                }
//                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_alarm_24),
                        contentDescription = "시작 시간"
                    )

                    Column {
                        Text(
                            text = "시작",
                            style = Typography.labelSmall
                        )

                        Text(
                            text = formatTime(start, "a hh:mm:ss"),
                            style = Typography.bodyMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (expandStartPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "시작 시간 선택 도구 펼치기",
                    )
                }

                if (expandStartPicker) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = startTimePickerState)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { expandStartPicker = false }
                            ) {
                                Text(
                                    text = "취소",
                                    style = Typography.bodyMedium
                                )
                            }

                            TextButton(
                                onClick = {
                                    expandStartPicker = false
                                    val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                    start = newStart
                                }
                            ) {
                                Text(
                                    text = "확인",
                                    style = Typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item(6) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            item(7) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandFinishPicker = !expandFinishPicker
//                            if (expandFinishPicker) {
//                                coroutineScope.launch {
//                                    lazyColumnState.animateScrollToItem(5) // Finish Picker가 화면에 나타나는 도중에 스크롤이 동작하면 에러가 나는 듯.
//                                }
//                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                        contentDescription = "종료 시간"
                    )

                    Column {
                        Text(
                            text = "종료",
                            style = Typography.labelSmall
                        )

                        Text(
                            text = formatTime(finish, "a hh:mm:ss"),
                            style = Typography.bodyMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (expandFinishPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "종료 시간 선택 도구 펼치기",
                    )
                }

                if (expandFinishPicker) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TimePicker(state = finishTimePickerState)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { expandFinishPicker = false }
                            ) {
                                Text(
                                    text = "취소",
                                    style = Typography.bodyMedium
                                )
                            }

                            TextButton(
                                onClick = {
                                    expandFinishPicker = false
                                    val newFinish = LocalTime.of(
                                        finishTimePickerState.hour,
                                        finishTimePickerState.minute
                                    )

                                    finish = newFinish
                                }
                            ) {
                                Text(
                                    text = "확인",
                                    style = Typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item(8) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            item(9) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "소요 시간"
                    )

                    Column {
                        Text(
                            text = "소요",
                            style = Typography.labelSmall
                        )

                        Text(
                            text = formatDuration(duration, mode = 3),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            item(10) {
                TextButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (isDeleteButtonPressed) {
                                colorResource(id = R.color.ghost_white)
                            } else {
                                colorResource(id = R.color.orange_red)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = if (isDeleteButtonPressed) {
                                colorResource(id = R.color.orange_red)
                            } else {
                                colorResource(id = R.color.ghost_white)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                    onClick = {
                        if (isDeleteButtonPressed) {
                            wiDService.deleteWiDById(id = wiDId)
                            navController.popBackStack()
                            mainTopBottomBarVisible.value = true
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
                                painter = painterResource(R.drawable.outline_delete_16),
                                contentDescription = "WiD 삭제",
                                tint = colorResource(id = R.color.orange_red)
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
                                Color.White
                            } else {
                                colorResource(id = R.color.orange_red)
                            }
                        )
                    }
                }
            }

            item(11) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(8.dp)
                        .background(Color.White)
                )
            }

            item(12) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "선택 가능한 시간 범위",
                        style = Typography.titleMedium
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 32.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(
                                    time = startLimit,
                                    patten = "a hh:mm:ss"
                                ),
                                style = Typography.bodyLarge
                            )

                            Text(
                                text = " ~ ",
                                style = Typography.bodyLarge
                            )

                            Text(
                                text = formatTime(
                                    time = finishLimit,
                                    patten = "a hh:mm:ss"
                                ),
                                style = Typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )
            }
        }

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
}

@Preview(showBackground = true)
@Composable
fun WiDFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    WiDFragment(0, NavController(LocalContext.current), mainTopBottomBarVisible)
}