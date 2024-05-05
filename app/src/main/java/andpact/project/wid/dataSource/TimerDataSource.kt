package andpact.project.wid.dataSource

import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.util.PlayerState
import andpact.project.wid.util.titles
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.timer

class TimerDataSource @Inject constructor(
    private val wiDRepository: WiDRepository
){
    private val TAG = "TimerDataSource"

    init {
        Log.d(TAG, "created")
    }

    fun onCleared() {
        Log.d(TAG, "cleared")
    }

    private var date: LocalDate = LocalDate.now()

    // 제목
    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private var start: LocalTime = LocalTime.now()

    private var finish: LocalTime = LocalTime.now()

    // 남은 시간 - 시간 표시용
    private val _remainingTime = mutableStateOf(Duration.ZERO)
    val remainingTime: State<Duration> = _remainingTime

    // 소요 시간 - 시간 설정용
    private val _selectedTime = mutableStateOf(Duration.ZERO)
    val selectedTime: State<Duration> = _selectedTime

    private var timer: Timer? = null
    private val _timerState = mutableStateOf(PlayerState.Stopped)
    val timerState: State<PlayerState> = _timerState

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        _title.value = newTitle
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        _selectedTime.value = newSelectedTime
    }

    fun startTimer(email: String) {
        Log.d(TAG, "startTimer executed")

        timer?.cancel()
        _timerState.value = PlayerState.Started

        date = LocalDate.now()
        start = LocalTime.now().withNano(0)

        timer = timer(period = 1_000) {
            finish = LocalTime.now().withNano(0)
            _remainingTime.value = _selectedTime.value - Duration.between(start, finish)

            if (_remainingTime.value <= Duration.ZERO) {
                pauseTimer(email = email)
                stopTimer()
            }
        }
    }

    fun pauseTimer(email: String) {
        Log.d(TAG, "pauseTimer executed")

        timer?.cancel()
        _timerState.value = PlayerState.Paused

        _selectedTime.value = _remainingTime.value

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
                duration = Duration.between(start, finish),
            )

//            wiDRepository.createWiD(email = email, wid = newWiD) {}
        } else {
            val midnight = LocalTime.MIDNIGHT
            val previousDate = date.minusDays(1)

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
                duration = Duration.between(start, midnight.plusSeconds(-1)),
            )
//            wiDRepository.createWiD(email = email, wid = firstWiD) {}

            val secondWiD = WiD(
                id = "null",
                date = date,
                title = title.value,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish),
            )
//            wiDRepository.createWiD(email = email, wid = secondWiD) {}
        }
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        timer?.cancel()
        _timerState.value = PlayerState.Stopped

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }
}