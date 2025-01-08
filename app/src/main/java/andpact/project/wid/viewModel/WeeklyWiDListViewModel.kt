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

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }

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
    val currentMap: State<Map<Title, Duration>> = derivedStateOf { setCurrentMap() }

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