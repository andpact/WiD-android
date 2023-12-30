package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
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
import kotlin.text.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWiDFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 날짜
    val today: LocalDate = LocalDate.now()
    var date by remember { mutableStateOf(today) }
    var expandDatePicker by remember { mutableStateOf(false) }
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
    var expandStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute, is24Hour = false)
    var isStartOverlap by remember { mutableStateOf(false) }
    val isStartOverCurrentTime by remember(date, start) { mutableStateOf(date == today && currentTime < start) }
//    var startPosition by remember { mutableStateOf((start.hour * 60 + start.minute).toFloat() / totalMinutes) }

    // 종료 시간
    var finish by remember { mutableStateOf(currentTime) }
    var expandFinishPicker by remember { mutableStateOf(false) }
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
            // 등호를 넣어서 부등호를 사용해야 기존의 WiD를 덮고 있는지를 정확히 확인할 수 있다.
            if (start <= existingWiD.start && existingWiD.finish <= finish) {
                isStartOverlap = true
                isFinishOverlap = true
                break
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        /**
         * 상단 바
         */
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
                style = Typography.titleLarge
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
                    contentDescription = "새 WiD 등록",
                    tint = if (!isTimeOverlap && durationExist) {
                        DeepSkyBlue
                    } else {
                        LightGray
                    }
                )

                Text(
                    text = "등록",
                    style = Typography.bodyMedium,
                    color = if (!isTimeOverlap && durationExist) {
                        DeepSkyBlue
                    } else {
                        LightGray
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
                .background(GhostWhite)
                .weight(1f),
            state = lazyColumnState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 정보 입력
            item {
                Column(
                    modifier = Modifier
                        .background(White)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "정보 입력",
                        style = Typography.titleMedium
                    )

                    // 날짜
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandDatePicker = !expandDatePicker
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp),
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

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        Icon(
                            imageVector = if (expandDatePicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "날짜 선택 도구 펼치기",
                        )
                    }

                    if (expandDatePicker) {
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
                                    onClick = { expandDatePicker = false }
                                ) {
                                    Text(
                                        text = "취소",
                                        style = Typography.bodyMedium,
                                        color = Black
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        expandDatePicker = false
                                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()

                                        // 날짜를 변경하면 이 Button scope의 내용이 먼저 실행된 후, remember에 의한 갱신이 발생한다.
                                        wiDList = wiDService.readDailyWiDListByDate(date)

                                        // WiD를 생성했으므로 wiDList가 비어있을 수가 없음.
                                        isNewStartOverlap()
                                        isNewFinishOverlap()
                                        isNewWiDOverlap()
                                    }
                                ) {
                                    Text(
                                        text = "확인",
                                        style = Typography.bodyMedium,
                                        color = Black
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )

                    // 제목
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                titleMenuExpanded = !titleMenuExpanded
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            painter = painterResource(id = R.drawable.baseline_title_24),
                            contentDescription = "제목",
                            tint = colorMap[title] ?: Black
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
                            contentDescription = "제목 메뉴 펼치기"
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
                                            containerColor = LightGray,
                                            labelColor = Black,
                                            selectedContainerColor = Black,
                                            selectedLabelColor = White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )

                    // 시작 시간
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandStartPicker = !expandStartPicker
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp),
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
                                        style = Typography.bodyMedium,
                                        color = Black
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        expandStartPicker = false
                                        val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                        start = newStart

                                        isNewStartOverlap()
                                        isNewWiDOverlap()
                                    }
                                ) {
                                    Text(
                                        text = "확인",
                                        style = Typography.bodyMedium,
                                        color = Black
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )

                    // 종료 시간
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandFinishPicker = !expandFinishPicker
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp),
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
                                        style = Typography.bodyMedium,
                                        color = Black
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        expandFinishPicker = false
                                        val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute)

                                        finish = newFinish

                                        isNewFinishOverlap()
                                        isNewWiDOverlap()
                                    }
                                ) {
                                    Text(
                                        text = "확인",
                                        style = Typography.bodyMedium,
                                        color = Black
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )

                    // 소요 시간
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp),
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
                                style = Typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // 타임 라인
            item {
                Column(
                    modifier = Modifier
                        .background(White)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "${getDayString(date)}의 타임 라인",
                        style = Typography.titleMedium
                    )

                    if (wiDList.isEmpty()) {
                        createEmptyView(text = "표시할 타임 라인이 없습니다.")()
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 32.dp)
                        ) {
                            StackedHorizontalBarChartFragment(wiDList = wiDList)
                        }
                    }
                }
            }

            // 선택 가능한 시간 단위
            item {
                Column(
                    modifier = Modifier
                        .background(White)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "선택 가능한 시간 단위",
                        style = Typography.titleMedium
                    )

                    if (emptyWiDList.isEmpty()) {
                        createEmptyView(text = "표시할 시간대가 없습니다.")()
                    } else {
                        emptyWiDList.forEachIndexed { index, emptyWiD: WiD ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        start = emptyWiD.start
                                        finish = emptyWiD.finish

                                        isNewStartOverlap()
                                        isNewFinishOverlap()
                                        isNewWiDOverlap()
                                    }
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp, 10.dp)
                                                .background(Black)
                                        )

                                        Text(
                                            text = "제목 없음",
                                            style = Typography.bodyMedium
                                        )
                                    }

                                    Icon(
                                        modifier = Modifier
                                            .rotate(90f),
                                        painter = painterResource(id = R.drawable.baseline_exit_to_app_16),
                                        contentDescription = "이 시간대 사용하기",
                                        tint = DeepSkyBlue
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = formatTime(emptyWiD.start, "a hh:mm:ss"),
                                            style = Typography.bodyMedium,
                                        )

                                        Text(
                                            text = formatTime(emptyWiD.finish, "a hh:mm:ss"),
                                            style = Typography.bodyMedium,
                                        )
                                    }

                                    Text(
                                        text = formatDuration(emptyWiD.duration, mode = 3),
                                        fontFamily = pyeongChangPeaceBold,
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            if (index < emptyWiDList.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
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
fun NewWiDFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    NewWiDFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}