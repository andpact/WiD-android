package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.levelToRequiredExpMap
import andpact.project.wid.util.titleToColorMap
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
class WiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "WiDViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val today = wiDDataSource.today.value
    private var now = LocalTime.now()
    private val user: State<User?> = userDataSource.user
    val titleColorMap = titleToColorMap

    private val _expandMenu = mutableStateOf(false)
    val expandMenu: State<Boolean> = _expandMenu

    /** 기존 WiD와 수정된 WiD의 차이를 파악하기 위해 필요함, 화면에 차이를 표시할 수도 있겠는데? */
    // WiD
    val wiD: State<WiD> = wiDDataSource.wiD // 수정 전
    private val _showDeleteWiDDialog = mutableStateOf(false)
    val showDeleteWiDDialog: State<Boolean> = _showDeleteWiDDialog

    // Updated WiD
    val updatedWiD: State<WiD> = wiDDataSource.updatedWiD // 수정 후
    private var updatedWiDTimer: Timer? = null

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
    private val _durationExist = mutableStateOf(true)
    val durationExist: State<Boolean> = _durationExist

    // WiD List
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())

    fun setExpandMenu(expand: Boolean) {
        Log.d(TAG, "setExpandMenu executed")

        _expandMenu.value = expand
    }

    fun setUpdatedWiD(updatedWiD: WiD) {
        Log.d(TAG, "setUpdatedWiD executed")

        wiDDataSource.setUpdatedWiD(updatedWiD = updatedWiD)

        checkNewStartOverlap()
        checkNewFinishOverlap()
        checkNewWiDOverlap()
        checkDurationExist()
    }

    fun startUpdatedWiDTimer() {
        Log.d(TAG, "startUpdatedWiDTimer executed")

        updatedWiDTimer = timer(period = 1_000) {
            now = LocalTime.now().withNano(0)

            val updatedWiD = updatedWiD.value.copy(
                finish = now,
                duration = Duration.between(updatedWiD.value.start, now)
            )

            setUpdatedWiD(updatedWiD = updatedWiD)
        }
    }

    fun stopUpdatedWiDTimer() {
        Log.d(TAG, "stopUpdatedWiDTimer executed")

        updatedWiDTimer?.cancel()
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

    fun setShowDeleteWiDDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteWiDDialog executed")

        _showDeleteWiDDialog.value = show
    }

    private fun checkNewStartOverlap() { // 생성할 WiD의 시작 시간이 겹치는지 확인
        Log.d(TAG, "checkNewStartOverlap executed")

        now = LocalTime.now().withNano(0)

        for (existingWiD in _wiDList.value) {
            if (updatedWiD.value.id == existingWiD.id) {
                continue
            }

            if (existingWiD.start < updatedWiD.value.start && updatedWiD.value.start < existingWiD.finish) {
                setStartOverlap(overlap = true)
                break
            } else if (updatedWiD.value.date == today && now < updatedWiD.value.start) {
                setStartOverlap(overlap = true)
                break
            } else {
                setStartOverlap(overlap = false)
            }
        }
    }

    private fun checkNewFinishOverlap() { // 생성할 WiD의 종료 시간이 겹치는지 확인
        Log.d(TAG, "checkNewFinishOverlap executed")

        now = LocalTime.now().withNano(0)

        for (existingWiD in _wiDList.value) {
            if (updatedWiD.value.id == existingWiD.id) {
                continue
            }

            if (existingWiD.start < updatedWiD.value.finish && updatedWiD.value.finish < existingWiD.finish) {
                setFinishOverlap(overlap = true)
                break
            } else if (updatedWiD.value.date == today && now < updatedWiD.value.finish) {
                setFinishOverlap(overlap = true)
                break
            } else {
                setFinishOverlap(overlap = false)
            }
        }
    }

    private fun checkNewWiDOverlap() { // 생성할 WiD가 기존의 WiD를 덮고 있는지 확인
        Log.d(TAG, "checkNewWiDOverlap executed")

        for (existingWiD in _wiDList.value) {
            if (updatedWiD.value.id == existingWiD.id) {
                continue
            }

            // 등호를 넣어서 부등호를 사용해야 기존의 WiD를 덮고 있는지를 정확히 확인할 수 있다.
            if (updatedWiD.value.start <= existingWiD.start && existingWiD.finish <= updatedWiD.value.finish) {
                setStartOverlap(overlap = true)
                setFinishOverlap(overlap = true)
                break
            } else {
                setStartOverlap(overlap = false)
                setFinishOverlap(overlap = false)
            }
        }
    }

    private fun checkDurationExist() {
        Log.d(TAG, "checkDurationExist executed")

        setDurationExist(exist = Duration.ZERO < updatedWiD.value.duration)
    }

    fun getWiDListByDate(currentDate: LocalDate) {
        Log.d(TAG, "getWiDListByDate executed")

        wiDDataSource.getWiDListOfDate(
            email = user.value?.email ?: "",
            collectionDate = currentDate,
            onWiDListFetchedByDate = { wiDList: List<WiD> ->
                _wiDList.value = wiDList
            }
        )
    }

    fun updateWiD(onWiDUpdated: (Boolean) -> Unit) {
        Log.d(TAG, "updateWiD executed")

        wiDDataSource.updateWiD(
            email = user.value?.email ?: "",
            onWiDUpdated = { wiDUpdated: Boolean ->
                if (wiDUpdated) { // 업데이트 성공
                    // 레벨
                    val currentLevel = user.value?.level ?: 1
                    val currentLevelAsString = currentLevel.toString()

                    // 경험치
                    val currentExp = user.value?.currentExp ?: 0
                    val currentLevelRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0
                    val exp = wiD.value.duration.seconds.toInt()
                    val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                    val updatedExp = updatedWiD.value.duration.seconds.toInt()
                    val updatedWiDTotalExp = wiDTotalExp - exp + updatedExp

                    // 제목
                    val title = wiD.value.title
                    val updatedTitle = updatedWiD.value.title
                    val duration = wiD.value.duration
                    val updatedDuration = updatedWiD.value.duration
                    val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
                    val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()

                    if (title == updatedTitle) { // 제목 변경 안함.
                        val currentTitleDuration = titleDurationMap[title] ?: Duration.ZERO
                        titleDurationMap[title] = currentTitleDuration - duration + updatedDuration
                    } else { // 제목 변경
                        val currentTitleCount = titleCountMap[title] ?: 0
                        titleCountMap[title] = currentTitleCount - 1
                        val currentTitleDuration = titleDurationMap[title] ?: Duration.ZERO
                        titleDurationMap[title] = currentTitleDuration - duration

                        val currentUpdatedTitleCount = titleCountMap[updatedTitle] ?: 0
                        titleCountMap[updatedTitle] = currentUpdatedTitleCount + 1
                        val currentUpdatedTitleDuration = titleDurationMap[updatedTitle] ?: Duration.ZERO
                        titleDurationMap[updatedTitle] = currentUpdatedTitleDuration + updatedDuration
                    }

                    if (currentLevelRequiredExp <= currentExp - exp + updatedExp) { // 레벨 업
                        // 레벨
                        val updatedLevel = currentLevel + 1
                        val newLevelAsString = updatedLevel.toString()
                        val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                        levelUpHistoryMap[newLevelAsString] = LocalDate.now()

                        // 경험치
                        val updatedCurrentExp = currentExp - exp + updatedExp - currentLevelRequiredExp

                        userDataSource.updateWiDWithLevelUp(
                            newLevel = updatedLevel,
                            newLevelUpHistoryMap = levelUpHistoryMap,
                            newCurrentExp = updatedCurrentExp,
                            newWiDTotalExp = updatedWiDTotalExp,
                            newTitleCountMap = titleCountMap,
                            newTitleDurationMap = titleDurationMap,
                        )
                    } else {
                        val updatedCurrentExp = currentExp - exp + updatedExp // 마이너스 값 나올 수 있음.

                        userDataSource.updateWiD(
                            newCurrentExp = updatedCurrentExp,
                            newWiDTotalExp = updatedWiDTotalExp,
                            newTitleCountMap = titleCountMap,
                            newTitleDurationMap = titleDurationMap,
                        )
                    }

                    onWiDUpdated(true)
                } else { // 업데이트 실패
                    onWiDUpdated(false)
                }
            }
        )
    }

    // 삭제할 때는 wiD를 사용해야 함.
    fun deleteWiD(onWiDDeleted: (Boolean) -> Unit) {
        Log.d(TAG, "deleteWiD executed")

        wiDDataSource.deleteWiD(
            email = user.value?.email ?: "",
            onWiDDeleted = { wiDDeleted: Boolean ->
                if (wiDDeleted) { // 삭제 성공
                    // 경험치
                    val currentExp = user.value?.currentExp ?: 0
                    val exp = wiD.value.duration.seconds.toInt()
                    val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                    val prevWiDTotalExp = wiDTotalExp - exp

                    // 제목
                    val title = wiD.value.title
                    val titleCountMap = user.value?.wiDTitleCountMap?.toMutableMap() ?: mutableMapOf()
                    val currentTitleCount = titleCountMap[title] ?: 0
                    titleCountMap[title] = currentTitleCount - 1
                    val duration = wiD.value.duration
                    val titleDurationMap = user.value?.wiDTitleDurationMap?.toMutableMap() ?: mutableMapOf()
                    val currentTitleDuration = titleDurationMap[title] ?: Duration.ZERO
                    titleDurationMap[title] = currentTitleDuration.minus(duration)

                    // 도구
                    val createdBy = wiD.value.createdBy
                    val toolCountMap = user.value?.wiDToolCountMap?.toMutableMap() ?: mutableMapOf()
                    val currentToolCount = toolCountMap[createdBy] ?: 0
                    toolCountMap[createdBy] = currentToolCount - 1

                    val prevCurrentExp = currentExp - exp // 마이너스 나올 수 있음.

                    userDataSource.deleteWiD(
                        newCurrentExp = prevCurrentExp,
                        newWiDTotalExp = prevWiDTotalExp,
                        newTitleCountMap = titleCountMap,
                        newTitleDurationMap = titleDurationMap,
                        newToolCountMap = toolCountMap
                    )

                    onWiDDeleted(true)
                } else { // 삭제 실패
                    onWiDDeleted(false)
                }
            }
        )
    }
}