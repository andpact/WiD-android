package andpact.project.wid.util

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timer

/**
 * 스톱 워치 플레이어에서는 WiD의 날짜, 제목, 시작 시간 할당 및 스톱 워치 시간 측정을 담당함.
 * State 변수는 직접 Setter를 선언하고,
 * 일반 변수는 get(), set()를 사용함.
 */
class StopwatchPlayer : ViewModel() {
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
    private val _inStopwatchView = mutableStateOf(false)
    val inStopwatchView: State<Boolean> = _inStopwatchView

    // 스톱 워치
    private var timer: Timer? = null
    private val _stopwatchState = mutableStateOf(PlayerState.Stopped)
    val stopwatchState: State<PlayerState> = _stopwatchState
    private val _elapsedTime = mutableLongStateOf(0L)
    val elapsedTime: State<Long> = _elapsedTime

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun setInStopwatchView(isInStopwatchView: Boolean) {
        _inStopwatchView.value = isInStopwatchView
    }

    fun startIt() {
        timer?.cancel()
        _stopwatchState.value = PlayerState.Started

        date = LocalDate.now()
        start = LocalTime.now()

        viewModelScope.launch {
            delay(1_000) // 1초 뒤에 스톱워치를 시작

            timer = timer(period = 1_000) {
                _elapsedTime.value += 1_000 // MilliSeconds 기준
            }
        }
    }

    fun restartIt() {
        _elapsedTime.value = 0
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