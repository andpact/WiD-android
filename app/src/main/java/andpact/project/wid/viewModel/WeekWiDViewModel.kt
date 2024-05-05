package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.WiD
import andpact.project.wid.util.*
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeekWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "WeekWiDViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email: String = userDataSource.firebaseUser.value?.email ?: ""

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfWeek(today))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(today))
    val finishDate: State<LocalDate> = _finishDate
//    private val _weekPickerExpanded = mutableStateOf(false)
//    val weekPickerExpanded: State<Boolean> = _weekPickerExpanded

    // WiD
//    private val wiDService = WiDService(context = application)
//    private val _wiDList = mutableStateOf<List<WiD>>(wiDService.readWiDListByDateRange(_startDate.value, _finishDate.value))
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
    val wiDList: State<List<WiD>> = _wiDList

    // 합계 selectedMap만 화면에 표시하니 state로 선언할 필요 없음.
//    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _wiDList.value))
//    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap
    var totalDurationMap = getTotalDurationMapByTitle(wiDList = _wiDList.value)

    // 평균
//    private val _averageDurationMap = mutableStateOf(getAverageDurationMapByTitle(wiDList = _wiDList.value))
//    val averageDurationMap: State<Map<String, Duration>> = _averageDurationMap
    var averageDurationMap = getAverageDurationMapByTitle(wiDList = _wiDList.value)

    // 최소
//    private val _minDurationMap = mutableStateOf(getMinDurationMapByTitle(wiDList = _wiDList.value))
//    val minDurationMap: State<Map<String, Duration>> = _minDurationMap
    var minDurationMap = getMinDurationMapByTitle(wiDList = _wiDList.value)

    // 최고
//    private val _maxDurationMap = mutableStateOf(getMaxDurationMapByTitle(wiDList = _wiDList.value))
//    val maxDurationMap: State<Map<String, Duration>> = _maxDurationMap
    var maxDurationMap = getMaxDurationMapByTitle(wiDList = _wiDList.value)

    private val _selectedMapText = mutableStateOf("합계")
    val selectedMapText: State<String> = _selectedMapText

    private val _selectedMap = mutableStateOf(totalDurationMap)
    var selectedMap: State<Map<String, Duration>> = _selectedMap

//    fun setWeekPickerExpanded(expand: Boolean) {
//        Log.d("WeekWiDViewModel", "setWeekPickerExpanded executed")
//        _weekPickerExpanded.value = expand
//    }

    fun setStartDateAndFinishDate(startDate: LocalDate, finishDate: LocalDate) {
        Log.d(TAG, "setStartDateAndFinishDate executed")

        _startDate.value = startDate
        _finishDate.value = finishDate

        wiDDataSource.getWiDListFromFirstDateToLastDate(email = email, firstDate = startDate, lastDate = finishDate) { wiDList ->
            _wiDList.value = wiDList

            totalDurationMap = getTotalDurationMapByTitle(wiDList = _wiDList.value)
            averageDurationMap = getAverageDurationMapByTitle(wiDList = _wiDList.value)
            minDurationMap = getMinDurationMapByTitle(wiDList = _wiDList.value)
            maxDurationMap = getMaxDurationMapByTitle(wiDList = _wiDList.value)

            _selectedMapText.value = "합계"
            _selectedMap.value = totalDurationMap
        }
//        _wiDList.value = wiDService.readWiDListByDateRange(startDate, finishDate)
//        totalDurationMap = getTotalDurationMapByTitle(wiDList = _wiDList.value)
//        averageDurationMap = getAverageDurationMapByTitle(wiDList = _wiDList.value)
//        minDurationMap = getMinDurationMapByTitle(wiDList = _wiDList.value)
//        maxDurationMap = getMaxDurationMapByTitle(wiDList = _wiDList.value)
//
//        _selectedMapText.value = "합계"
//        _selectedMap.value = totalDurationMap
    }

    // 합, 평, 고, 저 맵은 미리 준비되어 있고, 보여줄 맵만 변경함.
    fun updateSelectedMap(newText: String, newMap: Map<String, Duration>) {
        Log.d(TAG, "updateSelectedMap executed")

        _selectedMapText.value = newText
        _selectedMap.value = newMap
    }
}