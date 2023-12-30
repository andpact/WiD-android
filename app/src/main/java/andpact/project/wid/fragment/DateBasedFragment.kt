package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateBasedFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 날짜
    val today = LocalDate.now()
    var currentDate by remember { mutableStateOf(today) }
    var expandDatePicker by remember { mutableStateOf(false) }
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
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = remember(currentDate) { wiDService.readDailyWiDListByDate(currentDate) }

    // 다이어리
    val diaryService = DiaryService(context = LocalContext.current)
    val diary = remember(currentDate) { diaryService.getDiaryByDate(currentDate) }
    var expandDiary by remember { mutableStateOf(false) }
    var diaryOverflow by remember { mutableStateOf(false) }

    // 합계
    val totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

    // 화면
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//    val dateBasedFragmentHeight = screenHeight - 50.dp - 50.dp - 50.dp // 차례대로 탑 앱 바(50.dp), 날짜 변경 바(50.dp), 하단 네비게이션 바(50.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(GhostWhite),
//                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item("다이어리") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getDayStringWith3Lines(date = currentDate),
                                style = Typography.titleLarge,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (wiDList.isEmpty()) {
                                createNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                            } else {
                                DateBasedPieChartFragment(wiDList = wiDList)
                            }
                        }
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(!expandDiary && diaryOverflow) {
                                expandDiary = true // 한 번 펼치면 다시 접지 못하도록 함.
                            }
                            .padding(16.dp),
                        text = diary?.title ?: "제목을 입력해 주세요.",
                        style = Typography.bodyMedium,
                        minLines = 1,
                        maxLines = if (expandDiary) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { diaryTitleTextLayoutResult: TextLayoutResult ->
                            if (diaryTitleTextLayoutResult.didOverflowHeight) {
                                diaryOverflow = true
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(!expandDiary && diaryOverflow) {
                                expandDiary = true // 한 번 펼치면 다시 접지 못하도록 함.
                            }
                            .padding(16.dp),
                        text = diary?.content ?: "내용을 입력해 주세요.",
                        style = Typography.labelMedium,
                        minLines = 10,
                        maxLines = if (expandDiary) Int.MAX_VALUE else 10,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { diaryContentTextLayoutResult: TextLayoutResult ->
                            if (diaryContentTextLayoutResult.didOverflowHeight) {
                                diaryOverflow = true
                            }
                        }
                    )

                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(
                                color = DeepSkyBlue,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        onClick = {
                            navController.navigate(Destinations.DiaryFragmentDestination.route + "/${currentDate}")

                            mainTopBottomBarVisible.value = false
                        },
                    ) {
                        Text(
                            text = "다이어리 수정",
                            style = Typography.bodyMedium,
                            color = White
                        )
                    }
                }
            }

            item("합계 기록") {
                // 합계 기록
                Column(
                    modifier = Modifier
                        .background(White)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "합계 기록",
                        style = Typography.titleMedium
                    )

                    if (totalDurationMap.isEmpty()) {
                        createEmptyView(text = "표시할 합계 기록이 없습니다.")()
                    } else {
                        totalDurationMap.entries.forEachIndexed { index, (title, totalDuration) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = titleMap[title] ?: title,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )

                                Text(
                                    text = formatDuration(totalDuration, mode = 3),
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = pyeongChangPeaceBold
                                    )
                                )
                            }

                            if (index < totalDurationMap.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item("WiD 리스트") {
                // WiD 리스트
                Column(
                    modifier = Modifier
                        .background(White)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "WiD 리스트",
                        style = Typography.titleMedium
                    )

                    if (wiDList.isEmpty()) {
                        createEmptyView(text = "표시할 WiD가 없습니다.")()
                    } else {
                        wiDList.forEachIndexed { index, wiD ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(Destinations.WiDFragmentDestination.route + "/${wiD.id}")
                                        mainTopBottomBarVisible.value = false
                                    }
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp, 10.dp)
                                                .background(color = colorMap[wiD.title] ?: LightGray)
                                        )

                                        Text(
                                            text = titleMap[wiD.title] ?: wiD.title,
                                            style = Typography.bodyMedium
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "이 WiD로 전환하기",
                                        tint = DeepSkyBlue
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = formatTime(wiD.start, "a hh:mm:ss"),
                                            style = Typography.bodyMedium
                                        )

                                        Text(
                                            text = formatTime(wiD.finish, "a hh:mm:ss"),
                                            style = Typography.bodyMedium
                                        )
                                    }

                                    Text(
                                        text = formatDuration(wiD.duration, mode = 3),
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontFamily = pyeongChangPeaceBold
                                        )
                                    )
                                }
                            }

                            if (index < wiDList.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(80.dp)
                )
            }
        }

        /**
         * 하단 바
         */
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(GhostWhite)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = expandDatePicker,
                enter = expandVertically{ 0 },
                exit = shrinkVertically{ 0 },
            ) {
                Column(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "조회할 날짜를 선택해 주세요.",
                        style = Typography.bodyMedium
                    )

                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        title = null,
                        headline = null,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { expandDatePicker = false }
                        ) {
                            Text(
                                text = "취소",
                                style = Typography.bodyMedium,
                                color = Black
                            )
                        }

                        TextButton(
                            onClick = {
                                expandDatePicker = false
                                currentDate = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()

                                expandDiary = false
                                diaryOverflow = false
                            }
                        ) {
                            Text(
                                text = "확인",
                                style = Typography.bodyMedium,
                                color = Black
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton( // 아이콘 버튼은 기본 설정된 패딩이 없다!!!
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepSkyBlue),
                    onClick = {
                        expandDatePicker = !expandDatePicker
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "날짜 선택",
                        tint = White,
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                IconButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (currentDate != today) Black else Gray),
                    onClick = {
                        if (expandDatePicker) {
                            expandDatePicker = false
                        }
                        currentDate = today

                        expandDiary = false
                        diaryOverflow = false
                    },
                    enabled = currentDate != today,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "오늘 날짜",
                        tint = White,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Black),
                    onClick = {
                        if (expandDatePicker) {
                            expandDatePicker = false
                        }
                        currentDate = currentDate.minusDays(1)

                        expandDiary = false
                        diaryOverflow = false
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "이전 날짜",
                        tint = White,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (currentDate != today) Black else Gray),
                    onClick = {
                        if (expandDatePicker) {
                            expandDatePicker = false
                        }
                        currentDate = currentDate.plusDays(1)

                        expandDiary = false
                        diaryOverflow = false
                    },
                    enabled = currentDate != today
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "다음 날짜",
                        tint = White,
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DateBasedFragmentPreview() {
//    val navController: NavHostController = rememberNavController()
//    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
//    DateBasedFragment(navController = navController, mainTopBottomBarVisible)
//}
