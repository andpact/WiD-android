package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wiDDataSource: WiDDataSource,
    private val userDateSource: UserDataSource
) : ViewModel() {
    private val TAG = "HomeViewModel"
    init {
        Log.d(TAG, "created")

        startLastNewWiDTimer()
    }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

//    val firebaseUser: State<FirebaseUser?> = userDateSource.firebaseUser
    val user: State<User?> = userDateSource.user
    val levelRequiredExpMap = userDateSource.levelRequiredExpMap

    val today: State<LocalDate> = wiDDataSource.today
    val now: State<LocalTime> = wiDDataSource.now

    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD

    val currentToolState = wiDDataSource.currentToolState

    private fun startLastNewWiDTimer() {
        Log.d(TAG, "startLastNewWiDTimer executed")

        wiDDataSource.startLastNewWiDTimer()
    }

    fun getTimeString(time: LocalTime): String {
        Log.d(TAG, "getTimeString executed")
        // 'HH:mm:ss'

        return wiDDataSource.getTimeString(time = time)
    }
}