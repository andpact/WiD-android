package andpact.project.wid.dataSource

import andpact.project.wid.model.User
import andpact.project.wid.repository.UserRepository
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate
import javax.inject.Inject

// Firebase User, User Document 외에는 정보를 모르도록
class UserDataSource @Inject constructor(private val userRepository: UserRepository) {
    private val TAG = "UserDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    // 최초 null 상태로 시작한 후, 동적 링크를 감지하거나, 기존 로그인 상태가 있음을 확인하면 할당해줌.
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    val firebaseUser: State<FirebaseUser?> = _firebaseUser
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    fun hasFirebaseUser(): Boolean {
        Log.d(TAG, "hasFirebaseUser executed")

//        return userRepository.getFirebaseUser() != null

        /** 클라이언트 메모리 사용 */
        return true
        /** 클라이언트 메모리 사용 */
    }

    fun getFirebaseUserAndUser() {
        Log.d(TAG, "getFirebaseUserAndUser executed")

//        _firebaseUser.value = userRepository.getFirebaseUser()

//        userRepository.getUser(
//            email = _firebaseUser.value?.email ?: "",
//            onUserFetched = { user: User? ->
//                _user.value = user
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _firebaseUser.value = null
        _user.value = User.default()
        /** 클라이언트 메모리 사용 */

        Log.d(TAG, "firebaseUser : $firebaseUser")
        Log.d(TAG, "user : $user")
    }

    fun verifyAuthenticationLink(
        email: String,
        dynamicLink: String?,
        onAuthenticationLinkVerified: (Boolean) -> Unit
    ) {
        Log.d(TAG, "verifyAuthenticationLink executed")

        userRepository.verifyAuthenticationLink(
            email = email,
            dynamicLink = dynamicLink,
            onAuthenticationLinkVerified = { authenticationLinkVerified: Boolean ->
                onAuthenticationLinkVerified(authenticationLinkVerified)
            }
        )
    }

    fun sendAuthenticationLinkToEmail(
        email: String,
        onAuthenticationLinkSent: (Boolean) -> Unit
    ) {
        Log.d(TAG, "sendAuthenticationLinkToEmail executed")

        userRepository.sendAuthenticationLinkToEmail(
            email = email,
            onAuthenticationLinkSentToEmail = { authenticationLinkSent: Boolean ->
                onAuthenticationLinkSent(authenticationLinkSent)
            }
        )
    }

    fun signOut() {
        Log.d(TAG, "signOut executed")

        userRepository.signOut()
    }

    fun deleteUser(
        email: String,
        onUserDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteUser executed")

        userRepository.deleteUser(
            email = email,
            onUserDeleted = { userDeleted: Boolean ->
                onUserDeleted(userDeleted)
            }
        )
    }

    fun pauseStopwatch(
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "pauseStopwatch executed")

//        userRepository.pauseStopwatch(
//            email = _firebaseUser.value?.email ?: "",
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onStopwatchPaused = { stopwatchPaused: Boolean ->
//                if (stopwatchPaused) {
//                    _user.value = _user.value?.copy(
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

    fun pauseStopwatchWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "pauseStopwatchWithLevelUp executed")

//        userRepository.pauseStopwatchWithLevelUp(
//            email = _firebaseUser.value?.email ?: "",
//            newLevel = newLevel, // 레벨 업 시 추가 갱신 됨
//            newLevelUpHistoryMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onStopwatchPausedWithLevelUp = { stopwatchPausedWithLevelUp: Boolean ->
//                if (stopwatchPausedWithLevelUp) {
//                    _user.value = _user.value?.copy(
//                        level = newLevel, // 레벨 업 시 추가 갱신 됨
//                        levelDateMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            level = newLevel, // 레벨 업 시 추가 갱신 됨
            levelDateMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

    fun pauseTimer(
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "pauseTimer executed")

//        userRepository.pauseTimer(
//            email = _firebaseUser.value?.email ?: "",
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onTimerPaused = { timerPaused: Boolean ->
//                if (timerPaused) {
//                    _user.value = _user.value?.copy(
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

    fun pauseTimerWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "pauseTimerWithLevelUp executed")

//        userRepository.pauseTimerWithLevelUp(
//            email = _firebaseUser.value?.email ?: "",
//            newLevel = newLevel,
//            newLevelUpHistoryMap = newLevelUpHistoryMap,
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onTimerPausedWithLevelUp = { timerPausedWithLevelUp: Boolean ->
//                if (timerPausedWithLevelUp) {
//                    _user.value = _user.value?.copy(
//                        level = newLevel,
//                        levelDateMap = newLevelUpHistoryMap,
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            level = newLevel,
            levelDateMap = newLevelUpHistoryMap,
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

    fun autoStopTimer(
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "autoStopTimer executed")

//        userRepository.autoStopTimer(
//            email = _firebaseUser.value?.email ?: "",
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onTimerAutoStopped = { timerAutoStopped: Boolean ->
//                if (timerAutoStopped) {
//                    _user.value = _user.value?.copy(
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

    fun autoStopTimerWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "autoStopTimerWithLevelUp executed")

//        userRepository.autoStopTimerWithLevelUp(
//            email = _firebaseUser.value?.email ?: "",
//            newLevel = newLevel,
//            newLevelUpHistoryMap = newLevelUpHistoryMap,
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onTimerAutoStoppedWithLevelUp = { timerAutoStoppedWithLevelUp: Boolean ->
//                if (timerAutoStoppedWithLevelUp) {
//                    _user.value = _user.value?.copy(
//                        level = newLevel,
//                        levelDateMap = newLevelUpHistoryMap,
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            level = newLevel,
            levelDateMap = newLevelUpHistoryMap,
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

//    fun createdWiD(
//        newCurrentExp: Int,
//        newWiDTotalExp: Int
//    ) {
//        Log.d(TAG, "createdWiD executed")
//
//        userRepository.createWiD(
//            email = _firebaseUser.value?.email ?: "",
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onCreatedWiD = { createdWiD: Boolean ->
//                if (createdWiD) {
//                    _user.value = _user.value?.copy(
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )
//    }
//
//    fun createdWiDWithLevelUp(
//        newLevel: Int,
//        newLevelUpHistoryMap: Map<String, LocalDate>,
//        newCurrentExp: Int,
//        newWiDTotalExp: Int
//    ) {
//        Log.d(TAG, "createdWiDWithLevelUp executed")
//
//        userRepository.createdWiDWithLevelUp(
//            email = _firebaseUser.value?.email ?: "",
//            newLevel = newLevel,
//            newLevelUpHistoryMap = newLevelUpHistoryMap,
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onCreatedWiDWithLevelUp = { createdWiDWithLevelUp: Boolean ->
//                if (createdWiDWithLevelUp) {
//                    _user.value = _user.value?.copy(
//                        level = newLevel,
//                        levelDateMap = newLevelUpHistoryMap,
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )
//    }
//
//    fun updateWiD(
//        newCurrentExp: Int,
//        newWiDTotalExp: Int
//    ) {
//        Log.d(TAG, "updateWiD executed")
//
//        userRepository.updateWiD(
//            email = _firebaseUser.value?.email ?: "",
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onWiDUpdated = { wiDUpdated: Boolean ->
//                if (wiDUpdated) {
//                    _user.value = _user.value?.copy(
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )
//    }
//
//    fun updateWiDWithLevelUp(
//        newLevel: Int,
//        newLevelUpHistoryMap: Map<String, LocalDate>,
//        newCurrentExp: Int,
//        newWiDTotalExp: Int
//    ) {
//        Log.d(TAG, "updateWiDWithLevelUp executed")
//
//        userRepository.updateWiDWithLevelUp(
//            email = _firebaseUser.value?.email ?: "",
//            newLevel = newLevel,
//            newLevelUpHistoryMap = newLevelUpHistoryMap,
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onWiDUpdatedWithLevelUp = { wiDUpdatedWithLevelUp: Boolean ->
//                if (wiDUpdatedWithLevelUp) {
//                    _user.value = _user.value?.copy(
//                        level = newLevel,
//                        levelDateMap = newLevelUpHistoryMap,
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )
//    }

    /** 경험치가 있는 기록만 경험치를 제거 해야 함. */
    fun deleteWiD(
        newCurrentExp: Int,
        newWiDTotalExp: Int
    ) {
        Log.d(TAG, "deleteWiD executed")

//        userRepository.deleteWiD(
//            email = _firebaseUser.value?.email ?: "",
//            newCurrentExp = newCurrentExp,
//            newWiDTotalExp = newWiDTotalExp,
//            onWiDDeleted = { wiDDeleted: Boolean ->
//                if (wiDDeleted) {
//                    _user.value = _user.value?.copy(
//                        currentExp = newCurrentExp,
//                        wiDTotalExp = newWiDTotalExp
//                    )
//                }
//            }
//        )

        /** 클라이언트 메모리 사용 */
        _user.value = _user.value?.copy(
            currentExp = newCurrentExp,
            wiDTotalExp = newWiDTotalExp
        )
        /** 클라이언트 메모리 사용 */
    }

//    fun updateDisplayName(newDisplayName: String) {
//
//    }

    val levelRequiredExpMap: Map<Int, Int> = mapOf(
        1 to 86_400,
        2 to 172_800,
        3 to 259_200,
        4 to 345_600,
        5 to 432_000,
        6 to 518_400,
        7 to 604_800,
        8 to 691_200,
        9 to 777_600,
        10 to 864_000,
        11 to 950_400,   // 86,400 + 10 * 86,400
        12 to 1_036_800, // 86,400 + 11 * 86,400
        13 to 1_123_200, // 86,400 + 12 * 86,400
        14 to 1_209_600, // 86,400 + 13 * 86,400
        15 to 1_296_000, // 86,400 + 14 * 86,400
        16 to 1_382_400, // 86,400 + 15 * 86,400
        17 to 1_468_800, // 86,400 + 16 * 86,400
        18 to 1_555_200, // 86,400 + 17 * 86,400
        19 to 1_641_600, // 86,400 + 18 * 86,400
        20 to 1_728_000, // 86,400 + 19 * 86,400
        21 to 1_814_400, // 86,400 + 20 * 86,400
        22 to 1_900_800, // 86,400 + 21 * 86,400
        23 to 1_987_200, // 86,400 + 22 * 86,400
        24 to 2_073_600, // 86,400 + 23 * 86,400
        25 to 2_160_000, // 86,400 + 24 * 86,400
        26 to 2_246_400, // 86,400 + 25 * 86,400
        27 to 2_332_800, // 86,400 + 26 * 86,400
        28 to 2_419_200, // 86,400 + 27 * 86,400
        29 to 2_505_600, // 86,400 + 28 * 86,400
        30 to 2_592_000  // 86,400 + 29 * 86,400
    )

//val levelToTotalExpMap: Map<Int, Int> = mapOf(
//    1 to 86_400,
//    2 to 259_200,    // 86,400 + 172,800
//    3 to 518_400,    // 86,400 + 172,800 + 259,200
//    4 to 864_000,    // 86,400 + 172,800 + 259,200 + 345,600
//    5 to 1_296_000,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000
//    6 to 1_818_400,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400
//    7 to 2_433_600,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800
//    8 to 3_144_800,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800 + 691,200
//    9 to 3_955_200,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800 + 691,200 + 777,600
//    10 to 4_864_000, // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800 + 691,200 + 777,600 + 864,000
//    11 to 5_870_400, // 누적합 + 950,400
//    12 to 6_973_200, // 누적합 + 1,036,800
//    13 to 8_171_600, // 누적합 + 1,123,200
//    14 to 9_465_600, // 누적합 + 1,209,600
//    15 to 10_855_600, // 누적합 + 1,296,000
//    16 to 12_341_600, // 누적합 + 1,382,400
//    17 to 13_923_600, // 누적합 + 1,468,800
//    18 to 15_601_600, // 누적합 + 1,555,200
//    19 to 17_375_600, // 누적합 + 1,641,600
//    20 to 19_245_600, // 누적합 + 1,728,000
//    21 to 21_211_600, // 누적합 + 1,814,400
//    22 to 23_273_600, // 누적합 + 1,900,800
//    23 to 25_441_600, // 누적합 + 1,987,200
//    24 to 27_715_600, // 누적합 + 2,073,600
//    25 to 30_095_600, // 누적합 + 2,160,000
//    26 to 32_581_600, // 누적합 + 2,246,400
//    27 to 35_173_600, // 누적합 + 2,332,800
//    28 to 37_871_600, // 누적합 + 2,419,200
//    29 to 40_675_600, // 누적합 + 2,505,600
//    30 to 43_585_600  // 누적합 + 2,592,000
//)
}