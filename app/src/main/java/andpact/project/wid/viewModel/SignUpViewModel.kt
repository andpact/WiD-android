package andpact.project.wid.viewModel

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
//class SignUpViewModel @Inject constructor(
//    private val userDataSource: UserDataSource,
//    private val sharedPreferences: SharedPreferences
//) : ViewModel() {
//    private val TAG = "SignUpViewModel"
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
////    private val preferencesName = "WiDAuth"
////    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
//
//    // 싱글톤으로 만들기
////    private val userRepository: UserRepository = UserRepository()
//
//    private val _signUpEmail = mutableStateOf("")
//    val signUpEmail: State<String> = _signUpEmail
//    private val _isSignUpEmailEdited = mutableStateOf(false)
//    val isSignUpEmailEdited: State<Boolean> = _isSignUpEmailEdited
//    private val _isSignUpEmailValid = mutableStateOf(false)
//    val isSignUpEmailValid: State<Boolean> = _isSignUpEmailValid
//    private val _isSignUpEmailLinkSent = mutableStateOf(false)
//    val isSignUpEmailLinkSent: State<Boolean> = _isSignUpEmailLinkSent
//    private val _showGoToSignInDialog = mutableStateOf(false)
//    val showGoToSignInDialog: State<Boolean> = _showGoToSignInDialog
//
//    fun setSignUpEmail(newSignUpEmail: String) {
//        Log.d(TAG, "setSignUpEmail executed")
//
//        _signUpEmail.value = newSignUpEmail
//    }
//
//    fun setSignUpEmailEdited(edited: Boolean) {
//        Log.d(TAG, "setSignUpEmailEdited executed")
//
//        _isSignUpEmailEdited.value = edited
//    }
//
//    fun setSignUpEmailValid(signUpEmail: String) {
//        Log.d(TAG, "setSignUpEmailValid executed")
//
////        val emailRegex = Regex("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
//        val emailRegex = Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}\$")
//        _isSignUpEmailValid.value = signUpEmail.matches(emailRegex)
//    }
//
//    fun setSignUpEmailLinkSent(sent: Boolean) {
//        Log.d(TAG, "setSignUpEmailLinkSent executed")
//
//        _isSignUpEmailLinkSent.value = sent
//    }
//
//    fun setShowGoToSignInDialog(show: Boolean) {
//        Log.d(TAG, "setShowGoToSignInDialog executed")
//
//        _showGoToSignInDialog.value = show
//    }
//
//    // 회원가입 인증 링크를 전송하는 메서드
//    fun sendSignUpLinkToEmail(email: String) {
//        Log.d(TAG, "sendSignUpLinkToEmail executed")
//
//        setEmailToSharedPreferences(email = email)
//
//        userDataSource.sendSignUpLinkToEmail(email) { signUpLinkSent ->
//            setSignUpEmailLinkSent(signUpLinkSent)
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