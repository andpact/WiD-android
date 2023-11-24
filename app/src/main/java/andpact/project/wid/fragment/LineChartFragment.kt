package andpact.project.wid.fragment

import andpact.project.wid.model.WiD
import andpact.project.wid.util.getTotalDurationMapByDate
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.LocalDate

@Composable
fun LineChartFragment(wiDList: List<WiD>, startDate: LocalDate, finishDate: LocalDate) { // 존재하지 않는 데이터를 위해 나타내기 위해 startDate와 finishDate를 가져옴.
    val totalDurationMap = getTotalDurationMapByDate(wiDList = wiDList)

    val lineChartData = mutableListOf<Entry>()

    var currentDate = startDate
    while (currentDate.isBefore(finishDate) || currentDate.isEqual(finishDate)) {
        val dayOfMonth = currentDate.dayOfMonth.toFloat()
        val hours = totalDurationMap[currentDate]?.toHours()?.toFloat() ?: 0f
        lineChartData.add(Entry(dayOfMonth, hours))
        currentDate = currentDate.plusDays(1)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.5f)
    ) {
        Crossfade(targetState = lineChartData) { lineChartData -> // Crossfade 적용 안하면 차트 갱신이 안된다.
            AndroidView(factory = { context ->
                LineChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // 설정
                    legend.isEnabled = false
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)

                    // x축
                    val xAxis: XAxis = xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM // 축 위치
                    xAxis.setDrawGridLines(false) // 그리드 라인
                    xAxis.setDrawAxisLine(false) // 축선 표시
                    xAxis.granularity = 1f // 축 라벨 표시 간격
    //                xAxis.spaceMin = 0.1f // 축 왼쪽 여백
    //                xAxis.spaceMax = 0.1f // 축 오른쪽 여백

                    // 왼축
                    val leftAxis: YAxis = axisLeft
                    leftAxis.isEnabled = false // 축 표시 여부
                    leftAxis.setDrawAxisLine(false) // 축선 표시
                    leftAxis.setDrawGridLines(true) // 그리드 라인

                    // 오른축
                    val rightAxis: YAxis = axisRight
                    rightAxis.isEnabled = false // 축 표시 여부

                    // 데이터
                    val dataSet = LineDataSet(lineChartData, "Sample")
                    dataSet.color = Color.Black.toArgb() // 그래프 색
                    dataSet.setDrawCircles(false) // 데이터에 원 그리기
    //                dataSet.setDrawCircleHole(false) // 데이터 가운데 원 표시
    //                dataSet.setCircleColor(Color.Black.toArgb()) // 데이터에 원 색상
                    dataSet.setDrawValues(true) // 데이터에 값 표시하기
                    dataSet.lineWidth = 2f // 그래프 선 굵기
                    dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER // 그래프 스타일

                    dataSet.setDrawFilled(true)

                    val data = LineData(dataSet)
                    setData(data)
                }
            })
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LineChartFragmentPreview() {
//
//}