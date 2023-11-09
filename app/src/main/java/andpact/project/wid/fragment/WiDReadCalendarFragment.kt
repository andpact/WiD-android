package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDReadCalendarFragment() {
    val wiDService = WiDService(context = LocalContext.current)
    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)
    val today = LocalDate.now()

    // 기간 선택(지난 1년, 2023, 2023 ... ) 용 변수
    val yearList = listOf("지난 1년") + wiDService.getYearList()
    var selectedYear by remember { mutableStateOf(yearList[0]) }

    // 제목 선택(전체, 공부, 노동, ... ) 용 변수
    var titleMenuExpanded by remember { mutableStateOf(false) }
    val titles = arrayOf("ALL") + titles
    var selectedTitle by remember { mutableStateOf("ALL") }

    // WiD List를 불러오기 위한 변수
    var finishDate by remember { mutableStateOf(today) }
    var startDate by remember { mutableStateOf(getDate1yearAgo(finishDate)) }
    var wiDList by remember { mutableStateOf(wiDService.readWiDListByDateRange(startDate, finishDate)) }

    // 특정 기간 및 날짜의 데이터(합계, 평균, 최고, 연속..)를 조회하기 위한 변수
    var selectedDate by remember { mutableStateOf(today) }
    val firstDayOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val lastDayOfWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    // 합계 기록 용 (전체, 각 제목 용), 최초 화면에 필요한 Map, 전체 제목에 사용되니까 selectedTitle 파라미터 필요 없음.
    val dailyAllTitleDurationMap = getDailyAllTitleDurationMap(date = selectedDate, wiDList = wiDList)
    val weeklyAllTitleDurationMap = getWeeklyAllTitleDurationMap(date = selectedDate, wiDList = wiDList)
    val monthlyAllTitleDurationMap = getMonthlyAllTitleDurationMap(date = selectedDate, wiDList = wiDList)

    // 최고 기록 용 (각 제목 용), 각 제목에 사용되니까 selectedTitle 파라미터 필요함.
    val weeklyMaxTitleDuration = getWeeklyMaxTitleDuration(date = selectedDate, wiDList = wiDList, title = selectedTitle)
    val monthlyMaxTitleDuration = getMonthlyMaxTitleDuration(date = selectedDate, wiDList = wiDList, title = selectedTitle)

    // 평균 기록 용 (각 제목 용)
    val weeklyAverageTitleDuration = getWeeklyAverageTitleDuration(date = selectedDate, wiDList = wiDList, title = selectedTitle)
    val monthlyAverageTitleDuration = getMonthlyAverageTitleDuration(date = selectedDate, wiDList = wiDList, title = selectedTitle)

    // 연속 기록 용 (각 제목 용)
    var longestStreak by remember { mutableStateOf(wiDService.getLongestStreak(selectedTitle, startDate, finishDate)) }
    var currentStreak by remember { mutableStateOf(wiDService.getCurrentStreak(selectedTitle, getDate1yearAgo(today), today)) }

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(modifier = Modifier
                .weight(2f)
//                .padding(0.dp, 0.dp, 4.dp, 0.dp)
//                .border(
//                    BorderStroke(1.dp, Color.LightGray),
//                    shape = RoundedCornerShape(8.dp)
//                ),
            ) {
                items(yearList.size) { i ->
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        shape = CircleShape,
                        selected = (selectedYear == yearList[i]),
                        onClick = {
                            selectedYear = yearList[i]

                            if (selectedYear == "지난 1년") {
                                finishDate = today
                                startDate = getDate1yearAgo(finishDate)
                            } else if (selectedYear.matches("\\d{4}".toRegex())) {
                                val year = selectedYear.toInt()
                                finishDate = LocalDate.of(year, 12, 31)
                                startDate = LocalDate.of(year, 1, 1)
                            }

                            wiDList = wiDService.readWiDListByDateRange(startDate, finishDate)

                            longestStreak = wiDService.getLongestStreak(selectedTitle, startDate, finishDate)
                        },
                        label = {
                            Text(text = yearList[i])
                        },
                        leadingIcon = if (selectedYear == yearList[i]) {
                            { Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                ) }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = colorResource(id = R.color.light_gray)),
                    )
                }
            }

            ExposedDropdownMenuBox(modifier = Modifier
                .weight(1f),
//                .background(color = colorResource(id = R.color.light_gray)),
//                .border(
//                    BorderStroke(1.dp, Color.LightGray),
//                    shape = RoundedCornerShape(8.dp)
//                ),
                expanded = titleMenuExpanded,
                onExpandedChange = { titleMenuExpanded = !titleMenuExpanded },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
//                    shape = RectangleShape,
                    readOnly = true,
                    value = if (selectedTitle == "ALL") { "전체" } else { titleMap[selectedTitle] ?: "공부" },
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                ExposedDropdownMenu(
                    expanded = titleMenuExpanded,
                    onDismissRequest = { titleMenuExpanded = false },
                ) {
                    titles.forEach { title ->
                        DropdownMenuItem(
                            text = { Text( if (title == "ALL") { "전체" } else { titleMap[title] ?: "공부" }) },
                            onClick = {
                                selectedTitle = title
                                titleMenuExpanded = false

                                longestStreak = wiDService.getLongestStreak(selectedTitle, startDate, finishDate)
                                currentStreak = wiDService.getCurrentStreak(selectedTitle, getDate1yearAgo(today), today)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        // 달력 및 각종 기록
        Column(modifier = Modifier
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 달력
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                Text(text = "달력",
                    style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            val daysOfWeek = listOf("날짜") + daysOfWeek
                            daysOfWeek.forEachIndexed { index, day ->
                                val textColor = when (index) {
                                    1 -> Color.Red  // "일"의 인덱스는 1
                                    7 -> Color.Blue // "토"의 인덱스는 7
                                    else -> Color.Unspecified
                                }

                                Text(
                                    text = day,
                                    modifier = Modifier.weight(1f),
                                    style = TextStyle(textAlign = TextAlign.Center, color = textColor)
                                )
                            }
                        }

                        LazyVerticalGrid(
                            state = lazyGridState,
                            columns = GridCells.Fixed(8),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            val daysDifference = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
                            var dateIndex = 0
                            var gridIndex = 0

                            var previousMonth: Int? = null

                            if (startDate.dayOfWeek.value != 7) {
                                // 시작 날짜가 일요일(7)이 아니면 월 표시를 넣고 시작함.
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                    ) {
                                        Text(
                                            text = startDate.format(DateTimeFormatter.ofPattern("M월")),
                                            modifier = Modifier.align(Alignment.Center),
                                            style = TextStyle(fontSize = 12.sp)
                                        )
                                    }
                                }
                                gridIndex++

                                repeat(startDate.dayOfWeek.value % 7) {
                                    item {
                //                        EmptyPieChartView()
                                    }
                                    gridIndex++
                                }

                                previousMonth = startDate.monthValue
                            }

                            while (dateIndex < daysDifference) {
                                val currentDate = startDate.plusDays(dateIndex.toLong())
                                val currentMonth = currentDate.monthValue
                                val isSelected = currentDate == selectedDate

                                val filteredWiDList = if (selectedTitle == "ALL") {
                                    wiDList.filter { it.date == currentDate }
                                } else {
                                    wiDList.filter { it.date == currentDate && it.title == selectedTitle }
                                }

                                if (gridIndex % 8 == 0 && (previousMonth == null || currentMonth != previousMonth)) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f)
                                        ) {
                                            Text(
                                                text = currentDate.format(DateTimeFormatter.ofPattern("M월")),
                                                modifier = Modifier.align(Alignment.Center),
                                                style = TextStyle(fontSize = 12.sp)
                                            )
                                        }
                                    }
                                    previousMonth = currentMonth
                                } else if (gridIndex % 8 == 0) {
                                    item {
                //                        EmptyPieChartView()
                                    }
                                } else {
                                    item {
                                        Box(modifier = Modifier
                                            .clickable {
                                                selectedDate = currentDate
                                            }
                                            .border(
                                                BorderStroke(
                                                    1.dp,
                                                    color = if (isSelected) Color.Blue else Color.Unspecified
                                                ),
                                                RoundedCornerShape(8.dp)
                                            )
                                        ) {
                                            val chartView = when (selectedTitle) {
                                                "ALL" -> CalendarPieChartView(date = currentDate, wiDList = filteredWiDList)
                                                else -> OpacityChartView(date = currentDate, wiDList = filteredWiDList)
                                            }

                                            chartView
                                        }
                                    }
                                    dateIndex++
                                }
                                gridIndex++
                            }
                        }
                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = "달력을 클릭하여 조회",
                        style = TextStyle(fontSize = 12.sp, color = Color.Gray))

                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = R.drawable.baseline_drag_handle_24),
                        contentDescription = "Drag handle",
                        tint = Color.LightGray
                    )

                    if (selectedTitle != "ALL") {
                        val backgroundColor = colorResource(id = colorMap[selectedTitle]!!)
                        val opacities = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)

                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(text = "0시간",
                                style = TextStyle(fontSize = 12.sp))

                            for (opacity in opacities) {
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(10.dp)
                                        .border(
                                            BorderStroke(1.dp, Color.LightGray),
                                            RoundedCornerShape(2.dp)
                                        )
                                        .background(
                                            backgroundColor.copy(alpha = opacity),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                            }

                            Text(
//                                text = if (selectedTitle == "EXERCISE") "2시간" else "10시간",
                                text = "10시간",
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }
            }

            // 각종 기록
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                if (selectedTitle == "ALL") {
                    item {
                        val dayTotalText = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))) {
                                append("일(Day) 합계")
                            }

                            append(selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

                            withStyle(
                                style = SpanStyle(
                                    color = when (selectedDate.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Black
                                    }
                                )
                            ) {
                                append(selectedDate.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                            }

                            append(")")
                        }

                        Text(text = dayTotalText)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(modifier = Modifier
                                .padding(16.dp)
                            ) {
                                if (dailyAllTitleDurationMap.isEmpty()) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_textsms_24),
                                            contentDescription = "detail",
                                            tint = Color.Gray
                                        )

                                        Text(text = "표시할 데이터가 없습니다.",
                                            style = TextStyle(color = Color.Gray)
                                        )
                                    }
                                } else {
                                    for ((title, dayTotal) in dailyAllTitleDurationMap) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier
                                                .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                    contentDescription = "title")

                                                Text(
                                                    text = "제목 ",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    text = titleMap[title] ?: title,
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 10.dp, height = 25.dp)
                                                        .background(
                                                            color = colorResource(
                                                                id = colorMap[title]
                                                                    ?: R.color.light_gray
                                                            ),
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            BorderStroke(1.dp, Color.LightGray),
                                                            shape = RoundedCornerShape(8.dp)
                                                        ),
                                                )
                                            }

                                            Row(modifier = Modifier
                                                .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                    contentDescription = "duration")

                                                Text(
                                                    text = "소요 ",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    modifier = Modifier.weight(1f),
                                                    text = formatDuration(dayTotal, mode = 1),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val weekTotalText = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))) {
                                append("주(week) 합계")
                            }

                            append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

                            withStyle(
                                style = SpanStyle(
                                    color = when (firstDayOfWeek.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Black
                                    }
                                )
                            ) {
                                append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                            }

                            append(") ~ ")

                            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))

                            withStyle(
                                style = SpanStyle(
                                    color = when (lastDayOfWeek.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Black
                                    }
                                )
                            ) {
                                append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                            }

                            append(")")
                        }

                        Text(text = weekTotalText)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(modifier = Modifier
                                .padding(16.dp)
                            ) {
                                if (weeklyAllTitleDurationMap.isEmpty()) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_textsms_24),
                                            contentDescription = "detail",
                                            tint = Color.Gray
                                        )

                                        Text(text = "표시할 데이터가 없습니다.",
                                            style = TextStyle(color = Color.Gray)
                                        )
                                    }
                                } else {
                                    for ((title, weekTotal) in weeklyAllTitleDurationMap) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier
                                                .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                    contentDescription = "title")

                                                Text(
                                                    text = "제목 ",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    text = titleMap[title] ?: title,
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 10.dp, height = 25.dp)
                                                        .background(
                                                            color = colorResource(
                                                                id = colorMap[title]
                                                                    ?: R.color.light_gray
                                                            ),
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            BorderStroke(1.dp, Color.LightGray),
                                                            shape = RoundedCornerShape(8.dp)
                                                        ),
                                                )
                                            }

                                            Row(modifier = Modifier
                                                .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                    contentDescription = "duration")

                                                Text(
                                                    text = "소요 ",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    modifier = Modifier.weight(1f),
                                                    text = formatDuration(weekTotal, mode = 1),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val monthText = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))) {
                                append("월(Month) 합계")
                            }

                            append(selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월")))
                        }

                        Text(text = monthText)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 0.dp, 0.dp, 16.dp),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(modifier = Modifier
                                .padding(16.dp)
                            ) {
                                if (monthlyAllTitleDurationMap.isEmpty()) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_textsms_24),
                                            contentDescription = "detail",
                                            tint = Color.Gray
                                        )

                                        Text(text = "표시할 데이터가 없습니다.",
                                            style = TextStyle(color = Color.Gray)
                                        )
                                    }
                                } else {
                                    for ((title, monthTotal) in monthlyAllTitleDurationMap) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier
                                                .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                    contentDescription = "title")

                                                Text(
                                                    text = "제목 ",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    text = titleMap[title] ?: title,
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 10.dp, height = 25.dp)
                                                        .background(
                                                            color = colorResource(
                                                                id = colorMap[title]
                                                                    ?: R.color.light_gray
                                                            ),
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            BorderStroke(1.dp, Color.LightGray),
                                                            shape = RoundedCornerShape(8.dp)
                                                        ),
                                                )
                                            }

                                            Row(modifier = Modifier
                                                .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                    contentDescription = "duration")

                                                Text(
                                                    text = "소요 ",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(
                                                    modifier = Modifier.weight(1f),
                                                    text = formatDuration(monthTotal, mode = 1),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else { // selectedTitle이 "ALL"이 아닌 경우
                    item {
                        Text(
                            text = "합계 기록",
                            style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "일(Day)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "title")

                                        Text(
                                            text = if (dailyAllTitleDurationMap.isEmpty()) "•••" else formatDuration(dailyAllTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "주(Week)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "title")

                                        Text(
                                            text = if (weeklyAllTitleDurationMap.isEmpty()) "•••" else formatDuration(weeklyAllTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "월(Month)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "title")

                                        Text(
                                            text = if (monthlyAllTitleDurationMap.isEmpty()) "•••" else formatDuration(monthlyAllTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "평균 기록",
                            style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "주(Week)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "title")

                                        Text(
                                            text = formatDuration(weeklyAverageTitleDuration, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "monthlyMaxTitleDuration")

                                        Text(
                                            text = "월(Month)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "monthlyMaxTitleDuration")

                                        Text(
                                            text = formatDuration(monthlyAverageTitleDuration, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "최고 기록",
                            style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "주(Week)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "title")

                                        Text(
                                            text = formatDuration(weeklyMaxTitleDuration, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "monthlyMaxTitleDuration")

                                        Text(
                                            text = "월(Month)",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                            contentDescription = "monthlyMaxTitleDuration")

                                        Text(
                                            text = formatDuration(monthlyMaxTitleDuration, mode = 1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "연속 기록",
                            style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 0.dp, 0.dp, 16.dp),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "current streak")

                                        Text(
                                            text = "현재 진행",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "current streak")

                                        Text(
                                            text = if (currentStreak == null) { "0일" } else { "${ChronoUnit.DAYS.between(currentStreak, today) + 1}일" },
                                        )
                                    }
                                }

                                Text(
                                    text = if (currentStreak == null) {
                                        "•••"
                                    } else if (currentStreak == today) {
                                        "오늘"
                                    } else {
                                        "${currentStreak!!.format(DateTimeFormatter.ofPattern("M월 d일"))} ~ 오늘"
                                    },
                                    style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                                )

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "longest streak")

                                        Text(
                                            text = "최장 기간",
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                                            contentDescription = "longest streak")

                                        Text(
                                            text = if (longestStreak == null) { "0일" } else { "${ChronoUnit.DAYS.between(longestStreak!!.first, longestStreak!!.second) + 1}일" },
                                        )
                                    }
                                }

                                Text(
                                    text = if (longestStreak == null) {
                                        "•••"
                                    } else {
                                        val longestStreakFinishDate = longestStreak!!.first
                                        val longestStreakStartDate = longestStreak!!.second
                                        if (longestStreakStartDate == longestStreakFinishDate) {
                                            longestStreakFinishDate.format(DateTimeFormatter.ofPattern("M월 d일"))
                                        } else {
                                            val formattedStartDate = longestStreakFinishDate.format(DateTimeFormatter.ofPattern("M월 d일"))
                                            val formattedFinishDate = if (longestStreakStartDate == today) {
                                                "오늘"
                                            } else if ((longestStreakStartDate.month == longestStreakFinishDate.month)) {
                                                longestStreakStartDate.format(DateTimeFormatter.ofPattern("d일"))
                                            } else {
                                                longestStreakStartDate.format(DateTimeFormatter.ofPattern("M월 d일"))
                                            }
                                            "$formattedStartDate ~ $formattedFinishDate"
                                        }
                                    },
                                    style = TextStyle(color = Color.Gray, fontSize = 12.sp)
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
fun WiDReadCalendarFragmentPreview() {
    WiDReadCalendarFragment()
}