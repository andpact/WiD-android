package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.WiD
import andpact.project.wid.util.defaultTitleColorMapWithColors
import andpact.project.wid.util.getFullWiDListFromWiDList
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class WiDListViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
): ViewModel() {
    private val TAG = "WiDListViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email: String = userDataSource.firebaseUser.value?.email ?: ""
//    private val title = userDataSource.user.value.

    private val _titleColorMap = mutableStateOf(userDataSource.user.value?.titleColorMap ?: defaultTitleColorMapWithColors)
    val titleColorMap: State<Map<String, Color>> = _titleColorMap

    // 날짜
    private val today: LocalDate = LocalDate.now()
    private val _currentDate = mutableStateOf(today)
    val currentDate: State<LocalDate> = _currentDate
    private val _showDatePicker = mutableStateOf(false)
    val showDatePicker: State<Boolean> = _showDatePicker

    // WiD
    private val _wiDListGet = mutableStateOf(false)
    val wiDListGet: State<Boolean> = _wiDListGet
    private val _fullWiDList = mutableStateOf<List<WiD>>(emptyList())
    val fullWiDList: State<List<WiD>> = _fullWiDList

    fun setShowDatePicker(show: Boolean) {
        Log.d(TAG, "setExpandDatePicker executed")

        _showDatePicker.value = show
    }

    fun setCurrentDate(newDate: LocalDate) {
        Log.d(TAG, "setCurrentDate executed")

        _currentDate.value = newDate

        setWiDListGet(get = false)

        getFullWiDListFromWiDList(newDate)
    }

    private fun getFullWiDListFromWiDList(date: LocalDate) {
        Log.d(TAG, "getFullWiDListFromWiDList executed")

        wiDDataSource.getWiDListByDate(email = email, date = date) { wiDList ->
            val currentTime = LocalTime.now() // getWiDListByDate를 호출할 때마다 현재 시간을 계산해야 마이너스 시간이 안나옴.

            _fullWiDList.value = getFullWiDListFromWiDList(date = date, currentTime = currentTime, wiDList = wiDList)

            setWiDListGet(get = true)
        }
    }

    private fun setWiDListGet(get: Boolean) {
        Log.d(TAG, "setWiDListGet executed")

        _wiDListGet.value = get
    }

    fun setEmptyWiD(emptyWiD: WiD) {
        Log.d(TAG, "setEmptyWiD executed")

        wiDDataSource.setEmptyWiD(newEmptyWiD = emptyWiD)
    }

    fun setClickedWiD(clickedWiD: WiD) {
        Log.d(TAG, "setClickedWiD executed")

        wiDDataSource.setClickedWiD(updatedClickedWiD = clickedWiD)
    }
}