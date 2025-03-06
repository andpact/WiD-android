package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val TAG = "AuthenticationViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val _email = mutableStateOf("")
    val email: State<String> = _email
    private val _emailModified = mutableStateOf(false)
    val emailModified: State<Boolean> = _emailModified
    private val _emailValid = mutableStateOf(false)
    val emailValid: State<Boolean> = _emailValid
    private val _authenticationLinkSentButtonClicked = mutableStateOf(false)
    val authenticationLinkSentButtonClicked: State<Boolean> = _authenticationLinkSentButtonClicked
    private val _authenticationLinkSent = mutableStateOf(false)
    val authenticationLinkSent: State<Boolean> = _authenticationLinkSent

//    fun setEmail(newEmail: String) {
//        Log.d(TAG, "setEmail executed")
//
//        _email.value = newEmail
//    }
//
//    fun setEmailModified(edited: Boolean) {
//        Log.d(TAG, "setEmailModified executed")
//
//        _emailModified.value = edited
//    }
//
//    fun setEmailValid(signUpEmail: String) {
//        Log.d(TAG, "setEmailValid executed")
//
////        val emailRegex = Regex("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
//        val emailRegex = Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}\$")
//        _emailValid.value = signUpEmail.matches(emailRegex)
//    }
//
//    fun setAuthenticationLinkSentButtonClicked(clicked: Boolean) {
//        Log.d(TAG, "setAuthenticationLinkSentButtonClicked executed : $clicked")
//
//        _authenticationLinkSentButtonClicked.value = clicked
//    }
//
//    private fun setAuthenticationLinkSent(sent: Boolean) {
//        Log.d(TAG, "setAuthenticationLinkSent executed : $sent")
//
//        _authenticationLinkSent.value = sent
//    }
//
//    fun sendAuthenticationLinkToEmail(email: String) {
//        Log.d(TAG, "sendAuthenticationLinkToEmail executed")
//
//        setEmailToSharedPreferences(email = email)
//
//        userDataSource.sendAuthenticationLinkToEmail(email) { authenticationLinkSent ->
//            setAuthenticationLinkSent(authenticationLinkSent)
//        }
//
////        setAuthenticationLinkSent(sent = true)
//    }
//
//    private fun setEmailToSharedPreferences(email: String) {
//        val key = "Email"
//
//        Log.d(TAG, "setEmailToSharedPreferences executed : $email")
//
//        val editor = sharedPreferences.edit()
//        editor.putString(key, email)
//        editor.apply()
//    }
}