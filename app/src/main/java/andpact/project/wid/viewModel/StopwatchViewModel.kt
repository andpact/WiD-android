package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.StopwatchDataSource
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
    private val stopwatchDataSource: StopwatchDataSource,
    private val userDataSource: UserDataSource
) : ViewModel() {
    private val TAG = "StopwatchViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val email: String = userDataSource.firebaseUser.value?.email ?: ""

    // 제목
    val title: State<String> = stopwatchDataSource.title

    private val _titleColorMap = mutableStateOf(userDataSource.user.value?.titleColorMap ?: defaultTitleColorMapWithColors)
    val titleColorMap: State<Map<String, Color>> = _titleColorMap

    // 소요 시간
    val duration: State<Duration> = stopwatchDataSource.duration

    // 스톱 워치
    val stopwatchState: State<PlayerState> = stopwatchDataSource.stopwatchState

    private val _hideStopwatchViewBar = mutableStateOf(false)
    val hideStopwatchViewBar: State<Boolean> = _hideStopwatchViewBar

    fun setTitle(newTitle: String) {
        Log.d(TAG, "setTitle executed")

        stopwatchDataSource.setTitle(newTitle)
    }

    fun setHideStopwatchViewBar(hideStopwatchViewBar: Boolean) {
        Log.d(TAG, "setHideStopwatchViewBar executed")

        _hideStopwatchViewBar.value = hideStopwatchViewBar
    }

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

//    fun setTitleColorMap() {
//        stopwatchDataSource.setTitleColorMap(map = titleColorMap.value)
//    }
}