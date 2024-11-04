package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.User
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyToolViewModel @Inject constructor(
    private val userDataSource: UserDataSource
) : ViewModel() {
    private val TAG = "MyToolViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val user: State<User?> = userDataSource.user
}