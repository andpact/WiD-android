package andpact.project.wid.dataSource

import andpact.project.wid.model.WiD
import andpact.project.wid.repository.WiDRepository
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

// wiDListMap만 가지고 있고, 각 뷰 모델에 wiDList를 뿌려주는 용
class WiDDataSource @Inject constructor(
    private val wiDRepository: WiDRepository
) {
    private val TAG = "WiDDataSource"

    init {
        Log.d(TAG, "created")
    }

    fun onCleared() {
        Log.d(TAG, "cleared")
    }

    // 일주일, 한달(기간) 조회 시 - 뷰 모델이 Map을 참조하고, 뷰에서 firstDate ~ lastDate에 해당하는 WiDList를 참조하도록.
    private val _wiDListMap = mutableStateOf<Map<LocalDate, List<WiD>>>(emptyMap())

    private val defaultWiD = WiD(
        id = "",
        date = LocalDate.now(),
        title = "",
        start = LocalTime.MIN,
        finish = LocalTime.MIN,
        duration = Duration.ZERO
    )

    // EmptyWiD
    private val _emptyWiD = mutableStateOf(defaultWiD)
    val emptyWiD: State<WiD> = _emptyWiD

    // ClickedWiD
    private var _clickedWiD = mutableStateOf(defaultWiD)
    val clickedWiD: State<WiD> = _clickedWiD

    fun createWiD(email: String, onCreateWiDSuccess: () -> Unit) {
        Log.d(TAG, "createWiD executed")

        wiDRepository.createWiD(email = email, wid = _emptyWiD.value) { createdDocumentID ->
            val createdWiD = WiD(
                id = createdDocumentID,
                date = _emptyWiD.value.date,
                title = _emptyWiD.value.title,
                start = _emptyWiD.value.start,
                finish = _emptyWiD.value.finish,
                duration = _emptyWiD.value.duration
            )

            addWiD(date = createdWiD.date, createdWiD = createdWiD)

            onCreateWiDSuccess()
        }
    }

    private fun addWiD(date: LocalDate, createdWiD: WiD) {
        Log.d(TAG, "addWiD executed")

        val currentMap = _wiDListMap.value.toMutableMap()
        val currentList = currentMap[date]?.toMutableList() ?: mutableListOf()

        currentList.add(createdWiD)

        val sortedList = currentList.sortedBy { it.start }

        currentMap[date] = sortedList

        _wiDListMap.value = currentMap
    }

    fun getWiDListByDate(email: String, date: LocalDate, onGetWiDListByDateSuccess: (List<WiD>) -> Unit) {
        val existingWiDList = _wiDListMap.value[date]

        if (existingWiDList != null) { // 캐시된 WiDList가 있을 때
            Log.d(TAG, "getWiDListByDate executed : WiDList from Client")

            onGetWiDListByDateSuccess(existingWiDList)
        } else { // 캐시된 WiDList가 없을 때
            wiDRepository.readWiDListByDate(email = email, date = date) { wiDList ->
                _wiDListMap.value += (date to wiDList)

                Log.d(TAG, "getWiDListByDate executed : WiDList from Server")

                onGetWiDListByDateSuccess(wiDList)
            }
        }
    }

    fun getWiDListFromFirstDateToLastDate(email: String, firstDate: LocalDate, lastDate: LocalDate, callback: (List<WiD>) -> Unit) {
        Log.d(TAG, "getWiDListFromFirstDateToLastDate executed")

        val resultList = mutableListOf<WiD>()
        var currentDate = firstDate

        while (currentDate <= lastDate) {
            val existingWiDList = _wiDListMap.value[currentDate]

            if (existingWiDList != null) {
                // 캐시된 WiDList가 있는 경우 결과 리스트에 추가합니다.
                resultList.addAll(existingWiDList)
            } else {
                // 캐시된 WiDList가 없는 경우 wiDRepository를 통해 데이터를 가져와서 결과 리스트에 추가합니다.
                wiDRepository.readWiDListByDate(email = email, date = currentDate) { wiDs ->
                    _wiDListMap.value += (currentDate to wiDs)
                    resultList.addAll(wiDs)
                }
            }

            // 다음 날짜로 이동합니다.
            currentDate = currentDate.plusDays(1)
        }

        // 모든 날짜에 대한 처리가 끝나면 콜백을 호출합니다.
        callback(resultList)
    }

    fun setEmptyWiD(newEmptyWiD: WiD) {
        Log.d(TAG, "setEmptyWiD executed")

        _emptyWiD.value = newEmptyWiD
    }

    fun setClickedWiD(updatedClickedWiD: WiD) {
        Log.d(TAG, "setClickedWiD executed")

        _clickedWiD.value = updatedClickedWiD
    }

    fun updateClickedWiD(email: String, onUpdateWiDSuccess: () -> Unit) {
        Log.d(TAG, "updateClickedWiD executed")

        wiDRepository.updateWiD(email = email, updatedWiD = _clickedWiD.value) {
            onUpdateWiDSuccess()
        }
    }

    fun deleteCLickedWiD(email: String, onDeleteWiDSuccess: () -> Unit) {
        Log.d(TAG, "deleteCLickedWiD executed")

        wiDRepository.deleteWiD(email = email, clickedWiD = _clickedWiD.value) {
            onDeleteWiDSuccess()
        }

        val clickedWiDDate = _clickedWiD.value.date
        val clickedWiDID = _clickedWiD.value.id

        val currentMap = _wiDListMap.value.toMutableMap()
        val currentList = currentMap[clickedWiDDate]?.toMutableList()

        currentList?.removeIf { it.id == clickedWiDID }

        currentMap[clickedWiDDate] = currentList.orEmpty()

        _wiDListMap.value = currentMap
    }
}