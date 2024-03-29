package andpact.project.wid.viewModel

import andpact.project.wid.model.Diary
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
import java.time.LocalDate

// 뷰 모델은 한 번 생성하니까 안 없어지는데?
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    init {
        // 생성될 때 로그 출력
        Log.d("HomeViewModel", "HomeViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        // 제거될 때 로그 출력
        Log.d("HomeViewModel", "HomeViewModel is cleared")
    }

    // 날짜
//    private val today = LocalDate.now()
//    var startDate = getFirstDateOfMonth(today)
//    var finishDate = getLastDateOfMonth(today)

    // WiD
    private val wiDService = WiDService(context = application)
//    private val _wiDExistenceList = mutableStateOf(wiDService.checkWiDExistence(startDate = startDate, finishDate = finishDate))
//    val wiDExistenceList: State<Map<LocalDate, Boolean>> = _wiDExistenceList
    private val _lastWiD = mutableStateOf<WiD?>(null)
    val lastWiD: State<WiD?> = _lastWiD

    // 다이어리
    private val diaryService = DiaryService(context = application)
    private val _lastDiary = mutableStateOf<Diary?>(null)
    val lastDiary: State<Diary?> = _lastDiary
//    private val _diaryExistenceList = mutableStateOf(diaryService.checkDiaryExistence(startDate = startDate, finishDate = finishDate))
//    val diaryExistenceList: State<Map<LocalDate, Boolean>> = _diaryExistenceList
    private val _wiDList = mutableStateOf<List<WiD>>(emptyList())
    val wiDList: State<List<WiD>> = _wiDList

    fun setLastWiD() {
        Log.d("HomeViewModel", "setLastWiD executed")

        _lastWiD.value = wiDService.readMostRecentWiD()
    }

    fun setLastDiary() {
        Log.d("HomeViewModel", "setLastDiary executed")

        _lastDiary.value = diaryService.readMostRecentDiary()
        _wiDList.value = _lastDiary.value?.let { diary ->
            wiDService.readDailyWiDListByDate(diary.date)
        } ?: emptyList()
    }
}