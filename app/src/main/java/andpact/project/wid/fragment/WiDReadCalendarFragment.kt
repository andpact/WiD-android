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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.text.style.TextOverflow
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
                TextField(modifier = Modifier
                    .menuAnchor(),
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
                .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "🗓️ 달력",
                        style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                    )

                    if (selectedTitle == "ALL") {
                        Text(text = "달력을 클릭하여 조회",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                        )
                    } else {
                        val backgroundColor = colorResource(id = colorMap[selectedTitle]!!)
                        val opacities = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)

                        Row {
                            Text(text = "0시간",
                                style = TextStyle(fontSize = 12.sp)
                            )

                            for (opacity in opacities) {
                                Box(modifier = Modifier
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

                            Text(text = "10시간",
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }

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
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                    ) {
                                        Text(modifier = Modifier
                                            .align(Alignment.Center),
                                            text = startDate.format(DateTimeFormatter.ofPattern("M월")),
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
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                        ) {
                                            Text(modifier = Modifier
                                                .align(Alignment.Center),
                                                text = currentDate.format(DateTimeFormatter.ofPattern("M월")),
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
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "📕 일(Day) 합계",
                                    style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                                )

                                val dayTotalText = buildAnnotatedString {
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

                                    append(") 기준")
                                }

                                Text(text = dayTotalText,
                                    style = TextStyle(fontSize = 12.sp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
                            ) {
                                Column(modifier = Modifier
                                    .padding(16.dp)
                                ) {
                                    if (dailyAllTitleDurationMap.isEmpty()) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_textsms_24),
                                                contentDescription = "No day total",
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
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(modifier = Modifier
                                                        .scale(0.8f),
                                                        painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                        contentDescription = "Day total title")

                                                    Text(text = "제목",
                                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                                    )

                                                    Text(text = titleMap[title] ?: title)

                                                    Box(modifier = Modifier
                                                        .clip(CircleShape)
                                                        .size(10.dp)
                                                        .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
                                                    )
                                                }

                                                Row(modifier = Modifier
                                                    .weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(modifier = Modifier
                                                        .scale(0.8f),
                                                        painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                        contentDescription = "Day total duration")

                                                    Text(text = "소요",
                                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                                    )

                                                    Text(modifier = Modifier.weight(1f),
                                                        text = formatDuration(dayTotal, mode = 2),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "📗 주(week) 합계",
                                    style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                                )

                                val weekTotalText = buildAnnotatedString {
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

                                    if (firstDayOfWeek.year != lastDayOfWeek.year) {
                                        append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
                                    } else if (firstDayOfWeek.month != lastDayOfWeek.month) {
                                        append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
                                    } else {
                                        append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일 (")))
                                    }

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

                                    append(") 기준")
                                }

                                Text(text = weekTotalText,
                                    style = TextStyle(fontSize = 12.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Surface(modifier = Modifier
                                .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
                            ) {
                                Column(modifier = Modifier
                                    .padding(16.dp)
                                ) {
                                    if (weeklyAllTitleDurationMap.isEmpty()) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_textsms_24),
                                                contentDescription = "No week total",
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
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(modifier = Modifier
                                                        .scale(0.8f),
                                                        painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                        contentDescription = "Week total title")

                                                    Text(
                                                        text = "제목",
                                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                                    )

                                                    Text(text = titleMap[title] ?: title)

                                                    Box(modifier = Modifier
                                                        .clip(CircleShape)
                                                        .size(10.dp)
                                                        .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
                                                    )
                                                }

                                                Row(modifier = Modifier
                                                    .weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(modifier = Modifier
                                                        .scale(0.8f),
                                                        painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                        contentDescription = "Week total duration")

                                                    Text(text = "소요",
                                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                                    )

                                                    Text(modifier = Modifier.weight(1f),
                                                        text = formatDuration(weekTotal, mode = 2),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "📘 월(Month) 합계",
                                    style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                                )

                                val monthText = buildAnnotatedString {
                                    append(selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 기준")))
                                }

                                Text(text = monthText,
                                    style = TextStyle(fontSize = 12.sp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(PaddingValues(bottom = 16.dp)), // 아래쪽 패딩을 설정해서 elevation이 보이도록 하고 여유 공간을 만듬
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
                            ) {
                                Column(modifier = Modifier
                                    .padding(16.dp)
                                ) {
                                    if (monthlyAllTitleDurationMap.isEmpty()) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_textsms_24),
                                                contentDescription = "No month total",
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
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(modifier = Modifier
                                                        .scale(0.8f),
                                                        painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                        contentDescription = "Month total title")

                                                    Text(
                                                        text = "제목",
                                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                                    )

                                                    Text(text = titleMap[title] ?: title)

                                                    Box(modifier = Modifier
                                                        .clip(CircleShape)
                                                        .size(10.dp)
                                                        .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
                                                    )
                                                }

                                                Row(modifier = Modifier
                                                    .weight(1f),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(modifier = Modifier
                                                        .scale(0.8f),
                                                        painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                        contentDescription = "Month total duration")

                                                    Text(text = "소요",
                                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                                    )

                                                    Text(modifier = Modifier.weight(1f),
                                                        text = formatDuration(monthTotal, mode = 2),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else { // selectedTitle이 "ALL"이 아닌 경우
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "1️⃣ 합계 기록",
                                style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
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
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_today_24),
                                                contentDescription = "Day total title")

                                            Text(text = "일(Day)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Day total duration")

                                            Text(text = if (dailyAllTitleDurationMap.isEmpty()) "기록 없음" else formatDuration(dailyAllTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 2),)
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_date_range_24),
                                                contentDescription = "Week total title")

                                            Text(text = "주(Week)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Week total duration")

                                            Text(text = if (weeklyAllTitleDurationMap.isEmpty()) "기록 없음" else formatDuration(weeklyAllTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 2),)
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                                contentDescription = "Month total title")

                                            Text(text = "월(Month)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Month total duration")

                                            Text(text = if (monthlyAllTitleDurationMap.isEmpty()) "기록 없음" else formatDuration(monthlyAllTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 2),)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "2️⃣ 평균 기록",
                                style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
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
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_date_range_24),
                                                contentDescription = "Week average title")

                                            Text(text = "주(Week)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Week average duration")

                                            Text(text = formatDuration(weeklyAverageTitleDuration, mode = 2))
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                                contentDescription = "Month average title")

                                            Text(text = "월(Month)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Month average duration")

                                            Text(text = formatDuration(monthlyAverageTitleDuration, mode = 2))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "3️⃣ 최고 기록",
                                style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
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
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_date_range_24),
                                                contentDescription = "Week max title")

                                            Text(text = "주(Week)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Week max duration")

                                            Text(text = formatDuration(weeklyMaxTitleDuration, mode = 2))
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                                contentDescription = "Month max title")

                                            Text(text = "월(Month)")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "Month max duration")

                                            Text(text = formatDuration(monthlyMaxTitleDuration, mode = 2))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "4️⃣ 연속 기록",
                                style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(PaddingValues(bottom = 16.dp)),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
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
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .rotate(90f)
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_vertical_align_top_24),
                                                contentDescription = "Current streak")

                                            Text(text = "현재 진행")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_event_available_24),
                                                contentDescription = "Current streak")

                                            Text(text = if (currentStreak == null) { "기록 없음" } else { "${ChronoUnit.DAYS.between(currentStreak, today) + 1}일" },)
                                        }
                                    }

                                    if (currentStreak != null) {
                                        Text(text = if (currentStreak == today) {
                                                "오늘"
                                            } else {
                                                "${currentStreak!!.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))} ~ 오늘"
                                            },
                                            style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                                        )
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .rotate(90f)
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_expand_24),
                                                contentDescription = "Longest streak")

                                            Text(text = "최장 기간")
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.baseline_event_available_24),
                                                contentDescription = "Longest streak")

                                            Text(text = if (longestStreak == null) { "기록 없음" } else { "${ChronoUnit.DAYS.between(longestStreak!!.first, longestStreak!!.second) + 1}일" },)
                                        }
                                    }

                                    if (longestStreak != null) {
                                        val longestStreakStartDate = longestStreak!!.first // 최장 기간 시작 날짜
                                        val longestStreakFinishDate = longestStreak!!.second // 최장 기간 종료 날짜

                                        Text(text = if (longestStreakStartDate == longestStreakFinishDate) {
                                                if (longestStreakStartDate == today && longestStreakFinishDate == today) {
                                                    "오늘"
                                                } else {
                                                        longestStreakStartDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
                                                }
                                            } else {
                                                val formattedStartDate = longestStreakStartDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
                                                val formattedFinishDate = if (longestStreakFinishDate == today) {
                                                    "오늘"
                                                } else if (longestStreakStartDate.year != longestStreakFinishDate.year) {
                                                    longestStreakFinishDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
                                                } else if (longestStreakStartDate.month != longestStreakFinishDate.month) {
                                                    longestStreakFinishDate.format(DateTimeFormatter.ofPattern("M월 d일"))
                                                } else {
                                                    longestStreakFinishDate.format(DateTimeFormatter.ofPattern("d일"))
                                                }
                                                "$formattedStartDate ~ $formattedFinishDate"
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
    }
}

@Preview(showBackground = true)
@Composable
fun WiDReadCalendarFragmentPreview() {
    WiDReadCalendarFragment()
}