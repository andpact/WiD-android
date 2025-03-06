package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.PlayerState
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import javax.inject.Inject

/**
 * 값이 변함에 따라 ui를 갱신 해야 하는 State 변수는 직접 Setter 메서드를 선언하고,
 * 값이 변해도 표시할 필요 없는 일반 변수는 get(), set()를 사용함.
 * State 변수에 값을 할당할 때는 (_변수)를 사용해야 함.
 *
 * MutableLiveData는 Composable 밖(액티비티?)에서도 사용 가능함.
 * mutableState는 Composable 안에서 만 사용 가능 한듯.
 */
@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "StopwatchViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val LEVEL = userDataSource.LEVEL
    private val CURRENT_EXP = userDataSource.CURRENT_EXP
    private val WID_TOTAL_EXP = userDataSource.WID_TOTAL_EXP

    private val CITY = userDataSource.CITY

    val WID_LIST_LIMIT_PER_DAY = wiDDataSource.WID_LIST_LIMIT_PER_DAY

    val user: State<User?> = userDataSource.user
    val now: State<LocalDateTime> = wiDDataSource.now

    val currentWiD: State<WiD> = wiDDataSource.currentWiD

    val playerState: State<PlayerState> = wiDDataSource.playerState
    val totalDuration: State<Duration> = wiDDataSource.totalDuration
    private val _stopwatchViewBarVisible = mutableStateOf(true)
    val stopwatchViewBarVisible: State<Boolean> = _stopwatchViewBarVisible

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }

    // TODO: 플레이어 동작 중 자정 넘어가면 그 날짜의 기록에 카운트가 되나?
    private fun updateWiDList(): List<WiD> {
        Log.d(TAG, "updateWiDList executed")

        val currentToday = now.value.toLocalDate()

        return wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentToday.year), emptyMap())
            .getOrDefault(currentToday, emptyList())
    }

    fun setStopwatchViewBarVisible(stopwatchViewBarVisible: Boolean) {
        Log.d(TAG, "setStopwatchViewBarVisible executed")

        _stopwatchViewBarVisible.value = stopwatchViewBarVisible
    }

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        val currentUser = user.value ?: return

        wiDDataSource.startStopwatch(
            wiDMaxLimit = currentUser.wiDMaxLimit,
            onStopwatchAutoPaused = { stopwatchAutoPaused: Boolean ->
                if (stopwatchAutoPaused) {
                    pauseStopwatch()
                    stopStopwatch()
                }
            }
        )
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        val currentUser = user.value ?: return
        val wiDMinLimit = currentUser.wiDMinLimit
        val currentWiD = currentWiD.value
        if (currentWiD.duration < wiDMinLimit) return // 최소 시간 제한

        val newExp = currentWiD.exp // 플레이어는 무조건
        val currentLevel = currentUser.level
        val currentExp = currentUser.currentExp
        val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
        val wiDTotalExp = currentUser.wiDTotalExp
        val newWiDTotalExp = wiDTotalExp + newExp

        val updatedUserDocument = mutableMapOf<String, Any>()

        if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
            // 레벨 업데이트
            val newLevel = currentLevel + 1
            updatedUserDocument[LEVEL] = newLevel

            // 경험치 갱신
            val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
            updatedUserDocument[CURRENT_EXP] = newCurrentExp
        } else {
            // 레벨 업이 아닌 경우 현재 경험치만 갱신
            updatedUserDocument[CURRENT_EXP] = currentExp + newExp
        }

        updatedUserDocument[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
        updatedUserDocument[CITY] = currentUser.city.name // 도시 할당

        wiDDataSource.pauseStopwatch(
            email = currentUser.email,
            currentWiD = currentWiD,
            updatedUserDocument = updatedUserDocument,
            onResult = { success ->
                if (success) {
                    userDataSource.updateUser(updatedUserDocument = updatedUserDocument)
                }
            }
        )
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        wiDDataSource.stopStopwatch()
    }

    fun getDurationString(duration: Duration): String { // "H시간 m분 s초"
        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }

    fun getDurationTimeString(duration: Duration): String { // 'HH:mm:ss'
        Log.d(TAG, "getDurationTimeString executed")

        return wiDDataSource.getDurationTimeString(duration = duration)
    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
//        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(
            currentDateTime = now.value,
            dateTime = dateTime
        )
    }
}