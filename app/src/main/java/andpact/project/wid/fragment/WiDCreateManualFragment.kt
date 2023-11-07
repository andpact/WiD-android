package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import andpact.project.wid.util.titles
import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDCreateManualFragment() {
    val wiDService = WiDService(context = LocalContext.current)

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = LocalDate.now().year
                return year <= currentYear
            }
        }
    )

    val today: LocalDate = LocalDate.now()
    var date by remember { mutableStateOf(today) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("STUDY") }

    val currentTime: LocalTime = LocalTime.now().withSecond(0)
    val totalMinutes = 24 * 60 // 1440분 (24시간)

    var start by remember { mutableStateOf(currentTime) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    var isStartOverlap by remember { mutableStateOf(false) }
    var isStartOverCurrentTime by remember { mutableStateOf(false) }
    var startPosition by remember { mutableStateOf((start.hour * 60 + start.minute).toFloat() / totalMinutes) }

    var finish by remember { mutableStateOf(currentTime) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    var isFinishOverlap by remember { mutableStateOf(false) }
    var isFinishOverCurrentTime by remember { mutableStateOf(false) }
    var finishPosition by remember { mutableStateOf((finish.hour * 60 + finish.minute).toFloat() / totalMinutes) }

    var duration by remember { mutableStateOf(Duration.ZERO) }
    var isDurationUnderMin by remember { mutableStateOf(false) }

    var detail by remember { mutableStateOf("") }

    var wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        if (showDatePicker) {
            DatePickerDialog(
                shape = RoundedCornerShape(8.dp),
                colors = DatePickerDefaults.colors(containerColor = colorResource(id = R.color.light_gray)),
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()

                        wiDList = wiDService.readDailyWiDListByDate(date)

                        if (wiDList.isEmpty()) {
                            isStartOverlap = false
                            isFinishOverlap = false
                        } else {
                            for (existingWiD in wiDList) {
                                if (existingWiD.start <= start && start <= existingWiD.finish) {
                                    isStartOverlap = true
                                    break
                                } else {
                                    isStartOverlap = false
                                }
                            }

                            for (existingWiD in wiDList) {
                                if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                    isFinishOverlap = true
                                    break
                                } else {
                                    isFinishOverlap = false
                                }
                            }

                            for (existingWiD in wiDList) {
                                if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                    isStartOverlap = true
                                    isFinishOverlap = true
                                    break
                                }
                            }
                        }
                    }) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        Text(text = "취소")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null,
                )
            }
        }

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
                        .background(
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
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
                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                        isFinishOverlap = true
                                        break
                                    } else {
                                        isFinishOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
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
                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                        isFinishOverlap = true
                                        break
                                    } else {
                                        isFinishOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
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

                        append(" WiD리스트")
                    }
                }

                Text(
                    text = dateText,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                ) {
                    val configuration = LocalConfiguration.current
                    val screenWidthDp = configuration.screenWidthDp
//                    val density = LocalDensity.current.density
//                    val screenWidthPixels = (screenWidthDp * density).toInt()

                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        Icon(
                            modifier = Modifier
                                .scale(0.8f)
                                .rotate(90f)
                                .offset(y = -(screenWidthDp * startPosition * 0.8).dp),
                            painter = painterResource(id = R.drawable.outline_play_arrow_24),
                            contentDescription = "start",
                            tint = if (isStartOverlap || isStartOverCurrentTime) Color.Red else Color.Unspecified
                        )

                        Icon(
                            modifier = Modifier
                                .scale(0.8f)
                                .rotate(90f)
                                .offset(y = -(screenWidthDp * finishPosition * 0.8).dp),
                            painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                            contentDescription = "finish",
                            tint = if (isFinishOverlap || isFinishOverCurrentTime) Color.Red else Color.Unspecified)
                    }

                    HorizontalBarChartView(wiDList = wiDList)
                    
//                    Text(text = "screenWidthDp : $screenWidthDp")
//                    Text(text = "startPosition : $startPosition")
//                    Text(text = "screenWidthDp * startPosition : ${screenWidthDp * startPosition}")
                }
            }

            item {
                Text(
                    text = "New WiD",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                Column(
                    modifier = Modifier
                        .background(
                            color = colorResource(id = R.color.light_gray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        modifier = Modifier
                        .padding(horizontal = 8.dp),
                        text = "날짜"
                    )

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
                                .scale(0.8f)
                                .padding(8.dp),
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
                                .padding(8.dp)
                                .weight(1f),
                            text = dateText,
                        )

                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .scale(0.8f)
                                .padding(8.dp)
                                .clickable { showDatePicker = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "showDatePicker")
                    }

                    Text(
                        modifier = Modifier
                        .padding(horizontal = 8.dp),
                        text = "제목"
                    )

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
                            .scale(0.8f)
                            .padding(8.dp),
                            painter = painterResource(id = R.drawable.baseline_category_24),
                            contentDescription = "title")

                        Text(modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
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
                                .scale(0.8f)
                                .padding(8.dp)
                                .clickable { titleMenuExpanded = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "titleMenuExpanded")
                    }

                    Text(
                        modifier = Modifier
                        .padding(horizontal = 8.dp),
                        text = "시작 시간"
                    )

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
                                .scale(0.8f)
                                .padding(8.dp),
                            painter = painterResource(id = R.drawable.outline_play_arrow_24),
                            contentDescription = "start")

                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            text = start.format(DateTimeFormatter.ofPattern("a h:mm")),
                        )

                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .scale(0.8f)
                                .padding(8.dp)
                                .clickable { showStartPicker = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "showStartPicker")
                    }

                    Text(
                        modifier = Modifier
                        .padding(horizontal = 8.dp),
                        text = "종료 시간"
                    )

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
                                .scale(0.8f)
                                .padding(8.dp),
                            painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                            contentDescription = "finish")

                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            text = finish.format(DateTimeFormatter.ofPattern("a h:mm")),
                        )

                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .scale(0.8f)
                                .padding(8.dp)
                                .clickable { showFinishPicker = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "showFinishPicker")
                    }

                    Text(
                        modifier = Modifier
                        .padding(horizontal = 8.dp),
                        text = "지속 시간"
                    )

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
                                .scale(0.8f)
                                .padding(8.dp),
                            painter = painterResource(id = R.drawable.baseline_timelapse_24),
                            contentDescription = "duration")

                        Text(modifier = Modifier
                            .padding(8.dp),
                            text = formatDuration(duration, mode = 2),
                        )
                    }

                    Text(
                        modifier = Modifier
                        .padding(horizontal = 8.dp),
                        text = "설명"
                    )

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
                                .scale(0.8f)
                                .padding(8.dp),
                            painter = painterResource(id = R.drawable.baseline_message_24),
                            contentDescription = "detail")

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

                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .scale(0.8f)
                                .padding(8.dp)
                                .clickable {
                                    detail = ""
                                },
                            painter = painterResource(id = R.drawable.baseline_clear_24),
                            contentDescription = "detailClear"
                        )
                    }
                }
            }

            item {
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier
                        .weight(1f)
                    )

                    IconButton(
                        onClick = {
                            val newWiD = WiD(id = 0, date = date, title = title, start = start, finish = finish, duration = duration, detail = detail)
                            wiDService.createWiD(newWiD)

                            wiDList = wiDService.readDailyWiDListByDate(date)

                            for (existingWiD in wiDList) {
                                if (existingWiD.start <= start && start <= existingWiD.finish) {
                                    isStartOverlap = true
                                    break
                                } else {
                                    isStartOverlap = false
                                }
                            }

                            for (existingWiD in wiDList) {
                                if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                    isFinishOverlap = true
                                    break
                                } else {
                                    isFinishOverlap = false
                                }
                            }

                            for (existingWiD in wiDList) {
                                if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                    isStartOverlap = true
                                    isFinishOverlap = true
                                    break
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                color = if (isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime || isDurationUnderMin || duration == Duration.ZERO) {
                                    Color.LightGray
                                } else {
                                    Color.Blue
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding()
                            .weight(1f),
                        enabled = !(isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime || isDurationUnderMin || duration == Duration.ZERO)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(modifier = Modifier
                                .scale(0.8f),
                                painter = painterResource(id = R.drawable.outline_add_box_24),
                                contentDescription = "add",
                                tint = Color.White)

                            Text(text = "등록",
                                style = TextStyle(color = Color.White, textAlign = TextAlign.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateManualFragmentPreview() {
    WiDCreateManualFragment()
}