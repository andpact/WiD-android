package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.AppYellow
import andpact.project.wid.ui.theme.Black
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.getDateStringWith3Lines
import andpact.project.wid.util.getNoBackgroundEmptyViewWithMultipleLines
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
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
fun DayDiaryFragment(navController: NavController) {
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
    val diary = remember(currentDate) { diaryService.readDiaryByDate(currentDate) }

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
                .height(56.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable {
                        navController.navigate(Destinations.DiaryFragmentDestination.route + "/${currentDate}")
                    },
                text = "다이어리 수정",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Icon(
                modifier = Modifier
                    .clickable {
                        expandDatePicker = true
                    }
                    .size(24.dp),
                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                contentDescription = "날짜 선택",
                tint = MaterialTheme.colorScheme.primary
            )

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
                        if (expandDatePicker) {
                            expandDatePicker = false
                        }
                        currentDate = currentDate.minusDays(1)

//                        expandDiary = false
//                        diaryOverflow = false
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 날짜",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .clickable(enabled = currentDate != today) {
                        if (expandDatePicker) {
                            expandDatePicker = false
                        }
                        currentDate = currentDate.plusDays(1)

//                        expandDiary = false
//                        diaryOverflow = false
                    }
                    .size(24.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 날짜",
                tint = if (currentDate != today) MaterialTheme.colorScheme.primary else DarkGray
            )
        }

        /**
         * 다이어리
         */
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
                    modifier = Modifier
                        .clickable {
                            expandDatePicker = true
                        },
                    text = getDateStringWith3Lines(date = currentDate),
                    style = Typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f / 1f),
                contentAlignment = Alignment.Center
            ) {
                if (wiDList.isEmpty()) {
                    getNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                } else {
                    DateBasedPieChartFragment(wiDList = wiDList)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
//                .shadow(
//                    elevation = 2.dp,
//                    shape = RoundedCornerShape(8.dp),
//                    spotColor = MaterialTheme.colorScheme.primary,
//                )
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
//                    .clickable(!expandDiary && diaryOverflow) {
//                        expandDiary = true // 한 번 펼치면 다시 접지 못하도록 함.
//                    }
                    .padding(16.dp),
                text = diary?.title ?: "",
                style = Typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                minLines = 1,
//                maxLines = if (expandDiary) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
//                onTextLayout = { diaryTitleTextLayoutResult: TextLayoutResult ->
//                    if (diaryTitleTextLayoutResult.didOverflowHeight) {
//                        diaryOverflow = true
//                    }
//                }
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
//                    .clickable(!expandDiary && diaryOverflow) {
//                        expandDiary = true // 한 번 펼치면 다시 접지 못하도록 함.
//                    }
                    .padding(16.dp),
                text = diary?.content ?: "당신이 이 날 무엇을 하고,\n그 속에서 어떤 생각과 감정을 느꼈는지\n주체적으로 기록해보세요.",
                textAlign = if (diary == null) TextAlign.Center else null,
                style = if (diary == null) Typography.labelMedium else Typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                minLines = 10,
//                maxLines = if (expandDiary) Int.MAX_VALUE else 10,
                overflow = TextOverflow.Ellipsis,
//                onTextLayout = { diaryContentTextLayoutResult: TextLayoutResult ->
//                    if (diaryContentTextLayoutResult.didOverflowHeight) {
//                        diaryOverflow = true
//                    }
//                }
            )
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
                                    .atZone(
                                        ZoneId.systemDefault()
                                    )
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