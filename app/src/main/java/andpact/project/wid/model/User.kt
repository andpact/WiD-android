package andpact.project.wid.model

import java.time.Duration
import java.time.LocalDate

/**
 * 파이어 베이스 문서 최대 크기는 1MB(1,048,576바이트)
 */
data class User(
    val email: String, // email이 Firebase User와 UserCollection의 User를 연결하는 필드임.
    val signedUpOn: LocalDate, // <-> String("yyyy-MM-dd")
    val city: City, // <-> String("BUSAN")
    val level: Int, // <-> Long
    val levelDateMap: Map<String, LocalDate>, // <-> Map<String, String("yyyy-MM-dd")>
    val currentExp: Int, // <-> Long (초단위) 기록 또는 계획으로 얻은 현재(레벨) 경험치
    val wiDTotalExp: Int, // <-> Long (초단위) 기록(WiD)으로 얻은 경험치
    val wiDMinLimit: Duration, // <-> Long (초단위)
    val wiDMaxLimit: Duration, // <-> Long (초단위)

//    val wilDTotalExp: Int, // <-> Int (초단위) 계획으로 얻은 경험치
//    val wiDIntervalLimit: Duration // <-> Int (초단위)








//    val wiDTitleFirstDateMap: Map<Title, LocalDate>, /** 제목 별 최초 기록 날짜 */
//    val wiDTitleLastDateMap: Map<Title, LocalDate>, /** 제목 별 최근 기록 날짜 */

//    val clientCount: Int
//    val statusMessage: String, // 상태 메시지

//    val plan: String = "STANDARD", // 요금제 Boolean or Enum(STANDARD(FREE), PRO) 만들어서 사용하자.

//    val signInMethod: List<String>, // 언제 필요함?
//    val lastSignedInOn: LocalDate, // 앱 종료할 때만 갱신하면 될듯?, 나중에 추가해도 문제 없음.

//    val ranking: Int,

//    val challengeTotalExp: Int, 챌린지로 얻은 경험치

//    val titleOrderList: List<String>, // 제목 순서 관리용, 순서 변경할 필요는 없을 것 같다.
//    val titleOnOffMap: Map<String, Boolean>,
//    val titleColorMap: Map<String, Color>, // 제목 별 색상 관리용

//    val language: LANGUAGE,

//    val currentTitle: String, // 클라이언트 : String <-> 서버 : String
//    val currentTool: CurrentTool, // 클라이언트 : CurrentTool <-> 서버 : String
//    val currentToolState: CurrentToolState, // 클라이언트 : ToolState <-> 서버 : String
//    val stopwatchStartTime: LocalTime, // 클라이언트 : LocalTime <-> 서버 : Timestamp
//    val stopwatchPrevDuration: Duration, // 클라이언트 : Duration <-> 서버 : Long
//    val timerStartTime: LocalTime, // 클라이언트 : LocalTime <-> 서버 : Timestamp
//    val timerNextSelectedTime: Duration, // 클라이언트 : Duration <-> 서버 : Long
) {
    companion object {
        fun default(): User {
            return User(
                email = "example@gmail.com",
                signedUpOn = LocalDate.now(),
                level = 1,
                levelDateMap = mapOf<String, LocalDate>("1" to LocalDate.now()),
                currentExp = 0,
                wiDTotalExp = 0,
//                wiDMinLimit = Duration.ofMinutes(1), // 1분
                wiDMinLimit = Duration.ofSeconds(1), // 1초 TODO: 수정
                wiDMaxLimit = Duration.ofHours(12), // 12시간
                city = City.SEOUL
            )
        }
    }
}

//FirebaseUser(
//    uid
//    displayName
//    isAnonymous
//    email
//    isEmailVerified
//    phoneNumber
//    photoUrl
//)