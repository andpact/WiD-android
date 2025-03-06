package andpact.project.wid.chartView

import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationChartData
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MonthlyWiDListChartView(
    modifier: Modifier = Modifier,
    startDate: LocalDate, // 월의 시작 날짜로 보장됨.
    finishDate: LocalDate, // 월의 종료 날짜로 보장됨.
    wiDList: List<WiD> // 각 기록의 시작과 종료는 같은 날짜에 포함되도록 보장됨.
) {
    val TAG = "MonthlyWiDListChartView"

    val isDarkMode = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme // 캔버스 밖에 선언해야함.

    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    0 -> colorScheme.onErrorContainer
                    6 -> colorScheme.onTertiaryContainer
                    else -> colorScheme.onSurface
                }

                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp), // 레이지 컬럼 안에 레이지 그리드를 쓸려면 높이를 지정해줘야함.
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val totalCells = 42
            val totalDays = startDate.lengthOfMonth()
            val firstDayOffset = startDate.dayOfWeek.value % 7

            items(totalCells) { index ->
                if (index < firstDayOffset || firstDayOffset + totalDays <= index) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "")// TODO: 조회 이전 달의 날짜와 다음 달의 날짜만 표시하기. 파이 차트나 표시기 없이.
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                } else {
                    val dayNumber = index - firstDayOffset + 1

                    val currentDate = startDate.withDayOfMonth(dayNumber)
                    val dailyWiDList = wiDList.filter { it.start.toLocalDate() == currentDate }

                    val fullDayStart = currentDate.atStartOfDay() // 00:00:00
                    val fullDayEnd = currentDate.atTime(LocalTime.of(23, 59, 59)) // 23:59:59

                    val chartDataList = mutableListOf<TitleDurationChartData>()
                    if (dailyWiDList.isEmpty()) {
                        // 해당 날짜에 기록이 없다면 전체가 UNTITLED
                        chartDataList.add(
                            TitleDurationChartData(
                                title = Title.UNTITLED,
                                duration = Duration.between(fullDayStart, fullDayEnd)
                            )
                        )
                    } else {
                        // 여러 기록이 있을 수 있으므로, 우선 전체 기록을 시간순으로 정렬
                        val sortedRecords = dailyWiDList.sortedBy { it.start }
                        var currentTime = fullDayStart

                        sortedRecords.forEach { wiD ->
                            // 기록 이전 구간이 있다면 UNTITLED로 추가
                            if (wiD.start.isAfter(currentTime)) {
                                chartDataList.add(
                                    TitleDurationChartData(
                                        title = Title.UNTITLED,
                                        duration = Duration.between(currentTime, wiD.start)
                                    )
                                )
                            }
                            // 기록 구간 (실제 기록의 타이틀 사용)
                            chartDataList.add(
                                TitleDurationChartData(
                                    title = wiD.title,
                                    duration = Duration.between(wiD.start, wiD.finish)
                                )
                            )
                            // 다음 구간의 시작 시간은 해당 기록의 종료시간
                            currentTime = wiD.finish
                        }
                        // 마지막 기록 이후 구간이 남았다면 UNTITLED로 추가
                        if (currentTime.isBefore(fullDayEnd)) {
                            chartDataList.add(
                                TitleDurationChartData(
                                    title = Title.UNTITLED,
                                    duration = Duration.between(currentTime, fullDayEnd)
                                )
                            )
                        }
                    }

                    val count = dailyWiDList.size
                    val maxCount = 24
                    val countFraction = (count / maxCount).toFloat().coerceIn(0f, 1f)
                    
                    val totalDuration = dailyWiDList.fold(Duration.ZERO) { acc, wiD -> acc + wiD.duration }
                    val dailyMaxDuration = Duration.ofHours(24).seconds
                    val totalDurationFraction = (totalDuration.seconds.toFloat() / dailyMaxDuration).coerceIn(0f, 1f)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f / 1f)
                            ) {
                                var startAngle = -90f
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

                                drawCircle(
                                    color = colorScheme.surface,
                                    radius = size.width * 0.4f,
                                    center = Offset(size.width / 2, size.height / 2)
                                )
                            }

                            val textColor = when (currentDate.dayOfWeek.value % 7) {
                                0 -> colorScheme.onErrorContainer // 일요일
                                6 -> colorScheme.onTertiaryContainer // 토요일
                                else -> colorScheme.onSurface // 평일
                            }

                            Text(
                                text = currentDate.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor // 요일 색상 적용
                            )

                        }

                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(shape = MaterialTheme.shapes.extraSmall),
                            progress = countFraction,
                            color = colorScheme.onSecondaryContainer,
                            trackColor = colorScheme.secondaryContainer
                        )

                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(shape = MaterialTheme.shapes.extraSmall),
                            progress = totalDurationFraction,
                            color = colorScheme.onTertiaryContainer,
                            trackColor = colorScheme.tertiaryContainer
                        )
                    }
                }
            }
        }
    }
}