package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.User
import andpact.project.wid.util.defaultTitleDurationMap
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
    private val userDateSource: UserDataSource
) : ViewModel() {
    private val TAG = "HomeViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    // 계정
    val firebaseUser: State<FirebaseUser?> = userDateSource.firebaseUser
    val user: State<User?> = userDateSource.user

    // 날짜
    private val _today: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    val today: State<LocalDate> = _today

    // 시간
    private val _now: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    val now: State<LocalTime> = _now

    private var timerJob: Job? = null

    fun startTimer() {
        Log.d(TAG, "startTimer executed")

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000) // 1초마다 갱신
                _now.value = LocalTime.now().withNano(0)
            }
        }
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer executed")

        timerJob?.cancel()
    }
}