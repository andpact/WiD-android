package andpact.project.wid.dataSource

import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.util.PlayerState
import andpact.project.wid.util.titles
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

class StopwatchDataSource @Inject constructor(
    private val wiDRepository: WiDRepository
){
    private val TAG = "StopwatchDataSource"

    init {
        Log.d(TAG, "created")
    }

    fun onCleared() {
        Log.d(TAG, "cleared")
    }

    // 날짜
    private var date: LocalDate = LocalDate.now()

    // 제목
    private val _title = mutableStateOf("")
    val title: State<String> = _title

//    var titleColorMap: State<Map<String, Color>?> = emptyMap<String, Color>()
//    private var titleColorMap = emptyMap<String, Color>()

    // 시작 시간
    private var start: LocalTime = LocalTime.now()

    // 종료 시간
    private var finish: LocalTime = LocalTime.now()

    // 소요 시간 - 시간 표시용
    private val _duration = mutableStateOf(Duration.ZERO)
    val duration: State<Duration> = _duration
    private var prevDuration: Duration = Duration.ZERO

    // 스톱 워치
    private var timer: Timer? = null
    private val _stopwatchState = mutableStateOf(PlayerState.Stopped)
    val stopwatchState: State<PlayerState> = _stopwatchState

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        _title.value = newTitle
    }

//    fun setTitleColorMap(map: Map<String, Color>) {
//        titleColorMap = map
//    }

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

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

    fun pauseStopwatch(email: String) {
        Log.d(TAG, "pauseStopwatch executed")

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
                id = "null",
                date = date,
                title = title.value,
                start = start,
                finish = finish,
                duration = Duration.between(start, finish)
            )

            wiDRepository.createWiD(email = email, wid = newWiD) {}
        } else { // 자정 넘어가는 경우
            val previousDate = date.minusDays(1)
            val midnight = LocalTime.MIDNIGHT

            // 1분 미만의 WiD는 생성 안됨.
            if (Duration.between(start, midnight.plusSeconds(-1)) + Duration.between(midnight, finish) < Duration.ofMinutes(1)) {
                return
            }

            val firstWiD = WiD(
                id = "null",
                date = previousDate,
                title = title.value,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = Duration.between(start, midnight.plusSeconds(-1))
            )

            wiDRepository.createWiD(email = email, wid = firstWiD) {}

            val secondWiD = WiD(
                id = "null",
                date = date,
                title = title.value,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish)
            )

            wiDRepository.createWiD(email = email, wid = secondWiD) {}
        }
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        timer?.cancel()
        _stopwatchState.value = PlayerState.Stopped
        _duration.value = Duration.ZERO
        prevDuration = Duration.ZERO
    }
}