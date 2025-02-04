package andpact.project.wid.dataSource

import andpact.project.wid.model.*
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
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
    private val _today: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    val today: State<LocalDate> = _today
    private val _now: MutableState<LocalTime> = mutableStateOf(LocalTime.now().withNano(0))
    val now: State<LocalTime> = _now

    private val _yearDateWiDListMap = mutableStateOf<Map<Year, Map<LocalDate, List<WiD>>>>(emptyMap())
    val yearDateWiDListMap: State<Map<Year, Map<LocalDate, List<WiD>>>> = _yearDateWiDListMap

    // Current WiD(Tool)
    private val _firstCurrentWiD = mutableStateOf(WiD.default().copy(id = CURRENT_WID, title = Title.STUDY))
    val firstCurrentWiD: State<WiD> = _firstCurrentWiD
    private val _secondCurrentWiD = mutableStateOf(WiD.default().copy(id = CURRENT_WID, title = Title.STUDY))
    val secondCurrentWiD: State<WiD> = _secondCurrentWiD
    private val _isSameDateForStartAndFinish = mutableStateOf(true)
    val isSameDateForStartAndFinish: State<Boolean> = _isSameDateForStartAndFinish

    private val _currentToolState: MutableState<CurrentToolState> = mutableStateOf(CurrentToolState.STOPPED)
    val currentToolState: State<CurrentToolState> = _currentToolState

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
    private var _updateClickedWiDToNow = mutableStateOf(false)
    val updateClickedWiDToNow: State<Boolean> = _updateClickedWiDToNow
    private var _clickedWiD = mutableStateOf(WiD.default()) // 수정 전(사용되면 안됨)
    val clickedWiD: State<WiD> = _clickedWiD
    private var _updateClickedWiDCopyToNow = mutableStateOf(false)
    val updateClickedWiDCopyToNow: State<Boolean> = _updateClickedWiDCopyToNow
    private var _clickedWiDCopy = mutableStateOf(WiD.default()) // 수정 후
    val clickedWiDCopy: State<WiD> = _clickedWiDCopy

    fun setUpdateClickedWiDToNow(update: Boolean) { // 마지막 새 기록 클릭 시
        Log.d(TAG, "setUpdateClickedWiDToNow executed")

        _updateClickedWiDToNow.value = update
    }

    fun setUpdateClickedWiDCopyToNow(update: Boolean) { // 마지막 새 기록 클릭 시, 마지막 기록의 종료 시간 갱신 용
        Log.d(TAG, "setUpdateClickedWiDCopyToNow executed")

        _updateClickedWiDCopyToNow.value = update
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

                if (updateClickedWiDToNow.value) {
                    _clickedWiD.value = _clickedWiD.value.copy(
                        finish = realTime,
                        duration = Duration.between(_clickedWiD.value.start, realTime)
                    )
                }

                if (updateClickedWiDCopyToNow.value) {
                    _clickedWiDCopy.value = _clickedWiDCopy.value.copy(
                        finish = realTime,
                        duration = Duration.between(_clickedWiDCopy.value.start, realTime)
                    )
                }
            }
        )
    }

    private fun updateNow(): LocalTime {
        Log.d(TAG, "updateNow executed")

        val realTimeDate = LocalDate.now()
        val realTime = LocalTime.now().withNano(0)

        if (_today.value != realTimeDate) {
            _today.value = realTimeDate
        }
        _now.value = realTime

        return realTime // 계산된 realTime 반환
    }

    fun createWiD(
        email: String,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        val newWiD = _clickedWiDCopy.value.copy(id = generateUniqueWiDId()) // id를 직접 변경하지 않음
        val targetDate = newWiD.date
        val targetYear = Year.of(targetDate.year)

        val currentYearMap = _yearDateWiDListMap.value[targetYear]?.toMutableMap() ?: mutableMapOf() // 날짜 - 기록 리스트 맵(null이면 빈 맵이 됨)
        val currentDateWiDList = currentYearMap[targetDate]?.toMutableList() ?: mutableListOf() // 기록 리스트(null이면 빈 리스트이 됨)

        currentDateWiDList.add(newWiD) // 기록 리스트에 기록 추가
        currentYearMap[targetDate] = currentDateWiDList // 날짜 - 기록 리스트 맵 갱신

//        wiDRepository.createWiD(
//            email = email,
//            year = targetYear,
//            dateWiDListMap = currentYearMap, // 업데이트할 WiD 리스트 전달
//            onResult = { snackbarActionResult: SnackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_CREATE_WID) {
//                    _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
//                        this[targetYear] = currentYearMap // 업데이트된 연도 맵으로 갱신
//                    }
//                }
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라 메모리 사용 */
        _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
            this[targetYear] = currentYearMap // 업데이트된 연도 맵으로 갱신
        }
        onResult(SnackbarActionResult.SUCCESS_CREATE_WID)
        /** 클라 메모리 사용 */
    }

    /** 도구 시작할 때 호출안하면 서버 데이터가 아니라 빈 맵이 생김. */
    // TODO: 도구 화면에서 호출해야 기록 리스트 크기 확인 가능함.
    fun getYearlyWiDListMap(
        email: String,
        year: Year
    ) {
        Log.d(TAG, "getYearlyWiDListMap executed")

        if (_yearDateWiDListMap.value[year] == null) { // 년도 키가 없을 때만 서버 호출
//            wiDRepository.getYearlyWiDListMap(
//                email = email,
//                year = year,
//                onResult = { snackbarActionResult: SnackbarActionResult ->
//                    // TODO: 서버에서 데이터를 가져오지 못했을 때 어떻게 대처?
//                },
//                onYearlyWiDListMapFetched = { yearlyWiDListMap: YearlyWiDListMap ->
//                    _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { // 클라이언트 메모리에 업데이트
//                        this[year] = yearlyWiDListMap.wiDListMap // Year 키를 추가하며 업데이트
//                    }
//                }
//            )

            /** 클라 메모리 사용 */
            _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {// 클라이언트 메모리에 업데이트
                this[year] = emptyMap() // 빈 맵 할당
            }
            /** 클라 메모리 사용 */
        }
    }

    fun updateWiD(
        email: String,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        val clickedWiD = _clickedWiD.value
        val targetDate = clickedWiD.date
        val targetYear = Year.of(targetDate.year)

        val currentYearMap = _yearDateWiDListMap.value[targetYear]?.toMutableMap() ?: return // 날짜 - 기록 리스트 맵(null 이면 잘못된 접근)
        val currentDateWiDList = currentYearMap[targetDate]?.toMutableList() ?: return // 기록 리스트(null 이면 잘못된 접근)
        val wiDIndex = currentDateWiDList.indexOfFirst { it.id == clickedWiD.id } // 갱신할 기록 탐색
        if (wiDIndex == -1) return // 기록(null 이면 잘못된 접근)

        currentDateWiDList[wiDIndex] = _clickedWiDCopy.value // 기록 갱신
        currentYearMap[targetDate] = currentDateWiDList // 날짜 - 기록 리스트 맵 갱신

//        wiDRepository.updateWiD(
//            email = email,
//            year = targetYear,
//            dateWiDListMap = currentYearMap,
//            onResult = { snackbarActionResult: SnackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_UPDATE_WID) {
//                    _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[targetYear] = currentYearMap }
//                }
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라 메모리 사용 */
        _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[targetYear] = currentYearMap }
        onResult(SnackbarActionResult.SUCCESS_UPDATE_WID)
        /** 클라 메모리 사용 */
    }

    fun deleteWiD(
        email: String,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit,
        onWiDDeleted: (deletedExp: Int) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        val clickedWiD = _clickedWiD.value
        val targetDate = clickedWiD.date
        val targetYear = Year.of(targetDate.year)

        val currentYearMap = _yearDateWiDListMap.value[targetYear]?.toMutableMap() ?: return // 날짜 - 기록 리스트 맵(null 이면 잘못된 접근)
        val currentDateWiDList = currentYearMap[targetDate]?.toMutableList() ?: return // 기록 리스트(null 이면 잘못된 접근)
        val wiDIndex = currentDateWiDList.indexOfFirst { it.id == clickedWiD.id } // 삭제할 기록 탐색
        if (wiDIndex == -1) return // 기록(null 이면 잘못된 접근)

        currentDateWiDList.removeAt(wiDIndex) // 기록 삭제
        currentYearMap[targetDate] = currentDateWiDList // 기록 리스트 갱신

//        wiDRepository.deleteWiD(
//            email = email,
//            year = targetYear,
//            dateWiDListMap = currentYearMap,
//            onResult = { snackbarActionResult: SnackbarActionResult ->
//                if (snackbarActionResult == SnackbarActionResult.SUCCESS_DELETE_WID) {
//                    _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[targetYear] = currentYearMap }
//                    onWiDDeleted(clickedWiD.exp)
//                }
//                onResult(snackbarActionResult)
//            }
//        )

        /** 클라 메모리 사용 */
        _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[targetYear] = currentYearMap }
        onResult(SnackbarActionResult.SUCCESS_DELETE_WID)
        onWiDDeleted(clickedWiD.exp)
        /** 클라 메모리 사용 */
    }

    private fun insertCurrentWiDToMap() {
        Log.d(TAG, "insertCurrentWiDToMap executed")

        val wiDListToInsert = mutableListOf<WiD>()

        if (_isSameDateForStartAndFinish.value) { // 동일한 날짜 내 WiD 처리
            wiDListToInsert.add(_firstCurrentWiD.value) // 첫 번째 현재 기록만 추가
        } else { // 자정을 넘어가는 WiD 처리
            wiDListToInsert.add(_secondCurrentWiD.value) // 두 번째 현재 기록만 추가
        }

        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()

        wiDListToInsert.forEach { wiDToInsert: WiD ->
            val targetDate = wiDToInsert.date
            val targetYear = Year.of(targetDate.year)

            val currentDateListMap = currentYearMap[targetYear]?.toMutableMap() ?: mutableMapOf()
            val currentWiDList = currentDateListMap[targetDate]?.toMutableList() ?: mutableListOf()
            val wiDIndex = currentWiDList.indexOfFirst { it.id == wiDToInsert.id }

            if (wiDIndex != -1) { // 기존 WiD를 새로운 WiD로 대체
                currentWiDList[wiDIndex] = wiDToInsert
            } else { // 새로운 WiD 추가
                currentWiDList.add(wiDToInsert)
            }

            currentDateListMap[targetDate] = currentWiDList
            currentYearMap[targetYear] = currentDateListMap
        }

        _yearDateWiDListMap.value = currentYearMap
    }

    private fun removeCurrentWiDFromMap() { // 도구 정지 했을 때 or 서버 통신 실패 시(Current WiD 삭제)
        Log.d(TAG, "removeCurrentWiDFromMap executed")

        val wiDListToRemove = mutableListOf<WiD>()

        if (_isSameDateForStartAndFinish.value) { // 동일한 날짜 내 WiD 처리
            wiDListToRemove.add(_firstCurrentWiD.value) // 첫 번째 현재 기록만 추가
        } else { // 자정을 넘어가는 WiD 처리
            wiDListToRemove.add(_secondCurrentWiD.value) // 두 번째 현재 기록만 추가
        }

        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()

        wiDListToRemove.forEach { wiDToRemove: WiD ->
            val targetDate = wiDToRemove.date
            val targetYear = Year.of(targetDate.year)

            val currentDateListMap = currentYearMap[targetYear]?.toMutableMap() ?: return@forEach // null이면 잘못된 접근
            val currentWiDList = currentDateListMap[targetDate]?.toMutableList() ?: return@forEach // null이면 잘못된 접근
//            val wiDIndex = currentWiDList.indexOfFirst { it.id == wiDToRemove.id && it.start == wiDToRemove.start && it.finish == wiDToRemove.finish }
            val wiDIndex = currentWiDList.indexOfFirst { it.id == wiDToRemove.id }
            if (wiDIndex == -1) return@forEach // 잘못된 접근

            currentWiDList.removeAt(wiDIndex)

            if (currentWiDList.isEmpty()) {
                currentDateListMap.remove(targetDate)
            } else {
                currentDateListMap[targetDate] = currentWiDList
            }

            currentYearMap[targetYear] = currentDateListMap
        }

        _yearDateWiDListMap.value = currentYearMap
    }

    fun setCurrentWiDTitleAndSubTitle(newTitle: Title, newSubTitle: SubTitle) { // 도구에서만 실행됨
        Log.d(TAG, "setCurrentWiDTitleAndSubTitle executed")

        val updatedWiD = _firstCurrentWiD.value.copy(title = newTitle, subTitle = newSubTitle)
        _firstCurrentWiD.value = updatedWiD
        _secondCurrentWiD.value = updatedWiD
    }

    private fun setCurrentWiDtoNow(currentTool: Tool) {
        Log.d(TAG, "setCurrentWiDtoNow executed")

        val today = LocalDate.now()
        val nextDate = today.plusDays(1)
        val now = LocalTime.now().withNano(0)
        val minTime = LocalTime.MIN

        _firstCurrentWiD.value = _firstCurrentWiD.value.copy(
            date = today, // 날짜 갱신
            start = now, // 시작 시간 갱신
            finish = now, // 종료 시간도 갱신
            duration = Duration.ZERO, // 소요 시간도 갱신
            tool = currentTool // 도구 갱신
        )

        _secondCurrentWiD.value = _secondCurrentWiD.value.copy( // 미리 준비함
            date = nextDate, // 날짜 갱신
            start = minTime, // 시작 시간 갱신
            finish = minTime, // 종료 시간도 갱신
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
        _currentToolState.value = CurrentToolState.STARTED
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

                _isSameDateForStartAndFinish.value = _firstCurrentWiD.value.start <= realTime // 매 초 마다 자정 넘어가는 지 확인해야 함. 도구 시작할 때만 갱신하면 될 듯

                val newFirstCurrentWiDDuration = Duration.between(_firstCurrentWiD.value.start, realTime)
                if (_isSameDateForStartAndFinish.value) { // 동일한 날짜
                    _firstCurrentWiD.value = _firstCurrentWiD.value.copy( // First Current WiD만 갱신
                        finish = realTime, // 종료 시간 갱신
                        duration = newFirstCurrentWiDDuration, // 소요 시간 갱신
                        exp = newFirstCurrentWiDDuration.seconds.toInt() // 경험치 갱신
                    )

                    _totalDuration.value = prevDuration + newFirstCurrentWiDDuration
                } else { // 자정 넘어감
                    val newSecondCurrentWiDDuration = Duration.between(_secondCurrentWiD.value.start, realTime)

                    _secondCurrentWiD.value = _secondCurrentWiD.value.copy( // Second Current WiD만 갱신
                        finish = realTime, // 종료 시간 갱신
                        duration = newSecondCurrentWiDDuration, // 소요 시간 갱신
                        exp = newSecondCurrentWiDDuration.seconds.toInt() // 경험치 갱신
                    )

                    _totalDuration.value = prevDuration + newFirstCurrentWiDDuration + newSecondCurrentWiDDuration
                }

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
        _currentToolState.value = CurrentToolState.PAUSED
        prevDuration = _totalDuration.value

        val firstCurrentWiD = _firstCurrentWiD.value
        val secondCurrentWiD = _secondCurrentWiD.value

        val newWiDList = mutableListOf<WiD>()

        if (_isSameDateForStartAndFinish.value) { // Case 1: 동일 날짜 내에 WiD 생성
            if (firstCurrentWiD.duration < wiDMinLimit) {
                onResult(SnackbarActionResult.FAIL_TIME_LIMIT) // 시간 제한
                return
            }

            val firstCurrentWiDCopy = firstCurrentWiD.copy(id = generateUniqueWiDId())
            newWiDList.add(firstCurrentWiDCopy)
        } else { // Case 2: 자정을 넘어가는 경우 WiD 생성
            if (firstCurrentWiD.duration + secondCurrentWiD.duration < wiDMinLimit) {
                onResult(SnackbarActionResult.FAIL_TIME_LIMIT) // 시간 제한
                return
            }

            val firstCurrentWiDCopy = firstCurrentWiD.copy(id = generateUniqueWiDId())
            val secondCurrentWiDCopy = secondCurrentWiD.copy(id = generateUniqueWiDId())
            newWiDList.add(firstCurrentWiDCopy)
            newWiDList.add(secondCurrentWiDCopy)
        }

        if (newWiDList.isEmpty()) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 잘못된 접근
            return
        }

        val newDateWiDListMap = newWiDList.groupBy { it.date } // 날짜 - 기록 리스트 맵(1날짜 혹은 2날짜)
        val updatedYearlyWiDListMap = mutableMapOf<Year, Map<LocalDate, List<WiD>>>() // 년도 맵

        newDateWiDListMap.forEach { (date: LocalDate, wiDList: List<WiD>) ->
            val targetYear = Year.of(date.year)
            val currentYearDateWiDListMap = _yearDateWiDListMap.value[targetYear] ?: emptyMap() // 날짜 - 기록 리스트 맵
            updatedYearlyWiDListMap[targetYear] = currentYearDateWiDListMap.toMutableMap().apply { this[date] = (this[date] ?: emptyList()) + wiDList } // 날짜 - 기록 리스트 맵 갱신
        }

        var totalNewExp = 0 // 성공한 WiD의 총 소요 시간

        updatedYearlyWiDListMap.forEach { (year: Year, dateWiDListMap: Map<LocalDate, List<WiD>>) -> // 기록의 연도가 2개면 서버가 2번 호출 됨.
//            wiDRepository.createWiD(
//                email = email,
//                year = year,
//                dateWiDListMap = dateWiDListMap,
//                onResult = { snackbarActionResult: SnackbarActionResult ->
//                    if (snackbarActionResult == SnackbarActionResult.SUCCESS_CREATE_WID) {
//                        totalNewExp += dateWiDListMap.values.flatten()
//                            .filter { wiD -> newWiDList.any { it.id == wiD.id } }
//                            .sumOf { it.exp }
//
//                        _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[year] = dateWiDListMap }
//                    }
//
//                    onResult(snackbarActionResult)
//                }
//            )

            /** 클라 메모리 사용 */
            totalNewExp += dateWiDListMap.values.flatten()
                .filter { wiD -> newWiDList.any { it.id == wiD.id } }
                .sumOf { it.exp }
            _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[year] = dateWiDListMap }
            onResult(SnackbarActionResult.SUCCESS_CREATE_WID)
            /** 클라 메모리 사용 */
        }

        removeCurrentWiDFromMap()
        onStopwatchPaused(totalNewExp) // 유저 문서 갱신은 한 번만
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        startLastNewWiDTimer()
        removeCurrentWiDFromMap() // 도구를 중지하지 않고 정지할 수도 있으니 Current WiD 삭제
        resetCurrentWiD()

        _currentToolState.value = CurrentToolState.STOPPED
        _totalDuration.value = Duration.ZERO
        prevDuration = Duration.ZERO
    }

    private fun resetCurrentWiD() {
        Log.d(TAG, "resetCurrentWiD executed")

        _firstCurrentWiD.value = _firstCurrentWiD.value.copy(
            id = CURRENT_WID,
            tool = Tool.NONE
        )
        _secondCurrentWiD.value = _secondCurrentWiD.value.copy(
            id = CURRENT_WID,
            tool = Tool.NONE
        )
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
        _currentToolState.value = CurrentToolState.STARTED
        setCurrentWiDtoNow(currentTool = Tool.TIMER)
        updateNow()

        timer = timer(
            name = CURRENT_WID_TIMER,
            period = 1_000,
            action = {
                val realTime = updateNow()

                _isSameDateForStartAndFinish.value = _firstCurrentWiD.value.start <= realTime

                val newFirstCurrentWiDDuration = Duration.between(_firstCurrentWiD.value.start, realTime)
                if (_isSameDateForStartAndFinish.value) { // 자정 안 넘음
                    _firstCurrentWiD.value = _firstCurrentWiD.value.copy( // First Current WiD만 갱신
                        finish = realTime, // 종료 시간 갱신
                        duration = newFirstCurrentWiDDuration, // 소요 시간 갱신
                        exp = newFirstCurrentWiDDuration.seconds.toInt() // 경험치 갱신
                    )

//                    _remainingTime.value = _selectedTime.value - newFirstCurrentWiDDuration
                    _remainingTime.value = _selectedTime.value - (prevDuration + newFirstCurrentWiDDuration)
                } else { // 자정 넘음
                    val newSecondCurrentWiDDuration = Duration.between(_secondCurrentWiD.value.start, realTime)

                    _secondCurrentWiD.value = _secondCurrentWiD.value.copy( // Second Current WiD만 갱신
                        finish = realTime, // 종료 시간 갱신
                        duration = newSecondCurrentWiDDuration, // 소요 시간 갱신
                        exp = newSecondCurrentWiDDuration.seconds.toInt() // 경험치 갱신
                    )

//                    _remainingTime.value = _selectedTime.value - (newFirstCurrentWiDDuration + newSecondCurrentWiDDuration)
                    _remainingTime.value = _selectedTime.value - (prevDuration + newFirstCurrentWiDDuration + newSecondCurrentWiDDuration)
                }

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
        _currentToolState.value = CurrentToolState.PAUSED
        prevDuration = _selectedTime.value - _remainingTime.value
//        _selectedTime.value = _remainingTime.value

        val firstCurrentWiD = _firstCurrentWiD.value
        val secondCurrentWiD = _secondCurrentWiD.value

        val newWiDList = mutableListOf<WiD>()

        if (_isSameDateForStartAndFinish.value) { // Case 1: 동일 날짜 내 WiD 생성
            if (_firstCurrentWiD.value.duration < wiDMinLimit) {
                onResult(SnackbarActionResult.FAIL_TIME_LIMIT) // 시간 제한
                return
            }

            val firstCurrentWiDCopy = firstCurrentWiD.copy(id = generateUniqueWiDId())
            newWiDList.add(firstCurrentWiDCopy)
        } else { // Case 2: 자정을 넘어가는 경우 WiD 생성
            if (_firstCurrentWiD.value.duration + _secondCurrentWiD.value.duration < wiDMinLimit) {
                onResult(SnackbarActionResult.FAIL_TIME_LIMIT) // 시간 제한
                return
            }

            val firstCurrentWiDCopy = firstCurrentWiD.copy(id = generateUniqueWiDId())
            val secondCurrentWiDCopy = secondCurrentWiD.copy(id = generateUniqueWiDId())
            newWiDList.add(firstCurrentWiDCopy)
            newWiDList.add(secondCurrentWiDCopy)
        }

        if (newWiDList.isEmpty()) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 잘못된 접근
            return
        }

        val newDateWiDListMap = newWiDList.groupBy { it.date }
        val updatedYearlyWiDListMap = mutableMapOf<Year, Map<LocalDate, List<WiD>>>()

        newDateWiDListMap.forEach { (date: LocalDate, wiDList: List<WiD>) ->
            val targetYear = Year.of(date.year)
            val currentYearDateWiDListMap = _yearDateWiDListMap.value[targetYear] ?: emptyMap() // 날짜 - 기록 리스트 맵
            updatedYearlyWiDListMap[targetYear] = currentYearDateWiDListMap.toMutableMap().apply {
                this[date] = (this[date] ?: emptyList()) + wiDList
            }
        }

        var totalNewExp = 0

        updatedYearlyWiDListMap.forEach { (year: Year, dateWiDListMap: Map<LocalDate, List<WiD>>) -> // 기록이 2개면 서버가 2번 호출 됨.
            wiDRepository.createWiD(
                email = email,
                year = year,
                dateWiDListMap = dateWiDListMap,
                onResult = { snackbarActionResult: SnackbarActionResult ->
                    if (snackbarActionResult == SnackbarActionResult.SUCCESS_CREATE_WID) {
                        totalNewExp += dateWiDListMap.values.flatten()
                            .filter { wiD -> newWiDList.any { it.id == wiD.id } }
                            .sumOf { it.exp }
                        _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply { this[year] = dateWiDListMap }
                    }
                    onResult(snackbarActionResult)
                }
            )

            /** 클라 메모리 사용 */
            totalNewExp += dateWiDListMap.values.flatten()
                .filter { wiD -> newWiDList.any { it.id == wiD.id } }
                .sumOf { it.exp }
            _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
                this[year] = dateWiDListMap
            }
            onResult(SnackbarActionResult.SUCCESS_CREATE_WID)
            /** 클라 메모리 사용 */
        }

        removeCurrentWiDFromMap()
        onTimerPaused(totalNewExp) // 유저 문서 갱신은 한 번만
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        startLastNewWiDTimer()
        removeCurrentWiDFromMap()
        resetCurrentWiD()

        _currentToolState.value = CurrentToolState.STOPPED

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
            setUpdateClickedWiDToNow(update = true)
            setUpdateClickedWiDCopyToNow(update = true)
        } else {
            setUpdateClickedWiDToNow(update = false)
            setUpdateClickedWiDCopyToNow(update = false)
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

    /** 오늘 날짜여도 마지막 빈 공간 채워지도록? */
    fun getFullWiDListFromWiDList(
        date: LocalDate, // 조회 날짜
        wiDList: List<WiD>,
        today: LocalDate, // date가 today면 다르게 동작하도록 함.
        currentTime: LocalTime? // currentTime이 Null이면 다르게 동작함.
    ): List<WiD> {
        Log.d(TAG, "getFullWiDListFromWiDList executed")

//        if (wiDList.isEmpty()) { return emptyList() }

        val fullWiDList = mutableListOf<WiD>()

        var newWiDStart = LocalTime.MIN

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
                date = date,
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
                    id = if (currentTime == LocalTime.MAX.withNano(0)) { NEW_WID } else { LAST_NEW_WID },
                    date = today,
                    start = newWiDStart,
                    finish = currentTime,
                    duration = Duration.between(newWiDStart, currentTime),
                )
                fullWiDList.add(lastNewWiD)
                fullWiDList
            }
        } else { // 오늘 아닌 날짜 조회면 마지막 기록 추가함.
            val maxTime = LocalTime.MAX.withNano(0)
            val emptyWiDDuration = Duration.between(newWiDStart, maxTime)
            if (Duration.ZERO < emptyWiDDuration) { // 마지막 WiD의 소요 시간이 있으면 추가
                val newWiD = WiD.default().copy(
                    id = NEW_WID,
                    date = date,
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
            val date = wiD.date
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
            val date = wiD.date
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
            val date = wiD.date
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
            val date = wiD.date
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
            val date = wiD.date
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
            dateSet.add(wid.date)
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
            if (date.year == LocalDate.now().year) {
                append(date.format(DateTimeFormatter.ofPattern("M월 d일 (")))
            } else {
                append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
            }

            withStyle(
                style = SpanStyle(
                    color = when (date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> DeepSkyBlue
                        DayOfWeek.SUNDAY -> OrangeRed
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
}