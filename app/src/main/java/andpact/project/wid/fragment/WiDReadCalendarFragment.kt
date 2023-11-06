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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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

    val today = LocalDate.now()

    val yearList = listOf("지난 1년") + wiDService.getYearList()
    var selectedYear by remember { mutableStateOf(yearList[0]) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    val titles = arrayOf("ALL") + titles
    var selectedTitle by remember { mutableStateOf("ALL") }

    var finishDate by remember { mutableStateOf(today) }
    var startDate by remember { mutableStateOf(getDate1yearAgo(finishDate)) }

    var selectedDate by remember { mutableStateOf(today) }
    val firstDayOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val lastDayOfWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    val dailyTitleDurationMap = wiDService.getDailyTitleDurationMap(selectedDate)
    val weeklyTitleDurationMap = wiDService.getWeeklyTitleDurationMap(selectedDate)
    val monthlyTitleDurationMap = wiDService.getMonthlyTitleDurationMap(selectedDate)

    var longestStreak by remember { mutableStateOf(wiDService.getLongestStreak(selectedTitle, startDate, finishDate)) }
    var currentStreak by remember { mutableStateOf(wiDService.getCurrentStreak(selectedTitle, getDate1yearAgo(today),today)) }

    var totalDaysAndDuration by remember { mutableStateOf(wiDService.getTotalDaysAndDuration(selectedTitle, startDate, finishDate)) }
    var bestDateAndDuration by remember { mutableStateOf(wiDService.getBestDateAndDuration(selectedTitle, startDate, finishDate)) }

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
                .border(
                    BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(8.dp)
                ),
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

                            longestStreak = wiDService.getLongestStreak(selectedTitle, startDate, finishDate)

                            totalDaysAndDuration = wiDService.getTotalDaysAndDuration(selectedTitle, startDate, finishDate)
                            bestDateAndDuration = wiDService.getBestDateAndDuration(selectedTitle, startDate, finishDate)
                        },
                        label = {
                            Text(text = yearList[i])
                        },
                        leadingIcon = if (selectedYear == yearList[i]) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
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

                                totalDaysAndDuration = wiDService.getTotalDaysAndDuration(selectedTitle, startDate, finishDate)
                                bestDateAndDuration = wiDService.getBestDateAndDuration(selectedTitle, startDate, finishDate)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

//        HorizontalDivider()

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
                .weight(0.6f)
                .border(
                    BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            val daysDifference = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
            var dateIndex = 0
            var gridIndex = 0

            var previousMonth: Int? = null

            if (startDate.dayOfWeek.value != 7) {
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
                val date = startDate.plusDays(dateIndex.toLong())
                val currentMonth = date.monthValue
                val isSelected = date == selectedDate

                if (gridIndex % 8 == 0 && (previousMonth == null || currentMonth != previousMonth)) {
//                    val monthText = if (currentMonth == 1) {
//                        date.format(DateTimeFormatter.ofPattern("yyyy년 M월"))
//                    } else {
//                        date.format(DateTimeFormatter.ofPattern("M월"))
//                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            Text(
//                                text = monthText,
                                text = date.format(DateTimeFormatter.ofPattern("M월")),
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
                }
                else {
                    item {
                        Box(modifier = Modifier
                            .clickable {
                                selectedDate = date
                            }
                            .border(
                                BorderStroke(
                                    1.dp,
                                    color = if (isSelected) Color.Blue else Color.Unspecified
                                ),
                                RoundedCornerShape(8.dp)
                            )
                        ) {
                            if (selectedTitle == "ALL") {
                                PieChartView(date = date)
                            } else {
                                OpacityChartView(date = date, title = selectedTitle)
                            }
                        }
                    }
                    dateIndex++
                }
                gridIndex++
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
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
                val titleColorId = colorMap[selectedTitle]!!
                val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
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
                        text = if (selectedTitle == "EXERCISE") "2시간" else "10시간",
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (selectedTitle == "ALL") {
                item {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                    ) {
                        Text(
                            text = "일(Day) 합계",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
                            style = TextStyle(fontSize = 14.sp)
                        )

                        Text(text = "(",
                            style = TextStyle(fontSize = 14.sp))

                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                            style = TextStyle(fontSize = 14.sp),
                            color = when (selectedDate.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            },
                        )

                        Text(text = ")",
                            style = TextStyle(fontSize = 14.sp))
                    }
                }

                item {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                    ) {
                        if (dailyTitleDurationMap.isEmpty()) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_message_24),
                                    contentDescription = "detail")

                                Text(text = "표시할 데이터가 없습니다.")
                            }
                        } else {
                            for ((title, dayTotal) in dailyTitleDurationMap) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.baseline_category_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "제목",
                                            style = TextStyle(fontWeight = FontWeight.Bold)
                                        )

                                        Text(
                                            text = titleMap[title] ?: title,
                                        )

                                        val titleColorId = colorMap[title]
                                        if (titleColorId != null) {
                                            val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 10.dp, height = 25.dp)
                                                    .background(
                                                        color = backgroundColor,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .border(
                                                        BorderStroke(1.dp, Color.LightGray),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ),
                                            )
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.baseline_timelapse_24),
                                            contentDescription = "duration")

                                        Text(
                                            text = "소요",
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

                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        Text(
                            text = "주(week) 합계",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
                            style = TextStyle(fontSize = 14.sp)
                        )

                        Text(text = "(",
                            style = TextStyle(fontSize = 14.sp))

                        Text(
                            text = firstDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                            style = TextStyle(fontSize = 14.sp),
                            color = when (firstDayOfWeek.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            },
                        )

                        Text(text = ")",
                            style = TextStyle(fontSize = 14.sp))

                        Text(text = "~",
                            style = TextStyle(fontSize = 14.sp))

                        Text(
                            text = lastDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일")),
                            style = TextStyle(fontSize = 14.sp)
                        )

                        Text(text = "(",
                            style = TextStyle(fontSize = 14.sp))

                        Text(
                            text = lastDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                            style = TextStyle(fontSize = 14.sp),
                            color = when (lastDayOfWeek.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            },
                        )

                        Text(text = ")",
                            style = TextStyle(fontSize = 14.sp))
                    }
                }

                item {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                    ) {
                        if (weeklyTitleDurationMap.isEmpty()) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_message_24),
                                    contentDescription = "detail")

                                Text(text = "표시할 데이터가 없습니다.")
                            }
                        } else {
                            for ((title, weekTotal) in weeklyTitleDurationMap) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.baseline_category_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "제목",
                                            style = TextStyle(fontWeight = FontWeight.Bold)
                                        )

                                        Text(
                                            text = titleMap[title] ?: title,
                                        )

                                        val titleColorId = colorMap[title]
                                        if (titleColorId != null) {
                                            val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 10.dp, height = 25.dp)
                                                    .background(
                                                        color = backgroundColor,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .border(
                                                        BorderStroke(1.dp, Color.LightGray),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ),
                                            )
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.baseline_timelapse_24),
                                            contentDescription = "duration")

                                        Text(
                                            text = "소요",
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

                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        Text(
                            text = "월(Month) 합계",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }
                }

                item {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                    ) {
                        if (monthlyTitleDurationMap.isEmpty()) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_message_24),
                                    contentDescription = "detail")

                                Text(text = "표시할 데이터가 없습니다.")
                            }
                        } else {
                            for ((title, monthTotal) in monthlyTitleDurationMap) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.baseline_category_24),
                                            contentDescription = "title")

                                        Text(
                                            text = "제목",
                                            style = TextStyle(fontWeight = FontWeight.Bold)
                                        )

                                        Text(
                                            text = titleMap[title] ?: title,
                                        )

                                        val titleColorId = colorMap[title]
                                        if (titleColorId != null) {
                                            val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 10.dp, height = 25.dp)
                                                    .background(
                                                        color = backgroundColor,
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .border(
                                                        BorderStroke(1.dp, Color.LightGray),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ),
                                            )
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .weight(1f)
                                    ) {
                                        Icon(modifier = Modifier
                                            .scale(0.8f),
                                            painter = painterResource(id = R.drawable.baseline_timelapse_24),
                                            contentDescription = "duration")

                                        Text(
                                            text = "소요",
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
            } else { // selectedTitle이 "ALL"이 아닌 경우
                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        Text(
                            text = "합계 기록",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(1.dp, Color.LightGray),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier
                                .weight(1f)
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_category_24),
                                    contentDescription = "title")

                                Text(
                                    text = "일(Day)",
                                )
                            }

                            Row(modifier = Modifier
                                .weight(1f)
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_category_24),
                                    contentDescription = "title")

                                Text(
                                    text = if (dailyTitleDurationMap.isEmpty()) "•••" else formatDuration(dailyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier
                                .weight(1f)
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_category_24),
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
                                    painter = painterResource(id = R.drawable.baseline_category_24),
                                    contentDescription = "title")

                                Text(
                                    text = if (weeklyTitleDurationMap.isEmpty()) "•••" else formatDuration(weeklyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier
                                .weight(1f)
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_category_24),
                                    contentDescription = "title")

                                Text(
                                    text = "월(Month)",
                                )
                            }

                            Row(modifier = Modifier
                                .weight(1f)
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.baseline_category_24),
                                    contentDescription = "title")

                                Text(
                                    text = if (monthlyTitleDurationMap.isEmpty()) "•••" else formatDuration(monthlyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        Text(
                            text = "평균 기록",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }

//                item {
//                    Text(
//                        modifier = Modifier
//                        .padding(0.dp, 0.dp, 0.dp, 8.dp),
//                        text = "날짜 및 기간별 기록",
//                        style = TextStyle(fontWeight = FontWeight.Bold)
//                    )
//                }
//
//                item {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .border(
//                                BorderStroke(1.dp, Color.LightGray),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .padding(8.dp),
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                text = selectedDate.format(DateTimeFormatter.ofPattern("d일")),
//                            )
//
//                            Text(
//                                text = if (monthlyTitleDurationMap.isEmpty()) "•••" else formatDuration(dailyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                text = "${firstDayOfWeek.format(DateTimeFormatter.ofPattern("d일"))} ~ " +
//                                        lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일")),
//                            )
//
//                            Text(
//                                text = if (monthlyTitleDurationMap.isEmpty()) "•••" else formatDuration(weeklyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                text = selectedDate.format(DateTimeFormatter.ofPattern("M월")),
//                            )
//
//                            Text(
//                                text = if (monthlyTitleDurationMap.isEmpty()) "•••" else formatDuration(monthlyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    }
//                }
//
//                item {
//                    Text(modifier = Modifier
//                        .padding(vertical = 8.dp),
//                        text = "연속 기록",
//                        style = TextStyle(fontWeight = FontWeight.Bold)
//                    )
//                }
//
//                item {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .border(
//                                BorderStroke(1.dp, Color.LightGray),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .padding(8.dp),
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(text = "최장 기간")
//
//                            Text(
//                                text = if (longestStreak == null) { "0일" } else { "${ChronoUnit.DAYS.between(longestStreak!!.first, longestStreak!!.second) + 1}일" },
//                                fontWeight = FontWeight.Bold
//                            )
//
//                            Text(
//                                text = if (longestStreak == null) {
//                                    "•••"
//                                } else {
//                                    val longestStreakFinishDate = longestStreak!!.first
//                                    val longestStreakStartDate = longestStreak!!.second
//                                    if (longestStreakStartDate == longestStreakFinishDate) {
//                                        longestStreakFinishDate.format(DateTimeFormatter.ofPattern("M월 d일"))
//                                    } else {
//                                        val formattedStartDate = longestStreakFinishDate.format(DateTimeFormatter.ofPattern("M월 d일"))
//                                        val formattedFinishDate = if (longestStreakStartDate == today) {
//                                            "오늘"
//                                        } else if ((longestStreakStartDate.month == longestStreakFinishDate.month)) {
//                                            longestStreakStartDate.format(DateTimeFormatter.ofPattern("d일"))
//                                        } else {
//                                            longestStreakStartDate.format(DateTimeFormatter.ofPattern("M월 d일"))
//                                        }
//                                        "$formattedStartDate ~ $formattedFinishDate"
//                                    }
//                                },
//                                style = TextStyle(color = Color.Gray)
//                            )
//                        }
//
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(text = "현재 진행 중")
//
//                            Text(
//                                text = if (currentStreak == null) { "0일" } else { "${ChronoUnit.DAYS.between(currentStreak, today) + 1}일" },
//                                fontWeight = FontWeight.Bold
//                            )
//
//                            Text(
//                                text = if (currentStreak == null) {
//                                    "•••"
//                                } else if (currentStreak == today) {
//                                    "오늘"
//                                } else {
//                                    "${currentStreak!!.format(DateTimeFormatter.ofPattern("M월 d일"))} ~ 오늘"
//                                },
//                                style = TextStyle(color = Color.Gray)
//                            )
//                        }
//                    }
//                }
//
//                item {
//                    Text(modifier = Modifier
//                        .padding(vertical = 8.dp),
//                        text = "종합 기록",
//                        style = TextStyle(fontWeight = FontWeight.Bold)
//                    )
//                }
//
//                item {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .border(
//                                BorderStroke(1.dp, Color.LightGray),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .padding(8.dp),
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(text = "총")
//
//                            Text(
//                                text = "${totalDaysAndDuration?.first ?: 0}일",
//                                fontWeight = FontWeight.Bold
//                            )
//
//                            Text(
//                                text = if (totalDaysAndDuration == null) "•••" else formatDuration(
//                                    totalDaysAndDuration!!.second, mode = 1),
//                                style = TextStyle(color = Color.Gray)
//                            )
//                        }
//
//                        Column(
//                            modifier = Modifier
//                                .weight(1f),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(text = "최고")
//
//                            Text(text = if (bestDateAndDuration == null) "•••" else bestDateAndDuration!!.second.format(DateTimeFormatter.ofPattern("M월 d일")),
//                                fontWeight = FontWeight.Bold)
//
//                            Text(text = if (bestDateAndDuration == null) "•••" else formatDuration(
//                                bestDateAndDuration!!.first, mode = 1),
//                                style = TextStyle(color = Color.Gray)
//                            )
//                        }
//                    }
//                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDReadCalendarFragmentPreview() {
    WiDReadCalendarFragment()
}