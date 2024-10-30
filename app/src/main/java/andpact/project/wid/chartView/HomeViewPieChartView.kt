package andpact.project.wid.chartView

import andpact.project.wid.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Composable
fun HomeViewPieChartView(
    today: LocalDate,
    now: LocalTime,
    modifier: Modifier = Modifier
) {
    val localContext = LocalContext.current // 폰트 불러오기 위해 선언함.
    val colorScheme = MaterialTheme.colorScheme // 캔버스 밖에 선언해야함.

    val yearProgress = (today.dayOfYear.toFloat() / today.lengthOfYear().toFloat()) * 100
    val monthProgress = (today.dayOfMonth.toFloat() / YearMonth.from(today).lengthOfMonth().toFloat()) * 100
    val dayProgress = (now.toSecondOfDay().toFloat() / LocalTime.MAX.toSecondOfDay().toFloat()) * 100

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${today.year}년",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Canvas(
                modifier = Modifier
                    .aspectRatio(1f)
            ) {
                val radius = size.minDimension / 2

                drawArc(
                    color = colorScheme.secondaryContainer,
                    startAngle = -90f,
                    sweepAngle = (yearProgress / 100) * 360,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(
                        width = radius * 2,
                        height = radius * 2
                    ),
                    style = Stroke(
                        width = radius * 0.45f,
                        cap = StrokeCap.Round
                    )
                )

                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = colorScheme.onSurface.toArgb()
                        textSize = radius * 0.4f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_regular)
                    }

                    drawText(
                        "${yearProgress.toInt()}%",
                        center.x,
                        center.y + textPaint.textSize / 3,
                        textPaint
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${today.monthValue}월",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Canvas(
                modifier = Modifier
                    .aspectRatio(1f)
            ) {
                val radius = size.minDimension / 2

                drawArc(
                    color = colorScheme.secondaryContainer,
                    startAngle = -90f,
                    sweepAngle = (yearProgress / 100) * 360,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(
                        width = radius * 2,
                        height = radius * 2
                    ),
                    style = Stroke(
                        width = radius * 0.45f,
                        cap = StrokeCap.Round
                    )
                )

                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = colorScheme.onSurface.toArgb()
                        textSize = radius * 0.4f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_regular)
                    }

                    drawText(
                        "${monthProgress.toInt()}%",
                        center.x,
                        center.y + textPaint.textSize / 3,
                        textPaint
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${today.dayOfMonth}일",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Canvas(
                modifier = Modifier
                    .aspectRatio(1f)
            ) {
                val radius = size.minDimension / 2

                drawArc(
                    color = colorScheme.secondaryContainer,
                    startAngle = -90f,
                    sweepAngle = (yearProgress / 100) * 360,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(
                        width = radius * 2,
                        height = radius * 2
                    ),
                    style = Stroke(
                        width = radius * 0.45f,
                        cap = StrokeCap.Round
                    )
                )

                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = colorScheme.onSurface.toArgb()
                        textSize = radius * 0.4f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = ResourcesCompat.getFont(localContext, R.font.pretendard_regular)
                    }

                    drawText(
                        "${dayProgress.toInt()}%",
                        center.x,
                        center.y + textPaint.textSize / 3,
                        textPaint
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeViewPieChartPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HomeViewPieChartView(
            today = LocalDate.now(),
            now = LocalTime.now(),
        )
    }
}