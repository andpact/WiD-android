package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.UserDataSource
import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.*
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CityPickerViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val wiDDataSource: WiDDataSource
): ViewModel() {
    private val TAG = "CityPickerViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val countryList = Country.values().toList()
    private val _selectedCountry = mutableStateOf(Country.KOREA)
    val selectedCountry: State<Country> = _selectedCountry

    private val CITY = userDataSource.CITY

    val user: State<User?> = userDataSource.user

    private val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy // 수정 후

    val currentWiD: State<WiD> = wiDDataSource.currentWiD

    fun setSelectedCountry(newSelectedCountry: Country) {
        Log.d(TAG, "setSelectedCountry executed")

        _selectedCountry.value = newSelectedCountry
    }

    fun setUserCity(updatedCity: City) {
        Log.d(TAG, "setUserCity executed")

        val currentUser = user.value ?: return
        val updatedFields = mutableMapOf<String, Any>()
        updatedFields[CITY] = updatedCity.name // String 타입으로 서버로 보냄.

        // TODO: 스낵 바 호출해야함
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