package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.*
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
import java.time.*
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
    val now: State<LocalDateTime> = wiDDataSource.now
    private val initialToday = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfWeek(initialToday)) // 현재 조회 중인 주의 시작 날짜
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(initialToday)) // 현재 조회 중인 주의 종료 날짜
    val finishDate: State<LocalDate> = _finishDate

    private val _weekPickerExpanded = mutableStateOf(false)
    val weekPickerExpanded: State<Boolean> = _weekPickerExpanded

    private val _weekPickerMidDateOfCurrentMonth = mutableStateOf(_startDate.value.plusDays(3).withDayOfMonth(15))
    val weekPickerMidDateOfCurrentMonth: State<LocalDate> = _weekPickerMidDateOfCurrentMonth
    val weekPickerStartDateOfCurrentMonth = derivedStateOf { getFirstDateOfWeek(_weekPickerMidDateOfCurrentMonth.value.withDayOfMonth(1)) } // 해당 월의 1일을 포함하는 가장 가까운 이전 월요일
    val weekPickerFinishDateOfCurrentMonth = derivedStateOf { getLastDateOfWeek(_weekPickerMidDateOfCurrentMonth.value.withDayOfMonth(_weekPickerMidDateOfCurrentMonth.value.lengthOfMonth())) } // 해당 월의 마지막 날짜를 포함하는 가장 가까운 이후 일요일

    private val _weekPickerStartDateOfCurrentWeek = mutableStateOf(_startDate.value)
    val weekPickerStartDateOfCurrentWeek: State<LocalDate> = _weekPickerStartDateOfCurrentWeek
    private val _weekPickerFinishDateOfCurrentWeek = mutableStateOf(_finishDate.value)
    val weekPickerFinishDateOfCurrentWeek: State<LocalDate> = _weekPickerFinishDateOfCurrentWeek

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }

    val titleDurationMapList = TitleDurationMap.values().toList()

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
                year = Year.of(year)
            )
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
            val dayEnd = currentDate.plusDays(1).atStartOfDay()

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

    fun setWeekPickerExpanded(expand: Boolean) {
        Log.d(TAG, "setWeekPickerExpanded executed")

        _weekPickerExpanded.value = expand
    }

    fun setWeekPickerMidDateOfCurrentMonth(newWeekPickerMidDateOfCurrentMonth: LocalDate) {
        Log.d(TAG, "setWeekPickerMidDateOfCurrentMonth executed")

        _weekPickerMidDateOfCurrentMonth.value = newWeekPickerMidDateOfCurrentMonth
    }

    fun setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek(
        newWeekPickerStartDateOfCurrentWeek: LocalDate,
        newWeekPickerFinishDateOfCurrentWeek: LocalDate
    ) {
        Log.d(TAG, "setWeekPickerStartDateOfCurrentWeekAndWeekPickerFinishDateOfCurrentWeek executed")

        _weekPickerStartDateOfCurrentWeek.value = newWeekPickerStartDateOfCurrentWeek
        _weekPickerFinishDateOfCurrentWeek.value = newWeekPickerFinishDateOfCurrentWeek
    }

    fun getDurationString(duration: Duration): String {
        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    fun getDurationPercentageStringOfWeek(duration: Duration): String {
        val totalSecondsInWeek = 7 * 24 * 60 * 60
        val durationInSeconds = duration.seconds

        val percentage = (durationInSeconds.toFloat() / totalSecondsInWeek) * 100

        val tenTimesPercentage = (percentage * 10).toInt()

        return if (tenTimesPercentage % 10 == 0) { // 소수점 첫째 자리 숫자 확인
            "${percentage.toInt()}%" // 소수점 제거
        } else {
            "${tenTimesPercentage / 10f}%" // 소수점 첫째 자리까지 표시
        }
    }

    @Composable
    fun getWeekString(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
        Log.d(TAG, "getWeekString executed")

        return buildAnnotatedString {
            if (firstDayOfWeek.year == LocalDate.now().year) {
                append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
            } else {
                append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
            }

            withStyle(
                style = SpanStyle(
                    color = when (firstDayOfWeek.dayOfWeek) {
                        DayOfWeek.SATURDAY -> MaterialTheme.colorScheme.onTertiaryContainer
                        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onErrorContainer
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
                        DayOfWeek.SATURDAY -> MaterialTheme.colorScheme.onTertiaryContainer
                        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onErrorContainer
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

    fun getMidDateOfWeek(date: LocalDate): LocalDate {
        Log.d(TAG, "getMidDateOfWeek executed")

        return getFirstDateOfWeek(date = date).plusDays(3)
    }

    fun getLastDateOfWeek(date: LocalDate): LocalDate {
        Log.d(TAG, "getLastDateOfWeek executed")

        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }
}