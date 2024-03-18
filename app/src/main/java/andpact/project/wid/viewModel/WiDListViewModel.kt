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
//    private val currentTime: LocalTime = LocalTime.now() // 얘가.. 고정되면 안됨.
    val today: LocalDate = LocalDate.now()
    private val _currentDate = mutableStateOf(today)
    val currentDate: State<LocalDate> = _currentDate
//    private val _expandDatePicker = mutableStateOf(false)
//    val expandDatePicker: State<Boolean> = _expandDatePicker

    // WiD
    private val wiDService = WiDService(context = application)
//    private var wiDList = wiDService.readDailyWiDListByDate(currentDate.value)
    private var wiDList = emptyList<WiD>()
//    private val _fullWiDList = mutableStateOf(getFullWiDListFromWiDList(date = currentDate.value, currentTime = currentTime, wiDList = wiDList))
    private val _fullWiDList = mutableStateOf<List<WiD>>(emptyList())
    val fullWiDList: State<List<WiD>> = _fullWiDList

//    fun setExpandDatePicker(expand: Boolean) {
//        Log.d("WiDListViewModel", "setExpandDatePicker executed")
//        _expandDatePicker.value = expand
//    }

    fun setCurrentDate(newDate: LocalDate) {
        Log.d("WiDListViewModel", "setCurrentDate executed")

        _currentDate.value = newDate
        wiDList = wiDService.readDailyWiDListByDate(newDate)

        val currentTime = LocalTime.now() // setCurrentDate를 호출할 때마다 현재 시간을 계산해야 마이너스 시간이 안나옴.
        _fullWiDList.value = getFullWiDListFromWiDList(date = newDate, currentTime = currentTime, wiDList = wiDList)
    }
}