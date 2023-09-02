package andpact.project.wid.fragment

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.getFirstDayOfMonth
import andpact.project.wid.util.titleMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun WiDReadMonthFragment() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var firstDayOfMonth by remember { mutableStateOf(getFirstDayOfMonth(currentDate)) }

    val wiDService = WiDService(context = LocalContext.current)

    val wiDList = remember(firstDayOfMonth) {
        val allWiDs = mutableListOf<WiD>()

        val yearMonth = YearMonth.from(firstDayOfMonth)
        val daysInMonth = yearMonth.lengthOfMonth()

        for (index in 0 until daysInMonth) {
            val date = firstDayOfMonth.plusDays(index.toLong())
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = {
                    currentDate = LocalDate.now()
                    firstDayOfMonth = getFirstDayOfMonth(currentDate)
                },
                modifier = Modifier
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "ThisMonth")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.minusMonths(1)
                    firstDayOfMonth = getFirstDayOfMonth(currentDate)
                },
                modifier = Modifier
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "prevMonth")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.plusMonths(1)
                    firstDayOfMonth = getFirstDayOfMonth(currentDate)
                },
                enabled = currentDate != LocalDate.now(),
                modifier = Modifier
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "nextMonth")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    0 -> Color.Red  // "일"의 인덱스는 0
                    6 -> Color.Blue // "토"의 인덱스는 6
                    else -> Color.Black
                }

                Text(
                    text = day,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val dayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 (월요일)부터 7 (일요일)까지

            repeat(dayOfWeek) {
                item {
                    EmptyPieChartView()
                }
            }

            items(firstDayOfMonth.lengthOfMonth()) { index ->
                val date = firstDayOfMonth.plusDays(index.toLong())
                PieChartView(date = date, forReadDay = false)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
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
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            )

            Text(
                text = "최저",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            )

            Text(
                text = "최고",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            )

            Text(
                text = "평균",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            )

            Text(
                text = "총합",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            )
        }

        // 각 제목별로 정보를 표시
        for ((title, stats) in titleStats) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val titleColorId = colorMap[title]
                if (titleColorId != null) {
                    val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 25.dp)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }

                // "제목" 표시
                Text(
                    text = titleMap[title] ?: title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.5f)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                )

                // "최저" 표시
                Text(
                    text = formatDuration(stats.minDuration, mode = 1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                )

                // "최고" 표시
                Text(
                    text = formatDuration(stats.maxDuration, mode = 1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                )

                // "평균" 표시
                Text(
                    text = formatDuration(stats.averageDuration, mode = 1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                )

                // "총합" 표시
                Text(
                    text = formatDuration(stats.totalDuration, mode = 1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDReadMonthFragmentPreview() {
    WiDReadMonthFragment()
}
