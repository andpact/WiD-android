package andpact.project.wid.chartView

import andpact.project.wid.model.*
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Transparent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun MonthlyWiDListChartView(
    modifier: Modifier = Modifier,
    startDate: LocalDate,
    finishDate: LocalDate,
    wiDList: List<WiD>
) {
    val TAG = "MonthlyWiDListChartView"

    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    // 날짜 목록 생성
    val dates = generateSequence(startDate) { it.plusDays(1) }
        .takeWhile { it <= finishDate }
        .toList()

    // 월의 시작 요일
    val firstDayOffset = startDate.dayOfWeek.value % 7 // 일요일 기준 Offset

    // Map<LocalDate, List<Title>> 생성 (순위만 사용)
    val dateTitleMap: Map<LocalDate, List<Title>> = wiDList
        .groupBy { it.date }
        .mapValues { (_, records) ->
            records.groupBy { it.title }
                .entries
                .sortedByDescending { it.value.sumOf { record -> record.duration.toMinutes() } }
                .take(3) // 상위 3개
                .map { it.key } // Title 목록 반환
        }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(daysOfWeek.size) { index ->
            val textColor = when (index) {
                0 -> OrangeRed
                6 -> DeepSkyBlue
                else -> MaterialTheme.colorScheme.onSurface
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = daysOfWeek[index],
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }

        // 월 시작 전 빈 셀 추가
        items(firstDayOffset) {
            Spacer(modifier = Modifier)
        }

        // 날짜 및 순위 표시
        items(dates.size) { index ->
            val date = dates[index]
            val topTitles = dateTitleMap[date] ?: emptyList() // 순위만 사용

            Column(
                modifier = Modifier
                    .width(intrinsicSize = IntrinsicSize.Min),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ),
                    text = date.dayOfMonth.toString() + "일",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                for (i in 0 until 3) {
                    val title = topTitles.getOrNull(i) // i번째 제목 가져오기
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp) // 고정된 높이
                            .background(
                                color = title?.color ?: Transparent, // 데이터가 없으면 투명색
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )
                }
            }
        }
    }
}
//
//@Composable
//@Preview(showBackground = true)
//fun MonthlyWiDListChartPreview() {
//    val today = LocalDate.now()
//    val tmpStartDate = today.withDayOfMonth(1)
//    val tmpFinishDate = today.withDayOfMonth(today.lengthOfMonth())
//
//    val days = tmpStartDate.until(tmpFinishDate, ChronoUnit.DAYS).toInt() + 1
//
//    val tmpWiDList = mutableListOf<WiD>()
//
//    for (index in 0 until days step 12) {
//        val indexDate = tmpStartDate.plusDays(index.toLong())
//
//        tmpWiDList.add(
//            WiD(
//                id = "tmpWiD",
//                date = indexDate,
//                title = Title.STUDY,
//                subTitle = SubTitle.UNSELECTED,
//                start = LocalTime.of(0, 0),
//                finish = LocalTime.of(2, 0),
//                duration = Duration.ofHours(2),
//                city = City.SEOUL,
//                exp = 0,
//                createdBy = CurrentTool.LIST
//            )
//        )
//
//        tmpWiDList.add(
//            WiD(
//                id = "tmpWiD",
//                date = indexDate,
//                title = Title.WORK,
//                subTitle = SubTitle.UNSELECTED,
//                start = LocalTime.of(6, 0),
//                finish = LocalTime.of(9, 0),
//                duration = Duration.ofHours(3),
//                city = City.BUSAN,
//                exp = 0,
//                createdBy = CurrentTool.LIST
//            )
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        MonthlyWiDListChartView(
//            modifier = Modifier
//                .padding(horizontal = 16.dp),
//            startDate = tmpStartDate,
//            finishDate = tmpFinishDate,
//            wiDList = tmpWiDList
//        )
//    }
//}