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

    private val CURRENT_EXP = userDataSource.CURRENT_EXP
    private val WID_TOTAL_EXP = userDataSource.WID_TOTAL_EXP

    private val now: State<LocalDateTime> = wiDDataSource.now
    val user: State<User?> = userDataSource.user

    // WiD
    val clickedWiD: State<WiD> = wiDDataSource.clickedWiD // 수정 전
    val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy // 수정 후
    val updateClickedWiDFinishToNow: State<Boolean> = wiDDataSource.updateClickedWiDFinishToNow
    val updateClickedWiDCopyFinishToMaxFinish: State<Boolean> = wiDDataSource.updateClickedWiDCopyFinishToMaxFinish
    val isNewWiD: State<Boolean> = derivedStateOf { clickedWiD.value.id == NEW_WID || clickedWiD.value.id == LAST_NEW_WID }
    val isLastNewWiD: State<Boolean> = derivedStateOf { clickedWiD.value.id == LAST_NEW_WID }
    private val _showDeleteWiDDialog = mutableStateOf(false)
    val showDeleteWiDDialog: State<Boolean> = _showDeleteWiDDialog

    // 제목
    val titleModified: State<Boolean> = derivedStateOf { clickedWiD.value.title != clickedWiDCopy.value.title }
    val subTitleModified: State<Boolean> = derivedStateOf { clickedWiD.value.subTitle != clickedWiDCopy.value.subTitle }
    val titleExist: State<Boolean> = derivedStateOf { clickedWiDCopy.value.title != Title.UNTITLED }

    // 시작
//    val minStart: State<LocalDateTime> = derivedStateOf { getMinStart(clickedWiDCopy.value) }
//    val maxStart: State<LocalDateTime> = derivedStateOf { getMaxStart(clickedWiDCopy.value) }
//    val startEnabled: State<Boolean> = derivedStateOf { setStartEnabled() }
    val startModified: State<Boolean> = derivedStateOf { clickedWiD.value.start != clickedWiDCopy.value.start }

    // 종료
//    val minFinish: State<LocalDateTime> = derivedStateOf { getMinFinish(clickedWiDCopy.value) }
//    val maxFinish: State<LocalDateTime> = derivedStateOf { getMaxFinish(clickedWiDCopy.value) }
//    val finishEnabled: State<Boolean> = derivedStateOf { setFinishEnabled() }
    val finishModified: State<Boolean> = derivedStateOf { clickedWiD.value.finish != clickedWiDCopy.value.finish }

    val cityModified: State<Boolean> = derivedStateOf { clickedWiD.value.city != clickedWiDCopy.value.city }

    // 도구
    val playerState: State<PlayerState> = wiDDataSource.playerState

//    fun setClickedWiDCopy(newClickedWiDCopy: WiD) { // 제목, 시작, 종료, 위치 변경시
//        Log.d(TAG, "setClickedWiDCopy executed")
//
//        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
//    }

    fun setUpdateClickedWiDFinishToNow(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDToNow executed")

        wiDDataSource.setUpdateClickedWiDFinishToNow(update = update)
    }

    fun setUpdateClickedWiDCopyFinishToNow(update: Boolean) {
        Log.d(TAG, "setUpdateClickedWiDCopyFinishToNow executed")

        wiDDataSource.setUpdateClickedWiDCopyFinishToMaxFinish(update = update)
    }

    fun setShowDeleteWiDDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteWiDDialog executed")

        _showDeleteWiDDialog.value = show
    }

    fun createWiD() { // 새로운 기록 용
        Log.d(TAG, "createWiD executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.createWiD(email = currentUser.email)
    }

    fun updateWiD() { // 기존 기록 용
        Log.d(TAG, "updateWiD executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.updateWiD(email = currentUser.email)
    }

    fun deleteWiD() { // 삭제할 때는 wiD를 사용해야 함, 기존 기록 용
        Log.d(TAG, "deleteWiD executed")

        val currentUser = user.value ?: return // 잘못된 접근

        val expToDelete = clickedWiD.value.exp
        var updatedUserDocument: MutableMap<String, Any>? = null

        if (0 < expToDelete) {
            val currentExp = currentUser.currentExp
            val wiDTotalExp = currentUser.wiDTotalExp
            val prevWiDTotalExp = wiDTotalExp - expToDelete
            val prevCurrentExp = currentExp - expToDelete // 마이너스 나올 수 있음.

            updatedUserDocument = mutableMapOf() // null이면 새로 초기화
            updatedUserDocument[CURRENT_EXP] = prevCurrentExp
            updatedUserDocument[WID_TOTAL_EXP] = prevWiDTotalExp
        }

        wiDDataSource.deleteWiD(
            email = currentUser.email,
            updatedUserDocument = updatedUserDocument,
            onResult = { success: Boolean ->
                if (success && updatedUserDocument != null) { // 유저 갱신
                    userDataSource.updateUser(updatedUserDocument = updatedUserDocument)
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

//    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
//        Log.d(TAG, "getTimeString executed")
//
//        return wiDDataSource.getTimeString(time = time)
//    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
//        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(
            currentDateTime = now.value,
            dateTime = dateTime
        )
    }
}