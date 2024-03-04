package andpact.project.wid.viewModel

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.getFullWiDListFromWiDList
import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate
import java.time.LocalTime

class WiDListViewModel(application: Application) : AndroidViewModel(application) {

    init {
        Log.d("WiDListViewModel", "WiDListViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("WiDListViewModel", "WiDListViewModel is cleared")
    }

    // 날짜
    private val currentTime: LocalTime = LocalTime.now()
    val today: LocalDate = LocalDate.now()
    private val _currentDate = mutableStateOf(today)
    val currentDate: State<LocalDate> = _currentDate
    private val _expandDatePicker = mutableStateOf(false)
    val expandDatePicker: State<Boolean> = _expandDatePicker

    // WiD
    private val wiDService = WiDService(context = application)
    private var wiDList = wiDService.readDailyWiDListByDate(currentDate.value)
    private val _fullWiDList = mutableStateOf(getFullWiDListFromWiDList(date = currentDate.value, currentTime = currentTime, wiDList = wiDList))
//    private var wiDList: List<WiD> by mutableStateOf(emptyList()) // 초기값으로 빈 리스트 할당
//    private val _fullWiDList = mutableStateOf<List<WiD>>(emptyList()) // 초기값으로 빈 리스트 할당
    val fullWiDList: State<List<WiD>> = _fullWiDList

    fun setExpandDatePicker(expand: Boolean) {
        Log.d("WiDListViewModel", "setExpandDatePicker executed")
        _expandDatePicker.value = expand
    }

    fun setCurrentDate(newDate: LocalDate) {
        Log.d("WiDListViewModel", "setCurrentDate executed")

        _currentDate.value = newDate
        wiDList = wiDService.readDailyWiDListByDate(newDate)
        _fullWiDList.value = getFullWiDListFromWiDList(date = newDate, currentTime = currentTime, wiDList = wiDList)
    }
}