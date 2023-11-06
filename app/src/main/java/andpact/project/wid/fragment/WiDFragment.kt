package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import andpact.project.wid.util.titles
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun WiDView(wiDId: Long, navController: NavController, buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)
    val clickedWiD = wiDService.readWiDById(wiDId)

    if (clickedWiD == null) {
        Text(modifier = Modifier
            .fillMaxSize(),
            text = "WiD not found",
            textAlign = TextAlign.Center)
        return
    }

    val today: LocalDate = LocalDate.now()
    val date by remember { mutableStateOf(clickedWiD.date) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(clickedWiD.title) }

    val currentTime: LocalTime = LocalTime.now().withSecond(0)
    val totalMinutes = 24 * 60 // 1440분 (24시간)

    var start by remember { mutableStateOf(clickedWiD.start) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute)
    var isStartOverlap by remember { mutableStateOf(false) }
    var isStartOverCurrentTime by remember { mutableStateOf(false) }
    var startPosition by remember { mutableStateOf((start.hour * 60 + start.minute).toFloat() / totalMinutes) }

    var finish by remember { mutableStateOf(clickedWiD.finish) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute)
    var isFinishOverlap by remember { mutableStateOf(false) }
    var isFinishOverCurrentTime by remember { mutableStateOf(false) }
    var finishPosition by remember { mutableStateOf((finish.hour * 60 + finish.minute).toFloat() / totalMinutes) }

    var duration by remember { mutableStateOf(clickedWiD.duration) }
    var isDurationUnderMin by remember { mutableStateOf(false) }

    var detail by remember { mutableStateOf(clickedWiD.detail) }

    var isEditing by remember { mutableStateOf(false) }

    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    val wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
//    val currentIndex = wiDList.indexOf(wiD)

    LaunchedEffect(isDeleteButtonPressed) {
        if (isDeleteButtonPressed) {
            delay(2000L)
            isDeleteButtonPressed = false
        }
    }

    BackHandler(enabled = true) { // 휴대폰 뒤로 가기 버튼 클릭 시
        navController.popBackStack()
        buttonsVisible.value = true
    }

    Box(modifier = Modifier
        .fillMaxSize())
    {
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
                                startPosition = (start.hour * 60 + start.minute).toFloat() / totalMinutes

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
                                startPosition = (finish.hour * 60 + finish.minute).toFloat() / totalMinutes

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                ) {
                    val dateText = buildAnnotatedString {
                        date.let {
                            append(it.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 ")))
                            append("(")
                            withStyle(
                                style = SpanStyle(
                                    color = when (it.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Unspecified
                                    }
                                )
                            ) {
                                append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                            }
                            append(")")

                            append("의 WiD리스트")
                        }
                    }

                    Text(
                        text = dateText,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )

                    val configuration = LocalConfiguration.current
                    val screenWidthDp = configuration.screenWidthDp

                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        Icon(
                            modifier = Modifier
                                .rotate(90f)
                                .offset(y = -(screenWidthDp * startPosition * 0.8).dp),
                            painter = painterResource(id = R.drawable.outline_play_arrow_24),
                            contentDescription = "start",
                            tint = if (isStartOverlap || isStartOverCurrentTime) Color.Red else Color.Unspecified
                        )

                        Icon(
                            modifier = Modifier
                                .rotate(90f)
                                .offset(y = -(screenWidthDp * finishPosition * 0.8).dp),
                            painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                            contentDescription = "finish",
                            tint = if (isFinishOverlap || isFinishOverCurrentTime) Color.Red else Color.Unspecified)
                    }

                    HorizontalBarChartView(wiDList = wiDList)
                }
            }

            Text(
                text = "WiD No.$wiDId",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "날짜")

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "date")

                    val dateText = buildAnnotatedString {
                        date.let {
                            append(it.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 ")))
                            append("(")
                            withStyle(
                                style = SpanStyle(
                                    color = when (it.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Unspecified
                                    }
                                )
                            ) {
                                append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                            }
                            append(")")
                        }
                    }

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = dateText,
                    )
                }

                Text(text = "제목")

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier
                        .padding(16.dp)
                        .size(16.dp),
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        contentDescription = "title")

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = titleMap[title] ?: title,
                    )

                    val titleColorId = colorMap[title]
                    val backgroundColor = if (titleColorId != null) {
                        Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                    } else {
                        colorResource(id = R.color.light_gray)
                    }

                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(20.dp)
                            .border(
                                BorderStroke(1.dp, Color.LightGray),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = backgroundColor,
                                RoundedCornerShape(8.dp)
                            )
                    )

                    DropdownMenu(modifier = Modifier
//                        .padding(horizontal = 32.dp)
                        .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(8.dp)),
                        expanded = titleMenuExpanded,
                        onDismissRequest = {
                            titleMenuExpanded = false
                        },
                    ) {
                        titles.forEach { menuItem ->
                            DropdownMenuItem(modifier = Modifier,
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

                    VerticalDivider()

                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { titleMenuExpanded = true },
                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                        contentDescription = "titleMenuExpanded")
                }

                Text(text = "시작 시간")

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.outline_play_arrow_24),
                        contentDescription = "start")

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = start.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                    )

                    VerticalDivider()

                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { showStartPicker = true },
                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                        contentDescription = "showStartPicker")
                }

                Text(text = "종료 시간")

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                        contentDescription = "finish")

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = finish.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                    )

                    VerticalDivider()

                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { showFinishPicker = true },
                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                        contentDescription = "showFinishPicker")
                }

                Text(text = "지속 시간")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "duration")

                    Text(modifier = Modifier
                        .padding(16.dp),
                        text = formatDuration(duration, mode = 2),
                    )
                }

                Text(text = "설명")

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.baseline_message_24),
                        contentDescription = "detail")

                    if (isEditing) {
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1f),
                            value = detail,
                            onValueChange = { newText ->
                                detail = newText
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            text = detail.ifEmpty { "설명 입력.." },
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(16.dp)
                            .clickable {
                                detail = ""
                            },
                        painter = painterResource(id = R.drawable.baseline_clear_24),
                        contentDescription = "detailClear"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (isDeleteButtonPressed) {
                            wiDService.deleteWiDById(id = wiDId)
                            navController.popBackStack()
                            buttonsVisible.value = true
                        } else if (isEditing) {
                            isEditing = false

                            // date는 애초에 변경 불가로 설정함.
                            title = clickedWiD.title
                            start = clickedWiD.start
                            finish = clickedWiD.finish
                            duration = clickedWiD.duration
                            detail = clickedWiD.detail
                        } else {
                            isDeleteButtonPressed = true
                        }
                    },
                    modifier = Modifier
                        .background(
                            color = if (isDeleteButtonPressed) Color.Red else Color.Unspecified,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isEditing) Color.Gray else if (isDeleteButtonPressed) Color.Unspecified else Color.Red
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(modifier = Modifier
                            .size(16.dp),
                            painter = painterResource(id = if (isDeleteButtonPressed) R.drawable.outline_delete_24 else if (isEditing) R.drawable.outline_cancel_24 else R.drawable.baseline_delete_24),
                            contentDescription = "delete confirmed or cancel or delete",
                            tint = if (isDeleteButtonPressed) Color.White else if (isEditing) Color.Gray else Color.Red)

                        Text(text = if (isDeleteButtonPressed) "삭제 확인" else if (isEditing) "취소" else "삭제",
                            style = TextStyle(color = if (isDeleteButtonPressed) Color.White else if (isEditing) Color.Gray else Color.Red, textAlign = TextAlign.Center))
                    }
                }

                IconButton(
                    onClick = {
                        if (isEditing) {
                            wiDService.updateWiD(id = wiDId, date = date, title = title, start = start, finish = finish, duration = duration, detail = detail)

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
                                Color.Blue
                            } else {
                                Color.Green
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
                            tint = Color.White)

                        Text(text = if (isEditing) "완료" else "수정",
                            style = TextStyle(color = Color.White, textAlign = TextAlign.Center),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDViewPreview() {
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDView(0, NavController(LocalContext.current), buttonsVisible)
}