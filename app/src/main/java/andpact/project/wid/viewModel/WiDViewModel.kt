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

    val START = wiDDataSource.START
    val FINISH = wiDDataSource.FINISH

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
    val titleModified: State<Boolean> = derivedStateOf { clickedWiD.value.title != clickedWiDCopy.value.title }
    val subTitleModified: State<Boolean> = derivedStateOf { clickedWiD.value.subTitle != clickedWiDCopy.value.subTitle }
    val titleExist: State<Boolean> = derivedStateOf { clickedWiDCopy.value.title != Title.UNTITLED }

    // 시작
    val startModified: State<Boolean> = derivedStateOf{ clickedWiD.value.start != clickedWiDCopy.value.start }
    val finishModified: State<Boolean> = derivedStateOf{ clickedWiD.value.finish != clickedWiDCopy.value.finish }
    val cityModified: State<Boolean> = derivedStateOf { clickedWiD.value.city != clickedWiDCopy.value.city }

    // 도구
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    fun setClickedWiDCopy(newClickedWiDCopy: WiD) { // 제목, 시작, 종료, 위치 변경시
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

    fun setShowDeleteWiDDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteWiDDialog executed")

        _showDeleteWiDDialog.value = show
    }

    fun createWiD(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) { // 새로운 기록 용
        Log.d(TAG, "createWiD executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.createWiD(
            email = currentUser.email,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            }
        )
    }

    fun updateWiD(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) { // 기존 기록 용
        Log.d(TAG, "updateWiD executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.updateWiD(
            email = currentUser.email,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            }
        )
    }

    fun deleteWiD(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) { // 삭제할 때는 wiD를 사용해야 함, 기존 기록 용
        Log.d(TAG, "deleteWiD executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.deleteWiD(
            email = currentUser.email,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            },
            onWiDDeleted = { deletedExp: Int ->
                if (0 < deletedExp) {
                    val currentExp = currentUser.currentExp
                    val exp = clickedWiD.value.exp
                    val wiDTotalExp = currentUser.wiDTotalExp
                    val prevWiDTotalExp = wiDTotalExp - exp
                    val prevCurrentExp = currentExp - exp // 마이너스 나올 수 있음.

                    userDataSource.deleteWiD(
                        newCurrentExp = prevCurrentExp,
                        newWiDTotalExp = prevWiDTotalExp
                    )
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