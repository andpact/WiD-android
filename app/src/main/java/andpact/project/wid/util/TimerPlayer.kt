package andpact.project.wid.util

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerPlayer : ViewModel() {
    private var timer: CountDownTimer? = null

    // 현재 스톱워치 상태를 나타내는 StateFlow
    private val _timerState = MutableStateFlow(PlayerState.Stopped)
    val timerState: StateFlow<PlayerState> = _timerState

    fun startIt() {
            timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
//                _elapsedTime.value += 1
            }

            override fun onFinish() {
                // Do something on finish if needed
            }
        }

        timer?.start()
    }

    fun pauseIt() {
        timer?.cancel()
        _timerState.value = PlayerState.Paused
    }

    fun stopIt() {
        timer?.cancel()
//        _elapsedTime.value = 0
        _timerState.value = PlayerState.Stopped
    }
}