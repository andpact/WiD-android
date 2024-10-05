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

/**
 * 값이 변함에 따라 ui를 갱신 해야 하는 State 변수는 직접 Setter 메서드를 선언하고,
 * 값이 변해도 표시할 필요 없는 일반 변수는 get(), set()를 사용함.
 * State 변수에 값을 할당할 때는 (_변수)를 사용해야 함.
 *
 * MutableLiveData는 Composable 밖(액티비티?)에서도 사용 가능함.
 * mutableState는 Composable 안에서 만 사용 가능 한듯.
 */
@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val toolDataSource: ToolDataSource,
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "StopwatchViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val user: State<User?> = userDataSource.user

    val title: State<String> = toolDataSource.title
    val titleColorMap: Map<String, Color> = titleNumberStringToTitleColorMap

    val totalDuration: State<Duration> = toolDataSource.totalDuration

    private val _stopwatchViewBarVisible = mutableStateOf(true)
    val stopwatchViewBarVisible: State<Boolean> = _stopwatchViewBarVisible

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        toolDataSource.setTitle(newTitle)
    }

    fun setStopwatchViewBarVisible(stopwatchViewBarVisible: Boolean) {
        Log.d(TAG, "setStopwatchViewBarVisible executed")

        _stopwatchViewBarVisible.value = stopwatchViewBarVisible
    }

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        toolDataSource.startStopwatch(
            onStopwatchStarted = { newCurrentTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newStopwatchStartDate: LocalDate, newStopwatchStartTime: LocalTime ->
                userDataSource.startStopwatch(
                    newCurrentTitle = newCurrentTitle,
                    newCurrentTool = newCurrentTool,
                    newCurrentToolState = newCurrentToolState,
                    newStopwatchStartDate = newStopwatchStartDate,
                    newStopwatchStartTime = newStopwatchStartTime
                )
            }
        )
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        toolDataSource.pauseStopwatch(
            email = user.value?.email ?: "",
            onStopwatchPaused = { currentTitle: String, newCurrentToolState: CurrentToolState, newStopwatchAccumulatedPrevDuration: Duration, newStopwatchCurrentDuration: Duration ->
                val currentLevel = user.value?.level ?: 1
                val currentExp = user.value?.currentExp ?: 0
                val newExp = newStopwatchCurrentDuration.seconds.toInt()
                val currentLevelRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0

                val totalExp = user.value?.totalExp ?: 0
                val newTotalExp = totalExp + newExp
                val wiDTotalExp = user.value?.totalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                val titleCountMap = user.value?.titleCountMap?.toMutableMap() ?: mutableMapOf()
                val currentCount = titleCountMap[currentTitle] ?: 0
                titleCountMap[currentTitle] = currentCount + 1

                val titleDurationMap = user.value?.titleDurationMap?.toMutableMap() ?: mutableMapOf()
                val currentDuration = titleDurationMap[currentTitle] ?: Duration.ZERO
                titleDurationMap[currentTitle] = currentDuration.plus(newStopwatchCurrentDuration)

                if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()

                    val today = LocalDate.now()
                    val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                    levelUpHistoryMap[newLevelAsString] = today

                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp

                    userDataSource.pauseStopwatchWithLevelUp(
                        newCurrentToolState = newCurrentToolState,
                        newStopwatchPrevDuration = newStopwatchAccumulatedPrevDuration,
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelUpHistoryMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newTotalExp = newTotalExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap
                    )
                } else { // 레벨업 아님.
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseStopwatch(
                        newCurrentToolState = newCurrentToolState,
                        newStopwatchPrevDuration = newStopwatchAccumulatedPrevDuration,
                        newCurrentExp = newCurrentExp,
                        newTotalExp = newTotalExp,
                        newWiDTotalExp = newWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap
                    )
                }
            },
            onWiDCreated = { createdWiD: WiD ->
                addWiDToMap(createdWiD = createdWiD)
            }
        )
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        toolDataSource.stopStopwatch(
            onStopwatchStopped = { newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState ->
                userDataSource.stopStopwatch(
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