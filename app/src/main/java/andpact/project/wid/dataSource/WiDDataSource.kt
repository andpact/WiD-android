package andpact.project.wid.dataSource

import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import andpact.project.wid.util.titleList
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
 * wiDListMap만 가지고 있고, 각 뷰 모델에 wiDList를 뿌려주는 용
 */
class WiDDataSource @Inject constructor(private val wiDRepository: WiDRepository) {
    private val TAG = "WiDDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    // New WiD View, New WiD View Model, WiD View, WiD View Model에서 같은 "오늘"을 공유하기 위함
    private val _today: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    val today: State<LocalDate> = _today

    // WiD
    private val _dateToWiDListMap = mutableStateOf<Map<LocalDate, List<WiD>>>(emptyMap())

    // 도구
    private val _currentTool: MutableState<CurrentTool> = mutableStateOf(CurrentTool.NONE)
    val currentTool: State<CurrentTool> = _currentTool
    private val _currentToolState: MutableState<CurrentToolState> = mutableStateOf(CurrentToolState.STOPPED)
    val currentToolState: State<CurrentToolState> = _currentToolState

    /** 위드 객체로 만들기? */
    // Current WiD(Tool)
    private var currentWiDTimer: Timer? = null
    private val _date: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    val date: State<LocalDate> = _date
    private val _title: MutableState<String> = mutableStateOf(titleList[0])
    val title: State<String> = _title // 스톱워치, 타이머 제목 공유함
    private val _start: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    val start: State<LocalTime> = _start
    private val _finish: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    val finish: State<LocalTime> = _finish
//    private val _currentWiD = mutableStateOf<WiD?>(null)
//    val currentWiD: State<WiD?> = _currentWiD

    // 스톱 워치
    private val _totalDuration = mutableStateOf(Duration.ZERO) // accumulatedPrevDuration + currentDuration
    val totalDuration: State<Duration> = _totalDuration
    private var accumulatedPrevDuration: Duration = Duration.ZERO

    // 타이머
    private val _remainingTime = mutableStateOf(Duration.ZERO)
    val remainingTime: State<Duration> = _remainingTime
    private val _selectedTime = mutableStateOf(Duration.ZERO)
    val selectedTime: State<Duration> = _selectedTime

    // NewWiD View
    private val _newWiD = mutableStateOf(createDefaultWiD()) // 수정 전
    val newWiD: State<WiD> = _newWiD
    private val _updatedNewWiD = mutableStateOf(createDefaultWiD()) // 수정 후
    val updatedNewWiD: State<WiD> = _updatedNewWiD

    // WiD View
    private var _wiD = mutableStateOf(createDefaultWiD()) // 수정 전
    val wiD: State<WiD> = _wiD
    private var _updatedWiD = mutableStateOf(createDefaultWiD()) // 수정 후
    val updatedWiD: State<WiD> = _updatedWiD

    fun setToday(newDate: LocalDate) {
        Log.d(TAG, "setToday executed")

        _today.value = newDate
    }

    /** 기본 위드 생성하지 말고, null 허용으로 변경하기 */
    private fun createDefaultWiD(): WiD {
        return WiD(
            id = "",
            date = LocalDate.now(),
            title = "기록 없음",
            start = LocalTime.MIN,
            finish = LocalTime.MIN,
            duration = Duration.ZERO,
            createdBy = CurrentTool.LIST
        )
    }

    private fun createCurrentWiD() {
        Log.d(TAG, "createCurrentWiD executed")

        if (_start.value == _finish.value) {
            return
        } else if (_start.value.isBefore(_finish.value)) {
            val newCurrentWiD = WiD(
                id = "currentWiD", // 고유 ID 유지
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = Duration.between(_start.value, _finish.value),
                createdBy = _currentTool.value
            )

            setCurrentWiDToMap(newCurrentWiD = newCurrentWiD)
        } else { // 자정을 넘어가면 두 개의 WiD로 분리해서 처리(date에 해당하는 WiD는 갱신할 필요가 없음.)
            val minTime = LocalTime.MIN
            val newCurrentWiDForNextDate = WiD(
                id = "currentWiD",
                date = _date.value.plusDays(1),
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = Duration.between(minTime, _finish.value),
                createdBy = _currentTool.value
            )

            setCurrentWiDToMap(newCurrentWiD = newCurrentWiDForNextDate)
        }
    }

    private fun setCurrentWiDToMap(newCurrentWiD: WiD) {
        Log.d(TAG, "setCurrentWiDToMap executed")

        // WiD의 날짜를 가져옴
        val wiDDate = newCurrentWiD.date

        // 현재 WiD 리스트를 가져옴
        val currentMap = _dateToWiDListMap.value.toMutableMap()
        val currentWiDList = currentMap[wiDDate]?.toMutableList() ?: mutableListOf()

        // 동일한 ID의 WiD가 이미 있는 경우 기존 WiD를 대체
        val wiDIndex = currentWiDList.indexOfFirst { it.id == newCurrentWiD.id }

        if (wiDIndex != -1) { // 기존 WiD를 새로운 WiD로 대체
            currentWiDList[wiDIndex] = newCurrentWiD
        } else { // 새로운 WiD 추가
            currentWiDList.add(newCurrentWiD)
        }

        // 갱신된 리스트를 Map에 다시 추가
        currentMap[wiDDate] = currentWiDList
        _dateToWiDListMap.value = currentMap
    }

    // 도구 중지 했을 때
    private fun replaceCurrentWiDWithCreatedWiD(createdWiD: WiD) {
        Log.d(TAG, "replaceCurrentWiDWithCreatedWiD executed")

        // 생성된 WiD의 날짜를 가져옴
        val wiDDate = createdWiD.date

        // 현재 WiD 리스트를 가져옴
        val currentMap = _dateToWiDListMap.value.toMutableMap()
        val currentWiDList = currentMap[wiDDate]?.toMutableList() ?: mutableListOf()

        // ID가 "currentWiD"인 WiD를 찾아 대체
        val currentWiDIndex = currentWiDList.indexOfFirst { it.id == "currentWiD" }

        if (currentWiDIndex != -1) {
            // "currentWiD"를 발견하면 새로운 WiD로 대체
            currentWiDList[currentWiDIndex] = createdWiD
        } else {
            // "currentWiD"가 없으면 새로운 WiD를 리스트에 추가
            currentWiDList.add(createdWiD)
        }

        // 업데이트된 WiD 리스트를 맵에 다시 삽입
        currentMap[wiDDate] = currentWiDList

        // _dateToWiDListMap을 갱신
        _dateToWiDListMap.value = currentMap
    }

    // 도구 정지 했을 때
    private fun removeCurrentWiDFromMapOnCurrentDate() {
        Log.d(TAG, "removeCurrentWiDFromMapOnCurrentDate executed")

        val currentDate = _date.value
        val currentMap = _dateToWiDListMap.value.toMutableMap()

        // 현재 날짜의 WiD 리스트 가져오기
        val currentWiDList = currentMap[currentDate]?.toMutableList() ?: mutableListOf()

        // 해당 WiD 삭제 (ID를 currentWiD로 설정한 경우)
        val wiDIndex = currentWiDList.indexOfFirst { it.id == "currentWiD" }
        if (wiDIndex != -1) {
            currentWiDList.removeAt(wiDIndex)
            currentMap[currentDate] = currentWiDList
        }

        // 갱신된 Map을 업데이트
        _dateToWiDListMap.value = currentMap
    }

    // 도구 정지 했을 때
    private fun removeCurrentWiDFromMapOnNextDate() {
        Log.d(TAG, "removeCurrentWiDFromMapOnNextDate executed")

        val nextDate = _date.value.plusDays(1)
        val currentMap = _dateToWiDListMap.value.toMutableMap()

        // 자정 후 WiD 삭제
        val nextWiDList = currentMap[nextDate]?.toMutableList() ?: mutableListOf()
        val minTime = LocalTime.MIN

        // 해당 WiD 삭제 (ID를 currentWiD로 설정한 경우)
        val secondWiDIndex = nextWiDList.indexOfFirst { it.id == "currentWiD" && it.start == minTime }
        if (secondWiDIndex != -1) {
            nextWiDList.removeAt(secondWiDIndex)
            currentMap[nextDate] = nextWiDList
        }

        // 갱신된 Map을 업데이트
        _dateToWiDListMap.value = currentMap
    }

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        _title.value = newTitle
    }

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        _currentTool.value = CurrentTool.STOPWATCH
        _currentToolState.value = CurrentToolState.STARTED

        currentWiDTimer?.cancel()

        _date.value = LocalDate.now()
        _start.value = LocalTime.now().withNano(0)

        currentWiDTimer = timer(period = 1_000) {
            _finish.value = LocalTime.now().withNano(0)

            createCurrentWiD()

            /** 12시간 넘으면 자동 종료되도록 해야함!!!!!! */
            if (_start.value.equals(_finish.value) || _start.value.isBefore(_finish.value)) {
                _totalDuration.value = accumulatedPrevDuration + Duration.between(_start.value, _finish.value)
            } else {
                _totalDuration.value = accumulatedPrevDuration + Duration.between(_start.value, LocalTime.MAX.withNano(0)) + Duration.between(LocalTime.MIN, _finish.value)
            }
        }
    }

    fun pauseStopwatch(
        email: String,
        onStopwatchPaused: (newWiD: WiD) -> Unit,
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        _currentToolState.value = CurrentToolState.PAUSED

        accumulatedPrevDuration = _totalDuration.value

        currentWiDTimer?.cancel()

        if (_start.value.equals(_finish.value)) {
            return
        } else if (_start.value.isBefore(_finish.value)) {
            val duration = Duration.between(_start.value, _finish.value)
//            if (duration < Duration.ofMinutes(1)) { // 1분 미만의 WiD는 생성 안됨.
//                return
//            }

            val newWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = duration,
                createdBy = CurrentTool.STOPWATCH
            )

            wiDRepository.createWiD(
                email = email,
                wid = newWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = _start.value,
                            finish = _finish.value,
                            duration = duration,
                            createdBy = CurrentTool.STOPWATCH
                        )

                        onStopwatchPaused(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )
        } else { // 자정 넘어가는 경우
            val previousDate = _date.value.minusDays(1) /** 이거 맞는지 제대로 확인!!!!! */
            val minTime = LocalTime.MIN
            val maxTime = LocalTime.MAX.withNano(0)
            val firstWiDDuration = Duration.between(_start.value, maxTime)
            val secondWiDDuration = Duration.between(minTime, _finish.value)

            // 1분 미만의 WiD는 생성 안됨.
            if (firstWiDDuration + secondWiDDuration < Duration.ofMinutes(1)) {
                return
            }

            val firstWiD = WiD(
                id = "",
                date = previousDate,
                title = _title.value,
                start = _start.value,
                finish = maxTime,
                duration = firstWiDDuration,
                createdBy = CurrentTool.STOPWATCH
            )

            wiDRepository.createWiD(
                email = email,
                wid = firstWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = _start.value,
                            finish = maxTime,
                            duration = firstWiDDuration,
                            createdBy = CurrentTool.STOPWATCH
                        )
                        onStopwatchPaused(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )

            val secondWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = secondWiDDuration,
                createdBy = CurrentTool.STOPWATCH
            )

            wiDRepository.createWiD(
                email = email,
                wid = secondWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = minTime,
                            finish = _finish.value,
                            duration = secondWiDDuration,
                            createdBy = CurrentTool.STOPWATCH
                        )
                        onStopwatchPaused(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )
        }
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        _currentTool.value = CurrentTool.NONE
        _currentToolState.value = CurrentToolState.STOPPED

        currentWiDTimer?.cancel()

        removeCurrentWiDFromMapOnCurrentDate()
        removeCurrentWiDFromMapOnNextDate()

        _totalDuration.value = Duration.ZERO
        accumulatedPrevDuration = Duration.ZERO
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        _selectedTime.value = newSelectedTime
    }

    fun startTimer(
        email: String, // 자동 종료 용 콜백
        onTimerAutoStopped: (newWiD: WiD) -> Unit,
    ) {
        Log.d(TAG, "startTimer executed")

        _currentTool.value = CurrentTool.TIMER
        _currentToolState.value = CurrentToolState.STARTED

        currentWiDTimer?.cancel()

        _date.value = LocalDate.now()
        _start.value = LocalTime.now().withNano(0)

        currentWiDTimer = timer(period = 1_000) {
            _finish.value = LocalTime.now().withNano(0)

            createCurrentWiD()

            _remainingTime.value = _selectedTime.value - Duration.between(_start.value, _finish.value)

            if (_remainingTime.value <= Duration.ZERO) {
                autoStopTimer(
                    email = email,
                    onTimerAutoStopped = { createdWiD: WiD ->
                        onTimerAutoStopped(createdWiD)
                    },
                )
            }
        }
    }

    fun pauseTimer(
        email: String,
        onTimerPaused: (newWiD: WiD) -> Unit,
    ) {
        Log.d(TAG, "pauseTimer executed")

        _currentToolState.value = CurrentToolState.PAUSED

        currentWiDTimer?.cancel()

        _selectedTime.value = _remainingTime.value

        if (_start.value.equals(_finish.value)) {
            return
        } else if (_start.value.isBefore(_finish.value)) {
            // 1분 미만의 WiD는 생성 안됨.
            val duration = Duration.between(_start.value, _finish.value)
//            if (duration < Duration.ofMinutes(1)) {
//                return
//            }

            val newWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = duration,
                createdBy = CurrentTool.TIMER
            )

            wiDRepository.createWiD(
                email = email,
                wid = newWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = _start.value,
                            finish = _finish.value,
                            duration = duration,
                            createdBy = CurrentTool.TIMER
                        )
                        onTimerPaused(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )
        } else {
            val previousDate = _date.value.minusDays(1) // 왜 얘를 마이너스 해야 정상 작동이 되지?
            val minTime = LocalTime.MIN
            val maxTime = LocalTime.MAX.withNano(0)
            val firstWiDDuration = Duration.between(_start.value, maxTime)
            val secondWiDDuration = Duration.between(minTime, _finish.value)

            // 1분 미만의 WiD는 생성 안됨.
            if (firstWiDDuration + secondWiDDuration < Duration.ofMinutes(1)) {
                return
            }

            val firstWiD = WiD(
                id = "",
                date = previousDate,
                title = _title.value,
                start = _start.value,
                finish = maxTime,
                duration = firstWiDDuration,
                createdBy = CurrentTool.TIMER
            )

            wiDRepository.createWiD(
                email = email,
                wid = firstWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = _start.value,
                            finish = maxTime,
                            duration = firstWiDDuration,
                            createdBy = CurrentTool.TIMER
                        )
                        onTimerPaused(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )

            val secondWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = secondWiDDuration,
                createdBy = CurrentTool.TIMER
            )

            wiDRepository.createWiD(
                email = email,
                wid = secondWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = minTime,
                            finish = _finish.value,
                            duration = secondWiDDuration,
                            createdBy = CurrentTool.TIMER
                        )
                        onTimerPaused(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )
        }
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        _currentTool.value = CurrentTool.NONE
        _currentToolState.value = CurrentToolState.STOPPED

        currentWiDTimer?.cancel()

        removeCurrentWiDFromMapOnCurrentDate()
        removeCurrentWiDFromMapOnNextDate()

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }

    private fun autoStopTimer(
        email: String,
        onTimerAutoStopped: (newWiD: WiD) -> Unit,
    ) {
        Log.d(TAG, "autoStopTimer executed")

        _currentTool.value = CurrentTool.NONE
        _currentToolState.value = CurrentToolState.STOPPED

        currentWiDTimer?.cancel()

        if (_start.value.equals(_finish.value)) {
            return
        } else if (_start.value.isBefore(_finish.value)) {
            // 1분 미만의 WiD는 생성 안됨.
            val duration = Duration.between(_start.value, _finish.value)
//            if (duration < Duration.ofMinutes(1)) {
//                return
//            }

            val newWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = duration,
                createdBy = CurrentTool.TIMER
            )

            wiDRepository.createWiD(
                email = email,
                wid = newWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = _start.value,
                            finish = _finish.value,
                            duration = duration,
                            createdBy = CurrentTool.TIMER
                        )
                        onTimerAutoStopped(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )
        } else {
            val previousDate = _date.value.minusDays(1) // 왜 얘를 마이너스 해야 정상 작동이 되지?
            val minTime = LocalTime.MIN
            val maxTime = LocalTime.MAX.withNano(0)
            val firstWiDDuration = Duration.between(_start.value, maxTime)
            val secondWiDDuration = Duration.between(minTime, _finish.value)

            // 1분 미만의 WiD는 생성 안됨.
            if (firstWiDDuration + secondWiDDuration < Duration.ofMinutes(1)) {
                return
            }

            val firstWiD = WiD(
                id = "",
                date = previousDate,
                title = _title.value,
                start = _start.value,
                finish = maxTime,
                duration = firstWiDDuration,
                createdBy = CurrentTool.TIMER
            )

            wiDRepository.createWiD(
                email = email,
                wid = firstWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = _start.value,
                            finish = maxTime,
                            duration = firstWiDDuration,
                            createdBy = CurrentTool.TIMER
                        )
                        onTimerAutoStopped(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )

            val secondWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = secondWiDDuration,
                createdBy = CurrentTool.TIMER
            )

            wiDRepository.createWiD(
                email = email,
                wid = secondWiD,
                onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                    if (wiDCreated) {
                        val createdWiD = WiD(
                            id = createdDocumentID,
                            date = _date.value,
                            title = _title.value,
                            start = minTime,
                            finish = _finish.value,
                            duration = secondWiDDuration,
                            createdBy = CurrentTool.TIMER
                        )
                        onTimerAutoStopped(createdWiD)
                        replaceCurrentWiDWithCreatedWiD(createdWiD = createdWiD)
                    }
                }
            )
        }

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }

    fun createWiD(
        email: String,
        onWiDCreated: (wiDCreated: Boolean) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        wiDRepository.createWiD(
            email = email,
            wid = _newWiD.value,
            onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                if (wiDCreated) {
                    val createdWiD = WiD(
                        id = createdDocumentID,
                        date = _newWiD.value.date,
                        title = _newWiD.value.title,
                        start = _newWiD.value.start,
                        finish = _newWiD.value.finish,
                        duration = _newWiD.value.duration,
                        createdBy = _newWiD.value.createdBy
                    )

                    addCreatedWiDToMap(createdWiD = createdWiD)
                }

                onWiDCreated(wiDCreated)
            }
        )
    }

    private fun addCreatedWiDToMap(createdWiD: WiD) {
        Log.d(TAG, "addCreatedWiDtoMap executed")

        val currentMap = _dateToWiDListMap.value.toMutableMap()
        val currentList = currentMap[createdWiD.date]?.toMutableList() ?: mutableListOf()

        currentList.add(createdWiD)

        val sortedList = currentList.sortedBy { it.start }

        currentMap[createdWiD.date] = sortedList

        _dateToWiDListMap.value = currentMap
    }

    fun getWiDListOfDate(
        email: String,
        collectionDate: LocalDate,
        onWiDListFetchedByDate: (List<WiD>) -> Unit
    ) {
        // 다른 클라이언트에서 위드를 추가한 상태에서, 캐싱 맵의 위드 리스트를 사용하면 동기화가 안될 수 있음
        val existingWiDList = _dateToWiDListMap.value[collectionDate]

        if (existingWiDList != null) { // 캐시된 WiDList가 있을 때(key를 확인함)
            Log.d(TAG, "getWiDListOfDate executed FROM CLIENT")

            onWiDListFetchedByDate(existingWiDList)
        } else { // 캐시된 WiDList가 없을 때(처음 조회할 때, key + value 추가됨)
            wiDRepository.getWiDListByDate(
                email = email,
                collectionDate = collectionDate,
                onWiDListFetchedByDate = { wiDList: List<WiD> ->
                    Log.d(TAG, "getWiDListOfDate executed FROM SERVER")

//                    _dateToWiDListMap.value += (collectionDate to wiDList)
//                    onWiDListFetchedByDate(wiDList)

                    /** 임시(한 번 밖에 실행 안됨 결국에) */
                    val tmpWiD = WiD(
                        id = "tmpWiD",
                        date = collectionDate,
                        title = "1",
                        start = LocalTime.of(2, 30),
                        finish = LocalTime.of(3, 30),
                        duration = Duration.ofHours(1),
                        createdBy = CurrentTool.LIST
                    )

                    val updatedWiDList = if (collectionDate == LocalDate.now()) {
                        wiDList + tmpWiD
                    } else {
                        wiDList
                    }

                    _dateToWiDListMap.value += (collectionDate to updatedWiDList)
                    onWiDListFetchedByDate(updatedWiDList)
                }
            )
        }
    }

    fun getWiDListFromFirstDateToLastDate(
        email: String,
        firstDate: LocalDate,
        lastDate: LocalDate,
        onWiDListFetchedFromFirstDateToLastDate: (List<WiD>) -> Unit
    ) {
        Log.d(TAG, "getWiDListFromFirstDateToLastDate executed")

        val resultList = mutableListOf<WiD>()
        var currentDate = firstDate

        while (currentDate <= lastDate) {
            val existingWiDList = _dateToWiDListMap.value[currentDate]

            if (existingWiDList != null) {
                // 캐시된 WiDList가 있는 경우 결과 리스트에 추가합니다.
                resultList.addAll(existingWiDList)
            } else {
                // 캐시된 WiDList가 없는 경우 wiDRepository를 통해 데이터를 가져와서 결과 리스트에 추가합니다.
                wiDRepository.getWiDListByDate(
                    email = email,
                    collectionDate = currentDate,
                    onWiDListFetchedByDate = { wiDList: List<WiD> ->
                        _dateToWiDListMap.value += (currentDate to wiDList)
                        resultList.addAll(wiDList)
                    }
                )
            }

            // 다음 날짜로 이동합니다.
            currentDate = currentDate.plusDays(1)
        }

        // 모든 날짜에 대한 처리가 끝나면 콜백을 호출합니다.
        onWiDListFetchedFromFirstDateToLastDate(resultList)
    }

    fun setNewWiD(
        newWiD: WiD
    ) {
        Log.d(TAG, "setNewWiD executed")

        _newWiD.value = newWiD
    }

    fun setUpdatedNewWiD(updatedNewWiD: WiD) {
        Log.d(TAG, "setUpdatedNewWiD executed")

        _updatedNewWiD.value = updatedNewWiD
    }

    fun setWiD(wiD: WiD) {
        Log.d(TAG, "setWiD executed")

        _wiD.value = wiD
    }

    fun setUpdatedWiD(updatedWiD: WiD) {
        Log.d(TAG, "setUpdatedWiD executed")

        _updatedWiD.value = updatedWiD
    }

    fun updateWiD(
        email: String,
        onWiDUpdated: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        wiDRepository.updateWiD(
            email = email,
            updatedWiD = _updatedWiD.value,
            onWiDUpdated = { wiDUpdated: Boolean ->
                onWiDUpdated(wiDUpdated)

                // WiD 리스트 맵에서 기존 WiD 업데이트
                val existingWiDDate = _wiD.value.date
                val existingWiDID = _wiD.value.id

                val currentMap = _dateToWiDListMap.value.toMutableMap()
                val currentList = currentMap[existingWiDDate]?.toMutableList()

                currentList?.let { list ->
                    val wiDIndex = list.indexOfFirst { it.id == existingWiDID }

                    if (wiDIndex != -1) {
                        // 기존 WiD를 업데이트된 WiD로 교체
                        list[wiDIndex] = _updatedWiD.value
                    }

                    currentMap[existingWiDDate] = list
                    _dateToWiDListMap.value = currentMap
                }
            }
        )
    }

    fun deleteWiD(
        email: String,
        onWiDDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        wiDRepository.deleteWiD(
            email = email,
            wiD = _wiD.value, // existingWiD 사용해도 되고, updatedWiD 사용해도 됨. id는 동일하니.
            onWiDDeleted = { wiDDeleted: Boolean ->
                onWiDDeleted(wiDDeleted)

                val clickedWiDDate = _wiD.value.date
                val clickedWiDID = _wiD.value.id

                val currentMap = _dateToWiDListMap.value.toMutableMap()
                val currentList = currentMap[clickedWiDDate]?.toMutableList()

                currentList?.removeIf { it.id == clickedWiDID }

                currentMap[clickedWiDDate] = currentList.orEmpty()

                _dateToWiDListMap.value = currentMap
            }
        )
    }
}