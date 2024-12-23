package andpact.project.wid.tmp

//import andpact.project.wid.R
//import andpact.project.wid.model.DateDurationChartData
//import andpact.project.wid.model.Title
//import andpact.project.wid.model.WiD
//import andpact.project.wid.ui.theme.DeepSkyBlue
//import andpact.project.wid.ui.theme.OrangeRed
//import andpact.project.wid.ui.theme.Transparent
//import andpact.project.wid.ui.theme.Typography
//import andpact.project.wid.util.*
//import android.util.Log
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.content.res.ResourcesCompat
//import java.time.Duration
//import java.time.LocalDate
//import java.time.LocalTime
//import java.time.temporal.ChronoUnit
//
//@Composable
//fun TitleWiDListLineChartView(
//    modifier: Modifier = Modifier,
//    startDate: LocalDate,
//    finishDate: LocalDate,
//    currentTitle: Title,
//    wiDList: List<WiD>
//) {
//    val TAG = "TitleWiDListLineChartView"
//
//    val localContext = LocalContext.current // 폰트 불러오기 위해 선언함.
//    val colorScheme = MaterialTheme.colorScheme // 캔버스 밖에 선언해야 함.
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//        onDispose { Log.d(TAG, "disposed") }
//    }
//
//    val filteredWiDList = wiDList
//        .filter { it.title == currentTitle && it.date in startDate..finishDate }
//
//    val chartDataList = (0..ChronoUnit.DAYS.between(startDate, finishDate).toInt())
//        .map { startDate.plusDays(it.toLong()) } // startDate부터 finishDate까지 모든 날짜 생성
//        .map { date ->
//            val totalDuration = filteredWiDList
//                .filter { it.date == date }
//                .fold(Duration.ZERO) { acc, wiD -> acc + wiD.duration }
//            DateDurationChartData(
//                date = date,
//                duration = totalDuration
//            )
//        }
//        .sortedBy { it.date } // 날짜순 정렬
//
//
////    val maxDuration = chartDataList.maxOfOrNull { it.duration } ?: Duration.ZERO
////    val minDuration = chartDataList.minOfOrNull { it.duration } ?: Duration.ZERO
//    val maxDurationSeconds = chartDataList.maxOfOrNull { it.duration.seconds } ?: 0L
//    val minDurationSeconds = chartDataList.minOfOrNull { it.duration.seconds } ?: 0L
//
//    val durationRange = if (minDurationSeconds == maxDurationSeconds) {
//        1f // 모든 값이 같을 때 최소 1로 설정하여 중간에 표시
//    } else {
//        (maxDurationSeconds - minDurationSeconds).toFloat().coerceAtLeast(1f)
//    }
//
//    Canvas(
//        modifier = modifier
//            .fillMaxWidth()
//            .aspectRatio(1f)
//    ) {
//        val xAxisSpace = size.width / (chartDataList.size - 1).coerceAtLeast(1)
//        val yAxisHeight = size.height
//
//        val durationToY = { duration: Duration ->
//            if (minDurationSeconds == maxDurationSeconds) {
//                yAxisHeight / 2 // 모든 값이 같을 때는 그래프의 중간 지점에 그리기
//            } else {
//                val normalizedValue = (duration.seconds - minDurationSeconds) / durationRange
//                yAxisHeight * (1 - normalizedValue)
//            }
//        }
//
//        if (chartDataList.isNotEmpty()) {
//            val path = Path().apply {
//                moveTo(
//                    0f,
//                    durationToY(chartDataList[0].duration)
//                )
//
//                chartDataList.forEachIndexed { index, data ->
//                    if (index > 0) {
//                        val x = index * xAxisSpace
//                        val y = durationToY(data.duration)
//                        lineTo(x, y)
//                    }
//                }
//            }
//
//            // 선 그리기
//            drawPath(
//                path = path,
//                color = colorScheme.onSurface,
//                style = Stroke(
//                    width = 2.dp.toPx(),
//                    cap = StrokeCap.Round,
//                    join = StrokeJoin.Round
//                )
//            )
//
//            // 요일, 날짜 표시(x축)
//            chartDataList.forEachIndexed { index, data ->
//                val x = index * xAxisSpace
//                val y = durationToY(data.duration)
//
//                val dayOfWeek = daysOfWeekFromMonday[index] // 요일
//                val dayText = "${data.date.dayOfMonth}일" // 날짜
//                val textPaint = android.graphics.Paint().apply {
//                    color = when (index) {
//                        5 -> DeepSkyBlue.toArgb()
//                        6 -> OrangeRed.toArgb()
//                        else -> colorScheme.onSurface.toArgb()
//                    }
//                    textSize = 36f
//                    textAlign = android.graphics.Paint.Align.CENTER
//                    typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_semi_bold)
//                }
//
//                val durationTextPaint = android.graphics.Paint().apply {
//                    color = colorScheme.onSurface.toArgb()
//                    textSize = 36f
//                    textAlign = android.graphics.Paint.Align.CENTER
//                    typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_semi_bold)
//                }
//
//                drawContext.canvas.nativeCanvas.apply {
//                    // 요일
//                    drawText(
//                        dayOfWeek,
//                        x,
//                        -48f,
//                        textPaint
//                    )
//
//                    val durationText = getDurationStringEN(duration = data.duration)
//                    val padding = 8.dp.toPx()
//                    val textBounds = android.graphics.Rect()
//                    durationTextPaint.getTextBounds(durationText, 0, durationText.length, textBounds) // 얘 넣어줘야 함.
//
//                    // 소요 시간 배경
//                    drawRect(
//                        x - textBounds.width() / 2f - padding,
//                        y - textBounds.height(),
//                        x + textBounds.width() / 2f + padding,
//                        y + padding,
//                        android.graphics.Paint().apply {
//                            color = colorScheme.secondaryContainer.toArgb()
//                        }
//                    )
//
//                    // 소요 시간
//                    drawText(
//                        durationText,
//                        x,
//                        y + 12f,
//                        durationTextPaint
//                    )
//
//                    // 날짜
//                    drawText(
//                        dayText,
//                        x,
//                        size.height + 60f,
//                        textPaint
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TitleWiDListLineChartPreview() {
//}