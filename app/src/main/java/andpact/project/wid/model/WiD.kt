package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class WiD (
    val id: String,
    var date: LocalDate,
    val title: String,
    var start: LocalTime,
    val finish: LocalTime,
    val duration: Duration,
) {
//    override fun toString(): String {
//        return "$id,$date,$title,$start,$finish,${duration.toMillis()}"
//    }
//    constructor() : this(
//        0L,
//        LocalDate.now(),
//        "",
//        LocalTime.now(),
//        LocalTime.now(),
//        Duration.ZERO
//    )

    constructor(
        id: String,
        date: LocalDate,
        title: String,
        start: LocalTime,
        finish: LocalTime,
        durationMillis: Long,
    ) : this(
        id,
        date,
        title,
        start,
        finish,
        Duration.ofMillis(durationMillis),
    )

//    companion object {
//        fun fromString(string: String): WiD {
//            val parts = string.split(",")
//            require(parts.size == 6) { "Invalid string format" }
//            val id = parts[0].toLong()
//            val date = LocalDate.parse(parts[1])
//            val title = parts[2]
//            val start = LocalTime.parse(parts[3])
//            val finish = LocalTime.parse(parts[4])
//            val durationMillis = parts[5].toLong()
//            val duration = Duration.ofMillis(durationMillis)
//            return WiD(id, date, title, start, finish, duration)
//        }
//    }
}