package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDFragment(wiDId: Long, navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 데이터베이스
    val wiDService = WiDService(context = LocalContext.current)
    val clickedWiD = wiDService.readWiDById(wiDId)

    if (clickedWiD == null) {
        Text(modifier = Modifier
            .fillMaxSize(),
            text = "WiD not found",
            textAlign = TextAlign.Center)
        return
    }

    // 날짜
    val today: LocalDate = LocalDate.now()
    val date by remember { mutableStateOf(clickedWiD.date) }
    val currentTime: LocalTime = LocalTime.now().withSecond(0)
//    val totalMinutes = 24 * 60 // 1440분 (24시간)

    // 데이터베이스
    val wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(clickedWiD.title) }

    // 시작 시간
    var start by remember { mutableStateOf(clickedWiD.start) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    var isStartOverlap by remember { mutableStateOf(false) }
    var isStartOverCurrentTime by remember { mutableStateOf(false) }
//    var startPosition by remember { mutableStateOf((start.hour * 60 + start.minute).toFloat() / totalMinutes) }

    // 종료 시간
    var finish by remember { mutableStateOf(clickedWiD.finish) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    var isFinishOverlap by remember { mutableStateOf(false) }
    var isFinishOverCurrentTime by remember { mutableStateOf(false) }
//    var finishPosition by remember { mutableStateOf((finish.hour * 60 + finish.minute).toFloat() / totalMinutes) }

    // 소요 시간
    var duration by remember { mutableStateOf(clickedWiD.duration) }
    var isDurationUnderMin by remember { mutableStateOf(false) }

    // 설명
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(isDeleteButtonPressed) {
        if (isDeleteButtonPressed) {
            delay(2000L)
            isDeleteButtonPressed = false
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (showStartPicker) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = RoundedCornerShape(8.dp)
                    ),
                onDismissRequest = { showStartPicker = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.LightGray.copy(alpha = 0.3f))
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = startTimePickerState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showStartPicker = false }
                        ) {
                            Text(text = "취소")
                        }

                        TextButton(
                            onClick = {
                                showStartPicker = false
                                val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                start = newStart
//                                startPosition = (start.hour * 60 + start.minute).toFloat() / totalMinutes

                                for (existingWiD in wiDList) {
                                    // wiDList속의 clickedWiD를 사용할 필요가 없기 때문에 continue
                                    if (existingWiD == clickedWiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == clickedWiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                        isFinishOverlap = true
                                        break
                                    } else {
                                        isFinishOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == clickedWiD) {
                                        continue
                                    }

                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                        isStartOverlap = true
                                        isFinishOverlap = true
                                        break
                                    }
                                }

                                isStartOverCurrentTime = date == today && currentTime < start

                                duration = Duration.between(start, finish)
                                isDurationUnderMin = duration <= Duration.ZERO
                            }
                        ) {
                            Text(text = "확인")
                        }
                    }
                }
            }
        }

        if (showFinishPicker) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = RoundedCornerShape(8.dp)
                    ),
                onDismissRequest = { showFinishPicker = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = finishTimePickerState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showFinishPicker = false }
                        ) {
                            Text(text = "취소")
                        }

                        TextButton(
                            onClick = {
                                showFinishPicker = false
                                val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute)

                                finish = newFinish
//                                finishPosition = (finish.hour * 60 + finish.minute).toFloat() / totalMinutes

                                for (existingWiD in wiDList) {
                                    if (existingWiD == clickedWiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == clickedWiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                        isFinishOverlap = true
                                        break
                                    } else {
                                        isFinishOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == clickedWiD) {
                                        continue
                                    }

                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                        isStartOverlap = true
                                        isFinishOverlap = true
                                        break
                                    }
                                }

                                isFinishOverCurrentTime = date == today && currentTime < finish

                                duration = Duration.between(start, finish)
                                isDurationUnderMin = duration <= Duration.ZERO
                            }
                        ) {
                            Text(text = "확인")
                        }
                    }
                }
            }
        }

        // 전체 화면
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.ghost_white))
        ) {
            // 상단 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            navController.popBackStack()
                            mainTopBottomBarVisible.value = true
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    // AnnotatedString 끼리 결합해야 TextStyle 적용된다.
                    Text(buildAnnotatedString { append("WiD : ") } + getDayString(date))
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
            ) {
                if (isEditing) {
                    // 막대 그래프
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 가로 막대 차트
                        Text(
                            text = "시간 그래프",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                            )
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 32.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                HorizontalBarChartView(wiDList = wiDList)
                            }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clicked WiD
                    Text(
                        text = "WiD",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                        )
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .scale(0.8f)
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                                    contentDescription = "date"
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f),
                                    text = getDayString(date)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isEditing) { titleMenuExpanded = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.outline_subtitles_24),
                                    contentDescription = "title"
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = titleMap[title] ?: title,
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(10.dp)
                                        .background(
                                            color = colorResource(
                                                id = colorMap[title] ?: R.color.light_gray
                                            )
                                        )
                                )

                                DropdownMenu(
                                    modifier = Modifier
            //                        .padding(horizontal = 32.dp)
                                        .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(8.dp)),
                                    expanded = titleMenuExpanded,
                                    onDismissRequest = {
                                        titleMenuExpanded = false
                                    },
                                ) {
                                    titles.forEach { menuItem ->
                                        DropdownMenuItem(
                                            onClick = {
                                                title = menuItem
                                                titleMenuExpanded = false
                                            },
                                            text = {
                                                Text(text = titleMap[menuItem] ?: menuItem)
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isEditing) { showStartPicker = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.outline_play_arrow_24),
                                    contentDescription = "start"
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f),
                                    text = start.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isEditing) { showFinishPicker = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                    contentDescription = "finish"
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f),
                                    text = finish.format(DateTimeFormatter.ofPattern("a h:mm:ss"))
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                    contentDescription = "duration"
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = formatDuration(duration, mode = 2)
                                )
                            }
                        }
                    }

                    // 버튼
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isDeleteButtonPressed) {
                                    wiDService.deleteWiDById(id = wiDId)
                                    navController.popBackStack()
                                    mainTopBottomBarVisible.value = true
                                } else if (isEditing) {
                                    isEditing = false

                                    // date는 애초에 변경 불가로 설정함.
                                    title = clickedWiD.title
                                    start = clickedWiD.start
                                    finish = clickedWiD.finish
                                    duration = clickedWiD.duration
                                } else {
                                    isDeleteButtonPressed = true
                                }
                            },
                            modifier = Modifier
                                .background(
                                    color = if (isDeleteButtonPressed) colorResource(id = R.color.orange_red) else Color.Unspecified,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isEditing) Color.Gray else if (isDeleteButtonPressed) Color.Unspecified else colorResource(
                                            id = R.color.orange_red
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isDeleteButtonPressed) R.drawable.outline_delete_24 else if (isEditing) R.drawable.outline_cancel_24 else R.drawable.baseline_delete_24),
                                    contentDescription = "delete confirmed or cancel or delete",
                                    tint = if (isDeleteButtonPressed) Color.White else if (isEditing) Color.Gray else colorResource(id = R.color.orange_red)
                                )

                                Text(
                                    text = if (isDeleteButtonPressed) "삭제 확인" else if (isEditing) "취소" else "삭제",
                                    style = TextStyle(
                                        color = if (isDeleteButtonPressed) Color.White else if (isEditing) Color.Gray else colorResource(id = R.color.orange_red),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    wiDService.updateWiD(
                                        id = wiDId,
                                        date = date,
                                        title = title,
                                        start = start,
                                        finish = finish,
                                        duration = duration
                                    )

                                    isEditing = false
                                } else {
                                    isEditing = true

                                    // 수정 버튼을 누르면 삭제 버튼을 누른 상태를 해제함.
                                    isDeleteButtonPressed = false
                                }
                            },
                            modifier = Modifier
                                .background(
                                    color = if (isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime || isDurationUnderMin || duration == Duration.ZERO) {
                                        Color.LightGray
                                    } else if (isEditing) {
                                        colorResource(id = R.color.deep_sky_blue)
                                    } else {
                                        colorResource(id = R.color.lime_green)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .weight(1f),
                            enabled = !(isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime || isDurationUnderMin || duration == Duration.ZERO)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isEditing) R.drawable.outline_check_box_24 else R.drawable.baseline_edit_24),
                                    contentDescription = "edit & complete",
                                    tint = Color.White
                                )

                                Text(
                                    text = if (isEditing) "완료" else "수정",
                                    style = TextStyle(color = Color.White, textAlign = TextAlign.Center),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    WiDFragment(0, NavController(LocalContext.current), mainTopBottomBarVisible)
}