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
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class DailyWiDListViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource,
) : ViewModel() {
    private val TAG = "DailyWiDListViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    // 날짜
    private val _currentDate = mutableStateOf(LocalDate.now())
    val currentDate: State<LocalDate> = _currentDate
    private val _showDatePicker = mutableStateOf(false)
    val showDatePicker: State<Boolean> = _showDatePicker

    // 도구
    val currentTool: State<CurrentTool> = wiDDataSource.currentTool
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    // WiD List
    private val _fullWiDListLoaded = mutableStateOf(false)
    val fullWiDListLoaded: State<Boolean> = _fullWiDListLoaded
    private var wiDList: List<WiD> = emptyList() // State아니면 갱신 안되는듯?
    private val _fullWiDList = mutableStateOf<List<WiD>>(emptyList())
    val fullWiDList: State<List<WiD>> = _fullWiDList

    // 합계
    private val _totalDurationMap = mutableStateOf(getWiDTitleTotalDurationMap(wiDList = _fullWiDList.value))
    val totalDurationMap: State<Map<Title, Duration>> = _totalDurationMap

    // Current WiD
    val date: State<LocalDate> = wiDDataSource.date
    val start: State<LocalTime> = wiDDataSource.start
    val finish: State<LocalTime> = wiDDataSource.finish

    // Last New WiD
    private var lastNewWiDTimer: Timer? = null
//    private val _isLastNewWiDTimerRunning = mutableStateOf(false)
//    val isLastNewWiDTimerRunning: State<Boolean> = _isLastNewWiDTimerRunning

//    private fun setIsLastNewWiDTimerRunning(running: Boolean) {
//        Log.d(TAG, "setIsLastNewWiDTimerRunning executed")
//
//        _isLastNewWiDTimerRunning.value = running
//    }

    fun setToday(newDate: LocalDate) {
        Log.d(TAG, "setToday executed")

        wiDDataSource.setToday(newDate = newDate)
    }

    fun setShowDatePicker(show: Boolean) {
        Log.d(TAG, "setShowDatePicker executed")

        _showDatePicker.value = show
    }

    fun setCurrentDate(
        today: LocalDate,
        newDate: LocalDate
    ) {
        Log.d(TAG, "setCurrentDate executed")

        _currentDate.value = newDate

        setFullWiDListLoaded(wiDListLoaded = false)
        getWiDListOfDate(collectionDate = newDate)

        // 위치 여기 맞나?
        lastNewWiDTimer?.cancel()

        if (newDate == today) { // 오늘 날짜 조회
            if (currentToolState.value != CurrentToolState.STARTED) { // 도구 중지 및 정지 상태
//                setFullWiDList(
//                    today = today,
//                    collectionDate = newDate,
//                    wiDList = wiDList,
//                    currentTime = LocalTime.now().withNano(0)
//                )

                lastNewWiDTimer = timer(period = 1_000) {
                    val currentTime = LocalTime.now().withNano(0)

                    // LocalTime.MIN에 메서드가 실행되는 것 = Full WiD List에 WiD가 하나 있는 것 -> 화면에 WiD를 표시 하지 않음(Full WiD List에 최소 1초짜리 WiD가 있어야 함)
                    if (currentTime == LocalTime.MIN) {
                        lastNewWiDTimer?.cancel()
                    } else {
                        setFullWiDList(
                            today = today,
                            collectionDate = newDate,
                            wiDList = wiDList,
                            currentTime = currentTime
                        )
                    }
                }
            } else { // 도구 시작 상태
                setFullWiDList(
                    today = today,
                    collectionDate = newDate,
                    wiDList = wiDList,
                    currentTime = null
                )
            }
        } else { // 오늘 아닌 날짜 조회
            setFullWiDList(
                today = today,
                collectionDate = newDate,
                wiDList = wiDList,
                currentTime = null
            )
        }
    }

    fun stopLastNewWiDTimer() {
        Log.d(TAG, "stopLastNewWiDTimer executed")

        lastNewWiDTimer?.cancel()
    }

    private fun getWiDListOfDate(collectionDate: LocalDate) {
        Log.d(TAG, "getWiDListOfDate executed")

        wiDDataSource.getWiDListOfDate(
            email = user.value?.email ?: "",
            collectionDate = collectionDate,
            onWiDListFetchedByDate = { fetchedWiDList: List<WiD> ->
                /** 복구 */
                wiDList = sampleDailyWiDList

//                wiDList = fetchedWiDList
            }
        )
    }

    private fun setFullWiDList(
        today: LocalDate,
        collectionDate: LocalDate,
        wiDList: List<WiD>,
        currentTime: LocalTime?
    ) {
        Log.d(TAG, "setFullWiDList executed")

        _fullWiDList.value = getFullWiDListFromWiDList(
            date = collectionDate,
            wiDList = wiDList,
            today = today,
            currentTime = currentTime
        )

        setFullWiDListLoaded(wiDListLoaded = true)

        setTotalDurationMap(fullWiDList = _fullWiDList.value)
    }

    private fun setTotalDurationMap(fullWiDList: List<WiD>) {
        Log.d(TAG, "setTotalDurationMap executed")

        _totalDurationMap.value = getWiDTitleTotalDurationMap(wiDList = fullWiDList)
    }

    private fun setFullWiDListLoaded(wiDListLoaded: Boolean) {
        Log.d(TAG, "setFullWiDListLoaded executed")

        _fullWiDListLoaded.value = wiDListLoaded
    }

    fun setNewWiD(newWiD: WiD) {
        Log.d(TAG, "setNewWiD executed")

        wiDDataSource.setNewWiD(newWiD = newWiD)
    }

    fun setUpdatedNewWiD(updatedNewWiD: WiD) {
        Log.d(TAG, "setUpdatedNewWiD executed")

        wiDDataSource.setUpdatedNewWiD(updatedNewWiD = updatedNewWiD)
    }

    fun setExistingWiD(existingWiD: WiD) {
        Log.d(TAG, "setExistingWiD executed")

        wiDDataSource.setWiD(wiD = existingWiD)
    }

    fun setUpdatedWiD(updatedWiD: WiD) {
        Log.d(TAG, "setUpdatedWiD executed")

        wiDDataSource.setUpdatedWiD(updatedWiD = updatedWiD)
    }
}