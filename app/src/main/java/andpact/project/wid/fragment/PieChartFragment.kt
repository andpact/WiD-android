package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import android.graphics.Paint
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PieChartView(date: LocalDate, forReadDay: Boolean) {
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = wiDService.readWiDListByDate(date)

    val pieEntries = mutableListOf<PieEntry>()

    var startMinutes = 0

    for (wiD in wiDList) {
        val finishMinutes = wiD.finish.hour * 60 + wiD.finish.minute

        if (wiD.start.hour * 60 + wiD.start.minute > startMinutes) {
            val emptyMinutes = wiD.start.hour * 60 + wiD.start.minute - startMinutes
            pieEntries.add(PieEntry(emptyMinutes.toFloat(), ""))
        }

        startMinutes = wiD.start.hour * 60 + wiD.start.minute
        pieEntries.add(PieEntry((finishMinutes - startMinutes).toFloat(), wiD.title))

        startMinutes = wiD.finish.hour * 60 + wiD.finish.minute
    }

    if (startMinutes < 24 * 60) {
        val emptyMinutes = 24 * 60 - startMinutes
        pieEntries.add(PieEntry(emptyMinutes.toFloat(), ""))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = pieEntries) { pieEntries ->
            AndroidView(factory = { context ->
                PieChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setUsePercentValues(false) // Use absolute values
                    description.isEnabled = false // Disable description
                    legend.isEnabled = false // Disable legend

                    setDrawEntryLabels(false)
                    setTouchEnabled(false) // Disable touch gestures for zooming

                    isDrawHoleEnabled = true
                    holeRadius = 80f // Hole radius as percentage of the chart
                    setHoleColor(ContextCompat.getColor(context, R.color.transparent))

                    setDrawCenterText(true) // Draw center text
                    centerText = if (forReadDay) {
                        "오후 | 오전"
                    } else {
                        date.dayOfMonth.toString()
                    }
                    setCenterTextSize(16f)

                    if (wiDList.isEmpty()) {
                        setCenterTextColor(Color.LightGray.toArgb())
                    } else {
                        setCenterTextColor(ContextCompat.getColor(context, R.color.black))
                    }

                    val dataSet = PieDataSet(pieEntries, "")
                    val colors = pieEntries.map { entry ->
                        val label = entry.label ?: ""
                        val colorId = colorMap[label] ?: R.color.light_gray
                        ContextCompat.getColor(context, colorId)
                    }
                    dataSet.colors = colors
                    val data = PieData(dataSet)
                    data.setDrawValues(false)
                    this.data = data
                    this.invalidate()
                }
            })

            if (forReadDay) {
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)) {
                    val radius: Float = size.minDimension / 1.9f // 원의 반지름
                    val centerX = center.x
                    val centerY = center.y + 10

                    val textPaint = Paint().apply {
                        color = Color.Black.toArgb()
                        textSize = 30f
                        textAlign = Paint.Align.CENTER
                    }

                    for (i in 0 until 24) {
                        val angleInDegree = (i * 15.0) - 90.0 // 360도를 24등분, 초기 각도를 0으로 설정
                        val angleInRadian = Math.toRadians(angleInDegree)

                        // 시간 텍스트 좌표 계산
                        val x = centerX + radius * 0.85f * cos(angleInRadian)
                        val y = centerY + radius * 0.85f * sin(angleInRadian)

                        // 시간 텍스트 그리기
                        drawContext.canvas.nativeCanvas.drawText(i.toString(), x.toFloat(), y.toFloat(), textPaint)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPieChartView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { context ->
            PieChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setUsePercentValues(false) // Use absolute values
                description.isEnabled = false // Disable description
                legend.isEnabled = false // Disable legend

                setDrawEntryLabels(false)
                setTouchEnabled(false) // Disable touch gestures for zooming

//                isDrawHoleEnabled = true
//                holeRadius = 70f // Hole radius as percentage of the chart
//                setHoleColor(ContextCompat.getColor(context, R.color.transparent))

                val dataSet = PieDataSet(listOf(PieEntry(1.0F, "")), "")
                dataSet.colors = listOf(ContextCompat.getColor(context, R.color.transparent))
                val data = PieData(dataSet)
                data.setDrawValues(false)
                this.data = data
                this.invalidate()
            }
        })
    }
}