package andpact.project.wid.dataSource

import andpact.project.wid.model.WiD
import andpact.project.wid.model.YearlyWiDList
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import andpact.project.wid.util.Title
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

/**
 * state로 선언하지 않으면 다른 클래스에서 변수의 변화를 감지할 수 없다.
 * wiDListMap만 가지고 있고, 각 뷰 모델에 wiDList를 뿌려주는 용
 *
 * DS는 YearlyWiDList 사용하지 않음.
 */
class WiDDataSource @Inject constructor(private val wiDRepository: WiDRepository) {
    private val TAG = "WiDDataSource"
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    // New WiD View, New WiD View Model, WiD View, WiD View Model에서 같은 "오늘"을 공유하기 위함
    private val _today: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    val today: State<LocalDate> = _today

    // WiD / 기간(주, 월, 년)별 데이터 필요할 때는 startDate ~ finishDate
    private val _yearDateWiDListMap = mutableStateOf<Map<Year, Map<LocalDate, List<WiD>>>>(emptyMap())

    /** 위드 객체로 만들기? */
    // Current WiD(Tool)
    private var currentWiDTimer: Timer? = null
    private val _date: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    val date: State<LocalDate> = _date
    private val _title: MutableState<Title> = mutableStateOf(Title.STUDY)
    val title: State<Title> = _title // 스톱워치, 타이머 제목 공유함
    private val _start: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    val start: State<LocalTime> = _start
    private val _finish: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    val finish: State<LocalTime> = _finish

    private val _currentTool: MutableState<CurrentTool> = mutableStateOf(CurrentTool.NONE)
    val currentTool: State<CurrentTool> = _currentTool
    private val _currentToolState: MutableState<CurrentToolState> = mutableStateOf(CurrentToolState.STOPPED)
    val currentToolState: State<CurrentToolState> = _currentToolState

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
    private val _newWiD = mutableStateOf(createDefaultWiD()) // 수정 전(사용되면 안됨)
    val newWiD: State<WiD> = _newWiD
    private val _updatedNewWiD = mutableStateOf(createDefaultWiD()) // 수정 후
    val updatedNewWiD: State<WiD> = _updatedNewWiD

    // WiD View
    private var _wiD = mutableStateOf(createDefaultWiD()) // 수정 전(사용되면 안됨)
    val wiD: State<WiD> = _wiD
    private var _updatedWiD = mutableStateOf(createDefaultWiD()) // 수정 후
    val updatedWiD: State<WiD> = _updatedWiD

    fun addWiD(
        email: String,
        onWiDAdded: (wiDAdded: Boolean) -> Unit
    ) {
        val newWiD = _updatedNewWiD.value

        if (newWiD == null) {
            Log.e(TAG, "addWiD failed: _updatedNewWiD is null")
            onWiDAdded(false)
            return
        }

        Log.d(TAG, "addWiD executed with WiD: $newWiD")

        wiDRepository.addWiDList(
            email = email,
            wiDList = listOf(newWiD), // 단일 WiD를 리스트로 감싸서 서버 호출
            onWiDListAdded = { wiDListAdded: Boolean ->
                if (wiDListAdded) {
                    addCreatedWiDToMap(newWiD) // Firestore 추가 성공 시 로컬 캐시 업데이트
                    Log.d(TAG, "WiD added successfully to Firestore and cached")
                    onWiDAdded(true)
                } else {
                    Log.e(TAG, "Failed to add WiD to Firestore")
                    onWiDAdded(false)
                }
            }
        )
    }

    private fun getYearlyWiDList(
        email: String,
        year: Year,
        onYearlyWiDListFetched: (yearlyWiDListFetched: Boolean) -> Unit
    ) {
        Log.d(TAG, "getYearlyWiDList executed")

        wiDRepository.getYearlyWiDList(
            email = email,
            year = year,
            onYearlyWiDListFetched = { yearlyWiDList: YearlyWiDList ->
                // YearlyWiDList의 데이터를 날짜별로 그룹화
                val updatedDateWiDListMap = yearlyWiDList.wiDList.groupBy { it.date }

                // 기존 맵을 복사하여 업데이트 (_yearDateWiDListMap을 갱신)
                _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
                    val yearMap = this[year]?.toMutableMap() ?: mutableMapOf()
                    yearMap.putAll(updatedDateWiDListMap)
                    this[year] = yearMap
                }

                onYearlyWiDListFetched(true)

                Log.d(TAG, "Fetched and cached WiD list for year: $year")
            }
        )
    }

    fun getWiDListOfDate(
        email: String,
        date: LocalDate,
        onWiDListFetchedOfDate: (fetchedWiDList: List<WiD>) -> Unit
    ) {
        Log.d(TAG, "getWiDListOfDate2 executed")

        // 연도별 캐시에서 해당 날짜에 대한 WiDList를 탐색
        val year = Year.of(date.year)
        val cachedYearMap = _yearDateWiDListMap.value[year]
        val cachedWiDList = cachedYearMap?.get(date)

        if (cachedWiDList != null) {
            Log.d(TAG, "Returning cached WiD list for date: $date")
            onWiDListFetchedOfDate(cachedWiDList)
        } else {
            // 연도별 데이터가 없거나 해당 날짜의 데이터가 없으면 서버에서 가져옴
            getYearlyWiDList(
                email = email,
                year = year,
                onYearlyWiDListFetched = { wiDListFetched: Boolean ->
                    if (wiDListFetched) {
                        // 서버에서 데이터를 가져온 후, 다시 _yearDateWiDListMap에서 해당 날짜의 데이터를 탐색
                        val updatedYearMap = _yearDateWiDListMap.value[year]
                        val wiDListForDate = updatedYearMap?.get(date) ?: emptyList()

                        Log.d(TAG, "Fetched WiD list for date: $date from updated cache")
                        onWiDListFetchedOfDate(wiDListForDate)
                    } else {
                        // 서버에서 데이터를 가져오지 못한 경우 빈 리스트 반환
                        Log.e(TAG, "Failed to fetch WiD list for year: ${year}")
                        onWiDListFetchedOfDate(emptyList())
                    }
                }
            )
        }
    }

    fun getWiDListFromFirstDateToLastDate(
        email: String,
        firstDate: LocalDate,
        lastDate: LocalDate,
        onWiDListFetched: (fetchedWiDList: List<WiD>) -> Unit
    ) {
        Log.d(TAG, "getWiDListFromFirstDateToLastDate2 executed")

        val resultWiDList = mutableListOf<WiD>()
        var currentDate = firstDate

        fun fetchNext() {
            // 날짜 범위를 초과하면 종료
            if (currentDate > lastDate) {
                Log.d(TAG, "Finished fetching data for date range")
                onWiDListFetched(resultWiDList)
                return
            }

            val year = Year.of(currentDate.year)
            val cachedYearMap = _yearDateWiDListMap.value[year]
            val cachedWiDList = cachedYearMap?.get(currentDate)

            if (cachedWiDList != null) { // 캐시에 데이터가 있는 경우 처리
                resultWiDList.addAll(cachedWiDList)
                currentDate = currentDate.plusDays(1)
                fetchNext() // 다음 날짜 탐색
            } else { // 캐시에 데이터가 없는 경우 서버 호출
                Log.d(TAG, "Data for date $currentDate not found in cache, fetching year $year")
                getYearlyWiDList(
                    email = email,
                    year = year,
                    onYearlyWiDListFetched = { yearlyWiDListFetched ->
                        if (yearlyWiDListFetched) {
                            Log.d(TAG, "Fetched data for year $year, retrying date $currentDate")
                            fetchNext() // 서버 호출 후 다시 현재 날짜부터 탐색
                        } else {
                            Log.e(TAG, "Failed to fetch data for year $year, skipping date $currentDate")
                            currentDate = currentDate.plusDays(1)
                            fetchNext() // 실패한 경우에도 다음 날짜로 이동
                        }
                    }
                )
            }
        }

        // 탐색 시작
        fetchNext()
    }

    fun updateWiD(
        email: String,
        onWiDUpdated: (wiDUpdated: Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiD2 executed")

        // `_wiD`의 원래 값과 `_updatedWiD`의 새로운 값
        val targetDate = _wiD.value.date
        val targetYear = Year.of(targetDate.year)

        // `_yearDateWiDListMap`에서 해당 연도의 데이터를 수정
        val currentYearMap = _yearDateWiDListMap.value[targetYear]?.toMutableMap()

        if (currentYearMap != null) {
            // 날짜별 WiD 리스트에서 수정 대상 찾기
            val currentDateWiDList = currentYearMap[targetDate]?.toMutableList() ?: mutableListOf()
            val wiDIndex = currentDateWiDList.indexOfFirst { it.id == _wiD.value.id }

            if (wiDIndex != -1) {
                // 기존 WiD를 `_updatedWiD`로 교체
                currentDateWiDList[wiDIndex] = _updatedWiD.value
                currentYearMap[targetDate] = currentDateWiDList

                // 업데이트된 `YearlyWiDList` 생성
                val updatedYearlyWiDList = YearlyWiDList(
                    wiDList = currentYearMap.flatMap { it.value }
                )

                // Firestore에 업데이트 요청
                wiDRepository.updateWiD(
                    email = email,
                    year = targetYear,
                    yearlyWiDList = updatedYearlyWiDList,
                    onWiDUpdated = { wiDUpdated: Boolean ->
                        if (wiDUpdated) {
                            // Firestore 업데이트 성공 시, 로컬 캐시 수정
                            _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
                                this[targetYear] = currentYearMap
                            }

                            Log.d(TAG, "WiD updated successfully for year: $targetYear")
                            onWiDUpdated(true)
                        } else {
                            Log.e(TAG, "Failed to update WiD for year: $targetYear")
                            onWiDUpdated(false)
                        }
                    }
                )
            } else {
                Log.e(TAG, "WiD not found in the target date: $targetDate")
                onWiDUpdated(false)
            }
        } else {
            Log.e(TAG, "Year not found in _yearDateWiDListMap: $targetYear")
            onWiDUpdated(false)
        }
    }


    fun deleteWiD(
        email: String,
        onWiDDeleted: (wiDDeleted: Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiD2 executed")

        val targetDate = _wiD.value.date
        val targetYear = Year.of(targetDate.year)

        // `_yearDateWiDListMap`에서 해당 연도의 데이터를 가져오기
        val currentYearMap = _yearDateWiDListMap.value[targetYear]?.toMutableMap()

        if (currentYearMap != null) {
            // 날짜별 WiD 리스트에서 제거 대상 찾기
            val currentDateWiDList = currentYearMap[targetDate]?.toMutableList() ?: mutableListOf()
            val wiDIndex = currentDateWiDList.indexOfFirst { it.id == _wiD.value.id }

            if (wiDIndex != -1) {
                // WiD 제거
                currentDateWiDList.removeAt(wiDIndex)

                // 날짜 리스트 갱신
                if (currentDateWiDList.isEmpty()) {
                    currentYearMap.remove(targetDate)
                } else {
                    currentYearMap[targetDate] = currentDateWiDList
                }

                // 갱신된 `YearlyWiDList` 생성
                val updatedYearlyWiDList = YearlyWiDList(
                    wiDList = currentYearMap.flatMap { it.value }
                )

                // Firestore와 동기화
                wiDRepository.deleteWiD(
                    email = email,
                    year = targetYear,
                    yearlyWiDList = updatedYearlyWiDList,
                    onWiDDeleted = { wiDDeleted: Boolean ->
                        if (wiDDeleted) {
                            // Firestore 업데이트 성공 시, 로컬 캐시 업데이트
                            _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
                                this[targetYear] = currentYearMap
                            }

                            Log.d(TAG, "WiD deleted successfully for year: $targetYear")
                            onWiDDeleted(true)
                        } else {
                            Log.e(TAG, "Failed to delete WiD for year: $targetYear")
                            onWiDDeleted(false)
                        }
                    }
                )
            } else {
                Log.e(TAG, "WiD not found in the target date: $targetDate")
                onWiDDeleted(false)
            }
        } else {
            Log.e(TAG, "Year not found in _yearDateWiDListMap: $targetYear")
            onWiDDeleted(false)
        }
    }

    fun setToday(newDate: LocalDate) {
        Log.d(TAG, "setToday executed")

        _today.value = newDate
    }

    /** 기본 위드 생성하지 말고, null 허용으로 변경하기 */
    private fun createDefaultWiD(): WiD {
        return WiD(
            id = "currentWiD",
            date = LocalDate.now(),
            title = Title.UNTITLED,
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
        val wiDYear = Year.of(wiDDate.year) // 연도 추출

        // 현재 WiD 리스트를 가져옴
        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()
        val currentDateListMap = currentYearMap[wiDYear]?.toMutableMap() ?: mutableMapOf()
        val currentWiDList = currentDateListMap[wiDDate]?.toMutableList() ?: mutableListOf()

        // 동일한 ID의 WiD가 이미 있는 경우 기존 WiD를 대체
        val wiDIndex = currentWiDList.indexOfFirst { it.id == newCurrentWiD.id }

        if (wiDIndex != -1) { // 기존 WiD를 새로운 WiD로 대체
            currentWiDList[wiDIndex] = newCurrentWiD
            Log.d(TAG, "Updated existing WiD with ID: ${newCurrentWiD.id}")
        } else { // 새로운 WiD 추가
            currentWiDList.add(newCurrentWiD)
            Log.d(TAG, "Added new WiD with ID: ${newCurrentWiD.id}")
        }

        // 갱신된 리스트를 Yearly WiD Map에 다시 추가
        currentDateListMap[wiDDate] = currentWiDList
        currentYearMap[wiDYear] = currentDateListMap

        // 최종적으로 _yearDateWiDListMap을 업데이트
        _yearDateWiDListMap.value = currentYearMap

        Log.d(TAG, "Updated _yearDateWiDListMap for date: $wiDDate and year: $wiDYear")
    }

    // 도구 중지 했을 때
    private fun replaceCurrentWiDWithCreatedWiD(createdWiD: WiD) {
        Log.d(TAG, "replaceCurrentWiDWithCreatedWiD executed")

        // 생성된 WiD의 날짜와 연도를 가져옴
        val wiDDate = createdWiD.date
        val wiDYear = Year.of(wiDDate.year) // 연도 추출

        // 현재 WiD 리스트를 가져옴
        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()
        val currentDateListMap = currentYearMap[wiDYear]?.toMutableMap() ?: mutableMapOf()
        val currentWiDList = currentDateListMap[wiDDate]?.toMutableList() ?: mutableListOf()

        // ID가 "currentWiD"인 WiD를 찾아 대체
        val currentWiDIndex = currentWiDList.indexOfFirst { it.id == "currentWiD" }

        if (currentWiDIndex != -1) {
            // "currentWiD"를 발견하면 새로운 WiD로 대체
            currentWiDList[currentWiDIndex] = createdWiD
            Log.d(TAG, "Replaced currentWiD with createdWiD: ${createdWiD.id}")
        } else {
            // "currentWiD"가 없으면 새로운 WiD를 리스트에 추가
            currentWiDList.add(createdWiD)
            Log.d(TAG, "Added createdWiD to the list: ${createdWiD.id}")
        }

        // 업데이트된 리스트를 Yearly WiD Map에 다시 추가
        currentDateListMap[wiDDate] = currentWiDList
        currentYearMap[wiDYear] = currentDateListMap

        // 최종적으로 _yearDateWiDListMap을 업데이트
        _yearDateWiDListMap.value = currentYearMap

        Log.d(TAG, "Updated _yearDateWiDListMap for date: $wiDDate and year: $wiDYear")
    }

    private fun removeCurrentWiDFromYearMapOnCurrentDate() {
        Log.d(TAG, "removeCurrentWiDFromYearMapOnCurrentDate executed")

        // 현재 날짜 및 연도를 가져옴
        val currentDate = _date.value
        val currentYear = Year.of(currentDate.year)

        // 현재 연도의 WiD 리스트 맵을 가져옴
        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()
        val currentDateListMap = currentYearMap[currentYear]?.toMutableMap() ?: mutableMapOf()

        // 현재 날짜의 WiD 리스트 가져오기
        val currentWiDList = currentDateListMap[currentDate]?.toMutableList() ?: mutableListOf()

        // 해당 WiD 삭제 (ID가 "currentWiD"인 경우)
        val wiDIndex = currentWiDList.indexOfFirst { it.id == "currentWiD" }
        if (wiDIndex != -1) {
            currentWiDList.removeAt(wiDIndex)
            Log.d(TAG, "Removed currentWiD from date: $currentDate")

            // 업데이트된 WiD 리스트를 현재 날짜 리스트에 반영
            if (currentWiDList.isNotEmpty()) {
                currentDateListMap[currentDate] = currentWiDList // 비어있지 않으면 리스트 업데이트
            } else {
                currentDateListMap.remove(currentDate) // 비어있으면 해당 날짜 삭제
            }
        }

        // 현재 연도의 날짜 리스트 맵을 업데이트
        currentYearMap[currentYear] = currentDateListMap

        // 최종적으로 _yearDateWiDListMap을 업데이트
        _yearDateWiDListMap.value = currentYearMap
        Log.d(TAG, "Updated _yearDateWiDListMap for date: $currentDate and year: $currentYear")
    }

    private fun removeCurrentWiDFromYearMapOnNextDate() {
        Log.d(TAG, "removeCurrentWiDFromYearMapOnNextDate executed")

        // 다음 날짜 및 연도를 가져옴
        val nextDate = _date.value.plusDays(1)
        val nextYear = Year.of(nextDate.year)

        // 현재 연도의 WiD 리스트 맵을 가져옴
        val currentYearMap = _yearDateWiDListMap.value.toMutableMap()
        val nextDateListMap = currentYearMap[nextYear]?.toMutableMap() ?: mutableMapOf()

        // 다음 날짜의 WiD 리스트 가져오기
        val nextWiDList = nextDateListMap[nextDate]?.toMutableList() ?: mutableListOf()
        val minTime = LocalTime.MIN

        // 해당 WiD 삭제 (ID가 "currentWiD"이고 시작 시간이 minTime인 경우)
        val wiDIndex = nextWiDList.indexOfFirst { it.id == "currentWiD" && it.start == minTime }
        if (wiDIndex != -1) {
            nextWiDList.removeAt(wiDIndex)
            Log.d(TAG, "Removed currentWiD from next date: $nextDate")

            // 업데이트된 WiD 리스트를 다음 날짜 리스트에 반영
            if (nextWiDList.isNotEmpty()) {
                nextDateListMap[nextDate] = nextWiDList // 비어있지 않으면 리스트 업데이트
            } else {
                nextDateListMap.remove(nextDate) // 비어있으면 해당 날짜 삭제
            }
        }

        // 현재 연도의 날짜 리스트 맵을 업데이트
        currentYearMap[nextYear] = nextDateListMap

        // 최종적으로 _yearDateWiDListMap을 업데이트
        _yearDateWiDListMap.value = currentYearMap
        Log.d(TAG, "Updated _yearDateWiDListMap for next date: $nextDate and year: $nextYear")
    }

    fun setTitle(newTitle: Title) {
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
        onStopwatchPaused: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        _currentToolState.value = CurrentToolState.PAUSED

        accumulatedPrevDuration = _totalDuration.value

        currentWiDTimer?.cancel()

        if (_start.value.equals(_finish.value)) {
            return
        }

        val newWiDList = mutableListOf<WiD>()
        val totalDuration: Duration

        if (_start.value.isBefore(_finish.value)) { // Case 1: 동일 날짜 내에 WiD 생성
            totalDuration = Duration.between(_start.value, _finish.value)

//            if (totalDuration < Duration.ofMinutes(1)) {
//                return
//            }

            val newWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = totalDuration,
                createdBy = CurrentTool.STOPWATCH
            )
            newWiDList.add(newWiD)
        } else { // Case 2: 자정을 넘어가는 경우 WiD 생성
            val previousDate = _date.value.minusDays(1)
            val minTime = LocalTime.MIN
            val maxTime = LocalTime.MAX.withNano(0)

            val firstWiDDuration = Duration.between(_start.value, maxTime)
            val secondWiDDuration = Duration.between(minTime, _finish.value)
            totalDuration = firstWiDDuration + secondWiDDuration

//            if (totalDuration < Duration.ofMinutes(1)) {
//                return
//            }

            // 첫 번째 WiD
            val firstWiD = WiD(
                id = "",
                date = previousDate,
                title = _title.value,
                start = _start.value,
                finish = maxTime,
                duration = firstWiDDuration,
                createdBy = CurrentTool.STOPWATCH
            )
            newWiDList.add(firstWiD)

            // 두 번째 WiD
            val secondWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = secondWiDDuration,
                createdBy = CurrentTool.STOPWATCH
            )
            newWiDList.add(secondWiD)
        }

        if (newWiDList.isNotEmpty()) {
            wiDRepository.addWiDList(
                email = email,
                wiDList = newWiDList,
                onWiDListAdded = { wiDListAdded ->
                    if (wiDListAdded) {
                        Log.d(TAG, "WiD(s) added successfully for date: ${_date.value}")
                        onStopwatchPaused(totalDuration.seconds.toInt()) // 소요 시간 반환
                        newWiDList.forEach { replaceCurrentWiDWithCreatedWiD(it) }
                    } else {
                        Log.e(TAG, "Failed to add WiD(s) for date: ${_date.value}")
                        onStopwatchPaused(0) // 실패 시 0 반환
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

        removeCurrentWiDFromYearMapOnCurrentDate()
        removeCurrentWiDFromYearMapOnNextDate()

        _totalDuration.value = Duration.ZERO
        accumulatedPrevDuration = Duration.ZERO
    }

    fun setSelectedTime(newSelectedTime: Duration) {
        Log.d(TAG, "setSelectedTime executed")

        _selectedTime.value = newSelectedTime
    }

    fun startTimer(
        email: String, // 자동 종료 용 콜백
        onTimerAutoStopped: (newExp: Int) -> Unit,
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
                    onTimerAutoStopped = { newExp: Int ->
                        onTimerAutoStopped(newExp)
                    },
                )
            }
        }
    }

    fun pauseTimer(
        email: String,
        onTimerPaused: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "pauseTimer executed")

        _currentToolState.value = CurrentToolState.PAUSED

        currentWiDTimer?.cancel()

        _selectedTime.value = _remainingTime.value

        if (_start.value.equals(_finish.value)) {
            return
        }

        val newWiDList = mutableListOf<WiD>()
        val totalDuration: Duration

        if (_start.value.isBefore(_finish.value)) { // Case 1: 동일 날짜 내 WiD 생성
            totalDuration = Duration.between(_start.value, _finish.value)

//            if (totalDuration < Duration.ofMinutes(1)) {
//                return
//            }

            val newWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = totalDuration,
                createdBy = CurrentTool.TIMER
            )
            newWiDList.add(newWiD)
        } else { // Case 2: 자정을 넘어가는 경우 WiD 생성
            val previousDate = _date.value.minusDays(1)
            val minTime = LocalTime.MIN
            val maxTime = LocalTime.MAX.withNano(0)

            val firstWiDDuration = Duration.between(_start.value, maxTime)
            val secondWiDDuration = Duration.between(minTime, _finish.value)
            totalDuration = firstWiDDuration + secondWiDDuration

//            if (totalDuration < Duration.ofMinutes(1)) {
//                return
//            }

            val firstWiD = WiD(
                id = "",
                date = previousDate,
                title = _title.value,
                start = _start.value,
                finish = maxTime,
                duration = firstWiDDuration,
                createdBy = CurrentTool.TIMER
            )
            newWiDList.add(firstWiD)

            val secondWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = secondWiDDuration,
                createdBy = CurrentTool.TIMER
            )
            newWiDList.add(secondWiD)
        }

        if (newWiDList.isNotEmpty()) {
            wiDRepository.addWiDList(
                email = email,
                wiDList = newWiDList,
                onWiDListAdded = { wiDListAdded ->
                    if (wiDListAdded) {
                        Log.d(TAG, "WiD(s) added successfully for Timer")
                        onTimerPaused(totalDuration.seconds.toInt()) // 총 소요 시간 반환
                        newWiDList.forEach { replaceCurrentWiDWithCreatedWiD(it) }
                    } else {
                        Log.e(TAG, "Failed to add WiD(s) for Timer")
                        onTimerPaused(0) // 실패 시 0 반환
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

        removeCurrentWiDFromYearMapOnCurrentDate()
        removeCurrentWiDFromYearMapOnNextDate()

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }

    private fun autoStopTimer(
        email: String,
        onTimerAutoStopped: (newExp: Int) -> Unit
    ) {
        Log.d(TAG, "autoStopTimer executed")

        _currentTool.value = CurrentTool.NONE
        _currentToolState.value = CurrentToolState.STOPPED

        currentWiDTimer?.cancel()

        val totalDuration: Duration
        val newWiDList = mutableListOf<WiD>()

        if (_start.value.equals(_finish.value)) {
            return
        }

        if (_start.value.isBefore(_finish.value)) {
            // Case 1: 동일 날짜 내 WiD 생성
            totalDuration = Duration.between(_start.value, _finish.value)

//            if (totalDuration < Duration.ofMinutes(1)) {
//                Log.d(TAG, "Total duration is less than 1 minute. No WiD created.")
//                return
//            }

            val newWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = _start.value,
                finish = _finish.value,
                duration = totalDuration,
                createdBy = CurrentTool.TIMER
            )
            newWiDList.add(newWiD)
        } else {
            // Case 2: 자정을 넘어가는 경우 WiD 생성
            val previousDate = _date.value.minusDays(1)
            val minTime = LocalTime.MIN
            val maxTime = LocalTime.MAX.withNano(0)

            val firstWiDDuration = Duration.between(_start.value, maxTime)
            val secondWiDDuration = Duration.between(minTime, _finish.value)
            totalDuration = firstWiDDuration + secondWiDDuration

//            if (totalDuration < Duration.ofMinutes(1)) {
//                Log.d(TAG, "Total duration is less than 1 minute. No WiD created.")
//                return
//            }

            // 첫 번째 WiD
            val firstWiD = WiD(
                id = "",
                date = previousDate,
                title = _title.value,
                start = _start.value,
                finish = maxTime,
                duration = firstWiDDuration,
                createdBy = CurrentTool.TIMER
            )
            newWiDList.add(firstWiD)

            // 두 번째 WiD
            val secondWiD = WiD(
                id = "",
                date = _date.value,
                title = _title.value,
                start = minTime,
                finish = _finish.value,
                duration = secondWiDDuration,
                createdBy = CurrentTool.TIMER
            )
            newWiDList.add(secondWiD)
        }

        if (newWiDList.isNotEmpty()) {
            wiDRepository.addWiDList(
                email = email,
                wiDList = newWiDList,
                onWiDListAdded = { wiDListAdded ->
                    if (wiDListAdded) {
                        Log.d(TAG, "WiD(s) added successfully for auto-stop")
                        onTimerAutoStopped(totalDuration.seconds.toInt()) // 총 소요 시간 반환
                        newWiDList.forEach { replaceCurrentWiDWithCreatedWiD(it) }
                    } else {
                        Log.e(TAG, "Failed to add WiD(s) for auto-stop")
                        onTimerAutoStopped(0) // 실패 시 0 반환
                    }
                }
            )
        }

        _remainingTime.value = Duration.ZERO
        _selectedTime.value = Duration.ZERO
    }

    private fun addCreatedWiDToMap(createdWiD: WiD) {
        Log.d(TAG, "addCreatedWiDToMap executed with WiD: $createdWiD")

        val year = Year.of(createdWiD.date.year)
        val currentYearMap = _yearDateWiDListMap.value[year]?.toMutableMap() ?: mutableMapOf()
        val currentDateList = currentYearMap[createdWiD.date] ?: emptyList()
        val updatedDateList = currentDateList + createdWiD

        _yearDateWiDListMap.value = _yearDateWiDListMap.value.toMutableMap().apply {
            this[year] = currentYearMap.apply {
                this[createdWiD.date] = updatedDateList
            }
        }

        Log.d(TAG, "WiD added to local cache: $createdWiD")
    }

    fun setNewWiD(newWiD: WiD) {
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
}