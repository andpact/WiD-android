package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "TimerViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val LEVEL = userDataSource.LEVEL
    val CURRENT_EXP = userDataSource.CURRENT_EXP
    val WID_TOTAL_EXP = userDataSource.WID_TOTAL_EXP

    private val CITY = userDataSource.CITY

    val WID_LIST_LIMIT_PER_DAY = wiDDataSource.WID_LIST_LIMIT_PER_DAY

    val user: State<User?> = userDataSource.user
    val now: State<LocalDateTime> = wiDDataSource.now

    val currentWiD: State<WiD> = wiDDataSource.currentWiD

    val playerState: State<PlayerState> = wiDDataSource.playerState
    val remainingTime: State<Duration> = wiDDataSource.remainingTime
    val selectedTime: State<Duration> = wiDDataSource.selectedTime
    private val _timerViewBarVisible = mutableStateOf(true)
    val timerViewBarVisible: State<Boolean> = _timerViewBarVisible

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }

    private fun updateWiDList(): List<WiD> {
        Log.d(TAG, "updateWiDList executed")

        val currentToday = now.value.toLocalDate()

        return wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentToday.year), emptyMap())
            .getOrDefault(currentToday, emptyList())
    }

//    fun setTitle(newTitle: Title) {
//        Log.d(TAG, "setTitle executed")
//
//        wiDDataSource.setCurrentWiDTitle(newTitle)
//    }

    fun setTimerTime(newSelectedTime: Duration) {
        Log.d(TAG, "setTimerTime executed")

        wiDDataSource.setTimerTime(newSelectedTime)
    }

    fun setTimerViewBarVisible(timerViewBarVisible: Boolean) {
        Log.d(TAG, "setTimerViewBarVisible executed")

        _timerViewBarVisible.value = timerViewBarVisible
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

    fun getDurationString(duration: Duration): String { // "H시간 m분 s초"
        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    fun getDurationTimeString(duration: Duration): String { // 'HH:mm:ss'
        Log.d(TAG, "getDurationTimeString executed")

        return wiDDataSource.getDurationTimeString(duration = duration)
    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
//        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(
            currentDateTime = now.value,
            dateTime = dateTime
        )
    }
}