package andpact.project.wid.fragment

import andpact.project.wid.service.WiDService
import andpact.project.wid.util.getDate1yearAgo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDate

@Composable
fun WiDReadMonthFragment2() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var date1yearAgo by remember { mutableStateOf(getDate1yearAgo(currentDate)) }

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
            val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    0 -> Color.Red  // "일"의 인덱스는 0
                    6 -> Color.Blue // "토"의 인덱스는 6
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
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val dayOfWeek = date1yearAgo.dayOfWeek.value // 1 (월요일)부터 7 (일요일)까지

            repeat(dayOfWeek % 7) {
                item {
                    EmptyPieChartView()
                }
            }

            items(date1yearAgo.lengthOfMonth()) { index ->
                val date = date1yearAgo.plusDays(index.toLong())
                PieChartView(date = date, forReadDay = false)
            }
        }
    }
}

data class TitleStats2(
    val title: String,
    val dayTotalDuration: Duration,
    val weekTotalDuration: Duration,
    val monthTotalDuration: Duration,
//    val yearTotalDuration: Duration,
)