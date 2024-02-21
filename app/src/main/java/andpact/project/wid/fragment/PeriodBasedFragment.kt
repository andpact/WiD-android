package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun PeriodBasedFragment(navController: NavController) {
    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 날짜
    val today = LocalDate.now()
    var startDate by remember { mutableStateOf(getFirstDateOfWeek(today)) }
    var finishDate by remember { mutableStateOf(getLastDateOfWeek(today)) }

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
    val minDurationMap by remember(wiDList) { mutableStateOf(getMinDurationMapByTitle(wiDList = wiDList)) }

    // 최고
    val maxDurationMap by remember(wiDList) { mutableStateOf(getMaxDurationMapByTitle(wiDList = wiDList)) }

    // 맵
    var selectedMapText by remember { mutableStateOf("합계") }
    var selectedMap by remember(wiDList) { mutableStateOf(totalDurationMap) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "기간 조회",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                text = "${periodMap[selectedPeriod]} • ${titleMapWithAll[selectedTitle]}",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider()

        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (selectedTitle == titlesWithAll[0]) { // 제목이 "전체" 일 때
                item("타임라인") {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = when (selectedPeriod) {
                                periods[0] -> getPeriodStringOfWeek(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                                periods[1] -> getPeriodStringOfMonth(date = startDate)
                                else -> buildAnnotatedString { append("") }
                            },
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (wiDList.isEmpty()) {
                            getEmptyView(text = "표시할 타임라인이 없습니다.")()
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    val daysOfWeek = if (selectedPeriod == periods[0]) daysOfWeekFromMonday else daysOfWeekFromSunday

                                    daysOfWeek.forEachIndexed { index, day ->
                                        val textColor = when (index) {
                                            0 -> if (selectedPeriod == periods[1]) OrangeRed else MaterialTheme.colorScheme.primary
                                            5 -> if (selectedPeriod == periods[0]) DeepSkyBlue else MaterialTheme.colorScheme.primary
                                            6 -> if (selectedPeriod == periods[0]) OrangeRed else if (selectedPeriod == periods[1]) DeepSkyBlue else MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.primary
                                        }

                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = day,
                                            style = Typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            color = textColor
                                        )
                                    }
                                }

                                LazyVerticalGrid(
                                    modifier = Modifier
                                        .fillMaxWidth()
//                                            .padding(horizontal = 16.dp)
                                        .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                                    columns = GridCells.Fixed(7)
                                ) {
                                    if (selectedPeriod == periods[1]) {
                                        items(startDate.dayOfWeek.value % 7) {
                                            // selectedPeriod가 한달이면 달력의 빈 칸을 생성해줌.
                                        }
                                    }

                                    items(
                                        count = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
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
                }

                item {
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                item("합계, 평균, 최저, 최고") {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                style = Typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
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
                                    color = if (selectedMapText == "합계") MaterialTheme.colorScheme.primary else DarkGray
                                )

                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            selectedMapText = "평균"
                                            selectedMap = averageDurationMap
                                        },
                                    text = "평균",
                                    style = Typography.bodyMedium,
                                    color = if (selectedMapText == "평균") MaterialTheme.colorScheme.primary else DarkGray
                                )

                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            selectedMapText = "최저"
                                            selectedMap = minDurationMap
                                        },
                                    text = "최저",
                                    style = Typography.bodyMedium,
                                    color = if (selectedMapText == "최저") MaterialTheme.colorScheme.primary else DarkGray
                                )

                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            selectedMapText = "최고"
                                            selectedMap = maxDurationMap
                                        },
                                    text = "최고",
                                    style = Typography.bodyMedium,
                                    color = if (selectedMapText == "최고") MaterialTheme.colorScheme.primary else DarkGray
                                )
                            }
                        }

                        if (selectedMap.isEmpty()) {
                            getEmptyView(text = "표시할 $selectedMapText 기록이 없습니다.")()
                        } else {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                selectedMap.forEach { (title, duration) ->
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .shadow(
                                                    elevation = 2.dp,
                                                    shape = RoundedCornerShape(8.dp),
                                                    spotColor = MaterialTheme.colorScheme.primary,
                                                )
                                                .background(MaterialTheme.colorScheme.secondary)
                                                .padding(vertical = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .background(
                                                            (colorMap[title] ?: DarkGray).copy(
                                                                alpha = 0.1f
                                                            )
                                                        )
                                                        .padding(8.dp)
                                                        .size(24.dp),
                                                    painter = painterResource(id = titleIconMap[title] ?: R.drawable.baseline_title_24),
                                                    contentDescription = "제목",
                                                    tint = colorMap[title] ?: DarkGray
                                                )

                                                Text(
                                                    text = titleMap[title] ?: title,
                                                    style = Typography.titleLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Text(
                                                text = getDurationString(duration, mode = 3),
                                                style = Typography.titleLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

//                item("기록률") {
//                    Column(
//                        modifier = Modifier
//                            .background(MaterialTheme.colorScheme.secondary)
//                            .padding(vertical = 16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Text(
//                            modifier = Modifier
//                                .padding(horizontal = 16.dp),
//                            text = "기록률",
//                            style = Typography.titleMedium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//
//                        if (wiDList.isEmpty()) {
//                            getEmptyView(text = "표시할 기록률이 없습니다.")()
//                        } else {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp)
//                                    .shadow(
//                                        elevation = 2.dp,
//                                        shape = RoundedCornerShape(8.dp),
//                                        spotColor = MaterialTheme.colorScheme.primary,
//                                    )
//                                    .background(MaterialTheme.colorScheme.secondary)
//                            ) {
//                                VerticalBarChartFragment(
//                                    wiDList = wiDList,
//                                    startDate = startDate,
//                                    finishDate = finishDate
//                                )
//                            }
//                        }
//                    }
//                }
            } else { // 제목이 "전체"가 아닐 때
                item("선 그래프") {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = when (selectedPeriod) {
                                periods[0] -> getPeriodStringOfWeek(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                                periods[1] -> getPeriodStringOfMonth(date = startDate)
                                else -> buildAnnotatedString { append("") }
                            },
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (filteredWiDListByTitle.isEmpty()) {
                            getEmptyView(text = "표시할 그래프가 없습니다.")()
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                            ) {
                                LineChartFragment(
                                    title = selectedTitle,
                                    wiDList = filteredWiDListByTitle,
                                    startDate = startDate,
                                    finishDate = finishDate
                                )
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                item("시간 기록") {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = "시간 기록",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (filteredWiDListByTitle.isEmpty()) {
                            getEmptyView(text = "표시할 기록이 없습니다.")()
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "합계",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getDurationString(duration = totalDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "평균",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getDurationString(duration = averageDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "최저",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getDurationString(duration = minDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = MaterialTheme.colorScheme.primary,
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "최고",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getDurationString(duration = maxDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable {
                        if (titleMenuExpanded) {
                            titleMenuExpanded = false
                        }

                        periodMenuExpanded = true
                    }
                    .size(24.dp),
                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                contentDescription = "기간 선택",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable {
                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        titleMenuExpanded = true
                    }
                    .size(24.dp),
                painter = painterResource(titleIconMap[selectedTitle] ?: R.drawable.baseline_title_24),
                contentDescription = "제목 선택",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable(enabled = when (selectedPeriod) {
                        periods[0] -> !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today))
                        periods[1] -> !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))
                        else -> false
                    }) {
                        if (titleMenuExpanded) {
                            titleMenuExpanded = false
                        }

                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = getFirstDateOfWeek(today)
                                finishDate = getLastDateOfWeek(today)
                            }
                            periods[1] -> {
                                startDate = getFirstDateOfMonth(today)
                                finishDate = getLastDateOfMonth(today)
                            }
                        }

                        selectedMapText = "합계"
                    }
                    .size(24.dp),
                imageVector = Icons.Filled.Refresh,
                contentDescription = "기간 초기화",
                tint = if (selectedPeriod == periods[0] && !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today)) ||
                    selectedPeriod == periods[1] && !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    DarkGray
                }
            )

            Icon(
                modifier = Modifier
                    .clickable {
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
                                startDate = getFirstDateOfMonth(startDate.minusDays(15))
                                finishDate = getLastDateOfMonth(finishDate.minusDays(45))
                            }
                        }

                        selectedMapText = "합계"
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 기간",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable(enabled = when (selectedPeriod) {
                        periods[0] -> !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today))
                        periods[1] -> !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))
                        else -> false
                    }) {
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
                                startDate = getFirstDateOfMonth(startDate.plusDays(45))
                                finishDate = getLastDateOfMonth(finishDate.plusDays(15))
                            }
                        }

                        selectedMapText = "합계"
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 기간",
                tint = if (selectedPeriod == periods[0] && !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today)) ||
                    selectedPeriod == periods[1] && !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))
                ) {
                    MaterialTheme.colorScheme.primary
                } else {
                    DarkGray
                }
            )
        }
    }

    /**
     * 대화상자
     */
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = periodMenuExpanded || titleMenuExpanded,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(periodMenuExpanded || titleMenuExpanded) {
                    periodMenuExpanded = false
                    titleMenuExpanded = false
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .then(if (titleMenuExpanded) Modifier.height(screenHeight / 2) else Modifier)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = MaterialTheme.colorScheme.primary,
                    )
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (periodMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(false) {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable {
                                    periodMenuExpanded = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "기간 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "기간 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(periods.size) { index ->
                            val itemPeriod = periods[index]

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPeriod = itemPeriod
                                        periodMenuExpanded = false

                                        if (selectedPeriod == periods[0]) { // 일주일
                                            startDate = getFirstDateOfWeek(today)
                                            finishDate = getLastDateOfWeek(today)
                                        } else if (selectedPeriod == periods[1]) { // 한 달
                                            startDate = getFirstDateOfMonth(today)
                                            finishDate = getLastDateOfMonth(today)
                                        }

                                        selectedMapText = "합계"
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
//                                Icon(
//                                    modifier = Modifier
//                                        .padding(16.dp)
//                                        .size(24.dp),
//                                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                                    contentDescription = "제목",
//                                    tint = MaterialTheme.colorScheme.primary
//                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = "${index + 1}. ${periodMap[itemPeriod] ?: "일주일"}",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                if (selectedPeriod == itemPeriod) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = "선택됨",
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                if (titleMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(false) {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable {
                                    titleMenuExpanded = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "제목 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "제목 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTitle = titlesWithAll[0]
                                        titleMenuExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(id = R.drawable.baseline_title_24),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = titleMapWithAll[titlesWithAll[0]] ?: "전체",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                if (selectedTitle == titlesWithAll[0]) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = "선택됨",
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        items(titles.size) { index ->
                            val itemTitle = titles[index]
                            val iconResourceId = titleIconMap[itemTitle] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTitle = itemTitle
                                        titleMenuExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(id = iconResourceId),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = titleMap[itemTitle] ?: "공부",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                if (selectedTitle == itemTitle) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = "선택됨",
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
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

//@Preview(showBackground = true)
//@Composable
//fun PeriodBasedFragmentPreview() {
//    PeriodBasedFragment()
//}