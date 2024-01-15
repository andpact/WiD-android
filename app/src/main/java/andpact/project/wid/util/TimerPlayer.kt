package andpact.project.wid.util

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/**
 * 플레이어에서는 WiD의 날짜, 제목, 시작 시간 할당 및 타이머 시간 측정을 담당함.
 */
class TimerPlayer : ViewModel() {
    // 날짜
    var date: LocalDate
        get() = _date
        set(value) { _date = value }
    private var _date: LocalDate = LocalDate.now()

    // 시작 시간
    var start: LocalTime
        get() = _start
        set(value) { _start = value }
    private var _start: LocalTime = LocalTime.now()

    // 제목
    private val _title = mutableStateOf(titles[0])
    val title: State<String> = _title

    // 화면
    private val _inTimerView = mutableStateOf(false)
    val inTimerView: State<Boolean> = _inTimerView

    // 타이머
    private var timer: CountDownTimer? = null
    private val _timerState = mutableStateOf(PlayerState.Stopped)
    val timerState: State<PlayerState> = _timerState
    private val _remainingTime = mutableLongStateOf(0L)
    val remainingTime: State<Long> = _remainingTime

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun setRemainingTime(newRemainingTime: Long) {
        _remainingTime.value = newRemainingTime
    }

    fun setInTimerView(isInTimerView: Boolean) {
        _inTimerView.value = isInTimerView
    }

    fun startIt() {
        timer?.cancel()
        _timerState.value = PlayerState.Started

        date = LocalDate.now()
        start = LocalTime.now()

        viewModelScope.launch {
//            delay(1_000) // 1초 뒤에 타이머를 시작

            timer = object : CountDownTimer(_remainingTime.value, 1_000) { // MilliSeconds 기준
                override fun onTick(millisUntilFinished: Long) {
                    _remainingTime.value = millisUntilFinished // 남은 시간 업데이트
                }

                override fun onFinish() {

                }
            }

            timer?.start() // CountDownTimer는 타이머 객체 설정하고 시작을 해줘야 동자함.
        }
    }

    fun pauseIt() {
        timer?.cancel()
        _timerState.value = PlayerState.Paused
    }

    fun stopIt() {
        timer?.cancel()
        _timerState.value = PlayerState.Stopped

        _remainingTime.value = 0
    }
}