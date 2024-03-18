package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class WiD (
    val id: Long,
//    val username: String,
    val date: LocalDate,
    val title: String,
    val start: LocalTime,
    val finish: LocalTime,
    val duration: Duration,
//    val content: String
) {
    constructor(
        id: Long,
//        username: String,
        date: LocalDate,
        title: String,
        start: LocalTime,
        finish: LocalTime,
        durationMillis: Long,
//        content: String
    ) : this(
        id,
//        username,
        date,
        title,
        start,
        finish,
        Duration.ofMillis(durationMillis),
//        content
    )
}