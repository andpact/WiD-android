package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * 하루 생성 제한 12개 or 24개
 * id = "currentWiD" -> 현재 기록 중
 * id = "newWiD"("lastNewWiD") -> 빈 문자열(가장 최근 빈 문자열)
 * id = 랜덤 문자열 -> 서버에서 가져온 기록
 */
data class WiD(
    val id: String, // 14자리 문자열
    val date: LocalDate, // <-> 서버 : String("yyyy-MM-dd")
//    val category: Category, // <-> 서버 : String
    val title: Title, // <-> 서버 : String
    val start: LocalTime, // <-> 서버 : TimeStamp
    val finish: LocalTime, // <-> 서버 : TimeStamp
    val duration: Duration, // <-> 서버 : Int
    val exp: Int, // <-> 서버 : Int
    val createdBy: CurrentTool // <-> 서버 : String
//    val location: City // <-> 서버 : String
) {
    companion object {
        fun default(): WiD {
            return WiD(
                id = "",
                date = LocalDate.now(),
                title = Title.UNTITLED,
                start = LocalTime.MIN,
                finish = LocalTime.MIN,
                duration = Duration.ZERO,
                exp = 0,
                createdBy = CurrentTool.LIST
            )
        }
    }
}