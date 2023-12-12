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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWiDFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 날짜
    val today: LocalDate = LocalDate.now()
    var date by remember { mutableStateOf(today) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() + (9 * 60 * 60 * 1000),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // utcTimeMillis는 KST를 반환하는 듯하고,
                // System.currentTimeMillis()가 (KST -9시간)을 반환하는 듯.
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

    // 시작 시간
    var start by remember { mutableStateOf(currentTime) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    var isStartOverlap by remember { mutableStateOf(false) }
    val isStartOverCurrentTime by remember(date, start) { mutableStateOf(date == today && currentTime < start) }
//    var startPosition by remember { mutableStateOf((start.hour * 60 + start.minute).toFloat() / totalMinutes) }

    // 종료 시간
    var finish by remember { mutableStateOf(currentTime) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute, is24Hour = false)
    var isFinishOverlap by remember { mutableStateOf(false) }
    val isFinishOverCurrentTime by remember(date, finish) { mutableStateOf(date == today && currentTime < finish) }
//    var finishPosition by remember { mutableStateOf((finish.hour * 60 + finish.minute).toFloat() / totalMinutes) }

    // 소요 시간
    val duration by remember(start, finish) { mutableStateOf(Duration.between(start, finish)) }
    val durationExist by remember(duration) { mutableStateOf(Duration.ZERO < duration) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    var wiDList by remember(date) { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
    var emptyWiDList by remember(date, wiDList) { mutableStateOf(createEmptyWiDListFromWiDList(date = date, currentTime = currentTime, wiDList = wiDList)) }
    val isTimeOverlap = isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        mainTopBottomBarVisible.value = true
    }

    fun isNewStartOverlap() { // 생성할 WiD의 시작 시간이 겹치는지 확인
        for (existingWiD in wiDList) {
            if (existingWiD.start < start && start < existingWiD.finish) {
                isStartOverlap = true
                break
            } else {
                isStartOverlap = false
            }
        }
    }

    fun isNewFinishOverlap() { // 생성할 WiD의 종료 시간이 겹치는지 확인
        for (existingWiD in wiDList) {
            if (existingWiD.start < finish && finish < existingWiD.finish) {
                isFinishOverlap = true
                break
            } else {
                isFinishOverlap = false
            }
        }
    }

    fun isNewWiDOverlap() { // 생성할 WiD가 기존의 WiD를 덮고 있는지 확인
        for (existingWiD in wiDList) {
            if (start < existingWiD.start && existingWiD.finish < finish) {
                isStartOverlap = true
                isFinishOverlap = true
                break
            }
        }
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
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            date = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()

                            // 날짜를 변경하면 이 Button scope의 내용이 먼저 실행된 후, remember에 의한 갱신이 발생한다.
                            wiDList = wiDService.readDailyWiDListByDate(date)

                            // wiDList가 비어 있으면, 시간이 겹칠 가능성이 없음.
                            if (wiDList.isEmpty()) {
                                isStartOverlap = false
                                isFinishOverlap = false
                            } else {
                                isNewStartOverlap()
                                isNewFinishOverlap()
                                isNewWiDOverlap()
                            }
                        }
                    ) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
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
                        .padding(
                            top = 28.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 12.dp
                        ),
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
//                                startPosition = (start.hour * 60 + start.minute).toFloat() / totalMinutes

                                if (wiDList.isEmpty()) {
                                    isStartOverlap = false
                                    isFinishOverlap = false
                                } else {
                                    isNewStartOverlap()
                                    isNewFinishOverlap()
                                    isNewWiDOverlap()
                                }
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
                        .padding(
                            top = 28.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 12.dp
                        ),
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

                                if (wiDList.isEmpty()) {
                                    isStartOverlap = false
                                    isFinishOverlap = false
                                } else {
                                    isNewStartOverlap()
                                    isNewFinishOverlap()
                                    isNewWiDOverlap()
                                }
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
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "새로운 WiD",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier
                        .clickable(!isTimeOverlap && durationExist) {
                            val newWiD = WiD(
                                id = 0,
                                date = date,
                                title = title,
                                start = start,
                                finish = finish,
                                duration = duration
                            )
                            wiDService.createWiD(newWiD)

                            // wiDList를 갱신해야 방금 생성한 WiD를 표시하고 사용할 수 있음.
                            wiDList = wiDService.readDailyWiDListByDate(date)
                            emptyWiDList = createEmptyWiDListFromWiDList(date = date, currentTime = currentTime, wiDList = wiDList)

                            isNewStartOverlap()
                            isNewFinishOverlap()
                            isNewWiDOverlap()
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_16),
                        contentDescription = "Create new WiD",
                        tint = if (!isTimeOverlap && durationExist) {
                            colorResource(id = R.color.deep_sky_blue)
                        } else {
                            Color.LightGray
                        }
                    )

                    Text(
                        text = "등록",
                        style = TextStyle(
                            color = if (!isTimeOverlap && durationExist) {
                                colorResource(id = R.color.deep_sky_blue)
                            } else {
                                Color.LightGray
                            }
                        )
                    )
                }
            }

            // 컨텐츠
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
            ) {
                // 가로 막대 차트
                item {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
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
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )

                            Text(
                                text = getDayString(date),
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }

                        if (wiDList.isEmpty()) {
                            createEmptyView(text = "표시할 그래프가 없습니다.")()
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                ) {
                                    StackedHorizontalBarChartFragment(wiDList = wiDList)
                                }
                            }
                        }
                    }
                }

                // New WiD
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "새로운 WiD",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 1.dp
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_calendar_today_16),
                                            contentDescription = "Date"
                                        )

                                        Text(text = getDayString(date))
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Show date picker",
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { titleMenuExpanded = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_title_16),
                                            contentDescription = "Title"
                                        )

                                        Text(text = titleMap[title] ?: title)

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
                                                .background(
                                                    color = colorResource(id = R.color.white),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            expanded = titleMenuExpanded,
                                            onDismissRequest = { titleMenuExpanded = false },
                                        ) {
                                            titles.forEach { menuTitle ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = titleMap[menuTitle] ?: menuTitle
                                                        )
                                                    },
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
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Show title menu",
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showStartPicker = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_alarm_16),
                                            contentDescription = "Start"
                                        )

                                        Text(text = formatTime(start, "a hh:mm:ss"))
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Show start picker",
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showFinishPicker = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_alarm_on_16),
                                            contentDescription = "Finish"
                                        )

                                        Text(text = formatTime(finish, "a hh:mm:ss"))
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Show finish picker",
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_timelapse_16),
                                        contentDescription = "Duration"
                                    )

                                    Text(text = formatDuration(duration, mode = 3))
                                }
                            }
                        }
                    }
                }

                // 선택 가능한 시간대
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "선택 가능한 시간대",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        if (emptyWiDList.isEmpty()) {
                            createEmptyView(text = "표시할 시간대가 없습니다.")()
                        } else {
                            emptyWiDList.forEach { emptyWiD: WiD ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    shadowElevation = 1.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                start = emptyWiD.start
                                                finish = emptyWiD.finish
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = colorResource(id = R.color.light_gray))
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .size(10.dp)
                                                        .background(Color.Black)
                                                )

                                                Text("제목 없음")
                                            }

                                            Icon(
                                                modifier = Modifier
                                                    .rotate(90f),
                                                painter = painterResource(id = R.drawable.baseline_exit_to_app_16),
                                                contentDescription = "Use this time interval",
                                                tint = colorResource(id = R.color.deep_sky_blue)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = formatTime(emptyWiD.start, "a hh:mm:ss"),
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    text = formatTime(emptyWiD.finish, "a hh:mm:ss"),
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )
                                            }

                                            Text(
                                                text = formatDuration(emptyWiD.duration, mode = 3),
                                                style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.pyeong_chang_peace_bold)))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                }
            }

            // 하단 바
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "임시 하단 바",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewWiDFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    NewWiDFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}