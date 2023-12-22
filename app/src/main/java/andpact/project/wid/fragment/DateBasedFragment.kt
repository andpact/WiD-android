package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.WiD
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.pyeongChangPeaceBold
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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

    /**
     * 전체 화면
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
    ) {
        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item("다이어리") {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f),
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 1.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (wiDList.isEmpty()) {
                                    createNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                                } else {
                                    DateBasedPieChartFragment(wiDList = wiDList)
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
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

                            HorizontalDivider()

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
                        }
                    }

                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(id = R.color.deep_sky_blue),
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
                            color = Color.White
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(8.dp)
                        .background(Color.White)
                )
            }

            item("합계 기록") {
                // 합계
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "합계 기록",
                        style = Typography.titleMedium
                    )

                    if (totalDurationMap.isEmpty()) {
                        createEmptyView(text = "표시할 합계 기록이 없습니다.")()
                    } else {
                        for ((title, totalDuration) in totalDurationMap) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    colorResource(
                                                        id = colorMap[title]
                                                            ?: R.color.light_gray
                                                    ),
                                                    Color.White,
                                                )
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
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
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(8.dp)
                        .background(Color.White)
                )
            }

            item("WiD 리스트") {
                // WiD 리스트
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "WiD 리스트",
                        style = Typography.titleMedium
                    )

                    if (wiDList.isEmpty()) {
                        createEmptyView(text = "표시할 WiD가 없습니다.")()
                    } else {
                        wiDList.forEach { wiD: WiD ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate(Destinations.WiDFragmentDestination.route + "/${wiD.id}")
                                            mainTopBottomBarVisible.value = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(color = colorResource(id = R.color.light_gray))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .size(10.dp)
                                                    .background(
                                                        color = colorResource(
                                                            id = colorMap[wiD.title]
                                                                ?: R.color.light_gray
                                                        )
                                                    )
                                            )

                                            Text(
                                                text = titleMap[wiD.title] ?: wiD.title,
                                                style = Typography.bodyMedium
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "이 WiD로 전환하기",
                                            tint = colorResource(id = R.color.deep_sky_blue)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
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
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )
            }
        }

        HorizontalDivider()

        /**
         * 하단 바
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            AnimatedVisibility(
                visible = expandDatePicker,
                enter = expandVertically{ 0 },
                exit = shrinkVertically{ 0 },
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = "날짜 선택",
                        style = Typography.titleMedium,
                        textAlign = TextAlign.Start
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
                                color = Color.Black
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
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        expandDatePicker = !expandDatePicker
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "날짜 선택",
                    )
                }

                IconButton(
                    onClick = {
                        if (expandDatePicker) {
                            expandDatePicker = false
                        }
                        navController.navigate(Destinations.DiaryFragmentDestination.route + "/${currentDate}")

                        mainTopBottomBarVisible.value = false
                    },
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "다이어리 수정",
                        tint = Color.Transparent
                    )
                }

                IconButton(
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
                        contentDescription = "Today"
                    )
                }

                IconButton(
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
                        contentDescription = "Previous day"
                    )
                }

                IconButton(
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
                        contentDescription = "Next day"
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
