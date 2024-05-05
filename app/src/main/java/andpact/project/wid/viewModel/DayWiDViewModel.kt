package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.defaultTitleColorMapWithColors
import andpact.project.wid.util.getFullWiDListFromWiDList
import andpact.project.wid.util.getTotalDurationMapByTitle
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DayWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "DayWiDViewModel"

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

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _currentDate = mutableStateOf(today)
    val currentDate: State<LocalDate> = _currentDate
    private val _showDatePicker = mutableStateOf(false)
    val showDatePicker: State<Boolean> = _showDatePicker

    // WiD
    private val _wiDListGet = mutableStateOf(false)
    val wiDListGet: State<Boolean> = _wiDListGet
//    private var _wiDList = mutableStateOf<List<WiD>>(emptyList())
//    val wiDList: State<List<WiD>> = _wiDList
    private val _fullWiDList = mutableStateOf<List<WiD>>(emptyList())
    val fullWiDList: State<List<WiD>> = _fullWiDList

    // 합계
//    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _wiDList.value))
//    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap
    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _fullWiDList.value))
    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap

    fun setShowDatePicker(show: Boolean) {
        Log.d(TAG, "setShowDatePicker executed")

        _showDatePicker.value = show
    }

    fun setCurrentDate(newDate: LocalDate) {
        Log.d(TAG, "setCurrentDate executed")

        _currentDate.value = newDate

        setWiDListGet(get = false)

        getFullWiDListFromWiDList(date = newDate)
    }

    private fun getFullWiDListFromWiDList(date: LocalDate) {
        Log.d(TAG, "getFullWiDListFromWiDList executed")

        wiDDataSource.getWiDListByDate(email = email, date = date) { wiDList ->
            val currentTime = LocalTime.MAX

            _fullWiDList.value = getFullWiDListFromWiDList(date = date, currentTime = currentTime, wiDList = wiDList)
            _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = _fullWiDList.value)

            setWiDListGet(get = true)
        }
    }

    private fun setWiDListGet(get: Boolean) {
        Log.d(TAG, "setWiDListGet executed")

        _wiDListGet.value = get
    }
}