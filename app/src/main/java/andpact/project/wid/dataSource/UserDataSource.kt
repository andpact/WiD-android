package andpact.project.wid.dataSource

import andpact.project.wid.model.User
import andpact.project.wid.repository.UserRepository
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseUser
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

// Firebase User, User Document 외에는 정보를 모르도록
class UserDataSource @Inject constructor(
    private val userRepository: UserRepository
) {
    private val TAG = "UserDataSource"
    init {
        Log.d(TAG, "created")
//        addSnapshotListenerToUserDocument()
    }
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

    /** 데이터 소스 단에서 이메일을 참조할 수 있으니, 뷰 모델 단에서 실행할 필요가 없음. */
//    private fun addSnapshotListenerToUserDocument() {
//        Log.d(TAG, "addSnapshotListenerToUserDocument executed")
//
//        userRepository.addSnapshotListenerToUserDocument(
//            email = _firebaseUser.value?.email ?: "",
//            onUserDocumentChanged = { user: User? ->
//                _user.value = user
//            }
//        )
//    }

    fun startStopwatch(
        newCurrentTitle: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newStopwatchStartDate: LocalDate,
        newStopwatchStartTime: LocalTime
    ) {
        Log.d(TAG, "startStopwatch executed")

        userRepository.startStopwatch(
            email = _firebaseUser.value?.email ?: "",
            newCurrentTitle = newCurrentTitle,
            newCurrentTool = newCurrentTool,
            newCurrentToolState = newCurrentToolState,
            newStopwatchStartDate = newStopwatchStartDate,
            newStopwatchStartTime = newStopwatchStartTime,
            onStopwatchStarted = { stopwatchStarted: Boolean ->
                if (stopwatchStarted) { // 서버와 통신 성공 시에만 User 인스턴스 갱신 시킴
                    _user.value = _user.value?.copy(
                        currentTitle = newCurrentTitle,
                        currentTool = newCurrentTool,
                        currentToolState = newCurrentToolState,
                        stopwatchStartTime = newStopwatchStartTime
                    )
                }
            }
        )
    }

    fun pauseStopwatch(
        newCurrentToolState: CurrentToolState,
        newStopwatchPrevDuration: Duration,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        userRepository.pauseStopwatch(
            email = _firebaseUser.value?.email ?: "",
            newCurrentToolState = newCurrentToolState,
            newStopwatchPrevDuration = newStopwatchPrevDuration,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onStopwatchPaused = { stopwatchPaused: Boolean ->
                if (stopwatchPaused) {
                    _user.value = _user.value?.copy(
                        currentToolState = newCurrentToolState,
                        stopwatchPrevDuration = newStopwatchPrevDuration,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun pauseStopwatchWithLevelUp(
        newCurrentToolState: CurrentToolState,
        newStopwatchPrevDuration: Duration,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "pauseStopwatchWithLevelUp executed")

        userRepository.pauseStopwatchWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newCurrentToolState = newCurrentToolState,
            newStopwatchPrevDuration = newStopwatchPrevDuration,
            newLevel = newLevel, // 레벨 업 시 추가 갱신 됨
            newLevelUpHistoryMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap =newTitleDurationMap,
            onStopwatchPausedWithLevelUp = { stopwatchPausedWithLevelUp: Boolean ->
                if (stopwatchPausedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        currentToolState = newCurrentToolState,
                        stopwatchPrevDuration = newStopwatchPrevDuration,
                        level = newLevel, // 레벨 업 시 추가 갱신 됨
                        levelUpHistoryMap = newLevelUpHistoryMap, // 레벨 업 시 추가 갱신 됨
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun stopStopwatch(
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState
    ) {
        Log.d(TAG, "stopStopwatch executed")

        userRepository.stopStopwatch(
            email = _firebaseUser.value?.email ?: "",
            newCurrentTool = newCurrentTool,
            newCurrentToolState = newCurrentToolState,
            onStopwatchStopped = { stopwatchStopped: Boolean ->
                if (stopwatchStopped) {
                    _user.value = _user.value?.copy(
                        currentTool = newCurrentTool,
                        currentToolState = newCurrentToolState,
                    )
                }
            }
        )
    }

    fun startTimer(
        newCurrentTitle: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newTimerStartDate: LocalDate,
        newTimerStartTime: LocalTime
    ) {
        Log.d(TAG, "startTimer executed")

        userRepository.startTimer(
            email = _firebaseUser.value?.email ?: "",
            newCurrentTitle = newCurrentTitle,
            newCurrentTool = newCurrentTool,
            newCurrentToolState = newCurrentToolState,
            newTimerStartDate = newTimerStartDate,
            newTimerStartTime = newTimerStartTime,
            onTimerStarted = { timerStarted: Boolean ->
                if (timerStarted) { // 서버와 통신 성공 시에만 User 인스턴스 갱신 시킴
                    _user.value = _user.value?.copy(
                        currentTitle = newCurrentTitle,
                        currentTool = newCurrentTool,
                        currentToolState = newCurrentToolState,
                        timerStartTime = newTimerStartTime
                    )
                }
            }
        )
    }

    fun pauseTimer(
        newCurrentToolState: CurrentToolState,
        newTimerNextSelectedTime: Duration,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "pauseTimer executed")

        userRepository.pauseTimer(
            email = _firebaseUser.value?.email ?: "",
            newCurrentToolState = newCurrentToolState,
            newTimerNextSelectedTime = newTimerNextSelectedTime,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onTimerPaused = { timerPaused: Boolean ->
                if (timerPaused) {
                    _user.value = _user.value?.copy(
                        currentToolState = newCurrentToolState,
                        timerNextSelectedTime = newTimerNextSelectedTime,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun pauseTimerWithLevelUp(
        newCurrentToolState: CurrentToolState,
        newTimerNextSelectedTime: Duration,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "pauseTimerWithLevelUp executed")

        userRepository.pauseTimerWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newCurrentToolState = newCurrentToolState,
            newTimerNextSelectedTime = newTimerNextSelectedTime,
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onTimerPausedWithLevelUp = { timerPausedWithLevelUp: Boolean ->
                if (timerPausedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        currentToolState = newCurrentToolState,
                        timerNextSelectedTime = newTimerNextSelectedTime,
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun stopTimer(
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState
    ) {
        Log.d(TAG, "stopTimer executed")

        userRepository.stopTimer(
            email = _firebaseUser.value?.email ?: "",
            newCurrentTool = newCurrentTool,
            newCurrentToolState = newCurrentToolState,
            onTimerStopped = { timerStopped: Boolean ->
                if (timerStopped) {
                    _user.value = _user.value?.copy(
                        currentTool = newCurrentTool,
                        currentToolState = newCurrentToolState,
                    )
                }
            }
        )
    }

    fun autoStopTimerWithoutLevelUp(
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "autoStopTimerWithoutLevelUp executed")

        userRepository.autoStopTimer(
            email = _firebaseUser.value?.email ?: "",
            newCurrentTool = newCurrentTool,
            newCurrentToolState = newCurrentToolState,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onTimerAutoStopped = { timerAutoStopped: Boolean ->
                if (timerAutoStopped) {
                    _user.value = _user.value?.copy(
                        currentTool = newCurrentTool,
                        currentToolState = newCurrentToolState,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun autoStopTimerWithLevelUp(
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "autoStopTimerWithLevelUp executed")

        userRepository.autoStopTimerWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newCurrentTool = newCurrentTool,
            newCurrentToolState = newCurrentToolState,
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onTimerAutoStoppedWithLevelUp = { timerAutoStoppedWithLevelUp: Boolean ->
                if (timerAutoStoppedWithLevelUp) {
                    _user.value = _user.value?.copy(
                        currentTool = newCurrentTool,
                        currentToolState = newCurrentToolState,
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun createdWiD(
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
    ) {
        Log.d(TAG, "createdWiD executed")

        userRepository.createWiD(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleDurationMap = newTitleDurationMap,
            onCreatedWiD = { createdWiD: Boolean ->
                if (createdWiD) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun createdWiDWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
    ) {
        Log.d(TAG, "createdWiDWithLevelUp executed")

        userRepository.createdWiDWithLevelUp(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleDurationMap = newTitleDurationMap,
            onCreatedWiDWithLevelUp = { createdWiDWithLevelUp: Boolean ->
                if (createdWiDWithLevelUp) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun updateWiD(
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>
    ) {
        Log.d(TAG, "updateWiD executed")

        userRepository.updateWiD(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleDurationMap = newTitleDurationMap,
            onWiDUpdated = { wiDUpdated: Boolean ->
                if (wiDUpdated) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun updateWiDWithLevelDown(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
    ) {
        Log.d(TAG, "updateWiDWithLevelDown executed")

        userRepository.updateWiDWithLevelDown(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleDurationMap = newTitleDurationMap,
            onWiDUpdatedWithLevelDown = { wiDUpdatedWithLevelDown: Boolean ->
                if (wiDUpdatedWithLevelDown) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun updateWiDWithLevelUp(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
    ) {
        Log.d(TAG, "updateWiDWithLevelUp executed")

        userRepository.updateWiDWithLevelDown(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleDurationMap = newTitleDurationMap,
            onWiDUpdatedWithLevelDown = { wiDUpdatedWithLevelDown: Boolean ->
                if (wiDUpdatedWithLevelDown) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun deleteWiD(
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
    ) {
        Log.d(TAG, "deleteWiD executed")

        userRepository.deleteWiD(
            email = _firebaseUser.value?.email ?: "",
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onWiDDeleted = { wiDDeleted: Boolean ->
                if (wiDDeleted) {
                    _user.value = _user.value?.copy(
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
                    )
                }
            }
        )
    }

    fun deleteWiDWithLevelDown(
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
    ) {
        Log.d(TAG, "deleteWiDWithLevelDown executed")

        userRepository.deleteWiDWithLevelDown(
            email = _firebaseUser.value?.email ?: "",
            newLevel = newLevel,
            newLevelUpHistoryMap = newLevelUpHistoryMap,
            newCurrentExp = newCurrentExp,
            newTotalExp = newTotalExp,
            newWiDTotalExp = newWiDTotalExp,
            newTitleCountMap = newTitleCountMap,
            newTitleDurationMap = newTitleDurationMap,
            onWiDDeletedWithLevelDown = { wiDDeletedWithLevelDown: Boolean ->
                if (wiDDeletedWithLevelDown) {
                    _user.value = _user.value?.copy(
                        level = newLevel,
                        levelUpHistoryMap = newLevelUpHistoryMap,
                        currentExp = newCurrentExp,
                        totalExp = newTotalExp,
                        wiDTotalExp = newWiDTotalExp,
                        titleCountMap = newTitleCountMap,
                        titleDurationMap = newTitleDurationMap
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