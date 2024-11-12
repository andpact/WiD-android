package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.*
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
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
    val title: State<Title> = wiDDataSource.title

    // 도구
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState
    val remainingTime: State<Duration> = wiDDataSource.remainingTime
    val selectedTime: State<Duration> = wiDDataSource.selectedTime
    private val _timerViewBarVisible = mutableStateOf(true)
    val timerViewBarVisible: State<Boolean> = _timerViewBarVisible

    fun setTitle(newTitle: Title) {
        Log.d(TAG, "setTitle executed")

        wiDDataSource.setTitle(newTitle)
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
            onTimerAutoStopped = { newWiD: WiD ->
                // 레벨
                val currentLevel = user.value?.level ?: 1
                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentRequiredExp = levelRequiredExpMap[currentLevel] ?: 0
                val newExp = newWiD.duration.seconds.toInt()
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp
                // 제목
                val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
                val currentTitleCount = titleCountMap[title.value] ?: 0
                titleCountMap[title.value] = currentTitleCount + 1
                val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentTitleDuration = titleDurationMap[title.value] ?: Duration.ZERO
                titleDurationMap[title.value] = currentTitleDuration.plus(newWiD.duration)
                // 도구
                val createdBy = CurrentTool.TIMER
                val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
                val currentToolCount = toolCountMap[createdBy] ?: 0
                toolCountMap[createdBy] = currentToolCount + 1
                val toolDurationMap = user.value?.wiDToolDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentToolDuration = toolDurationMap[createdBy] ?: Duration.ZERO
                toolDurationMap[createdBy] = currentToolDuration.plus(Duration.ofSeconds(newExp.toLong()))

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.autoStopTimerWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                        newToolCountMap = toolCountMap,
                        newToolDurationMap = toolDurationMap
                    )
                } else {
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.autoStopTimer(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                        newToolCountMap = toolCountMap,
                        newToolDurationMap = toolDurationMap
                    )
                }
            },
        )
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        wiDDataSource.pauseTimer(
            email = user.value?.email ?: "",
            onTimerPaused = { newWiD: WiD ->
                // 레벨
                val currentLevel = user.value?.level ?: 1

                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentRequiredExp = levelRequiredExpMap[currentLevel] ?: 0
                val newExp = newWiD.duration.seconds.toInt()
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                // 제목
                val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
                val currentTitleCount = titleCountMap[title.value] ?: 0
                titleCountMap[title.value] = currentTitleCount + 1
                val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentTitleDuration = titleDurationMap[title.value] ?: Duration.ZERO
                titleDurationMap[title.value] = currentTitleDuration.plus(newWiD.duration)

                // 도구
                val createdBy = CurrentTool.TIMER
                val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
                val currentToolCount = toolCountMap[createdBy] ?: 0
                toolCountMap[createdBy] = currentToolCount + 1
                val toolDurationMap = user.value?.wiDToolDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentToolDuration = toolDurationMap[createdBy] ?: Duration.ZERO
                toolDurationMap[createdBy] = currentToolDuration.plus(Duration.ofSeconds(newExp.toLong()))

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.pauseTimerWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                        newToolCountMap = toolCountMap,
                        newToolDurationMap = toolDurationMap
                    )
                } else {
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseTimer(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                        newToolCountMap = toolCountMap,
                        newToolDurationMap = toolDurationMap
                    )
                }
            },
        )
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        wiDDataSource.stopTimer()
    }
}