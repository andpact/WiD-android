package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
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

    val now: State<LocalDateTime> = wiDDataSource.now

    val currentWiD: State<WiD> = wiDDataSource.currentWiD

    val playerState = wiDDataSource.playerState

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