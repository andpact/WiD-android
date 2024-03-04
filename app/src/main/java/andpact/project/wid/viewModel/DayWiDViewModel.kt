package andpact.project.wid.viewModel

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.getFullWiDListFromWiDList
import andpact.project.wid.util.getTotalDurationMapByTitle
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.time.Duration
import java.time.LocalDate

class DayWiDViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d("DayWiDViewModel", "DayWiDViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("DayWiDViewModel", "DayWiDViewModel is cleared")
    }

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _currentDate = mutableStateOf(today)
    val currentDate: State<LocalDate> = _currentDate
    private val _expandDatePicker = mutableStateOf(false)
    val expandDatePicker: State<Boolean> = _expandDatePicker

    // WiD
    private val wiDService = WiDService(context = application)
    private val _wiDList = mutableStateOf(wiDService.readDailyWiDListByDate(today)) // 초기값으로 빈 리스트 할당
    val wiDList: State<List<WiD>> = _wiDList

    // 합계
    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _wiDList.value)) // 초기값으로 빈 맵 할당
    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap

    fun setExpandDatePicker(expand: Boolean) {
        Log.d("DayWiDViewModel", "setExpandDatePicker executed")
        _expandDatePicker.value = expand
    }

    // 정해진 날짜로 이동
    fun setCurrentDate(newDate: LocalDate) {
        Log.d("DayWiDViewModel", "setCurrentDate executed")

        _currentDate.value = newDate
        _wiDList.value = wiDService.readDailyWiDListByDate(_currentDate.value)
        _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = _wiDList.value)
    }
}