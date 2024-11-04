package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.User
import andpact.project.wid.util.getRandomNickname
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyAccountViewModel @Inject constructor(
    private val userDataSource: UserDataSource
) : ViewModel() {
    private val TAG = "MyAccountViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    // user 인스턴스 자체가 갱신되면서 화면의 데이터가 갱신되도록 함.
    val firebaseUser: State<FirebaseUser?> = userDataSource.firebaseUser
    val user: State<User?> = userDataSource.user

    // 이메일(계정 삭제 대화상자 속)
    private val _emailForDialog = mutableStateOf(userDataSource.firebaseUser.value?.email ?: "")
    val emailForDialog: State<String> = _emailForDialog

    // 닉네임(대화상자)
    private val _displayNameForDialog = mutableStateOf(userDataSource.firebaseUser.value?.displayName ?: getRandomNickname())
    val displayNameForDialog: State<String> = _displayNameForDialog
    private val _showDisplayNameDialog = mutableStateOf(false)
    val showDisplayNameDialog: State<Boolean> = _showDisplayNameDialog

    // 계정
    private val _showLevelDateMapDialog = mutableStateOf(false)
    val showLevelDateMapDialog: State<Boolean> = _showLevelDateMapDialog
    private val _showSignOutDialog = mutableStateOf(false)
    val showSignOutDialog: State<Boolean> = _showSignOutDialog
    private val _showDeleteUserDialog = mutableStateOf(false)
    val showDeleteUserDialog: State<Boolean> = _showDeleteUserDialog

    fun setEmailForDialog(newEmailForDialog: String) {
        Log.d(TAG, "setEmailForDialog executed")

        _emailForDialog.value = newEmailForDialog
    }

    fun setDisplayNameForDialog(newDisplayNameForDialog: String) {
        Log.d(TAG, "setDisplayNameForDialog executed")

        _displayNameForDialog.value = newDisplayNameForDialog
    }

    fun updateDisplayName(newDisplayName: String) {
        Log.d(TAG, "updateDisplayName executed")

        /** 파이어베이스 유저 갱신하는 코드 작성. */
    }

    fun setShowDisplayNameDialog(show: Boolean) {
        Log.d(TAG, "setShowDisplayNameDialog executed")

        _showDisplayNameDialog.value = show
    }

    fun setShowLevelDateMapDialog(show: Boolean) {
        Log.d(TAG, "setShowLevelDateMapDialog executed")

        _showLevelDateMapDialog.value = show
    }

    fun setShowSignOutDialog(show: Boolean) {
        Log.d(TAG, "setShowSignOutDialog executed")

        _showSignOutDialog.value = show
    }

    fun signOut() {
        Log.d(TAG, "signOut executed")

        userDataSource.signOut()
    }

    fun setShowDeleteUserDialog(show: Boolean) {
        Log.d(TAG, "setShowDeleteUserDialog executed")

        _showDeleteUserDialog.value = show
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