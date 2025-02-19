package andpact.project.wid.dataSource

import andpact.project.wid.model.*
import andpact.project.wid.repository.WiDRepository
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

/*
- 데이터 조회할 때(서버에 데이터가 없으면 굳이 클라에 날짜 맵을 만들 필요는 없음)
년도 키가 null이면 서버 호출 안한 것
날짜 키가 null이면 서버에 데이터가 없는 것

DS 단에서는 null을 가져도 됨.
VM 단에서는 null을 못 가지도록
 */
class WiDDataSource @Inject constructor(private val wiDRepository: WiDRepository) {
    private val TAG = "WiDDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }
    private val LAST_NEW_WID_TIMER = "lastNewWiDTimer"
    private val CURRENT_WID_TIMER = "currentWiDTimer"
    val NEW_WID = "newWiD"
    val LAST_NEW_WID = "lastNewWiD"
    val CURRENT_WID = "currentWiD"

    val START = wiDRepository.START
    val FINISH = wiDRepository.FINISH

    val WID_LIST_LIMIT_PER_DAY = 24 // TODO: 몇 개로 제한할지 정하기

    private var timer: Timer? = null
    private val _now: MutableState<LocalDateTime> = mutableStateOf(LocalDateTime.now().withNano(0))
    val now: State<LocalDateTime> = _now

    private val _yearDateWiDListMap = mutableStateOf<Map<Year, Map<LocalDate, List<WiD>>>>(emptyMap())
    val yearDateWiDListMap: State<Map<Year, Map<LocalDate, List<WiD>>>> = _yearDateWiDListMap

    // Current WiD
    private val _currentWiD = mutableStateOf(WiD.default().copy(id = CURRENT_WID, title = Title.STUDY, subTitle = SubTitle.UNSELECTED_STUDY))
    val currentWiD: State<WiD> = _currentWiD

    private val _playerState: MutableState<PlayerState> = mutableStateOf(PlayerState.STOPPED)
    val playerState: State<PlayerState> = _playerState

    private var prevDuration: Duration = Duration.ZERO // 기록의 누적 시간

    // 스톱 워치
    private val _totalDuration = mutableStateOf(Duration.ZERO) // 화면에 표시
    val totalDuration: State<Duration> = _totalDuration

    // 타이머
    private val _selectedTime = mutableStateOf(Duration.ZERO) // 화면에 표시
    val selectedTime: State<Duration> = _selectedTime
    private val _remainingTime = mutableStateOf(Duration.ZERO) // 화면에 표시
    val remainingTime: State<Duration> = _remainingTime

    // WiD View - 마지막 기록일 수도 있고 마지막 전 기록일 수도 있어서 종료 또는 소요 시간을 갱신할 수 있음
    private var _updateClickedWiDFinishToNow = mutableStateOf(false)
    val updateClickedWiDFinishToNow: State<Boolean> = _updateClickedWiDFinishToNow
    private var _clickedWiD = mutableStateOf(WiD.default()) // 수정 전(수정에 사용되면 안됨)
    val clickedWiD: State<WiD> = _clickedWiD
    private var _updateClickedWiDCopyStartToNowMinus12Hours = mutableStateOf(false)
    val updateClickedWiDCopyStartToNowMinus12Hours: State<Boolean> = _updateClickedWiDCopyStartToNowMinus12Hours
    private var _updateClickedWiDCopyFinishToNow = mutableStateOf(false)
    val updateClickedWiDCopyFinishToNow: State<Boolean> = _updateClickedWiDCopyFinishToNow
    private var _clickedWiDCopy = mutableStateOf(WiD.default()) // 수정 후
    val clickedWiDCopy: State<WiD> = _clickedWiDCopy

    fun setUpdateClickedWiDFinishToNow(update: Boolean) { // 마지막 새 기록 클릭 시
        Log.d(TAG, "setUpdateClickedWiDFinishToNow executed")

        _updateClickedWiDFinishToNow.value = update
    }

    fun setUpdateClickedWiDCopyStartToNowMinus12Hours(update: Boolean) { // 마지막 새 기록 클릭 시, 마지막 기록의 시작 시간 갱신 용
        Log.d(TAG, "setUpdateClickedWiDCopyStartToNowMinus12Hours executed")

        _updateClickedWiDCopyStartToNowMinus12Hours.value = update
    }
    
    fun setUpdateClickedWiDCopyFinishToNow(update: Boolean) { // 마지막 새 기록 클릭 시, 마지막 기록의 종료 시간 갱신 용
        Log.d(TAG, "setUpdateClickedWiDCopyFinishToNow executed")

        _updateClickedWiDCopyFinishToNow.value = update
    }

    fun startLastNewWiDTimer() {
        Log.d(TAG, "startLastNewWiDTimer executed")

        timer?.cancel()

        updateNow()

        timer = timer(
            name = LAST_NEW_WID_TIMER,
            period = 1_000,
            action = {
                val realTime = updateNow()
                
                val clickedWiD = _clickedWiD.value
                val clickedWiDCopy = _clickedWiDCopy.value

                // 조회 기록이 마지막 새 기록일 때
                if (_updateClickedWiDFinishToNow.value) {
                    _clickedWiD.value = clickedWiD.copy(
                        finish = realTime,
                        duration = Duration.between(clickedWiD.start, realTime)
                    )
                }

                // TODO: 12시간 뿐만 아니라 다른 시간 차이도 적용할 수 있는데? 
                if(_updateClickedWiDCopyFinishToNow.value) {
                    val realTimeMinus12hours = realTime.minusHours(12)
                    val newClickedWiDCopyStart = if (_updateClickedWiDCopyStartToNowMinus12Hours.value) realTimeMinus12hours else clickedWiDCopy.start
                    _clickedWiDCopy.value = clickedWiDCopy.copy(
                        start = newClickedWiDCopyStart, 
                        finish = realTime,
                        duration = Duration.between(newClickedWiDCopyStart, realTime)
                    )   
                }
                
//                if (_updateClickedWiDCopyFinishToNow.value) {
//                    _clickedWiDCopy.value = clickedWiDCopy.copy(
//                        finish = realTime,
//                        duration = Duration.between(clickedWiDCopy.start, realTime)
//                    )
//                }
            }
        )
    }

    private fun updateNow(): LocalDateTime {
        Log.d(TAG, "updateNow executed")

        val realTime = LocalDateTime.now().withNano(0)
        _now.value = realTime

        return realTime // 계산된 realTime 반환
    }

    fun createWiD(
        email: String,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        val newWiD = _clickedWiDCopy.value.copy(id = generateUniqueWiDId()) // 새로운 WiD ID 생성

        // 로컬 시간 기준으로 기록 저장할 날짜 결정
        val startDate = newWiD.start.toLocalDate()
        val finishDate = newWiD.finish.toLocalDate()

        val updatedYearMap = _yearDateWiDListMap.value.toMutableMap()

        // TODO: 시작 날짜의 시간이 23:59:59면? 추가할 필요가 없지 않나? 표시될 게 없는데?
        // 시작 날짜에 WiD 추가
        val startYear = Year.of(startDate.year)
        val startYearMap = updatedYearMap[startYear]?.toMutableMap() ?: mutableMapOf()
        val startDateWiDList = startYearMap[startDate]?.toMutableList() ?: mutableListOf()
        startDateWiDList.add(newWiD)
        startYearMap[startDate] = startDateWiDList
        updatedYearMap[startYear] = startYearMap

        // 종료 날짜가 다를 경우 종료 날짜에도 WiD 추가
        if (startDate != finishDate) {
            val finishYear = Year.of(finishDate.year)
            val finishYearMap = updatedYearMap[finishYear]?.toMutableMap() ?: mutableMapOf()
            val finishDateWiDList = finishYearMap[finishDate]?.toMutableList() ?: mutableListOf()
            finishDateWiDList.add(newWiD)
            finishYearMap[finishDate] = finishDateWiDList
            updatedYearMap[finishYear] = finishYearMap
        }

        // 서버에 WiD 저장 요청
//        wiDRepository.createWiD(
//            email = email,
//            wiD = newWiD,
//            onResult = { snackbarActionResult: SnackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_CREATE_WID) {
//                    _yearDateWiDListMap.value = updatedYearMap
//                }
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라이언트 코드 */
        _yearDateWiDListMap.value = updatedYearMap
        onResult(SnackbarActionResult.SUCCESS_CREATE_WID)
    }

    // TODO: 도구 화면에서 호출해야 기록 리스트 크기 확인 가능함.
    fun getWiD(
        email: String,
        year: Year,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "getWiD executed")

        if (_yearDateWiDListMap.value[year] == null) { // 년도 키가 없을 때만 서버 호출
//            wiDRepository.getWiD(
//                email = email,
//                year = year,
//                onDateWiDListMapFetched = { dateWiDListMap: Map<LocalDate, List<WiD>> ->
//                    _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
//                        this[year] = dateWiDListMap
//                    }
//                },
//                onResult = { snackbarActionResult: SnackbarActionResult ->
//                    if (snackbarActionResult == SnackbarActionResult.FAIL_SERVER_ERROR) {
//                        onResult(snackbarActionResult) // 서버 호출 실패
//                    }
//                }
//            )

            /** 클라이언트 코드 */
            _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
                this[year] = emptyMap()
            }
        }
    }

    fun updateWiD(
        email: String,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        val originalWiD = _clickedWiD.value
        val updatedWiD = _clickedWiDCopy.value

        val originalStartDate = originalWiD.start.toLocalDate()
        val originalFinishDate = originalWiD.finish.toLocalDate()

        val updatedStartDate = updatedWiD.start.toLocalDate()
        val updatedFinishDate = updatedWiD.finish.toLocalDate()

        val updatedYearMap = _yearDateWiDListMap.value.toMutableMap()

        // 원본 WiD 제거
        val originalYears = setOf(Year.of(originalStartDate.year), Year.of(originalFinishDate.year))
        for (year in originalYears) {
            val yearMap = updatedYearMap[year]?.toMutableMap() ?: continue
            listOf(originalStartDate, originalFinishDate).forEach { date ->
                val dateWiDList = yearMap[date]?.toMutableList() ?: return@forEach
                yearMap[date] = dateWiDList.filter { it.id != originalWiD.id } // WiD 삭제
            }
            updatedYearMap[year] = yearMap
        }

        // 수정본 WiD 추가
        val updatedYears = setOf(Year.of(updatedStartDate.year), Year.of(updatedFinishDate.year))
        for (year in updatedYears) {
            val yearMap = updatedYearMap[year]?.toMutableMap() ?: mutableMapOf()
            listOf(updatedStartDate, updatedFinishDate).forEach { date ->
                val dateWiDList = yearMap[date]?.toMutableList() ?: mutableListOf()
                dateWiDList.add(updatedWiD)
                yearMap[date] = dateWiDList
            }
            updatedYearMap[year] = yearMap
        }

        // 서버에 WiD 업데이트 요청
//        wiDRepository.updateWiD(
//            email = email,
//            wiD = updatedWiD,
//            onResult = { snackbarActionResult: SnackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_UPDATE_WID) {
//                    _yearDateWiDListMap.value = updatedYearMap
//                }
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라이언트 코드 */
        _yearDateWiDListMap.value = updatedYearMap
        onResult(SnackbarActionResult.SUCCESS_UPDATE_WID)
    }

    fun deleteWiD(
        email: String,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onWiDDeleted: (deletedExp: Int) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        val wiDToDelete = _clickedWiD.value

        val startDate = wiDToDelete.start.toLocalDate()
        val finishDate = wiDToDelete.finish.toLocalDate()

        val updatedYearMap = _yearDateWiDListMap.value.toMutableMap()

        // WiD 삭제 로직
        val yearsToUpdate = setOf(Year.of(startDate.year), Year.of(finishDate.year))
        for (year in yearsToUpdate) {
            val yearMap = updatedYearMap[year]?.toMutableMap() ?: continue
            listOf(startDate, finishDate).forEach { date ->
                val dateWiDList = yearMap[date]?.toMutableList() ?: return@forEach
                val updatedWiDList = dateWiDList.filter { it.id != wiDToDelete.id }

                if (updatedWiDList.isEmpty()) {
                    yearMap.remove(date) // WiD가 없으면 날짜 자체를 삭제
                } else {
                    yearMap[date] = updatedWiDList
                }
            }
            updatedYearMap[year] = yearMap
        }

        // 서버에서 WiD 삭제 요청
//        wiDRepository.deleteWiD(
//            email = email,
//            wiD = wiDToDelete,
//            onResult = { snackbarActionResult: SnackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_DELETE_WID) {
//                    _yearDateWiDListMap.value = updatedYearMap // 서버 성공 시 캐시 반영
//                    onWiDDeleted(wiDToDelete.exp)
//                }
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라이언트 코드 */
        _yearDateWiDListMap.value = updatedYearMap
        onResult(SnackbarActionResult.SUCCESS_DELETE_WID)
        onWiDDeleted(wiDToDelete.exp) // 몇 개가
    }

    private fun insertCurrentWiDToMap() {
        Log.d(TAG, "insertCurrentWiDToMap executed")

        val currentWiD = _currentWiD.value
        val startDate = currentWiD.start.toLocalDate()
        val finishDate = currentWiD.finish.toLocalDate()

        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()

        // startDate부터 finishDate까지 반복
        for (i in 0..ChronoUnit.DAYS.between(startDate, finishDate)) {
            val date = startDate.plusDays(i) // 날짜 증가
            val year = Year.of(date.year)

            // 연도별 기록 맵 가져오기 (없으면 새로 생성)
            val dateListMap = currentYearMap[year]?.toMutableMap() ?: mutableMapOf()

            // 날짜별 기록 리스트 가져오기 (없으면 새로 생성)
            val wiDList = dateListMap[date]?.toMutableList() ?: mutableListOf()

            // 기존 WiD가 존재하면 업데이트, 없으면 추가
            val wiDIndex = wiDList.indexOfFirst { it.id == currentWiD.id }
            if (wiDIndex != -1) {
                wiDList[wiDIndex] = currentWiD
            } else {
                wiDList.add(currentWiD)
            }

            // 갱신된 리스트를 맵에 반영
            dateListMap[date] = wiDList
            currentYearMap[year] = dateListMap
        }

        _yearDateWiDListMap.value = currentYearMap
    }

    private fun removeCurrentWiDFromMap() {
        Log.d(TAG, "removeCurrentWiDFromMap executed")

        val currentWiD = _currentWiD.value
        val startDate = currentWiD.start.toLocalDate()
        val finishDate = currentWiD.finish.toLocalDate()

        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()

        for (i in 0..ChronoUnit.DAYS.between(startDate, finishDate)) {
            val date = startDate.plusDays(i)
            val year = Year.of(date.year)

            val dateListMap = currentYearMap[year]?.toMutableMap() ?: continue
            val wiDList = dateListMap[date]?.toMutableList() ?: continue

            val wiDIndex = wiDList.indexOfFirst { it.id == currentWiD.id }
            if (wiDIndex == -1) continue

            wiDList.removeAt(wiDIndex)

            if (wiDList.isEmpty()) {
                dateListMap.remove(date)
            } else {
                dateListMap[date] = wiDList
            }

            currentYearMap[year] = dateListMap
        }

        _yearDateWiDListMap.value = currentYearMap
    }

    fun setCurrentWiDTitleAndSubTitle(newTitle: Title, newSubTitle: SubTitle) { // 도구에서만 실행됨
        Log.d(TAG, "setCurrentWiDTitleAndSubTitle executed")

        _currentWiD.value = _currentWiD.value.copy(title = newTitle, subTitle = newSubTitle)
    }

    private fun setCurrentWiDtoNow(currentTool: Tool) {
        Log.d(TAG, "setCurrentWiDtoNow executed")

        val now = LocalDateTime.now().withNano(0)
        _currentWiD.value = _currentWiD.value.copy(
            start = now, // 시작 시간 갱신
            finish = now, // 종료 시간도 갱신
            duration = Duration.ZERO, // 소요 시간도 갱신
            tool = currentTool // 도구 갱신
        )
    }

    fun startStopwatch(
        email: String,
        wiDMinLimit: Duration,
        wiDMaxLimit: Duration,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onStopwatchAutoPaused: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "startStopwatch executed")

        timer?.cancel() // 이전 타이머 캔슬
        _playerState.value = PlayerState.STARTED
        setCurrentWiDtoNow(currentTool = Tool.STOPWATCH)
        updateNow()

        timer = timer(
            name = CURRENT_WID_TIMER,
            period = 1_000,
            action = {
                if (wiDMaxLimit <= _totalDuration.value) { // 최대 시간 초과
                    pauseStopwatch(
                        email = email,
                        wiDMinLimit = wiDMinLimit,
                        onResult = { snackbarActionResult: SnackbarActionResult ->
                            onResult(snackbarActionResult)
                        },
                        onStopwatchPaused = { newExp: Int ->
                            onStopwatchAutoPaused(newExp)
                        }
                    )

                    stopStopwatch()
                }

                val realTime = updateNow()
                val newCurrentWiDDuration = Duration.between(_currentWiD.value.start, realTime)

                _currentWiD.value = _currentWiD.value.copy(
                    finish = realTime, // 종료 시간 갱신
                    duration = newCurrentWiDDuration, // 소요 시간 갱신
                    exp = newCurrentWiDDuration.seconds.toInt() // 경험치 갱신
                )

                _totalDuration.value = prevDuration + newCurrentWiDDuration

                insertCurrentWiDToMap()
            }
        )
    }

    fun pauseStopwatch(
        email: String,
        wiDMinLimit: Duration,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onStopwatchPaused: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        startLastNewWiDTimer()
        _playerState.value = PlayerState.PAUSED
        prevDuration = _totalDuration.value

        val currentWiD = _currentWiD.value

        // 기록이 최소 제한 시간보다 짧으면 중단
        if (currentWiD.duration < wiDMinLimit) {
            onResult(SnackbarActionResult.FAIL_TIME_LIMIT)
            return
        }

        // 랜덤 ID 부여
        val newWiD = currentWiD.copy(id = generateUniqueWiDId())

        // 서버로 WiD 전송
//        wiDRepository.createWiD(
//            email = email,
//            wiD = newWiD,
//            onResult = { snackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_CREATE_WID) {
//                    val startDate = newWiD.start.toLocalDate()
//                    val finishDate = newWiD.finish.toLocalDate()
//
//                    val currentYearMap = _yearDateWiDListMap.value.toMutableMap()
//
//                    for (i in 0..ChronoUnit.DAYS.between(startDate, finishDate)) {
//                        val date = startDate.plusDays(i)
//                        val year = Year.of(date.year)
//                        val dateListMap = currentYearMap[year]?.toMutableMap() ?: mutableMapOf()
//                        val wiDList = dateListMap[date]?.toMutableList() ?: mutableListOf()
//
//                        // 새 WiD 추가
//                        wiDList.add(newWiD)
//                        dateListMap[date] = wiDList
//                        currentYearMap[year] = dateListMap
//                    }
//
//                    _yearDateWiDListMap.value = currentYearMap
//
//                    removeCurrentWiDFromMap()
//                    onStopwatchPaused(newWiD.exp)
//                }
//
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라이언트 코드 */
        val startDate = newWiD.start.toLocalDate()
        val finishDate = newWiD.finish.toLocalDate()

        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()

        for (i in 0..ChronoUnit.DAYS.between(startDate, finishDate)) {
            val date = startDate.plusDays(i)
            val year = Year.of(date.year)
            val dateListMap = currentYearMap[year]?.toMutableMap() ?: mutableMapOf()
            val wiDList = dateListMap[date]?.toMutableList() ?: mutableListOf()

            // 새 WiD 추가
            wiDList.add(newWiD)
            dateListMap[date] = wiDList
            currentYearMap[year] = dateListMap
        }

        _yearDateWiDListMap.value = currentYearMap

        removeCurrentWiDFromMap()
        onStopwatchPaused(newWiD.exp)
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        startLastNewWiDTimer()
        removeCurrentWiDFromMap() // 도구를 중지하지 않고 정지할 수도 있으니 Current WiD 삭제
        resetCurrentWiD()

        _playerState.value = PlayerState.STOPPED
        _totalDuration.value = Duration.ZERO
        prevDuration = Duration.ZERO
    }

    private fun resetCurrentWiD() {
        Log.d(TAG, "resetCurrentWiD executed")

        _currentWiD.value = _currentWiD.value.copy(id = CURRENT_WID, tool = Tool.NONE)
    }

    fun setTimerTime(newSelectedTime: Duration) {
        Log.d(TAG, "setTimerTime executed")

        _selectedTime.value = newSelectedTime // 얘는 고정
        _remainingTime.value = newSelectedTime // 최초에 같이 설정해줌.
    }

    fun startTimer(
        email: String,
        wiDMinLimit: Duration,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onTimerAutoStopped: (newExp: Int) -> Unit, // 자동 종료 용 콜백
    ) {
        Log.d(TAG, "startTimer executed")

        timer?.cancel()
        _playerState.value = PlayerState.STARTED
        setCurrentWiDtoNow(currentTool = Tool.TIMER)
        updateNow()

        timer = timer(
            name = CURRENT_WID_TIMER,
            period = 1_000,
            action = {
                val realTime = updateNow()

                val newCurrentWiDDuration = Duration.between(_currentWiD.value.start, realTime)
                _currentWiD.value = _currentWiD.value.copy( // First Current WiD만 갱신
                    finish = realTime, // 종료 시간 갱신
                    duration = newCurrentWiDDuration, // 소요 시간 갱신
                    exp = newCurrentWiDDuration.seconds.toInt() // 경험치 갱신
                )

                _remainingTime.value = _selectedTime.value - (prevDuration + newCurrentWiDDuration)

                insertCurrentWiDToMap()

                if (_remainingTime.value <= Duration.ZERO) {
                    autoStopTimer(
                        email = email,
                        wiDMinLimit = wiDMinLimit,
                        onResult = { snackbarActionResult: SnackbarActionResult ->
                            onResult(snackbarActionResult)
                        },
                        onTimerAutoStopped = { newExp: Int ->
                            onTimerAutoStopped(newExp)
                        },
                    )
                }
            }
        )
    }

    fun pauseTimer(
        email: String,
        wiDMinLimit: Duration,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onTimerPaused: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "pauseTimer executed")

        startLastNewWiDTimer()
        _playerState.value = PlayerState.PAUSED
        prevDuration = _selectedTime.value - _remainingTime.value

        val currentWiD = _currentWiD.value

        // 기록이 최소 제한 시간보다 짧으면 중단
        if (currentWiD.duration < wiDMinLimit) {
            onResult(SnackbarActionResult.FAIL_TIME_LIMIT)
            return
        }

        // 랜덤 ID 부여
        val newWiD = currentWiD.copy(id = generateUniqueWiDId())

        // 서버로 WiD 전송
//        wiDRepository.createWiD(
//            email = email,
//            wiD = newWiD,
//            onResult = { snackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_CREATE_WID) {
//                    val startDate = newWiD.start.toLocalDate()
//                    val finishDate = newWiD.finish.toLocalDate()
//
//                    val currentYearMap = _yearDateWiDListMap.value.toMutableMap()
//
//                    for (i in 0..ChronoUnit.DAYS.between(startDate, finishDate)) {
//                        val date = startDate.plusDays(i)
//                        val year = Year.of(date.year)
//                        val dateListMap = currentYearMap[year]?.toMutableMap() ?: mutableMapOf()
//                        val wiDList = dateListMap[date]?.toMutableList() ?: mutableListOf()
//
//                        // 새 WiD 추가
//                        wiDList.add(newWiD)
//                        dateListMap[date] = wiDList
//                        currentYearMap[year] = dateListMap
//                    }
//
//                    _yearDateWiDListMap.value = currentYearMap
//
//                    removeCurrentWiDFromMap()
//                    onTimerPaused(newWiD.exp)
//                }
//
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라이언트 코드 */
        val startDate = newWiD.start.toLocalDate()
        val finishDate = newWiD.finish.toLocalDate()

        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()

        for (i in 0..ChronoUnit.DAYS.between(startDate, finishDate)) {
            val date = startDate.plusDays(i)
            val year = Year.of(date.year)
            val dateListMap = currentYearMap[year]?.toMutableMap() ?: mutableMapOf()
            val wiDList = dateListMap[date]?.toMutableList() ?: mutableListOf()

            // 새 WiD 추가
            wiDList.add(newWiD)
            dateListMap[date] = wiDList
            currentYearMap[year] = dateListMap
        }

        _yearDateWiDListMap.value = currentYearMap

        removeCurrentWiDFromMap()
        onTimerPaused(newWiD.exp)
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        startLastNewWiDTimer()
        removeCurrentWiDFromMap()
        resetCurrentWiD()

        _playerState.value = PlayerState.STOPPED

        _selectedTime.value = Duration.ZERO // 선택 시간 초기화
        prevDuration = Duration.ZERO // 진행 시간 초기화
        _remainingTime.value = Duration.ZERO // 남은 시간 초기화
    }

    private fun autoStopTimer( // pause와 stop 둘 다 동작해야 함.
        email: String,
        wiDMinLimit: Duration,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onTimerAutoStopped: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "autoStopTimer executed")

        pauseTimer(
            email = email,
            wiDMinLimit = wiDMinLimit,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            },
            onTimerPaused = { newExp: Int ->
                onTimerAutoStopped(newExp)
            }
        )

        stopTimer()
    }

    fun setClickedWiDAndCopy(clickedWiD: WiD) { // clickedWiD와 clickedWiDCopy를 같이 할당하는 경우의 수 밖에 없음
        Log.d(TAG, "setClickedWiDAndCopy executed")

        _clickedWiD.value = clickedWiD
        _clickedWiDCopy.value = clickedWiD

        if (clickedWiD.id == LAST_NEW_WID) {
            setUpdateClickedWiDFinishToNow(update = true)
            setUpdateClickedWiDCopyFinishToNow(update = true)
        } else {
            setUpdateClickedWiDFinishToNow(update = false)
            setUpdateClickedWiDCopyFinishToNow(update = false)
        }
    }

    fun setClickedWiDCopy(newClickedWiDCopy: WiD) {
        Log.d(TAG, "setClickedWiDCopy executed")

        _clickedWiDCopy.value = newClickedWiDCopy
    }

    /** **************************************** 유틸 메서드 **************************************** */
    private fun generateUniqueWiDId(): String { // 14자리 문자열 생성
        Log.d(TAG, "generateUniqueId executed")

        val timestamp = Instant.now().epochSecond  // 10자리 타임스탬프
        val random = UUID.randomUUID().toString().substring(0, 4)  // 4자리 랜덤 값
        return "$timestamp$random"
    }

    fun getFullWiDList(
        date: LocalDate, // 조회 날짜
        wiDList: List<WiD>,
        today: LocalDate, // date가 today면 다르게 동작하도록 함.
        currentTime: LocalDateTime? // currentTime이 Null이면 다르게 동작함.
    ): List<WiD> {
        Log.d(TAG, "getFullWiDListFromWiDList executed")

//        if (wiDList.isEmpty()) { return emptyList() }

        val fullWiDList = mutableListOf<WiD>()

        var newWiDStart = LocalDateTime.MIN

        // 데이터 베이스에서 가져온 WiD는 0나노 세컨드를 가짐.
        for (wiD in wiDList) {
            val newWiDFinish = wiD.start

            if (newWiDStart.equals(newWiDFinish)) {
                newWiDStart = wiD.finish

                fullWiDList.add(wiD)
                continue
            }

            val newWiD = WiD.default().copy(
                id = NEW_WID,
                start = newWiDStart,
                finish = newWiDFinish,
                duration = Duration.between(newWiDStart, newWiDFinish),
            )

            fullWiDList.add(newWiD)
            fullWiDList.add(wiD) // 당연히 currentWiD를 newWiD 뒤에 넣어줘야 제대로 동작함

            newWiDStart = wiD.finish
        }

        if (date == today) { // 오늘 날짜 조회
            return if (currentTime == null) { // 도구 시작 상태면 새 기록 추가하지 않음
                fullWiDList
            } else { // 도구 정지 및 중지 상태면 마지막 새 기록 추가
                val lastNewWiD = WiD.default().copy(
                    id = if (currentTime == LocalDateTime.MAX.withNano(0)) { NEW_WID } else { LAST_NEW_WID },
                    start = newWiDStart,
                    finish = currentTime,
                    duration = Duration.between(newWiDStart, currentTime),
                )
                fullWiDList.add(lastNewWiD)
                fullWiDList
            }
        } else { // 오늘 아닌 날짜 조회면 마지막 기록 추가함.
            val maxTime = LocalDateTime.MAX.withNano(0)
            val emptyWiDDuration = Duration.between(newWiDStart, maxTime)
            if (Duration.ZERO < emptyWiDDuration) { // 마지막 WiD의 소요 시간이 있으면 추가
                val newWiD = WiD.default().copy(
                    id = NEW_WID,
                    start = newWiDStart,
                    finish = maxTime,
                    duration = emptyWiDDuration,
                )
                fullWiDList.add(newWiD)
            }

            return fullWiDList
        }
    }

    fun getWiDTitleTotalDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
        Log.d(TAG, "getWiDTitleTotalDurationMap executed")

        val result = mutableMapOf<Title, Duration>()

        for (wiD in wiDList) {
            // WiD의 title을 Title enum으로 변환
            val title = Title.values().find { it == wiD.title } ?: continue // kr 값으로 Title을 찾음
            val duration = wiD.duration

            // 제목 별로 소요 시간을 누적
            val totalDuration = result[title]
            if (totalDuration != null) {
                result[title] = totalDuration.plus(duration)
            } else {
                result[title] = duration
            }
        }

        // 소요 시간을 기준으로 내림차순으로 정렬
        val sortedResult = result.entries.sortedByDescending { it.value }

        // 정렬된 결과를 새로운 Map으로 반환
        return sortedResult.associate { it.toPair() }
    }

    fun getWiDTitleAverageDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
        Log.d(TAG, "getWiDTitleAverageDurationMap executed")

        val result = mutableMapOf<Title, Duration>()

        val totalDurationMapByTitleByDate = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

        // 각 제목에 대한 날짜별 총 소요 시간을 계산
        for (wiD in wiDList) {
            val title = wiD.title
            val date = wiD.start.toLocalDate()
            val duration = wiD.duration

            val titleMap = totalDurationMapByTitleByDate.computeIfAbsent(title) { mutableMapOf() }
            titleMap[date] = titleMap.getOrDefault(date, Duration.ZERO) + duration
        }

        // 각 제목에 대한 평균 소요 시간을 계산하여 result 맵에 할당
        for ((title, totalDurationMapByDate) in totalDurationMapByTitleByDate) {
            val totalDuration = totalDurationMapByDate.values.reduce(Duration::plus)
            val averageDuration = totalDuration.dividedBy(totalDurationMapByDate.size.toLong())
            result[title] = averageDuration
        }

        // 소요 시간을 기준으로 내림차순으로 정렬
        val sortedResult = result.entries.sortedByDescending { it.value }

        // 정렬된 결과를 새로운 Map으로 반환
        return sortedResult.associate { it.toPair() }
    }

    fun getWiDTitleMaxDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
        Log.d(TAG, "getWiDTitleMaxDurationMap executed")

        val result = mutableMapOf<Title, Duration>()

        val totalDurationMapByTitleByDate = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

        // 각 제목에 대한 날짜별 총 소요 시간을 계산
        for (wiD in wiDList) {
            val title = wiD.title
            val date = wiD.start.toLocalDate()
            val duration = wiD.duration

            val titleMap = totalDurationMapByTitleByDate.computeIfAbsent(title) { mutableMapOf() }
            titleMap[date] = titleMap.getOrDefault(date, Duration.ZERO) + duration
        }

        // 각 제목에 대한 최대 소요 시간을 계산하여 result 맵에 할당
        for ((title, totalDurationMapByDate) in totalDurationMapByTitleByDate) {
            val maxDuration = totalDurationMapByDate.values.maxOrNull() ?: Duration.ZERO
            result[title] = maxDuration
        }

        // 소요 시간을 기준으로 내림차순으로 정렬
        val sortedResult = result.entries.sortedByDescending { it.value }

        // 정렬된 결과를 새로운 Map으로 반환
        return sortedResult.associate { it.toPair() }
    }

    fun getWiDTitleMinDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
        Log.d(TAG, "getWiDTitleMinDurationMap executed")

        val result = mutableMapOf<Title, Duration>()

        val totalDurationMapByTitleByDate = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

        // 각 제목에 대한 날짜별 총 소요 시간을 계산
        for (wiD in wiDList) {
            val title = wiD.title
            val date = wiD.start.toLocalDate()
            val duration = wiD.duration

            val titleMap = totalDurationMapByTitleByDate.computeIfAbsent(title) { mutableMapOf() }
            titleMap[date] = titleMap.getOrDefault(date, Duration.ZERO) + duration
        }

        // 각 제목에 대한 최소 소요 시간을 계산하여 result 맵에 할당
        for ((title, totalDurationMapByDate) in totalDurationMapByTitleByDate) {
            val minDuration = totalDurationMapByDate.values.minOrNull() ?: Duration.ZERO
            result[title] = minDuration
        }

        // 소요 시간을 기준으로 내림차순으로 정렬
        val sortedResult = result.entries.sortedByDescending { it.value }

        // 정렬된 결과를 새로운 Map으로 반환
        return sortedResult.associate { it.toPair() }
    }

    fun getWiDTitleMaxDateMap(wiDList: List<WiD>): Map<Title, LocalDate> {
        Log.d(TAG, "getWiDTitleMaxDateMap executed")

        // Step 1: Title 별로 날짜 별 Duration을 누적
        val durationMap = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

        for (wiD in wiDList) {
            val title = wiD.title
            val date = wiD.start.toLocalDate()
            val duration = wiD.duration

            // Title과 날짜별로 누적 시간을 저장
            val dateDurationMap = durationMap.getOrPut(title) { mutableMapOf() }
            dateDurationMap[date] = dateDurationMap.getOrDefault(date, Duration.ZERO).plus(duration)
        }

        // Step 2: 각 Title에 대해 최대 Duration을 가진 날짜 찾기
        return durationMap.mapValues { (_, dateMap) ->
            dateMap.maxByOrNull { it.value }?.key ?: LocalDate.now() // 최대 Duration을 가진 날짜 반환
        }
    }

    fun getWiDTitleMinDateMap(wiDList: List<WiD>): Map<Title, LocalDate> {
        Log.d(TAG, "getWiDTitleMinDateMap executed")

        // Step 1: Title 별로 날짜 별 Duration을 누적
        val durationMap = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

        for (wiD in wiDList) {
            val title = wiD.title
            val date = wiD.start.toLocalDate()
            val duration = wiD.duration

            // Title과 날짜별로 누적 시간을 저장
            val dateDurationMap = durationMap.getOrPut(title) { mutableMapOf() }
            dateDurationMap[date] = dateDurationMap.getOrDefault(date, Duration.ZERO).plus(duration)
        }

        // Step 2: 각 Title에 대해 최소 Duration을 가진 날짜 찾기
        return durationMap.mapValues { (_, dateMap) ->
            dateMap.minByOrNull { it.value }?.key ?: LocalDate.now() // 최소 Duration을 가진 날짜 반환
        }
    }

    fun getWiDTitleDateCountMap(wiDList: List<WiD>): Map<Title, Int> {
        Log.d(TAG, "getWiDTitleDateCountMap executed")

        // 제목별로 날짜를 저장하기 위한 맵을 초기화합니다.
        val titleDateSetMap = mutableMapOf<Title, MutableSet<LocalDate>>()

        // wiDList를 순회하면서 각 WiD의 제목과 날짜를 맵에 추가합니다.
        for (wid in wiDList) {
            // 제목별로 존재하는 날짜를 집합(Set)으로 관리합니다.
            val dateSet = titleDateSetMap.getOrPut(wid.title) { mutableSetOf() }
            dateSet.add(wid.start.toLocalDate())
        }

        // 제목별 날짜 집합의 크기를 이용해 제목별 존재 일수를 계산하여 맵으로 변환합니다.
        return titleDateSetMap.mapValues { it.value.size }
    }

    fun getDurationString(duration: Duration): String { // "H시간 m분 s초"
//        Log.d(TAG, "getDurationString executed")

        val hours = duration.toHours()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        return when {
            hours > 0 && minutes == 0 && seconds == 0 -> String.format("%d시간", hours)
            hours > 0 && minutes > 0 && seconds == 0 -> String.format("%d시간 %d분", hours, minutes)
            hours > 0 && minutes == 0 && seconds > 0 -> String.format("%d시간 %d초", hours, seconds)
            hours > 0 -> String.format("%d시간 %d분 %d초", hours, minutes, seconds)
            minutes > 0 && seconds == 0 -> String.format("%d분", minutes)
            minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
            else -> String.format("%d초", seconds)
        }
    }

    fun getDurationTimeString(duration: Duration): String { // "HH:mm:ss"
//        Log.d(TAG, "getDurationTimeString executed")

        val totalSeconds = duration.seconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

//    fun getDurationStringEN(duration: Duration): String {
////    Log.d(TAG, "getDurationStringEN executed")
////    "Hh mm ss"
//
//        val hours = duration.toHours()
//        val minutes = (duration.toMinutes() % 60)
//        val seconds = (duration.seconds % 60)
//
//        return buildString {
//            if (hours > 0) append("${hours}h ")
//            if (minutes > 0) append("${minutes}m ")
//            if (seconds > 0) append("${seconds}s")
//        }.trim()
//    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
//        Log.d(TAG, "getDateString executed")

        val formattedString = buildAnnotatedString {
            if (date.year == LocalDateTime.now().year) { // TODO: 현재를 파라미터로 받도록
                append(date.format(DateTimeFormatter.ofPattern("M월 d일 (")))
            } else {
                append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
            }

            withStyle(
                style = SpanStyle(
                    color = when (date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> MaterialTheme.colorScheme.onTertiaryContainer
                        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
            }
            append(")")
        }

        return formattedString
    }

    fun getTimeString(time: LocalTime): String {
//        Log.d(TAG, "getTimeString executed")
        // 'HH:mm:ss'

        return when (time) {
            LocalTime.MIDNIGHT -> "Start" // 00:00:00일 때
            LocalTime.MAX -> "End"       // 23:59:59일 때
            else -> time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) // 다른 경우 일반 시간 형식
        }
    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
        val formattedString = buildAnnotatedString {
            append(dateTime.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

            withStyle(
                style = SpanStyle(
                    color = when (dateTime.dayOfWeek) {
                        DayOfWeek.SATURDAY -> MaterialTheme.colorScheme.onTertiaryContainer
                        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                append(dateTime.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
            }
            append(")")

            append(dateTime.format(DateTimeFormatter.ofPattern(" HH:mm:ss")))
        }

        return formattedString
    }
}