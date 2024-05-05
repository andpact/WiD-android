package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.model.Diary
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.getFirstDateOfMonth
import andpact.project.wid.util.getLastDateOfMonth
import andpact.project.wid.util.titles
import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDateSource: UserDataSource
) : ViewModel() {
    private val TAG = "HomeViewModel"

    init {
        Log.d(TAG, "created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val firebaseUser: State<FirebaseUser?> = userDateSource.firebaseUser
    val user: State<User?> = userDateSource.user

//    val email: State<String> = userDateSource.email
//    private val email = userDateSource.email
}