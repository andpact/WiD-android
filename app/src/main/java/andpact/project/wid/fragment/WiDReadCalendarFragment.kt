package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.getDate1yearAgo
import andpact.project.wid.util.titleMap
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDReadCalendarFragment() {
    val wiDService = WiDService(context = LocalContext.current)

    val today = LocalDate.now()

    val yearList = listOf("지난 1년") + wiDService.getYearList()
    var selectedYear by remember { mutableStateOf(yearList[0]) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    val titles = arrayOf("ALL", "STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
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
                .padding(0.dp, 0.dp, 4.dp, 0.dp)
//                .border(
//                    BorderStroke(1.dp, Color.LightGray),
//                    shape = RoundedCornerShape(8.dp)
//                ),
            ) {
                items(yearList.size) { i ->
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 6.dp),
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

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            val daysOfWeek = listOf("날짜", "일", "월", "화", "수", "목", "금", "토")
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
//                            style = TextStyle(fontSize = 12.sp)
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
                                PieChartView(date = date, forReadDay = false)
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

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ) {
            Text(modifier = Modifier.align(Alignment.CenterStart),
                text = "달력 날짜를 클릭하여 조회",
                style = TextStyle(fontSize = 12.sp, color = Color.LightGray))

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

                Row(modifier = Modifier.align(Alignment.CenterEnd)
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

                    Text(text = "10시간",
                        style = TextStyle(fontSize = 12.sp))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (selectedTitle == "ALL") {
                Text(text = "날짜 및 기간별 기록",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(10.dp)
                        )

                        Text(
                            modifier = Modifier.weight(0.5f),
                            text = "제목",
                            textAlign = TextAlign.Center,
                        )

                        Text(modifier = Modifier
                            .weight(1f),
                            text = selectedDate.format(DateTimeFormatter.ofPattern("d일")),
                            textAlign = TextAlign.Center)

                        Text(modifier = Modifier
                            .weight(1f),
                            text = "${firstDayOfWeek.format(DateTimeFormatter.ofPattern("d일"))} ~ " +
                                    lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일")),
                            textAlign = TextAlign.Center)

                        Text(modifier = Modifier
                            .weight(1f),
                            text = selectedDate.format(DateTimeFormatter.ofPattern("M월")),
                            textAlign = TextAlign.Center)
                    }

                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        item {
                            if (monthlyTitleDurationMap.isEmpty()) {
                                Text(
                                    text = "표시할 데이터가 없습니다.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = TextStyle(textAlign = TextAlign.Center, color = Color.LightGray)
                                )
                            } else {
                                for ((title, monthTotal) in monthlyTitleDurationMap) {
                                    val weekTotal = weeklyTitleDurationMap[title] ?: Duration.ZERO
                                    val dayTotal = dailyTitleDurationMap[title] ?: Duration.ZERO

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                            val titleColorId = colorMap[title]
                                            if (titleColorId != null) {
                                                val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(color = backgroundColor)
                                                )
                                            }

                                        Text(
                                            modifier = Modifier.weight(0.5f),
                                            text = titleMap[title] ?: title,
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = formatDuration(dayTotal, mode = 1),
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = formatDuration(weekTotal, mode = 1),
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = formatDuration(monthTotal, mode = 1),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = "날짜 및 기간별 기록",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("d일")),
                        )

                        Text(
                            text = if (monthlyTitleDurationMap.isEmpty()) "기록 없" else formatDuration(dailyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )


                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${firstDayOfWeek.format(DateTimeFormatter.ofPattern("d일"))} ~ " +
                                    lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일")),
                        )

                        Text(
                            text = if (monthlyTitleDurationMap.isEmpty()) "기록 없" else formatDuration(weeklyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("M월")),
                        )

                        Text(
                            text = if (monthlyTitleDurationMap.isEmpty()) "기록 없" else formatDuration(monthlyTitleDurationMap[selectedTitle] ?: Duration.ZERO, mode = 1),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(text = "연속 기록",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "최장 기간")

                        Text(
                            text = if (longestStreak == null) { "0일" } else { "${ChronoUnit.DAYS.between(longestStreak!!.first, longestStreak!!.second) + 1}일" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (longestStreak == null) {
                                "기간 없음"
                            } else {
                                val finishDate = longestStreak!!.first
                                val startDate = longestStreak!!.second
                                val formattedStartDate = finishDate.format(DateTimeFormatter.ofPattern("M월 d일"))
                                val formattedFinishDate = if (startDate == today) {
                                    "오늘"
                                } else if ((startDate.month == finishDate.month)) {
                                    startDate.format(DateTimeFormatter.ofPattern("d일"))
                                } else {
                                    startDate.format(DateTimeFormatter.ofPattern("M월 d일"))
                                }
                                "$formattedStartDate ~ $formattedFinishDate"
                            },
                            style = TextStyle(color = Color.Gray)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "현재 진행 중")

                        Text(
                            text = if (currentStreak == null) { "0일" } else { "${ChronoUnit.DAYS.between(currentStreak, today) + 1}일" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (currentStreak == null) {
                                "기간 없음"
                            } else if (currentStreak == today) {
                                "오늘"
                            } else {
                                "${currentStreak!!.format(DateTimeFormatter.ofPattern("M월 d일"))} ~ 오늘"
                            },
                            style = TextStyle(color = Color.Gray)
                        )
                    }
                }

                Text(text = "종합 기록",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "총")

                        Text(
                            text = "${totalDaysAndDuration?.first ?: 0}일",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (totalDaysAndDuration == null) "날짜 없음" else formatDuration(
                                totalDaysAndDuration!!.second, mode = 1),
                            style = TextStyle(color = Color.Gray)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "최고")

                        Text(text = if (bestDateAndDuration == null) "날짜 없음" else bestDateAndDuration!!.second.format(DateTimeFormatter.ofPattern("M월 d일")),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold)

                        Text(text = if (bestDateAndDuration == null) "기록 없음" else formatDuration(
                            bestDateAndDuration!!.first, mode = 1),
                            style = TextStyle(color = Color.Gray)
                        )
                    }
                }
            }
        }
    }
}