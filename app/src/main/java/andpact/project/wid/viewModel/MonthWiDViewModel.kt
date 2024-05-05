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

/**
 * _변수는 뷰 모델 내부에서 사용됨.
 */
@HiltViewModel
class MonthWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
//    private val wiDRepository: WiDRepository
) : ViewModel() {
    private val TAG = "MonthWiDViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email = userDataSource.firebaseUser.value?.email ?: ""

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfMonth(today))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfMonth(today))
    val finishDate: State<LocalDate> = _finishDate
//    private val _monthPickerExpanded = mutableStateOf(false)
//    val monthPickerExpanded: State<Boolean> = _monthPickerExpanded

    // WiD
//    private val wiDService = WiDService(context = application)
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

    fun updateSelectedMap(newText: String, newMap: Map<String, Duration>) {
        Log.d(TAG, "updateSelectedMap executed")

        _selectedMapText.value = newText
        _selectedMap.value = newMap
    }
}