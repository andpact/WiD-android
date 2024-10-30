package andpact.project.wid.model

import andpact.project.wid.util.CurrentTool
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

data class WiD (
    val id: String,
//    val email: String,
    val date: LocalDate, // 클라이언트 : LocalDate <-> 서버 : String("yyyy-MM-dd")
    val title: String,
//    val location: String,
    val start: LocalTime, // 클라이언트 : LocalDate <-> 서버 : TimeStamp
    val finish: LocalTime, // 클라이언트 : LocalDate <-> 서버 : TimeStamp
    val duration: Duration, // 클라이언트 : Duration <-> 서버 : Int
//    val detail: String, // WiDView에서 수정하도록.
//    val version: Int,
    val createdBy: CurrentTool, // 클라이언트 : CurrentTool <-> 서버 : String
//    val createdOn: LocalDate, // 클라이언트 : LocalDate <-> 서버 : TimeStamp
//    val modified: Boolean // 수정 여부
//    val lastModifiedOn: LocalDate // 최종 수정 날짜
) {
//    constructor(
//        id: String,
//        date: LocalDate,
//        title: String,
//        start: LocalTime,
//        finish: LocalTime,
//        durationMillis: Long,
//    ) : this(
//        id,
//        date,
//        title,
//        start,
//        finish,
//        Duration.ofMillis(durationMillis),
//    )

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