package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.*
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class WeeklyWiDListViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "WeeklyWiDListViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    // 날짜
    val today: State<LocalDate> = wiDDataSource.today
    private val _startDate = mutableStateOf(getFirstDateOfWeek(today.value))
    val startDate: State<LocalDate> = _startDate
    private val _finishDate = mutableStateOf(getLastDateOfWeek(today.value))
    val finishDate: State<LocalDate> = _finishDate
    private val _weekPickerExpanded = mutableStateOf(false)
    val weekPickerExpanded: State<Boolean> = _weekPickerExpanded

    // WiD
    private val _wiDListFetched = mutableStateOf(false)
    val wiDListFetched: State<Boolean> = _wiDListFetched
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
    val wiDList: State<List<WiD>> = _wiDList

    // 맵(모든 제목의 맵을 만들어둠).
    private var totalDurationMap = getWiDTitleTotalDurationMap(wiDList = _wiDList.value)
    private var averageDurationMap = getWiDTitleAverageDurationMap(wiDList = _wiDList.value)
    private var maxDurationMap = getWiDTitleMaxDurationMap(wiDList = _wiDList.value)
    private var minDurationMap = getWiDTitleMinDurationMap(wiDList = _wiDList.value)
    private val _titleMaxDateMap = mutableStateOf(getWiDTitleMaxDateMap(wiDList = _wiDList.value))
    val titleMaxDateMap: State<Map<Title, LocalDate>> = _titleMaxDateMap
    private val _titleMinDateMap = mutableStateOf(getWiDTitleMinDateMap(wiDList = _wiDList.value))
    val titleMinDateMap: State<Map<Title, LocalDate>> = _titleMinDateMap
    private val _titleDateCountMap = mutableStateOf(getWiDTitleDateCountMap(wiDList = _wiDList.value))
    val titleDateCountMap: State<Map<Title, Int>> = _titleDateCountMap

    // 표시 되는 맵
    private val _currentMapType = mutableStateOf(TitleDurationMap.TOTAL)
    val currentMapType: State<TitleDurationMap> = _currentMapType
    private val _currentMap = mutableStateOf(totalDurationMap)
    val currentMap: State<Map<Title, Duration>> = _currentMap

    // Current WiD
    val date: State<LocalDate> = wiDDataSource.date
    val start: State<LocalTime> = wiDDataSource.start
    val finish: State<LocalTime> = wiDDataSource.finish

    fun setToday(newDate: LocalDate) {
        Log.d(TAG, "setToday executed")

        wiDDataSource.setToday(newDate = newDate)
    }

    fun setCurrentMapType(mapType: TitleDurationMap) {
        Log.d(TAG, "setCurrentMapType executed with mapType: $mapType")

        _currentMapType.value = mapType

        setCurrentMap(mapType = mapType)
    }

    private fun setCurrentMap(mapType: TitleDurationMap) {
        Log.d(TAG, "setCurrentMap executed with mapType: $mapType")

        _currentMap.value = when (mapType) {
            TitleDurationMap.TOTAL -> totalDurationMap
            TitleDurationMap.AVERAGE -> averageDurationMap
            TitleDurationMap.MAX -> maxDurationMap
            TitleDurationMap.MIN -> minDurationMap
        }
    }

    fun setWeekPickerExpanded(expand: Boolean) {
        Log.d(TAG, "setWeekPickerExpanded executed")

        _weekPickerExpanded.value = expand
    }

    fun setStartDateAndFinishDate(
        startDate: LocalDate,
        finishDate: LocalDate
    ) {
        Log.d(TAG, "setStartDateAndFinishDate executed")

        _startDate.value = startDate
        _finishDate.value = finishDate

        setWiDListFetched(wiDListFetched = false)

        getWiDListFromStartDateToFinishDate(
            startDate = startDate,
            finishDate = finishDate
        )
    }

    private fun getWiDListFromStartDateToFinishDate(
        startDate: LocalDate,
        finishDate: LocalDate
    ) {
        Log.d(TAG, "getWiDListFromStartDateToFinishDate executed")

        wiDDataSource.getWiDListFromFirstDateToLastDate(
            email = user.value?.email ?: "",
            firstDate = startDate,
            lastDate = finishDate,
            onWiDListFetched = { wiDList: List<WiD> ->
                Log.d(TAG, "WiD Collection Changed: New List Size = ${wiDList.size}")

                /** 복구 */
                val sampleWeeklyWiDList = generateSampleWeeklyWiDList()
                _wiDList.value = sampleWeeklyWiDList
                setDurationMaps(wiDList = sampleWeeklyWiDList)

//                _wiDList.value = wiDList
//                setDurationMaps(wiDList = wiDList)
                setWiDListFetched(wiDListFetched = true)
            }
        )
    }

    private fun setDurationMaps(wiDList: List<WiD>) {
        Log.d(TAG, "setDurationMaps executed")

        totalDurationMap = getWiDTitleTotalDurationMap(wiDList = wiDList)
        averageDurationMap = getWiDTitleAverageDurationMap(wiDList = wiDList)
        maxDurationMap = getWiDTitleMaxDurationMap(wiDList = wiDList)
        minDurationMap = getWiDTitleMinDurationMap(wiDList = wiDList)

        _titleDateCountMap.value = getWiDTitleDateCountMap(wiDList = wiDList)
        _titleMaxDateMap.value = getWiDTitleMaxDateMap(wiDList = wiDList)
        _titleMinDateMap.value = getWiDTitleMinDateMap(wiDList = wiDList)

        setCurrentMap(_currentMapType.value) // 예를 계속 갱신해줘야 함.
    }

    private fun setWiDListFetched(wiDListFetched: Boolean) {
        Log.d(TAG, "setWiDListFetched executed")

        _wiDListFetched.value = wiDListFetched
    }
}