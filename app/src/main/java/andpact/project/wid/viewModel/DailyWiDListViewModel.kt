package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class DailyWiDListViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "DailyWiDListViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val WID_LIST_LIMIT_PER_DAY = wiDDataSource.WID_LIST_LIMIT_PER_DAY

    val NEW_WID = wiDDataSource.NEW_WID
    val LAST_NEW_WID = wiDDataSource.LAST_NEW_WID
    val CURRENT_WID = wiDDataSource.CURRENT_WID

    val today: State<LocalDate> = wiDDataSource.today
    val now: State<LocalTime> = wiDDataSource.now

    // 유저
    private val user: State<User?> = userDataSource.user

    // 날짜
    private val initialToday = LocalDate.now()
    private val _currentDate = mutableStateOf(initialToday)
    val currentDate: State<LocalDate> = _currentDate
    private val _showDatePicker = mutableStateOf(false)
    val showDatePicker: State<Boolean> = _showDatePicker

    private val _dayPickerMidDateOfCurrentMonth = mutableStateOf(initialToday.withDayOfMonth(15))
    val dayPickerMidDateOfCurrentMonth: State<LocalDate> = _dayPickerMidDateOfCurrentMonth
    private val _dayPickerCurrentDate = mutableStateOf(initialToday)
    val dayPickerCurrentDate: State<LocalDate> = _dayPickerCurrentDate

    // 도구
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    // Full WiD List
    val fullWiDList: State<List<WiD>> = derivedStateOf { updateFullWiDList() } // 이 블럭 안의 State 변수가 변화하면 영향을 받음

    // 합계
    val totalDurationMap: State<Map<Title, Duration>> = derivedStateOf { getWiDTitleTotalDurationMap() }

    fun setShowDatePicker(show: Boolean) {
        Log.d(TAG, "setShowDatePicker executed")

        _showDatePicker.value = show
    }

    fun setCurrentDate(newDate: LocalDate) {
        Log.d(TAG, "setCurrentDate executed")

        _currentDate.value = newDate

        val currentUser = user.value ?: return

        wiDDataSource.getYearlyWiDListMap(
            email = currentUser.email,
            year = Year.of(newDate.year)
        )
    }

    fun setDayPickerCurrentDate(newDayPickerCurrentDate: LocalDate) {
        Log.d(TAG, "setDayPickerCurrentDate executed")

        _dayPickerCurrentDate.value = newDayPickerCurrentDate

    }

    fun setDayPickerMidDateOfCurrentMonth(newDayPickerMidDateOfCurrentMonth: LocalDate) {
        Log.d(TAG, "setDayPickerMidDateOfCurrentMonth executed")

        _dayPickerMidDateOfCurrentMonth.value = newDayPickerMidDateOfCurrentMonth
    }

    fun setClickedWiDAndCopy(clickedWiD: WiD) {
        Log.d(TAG, "setClickedWiDAndCopy executed")

        val currentUser = user.value ?: return

        val updatedClickedWiD = clickedWiD.copy(city = currentUser.city)

        wiDDataSource.setClickedWiDAndCopy(clickedWiD = updatedClickedWiD)
    }

    fun getDurationString(duration: Duration): String { // 'H시간 m분 s초'
//        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    private fun updateFullWiDList(): List<WiD> {
        Log.d(TAG, "fullWiDList updated")

        val currentDate = _currentDate.value
        val now = if (currentToolState.value == CurrentToolState.STARTED) { null } else { now.value }

        val wiDList = wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentDate.year), emptyMap())
            .getOrDefault(currentDate, emptyList())

        return wiDDataSource.getFullWiDListFromWiDList(
            date = currentDate,
            wiDList = wiDList,
            today = today.value,
            currentTime = now
        )
    }

    private fun getWiDTitleTotalDurationMap(): Map<Title, Duration> {
        Log.d(TAG, "getWiDTitleTotalDurationMap executed")

        val currentDate = _currentDate.value
        val wiDList = wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentDate.year), emptyMap())
            .getOrDefault(currentDate, emptyList())

        return wiDDataSource.getWiDTitleTotalDurationMap(wiDList = wiDList)
    }

    fun getDurationPercentageStringOfDay(duration: Duration): String {
//        Log.d(TAG, "getDurationPercentageStringOfDay executed")

        val totalSecondsInDay = 24 * 60 * 60
        val durationInSeconds = duration.seconds

        val percentage = (durationInSeconds.toFloat() / totalSecondsInDay) * 100

        return if (percentage % 1.0 == 0.0) {
            "${percentage.toInt()}%"
        } else {
            "${String.format("%.1f", percentage)}%"
        }
    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
//        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
//        Log.d(TAG, "getTimeString executed")

        return wiDDataSource.getTimeString(time = time)
    }
}