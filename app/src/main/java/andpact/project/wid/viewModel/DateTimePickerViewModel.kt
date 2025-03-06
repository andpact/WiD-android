package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class DateTimePickerViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "DateTimePickerView"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val user: State<User?> = userDataSource.user

    private val LAST_NEW_WID = wiDDataSource.LAST_NEW_WID

    private val initialToday = LocalDate.now()
    private val now = wiDDataSource.now

    val clickedWiD: State<WiD> = wiDDataSource.clickedWiD // 원본
    val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy // 사본
    val isLastNewWiD: State<Boolean> = derivedStateOf { clickedWiDCopy.value.id == LAST_NEW_WID }

    private val _currentDate = mutableStateOf(initialToday)
    val currentDate: State<LocalDate> = _currentDate

    private val _startCurrentDate = mutableStateOf(initialToday)
    val startCurrentDate: State<LocalDate> = _startCurrentDate
    val minStart: State<LocalDateTime> = derivedStateOf { getMinStart(clickedWiDCopy.value) }

    private val _finishCurrentDate = mutableStateOf(initialToday)
    val finishCurrentDate: State<LocalDate> = _finishCurrentDate
    private val _finishDate = mutableStateOf(clickedWiDCopy.value.finish.toLocalDate())
    val finishDate: State<LocalDate> = _finishDate
    val maxFinish: State<LocalDateTime> = derivedStateOf { getMaxFinish(clickedWiDCopy.value) }
    val updateMaxFinish = derivedStateOf { maxFinish.value == now.value }
    private val _applyMaxFinish = mutableStateOf(false)
    val applyMaxFinish: State<Boolean> = _applyMaxFinish

    val playerState: State<PlayerState> = wiDDataSource.playerState

    fun setClickedWiDCopyStart(newStart: LocalDateTime) {
        Log.d(TAG, "setClickedWiDCopyStart executed")

        val currentClickedWiDCopy = clickedWiDCopy.value

        val newClickedWiDCopy = currentClickedWiDCopy.copy(
            start = newStart,
            duration = Duration.between(newStart, currentClickedWiDCopy.finish)
        )

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
    }

    fun setClickedWiDCopyFinish(newFinish: LocalDateTime) {
        Log.d(TAG, "setClickedWiDCopyFinish executed")

        val currentClickedWiDCopy = clickedWiDCopy.value

        val newClickedWiDCopy = currentClickedWiDCopy.copy(
            finish = newFinish,
            duration = Duration.between(currentClickedWiDCopy.start, newFinish)
        )

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
    }

    /** DailyWiDList의 currentDate */
    fun setCurrentDate(newCurrentDate: LocalDate) {
        Log.d(TAG, "setCurrentDate executed")

        _currentDate.value = newCurrentDate
    }

    fun setStartCurrentDate(newStartCurrentDate: LocalDate) {
        Log.d(TAG, "setStartCurrentDate executed")

        _startCurrentDate.value = newStartCurrentDate
    }

    fun setFinishCurrentDate(newFinishCurrentDate: LocalDate) {
        Log.d(TAG, "setFinishCurrentDate executed")

        _finishCurrentDate.value = newFinishCurrentDate
    }

    fun setApplyMaxFinish(apply: Boolean) {
        Log.d(TAG, "setApplyMaxFinish executed")

        _applyMaxFinish.value = apply
    }

    private fun getMinStart(clickedWiDCopy: WiD): LocalDateTime {
        Log.d(TAG, "getMinStart executed")

        val fixedSearchStartDateTime = LocalDateTime.of(_currentDate.value.minusDays(1), LocalTime.NOON) // 조회 전 날짜 -12 시간 전부터
        val searchEndDateTime = clickedWiDCopy.start // 조회 기록의 시작 시간까지

        val records = mutableListOf<WiD>()
        var date = fixedSearchStartDateTime.toLocalDate()
        val endDate = searchEndDateTime.toLocalDate()
        while (!date.isAfter(endDate)) { // 조회 날짜와 그 전 날짜의 기록을 다 가져와서
            val year = Year.of(date.year)
            val dayRecords = wiDDataSource.yearDateWiDListMap.value[year]?.get(date)
            if (dayRecords != null) {
                records.addAll(dayRecords)
            }
            date = date.plusDays(1)
        }

        // 필터링된 기록 중 해당 기록의 종료 시간이 조회 기록의 시작과 가장 가까운 것을 찾음
        val previousWiD = records
            .filter { fixedSearchStartDateTime < it.finish && it.finish <= searchEndDateTime }
            .maxByOrNull { it.finish }

        return previousWiD?.finish ?: fixedSearchStartDateTime
    }

    private fun getMaxFinish(clickedWiDCopy: WiD): LocalDateTime {
        Log.d(TAG, "getMaxFinish executed")

        val searchStartDateTime = clickedWiDCopy.finish // 조회 기록의 종료 시간부터
        val searchEndDateTime = LocalDateTime.of(_currentDate.value.plusDays(1), LocalTime.NOON) // 조회 전 날짜 +12 시간 후까지

        val records = mutableListOf<WiD>()
        var date = searchStartDateTime.toLocalDate()
        val endDate = searchEndDateTime.toLocalDate()
        while (!date.isAfter(endDate)) { // 조회 날짜와 그 다음 날짜의 기록을 다 가져와서
            val year = Year.of(date.year)
            val dayRecords = wiDDataSource.yearDateWiDListMap.value[year]?.get(date)
            if (dayRecords != null) {
                records.addAll(dayRecords)
            }
            date = date.plusDays(1)
        }

        // 필터링된 기록 중 해당 기록의 시작 시간이 조회 기록의 종료과 가장 가까운 것을 찾음
        val nextWiD = records
            .filter { searchStartDateTime <= it.start && it.start < searchEndDateTime }
            .minByOrNull { it.start }

        return nextWiD?.start ?: minOf(now.value, searchEndDateTime)
    }

    fun setUpdateClickedWiDCopyFinishToMaxFinish(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDCopyFinishToMaxFinish executed")

        wiDDataSource.setUpdateClickedWiDCopyFinishToMaxFinish(update = update)
    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
//        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
//        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(
            currentDateTime = now.value,
            dateTime = dateTime
        )
    }

    fun getDurationString(duration: Duration): String {
        return wiDDataSource.getDurationString(duration = duration)
    }
}