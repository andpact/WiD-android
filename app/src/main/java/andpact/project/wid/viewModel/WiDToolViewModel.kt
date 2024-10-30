package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.CurrentToolState
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// @Inject 사용 안 할거면 @HiltViewModel 안 붙혀도 됨.
@HiltViewModel
class WiDToolViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "WiDToolViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

//    val pages = listOf("스톱 워치", "타이머", "포모도로")
    val pages = listOf("스톱 워치", "타이머")

    val user: State<User?> = userDataSource.user

    val currentTool: State<CurrentTool> = wiDDataSource.currentTool
    val currentToolState: State<CurrentToolState> = wiDDataSource.currentToolState

    private val _wiDToolViewBarVisible = mutableStateOf(true)
    val wiDToolViewBarVisible: State<Boolean> = _wiDToolViewBarVisible

    fun setWiDToolViewBarVisible(visible: Boolean) {
        Log.d(TAG, "setWiDToolViewBarVisible executed")

        _wiDToolViewBarVisible.value = visible
    }
}