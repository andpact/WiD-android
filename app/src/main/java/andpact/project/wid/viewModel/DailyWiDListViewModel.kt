package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.*
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
import javax.inject.Inject

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

    val now: State<LocalDateTime> = wiDDataSource.now

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
    val playerState: State<PlayerState> = wiDDataSource.playerState

    // Full WiD List
    val fullWiDList: State<List<WiD>> = derivedStateOf { getFullWiDList() } // 이 블럭 안의 State 변수가 변화하면 영향을 받음

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

        wiDDataSource.getWiD(
            email = currentUser.email,
            year = Year.of(newDate.year),
            onResult = { snackbarActionResult: SnackbarActionResult ->
                if (snackbarActionResult == SnackbarActionResult.FAIL_SERVER_ERROR) {
                    // TODO: 실패 시 스낵 바 띄우기
                }
            }
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

    private fun getFullWiDList(): List<WiD> {
        Log.d(TAG, "getFullWiDList updated")

        // TODO: 파이 차트 코드 안에서 날짜에 포함되지 않는 앞 뒤 자르기.
        // TODO: 리스트에서는 자르지 않고 표시(전날 3시간 30분 + 당일 3시간 30분) 
        val currentDate = _currentDate.value
        val currentTime = if (playerState.value == PlayerState.STARTED) { null } else { now.value }

        val wiDList = wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentDate.year), emptyMap())
            .getOrDefault(currentDate, emptyList())

        return wiDDataSource.getFullWiDList(
            date = currentDate,
            wiDList = wiDList,
            today = now.value.toLocalDate(),
            currentTime = currentTime
        )
    }

    private fun getWiDTitleTotalDurationMap(): Map<Title, Duration> {
//        Log.d(TAG, "getWiDTitleTotalDurationMap executed")

        val currentDate = _currentDate.value
        val startOfDay = currentDate.atStartOfDay()
//        val endOfDay = currentDate.atTime(LocalTime.MAX.withNano(0))
        val endOfDay = currentDate.atTime(LocalTime.MAX) // TODO: 나노 세컨드를 없애서 반올림하면?

        val wiDList: List<WiD> = wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentDate.year), emptyMap())
            .getOrDefault(currentDate, emptyList())

        // 날짜를 벗어난 기록의 시간을 조정
        val adjustedWiDList = wiDList.mapNotNull { wiD: WiD ->
            val adjustedStart = maxOf(wiD.start, startOfDay)
            val adjustedFinish = minOf(wiD.finish, endOfDay)

            if (adjustedStart.isBefore(adjustedFinish)) {
                wiD.copy(
                    start = adjustedStart,
                    finish = adjustedFinish,
                    duration = Duration.between(adjustedStart, adjustedFinish)
                )
            } else {
                null // 시작과 종료 시간이 같거나 잘려서 없어진 경우 제외
            }
        }

        return wiDDataSource.getWiDTitleTotalDurationMap(wiDList = adjustedWiDList)
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

//    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
////        Log.d(TAG, "getTimeString executed")
//
//        return wiDDataSource.getTimeString(time = time)
//    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
//        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(dateTime = dateTime)
    }
}