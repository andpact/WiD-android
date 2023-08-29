package andpact.project.wid.fragment

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.PieChartView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

    // 일주일의 WiD 리스트
    val wiDList = remember(firstDayOfWeek) {
        val allWiDs = mutableListOf<WiD>()

        for (index in 0 until 7) {
            val date = firstDayOfWeek.plusDays(index.toLong())
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
                text = "${currentDate.format(DateTimeFormatter.ofPattern("yyyy년"))} ${getWeekNumber(currentDate)}번째 주",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = {
                    currentDate = LocalDate.now()
                    firstDayOfWeek = getFirstDayOfWeek(currentDate)
                },
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "ThisWeek")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.minusDays(7)
                    firstDayOfWeek = getFirstDayOfWeek(currentDate)
                },
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "prevWeek")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.plusDays(7)
                    firstDayOfWeek = getFirstDayOfWeek(currentDate)
                },
                enabled = currentDate != LocalDate.now(),
                modifier = Modifier
                    .border(1.dp, Color.Black)
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
            items(7) { index ->
                val date = firstDayOfWeek.plusDays(index.toLong())
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

fun getFirstDayOfWeek(date: LocalDate): LocalDate {
    val dayOfWeek = date.dayOfWeek
    val daysToSubtract = (dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

    return date.minusDays(daysToSubtract.toLong())
}

fun getWeekNumber(date: LocalDate): Int {
    val weekFields = WeekFields.of(java.util.Locale.getDefault())
    return date.get(weekFields.weekOfWeekBasedYear())
}

@Preview(showBackground = true)
@Composable
fun WiDReadWeekFragmentPreview() {
    WiDReadWeekFragment()
}
