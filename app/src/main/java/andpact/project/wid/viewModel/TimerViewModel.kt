package andpact.project.wid.viewModel

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.PlayerState
import andpact.project.wid.util.titles
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timer

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d("TimerViewModel", "TimerViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TimerViewModel", "TimerViewModel is cleared")
    }

    // WiD
    private val wiDService = WiDService(application)

    // 날짜
//    var date: LocalDate
//        get() = _date
//        set(value) { _date = value }
//    private var _date: LocalDate = LocalDate.now()
    private var date: LocalDate = LocalDate.now()

    // 제목
    private val _title = mutableStateOf(titles[0])
    val title: State<String> = _title

    // 시작 시간
//    var start: LocalTime
//        get() = _start
//        set(value) { _start = value }
//    private var _start: LocalTime = LocalTime.now()
    private var start: LocalTime = LocalTime.now()

    // 종료 시간
//    var finish: LocalTime
//        get() = _finish
//        set(value) { _finish = value }
//    private var _finish: LocalTime = LocalTime.now()
    private var finish: LocalTime = LocalTime.now()

    // 남은 시간 - 시간 표시용
    private val _remainingTime = mutableStateOf(Duration.ZERO)
    val remainingTime: State<Duration> = _remainingTime

    // 소요 시간 - 시간 설정용
    private val _seletedTime = mutableStateOf(Duration.ZERO)
    val seletedTime: State<Duration> = _seletedTime

    // 화면
//    private val _inTimerView = mutableStateOf(false)
//    val inTimerView: State<Boolean> = _inTimerView

    // 타이머
//    private var timer: CountDownTimer? = null
    private var timer: Timer? = null
    private val _timerState = mutableStateOf(PlayerState.Stopped)
    val timerState: State<PlayerState> = _timerState
    private val _timerTopBottomBarVisible = mutableStateOf(true)
    val timerTopBottomBarVisible: State<Boolean> = _timerTopBottomBarVisible

    fun setTitle(newTitle: String) {
        Log.d("TimerPlayer", "setTitle executed")

        _title.value = newTitle
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d("TimerPlayer", "setRemainingTime executed")

        _seletedTime.value = newSelectedTime
    }

//    fun setInTimerView(isInTimerView: Boolean) {
//        Log.d("TimerPlayer", "setInTimerView executed")
//
//        _inTimerView.value = isInTimerView
//    }

    fun setTimerTopBottomBarVisible(timerTopBottomBarVisible: Boolean) {
        Log.d("TimerPlayer", "setTimerTopBottomBarVisible executed")

        _timerTopBottomBarVisible.value = timerTopBottomBarVisible
    }

    fun startTimer() {
        Log.d("TimerPlayer", "startTimer executed")

        timer?.cancel()
        _timerState.value = PlayerState.Started

        date = LocalDate.now()
        start = LocalTime.now().withNano(0)

//        viewModelScope.launch {
////            delay(1_000) // 1초 뒤에 타이머를 시작
//
//            timer = object : CountDownTimer(_remainingTime.value, 1_000) { // MilliSeconds 기준
//                override fun onTick(millisUntilFinished: Long) {
//                    _remainingTime.value = millisUntilFinished // 남은 시간 업데이트
//                }
//
//                override fun onFinish() {
//                    pauseTimer()
//                    stopTimer()
//                }
//            }
//
//            timer?.start() // CountDownTimer는 타이머 객체 설정하고 시작을 해줘야 동작함.
//        }

        timer = timer(period = 1_000) {
            finish = LocalTime.now().withNano(0)
            _remainingTime.value = _seletedTime.value - Duration.between(start, finish)

            if (_remainingTime.value <= Duration.ZERO) {
                pauseTimer()
                stopTimer()
            }
        }
    }

    fun pauseTimer() {
        Log.d("TimerPlayer", "pauseTimer executed")

        timer?.cancel()
        _timerState.value = PlayerState.Paused

        _seletedTime.value = _remainingTime.value

        if (start.equals(finish))
            return

        if (start.isBefore(finish)) {
            // 1분 미만의 WiD는 생성 안됨.
//            if (duration.value < Duration.ofMinutes(1)) {
//                return
//            }

            val newWiD = WiD(
                id = 0,
                date = date,
                title = title.value,
                start = start,
                finish = finish,
                duration = Duration.between(start, finish),
            )
            wiDService.createWiD(newWiD)
        } else {
            val midnight = LocalTime.MIDNIGHT
            val previousDate = date.minusDays(1)

            // 1분 미만의 WiD는 생성 안됨.
            if (Duration.between(start, midnight.plusSeconds(-1)) + Duration.between(midnight, finish) < Duration.ofMinutes(1)) {
                return
            }

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
        }
    }

    fun stopTimer() {
        Log.d("TimerPlayer", "stopTimer executed")

        timer?.cancel()
        _timerState.value = PlayerState.Stopped

        _remainingTime.value = Duration.ZERO
        _seletedTime.value = Duration.ZERO

        _timerTopBottomBarVisible.value = true
    }
}