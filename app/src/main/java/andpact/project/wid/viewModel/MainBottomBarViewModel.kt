package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import andpact.project.wid.util.levelToRequiredExpMap
import andpact.project.wid.util.titleToColorMap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel // <- 생성자 주입 할 때 명시해야함.
class MainBottomBarViewModel @Inject constructor(
//    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "MainBottomBarViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

//    val user: State<User?> = userDataSource.user
//
//    val currentTool: State<CurrentTool> = toolDataSource.currentTool
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    val title: State<String> = wiDDataSource.title
    val titleColorMap: Map<String, Color> = titleToColorMap

//    val totalDuration: State<Duration> = toolDataSource.totalDuration
//    val remainingTime: State<Duration> = toolDataSource.remainingTime
//
    val destinationList = listOf(
        MainViewDestinations.HomeViewDestination,
        MainViewDestinations.WiDToolViewDestination,
        MainViewDestinations.WiDDisplayViewDestination,
    )
//
//    fun startStopwatch() {
//        Log.d(TAG, "startStopwatch executed")
//
//        toolDataSource.startStopwatch(
////            onStopwatchStarted = { newCurrentTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newStopwatchStartDate: LocalDate, newStopwatchStartTime: LocalTime ->
////                userDataSource.startStopwatch(
////                    newCurrentTitle = newCurrentTitle,
////                    newCurrentTool = newCurrentTool,
////                    newCurrentToolState = newCurrentToolState,
////                    newStopwatchStartDate = newStopwatchStartDate,
////                    newStopwatchStartTime = newStopwatchStartTime
////                )
////            }
//        )
//    }
//
//    fun pauseStopwatch() {
//        Log.d(TAG, "pauseStopwatch executed")
//
//        toolDataSource.pauseStopwatch(
//            email = user.value?.email ?: "",
////            onStopwatchPaused = { currentTitle: String, newCurrentToolState: CurrentToolState, newStopwatchAccumulatedPrevDuration: Duration, newStopwatchCurrentDuration: Duration ->
////            onStopwatchPaused = { newStopwatchCurrentDuration: Duration ->
//            onStopwatchPaused = { newWiD: WiD ->
//                val currentLevel = user.value?.level ?: 1
//                val currentExp = user.value?.currentExp ?: 0
//                val newExp = newWiD.duration.seconds.toInt()
//                val currentRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0
//
//                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
//                val newWiDTotalExp = wiDTotalExp + newExp
//
//                val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
//                val currentCount = titleCountMap[title.value] ?: 0
//                titleCountMap[title.value] = currentCount + 1
//
//                val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
//                val currentDuration = titleDurationMap[title.value] ?: Duration.ZERO
//                titleDurationMap[title.value] = currentDuration.plus(newWiD.duration)
//
//                val createdBy = CurrentTool.STOPWATCH
//                val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
//                val currentToolCount = toolCountMap[createdBy] ?: 0
//                toolCountMap[createdBy] = currentToolCount + 1
//
//                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
//                    val newLevel = currentLevel + 1
//                    val newLevelAsString = newLevel.toString()
//
//                    val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
//                    val today = LocalDate.now()
//                    levelUpHistoryMap[newLevelAsString] = today
//
//                    val newCurrentExp = currentExp + newExp - currentRequiredExp
//
//                    userDataSource.pauseStopwatchWithLevelUp(
////                        newCurrentToolState = newCurrentToolState,
////                        newStopwatchPrevDuration = newStopwatchAccumulatedPrevDuration,
//                        newLevel = newLevel,
//                        newLevelUpHistoryMap = levelUpHistoryMap,
//                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
//                        newWiDTotalExp = newWiDTotalExp,
//                        newTitleCountMap = titleCountMap,
//                        newTitleDurationMap = titleDurationMap,
//                        newToolCountMap = toolCountMap
//                    )
//                } else { // 레벨업 아님.
//                    val newCurrentExp = currentExp + newExp
//
//                    userDataSource.pauseStopwatch(
////                        newCurrentToolState = newCurrentToolState,
////                        newStopwatchPrevDuration = newStopwatchAccumulatedPrevDuration,
//                        newCurrentExp = newCurrentExp,
//                        newWiDTotalExp = newWiDTotalExp,
//                        newTitleCountMap = titleCountMap,
//                        newTitleDurationMap = titleDurationMap,
//                        newToolCountMap = toolCountMap
//                    )
//                }
//
//                addWiDToMap(createdWiD = newWiD)
//            },
////            onWiDCreated = { createdWiD: WiD ->
////                addWiDToMap(createdWiD = createdWiD)
////            }
//        )
//    }
//
//    fun stopStopwatch() {
//        Log.d(TAG, "stopStopwatch executed")
//
//        toolDataSource.stopStopwatch(
////            onStopwatchStopped = { newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState ->
////                userDataSource.stopStopwatch(
////                    newCurrentTool = newCurrentTool,
////                    newCurrentToolState = newCurrentToolState
////                )
////            }
//        )
//    }
//
//    fun startTimer() {
//        Log.d(TAG, "startTimer executed")
//
//        toolDataSource.startTimer(
//            email = user.value?.email ?: "",
////            onTimerStarted = { newTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newTimerStartDate: LocalDate, newTimerStartTime: LocalTime ->
////                userDataSource.startTimer(
////                    newCurrentTitle = newTitle,
////                    newCurrentTool = newCurrentTool,
////                    newCurrentToolState = newCurrentToolState,
////                    newTimerStartDate = newTimerStartDate,
////                    newTimerStartTime = newTimerStartTime,
////                )
////            },
////            onTimerAutoStopped = { currentTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newTimerCurrentDuration: Duration ->
////            onTimerAutoStopped = { newTimerCurrentDuration: Duration ->
//            onTimerAutoStopped = { newWiD: WiD ->
//                val currentLevel = user.value?.level ?: 1
//                val currentExp = user.value?.currentExp ?: 0
//                val newExp = newWiD.duration.seconds.toInt()
//                val currentRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0
//
//                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
//                val newWiDTotalExp = wiDTotalExp + newExp
//
//                val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
//                val currentCount = titleCountMap[title.value] ?: 0
//                titleCountMap[title.value] = currentCount + 1
//
//                val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
//                val currentDuration = titleDurationMap[title.value] ?: Duration.ZERO
//                titleDurationMap[title.value] = currentDuration.plus(newWiD.duration)
//
//                val createdBy = CurrentTool.TIMER
//                val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
//                val currentToolCount = toolCountMap[createdBy] ?: 0
//                toolCountMap[createdBy] = currentToolCount + 1
//
//                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
//                    val newLevel = currentLevel + 1
//                    val newLevelAsString = newLevel.toString()
//
//                    val today = LocalDate.now()
//                    val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
//                    levelUpHistoryMap[newLevelAsString] = today
//
//                    val newCurrentExp = currentExp + newExp - currentRequiredExp
//
//                    userDataSource.autoStopTimerWithLevelUp(
////                        newCurrentTool = newCurrentTool,
////                        newCurrentToolState = newCurrentToolState,
//                        newLevel = newLevel,
//                        newLevelUpHistoryMap = levelUpHistoryMap,
//                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
//                        newWiDTotalExp = newWiDTotalExp,
//                        newTitleCountMap = titleCountMap,
//                        newTitleDurationMap = titleDurationMap,
//                        newToolCountMap = toolCountMap
//                    )
//                } else {
//                    val newCurrentExp = currentExp + newExp
//
//                    userDataSource.autoStopTimerWithoutLevelUp(
////                        newCurrentTool = newCurrentTool,
////                        newCurrentToolState = newCurrentToolState,
//                        newCurrentExp = newCurrentExp,
//                        newWiDTotalExp = newWiDTotalExp,
//                        newTitleCountMap = titleCountMap,
//                        newTitleDurationMap = titleDurationMap,
//                        newToolCountMap = toolCountMap
//                    )
//                }
//
//                addWiDToMap(createdWiD = newWiD)
//            },
////            onWiDCreated = { createdWiD: WiD ->
////                addWiDToMap(createdWiD = createdWiD)
////            }
//        )
//    }
//
//    fun pauseTimer() {
//        Log.d(TAG, "pauseTimer executed")
//
//        toolDataSource.pauseTimer(
//            email = user.value?.email ?: "",
////            onTimerPaused = { currentTitle: String, newCurrrentToolState: CurrentToolState, newTimerCurrentDuration: Duration, newTimerNextSelectedTime: Duration ->
////            onTimerPaused = { newTimerCurrentDuration: Duration ->
//            onTimerPaused = { newWiD: WiD ->
//                val currentLevel = user.value?.level ?: 1
//                val currentExp = user.value?.currentExp ?: 0
//                val newExp = newWiD.duration.seconds.toInt()
//                val currentRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0
//
//                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
//                val newWiDTotalExp = wiDTotalExp + newExp
//
//                val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
//                val currentCount = titleCountMap[title.value] ?: 0
//                titleCountMap[title.value] = currentCount + 1
//
//                val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
//                val currentDuration = titleDurationMap[title.value] ?: Duration.ZERO
//                titleDurationMap[title.value] = currentDuration.plus(newWiD.duration)
//
//                val createdBy = CurrentTool.TIMER
//                val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
//                val currentToolCount = toolCountMap[createdBy] ?: 0
//                toolCountMap[createdBy] = currentToolCount + 1
//
//                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
//                    val newLevel = currentLevel + 1
//                    val newLevelAsString = newLevel.toString()
//
//                    val today = LocalDate.now()
//                    val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
//                    levelUpHistoryMap[newLevelAsString] = today
//
//                    val newCurrentExp = currentExp + newExp - currentRequiredExp
//
//                    userDataSource.pauseTimerWithLevelUp(
////                        newCurrentToolState = newCurrentToolState,
////                        newTimerNextSelectedTime = newTimerNextSelectedTime,
//                        newLevel = newLevel,
//                        newLevelUpHistoryMap = levelUpHistoryMap,
//                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
//                        newWiDTotalExp = newWiDTotalExp,
//                        newTitleCountMap = titleCountMap,
//                        newTitleDurationMap = titleDurationMap,
//                        newToolCountMap = toolCountMap
//                    )
//                } else {
//                    val newCurrentExp = currentExp + newExp
//
//                    userDataSource.pauseTimer(
////                        newCurrentToolState = newCurrentToolState,
////                        newTimerNextSelectedTime = newTimerNextSelectedTime,
//                        newCurrentExp = newCurrentExp,
//                        newWiDTotalExp = newWiDTotalExp,
//                        newTitleCountMap = titleCountMap,
//                        newTitleDurationMap = titleDurationMap,
//                        newToolCountMap = toolCountMap
//                    )
//                }
//
//                addWiDToMap(createdWiD = newWiD)
//            },
////            onWiDCreated = { createdWiD: WiD ->
////                addWiDToMap(createdWiD = createdWiD)
////            }
//        )
//    }
//
//    fun stopTimer() {
//        Log.d(TAG, "stopTimer executed")
//
//        toolDataSource.stopTimer(
////            onTimerStopped = { newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState ->
////                userDataSource.stopTimer(
////                    newCurrentTool = newCurrentTool,
////                    newCurrentToolState = newCurrentToolState
////                )
////            }
//        )
//    }
}