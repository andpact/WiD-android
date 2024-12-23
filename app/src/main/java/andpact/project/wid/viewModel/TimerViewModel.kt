package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.chivoMonoBlackItalic
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
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

    // 유저
    val user: State<User?> = userDataSource.user

    // 제목
    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD
    val secondCurrentWiD: State<WiD> = wiDDataSource.secondCurrentWiD

    // 도구
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState
    val remainingTime: State<Duration> = wiDDataSource.remainingTime
    val selectedTime: State<Duration> = wiDDataSource.selectedTime
    private val _timerViewBarVisible = mutableStateOf(true)
    val timerViewBarVisible: State<Boolean> = _timerViewBarVisible

    fun setTitle(newTitle: Title) {
        Log.d(TAG, "setTitle executed")

        wiDDataSource.setCurrentWiDTitle(newTitle)
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        wiDDataSource.setSelectedTime(newSelectedTime)
    }

    fun setTimerViewBarVisible(timerViewBarVisible: Boolean) {
        Log.d(TAG, "setTimerViewBarVisible executed")

        _timerViewBarVisible.value = timerViewBarVisible
    }

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        wiDDataSource.startTimer(
            email = user.value?.email ?: "",
            onTimerAutoStopped = { newExp: Int ->
                // 레벨
                val currentLevel = user.value?.level ?: 1
                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelDateMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.autoStopTimerWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp
                    )
                } else {
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.autoStopTimer(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp
                    )
                }
            },
        )
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        wiDDataSource.pauseTimer(
            email = user.value?.email ?: "",
            onTimerPaused = { newExp: Int ->
                // 레벨
                val currentLevel = user.value?.level ?: 1

                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelDateMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.pauseTimerWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp
                    )
                } else {
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseTimer(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp
                    )
                }
            }
        )
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        wiDDataSource.stopTimer()
    }


    fun getTimerDurationString(duration: Duration): AnnotatedString {
        Log.d(TAG, "getTimerDurationString executed")

        val hours = duration.toHours()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        val hoursText = hours.toString()
        val minutesText = minutes.toString().padStart(2, '0')
        val secondsText = seconds.toString().padStart(2, '0')

        return buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 60.sp, fontFamily = chivoMonoBlackItalic)) {
                append("$hoursText:$minutesText:$secondsText")
            }
        }
    }
}