package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.Duration
import java.time.LocalDate

@Composable
fun TitleWiDFragment() {
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
                text = when (selectedPeriod) {
                    periods[0] -> getPeriodStringOfWeek(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                    periods[1] -> getPeriodStringOfMonth(date = startDate)
                    else -> buildAnnotatedString { append("") }
                },
                style = Typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

//            Icon(
//                modifier = Modifier
//                    .clickable {
//                        if (titleMenuExpanded) {
//                            titleMenuExpanded = false
//                        }
//
//                        periodMenuExpanded = true
//                    }
//                    .size(24.dp),
//                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                contentDescription = "기간 선택",
//                tint = MaterialTheme.colorScheme.primary
//            )

            Icon(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(4.dp)
                    .clickable {
                        if (periodMenuExpanded) {
                            periodMenuExpanded = false
                        }

                        titleMenuExpanded = true
                    }
                    .size(24.dp),
                painter = painterResource(titleIconMap[selectedTitle] ?: R.drawable.baseline_title_24),
                contentDescription = "제목 선택",
                tint = MaterialTheme.colorScheme.secondary
            )

//            Icon(
//                modifier = Modifier
//                    .clickable(enabled = when (selectedPeriod) {
//                        periods[0] -> !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today))
//                        periods[1] -> !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))
//                        else -> false
//                    }) {
//                        if (titleMenuExpanded) {
//                            titleMenuExpanded = false
//                        }
//
//                        if (periodMenuExpanded) {
//                            periodMenuExpanded = false
//                        }
//
//                        when (selectedPeriod) {
//                            periods[0] -> {
//                                startDate = getFirstDateOfWeek(today)
//                                finishDate = getLastDateOfWeek(today)
//                            }
//                            periods[1] -> {
//                                startDate = getFirstDateOfMonth(today)
//                                finishDate = getLastDateOfMonth(today)
//                            }
//                        }
//
//                        selectedMapText = "합계"
//                    }
//                    .size(24.dp),
//                imageVector = Icons.Filled.Refresh,
//                contentDescription = "기간 초기화",
//                tint = if (selectedPeriod == periods[0] && !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today)) ||
//                    selectedPeriod == periods[1] && !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))
//                ) {
//                    MaterialTheme.colorScheme.primary
//                } else {
//                    DarkGray
//                }
//            )

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
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 기간",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable(
                        enabled = when (selectedPeriod) {
                            periods[0] -> !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(
                                today
                            ))
                            periods[1] -> !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(
                                today
                            ))
                            else -> false
                        }
                    ) {
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

    /**
     * 대화상자
     */
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = titleMenuExpanded,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(titleMenuExpanded) {
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