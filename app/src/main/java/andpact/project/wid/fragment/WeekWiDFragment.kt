package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekWiDFragment() {
    // 날짜
    val today = LocalDate.now()
    var startDate by remember { mutableStateOf(getFirstDateOfWeek(today)) }
    var finishDate by remember { mutableStateOf(getLastDateOfWeek(today)) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList by remember(startDate, finishDate) { mutableStateOf(wiDService.readWiDListByDateRange(startDate, finishDate)) }

    // 합계
    val totalDurationMap by remember(wiDList) { mutableStateOf(getTotalDurationMapByTitle(wiDList = wiDList)) }

    // 평균
    val averageDurationMap by remember(wiDList) { mutableStateOf(getAverageDurationMapByTitle(wiDList = wiDList)) }

    // 최고
    val minDurationMap by remember(wiDList) { mutableStateOf(getMinDurationMapByTitle(wiDList = wiDList)) }

    // 최고
    val maxDurationMap by remember(wiDList) { mutableStateOf(getMaxDurationMapByTitle(wiDList = wiDList)) }

//    // 맵
    var selectedMapText by remember { mutableStateOf("합계") }
    var selectedMap by remember(wiDList) { mutableStateOf(totalDurationMap) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .clickable {
//                        expandDatePicker = true
                    },
                text = getPeriodStringOfWeek(firstDayOfWeek = startDate, lastDayOfWeek = finishDate),
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

//            Icon(
//                modifier = Modifier
//                    .clickable {
//                        expandDatePicker = true
//                    }
//                    .size(24.dp),
//                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                contentDescription = "날짜 선택",
//                tint = MaterialTheme.colorScheme.primary
//            )

//            Icon(
//                modifier = Modifier
//                    .clickable(enabled = false) {
//                        // 클릭 이벤트를 처리할 내용
//                    }
//                    .size(24.dp),
//                painter = painterResource(R.drawable.baseline_title_24),
//                contentDescription = "제목 선택",
//                tint = DarkGray
//            )

//            Icon(
//                modifier = Modifier
//                    .clickable(enabled = currentDate != today) {
//                        if (expandDatePicker) {
//                            expandDatePicker = false
//                        }
//                        currentDate = today
//
////                        expandDiary = false
////                        diaryOverflow = false
//                    }
//                    .size(24.dp),
//                imageVector = Icons.Filled.Refresh,
//                contentDescription = "오늘 날짜",
//                tint = if (currentDate != today) MaterialTheme.colorScheme.primary else DarkGray
//            )

            Icon(
                modifier = Modifier
                    .clickable {
                        startDate = startDate.minusWeeks(1)
                        finishDate = finishDate.minusWeeks(1)
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 날짜",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable(
                        enabled = !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(
                            today
                        ))
                    ) {
                        startDate = startDate.plusWeeks(1)
                        finishDate = finishDate.plusWeeks(1)
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 날짜",
                tint = if (startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today)) DarkGray else MaterialTheme.colorScheme.primary
            )
        }

        if (wiDList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "표시할 데이터가 없습니다.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

//            getEmptyView(text = "표시할 타임라인이 없습니다.")()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        val daysOfWeek = daysOfWeekFromMonday

                        daysOfWeek.forEachIndexed { index, day ->
                            val textColor = when (index) {
                                5 -> DeepSkyBlue
                                6 -> OrangeRed
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
    //                        .padding(horizontal = 16.dp)
                            .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                        columns = GridCells.Fixed(7)
                    ) {
                        items(
                            count = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
                        ) { index: Int ->
                            val indexDate = startDate.plusDays(index.toLong())
                            val filteredWiDListByDate = wiDList.filter { it.date == indexDate }

                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
    //                            verticalArrangement = Arrangement.Center
                            ) {
                                PeriodBasedPieChartFragment(
                                    date = indexDate,
                                    wiDList = filteredWiDListByDate
                                )

                                Text(
                                    text = "${getTotalDurationPercentageFromWiDList(wiDList = filteredWiDListByDate)}%",
                                    style = Typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        FilterChip(
                            modifier = Modifier
                                .weight(1f),
                            selected = selectedMapText == "합계",
                            onClick = {
                                selectedMapText = "합계"
                                selectedMap = totalDurationMap
                            },
                            label = {
                                Text( // 텍스트 색상은 아래에 지정함.
                                    modifier = Modifier
                                        .weight(1f),
                                    text = "합계",
                                    style = Typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                labelColor = MaterialTheme.colorScheme.primary,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                        )

                        FilterChip(
                            modifier = Modifier
                                .weight(1f),
                            selected = selectedMapText == "평균",
                            onClick = {
                                selectedMapText = "평균"
                                selectedMap = averageDurationMap
                            },
                            label = {
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = "평균",
                                    style = Typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                labelColor = MaterialTheme.colorScheme.primary,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RectangleShape
                        )

                        FilterChip(
                            modifier = Modifier
                                .weight(1f),
                            selected = selectedMapText == "최저",
                            onClick = {
                                selectedMapText = "최저"
                                selectedMap = minDurationMap
                            },
                            label = {
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = "최저",
                                    style = Typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                labelColor = MaterialTheme.colorScheme.primary,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RectangleShape
                        )

                        FilterChip(
                            modifier = Modifier
                                .weight(1f),
                            selected = selectedMapText == "최고",
                            onClick = {
                                selectedMapText = "최고"
                                selectedMap = maxDurationMap
                            },
                            label = {
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = "최고",
                                    style = Typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                labelColor = MaterialTheme.colorScheme.primary,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
                        )
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp)
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


//        if (selectedMap.isEmpty()) {
//            getEmptyView(text = "표시할 $selectedMapText 기록이 없습니다.")()
//        } else {
//            LazyVerticalGrid(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
//                columns = GridCells.Fixed(2),
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//            ) {
//                selectedMap.forEach { (title, duration) ->
//                    item {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp)
//                                .shadow(
//                                    elevation = 2.dp,
//                                    shape = RoundedCornerShape(8.dp),
//                                    spotColor = MaterialTheme.colorScheme.primary,
//                                )
//                                .background(MaterialTheme.colorScheme.secondary)
//                                .padding(vertical = 16.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Row(
//                                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Icon(
//                                    modifier = Modifier
//                                        .clip(CircleShape)
//                                        .background(
//                                            (colorMap[title] ?: DarkGray).copy(
//                                                alpha = 0.1f
//                                            )
//                                        )
//                                        .padding(8.dp)
//                                        .size(24.dp),
//                                    painter = painterResource(id = titleIconMap[title] ?: R.drawable.baseline_title_24),
//                                    contentDescription = "제목",
//                                    tint = colorMap[title] ?: DarkGray
//                                )
//
//                                Text(
//                                    text = titleMap[title] ?: title,
//                                    style = Typography.titleLarge,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Text(
//                                text = getDurationString(duration, mode = 3),
//                                style = Typography.titleLarge,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}