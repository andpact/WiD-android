package andpact.project.wid.util

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer

class StopwatchPlayer : ViewModel() {
    private var timer: Timer? = null

    // 현재 스톱워치 상태를 나타내는 StateFlow
    private val _stopwatchState = MutableStateFlow(PlayerState.Stopped)
    val stopwatchState: StateFlow<PlayerState> = _stopwatchState

    // 스톱워치의 경과 시간을 나타내는 StateFlow
    private val _elapsedTime = mutableStateOf(0L)
    val elapsedTime: State<Long> = _elapsedTime

//    private val _elapsedTime = MutableStateFlow(0L)
//    val elapsedTime: StateFlow<Long> = _elapsedTime

    fun startIt() {
        timer?.cancel()
        _stopwatchState.value = PlayerState.Started

        viewModelScope.launch {
            delay(1000) // 1초 뒤에 타이머를 시작

            timer = timer(period = 1000) {
                _elapsedTime.value += 1
            }
        }
    }

    fun restartIt() {

    }

    fun pauseIt() {
        timer?.cancel()
        _stopwatchState.value = PlayerState.Paused
    }

    fun stopIt() {
        timer?.cancel()
        _stopwatchState.value = PlayerState.Stopped
        _elapsedTime.value = 0
    }

}

enum class PlayerState {
    Started,
    Paused,
    Stopped
}
