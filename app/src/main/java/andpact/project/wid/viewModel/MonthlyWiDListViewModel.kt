package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.Year
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

    val today: State<LocalDate> = wiDDataSource.today
    private val _startDate = mutableStateOf(getFirstDateOfMonth(LocalDate.now()))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfMonth(LocalDate.now()))
    val finishDate: State<LocalDate> = _finishDate
    private val _monthPickerExpanded = mutableStateOf(false)
    val monthPickerExpanded: State<Boolean> = _monthPickerExpanded

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }
//    val dateWiDListMap: State<Map<LocalDate, List<WiD>>> = derivedStateOf { updateDateWiDListMap() }

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
        startDate: LocalDate,
        finishDate: LocalDate
    ) {
        Log.d(TAG, "setStartDateAndFinishDate executed")

        val currentUser = user.value ?: return

        _startDate.value = startDate
        _finishDate.value = finishDate

        (startDate.year..finishDate.year).forEach { year: Int ->
            wiDDataSource.getYearlyWiDListMap(
                email = currentUser.email,
                year = Year.of(year)
            )
        }
    }

    fun setMonthPickerExpanded(expand: Boolean) {
        Log.d(TAG, "setMonthPickerExpanded executed")

        _monthPickerExpanded.value = expand
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

        return wiDDataSource.yearDateWiDListMap.value
            .filterKeys { year -> year.value == start.year || year.value == finish.year } // 필요한 연도만 필터링
            .flatMap { (_, dateMap: Map<LocalDate, List<WiD>>) ->
                dateMap.filterKeys { date -> date in start..finish } // start부터 finish까지의 날짜만 필터링
                    .values.flatten() // 날짜에 해당하는 WiD 리스트를 병합
            }
    }

    fun getMonthString(firstDayOfMonth: LocalDate): String {
        Log.d(TAG, "getMonthString executed")

        val currentYear = today.value.year
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

//    private fun updateDateWiDListMap(): Map<LocalDate, List<WiD>> {
//        Log.d(TAG, "updateDateWiDListMap executed")
//
//        return wiDList.value // wiDList의 데이터를 기반으로 날짜별로 그룹화
//            .groupBy { it.date }
//
////        val start = _startDate.value
////        val finish = _finishDate.value
////
////        return wiDDataSource.yearDateWiDListMap.value
////            .filterKeys { year -> year.value == start.year || year.value == finish.year } // 필요한 연도만 필터링
////            .flatMap { (_, dateMap: Map<LocalDate, List<WiD>>) ->
////                dateMap.filterKeys { date -> date in start..finish } // 시작 날짜와 종료 날짜 사이의 기록만 필터링
////                    .entries // Map의 항목을 가져옴
////            }
////            .associate { it.toPair() } // 다시 Map 형태로 변환
//    }
}