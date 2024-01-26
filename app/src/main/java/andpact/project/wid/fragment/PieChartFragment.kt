package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.getTotalDurationFromWiDList
import andpact.project.wid.util.getTotalDurationPercentageFromWiDList
import android.graphics.Paint
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DateBasedPieChartFragment(wiDList: List<WiD>) {
    val localContext = LocalContext.current // 폰트 불러오기 위해 선언함.

//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp

    val pieEntries = mutableListOf<PieEntry>()

    val totalMinutes = 24 * 60 // 1440분(24시간)
    var currentMinute = 0

    for (wiD in wiDList) {
        val finishMinutes = wiD.finish.hour * 60 + wiD.finish.minute

        // 비어 있는 시간대의 엔트리 추가
        if (wiD.start.hour * 60 + wiD.start.minute > currentMinute) {
            val emptyMinutes = wiD.start.hour * 60 + wiD.start.minute - currentMinute
            pieEntries.add(PieEntry(emptyMinutes.toFloat(), ""))
        }

        // 엔트리 셋에 해당 WiD 객체의 시간대를 추가
        currentMinute = wiD.start.hour * 60 + wiD.start.minute
        pieEntries.add(PieEntry((finishMinutes - currentMinute).toFloat(), wiD.title))

        // 시작 시간 업데이트
        currentMinute = wiD.finish.hour * 60 + wiD.finish.minute
    }

    // 마지막 WiD 객체 이후의 비어 있는 시간대의 엔트리 추가
    if (currentMinute < totalMinutes) {
        val emptyMinutes = totalMinutes - currentMinute
        pieEntries.add(PieEntry(emptyMinutes.toFloat(), ""))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "오후 오전",
                style = Typography.bodyMedium,
                fontSize = 10.sp
            )

            Text(
                text = "${getTotalDurationPercentageFromWiDList(wiDList = wiDList)}%",
                style = Typography.titleLarge,
                fontSize = 30.sp
            )

            Text(
                text = "${formatDuration(getTotalDurationFromWiDList(wiDList = wiDList), mode = 1)} / 24시간",
                style = Typography.bodyMedium,
                fontSize = 10.sp
            )
        }

        Crossfade(targetState = pieEntries) { pieEntries ->
            val colorScheme = MaterialTheme.colorScheme
            AndroidView(factory = { context ->
                PieChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setUsePercentValues(false) // Use absolute values
                    description.isEnabled = false // Disable description
                    legend.isEnabled = false // Disable legend

//                    animateX(500)
//                    animateY(500)

                    setDrawEntryLabels(false)
                    setTouchEnabled(false) // Disable touch gestures for zooming

                    isDrawHoleEnabled = true
                    holeRadius = 80f
                    setHoleColor(Transparent.toArgb())

//                    setDrawCenterText(true)
//                    centerText = buildAnnotatedString {
//                        withStyle(style = SpanStyle(
//                            fontFamily = pretendardRegular,
////                            fontSize = 30.sp
//                            fontSize = (size).sp
//                        )
//                        ) {
//                            append("${getTotalDurationPercentageFromWiDList(wiDList = wiDList)}%\n")
//                        }
//                        withStyle(style = SpanStyle(
//                            fontFamily = pretendardRegular,
//                            fontSize = 14.sp
//                        )) {
//                            append("${formatDuration(getTotalDurationFromWiDList(wiDList = wiDList), mode = 1)} / 24시간")
//                        }
//                    }

//                    setCenterTextColor(Color.Black.toArgb())

                    val dataSet = PieDataSet(pieEntries, "")
                    val colors = pieEntries.map { entry ->
                        val label = entry.label ?: ""
                        (colorMap[label] ?: colorScheme.primary).toArgb()

//                        val colorId = colorMap[label] ?: R.color.black
//                        ContextCompat.getColor(context, colorId)
                    }
                    dataSet.colors = colors
                    val data = PieData(dataSet)
                    data.setDrawValues(false)
                    this.data = data
                    this.invalidate()
                }
            })

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
//                val radius: Float = size.minDimension / 1.85f // 원의 반지름
                val radius: Float = size.minDimension / 1.9f // 원의 반지름
                val centerX = center.x
                val centerY = center.y + radius / 25

                val textPaint = Paint().apply {
                    color = colorScheme.secondary.toArgb()
                    textSize = radius / 10
                    textAlign = Paint.Align.CENTER
                    typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_extra_bold)
                }

                for (i in 0 until 24) {
                    val hour = if (i < 12) i + 1 else i - 11 // 0...23을 1...12, 1...12로 변환
                    val angleInDegree = (i * 15.0) - 75.0 // 360도를 24등분, 초기 각도를 0으로 설정
                    val angleInRadian = Math.toRadians(angleInDegree)

                    // 시간 텍스트 좌표 계산
                    val x = centerX + radius * 0.8f * cos(angleInRadian)
                    val y = centerY + radius * 0.8f * sin(angleInRadian)

                    // 시간 텍스트 그리기
                    drawContext.canvas.nativeCanvas.drawText(hour.toString(), x.toFloat(), y.toFloat(), textPaint)
                }
            }
        }
    }
}

@Composable
fun PeriodBasedPieChartFragment(date: LocalDate, wiDList: List<WiD>) {
    val pieEntries = mutableListOf<PieEntry>()

    val totalMinutes = 24 * 60 // 1440분(24시간)
    var currentMinute = 0

    for (wiD in wiDList) {
        val finishMinutes = wiD.finish.hour * 60 + wiD.finish.minute

        // 비어 있는 시간대의 엔트리 추가
        if (wiD.start.hour * 60 + wiD.start.minute > currentMinute) {
            val emptyMinutes = wiD.start.hour * 60 + wiD.start.minute - currentMinute
            pieEntries.add(PieEntry(emptyMinutes.toFloat(), ""))
        }

        // 엔트리 셋에 해당 WiD 객체의 시간대를 추가
        currentMinute = wiD.start.hour * 60 + wiD.start.minute
        pieEntries.add(PieEntry((finishMinutes - currentMinute).toFloat(), wiD.title))

        // 시작 시간 업데이트
        currentMinute = wiD.finish.hour * 60 + wiD.finish.minute
    }

    // 마지막 WiD 객체 이후의 비어 있는 시간대의 엔트리 추가
    if (currentMinute < totalMinutes) {
        val emptyMinutes = totalMinutes - currentMinute
        pieEntries.add(PieEntry(emptyMinutes.toFloat(), ""))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Crossfade 적용 안하면 차트 갱신이 안된다.
        Crossfade(targetState = pieEntries) { pieEntries ->
            val colorScheme = MaterialTheme.colorScheme
            AndroidView(factory = { context ->
                PieChart(context).apply {
                    // 설정
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

//                    animateX(500)
//                    animateY(500)

                    setUsePercentValues(false) // Use absolute values
                    description.isEnabled = false // Disable description
                    legend.isEnabled = false // Disable legend

                    setDrawEntryLabels(false)
                    setTouchEnabled(false) // Disable touch gestures for zooming

                    isDrawHoleEnabled = true
                    holeRadius = 80f
                    setHoleColor(Transparent.toArgb())

                    setDrawCenterText(true)
                    centerText = date.dayOfMonth.toString()
                    setCenterTextSize(12f)

                    if (wiDList.isEmpty()) {
                        setCenterTextColor(DarkGray.toArgb())
                    } else {
                        setCenterTextColor(colorScheme.primary.toArgb())
                    }

                    val dataSet = PieDataSet(pieEntries, "")
                    val colors = pieEntries.map { entry ->
                        val label = entry.label ?: ""
                        (colorMap[label] ?: colorScheme.primary).toArgb()
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

//@Composable
//fun EmptyPieChartView() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f),
//        contentAlignment = Alignment.Center
//    ) {
//        AndroidView(factory = { context ->
//            PieChart(context).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//                setUsePercentValues(false) // Use absolute values
//                description.isEnabled = false // Disable description
//                legend.isEnabled = false // Disable legend
//
//                setDrawEntryLabels(false)
//                setTouchEnabled(false) // Disable touch gestures for zooming
//
////                isDrawHoleEnabled = true
////                holeRadius = 70f // Hole radius as percentage of the chart
////                setHoleColor(ContextCompat.getColor(context, R.color.transparent))
//
//                val dataSet = PieDataSet(listOf(PieEntry(1.0F, "")), "")
//                dataSet.colors = listOf(ContextCompat.getColor(context, R.color.transparent))
//                val data = PieData(dataSet)
//                data.setDrawValues(false)
//                this.data = data
//                this.invalidate()
//            }
//        })
//    }
//}

@Preview(showBackground = true)
@Composable
fun DateBasedPieChartFragmentPreview() {
    DateBasedPieChartFragment(emptyList())
}