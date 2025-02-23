package andpact.project.wid.tmp

//import andpact.project.wid.dataSource.UserDataSource
//import andpact.project.wid.dataSource.WiDDataSource
//import andpact.project.wid.model.Title
//import andpact.project.wid.model.User
//import andpact.project.wid.model.WiD
//import andpact.project.wid.util.*
//import android.util.Log
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.time.Duration
//import java.time.LocalDate
//import java.time.LocalTime
//import javax.inject.Inject
//
//@HiltViewModel
//class TitleWiDListViewModel @Inject constructor(
//    private val userDataSource: UserDataSource,
//    private val wiDDataSource: WiDDataSource,
//) : ViewModel()  {
//    private val TAG = "TitleWiDListViewModel"
//    init { Log.d(TAG, "created") }
//    override fun onCleared() {
//        super.onCleared()
//        Log.d(TAG, "cleared")
//    }
//
//    private val user: State<User?> = userDataSource.user
//
//    // 날짜
//    val today: State<LocalDate> = wiDDataSource.today
//    private val _startDate = mutableStateOf(getFirstDateOfWeek(today.value))
//    val startDate: State<LocalDate> = _startDate
//    private val _finishDate = mutableStateOf(getLastDateOfWeek(today.value))
//    val finishDate: State<LocalDate> = _finishDate
//    private val _weekPickerExpanded = mutableStateOf(false)
//    val weekPickerExpanded: State<Boolean> = _weekPickerExpanded
//
//    // WiD
//    private val _wiDListFetched = mutableStateOf(false)
//    val wiDListFetched: State<Boolean> = _wiDListFetched
//    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
//    val wiDList: State<List<WiD>> = _wiDList
//
//    // 맵(갱신되야 하니까 State로 선언)
//    private val _currentTitle = mutableStateOf(Title.STUDY)
//    val currentTitle: State<Title> = _currentTitle
//    private var _totalDurationMap = mutableStateOf(wiDDataSource.getWiDTitleTotalDurationMap(wiDList = _wiDList.value))
//    var totalDurationMap: State<Map<Title, Duration>> = _totalDurationMap
//    private var _averageDurationMap = mutableStateOf(wiDDataSource.getWiDTitleTotalDurationMap(wiDList = _wiDList.value))
//    var averageDurationMap: State<Map<Title, Duration>> = _averageDurationMap
//    private var _maxDurationMap = mutableStateOf(wiDDataSource.getWiDTitleTotalDurationMap(wiDList = _wiDList.value))
//    var maxDurationMap: State<Map<Title, Duration>> = _maxDurationMap
//    private var _minDurationMap = mutableStateOf(wiDDataSource.getWiDTitleTotalDurationMap(wiDList = _wiDList.value))
//    var minDurationMap: State<Map<Title, Duration>> = _minDurationMap
//    private val _titleMaxDateMap = mutableStateOf(wiDDataSource.getWiDTitleMaxDateMap(wiDList = _wiDList.value))
//    val titleMaxDateMap: State<Map<Title, LocalDate>> = _titleMaxDateMap
//    private val _titleMinDateMap = mutableStateOf(wiDDataSource.getWiDTitleMinDateMap(wiDList = _wiDList.value))
//    val titleMinDateMap: State<Map<Title, LocalDate>> = _titleMinDateMap
//    private val _titleDateCountMap = mutableStateOf(wiDDataSource.getWiDTitleDateCountMap(wiDList = _wiDList.value))
//    val titleDateCountMap: State<Map<Title, Int>> = _titleDateCountMap
//
//    // Current WiD
//    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD
//    val secondCurrentWiD: State<WiD> = wiDDataSource.secondCurrentWiD
//
//    fun setCurrentTitle(newTitle: Title) {
//        Log.d(TAG, "setCurrentTitle executed")
//
//        _currentTitle.value = newTitle
//    }
//
//    fun setWeekPickerExpanded(expand: Boolean) {
//        Log.d(TAG, "setWeekPickerExpanded executed")
//
//        _weekPickerExpanded.value = expand
//    }
//
//    fun setStartDateAndFinishDate(
//        startDate: LocalDate,
//        finishDate: LocalDate
//    ) {
//        Log.d(TAG, "setStartDateAndFinishDate executed")
//
//        _startDate.value = startDate
//        _finishDate.value = finishDate
//
//        setWiDListFetched(wiDListFetched = false)
//
//        getWiDListFromStartDateToFinishDate(
//            startDate = startDate,
//            finishDate = finishDate
//        )
//    }
//
//    private fun getWiDListFromStartDateToFinishDate(
//        startDate: LocalDate,
//        finishDate: LocalDate
//    ) {
//        Log.d(TAG, "getWiDListFromStartDateToFinishDate executed")
//
//        wiDDataSource.getWiDListFromFirstDateToLastDate(
//            email = user.value?.email ?: "",
//            firstDate = startDate,
//            lastDate = finishDate,
//            onWiDListFetched = { wiDList: List<WiD> ->
//                _wiDList.value = wiDList
//                setDurationMaps(wiDList = wiDList)
//                setWiDListFetched(wiDListFetched = true)
//            }
//        )
//    }
//
//    private fun setDurationMaps(wiDList: List<WiD>) {
//        Log.d(TAG, "setDurationMaps executed")
//
//        _totalDurationMap.value = wiDDataSource.getWiDTitleTotalDurationMap(wiDList = wiDList)
//        _averageDurationMap.value = wiDDataSource.getWiDTitleAverageDurationMap(wiDList = wiDList)
//        _maxDurationMap.value = wiDDataSource.getWiDTitleMaxDurationMap(wiDList = wiDList)
//        _minDurationMap.value = wiDDataSource.getWiDTitleMinDurationMap(wiDList = wiDList)
//
//        _titleDateCountMap.value = wiDDataSource.getWiDTitleDateCountMap(wiDList = wiDList)
//        _titleMaxDateMap.value = wiDDataSource.getWiDTitleMaxDateMap(wiDList = wiDList)
//        _titleMinDateMap.value = wiDDataSource.getWiDTitleMinDateMap(wiDList = wiDList)
//    }
//
//    private fun setWiDListFetched(wiDListFetched: Boolean) {
//        Log.d(TAG, "setWiDListFetched executed")
//
//        _wiDListFetched.value = wiDListFetched
//    }
//}