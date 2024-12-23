package andpact.project.wid.tmp

//import andpact.project.wid.dataSource.UserDataSource
//import andpact.project.wid.dataSource.WiDDataSource
//import andpact.project.wid.model.User
//import andpact.project.wid.model.WiD
//import android.util.Log
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.text.AnnotatedString
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import java.time.Duration
//import java.time.LocalDate
//import java.time.LocalTime
//import java.util.Timer
//import javax.inject.Inject
//import kotlin.concurrent.timer
//
//@HiltViewModel
//class NewWiDViewModel @Inject constructor(
//    private val userDataSource: UserDataSource,
//    private val wiDDataSource: WiDDataSource
//): ViewModel() {
//    private val TAG = "NewWiDViewModel"
//    init { Log.d(TAG, "created") }
//    override fun onCleared() {
//        super.onCleared()
//        Log.d(TAG, "cleared")
//    }
//
//    private val today: State<LocalDate> = wiDDataSource.today
//    private var now = LocalTime.now()
//    private val user: State<User?> = userDataSource.user
//
//    // 제목
//    private val _showTitleMenu = mutableStateOf(false)
//    val showTitleMenu: State<Boolean> = _showTitleMenu
//    private val _titleExist = mutableStateOf(false)
//    val titleExist: State<Boolean> = _titleExist
//
//    // 시작
//    private val _showStartPicker = mutableStateOf(false)
//    val showStartPicker: State<Boolean> = _showStartPicker
//    private val _startOverlap = mutableStateOf(false)
//    val startOverlap: State<Boolean> = _startOverlap
//    private val _startModified = mutableStateOf(false)
//    val startModified: State<Boolean> = _startModified
//    // 종료
//    private val _showFinishPicker = mutableStateOf(false)
//    val showFinishPicker: State<Boolean> = _showFinishPicker
//    private val _finishOverlap = mutableStateOf(false)
//    val finishOverlap: State<Boolean> = _finishOverlap
//    private val _finishModified = mutableStateOf(false)
//    val finishModified: State<Boolean> = _finishModified
//    // 소요
//    private val _durationExist = mutableStateOf(false)
//    val durationExist: State<Boolean> = _durationExist
//
//    // WiD List
//    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
//
//    // New WiD
//    private var lastNewWiDTimer: Timer? = null
//    val newWiD: State<WiD> = wiDDataSource.newWiD // 변경 전
//
//    // Updated New WiD
//    private var lastUpdatedNewWiDTimer: Timer? = null
//    val updatedNewWiD: State<WiD> = wiDDataSource.updatedNewWiD // 변경 후
//    private val _isLastUpdatedNewWiDTimerRunning = mutableStateOf(false) // Updated New WiD의 종료 시간이 실시간인지 파악하려고
//    val isLastUpdatedNewWiDTimerRunning: State<Boolean> = _isLastUpdatedNewWiDTimerRunning
//
//    fun updateLastNewWiD() {
//        Log.d(TAG, "updateLastNewWiD executed")
//    }
//
//    fun startLastNewWiDTimer() { // 실행되면 뷰가 사라질 때까지 종료 되지 않음.
//        Log.d(TAG, "startLastNewWiDTimer executed")
//
//        /** 자정 넘어가면? */
//        lastNewWiDTimer = timer(period = 1_000) {
//            now = LocalTime.now().withNano(0)
//
//            val newWiD = newWiD.value.copy(
//                finish = now,
//                duration = Duration.between(newWiD.value.start, now)
//            )
//
//            setNewWiD(newWiD = newWiD)
//        }
//    }
//
//    fun stopLastNewWiDTimer() {
//        Log.d(TAG, "stopLastNewWiDTimer executed")
//
//        lastNewWiDTimer?.cancel()
//    }
//
//    fun startLastUpdatedNewWiDTimer() {
//        Log.d(TAG, "startLastUpdatedNewWiDTimer executed")
//
//        setIsLastUpdatedNewWiDTimerRunning(running = true)
//
//        lastUpdatedNewWiDTimer = timer(period = 1_000) {
//            now = LocalTime.now().withNano(0)
//
//            val updatedWiD = updatedNewWiD.value.copy(
//                finish = now,
//                duration = Duration.between(updatedNewWiD.value.start, now)
//            )
//
//            setUpdateNewWiD(updatedNewWiD = updatedWiD)
//        }
//    }
//
//    fun stopLastUpdatedNewWiDTimer() {
//        Log.d(TAG, "stopLastUpdatedNewWiDTimer executed")
//
//        setIsLastUpdatedNewWiDTimerRunning(running = false)
//
//        lastUpdatedNewWiDTimer?.cancel()
//    }
//
//    private fun setIsLastUpdatedNewWiDTimerRunning(running: Boolean) {
//        Log.d(TAG, "setIsLastUpdatedNewWiDTimerRunning executed")
//
//        _isLastUpdatedNewWiDTimerRunning.value = running
//    }
//
//    private fun setNewWiD(newWiD: WiD) {
//        Log.d(TAG, "setNewWiD executed")
//
//        wiDDataSource.setNewWiD(newWiD = newWiD)
//
////        if (_wiDList.value.isNotEmpty()) {
////            checkNewStartOverlap()
////            checkNewFinishOverlap()
////            checkNewWiDOverlap()
////        }
//    }
//
//    fun setUpdateNewWiD(updatedNewWiD: WiD) {
//        Log.d(TAG, "setUpdateNewWiD executed")
//
//        wiDDataSource.setUpdatedNewWiD(updatedNewWiD = updatedNewWiD)
//
//        if (_wiDList.value.isNotEmpty()) {
//            checkNewStartOverlap()
//            checkNewFinishOverlap()
//            checkNewWiDOverlap()
//        }
//
//        setDurationExist(Duration.ZERO < updatedNewWiD.duration)
//    }
//
//    fun setShowTitleMenu(show: Boolean) {
//        Log.d(TAG, "setShowTitleMenu executed")
//
//        _showTitleMenu.value = show
//    }
//
//    fun setTitleExist(exist: Boolean) {
//        Log.d(TAG, "setTitleExist executed")
//
//        _titleExist.value = exist
//    }
//
//    fun setShowStartPicker(show: Boolean) {
//        Log.d(TAG, "setShowStartPicker executed")
//
//        _showStartPicker.value = show
//    }
//
//    private fun setStartOverlap(overlap: Boolean) {
//        Log.d(TAG, "setStartOverlap executed")
//
//        _startOverlap.value = overlap
//    }
//
//    fun setStartModified(modified: Boolean) {
//        Log.d(TAG, "setStartModified executed")
//
//        _startModified.value = modified
//    }
//
//    fun setShowFinishPicker(show: Boolean) {
//        Log.d(TAG, "setShowFinishPicker executed")
//
//        _showFinishPicker.value = show
//    }
//
//    private fun setFinishOverlap(overlap: Boolean) {
//        Log.d(TAG, "setFinishOverlap executed")
//
//        _finishOverlap.value = overlap
//    }
//
//    fun setFinishModified(modified: Boolean) {
//        Log.d(TAG, "setFinishModified executed")
//
//        _finishModified.value = modified
//    }
//
//    private fun setDurationExist(exist: Boolean) {
//        Log.d(TAG, "setDurationExist executed")
//
//        _durationExist.value = exist
//    }
//
//    private fun checkNewStartOverlap() { // Updated New WiD의 시작 시간이 겹치는지 확인
//        Log.d(TAG, "checkNewStartOverlap executed")
//
//        now = LocalTime.now().withNano(0) // 타이머 멈춰 있을 때도 있기 때문에 갱신해줌.
//
//        for (existingWiD in _wiDList.value) {
//            if (existingWiD.start < updatedNewWiD.value.start && updatedNewWiD.value.start < existingWiD.finish) { //
//                setStartOverlap(overlap = true)
//                break
//            } else if (updatedNewWiD.value.date == today.value && now < updatedNewWiD.value.start) { //
//                setStartOverlap(overlap = true)
//                break
//            } else {
//                setStartOverlap(overlap = false)
//            }
//        }
//    }
//
//    private fun checkNewFinishOverlap() { // Updated New WiD의 종료 시간이 겹치는지 확인
//        Log.d(TAG, "checkNewFinishOverlap executed")
//
//        now = LocalTime.now().withNano(0)
//
//        for (existingWiD in _wiDList.value) {
//            if (existingWiD.start < updatedNewWiD.value.finish && updatedNewWiD.value.finish < existingWiD.finish) {
//                setFinishOverlap(overlap = true)
//                break
//            } else if (updatedNewWiD.value.date == today.value && now < updatedNewWiD.value.finish) {
//                setFinishOverlap(overlap = true)
//                break
//            } else {
//                setFinishOverlap(overlap = false)
//            }
//        }
//    }
//
//    private fun checkNewWiDOverlap() { // Updated New WiD가 기존의 WiD를 덮고 있는지 확인
//        Log.d(TAG, "checkNewWiDOverlap executed")
//
//        for (existingWiD in _wiDList.value) {
//            if (updatedNewWiD.value.start <= existingWiD.start && existingWiD.finish <= updatedNewWiD.value.finish) { // 등호를 넣어서 부등호를 사용해야 기존의 WiD를 덮고 있는지를 정확히 확인할 수 있다.
//                setStartOverlap(overlap = true)
//                setFinishOverlap(overlap = true)
//                break
//            }
//        }
//    }
//
//    fun getWiDListOfDate(collectionDate: LocalDate) {
//        Log.d(TAG, "getWiDListOfDate executed")
//
//        wiDDataSource.getWiDListOfDate(
//            email = user.value?.email ?: "",
//            date = collectionDate,
//            onWiDListFetchedOfDate = { wiDList: List<WiD> ->
//                _wiDList.value = wiDList
//            }
//        )
//    }
//
//    fun createWiD(onWiDCreated: (Boolean) -> Unit) {
//        Log.d(TAG, "createWiD executed")
//
//        wiDDataSource.createWiD(
//            email = user.value?.email ?: "",
//            onWiDAdded = { wiDAdded: Boolean ->
//                if (wiDAdded) {
//                    onWiDCreated(true)
//                } else {
//                    onWiDCreated(false)
//                }
//            }
//        )
//    }
//
//    fun getDurationString(duration: Duration): String {
//        Log.d(TAG, "getDurationString executed")
//
//        return wiDDataSource.getDurationString(duration = duration)
//    }
//
//    @Composable
//    fun getDateString(date: LocalDate): AnnotatedString {
//        Log.d(TAG, "getDateString executed")
//
//        return wiDDataSource.getDateString(date = date)
//    }
//
//    fun getTimeString(time: LocalTime): String {
//        Log.d(TAG, "getTimeString executed")
//        // 'HH:mm:ss'
//
//        return wiDDataSource.getTimeString(time = time)
//    }
//}