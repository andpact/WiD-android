package andpact.project.wid.chartView

import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationChartData
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.*

@Composable
fun DailyWiDListChartView(
    currentDate: LocalDate,
    fullWiDList: List<WiD>,
    modifier: Modifier = Modifier,
//    onWiDClicked: (WiD) -> Unit,
) {
    val TAG = "DailyWiDListChartView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val isDarkMode = isSystemInDarkTheme()

    val colorScheme = MaterialTheme.colorScheme // 캔버스 밖에 선언해야함.
    val typography = MaterialTheme.typography

    val dailyMaxDuration = Duration.ofHours(24).seconds

    val chartDataList: List<TitleDurationChartData> = fullWiDList.mapNotNull { wiD ->
        val dayStart = currentDate.atStartOfDay()
        val dayEnd = currentDate.plusDays(1).atStartOfDay()

        // WiD가 currentDate와 겹치는지 확인
        if (wiD.finish <= dayStart || dayEnd <= wiD.start ) {
            null // 현재 날짜에 포함되지 않는 경우 제거
        } else {
            val adjustedStart = maxOf(wiD.start, dayStart)
            val adjustedFinish = minOf(wiD.finish, dayEnd)

            // 조정된 기간의 duration 계산
            val adjustedDuration = Duration.between(adjustedStart, adjustedFinish)

            TitleDurationChartData(wiD.title, adjustedDuration)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
        ) {
            val outerRadius = size.width / 2.01f
            val innerRadius = size.width * 0.402f

            // 바깥쪽 테두리
            drawCircle(
                color = colorScheme.onSurface,
                radius = outerRadius,
                center = Offset(size.width / 2, size.height / 2),
                style = Stroke(width = 0.5.dp.toPx()) // 테두리 두께
            )

            // 안쪽 원 테두리
            drawCircle(
                color = colorScheme.onSurface,
                radius = innerRadius,
                center = Offset(size.width / 2, size.height / 2),
                style = Stroke(width = 0.5.dp.toPx()) // 테두리 두께
            )

            var startAngle = -90f

            // 파이 차트
            chartDataList.forEach { data: TitleDurationChartData ->
                val sweepAngle = (data.duration.seconds.toFloat() / dailyMaxDuration) * 360f
                if (sweepAngle <= 0) return@forEach

                val arcColor = if (isDarkMode) {
                    data.title.darkColor
                } else {
                    data.title.lightColor
                }

                drawArc(
                    color = arcColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height),
                )

                startAngle += sweepAngle
            }

            // 가운데 원
            drawCircle(
                color = colorScheme.surface,
                radius = size.width * 0.4f,
                center = Offset(size.width / 2, size.height / 2)
            )

            val radius: Float = size.minDimension / 2.8f // 원의 반지름
            val centerX = center.x
            val centerY = center.y + radius / 25

            val textPaint = android.graphics.Paint().apply {
                color = colorScheme.onSurface.toArgb()
//                textSize = typography.labelSmall.fontSize.toPx()
                textSize = typography.bodyMedium.fontSize.toPx()
            }

            for (i in 0 until 24) {
                val angleInDegree = (i * 15.0) - 90.0 // 360도를 24등분, 초기 각도를 0으로 설정
                val angleInRadian = Math.toRadians(angleInDegree)

                val x = centerX + radius * cos(angleInRadian).toFloat()
                val y = centerY + radius * sin(angleInRadian).toFloat()

                val timeText = when (i) {
                    0 -> "자정"
                    6 -> "오전 6시"
                    12 -> "정오"
                    18 -> "오후 18시"
                    else -> "$i"
                }

                val paint = when (i) {
                    6 -> textPaint.apply {
                        color = colorScheme.onTertiaryContainer.toArgb()
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    18 -> textPaint.apply {
                        color = colorScheme.onTertiaryContainer.toArgb()
                        textAlign = android.graphics.Paint.Align.LEFT
                    }
                    else -> textPaint.apply {
                        color = colorScheme.onSurface.toArgb()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                }

                val padding = 4.dp.toPx()
                val textBounds = android.graphics.Rect()
                paint.getTextBounds(timeText, 0, timeText.length, textBounds) // 얘 넣어 줘야 함.

                val backgroundLeft: Float
                val backgroundRight: Float

                // 배경 사각형의 좌우 위치 조정
                when (i) {
                    6 -> {
                        // 오른쪽 정렬 (텍스트 기준으로 왼쪽으로 이동)
                        backgroundLeft = x - textBounds.width() - padding
                        backgroundRight = x + padding
                    }
                    18 -> {
                        // 왼쪽 정렬 (텍스트 기준으로 오른쪽으로 이동)
                        backgroundLeft = x - padding
                        backgroundRight = x + textBounds.width() + padding
                    }
                    else -> {
                        // 가운데 정렬
                        backgroundLeft = x - textBounds.width() / 2f - padding
                        backgroundRight = x + textBounds.width() / 2f + padding
                    }
                }

                drawContext.canvas.nativeCanvas.apply {
                    if (i == 0 || i == 6 || i == 12 || i == 18) {
                        // 배경 사각형 그리기
                        drawRect(
                            backgroundLeft,
                            y - textBounds.height() / 2f - 8.dp.toPx(),
                            backgroundRight,
                            y + textBounds.height() / 2f - 2.dp.toPx(),
                            android.graphics.Paint().apply {
                                color = colorScheme.tertiaryContainer.toArgb()
                            }
                        )
                    }

                    // 시간 표시
                    drawText(
                        timeText,
                        x,
                        y,
                        paint
                    )
                }
            }
        }
    }
}

//@Composable
//fun DailyWiDListChartView(
//    currentDate: LocalDate,
//    fullWiDList: List<WiD>,
//    modifier: Modifier = Modifier,
//    onWiDClicked: (WiD) -> Unit,
//) {
//    val TAG = "DailyWiDListChartView"
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//        onDispose { Log.d(TAG, "disposed") }
//    }
//
//    val isDarkMode = isSystemInDarkTheme()
//    val colorScheme = MaterialTheme.colorScheme
//    val dailyMaxDuration = Duration.ofHours(24).seconds
//
//    val chartDataList: List<Pair<WiD, Float>> = fullWiDList.mapNotNull { wiD ->
//        val dayStart = currentDate.atStartOfDay()
//        val dayEnd = currentDate.plusDays(1).atStartOfDay()
//
//        if (wiD.finish <= dayStart || dayEnd <= wiD.start) {
//            null
//        } else {
//            val adjustedStart = maxOf(wiD.start, dayStart)
//            val adjustedFinish = minOf(wiD.finish, dayEnd)
//            val adjustedDuration = Duration.between(adjustedStart, adjustedFinish)
//            val sweepAngle = (adjustedDuration.seconds.toFloat() / dailyMaxDuration) * 360f
//            wiD to sweepAngle
//        }
//    }
//
//    Box(
//        modifier = modifier.fillMaxWidth(),
//        contentAlignment = Alignment.Center
//    ) {
//        Canvas(
//            modifier = Modifier
//                .size(300.dp)
//                .pointerInput(Unit) {
//                    detectTapGestures { offset ->
//                        val center = Offset(size.width.toFloat() / 2, size.height.toFloat() / 2)
//                        val tappedAngle = atan2(offset.y - center.y, offset.x - center.x) * (180 / Math.PI).toFloat()
//                        val adjustedAngle = if (tappedAngle < -90) 360 + tappedAngle else tappedAngle + 90
//
//                        var accumulatedAngle = -90f
//                        for ((wiD, sweepAngle) in chartDataList) {
//                            if (adjustedAngle in accumulatedAngle..(accumulatedAngle + sweepAngle)) {
//                                onWiDClicked(wiD)
//                                break
//                            }
//                            accumulatedAngle += sweepAngle
//                        }
//                    }
//                }
//        ) {
//            var startAngle = -90f
//
//            chartDataList.forEach { (wiD, sweepAngle) ->
//                if (sweepAngle > 0) {
//                    val arcColor = if (isDarkMode) wiD.title.darkColor else wiD.title.lightColor
//                    drawArc(
//                        color = arcColor,
//                        startAngle = startAngle,
//                        sweepAngle = sweepAngle,
//                        useCenter = true,
//                        size = Size(size.width, size.height)
//                    )
//                }
//                startAngle += sweepAngle
//            }
//
//            drawCircle(
//                color = colorScheme.surface,
//                radius = size.width * 0.4f,
//                center = Offset(size.width / 2, size.height / 2)
//            )
//        }
//    }
//}