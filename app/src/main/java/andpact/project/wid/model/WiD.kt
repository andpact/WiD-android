package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class WiD(
    val id: Long,
    val date: LocalDate,
    val title: String,
    val start: LocalTime,
    val finish: LocalTime,
    val duration: Duration,
    val detail: String
) {
    constructor(
        id: Long,
        date: LocalDate,
        title: String,
        start: LocalTime,
        finish: LocalTime,
        durationMillis: Long,
        detail: String
    ) : this(
        id,
        date,
        title,
        start,
        finish,
        Duration.ofMillis(durationMillis),
        detail
    )
}