package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 날짜
    val today: LocalDate = LocalDate.now()
    var date by remember { mutableStateOf(today) }
    var isDateAssigned by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // System.currentTimeMillis()가 9시간 전의 시간을 가져오는 듯.
                return utcTimeMillis <= System.currentTimeMillis() + (9 * 60 * 60 * 1000)
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = LocalDate.now().year
                return year <= currentYear
            }
        }
    )
    val currentTime: LocalTime = LocalTime.now().withSecond(0)
//    val totalMinutes = 24 * 60 // 1440분 (24시간)

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("STUDY") }
    var isTitleAssigned by remember { mutableStateOf(false) }

    // 시작 시간
    var start by remember { mutableStateOf(currentTime) }
    var isStartAssigned by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    var isStartOverlap by remember { mutableStateOf(false) }
//    var isStartOverCurrentTime by remember { mutableStateOf(false) }
    var isStartOverCurrentTime by remember(start) { mutableStateOf(date == today && currentTime < start) }
//    var startPosition by remember { mutableStateOf((start.hour * 60 + start.minute).toFloat() / totalMinutes) }

    // 종료 시간
    var finish by remember { mutableStateOf(currentTime) }
    var isFinishAssigned by remember { mutableStateOf(false) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    var isFinishOverlap by remember { mutableStateOf(false) }
//    var isFinishOverCurrentTime by remember { mutableStateOf(false) }
    var isFinishOverCurrentTime by remember(finish) { mutableStateOf(date == today && currentTime < finish) }
//    var finishPosition by remember { mutableStateOf((finish.hour * 60 + finish.minute).toFloat() / totalMinutes) }

    // 소요 시간
//    var duration by remember { mutableStateOf(Duration.ZERO) }
    var duration by remember(start, finish) { mutableStateOf(Duration.between(start, finish)) }
//    var durationExist by remember { mutableStateOf(false) }
    var durationExist by remember(duration) { mutableStateOf(Duration.ZERO < duration) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    var wiDList by remember(date) { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
    val isWiDAssigned = isDateAssigned && isTitleAssigned && isStartAssigned && isFinishAssigned && durationExist
    val isTimeOverlap = isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
    ) {
        // 날짜 선택
        if (showDatePicker) {
            DatePickerDialog(
                shape = RoundedCornerShape(8.dp),
                colors = DatePickerDefaults.colors(containerColor = Color.White),
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()

//                        wiDList = wiDService.readDailyWiDListByDate(date)

                        isDateAssigned = true

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

        // 시작 시간 선택
        if (showStartPicker) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
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

                                isStartAssigned = true

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

//                                isStartOverCurrentTime = date == today && currentTime < start

//                                duration = Duration.between(start, finish)
//                                durationExist = Duration.ZERO < duration
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
                        color = Color.White,
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

                                isFinishAssigned = true

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

//                                isFinishOverCurrentTime = date == today && currentTime < finish

//                                duration = Duration.between(start, finish)
//                                durationExist = Duration.ZERO < duration
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        shape = RectangleShape,
                        onClick = {
                            navController.popBackStack()
                            mainTopBottomBarVisible.value = true
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Manual create",
                            tint = Color.Black
                        )
                    }

                    Text(text = "직접 입력")
                }

                TextButton(
                    onClick = {
                        val newWiD = WiD(
                            id = 0,
                            date = date,
                            title = title,
                            start = start,
                            finish = finish,
                            duration = duration
                        )
                        wiDService.createWiD(newWiD)

                        // wiDList를 불러와야 방금 생성한 WiD를 표시할 수 있음.
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
                    shape = RectangleShape,
                    enabled = !isTimeOverlap && isWiDAssigned
                ) {
                    Text(
                        text = "등록",
                        style = TextStyle(
                            color = if (!isTimeOverlap && isWiDAssigned) {
                                colorResource(id = R.color.lime_green)
                            } else {
                                Color.LightGray
                            }
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
            ) {
                // 가로 막대 차트
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "시간 그래프",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                            )
                        )

                        if (isDateAssigned) {
                            Text(
                                text = getDayString(date),
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
        //                    val configuration = LocalConfiguration.current
        //                    val screenWidthDp = configuration.screenWidthDp
        //                    val density = LocalDensity.current.density
        //                    val screenWidthPixels = (screenWidthDp * density).toInt()
        //
        //                    Box(modifier = Modifier
        //                        .fillMaxWidth()
        //                    ) {
        //                        Icon(
        //                            modifier = Modifier
        //                                .scale(0.8f)
        //                                .rotate(90f)
        //                                .offset(y = -(screenWidthDp * startPosition * 0.8).dp),
        //                            painter = painterResource(id = R.drawable.outline_play_arrow_24),
        //                            contentDescription = "startPosition",
        //                            tint = if (isStartOverlap || isStartOverCurrentTime) Color.Red else Color.Unspecified
        //                        )
        //
        //                        Icon(
        //                            modifier = Modifier
        //                                .scale(0.8f)
        //                                .rotate(90f)
        //                                .offset(y = -(screenWidthDp * finishPosition * 0.8).dp),
        //                            painter = painterResource(id = R.drawable.baseline_play_arrow_24),
        //                            contentDescription = "finishPosition",
        //                            tint = if (isFinishOverlap || isFinishOverCurrentTime) Color.Red else Color.Unspecified)
        //                    }

                            if (isDateAssigned) {
                                HorizontalBarChartView(wiDList = wiDList)
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                                    contentDescription = "No selected date",
                                    tint = Color.Gray
                                )

                                Text(
                                    text = "날짜를 선택해 주세요.",
                                    style = TextStyle(color = Color.Gray)
                                )
                            }
                        }
                    }
                }

                // New WiD
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "새로운 WiD",
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
                                    .fillMaxWidth()
                                    .clickable { showDatePicker = true }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .scale(0.8f)
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                                    contentDescription = "Date",
                                    tint = if (isDateAssigned) Color.Unspecified else Color.Gray
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = if (isDateAssigned) getDayString(date) else buildAnnotatedString { append("날짜") },
                                    color = if (isDateAssigned) Color.Unspecified else Color.Gray
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { titleMenuExpanded = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.outline_subtitles_24),
                                    contentDescription = "Title",
                                    tint = if (isTitleAssigned) Color.Unspecified else Color.Gray
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = if (isTitleAssigned) titleMap[title] ?: title else "제목",
                                    color = if (isTitleAssigned) Color.Unspecified else Color.Gray
                                )

                                if (isTitleAssigned) {
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
                                }

                                DropdownMenu(
                                    modifier = Modifier
                                        .background(
                                            color = colorResource(id = R.color.white),
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    expanded = titleMenuExpanded,
                                    onDismissRequest = { titleMenuExpanded = false },
                                ) {
                                    titles.forEach { menuTitle ->
                                        DropdownMenuItem(
                                            text = { Text(text = titleMap[menuTitle] ?: menuTitle) },
                                            onClick = {
                                                title = menuTitle
                                                isTitleAssigned = true
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
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showStartPicker = true }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.outline_play_arrow_24),
                                    contentDescription = "Start",
                                    tint = if (isStartAssigned) Color.Unspecified else Color.Gray
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = if (isStartAssigned) formatTime(start, "a h:mm") else "시작",
                                    color = if (isStartAssigned) Color.Unspecified else Color.Gray
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showFinishPicker = true }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                    contentDescription = "Finish",
                                    tint = if (isFinishAssigned) Color.Unspecified else Color.Gray
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f),
                                    text = if (isFinishAssigned) formatTime(finish, "a h:mm") else "종료",
                                    color = if (isFinishAssigned) Color.Unspecified else Color.Gray
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                    contentDescription = "Duration",
                                    tint = if (durationExist && isStartAssigned && isFinishAssigned) Color.Unspecified else Color.Gray
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = if (durationExist && isStartAssigned && isFinishAssigned) formatDuration(duration, mode = 2) else "소요",
                                    color = if (durationExist && isStartAssigned && isFinishAssigned) Color.Unspecified else Color.Gray
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
fun ManualFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    ManualFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}