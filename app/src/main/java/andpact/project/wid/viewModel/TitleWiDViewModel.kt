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

class TitleWiDViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d("TitleWiDViewModel", "TitleWiDViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TitleWiDViewModel", "TitleWiDViewModel is cleared")
    }

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfWeek(today))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(today))
    val finishDate: State<LocalDate> = _finishDate
    private val _weekMonthPickerExpanded = mutableStateOf(false)
    val weekMonthPickerExpanded: State<Boolean> = _weekMonthPickerExpanded

    // 제목
    private val _selectedTitle = mutableStateOf(titles[0])
    val selectedTitle: State<String> = _selectedTitle
//    private val _titleMenuExpanded = mutableStateOf(false)
//    val titleMenuExpanded: State<Boolean> = _titleMenuExpanded

    // 기간
    private val _selectedPeriod = mutableStateOf(periods[0])
    val selectedPeriod: State<String> = _selectedPeriod
//    private val _periodMenuExpanded = mutableStateOf(false)
//    val periodMenuExpanded: State<Boolean> = _periodMenuExpanded

    // WiD
    private val wiDService = WiDService(context = application)
//    private val _wiDList = mutableStateOf(wiDService.readWiDListByDateRange(_startDate.value, _finishDate.value))
//    val wiDList: State<List<WiD>> = _wiDList
    var wiDList = wiDService.readWiDListByDateRange(_startDate.value, _finishDate.value)
    private val _filteredWiDListByTitle = mutableStateOf(wiDList.filter { it.title == _selectedTitle.value })
    val filteredWiDListByTitle: State<List<WiD>> = _filteredWiDListByTitle

    // 합계
//    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = wiDList))
    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _filteredWiDListByTitle.value))
    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap

    // 평균
//    private val _averageDurationMap = mutableStateOf(getAverageDurationMapByTitle(wiDList = wiDList))
    private val _averageDurationMap = mutableStateOf(getAverageDurationMapByTitle(wiDList = _filteredWiDListByTitle.value))
    val averageDurationMap: State<Map<String, Duration>> = _averageDurationMap

    // 최고
//    private val _minDurationMap = mutableStateOf(getMinDurationMapByTitle(wiDList = wiDList))
    private val _minDurationMap = mutableStateOf(getMinDurationMapByTitle(wiDList = _filteredWiDListByTitle.value))
    val minDurationMap: State<Map<String, Duration>> = _minDurationMap

    // 최고
//    private val _maxDurationMap = mutableStateOf(getMaxDurationMapByTitle(wiDList = wiDList))
    private val _maxDurationMap = mutableStateOf(getMaxDurationMapByTitle(wiDList = _filteredWiDListByTitle.value))
    val maxDurationMap: State<Map<String, Duration>> = _maxDurationMap

    fun setWeekMonthPickerExpanded(expand: Boolean) {
        Log.d("TitleWiDViewModel", "setWeekMonthPickerExpanded executed")
        _weekMonthPickerExpanded.value = expand
    }

    fun setTitle(newTitle: String) {
        Log.d("TitleWiDViewModel", "setTitle executed")

        _selectedTitle.value = newTitle
        _filteredWiDListByTitle.value = wiDList.filter { it.title == newTitle }
    }

//    fun setTitleMenuExpanded(expanded: Boolean) {
//        Log.d("TitleWiDViewModel", "setTitleMenuExpanded executed")
//
//        _titleMenuExpanded.value = expanded
//    }

    fun setPeriod(newPeriod: String) {
        Log.d("TitleWiDViewModel", "setPeriod executed")

        _selectedPeriod.value = newPeriod

        when (newPeriod) {
            periods[0] -> { // 일주일
                _startDate.value = getFirstDateOfWeek(today)
                _finishDate.value = getLastDateOfWeek(today)
            }
            periods[1] -> { // 한 달
                _startDate.value = getFirstDateOfMonth(today)
                _finishDate.value = getLastDateOfMonth(today)
            }
        }
    }

//    fun setPeriodMenuExpanded(expanded: Boolean) {
//        Log.d("TitleWiDViewModel", "setPeriodMenuExpanded executed")
//
//        _periodMenuExpanded.value = expanded
//    }

    fun setStartDateAndFinishDate(startDate: LocalDate, finishDate: LocalDate) {
        Log.d("TitleWiDViewModel", "setStartDateAndFinishDate executed")

        _startDate.value = startDate
        _finishDate.value = finishDate

        wiDList = wiDService.readWiDListByDateRange(startDate, finishDate)
        _filteredWiDListByTitle.value = wiDList.filter { it.title == _selectedTitle.value }
        _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = wiDList)
        _averageDurationMap.value = getAverageDurationMapByTitle(wiDList = wiDList)
        _minDurationMap.value = getMinDurationMapByTitle(wiDList = wiDList)
        _maxDurationMap.value = getMaxDurationMapByTitle(wiDList = wiDList)
    }
}