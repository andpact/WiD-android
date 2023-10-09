package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var title by remember { mutableStateOf("STUDY") }

    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    var start by remember { mutableStateOf(currentTime) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState()
    var isStartOverlap by remember { mutableStateOf(false) }

    var finish by remember { mutableStateOf(currentTime) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState()
    var isFinishOverlap by remember { mutableStateOf(false) }

    var duration by remember { mutableStateOf(Duration.ZERO) }
    var isDurationMinOrMax by remember { mutableStateOf(true) }

    var detail by remember { mutableStateOf("") }

    var wiDList by remember { mutableStateOf(wiDService.readWiDListByDate(date))}

//    dateText = buildAnnotatedString {
//        date.let {
//            append(it.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 ")))
//            append("(")
//            withStyle(
//                style = SpanStyle(
//                    color = when (it.dayOfWeek) {
//                        DayOfWeek.SATURDAY -> Color.Blue
//                        DayOfWeek.SUNDAY -> Color.Red
//                        else -> Color.Unspecified
//                    }
//                )
//            ) {
//                append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
//            }
//            append(")")
//        }
//    }

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

                        wiDList = wiDService.readWiDListByDate(date)

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

                                duration = Duration.between(start, finish)
                                isDurationMinOrMax = Duration.ofHours(12) < duration || duration <= Duration.ZERO

                                for (existingWiD in wiDList) {
                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                        isStartOverlap = true
                                        isFinishOverlap = true
                                        break
                                    }
                                }

//                                if (date == today && currentTime < start) {
//                                    start = currentTime
//                                    finish = currentTime
//                                }
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

                                duration = Duration.between(start, finish)
                                isDurationMinOrMax = Duration.ofHours(12) < duration || duration <= Duration.ZERO

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

//                                if (date == today && currentTime < finish) {
//                                    finish = currentTime
//                                }
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
//                verticalArrangement = Arrangement.spacedBy(4.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val titleColorId = colorMap[title]
                val backgroundColor = if (titleColorId != null) {
                    Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                } else {
                    colorResource(id = R.color.light_gray)
                }

                Box(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                        )
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(0.dp, 8.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "날짜",
                        style = TextStyle(fontSize = 14.sp))
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
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

                    VerticalDivider()

                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { showDatePicker = true },
                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                        contentDescription = "showDatePicker")
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "제목",
                        style = TextStyle(fontSize = 14.sp))
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier
                        .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        contentDescription = "title")

//                    ExposedDropdownMenuBox(
//                        expanded = titleMenuExpanded,
//                        onExpandedChange = { titleMenuExpanded = !titleMenuExpanded }
//                    ) {
//                        Text(modifier = Modifier
//                            .padding(16.dp)
//                            .weight(1f)
//                            .menuAnchor(),
//                            text = titleMap[title] ?: title
//                        )
//
//                        ExposedDropdownMenu(
//                            expanded = titleMenuExpanded,
//                            onDismissRequest = { titleMenuExpanded = false }
//                        ) {
//                            titles.forEach { menuItem ->
//                                DropdownMenuItem(modifier = Modifier,
//                                    onClick = {
//                                        title = menuItem
//                                        titleMenuExpanded = false
//                                    },
//                                    text = {
//                                        Text(text = titleMap[menuItem] ?: menuItem)
//                                    },
//                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
//                                )
//                            }
//                        }
//                    }

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = titleMap[title] ?: title,
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

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = "시작",
                        style = TextStyle(fontSize = 14.sp))

                    if (isStartOverlap) {
                        Text(text = "다른 시간을 선택해 주세요.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
                    }
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
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
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_access_time_24),
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

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = "종료",
                        style = TextStyle(fontSize = 14.sp))

                    if (isFinishOverlap) {
                        Text(text = "다른 시간을 선택해 주세요.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
                    }
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
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
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_access_time_filled_24),
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

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = "경과",
                        style = TextStyle(fontSize = 14.sp))

                    if (isDurationMinOrMax) {
                        Text(text = "error",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
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
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "elapsedTime")

                    Text(modifier = Modifier
                        .padding(16.dp),
                        text = formatDuration(duration, mode = 2),
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "설명",
                        style = TextStyle(fontSize = 14.sp)
                    )

//                    Text(
//                        text = "(선택 사항)",
//                        style = TextStyle(fontSize = 14.sp)
//                    )
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
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
                            .padding(16.dp)
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
                    .padding(16.dp)
                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        date = today
                        title = "STUDY"
                        start = currentTime
                        finish = currentTime
                        duration = Duration.ZERO
                        detail = ""

                        isStartOverlap = false
                        isFinishOverlap = false
                        isDurationMinOrMax = false
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "초기화",
                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                    )
                }

                IconButton(
                    onClick = {
                        val newWiD = WiD(id = 0, date = date, title = title, start = start, finish = finish, duration = duration, detail = detail)
                        wiDService.createWiD(newWiD)

                        wiDList = wiDService.readWiDListByDate(date)

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
                        .weight(1f),
                    enabled = !(isStartOverlap || isFinishOverlap || isDurationMinOrMax)
                ) {
                    Text(text = "등록",
                        color = if (isStartOverlap || isFinishOverlap || isDurationMinOrMax)
                        { Color.Unspecified } else { colorResource(id = R.color.exercise) },
                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                    )
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