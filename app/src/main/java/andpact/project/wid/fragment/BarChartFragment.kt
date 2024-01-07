package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.Black
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.LightGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.getTotalDurationPercentageFromWiDList
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

//@Composable
//fun StackedHorizontalBarChartFragment(wiDList: List<WiD>) {
//    val barChartData = mutableListOf<BarChartData>()
//
//    val totalMinutes = 24 * 60 // 1440분(24시간) 분 단위로
//    var currentMinute = 0 // 채워진 공간의 종료 시간(지점), 채워질 공간의 시작 시간(지점)을 의미함
//
//    if (wiDList.isEmpty()) {
//        val noBarChartData = BarChartData(1f, LightGray)
//        barChartData.add(noBarChartData)
//    } else {
//        for (wiD in wiDList) {
//            val startMinutes = wiD.start.hour * 60 + wiD.start.minute
//
//            // 비어 있는 시간대의 엔트리 추가
//            if (startMinutes > currentMinute) {
//                val emptyMinutes = startMinutes - currentMinute
//                val emptyBarChartData = BarChartData(emptyMinutes.toFloat() / totalMinutes, LightGray)
//                barChartData.add(emptyBarChartData)
//            }
//
//            // 엔트리 셋에 해당 WiD 객체의 시간대를 추가
//            val durationMinutes = wiD.duration.toMinutes().toInt()
//            if (1 <= durationMinutes) { // 1분 이상의 기록만 막대차트로 보여줌.(막대 차트의 weight에 0.1 미만의 작은 값이 사용될 수도 없고 막대 차트에 표시도 안되기 때문)
//                val widBarChartData = BarChartData(durationMinutes.toFloat() / totalMinutes, colorMap[wiD.title] ?: LightGray)
//                barChartData.add(widBarChartData)
//            }
//
//            // 시작 시간 업데이트
//            currentMinute = startMinutes + durationMinutes
//        }
//
//        // 마지막 WiD 객체 이후의 비어 있는 시간대의 엔트리 추가
//        if (currentMinute < totalMinutes) {
//            val emptyMinutes = totalMinutes - currentMinute
//            val emptyBarChartData = BarChartData(emptyMinutes.toFloat() / totalMinutes, LightGray)
//            barChartData.add(emptyBarChartData)
//        }
//    }
//
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(IntrinsicSize.Min)
//        ) {
//            Text(
//                modifier = Modifier
//                    .weight(1f),
//                text = "오전",
//                style = Typography.bodySmall,
//                textAlign = TextAlign.Center
//            )
//
//            VerticalDivider()
//
//            Text(
//                modifier = Modifier
//                    .weight(1f),
//                text = "오후",
//                style = Typography.bodySmall,
//                textAlign = TextAlign.Center
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(0.97f)
//                .border(
//                    BorderStroke(1.dp, Black),
//                ),
//            horizontalArrangement = Arrangement.spacedBy(0.dp),
//        ) {
//            for (barData in barChartData) {
//                Box(
//                    modifier = Modifier
////                        .fillMaxWidth(barData.value) // 동작 안하네.
//                        .weight(barData.value)
//                        .height(10.dp)
//                        .background(color = barData.color)
//                )
//            }
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            for (hour in 0 until 13) {
//                val adjustedHour = (hour * 2) % 12
//                Text(
//                    modifier = Modifier
//                        .width(14.dp),
//                    text = if (adjustedHour == 0 && hour != 0) "12" else adjustedHour.toString(),
//                    style = Typography.bodySmall,
//                    fontSize = 10.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//    }
//}

//data class BarChartData (val value: Float, val color: Color)

@Composable
fun VerticalBarChartFragment(wiDList: List<WiD>, startDate: LocalDate, finishDate: LocalDate) {

    val dateList = mutableListOf<String>()
    val entryList = mutableListOf<BarEntry>()

    for (index in 0 until ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1) {
        val indexDate = startDate.plusDays(index.toLong())

        val filteredWiDListByDate = wiDList.filter { it.date == indexDate }
        val percentage = getTotalDurationPercentageFromWiDList(filteredWiDListByDate)

        val xValue = index.toFloat()
        val yValue = percentage.toFloat()

        dateList.add(indexDate.dayOfMonth.toString())
        entryList.add(BarEntry(xValue, yValue))
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .aspectRatio(1.5f)
    ) {
        // Crossfade 적용 안하면 차트 갱신이 안된다.
        Crossfade(targetState = entryList) { entryList ->
            val colorScheme = MaterialTheme.colorScheme
            AndroidView(factory = { context ->
                BarChart(context).apply {
                    // 데이터
                    val dataSet = BarDataSet(entryList, "단위 : 퍼센트").apply {
                        setDrawValues(false) // 막대 끝에 값 표시하기
                        color = DarkGray.toArgb()
                    }
                    val data = BarData(dataSet)
                    setData(data)

                    // 차트 설정
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    legend.isEnabled = true
                    legend.textColor = colorScheme.primary.toArgb()
                    legend.textSize = 12f
                    legend.form = Legend.LegendForm.LINE // 범례 아이콘 형태
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // 범례 수평 정렬
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // 범례 수직 정렬
                    description.isEnabled = false
                    setTouchEnabled(false)
                    isDragEnabled = false
                    setScaleEnabled(false)
                    setPinchZoom(false)
//                    animateY(500)

                    // x축
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM // 축 위치
                        setDrawGridLines(false) // 그리드 라인
                        setDrawAxisLine(false) // 축선 표시
                        granularity = 1f // 축 라벨 표시 단위
                        textColor = colorScheme.primary.toArgb()
                        textSize = 12f // 축 라벨 글자 크기
                        val labelCount = if (dateList.size <= 7) { dateList.size / 1 } else { dateList.size / 3 }
                        setLabelCount(labelCount, false) // 라벨 표시 간격
                        valueFormatter = object : ValueFormatter() { // x축 라벨
                            override fun getFormattedValue(value: Float): String {
                                return "${dateList[value.toInt()]}일"
                            }
                        }
                    }

                    // 왼축
                    axisLeft.apply {
                        isEnabled = true // 축 표시 여부
                        setDrawAxisLine(false) // 축선 표시
                        setDrawGridLines(true) // 그리드 라인
                        textColor = colorScheme.primary.toArgb()
                        textSize = 12f
                    }

                    // 오른 축
                    axisRight.apply {
                        isEnabled = false // 축 표시 여부
                    }

                    invalidate()
                }
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarChartFragmentPreview() {
    val days: Long = 7
    val tmpStartDate = LocalDate.now()
    val tmpFinishDate = tmpStartDate.minusDays(days - 1)

    val tmpWiDList = mutableListOf<WiD>()

    for (index in 0 until days) {
        val indexDate = tmpStartDate.plusDays(index)

        tmpWiDList.add(
            WiD(
                id = 0,
                date = indexDate,
                title = "STUDY",
                start = LocalTime.of(0, 0),
                finish = LocalTime.of(2, 0),
                duration = Duration.ofHours(2)
            )
        )
    }

//    StackedHorizontalBarChartFragment(tmpWiDList)

//    VerticalBarChartFragment(tmpWiDList, tmpStartDate, tmpFinishDate)
}