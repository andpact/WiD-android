package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.StopwatchDataSource
import andpact.project.wid.dataSource.TimerDataSource
import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.PlayerState
import andpact.project.wid.util.defaultTitleColorMapWithColors
import andpact.project.wid.util.titles
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerDataSource: TimerDataSource,
    private val userDataSource: UserDataSource
): ViewModel() {
    private val TAG = "TimerViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email: String = userDataSource.firebaseUser.value?.email ?: ""

    // 제목
    val title: State<String> = timerDataSource.title

    private val _titleColorMap = mutableStateOf(userDataSource.user.value?.titleColorMap ?: defaultTitleColorMapWithColors)
    val titleColorMap: State<Map<String, Color>> = _titleColorMap

    // 남은 시간 - 시간 표시용
    val remainingTime: State<Duration> = timerDataSource.remainingTime

    // 소요 시간 - 시간 설정용
    val selectedTime: State<Duration> = timerDataSource.selectedTime

    // 타이머
    val timerState: State<PlayerState> = timerDataSource.timerState
    private val _hideTimerViewBar = mutableStateOf(false)
    val hideTimerViewBar: State<Boolean> = _hideTimerViewBar

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        timerDataSource.setTitle(newTitle)
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        timerDataSource.setSelectedTime(newSelectedTime)
    }

    fun setHideTimerViewBar(hideTimerViewBar: Boolean) {
        Log.d(TAG, "setHideTimerViewBar executed")

        _hideTimerViewBar.value = hideTimerViewBar
    }

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        timerDataSource.startTimer(email = email)
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        timerDataSource.pauseTimer(email = email)
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        timerDataSource.stopTimer()
    }
}