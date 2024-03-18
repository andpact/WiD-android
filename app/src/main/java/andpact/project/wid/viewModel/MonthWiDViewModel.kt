package andpact.project.wid.viewModel

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import java.time.Duration
import java.time.LocalDate

/**
 * _변수는 뷰 모델 내부에서 사용됨.
 */
class MonthWiDViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d("MonthWiDViewModel", "MonthWiDViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MonthWiDViewModel", "MonthWiDViewModel is cleared")
    }

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfMonth(today))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfMonth(today))
    val finishDate: State<LocalDate> = _finishDate
//    private val _monthPickerExpanded = mutableStateOf(false)
//    val monthPickerExpanded: State<Boolean> = _monthPickerExpanded

    // WiD
    private val wiDService = WiDService(context = application)
//    private val _wiDList = mutableStateOf(wiDService.readWiDListByDateRange(_startDate.value, _finishDate.value))
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
    val wiDList: State<List<WiD>> = _wiDList

    // 합계 selectedMap만 화면에 표시하니 state로 선언할 필요 없음.
    var totalDurationMap = getTotalDurationMapByTitle(wiDList = _wiDList.value)

    // 평균
    var averageDurationMap = getAverageDurationMapByTitle(wiDList = _wiDList.value)

    // 최소
    var minDurationMap = getMinDurationMapByTitle(wiDList = _wiDList.value)

    // 최고
    var maxDurationMap = getMaxDurationMapByTitle(wiDList = _wiDList.value)

    private val _selectedMapText = mutableStateOf("합계")
    val selectedMapText: State<String> = _selectedMapText

    private val _selectedMap = mutableStateOf(totalDurationMap)
    var selectedMap: State<Map<String, Duration>> = _selectedMap

//    fun setMonthPickerExpanded(expand: Boolean) {
//        Log.d("MonthWiDViewModel", "setMonthPickerExpanded executed")
//        _monthPickerExpanded.value = expand
//    }

    fun setStartDateAndFinishDate(startDate: LocalDate, finishDate: LocalDate) {
        Log.d("MonthWiDViewModel", "setStartDateAndFinishDate executed")

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
        Log.d("MonthWiDViewModel", "updateSelectedMap executed")

        _selectedMapText.value = newText
        _selectedMap.value = newMap
    }
}