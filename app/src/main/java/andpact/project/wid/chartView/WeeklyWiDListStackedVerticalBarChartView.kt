package andpact.project.wid.chartView

import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.TitleDurationChartData
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.model.Title
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun WeeklyWiDListStackedVerticalBarChartView(
    modifier: Modifier = Modifier,
    startDate: LocalDate,
    finishDate: LocalDate,
    wiDList: List<WiD>
) {
    val TAG = "WeeklyWiDListStackedVerticalBarChartView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 요일
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 더미
            Text(
                modifier = Modifier,
                text = "24",
                style = Typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Transparent
            )

            val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    5 -> DeepSkyBlue
                    6 -> OrangeRed
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = day,
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
        ) {
            // 시간
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (hour in 0..24 step 2) { // 2시간 간격으로 숫자 표시
                    Text(
                        text = hour.toString(),
                        style = Typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val days = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1

            for (i in 0 until days) {
                val currentDate = startDate.plusDays(i.toLong())

                // currentDate에 해당하는 WiD 리스트 필터링
                val dailyWiDList = wiDList.filter { it.date == currentDate }

                // 막대 차트 데이터 생성
                val barChartData = mutableListOf<TitleDurationChartData>()
                val totalMinutes = 24 * 60
                var currentMinute = 0

                if (dailyWiDList.isEmpty()) {
                    val noBarChartData = TitleDurationChartData(
                        duration = Duration.ofMinutes(totalMinutes.toLong()), // 수정된 부분
                        title = Title.UNTITLED
                    )
                    barChartData.add(noBarChartData)
                } else {
                    for (wiD in dailyWiDList) {
                        val startMinutes = wiD.start.hour * 60 + wiD.start.minute

                        // 비어 있는 시간대의 엔트리 추가
                        if (startMinutes > currentMinute) {
                            val emptyMinutes = startMinutes - currentMinute
                            val emptyBarChartData = TitleDurationChartData(
                                duration = Duration.ofMinutes(emptyMinutes.toLong()), // 수정된 부분
                                title = Title.UNTITLED
                            )
                            barChartData.add(emptyBarChartData)
                        }

                        // WiD 데이터 추가
                        val durationMinutes = wiD.duration.toMinutes().toInt()
                        if (durationMinutes >= 1) {
                            val wiDBarChartData = TitleDurationChartData(
                                duration = wiD.duration, // 수정된 부분
                                title = wiD.title
                            )
                            barChartData.add(wiDBarChartData)
                        }

                        // 시작 시간 업데이트
                        currentMinute = startMinutes + durationMinutes
                    }

                    // 남은 시간대 비어 있는 막대 추가
                    if (currentMinute < totalMinutes) {
                        val emptyMinutes = totalMinutes - currentMinute
                        val emptyBarChartData = TitleDurationChartData(
                            duration = Duration.ofMinutes(emptyMinutes.toLong()), // 수정된 부분
                            title = Title.UNTITLED
                        )
                        barChartData.add(emptyBarChartData)
                    }
                }

                // 그래프
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 7.dp)
                ) {
                    for (barData in barChartData) {
                        val barHeight = barData.duration.toMinutes().toFloat() / totalMinutes // duration을 비율로 계산
                        if (0.01f <= barHeight) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 1.dp)
                                    .weight(barHeight) // 계산된 높이를 weight로 설정
                                    .background(
                                        color = barData.title.color,
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                            )
                        }
                    }
                }
            }
        }

        // 날짜
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 더미
            Text(
                modifier = Modifier,
                text = "24",
                style = Typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Transparent
            )

            generateSequence(startDate) { it.plusDays(1) }
                .takeWhile { it <= finishDate }
                .forEach { currentDate ->
                    val dayNumber = currentDate.dayOfMonth
                    val dayOfWeek = currentDate.dayOfWeek // 요일 가져오기

                    // 토요일과 일요일에 대해 색상 설정
                    val textColor = when (dayOfWeek) {
                        DayOfWeek.SATURDAY -> DeepSkyBlue
                        DayOfWeek.SUNDAY -> OrangeRed
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "${dayNumber}일", // "n일" 형식으로 날짜 표시
                        style = Typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = textColor // 조건부 색상 적용
                    )
                }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WeeklyWiDListStackedVerticalBarChartPreview() {
//    val days: Long = 7
//    val tmpStartDate = LocalDate.now()
//    val tmpFinishDate = tmpStartDate.plusDays(days - 1)
//
//    val tmpWiDList = mutableListOf<WiD>()
//
//    for (index in 0 until days) {
//        val indexDate = tmpStartDate.plusDays(index)
//
//        tmpWiDList.add(
//            WiD(
//                id = "tmpWiD",
//                date = indexDate,
//                title = Title.STUDY,
//                start = LocalTime.of(0, 0),
//                finish = LocalTime.of(2, 0),
//                duration = Duration.ofHours(2),
//                createdBy = CurrentTool.LIST
//            )
//        )
//
//        tmpWiDList.add(
//            WiD(
//                id = "tmpWiD",
//                date = indexDate,
//                title = Title.STUDY,
//                start = LocalTime.of(6, 0),
//                finish = LocalTime.of(9, 0),
//                duration = Duration.ofHours(3),
//                createdBy = CurrentTool.LIST
//            )
//        )
//    }
//
//    WeeklyWiDListStackedVerticalBarChartView(
//        startDate = tmpStartDate,
//        finishDate = tmpFinishDate,
//        wiDList = tmpWiDList
//    )
//}