package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject

@HiltViewModel // <- 생성자 주입 할 때 명시해야함.
class MainBottomBarViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "MainBottomBarViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val LEVEL = userDataSource.LEVEL
    val CURRENT_EXP = userDataSource.CURRENT_EXP
    val WID_TOTAL_EXP = userDataSource.WID_TOTAL_EXP

    private val CITY = userDataSource.CITY

    val user: State<User?> = userDataSource.user

    val currentWiD: State<WiD> = wiDDataSource.currentWiD

    val playerState: State<PlayerState> = wiDDataSource.playerState

    val totalDuration: State<Duration> = wiDDataSource.totalDuration
    val selectedTime: State<Duration> = wiDDataSource.selectedTime
    val remainingTime: State<Duration> = wiDDataSource.remainingTime

    val destinationList = listOf(
        MainViewDestinations.HomeViewDestination,
        MainViewDestinations.WiDListViewDestination,
        MainViewDestinations.MyPageViewDestination
    )

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        val currentUser = user.value ?: return

        wiDDataSource.startStopwatch(
            wiDMaxLimit = currentUser.wiDMaxLimit,
            onStopwatchAutoPaused = { stopwatchAutoPaused: Boolean ->
                if (stopwatchAutoPaused) {
                    pauseStopwatch()
                    stopStopwatch()
                }
            }
        )
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        val currentUser = user.value ?: return
        val wiDMinLimit = currentUser.wiDMinLimit
        val currentWiD = currentWiD.value
        if (currentWiD.duration < wiDMinLimit) return // 최소 시간 제한

        val newExp = currentWiD.exp // 플레이어는 무조건
        val currentLevel = currentUser.level
        val currentExp = currentUser.currentExp
        val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
        val wiDTotalExp = currentUser.wiDTotalExp
        val newWiDTotalExp = wiDTotalExp + newExp

        val updatedUserDocument = mutableMapOf<String, Any>()

        if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
            // 레벨 업데이트
            val newLevel = currentLevel + 1
            updatedUserDocument[LEVEL] = newLevel

            // 경험치 갱신
            val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
            updatedUserDocument[CURRENT_EXP] = newCurrentExp
        } else {
            // 레벨 업이 아닌 경우 현재 경험치만 갱신
            updatedUserDocument[CURRENT_EXP] = currentExp + newExp
        }

        updatedUserDocument[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
        updatedUserDocument[CITY] = currentUser.city.name // 도시 할당

        wiDDataSource.pauseStopwatch(
            email = currentUser.email,
            currentWiD = currentWiD,
            updatedUserDocument = updatedUserDocument,
            onResult = { success ->
                if (success) {
                    userDataSource.updateUser(updatedUserDocument = updatedUserDocument)
                }
            }
        )
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        wiDDataSource.stopStopwatch()
    }

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.startTimer(
            email = currentUser.email,
            onTimerAutoStopped = { timerAutoStopped: Boolean ->
                if (timerAutoStopped) {
                    pauseTimer()
                    stopTimer()
                }
            }
        )
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        val currentUser = user.value ?: return // 잘못된 접근
        val wiDMinLimit = currentUser.wiDMinLimit
        val currentWiD = currentWiD.value
        if (currentWiD.duration < wiDMinLimit) return // 최소 시간 제한

        val newExp = currentWiD.exp // 플레이어는 무조건
        val currentLevel = currentUser.level
        val currentExp = currentUser.currentExp
        val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
        val wiDTotalExp = currentUser.wiDTotalExp
        val newWiDTotalExp = wiDTotalExp + newExp

        val updatedUserDocument = mutableMapOf<String, Any>()

        if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
            // 레벨 업데이트
            val newLevel = currentLevel + 1
            updatedUserDocument[LEVEL] = newLevel

            // 경험치 갱신
            val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
            updatedUserDocument[CURRENT_EXP] = newCurrentExp
        } else {
            // 레벨 업이 아닌 경우 현재 경험치만 갱신
            updatedUserDocument[CURRENT_EXP] = currentExp + newExp
        }

        updatedUserDocument[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
        updatedUserDocument[CITY] = currentUser.city.name // 도시 할당

        wiDDataSource.pauseTimer(
            email = currentUser.email,
            currentWiD = currentWiD,
            updatedUserDocument = updatedUserDocument,
            onResult = { success: Boolean ->
                if (success) {
                    userDataSource.updateUser(updatedUserDocument = updatedUserDocument)
                }
            }
        )
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        wiDDataSource.stopTimer()
    }

    fun getDurationTimeString(duration: Duration): String {
//        Log.d(TAG, "getDurationTimeString executed")

        return wiDDataSource.getDurationTimeString(duration = duration)
    }
}