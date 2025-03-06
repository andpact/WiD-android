package andpact.project.wid.chartView

import andpact.project.wid.model.*
import andpact.project.wid.ui.theme.Transparent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.*
import java.time.temporal.ChronoUnit

@Composable
fun WeeklyWiDListChartView(
    modifier: Modifier = Modifier,
    startDate: LocalDate, // 주의 시작 날짜로 보장됨.
    finishDate: LocalDate, // 주의 종 날짜로 보장됨.
    wiDList: List<WiD> // 각 기록의 시작과 종료는 같은 날짜에 포함되도록 보장됨.
) {
    val TAG = "WeeklyWiDListChartView"

    val isDarkMode = isSystemInDarkTheme()

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 요일
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 더미
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "자정",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = Transparent
            )

            val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

            daysOfWeek.forEachIndexed { index, day ->
                val textColor = when (index) {
                    5 -> MaterialTheme.colorScheme.onTertiaryContainer
                    6 -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
        ) {
            // 시간
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 12.dp), // 기록 개수, 기록률 높이
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (hour in 0..24 step 2) { // 2시간 간격으로 숫자 표시
                    val displayText = when (hour) {
                        0 -> "자정"
                        12 -> "정오"
                        24 -> "자정"
                        else -> hour.toString()
                    }

                    val backgroundColor = when (hour) {
                        0, 12, 24 -> MaterialTheme.colorScheme.tertiaryContainer // 배경색
                        else -> Transparent // 기본 투명
                    }

                    Text(
                        modifier = Modifier
                            .background(
                                color = backgroundColor,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(horizontal = 4.dp),
                        text = displayText,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val days = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1

            for (i in 0 until days) {
                val currentDate = startDate.plusDays(i.toLong())
                val dailyWiDList = wiDList.filter { it.start.toLocalDate() == currentDate }

                // 막대 차트 데이터 생성
                val maxSeconds = 24 * 60 * 60L
                var currentSecond = 0L
                val chartDataList = mutableListOf<TitleDurationChartData>()

                if (dailyWiDList.isEmpty()) {
                    chartDataList.add(
                        TitleDurationChartData(
                            title = Title.UNTITLED,
                            duration = Duration.ofSeconds(maxSeconds)
                        )
                    )
                } else {
                    val sortedDailyWiDList = dailyWiDList.sortedBy { it.start }

                    for (wiD in sortedDailyWiDList) {
                        val startSeconds = wiD.start.hour * 60 + wiD.start.minute + wiD.start.second

                        // 비어 있는 시간대의 엔트리 추가
                        if (currentSecond < startSeconds) {
                            val emptySeconds = startSeconds - currentSecond
                            chartDataList.add(
                                TitleDurationChartData(
                                    title = Title.UNTITLED,
                                    duration = Duration.ofSeconds(emptySeconds)
                                )
                            )
                        }

                        // WiD 데이터 추가
                        val durationSeconds = wiD.duration.seconds
                        if (0 < durationSeconds) {
                            chartDataList.add(
                                TitleDurationChartData(
                                    title = wiD.title,
                                    duration = wiD.duration
                                )
                            )
                        }

                        // 시작 시간 업데이트
                        currentSecond = startSeconds + durationSeconds
                    }

                    // 남은 시간대 비어 있는 막대 추가
                    if (currentSecond < maxSeconds) {
                        val emptyMinutes = maxSeconds - currentSecond
                        chartDataList.add(
                            TitleDurationChartData(
                                title = Title.UNTITLED,
                                duration = Duration.ofSeconds(emptyMinutes)
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

                // 막대 그래프 + 기록 개수 + 기록률
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // 그래프
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                            .clip(shape = MaterialTheme.shapes.extraSmall)
                    ) {
                        for (barData in chartDataList) {
                            val barHeight = barData.duration.seconds.toFloat() / maxSeconds // duration을 비율로 계산
                            val barColor = if (isDarkMode) {
                                barData.title.darkColor
                            } else {
                                barData.title.lightColor
                            }

                            if (0.01f <= barHeight) { // 0.01 이하는 오류가 나지? 아마?
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(barHeight) // 계산된 높이를 weight로 설정
                                        .background(color = barColor)
                                )
                            }
                        }
                    }

                    LinearProgressIndicator( // 기록 개수
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(shape = MaterialTheme.shapes.extraSmall),
                        progress = countFraction,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer
                    )

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(shape = MaterialTheme.shapes.extraSmall),
                        progress = totalDurationFraction,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        trackColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }
        }

        // 날짜
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // 더미
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "자정",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = Transparent
            )

            generateSequence(startDate) { it.plusDays(1) }
                .takeWhile { it <= finishDate }
                .forEach { currentDate ->
                    val dayNumber = currentDate.dayOfMonth
                    val dayOfWeek = currentDate.dayOfWeek // 요일 가져오기

                    // 토요일과 일요일에 대해 색상 설정
                    val textColor = when (dayOfWeek) {
                        DayOfWeek.SATURDAY -> MaterialTheme.colorScheme.onTertiaryContainer
                        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "${dayNumber}일", // "n일" 형식으로 날짜 표시
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = textColor // 조건부 색상 적용
                    )
                }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WeeklyWiDListStackedVerticalBarChartPreview() {
//    val days: Long = 7
//    val tmpStartDate = LocalDate.now().minusDays(1)
//    val tmpFinishDate = tmpStartDate.plusDays(days - 1)
//
//    val tmpWiDList = mutableListOf<WiD>()
//
//    for (index in 0 until days) {
//        val indexDate = tmpStartDate.plusDays(index)
//
//        tmpWiDList.add(
//            WiD(
//                id = "tmpWiD",
//                title = Title.STUDY,
//                subTitle = SubTitle.UNSELECTED_STUDY,
//                start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)),
//                finish = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0)),
//                duration = Duration.ofHours(2),
//                city = City.SEOUL,
//                exp = 0,
//                tool = Tool.LIST
//            )
//        )
//
//        tmpWiDList.add(
//            WiD(
//                id = "tmpWiD",
//                title = Title.STUDY,
//                subTitle = SubTitle.UNSELECTED_STUDY,
//                start = LocalDateTime.of(LocalDate.now(), LocalTime.of(4, 0)),
//                finish = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)),
//                duration = Duration.ofHours(3),
//                city = City.BUSAN,
//                exp = 0,
//                tool = Tool.LIST
//            )
//        )
//    }
//
//    WeeklyWiDListChartView(
//        startDate = tmpStartDate,
//        finishDate = tmpFinishDate,
//        wiDList = tmpWiDList
//    )
//}