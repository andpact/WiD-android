package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.ToolDataSource
import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import andpact.project.wid.util.levelToRequiredExpMap
import andpact.project.wid.util.titleNumberStringToTitleColorMap
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
    private val toolDataSource: ToolDataSource,
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "TimerViewModel"

    init { Log.d(TAG, "created") }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val user: State<User?> = userDataSource.user

    val title: State<String> = toolDataSource.title
    val titleColorMap: Map<String, Color> = titleNumberStringToTitleColorMap

    val remainingTime: State<Duration> = toolDataSource.remainingTime
    val selectedTime: State<Duration> = toolDataSource.selectedTime

    private val _timerViewBarVisible = mutableStateOf(true)
    val timerViewBarVisible: State<Boolean> = _timerViewBarVisible

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        toolDataSource.setTitle(newTitle)
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        toolDataSource.setSelectedTime(newSelectedTime)
    }

    fun setTimerViewBarVisible(timerViewBarVisible: Boolean) {
        Log.d(TAG, "setTimerViewBarVisible executed")

        _timerViewBarVisible.value = timerViewBarVisible
    }

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        toolDataSource.startTimer(
            email = user.value?.email ?: "",
            onTimerStarted = { newTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newTimerStartDate: LocalDate, newTimerStartTime: LocalTime ->
                userDataSource.startTimer(
                    newCurrentTitle = newTitle,
                    newCurrentTool = newCurrentTool,
                    newCurrentToolState = newCurrentToolState,
                    newTimerStartDate = newTimerStartDate,
                    newTimerStartTime = newTimerStartTime,
                )
            },
            onTimerAutoStopped = { currentTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newTimerCurrentDuration: Duration ->
                val currentLevel = user.value?.level ?: 1
                val currentExp = user.value?.currentExp ?: 0
                val newExp = newTimerCurrentDuration.seconds.toInt()
                val currentRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0

                val totalExp = user.value?.totalExp ?: 0
                val newTotalExp = totalExp + newExp
                val wiDTotalExp = user.value?.totalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                val titleCountMap = user.value?.titleCountMap?.toMutableMap() ?: mutableMapOf()
                val currentCount = titleCountMap[currentTitle] ?: 0
                titleCountMap[currentTitle] = currentCount + 1

                val titleDurationMap = user.value?.titleDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentDuration = titleDurationMap[currentTitle] ?: Duration.ZERO
                titleDurationMap[currentTitle] = currentDuration.plus(newTimerCurrentDuration)

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()

                    val today = LocalDate.now()
                    val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                    levelUpHistoryMap[newLevelAsString] = today

                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.autoStopTimerWithLevelUp(
                        newCurrentTool = newCurrentTool,
                        newCurrentToolState = newCurrentToolState,
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelUpHistoryMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newTotalExp = newTotalExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                    )
                } else {
                    val newCurrentExp = currentExp + newExp

                    userDataSource.autoStopTimerWithoutLevelUp(
                        newCurrentTool = newCurrentTool,
                        newCurrentToolState = newCurrentToolState,
                        newCurrentExp = newCurrentExp,
                        newTotalExp = newTotalExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                    )
                }
            },
            onWiDCreated = { createdWiD: WiD ->
                addWiDToMap(createdWiD = createdWiD)
            }
        )
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        toolDataSource.pauseTimer(
            email = user.value?.email ?: "",
            onTimerPaused = { currentTitle: String, newCurrentToolState: CurrentToolState, newTimerCurrentDuration: Duration, newTimerNextSelectedTime: Duration ->
                val currentLevel = user.value?.level ?: 1
                val currentExp = user.value?.currentExp ?: 0
                val newExp = newTimerCurrentDuration.seconds.toInt()
                val currentRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0

                val totalExp = user.value?.totalExp ?: 0
                val newTotalExp = totalExp + newExp
                val wiDTotalExp = user.value?.totalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                val titleCountMap = user.value?.titleCountMap?.toMutableMap() ?: mutableMapOf()
                val currentCount = titleCountMap[currentTitle] ?: 0
                titleCountMap[currentTitle] = currentCount + 1

                val titleDurationMap = user.value?.titleDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentDuration = titleDurationMap[currentTitle] ?: Duration.ZERO
                titleDurationMap[currentTitle] = currentDuration.plus(newTimerCurrentDuration)

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()

                    val today = LocalDate.now()
                    val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                    levelUpHistoryMap[newLevelAsString] = today

                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.pauseTimerWithLevelUp(
                        newCurrentToolState = newCurrentToolState,
                        newTimerNextSelectedTime = newTimerNextSelectedTime,
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelUpHistoryMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newTotalExp = newTotalExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                    )
                } else {
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseTimer(
                        newCurrentToolState = newCurrentToolState,
                        newTimerNextSelectedTime = newTimerNextSelectedTime,
                        newCurrentExp = newCurrentExp,
                        newTotalExp = newTotalExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                    )
                }
            },
            onWiDCreated = { createdWiD: WiD ->
                addWiDToMap(createdWiD = createdWiD)
            }
        )
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        toolDataSource.stopTimer(
            onTimerStopped = { newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState ->
                userDataSource.stopTimer(
                    newCurrentTool = newCurrentTool,
                    newCurrentToolState = newCurrentToolState
                )
            }
        )
    }

    private fun addWiDToMap(createdWiD: WiD) {
        Log.d(TAG, "addWiDToMap executed")

        wiDDataSource.addWiDToMap(createdWiD = createdWiD)
    }
}