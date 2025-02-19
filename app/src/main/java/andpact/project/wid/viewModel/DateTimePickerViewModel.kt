package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.PlayerState
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
//    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "DateTimePickerView"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

//    private val user: State<User?> = userDataSource.user

    private val NEW_WID = wiDDataSource.NEW_WID
    private val LAST_NEW_WID = wiDDataSource.LAST_NEW_WID

    val START = wiDDataSource.START
    val FINISH = wiDDataSource.FINISH

    private val initialToday = LocalDate.now()
    val now = wiDDataSource.now

    private val clickedWiD: State<WiD> = wiDDataSource.clickedWiD
    val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy
    val updateClickedWiDCopyToNow: State<Boolean> = wiDDataSource.updateClickedWiDCopyFinishToNow
    val isLastNewWiD: State<Boolean> = derivedStateOf { clickedWiD.value.id == LAST_NEW_WID }

    // 뷰가 나타나면 이전과 이후 기록이 계산됨.
    private val previousWiD: State<WiD?> = derivedStateOf { getPreviousWiD() }
    private val nextWiD: State<WiD?> = derivedStateOf { getNextWiD() }

    // TODO: derivedStateOf 내부 메서드에 파라미터 전달하기
//    private val _startDatePickerMidDate = mutableStateOf(initialToday)
//    val startDatePickerMidDate: State<LocalDate> = _startDatePickerMidDate
    private val _startCurrentDate = mutableStateOf(initialToday)
    val startCurrentDate: State<LocalDate> = _startCurrentDate
    val minStart: State<LocalDateTime> = derivedStateOf { getMinStart(clickedWiDCopy.value) }
    val maxStart: State<LocalDateTime> = derivedStateOf { getMaxStart(clickedWiDCopy.value) }
    val startEnabled: State<Boolean> = derivedStateOf { setStartEnabled() }

//    private val _finishDatePickerMidDate = mutableStateOf(initialToday)
//    val finishDatePickerMidDate: State<LocalDate> = _finishDatePickerMidDate
    private val _finishCurrentDate = mutableStateOf(initialToday)
    val finishCurrentDate: State<LocalDate> = _finishCurrentDate
    private val _finishDate = mutableStateOf(clickedWiDCopy.value.finish.toLocalDate())
    val finishDate: State<LocalDate> = _finishDate
    val minFinish: State<LocalDateTime> = derivedStateOf { getMinFinish(clickedWiDCopy.value) }
    val maxFinish: State<LocalDateTime> = derivedStateOf { getMaxFinish(clickedWiDCopy.value) }
    val finishEnabled: State<Boolean> = derivedStateOf { setFinishEnabled() }

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

//    fun setStartDatePickerMidDate(newStartDatePickerMidDate: LocalDate) {
//        Log.d(TAG, "setStartDatePickerMidDate executed")
//
//        _startDatePickerMidDate.value = newStartDatePickerMidDate
//    }

    fun setStartCurrentDate(newStartCurrentDate: LocalDate) {
        Log.d(TAG, "setStartCurrentDate executed")

        _startCurrentDate.value = newStartCurrentDate
    }

//    fun setFinishDatePickerMidDate(newFinishDatePickerMidDate: LocalDate) {
//        Log.d(TAG, "setFinishDatePickerMidDate executed")
//
//        _finishDatePickerMidDate.value = newFinishDatePickerMidDate
//    }

    fun setFinishCurrentDate(newFinishCurrentDate: LocalDate) {
        Log.d(TAG, "setFinishCurrentDate executed")

        _finishCurrentDate.value = newFinishCurrentDate
    }

    private fun setStartEnabled(): Boolean {
        Log.d(TAG, "setStartEnabled executed")

        val startTime = clickedWiDCopy.value.start
        return startTime < minStart.value || maxStart.value < startTime
    }

    private fun setFinishEnabled(): Boolean {
        Log.d(TAG, "setFinishEnabled executed")

        val finishTime = clickedWiDCopy.value.finish
        return finishTime < minFinish.value || maxFinish.value < finishTime
    }

    private fun getPreviousWiD(): WiD? {
        Log.d(TAG, "getPreviousWiD executed")

        val currentWiD = clickedWiD.value
        // 검색 범위: currentWiD.finish - 12시간 ~ currentWiD.finish
        val searchStartTime = currentWiD.finish.minusHours(12)
        val searchEndTime = currentWiD.finish

        // 해당 시간 구간에 속하는 기록들을 모으기
        val records = mutableListOf<WiD>()
        var date = searchStartTime.toLocalDate()
        val endDate = searchEndTime.toLocalDate()
        while (!date.isAfter(endDate)) {
            val year = Year.of(date.year)
            // 해당 연도, 날짜의 기록을 가져오기 (없으면 null)
            val dayRecords = wiDDataSource.yearDateWiDListMap.value[year]?.get(date)
            if (dayRecords != null) {
                records.addAll(dayRecords)
            }
            date = date.plusDays(1)
        }

        // finish 시간이 [searchStartTime, searchEndTime]에 속하는 기록들만 필터링
        val validRecords = records.filter { record ->
            record.finish.isAfter(searchStartTime) && (record.finish.isBefore(searchEndTime) || record.finish == searchEndTime)
        }

        // 종료 시간이 가장 늦은 기록을 반환 (즉, 현재 기록 종료 시간에 가장 가까운 기록)
        return validRecords.maxByOrNull { it.finish }
    }

    private fun getNextWiD(): WiD? {
        Log.d(TAG, "getNextWiD executed")

        val currentWiD = clickedWiD.value
        // 검색 범위: currentWiD.start ~ currentWiD.start + 12시간
        val searchStartTime = currentWiD.start
        val searchEndTime = currentWiD.start.plusHours(12)

        val records = mutableListOf<WiD>()
        var date = searchStartTime.toLocalDate()
        val endDate = searchEndTime.toLocalDate()
        while (!date.isAfter(endDate)) {
            val year = Year.of(date.year)
            val dayRecords = wiDDataSource.yearDateWiDListMap.value[year]?.get(date)
            if (dayRecords != null) {
                records.addAll(dayRecords)
            }
            date = date.plusDays(1)
        }

        // start 시간이 (searchStartTime, searchEndTime]에 속하는 기록들만 필터링
        val validRecords = records.filter { record ->
            record.start.isAfter(searchStartTime) && (record.start.isBefore(searchEndTime) || record.start == searchEndTime)
        }

        // 시작 시간이 가장 빠른 기록을 반환 (즉, 현재 기록 시작 시간에 가장 가까운 기록)
        return validRecords.minByOrNull { it.start }
    }

    private fun getMinStart(clickedWiDCopy: WiD): LocalDateTime {
        Log.d(TAG, "getMinStart executed")
        
        // 직전 기록의 종료 시간 아니면 현재 기록의 종료 시간 -12시간
        return previousWiD.value?.finish ?: clickedWiDCopy.finish.minusHours(12)
    }

    private fun getMaxStart(clickedWiDCopy: WiD): LocalDateTime {
        Log.d(TAG, "getMaxStart executed")

        // TODO: 기록 크기 제한 사용해서 설정할 것
        return clickedWiDCopy.finish.minusSeconds(1) // 마이너스 소요 시간이 안 나오도록
    }

    private fun getMinFinish(clickedWiDCopy: WiD): LocalDateTime {
        Log.d(TAG, "getMinFinish executed")

        // TODO: 기록 크기 제한 사용해서 설정할 것
        return clickedWiDCopy.start.plusSeconds(1) // 마이너스 소요 시간이 안 나오도록
    }

    private fun getMaxFinish(clickedWiDCopy: WiD): LocalDateTime {
        Log.d(TAG, "getMaxFinish executed")

//        val now = LocalDateTime.now()

        // 최대 종료 시간 제한을 동적으로 사용하면 시작 시간도 변동하니 실시간을 사용하지 않음.
        return when (clickedWiDCopy.id) {
            LAST_NEW_WID -> now.value // 마지막 새 기록일 때
            else -> nextWiD.value?.start ?: minOf(now.value, clickedWiDCopy.start.plusHours(12)) // 기존 혹은 새 기록일 때
//            LAST_NEW_WID -> now // 마지막 새 기록일 때
//            else -> nextWiD.value?.start ?: minOf(now, clickedWiDCopy.value.start.plusHours(12)) // 기존 혹은 새 기록일 때
        }
    }

    fun setUpdateClickedWiDCopyStartToNowMinus12Hours(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDCopyStartToNowMinus12Hours executed")

        wiDDataSource.setUpdateClickedWiDCopyStartToNowMinus12Hours(update = update)
    }

    fun setUpdateClickedWiDCopyFinishToNow(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDCopyFinishToNow executed")

        wiDDataSource.setUpdateClickedWiDCopyFinishToNow(update = update)
    }

//    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
//        Log.d(TAG, "getTimeString executed")
//
//        return wiDDataSource.getTimeString(time = time)
//    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(dateTime = dateTime)
    }
}