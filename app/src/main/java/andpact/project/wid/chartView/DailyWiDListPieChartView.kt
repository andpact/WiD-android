package andpact.project.wid.chartView

import andpact.project.wid.R
import andpact.project.wid.model.TitleDurationChartData
import andpact.project.wid.model.WiD
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.Title
import andpact.project.wid.util.getFullWiDListFromWiDList
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
import androidx.core.content.res.ResourcesCompat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.*

@Composable
fun DailyWiDListPieChartView(
    fullWiDList: List<WiD>,
    onNewWiDClicked: (newWiD: WiD) -> Unit,
    onWiDClicked: (wiD: WiD) -> Unit,
    modifier: Modifier = Modifier
) {
    val TAG = "DailyWiDListPieChartView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val localContext = LocalContext.current // 폰트 불러오기 위해 선언함.
    val colorScheme = MaterialTheme.colorScheme // 캔버스 밖에 선언해야함.

    val totalDuration = Duration.ofHours(24).seconds
    val chartDataList = fullWiDList.map { TitleDurationChartData(it.title, it.duration) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1f)
//            .pointerInput(Unit) {
//                detectTapGestures { tapOffset ->
//                    // 캔버스 중심 좌표
//                    val centerX = size.width / 2
//                    val centerY = size.height / 2
//                    val tapX = tapOffset.x - centerX
//                    val tapY = tapOffset.y - centerY
//                    val tapRadius = hypot(tapX, tapY) // 클릭 반지름
//                    val tapAngle = (atan2(tapY, tapX) * 180f / PI.toFloat() + 360f) % 360f // 클릭 각도
//
//                    val outerRadius = size.width / 2f
//                    val innerRadius = outerRadius * 0.4f
//
//                    // 클릭이 파이 차트의 유효 반지름 내에 있는지 확인
//                    if (tapRadius !in innerRadius..outerRadius) return@detectTapGestures
//
//                    var startAngle = -90f
//                    val gapAngle = 1f
//                    val halfGapAngle = gapAngle / 2
//
//                    chartDataList.forEachIndexed { index, data ->
//                        val sweepAngle = (data.duration.seconds.toFloat() / totalDuration) * 360f
//                        if (sweepAngle <= 0) return@forEachIndexed
//
//                        // 아크 시작 각도와 끝 각도 설정 (간격 적용)
//                        val arcStartAngle = startAngle + halfGapAngle
//                        val arcEndAngle = arcStartAngle + sweepAngle - gapAngle
//
//                        // 클릭 위치가 해당 아크의 각도 내에 있는지 확인
//                        if (tapAngle in arcStartAngle..arcEndAngle) {
//                            val tappedWiD = fullWiDList[index]
//                            if (tappedWiD.id == "newWiD" || tappedWiD.id == "lastNewWiD") {
//                                onNewWiDClicked(tappedWiD)
//                            } else if (tappedWiD.id != "currentWiD") {
//                                onWiDClicked(tappedWiD)
//                            }
//                            return@detectTapGestures
//                        }
//
//                        // 다음 아크의 시작 각도 설정
//                        startAngle += sweepAngle + halfGapAngle
//                    }
//                }
//            }
    ) {
        var startAngle = -90f
        val gapAngle = 1f // 각 아크 사이의 간격을 나타내는 각도
        val halfGapAngle = gapAngle / 2 // 각 아크 앞뒤로 반씩 빈 공간을 할당

        // 파이 차트
        chartDataList.forEach { data ->
            val sweepAngle = (data.duration.seconds.toFloat() / totalDuration) * 360f
            if (sweepAngle <= 0) return@forEach

            startAngle += halfGapAngle

            drawArc(
                color = data.title.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle - gapAngle,
                useCenter = true,
                size = Size(size.width, size.height),
            )

            startAngle += sweepAngle - gapAngle + halfGapAngle
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
            textSize = radius / 10
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_regular)
        }

        for (i in 0 until 24) {
            val angleInDegree = (i * 15.0) - 90.0 // 360도를 24등분, 초기 각도를 0으로 설정
            val angleInRadian = Math.toRadians(angleInDegree)

            val x = centerX + radius * cos(angleInRadian)
            val y = centerY + radius * sin(angleInRadian)

            val timeText = when (i) {
                0 -> "자정"
                6 -> "오전 6시"
                12 -> "정오"
                18 -> "오후 6시"
                else -> (i % 12).toString()
            }

            val paint = when (i) {
                0, 12 -> textPaint.apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_extra_bold)
                }
                6 -> textPaint.apply {
                    textAlign = android.graphics.Paint.Align.RIGHT
                    typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_extra_bold)
                }
                18 -> textPaint.apply {
                    textAlign = android.graphics.Paint.Align.LEFT
                    typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_extra_bold)
                }
                else -> textPaint
            }

            drawContext.canvas.nativeCanvas.drawText(
                timeText,
                x.toFloat(),
                y.toFloat(),
                paint
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyWiDListPieChartPreview() {
    val yesterday = LocalDate.now().minusDays(1)
    val today = LocalDate.now()
    val now = LocalTime.now().withNano(0)
    val tmpWiDList = mutableListOf<WiD>()

    tmpWiDList.add(
        WiD(
            id = "tmpWiD",
            date = yesterday,
            title = Title.STUDY,
            start = LocalTime.of(0, 0),
            finish = LocalTime.of(1, 0),
            duration = Duration.ofHours(1),
            createdBy = CurrentTool.LIST
        )
    )
    tmpWiDList.add(
        WiD(
            id = "tmpWiD2",
            date = yesterday,
            title = Title.STUDY,
            start = LocalTime.of(2, 0),
            finish = LocalTime.of(5, 0),
            duration = Duration.ofHours(3),
            createdBy = CurrentTool.LIST
        )
    )
    tmpWiDList.add(
        WiD(
            id = "tmpWiD3",
            date = yesterday,
            title = Title.STUDY,
            start = LocalTime.of(6, 0),
            finish = LocalTime.of(7, 0),
            duration = Duration.ofHours(1),
            createdBy = CurrentTool.LIST
        )
    )

    val tmpFullWiDList = getFullWiDListFromWiDList(
        date = yesterday,
        wiDList = tmpWiDList,
        today = today,
        currentTime = now
    )

    DailyWiDListPieChartView(
        fullWiDList = tmpFullWiDList,
        onNewWiDClicked = { newWiD: WiD ->

        },
        onWiDClicked = { wiD: WiD ->

        }
    )
}