package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.getTotalDurationMapByDate
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable // 존재하지 않는 데이터를 위해 나타내기 위해 startDate와 finishDate를 가져옴.
fun LineChartFragment(title: String, wiDList: List<WiD>, startDate: LocalDate, finishDate: LocalDate) {
    val totalDurationMap = getTotalDurationMapByDate(wiDList = wiDList)

    // Entry의 x값은 오름차순 정렬되어야 하기 때문에 index를 사용함. 일(day)을 dateList로 만들어서 x축 라벨로 사용함.
    val dateList = mutableListOf<String>()
    val entryList = mutableListOf<Entry>()
    for (index in 0 until ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1) {
        val indexDate = startDate.plusDays(index.toLong())

        val xValue = index.toFloat()

        val duration = totalDurationMap[indexDate] ?: Duration.ZERO
        val totalHours = duration.toHours()
        val totalMinutes = duration.toMinutes() % 60
        val yValue = totalHours.toFloat() + (totalMinutes.toFloat() / 60)

        entryList.add(Entry(xValue, yValue))

        dateList.add(indexDate.dayOfMonth.toString())
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .aspectRatio(1.5f)
    ) {
        // Crossfade 적용 안하면 차트 갱신이 안된다.
        Crossfade(targetState = entryList) { entryList ->
            AndroidView(factory = { context ->
                LineChart(context).apply {
                    // 설정
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    legend.isEnabled = false
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = false
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    animateY(500)

                    // x축
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM // 축 위치
                        setDrawGridLines(false) // 그리드 라인
                        setDrawAxisLine(false) // 축선 표시
                        granularity = 1f // 축 라벨 표시 간격
//                        labelRotationAngle = -50f // 라벨 회전
                        valueFormatter = object : ValueFormatter() { // x축 라벨
                            override fun getFormattedValue(value: Float): String {
                                return "${dateList[value.toInt()]}일"
                            }
                        }
                    }

                    // 왼축
                    axisLeft.apply {
                        isEnabled = false // 축 표시 여부
                        setDrawAxisLine(false) // 축선 표시
                        setDrawGridLines(true) // 그리드 라인
                    }

                    // 오른 축
                    axisRight.apply {
                        isEnabled = false // 축 표시 여부
                    }

                    // 데이터
                    val dataSet = LineDataSet(entryList, null).apply {
                        color = Color.Black.toArgb() // 선 색상
                        setDrawCircles(false) // 선 꼭지점 원 표시
                        setDrawValues(true) // 꼭지점 값 표시하기
                        lineWidth = 2f // 선 굵기
                        mode = LineDataSet.Mode.HORIZONTAL_BEZIER // 선 스타일
                        setDrawFilled(true) // 선 아래 공간 채우기
                        fillDrawable = GradientDrawable().apply { // 선 아래 공간 그라디언트, 색상
                            shape = GradientDrawable.RECTANGLE
                            gradientType = GradientDrawable.LINEAR_GRADIENT
                            val startColorID = when (title) {
                                "STUDY" -> R.color.study_color
                                "WORK" -> R.color.work_color
                                "EXERCISE" -> R.color.exercise_color
                                "HOBBY" -> R.color.hobby_color
                                "PLAY" -> R.color.play_color
                                "MEAL" -> R.color.meal_color
                                "SHOWER" -> R.color.shower_color
                                "TRAVEL" -> R.color.travel_color
                                "SLEEP" -> R.color.sleep_color
                                "ETC" -> R.color.etc_color
                                else -> R.color.light_gray
                            }
                            val startColor = ContextCompat.getColor(context, startColorID) // colorMap을 사용하니 색상 적용이 안되서 직접 리소스 아이디를 가져옴.
                            val endColor = Color.White.toArgb()
                            colors = intArrayOf(startColor, endColor)
                            orientation = GradientDrawable.Orientation.TOP_BOTTOM
                        }
                        valueTextSize = 10f // 값 글자 크기
                        valueFormatter = object : ValueFormatter() { // 값 소수점 처리
                            override fun getFormattedValue(value: Float): String {
                                // 값이 정수인 경우 소수점을 표시하지 않음
                                return if (value % 1 == 0f) {
                                    "${value.toInt()}h"
                                } else {
                                    String.format("%.1fh", value)
                                }
                            }
                        }
                    }
                    val data = LineData(dataSet)
                    setData(data)

                    invalidate()
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