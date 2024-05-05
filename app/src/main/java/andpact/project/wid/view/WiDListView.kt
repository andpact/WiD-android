package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.activity.MainActivityViewDestinations
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.WiDListViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDListView(
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    wiDListViewModel: WiDListViewModel = hiltViewModel()
) {
    val TAG = "WiDListView"

    val titleColorMap = wiDListViewModel.titleColorMap.value

    // 날짜
    val today = LocalDate.now()
    val currentDate = wiDListViewModel.currentDate.value
    val showDatePicker = wiDListViewModel.showDatePicker.value
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
    val wiDListGet = wiDListViewModel.wiDListGet.value
    val fullWiDList = wiDListViewModel.fullWiDList.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        // WiD를 삭제하거나 생성한 후 돌아 왔을 때, 리스트가 갱신되도록.
        wiDListViewModel.setCurrentDate(currentDate)

        onDispose {
            Log.d(TAG, "disposed")

            wiDListViewModel.setShowDatePicker(false)
        }
    }

    BackHandler(
        enabled = showDatePicker,
        onBack = {
            wiDListViewModel.setShowDatePicker(false)
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        topBar = {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
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
                            wiDListViewModel.setShowDatePicker(true)
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
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.minusDays(1)
                            wiDListViewModel.setCurrentDate(newDate)
                        },
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 날짜",
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            enabled = currentDate != today,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.plusDays(1)
                            wiDListViewModel.setCurrentDate(newDate)
                        },
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 날짜",
                    tint = if (currentDate == today) DarkGray else MaterialTheme.colorScheme.primary
                )
            }
        },
        floatingActionButton = {
            if (fullWiDList.size == 1) {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DeepSkyBlue)
                        .padding(16.dp)
                        .size(32.dp)
                        .clickable(
                            enabled = fullWiDList.isEmpty(),
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val emptyWiD = WiD(
                                id = "",
                                date = wiDListViewModel.currentDate.value,
                                title = "제목을 지정해 주세요.",
                                start = LocalTime.MIN,
                                finish = LocalTime.MIN,
                                duration = Duration.ZERO
                            )

                            wiDListViewModel.setEmptyWiD(emptyWiD)

                            onEmptyWiDClicked()
                        },
                    imageVector = Icons.Default.Add,
                    contentDescription = "새로운 WiD",
                    tint = White
                )
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (wiDListGet) {
                    if (fullWiDList.size == 1) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
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
                                .weight(1f),
                        ) {
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
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    HorizontalDivider(thickness = 1.dp)
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
                                                    .background(MaterialTheme.colorScheme.tertiary)
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) {
                                                        val emptyWiD = WiD(
                                                            id = "",
                                                            date = wiDListViewModel.currentDate.value,
                                                            title = "제목을 지정해 주세요.",
                                                            start = wiD.start,
                                                            finish = wiD.finish,
                                                            duration = Duration.between(
                                                                wiD.start,
                                                                wiD.finish
                                                            )
                                                        )

                                                        wiDListViewModel.setEmptyWiD(emptyWiD)

                                                        onEmptyWiDClicked()

                                                    },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "기록 없음",
                                                        style = Typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )

                                                    Text(
                                                        text = getDurationString(wiD.duration, mode = 3),
                                                        style = Typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.primary
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
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Text(
                                                    text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                                    style = Typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary
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
                                                    .background(titleColorMap[wiD.title] ?: DarkGray)
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) {
                                                        wiDListViewModel.setClickedWiD(wiD)

                                                        onWiDClicked() // 화면 전환 용
                                                    },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = wiD.title,
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
                                                    color = MaterialTheme.colorScheme.primary
                                                )

                                                HorizontalDivider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }

                /**
                 * 날짜 대화상자
                 */
                if (showDatePicker) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                enabled = showDatePicker,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                wiDListViewModel.setShowDatePicker(false)
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
                                            wiDListViewModel.setShowDatePicker(false)
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
                                            wiDListViewModel.setShowDatePicker(false)
                                            val newDate = Instant
                                                .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                            wiDListViewModel.setCurrentDate(newDate)
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
    }
}