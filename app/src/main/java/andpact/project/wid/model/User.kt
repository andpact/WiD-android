package andpact.project.wid.model

import andpact.project.wid.util.PlayerState
import androidx.compose.ui.graphics.Color

data class User (
    val email: String, // email이 Firebase User와 UserCollection의 User을 연결하는 필드임.
//    val plan: String = "STANDARD", // 요금제 Boolean or Enum(STANDARD(FREE), PRO) 만들어서 사용하자.
//    val state: PlayerState, // 스톱 워치나 타이머 동작 중인지?
    val statusMessage: String, // 상태 메시지
    val titleColorMap: Map<String, Color>, // 제목, 색상
//    val titleMap: Map<String, List<Any>>, // 제목, 색상
//    val signInMethod: List<String>, // 언제 필요함?
//    val setting: Map<String, String>,
)

// 스톱 워치
// state를 어떻게 참조할 것인지?


//FirebaseUser(
//    uid
//    displayName
//    isAnonymous
//    email
//    isEmailVerified
//    phoneNumber
//    photoUrl
//)