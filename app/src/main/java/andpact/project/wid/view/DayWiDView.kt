package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DayPieChartView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.DayWiDViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWiDView(dayWiDViewModel: DayWiDViewModel = hiltViewModel()) {
    val TAG = "DayWiDView"

    val titleColorMap = dayWiDViewModel.titleColorMap.value

    // 날짜
//    val today = dayWiDViewModel.today
    val today = LocalDate.now()
    val currentDate = dayWiDViewModel.currentDate.value
    val showDatePicker = dayWiDViewModel.showDatePicker.value
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() + (9 * 60 * 60 * 1000),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // utcTimeMillis는 KST를 반환하는 듯하고,
                // System.currentTimeMillis()가 (KST -9시간)을 반환하는 듯.
                return utcTimeMillis <= System.currentTimeMillis() + (9 * 60 * 60 * 1000)
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = LocalDate.now().year
                return year <= currentYear
            }
        }
    )

    // WiD
    val wiDListGet = dayWiDViewModel.wiDListGet.value
    val fullWiDList = dayWiDViewModel.fullWiDList.value

    // 합계
    val totalDurationMap = dayWiDViewModel.totalDurationMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        dayWiDViewModel.setCurrentDate(dayWiDViewModel.currentDate.value)

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    BackHandler(
        enabled = showDatePicker,
        onBack = {
            dayWiDViewModel.setShowDatePicker(show = false)
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        topBar = {
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
                            dayWiDViewModel.setShowDatePicker(show = true)
                        },
                    text = getDateString(currentDate),
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
                            val newDate = currentDate.minusDays(1)
                            dayWiDViewModel.setCurrentDate(newDate)
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 날짜",
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    modifier = Modifier
                        .clickable(
                            enabled = currentDate != today,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.plusDays(1)
                            dayWiDViewModel.setCurrentDate(newDate)
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 날짜",
                    tint = if (currentDate == today) DarkGray else MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (showDatePicker) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (wiDListGet) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 파이 차트
                        item {
                            DayPieChartView(
                                wiDList = fullWiDList,
                                titleColorMap = titleColorMap
                            )
                        }

                        // 합계 기록
                        totalDurationMap.forEach { (title, totalDuration) ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8))
                                        .background(titleColorMap[title] ?: MaterialTheme.colorScheme.tertiary),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = title,
                                        style = Typography.bodyLarge,
                                        color = titleColorMap[title]?.let { textColor ->
                                            if (0.5f < textColor.luminance()) {
                                                Black
                                            } else {
                                                White
                                            }
                                        } ?: MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = getDurationString(duration = totalDuration, mode = 3),
                                        style = Typography.bodyLarge,
                                        color = titleColorMap[title]?.let { textColor ->
                                            if (0.5f < textColor.luminance()) {
                                                Black
                                            } else {
                                                White
                                            }
                                        } ?: MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }
            }

            /** 날짜 선택 대화상자 */
            if (showDatePicker) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            enabled = showDatePicker,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            dayWiDViewModel.setShowDatePicker(show = false)
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
                                        dayWiDViewModel.setShowDatePicker(show = false)
                                    },
                                painter = painterResource(id = R.drawable.baseline_close_24),
                                contentDescription = "날짜 메뉴 닫기",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                text = "날짜 선택",
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
                                        dayWiDViewModel.setShowDatePicker(show = false)

                                        val newDate = Instant
                                            .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                        dayWiDViewModel.setCurrentDate(newDate)
                                    },
                                text = "확인",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        HorizontalDivider()

                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            title = null,
                            headline = null,
                        )
                    }
                }
            }
        }
    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    if (showDatePicker) {
//                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
//                    } else {
//                        MaterialTheme.colorScheme.secondary
//                    }
//                ),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            /** 상단 바 */
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .height(56.dp),
//                horizontalArrangement = Arrangement.spacedBy(32.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    modifier = Modifier
//                        .clickable(
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null
//                        ) {
//                            dayWiDViewModel.setShowDatePicker(show = true)
//                        },
//                    text = getDateString(currentDate),
//                    style = Typography.titleLarge,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                )
//
//                Icon(
//                    modifier = Modifier
//                        .clickable(
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null
//                        ) {
//                            val newDate = currentDate.minusDays(1)
//                            dayWiDViewModel.setCurrentDate(newDate)
//                        }
//                        .size(24.dp),
//                    imageVector = Icons.Default.KeyboardArrowLeft,
//                    contentDescription = "이전 날짜",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//
//                Icon(
//                    modifier = Modifier
//                        .clickable(
//                            enabled = currentDate != today,
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null
//                        ) {
//                            val newDate = currentDate.plusDays(1)
//                            dayWiDViewModel.setCurrentDate(newDate)
//                        }
//                        .size(24.dp),
//                    imageVector = Icons.Default.KeyboardArrowRight,
//                    contentDescription = "다음 날짜",
//                    tint = if (currentDate == today) DarkGray else MaterialTheme.colorScheme.primary
//                )
//            }
//
//            /** 컨텐츠 */
//            if (wiDListGet) {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp)
//                        .weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    // 파이 차트
//                    item {
//                        DayPieChartView(
//                            wiDList = fullWiDList,
//                            titleColorMap = titleColorMap
//                        )
//                    }
//
//                    // 합계 기록
//                    totalDurationMap.forEach { (title, totalDuration) ->
//                        item {
//                            Row(
//                                modifier = Modifier
//                                    .clip(RoundedCornerShape(8))
//                                    .background(titleColorMap[title] ?: MaterialTheme.colorScheme.tertiary),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    modifier = Modifier
//                                        .padding(16.dp),
//                                    text = title,
//                                    style = Typography.bodyLarge,
//                                    color = titleColorMap[title]?.let { textColor ->
//                                        if (0.5f < textColor.luminance()) {
//                                            Black
//                                        } else {
//                                            White
//                                        }
//                                    } ?: MaterialTheme.colorScheme.primary
//                                )
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .padding(16.dp),
//                                    text = getDurationString(duration = totalDuration, mode = 3),
//                                    style = Typography.bodyLarge,
//                                    color = titleColorMap[title]?.let { textColor ->
//                                        if (0.5f < textColor.luminance()) {
//                                            Black
//                                        } else {
//                                            White
//                                        }
//                                    } ?: MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
//                    }
//                }
//            } else {
//                CircularProgressIndicator()
//            }
//        }
//
//        /** 날짜 선택 대화상자 */
//        if (showDatePicker) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clickable(
//                        enabled = showDatePicker,
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        dayWiDViewModel.setShowDatePicker(show = false)
//                    }
//            ) {
//                Column(
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .padding(16.dp)
//                        .shadow(
//                            elevation = 2.dp,
//                            shape = RoundedCornerShape(8.dp),
//                            spotColor = MaterialTheme.colorScheme.primary,
//                        )
//                        .background(
//                            color = MaterialTheme.colorScheme.tertiary,
//                            shape = RoundedCornerShape(8.dp)
//                        ),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable(
//                                enabled = false,
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {}
//                    ) {
//                        Icon(
//                            modifier = Modifier
//                                .padding(16.dp)
//                                .size(24.dp)
//                                .align(Alignment.CenterStart)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    dayWiDViewModel.setShowDatePicker(show = false)
//                                },
//                            painter = painterResource(id = R.drawable.baseline_close_24),
//                            contentDescription = "날짜 메뉴 닫기",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//
//                        Text(
//                            modifier = Modifier
//                                .align(Alignment.Center),
//                            text = "날짜 선택",
//                            style = Typography.titleMedium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//
//                        Text(
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                                .padding(horizontal = 16.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    dayWiDViewModel.setShowDatePicker(show = false)
//
//                                    val newDate = Instant
//                                        .ofEpochMilli(datePickerState.selectedDateMillis!!)
//                                        .atZone(ZoneId.systemDefault())
//                                        .toLocalDate()
//                                    dayWiDViewModel.setCurrentDate(newDate)
//                                },
//                            text = "확인",
//                            style = Typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//
//                    HorizontalDivider()
//
//                    DatePicker(
//                        state = datePickerState,
//                        showModeToggle = false,
//                        title = null,
//                        headline = null,
//                    )
//                }
//            }
//        }
//    }
}