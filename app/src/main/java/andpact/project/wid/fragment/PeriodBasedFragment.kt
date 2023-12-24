package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.pyeongChangPeaceBold
import andpact.project.wid.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodBasedFragment() {
    // 날짜
    val today = LocalDate.now()
    var startDate by remember { mutableStateOf(getFirstDayOfWeek(today)) }
    var finishDate by remember { mutableStateOf(getLastDayOfWeek(today)) }

    // 제목
    var selectedTitle by remember { mutableStateOf(titlesWithAll[0]) }
    var titleMenuExpanded by remember { mutableStateOf(false) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList by remember(startDate, finishDate) { mutableStateOf(wiDService.readWiDListByDateRange(startDate, finishDate)) }
    val filteredWiDListByTitle by remember(wiDList, selectedTitle) { mutableStateOf(wiDList.filter { it.title == selectedTitle }) }

    // 기간
    var selectedPeriod by remember { mutableStateOf(periods[0]) }
    var periodMenuExpanded by remember { mutableStateOf(false) }

    // 합계
    val totalDurationMap by remember(wiDList) { mutableStateOf(getTotalDurationMapByTitle(wiDList = wiDList)) }

    // 평균
    val averageDurationMap by remember(wiDList) { mutableStateOf(getAverageDurationMapByTitle(wiDList = wiDList)) }

    // 최고
    val maxDurationMap by remember(wiDList) { mutableStateOf(getMaxDurationMapByTitle(wiDList = wiDList)) }

    // 맵
    var selectedMapText by remember { mutableStateOf("합계") }
    var selectedMap by remember(wiDList) { mutableStateOf(totalDurationMap) }

//    LaunchedEffect(Unit) {
//        titleBottomSheetState.hide()
//        periodBottomSheetState.hide()
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.ghost_white))
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTitle == titlesWithAll[0]) {
                item("타임라인") {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = when (selectedPeriod) {
                                periods[0] -> getWeekString(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                                periods[1] -> getMonthString(date = startDate)
                                else -> buildAnnotatedString { append("") }
                            },
                            style = Typography.titleMedium
                        )

                        if (wiDList.isEmpty()) {
                            createEmptyView(text = "표시할 타임라인이 없습니다.")()
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val daysOfWeek = if (selectedPeriod == periods[0]) daysOfWeekFromMonday else daysOfWeekFromSunday

                                daysOfWeek.forEachIndexed { index, day ->
                                    val textColor = when (index) {
                                        0 -> if (selectedPeriod == periods[1]) Color.Red else Color.Unspecified
                                        5 -> if (selectedPeriod == periods[0]) Color.Blue else Color.Unspecified
                                        6 -> if (selectedPeriod == periods[0]) Color.Red else if (selectedPeriod == periods[1]) Color.Blue else Color.Unspecified
                                        else -> Color.Unspecified
                                    }

                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = day,
                                        style = TextStyle(
                                            textAlign = TextAlign.Center,
                                            color = textColor
                                        )
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )

                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp, 8.dp, 16.dp, 0.dp)
                                    .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                                columns = GridCells.Fixed(7)
                            ) {
                                if (selectedPeriod == periods[1]) {
                                    items(startDate.dayOfWeek.value % 7) {
                                        // selectedPeriod가 한달이면 달력의 빈 칸을 생성해줌.
                                    }
                                }

                                items(
                                    ChronoUnit.DAYS.between(startDate, finishDate)
                                        .toInt() + 1
                                ) { index: Int ->
                                    val indexDate = startDate.plusDays(index.toLong())
                                    val filteredWiDListByDate =
                                        wiDList.filter { it.date == indexDate }

                                    PeriodBasedPieChartFragment(
                                        date = indexDate,
                                        wiDList = filteredWiDListByDate
                                    )
                                }
                            }
                        }
                    }
                }

                item("합계, 평균, 최고") {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedMapText 기록",
                                style = Typography.titleMedium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            selectedMapText = "합계"
                                            selectedMap = totalDurationMap
                                        },
                                    text = "합계",
                                    style = Typography.bodyMedium,
                                    color = if (selectedMapText == "합계") Color.Black else Color.LightGray
                                )

                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            selectedMapText = "평균"
                                            selectedMap = averageDurationMap
                                        },
                                    text = "평균",
                                    style = Typography.bodyMedium,
                                    color = if (selectedMapText == "평균") Color.Black else Color.LightGray
                                )

                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            selectedMapText = "최고"
                                            selectedMap = maxDurationMap
                                        },
                                    text = "최고",
                                    style = Typography.bodyMedium,
                                    color = if (selectedMapText == "최고") Color.Black else Color.LightGray
                                )
                            }
                        }

                        if (selectedMap.isEmpty()) {
                            createEmptyView(text = "표시할 $selectedMapText 기록이 없습니다.")()
                        } else {
                            selectedMap.entries.forEachIndexed { index, (title, duration) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = titleMap[title] ?: title,
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontFamily = pyeongChangPeaceBold
                                        )
                                    )

                                    Text(
                                        text = formatDuration(duration, mode = 3),
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontFamily = pyeongChangPeaceBold
                                        )
                                    )
                                }

                                if (index < selectedMap.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item("기록률") {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = "기록률",
                            style = Typography.titleMedium
                        )

                        if (wiDList.isEmpty()) {
                            createEmptyView(text = "표시할 기록률이 없습니다.")()
                        } else {
                            VerticalBarChartFragment(
                                wiDList = wiDList,
                                startDate = startDate,
                                finishDate = finishDate
                            )
                        }
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = when (selectedPeriod) {
                                periods[0] -> getWeekString(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                                periods[1] -> getMonthString(date = startDate)
                                else -> buildAnnotatedString { append("") }
                            },
                            style = Typography.titleMedium
                        )

                        if (filteredWiDListByTitle.isEmpty()) {
                            createEmptyView(text = "표시할 그래프가 없습니다.")()
                        } else {
                            LineChartFragment(
                                title = selectedTitle,
                                wiDList = filteredWiDListByTitle,
                                startDate = startDate,
                                finishDate = finishDate
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(vertical = 16.dp),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = "시간 기록",
                            style = Typography.titleMedium
                        )

                        if (filteredWiDListByTitle.isEmpty()) {
                            createEmptyView(text = "표시할 기록이 없습니다.")()
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "합계",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )

                                Text(
                                    text = formatDuration(duration = totalDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "평균",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )

                                Text(
                                    text = formatDuration(duration = averageDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "최고",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )

                                Text(
                                    text = formatDuration(duration = maxDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        /**
         * 하단 바
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            // 기간 선택
            AnimatedVisibility(
                visible = periodMenuExpanded,
                enter = expandVertically{ 0 },
                exit = shrinkVertically{ 0 },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "기간 선택",
                        style = Typography.titleMedium
                    )

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth(),
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        periods.forEach { chipPeriod ->
                            item {
                                FilterChip(
                                    selected = selectedPeriod == chipPeriod,
                                    onClick = {
                                        selectedPeriod = chipPeriod
                                        periodMenuExpanded = false

                                        if (selectedPeriod == periods[0]) { // 일주일
                                            startDate = getFirstDayOfWeek(today)
                                            finishDate = getLastDayOfWeek(today)
                                        } else if (selectedPeriod == periods[1]) { // 한 달
                                            startDate = getFirstDayOfMonth(today)
                                            finishDate = getLastDayOfMonth(today)
                                        }
                                    },
                                    label = {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            text = periodMap[chipPeriod] ?: chipPeriod,
                                            style = Typography.bodySmall,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorResource(id = R.color.light_gray),
                                        labelColor = Color.Black,
                                        selectedContainerColor = Color.Black,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 제목 선택
            AnimatedVisibility(
                visible = titleMenuExpanded,
                enter = expandVertically{ 0 },
                exit = shrinkVertically{ 0 },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "제목 선택",
                        style = Typography.titleMedium
                    )

                    FilterChip(
                        modifier = Modifier
                            .fillMaxWidth(),
                        selected = selectedTitle == titlesWithAll[0],
                        onClick = {
                            selectedTitle = titlesWithAll[0]
                            titleMenuExpanded = false
                        },
                        label = {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = titleMapWithAll[titlesWithAll[0]] ?: titlesWithAll[0],
                                style = Typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = colorResource(id = R.color.light_gray),
                            labelColor = Color.Black,
                            selectedContainerColor = Color.Black,
                            selectedLabelColor = Color.White
                        )
                    )

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth(),
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        titles.forEach { chipTitle ->
                            item {
                                FilterChip(
                                    selected = selectedTitle == chipTitle,
                                    onClick = {
                                        selectedTitle = chipTitle
                                        titleMenuExpanded = false
                                    },
                                    label = {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            text = titleMapWithAll[chipTitle] ?: chipTitle,
                                            style = Typography.bodySmall,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorResource(id = R.color.light_gray),
                                        labelColor = Color.Black,
                                        selectedContainerColor = Color.Black,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (titleMenuExpanded) {
                            titleMenuExpanded = false
                        }

                        periodMenuExpanded = !periodMenuExpanded
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "기간 선택",
                    )
                }

                IconButton(
                    onClick = {
                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        titleMenuExpanded = !titleMenuExpanded
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_title_24),
                        contentDescription = "제목 선택",
                        tint = colorResource(
                            id = colorMap[selectedTitle] ?: R.color.black
                        )
                    )
                }

                IconButton(
                    onClick = {
                        if (titleMenuExpanded) {
                            titleMenuExpanded = false
                        }

                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = getFirstDayOfWeek(today)
                                finishDate = getLastDayOfWeek(today)
                            }
                            periods[1] -> {
                                startDate = getFirstDayOfMonth(today)
                                finishDate = getLastDayOfMonth(today)
                            }
                        }

                        selectedMapText = "합계"
                    },
                    enabled = when (selectedPeriod) {
                        periods[0] -> !(startDate == getFirstDayOfWeek(today) && finishDate == getLastDayOfWeek(today))
                        periods[1] -> !(startDate == getFirstDayOfMonth(today) && finishDate == getLastDayOfMonth(today))
                        else -> false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "기간 초기화"
                    )
                }

                IconButton(
                    onClick = {
                        if (titleMenuExpanded) {
                            titleMenuExpanded = false
                        }

                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = startDate.minusWeeks(1)
                                finishDate = finishDate.minusWeeks(1)
                            }
                            periods[1] -> {
                                startDate = getFirstDayOfMonth(startDate.minusDays(15))
                                finishDate = getLastDayOfMonth(finishDate.minusDays(45))
                            }
                        }

                        selectedMapText = "합계"
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 기간"
                    )
                }

                IconButton(
                    onClick = {
                        if (titleMenuExpanded) {
                            titleMenuExpanded = false
                        }

                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = startDate.plusWeeks(1)
                                finishDate = finishDate.plusWeeks(1)
                            }
                            periods[1] -> {
                                startDate = getFirstDayOfMonth(startDate.plusDays(45))
                                finishDate = getLastDayOfMonth(finishDate.plusDays(15))
                            }
                        }

                        selectedMapText = "합계"
                    },
                    enabled = when (selectedPeriod) {
                        periods[0] -> !(startDate == getFirstDayOfWeek(today) && finishDate == getLastDayOfWeek(today))
                        periods[1] -> !(startDate == getFirstDayOfMonth(today) && finishDate == getLastDayOfMonth(today))
                        else -> false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 기간"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PeriodBasedFragmentPreview() {
    PeriodBasedFragment()
}