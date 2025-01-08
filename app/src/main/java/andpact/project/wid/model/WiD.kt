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
    val title: Title, // <-> 서버 : String(부제목을 선택 안 할 수도 있기 때문에 제목이 필요함)
    val subTitle: SubTitle, // <-> 서버 : String
    val start: LocalTime, // <-> 서버 : TimeStamp
    val finish: LocalTime, // <-> 서버 : TimeStamp
    val duration: Duration, // <-> 서버 : Long
    val city: City, // <-> 서버 : String(도시로 나라를 참조할 수 있어서 나라를 필드로 넣지 않음)
    val exp: Int, // <-> 서버 : Long
    val createdBy: CurrentTool // <-> 서버 : String
) {
    companion object {
        fun default(): WiD {
            return WiD(
                id = "",
                date = LocalDate.now(),
                title = Title.UNTITLED,
                subTitle = SubTitle.UNSELECTED,
                start = LocalTime.MIN,
                finish = LocalTime.MIN,
                duration = Duration.ZERO,
                city = City.SEOUL,
                exp = 0,
                createdBy = CurrentTool.LIST
            )
        }
    }
}