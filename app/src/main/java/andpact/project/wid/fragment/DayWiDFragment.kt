package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWiDFragment() {
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
//    val diaryService = DiaryService(context = LocalContext.current)
//    val diary = remember(currentDate) { diaryService.readDiaryByDate(currentDate) }
//    var expandDiary by remember { mutableStateOf(false) }
//    var diaryOverflow by remember { mutableStateOf(false) }

    // 합계
    val totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
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
                    .clickable {
                        expandDatePicker = true
                    },
                text = getDateString(currentDate),
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

//            Icon(
//                modifier = Modifier
//                    .clickable {
//                        expandDatePicker = true
//                    }
//                    .size(24.dp),
//                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                contentDescription = "날짜 선택",
//                tint = MaterialTheme.colorScheme.primary
//            )

//            Icon(
//                modifier = Modifier
//                    .clickable(enabled = false) {
//                        // 클릭 이벤트를 처리할 내용
//                    }
//                    .size(24.dp),
//                painter = painterResource(R.drawable.baseline_title_24),
//                contentDescription = "제목 선택",
//                tint = DarkGray
//            )

//            Icon(
//                modifier = Modifier
//                    .clickable(enabled = currentDate != today) {
//                        if (expandDatePicker) {
//                            expandDatePicker = false
//                        }
//                        currentDate = today
//
////                        expandDiary = false
////                        diaryOverflow = false
//                    }
//                    .size(24.dp),
//                imageVector = Icons.Filled.Refresh,
//                contentDescription = "오늘 날짜",
//                tint = if (currentDate != today) MaterialTheme.colorScheme.primary else DarkGray
//            )

            Icon(
                modifier = Modifier
                    .clickable {
//                        if (expandDatePicker) {
//                            expandDatePicker = false
//                        }

                        currentDate = currentDate.minusDays(1)
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 날짜",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable(enabled = currentDate != today) {
//                        if (expandDatePicker) {
//                            expandDatePicker = false
//                        }

                        currentDate = currentDate.plusDays(1)
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 날짜",
                tint = if (currentDate == today) DarkGray else MaterialTheme.colorScheme.primary
            )
        }

        /**
         * 컨텐츠
         */
        if (wiDList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "표시할 데이터가 없습니다.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // 파이 차트
                item {
                    DateBasedPieChartFragment(wiDList = wiDList)
                }

                totalDurationMap.forEach { (title, totalDuration) ->
                    item {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8))
                                .background(colorMap[title] ?: DarkGray),
//                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(8.dp)
                                    .size(24.dp),
                                painter = painterResource(id = titleIconMap[title] ?: R.drawable.baseline_title_24),
                                contentDescription = "제목",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "${titleMap[title]}",
                                style = Typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = getDurationString(totalDuration, mode = 3),
                                style = Typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                    // 합계 기록
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        LazyVerticalGrid(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                                .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
//                            columns = GridCells.Fixed(2),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        ) {
//                            totalDurationMap.forEach { (title, totalDuration) ->
//                                item {
//                                    Row(
//                                        modifier = Modifier
//                                            .clip(RoundedCornerShape(8))
//                                            .background(colorMap[title] ?: DarkGray),
////                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Icon(
//                                            modifier = Modifier
//                                                .padding(16.dp)
//                                                .clip(CircleShape)
//                                                .background(MaterialTheme.colorScheme.secondary)
//                                                .padding(8.dp)
//                                                .size(24.dp),
//                                            painter = painterResource(id = titleIconMap[title] ?: R.drawable.baseline_title_24),
//                                            contentDescription = "제목",
//                                            tint = MaterialTheme.colorScheme.primary
//                                        )
//
//                                        Column(
////                                        modifier = Modifier
////                                            .padding(16.dp),
//                                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                                        ) {
//                                            Text(
//                                                text = "${titleMap[title]}",
//                                                style = Typography.bodyMedium,
//                                                color = MaterialTheme.colorScheme.primary
//                                            )
//
//                                            Text(
//                                                text = getDurationString(totalDuration, mode = 3),
//                                                style = Typography.bodyMedium,
//                                                color = MaterialTheme.colorScheme.primary
//                                            )
//                                        }
//
//                                        Spacer(
//                                            modifier = Modifier
//                                                .weight(1f)
//                                        )
//                                    }

//                                    Column(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(vertical = 4.dp) // 바깥 패딩
//                                            .shadow(
//                                                elevation = 2.dp,
//                                                shape = RoundedCornerShape(8.dp),
//                                                spotColor = MaterialTheme.colorScheme.primary,
//                                            )
//                                            .background(MaterialTheme.colorScheme.secondary)
//                                            .padding(vertical = 16.dp), // 안쪽 패딩
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                                    ) {
//                                        Row(
//                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                            verticalAlignment = Alignment.CenterVertically
//                                        ) {
//                                            Icon(
//                                                modifier = Modifier
//                                                    .clip(CircleShape)
//                                                    .background(
//                                                        (colorMap[title] ?: DarkGray).copy(
//                                                            alpha = 0.1f
//                                                        )
//                                                    )
//                                                    .padding(8.dp)
//                                                    .size(24.dp),
//                                                painter = painterResource(id = titleIconMap[title] ?: R.drawable.baseline_title_24),
//                                                contentDescription = "제목",
//                                                tint = colorMap[title] ?: DarkGray
//                                            )
//
//                                            Text(
//                                                text = titleMap[title] ?: title,
//                                                style = Typography.titleLarge,
//                                                color = MaterialTheme.colorScheme.primary
//                                            )
//                                        }
//
//                                        Text(
//                                            text = getDurationString(totalDuration, mode = 3),
//                                            style = Typography.titleLarge,
//                                            color = MaterialTheme.colorScheme.primary
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
    }

    /**
     * 대화상자
     */
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = expandDatePicker,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(expandDatePicker) {
                    expandDatePicker = false
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
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
                        .clickable(false) {}
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .clickable {
                                expandDatePicker = false
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
                            .clickable {
                                expandDatePicker = false
                                currentDate = Instant
                                    .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

//                                expandDiary = false
//                                diaryOverflow = false
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

//@Preview(showBackground = true)
//@Composable
//fun DateBasedFragmentPreview() {
//    val navController: NavHostController = rememberNavController()
//    DateBasedFragment(navController = navController)
//}
