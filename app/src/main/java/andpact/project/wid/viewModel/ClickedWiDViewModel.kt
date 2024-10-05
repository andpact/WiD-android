package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.levelToRequiredExpMap
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
class ClickedWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "ClickedWiDViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    val titleColorMap = titleNumberStringToTitleColorMap

    /** 기존 WiD와 수정된 WiD의 차이를 파악하기 위해 필요함, 화면에 차이를 표시할 수도 있겠는데? */
    private val existingWiD: State<WiD> = wiDDataSource.existingWiD
    val updatedWiD: State<WiD> = wiDDataSource.updatedWiD

    private val _showTitleMenu = mutableStateOf(false)
    val showTitleMenu: State<Boolean> = _showTitleMenu

    private val _showStartPicker = mutableStateOf(false)
    val showStartPicker: State<Boolean> = _showStartPicker
    private val _startOverlap = mutableStateOf(false)
    val startOverlap: State<Boolean> = _startOverlap
    private val _startModified = mutableStateOf(false)
    val startModified: State<Boolean> = _startModified

    private val _showFinishPicker = mutableStateOf(false)
    val showFinishPicker: State<Boolean> = _showFinishPicker
    private val _finishOverlap = mutableStateOf(false)
    val finishOverlap: State<Boolean> = _finishOverlap
    private val _finishModified = mutableStateOf(false)
    val finishModified: State<Boolean> = _finishModified

    private val _durationExist = mutableStateOf(true)
    val durationExist: State<Boolean> = _durationExist

    private val _showDeleteClickedWiDDialog = mutableStateOf(false)
    val showDeleteClickedWiDDialog: State<Boolean> = _showDeleteClickedWiDDialog

    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())

    fun setUpdatedWiD(updatedWiD: WiD) {
        Log.d(TAG, "setUpdatedWiD executed")

        wiDDataSource.setUpdatedWiD(updatedWiD = updatedWiD)
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

    fun setShowDeleteClickedWiDDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteClickedWiDDialog executed")

        _showDeleteClickedWiDDialog.value = show
    }

    fun checkDurationExist() {
        Log.d(TAG, "checkDurationExist executed")

        setDurationExist(Duration.ZERO < updatedWiD.value.duration)
    }

    fun checkNewStartOverlap() { // 생성할 WiD의 시작 시간이 겹치는지 확인
        Log.d(TAG, "checkNewStartOverlap executed")

        val today = LocalDate.now()
        val now = LocalTime.now()

        for (existingWiD in _wiDList.value) {
            if (updatedWiD.value == existingWiD) {
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

    fun checkNewFinishOverlap() { // 생성할 WiD의 종료 시간이 겹치는지 확인
        Log.d(TAG, "checkNewFinishOverlap executed")

        val today = LocalDate.now()
        val now = LocalTime.now()

        for (existingWiD in _wiDList.value) {
            if (updatedWiD.value == existingWiD) {
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

    fun checkNewWiDOverlap() { // 생성할 WiD가 기존의 WiD를 덮고 있는지 확인
        Log.d(TAG, "checkNewWiDOverlap executed")

        for (existingWiD in _wiDList.value) {
            if (updatedWiD.value == existingWiD) {
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

    fun getWiDListByDate(currentDate: LocalDate) {
        Log.d(TAG, "getWiDListByDate executed")

        wiDDataSource.getWiDListByDate(
            email = user.value?.email ?: "",
            collectionDate = currentDate,
            onWiDListFetchedByDate = { wiDList: List<WiD> ->
                _wiDList.value = wiDList
            }
        )

        /** 이 뷰에서 리스너를 사용할 일이 있을까? */
//        wiDDataSource.addSnapshotListenerToWiDCollectionByDate(
//            email = user.value?.email ?: "",
//            collectionDate = currentDate,
//            onWiDCollectionChanged = { wiDList: List<WiD> ->
//                _wiDList.value = wiDList
//            }
//        )
    }

    fun updateWiD(onWiDUpdated: (Boolean) -> Unit) {
        Log.d(TAG, "updateWiD executed")

        wiDDataSource.updateWiD(
            email = user.value?.email ?: "",
            onWiDUpdated = { wiDUpdated: Boolean ->
                if (wiDUpdated) {
                    val currentExp = user.value?.currentExp ?: 0

                    val existingExp = existingWiD.value.duration.seconds.toInt()
                    val updatedExp = updatedWiD.value.duration.seconds.toInt()

                    val currentTotalExp = user.value?.totalExp ?: 0
                    val updatedTotalExp = currentTotalExp - existingExp + updatedExp
                    val currentWiDTotalExp = user.value?.totalExp ?: 0
                    val updatedWiDTotalExp = currentWiDTotalExp - existingExp + updatedExp

                    val title = updatedWiD.value.title
                    val existingDuration = existingWiD.value.duration
                    val updatedDuration = updatedWiD.value.duration
                    val titleDurationMap = user.value?.titleDurationMap?.toMutableMap() ?: mutableMapOf()
                    val currentDuration = titleDurationMap[title] ?: Duration.ZERO
                    titleDurationMap[title] = currentDuration - existingDuration + updatedDuration

                    if (currentExp - existingExp + updatedExp < 0) { // 레벨 다운
                        val currentLevel = user.value?.level ?: 1
                        val currentLevelAsString = currentLevel.toString()
                        val updatedLevel = currentLevel - 1

                        val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                        levelUpHistoryMap.remove(currentLevelAsString)

                        val updatedLevelRequiredExp = levelToRequiredExpMap[updatedLevel] ?: 0
                        val updatedCurrentExp = updatedLevelRequiredExp - existingExp + updatedExp

                        userDataSource.updateWiDWithLevelDown(
                            newLevel = updatedLevel,
                            newLevelUpHistoryMap = levelUpHistoryMap,
                            newCurrentExp = updatedCurrentExp,
                            newTotalExp = updatedTotalExp,
                            newWiDTotalExp = updatedWiDTotalExp,
                            newTitleDurationMap = titleDurationMap,
                        )
                    } else {
                        val updatedCurrentExp = currentExp - existingExp + updatedExp

                        userDataSource.updateWiD(
                            newCurrentExp = updatedCurrentExp,
                            newTotalExp = updatedTotalExp,
                            newWiDTotalExp = updatedWiDTotalExp,
                            newTitleDurationMap = titleDurationMap,
                        )
                    }

                    onWiDUpdated(true)
                } else {
                    onWiDUpdated(false)
                }
            }
        )
    }

    // 삭제할 때는 existingWiD를 사용해야 함.
    fun deleteWiD(onWiDDeleted: (Boolean) -> Unit) {
        Log.d(TAG, "deleteWiD executed")

        wiDDataSource.deleteWiD(
            email = user.value?.email ?: "",
            onWiDDeleted = { wiDDeleted: Boolean ->
                if (wiDDeleted) {
                    val currentExp = user.value?.currentExp ?: 0
                    val usedExp = existingWiD.value.duration.seconds.toInt()

                    val currentTotalExp = user.value?.totalExp ?: 0
                    val prevTotalExp = currentTotalExp - usedExp
                    val currentWiDTotalExp = user.value?.totalExp ?: 0
                    val prevWiDTotalExp = currentWiDTotalExp - usedExp

                    val title = existingWiD.value.title
                    val titleCountMap = user.value?.titleCountMap?.toMutableMap() ?: mutableMapOf()
                    val currentCount = titleCountMap[title] ?: 0
                    titleCountMap[title] = currentCount - 1

                    val usedDuration = existingWiD.value.duration
                    val titleDurationMap = user.value?.titleDurationMap?.toMutableMap() ?: mutableMapOf()
                    val currentDuration = titleDurationMap[title] ?: Duration.ZERO
                    titleDurationMap[title] = currentDuration - usedDuration

                    if (currentExp - usedExp < 0) { // 레벨 다운
                        val currentLevel = user.value?.level ?: 1
                        val currentLevelAsString = currentLevel.toString()
                        val prevLevel = currentLevel - 1

                        val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                        levelUpHistoryMap.remove(currentLevelAsString)

                        val prevLevelRequiredExp = levelToRequiredExpMap[prevLevel] ?: 0
                        val prevCurrentExp = prevLevelRequiredExp + currentExp - usedExp

                        userDataSource.deleteWiDWithLevelDown(
                            newLevel = prevLevel,
                            newLevelUpHistoryMap = levelUpHistoryMap,
                            newCurrentExp = prevCurrentExp,
                            newTotalExp = prevTotalExp,
                            newWiDTotalExp = prevWiDTotalExp,
                            newTitleCountMap = titleCountMap,
                            newTitleDurationMap = titleDurationMap
                        )
                    } else {
                        val prevCurrentExp = currentExp - usedExp

                        userDataSource.deleteWiD(
                            newCurrentExp = prevCurrentExp,
                            newTotalExp = prevTotalExp,
                            newWiDTotalExp = prevWiDTotalExp,
                            newTitleCountMap = titleCountMap,
                            newTitleDurationMap = titleDurationMap
                        )
                    }

                    /**
                     * 위드 삭제 후 콜백 반환이 아니라,
                     * 위드 삭제 후 -> 유저 문서 갱신 후 콜백 반환 해야 하지 않을까?
                     */
                    onWiDDeleted(true)
                } else {
                    onWiDDeleted(false)
                }
            }
        )
    }
}