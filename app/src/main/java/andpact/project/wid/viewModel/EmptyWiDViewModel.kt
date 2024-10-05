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
class EmptyWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "EmptyWiDViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val user: State<User?> = userDataSource.user

    val titleColorMap = titleNumberStringToTitleColorMap

    // 기존 WiD가 없기 때문에, existingWiD, updatedWiD 둘이 아니라, emptyWiD 하나만 사용함.
    val emptyWiD: State<WiD> = wiDDataSource.emptyWiD

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

    private val _durationExist = mutableStateOf(false)
    val durationExist: State<Boolean> = _durationExist

    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())

    fun setEmptyWiD(newWiD: WiD) {
        Log.d(TAG, "setEmptyWiD executed")

        wiDDataSource.setEmptyWiD(newWiD = newWiD)

        if (_wiDList.value.isNotEmpty()) {
            checkNewStartOverlap()
            checkNewFinishOverlap()
            checkNewWiDOverlap()
        }

        setDurationExist(Duration.ZERO < newWiD.duration)
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

    private fun checkNewStartOverlap() { // 생성할 WiD의 시작 시간이 겹치는지 확인
        Log.d(TAG, "checkNewStartOverlap executed")

        val today = LocalDate.now()
        val now = LocalTime.now()

        for (existingWiD in _wiDList.value) {
            if (existingWiD.start < emptyWiD.value.start && emptyWiD.value.start < existingWiD.finish) {
                setStartOverlap(overlap = true)
                break
            } else if (emptyWiD.value.date == today && now < emptyWiD.value.start) {
                setStartOverlap(overlap = true)
                break
            } else {
                setStartOverlap(overlap = false)
            }
        }
    }

    private fun checkNewFinishOverlap() { // 생성할 WiD의 종료 시간이 겹치는지 확인
        Log.d(TAG, "checkNewFinishOverlap executed")

        val today = LocalDate.now()
        val now = LocalTime.now()

        for (existingWiD in _wiDList.value) {
            if (existingWiD.start < emptyWiD.value.finish && emptyWiD.value.finish < existingWiD.finish) {
                setFinishOverlap(overlap = true)
                break
            } else if (emptyWiD.value.date == today && now < emptyWiD.value.finish) {
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
            // 등호를 넣어서 부등호를 사용해야 기존의 WiD를 덮고 있는지를 정확히 확인할 수 있다.
            if (emptyWiD.value.start <= existingWiD.start && existingWiD.finish <= emptyWiD.value.finish) {
                setStartOverlap(overlap = true)
                setFinishOverlap(overlap = true)
                break
            }
        }
    }

    fun getWiDListByDate(collectionDate: LocalDate) {
        Log.d(TAG, "getWiDListByDate executed")

        wiDDataSource.getWiDListByDate(
            email = user.value?.email ?: "",
            collectionDate = collectionDate,
            onWiDListFetchedByDate = { wiDList: List<WiD> ->
                _wiDList.value = wiDList
            }
        )

        /** 이 뷰에서 리스너를 사용할 일이 있을까? */
//        wiDDataSource.addSnapshotListenerToWiDCollectionByDate(
//            email = user.value?.email ?: "",
//            collectionDate = collectionDate,
//            onWiDCollectionChanged = { wiDList: List<WiD> ->
//                _wiDList.value = wiDList
//            }
//        )
    }

    fun createWiD(onWiDCreated: (Boolean) -> Unit) {
        Log.d(TAG, "createWiD executed")

        wiDDataSource.createWiD(
            email = user.value?.email ?: "",
            onWiDCreated = { wiDCreated: Boolean ->
                if (wiDCreated) {
                    val currentLevel = user.value?.level ?: 1
                    val currentExp = user.value?.currentExp ?: 0
                    val newExp = emptyWiD.value.duration.seconds.toInt()
                    val currentLevelRequiredExp = levelToRequiredExpMap[currentLevel] ?: 0

                    val totalExp = user.value?.totalExp ?: 0
                    val newTotalExp = totalExp + newExp
                    val wiDTotalExp = user.value?.totalExp ?: 0
                    val newWiDTotalExp = wiDTotalExp + newExp

                    val title = emptyWiD.value.title
                    val titleCountMap = user.value?.titleCountMap?.toMutableMap() ?: mutableMapOf()
                    val currentCount = titleCountMap[title] ?: 0
                    titleCountMap[title] = currentCount + 1

                    val titleDurationMap = user.value?.titleDurationMap?.toMutableMap() ?: mutableMapOf()
                    val currentDuration = titleDurationMap[title] ?: Duration.ZERO
                    titleDurationMap[title] = currentDuration.plus(Duration.ofSeconds(newExp.toLong()))

                    if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                        val newLevel = currentLevel + 1
                        val newLevelAsString = newLevel.toString()

                        val today = LocalDate.now()
                        val levelUpHistoryMap = user.value?.levelUpHistoryMap?.toMutableMap() ?: mutableMapOf()
                        levelUpHistoryMap[newLevelAsString] = today

                        val newCurrentExp = currentExp + newExp - currentLevelRequiredExp

                        userDataSource.createdWiDWithLevelUp(
                            newLevel = newLevel,
                            newLevelUpHistoryMap = levelUpHistoryMap,
                            newCurrentExp = newCurrentExp,
                            newTotalExp = newTotalExp,
                            newWiDTotalExp = newWiDTotalExp,
                            newTitleDurationMap = titleDurationMap,
                        )
                    } else { // 레벨 업 아님
                        val newCurrentExp = currentExp + newExp

                        userDataSource.createdWiD(
                            newCurrentExp = newCurrentExp,
                            newTotalExp = newTotalExp,
                            newWiDTotalExp = newWiDTotalExp,
                            newTitleDurationMap = titleDurationMap
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