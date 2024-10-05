package andpact.project.wid.tmp

//import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
//import andpact.project.wid.service.DiaryService
//import andpact.project.wid.service.WiDService
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate

//class SearchViewModel(application: Application) : AndroidViewModel(application) {
//    private val TAG = "SearchViewModel"
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
//    // WiD
//    private val wiDService = WiDService(context = application)
//    private val _wiDMap = mutableStateOf<Map<LocalDate, List<WiD>>>(emptyMap())
//    val wiDMap: State<Map<LocalDate, List<WiD>>> = _wiDMap
//
//    // 다이어리
////    val searchFilter = SearchFilter.ByTitleOrContent
//    private val _searchFilter = mutableStateOf("ByTitleOrContent")
//    val searchFilter: State<String> = _searchFilter
//    private val diaryService = DiaryService(context = application)
//    private val _searchText = mutableStateOf("")
//    val searchText: State<String> = _searchText
//    private val _searchComplete = mutableStateOf(false)
//    val searchComplete: State<Boolean> = _searchComplete
//    private val _diaryDateList = mutableStateOf<List<LocalDate>>(emptyList())
//    val diaryDateList: State<List<LocalDate>> = _diaryDateList
//    private val _diaryMap = mutableStateOf<Map<LocalDate, Diary?>>(emptyMap())
//    val diaryMap: State<Map<LocalDate, Diary?>> = _diaryMap
//
//    fun setSearchFilter(newSearchFilter: String) {
//        Log.d(TAG, "setSearchFilter executed")
//
//        _searchFilter.value = newSearchFilter
//    }
//
//    fun setSearchText(text: String) {
//        Log.d(TAG, "setSearchText executed")
//
//        _searchText.value = text
//    }
//
//    fun setSearchComplete(isComplete: Boolean) {
//        Log.d(TAG, "setSearchComplete executed")
//
//        _searchComplete.value = isComplete
//    }
//
//    // 날짜를 가져오면서 WiDMap과 DiaryMap도 같이 할당함.
//    fun fetchDiaryDates() {
//        Log.d(TAG, "fetchDiaryDates executed")
//
////        val existingDates = _diaryDateList.value
//
//        val randomDates: List<LocalDate> = when (_searchFilter.value) {
//            "ByTitleOrContent" -> diaryService.readDiaryDatesByTitleOrContent(_searchText.value)
//            "ByTitle" -> diaryService.readDiaryDatesByTitle(_searchText.value)
//            "ByContent" -> diaryService.readDiaryDatesByContent(_searchText.value)
//            else -> emptyList() // 기본값 처리
//        }
//        _diaryDateList.value = randomDates
//
//        // 랜덤한 날짜에 해당하는 WiD와 Diary 읽어오기
//        val newWiDMap = mutableMapOf<LocalDate, List<WiD>>()
//        val newDiaryMap = mutableMapOf<LocalDate, Diary?>()
//        for (date in randomDates) {
//            val wiDListByDate = wiDService.readDailyWiDListByDate(date)
//            newWiDMap[date] = wiDListByDate
//
//            val diary = diaryService.readDiaryByDate(date)
//            newDiaryMap[date] = diary
//        }
//        _wiDMap.value = newWiDMap
//        _diaryMap.value = newDiaryMap
//    }
//
////    fun clearData() {
////        Log.d(TAG, "clearData executed")
////
////        _wiDMap.value = emptyMap()
////        _diaryDateList.value = emptyList()
////        _diaryMap.value = emptyMap()
////    }
//}