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

        wiDDataSource.startStopwatch()
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        wiDDataSource.pauseStopwatch(
            email = user.value?.email ?: "",
            onStopwatchPaused = { newExp: Int ->
                // 레벨
                val currentLevel = user.value?.level ?: 1
                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentLevelRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                if (currentLevelRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelDateMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentLevelRequiredExp

                    userDataSource.pauseStopwatchWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp
                    )
                } else { // 레벨업 아님.
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseStopwatch(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp
                    )
                }
            }
        )
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        wiDDataSource.stopStopwatch()
    }

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        wiDDataSource.startTimer(
            email = user.value?.email ?: "",
            onTimerAutoStopped = { newExp: Int ->
                // 레벨
                val currentLevel = user.value?.level ?: 1
                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelDateMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.autoStopTimerWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp
                    )
                } else {
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.autoStopTimer(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp
                    )
                }
            },
        )
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        wiDDataSource.pauseTimer(
            email = user.value?.email ?: "",
            onTimerPaused = { newExp: Int ->
                // 레벨
                val currentLevel = user.value?.level ?: 1

                // 경험치
                val currentExp = user.value?.currentExp ?: 0
                val currentRequiredExp = userDataSource.levelRequiredExpMap[currentLevel] ?: 0
                val wiDTotalExp = user.value?.wiDTotalExp ?: 0
                val newWiDTotalExp = wiDTotalExp + newExp

                if (currentRequiredExp <= currentExp + newExp) { // 레벨 업
                    // 레벨
                    val newLevel = currentLevel + 1
                    val newLevelAsString = newLevel.toString()
                    val levelDateMap = user.value?.levelDateMap?.toMutableMap() ?: mutableMapOf()
                    levelDateMap[newLevelAsString] = LocalDate.now() // 실행되는 순간 날짜를 사용함

                    // 경험치
                    val newCurrentExp = currentExp + newExp - currentRequiredExp

                    userDataSource.pauseTimerWithLevelUp(
                        newLevel = newLevel,
                        newLevelUpHistoryMap = levelDateMap,
                        newCurrentExp = newCurrentExp, // 현재 경험치 초기화
                        newWiDTotalExp = newWiDTotalExp
                    )
                } else {
                    // 경험치
                    val newCurrentExp = currentExp + newExp

                    userDataSource.pauseTimer(
                        newCurrentExp = newCurrentExp,
                        newWiDTotalExp = newWiDTotalExp
                    )
                }
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