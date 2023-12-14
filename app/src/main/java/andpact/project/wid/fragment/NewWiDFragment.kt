package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    // 화면
    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
            state = lazyColumnState
        ) {
            // item 1(타임 라인)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_history_24),
                        contentDescription = "타임 라인"
                    )

                    Column {
                        Text(text = "${getDayString(date)}의 타임 라인")

                        if (wiDList.isEmpty()) {
                            Text(
                                text = "표시할 타임라인이 없습니다.",
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
                        } else {
                            StackedHorizontalBarChartFragment(wiDList = wiDList)
                        }
                    }
                }
            }

            // item 2
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 3
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
//                                if (!showDatePicker) {
//                                    coroutineScope.launch {
//                                        lazyColumnState.animateScrollToItem(1)
//                                        delay(1500)
//                                    }
//                                }
//                                showDatePicker = !showDatePicker

                            // 컨텐츠가 충분해서 스크롤 할 수 있어야 스크롤 메서드가 사용가능하다?
                            showDatePicker = !showDatePicker
                            if (showDatePicker) {
                                coroutineScope.launch {
//                                    delay(200)
                                    lazyColumnState.animateScrollToItem(1)
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "Date"
                    )

                    Column {
                        Text("날짜")

                        Text(
                            text = getDayString(date),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (showDatePicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show date picker",
                    )
                }

                AnimatedVisibility(
                    visible = showDatePicker,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            title = null,
                            headline = null,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text(text = "취소")
                            }

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
                        }
                    }
                }
            }

            // item 4
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 5
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            titleMenuExpanded = !titleMenuExpanded
                            if (titleMenuExpanded) {
                                coroutineScope.launch {
//                                        delay(100)
                                    lazyColumnState.animateScrollToItem(3)
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_title_24),
                        contentDescription = "Title"
                    )

                    Column {
                        Text("제목")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = titleMap[title] ?: title,
                                style = TextStyle(fontWeight = FontWeight.Bold)
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
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (titleMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show title menu",
                    )
                }

                AnimatedVisibility(
                    visible = titleMenuExpanded,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
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
                                            style = TextStyle(textAlign = TextAlign.Center)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }

            // item 6
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 7
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showStartPicker = !showStartPicker
                            if (showStartPicker) {
                                coroutineScope.launch {
//                                    delay(100)
                                    lazyColumnState.animateScrollToItem(5)
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_alarm_24),
                        contentDescription = "Start"
                    )

                    Column {
                        Text("시작")

                        Text(
                            text = formatTime(start, "a hh:mm:ss"),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (showStartPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show start picker",
                    )
                }

                AnimatedVisibility(
                    visible = showStartPicker,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
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
                                onClick = { showStartPicker = false }
                            ) {
                                Text(text = "취소")
                            }

                            TextButton(
                                onClick = {
                                    showStartPicker = false
                                    val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                    start = newStart
                                }
                            ) {
                                Text(text = "확인")
                            }
                        }
                    }
                }
            }

            // item 8
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 9
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showFinishPicker = !showFinishPicker
                            if (showFinishPicker) {
                                coroutineScope.launch {
//                                    delay(100)
                                    lazyColumnState.animateScrollToItem(7) // Finish Picker가 화면에 나타나는 도중에 스크롤이 동작하면 에러가 나는 듯.
                                }
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                        contentDescription = "Finish"
                    )

                    Column {
                        Text("종료")

                        Text(
                            text = formatTime(finish, "a hh:mm:ss"),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (showFinishPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Show finish picker",
                    )
                }

                AnimatedVisibility(
                    visible = showFinishPicker,
//                    enter = expandVertically{ 0 },
                    enter = expandVertically(animationSpec = tween(500)) { 0 },
//                    exit = shrinkVertically{ 0 },
                    exit = shrinkVertically(animationSpec = tween(500)) { 0 },
                ) {
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
                                onClick = { showFinishPicker = false }
                            ) {
                                Text(text = "취소")
                            }

                            TextButton(
                                onClick = {
                                    showFinishPicker = false
                                    val newFinish = LocalTime.of(
                                        finishTimePickerState.hour,
                                        finishTimePickerState.minute
                                    )

                                    finish = newFinish
                                }
                            ) {
                                Text(text = "확인")
                            }
                        }
                    }
                }
            }

            // item 10
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 11
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "Duration"
                    )

                    Column {
                        Text("소요")

                        Text(
                            text = formatDuration(duration, mode = 3),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // item 11
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            // item 12(선택 가능한 시간 단위)
            item {
                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "선택 가능한 시간 단위",
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

@Preview(showBackground = true)
@Composable
fun NewWiDFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    NewWiDFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}