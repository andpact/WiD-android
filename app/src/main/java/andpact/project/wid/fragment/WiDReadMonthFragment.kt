package andpact.project.wid.fragment

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.getFirstDayOfMonth
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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

    val totalDurationMap = remember(wiDList) {
        val result = mutableMapOf<String, Duration>()

        for (wiD in wiDList) {
            result[wiD.title] = (result[wiD.title] ?: Duration.ZERO) + wiD.duration
        }
        result
    }

    val sortedTotalDurationList = totalDurationMap.entries.sortedByDescending { it.value }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = {
                    currentDate = LocalDate.now()
                    firstDayOfMonth = getFirstDayOfMonth(currentDate)
                },
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "ThisMonth")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.minusMonths(1)
                    firstDayOfMonth = getFirstDayOfMonth(currentDate)
                },
                modifier = Modifier
                    .border(1.dp, Color.Black)
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
                    .border(1.dp, Color.Black)
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
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "제목",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = "총합",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(8.dp)
            )
        }

        for ((title, duration) in sortedTotalDurationList) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Title: $title",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = "Duration: ${formatDuration(duration = duration, mode = 1)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
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
