package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerFragment(navController: NavController, timerPlayer: TimerPlayer) {
    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    // 타이머
    val itemHeight = 30.dp
    val pickerHeight = itemHeight * 3 + (16.dp * 2) // 아이템 사이의 여백 16을 두 번 추가해줌.
    var timerTopBottomBarVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // 타이머 시간 선택
    var selectedHour by remember { mutableStateOf(0) }
    val lazyHourListState = rememberLazyListState(Int.MAX_VALUE / 2 - 16) // 정중앙의 0을 찾기 위해서 마이너스 16 해줌.
    val isHourScrollInProgress = remember { derivedStateOf { lazyHourListState.isScrollInProgress } }
    val currentHourIndex = remember { derivedStateOf { lazyHourListState.firstVisibleItemIndex } }
    val currentHourScrollOffset = remember { derivedStateOf { lazyHourListState.firstVisibleItemScrollOffset } }

    // 타이머 분 선택
    var selectedMinute by remember { mutableStateOf(0) }
    val lazyMinuteListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isMinuteScrollInProgress = remember { derivedStateOf { lazyMinuteListState.isScrollInProgress } }
    val currentMinuteIndex = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemIndex } }
    val currentMinuteScrollOffset = remember { derivedStateOf { lazyMinuteListState.firstVisibleItemScrollOffset } }

    // 타이머 초 선택
    var selectedSecond by remember { mutableStateOf(0) }
    val lazySecondListState = rememberLazyListState(Int.MAX_VALUE / 2 - 4) // 정중앙의 0을 찾기 위해서 마이너스 4 해줌.
    val isSecondScrollInProgress = remember { derivedStateOf { lazySecondListState.isScrollInProgress } }
    val currentSecondIndex = remember { derivedStateOf { lazySecondListState.firstVisibleItemIndex } }
    val currentSecondScrollOffset = remember { derivedStateOf { lazySecondListState.firstVisibleItemScrollOffset } }

    DisposableEffect(Unit) {
        // Fragment가 나타날 때
        timerPlayer.setInTimerView(true)

        onDispose {
            // Fragment가 사라질 때
            timerPlayer.setInTimerView(false)
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(enabled = timerPlayer.timerState.value == PlayerState.Started) {
                timerTopBottomBarVisible = !timerTopBottomBarVisible
            }
    ) {
        /**
         * 상단 바
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter),
            visible = timerTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.popBackStack()
                        },
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "뒤로 가기",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "타이머",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        /**
         * 컨텐츠
         */
        if (timerPlayer.timerState.value == PlayerState.Stopped) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
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
                            val adjustedIndex = index % 24 // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentHourIndex.value) {
                                selectedHour = (adjustedIndex + 1) % 24 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                val newRemainingTime = selectedHour * 3_600_000L + selectedMinute * 60_000L + selectedSecond * 1_000L
                                timerPlayer.setRemainingTime(newRemainingTime)
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
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
                            val adjustedIndex = index % 60 // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentMinuteIndex.value) {
                                selectedMinute = (adjustedIndex + 1) % 60 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                val newRemainingTime = selectedHour * 3_600_000L + selectedMinute * 60_000L + selectedSecond * 1_000L
                                timerPlayer.setRemainingTime(newRemainingTime)
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
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
                            val adjustedIndex = index % 60 // adjustedIndex는 시간 할당과 표시에만 사용됨.
                            if (index == currentSecondIndex.value) {
                                selectedSecond = (adjustedIndex + 1) % 60 // 가운데 표시된 시간을 사용하기 위해 1을 더해줌.
                                val newRemainingTime = selectedHour * 3_600_000L + selectedMinute * 60_000L + selectedSecond * 1_000L
                                timerPlayer.setRemainingTime(newRemainingTime)
                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
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
                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = formatTimeHorizontally(time = timerPlayer.remainingTime.value),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 60.sp,
                        fontFamily = chivoMonoBlackItalic,
                    )
                )

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    TextButton(
//                        onClick = {
//                            finishTime += 5 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 5m")
//                    }
//
//                    TextButton(
//                        onClick = {
//                            finishTime += 15 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 15m")
//                    }
//
//                    TextButton(
//                        onClick = {
//                            finishTime += 30 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 30m")
//                    }
//
//                    TextButton(
//                        onClick = {
//                            finishTime += 60 * 60 * 1000
//                        }
//                    ) {
//                        Text("+ 60m")
//                    }
//                }
            }
        }

        /**
         * 하단 바
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = timerTopBottomBarVisible,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(timerPlayer.timerState.value == PlayerState.Stopped) {
                            titleMenuExpanded = true
                        }
                        .background(color = AppIndigo)
                        .padding(16.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp),
                        painter = painterResource(titleIconMap[timerPlayer.title.value] ?: R.drawable.baseline_menu_book_16),
                        contentDescription = "제목",
                        tint = White
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (timerPlayer.timerState.value == PlayerState.Paused) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                timerPlayer.stopTimer()

                                // 초기화 버튼을 누르면 0시로 초기화 해버림.
                                coroutineScope.launch {
                                    lazyHourListState.animateScrollToItem(Int.MAX_VALUE / 2 - 16)
                                    lazyMinuteListState.animateScrollToItem(Int.MAX_VALUE / 2 - 4)
                                    lazySecondListState.animateScrollToItem(Int.MAX_VALUE / 2 - 4)
                                }

                                if (!timerTopBottomBarVisible) {
                                    timerTopBottomBarVisible = true
                                }
                            }
                            .background(color = DeepSkyBlue)
                            .padding(16.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(id = R.drawable.baseline_refresh_24),
                            contentDescription = "타이머 초기화",
                            tint = White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(0L < timerPlayer.remainingTime.value) {
                            if (timerPlayer.timerState.value == PlayerState.Started) {
                                timerPlayer.pauseTimer()
                            } else {
                                timerPlayer.startTimer()

                                titleMenuExpanded = false
                            }
                        }
                        .background(
                            color =if (timerPlayer.remainingTime.value <= 0L) DarkGray
                            else if (timerPlayer.timerState.value == PlayerState.Stopped) MaterialTheme.colorScheme.primary
                            else if (timerPlayer.timerState.value == PlayerState.Paused) LimeGreen
                            else OrangeRed
                        )
                        .padding(16.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp),
                        painter = painterResource(
                            id = if (timerPlayer.timerState.value == PlayerState.Started) {
                                R.drawable.baseline_pause_24
                            } else {
                                R.drawable.baseline_play_arrow_24
                            }
                        ),
                        contentDescription = "타이머 시작 및 중지",
                        tint = if (timerPlayer.timerState.value == PlayerState.Stopped) MaterialTheme.colorScheme.secondary else White
                    )
                }
            }
        }

        /**
         * 제목 바텀 시트
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter), // 여기에서 정렬을 설정해야 올바르게 동작함. 아래의 열이 아니라.
            visible = titleMenuExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .height(screenHeight / 2)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    )
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
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(titles.size) { index ->
                        val title = titles[index]
                        val iconResourceId = titleIconMap[title] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(timerPlayer.timerState.value == PlayerState.Stopped) {
                                    timerPlayer.setTitle(title)
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
                                text = titleMap[title] ?: "공부",
                                style = Typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            if (title == timerPlayer.title.value) {
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

//@Preview(showBackground = true)
//@Composable
//fun TimerFragmentPreview() {
//    val dummyNavController = rememberNavController()
//
//    val context = LocalContext.current
//    val dummyApplication = context.applicationContext as Application
//
//    val timerViewModel = TimerPlayer(dummyApplication)
//
//    TimerFragment(dummyNavController, timerViewModel)
//}