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
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
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
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "StopwatchViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    // 유저
    val user: State<User?> = userDataSource.user

    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD
    val secondCurrentWiD: State<WiD> = wiDDataSource.secondCurrentWiD

    // 도구
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState
    val totalDuration: State<Duration> = wiDDataSource.totalDuration
    private val _stopwatchViewBarVisible = mutableStateOf(true)
    val stopwatchViewBarVisible: State<Boolean> = _stopwatchViewBarVisible

    fun setTitle(newTitle: Title) {
        Log.d(TAG, "setTitle executed")

        wiDDataSource.setCurrentWiDTitle(newTitle)
    }

    fun setStopwatchViewBarVisible(stopwatchViewBarVisible: Boolean) {
        Log.d(TAG, "setStopwatchViewBarVisible executed")

        _stopwatchViewBarVisible.value = stopwatchViewBarVisible
    }

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        wiDDataSource.startStopwatch()
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        wiDDataSource.pauseStopwatch(
            email = user.value?.email ?: "",
            onStopwatchPaused = { newExp: Int ->
                // 레벨
                val currentLevel = user.value?.level ?: 1
                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelDateMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp

                    userDataSource.pauseStopwatchWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp
                    )
                } else { // 레벨업 아님.
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseStopwatch(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp
                    )
                }
            }
        )
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        wiDDataSource.stopStopwatch()
    }

    fun getStopwatchDurationString(duration: Duration): AnnotatedString {
        Log.d(TAG, "getStopwatchDurationString executed")

        val hours = duration.toHours()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        val hoursText = hours.toString()
        val minutesText = if (0 < hours) {
            minutes.toString().padStart(2, '0')
        } else {
            minutes.toString().padStart(1, '0')
        }
        val secondsText = if (0 < minutes || 0 < hours) {
            seconds.toString().padStart(2, '0')
        } else {
            seconds.toString().padStart(1, '0')
        }

        return buildAnnotatedString {
            withStyle(style = ParagraphStyle(lineHeight = 80.sp)) {
                if (0 < hours) {
                    withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                        append(hoursText + "\n")
                    }
                }

                if (0 < minutes || 0 < hours) {
                    withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                        append(minutesText + "\n")
                    }
                }

                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                    append(secondsText + "\n")
                }
            }
        }
    }
}