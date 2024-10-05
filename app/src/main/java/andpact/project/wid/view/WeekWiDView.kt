package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.PeriodBasedPieChartFragment
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.WeekWiDViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekWiDView(weekWiDViewModel: WeekWiDViewModel = hiltViewModel()) {
    val TAG = "WeekWiDView"

    // 날짜
    val today = weekWiDViewModel.today
    val startDate = weekWiDViewModel.startDate.value
    val finishDate = weekWiDViewModel.finishDate.value
    val weekPickerExpanded = weekWiDViewModel.weekPickerExpanded.value

    // WiD
    val wiDList = weekWiDViewModel.wiDList.value

    val titleColorMap = weekWiDViewModel.titleColorMap

    // 합계
    val totalDurationMap = weekWiDViewModel.totalDurationMap

    // 평균
    val averageDurationMap = weekWiDViewModel.averageDurationMap

    // 최고
    val minDurationMap = weekWiDViewModel.minDurationMap

    // 최고
    val maxDurationMap = weekWiDViewModel.maxDurationMap

//    // 맵
//    val selectedMapText = weekWiDViewModel.selectedMapText.value
//    val selectedMap = weekWiDViewModel.selectedMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        weekWiDViewModel.setStartDateAndFinishDate(
            startDate = weekWiDViewModel.startDate.value,
            finishDate = weekWiDViewModel.finishDate.value
        )

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    BackHandler(
        enabled = weekPickerExpanded,
        onBack = {
            weekWiDViewModel.setWeekPickerExpanded(false)
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
//                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        weekWiDViewModel.setWeekPickerExpanded(true)
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = "날짜",
                        )

                        Text(
                            text = getPeriodStringOfWeek(firstDayOfWeek = startDate, lastDayOfWeek = finishDate),
                            style = Typography.titleLarge,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                FilledTonalIconButton(
                    onClick = {
                        val newStartDate = startDate.minusWeeks(1)
                        val newFinishDate = finishDate.minusWeeks(1)

                        weekWiDViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                    },
//                    enabled = 0 < pagerState.currentPage
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 날짜",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        val newStartDate = startDate.plusWeeks(1)
                        val newFinishDate = finishDate.plusWeeks(1)

                        weekWiDViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                    },
                    enabled = !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today))
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 날짜",
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            if (wiDList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(100.dp),
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "다음 날짜",
                    )

                    Text(
                        text = "표시할 데이터가 없습니다.",
                        style = Typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 요일
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
                                    else -> MaterialTheme.colorScheme.onSurface
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
                    }

                    // 파이차트
                    item {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                ) {
                                    PeriodBasedPieChartFragment(
                                        date = indexDate,
                                        wiDList = filteredWiDListByDate
                                    )

//                                    Text(
//                                        text = "${getTotalDurationPercentageFromWiDList(wiDList = filteredWiDListByDate)}%",
//                                        style = Typography.labelSmall,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
                                }
                            }
                        }
                    }

                    // 탭
//                    item {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                        ) {
//                            FilterChip(
//                                modifier = Modifier
//                                    .weight(1f),
//                                selected = selectedMapText == "합계",
//                                onClick = {
//                                    weekWiDViewModel.updateSelectedMap(newText = "합계", newMap = totalDurationMap)
//                                },
//                                label = {
//                                    Text( // 텍스트 색상은 아래에 지정함.
//                                        modifier = Modifier
//                                            .weight(1f),
//                                        text = "합계",
//                                        style = Typography.bodyMedium,
//                                        textAlign = TextAlign.Center
//                                    )
//                                },
//                                shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp),
//                                border = FilterChipDefaults.filterChipBorder(
//                                    borderColor = Transparent
//                                )
//                            )
//
//                            FilterChip(
//                                modifier = Modifier
//                                    .weight(1f),
//                                selected = selectedMapText == "평균",
//                                onClick = {
//                                    weekWiDViewModel.updateSelectedMap(newText = "평균", newMap = averageDurationMap)
//                                },
//                                label = {
//                                    Text(
//                                        modifier = Modifier
//                                            .weight(1f),
//                                        text = "평균",
//                                        style = Typography.bodyMedium,
//                                        textAlign = TextAlign.Center
//                                    )
//                                },
//                                shape = RectangleShape,
//                                border = FilterChipDefaults.filterChipBorder(
//                                    borderColor = Transparent
//                                )
//                            )
//
//                            FilterChip(
//                                modifier = Modifier
//                                    .weight(1f),
//                                selected = selectedMapText == "최저",
//                                onClick = {
//                                    weekWiDViewModel.updateSelectedMap(newText = "최저", newMap = minDurationMap)
//                                },
//                                label = {
//                                    Text(
//                                        modifier = Modifier
//                                            .weight(1f),
//                                        text = "최저",
//                                        style = Typography.bodyMedium,
//                                        textAlign = TextAlign.Center
//                                    )
//                                },
//                                shape = RectangleShape,
//                                border = FilterChipDefaults.filterChipBorder(
//                                    borderColor = Transparent
//                                )
//                            )
//
//                            FilterChip(
//                                modifier = Modifier
//                                    .weight(1f),
//                                selected = selectedMapText == "최고",
//                                onClick = {
//                                    weekWiDViewModel.updateSelectedMap(newText = "최고", newMap = maxDurationMap)
//                                },
//                                label = {
//                                    Text(
//                                        modifier = Modifier
//                                            .weight(1f),
//                                        text = "최고",
//                                        style = Typography.bodyMedium,
//                                        textAlign = TextAlign.Center
//                                    )
//                                },
//                                shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp),
//                                border = FilterChipDefaults.filterChipBorder(
//                                    borderColor = Transparent
//                                )
//                            )
//                        }
//                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "합계 기록",
                                style = Typography.bodyLarge,
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "각 제목 합계",
                                style = Typography.bodyMedium,
                            )
                        }
                    }

//                    selectedMap.forEach { (title, duration) ->
                    totalDurationMap.forEach { (title, duration) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8))
                                    .background(
                                        titleColorMap[title]
                                            ?: MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(
                                        id = titleNumberStringToTitleIconMap[title] ?: R.drawable.baseline_title_24
                                    ),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = titleNumberStringToTitleKRStringMap[title] ?: "",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getDurationString(duration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "평균 기록",
                                style = Typography.bodyLarge,
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "하루 단위 평균",
                                style = Typography.bodyMedium,
                            )
                        }
                    }

                    averageDurationMap.forEach { (title, duration) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8))
                                    .background(
                                        titleColorMap[title]
                                            ?: MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(
                                        id = titleNumberStringToTitleIconMap[title] ?: R.drawable.baseline_title_24
                                    ),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = titleNumberStringToTitleKRStringMap[title] ?: "",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getDurationString(duration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "최고 기록",
                                style = Typography.bodyLarge,
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "하루 단위 최고",
                                style = Typography.bodyMedium,
                            )
                        }
                    }

                    maxDurationMap.forEach { (title, duration) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8))
                                    .background(
                                        titleColorMap[title]
                                            ?: MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(
                                        id = titleNumberStringToTitleIconMap[title] ?: R.drawable.baseline_title_24
                                    ),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = titleNumberStringToTitleKRStringMap[title] ?: "",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getDurationString(duration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "최저 기록",
                                style = Typography.bodyLarge,
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "하루 단위 최저",
                                style = Typography.bodyMedium,
                            )
                        }
                    }

                    minDurationMap.forEach { (title, duration) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8))
                                    .background(
                                        titleColorMap[title]
                                            ?: MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(
                                        id = titleNumberStringToTitleIconMap[title] ?: R.drawable.baseline_title_24
                                    ),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = titleNumberStringToTitleKRStringMap[title] ?: "",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getDurationString(duration, mode = 3),
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        /**
         * 주 선택 대화상자
         */
        if (weekPickerExpanded) {
            AlertDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                onDismissRequest = {
                    weekWiDViewModel.setWeekPickerExpanded(false)
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "기간 선택",
                        style = Typography.titleLarge
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(5) { index -> // 0부터 시작

                            val reverseIndex = 4 - index // 역순 인덱스 계산

                            val firstDayOfWeek = getFirstDateOfWeek(today).minusWeeks(reverseIndex.toLong())
                            val lastDayOfWeek = getLastDateOfWeek(today).minusWeeks(reverseIndex.toLong())

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        weekWiDViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfWeek,
                                            finishDate = lastDayOfWeek
                                        )

                                        weekWiDViewModel.setWeekPickerExpanded(false)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getPeriodStringOfWeek(firstDayOfWeek = firstDayOfWeek, lastDayOfWeek = lastDayOfWeek),
                                    style = Typography.bodyMedium,
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                RadioButton(
                                    selected = startDate == firstDayOfWeek && finishDate == lastDayOfWeek, // Set this according to your logic
                                    onClick = { },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}