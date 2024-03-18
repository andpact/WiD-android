package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.TitleWiDViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun TitleWiDFragment(titleWiDViewModel: TitleWiDViewModel) {
    // 날짜
    val today = titleWiDViewModel.today
    val startDate = titleWiDViewModel.startDate.value
    val finishDate = titleWiDViewModel.finishDate.value
    var weekPickerExpanded by remember { mutableStateOf(false) }
    var monthPickerExpanded by remember { mutableStateOf(false) }

    // 제목
    val selectedTitle = titleWiDViewModel.selectedTitle.value

    // WiD
    val filteredWiDListByTitle = titleWiDViewModel.filteredWiDListByTitle.value

    // 기간
    val selectedPeriod = titleWiDViewModel.selectedPeriod.value

    // 합계
    val totalDurationMap = titleWiDViewModel.totalDurationMap.value

    // 평균
    val averageDurationMap = titleWiDViewModel.averageDurationMap.value

    // 최저
    val minDurationMap = titleWiDViewModel.minDurationMap.value

    // 최고
    val maxDurationMap = titleWiDViewModel.maxDurationMap.value

    DisposableEffect(Unit) {
        Log.d("TitleWiDFragment", "TitleWiDFragment is being composed")

        titleWiDViewModel.setStartDateAndFinishDate(
            startDate = titleWiDViewModel.startDate.value,
            finishDate = titleWiDViewModel.finishDate.value
        )

        onDispose {
            Log.d("TitleWiDFragment", "TitleWiDFragment is being disposed")
        }
    }

    BackHandler(
        enabled = weekPickerExpanded || monthPickerExpanded,
        onBack = {
            weekPickerExpanded = false
            monthPickerExpanded = false
        }
    )

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
                            when (selectedPeriod) {
                                periods[0] -> weekPickerExpanded = true
                                periods[1] -> monthPickerExpanded = true
                                else -> buildAnnotatedString { append("") }
                            }
                        },
                    text = when (selectedPeriod) {
                        periods[0] -> getPeriodStringOfWeek(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                        periods[1] -> getPeriodStringOfMonth(date = startDate)
                        else -> buildAnnotatedString { append("") }
                    },
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    textAlign = TextAlign.Start
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
                            when (selectedPeriod) {
                                periods[0] -> {
                                    val newStartDate = startDate.minusWeeks(1)
                                    val newFinishDate = finishDate.minusWeeks(1)

                                    titleWiDViewModel.setStartDateAndFinishDate(
                                        newStartDate,
                                        newFinishDate
                                    )
                                }
                                periods[1] -> {
                                    val newStartDate = getFirstDateOfMonth(startDate.minusDays(15))
                                    val newFinishDate = getLastDateOfMonth(finishDate.minusDays(45))

                                    titleWiDViewModel.setStartDateAndFinishDate(
                                        newStartDate,
                                        newFinishDate
                                    )
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
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            when (selectedPeriod) {
                                periods[0] -> {
                                    val newStartDate = startDate.plusWeeks(1)
                                    val newFinishDate = finishDate.plusWeeks(1)

                                    titleWiDViewModel.setStartDateAndFinishDate(
                                        newStartDate,
                                        newFinishDate
                                    )
                                }
                                periods[1] -> {
                                    val newStartDate = getFirstDateOfMonth(startDate.plusDays(45))
                                    val newFinishDate = getLastDateOfMonth(finishDate.plusDays(15))

                                    titleWiDViewModel.setStartDateAndFinishDate(
                                        newStartDate,
                                        newFinishDate
                                    )
                                }
                            }
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 기간",
                    tint = if (selectedPeriod == periods[0] && !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today)) ||
                        selectedPeriod == periods[1] && !(startDate == getFirstDateOfMonth(today) && finishDate == getLastDateOfMonth(today))) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        DarkGray
                    }
                )
            }

            if (filteredWiDListByTitle.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "표시할 기록이 없습니다.",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    item {
                        LineChartFragment(
                            title = selectedTitle,
                            wiDList = filteredWiDListByTitle,
                            startDate = startDate,
                            finishDate = finishDate
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${titleMap[selectedTitle]}",
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "합계",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            text = getDurationString(duration = totalDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                            style = Typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "평균",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            text = getDurationString(duration = averageDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                            style = Typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "최저",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            text = getDurationString(duration = minDurationMap[selectedTitle] ?: Duration.ZERO, mode = 3),
                                            style = Typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = "최고",
                                            style = Typography.bodyMedium,
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
            }
        }

        /**
         * 월 선택 대화상자
         */
        if (weekPickerExpanded || monthPickerExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = weekPickerExpanded || monthPickerExpanded,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        weekPickerExpanded = false
                        monthPickerExpanded = false
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
                    if (titleWiDViewModel.selectedPeriod.value == periods[0]) { // 주 선택
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
                                        titleWiDViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfWeek,
                                            finishDate = lastDayOfWeek
                                        )

                                        weekPickerExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getPeriodStringOfWeek(firstDayOfWeek = firstDayOfWeek, lastDayOfWeek = lastDayOfWeek),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                RadioButton(
                                    selected = startDate == firstDayOfWeek && finishDate == lastDayOfWeek,
                                    onClick = { },
                                )
                            }
                        }
                    } else { // 월 선택
                        repeat(5) { index ->
                            val reverseIndex = 4 - index // 역순 인덱스 계산

                            val currentDate = LocalDate.now()
                            val targetDate = currentDate.minusMonths(reverseIndex.toLong())

                            val firstDayOfMonth = YearMonth.from(targetDate).atDay(1)
                            val lastDayOfMonth = YearMonth.from(targetDate).atEndOfMonth()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        titleWiDViewModel.setStartDateAndFinishDate(
                                            startDate = firstDayOfMonth,
                                            finishDate = lastDayOfMonth
                                        )

                                        monthPickerExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = getPeriodStringOfMonth(date = firstDayOfMonth),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                RadioButton(
                                    selected = startDate == firstDayOfMonth && finishDate == lastDayOfMonth,
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