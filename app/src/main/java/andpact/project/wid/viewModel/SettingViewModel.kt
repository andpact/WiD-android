package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.User
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDataSource: UserDataSource
) : ViewModel() {
    private val TAG = "SettingViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    // user 인스턴스 자체가 갱신되면서 화면의 데이터가 갱신되도록 함.
    val firebaseUser: State<FirebaseUser?> = userDataSource.firebaseUser
    val user: State<User?> = userDataSource.user

    // 대화 상자에서 사용할 변수
    private val _displayName = mutableStateOf(userDataSource.firebaseUser.value?.displayName ?: "")
    val displayName: State<String> = _displayName
//    private val _statusMessage = mutableStateOf(userDataSource.user.value?.statusMessage ?: "")
//    val statusMessage: State<String> = _statusMessage

    private val _showDisplayNameDialog = mutableStateOf(false)
    val showDisplayNameDialog: State<Boolean> = _showDisplayNameDialog

    private val _showStatusMessageDialog = mutableStateOf(false)
    val showStatusMessageDialog: State<Boolean> = _showStatusMessageDialog

    private val _showSignOutDialog = mutableStateOf(false)
    val showSignOutDialog: State<Boolean> = _showSignOutDialog

    private val _showDeleteUserDialog = mutableStateOf(false)
    val showDeleteUserDialog: State<Boolean> = _showDeleteUserDialog

    fun setDisplayName(newDisplayName: String) {
        Log.d(TAG, "setDisplayName executed")

        _displayName.value = newDisplayName
    }

    fun updateDisplayName(newDisplayName: String) {
        Log.d(TAG, "updateDisplayName executed")


    }

    fun setShowDisplayNameDialog(show: Boolean) {
        Log.d(TAG, "setShowDisplayNameDialog executed")

        _showDisplayNameDialog.value = show
    }

//    fun setStatusMessage(newStatusMessage: String) {
//        Log.d(TAG, "setUserStatusMessage executed")
//
//        _statusMessage.value = newStatusMessage
//    }
//
//    fun updateStatusMessage(newStatusMessage: String) {
//        Log.d(TAG, "updateStatusMessage executed")
//
//        userDataSource.updateStatusMessage(email = firebaseUser.value?.email ?: "", newStatusMessage = newStatusMessage)
//    }

    fun setShowStatusMessageDialog(show: Boolean) {
        Log.d(TAG, "setShowStatusMessageDialog executed")

        _showStatusMessageDialog.value = show
    }

    fun setShowSignOutDialog(show: Boolean) {
        Log.d(TAG, "setShowSignOutDialog executed")

        _showSignOutDialog.value = show
    }

    fun setShowDeleteUserDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteUserDialog executed")

        _showDeleteUserDialog.value = show
    }

    fun signOut() {
        Log.d(TAG, "signOut executed")

        userDataSource.signOut()
    }

    fun deleteUser(onUserDeleted: (Boolean) -> Unit) {
        Log.d(TAG, "deleteUser executed")

        userDataSource.deleteUser(
            email = user.value?.email ?: "",
            onUserDeleted = { userDeleted: Boolean ->
                onUserDeleted(userDeleted)
            }
        )
    }
}