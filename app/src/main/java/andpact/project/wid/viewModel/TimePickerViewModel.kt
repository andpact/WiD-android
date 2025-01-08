package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalTime
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class TimePickerViewModel @Inject constructor(private val wiDDataSource: WiDDataSource): ViewModel() {
    private val TAG = "TimePickerViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val NEW_WID = wiDDataSource.NEW_WID
    private val LAST_NEW_WID = wiDDataSource.LAST_NEW_WID

    val START = wiDDataSource.START
    val FINISH = wiDDataSource.FINISH

    private val today = wiDDataSource.today
    val now = wiDDataSource.now

    val clickedWiD: State<WiD> = wiDDataSource.clickedWiD
    val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy
    val updateClickedWiDCopyToNow: State<Boolean> = wiDDataSource.updateClickedWiDCopyToNow
    val isLastNewWiD: State<Boolean> = derivedStateOf { clickedWiD.value.id == LAST_NEW_WID }

    val minStart: State<LocalTime> = derivedStateOf { getMinStart() }
    val maxStart: State<LocalTime> = derivedStateOf { getMaxStart() }
    val isStartOutOfRange: State<Boolean> = derivedStateOf { setIsStartOutOfRange() }

    val minFinish: State<LocalTime> = derivedStateOf { getMinFinish() }
    val maxFinish: State<LocalTime> = derivedStateOf { getMaxFinish() }
    val isFinishOutOfRange: State<Boolean> = derivedStateOf { setIsFinishOutOfRange() }

    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    private val fullWiDList: State<List<WiD>> = derivedStateOf { updateFullWiDList() }

    private fun updateFullWiDList(): List<WiD> {
        Log.d(TAG, "fullWiDList updated")

        val date = clickedWiD.value.date // date가 오늘 날짜면 기록 리스트가 계속 갱신되기 때문에 반영되도록 해줌.
        val wiDList = wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(date.year), emptyMap())
            .getOrDefault(date, emptyList())
        val now = if (currentToolState.value == CurrentToolState.STARTED) { null } else { now.value }

        return wiDDataSource.getFullWiDListFromWiDList(
            date = date,
            wiDList = wiDList,
            today = today.value,
            currentTime = now
        )
    }

    fun setClickedWiDCopyStart(newStart: LocalTime) {
        Log.d(TAG, "setClickedWiDCopyStart executed")

        val currentClickedWiDCopy = clickedWiDCopy.value

        val newClickedWiDCopy = currentClickedWiDCopy.copy(
            start = newStart,
            duration = Duration.between(newStart, currentClickedWiDCopy.finish)
        )

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
    }

    fun setClickedWiDCopyFinish(newFinish: LocalTime) {
        Log.d(TAG, "setClickedWiDCopyFinish executed")

        val currentClickedWiDCopy = clickedWiDCopy.value

        val newClickedWiDCopy = currentClickedWiDCopy.copy(
            finish = newFinish,
            duration = Duration.between(currentClickedWiDCopy.start, newFinish)
        )

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
    }

    private fun setIsStartOutOfRange(): Boolean {
        Log.d(TAG, "setIsStartOutOfRange executed")

        val startTime = clickedWiDCopy.value.start
        return startTime < minStart.value || maxStart.value < startTime
    }

    private fun setIsFinishOutOfRange(): Boolean {
        Log.d(TAG, "setIsFinishOutOfRange executed")

        val finishTime = clickedWiDCopy.value.finish
        return finishTime < minFinish.value || maxFinish.value < finishTime
    }

    private fun getPreviousWiD(): WiD? {
        Log.d(TAG, "getPreviousWiD executed")

        val currentWiD = clickedWiD.value
        val sortedWiDList = fullWiDList.value.filterNot { it.id == LAST_NEW_WID }.sortedBy { it.start }

        val currentIndex = sortedWiDList.indexOfFirst { it == currentWiD }
        return if (currentIndex > 0) sortedWiDList[currentIndex - 1] else null
    }

    private fun getNextWiD(): WiD? {
        Log.d(TAG, "getNextWiD executed")

        val currentWiD = clickedWiD.value
        val sortedWiDList = fullWiDList.value.filterNot { it.id == LAST_NEW_WID }.sortedBy { it.start }

        val currentIndex = sortedWiDList.indexOfFirst { it == currentWiD }
        return if (currentIndex >= 0 && currentIndex < sortedWiDList.size - 1) sortedWiDList[currentIndex + 1] else null
    }

    private fun getMinStart(): LocalTime {
        Log.d(TAG, "getMinStart executed")

//        val currentWiD = clickedWiD.value
//        val previousWiD = getPreviousWiD()

//        return when (currentWiD.id) {
//            LAST_NEW_WID -> previousWiD?.finish ?: LocalTime.MIN
//            else -> previousWiD?.finish ?: LocalTime.MIN
//        }

        val previousWiD = getPreviousWiD()
        return previousWiD?.finish ?: LocalTime.MIN
    }

    private fun getMaxStart(): LocalTime {
        Log.d(TAG, "getMaxStart executed")

//        val currentWiD = clickedWiD.value
//        val nextWiD = getNextWiD()

//        return when (currentWiD.id) {
//            LAST_NEW_WID -> now.value.minusSeconds(1)
//            else -> nextWiD?.start?.minusSeconds(1) ?: LocalTime.MAX.withNano(0).minusSeconds(1) /** 꼭 1초가 아니고 사용자 설정 값이 들어가도록 */
//        }

        return clickedWiDCopy.value.finish.minusSeconds(1) // 마이너스 소요 시간이 안나오도록
    }

    private fun getMinFinish(): LocalTime {
        Log.d(TAG, "getMinFinish executed")

//        val currentWiD = clickedWiD.value
//        val previousWiD = getPreviousWiD()
//
//        return when (currentWiD.id) {
//            LAST_NEW_WID -> (previousWiD?.finish ?: LocalTime.MIN).plusSeconds(1)
//            else -> (previousWiD?.finish ?: LocalTime.MIN).plusSeconds(1)
//        }

//        val previousWiD = getPreviousWiD()
//        return (previousWiD?.finish ?: LocalTime.MIN).plusSeconds(1) /** 꼭 1초가 아니고 사용자 설정 값이 들어가도록 */

        return clickedWiDCopy.value.start.plusSeconds(1) // 마이너스 소요 시간이 안나오도록
    }

    private fun getMaxFinish(): LocalTime {
        Log.d(TAG, "getMaxFinish executed")

        val currentWiD = clickedWiD.value
        val nextWiD = getNextWiD()

        return when (currentWiD.id) {
            LAST_NEW_WID -> now.value
            else -> nextWiD?.start ?: LocalTime.MAX.withNano(0)
        }
    }

    fun setUpdateClickedWiDCopyToNow(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDCopyToNow executed")

        wiDDataSource.setUpdateClickedWiDCopyToNow(update = update)
    }

    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
        Log.d(TAG, "getTimeString executed")

        return wiDDataSource.getTimeString(time = time)
    }
}