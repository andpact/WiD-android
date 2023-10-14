package andpact.project.wid.fragment

import andpact.project.wid.service.WiDService
import andpact.project.wid.util.getDate1yearAgo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun WiDReadCalendarFragment() {
    val currentDate by remember { mutableStateOf(LocalDate.now()) }
    val date1yearAgo by remember { mutableStateOf(getDate1yearAgo(currentDate)) }

    val lazyGridState = rememberLazyGridState(
        initialFirstVisibleItemScrollOffset = Int.MAX_VALUE
    )

    val wiDService = WiDService(context = LocalContext.current)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val daysOfWeek = listOf("날짜", "일", "월", "화", "수", "목", "금", "토")
            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    1 -> Color.Red  // "일"의 인덱스는 1
                    7 -> Color.Blue // "토"의 인덱스는 7
                    else -> Color.Unspecified
                }

                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(textAlign = TextAlign.Center, color = textColor)
                )
            }
        }

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(8),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val daysDifference = ChronoUnit.DAYS.between(date1yearAgo, currentDate).toInt() + 1
            var dateIndex = 0
            var gridIndex = 0

            var previousMonth: Int? = null

            while (dateIndex < daysDifference) {
                val date = date1yearAgo.plusDays(dateIndex.toLong())
                val currentMonth = date.monthValue

                if (gridIndex % 8 == 0 && (previousMonth == null || currentMonth != previousMonth)) {
                    item {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("yyyy M"))
                        )
                    }
                    previousMonth = currentMonth
                } else if (gridIndex % 8 == 0) {
                    item {
                        EmptyPieChartView()
                    }
                }
                else {
                    item {
                        PieChartView(date = date, forReadDay = false)
                    }
                    dateIndex++
                }
                gridIndex++
            }
        }
    }
}

//data class TitleStats2(
//    val title: String,
//    val dayTotalDuration: Duration,
//    val weekTotalDuration: Duration,
//    val monthTotalDuration: Duration,
//    val yearTotalDuration: Duration,
//)