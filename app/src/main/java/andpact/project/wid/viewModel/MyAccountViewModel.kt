package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.City
import andpact.project.wid.model.User
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MyAccountViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "MyAccountViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val CITY = userDataSource.CITY

    // user 인스턴스 자체가 갱신되면서 화면의 데이터가 갱신되도록 함.
//    val firebaseUser: State<FirebaseUser?> = userDataSource.firebaseUser
    val user: State<User?> = userDataSource.user

    // 이메일(계정 삭제 대화상자 속)
    private val _emailForDialog = mutableStateOf(userDataSource.firebaseUser.value?.email ?: "")
    val emailForDialog: State<String> = _emailForDialog

    // 닉네임(대화상자)
//    private val _displayNameForDialog = mutableStateOf(userDataSource.firebaseUser.value?.displayName ?: "tmp nickname")
//    val displayNameForDialog: State<String> = _displayNameForDialog
//    private val _showDisplayNameDialog = mutableStateOf(false)
//    val showDisplayNameDialog: State<Boolean> = _showDisplayNameDialog

    // 계정
    private val _showSignOutDialog = mutableStateOf(false)
    val showSignOutDialog: State<Boolean> = _showSignOutDialog
    private val _showDeleteUserDialog = mutableStateOf(false)
    val showDeleteUserDialog: State<Boolean> = _showDeleteUserDialog

    fun setEmailForDialog(newEmailForDialog: String) {
        Log.d(TAG, "setEmailForDialog executed")

        _emailForDialog.value = newEmailForDialog
    }

//    fun setDisplayNameForDialog(newDisplayNameForDialog: String) {
//        Log.d(TAG, "setDisplayNameForDialog executed")
//
//        _displayNameForDialog.value = newDisplayNameForDialog
//    }

//    fun updateDisplayName(newDisplayName: String) {
//        Log.d(TAG, "updateDisplayName executed")
//
//         TODO: 파이어베이스 유저 갱신하는 코드 작성
//    }

//    fun setShowDisplayNameDialog(show: Boolean) {
//        Log.d(TAG, "setShowDisplayNameDialog executed")
//
//        _showDisplayNameDialog.value = show
//    }

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

        val currentUser = user.value ?: return

        userDataSource.deleteUser(
            email = currentUser.email,
            onUserDeleted = { userDeleted: Boolean ->
                onUserDeleted(userDeleted)
            }
        )
    }

    @Composable
    fun getDateString(date: LocalDate): AnnotatedString {
        Log.d(TAG, "getDateString executed")

        return wiDDataSource.getDateString(date = date)
    }

//    fun getRandomNickname(): String {
//        Log.d(TAG, "getRandomNickname executed")
//
//        return tmpNicknameList[Random.nextInt(tmpNicknameList.size)]
//    }
//
//    val tmpNicknameList: List<String> = listOf(
//        "부지런한 고양이",
//        "활기찬 햇살",
//        "즐거운 토끼",
//        "웃음 가득한 나비",
//        "활동적인 다람쥐",
//        "상큼한 레몬",
//        "밝은 별빛",
//        "행복한 새벽",
//        "긍정적인 물결",
//        "힘찬 파랑새",
//        "활력 넘치는 해바라기"
//    )
}