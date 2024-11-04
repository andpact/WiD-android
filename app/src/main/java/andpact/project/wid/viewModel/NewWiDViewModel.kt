package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.levelRequiredExpMap
import andpact.project.wid.util.titleColorMap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class NewWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "NewWiDViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    /** 레벨 업 순간에 today 갱신되는지? */
    private val today: State<LocalDate> = wiDDataSource.today
    private var now = LocalTime.now()
    private val user: State<User?> = userDataSource.user

    // 제목
    private val _showTitleMenu = mutableStateOf(false)
    val showTitleMenu: State<Boolean> = _showTitleMenu

    // 시작
    private val _showStartPicker = mutableStateOf(false)
    val showStartPicker: State<Boolean> = _showStartPicker
    private val _startOverlap = mutableStateOf(false)
    val startOverlap: State<Boolean> = _startOverlap
    private val _startModified = mutableStateOf(false)
    val startModified: State<Boolean> = _startModified

    // 종료
    private val _showFinishPicker = mutableStateOf(false)
    val showFinishPicker: State<Boolean> = _showFinishPicker
    private val _finishOverlap = mutableStateOf(false)
    val finishOverlap: State<Boolean> = _finishOverlap
    private val _finishModified = mutableStateOf(false)
    val finishModified: State<Boolean> = _finishModified

    // 소요
    private val _durationExist = mutableStateOf(false)
    val durationExist: State<Boolean> = _durationExist

    // WiD List
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())

    // New WiD
    private var lastNewWiDTimer: Timer? = null
    val newWiD: State<WiD> = wiDDataSource.newWiD // 변경 전

    // Updated New WiD
    private var lastUpdatedNewWiDTimer: Timer? = null
    val updatedNewWiD: State<WiD> = wiDDataSource.updatedNewWiD // 변경 후
    private val _isLastUpdatedNewWiDTimerRunning = mutableStateOf(false) // Updated New WiD의 종료 시간이 실시간인지 파악하려고
    val isLastUpdatedNewWiDTimerRunning: State<Boolean> = _isLastUpdatedNewWiDTimerRunning

    fun startLastNewWiDTimer() { // 실행되면 뷰가 사라질 때까지 종료 되지 않음.
        Log.d(TAG, "startLastNewWiDTimer executed")

        lastNewWiDTimer = timer(period = 1_000) {
            now = LocalTime.now().withNano(0)

            val newWiD = newWiD.value.copy(
                finish = now,
                duration = Duration.between(newWiD.value.start, now)
            )

            setNewWiD(newWiD = newWiD)
        }
    }

    fun stopLastNewWiDTimer() {
        Log.d(TAG, "stopLastNewWiDTimer executed")

        lastNewWiDTimer?.cancel()
    }

    fun startLastUpdatedNewWiDTimer() {
        Log.d(TAG, "startLastUpdatedNewWiDTimer executed")

        setIsLastUpdatedNewWiDTimerRunning(running = true)

        lastUpdatedNewWiDTimer = timer(period = 1_000) {
            now = LocalTime.now().withNano(0)

            val updatedWiD = updatedNewWiD.value.copy(
                finish = now,
                duration = Duration.between(updatedNewWiD.value.start, now)
            )

            setUpdateNewWiD(updatedNewWiD = updatedWiD)
        }
    }

    fun stopLastUpdatedNewWiDTimer() {
        Log.d(TAG, "stopLastUpdatedNewWiDTimer executed")

        setIsLastUpdatedNewWiDTimerRunning(running = false)

        lastUpdatedNewWiDTimer?.cancel()
    }

    private fun setIsLastUpdatedNewWiDTimerRunning(running: Boolean) {
        Log.d(TAG, "setIsLastUpdatedNewWiDTimerRunning executed")

        _isLastUpdatedNewWiDTimerRunning.value = running
    }

    private fun setNewWiD(newWiD: WiD) {
        Log.d(TAG, "setNewWiD executed")

        wiDDataSource.setNewWiD(newWiD = newWiD)

        if (_wiDList.value.isNotEmpty()) {
            checkNewStartOverlap()
            checkNewFinishOverlap()
            checkNewWiDOverlap()
        }

        // 소요 시간은 검사할 필요 없음.
    }

    fun setUpdateNewWiD(updatedNewWiD: WiD) {
        Log.d(TAG, "setUpdateNewWiD executed")

        wiDDataSource.setUpdatedNewWiD(updatedNewWiD = updatedNewWiD)

        if (_wiDList.value.isNotEmpty()) {
            checkNewStartOverlap()
            checkNewFinishOverlap()
            checkNewWiDOverlap()
        }

        setDurationExist(Duration.ZERO < updatedNewWiD.duration)
    }

    fun setShowTitleMenu(show: Boolean) {
        Log.d(TAG, "setShowTitleMenu executed")

        _showTitleMenu.value = show
    }

    fun setShowStartPicker(show: Boolean) {
        Log.d(TAG, "setShowStartPicker executed")

        _showStartPicker.value = show
    }

    private fun setStartOverlap(overlap: Boolean) {
        Log.d(TAG, "setStartOverlap executed")

        _startOverlap.value = overlap
    }

    fun setStartModified(modified: Boolean) {
        Log.d(TAG, "setStartModified executed")

        _startModified.value = modified
    }

    fun setShowFinishPicker(show: Boolean) {
        Log.d(TAG, "setShowFinishPicker executed")

        _showFinishPicker.value = show
    }

    private fun setFinishOverlap(overlap: Boolean) {
        Log.d(TAG, "setFinishOverlap executed")

        _finishOverlap.value = overlap
    }

    fun setFinishModified(modified: Boolean) {
        Log.d(TAG, "setFinishModified executed")

        _finishModified.value = modified
    }

    private fun setDurationExist(exist: Boolean) {
        Log.d(TAG, "setDurationExist executed")

        _durationExist.value = exist
    }

    private fun checkNewStartOverlap() { // Updated New WiD의 시작 시간이 겹치는지 확인
        Log.d(TAG, "checkNewStartOverlap executed")

        now = LocalTime.now().withNano(0) // 타이머 멈춰 있을 때도 있기 때문에 갱신해줌.

        for (existingWiD in _wiDList.value) {
            if (existingWiD.start < updatedNewWiD.value.start && updatedNewWiD.value.start < existingWiD.finish) {
                setStartOverlap(overlap = true)
                break
            } else if (updatedNewWiD.value.date == today.value && now < updatedNewWiD.value.start) {
                setStartOverlap(overlap = true)
                break
            } else {
                setStartOverlap(overlap = false)
            }
        }
    }

    private fun checkNewFinishOverlap() { // Updated New WiD의 종료 시간이 겹치는지 확인
        Log.d(TAG, "checkNewFinishOverlap executed")

        now = LocalTime.now().withNano(0)

        for (existingWiD in _wiDList.value) {
            if (existingWiD.start < updatedNewWiD.value.finish && updatedNewWiD.value.finish < existingWiD.finish) {
                setFinishOverlap(overlap = true)
                break
            } else if (updatedNewWiD.value.date == today.value && now < updatedNewWiD.value.finish) {
                setFinishOverlap(overlap = true)
                break
            } else {
                setFinishOverlap(overlap = false)
            }
        }
    }

    private fun checkNewWiDOverlap() { // Updated New WiD가 기존의 WiD를 덮고 있는지 확인
        Log.d(TAG, "checkNewWiDOverlap executed")

        for (existingWiD in _wiDList.value) {
            // 등호를 넣어서 부등호를 사용해야 기존의 WiD를 덮고 있는지를 정확히 확인할 수 있다.
            if (updatedNewWiD.value.start <= existingWiD.start && existingWiD.finish <= updatedNewWiD.value.finish) {
                setStartOverlap(overlap = true)
                setFinishOverlap(overlap = true)
                break
            }
        }
    }

    fun getWiDListByDate(collectionDate: LocalDate) {
        Log.d(TAG, "getWiDListByDate executed")

        wiDDataSource.getWiDListOfDate(
            email = user.value?.email ?: "",
            collectionDate = collectionDate,
            onWiDListFetchedByDate = { wiDList: List<WiD> ->
                _wiDList.value = wiDList
            }
        )
    }

    fun createWiD(onWiDCreated: (Boolean) -> Unit) {
        Log.d(TAG, "createWiD executed")

        wiDDataSource.createWiD(
            email = user.value?.email ?: "",
            onWiDCreated = { wiDCreated: Boolean ->
                if (wiDCreated) {
                    // 레벨
                    val currentLevel = user.value?.level ?: 1
                    // 경험치
                    val currentExp = user.value?.currentExp ?: 0
                    val currentLevelRequiredExp = levelRequiredExpMap[currentLevel] ?: 0
                    val newExp = updatedNewWiD.value.duration.seconds.toInt()
                    val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                    val newWiDTotalExp = wiDTotalExp + newExp
                    // 제목
                    val title = updatedNewWiD.value.title
                    val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
                    val currentTitleCount = titleCountMap[title] ?: 0
                    titleCountMap[title] = currentTitleCount + 1
                    val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
                    val currentTitleDuration = titleDurationMap[title] ?: Duration.ZERO
                    titleDurationMap[title] = currentTitleDuration.plus(Duration.ofSeconds(newExp.toLong()))
                    // 도구
                    val createdBy = updatedNewWiD.value.createdBy
                    val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
                    val currentToolCount = toolCountMap[createdBy] ?: 0
                    toolCountMap[createdBy] = currentToolCount + 1
                    val toolDurationMap = user.value?.wiDToolDurationMap?.toMutableMap() ?: mutableMapOf()
                    val currentToolDuration = toolDurationMap[createdBy] ?: Duration.ZERO
                    toolDurationMap[createdBy] = currentToolDuration.plus(Duration.ofSeconds(newExp.toLong()))

                    if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                        // 레벨
                        val newLevel = currentLevel + 1
                        val newLevelAsString = newLevel.toString()
                        val levelDateMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                        levelDateMap[newLevelAsString] = LocalDate.now()

                        // 경험
                        val newCurrentExp = currentExp + newExp - currentLevelRequiredExp

                        userDataSource.createdWiDWithLevelUp(
                            newLevel = newLevel,
                            newLevelUpHistoryMap = levelDateMap,
                            newCurrentExp = newCurrentExp,
                            newWiDTotalExp = newWiDTotalExp,
                            newTitleCountMap = titleCountMap,
                            newTitleDurationMap = titleDurationMap,
                            newToolCountMap = toolCountMap,
                            newToolDurationMap = toolDurationMap
                        )
                    } else { // 레벨 업 아님
                        // 경험치
                        val newCurrentExp = currentExp + newExp

                        userDataSource.createdWiD(
                            newCurrentExp = newCurrentExp,
                            newWiDTotalExp = newWiDTotalExp,
                            newTitleCountMap = titleCountMap,
                            newTitleDurationMap = titleDurationMap,
                            newToolCountMap = toolCountMap,
                            newToolDurationMap = toolDurationMap
                        )
                    }

                    /** 서버 통신 성공하고, 클라 내의 유저도 수정한 후 콜백 반환해야 하는 거 아닌가? */
                    onWiDCreated(true)
                } else {
                    onWiDCreated(false)
                }
            }
        )
    }
}