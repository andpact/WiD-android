package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun WiDListView(
//    onEmptyWiDClicked: () -> Unit,
//    onWiDClicked: () -> Unit,
//    wiDListViewModel: WiDListViewModel = hiltViewModel()
//) {
//    val TAG = "WiDListView"
//
//    val titleColorMap = wiDListViewModel.titleColorMap
//
//    // 날짜
//    val today = LocalDate.now()
//    val currentDate = wiDListViewModel.currentDate.value
//    val showDatePicker = wiDListViewModel.showDatePicker.value
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = System.currentTimeMillis() + (9 * 60 * 60 * 1000),
//        selectableDates = object : SelectableDates {
//            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
//                // utcTimeMillis는 KST를 반환하는 듯하고,
//                // System.currentTimeMillis()가 (KST -9시간)을 반환하는 듯.
//                return utcTimeMillis <= System.currentTimeMillis() + (9 * 60 * 60 * 1000)
//            }
//
//            override fun isSelectableYear(year: Int): Boolean {
//                val currentYear = LocalDate.now().year
//                return year <= currentYear
//            }
//        }
//    )
//
//    // WiD
//    val wiDListLoaded = wiDListViewModel.wiDListLoaded.value
//    val fullWiDList = wiDListViewModel.fullWiDList.value
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
//        // WiD를 삭제하거나 생성한 후 돌아 왔을 때, 리스트가 갱신되도록.
//        wiDListViewModel.setCurrentDate(currentDate)
//
//        onDispose {
//            Log.d(TAG, "disposed")
//
//            wiDListViewModel.setShowDatePicker(false)
//        }
//    }
//
//    BackHandler(
//        enabled = showDatePicker,
//        onBack = {
//            wiDListViewModel.setShowDatePicker(false)
//        }
//    )
//
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize(),
//        containerColor = MaterialTheme.colorScheme.surface,
//        topBar = {
//            Row(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .fillMaxWidth()
//                    .height(56.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                TextButton(
//                    onClick = {
//                        wiDListViewModel.setShowDatePicker(true)
//                    }
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                            contentDescription = "날짜",
//                        )
//
//                        Text(
//                            text = getDateString(currentDate),
//                            style = Typography.titleLarge,
//                        )
//                    }
//                }
//
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                )
//
//                FilledTonalIconButton(
//                    onClick = {
//                        val newDate = currentDate.minusDays(1)
//                        wiDListViewModel.setCurrentDate(newDate)
//                    },
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowLeft,
//                        contentDescription = "이전 날짜",
//                    )
//                }
//
//                FilledTonalIconButton(
//                    onClick = {
//                        val newDate = currentDate.plusDays(1)
//                        wiDListViewModel.setCurrentDate(newDate)
//                    },
//                    enabled = currentDate != today
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowRight,
//                        contentDescription = "다음 날짜"
//                    )
//                }
//            }
//        },
//        floatingActionButton = {
//            if (fullWiDList.size == 1) {
//                FloatingActionButton(
//                    onClick = {
//                        val emptyWiD = WiD(
//                            id = "",
//                            date = wiDListViewModel.currentDate.value,
//                            title = "무엇을 하셨나요?",
//                            start = LocalTime.MIN,
//                            finish = LocalTime.MIN,
//                            duration = Duration.ZERO
//                        )
//
//                        wiDListViewModel.setEmptyWiD(emptyWiD)
//
//                        onEmptyWiDClicked()
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = "새로운 WiD",
//                    )
//                }
//            }
//        },
//    ) { contentPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(contentPadding),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            if (wiDListLoaded) {
//                if (fullWiDList.size == 1) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
//                    ) {
//                        Icon(
//                            modifier = Modifier
//                                .size(48.dp),
//                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                            contentDescription = "표시할 WiD가 없습니다.",
//                        )
//
//                        Text(
//                            text = "표시할 WiD가 없습니다.",
//                            style = Typography.titleLarge,
//                        )
//
//                        Text(
//                            text = "WiD를 생성하여\n하루를 어떻게 보냈는지\n기록해 보세요.",
//                            style = Typography.bodyMedium,
//                            textAlign = TextAlign.Center
//                        )
//                    }
//                } else {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f),
//                    ) {
//                        item {
//                            Row(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(16.dp)
//                            ) {
//                                Text(
//                                    text = getTimeString(LocalTime.MIN, "a hh:mm:ss"),
//                                    style = Typography.bodyMedium,
//                                )
//
//                                HorizontalDivider()
//                            }
//
//                            fullWiDList.forEach { wiD ->
//                                if (wiD.id.isBlank()) { // 빈 WiD
//                                    Column(
//                                        modifier = Modifier
//                                            .padding(horizontal = 16.dp)
//                                    ) {
//                                        Row(
//                                            modifier = Modifier
//                                                .clip(RoundedCornerShape(8))
//                                                .background(MaterialTheme.colorScheme.secondaryContainer)
//                                                .clickable(
//                                                    interactionSource = remember { MutableInteractionSource() },
//                                                    indication = null
//                                                ) {
//                                                    val emptyWiD = WiD(
//                                                        id = "",
//                                                        date = wiDListViewModel.currentDate.value,
//                                                        title = "무엇을 하셨나요?",
//                                                        start = wiD.start,
//                                                        finish = wiD.finish,
//                                                        duration = Duration.between(
//                                                            wiD.start,
//                                                            wiD.finish
//                                                        )
//                                                    )
//
//                                                    wiDListViewModel.setEmptyWiD(emptyWiD = emptyWiD)
//
//                                                    onEmptyWiDClicked()
//
//                                                },
//                                            verticalAlignment = Alignment.CenterVertically
//                                        ) {
//                                            Icon(
//                                                modifier = Modifier
//                                                    .padding(16.dp)
//                                                    .size(24.dp),
//                                                painter = painterResource(
//                                                    id = titleNumberStringToTitleIconMap[wiD.title] ?: R.drawable.baseline_title_24
//                                                ),
//                                                contentDescription = "제목",
//                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
//                                            )
//
//                                            Column(
//                                                modifier = Modifier
//                                                    .padding(16.dp),
//                                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                                            ) {
//                                                Text(
//                                                    text = "무엇을 하셨나요?",
//                                                    style = Typography.bodyMedium,
//                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                                                )
//
//                                                Text(
//                                                    text = getDurationString(wiD.duration, mode = 3),
//                                                    style = Typography.bodyMedium,
//                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                                                )
//                                            }
//
//                                            Spacer(
//                                                modifier = Modifier
//                                                    .weight(1f)
//                                            )
//
//                                            Icon(
//                                                modifier = Modifier
//                                                    .padding(16.dp)
//                                                    .size(24.dp),
//                                                imageVector = Icons.Default.KeyboardArrowRight,
//                                                contentDescription = "이 WiD로 전환하기",
//                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
//                                            )
//                                        }
//
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
//                                        ) {
//                                            Text(
//                                                text = getTimeString(wiD.finish, "a hh:mm:ss"),
//                                                style = Typography.bodyMedium,
//                                                color = MaterialTheme.colorScheme.onSecondaryContainer
//                                            )
//
//                                            HorizontalDivider()
//                                        }
//                                    }
//                                } else {
//                                    Column(
//                                        modifier = Modifier
//                                            .padding(horizontal = 16.dp)
//                                    ) {
//                                        Row(
//                                            modifier = Modifier
//                                                .clip(RoundedCornerShape(8))
//                                                .background(
//                                                    titleColorMap[wiD.title]
//                                                        ?: MaterialTheme.colorScheme.secondaryContainer
//                                                )
//                                                .clickable(
//                                                    interactionSource = remember { MutableInteractionSource() },
//                                                    indication = null
//                                                ) {
//                                                    wiDListViewModel.setExistingWiD(existingWiD = wiD)
//                                                    wiDListViewModel.setUpdatedWiD(updatedWiD = wiD)
//
//                                                    onWiDClicked() // 화면 전환 용
//                                                },
//                                            verticalAlignment = Alignment.CenterVertically
//                                        ) {
//                                            Icon(
//                                                modifier = Modifier
//                                                    .padding(16.dp)
//                                                    .size(24.dp),
//                                                painter = painterResource(
//                                                    id = titleNumberStringToTitleIconMap[wiD.title] ?: R.drawable.baseline_title_24
//                                                ),
//                                                contentDescription = "제목",
//                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
//                                            )
//
//                                            Column(
//                                                modifier = Modifier
//                                                    .padding(16.dp),
//                                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                                            ) {
//                                                Text(
//                                                    text = titleNumberStringToTitleKRStringMap[wiD.title] ?: "",
//                                                    style = Typography.bodyMedium,
//                                                    color = titleColorMap[wiD.title]?.let { textColor ->
//                                                        if (0.5f < textColor.luminance()) {
//                                                            Black
//                                                        } else {
//                                                            White
//                                                        }
//                                                    } ?: MaterialTheme.colorScheme.primary
//                                                )
//
//                                                Text(
//                                                    text = getDurationString(wiD.duration, mode = 3),
//                                                    style = Typography.bodyMedium,
//                                                    color = titleColorMap[wiD.title]?.let { textColor ->
//                                                        if (0.5f < textColor.luminance()) {
//                                                            Black
//                                                        } else {
//                                                            White
//                                                        }
//                                                    } ?: MaterialTheme.colorScheme.primary
//                                                )
//                                            }
//
//                                            Spacer(
//                                                modifier = Modifier
//                                                    .weight(1f)
//                                            )
//
//                                            Icon(
//                                                modifier = Modifier
//                                                    .padding(16.dp)
//                                                    .size(24.dp),
//                                                imageVector = Icons.Default.KeyboardArrowRight,
//                                                contentDescription = "이 WiD로 전환하기",
//                                                tint = titleColorMap[wiD.title]?.let { textColor ->
//                                                    if (0.5f < textColor.luminance()) {
//                                                        Black
//                                                    } else {
//                                                        White
//                                                    }
//                                                } ?: MaterialTheme.colorScheme.primary
//                                            )
//                                        }
//
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
//                                        ) {
//                                            Text(
//                                                text = getTimeString(wiD.finish, "a hh:mm:ss"),
//                                                style = Typography.bodyMedium,
//                                                color = MaterialTheme.colorScheme.onSecondaryContainer
//                                            )
//
//                                            HorizontalDivider()
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                CircularProgressIndicator()
//            }
//        }
//
//        if (showDatePicker) {
//            DatePickerDialog(
//                onDismissRequest = {
//                    wiDListViewModel.setShowDatePicker(false)
//                },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            val newDate = Instant
//                                .ofEpochMilli(datePickerState.selectedDateMillis!!)
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDate()
//
//                            wiDListViewModel.setCurrentDate(newDate)
//                            wiDListViewModel.setShowDatePicker(false)
//                    }) {
//                        Text(
//                            text = "확인",
//                            style = Typography.bodyMedium
//                        )
//                    }
//                },
//                dismissButton = {
//                    TextButton(
//                        onClick = {
//                            wiDListViewModel.setShowDatePicker(false)
//                        }
//                    ) {
//                        Text(
//                            text = "취소",
//                            style = Typography.bodyMedium
//                        )
//                    }
//                }
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .padding(16.dp),
//                        text = "날짜 선택",
//                        style = Typography.titleLarge
//                    )
//
//                    DatePicker(
//                        state = datePickerState,
//                        title = null,
//                        headline = null,
//                        showModeToggle = false
//                    )
//                }
//            }
//        }
//    }
//}