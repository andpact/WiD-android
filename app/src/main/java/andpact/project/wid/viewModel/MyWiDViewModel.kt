package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.User
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class MyWiDViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
) : ViewModel() {
    private val TAG = "MyAccountViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    private val WID_MIN_LIMIT = userDataSource.WID_MIN_LIMIT
    private val WID_MAX_LIMIT = userDataSource.WID_MAX_LIMIT

    val user: State<User?> = userDataSource.user

    private val _showUpdateWiDMinLimitDialog = mutableStateOf(false)
    val showUpdateWiDMinLimitDialog: State<Boolean> = _showUpdateWiDMinLimitDialog
    private val _showUpdateWiDMaxLimitDialog = mutableStateOf(false)
    val showUpdateWiDMaxLimitDialog: State<Boolean> = _showUpdateWiDMaxLimitDialog

    fun setShowUpdateWiDMinLimitDialog(show: Boolean) {
        Log.d(TAG, "setShowUpdateWiDMinLimitDialog executed")

        _showUpdateWiDMinLimitDialog.value = show
    }

    fun setShowUpdateWiDMaxLimitDialog(show: Boolean) {
        Log.d(TAG, "setShowUpdateWiDMaxLimitDialog executed")

        _showUpdateWiDMaxLimitDialog.value = show
    }

    fun updateWiDMinLimit(minLimit: Duration) {
        Log.d(TAG, "updateWiDMinLimit executed")

        val currentUser = user.value ?: return // 잘못된 접근

        val updatedFields = mutableMapOf<String, Any>()
        updatedFields[WID_MIN_LIMIT] = minLimit.seconds // Long 타입으로 서버로 보냄

        userDataSource.setUserDocument(
            email = currentUser.email,
            updatedUserDocument = updatedFields
        )
    }

    fun updateWiDMaxLimit(maxLimit: Duration) {
        Log.d(TAG, "updateWiDMaxLimit executed")

        val currentUser = user.value ?: return // 잘못된 접근

        val updatedFields = mutableMapOf<String, Any>()
        updatedFields[WID_MAX_LIMIT] = maxLimit.seconds // Long 타입으로 서버로 보냄

        userDataSource.setUserDocument(
            email = currentUser.email,
            updatedUserDocument = updatedFields
        )
    }

    fun getDurationString(duration: Duration): String { // "H시간 m분 s초"
//        Log.d(TAG, "getDurationString executed")

        return wiDDataSource.getDurationString(duration = duration)
    }
}