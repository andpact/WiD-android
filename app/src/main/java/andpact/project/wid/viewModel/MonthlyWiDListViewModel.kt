package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.*
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
import javax.inject.Inject

@HiltViewModel
class MonthlyWiDListViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "MonthlyWiDListViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    val now: State<LocalDateTime> = wiDDataSource.now

    private val initialToday = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfMonth(initialToday))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfMonth(initialToday))
    val finishDate: State<LocalDate> = _finishDate

    private val _monthPickerExpanded = mutableStateOf(false)
    val monthPickerExpanded: State<Boolean> = _monthPickerExpanded

    private val _monthPickerCurrentYear = mutableStateOf(Year.of(initialToday.year))
    val monthPickerCurrentYear: State<Year> = _monthPickerCurrentYear
    private val _monthPickerStartDateOfCurrentMonth = mutableStateOf(_startDate.value)
    val monthPickerStartDateOfCurrentMonth: State<LocalDate> = _monthPickerStartDateOfCurrentMonth
    private val _monthPickerFinishDateOfCurrentMonth = mutableStateOf(_finishDate.value)
    val monthPickerFinishDateOfCurrentMonth: State<LocalDate> = _monthPickerFinishDateOfCurrentMonth

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }

    val titleDurationMapList = TitleDurationMap.values().toList()

    private val totalDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleTotalDurationMap(wiDList = wiDList.value) }
    private val averageDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleAverageDurationMap(wiDList = wiDList.value) }
    private val maxDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleMaxDurationMap(wiDList = wiDList.value) }
    private val minDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleMinDurationMap(wiDList = wiDList.value) }

    val titleMaxDateMap: State<Map<Title, LocalDate>> = derivedStateOf { wiDDataSource.getWiDTitleMaxDateMap(wiDList = wiDList.value) }
    val titleMinDateMap: State<Map<Title, LocalDate>> = derivedStateOf { wiDDataSource.getWiDTitleMinDateMap(wiDList = wiDList.value) }
    val titleDateCountMap: State<Map<Title, Int>> = derivedStateOf { wiDDataSource.getWiDTitleDateCountMap(wiDList = wiDList.value) }

    private val _currentMapType = mutableStateOf(TitleDurationMap.TOTAL)
    val currentMapType: State<TitleDurationMap> = _currentMapType
    val currentMap: State<Map<Title, Duration>> = derivedStateOf { setCurrentMap() }

    fun setStartDateAndFinishDate(
        newStartDate: LocalDate,
        newFinishDate: LocalDate
    ) {
        Log.d(TAG, "setStartDateAndFinishDate executed")

        val currentUser = user.value ?: return

        _startDate.value = newStartDate
        _finishDate.value = newFinishDate

        (newStartDate.year..newFinishDate.year).forEach { year: Int ->
            wiDDataSource.getWiD(
                email = currentUser.email,
                year = Year.of(year),
                onResult = { snackbarActionResult: SnackbarActionResult ->
                    if (snackbarActionResult == SnackbarActionResult.FAIL_SERVER_ERROR) {
                        // TODO: 서버 호출 실패 시 스낵 바 띄우기
                    }
                }
            )
        }
    }

    fun setMonthPickerExpanded(expand: Boolean) {
        Log.d(TAG, "setMonthPickerExpanded executed")

        _monthPickerExpanded.value = expand
    }

    fun setMonthPickerCurrentYear(newYear: Year) {
        Log.d(TAG, "setMonthPickerExpanded executed")

        _monthPickerCurrentYear.value = newYear
    }

    fun setMonthPickerStartDateOfCurrentMonthAndMonthPickerFinishDateOfCurrentMonth(
        newMonthPickerStartDateOfCurrentMonth: LocalDate,
        newMonthPickerFinishDateOfCurrentMonth: LocalDate
    ) {
        Log.d(TAG, "setMonthPickerStartDateOfCurrentMonthAndMonthPickerFinishDateOfCurrentMonth executed")

        _monthPickerStartDateOfCurrentMonth.value = newMonthPickerStartDateOfCurrentMonth
        _monthPickerFinishDateOfCurrentMonth.value = newMonthPickerFinishDateOfCurrentMonth
    }

    fun getFirstDateOfMonth(date: LocalDate): LocalDate {
        Log.d(TAG, "getFirstDateOfMonth executed")
        return date.withDayOfMonth(1) // 월의 첫째 날
    }

    fun getLastDateOfMonth(date: LocalDate): LocalDate {
        Log.d(TAG, "getLastDateOfMonth executed")
        return date.withDayOfMonth(date.lengthOfMonth()) // 월의 마지막 날
    }


    fun setCurrentMapType(mapType: TitleDurationMap) {
        Log.d(TAG, "setCurrentMapType executed")

        _currentMapType.value = mapType
    }

    private fun setCurrentMap(): Map<Title, Duration> {
        Log.d(TAG, "setCurrentMap executed")

        return when (_currentMapType.value) {
            TitleDurationMap.TOTAL -> totalDurationMap.value
            TitleDurationMap.AVERAGE -> averageDurationMap.value
            TitleDurationMap.MAX -> maxDurationMap.value
            TitleDurationMap.MIN -> minDurationMap.value
        }
    }

    private fun updateWiDList(): List<WiD> {
        Log.d(TAG, "updateWiDList executed")

        val start = _startDate.value
        val finish = _finishDate.value
        val resultWiDList = mutableListOf<WiD>()

        var currentDate = start
        while (currentDate <= finish) {
            val dayStart = currentDate.atStartOfDay()
            val dayEnd = currentDate.atTime(LocalTime.MAX)

            // 현재 날짜에 해당하는 기록 리스트 가져오기
            val wiDListForDay = wiDDataSource.yearDateWiDListMap.value
                .getOrDefault(Year.of(currentDate.year), emptyMap())
                .getOrDefault(currentDate, emptyList())

            // 날짜를 벗어나는 부분을 잘라서 리스트에 추가
            val adjustedWiDList = wiDListForDay.mapNotNull { wiD ->
                val adjustedStart = maxOf(wiD.start, dayStart)
                val adjustedFinish = minOf(wiD.finish, dayEnd)

                if (adjustedStart.isBefore(adjustedFinish)) {
                    wiD.copy(
                        start = adjustedStart,
                        finish = adjustedFinish,
                        duration = Duration.between(adjustedStart, adjustedFinish)
                    )
                } else {
                    null // 잘린 후 남은 시간이 없는 경우 제외
                }
            }

            resultWiDList.addAll(adjustedWiDList)
            currentDate = currentDate.plusDays(1) // 다음 날짜로 이동
        }

        return resultWiDList
    }

    fun getMonthString(firstDayOfMonth: LocalDate): String {
        Log.d(TAG, "getMonthString executed")

        val currentYear = now.value.year
        val year = firstDayOfMonth.year
        val month = firstDayOfMonth.monthValue

        return if (year == currentYear) {
            "${month}월"
        } else {
            "${year}년 ${month}월"
        }
    }

    fun getDurationString(duration: Duration): String {
        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    fun getDurationPercentageStringOfMonth(date: LocalDate, duration: Duration): String {
        Log.d(TAG, "getDurationPercentageStringOfMonth executed")

        // 해당 월의 총 초 계산
        val daysInMonth = date.lengthOfMonth()
        val totalSecondsInMonth = daysInMonth * 24 * 60 * 60

        // 소요 시간 초 계산
        val durationInSeconds = duration.seconds

        // 퍼센트 계산
        val percentage = (durationInSeconds.toFloat() / totalSecondsInMonth) * 100

        val tenTimesPercentage = (percentage * 10).toInt()

        // 퍼센트 포맷팅 및 반환
        return if (tenTimesPercentage % 10 == 0) { // 소수점 첫째 자리 숫자 확인
            "${percentage.toInt()}%" // 소수점 제거
        } else {
            "${tenTimesPercentage / 10f}%" // 소수점 첫째 자리까지 표시
        }
    }
}