package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.*
import andpact.project.wid.ui.theme.chivoMonoBlackItalic
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "TimerViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val LEVEL = userDataSource.LEVEL
    val CURRENT_EXP = userDataSource.CURRENT_EXP
    val WID_TOTAL_EXP = userDataSource.WID_TOTAL_EXP

    private val CITY = userDataSource.CITY

    val WID_LIST_LIMIT_PER_DAY = wiDDataSource.WID_LIST_LIMIT_PER_DAY

    val user: State<User?> = userDataSource.user
    val now: State<LocalDateTime> = wiDDataSource.now

    val currentWiD: State<WiD> = wiDDataSource.currentWiD

    val playerState: State<PlayerState> = wiDDataSource.playerState
    val remainingTime: State<Duration> = wiDDataSource.remainingTime
    val selectedTime: State<Duration> = wiDDataSource.selectedTime
    private val _timerViewBarVisible = mutableStateOf(true)
    val timerViewBarVisible: State<Boolean> = _timerViewBarVisible

    val wiDList: State<List<WiD>> = derivedStateOf { updateWiDList() }

    private fun updateWiDList(): List<WiD> {
        Log.d(TAG, "updateWiDList executed")

        val currentToday = now.value.toLocalDate()

        return wiDDataSource.yearDateWiDListMap.value
            .getOrDefault(Year.of(currentToday.year), emptyMap())
            .getOrDefault(currentToday, emptyList())
    }

//    fun setTitle(newTitle: Title) {
//        Log.d(TAG, "setTitle executed")
//
//        wiDDataSource.setCurrentWiDTitle(newTitle)
//    }

    fun setTimerTime(newSelectedTime: Duration) {
        Log.d(TAG, "setTimerTime executed")

        wiDDataSource.setTimerTime(newSelectedTime)
    }

    fun setTimerViewBarVisible(timerViewBarVisible: Boolean) {
        Log.d(TAG, "setTimerViewBarVisible executed")

        _timerViewBarVisible.value = timerViewBarVisible
    }

    fun startTimer(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) {
        Log.d(TAG, "startTimer executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.startTimer(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            },
            onTimerAutoStopped = { newExp: Int ->
                // 현재 상태
                val currentLevel = currentUser.level
                val currentExp = currentUser.currentExp
                val currentRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = currentUser.wiDTotalExp
                val newWiDTotalExp = wiDTotalExp + newExp

                // 업데이트할 필드
                val updatedFields = mutableMapOf<String, Any>()

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨 업데이트
                    val newLevel = currentLevel + 1
                    updatedFields[LEVEL] = newLevel

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentRequiredExp
                    updatedFields[CURRENT_EXP] = newCurrentExp
                } else {
                    // 레벨 업이 아닌 경우 현재 경험치만 갱신
                    updatedFields[CURRENT_EXP] = currentExp + newExp
                }

                updatedFields[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
                updatedFields[CITY] = currentUser.city.name // 도시 할당

                // UserDataSource를 통해 문서 갱신
                userDataSource.setUserDocument(
                    email = currentUser.email,
                    updatedUserDocument = updatedFields
                )
            }
        )
    }

    fun pauseTimer(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) {
        Log.d(TAG, "pauseTimer executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.pauseTimer(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            },
            onTimerPaused = { newExp: Int ->
                // 현재 상태
                val currentLevel = currentUser.level
                val currentExp = currentUser.currentExp
                val currentRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = currentUser.wiDTotalExp
                val newWiDTotalExp = wiDTotalExp + newExp

                // 업데이트할 필드
                val updatedFields = mutableMapOf<String, Any>()

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨 업데이트
                    val newLevel = currentLevel + 1
                    updatedFields[LEVEL] = newLevel

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentRequiredExp
                    updatedFields[CURRENT_EXP] = newCurrentExp
                } else {
                    // 레벨 업이 아닌 경우 현재 경험치만 갱신
                    updatedFields[CURRENT_EXP] = currentExp + newExp
                }

                // 총 WiD 경험치 업데이트
                updatedFields[WID_TOTAL_EXP] = newWiDTotalExp
                updatedFields[CITY] = currentUser.city.name // 도시 할당

                // UserDataSource를 통해 문서 갱신
                userDataSource.setUserDocument(
                    email = currentUser.email,
                    updatedUserDocument = updatedFields
                )
            }
        )
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        wiDDataSource.stopTimer()
    }


    fun getTimerDurationString(duration: Duration): AnnotatedString {
        Log.d(TAG, "getTimerDurationString executed")

        val hours = duration.toHours()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        val hoursText = hours.toString()
        val minutesText = minutes.toString().padStart(2, '0')
        val secondsText = seconds.toString().padStart(2, '0')

        return buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 60.sp, fontFamily = chivoMonoBlackItalic)) {
                append("$hoursText:$minutesText:$secondsText")
            }
        }
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

    fun getTimeString(time: LocalTime): String { // 'HH:mm:ss'
        Log.d(TAG, "getTimeString executed")

        return wiDDataSource.getTimeString(time = time)
    }

    @Composable
    fun getDateTimeString(dateTime: LocalDateTime): AnnotatedString {
//        Log.d(TAG, "getDateTimeString executed")

        return wiDDataSource.getDateTimeString(dateTime = dateTime)
    }
}