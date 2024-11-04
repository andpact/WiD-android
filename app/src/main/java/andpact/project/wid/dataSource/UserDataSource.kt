package andpact.project.wid.dataSource

import andpact.project.wid.model.User
import andpact.project.wid.repository.UserRepository
import andpact.project.wid.util.CurrentTool
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseUser
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

// Firebase User, User Document 외에는 정보를 모르도록
class UserDataSource @Inject constructor(
    private val userRepository: UserRepository
) {
    private val TAG = "UserDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    // 최초 null 상태로 시작한 후, 동적 링크를 감지하거나, 기존 로그인 상태가 있음을 확인하면 할당해줌.
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    val firebaseUser: State<FirebaseUser?> = _firebaseUser
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    fun setFirebaseUserAndUser() {
        Log.d(TAG, "setFirebaseUserAndUser executed")

        _firebaseUser.value = userRepository.getFirebaseUser()

        userRepository.getUser(
            email = _firebaseUser.value?.email ?: "",
            onUserFetched = { user: User? ->
                _user.value = user
            }
        )

        Log.d(TAG, "firebaseUser : $firebaseUser")
        Log.d(TAG, "user : $user")
    }

    fun hasFirebaseUser(): Boolean {
        Log.d(TAG, "hasFirebaseUser executed")

        return userRepository.getFirebaseUser() != null
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
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        userRepository.pauseStopwatch(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onStopwatchPaused = { stopwatchPaused: Boolean ->
                if (stopwatchPaused) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun pauseStopwatchWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "pauseStopwatchWithLevelUp executed")

        userRepository.pauseStopwatchWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel, // 레벨 업 시 추가 갱신 됨
            newLevelUpHistoryMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onStopwatchPausedWithLevelUp = { stopwatchPausedWithLevelUp: Boolean ->
                if (stopwatchPausedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        level = newLevel, // 레벨 업 시 추가 갱신 됨
                        levelUpHistoryMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun pauseTimer(
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "pauseTimer executed")

        userRepository.pauseTimer(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onTimerPaused = { timerPaused: Boolean ->
                if (timerPaused) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun pauseTimerWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "pauseTimerWithLevelUp executed")

        userRepository.pauseTimerWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onTimerPausedWithLevelUp = { timerPausedWithLevelUp: Boolean ->
                if (timerPausedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun autoStopTimer(
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "autoStopTimer executed")

        userRepository.autoStopTimer(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onTimerAutoStopped = { timerAutoStopped: Boolean ->
                if (timerAutoStopped) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun autoStopTimerWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "autoStopTimerWithLevelUp executed")

        userRepository.autoStopTimerWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onTimerAutoStoppedWithLevelUp = { timerAutoStoppedWithLevelUp: Boolean ->
                if (timerAutoStoppedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun createdWiD(
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "createdWiD executed")

        userRepository.createWiD(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onCreatedWiD = { createdWiD: Boolean ->
                if (createdWiD) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun createdWiDWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "createdWiDWithLevelUp executed")

        userRepository.createdWiDWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onCreatedWiDWithLevelUp = { createdWiDWithLevelUp: Boolean ->
                if (createdWiDWithLevelUp) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun updateWiD(
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "updateWiD executed")

        userRepository.updateWiD(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolDurationMap = newToolDurationMap,
            onWiDUpdated = { wiDUpdated: Boolean ->
                if (wiDUpdated) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun updateWiDWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "updateWiDWithLevelUp executed")

        userRepository.updateWiDWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolDurationMap = newToolDurationMap,
            onWiDUpdatedWithLevelUp = { wiDUpdatedWithLevelUp: Boolean ->
                if (wiDUpdatedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

    fun deleteWiD(
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>
    ) {
        Log.d(TAG, "deleteWiD executed")

        userRepository.deleteWiD(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            newToolCountMap = newToolCountMap,
            newToolDurationMap = newToolDurationMap,
            onWiDDeleted = { wiDDeleted: Boolean ->
                if (wiDDeleted) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        wiDTotalExp = newWiDTotalExp,
                        wiDTitleCountMap = newTitleCountMap,
                        wiDTitleDurationMap = newTitleDurationMap,
                        wiDToolCountMap = newToolCountMap,
                        wiDToolDurationMap = newToolDurationMap
                    )
                }
            }
        )
    }

//    fun updateDisplayName(newDisplayName: String) {
//
//    }

//    fun updateStatusMessage(email: String, newStatusMessage: String) {
//        _user.value = _user.value?.copy(statusMessage = newStatusMessage)
//
//        userRepository.updateStatusMessage(email = email, newStatusMessage = newStatusMessage)
//    }
}