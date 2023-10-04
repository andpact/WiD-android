package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
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
    var date: LocalDate by remember { mutableStateOf(Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var title by remember { mutableStateOf("STUDY") }

    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    var start: LocalTime by remember { mutableStateOf(currentTime) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = currentTime.hour, initialMinute = currentTime.minute)
    var isStartOverlap by remember { mutableStateOf(false) }

    var finish: LocalTime by remember { mutableStateOf(currentTime) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = currentTime.hour, initialMinute = currentTime.minute)
    var isFinishOverlap by remember { mutableStateOf(false) }

    var duration: Duration by remember { mutableStateOf(Duration.between(start, finish)) }
    var isDurationMinOrMax by remember { mutableStateOf(false) }

    var detail by remember { mutableStateOf("") }

    var wiDList: List<WiD>

    fun updateWiDListAndOverlapFlags() {
        wiDList = wiDService.readWiDListByDate(date)

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

    updateWiDListAndOverlapFlags()

    Box(modifier = Modifier
        .fillMaxSize())
    {
        if (showDatePicker) {
            DatePickerDialog(
//                modifier = Modifier
//                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()
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
//                    modifier = Modifier
//                        .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
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
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
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
                                val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute).withSecond(0)

                                if (newStart.isAfter(finish)) {
                                    finish = newStart
                                }

                                start = newStart

                                val today = LocalDate.now()
                                if (date == today && currentTime < start) {
                                    start = currentTime
                                    finish = currentTime
                                }

                                updateWiDListAndOverlapFlags()
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
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
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
                                val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute).withSecond(0)

                                if (newFinish.isBefore(start)) {
                                    start = newFinish
                                }

                                finish = newFinish

                                val today = LocalDate.now()
                                if (date == today && currentTime < finish) {
                                    finish = currentTime
                                }

                                updateWiDListAndOverlapFlags()
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
                    .padding(16.dp)
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val titleColorId = colorMap[title]
                if (titleColorId != null) {
                    val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                    Box(
                        modifier = Modifier
                            .height(10.dp)
                            .fillMaxWidth()
                            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                    )
                }

                Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier.padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "date")

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.clickable {
                            showDatePicker = true
                        },
//                            text = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd ")),
                            text = date.format(DateTimeFormatter.ofPattern("M월 d일 ")),
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )

                        Text(text = "(",
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )

                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                            color = when (date.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            },
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )

                        Text(text = ")",
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )
                    }
                }

                Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier.padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        contentDescription = "title")

                    Text(
                        modifier = Modifier.clickable {
                            titleMenuExpanded = true
                        }
                            .padding(16.dp)
                            .fillMaxWidth(),
                        text = titleMap[title] ?: title,
                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                    )

                    DropdownMenu(modifier = Modifier
                        .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(8.dp)),
                        offset = DpOffset(150.dp, 0.dp),
                        expanded = titleMenuExpanded,
                        onDismissRequest = {
                            titleMenuExpanded = false
                        }
                    ) {
                        titles.forEach { menuItem ->
                            DropdownMenuItem(
                                onClick = {
                                    title = menuItem
                                    titleMenuExpanded = true
                                },
                                text = {
                                    Text(text = titleMap[menuItem] ?: menuItem)
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier.padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_access_time_24),
                        contentDescription = "start")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.clickable {showStartPicker = true },
                            text = start.format(DateTimeFormatter.ofPattern("a h:mm")),
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )

                        if (isStartOverlap) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_priority_high_24),
                                contentDescription = "StartOverlap",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = Color.Red
                            )
                        }
                    }
                }

                Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier.padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_access_time_filled_24),
                        contentDescription = "finish")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.clickable { showFinishPicker = true },
                            text = finish.format(DateTimeFormatter.ofPattern("a h:mm")),
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )

                        if (isFinishOverlap) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_priority_high_24),
                                contentDescription = "FinishOverlap",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = Color.Red
                            )
                        }
                    }
                }

                Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier.padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "elapsedTime")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatDuration(duration, mode = 2),
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                        )

                        if (isDurationMinOrMax) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_priority_high_24),
                                contentDescription = "DurationMinOrMax",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = Color.Red
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(0.dp, 0.dp, 0.dp, 10.dp),
//                        .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp)),
                    value = detail,
                    onValueChange = { newText ->
                        detail = newText
                    },
                    placeholder = {
                        Text(text = "설명 입력..",
                            textAlign = TextAlign.Center)
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            painter = painterResource(id = R.drawable.baseline_message_24),
                            contentDescription = "detail")
                    }
                )
            }

            Row(
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newWiD = WiD(id = 0, date = date, title = title, start = start, finish = finish, duration = duration, detail = detail)
                        wiDService.createWiD(newWiD)

//                        wiDList = wiDService.readWiDListByDate(date)

                        updateWiDListAndOverlapFlags()
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = !(isStartOverlap || isFinishOverlap || isDurationMinOrMax)
                ) {
                    Text(text = "등록",
                        color = if (isStartOverlap || isFinishOverlap || isDurationMinOrMax) { Color.Unspecified } else { colorResource(id = R.color.exercise) },
                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
                    )
                }

                IconButton(
                    onClick = {
                        date = LocalDate.now()
                        title = "STUDY"
                        start = currentTime
                        finish = currentTime
                        duration = Duration.ZERO
                        detail = ""

//                        updateWiDListAndOverlapFlags()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "초기화",
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