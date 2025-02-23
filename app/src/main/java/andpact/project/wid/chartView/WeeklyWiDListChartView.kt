package andpact.project.wid.chartView

import andpact.project.wid.model.*
import andpact.project.wid.ui.theme.Transparent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    startDate: LocalDate,
    finishDate: LocalDate,
    wiDList: List<WiD>
) {
    val TAG = "WeeklyWiDListChartView"

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
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 더미
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "자정",
                style = MaterialTheme.typography.bodySmall,
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
                    style = MaterialTheme.typography.bodySmall,
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
                    .fillMaxHeight(),
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
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val days = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1

            for (i in 0 until days) {
                val currentDate = startDate.plusDays(i.toLong())

                // currentDate에 해당하는 WiD 리스트 필터링
                val dailyWiDList = wiDList.filter { it.start.toLocalDate() == currentDate }

                // 막대 차트 데이터 생성
                val barChartData = mutableListOf<TitleDurationChartData>()
                val totalMinutes = 24 * 60
                var currentMinute = 0

                if (dailyWiDList.isEmpty()) {
                    val noBarChartData = TitleDurationChartData(
                        duration = Duration.ofMinutes(totalMinutes.toLong()), // 수정된 부분
                        title = Title.UNTITLED
                    )
                    barChartData.add(noBarChartData)
                } else {
                    for (wiD in dailyWiDList) {
                        val startMinutes = wiD.start.hour * 60 + wiD.start.minute

                        // 비어 있는 시간대의 엔트리 추가
                        if (startMinutes > currentMinute) {
                            val emptyMinutes = startMinutes - currentMinute
                            val emptyBarChartData = TitleDurationChartData(
                                duration = Duration.ofMinutes(emptyMinutes.toLong()), // 수정된 부분
                                title = Title.UNTITLED
                            )
                            barChartData.add(emptyBarChartData)
                        }

                        // WiD 데이터 추가
                        val durationMinutes = wiD.duration.toMinutes().toInt()
                        if (durationMinutes >= 1) {
                            val wiDBarChartData = TitleDurationChartData(
                                duration = wiD.duration, // 수정된 부분
                                title = wiD.title
                            )
                            barChartData.add(wiDBarChartData)
                        }

                        // 시작 시간 업데이트
                        currentMinute = startMinutes + durationMinutes
                    }

                    // 남은 시간대 비어 있는 막대 추가
                    if (currentMinute < totalMinutes) {
                        val emptyMinutes = totalMinutes - currentMinute
                        val emptyBarChartData = TitleDurationChartData(
                            duration = Duration.ofMinutes(emptyMinutes.toLong()), // 수정된 부분
                            title = Title.UNTITLED
                        )
                        barChartData.add(emptyBarChartData)
                    }
                }

                // 그래프
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .clip(shape = MaterialTheme.shapes.extraSmall)
                ) {
                    for (barData in barChartData) {
                        val barHeight = barData.duration.toMinutes().toFloat() / totalMinutes // duration을 비율로 계산
                        val barColor = if (barData.title == Title.UNTITLED) MaterialTheme.colorScheme.surfaceContainer else barData.title.color
                        if (0.01f <= barHeight) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(barHeight) // 계산된 높이를 weight로 설정
                                    .background(color = barColor)
                            )
                        }
                    }
                }
            }
        }

        // 날짜
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 더미
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "자정",
                style = MaterialTheme.typography.bodySmall,
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
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = textColor // 조건부 색상 적용
                    )
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeeklyWiDListStackedVerticalBarChartPreview() {
    val days: Long = 7
    val tmpStartDate = LocalDate.now().minusDays(1)
    val tmpFinishDate = tmpStartDate.plusDays(days - 1)

    val tmpWiDList = mutableListOf<WiD>()

    for (index in 0 until days) {
        val indexDate = tmpStartDate.plusDays(index)

        tmpWiDList.add(
            WiD(
                id = "tmpWiD",
                title = Title.STUDY,
                subTitle = SubTitle.UNSELECTED_STUDY,
                start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)),
                finish = LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0)),
                duration = Duration.ofHours(2),
                city = City.SEOUL,
                exp = 0,
                tool = Tool.LIST
            )
        )

        tmpWiDList.add(
            WiD(
                id = "tmpWiD",
                title = Title.STUDY,
                subTitle = SubTitle.UNSELECTED_STUDY,
                start = LocalDateTime.of(LocalDate.now(), LocalTime.of(4, 0)),
                finish = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)),
                duration = Duration.ofHours(3),
                city = City.BUSAN,
                exp = 0,
                tool = Tool.LIST
            )
        )
    }

    WeeklyWiDListChartView(
        startDate = tmpStartDate,
        finishDate = tmpFinishDate,
        wiDList = tmpWiDList
    )
}