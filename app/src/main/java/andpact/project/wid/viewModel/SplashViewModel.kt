package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 인터넷 연결 확인 메서드 작성 해야 함.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val TAG = "SplashViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    fun getUserDocument() {
        Log.d(TAG, "getUserDocument executed")

        userDataSource.getUserDocument()
    }

    // 한 번 실행하고 말거니까, 필드로 굳이 빼지말자, 스플래쉬 뷰에서는 로그인 기록이 있는 지만 확인하면 되니까 FirebaseUser객체를 반환할 필요 없다.
    fun getFirebaseUser(): Boolean {
        Log.d(TAG, "hasFirebaseUser executed")

        return userDataSource.getFirebaseUser()
    }

//    fun verifyAuthenticationLink(
//        dynamicLink: String?,
//        onAuthenticationLinkVerified: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "verifyAuthenticationLink executed")
//
//        val email = getEmailFromSharedPreferences()
//
//        userDataSource.verifyAuthenticationLink(
//            email = email,
//            dynamicLink = dynamicLink,
//            onAuthenticationLinkVerified = { authenticationLinkVerified: Boolean ->
//                onAuthenticationLinkVerified(authenticationLinkVerified)
//            }
//        )
//    }
//
//    private fun getEmailFromSharedPreferences(): String {
//        val key = "Email"
//        val email = sharedPreferences.getString(key, "") ?: ""
//
//        Log.d(TAG, "getEmailFromSharedPreferences executed : $email")
//
//        return email
//    }

//    fun getCurrentUser() : FirebaseUser? {
//        Log.d(TAG, "isCurrentSignedIn executed")
//
//        return userRepository.getCurrentUser()
//    }

//    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
//
//    private val _isInternetConnected = mutableStateOf(false)
//    val isInternetConnected: State<Boolean> = _isInternetConnected
//
//
////    suspend fun isInternetConnected(): Boolean {
////        return withContext(Dispatchers.IO) {
////            val networkInfo = connectivityManager.activeNetworkInfo
////            networkInfo != null && networkInfo.isConnected
////        }
////    }
//
//    private fun checkInternetConnection() {
//        _isInternetConnected.value = connectivityManager.activeNetworkInfo?.isConnected ?: false
//    }
//
//    suspend fun checkCurrentUser(): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                val user = auth.currentUser
//                user != null
//            } catch (e: Exception) {
//                false
//            }
//        }
//    }
}