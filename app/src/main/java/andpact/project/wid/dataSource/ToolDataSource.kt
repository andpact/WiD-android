package andpact.project.wid.dataSource

import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import andpact.project.wid.util.titleNumberStringList
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

class ToolDataSource @Inject constructor(
    private val wiDRepository: WiDRepository
) {
    private val TAG = "ToolDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    private var date: LocalDate = LocalDate.now()

    private val _title = mutableStateOf(titleNumberStringList[0])
    val title: State<String> = _title

    private var start: LocalTime = LocalTime.now()
    private var finish: LocalTime = LocalTime.now()

    private var timer: Timer? = null

    /** 스톱워치 */
    private val _totalDuration = mutableStateOf(Duration.ZERO) // accumulatedPrevDuration + currentDuration
    val totalDuration: State<Duration> = _totalDuration
    private var accumulatedPrevDuration: Duration = Duration.ZERO

    /** 타이머 */
    private val _remainingTime = mutableStateOf(Duration.ZERO)
    val remainingTime: State<Duration> = _remainingTime
    private val _selectedTime = mutableStateOf(Duration.ZERO)
    val selectedTime: State<Duration> = _selectedTime

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        _title.value = newTitle
    }

    fun startStopwatch(
        onStopwatchStarted: (
            newCurrentTitle: String,
            newCurrentTool: CurrentTool,
            newCurrentToolState: CurrentToolState,
            newStopwatchStartDate: LocalDate,
            newStopwatchStartTime: LocalTime
        ) -> Unit
    ) {
        Log.d(TAG, "startStopwatch executed")

        timer?.cancel()

        date = LocalDate.now()
        start = LocalTime.now().withNano(0)

        onStopwatchStarted(_title.value, CurrentTool.STOPWATCH, CurrentToolState.STARTED, date, start) // User 문서 수정 용

        timer = timer(period = 1_000) {
            finish = LocalTime.now().withNano(0)

            // 소요 시간은 start와 finish 사이의 값으로 구해야 한다.
            if (start.equals(finish) || start.isBefore(finish)) {
                _totalDuration.value = accumulatedPrevDuration + Duration.between(start, finish)
            } else {
                _totalDuration.value = accumulatedPrevDuration + Duration.between(start, LocalTime.MAX.withNano(0)) + Duration.between(LocalTime.MIN, finish)
            }
        }
    }

    fun pauseStopwatch(
        email: String,
        onStopwatchPaused: (
            currentTitle: String, // 제목 카운트, 소요 시간 맵 갱신 용
            newCurrentToolState: CurrentToolState,
            newStopwatchAccumulatedPrevDuration: Duration,
            newStopwatchCurrentDuration: Duration,
        ) -> Unit,
        onWiDCreated: (newWiD: WiD) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        accumulatedPrevDuration = _totalDuration.value

        timer?.cancel()

        if (start.equals(finish)) {
            return
        }

        if (start.isBefore(finish)) {
            // 1분 미만의 WiD는 생성 안됨.
//            if (duration.value < Duration.ofMinutes(1)) {
//                return
//            }

            val duration = Duration.between(start, finish)
            val newWiD = WiD(
                id = "null",
                date = date,
                title = _title.value,
                start = start,
                finish = finish,
                duration = duration
            )

            wiDRepository.createWiD(
                email = email,
                wid = newWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = duration
                        )
                        onWiDCreated(createdWiD)

                        onStopwatchPaused(_title.value, CurrentToolState.PAUSED, accumulatedPrevDuration, duration)
                    }
                }
            )
        } else { // 자정 넘어가는 경우
            val previousDate = date.minusDays(1)
            val midnight = LocalTime.MIDNIGHT

            // 1분 미만의 WiD는 생성 안됨.
            if (Duration.between(start, midnight.plusSeconds(-1)) + Duration.between(midnight, finish) < Duration.ofMinutes(1)) {
                return
            }

            val firstWiDDuration = Duration.between(start, midnight.plusSeconds(-1))
            val firstWiD = WiD(
                id = "null",
                date = previousDate,
                title = _title.value,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = firstWiDDuration
            )

            wiDRepository.createWiD(
                email = email,
                wid = firstWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = firstWiDDuration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )

            val secondWiDDuration = Duration.between(midnight, finish)
            val secondWiD = WiD(
                id = "null",
                date = date,
                title = _title.value,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish)
            )

            val totalDuration = firstWiDDuration.plus(secondWiDDuration)
            onStopwatchPaused(_title.value, CurrentToolState.PAUSED, accumulatedPrevDuration, totalDuration)

            wiDRepository.createWiD(
                email = email,
                wid = secondWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = secondWiDDuration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )
        }
    }

    fun stopStopwatch(
        onStopwatchStopped: (
            newCurrentTool: CurrentTool,
            newCurrentToolState: CurrentToolState
        ) -> Unit
    ) {
        Log.d(TAG, "stopStopwatch executed")

        timer?.cancel()

        onStopwatchStopped(CurrentTool.NONE, CurrentToolState.STOPPED)

        _totalDuration.value = Duration.ZERO
        accumulatedPrevDuration = Duration.ZERO
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        _selectedTime.value = newSelectedTime
    }

    fun startTimer(
        email: String, // 자동 종료 용 콜백
        onTimerStarted: (
            newCurrentTitle: String,
            newCurrentTool: CurrentTool,
            newCurrentToolState: CurrentToolState,
            newTimerStartDate: LocalDate,
            newTimerStartTime: LocalTime,
        ) -> Unit, // startTimestamp용 date필요
        onTimerAutoStopped: (
            currentTitle: String,
            newCurrentTool: CurrentTool,
            newCurrentToolState: CurrentToolState,
            newTimerCurrentDuration: Duration
        ) -> Unit,
        onWiDCreated: (newWiD: WiD) -> Unit
    ) {
        Log.d(TAG, "startTimer executed")

        timer?.cancel()

        date = LocalDate.now()
        start = LocalTime.now().withNano(0)

        onTimerStarted(_title.value, CurrentTool.TIMER, CurrentToolState.STARTED, date, start)

        timer = timer(period = 1_000) {
            finish = LocalTime.now().withNano(0)
            _remainingTime.value = _selectedTime.value - Duration.between(start, finish)

            if (_remainingTime.value <= Duration.ZERO) {
                autoStopTimer(
                    email = email,
                    onTimerAutoStopped = { currentTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newTimerCurrentDuration: Duration ->
                        onTimerAutoStopped(currentTitle, newCurrentTool, newCurrentToolState, newTimerCurrentDuration)
                    },
                    onWiDCreated = { createdWiD: WiD ->
                        onWiDCreated(createdWiD)
                    }
                )
            }
        }
    }

    fun pauseTimer(
        email: String,
        onTimerPaused: (
            currentTitle: String, // 제목 카운트, 소요 시간 맵 갱신 용
            newCurrentToolState: CurrentToolState,
            newTimerCurrentDuration: Duration,
            newTimerNextSelectedTime: Duration
        ) -> Unit,
        onWiDCreated: (newWiD: WiD) -> Unit
    ) {
        Log.d(TAG, "pauseTimer executed")

        timer?.cancel()

        _selectedTime.value = _remainingTime.value

        if (start.equals(finish)) {
            return
        }

        if (start.isBefore(finish)) {
            // 1분 미만의 WiD는 생성 안됨.
//            if (duration.value < Duration.ofMinutes(1)) {
//                return
//            }

            val duration = Duration.between(start, finish)
            val newWiD = WiD(
                id = "null",
                date = date,
                title = _title.value,
                start = start,
                finish = finish,
                duration = duration
            )

            onTimerPaused(_title.value, CurrentToolState.PAUSED, duration, _selectedTime.value)

            wiDRepository.createWiD(
                email = email,
                wid = newWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = duration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )
        } else {
            val midnight = LocalTime.MIDNIGHT
            val previousDate = date.minusDays(1)

            // 1분 미만의 WiD는 생성 안됨.
            if (Duration.between(start, midnight.plusSeconds(-1)) + Duration.between(midnight, finish) < Duration.ofMinutes(1)) {
                return
            }

            val firstWiDDuration = Duration.between(start, midnight.plusSeconds(-1))
            val firstWiD = WiD(
                id = "null",
                date = previousDate,
                title = _title.value,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = firstWiDDuration,
            )
            wiDRepository.createWiD(
                email = email,
                wid = firstWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = firstWiDDuration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )

            val secondWiDDuration = Duration.between(midnight, finish)
            val secondWiD = WiD(
                id = "null",
                date = date,
                title = _title.value,
                start = midnight,
                finish = finish,
                duration = secondWiDDuration,
            )

            val totalDuration = firstWiDDuration.plus(secondWiDDuration)
            onTimerPaused(_title.value, CurrentToolState.PAUSED, totalDuration, _selectedTime.value)

            wiDRepository.createWiD(
                email = email,
                wid = secondWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = secondWiDDuration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )
        }
    }

    fun stopTimer(
        onTimerStopped: (
            newCurrentTool: CurrentTool,
            newCurrentToolState: CurrentToolState
        ) -> Unit
    ) {
        Log.d(TAG, "stopTimer executed")

        timer?.cancel()

        onTimerStopped(CurrentTool.NONE, CurrentToolState.STOPPED)

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }

    private fun autoStopTimer(
        email: String,
        onTimerAutoStopped: (
            currentTitle: String,
            newCurrentTool: CurrentTool,
            newCurrentToolState: CurrentToolState,
            newTimerCurrentDuration: Duration,
        ) -> Unit,
        onWiDCreated: (newWiD: WiD) -> Unit
    ) {
        Log.d(TAG, "autoStopTimer executed")

        timer?.cancel()

        if (start.equals(finish)) {
            return
        }

        if (start.isBefore(finish)) {
            // 1분 미만의 WiD는 생성 안됨.
//            if (duration.value < Duration.ofMinutes(1)) {
//                return
//            }

            val duration = Duration.between(start, finish)
            val newWiD = WiD(
                id = "null",
                date = date,
                title = _title.value,
                start = start,
                finish = finish,
                duration = duration
            )

            onTimerAutoStopped(_title.value, CurrentTool.NONE, CurrentToolState.PAUSED, duration)

            wiDRepository.createWiD(
                email = email,
                wid = newWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = duration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )
        } else {
            val midnight = LocalTime.MIDNIGHT
            val previousDate = date.minusDays(1)

            // 1분 미만의 WiD는 생성 안됨.
            if (Duration.between(start, midnight.plusSeconds(-1)) + Duration.between(midnight, finish) < Duration.ofMinutes(1)) {
                return
            }

            val firstWiDDuration = Duration.between(start, midnight.plusSeconds(-1))
            val firstWiD = WiD(
                id = "null",
                date = previousDate,
                title = _title.value,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = firstWiDDuration,
            )

            wiDRepository.createWiD(
                email = email,
                wid = firstWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = firstWiDDuration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )

            val secondWiDDuration = Duration.between(midnight, finish)
            val secondWiD = WiD(
                id = "null",
                date = date,
                title = _title.value,
                start = midnight,
                finish = finish,
                duration = secondWiDDuration,
            )

            val totalDuration = firstWiDDuration.plus(secondWiDDuration)
            onTimerAutoStopped(_title.value, CurrentTool.NONE, CurrentToolState.PAUSED, totalDuration)

            wiDRepository.createWiD(
                email = email,
                wid = secondWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = date,
                            title = _title.value,
                            start = start,
                            finish = finish,
                            duration = secondWiDDuration
                        )
                        onWiDCreated(createdWiD)
                    }
                }
            )
        }

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }
}