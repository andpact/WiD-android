package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WiDReadDayFragment(navController: NavController, buttonsVisible: MutableState<Boolean>) {
    // 날짜
    val today = LocalDate.now()
    var currentDate by remember { mutableStateOf(today) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = remember(currentDate) { wiDService.readDailyWiDListByDate(currentDate) }
    val dailyAllTitleDurationMap = getDailyAllTitleDurationMap(date = currentDate, wiDList = wiDList)

    // 다이어리
    val diaryService = DiaryService(context = LocalContext.current)
    val diary = remember(currentDate) { diaryService.getDiaryByDate(currentDate) }

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        // 날짜
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = "WiD",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.acme_regular)))
            )

            val dateText = buildAnnotatedString {
                currentDate.let {
                    append(it.format(DateTimeFormatter.ofPattern("M월 d일 (")))
                    withStyle(
                        style = SpanStyle(
                            color = when (it.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            }
                        )
                    ) {
                        append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                    }
                    append(")")
                }
            }

            Text(
                modifier = Modifier
                    .weight(1f),
                text = dateText,
                style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            )

            IconButton(
                onClick = {
                    currentDate = today
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
                    currentDate = currentDate.minusDays(1)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous day"
                )
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.plusDays(1)
                },
                enabled = currentDate != today
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next day"
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp) // item 간에 32.Dp의 공간이 설정됨.
        ) {
            item {
                // 파이 차트
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⭕️ 파이 차트",
                        style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(2f)
                            ) {
                                DayPieChartView(wiDList = wiDList)
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "기록된 시간",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                Text(
                                    text = "${getDailyTotalDurationPercentage(wiDList = wiDList)}%",
                                    style = TextStyle(fontSize = 40.sp, color = if (wiDList.isEmpty()) { Color.Gray } else { Color.Unspecified }, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                                )

                                Text(
                                    text = if (wiDList.isEmpty()) { "기록 없음" } else { "${formatDuration(getDailyTotalDuration(wiDList = wiDList), mode = 1)} / 24시간" },
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // 다이어리
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "다이어리",
                            style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                        )

                        TextButton(
                            onClick = {
                                navController.navigate(Destinations.DiaryFragment.route + "/${currentDate}")

                                buttonsVisible.value = false
                            },
                        ) {
                            Text(text = "수정")
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            if (diary == null) {
                                Icon(
                                    modifier = Modifier
                                        .scale(0.8f)
                                        .padding(vertical = 16.dp),
                                    painter = painterResource(id = R.drawable.outline_textsms_24),
                                    contentDescription = "No diary content",
                                    tint = Color.Gray
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp),
                                    text = "이 날의 다이어리를 작성해 보세요.",
                                    style = TextStyle(color = Color.Gray)
                                )
                            } else {
                                Text(diary.content)
                            }
                        }
                    }
                }
            }

            item {
                // 합계
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "합계",
                        style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (dailyAllTitleDurationMap.isEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .scale(0.8f),
                                        painter = painterResource(id = R.drawable.outline_textsms_24),
                                        contentDescription = "No day total",
                                        tint = Color.Gray
                                    )

                                    Text(
                                        text = "표시할 합계 기록이 없습니다.",
                                        style = TextStyle(color = Color.Gray)
                                    )
                                }
                            } else {
                                for ((title, dayTotal) in dailyAllTitleDurationMap) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = titleMap[title] ?: title,
                                            style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(10.dp)
                                                .background(
                                                    color = colorResource(
                                                        id = colorMap[title] ?: R.color.light_gray
                                                    )
                                                )
                                        )

                                        Spacer(modifier = Modifier.weight(1f))

                                        Text(
                                            text = formatDuration(dayTotal, mode = 2),
                                            style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // 비율
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⭕️ 비율",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                        )
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "비율표시")
                        }
                    }
                }
            }

            item {
                // WiD 리스트
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📜 WiD 리스트",
                        style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (wiDList.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(PaddingValues(bottom = 16.dp)),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .scale(0.8f),
                                        painter = painterResource(id = R.drawable.outline_textsms_24),
                                        tint = Color.Gray,
                                        contentDescription = "detail"
                                    )

                                    Text(
                                        text = "표시할 WiD가 없습니다.",
                                        style = TextStyle(color = Color.Gray)
                                    )
                                }
                            }
                        } else {
                            wiDList.forEachIndexed { index, wiD ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (index == wiDList.size - 1) {
                                                Modifier.padding(PaddingValues(bottom = 16.dp))
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable {
                                                navController.navigate(Destinations.WiDViewFragment.route + "/${wiD.id}")
                                                buttonsVisible.value = false
                                            },
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                    contentDescription = "title"
                                                )

                                                Text(
                                                    text = "제목",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(text = titleMap[wiD.title] ?: wiD.title)

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
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                    contentDescription = "duration"
                                                )

                                                Text(
                                                    text = "소요",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(text = formatDuration(wiD.duration, mode = 2))
                                            }
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_play_arrow_24),
                                                    contentDescription = "finish"
                                                )

                                                Text(
                                                    text = "시작",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(text = wiD.start.format(DateTimeFormatter.ofPattern("a h:mm")))
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                                    contentDescription = "finish"
                                                )

                                                Text(
                                                    text = "종료",
                                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                                )

                                                Text(text = wiD.finish.format(DateTimeFormatter.ofPattern("a h:mm")))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDReadDayFragmentPreview() {
    val navController: NavHostController = rememberNavController()
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDReadDayFragment(navController = navController, buttonsVisible)
}
