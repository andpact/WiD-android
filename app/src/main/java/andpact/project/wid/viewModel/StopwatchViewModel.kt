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
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
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

    fun startStopwatch(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) {
        Log.d(TAG, "startStopwatch executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.startStopwatch(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
            wiDMaxLimit = currentUser.wiDMaxLimit,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            },
            onStopwatchAutoPaused = { newExp: Int ->
                val currentLevel = currentUser.level
                val currentExp = currentUser.currentExp
                val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = currentUser.wiDTotalExp
                val newWiDTotalExp = wiDTotalExp + newExp

                // 업데이트할 필드
                val updatedFields = mutableMapOf<String, Any>()

                if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨 업데이트
                    val newLevel = currentLevel + 1
                    updatedFields[LEVEL] = newLevel

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
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

    fun pauseStopwatch(onResult: (snackbarActionResult: SnackbarActionResult) -> Unit) {
        Log.d(TAG, "pauseStopwatch executed")

        val currentUser = user.value
        if (currentUser == null) {
            onResult(SnackbarActionResult.FAIL_CLIENT_ERROR) // 클라이언트 에러 콜백 호출
            return
        }

        wiDDataSource.pauseStopwatch(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
            onResult = { snackbarActionResult: SnackbarActionResult ->
                onResult(snackbarActionResult)
            },
            onStopwatchPaused = { newExp: Int ->
                val currentLevel = currentUser.level
                val currentExp = currentUser.currentExp
                val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = currentUser.wiDTotalExp
                val newWiDTotalExp = wiDTotalExp + newExp

                // 업데이트할 필드
                val updatedFields = mutableMapOf<String, Any>()

                if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨 업데이트
                    val newLevel = currentLevel + 1
                    updatedFields[LEVEL] = newLevel

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
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

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        wiDDataSource.stopStopwatch()
    }

    fun getStopwatchDurationString(duration: Duration): AnnotatedString {
        Log.d(TAG, "getStopwatchDurationString executed")

        val hours = duration.toHours()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        val hoursText = hours.toString()
        val minutesText = if (0 < hours) {
            minutes.toString().padStart(2, '0')
        } else {
            minutes.toString().padStart(1, '0')
        }
        val secondsText = if (0 < minutes || 0 < hours) {
            seconds.toString().padStart(2, '0')
        } else {
            seconds.toString().padStart(1, '0')
        }

        return buildAnnotatedString {
            withStyle(style = ParagraphStyle(lineHeight = 80.sp)) {
                if (0 < hours) {
                    withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                        append(hoursText + "\n")
                    }
                }

                if (0 < minutes || 0 < hours) {
                    withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                        append(minutesText + "\n")
                    }
                }

                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                    append(secondsText + "\n")
                }
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