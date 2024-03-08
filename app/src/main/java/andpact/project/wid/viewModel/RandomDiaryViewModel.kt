package andpact.project.wid.viewModel

import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate

class RandomDiaryViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d("RandomDiaryViewModel", "RandomDiaryViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("RandomDiaryViewModel", "RandomDiaryViewModel is cleared")
    }

    // WiD
    private val wiDService = WiDService(context = application)
    private val _wiDMap = mutableStateOf<Map<LocalDate, List<WiD>>>(emptyMap())
    val wiDMap: State<Map<LocalDate, List<WiD>>> = _wiDMap

    // 다이어리
    private val diaryService = DiaryService(context = application)
//    private val _totalDiaryCount = mutableStateOf(diaryService.getDiaryCount())
//    val totalDiaryCount: State<Int> = _totalDiaryCount
    val totalDiaryCount = diaryService.getDiaryCount()
    private val _diaryDateList = mutableStateOf<List<LocalDate>>(emptyList())
    val diaryDateList: State<List<LocalDate>> = _diaryDateList
    private val _diaryMap = mutableStateOf<Map<LocalDate, Diary?>>(emptyMap())
    val diaryMap: State<Map<LocalDate, Diary?>> = _diaryMap

    // 랜덤 날짜를 가져오면서 WiDMap과 DiaryMap도 같이 할당함.
    fun fetchRandomDiaryDates() {
        Log.d("RandomDiaryViewModel", "fetchRandomDiaryDates executed")

        val existingDates = _diaryDateList.value
        val randomDates = diaryService.readRandomDiaryDates(existingDates)
        _diaryDateList.value = randomDates

        // 랜덤한 날짜에 해당하는 WiD와 Diary 읽어오기
        val newWiDMap = mutableMapOf<LocalDate, List<WiD>>()
        val newDiaryMap = mutableMapOf<LocalDate, Diary?>()
        for (date in randomDates) {
            val wiDListByDate = wiDService.readDailyWiDListByDate(date)
            newWiDMap[date] = wiDListByDate

            val diary = diaryService.readDiaryByDate(date)
            newDiaryMap[date] = diary
        }
        _wiDMap.value = newWiDMap
        _diaryMap.value = newDiaryMap
    }
}