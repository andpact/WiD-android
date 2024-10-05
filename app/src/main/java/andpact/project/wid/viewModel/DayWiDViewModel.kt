package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.getFullWiDListFromWiDList
import andpact.project.wid.util.getTotalDurationMapByTitle
import andpact.project.wid.util.titleNumberStringToTitleColorMap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DayWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "DayWiDViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    val titleColorMap = titleNumberStringToTitleColorMap

    // 날짜
    val today: LocalDate = LocalDate.now()
    private val _currentDate = mutableStateOf(today)
    val currentDate: State<LocalDate> = _currentDate
    private val _showDatePicker = mutableStateOf(false)
    val showDatePicker: State<Boolean> = _showDatePicker

    // WiD
    private val _wiDListLoaded = mutableStateOf(false)
    val wiDListLoaded: State<Boolean> = _wiDListLoaded
    private val _fullWiDList = mutableStateOf<List<WiD>>(emptyList())
    val fullWiDList: State<List<WiD>> = _fullWiDList

    // 합계
    private val _totalDurationMap = mutableStateOf(getTotalDurationMapByTitle(wiDList = _fullWiDList.value))
    val totalDurationMap: State<Map<String, Duration>> = _totalDurationMap

    fun setShowDatePicker(show: Boolean) {
        Log.d(TAG, "setShowDatePicker executed")

        _showDatePicker.value = show
    }

    fun setCurrentDate(newDate: LocalDate) {
        Log.d(TAG, "setCurrentDate executed")

        _currentDate.value = newDate

        setWiDListLoaded(loaded = false)

        getFullWiDListByDate(collectionDate = newDate)

        // 컬렉션 리스너 부착
//        wiDDataSource.addSnapshotListenerToWiDCollectionByDate(
//            email = user.value?.email ?: "",
//            collectionDate = newDate,
//            onWiDCollectionChanged = { wiDList: List<WiD> ->
//                val currentTime = LocalTime.MAX
//
//                _fullWiDList.value = getFullWiDListFromWiDList(
//                    date = newDate,
//                    currentTime = currentTime,
//                    wiDList = wiDList
//                )
//                _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = _fullWiDList.value)
//            }
//        )
    }

    private fun getFullWiDListByDate(collectionDate: LocalDate) {
        Log.d(TAG, "getFullWiDListByDate executed")

        wiDDataSource.getWiDListByDate(
            email = user.value?.email ?: "",
            collectionDate = collectionDate,
            onWiDListFetchedByDate = { wiDList: List<WiD> ->
                val currentTime = LocalTime.MAX

                _fullWiDList.value = getFullWiDListFromWiDList(
                    date = collectionDate,
                    currentTime = currentTime,
                    wiDList = wiDList
                )
                _totalDurationMap.value = getTotalDurationMapByTitle(wiDList = _fullWiDList.value)

                setWiDListLoaded(loaded = true)
            }
        )
    }

    private fun setWiDListLoaded(loaded: Boolean) {
        Log.d(TAG, "setWiDListLoaded executed")

        _wiDListLoaded.value = loaded
    }

    fun setEmptyWiD(emptyWiD: WiD) {
        Log.d(TAG, "setEmptyWiD executed")

        wiDDataSource.setEmptyWiD(newWiD = emptyWiD)
    }

    fun setExistingWiD(existingWiD: WiD) {
        Log.d(TAG, "setExistingWiD executed")

        wiDDataSource.setExistingWiD(existingWiD = existingWiD)
    }

    fun setUpdatedWiD(updatedWiD: WiD) {
        Log.d(TAG, "setUpdatedWiD executed")

        wiDDataSource.setUpdatedWiD(updatedWiD = updatedWiD)
    }
}