package andpact.project.wid.viewModel

import andpact.project.wid.dataSource.WiDDataSource
import andpact.project.wid.model.SubTitle
import andpact.project.wid.model.Title
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TitlePickerViewModel @Inject constructor(private val wiDDataSource: WiDDataSource): ViewModel() {
    private val TAG = "TitlePickerViewModel"
    init { Log.d(TAG, "created") }
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "cleared")
    }

    val titleList = Title.values().drop(1)
    private val _selectedTitle = mutableStateOf(Title.STUDY)
    val selectedTitle: State<Title> = _selectedTitle

//    val sutTitleArray = SubTitle.values()
//
//    private val _isSearchMode = mutableStateOf(false)
//    val isSearchMode: State<Boolean> = _isSearchMode
//
//    private val _searchText = mutableStateOf("")
//    val searchText: State<String> = _searchText
//    val searchedSubTitleList: State<List<SubTitle>> = derivedStateOf { updateSearchedSubTitleList() }

    val clickedWiDCopy: State<WiD> = wiDDataSource.clickedWiDCopy

    val firstCurrentWiD: State<WiD> = wiDDataSource.firstCurrentWiD
    val secondCurrentWiD: State<WiD> = wiDDataSource.secondCurrentWiD

    fun setSelectedTitle(newSelectedTitle: Title) {
        Log.d(TAG, "setSelectedTitle executed")

        _selectedTitle.value = newSelectedTitle
    }

//    fun setIsSearchMode(set: Boolean) {
//        Log.d(TAG, "setIsSearchMode executed")
//
//        _isSearchMode.value = set
//    }
//
//    fun updateSearchText(newSearchText: String) {
//        Log.d(TAG, "updateSearchText executed")
//
//        _searchText.value = newSearchText
//    }
//
//    private fun updateSearchedSubTitleList(): List<SubTitle> {
//        Log.d(TAG, "updateSearchedSubTitleList executed")
//
//        return if (_searchText.value.isBlank()) { // 검색어 없으면 빈 리스트
//            emptyList()
//        } else {
//            sutTitleArray.filter {
//                it.kr.contains(_searchText.value, ignoreCase = true) || // 한국어 검색
//                        it.name.contains(_searchText.value, ignoreCase = true) // 영어 검색
//            }
//        }
//    }

    fun setCurrentWiDTitleAndSubTitle(newTitle: Title, newSubTitle: SubTitle) { // 현재 기록은 제목과 부제목만 수정
        Log.d(TAG, "setCurrentWiDTitleAndSubTitle executed")

        wiDDataSource.setCurrentWiDTitleAndSubTitle(newTitle = newTitle, newSubTitle = newSubTitle)
    }

    fun setClickedWiDCopyTitleAndSubTitle(newTitle: Title, newSubTitle: SubTitle) { // 클릭된 기록은 기록 객체를 전달
        Log.d(TAG, "setClickedWiDCopyTitleAndSubTitle executed")

        val newClickedWiDCopy = clickedWiDCopy.value.copy(title = newTitle, subTitle = newSubTitle)

        wiDDataSource.setClickedWiDCopy(newClickedWiDCopy = newClickedWiDCopy)
    }
}