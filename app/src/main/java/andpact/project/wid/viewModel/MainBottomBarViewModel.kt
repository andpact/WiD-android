package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel // <- 생성자 주입 할 때 명시해야함.
class MainBottomBarViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "MainBottomBarViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val LEVEL = userDataSource.LEVEL
    val LEVEL_DATE_MAP = userDataSource.LEVEL_DATE_MAP
    val CURRENT_EXP = userDataSource.CURRENT_EXP
    val WID_TOTAL_EXP = userDataSource.WID_TOTAL_EXP

    private val CITY = userDataSource.CITY

    val today: State<LocalDate> = wiDDataSource.today

    val user: State<User?> = userDataSource.user

    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD
    val secondCurrentWiD: State<WiD> = wiDDataSource.secondCurrentWiD

    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    val totalDuration: State<Duration> = wiDDataSource.totalDuration
    val remainingTime: State<Duration> = wiDDataSource.remainingTime

    val destinationList = listOf(
        MainViewDestinations.HomeViewDestination,
        MainViewDestinations.WiDListViewDestination,
        MainViewDestinations.MyPageViewDestination
    )

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.startStopwatch(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
            wiDMaxLimit = currentUser.wiDMaxLimit,
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
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = currentUser.levelDateMap.toMutableMap()
                    levelDateMap[newLevelAsString] = today.value

                    updatedFields[LEVEL] = newLevel
                    updatedFields[LEVEL_DATE_MAP] = levelDateMap.mapValues { it.value.toString() }

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
                    updatedFields[CURRENT_EXP] = newCurrentExp
                } else {
                    // 레벨 업이 아닌 경우 현재 경험치만 갱신
                    updatedFields[CURRENT_EXP] = currentExp + newExp
                }

                updatedFields[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
                updatedFields[CITY] = currentUser.city // 도시 할당

                // UserDataSource를 통해 문서 갱신
                userDataSource.setUserDocument(
                    email = currentUser.email,
                    updatedUserDocument = updatedFields
                )
            }
        )
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.pauseStopwatch(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
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
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = currentUser.levelDateMap.toMutableMap()
                    levelDateMap[newLevelAsString] = today.value

                    updatedFields[LEVEL] = newLevel
                    updatedFields[LEVEL_DATE_MAP] = levelDateMap.mapValues { it.value.toString() }

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp
                    updatedFields[CURRENT_EXP] = newCurrentExp
                } else {
                    // 레벨 업이 아닌 경우 현재 경험치만 갱신
                    updatedFields[CURRENT_EXP] = currentExp + newExp
                }

                updatedFields[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
                updatedFields[CITY] = currentUser.city // 도시 할당

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

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.startTimer(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
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
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = currentUser.levelDateMap.toMutableMap()
                    levelDateMap[newLevelAsString] = today.value

                    updatedFields[LEVEL] = newLevel
                    updatedFields[LEVEL_DATE_MAP] = levelDateMap.mapValues { it.value.toString() }

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentRequiredExp
                    updatedFields[CURRENT_EXP] = newCurrentExp
                } else {
                    // 레벨 업이 아닌 경우 현재 경험치만 갱신
                    updatedFields[CURRENT_EXP] = currentExp + newExp
                }

                updatedFields[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
                updatedFields[CITY] = currentUser.city // 도시 할당

                // UserDataSource를 통해 문서 갱신
                userDataSource.setUserDocument(
                    email = currentUser.email,
                    updatedUserDocument = updatedFields
                )
            }
        )
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        val currentUser = user.value ?: return // 잘못된 접근

        wiDDataSource.pauseTimer(
            email = currentUser.email,
            wiDMinLimit = currentUser.wiDMinLimit,
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
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = currentUser.levelDateMap.toMutableMap()
                    levelDateMap[newLevelAsString] = today.value

                    updatedFields[LEVEL] = newLevel
                    updatedFields[LEVEL_DATE_MAP] = levelDateMap.mapValues { it.value.toString() }

                    // 경험치 갱신
                    val newCurrentExp = currentExp + newExp - currentRequiredExp
                    updatedFields[CURRENT_EXP] = newCurrentExp
                } else {
                    // 레벨 업이 아닌 경우 현재 경험치만 갱신
                    updatedFields[CURRENT_EXP] = currentExp + newExp
                }

                updatedFields[WID_TOTAL_EXP] = newWiDTotalExp // 총 WiD 경험치 업데이트
                updatedFields[CITY] = currentUser.city // 도시 할당

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

    fun getDurationString(duration: Duration): String {
//        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }
}