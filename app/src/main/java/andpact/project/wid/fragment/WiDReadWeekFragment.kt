package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

@Composable
fun WiDReadWeekFragment() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var firstDayOfWeek by remember { mutableStateOf(getFirstDayOfWeek(currentDate)) }

    val wiDService = WiDService(context = LocalContext.current)

    val wiDList = remember(firstDayOfWeek) {
        val allWiDs = mutableListOf<WiD>()

        for (index in 0 until 7) {
            val date = firstDayOfWeek.plusDays(index.toLong())
            val wiDsForDate = wiDService.readWiDListByDate(date)
            allWiDs.addAll(wiDsForDate)
        }
        allWiDs
    }

    // 각 제목별 날짜별 종합 시간을 추적하기 위한 맵
    val titleDateDurations = remember(wiDList) {
        val map = mutableMapOf<String, MutableMap<LocalDate, Duration>>()

        for (wiD in wiDList) {
            val title = wiD.title
            val date = wiD.date
            val duration = wiD.duration

            // 각 제목에 대한 맵 가져오기
            val titleMap = map.getOrPut(title) { mutableMapOf() }

            // 날짜별 시간 누적
            titleMap[date] = (titleMap[date] ?: Duration.ZERO) + duration
        }
        map
    }

    // 최저, 최고, 평균, 종합 시간 계산
    val titleStats = remember(titleDateDurations) {
        val map = mutableMapOf<String, TitleStats>()

        for ((title, dateDurations) in titleDateDurations) {
            val minDuration = dateDurations.values.minOrNull() ?: Duration.ZERO
            val maxDuration = dateDurations.values.maxOrNull() ?: Duration.ZERO
            val totalDuration = dateDurations.values.fold(Duration.ZERO) { acc, duration -> acc + duration }
            val averageDuration = if (dateDurations.isNotEmpty()) {
                val totalMinutes = totalDuration.toMinutes()
                Duration.ofMinutes(totalMinutes / dateDurations.size)
            } else {
                Duration.ZERO
            }

            map[title] = TitleStats(minDuration, maxDuration, averageDuration, totalDuration)
        }
        map
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "WiD",
                style = TextStyle(textAlign = TextAlign.Center, fontSize = 22.sp,
                    fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.acme_regular))
                )
            )

            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 ")),
                    style = TextStyle(textAlign = TextAlign.Center)
                )

                Text(
                    text = "${getWeekNumber(currentDate)}",
                    style = TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "번째 주",
                    style = TextStyle(textAlign = TextAlign.Center)
                )
            }


            IconButton(
                onClick = {
                    currentDate = LocalDate.now()
                    firstDayOfWeek = getFirstDayOfWeek(currentDate)
                },
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "ThisWeek")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.minusDays(7)
                    firstDayOfWeek = getFirstDayOfWeek(currentDate)
                },
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "prevWeek")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.plusDays(7)
                    firstDayOfWeek = getFirstDayOfWeek(currentDate)
                },
                enabled = currentDate != LocalDate.now(),
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "nextWeek")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    5 -> Color.Blue // "토"의 인덱스는 5
                    6 -> Color.Red  // "일"의 인덱스는 6
                    else -> Color.Unspecified
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = day,
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(7) { index ->
                val date = firstDayOfWeek.plusDays(index.toLong())
                PieChartView(date = date, forReadDay = false)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .background(
                    color = colorResource(id = R.color.light_gray),
                    shape = RoundedCornerShape(8.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 25.dp)
                    .background(Color.Transparent)
            )

            Text(
                text = "제목",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.5f)
            )

            Text(
                text = "최저",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "최고",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "평균",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "총합",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(0.dp, 0.dp, 0.dp, 8.dp)
//                .background(
//                    color = colorResource(id = R.color.light_gray),
//                    shape = RoundedCornerShape(8.dp)
//                ),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(width = 10.dp, height = 25.dp)
//                    .background(Color.Black)
//            )
//
//            Text(
//                text = "공부",
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(0.5f)
//            )
//
//            Text(
//                text = "999.9시간",
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(1f)
//            )
//
//            Text(
//                text = "999.9시간",
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(1f)
//            )
//
//            Text(
//                text = "999.9시간",
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(1f)
//            )
//
//            Text(
//                text = "999.9시간",
//                textAlign = TextAlign.Center,
//                modifier = Modifier.weight(1f)
//            )
//        }

        if (wiDList.isEmpty()) {
            Text(modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
                text = "표시할 정보가 없습니다.",
                style = TextStyle(textAlign = TextAlign.Center, color = Color.LightGray)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for ((title, stats) in titleStats) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = colorResource(id = R.color.light_gray),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val titleColorId = colorMap[title]
                            if (titleColorId != null) {
                                val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                                Box(
                                    modifier = Modifier
                                        .size(width = 10.dp, height = 25.dp)
                                        .background(
                                            color = backgroundColor,
                                            shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                                        )
                                )
                            }

                            // "제목" 표시
                            Text(
                                text = titleMap[title] ?: title,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(0.5f)
                            )

                            // "최저" 표시
                            Text(
                                text = formatDuration(stats.minDuration, mode = 1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            // "최고" 표시
                            Text(
                                text = formatDuration(stats.maxDuration, mode = 1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            // "평균" 표시
                            Text(
                                text = formatDuration(stats.averageDuration, mode = 1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            // "총합" 표시
                            Text(
                                text = formatDuration(stats.totalDuration, mode = 1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TitleStats(
    val minDuration: Duration,
    val maxDuration: Duration,
    val averageDuration: Duration,
    val totalDuration: Duration
)

@Preview(showBackground = true)
@Composable
fun WiDReadWeekFragmentPreview() {
    WiDReadWeekFragment()
}
