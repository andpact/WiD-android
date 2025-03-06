package andpact.project.wid.dataSource

import andpact.project.wid.model.City
import andpact.project.wid.model.User
import andpact.project.wid.repository.Repository
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseUser
import java.time.Duration
import javax.inject.Inject

// Firebase User, User Document 외에는 정보를 모르도록
class UserDataSource @Inject constructor(private val repository: Repository) {
    private val TAG = "UserDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    val LEVEL = repository.LEVEL
    val CURRENT_EXP = repository.CURRENT_EXP
    val WID_TOTAL_EXP = repository.WID_TOTAL_EXP
    val WID_MIN_LIMIT = repository.WID_MIN_LIMIT
    val WID_MAX_LIMIT = repository.WID_MAX_LIMIT
    val CITY = repository.CITY

    // 최초 null 상태로 시작한 후, 동적 링크를 감지하거나, 기존 로그인 상태가 있음을 확인하면 할당해줌.
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    val firebaseUser: State<FirebaseUser?> = _firebaseUser
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    fun updateUser(updatedUserDocument: Map<String, Any>) {
        Log.d(TAG, "updateUser executed")

        val currentUser = _user.value ?: return

        _user.value = currentUser.copy(
            level = updatedUserDocument[LEVEL]?.let { it as Int } ?: currentUser.level,
            city = updatedUserDocument[CITY]?.let { City.valueOf(it as String) } ?: currentUser.city,
            currentExp = updatedUserDocument[CURRENT_EXP]?.let { it as Int } ?: currentUser.currentExp,
            wiDTotalExp = updatedUserDocument[WID_TOTAL_EXP]?.let { it as Int } ?: currentUser.wiDTotalExp,
            wiDMinLimit = updatedUserDocument[WID_MIN_LIMIT]?.let { Duration.ofSeconds((it as Long)) } ?: currentUser.wiDMinLimit,
            wiDMaxLimit = updatedUserDocument[WID_MAX_LIMIT]?.let { Duration.ofSeconds((it as Long)) } ?: currentUser.wiDMaxLimit
        )
    }

    fun getFirebaseUser(): Boolean {
        Log.d(TAG, "getFirebaseUser executed")

//        val firebaseUser = repository.getFirebaseUser()
//        _firebaseUser.value = firebaseUser
//        return firebaseUser != null

        /** 클라이언트 메모리 사용 */
        _firebaseUser.value = null
        return true
        /** 클라이언트 메모리 사용 */
    }

    fun getUserDocument() {
        Log.d(TAG, "getUserDocument executed")

//        repository.getUser(
//            email = _firebaseUser.value?.email ?: "",
//            onUserFetched = { user: User? ->
//                _user.value = user
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = User.default()
        /** 클라이언트 메모리 사용 */
    }

//    fun verifyAuthenticationLink(
//        email: String,
//        dynamicLink: String?,
//        onAuthenticationLinkVerified: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "verifyAuthenticationLink executed")
//
//        repository.verifyAuthenticationLink(
//            email = email,
//            dynamicLink = dynamicLink,
//            onAuthenticationLinkVerified = { authenticationLinkVerified: Boolean ->
//                onAuthenticationLinkVerified(authenticationLinkVerified)
//            }
//        )
//    }

//    fun sendAuthenticationLinkToEmail(
//        email: String,
//        onAuthenticationLinkSent: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "sendAuthenticationLinkToEmail executed")
//
//        userRepository.sendAuthenticationLinkToEmail(
//            email = email,
//            onAuthenticationLinkSentToEmail = { authenticationLinkSent: Boolean ->
//                onAuthenticationLinkSent(authenticationLinkSent)
//            }
//        )
//    }

    fun signOut() {
        Log.d(TAG, "signOut executed")

//        repository.signOut()
    }

    fun deleteUserAndDataDocument(
        email: String,
        onUserDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteUserAndDataDocument executed")

//        repository.deleteUserAndDataDocument(
//            email = email,
//            onResult = { success: Boolean ->
//                onUserDeleted(success)
//            }
//        )
    }

    fun updateUserDocument(
        email: String,
        updatedUserDocument: Map<String, Any>,
    ) {
        Log.d(TAG, "updateUserDocument executed")

        val currentUser = _user.value ?: return

//        repository.updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onResult = { success: Boolean ->
//                if (success) {
//                    _user.value = currentUser.copy( // 키가 있으면 밸류 갱신, 없으면 그대로
//                        level = updatedUserDocument[LEVEL]?.let { (it as Int) } ?: currentUser.level, // 맵 안에서 Int 타입
//                        city = updatedUserDocument[CITY]?.let { City.valueOf(it as String) } ?: currentUser.city, // 맵 안에서 String 타입
//                        currentExp = updatedUserDocument[CURRENT_EXP]?.let { (it as Int) } ?: currentUser.currentExp, // 맵 안에서 Int 타입
//                        wiDTotalExp = updatedUserDocument[WID_TOTAL_EXP]?.let { (it as Int) } ?: currentUser.wiDTotalExp, // 맵 안에서 Int 타입
//                        wiDMinLimit = updatedUserDocument[WID_MIN_LIMIT]?.let { Duration.ofSeconds(it as Long) } ?: currentUser.wiDMinLimit,  // 맵 안에서 Long 타입
//                        wiDMaxLimit = updatedUserDocument[WID_MAX_LIMIT]?.let { Duration.ofSeconds(it as Long) } ?: currentUser.wiDMaxLimit // 맵 안에서 Long 타입
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = currentUser.copy(
            level = updatedUserDocument[LEVEL]?.let { it as Int } ?: currentUser.level,
            city = updatedUserDocument[CITY]?.let { City.valueOf(it as String) } ?: currentUser.city,
            currentExp = updatedUserDocument[CURRENT_EXP]?.let { it as Int } ?: currentUser.currentExp,
            wiDTotalExp = updatedUserDocument[WID_TOTAL_EXP]?.let { it as Int } ?: currentUser.wiDTotalExp,
            wiDMinLimit = updatedUserDocument[WID_MIN_LIMIT]?.let { Duration.ofSeconds(it as Long) } ?: currentUser.wiDMinLimit,
            wiDMaxLimit = updatedUserDocument[WID_MAX_LIMIT]?.let { Duration.ofSeconds(it as Long) } ?: currentUser.wiDMaxLimit
        )
        /** 클라이언트 메모리 사용 */
    }

//    fun updateDisplayName(newDisplayName: String) { // 파이어 베이스 유저의 닉네임
//
//    }

    val levelRequiredExpMap: Map<Int, Int> = (1..99).associateWith { level ->
        86_000 * level
    }

//    val levelToTotalExpMap: Map<Int, Int> = (1..99).fold(mutableMapOf()) { acc, level ->
//        val requiredExp = 86_000 * level
//        val totalExp = (acc[level - 1] ?: 0) + requiredExp // 이전 레벨의 총 경험치와 현재 레벨의 필요 경험치 합산
//        acc[level] = totalExp
//        acc
//    }
}