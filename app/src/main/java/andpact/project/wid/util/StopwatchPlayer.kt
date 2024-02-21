package andpact.project.wid.util

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.timer

/**
 * 스톱 워치 플레이어에서는 WiD의 날짜, 제목, 시작 시간 할당 및 스톱 워치 시간 측정을 담당함.
 * 값이 변함에 따라 ui를 갱신 해야 하는 State 변수는 직접 Setter 메서드를 선언하고,
 * 값이 변해도 표시할 필요 없는 일반 변수는 get(), set()를 사용함.
 * State 변수에 값을 할당할 때는 (_변수)를 사용해야 함.
 */
class StopwatchPlayer(application: Application) : AndroidViewModel(application) {
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

    // 소요 시간 - 시간 표시용
    private val _duration = mutableStateOf(Duration.ZERO)
    val duration: State<Duration> = _duration
    //    private var prevDuration: Duration
//        get() = _prevDuration
//        set(value) { _prevDuration = value }
    private var prevDuration: Duration = Duration.ZERO

    // 스톱 워치
    private var timer: Timer? = null
    private val _stopwatchState = mutableStateOf(PlayerState.Stopped)
    val stopwatchState: State<PlayerState> = _stopwatchState
    private val _inStopwatchView = mutableStateOf(false)
    val inStopwatchView: State<Boolean> = _inStopwatchView
    private val _stopwatchTopBottomBarVisible = mutableStateOf(true)
    val stopwatchTopBottomBarVisible: State<Boolean> = _stopwatchTopBottomBarVisible

    fun setTitle(newTitle: String) {
        Log.d("StopwatchPlayer", "setTitle executed")

        _title.value = newTitle
    }

    fun setInStopwatchView(isInStopwatchView: Boolean) {
        Log.d("StopwatchPlayer", "setInStopwatchView executed")

        _inStopwatchView.value = isInStopwatchView
    }

    fun setStopwatchTopBottomBarVisible(stopwatchTopBottomBarVisible: Boolean) {
        Log.d("StopwatchPlayer", "setStopwatchTopBottomBarVisible executed")

        _stopwatchTopBottomBarVisible.value = stopwatchTopBottomBarVisible
    }

    fun startStopwatch() {
        Log.d("StopwatchPlayer", "startStopwatch executed")

        timer?.cancel()
        _stopwatchState.value = PlayerState.Started

        date = LocalDate.now()
        start = LocalTime.now().withNano(0)

        timer = timer(period = 1_000) {
            finish = LocalTime.now().withNano(0)

            // 소요 시간은 start와 finish 사이의 값으로 구해야 한다.
            if (start.equals(finish) || start.isBefore(finish)) {
                _duration.value = prevDuration + Duration.between(start, finish)
            } else {
                _duration.value = prevDuration + Duration.between(start, LocalTime.MAX.withNano(0)) + Duration.between(LocalTime.MIN, finish)
            }
        }
    }

    fun pauseStopwatch() {
        Log.d("StopwatchPlayer", "pauseStopwatch executed")

        prevDuration = _duration.value

        timer?.cancel()
        _stopwatchState.value = PlayerState.Paused

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
                duration = Duration.between(start, finish)
            )
            wiDService.createWiD(newWiD)
        } else { // 자정 넘어가는 경우
            val previousDate = date.minusDays(1)
            val midnight = LocalTime.MIDNIGHT

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
                duration = Duration.between(start, midnight.plusSeconds(-1))
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title.value,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish)
            )
            wiDService.createWiD(secondWiD)
        }
    }

    fun stopStopwatch() {
        Log.d("StopwatchPlayer", "stopStopwatch executed")

        timer?.cancel()
        _stopwatchState.value = PlayerState.Stopped
        _duration.value = Duration.ZERO
    }
}