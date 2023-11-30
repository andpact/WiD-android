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

    // 날짜
    val today: LocalDate = LocalDate.now()
    val date by remember { mutableStateOf(clickedWiD.date) }
    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    // WiD
    val wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
    val clickedWiDIndex = wiDList.indexOf(clickedWiD)
    var isDeleteButtonPressed by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(clickedWiD.title) }

    // 시작 시간
    var start by remember { mutableStateOf(clickedWiD.start) }
    var startLimit = if (0 < clickedWiDIndex) {
        wiDList[clickedWiDIndex - 1].finish.plusMinutes(1).withSecond(0)
    } else {
        LocalTime.MIDNIGHT
    }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    var isStartOverlap by remember(start) { mutableStateOf(start < startLimit)}
    var isStartOverCurrentTime by remember(start) { mutableStateOf(date == today && currentTime < start) }

    // 종료 시간
    var finish by remember { mutableStateOf(clickedWiD.finish) }
    var finishLimit = if (clickedWiDIndex < wiDList.size - 1) {
        wiDList[clickedWiDIndex + 1].start.minusMinutes(1).withSecond(0)
    } else if (date == today) {
        currentTime
    } else {
        LocalTime.MIDNIGHT.minusSeconds(1)
    }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    var isFinishOverlap by remember(finish) { mutableStateOf(finishLimit < finish) }
    var isFinishOverCurrentTime by remember(finish) { mutableStateOf(date == today && currentTime < finish) }

    // 소요 시간
//    var duration by remember { mutableStateOf(clickedWiD.duration) }
    var duration by remember(start, finish) { mutableStateOf(Duration.between(start, finish)) }
//    var isDurationUnderMin by remember { mutableStateOf(false) }
    var durationExist by remember(duration) { mutableStateOf(Duration.ZERO < duration) }

    // WiD
    val isTimeOverlap = isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime

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
        // 시작 시간 선택
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
                        TextButton(
                            onClick = { showStartPicker = false }
                        ) {
                            Text(text = "취소")
                        }

                        TextButton(
                            onClick = {
                                showStartPicker = false
                                val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                start = newStart

//                                for (existingWiD in wiDList) {
//                                    // wiDList속의 clickedWiD를 사용할 필요가 없기 때문에 continue
//                                    if (existingWiD == clickedWiD) {
//                                        continue
//                                    }
//
//                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
//                                        isStartOverlap = true
//                                        break
//                                    } else {
//                                        isStartOverlap = false
//                                    }
//                                }
//
//                                for (existingWiD in wiDList) {
//                                    if (existingWiD == clickedWiD) {
//                                        continue
//                                    }
//
//                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
//                                        isFinishOverlap = true
//                                        break
//                                    } else {
//                                        isFinishOverlap = false
//                                    }
//                                }
//
//                                for (existingWiD in wiDList) {
//                                    if (existingWiD == clickedWiD) {
//                                        continue
//                                    }
//
//                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
//                                        isStartOverlap = true
//                                        isFinishOverlap = true
//                                        break
//                                    }
//                                }

//                                isStartOverCurrentTime = date == today && currentTime < start

//                                duration = Duration.between(start, finish)
//                                isDurationUnderMin = duration <= Duration.ZERO
                            }
                        ) {
                            Text(text = "확인")
                        }
                    }
                }
            }
        }

        // 종료 시간 선택
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
                        .background(color = Color.LightGray.copy(alpha = 0.3f))
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
                        TextButton(
                            onClick = { showFinishPicker = false }
                        ) {
                            Text(text = "취소")
                        }

                        TextButton(
                            onClick = {
                                showFinishPicker = false
                                val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute)

                                finish = newFinish

//                                for (existingWiD in wiDList) {
//                                    if (existingWiD == clickedWiD) {
//                                        continue
//                                    }
//
//                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
//                                        isStartOverlap = true
//                                        break
//                                    } else {
//                                        isStartOverlap = false
//                                    }
//                                }
//
//                                for (existingWiD in wiDList) {
//                                    if (existingWiD == clickedWiD) {
//                                        continue
//                                    }
//
//                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
//                                        isFinishOverlap = true
//                                        break
//                                    } else {
//                                        isFinishOverlap = false
//                                    }
//                                }
//
//                                for (existingWiD in wiDList) {
//                                    if (existingWiD == clickedWiD) {
//                                        continue
//                                    }
//
//                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
//                                        isStartOverlap = true
//                                        isFinishOverlap = true
//                                        break
//                                    }
//                                }

//                                isFinishOverCurrentTime = date == today && currentTime < finish

//                                duration = Duration.between(start, finish)
//                                isDurationUnderMin = duration <= Duration.ZERO
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
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            navController.popBackStack()
                            mainTopBottomBarVisible.value = true
                        },
                        shape = RectangleShape
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    // AnnotatedString 끼리 결합해야 TextStyle 적용된다.
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("WiD")
                            }
                            append(" - ")
                            append(getDayString(date))
                        }
                    )
                }

                TextButton(
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
                        }
                    },
                    shape = RectangleShape,
                    enabled = !isTimeOverlap && durationExist
                ) {
                    Text(
                        text = if (isEditing) "완료" else "수정",
                        style = TextStyle(
                            color = if (!(!isTimeOverlap && durationExist)) {
                                Color.LightGray
                            } else if (isEditing) {
                                colorResource(id = R.color.lime_green)
                            } else {
                                colorResource(id = R.color.deep_sky_blue)
                            }
                        )
                    )
                }
            }

            // 컨텐츠
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
            ) {
                // Clicked WiD
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                    contentDescription = "Date"
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        painter = painterResource(id = R.drawable.outline_subtitles_24),
                                        contentDescription = "Title"
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
                                            .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(8.dp)),
                                        expanded = titleMenuExpanded,
                                        onDismissRequest = {
                                            titleMenuExpanded = false
                                        },
                                    ) {
                                        titles.forEach { menuTitle ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    title = menuTitle
                                                    titleMenuExpanded = false
                                                },
                                                trailingIcon = {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(CircleShape)
                                                            .size(10.dp)
                                                            .background(
                                                                color = colorResource(
                                                                    id = colorMap[menuTitle]
                                                                        ?: R.color.light_gray
                                                                )
                                                            )
                                                    )
                                                },
                                                text = { Text(text = titleMap[menuTitle] ?: menuTitle) }
                                            )
                                        }
                                    }
                                }

                                if (isEditing) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                        contentDescription = "Edit title"
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isEditing) { showStartPicker = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        painter = painterResource(id = R.drawable.outline_play_arrow_24),
                                        contentDescription = "Start"
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = formatTime(start, "a h:mm:ss")
                                    )
                                }

                                if (isEditing) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                        contentDescription = "Edit start"
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isEditing) { showFinishPicker = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                        contentDescription = "Finish"
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = formatTime(finish, "a h:mm:ss")
                                    )
                                }

                                if (isEditing) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                        contentDescription = "Edit finish"
                                    )
                                }
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
                                    contentDescription = "Duration"
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = formatDuration(duration, mode = 2)
                                )
                            }
                        }
                    }
                }
            }

            // 하단 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 16.dp)
            ) {
                if (isEditing) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("선택 가능한 시간")
                            }
                            append(" - ")
                            append(
                                formatTime(
                                    time = startLimit,
                                    patten = "a H:mm"
                                )
                            )
                            append(" ~ ")
                            append(
                                formatTime(
                                    time = finishLimit,
                                    patten = "a H:mm"
                                )
                            )
                        }
                    )
                }

                TextButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    onClick = {
                        if (isDeleteButtonPressed) {
                            wiDService.deleteWiDById(id = wiDId)
                            navController.popBackStack()
                            mainTopBottomBarVisible.value = true
                        } else {
                            isDeleteButtonPressed = true
                        }
                    },
                    shape = RectangleShape
                ) {
                    Text(
                        text = if (isDeleteButtonPressed) buildAnnotatedString {
                            withStyle(style = SpanStyle(color = colorResource(id = R.color.orange_red))) {
                                append("삭제 확인")
                            }
                        } else {
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("WiD ")
                                }
                                withStyle(style = SpanStyle(color = colorResource(id = R.color.orange_red))) {
                                    append("삭제")
                                }
                            }
                        }
                    )
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