package andpact.project.wid.tmp

import andpact.project.wid.model.WiD
import andpact.project.wid.util.getWiDTitleTotalDurationMap
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

//@Composable // 존재하지 않는 데이터를 위해 나타내기 위해 startDate와 finishDate를 가져옴.
//fun LineChartView(
//    title: String,
//    wiDList: List<WiD>,
//    startDate: LocalDate,
//    finishDate: LocalDate,
//    modifier: Modifier = Modifier
//) {
//    val colorScheme = MaterialTheme.colorScheme
//
//    val totalDurationMap = getWiDTitleTotalDurationMap(wiDList = wiDList)
//
//    // Entry의 x값은 오름차순 정렬되어야 하기 때문에 index를 사용함. 일(day)을 dateList로 만들어서 x축 라벨로 사용함.
//    val dateList = mutableListOf<String>()
//    val entryList = mutableListOf<Entry>()
//    for (index in 0 until ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1) {
//        val indexDate = startDate.plusDays(index.toLong())
//
//        val xValue = index.toFloat()
//
//        val duration = totalDurationMap[indexDate] ?: Duration.ZERO
//        val totalHours = duration.toHours()
//        val totalMinutes = duration.toMinutes() % 60
//        val yValue = totalHours.toFloat() + (totalMinutes.toFloat() / 60)
//
//        dateList.add(indexDate.dayOfMonth.toString())
//        entryList.add(Entry(xValue, yValue))
//    }
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .padding(16.dp)
//        .aspectRatio(1f / 1f)
//    ) {
//        // Crossfade 적용 안하면 차트 갱신이 안된다.
//        Crossfade(targetState = entryList) { entryList ->
//            AndroidView(factory = { context ->
//                LineChart(context).apply {
//                    // 데이터
//                    val dataSet = LineDataSet(entryList, "단위 : 시간").apply {
//                        color = colorScheme.onSurface.toArgb() // 선 색상
//                        setDrawCircles(false) // 선 꼭지점 원 표시
//                        setDrawValues(false) // 꼭지점 값 표시하기
//                        lineWidth = 2f // 선 굵기
//                        mode = LineDataSet.Mode.HORIZONTAL_BEZIER // 선 스타일
//                        setDrawFilled(true) // 선 아래 공간 채우기
//                        fillDrawable = GradientDrawable().apply { // 선 아래 공간 그라디언트, 색상
//                            shape = GradientDrawable.RECTANGLE
//                            gradientType = GradientDrawable.LINEAR_GRADIENT
//                            val startColor = (titleColorMap[title] ?: colorScheme.secondaryContainer).toArgb()
//                            val endColor = colorScheme.onSurface.toArgb()
//                            colors = intArrayOf(startColor, endColor)
//                            orientation = GradientDrawable.Orientation.TOP_BOTTOM
//                        }
////                        valueTextSize = 10f // 값 글자 크기
////                        valueFormatter = object : ValueFormatter() { // 값 소수점 처리
////                            override fun getFormattedValue(value: Float): String {
////                                // 값이 정수인 경우 소수점을 표시하지 않음
////                                return if (value % 1 == 0f) {
////                                    "${value.toInt()}"
////                                } else {
////                                    String.format("%.1f", value)
////                                }
////                            }
////                        }
//                    }
//                    val data = LineData(dataSet)
//                    setData(data)
//
//                    // 차트 설정
//                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//                    legend.isEnabled = true
//                    legend.textColor = colorScheme.onSurface.toArgb()
//                    legend.textSize = 12f
//                    legend.form = Legend.LegendForm.LINE // 범례 아이콘 형태
//                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // 범례 수평 정렬
//                    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // 범례 수직 정렬
//                    description.isEnabled = false
//                    setTouchEnabled(false)
//                    isDragEnabled = false
//                    setScaleEnabled(false)
//                    setPinchZoom(false)
////                    animateY(500)
//
//                    // x축
//                    xAxis.apply {
//                        position = XAxis.XAxisPosition.BOTTOM // 축 위치
//                        setDrawGridLines(false) // 그리드 라인
//                        setDrawAxisLine(false) // 축선 표시
//                        granularity = 1f // 축 라벨 표시 단위
//                        textColor = colorScheme.onSurface.toArgb()
//                        textSize = 12f // 축 라벨 글자 크기
//                        val labelCount = if (dateList.size <= 7) { dateList.size / 1 } else { dateList.size / 3 } // 주간 데이터인지, 월간 데이터인지
//                        setLabelCount(labelCount, false) // 라벨 표시 간격
//                        valueFormatter = object : ValueFormatter() { // x축 라벨
//                            override fun getFormattedValue(value: Float): String {
//                                return "${dateList[value.toInt()]}일"
//                            }
//                        }
//                    }
//
//                    // 왼축
//                    axisLeft.apply {
//                        isEnabled = true // 축 표시 여부
//                        setDrawAxisLine(false) // 축선 표시
//                        setDrawGridLines(true) // 그리드 라인
//                        textColor = colorScheme.onSurface.toArgb()
//                        textSize = 12f
//                    }
//
//                    // 오른 축
//                    axisRight.apply {
//                        isEnabled = false // 축 표시 여부
//                    }
//
//                    invalidate()
//                }
//            })
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun LineChartFragmentPreview() {
//
//}