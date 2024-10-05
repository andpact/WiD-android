package andpact.project.wid.tmp

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.repository.UserRepository
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
//class SignInViewModel @Inject constructor(
////    private val userRepository: UserRepository,
//    private val userDataSource: UserDataSource,
//    private val sharedPreferences: SharedPreferences
//) : ViewModel() {
//    private val TAG = "SignInViewModel"
//
//    init {
//        Log.d(TAG, "created")
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        Log.d(TAG, "cleared")
//    }
//
////    private val userRepository: UserRepository = UserRepository()
////    private val preferencesName = "WiDAuth"
////    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
//
//    private val _signInEmail = mutableStateOf("")
//    private val _isSignInEmailEdited = mutableStateOf(false)
//    private val _isSignInEmailValid = mutableStateOf(false)
//    private val _isSignInEmailLinkSent = mutableStateOf(false)
//    private val _showGoToSignUpDialog = mutableStateOf(false)
//    val signInEmail: State<String> = _signInEmail
//    val isSignInEmailEdited: State<Boolean> = _isSignInEmailEdited
//    val isSignInEmailValid: State<Boolean> = _isSignInEmailValid
//    val isSignInEmailLinkSent: State<Boolean> = _isSignInEmailLinkSent
//    val showGoToSignUpDialog: State<Boolean> = _showGoToSignUpDialog
//
//    fun setSignInEmail(newSignInEmail: String) {
//        Log.d(TAG, "setSignInEmail executed")
//
//        _signInEmail.value = newSignInEmail
//    }
//
//    fun setSignInEmailEdited(edited: Boolean) {
//        Log.d(TAG, "setSignInEmailEdited executed")
//
//        _isSignInEmailEdited.value = edited
//    }
//
//    fun setSignInEmailValid(signInEmail: String) {
//        Log.d(TAG, "setSignInEmailValid executed")
//
////        val emailRegex = Regex("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
//        val emailRegex = Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}\$")
//        _isSignInEmailValid.value = signInEmail.matches(emailRegex)
//    }
//
//    fun setSignInEmailLinkSent(sent: Boolean) {
//        Log.d(TAG, "setSignInEmailLinkSent executed")
//
//        _isSignInEmailLinkSent.value = sent
//    }
//
//    fun setShowGoToSignUpDialog(show: Boolean) {
//        Log.d(TAG, "setShowGoToSignUpDialog executed")
//
//        _showGoToSignUpDialog.value = show
//    }
//
//    fun sendSignInLinkToEmail(email: String) {
//        Log.d(TAG, "sendSignInLinkToEmail executed")
//
//        setEmailToSharedPreferences(email = email)
//
//        userDataSource.sendSignInLinkToEmail(email) { signInLinkSent ->
//            setSignInEmailLinkSent(signInLinkSent)
//        }
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
//}