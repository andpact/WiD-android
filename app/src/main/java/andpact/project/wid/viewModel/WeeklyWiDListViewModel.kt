package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.Title
import andpact.project.wid.model.TitleDurationMap
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeeklyWiDListViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "WeeklyWiDListViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    // 날짜
    val today: State<LocalDate> = wiDDataSource.today
    private val _startDate = mutableStateOf(getFirstDateOfWeek(LocalDate.now()))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(LocalDate.now()))
    val finishDate: State<LocalDate> = _finishDate
    private val _weekPickerExpanded = mutableStateOf(false)
    val weekPickerExpanded: State<Boolean> = _weekPickerExpanded

    // Map
//    val yearDateWiDListMap: State<Map<Year, Map<LocalDate, List<WiD>>>> = wiDDataSource.yearDateWiDListMap

    // WiD
//    private val _wiDListFetched = mutableStateOf(false)
//    val wiDListFetched: State<Boolean> = _wiDListFetched
//    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
//    val wiDList: State<List<WiD>> = _wiDList
    val wiDList: State<List<WiD>> = derivedStateOf { // 이 블럭 안의 State 변수가 변화하면 영향을 받음
        Log.d(TAG, "wiDList updated from yearDateWiDListMap")

        val start = _startDate.value
        val finish = _finishDate.value

        wiDDataSource.yearDateWiDListMap.value
            .filterKeys { year -> year.value == start.year || year.value == finish.year } // 필요한 연도만 필터링
            .flatMap { (_, dateMap: Map<LocalDate, List<WiD>>) ->
                dateMap.filterKeys { date -> date in start..finish } // start부터 finish까지의 날짜만 필터링
                    .values.flatten() // 날짜에 해당하는 WiD 리스트를 병합
            }
    }

    // 맵(모든 제목의 맵을 만들어둠).
    private val totalDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleTotalDurationMap(wiDList = wiDList.value) }
    private val averageDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleAverageDurationMap(wiDList = wiDList.value) }
    private val maxDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleMaxDurationMap(wiDList = wiDList.value) }
    private val minDurationMap: State<Map<Title, Duration>> = derivedStateOf { wiDDataSource.getWiDTitleMinDurationMap(wiDList = wiDList.value) }

    val titleMaxDateMap: State<Map<Title, LocalDate>> = derivedStateOf { wiDDataSource.getWiDTitleMaxDateMap(wiDList = wiDList.value) }
    val titleMinDateMap: State<Map<Title, LocalDate>> = derivedStateOf { wiDDataSource.getWiDTitleMinDateMap(wiDList = wiDList.value) }
    val titleDateCountMap: State<Map<Title, Int>> = derivedStateOf { wiDDataSource.getWiDTitleDateCountMap(wiDList = wiDList.value) }

    // 표시 되는 맵
    private val _currentMapType = mutableStateOf(TitleDurationMap.TOTAL)
    val currentMapType: State<TitleDurationMap> = _currentMapType
    private val _currentMap = mutableStateOf(totalDurationMap.value)
    val currentMap: State<Map<Title, Duration>> = _currentMap

    // Current WiD
//    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD
//    val secondCurrentWiD: State<WiD> = wiDDataSource.secondCurrentWiD

    fun setCurrentMapType(mapType: TitleDurationMap) {
        Log.d(TAG, "setCurrentMapType executed with mapType: $mapType")

        _currentMapType.value = mapType

        setCurrentMap(mapType = mapType)
    }

    private fun setCurrentMap(mapType: TitleDurationMap) {
        Log.d(TAG, "setCurrentMap executed with mapType: $mapType")

        _currentMap.value = when (mapType) {
            TitleDurationMap.TOTAL -> totalDurationMap.value
            TitleDurationMap.AVERAGE -> averageDurationMap.value
            TitleDurationMap.MAX -> maxDurationMap.value
            TitleDurationMap.MIN -> minDurationMap.value
        }
    }

    fun setWeekPickerExpanded(expand: Boolean) {
        Log.d(TAG, "setWeekPickerExpanded executed")

        _weekPickerExpanded.value = expand
    }

    fun setStartDateAndFinishDate(
        startDate: LocalDate,
        finishDate: LocalDate
    ) {
        Log.d(TAG, "setStartDateAndFinishDate executed")

        _startDate.value = startDate
        _finishDate.value = finishDate

        (startDate.year..finishDate.year).forEach { year: Int ->
            wiDDataSource.getYearlyWiDListMap(
                email = user.value?.email ?: "",
                year = Year.of(year)
            )
        }

//        setWiDListFetched(wiDListFetched = false)

//        getWiDListFromStartDateToFinishDate(
//            startDate = startDate,
//            finishDate = finishDate
//        )
    }

//    private fun getWiDListFromStartDateToFinishDate(
//        startDate: LocalDate,
//        finishDate: LocalDate
//    ) {
//        Log.d(TAG, "getWiDListFromStartDateToFinishDate executed")
//
//        wiDDataSource.getWiDListFromFirstDateToLastDate(
//            email = user.value?.email ?: "",
//            firstDate = startDate,
//            lastDate = finishDate,
//            onWiDListFetched = { wiDList: List<WiD> ->
//                _wiDList.value = wiDList
//                setDurationMaps(wiDList = wiDList)
//                setWiDListFetched(wiDListFetched = true)
//            }
//        )
//    }

//    private fun setDurationMaps(wiDList: List<WiD>) {
//        Log.d(TAG, "setDurationMaps executed")
//
//        totalDurationMap = wiDDataSource.getWiDTitleTotalDurationMap(wiDList = wiDList)
//        averageDurationMap = wiDDataSource.getWiDTitleAverageDurationMap(wiDList = wiDList)
//        maxDurationMap = wiDDataSource.getWiDTitleMaxDurationMap(wiDList = wiDList)
//        minDurationMap = wiDDataSource.getWiDTitleMinDurationMap(wiDList = wiDList)
//
//        _titleDateCountMap.value = wiDDataSource.getWiDTitleDateCountMap(wiDList = wiDList)
//        _titleMaxDateMap.value = wiDDataSource.getWiDTitleMaxDateMap(wiDList = wiDList)
//        _titleMinDateMap.value = wiDDataSource.getWiDTitleMinDateMap(wiDList = wiDList)
//
//        setCurrentMap(_currentMapType.value) // 예를 계속 갱신해줘야 함.
//    }

//    private fun setWiDListFetched(wiDListFetched: Boolean) {
//        Log.d(TAG, "setWiDListFetched executed")
//
//        _wiDListFetched.value = wiDListFetched
//    }

    fun getDurationString(duration: Duration): String {
        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    fun getDurationPercentageStringOfWeek(duration: Duration): String {
        Log.d(TAG, "getDurationPercentageStringOfWeek executed")

        val totalSecondsInWeek = 7 * 24 * 60 * 60
        val durationInSeconds = duration.seconds

        val percentage = (durationInSeconds.toFloat() / totalSecondsInWeek) * 100

        return if (percentage % 1.0 == 0.0) {
            "${percentage.toInt()}%"
        } else {
            "${String.format("%.1f", percentage)}%"
        }
    }

    @Composable
    fun getPeriodStringOfWeek(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
        Log.d(TAG, "getPeriodStringOfWeek executed")

        return buildAnnotatedString {
            if (firstDayOfWeek.year == LocalDate.now().year) {
                append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
            } else {
                append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
            }

            withStyle(
                style = SpanStyle(
                    color = when (firstDayOfWeek.dayOfWeek) {
                        DayOfWeek.SATURDAY -> DeepSkyBlue
                        DayOfWeek.SUNDAY -> OrangeRed
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
            }

            append(") ~ ")

            if (firstDayOfWeek.year != lastDayOfWeek.year) {
                append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
            } else if (firstDayOfWeek.month != lastDayOfWeek.month) {
                append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
            } else {
                append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일 (")))
            }

            withStyle(
                style = SpanStyle(
                    color = when (lastDayOfWeek.dayOfWeek) {
                        DayOfWeek.SATURDAY -> DeepSkyBlue
                        DayOfWeek.SUNDAY -> OrangeRed
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
            }

            append(")")
        }
    }

    fun getFirstDateOfWeek(date: LocalDate): LocalDate {
        Log.d(TAG, "getFirstDateOfWeek executed")

        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    fun getLastDateOfWeek(date: LocalDate): LocalDate {
        Log.d(TAG, "getLastDateOfWeek executed")

        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }
}