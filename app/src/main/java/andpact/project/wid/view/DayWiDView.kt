package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DayPieChartView
import andpact.project.wid.model.WiD
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWiDView(
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    dayWiDViewModel: DayWiDViewModel = hiltViewModel()
) {
    val TAG = "DayWiDView"

    val titleColorMap = dayWiDViewModel.titleColorMap

    // 날짜
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
    val wiDListLoaded = dayWiDViewModel.wiDListLoaded.value
    val fullWiDList = dayWiDViewModel.fullWiDList.value

    // 합계
    val totalDurationMap = dayWiDViewModel.totalDurationMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        // WiD를 삭제하거나 생성한 후 돌아 왔을 때, 리스트가 동기화되도록.
        dayWiDViewModel.setCurrentDate(currentDate)

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
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // Scaffold 중첩 시 배경색을 자식 뷰도 지정해야함.
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        dayWiDViewModel.setShowDatePicker(show = true)
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
                            text = getDateString(currentDate),
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
                        val newDate = currentDate.minusDays(1)
                        dayWiDViewModel.setCurrentDate(newDate)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 날짜",
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        val newDate = currentDate.plusDays(1)
                        dayWiDViewModel.setCurrentDate(newDate)
                    },
                    enabled = currentDate != today
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 날짜",
                    )
                }
            }
        },
        floatingActionButton = {
            if (fullWiDList.size == 1) {
                FloatingActionButton(
                    onClick = {
                        val emptyWiD = WiD(
                            id = "",
                            date = dayWiDViewModel.currentDate.value,
                            title = "무엇을 하셨나요?",
                            start = LocalTime.MIN,
                            finish = LocalTime.MIN,
                            duration = Duration.ZERO
                        )

                        dayWiDViewModel.setEmptyWiD(emptyWiD)

                        onEmptyWiDClicked()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새로운 WiD",
                    )
                }
            }
//            else {
//                FloatingActionButton(
//                    onClick = {
//                        // 다이어리 이동 버튼
//                        // 레이지 컬러을 다이어리까지 내림
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = "다이어리로 이동",
//                    )
//                }
//            }
        },
//        floatingActionButtonPosition = if (fullWiDList.size == 1) { FabPosition.End } else { FabPosition.Center },
        content = { contentPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
//                if (wiDListLoaded && fullWiDList.isEmpty()) {
                if (wiDListLoaded && fullWiDList.size == 1) {
                    Icon(
                        modifier = Modifier
                            .size(48.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "표시할 기록이 없습니다.",
                    )

                    Text(
                        text = "표시할 기록이 없습니다.",
                        style = Typography.titleLarge,
                    )

                    Text(
                        text = "WiD를 생성하여\n하루를 어떻게 보냈는지\n기록해 보세요.",
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                } else if (wiDListLoaded) {
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

                        item {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = getTimeString(LocalTime.MIN, "a hh:mm:ss"),
                                    style = Typography.bodyMedium,
                                )

                                HorizontalDivider()
                            }

                            fullWiDList.forEach { wiD ->
                                if (wiD.id.isBlank()) { // 빈 WiD
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8))
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    val emptyWiD = WiD(
                                                        id = "",
                                                        date = dayWiDViewModel.currentDate.value,
                                                        title = "무엇을 하셨나요?",
                                                        start = wiD.start,
                                                        finish = wiD.finish,
                                                        duration = Duration.between(
                                                            wiD.start,
                                                            wiD.finish
                                                        )
                                                    )

                                                    dayWiDViewModel.setEmptyWiD(emptyWiD = emptyWiD)

                                                    onEmptyWiDClicked()

                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(24.dp),
                                                painter = painterResource(
                                                    id = titleNumberStringToTitleIconMap[wiD.title] ?: R.drawable.baseline_title_24
                                                ),
                                                contentDescription = "제목",
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "무엇을 하셨나요?",
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )

                                                Text(
                                                    text = getDurationString(wiD.duration, mode = 3),
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }

                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                            )

                                            Icon(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(24.dp),
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "이 WiD로 전환하기",
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )

                                            HorizontalDivider()
                                        }
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8))
                                                .background(
                                                    titleColorMap[wiD.title]
                                                        ?: MaterialTheme.colorScheme.secondaryContainer
                                                )
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    dayWiDViewModel.setExistingWiD(existingWiD = wiD)
                                                    dayWiDViewModel.setUpdatedWiD(updatedWiD = wiD)

                                                    onWiDClicked() // 화면 전환 용
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(24.dp),
                                                painter = painterResource(
                                                    id = titleNumberStringToTitleIconMap[wiD.title] ?: R.drawable.baseline_title_24
                                                ),
                                                contentDescription = "제목",
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = titleNumberStringToTitleKRStringMap[wiD.title] ?: "",
                                                    style = Typography.bodyMedium,
                                                    color = titleColorMap[wiD.title]?.let { textColor ->
                                                        if (0.5f < textColor.luminance()) {
                                                            Black
                                                        } else {
                                                            White
                                                        }
                                                    } ?: MaterialTheme.colorScheme.primary
                                                )

                                                Text(
                                                    text = getDurationString(wiD.duration, mode = 3),
                                                    style = Typography.bodyMedium,
                                                    color = titleColorMap[wiD.title]?.let { textColor ->
                                                        if (0.5f < textColor.luminance()) {
                                                            Black
                                                        } else {
                                                            White
                                                        }
                                                    } ?: MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                            )

                                            Icon(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .size(24.dp),
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "이 WiD로 전환하기",
                                                tint = titleColorMap[wiD.title]?.let { textColor ->
                                                    if (0.5f < textColor.luminance()) {
                                                        Black
                                                    } else {
                                                        White
                                                    }
                                                } ?: MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Text(
                                                text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )

                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                        }

                        // 함계 기록
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
                                    text = "각 제목 합계 소요 시간",
                                    style = Typography.bodyMedium,
                                )
                            }
                        }

                        // 합계 기록
                        totalDurationMap.forEach { (title, totalDuration) ->
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

                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .padding(16.dp),
                                            text = titleNumberStringToTitleKRStringMap[title] ?: "",
                                            style = Typography.bodyLarge,
                                            color = titleColorMap[title]?.let { textColor ->
                                                if (0.5f < textColor.luminance()) {
                                                    Black
                                                } else {
                                                    White
                                                }
                                            } ?: MaterialTheme.colorScheme.onSecondaryContainer
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
                                            } ?: MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = "${"%.1f".format(getTitlePercentageOfDay(totalDuration))}%",
                                        style = Typography.titleLarge
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
                DatePickerDialog(
                    onDismissRequest = {
                        dayWiDViewModel.setShowDatePicker(show = false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newDate = Instant
                                    .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                dayWiDViewModel.setCurrentDate(newDate)
                                dayWiDViewModel.setShowDatePicker(show = false)
                            }) {
                            Text(
                                text = "확인",
                                style = Typography.bodyMedium
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                dayWiDViewModel.setShowDatePicker(show = false)
                            }
                        ) {
                            Text(
                                text = "취소",
                                style = Typography.bodyMedium
                            )
                        }
                    }
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
                            text = "날짜 선택",
                            style = Typography.titleLarge
                        )

                        DatePicker(
                            state = datePickerState,
                            title = null,
                            headline = null,
                            showModeToggle = false
                        )
                    }
                }

            }
        }
    )
}