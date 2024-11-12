package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate

data class DateDurationChartData(
    val date: LocalDate,
    val duration: Duration
)