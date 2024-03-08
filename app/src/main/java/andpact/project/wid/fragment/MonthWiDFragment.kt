package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.MonthWiDViewModel
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthWiDFragment(monthWiDViewModel: MonthWiDViewModel) {
    // 뷰 모델
//    val monthWiDViewModel: MonthWiDViewModel = viewModel()

    // 날짜
//    val today = LocalDate.now()
    val today = monthWiDViewModel.today
//    var startDate by remember { mutableStateOf(getFirstDateOfMonth(today)) }
    val startDate = monthWiDViewModel.startDate.value
//    var finishDate by remember { mutableStateOf(getLastDateOfMonth(today)) }
    val finishDate = monthWiDViewModel.finishDate.value
//    val monthPickerExpanded by remember { mutableStateOf(false) }
    val monthPickerExpanded = monthWiDViewModel.monthPickerExpanded.value

    // WiD
//    val wiDService = WiDService(context = LocalContext.current)
//    val wiDList by remember(startDate, finishDate) { mutableStateOf(wiDService.readWiDListByDateRange(startDate, finishDate)) }
    val wiDList = monthWiDViewModel.wiDList.value

    // 합계
//    val totalDurationMap by remember(wiDList) { mutableStateOf(getTotalDurationMapByTitle(wiDList = wiDList)) }
    val totalDurationMap = monthWiDViewModel.totalDurationMap

    // 평균
//    val averageDurationMap by remember(wiDList) { mutableStateOf(getAverageDurationMapByTitle(wiDList = wiDList)) }
    val averageDurationMap = monthWiDViewModel.averageDurationMap

    // 최고
//    val minDurationMap by remember(wiDList) { mutableStateOf(getMinDurationMapByTitle(wiDList = wiDList)) }
    val minDurationMap = monthWiDViewModel.minDurationMap

    // 최고
//    val maxDurationMap by remember(wiDList) { mutableStateOf(getMaxDurationMapByTitle(wiDList = wiDList)) }
    val maxDurationMap = monthWiDViewModel.maxDurationMap

//    // 맵
//    var selectedMapText by remember { mutableStateOf("합계") }
    val selectedMapText = monthWiDViewModel.selectedMapText.value
//    var selectedMap by remember(wiDList) { mutableStateOf(totalDurationMap) }
    val selectedMap = monthWiDViewModel.selectedMap.value

    DisposableEffect(Unit) {
        Log.d("MonthWiDFragment", "MonthWiDFragment is being composed")

        onDispose {
            Log.d("MonthWiDFragment", "MonthWiDFragment is being disposed")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
    //                        expandDatePicker = true // 월 선택 대화상자 구현해야 함.
                            monthWiDViewModel.setMonthPickerExpanded(expand = true)
                        },
                    text = getPeriodStringOfMonth(date = startDate),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newStartDate = getFirstDateOfMonth(startDate.minusDays(15))
                            val newFinishDate = getLastDateOfMonth(finishDate.minusDays(45))

                            monthWiDViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 날짜",
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    modifier = Modifier
                        .clickable(
                            enabled = !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(
                                today
                            )),
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newStartDate = getFirstDateOfMonth(startDate.plusDays(45))
                            val newFinishDate = getLastDateOfMonth(finishDate.plusDays(15))

                            monthWiDViewModel.setStartDateAndFinishDate(newStartDate, newFinishDate)
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 날짜",
                    tint = if (startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today)) DarkGray else MaterialTheme.colorScheme.primary
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
                            val daysOfWeek = daysOfWeekFromSunday

                            daysOfWeek.forEachIndexed { index, day ->
                                val textColor = when (index) {
                                    0 -> OrangeRed
                                    6 -> DeepSkyBlue
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
                    }

                    // 파이차트
                    item {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                            columns = GridCells.Fixed(7)
                        ) {
                            items(startDate.dayOfWeek.value % 7) {
                                // selectedPeriod가 한달이면 달력의 빈 칸을 생성해줌.
                            }

                            items(
                                count = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
                            ) { index: Int ->
                                val indexDate = startDate.plusDays(index.toLong())
                                val filteredWiDListByDate =
                                    wiDList.filter { it.date == indexDate }

                                Column(
                                    modifier = Modifier
                                        .weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
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
                    }

                    // 탭
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            FilterChip(
                                modifier = Modifier
                                    .weight(1f),
                                selected = selectedMapText == "합계",
                                onClick = {
    //                                selectedMapText = "합계"
    //                                selectedMap = totalDurationMap

                                    monthWiDViewModel.updateSelectedMap(newText = "합계", newMap = totalDurationMap)
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
                                shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    labelColor = MaterialTheme.colorScheme.primary,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.secondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Transparent
                                )
                            )

                            FilterChip(
                                modifier = Modifier
                                    .weight(1f),
                                selected = selectedMapText == "평균",
                                onClick = {
    //                                selectedMapText = "평균"
    //                                selectedMap = averageDurationMap

                                    monthWiDViewModel.updateSelectedMap(newText = "평균", newMap = averageDurationMap)
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
                                shape = RectangleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    labelColor = MaterialTheme.colorScheme.primary,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.secondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Transparent
                                )
                            )

                            FilterChip(
                                modifier = Modifier
                                    .weight(1f),
                                selected = selectedMapText == "최저",
                                onClick = {
    //                                selectedMapText = "최저"
    //                                selectedMap = minDurationMap

                                    monthWiDViewModel.updateSelectedMap(newText = "최저", newMap = minDurationMap)
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
                                shape = RectangleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    labelColor = MaterialTheme.colorScheme.primary,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.secondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Transparent
                                )
                            )

                            FilterChip(
                                modifier = Modifier
                                    .weight(1f),
                                selected = selectedMapText == "최고",
                                onClick = {
    //                                selectedMapText = "최고"
    //                                selectedMap = maxDurationMap

                                    monthWiDViewModel.updateSelectedMap(newText = "최고", newMap = maxDurationMap)
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
                                shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    labelColor = MaterialTheme.colorScheme.primary,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.secondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Transparent
                                )
                            )
                        }
                    }

                    // 합계
                    selectedMap.forEach { (title, duration) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8))
                                    .background(colorMap[title] ?: DarkGray),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(8.dp)
                                        .size(24.dp),
                                    painter = painterResource(
                                        id = titleIconMap[title] ?: R.drawable.baseline_title_24
                                    ),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "${titleMap[title]}",
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
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
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        /**
         * 월 선택 대화상자
         */
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxSize(),
            visible = monthPickerExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            var currentYear: Int = LocalDate.now().year

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = monthPickerExpanded,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
//                        expandDatePicker = false
                        monthWiDViewModel.setMonthPickerExpanded(expand = false)
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
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
                            .clickable(
                                enabled = false,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    monthWiDViewModel.setMonthPickerExpanded(expand = false)
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "월 선택 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "월 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(horizontal = 16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    monthWiDViewModel.setMonthPickerExpanded(expand = false)
                                },
                            text = "확인",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentYear}년",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        Icon(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    currentYear -= 1
                                }
                                .size(24.dp),
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 달",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Icon(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    currentYear += 1
                                }
                                .size(24.dp),
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 달",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(bottom = 16.dp)
                            .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                        columns = GridCells.Fixed(3),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(12) { index ->
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .weight(1f)
                                    .clickable {

                                    },
                                text = "${index}월",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}