package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.City
import andpact.project.wid.model.Title
import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CityPickerViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "TitlePickerViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val cityArray = City.values()

    private val CITY = userDataSource.CITY

    val user: State<User?> = userDataSource.user

    private val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy // 수정 후

    fun setUserCity(updatedCity: City) {
        Log.d(TAG, "setUserCity executed")

        val currentUser = user.value ?: return
        val updatedFields = mutableMapOf<String, Any>()
        updatedFields[CITY] = updatedCity.name // String 타입으로 서버로 보냄.

        userDataSource.setUserDocument(
            email = currentUser.email,
            updatedUserDocument = updatedFields
        )
    }

    fun setClickedWiDCopyCity(updatedCity: City) {
        Log.d(TAG, "setWiDCity executed")

        val updatedClickedWiDCopy = clickedWiDCopy.value.copy(city = updatedCity)

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = updatedClickedWiDCopy)
    }
}