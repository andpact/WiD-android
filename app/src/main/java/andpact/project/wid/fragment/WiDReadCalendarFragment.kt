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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
    var titleMenuExpanded by remember { mutableStateOf(false) }
    val titles = arrayOf("ALL", "STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var selectedTitle by remember { mutableStateOf("ALL") }

    val startDate by remember { mutableStateOf(LocalDate.now()) }
    val finishDate by remember { mutableStateOf(getDate1yearAgo(startDate)) }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val firstDayOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val lastDayOfWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    val wiDService = WiDService(context = LocalContext.current)

    var dailyWiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(selectedDate)) }
    var weeklyWiDList by remember { mutableStateOf(wiDService.readWeeklyWiDListByDate(selectedDate)) }
    var monthlyWiDList by remember { mutableStateOf(wiDService.readMonthlyWiDListByDate(selectedDate)) }

    val dailyTitleDurationMap = mutableMapOf<String, Duration>()
    val weeklyTitleDurationMap = mutableMapOf<String, Duration>()
    val monthlyTitleDurationMap = mutableMapOf<String, Duration>()

    for (wiD in dailyWiDList) {
        val title = wiD.title
        val duration = wiD.duration
        dailyTitleDurationMap[title] = (dailyTitleDurationMap[title] ?: Duration.ZERO) + duration
    }

    for (wiD in weeklyWiDList) {
        val title = wiD.title
        val duration = wiD.duration
        weeklyTitleDurationMap[title] = (weeklyTitleDurationMap[title] ?: Duration.ZERO) + duration
    }

    for (wiD in monthlyWiDList) {
        val title = wiD.title
        val duration = wiD.duration
        monthlyTitleDurationMap[title] = (monthlyTitleDurationMap[title] ?: Duration.ZERO) + duration
    }

    LaunchedEffect(selectedDate, selectedTitle) {
        if (selectedTitle == "ALL") {
            // Calculate and update dailyWiDList, weeklyWiDList, monthlyWiDList
            dailyWiDList = wiDService.readDailyWiDListByDate(selectedDate)
            weeklyWiDList = wiDService.readWeeklyWiDListByDate(selectedDate)
            monthlyWiDList = wiDService.readMonthlyWiDListByDate(selectedDate)
        } else {
            // Calculate and update dailyWiDList, weeklyWiDList, monthlyWiDList based on selectedTitle
            dailyWiDList = wiDService.readDailyWiDListByDate(selectedDate, selectedTitle)
            weeklyWiDList = wiDService.readWeeklyWiDListByDate(selectedDate, selectedTitle)
            monthlyWiDList = wiDService.readMonthlyWiDListByDate(selectedDate, selectedTitle)
        }

        // Calculate and update title duration maps
        dailyTitleDurationMap.clear()
        weeklyTitleDurationMap.clear()
        monthlyTitleDurationMap.clear()

        for (wiD in dailyWiDList) {
            val title = wiD.title
            val duration = wiD.duration
            dailyTitleDurationMap[title] = (dailyTitleDurationMap[title] ?: Duration.ZERO) + duration
        }

        for (wiD in weeklyWiDList) {
            val title = wiD.title
            val duration = wiD.duration
            weeklyTitleDurationMap[title] = (weeklyTitleDurationMap[title] ?: Duration.ZERO) + duration
        }

        for (wiD in monthlyWiDList) {
            val title = wiD.title
            val duration = wiD.duration
            monthlyTitleDurationMap[title] = (monthlyTitleDurationMap[title] ?: Duration.ZERO) + duration
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.weight(1f),
                text = "2023",
                textAlign = TextAlign.Center)

            Text(modifier = Modifier.weight(1f),
                text = "2022",
                textAlign = TextAlign.Center)

            ExposedDropdownMenuBox(modifier = Modifier.weight(1f),
                expanded = titleMenuExpanded,
                onExpandedChange = { titleMenuExpanded = !titleMenuExpanded },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
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
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                .aspectRatio(1f)
        ) {
            val daysDifference = ChronoUnit.DAYS.between(finishDate, startDate).toInt() + 1
            var dateIndex = 0
            var gridIndex = 0

            var previousMonth: Int? = null

            while (dateIndex < daysDifference) {
                val date = finishDate.plusDays(dateIndex.toLong())
                val currentMonth = date.monthValue
                val isSelected = date == selectedDate

                if (gridIndex % 8 == 0 && (previousMonth == null || currentMonth != previousMonth)) {
                    val monthText = if (currentMonth == 1) {
                        date.format(DateTimeFormatter.ofPattern("yyyy년 M월"))
                    } else {
                        date.format(DateTimeFormatter.ofPattern("M월"))
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            Text(
                                text = monthText,
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
                                    if (isSelected) Color.LightGray else Color.Unspecified
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

        if (selectedTitle != "ALL") {
            val titleColorId = colorMap[selectedTitle]!!
            val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
            val opacities = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "less",
                    style = TextStyle(fontSize = 12.sp))

                for (opacity in opacities) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(10.dp)
                            .background(
                                backgroundColor.copy(alpha = opacity),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }

                Text(text = "more",
                    style = TextStyle(fontSize = 12.sp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.light_gray),
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 25.dp)
            )

            Text(modifier = Modifier
                .weight(0.5f),
                text = "제목",
                textAlign = TextAlign.Center)

            Text(modifier = Modifier
                .weight(1f),
                text = selectedDate.format(DateTimeFormatter.ofPattern("M월")),
                textAlign = TextAlign.Center)

            Text(modifier = Modifier
                .weight(1f),
                text = "${firstDayOfWeek.format(DateTimeFormatter.ofPattern("d일"))} ~ " +
                        "${lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일"))}",
                textAlign = TextAlign.Center)

            Text(modifier = Modifier
                .weight(1f),
                text = selectedDate.format(DateTimeFormatter.ofPattern("d일")),
                textAlign = TextAlign.Center)
        }

        for ((title, monthTotal) in monthlyTitleDurationMap) {
            val weekTotal = weeklyTitleDurationMap[title] ?: Duration.ZERO
            val dayTotal = dailyTitleDurationMap[title] ?: Duration.ZERO

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = RoundedCornerShape(8.dp)
                    ),
            ) {
                val titleColorId = colorMap[title]
                if (titleColorId != null) {
                    val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 25.dp)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                            )
                    )
                }

                Text(
                    modifier = Modifier.weight(0.5f),
                    text = titleMap[title] ?: title,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = formatDuration(monthTotal, mode = 1),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = formatDuration(weekTotal, mode = 1),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = formatDuration(dayTotal, mode = 1),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}