package andpact.project.wid.chartView

import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationChartData
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalTime
import kotlin.math.*

@Composable
fun DailyWiDListChartView(
    fullWiDList: List<WiD>,
//    onWiDClicked: (wiD: WiD) -> Unit,
    modifier: Modifier = Modifier
) {
    val TAG = "DailyWiDListChartView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

//    val localContext = LocalContext.current // 폰트 불러오기 위해 선언함.
    val colorScheme = MaterialTheme.colorScheme // 캔버스 밖에 선언해야함.
    val typography = MaterialTheme.typography

    val totalDuration = Duration.ofHours(24).seconds

    // TODO: 날짜에 포함안되는 기록 앞 뒤 자르기.
    val chartDataList = fullWiDList.map { TitleDurationChartData(it.title, it.duration) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1f),
        onDraw = {
            var startAngle = -90f

            // 파이 차트
            chartDataList.forEach { data: TitleDurationChartData ->
                val sweepAngle = (data.duration.seconds.toFloat() / totalDuration) * 360f
                if (sweepAngle <= 0) return@forEach

                val arcColor = if (data.title == Title.UNTITLED) colorScheme.surfaceContainer else data.title.color

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
            val centerY = center.y + radius / 25 // TODO: 절대 값이 아니라 상대 값 사용?

            val textPaint = android.graphics.Paint().apply {
                color = colorScheme.onSurface.toArgb()
                textSize = typography.bodySmall.fontSize.toPx()
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
    )
}