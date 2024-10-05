package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.User
import andpact.project.wid.util.defaultTitleNumberStringToTitleDurationMap
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val firebaseUser: State<FirebaseUser?> = userDateSource.firebaseUser
    val user: State<User?> = userDateSource.user

    val numberToDurationMap = defaultTitleNumberStringToTitleDurationMap
}