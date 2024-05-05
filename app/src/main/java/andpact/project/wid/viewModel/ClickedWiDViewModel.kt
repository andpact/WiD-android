package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.WiD
import andpact.project.wid.util.defaultTitleColorMapWithColors
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ClickedWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "ClickedWiDViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email = userDataSource.firebaseUser.value?.email ?: ""

    private val _titleColorMap = mutableStateOf(userDataSource.user.value?.titleColorMap ?: defaultTitleColorMapWithColors)
    val titleColorMap: State<Map<String, Color>> = _titleColorMap

    val clickedWiD: State<WiD> = wiDDataSource.clickedWiD

    private val _showTitleMenu = mutableStateOf(false)
    val showTitleMenu: State<Boolean> = _showTitleMenu

    private val _showStartPicker = mutableStateOf(false)
    val showStartPicker: State<Boolean> = _showStartPicker
    private val _startOverlap = mutableStateOf(false)
    val startOverlap: State<Boolean> = _startOverlap

    private val _showFinishPicker = mutableStateOf(false)
    val showFinishPicker: State<Boolean> = _showFinishPicker
    private val _finishOverlap = mutableStateOf(false)
    val finishOverlap: State<Boolean> = _finishOverlap

    private val _durationExist = mutableStateOf(false)
    val durationExist: State<Boolean> = _durationExist

    private val _showDeleteClickedWiDDialog = mutableStateOf(false)
    val showDeleteClickedWiDDialog: State<Boolean> = _showDeleteClickedWiDDialog

    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())

    fun setClickedWiD(clickedWiD: WiD) {
        Log.d(TAG, "setClickedWiD executed")

        wiDDataSource.setClickedWiD(updatedClickedWiD = clickedWiD)
    }

    fun setShowTitleMenu(show: Boolean) {
        Log.d(TAG, "setShowTitleMenu executed")

        _showTitleMenu.value = show
    }

    fun setShowStartPicker(show: Boolean) {
        Log.d(TAG, "setShowStartPicker executed")

        _showStartPicker.value = show
    }

    private fun setStartOverlap(overlap: Boolean) {
        Log.d(TAG, "setStartOverlap executed")

        _startOverlap.value = overlap
    }

    fun setShowFinishPicker(show: Boolean) {
        Log.d(TAG, "setShowFinishPicker executed")

        _showFinishPicker.value = show
    }

    private fun setFinishOverlap(overlap: Boolean) {
        Log.d(TAG, "setFinishOverlap executed")

        _finishOverlap.value = overlap
    }

    private fun setDurationExist(exist: Boolean) {
        Log.d(TAG, "setDurationExist executed")

        _durationExist.value = exist
    }

    fun setShowDeleteClickedWiDDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteClickedWiDDialog executed")

        _showDeleteClickedWiDDialog.value = show
    }

    fun checkDurationExist() {
        Log.d(TAG, "checkDurationExist executed")

        setDurationExist(Duration.ZERO < clickedWiD.value.duration)
    }

    fun checkNewStartOverlap() { // 생성할 WiD의 시작 시간이 겹치는지 확인
        Log.d(TAG, "checkNewStartOverlap executed")

        val today = LocalDate.now()
        val now = LocalTime.now()

        for (existingWiD in _wiDList.value) {
            if (clickedWiD.value == existingWiD) {
                continue
            }

            if (existingWiD.start < clickedWiD.value.start && clickedWiD.value.start < existingWiD.finish) {
                setStartOverlap(overlap = true)
                break
            } else if (clickedWiD.value.date == today && now < clickedWiD.value.start) {
                setStartOverlap(overlap = true)
                break
            } else {
                setStartOverlap(overlap = false)
            }
        }
    }

    fun checkNewFinishOverlap() { // 생성할 WiD의 종료 시간이 겹치는지 확인
        Log.d(TAG, "checkNewFinishOverlap executed")

        val today = LocalDate.now()
        val now = LocalTime.now()

        for (existingWiD in _wiDList.value) {
            if (clickedWiD.value == existingWiD) {
                continue
            }

            if (existingWiD.start < clickedWiD.value.finish && clickedWiD.value.finish < existingWiD.finish) {
                setFinishOverlap(overlap = true)
                break
            } else if (clickedWiD.value.date == today && now < clickedWiD.value.finish) {
                setFinishOverlap(overlap = true)
                break
            } else {
                setFinishOverlap(overlap = false)
            }
        }
    }

    fun checkNewWiDOverlap() { // 생성할 WiD가 기존의 WiD를 덮고 있는지 확인
        Log.d(TAG, "checkNewWiDOverlap executed")

        for (existingWiD in _wiDList.value) {
            if (clickedWiD.value == existingWiD) {
                continue
            }

            // 등호를 넣어서 부등호를 사용해야 기존의 WiD를 덮고 있는지를 정확히 확인할 수 있다.
            if (clickedWiD.value.start <= existingWiD.start && existingWiD.finish <= clickedWiD.value.finish) {
                setStartOverlap(overlap = true)
                setFinishOverlap(overlap = true)
                break
            } else {
                setStartOverlap(overlap = false)
                setFinishOverlap(overlap = false)
            }
        }
    }

    fun setWiDList(currentDate: LocalDate) {
        Log.d(TAG, "setWiDList executed")

        wiDDataSource.getWiDListByDate(email = email, date = currentDate) { wiDList ->
            _wiDList.value = wiDList
        }
    }

    fun updateClickedWiD(onUpdateWiDSuccess: () -> Unit) {
        Log.d(TAG, "updateClickedWiD executed")

        wiDDataSource.updateClickedWiD(email = email) {
            onUpdateWiDSuccess()
        }
    }

    fun deleteCLickedWiD(onDeleteWiDSuccess: () -> Unit) {
        Log.d(TAG, "deleteCLickedWiD executed")

        wiDDataSource.deleteCLickedWiD(email = email) {
            onDeleteWiDSuccess()
        }
    }
}