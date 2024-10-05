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

//class DayDiaryViewModel(application: Application) : AndroidViewModel(application) {
//    private val TAG = "DayDiaryViewModel"
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
//    // 날짜
//    val today: LocalDate = LocalDate.now()
//    private val _currentDate = mutableStateOf(today)
//    val currentDate: State<LocalDate> = _currentDate
////    private val _expandDatePicker = mutableStateOf(false)
////    val expandDatePicker: State<Boolean> = _expandDatePicker
//
//    // WiD
//    private val wiDService = WiDService(context = application)
////    private val _wiDList = mutableStateOf(wiDService.readDailyWiDListByDate(today))
//    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
//    val wiDList: State<List<WiD>> = _wiDList
//
//    // 다이어리
//    private val diaryService = DiaryService(context = application)
////    private val _diary = mutableStateOf(diaryService.readDiaryByDate(_currentDate.value))
//    private val _diary = mutableStateOf<Diary?>(null)
//    val diary: State<Diary?> = _diary
//
////    fun setExpandDatePicker(expand: Boolean) {
////        Log.d("DayDiaryViewModel", "setExpandDatePicker executed")
////        _expandDatePicker.value = expand
////    }
//
//    fun setCurrentDate(newDate: LocalDate) {
//        Log.d(TAG, "setCurrentDate executed")
//
//        _currentDate.value = newDate
//        _wiDList.value = wiDService.readDailyWiDListByDate(_currentDate.value)
//        _diary.value = diaryService.readDiaryByDate(_currentDate.value)
//    }
//}