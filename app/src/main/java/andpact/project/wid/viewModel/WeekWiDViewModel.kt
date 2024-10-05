package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
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
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _startDate = mutableStateOf(getFirstDateOfWeek(today))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(today))
    val finishDate: State<LocalDate> = _finishDate
    private val _weekPickerExpanded = mutableStateOf(false)
    val weekPickerExpanded: State<Boolean> = _weekPickerExpanded

    // WiD
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
    val wiDList: State<List<WiD>> = _wiDList

    val titleColorMap = titleNumberStringToTitleColorMap

    // 합계 selectedMap만 화면에 표시하니 state로 선언할 필요 없음.
    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _wiDList.value))
    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap

    // 평균
    private val _averageDurationMap = mutableStateOf(getAverageDurationMapByTitle(wiDList = _wiDList.value))
    val averageDurationMap: State<Map<String, Duration>> = _averageDurationMap

    // 최소
    private val _minDurationMap = mutableStateOf(getMinDurationMapByTitle(wiDList = _wiDList.value))
    val minDurationMap: State<Map<String, Duration>> = _minDurationMap

    // 최고
    private val _maxDurationMap = mutableStateOf(getMaxDurationMapByTitle(wiDList = _wiDList.value))
    val maxDurationMap: State<Map<String, Duration>> = _maxDurationMap

    fun setWeekPickerExpanded(expand: Boolean) {
        Log.d("WeekWiDViewModel", "setWeekPickerExpanded executed")
        _weekPickerExpanded.value = expand
    }

    fun setStartDateAndFinishDate(
        startDate: LocalDate,
        finishDate: LocalDate
    ) {
        Log.d(TAG, "setStartDateAndFinishDate executed")

        _startDate.value = startDate
        _finishDate.value = finishDate

        wiDDataSource.getWiDListFromFirstDateToLastDate(
            email = user.value?.email ?: "",
            firstDate = startDate,
            lastDate = finishDate,
            onWiDListFetchedFromFirstDateToLastDate = { wiDList: List<WiD> ->
                Log.d(TAG, "WiD Collection Changed: New List Size = ${wiDList.size}")

                _wiDList.value = wiDList

                _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = _wiDList.value)
                _averageDurationMap.value = getAverageDurationMapByTitle(wiDList = _wiDList.value)
                _minDurationMap.value = getMinDurationMapByTitle(wiDList = _wiDList.value)
                _maxDurationMap.value = getMaxDurationMapByTitle(wiDList = _wiDList.value)
            }
        )

        // 리스너 부착
//        wiDDataSource.addSnapshotListenerToWiDCollectionFromFirstDateToLastDate(
//            email = user.value?.email ?: "",
//            collectionFirstDate = startDate,
//            collectionLastDate = finishDate,
//            onWiDCollectionChanged = { wiDList: List<WiD> ->
//                _wiDList.value = wiDList
//
//                _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = _wiDList.value)
//                _averageDurationMap.value = getAverageDurationMapByTitle(wiDList = _wiDList.value)
//                _minDurationMap.value = getMinDurationMapByTitle(wiDList = _wiDList.value)
//                _maxDurationMap.value = getMaxDurationMapByTitle(wiDList = _wiDList.value)
//            }
//        )
    }
}