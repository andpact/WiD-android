package andpact.project.wid.fragment

import andpact.project.wid.model.WiD
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun LineChartFragment(wiDList: List<WiD>) {

    val lineChartData = listOf(
        Entry(0f, 2f),
        Entry(1f, 5f),
        Entry(2f, 8f),
        Entry(3f, 3f),
        Entry(4f, 10f)
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
        AndroidView(factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)

                val leftAxis: YAxis = axisLeft
                leftAxis.removeAllLimitLines()
                leftAxis.setDrawZeroLine(false)
                leftAxis.setDrawGridLines(true)

                axisRight.isEnabled = false

                // Add data to the Line Chart
                val dataSet = LineDataSet(lineChartData, "Sample")
                dataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()
                dataSet.setDrawCircles(false)
                dataSet.setDrawValues(true)

                val data = LineData(dataSet)
                setData(data)
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
fun LineChartFragmentPreview() {
    LineChartFragment(emptyList())
}