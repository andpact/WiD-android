package andpact.project.wid.model

import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.Title
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/** 하루 생성 제한 12개 */
data class WiD(
    val id: String, // 14자리 문자열
    val date: LocalDate, // <-> 서버 : String("yyyy-MM-dd")
    val title: Title,
    val start: LocalTime, // <-> 서버 : TimeStamp
    val finish: LocalTime, // <-> 서버 : TimeStamp
    val duration: Duration, // <-> 서버 : Int
    val createdBy: CurrentTool, // <-> 서버 : String
//    val subTitle: Title,
//    val description: String, // 최대 20자
//    val location: String
)