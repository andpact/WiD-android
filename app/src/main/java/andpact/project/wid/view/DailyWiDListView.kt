package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.DailyWiDListChartView
import andpact.project.wid.model.Title
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.DailyWiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWiDListView(
    onWiDClicked: () -> Unit,
    dailyWiDListViewModel: DailyWiDListViewModel = hiltViewModel()
) {
    val TAG = "DailyWiDListView"

    val WID_LIST_LIMIT_PER_DAY: Int = dailyWiDListViewModel.WID_LIST_LIMIT_PER_DAY

    val NEW_WID: String = dailyWiDListViewModel.NEW_WID
    val LAST_NEW_WID: String = dailyWiDListViewModel.LAST_NEW_WID
    val CURRENT_WID: String = dailyWiDListViewModel.CURRENT_WID

    // 날짜
    val today = dailyWiDListViewModel.today.value
    val now = dailyWiDListViewModel.now.value
    val currentDate = dailyWiDListViewModel.currentDate.value // 조회하려는 날짜
    val showDatePicker = dailyWiDListViewModel.showDatePicker.value
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

    // WiD List
    val fullWiDList = dailyWiDListViewModel.fullWiDList.value
    val filteredWiDList = fullWiDList.filterNot { it.id == NEW_WID || it.id == LAST_NEW_WID }

    // 합계
    val totalDurationMap = dailyWiDListViewModel.totalDurationMap.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    BackHandler(
        enabled = showDatePicker,
        onBack = { dailyWiDListViewModel.setShowDatePicker(show = false) }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // Scaffold 중첩 시 배경색을 자식 뷰도 지정해야함.
        topBar = {
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            dailyWiDListViewModel.setShowDatePicker(show = true)
                        }
                    ) {
                        Text(
                            text = dailyWiDListViewModel.getDateString(currentDate),
                            style = MaterialTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = {
                            val newDate = currentDate.minusDays(1)
                            dailyWiDListViewModel.setCurrentDate(newDate = newDate)
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
                            dailyWiDListViewModel.setCurrentDate(newDate = newDate)
                        },
                        enabled = currentDate != today
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 날짜",
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (fullWiDList.size == 1) {
                FloatingActionButton(
                    onClick = {
                        val isToday = today == currentDate
                        val minTime = LocalTime.MIN
                        val nowOrMax = if (isToday) LocalTime.now() else LocalTime.MAX.withNano(0)

                        val newClickedWiD = WiD.default().copy(
                            id = if (isToday) LAST_NEW_WID else NEW_WID,
                            date = currentDate,
                            start = minTime, // 기록이 없는 날짜기 때문에 무조건 Min에서 시작
                            finish = nowOrMax, // 갱신되기 전에 최초 초기화
                            duration = Duration.between(minTime, nowOrMax)
                        )

                        dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = newClickedWiD)

                        onWiDClicked()
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새로운 기록 생성"
                    )
                }
            }
        },
        content = { contentPadding: PaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    if (fullWiDList.size == 1) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "표시할 기록이 없습니다."
                                    )

                                    Text(text = "표시할 기록이 없습니다.")
                                }
                            )
                        }
                    } else {
                        item {
                            DailyWiDListChartView(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                fullWiDList = fullWiDList,
                                wiDListLimitPerDay = WID_LIST_LIMIT_PER_DAY.toString()
//                                onWiDClicked = { wiD: WiD ->
//                                    dailyWiDListViewModel.setExistingWiD(existingWiD = wiD)
//                                    dailyWiDListViewModel.setUpdatedWiD(updatedWiD = wiD)
//
//                                    onWiDClicked()
//                                }
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
                                    modifier = Modifier
                                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 4.dp),
                                    text = dailyWiDListViewModel.getTimeString(time = LocalTime.MIN),
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    letterSpacing = (-1).sp
                                )

                                HorizontalDivider()
                            }
                        }

                        fullWiDList.forEach { wiD: WiD ->
                            val isNewWiD = wiD.id == NEW_WID || wiD.id == LAST_NEW_WID
                            val isCurrentWiD = wiD.id == CURRENT_WID
                            val enableToCreateNewWiD = filteredWiDList.size <= WID_LIST_LIMIT_PER_DAY

                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    if (isNewWiD) { // 새로운 기록
                                        OutlinedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .padding(horizontal = 16.dp),
                                            onClick = {
                                                dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = wiD)
                                                onWiDClicked()
                                            },
                                            enabled = enableToCreateNewWiD
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxHeight(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                ) {
                                                    Text(
                                                        text = wiD.title.kr,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )

                                                    Text(
                                                        text = dailyWiDListViewModel.getDurationString(wiD.duration),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }

                                                Icon(
                                                    imageVector = Icons.Default.KeyboardArrowRight,
                                                    contentDescription = "이 WiD로 전환하기",
                                                )
                                            }
                                        }
                                    } else { // 현재 기록 또는 기존 기록
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .height(intrinsicSize = IntrinsicSize.Min)
                                                .padding(horizontal = 16.dp),
                                            onClick = {
                                                dailyWiDListViewModel.setClickedWiDAndCopy(clickedWiD = wiD)
                                                onWiDClicked()
                                            },
                                            enabled = !isCurrentWiD
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxHeight(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Image(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clip(MaterialTheme.shapes.medium),
                                                    painter = painterResource(id = wiD.title.smallImage),
                                                    contentDescription = "제목 이미지"
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                ) {
                                                    Text(
                                                        text = wiD.title.kr,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )

                                                    Text(
                                                        text = dailyWiDListViewModel.getDurationString(wiD.duration) + " / " + wiD.createdBy.kr,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }

                                                if (isCurrentWiD) {
                                                    Text(
                                                        modifier = Modifier
                                                            .background(
                                                                color = MaterialTheme.colorScheme.error,
                                                                shape = MaterialTheme.shapes.extraSmall
                                                            )
                                                            .padding(horizontal = 4.dp),
                                                        text = "LIVE",
                                                        color = MaterialTheme.colorScheme.onError
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowRight,
                                                        contentDescription = "이 WiD로 전환하기",
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .background(color = if (wiD.finish == now) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(horizontal = 4.dp),
                                        text = dailyWiDListViewModel.getTimeString(time = wiD.finish),
                                        fontFamily = FontFamily.Monospace,
                                        color = if (wiD.finish == now) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondaryContainer,
                                        letterSpacing = (-1).sp
                                    )

                                    HorizontalDivider()
                                }
                            }
                        }

                        item {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                text = "합계 기록",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        item { // 요소 사이 여백 필요 없으니 하나의 아이템으로 처리
                            totalDurationMap.onEachIndexed { index: Int, (title: Title, totalDuration: Duration) ->
                                ListItem(
                                    leadingContent = {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = MaterialTheme.shapes.medium
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    },
                                    headlineContent = {
                                        Text(text = title.kr)
                                    },
                                    supportingContent = {
                                        Text(text = dailyWiDListViewModel.getDurationString(duration = totalDuration))
                                    },
                                    trailingContent = {
                                        Text(text = dailyWiDListViewModel.getDurationPercentageStringOfDay(duration = totalDuration))
                                    }
                                )

//                                if (index < totalDurationMap.size - 1) {
//                                    HorizontalDivider(
//                                        modifier = Modifier
//                                            .padding(start = (16 + 40 + 8).dp, end = 16.dp),
//                                        thickness = 0.5.dp
//                                    )
//                                }
                            }
                        }
                    }
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        dailyWiDListViewModel.setShowDatePicker(show = false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val newDate = Instant
                                    .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                dailyWiDListViewModel.setCurrentDate(newDate = newDate)
                                dailyWiDListViewModel.setShowDatePicker(show = false)
                            },
                            content = {
                                Text(text = "확인")
                            }
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                dailyWiDListViewModel.setShowDatePicker(show = false)
                            },
                            content = {
                                Text(text = "취소")
                            }
                        )
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        title = {
                            Text(text = "title")
                        },// TODO: 수정!!
                        headline = {
                            Text(text = "headline")
                        },
                        showModeToggle = false
                    )
                }
            }
        }
    )
}