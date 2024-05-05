package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.StopwatchDataSource
import andpact.project.wid.dataSource.TimerDataSource
import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.util.PlayerState
import andpact.project.wid.util.defaultTitleColorMapWithColors
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject

@HiltViewModel // <- 생성자 주입 할 때 명시해야함.
class MainBottomBarViewModel @Inject constructor(
    private val stopwatchDataSource: StopwatchDataSource,
    private val timerDataSource: TimerDataSource,
    private val userDataSource: UserDataSource
) : ViewModel() {
    private val TAG = "MainBottomBarViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email = userDataSource.firebaseUser.value?.email ?: ""

    private val _titleColorMap = mutableStateOf(userDataSource.user.value?.titleColorMap ?: defaultTitleColorMapWithColors)
    val titleColorMap: State<Map<String, Color>> = _titleColorMap

    val stopwatchTitle: State<String> = stopwatchDataSource.title
    val stopwatchDuration: State<Duration> = stopwatchDataSource.duration
    val stopwatchState: State<PlayerState> = stopwatchDataSource.stopwatchState

    val timerTitle: State<String> = timerDataSource.title
    val timerRemainingTime: State<Duration> = timerDataSource.remainingTime
    val timerState: State<PlayerState> = timerDataSource.timerState

    fun startStopwatch() {
        Log.d(TAG, "startStopwatch executed")

        stopwatchDataSource.startStopwatch()
    }

    fun pauseStopwatch() {
        Log.d(TAG, "pauseStopwatch executed")

        stopwatchDataSource.pauseStopwatch(email = email)
    }

    fun stopStopwatch() {
        Log.d(TAG, "stopStopwatch executed")

        stopwatchDataSource.stopStopwatch()
    }

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        timerDataSource.startTimer(email = email)
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer executed")

        timerDataSource.pauseTimer(email = email)
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        timerDataSource.stopTimer()
    }
}