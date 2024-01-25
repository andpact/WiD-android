package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.WiD
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
fun DateBasedFragment(navController: NavController) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
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
                text = "날짜 조회",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider()

        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item("다이어리") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                createNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                            } else {
                                DateBasedPieChartFragment(wiDList = wiDList)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(8.dp),
                                spotColor = MaterialTheme.colorScheme.primary,
                            )
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(!expandDiary && diaryOverflow) {
                                    expandDiary = true // 한 번 펼치면 다시 접지 못하도록 함.
                                }
                                .padding(16.dp),
                            text = diary?.content ?: "",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            minLines = 1,
                            maxLines = if (expandDiary) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { diaryTitleTextLayoutResult: TextLayoutResult ->
                                if (diaryTitleTextLayoutResult.didOverflowHeight) {
                                    diaryOverflow = true
                                }
                            }
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(!expandDiary && diaryOverflow) {
                                    expandDiary = true // 한 번 펼치면 다시 접지 못하도록 함.
                                }
                                .padding(16.dp),
                            text = diary?.content ?: "당신이 이 날 무엇을 하고,\n그 속에서 어떤 생각과 감정을 느꼈는지\n주체적으로 기록해보세요.",
                            textAlign = if (diary == null) TextAlign.Center else null,
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
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

                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(
                                color = AppYellow,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        onClick = {
                            navController.navigate(Destinations.DiaryFragmentDestination.route + "/${currentDate}")
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

            item {
                HorizontalDivider(
                    thickness = 8.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            item("합계 기록") {
                // 합계 기록
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "합계 기록",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (totalDurationMap.isEmpty()) {
                        createEmptyView(text = "표시할 합계 기록이 없습니다.")()
                    } else {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            totalDurationMap.forEach { (title, totalDuration) ->
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp) // 바깥 패딩
                                            .shadow(
                                                elevation = 2.dp,
                                                shape = RoundedCornerShape(8.dp),
                                                spotColor = MaterialTheme.colorScheme.primary,
                                            )
                                            .background(MaterialTheme.colorScheme.secondary)
                                            .padding(vertical = 16.dp), // 안쪽 패딩
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(
                                                        (colorMap[title] ?: DarkGray).copy(
                                                            alpha = 0.1f
                                                        )
                                                    )
                                                    .padding(8.dp),
                                                painter = painterResource(id = titleIconMap[title] ?: R.drawable.baseline_title_24),
                                                contentDescription = "제목",
                                                tint = colorMap[title] ?: DarkGray
                                            )

                                            Text(
                                                text = titleMap[title] ?: title,
                                                style = Typography.titleLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Text(
                                            text = formatDuration(totalDuration, mode = 3),
                                            style = Typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(
                    thickness = 8.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            item("WiD 리스트") {
                // WiD 리스트
                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "WiD 리스트",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (wiDList.isEmpty()) {
                        createEmptyView(text = "표시할 WiD가 없습니다.")()
                    } else {
                        wiDList.forEach { wiD ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .fillMaxHeight()
                                        .background(colorMap[wiD.title] ?: DarkGray)
                                )

                                Row(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = 2.dp,
                                            shape = RoundedCornerShape(8.dp),
                                            spotColor = MaterialTheme.colorScheme.primary,
                                        )
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .clickable {
                                            navController.navigate(Destinations.WiDFragmentDestination.route + "/${wiD.id}")
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "${formatTime(wiD.start, "a hh:mm:ss")} ~ ${formatTime(wiD.finish, "a hh:mm:ss")}",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            text = "${titleMap[wiD.title]} • ${formatDuration(wiD.duration, mode = 3)}",
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
                                            .padding(horizontal = 16.dp)
                                            .size(24.dp),
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "이 WiD로 전환하기",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * 하단 바
         */
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            HorizontalDivider()

            AnimatedVisibility(
                visible = expandDatePicker,
                enter = expandVertically{ 0 },
                exit = shrinkVertically{ 0 },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        text = "조회할 날짜를 선택해 주세요.",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
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
                                color = MaterialTheme.colorScheme.primary
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
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .height(56.dp),
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                        expandDatePicker = !expandDatePicker
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "날짜 선택",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                    },
                    enabled = false
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_title_24),
                        contentDescription = "제목 선택",
                        tint = DarkGray
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(1f),
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
                        tint = if (currentDate != today) MaterialTheme.colorScheme.primary else DarkGray
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(1f),
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(1f),
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
                        tint = if (currentDate != today) MaterialTheme.colorScheme.primary else DarkGray
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
//    DateBasedFragment(navController = navController)
//}
