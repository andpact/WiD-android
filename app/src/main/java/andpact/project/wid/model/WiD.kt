package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class WiD (
    val id: Long,
    val date: LocalDate,
    val title: String,
    val start: LocalTime,
    val finish: LocalTime,
    val duration: Duration
) {
    constructor(
        id: Long,
        date: LocalDate,
        title: String,
        start: LocalTime,
        finish: LocalTime,
        durationMillis: Long
    ) : this(
        id,
        date,
        title,
        start,
        finish,
        Duration.ofMillis(durationMillis)
    )
}