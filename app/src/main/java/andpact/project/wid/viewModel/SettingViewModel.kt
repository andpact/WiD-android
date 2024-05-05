package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.User
import andpact.project.wid.repository.UserRepository
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDateSource: UserDataSource
) : ViewModel() {
    private val TAG = "SettingViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val firebaseUser: State<FirebaseUser?> = userDateSource.firebaseUser
    val user: State<User?> = userDateSource.user

    private val _showSignOutDialog = mutableStateOf(false)
    private val _showDeleteUserDialog = mutableStateOf(false)
    val showSignOutDialog: State<Boolean> = _showSignOutDialog
    val showDeleteUserDialog: State<Boolean> = _showDeleteUserDialog

    fun signOut() {
        Log.d(TAG, "signOut executed")

        userDateSource.signOut()
    }

    fun deleteUser(onUserDeleted: (Boolean) -> Unit) {
        Log.d(TAG, "deleteUser executed")

        userDateSource.deleteUser(
            onUserDeleted = { userDeleted: Boolean ->
                onUserDeleted(userDeleted)
            }
        )
    }

    fun setShowSignOutDialog(show: Boolean) {
        Log.d(TAG, "setShowSignOutDialog executed")

        _showSignOutDialog.value = show
    }

    fun setShowDeleteUserDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteUserDialog executed")

        _showDeleteUserDialog.value = show
    }
}