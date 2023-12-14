package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.WiD
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateBasedFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 날짜
    val today = LocalDate.now()
    var currentDate by remember { mutableStateOf(today) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = remember(currentDate) { wiDService.readDailyWiDListByDate(currentDate) }

    // 다이어리
    val diaryService = DiaryService(context = LocalContext.current)
    val diary = remember(currentDate) { diaryService.getDiaryByDate(currentDate) }

    // 합계
    val totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val dateBasedFragmentHeight = screenHeight - 50.dp - 50.dp - 50.dp // 차례대로 탑 앱 바(50.dp), 날짜 변경 바(50.dp), 하단 네비게이션 바(50.dp)

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
    ) {
        // 컨텐츠
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(32.dp) // item 간에 32.Dp의 공간이 설정됨.
        ) {
            item("다이어리") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                        .heightIn(min = dateBasedFragmentHeight),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )

                    Text(
                        modifier = Modifier
                            .border(1.dp, Color.Black),
                        text = getDayString(date = currentDate)
                    )

                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(2f)
                        ) {
                            DateBasedPieChartFragment(wiDList = wiDList)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "기록률",
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )

                            Text(
                                text = "${getTotalDurationPercentageFromWiDList(wiDList = wiDList)}%",
                                style = TextStyle(
                                    fontSize = 30.sp,
//                                    fontFamily = FontFamily(Font(R.font.pyeong_chang_peace_bold))
                                )
                            )

                            Text(
                                text = "${formatDuration(getTotalDurationFromWiDList(wiDList = wiDList), mode = 1)} / 24시간",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .border(1.dp, Color.Black),
                        text = diary?.title ?: "다이어리 제목",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = if (diary == null) Color.LightGray else Color.Black
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        modifier = Modifier
                            .border(1.dp, Color.Black),
                        text = diary?.content ?: "다이어리 내용",
                        style = TextStyle(color = if (diary == null) Color.LightGray else Color.Black),
                    )
                }
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
                        style = TextStyle(fontWeight = FontWeight.Bold)
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
                                            fontFamily = FontFamily(Font(R.font.pyeong_chang_peace_bold))
                                        )
                                    )

                                    Text(
                                        text = formatDuration(totalDuration, mode = 3),
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily(Font(R.font.pyeong_chang_peace_bold))
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
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
                        style = TextStyle(fontWeight = FontWeight.Bold)
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

                                            Text(text = titleMap[wiD.title] ?: wiD.title)
                                        }

                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "Navigate to WiD fragment",
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
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = formatTime(wiD.start, "a hh:mm:ss"),
                                                style = TextStyle(fontWeight = FontWeight.Bold)
                                            )

                                            Text(
                                                text = formatTime(wiD.finish, "a hh:mm:ss"),
                                                style = TextStyle(fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        Text(
                                            text = formatDuration(wiD.duration, mode = 3),
                                            style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.pyeong_chang_peace_bold)))
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

        // 하단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        navController.navigate(Destinations.DiaryFragmentDestination.route + "/${currentDate}")

                        mainTopBottomBarVisible.value = false
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_16),
                    contentDescription = "다이어리 수정",
                    tint = colorResource(id = R.color.deep_sky_blue)
                )

                Text(
                    text = "다이어리 수정",
                    style = TextStyle(color = colorResource(id = R.color.deep_sky_blue))
                )
            }

            Row {
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
