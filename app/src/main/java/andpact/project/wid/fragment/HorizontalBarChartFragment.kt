package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.util.colorMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HorizontalBarChartView(wiDList: List<WiD>) {
    val barChartData = mutableListOf<BarChartData>()

    val totalMinutes = 24 * 60 // 1440분(24시간) 분 단위로
    var currentMinute = 0 // 채워진 공간의 종료 시간(지점), 채워질 공간의 시작 시간(지점)을 의미함

    if (wiDList.isEmpty()) {
        val noBarChartData = BarChartData(1f, Color.Black)
        barChartData.add(noBarChartData)
    } else {
        for (wiD in wiDList) {
            val startMinutes = wiD.start.hour * 60 + wiD.start.minute

            // 비어 있는 시간대의 엔트리 추가
            if (startMinutes > currentMinute) {
                val emptyMinutes = startMinutes - currentMinute
                val emptyBarChartData = BarChartData(emptyMinutes.toFloat() / totalMinutes, Color.Black)
                barChartData.add(emptyBarChartData)
            }

            // 엔트리 셋에 해당 WiD 객체의 시간대를 추가
            val durationMinutes = wiD.duration.toMinutes().toInt()
            if (1 <= durationMinutes) { // 1분 이상의 기록만 막대차트로 보여줌.(막대 차트의 weight에 0.1 미만의 작은 값이 들어갈 수 없기때문)
                val widBarChartData = BarChartData(durationMinutes.toFloat() / totalMinutes, colorResource(id = colorMap[wiD.title] ?: R.color.black))
                barChartData.add(widBarChartData)
            }

            // 시작 시간 업데이트
            currentMinute = startMinutes + durationMinutes
//            currentMinute = wiD.finish.hour * 60 + wiD.finish.minute
        }

        // 마지막 WiD 객체 이후의 비어 있는 시간대의 엔트리 추가
        if (currentMinute < totalMinutes) {
            val emptyMinutes = totalMinutes - currentMinute
            val emptyBarChartData = BarChartData(emptyMinutes.toFloat() / totalMinutes, Color.Black)
            barChartData.add(emptyBarChartData)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.97f)
                .border(
                    BorderStroke(1.dp, Color.Black),
                ),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            for (barData in barChartData) {
                Box(
                    modifier = Modifier
//                        .fillMaxWidth(barData.value) // 동작 안하네.
                        .weight(barData.value)
                        .height(10.dp)
                        .background(color = barData.color)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (hour in 0 until 13) {
                Text(
                    modifier = Modifier
                        .width(14.dp),
                    text = (hour * 2).toString(),
                    style = TextStyle(fontSize = 10.sp, textAlign = TextAlign.Center)
                )
            }
        }
    }
}

data class BarChartData (val value: Float, val color: Color)

@Preview(showBackground = true)
@Composable
fun HorizontalBarChartViewPreview() {
    val temporaryWiDList = listOf(
        WiD(1, LocalDate.now(), "STUDY", LocalTime.of(0, 0), LocalTime.of(1, 0), Duration.ofHours(1), "Details 1"),
//        WiD(1, LocalDate.now(), "STUDY", LocalTime.of(0, 0), LocalTime.of(1, 0), Duration.ofSeconds(60), "Details 1"),
        WiD(1, LocalDate.now(), "ETC", LocalTime.of(3, 0), LocalTime.of(4, 0), Duration.ofHours(1), "Details 1"),
        WiD(1, LocalDate.now(), "STUDY", LocalTime.of(8, 0), LocalTime.of(9, 0), Duration.ofHours(1), "Details 1"),
        WiD(2, LocalDate.now(), "WORK", LocalTime.of(10, 0), LocalTime.of(11, 0), Duration.ofHours(1), "Details 2"),
        WiD(3, LocalDate.now(), "HOBBY", LocalTime.of(13, 0), LocalTime.of(15, 0), Duration.ofHours(2), "Details 3"),
    )

    HorizontalBarChartView(temporaryWiDList)
//    HorizontalBarChartView(emptyList())
}