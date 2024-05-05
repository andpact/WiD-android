package andpact.project.wid.dataSource

import andpact.project.wid.model.User
import andpact.project.wid.repository.UserRepository
import andpact.project.wid.repository.WiDRepository
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate
import javax.inject.Inject

// Firebase User, User Document 외에는 정보를 모르도록
class UserDataSource @Inject constructor(
    private val userRepository: UserRepository
){
    private val TAG = "UserDataSource"

    init {
        Log.d(TAG, "created")
    }

    fun onCleared() {
        Log.d(TAG, "cleared")
    }


    // 최초 null 상태로 시작한 후, 동적 링크를 감지하거나, 기존 로그인 상태가 있음을 확인하면 할당해줌.
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    val firebaseUser: State<FirebaseUser?> = _firebaseUser
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    fun setFirebaseUserAndUser() {
        Log.d(TAG, "setFirebaseUserAndUser executed")

        _firebaseUser.value = userRepository.getFirebaseUser()

        userRepository.getUser(
            email = _firebaseUser.value?.email ?: "",
            onUserFetched = { userFetched ->
                _user.value = userFetched
            }
        )

        Log.d(TAG, "firebaseUser : $firebaseUser")
        Log.d(TAG, "user : $user")
    }

    fun hasFirebaseUser(): Boolean {
        Log.d(TAG, "hasFirebaseUser executed")

        return userRepository.getFirebaseUser() != null
    }

    fun verifyAuthenticationLink(email: String, dynamicLink: String?, onAuthenticationLinkVerified: (Boolean) -> Unit) {
        Log.d(TAG, "verifyAuthenticationLink executed")

        userRepository.verifyAuthenticationLink(email, dynamicLink) { authenticationLinkVerified: Boolean ->
            onAuthenticationLinkVerified(authenticationLinkVerified)
        }
    }

    fun sendAuthenticationLinkToEmail(email: String, onAuthenticationLinkSent: (Boolean) -> Unit) {
        Log.d(TAG, "sendAuthenticationLinkToEmail executed")

        userRepository.sendAuthenticationLinkToEmail(email) { authenticationLinkSent: Boolean ->
            onAuthenticationLinkSent(authenticationLinkSent)
        }
    }

//    fun sendSignInLinkToEmail(email: String, onSignInLinkSent: (Boolean) -> Unit) {
//        Log.d(TAG, "sendSignInLinkToEmail executed")
//
//        userRepository.sendSignInLinkToEmail(email) { signInLinkSent ->
//            onSignInLinkSent(signInLinkSent)
//        }
//    }

    fun signOut() {
        Log.d(TAG, "signOut executed")

        userRepository.signOut()
    }

    fun deleteUser(onUserDeleted: (Boolean) -> Unit) {
        Log.d(TAG, "deleteUser executed")

        userRepository.deleteUser(
            email = _firebaseUser.value?.email ?: "",
            onUserDeleted = { userDeleted: Boolean ->
                onUserDeleted(userDeleted)
            }
        )
    }
}