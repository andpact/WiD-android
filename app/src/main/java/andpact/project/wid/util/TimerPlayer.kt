package andpact.project.wid.util

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import android.app.Application
import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

//class TimerPlayer(application: Application) : AndroidViewModel(application) {
class TimerPlayer(application: Application) : ViewModel() {
    // WiD
    val wiDService = WiDService(application)

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

    fun startTimer() {
        timer?.cancel()
        _timerState.value = PlayerState.Started

        date = LocalDate.now()
        start = LocalTime.now()

        viewModelScope.launch {
            delay(1_000) // 1초 뒤에 타이머를 시작

            timer = object : CountDownTimer(_remainingTime.value, 1_000) { // MilliSeconds 기준
                override fun onTick(millisUntilFinished: Long) {
                    _remainingTime.value = millisUntilFinished // 남은 시간 업데이트
                }

                override fun onFinish() {
                    pauseTimer()
                    stopTimer()
                }
            }

            timer?.start() // CountDownTimer는 타이머 객체 설정하고 시작을 해줘야 동작함.
        }
    }

    fun pauseTimer() {
        timer?.cancel()
        _timerState.value = PlayerState.Paused

        val start = this.start.withNano(0)
        val finish = LocalTime.now().withNano(0)
        val duration = Duration.between(start, finish)

        if (duration <= Duration.ZERO) {
            return
        }

        if (finish.isBefore(start)) {
            val midnight = LocalTime.MIDNIGHT

            val previousDate = date.minusDays(1)

            val firstWiD = WiD(
                id = 0,
                date = previousDate,
                title = title.value,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = Duration.between(start, midnight.plusSeconds(-1)),
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title.value,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish),
            )
            wiDService.createWiD(secondWiD)
        } else {
            val newWiD = WiD(
                id = 0,
                date = date,
                title = title.value,
                start = start,
                finish = finish,
                duration = duration,
            )
            wiDService.createWiD(newWiD)
        }
    }

    fun stopTimer() {
        timer?.cancel()
        _timerState.value = PlayerState.Stopped

        _remainingTime.value = 0
    }
}