package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.Title
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
import java.time.LocalTime
import java.time.Year
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class WiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "WiDViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val NEW_WID = wiDDataSource.NEW_WID
    private val LAST_NEW_WID = wiDDataSource.LAST_NEW_WID

    private val today = wiDDataSource.today
    val now = wiDDataSource.now
    private val user: State<User?> = userDataSource.user

    // WiD
    val clickedWiD: State<WiD> = wiDDataSource.clickedWiD // 수정 전
    val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy // 수정 후
    val updateClickedWiDToNow: State<Boolean> = wiDDataSource.updateClickedWiDToNow
    val updateClickedWiDCopyToNow: State<Boolean> = wiDDataSource.updateClickedWiDCopyToNow
    val isNewWiD: State<Boolean> = derivedStateOf { clickedWiD.value.id == NEW_WID || clickedWiD.value.id == LAST_NEW_WID }
    val isLastNewWiD: State<Boolean> = derivedStateOf { clickedWiD.value.id == LAST_NEW_WID }
    private val _showDeleteWiDDialog = mutableStateOf(false)
    val showDeleteWiDDialog: State<Boolean> = _showDeleteWiDDialog

    // 제목
    private val _showTitleMenu = mutableStateOf(false)
    val showTitleMenu: State<Boolean> = _showTitleMenu
    val titleModified: State<Boolean> = derivedStateOf { clickedWiD.value.title != clickedWiDCopy.value.title }
    val titleExist: State<Boolean> = derivedStateOf { clickedWiD.value.title != Title.UNTITLED }

    // 시작
    private val _showStartPicker = mutableStateOf(false)
    val showStartPicker: State<Boolean> = _showStartPicker
    val minStart: State<LocalTime> = derivedStateOf { getMinStart() }
    val maxStart: State<LocalTime> = derivedStateOf { getMaxStart() }
    val startModified: State<Boolean> = derivedStateOf{ clickedWiD.value.start != clickedWiDCopy.value.start }
    val isStartOutOfRange: State<Boolean> = derivedStateOf { setIsStartOutOfRange() }

    // 종료
    private val _showFinishPicker = mutableStateOf(false)
    val showFinishPicker: State<Boolean> = _showFinishPicker
    val minFinish: State<LocalTime> = derivedStateOf { getMinFinish() }
    val maxFinish: State<LocalTime> = derivedStateOf { getMaxFinish() }
    val finishModified: State<Boolean> = derivedStateOf{ clickedWiD.value.finish != clickedWiDCopy.value.finish }
    val isFinishOutOfRange: State<Boolean> = derivedStateOf { setIsFinishOutOfRange() }

    // 도구
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    // WiD List
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

    fun setClickedWiDCopy(newClickedWiDCopy: WiD) { // 제목, 시작, 종료 변경시
        Log.d(TAG, "setClickedWiDCopy executed")

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
    }

    fun setUpdateClickedWiDToNow(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDToNow executed")

        wiDDataSource.setUpdateClickedWiDToNow(update = update)
    }

    fun setUpdateClickedWiDCopyToNow(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDCopyToNow executed")

        wiDDataSource.setUpdateClickedWiDCopyToNow(update = update)
    }

    fun setShowTitleMenu(show: Boolean) {
        Log.d(TAG, "setShowTitleMenu executed")

        _showTitleMenu.value = show
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

    fun setShowStartPicker(show: Boolean) {
        Log.d(TAG, "setShowStartPicker executed")

        _showStartPicker.value = show
    }

    fun setShowFinishPicker(show: Boolean) {
        Log.d(TAG, "setShowFinishPicker executed")

        _showFinishPicker.value = show
    }

    fun setShowDeleteWiDDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteWiDDialog executed")

        _showDeleteWiDDialog.value = show
    }

    fun createWiD(onWiDCreated: (Boolean) -> Unit) { // 새로운 기록 용
        Log.d(TAG, "createWiD executed")

        wiDDataSource.createWiD(
            email = user.value?.email ?: "",
            onWiDAdded = { success: Boolean ->
                onWiDCreated(success)
            }
        )
    }

    fun updateWiD(onWiDUpdated: (Boolean) -> Unit) { // 기존 기록 용
        Log.d(TAG, "updateWiD executed")

        wiDDataSource.updateWiD(
            email = user.value?.email ?: "",
            onWiDUpdated = { wiDUpdated: Boolean ->
                onWiDUpdated(wiDUpdated)
            }
        )
    }

    fun deleteWiD(onWiDDeleted: (Boolean) -> Unit) { // 삭제할 때는 wiD를 사용해야 함, 기존 기록 용
        Log.d(TAG, "deleteWiD executed")

        wiDDataSource.deleteWiD(
            email = user.value?.email ?: "",
            onWiDDeleted = { wiDDeleted: Boolean ->
                if (wiDDeleted) { // 삭제 성공
                    // 경험치
                    val currentExp = user.value?.currentExp ?: 0
                    val exp = clickedWiD.value.duration.seconds.toInt()
                    val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                    val prevWiDTotalExp = wiDTotalExp - exp
                    val prevCurrentExp = currentExp - exp // 마이너스 나올 수 있음.

                    userDataSource.deleteWiD(
                        newCurrentExp = prevCurrentExp,
                        newWiDTotalExp = prevWiDTotalExp
                    )

                    onWiDDeleted(true)
                } else { // 삭제 실패
                    onWiDDeleted(false)
                }
            }
        )
    }

    fun getDurationString(duration: Duration): String {
        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
        Log.d(TAG, "getTimeString executed")

        return wiDDataSource.getTimeString(time = time)
    }
}