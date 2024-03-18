package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.Duration

@Composable
fun TimerFragment(timerViewModel: TimerViewModel) {
    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }

    // 타이머
    val itemHeight = 30.dp
    val pickerHeight = itemHeight * 3 + (16.dp * 2) // 아이템 사이의 여백 16을 두 번 추가해줌.
    val coroutineScope = rememberCoroutineScope()

    // 타이머 시간 선택
    var selectedHour by remember { mutableStateOf(0L) }
    val lazyHourListState = rememberLazyListState(Int.MAX_VALUE / 2 - 16) // 정중앙의 0을 찾기 위해서 마이너스 16 해줌.
    val isHourScrollInProgress = remember { derivedStateOf { lazyHourListState.isScrollInProgress } }
    val currentHourIndex = remember { derivedStateOf { lazyHourListState.firstVisibleItemIndex } }
    val currentHourScrollOffset = remember { derivedStateOf { lazyHourListState.firstVisibleItemScrollOffset } }

    // 타이머 분 선택
    var selectedMinute by remember { mutableStateOf(0L) }
    val lazyMinuteListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isMinuteScrollInProgress = remember { derivedStateOf { lazyMinuteListState.isScrollInProgress } }
    val currentMinuteIndex = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemIndex } }
    val currentMinuteScrollOffset = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemScrollOffset } }

    // 타이머 초 선택
    var selectedSecond by remember { mutableStateOf(0L) }
    val lazySecondListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isSecondScrollInProgress = remember { derivedStateOf { lazySecondListState.isScrollInProgress } }
    val currentSecondIndex = remember { derivedStateOf { lazySecondListState.firstVisibleItemIndex } }
    val currentSecondScrollOffset = remember { derivedStateOf { lazySecondListState.firstVisibleItemScrollOffset } }

    DisposableEffect(Unit) {
        Log.d("TimerFragment", "TimerFragment is being composed")

        onDispose {
            Log.d("TimerFragment", "TimerFragment is being disposed")
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = titleMenuExpanded) {
        titleMenuExpanded = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(
                enabled = timerViewModel.timerState.value == PlayerState.Started,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                timerViewModel.setTimerTopBottomBarVisible(!timerViewModel.timerTopBottomBarVisible.value)
            }
    ) {
        /**
         * 컨텐츠
         */
        if (timerViewModel.timerState.value == PlayerState.Stopped) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
//                    .padding(bottom = screenHeight / 2),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 시간 선택
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(pickerHeight),
                        state = lazyHourListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Interactive mode를 켜야 프리뷰에서 스크롤이 동작한다.
                        items(count = Int.MAX_VALUE) {index ->
                            val adjustedIndex = index % 24L // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentHourIndex.value) {
                                selectedHour = (adjustedIndex + 1) % 24 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                val newRemainingTime = Duration.ofHours(selectedHour.toLong()) + Duration.ofMinutes(selectedMinute.toLong()) + Duration.ofSeconds(selectedSecond.toLong())
                                timerViewModel.setSelectedTime(newRemainingTime)
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            lazyHourListState.animateScrollToItem(index - 1) // 아이템 클릭 시 열의 가운데로 오도록 함.
                                        }
                                    },
                                text = "$adjustedIndex",
                                style = TextStyle(
                                    fontSize = if (index == currentHourIndex.value + 1) 30.sp else 20.sp,
                                    fontFamily = if (index == currentHourIndex.value + 1) chivoMonoBlackItalic else null,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (index == currentHourIndex.value + 1) FontWeight.Bold else null,
                                    color = if (index == currentHourIndex.value + 1) MaterialTheme.colorScheme.primary else DarkGray
                                )
                            )
                        }
                    }

                    Text(
                        text = ":",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 분 선택
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(pickerHeight),
                        state = lazyMinuteListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(count = Int.MAX_VALUE) {index ->
                            val adjustedIndex = index % 60L // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentMinuteIndex.value) {
                                selectedMinute = (adjustedIndex + 1) % 60 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                val newRemainingTime = Duration.ofHours(selectedHour) + Duration.ofMinutes(selectedMinute) + Duration.ofSeconds(selectedSecond)
                                timerViewModel.setSelectedTime(newRemainingTime)
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            lazyMinuteListState.animateScrollToItem(index - 1) // 아이템 클릭 시 열의 가운데로 오도록 함.
                                        }
                                    },
                                text = "$adjustedIndex",
                                style = TextStyle(
                                    fontSize = if (index == currentMinuteIndex.value + 1) 30.sp else 20.sp,
                                    fontFamily = if (index == currentMinuteIndex.value + 1) chivoMonoBlackItalic else null,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (index == currentMinuteIndex.value + 1) FontWeight.Bold else null,
                                    color = if (index == currentMinuteIndex.value + 1) MaterialTheme.colorScheme.primary else DarkGray
                                )
                            )
                        }
                    }

                    Text(
                        text = ":",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 초 선택
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(pickerHeight),
                        state = lazySecondListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(count = Int.MAX_VALUE) {index ->
                            val adjustedIndex = index % 60L // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentSecondIndex.value) {
                                selectedSecond = (adjustedIndex + 1) % 60 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                val newRemainingTime = Duration.ofHours(selectedHour) + Duration.ofMinutes(selectedMinute) + Duration.ofSeconds(selectedSecond)
                                timerViewModel.setSelectedTime(newRemainingTime)
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            lazySecondListState.animateScrollToItem(index - 1) // 아이템 클릭 시 열의 가운데로 오도록 함.
                                        }
                                    },
                                text = "$adjustedIndex",
                                style = TextStyle(
                                    fontSize = if (index == currentSecondIndex.value + 1) 30.sp else 20.sp,
                                    fontFamily = if (index == currentSecondIndex.value + 1) chivoMonoBlackItalic else null,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (index == currentSecondIndex.value + 1) FontWeight.Bold else null,
                                    color = if (index == currentSecondIndex.value + 1) MaterialTheme.colorScheme.primary else DarkGray
                                )
                            )
                        }
                    }

                    if (!isHourScrollInProgress.value) {
                        coroutineScope.launch {
                            if (currentHourScrollOffset.value < pickerHeight.value / 2) {
                                if (lazyHourListState.layoutInfo.totalItemsCount == 0)
                                    return@launch
                                lazyHourListState.animateScrollToItem(index = currentHourIndex.value)
                            } else {
                                lazyHourListState.animateScrollToItem(index = currentHourIndex.value + 1)
                            }
                        }
                    }

                    if (!isMinuteScrollInProgress.value) {
                        coroutineScope.launch {
                            if (currentMinuteScrollOffset.value < pickerHeight.value / 2) {
                                if (lazyMinuteListState.layoutInfo.totalItemsCount == 0)
                                    return@launch
                                lazyMinuteListState.animateScrollToItem(index = currentMinuteIndex.value)
                            } else {
                                lazyMinuteListState.animateScrollToItem(index = currentMinuteIndex.value + 1)
                            }
                        }
                    }

                    if (!isSecondScrollInProgress.value) {
                        coroutineScope.launch {
                            if (currentSecondScrollOffset.value < pickerHeight.value / 2) {
                                if (lazySecondListState.layoutInfo.totalItemsCount == 0)
                                    return@launch
                                lazySecondListState.animateScrollToItem(index = currentSecondIndex.value)
                            } else {
                                lazySecondListState.animateScrollToItem(index = currentSecondIndex.value + 1)
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            // 타이머 남은 시간 텍스트 뷰
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = screenHeight / 2),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = getTimerTimeString(duration = timerViewModel.remainingTime.value),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }

        /**
         * 하단 바
         */
        if (timerViewModel.timerTopBottomBarVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            enabled = timerViewModel.timerState.value == PlayerState.Stopped,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            titleMenuExpanded = true
                        }
                        .background(
                            color = if (timerViewModel.timerState.value == PlayerState.Stopped)
                                AppIndigo
                            else
                                DarkGray
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(titleIconMap[timerViewModel.title.value] ?: R.drawable.baseline_menu_book_16),
                    contentDescription = "제목",
                    tint = White
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (timerViewModel.timerState.value == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                timerViewModel.stopTimer()

                                // 초기화 버튼을 누르면 0시로 초기화 해버림.
                                coroutineScope.launch {
                                    lazyHourListState.animateScrollToItem(Int.MAX_VALUE / 2 - 16)
                                    lazyMinuteListState.animateScrollToItem(Int.MAX_VALUE / 2 - 4)
                                    lazySecondListState.animateScrollToItem(Int.MAX_VALUE / 2 - 4)
                                }
                            }
                            .background(color = DeepSkyBlue)
                            .padding(16.dp)
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "타이머 초기화",
                        tint = White
                    )
                }

                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            enabled = Duration.ZERO < timerViewModel.seletedTime.value,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (timerViewModel.timerState.value == PlayerState.Started) {
                                timerViewModel.pauseTimer()
                            } else {
                                timerViewModel.startTimer()

                                titleMenuExpanded = false
                            }
                        }
                        .background(
                            color = if (timerViewModel.seletedTime.value <= Duration.ZERO) DarkGray
                            else if (timerViewModel.timerState.value == PlayerState.Stopped) MaterialTheme.colorScheme.primary
                            else if (timerViewModel.timerState.value == PlayerState.Paused) LimeGreen
                            else OrangeRed
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(
                        id = if (timerViewModel.timerState.value == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "타이머 시작 및 중지",
                    tint = if (timerViewModel.timerState.value == PlayerState.Stopped) MaterialTheme.colorScheme.secondary else White
                )
            }
        }

        /**
         * 제목 바텀 시트
         */
        if (titleMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = titleMenuExpanded,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        titleMenuExpanded = false
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(screenHeight / 2)
                        .padding(16.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = MaterialTheme.colorScheme.primary,
                        )
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(titles.size) { index ->
                            val itemTitle = titles[index]
                            val iconResourceId = titleIconMap[itemTitle] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        enabled = timerViewModel.timerState.value == PlayerState.Stopped,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        timerViewModel.setTitle(itemTitle)
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

                                RadioButton(
                                    selected = itemTitle == timerViewModel.title.value,
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