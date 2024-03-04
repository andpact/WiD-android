package andpact.project.wid.viewModel

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import java.time.Duration
import java.time.LocalDate

class WeekWiDViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d("WeekWiDViewModel", "WeekWiDViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("WeekWiDViewModel", "WeekWiDViewModel is cleared")
    }

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfWeek(today))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(today))
    val finishDate: State<LocalDate> = _finishDate

    // WiD
    private val wiDService = WiDService(context = application)
    private val _wiDList = mutableStateOf(wiDService.readWiDListByDateRange(_startDate.value, _finishDate.value))
    val wiDList: State<List<WiD>> = _wiDList

    // 합계 selectedMap만 화면에 표시하니 state로 선언할 필요 없음.
//    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _wiDList.value))
//    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap
    var totalDurationMap = getTotalDurationMapByTitle(wiDList = _wiDList.value)

    // 합계
//    private val _averageDurationMap = mutableStateOf(getAverageDurationMapByTitle(wiDList = _wiDList.value))
//    val averageDurationMap: State<Map<String, Duration>> = _averageDurationMap
    var averageDurationMap = getAverageDurationMapByTitle(wiDList = _wiDList.value)

    // 합계
//    private val _minDurationMap = mutableStateOf(getMinDurationMapByTitle(wiDList = _wiDList.value))
//    val minDurationMap: State<Map<String, Duration>> = _minDurationMap
    var minDurationMap = getMinDurationMapByTitle(wiDList = _wiDList.value)

    // 합계
//    private val _maxDurationMap = mutableStateOf(getMaxDurationMapByTitle(wiDList = _wiDList.value))
//    val maxDurationMap: State<Map<String, Duration>> = _maxDurationMap
    var maxDurationMap = getMaxDurationMapByTitle(wiDList = _wiDList.value)

    private val _selectedMapText = mutableStateOf("합계")
    val selectedMapText: State<String> = _selectedMapText

    private val _selectedMap = mutableStateOf(totalDurationMap)
    var selectedMap: State<Map<String, Duration>> = _selectedMap

    fun setStartDateAndFinishDate(startDate: LocalDate, finishDate: LocalDate) {
        _startDate.value = startDate
        _finishDate.value = finishDate

        _wiDList.value = wiDService.readWiDListByDateRange(startDate, finishDate)
        totalDurationMap = getTotalDurationMapByTitle(wiDList = _wiDList.value)
        averageDurationMap = getAverageDurationMapByTitle(wiDList = _wiDList.value)
        minDurationMap = getMinDurationMapByTitle(wiDList = _wiDList.value)
        maxDurationMap = getMaxDurationMapByTitle(wiDList = _wiDList.value)

        _selectedMapText.value = "합계"
        _selectedMap.value = totalDurationMap
    }

    fun updateSelectedMap(newText: String, newMap: Map<String, Duration>) {
        _selectedMapText.value = newText
        _selectedMap.value = newMap
    }
}