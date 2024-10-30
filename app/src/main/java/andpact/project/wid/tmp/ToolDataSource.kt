package andpact.project.wid.tmp

import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

/**
 * state로 선언하지 않으면 다른 클래스에서 변수의 변화를 감지할 수 없다.
 */
//class ToolDataSource @Inject constructor(private val wiDRepository: WiDRepository) {
//    private val TAG = "ToolDataSource"
//    init { Log.d(TAG, "created") }
//    fun onCleared() { Log.d(TAG, "cleared") }
//
//    private val _date: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
//    val date: State<LocalDate> = _date
//    private val _title: MutableState<String> = mutableStateOf(titleNumberStringList[0])
//    val title: State<String> = _title // 스톱워치, 타이머 제목 공유함
//    private val _start: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
//    val start: State<LocalTime> = _start
//    private val _finish: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
//    val finish: State<LocalTime> = _finish
//
//    private var timer: Timer? = null
//
//    private val _currentTool: MutableState<CurrentTool> = mutableStateOf(CurrentTool.NONE)
//    val currentTool: State<CurrentTool> = _currentTool
//    private val _currentToolState: MutableState<CurrentToolState> = mutableStateOf(CurrentToolState.STOPPED)
//    val currentToolState: State<CurrentToolState> = _currentToolState
//
//    /** 스톱워치 */
//    private val _totalDuration = mutableStateOf(Duration.ZERO) // accumulatedPrevDuration + currentDuration
//    val totalDuration: State<Duration> = _totalDuration
//    private var accumulatedPrevDuration: Duration = Duration.ZERO
//
//    /** 타이머 */
//    private val _remainingTime = mutableStateOf(Duration.ZERO)
//    val remainingTime: State<Duration> = _remainingTime
//    private val _selectedTime = mutableStateOf(Duration.ZERO)
//    val selectedTime: State<Duration> = _selectedTime
//
//    fun setTitle(newTitle: String) {
//        Log.d(TAG, "setTitle executed")
//
//        _title.value = newTitle
//    }
//
//    fun startStopwatch(
////        onStopwatchStarted: (
////            newCurrentTitle: String,
////            newCurrentTool: CurrentTool,
////            newCurrentToolState: CurrentToolState,
////            newStopwatchStartDate: LocalDate,
////            newStopwatchStartTime: LocalTime
////        ) -> Unit
//    ) {
//        Log.d(TAG, "startStopwatch executed")
//
//        _currentTool.value = CurrentTool.STOPWATCH
//        _currentToolState.value = CurrentToolState.STARTED
//
//        timer?.cancel()
//
//        _date.value = LocalDate.now()
//        _start.value = LocalTime.now().withNano(0)
//
////        onStopwatchStarted(_title.value, CurrentTool.STOPWATCH, CurrentToolState.STARTED, date, start) // User 문서 수정 용
//
//        timer = timer(period = 1_000) {
//            _finish.value = LocalTime.now().withNano(0)
//
//            /** Stopwatch View Model -> Tool Data Source ->  */
//
//            // 소요 시간은 start와 finish 사이의 값으로 구해야 한다.
//            if (_start.value.equals(_finish.value) || _start.value.isBefore(_finish.value)) {
//                _totalDuration.value = accumulatedPrevDuration + Duration.between(_start.value, _finish.value)
//            } else {
//                _totalDuration.value = accumulatedPrevDuration + Duration.between(_start.value, LocalTime.MAX.withNano(0)) + Duration.between(LocalTime.MIN, _finish.value)
//            }
//        }
//    }
//
//    fun pauseStopwatch(
//        email: String,
//        onStopwatchPaused: (
////            currentTitle: String, // 제목 카운트, 소요 시간 맵 갱신 용
////            newCurrentToolState: CurrentToolState,
////            newStopwatchAccumulatedPrevDuration: Duration,
////            newStopwatchCurrentDuration: Duration,
//            newWiD: WiD
//        ) -> Unit,
////        onWiDCreated: (newWiD: WiD) -> Unit // 생성된 각각의 위드
//    ) {
//        Log.d(TAG, "pauseStopwatch executed")
//
////        _currentToolState.value = CurrentToolState.PAUSED
//
//        accumulatedPrevDuration = _totalDuration.value
//
//        timer?.cancel()
//
//        if (_start.value.equals(_finish.value)) {
//            return
//        }
//
//        if (_start.value.isBefore(_finish.value)) {
//            val duration = Duration.between(_start.value, _finish.value)
////            if (duration < Duration.ofMinutes(1)) { // 1분 미만의 WiD는 생성 안됨.
////                return
////            }
//
//            val newWiD = WiD(
//                id = "null",
//                date = _date.value,
//                title = _title.value,
//                start = _start.value,
//                finish = _finish.value,
//                duration = duration,
//                createdBy = CurrentTool.STOPWATCH
//            )
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = newWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = _start.value,
//                            finish = _finish.value,
//                            duration = duration,
//                            createdBy = CurrentTool.STOPWATCH
//                        )
////                        onWiDCreated(createdWiD)
//
////                        onStopwatchPaused(_title.value, CurrentToolState.PAUSED, accumulatedPrevDuration, duration)
////                        onStopwatchPaused(duration)
//
//                        onStopwatchPaused(createdWiD)
//                    }
//                }
//            )
//        } else { // 자정 넘어가는 경우
//            val previousDate = _date.value.minusDays(1) // 왜 얘를 마이너스 해야 정상 작동이 되지?
//            val midnight = LocalTime.MIDNIGHT
//            val firstWiDDuration = Duration.between(_start.value, midnight.plusSeconds(-1))
//            val secondWiDDuration = Duration.between(midnight, _finish.value)
//
//            // 1분 미만의 WiD는 생성 안됨.
//            if (firstWiDDuration + secondWiDDuration < Duration.ofMinutes(1)) {
//                return
//            }
//
//            val firstWiD = WiD(
//                id = "null",
//                date = previousDate,
//                title = _title.value,
//                start = _start.value,
//                finish = midnight.plusSeconds(-1),
//                duration = firstWiDDuration,
//                createdBy = CurrentTool.STOPWATCH
//            )
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = firstWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = _start.value,
//                            finish = midnight.plusSeconds(-1),
//                            duration = firstWiDDuration,
//                            createdBy = CurrentTool.STOPWATCH
//                        )
////                        onWiDCreated(createdWiD)
//                        onStopwatchPaused(createdWiD)
//                    }
//                }
//            )
//
//            val secondWiD = WiD(
//                id = "null",
//                date = _date.value,
//                title = _title.value,
//                start = midnight,
//                finish = _finish.value,
//                duration = secondWiDDuration,
//                createdBy = CurrentTool.STOPWATCH
//            )
//
////            val totalDuration = firstWiDDuration.plus(secondWiDDuration)
////            onStopwatchPaused(_title.value, CurrentToolState.PAUSED, accumulatedPrevDuration, totalDuration)
////            onStopwatchPaused(totalDuration)
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = secondWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = midnight,
//                            finish = _finish.value,
//                            duration = secondWiDDuration,
//                            createdBy = CurrentTool.STOPWATCH
//                        )
////                        onWiDCreated(createdWiD)
//                        onStopwatchPaused(createdWiD)
//                    }
//                }
//            )
//        }
//    }
//
//    fun stopStopwatch(
////        onStopwatchStopped: (
////            newCurrentTool: CurrentTool,
////            newCurrentToolState: CurrentToolState
////        ) -> Unit
//    ) {
//        Log.d(TAG, "stopStopwatch executed")
//
//        _currentTool.value = CurrentTool.NONE
//        _currentToolState.value = CurrentToolState.STOPPED
//
//        timer?.cancel()
//
////        onStopwatchStopped(CurrentTool.NONE, CurrentToolState.STOPPED)
//
//        _totalDuration.value = Duration.ZERO
//        accumulatedPrevDuration = Duration.ZERO
//    }
//
//    fun setSelectedTime(newSelectedTime: Duration) {
//        Log.d(TAG, "setSelectedTime executed")
//
//        _selectedTime.value = newSelectedTime
//    }
//
//    fun startTimer(
//        email: String, // 자동 종료 용 콜백
////        onTimerStarted: (
////            newCurrentTitle: String,
////            newCurrentTool: CurrentTool,
////            newCurrentToolState: CurrentToolState,
////            newTimerStartDate: LocalDate,
////            newTimerStartTime: LocalTime,
////        ) -> Unit, // startTimestamp용 date필요
//        onTimerAutoStopped: (
////            currentTitle: String,
////            newCurrentTool: CurrentTool,
////            newCurrentToolState: CurrentToolState,
////            newTimerCurrentDuration: Duration,
//            newWiD: WiD
//        ) -> Unit,
////        onWiDCreated: (newWiD: WiD) -> Unit
//    ) {
//        Log.d(TAG, "startTimer executed")
//
//        _currentTool.value = CurrentTool.TIMER
//        _currentToolState.value = CurrentToolState.STARTED
//
//        timer?.cancel()
//
//        _date.value = LocalDate.now()
//        _start.value = LocalTime.now().withNano(0)
//
////        onTimerStarted(_title.value, CurrentTool.TIMER, CurrentToolState.STARTED, date, start)
//
//        timer = timer(period = 1_000) {
//            _finish.value = LocalTime.now().withNano(0)
//            _remainingTime.value = _selectedTime.value - Duration.between(_start.value, _finish.value)
//
//            if (_remainingTime.value <= Duration.ZERO) {
//                autoStopTimer(
//                    email = email,
////                    onTimerAutoStopped = { currentTitle: String, newCurrentTool: CurrentTool, newCurrentToolState: CurrentToolState, newTimerCurrentDuration: Duration ->
////                    onTimerAutoStopped = { newTimerCurrentDuration: Duration ->
//                    onTimerAutoStopped = { createdWiD: WiD ->
////                        onTimerAutoStopped(currentTitle, newCurrentTool, newCurrentToolState, newTimerCurrentDuration)
//                        onTimerAutoStopped(createdWiD)
//                    },
////                    onWiDCreated = { createdWiD: WiD ->
////                        onWiDCreated(createdWiD)
////                    }
//                )
//            }
//        }
//    }
//
//    fun pauseTimer(
//        email: String,
//        onTimerPaused: (
////            currentTitle: String, // 제목 카운트, 소요 시간 맵 갱신 용
////            newCurrentToolState: CurrentToolState,
////            newTimerCurrentDuration: Duration,
////            newTimerNextSelectedTime: Duration,
//            newWiD: WiD
//        ) -> Unit,
////        onWiDCreated: (newWiD: WiD) -> Unit
//    ) {
//        Log.d(TAG, "pauseTimer executed")
//
//        _currentToolState.value = CurrentToolState.PAUSED
//
//        timer?.cancel()
//
//        _selectedTime.value = _remainingTime.value
//
//        if (_start.value.equals(_finish.value)) {
//            return
//        }
//
//        if (_start.value.isBefore(_finish.value)) {
//            // 1분 미만의 WiD는 생성 안됨.
////            if (duration.value < Duration.ofMinutes(1)) {
////                return
////            }
//
//            val duration = Duration.between(_start.value, _finish.value)
//            val newWiD = WiD(
//                id = "null",
//                date = _date.value,
//                title = _title.value,
//                start = _start.value,
//                finish = _finish.value,
//                duration = duration,
//                createdBy = CurrentTool.TIMER
//            )
//
////            onTimerPaused(_title.value, CurrentToolState.PAUSED, duration, _selectedTime.value)
////            onTimerPaused(duration)
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = newWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = _start.value,
//                            finish = _finish.value,
//                            duration = duration,
//                            createdBy = CurrentTool.TIMER
//                        )
//                        onTimerPaused(createdWiD)
//                    }
//                }
//            )
//        } else {
//            val midnight = LocalTime.MIDNIGHT
//            val previousDate = _date.value.minusDays(1)
//            val firstWiDDuration = Duration.between(_start.value, midnight.plusSeconds(-1))
//            val secondWiDDuration = Duration.between(midnight, _finish.value)
//
//            // 1분 미만의 WiD는 생성 안됨.
//            if (firstWiDDuration + secondWiDDuration < Duration.ofMinutes(1)) {
//                return
//            }
//
//            val firstWiD = WiD(
//                id = "null",
//                date = previousDate,
//                title = _title.value,
//                start = _start.value,
//                finish = midnight.plusSeconds(-1),
//                duration = firstWiDDuration,
//                createdBy = CurrentTool.TIMER
//            )
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = firstWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = _start.value,
//                            finish = midnight.plusSeconds(-1),
//                            duration = firstWiDDuration,
//                            createdBy = CurrentTool.TIMER
//                        )
//                        onTimerPaused(createdWiD)
//                    }
//                }
//            )
//
//            val secondWiD = WiD(
//                id = "null",
//                date = _date.value,
//                title = _title.value,
//                start = midnight,
//                finish = _finish.value,
//                duration = secondWiDDuration,
//                createdBy = CurrentTool.TIMER
//            )
//
////            val totalDuration = firstWiDDuration.plus(secondWiDDuration)
////            onTimerPaused(totalDuration)
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = secondWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = midnight,
//                            finish = _finish.value,
//                            duration = secondWiDDuration,
//                            createdBy = CurrentTool.TIMER
//                        )
//                        onTimerPaused(createdWiD)
//                    }
//                }
//            )
//        }
//    }
//
//    fun stopTimer(
////        onTimerStopped: (
////            newCurrentTool: CurrentTool,
////            newCurrentToolState: CurrentToolState
////        ) -> Unit
//    ) {
//        Log.d(TAG, "stopTimer executed")
//
//        _currentTool.value = CurrentTool.NONE
//        _currentToolState.value = CurrentToolState.STOPPED
//
//        timer?.cancel()
//
////        onTimerStopped(CurrentTool.NONE, CurrentToolState.STOPPED)
//
//        _remainingTime.value = Duration.ZERO
//        _selectedTime.value = Duration.ZERO
//    }
//
//    private fun autoStopTimer(
//        email: String,
//        onTimerAutoStopped: (
////            currentTitle: String,
////            newCurrentTool: CurrentTool,
////            newCurrentToolState: CurrentToolState,
////            newTimerCurrentDuration: Duration,
//            newWiD: WiD
//        ) -> Unit,
////        onWiDCreated: (newWiD: WiD) -> Unit
//    ) {
//        Log.d(TAG, "autoStopTimer executed")
//
//        _currentTool.value = CurrentTool.NONE
//        _currentToolState.value = CurrentToolState.STOPPED
//
//        timer?.cancel()
//
//        if (_start.value.equals(_finish.value)) {
//            return
//        }
//
//        if (_start.value.isBefore(_finish.value)) {
//            // 1분 미만의 WiD는 생성 안됨.
////            if (duration.value < Duration.ofMinutes(1)) {
////                return
////            }
//
//            val duration = Duration.between(_start.value, _finish.value)
//            val newWiD = WiD(
//                id = "null",
//                date = _date.value,
//                title = _title.value,
//                start = _start.value,
//                finish = _finish.value,
//                duration = duration,
//                createdBy = CurrentTool.TIMER
//            )
//
////            onTimerAutoStopped(_title.value, CurrentTool.NONE, CurrentToolState.PAUSED, duration)
////            onTimerAutoStopped(duration)
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = newWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = _start.value,
//                            finish = _finish.value,
//                            duration = duration,
//                            createdBy = CurrentTool.TIMER
//                        )
//                        onTimerAutoStopped(createdWiD)
//                    }
//                }
//            )
//        } else {
//            val midnight = LocalTime.MIDNIGHT
//            val previousDate = _date.value.minusDays(1)
//            val firstWiDDuration = Duration.between(_start.value, midnight.plusSeconds(-1))
//            val secondWiDDuration = Duration.between(midnight, _finish.value)
//
//            // 1분 미만의 WiD는 생성 안됨.
//            if (firstWiDDuration + secondWiDDuration < Duration.ofMinutes(1)) {
//                return
//            }
//
//            val firstWiD = WiD(
//                id = "null",
//                date = previousDate,
//                title = _title.value,
//                start = _start.value,
//                finish = midnight.plusSeconds(-1),
//                duration = firstWiDDuration,
//                createdBy = CurrentTool.TIMER
//            )
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = firstWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = _start.value,
//                            finish = midnight.plusSeconds(-1),
//                            duration = firstWiDDuration,
//                            createdBy = CurrentTool.TIMER
//                        )
//                        onTimerAutoStopped(createdWiD)
//                    }
//                }
//            )
//
//            val secondWiD = WiD(
//                id = "null",
//                date = _date.value,
//                title = _title.value,
//                start = midnight,
//                finish = _finish.value,
//                duration = secondWiDDuration,
//                createdBy = CurrentTool.TIMER
//            )
//
////            val totalDuration = firstWiDDuration.plus(secondWiDDuration)
////            onTimerAutoStopped(_title.value, CurrentTool.NONE, CurrentToolState.PAUSED, totalDuration)
////            onTimerAutoStopped(totalDuration)
//
//            wiDRepository.createWiD(
//                email = email,
//                wid = secondWiD,
//                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
//                    if (wiDCreated) {
//                        val createdWiD = WiD(
//                            id = createdDocumentID,
//                            date = _date.value,
//                            title = _title.value,
//                            start = midnight,
//                            finish = _finish.value,
//                            duration = secondWiDDuration,
//                            createdBy = CurrentTool.TIMER
//                        )
//                        onTimerAutoStopped(createdWiD)
//                    }
//                }
//            )
//        }
//
//        _remainingTime.value = Duration.ZERO
//        _selectedTime.value = Duration.ZERO
//    }
//}