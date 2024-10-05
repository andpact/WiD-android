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
    init { Log.d(TAG, "created") }
    fun onCleared() { Log.d(TAG, "cleared") }

    private val _wiDListMap = mutableStateOf<Map<LocalDate, List<WiD>>>(emptyMap())

    private val defaultWiD = WiD(
        id = "",
        date = LocalDate.now(),
        title = "무엇을 하셨나요?",
        start = LocalTime.MIN,
        finish = LocalTime.MIN,
        duration = Duration.ZERO
    )

    private val _emptyWiD = mutableStateOf(defaultWiD)
    val emptyWiD: State<WiD> = _emptyWiD

    private var _existingWiD = mutableStateOf(defaultWiD) // 얘는 수정할 일이 없음.
    val existingWiD: State<WiD> = _existingWiD

    private var _updatedWiD = mutableStateOf(defaultWiD)
    val updatedWiD: State<WiD> = _updatedWiD

    /** 데이터 소스 단에서 이메일을 참조할 수 없으니, 뷰 모델 단에서 실행해야 함. */
//    fun addSnapshotListenerToWiDCollectionByDate(
//        email: String,
//        collectionDate: LocalDate,
//        onWiDCollectionChanged: (List<WiD>) -> Unit
//    ) {
//        Log.d(TAG, "addSnapshotListenerToWiDCollectionByDate executed")
//
//        wiDRepository.addSnapshotListenerToWiDCollectionByDate(
//            email = email,
//            collectionDate = collectionDate,
//            onWiDCollectionChanged = { wiDList: List<WiD> ->
////                _wiDListMap.value += (collectionDate to wiDList)
//
//                onWiDCollectionChanged(wiDList)
//            }
//        )
//    }

//    fun addSnapshotListenerToWiDCollectionFromFirstDateToLastDate(
//        email: String,
//        collectionFirstDate: LocalDate,
//        collectionLastDate: LocalDate,
//        onWiDCollectionChanged: (List<WiD>) -> Unit
//    ) {
//        Log.d(TAG, "addSnapshotListenerToWiDCollectionFromFirstDateToLastDate executed")
//
//        wiDRepository.addSnapshotListenerToWiDCollectionFromFirstDateToLastDate(
//            email = email,
//            collectionFirstDate = collectionFirstDate,
//            collectionLastDate = collectionLastDate,
//            onWiDCollectionChanged = { wiDList: List<WiD> ->
//                onWiDCollectionChanged(wiDList)
//            }
//        )
//    }

    fun createWiD(
        email: String,
        onWiDCreated: (Boolean) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        wiDRepository.createWiD(
            email = email,
            wid = _emptyWiD.value,
            onWiDCreated = { createdDocumentID: String, wiDCreated: Boolean ->
                if (wiDCreated) {
                    val createdWiD = WiD(
                        id = createdDocumentID,
                        date = _emptyWiD.value.date,
                        title = _emptyWiD.value.title,
                        start = _emptyWiD.value.start,
                        finish = _emptyWiD.value.finish,
                        duration = _emptyWiD.value.duration
                    )

                    addWiDToMap(createdWiD = createdWiD)
                }

                onWiDCreated(wiDCreated)
            }
        )
    }

    fun addWiDToMap(createdWiD: WiD) {
        Log.d(TAG, "addWiDtoMap executed")

        val currentMap = _wiDListMap.value.toMutableMap()
        val currentList = currentMap[createdWiD.date]?.toMutableList() ?: mutableListOf()

        currentList.add(createdWiD)

        val sortedList = currentList.sortedBy { it.start }

        currentMap[createdWiD.date] = sortedList

        _wiDListMap.value = currentMap
    }

    fun getWiDListByDate(
        email: String,
        collectionDate: LocalDate,
        onWiDListFetchedByDate: (List<WiD>) -> Unit
    ) {
        Log.d(TAG, "getWiDListByDate executed")

        // 다른 클라이언트에서 위드를 추가한 상태에서, 캐싱 맵의 위드 리스트를 사용하면 동기화가 안될 수 있음
        val existingWiDList = _wiDListMap.value[collectionDate]

        if (existingWiDList != null) { // 캐시된 WiDList가 있을 때
            Log.d(TAG, "getWiDListByDate executed : WiDList from Client")

            onWiDListFetchedByDate(existingWiDList)
        } else { // 캐시된 WiDList가 없을 때
            wiDRepository.getWiDListByDate(
                email = email,
                collectionDate = collectionDate,
                onWiDListFetchedByDate = { wiDList: List<WiD> ->
                    _wiDListMap.value += (collectionDate to wiDList)

                    Log.d(TAG, "getWiDListByDate executed : WiDList from Server")

                    onWiDListFetchedByDate(wiDList)
                }
            )
        }
    }

    fun getWiDListFromFirstDateToLastDate(
        email: String,
        firstDate: LocalDate,
        lastDate: LocalDate,
        onWiDListFetchedFromFirstDateToLastDate: (List<WiD>) -> Unit
    ) {
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
                wiDRepository.getWiDListByDate(
                    email = email,
                    collectionDate = currentDate,
                    onWiDListFetchedByDate = { wiDList: List<WiD> ->
                        _wiDListMap.value += (currentDate to wiDList)
                        resultList.addAll(wiDList)
                    }
                )
            }

            // 다음 날짜로 이동합니다.
            currentDate = currentDate.plusDays(1)
        }

        // 모든 날짜에 대한 처리가 끝나면 콜백을 호출합니다.
        onWiDListFetchedFromFirstDateToLastDate(resultList)
    }

    fun setEmptyWiD(newWiD: WiD) {
        Log.d(TAG, "setEmptyWiD executed")

        _emptyWiD.value = newWiD
    }

    fun setExistingWiD(existingWiD: WiD) {
        Log.d(TAG, "setExistingWiD executed")

        _existingWiD.value = existingWiD
    }

    fun setUpdatedWiD(updatedWiD: WiD) {
        Log.d(TAG, "setUpdatedWiD executed")

        _updatedWiD.value = updatedWiD
    }

    fun updateWiD(
        email: String,
        onWiDUpdated: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        wiDRepository.updateWiD(
            email = email,
            updatedWiD = _updatedWiD.value,
            onWiDUpdated = { wiDUpdated: Boolean ->
                onWiDUpdated(wiDUpdated)
            }
        )
    }

    fun deleteWiD(
        email: String,
        onWiDDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        wiDRepository.deleteWiD(
            email = email,
            wiD = _existingWiD.value, // existingWiD 사용해도 되고, updatedWiD 사용해도 됨. id는 동일하니.
            onWiDDeleted = { wiDDeleted: Boolean ->
                onWiDDeleted(wiDDeleted)
            }
        )

        val clickedWiDDate = _existingWiD.value.date
        val clickedWiDID = _existingWiD.value.id

        val currentMap = _wiDListMap.value.toMutableMap()
        val currentList = currentMap[clickedWiDDate]?.toMutableList()

        currentList?.removeIf { it.id == clickedWiDID }

        currentMap[clickedWiDDate] = currentList.orEmpty()

        _wiDListMap.value = currentMap
    }
}