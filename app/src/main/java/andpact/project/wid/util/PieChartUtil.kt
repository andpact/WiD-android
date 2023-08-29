package andpact.project.wid.util

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    holeRadius = 70f // Hole radius as percentage of the chart
                    setHoleColor(ContextCompat.getColor(context, R.color.transparent))

                    setDrawCenterText(true) // Draw center text

                    if (forReadDay) {
                        centerText = "오후 | 오전"
                    } else {
                        centerText = date.dayOfMonth.toString()
                    }

                    setCenterTextSize(16f)

                    if (wiDList.isEmpty()) {
                        setCenterTextColor(ContextCompat.getColor(context, R.color.gray))
                    } else {
                        setCenterTextColor(ContextCompat.getColor(context, R.color.black))
                    }

                    val dataSet = PieDataSet(pieEntries, "")
                    val colors = pieEntries.map { entry ->
                        val label = entry.label ?: ""
                        val colorId = DataMapsUtil.colorMap[label] ?: R.color.gray
                        ContextCompat.getColor(context, colorId)
                    }
                    dataSet.colors = colors
                    val data = PieData(dataSet)
                    data.setDrawValues(false)
                    this.data = data
                    this.invalidate()
                }
            })
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

                isDrawHoleEnabled = true
                holeRadius = 70f // Hole radius as percentage of the chart
                setHoleColor(ContextCompat.getColor(context, R.color.transparent))

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