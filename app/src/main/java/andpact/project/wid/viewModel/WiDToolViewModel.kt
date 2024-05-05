package andpact.project.wid.viewModel

import andpact.project.wid.util.PlayerState
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// @Inject 사용 안 할거면 @HiltViewModel 안 붙혀도 됨.
class WiDToolViewModel: ViewModel() {
    private val TAG = "WiDToolViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val pages = listOf("스톱 워치", "타이머", "WiD 리스트")

    private val _stopwatchState = mutableStateOf(PlayerState.Stopped)
    val stopwatchState: State<PlayerState> = _stopwatchState

    private val _timerState = mutableStateOf(PlayerState.Stopped)
    val timerState: State<PlayerState> = _timerState

    private val _hideWiDToolViewBar = mutableStateOf(false)
    val hideWiDToolViewBar: State<Boolean> = _hideWiDToolViewBar

    fun setStopwatchState(newState: PlayerState) {
        _stopwatchState.value = newState
    }

    fun setTimerState(newState: PlayerState) {
        _timerState.value = newState
    }

    fun setHideWiDToolViewBar(hide: Boolean) {
        _hideWiDToolViewBar.value = hide
    }
}