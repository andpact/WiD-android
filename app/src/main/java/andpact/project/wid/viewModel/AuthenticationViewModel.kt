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

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val _email = mutableStateOf("")
    val email: State<String> = _email
    private val _isEmailEdited = mutableStateOf(false)
    val isEmailEdited: State<Boolean> = _isEmailEdited
    private val _isEmailValid = mutableStateOf(false)
    val isEmailValid: State<Boolean> = _isEmailValid
    private val _isAuthenticationLinkSentButtonClicked = mutableStateOf(false)
    val isAuthenticationLinkSentButtonClicked: State<Boolean> = _isAuthenticationLinkSentButtonClicked
    private val _isAuthenticationLinkSent = mutableStateOf(false)
    val isAuthenticationLinkSent: State<Boolean> = _isAuthenticationLinkSent

    fun setEmail(newEmail: String) {
        Log.d(TAG, "setEmail executed")

        _email.value = newEmail
    }

    fun setEmailEdited(edited: Boolean) {
        Log.d(TAG, "setSignUpEmailEdited executed")

        _isEmailEdited.value = edited
    }

    fun setEmailValid(signUpEmail: String) {
        Log.d(TAG, "setSignUpEmailValid executed")

//        val emailRegex = Regex("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
        val emailRegex = Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}\$")
        _isEmailValid.value = signUpEmail.matches(emailRegex)
    }

    fun setAuthenticationLinkSentButtonClicked(clicked: Boolean) {
        _isAuthenticationLinkSentButtonClicked.value = clicked
    }

    private fun setAuthenticationLinkSent(sent: Boolean) {
        _isAuthenticationLinkSent.value = sent
    }

    fun sendAuthenticationLinkToEmail(email: String) {
        Log.d(TAG, "sendLinkToEmail executed")

        setEmailToSharedPreferences(email = email)

        userDataSource.sendAuthenticationLinkToEmail(email) { authenticationLinkSent ->
            setAuthenticationLinkSent(authenticationLinkSent)
        }
    }

    private fun setEmailToSharedPreferences(email: String) {
        val key = "Email"

        Log.d(TAG, "setEmailToSharedPreferences executed : $email")

        val editor = sharedPreferences.edit()
        editor.putString(key, email)
        editor.apply()
    }
}