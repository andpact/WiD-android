package andpact.project.wid.repository

import andpact.project.wid.model.WiD
import andpact.project.wid.model.YearlyWiDList
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.Title
import andpact.project.wid.util.toDocument
import andpact.project.wid.util.toYearlyWiDList
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import java.time.*
import java.util.*
import javax.inject.Inject

/**
 * DATA 컬렉션 안에서 이메일로 문서를 선택한 후 WiD 컬렉션으로 들어감.
 */
class WiDRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    private val TAG = "WiDRepository"
    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

    private val TMP_EMAIL = "andpact@gmail.com"

    // 컬렉션
    private val DATA_COLLECTION = "DATA"
    private val WID_COLLECTION = "WiDCollection"

    // 문서 필드
    private val WID_LIST = "wiDList"

//    private val ID = "id"
//    private val DATE = "date"
//    private val TITLE = "title"
//    private val START = "start"
//    private val FINISH = "finish"
//    private val DURATION = "duration"
//    private val CREATED_BY = "createdBy"

    /** 2024년 문서를 불러온 후 시간이 2025년으로 넘어가면 2025년 문서가 없는데? 년도가 바뀌는 걸 감지하고 메서드를 자동으로 실행해서 문서를 생성하도록?*/
    fun getYearlyWiDList(
        email: String,
        year: Year,
        onYearlyWiDListFetched: (yearlyWiDListFetched: YearlyWiDList) -> Unit,
    ) {
        val yearlyDocumentRef = firestore.collection(DATA_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(WID_COLLECTION)
            .document(year.toString())

        val defaultYearlyWiDList = YearlyWiDList(wiDList = emptyList())

        yearlyDocumentRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val yearlyWiDList = snapshot.data?.toYearlyWiDList() ?: defaultYearlyWiDList
                    Log.d(TAG, "Yearly document fetched successfully")
                    onYearlyWiDListFetched(yearlyWiDList)
                } else {
                    Log.d(TAG, "Yearly document does not exist. Creating a new one.")
                    yearlyDocumentRef.set(defaultYearlyWiDList.toDocument())
                        .addOnSuccessListener {
                            Log.d(TAG, "New yearly document created successfully")
                            onYearlyWiDListFetched(defaultYearlyWiDList)
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Failed to create new yearly document: ${exception.message}")
                            onYearlyWiDListFetched(defaultYearlyWiDList)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to fetch yearly document: ${exception.message}")
            }
    }

    /** set으로 추가, 갱신, 삭제 다 할 수 있을 것 같은데? */
    fun addWiDList(
        email: String,
        wiDList: List<WiD>,
        onWiDListAdded: (wiDListAdded: Boolean) -> Unit
    ) {
        if (wiDList.isEmpty()) {
            Log.e(TAG, "addWiDs failed: WiD list is empty")
            onWiDListAdded(false)
            return
        }

        Log.d(TAG, "addWiDs executed with ${wiDList.size} WiD(s)")

        // Firestore 경로: DATA_COLLECTION/{email}/WiDCollection/{year}
        val year = wiDList.first().date.year // 모든 WiD는 같은 연도에 있어야 함
        val documents = wiDList.map { it.toDocument() } // WiD를 Firestore 문서 형식으로 변환

        firestore.collection(DATA_COLLECTION)
//        .document(email)
            .document(TMP_EMAIL)
            .collection(WID_COLLECTION)
            .document(year.toString())
            .update(WID_LIST, FieldValue.arrayUnion(*documents.toTypedArray())) // 여러 WiD를 한 번에 추가
            .addOnSuccessListener {
                Log.d(TAG, "WiDs added successfully to YearlyWiDList document")
                onWiDListAdded(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to add WiDs: ${exception.message}")
                onWiDListAdded(false)
            }
    }

    fun updateWiD(
        email: String,
        year: Year,
        yearlyWiDList: YearlyWiDList?,
        onWiDUpdated: (wiDListUpdated: Boolean) -> Unit
    ) {
        if (yearlyWiDList == null) {
            Log.e(TAG, "updateWiD failed: YearlyWiDList is null")
            onWiDUpdated(false)
            return
        }

        Log.d(TAG, "updateWiD executed for year: $year")

        firestore.collection(DATA_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(WID_COLLECTION)
            .document(year.toString())
            .set(yearlyWiDList.toDocument())
            .addOnSuccessListener {
                Log.d(TAG, "YearlyWiDList updated successfully for year: $year")
                onWiDUpdated(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to update YearlyWiDList for year: $year: ${exception.message}")
                onWiDUpdated(false)
            }
    }

    fun deleteWiD(
        email: String,
        year: Year,
        yearlyWiDList: YearlyWiDList?,
        onWiDDeleted: (wiDDeleted: Boolean) -> Unit
    ) {
        if (yearlyWiDList == null) {
            Log.e(TAG, "deleteWiD failed: YearlyWiDList is null")
            onWiDDeleted(false)
            return
        }

        Log.d(TAG, "deleteWiD executed")

        firestore.collection(DATA_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL) // Replace with `email` if dynamic email is used
            .collection(WID_COLLECTION)
            .document(year.toString())
            .set(yearlyWiDList.toDocument())
            .addOnSuccessListener {
                Log.d(TAG, "YearlyWiDList updated successfully after deletion")
                onWiDDeleted(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to update YearlyWiDList after deletion: ${exception.message}")
                onWiDDeleted(false)
            }
    }

//    fun createWiD(
//        email: String,
//        wid: WiD?,
//        onWiDCreated: (createdWiDID: String, wiDCreated: Boolean) -> Unit
//    ) {
//        if (wid == null) return
//
//        Log.d(TAG, "createWiD executed")
//
//        val dateAsString = wid.date.toString()
//        val titleAsString = wid.title.name
//        val startAsTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneId.systemDefault()).toInstant()))
//        val finishAsTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneId.systemDefault()).toInstant()))
//        val durationAsInt = wid.duration.seconds.toInt()
//        val createdByAsString = wid.createdBy.name
//
//        // id 필드는 제거하지말고, WiD 문서를 가져왔을 떄, 해당 문서의 자동 생성 ID를 할당해서 사용하자.
//        val newWiDDocument = hashMapOf(
//            ID to "0", // 문서의 필드에는 0을 할당함.
//            DATE to dateAsString,
//            TITLE to titleAsString,
//            START to startAsTimestamp,
//            FINISH to finishAsTimestamp,
//            DURATION to durationAsInt,
//            CREATED_BY to createdByAsString
//        )
//
//        firestore.collection(WID_COLLECTION)
////            .document(email)
//            .document(TMP_EMAIL)
//            .collection(dateAsString)
//            .add(newWiDDocument)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//
//                onWiDCreated(documentReference.id, true)
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//
//                onWiDCreated("..", false)
//            }
//    }
//
//    fun getWiDListByDate(
//        email: String,
//        collectionDate: LocalDate,
//        onWiDListFetchedByDate: (wiDList: List<WiD>) -> Unit
//    ) {
//        Log.d(TAG, "getWiDListByDate executed")
//
//        val collectionDateAsString = collectionDate.toString()
//        val wiDList = mutableListOf<WiD>()
//
//        firestore.collection(WID_COLLECTION)
////            .document(email)
//            .document(TMP_EMAIL)
//            .collection(collectionDateAsString)
//            .orderBy(START, Query.Direction.ASCENDING) // 시작 시간 기준으로 오름차순 정렬
//            .get()
//            .addOnSuccessListener { result ->
//                Log.d(TAG, "Success getting documents")
//
//                for (document in result) {
//                    val iD = document.id
//                    val dateAsString = document.getString(DATE)
//                    val date = LocalDate.parse(dateAsString)
//                    val title = document.getString(TITLE)!!
//                    val titleAsTitle = Title.valueOf(title)
//                    val startAsTimestamp = document.getTimestamp(START)
//                    val start = startAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                    val finishAsTimestamp = document.getTimestamp(FINISH)
//                    val finish = finishAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                    val durationAsLong = document.getLong(DURATION)!!
//                    val duration = Duration.ofSeconds(durationAsLong)
//                    val createdByAsString = document.getString(CREATED_BY)!!
//                    val createdBy = CurrentTool.valueOf(createdByAsString)
//
//                    val wiD = WiD(
//                        id = iD,
//                        date = date,
//                        title = titleAsTitle,
//                        start = start,
//                        finish = finish,
//                        duration = duration,
//                        createdBy = createdBy
//                    )
//
//                    wiDList.add(wiD)
//
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//
//                onWiDListFetchedByDate(wiDList) // 완성된 리스트를 콜백으로 반환
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
//
//                onWiDListFetchedByDate(emptyList()) // 실패할 경우 빈 리스트를 콜백으로 반환
//            }
//    }
//
//    fun updateWiD(
//        email: String,
//        updatedWiD: WiD?,
//        onWiDUpdated: (Boolean) -> Unit
//    ) {
//        if (updatedWiD == null) return
//
//        Log.d(TAG, "updateWiD executed")
//
//        val collectionDateAsString = updatedWiD.date.toString()
//        val iD = updatedWiD.id
//
//        val updatedTitle = updatedWiD.title.name
//        val updatedStart = updatedWiD.start
//        val updatedFinish = updatedWiD.finish
//        val updatedDuration = updatedWiD.duration.seconds.toInt()
//
//        val updatedWiDDocument = hashMapOf(
//            TITLE to updatedTitle,
//            START to updatedStart,
//            FINISH to updatedFinish,
//            DURATION to updatedDuration
//        )
//
//        firestore.collection(WID_COLLECTION)
////            .document(email)
//            .document(TMP_EMAIL)
//            .collection(collectionDateAsString)
//            .document(iD)
//            .set(updatedWiDDocument)
//            .addOnSuccessListener {
//                Log.d(TAG, "Document successfully updated")
//
//                onWiDUpdated(true)
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error updating document", e)
//
//                /** 업데이트 실패 이유를 화면에 띄우면 좋을 듯? 예를 들어 인터넷이 안된다거나.. */
//                onWiDUpdated(false)
//            }
//    }
//
//    fun deleteWiD(
//        email: String,
//        wiD: WiD?,
//        onWiDDeleted: (Boolean) -> Unit
//    ) {
//        // 위드가 비어 있으면 "잘못된 접근입니다." 알려주기?
//        if (wiD == null) return
//
//        Log.d(TAG, "deleteWiD executed")
//
//        val collectionDateAsString = wiD.date.toString()
//        val iD = wiD.id
//
//        firestore.collection(WID_COLLECTION)
////            .document(email)
//            .document(TMP_EMAIL)
//            .collection(collectionDateAsString)
//            .document(iD)
//            .delete()
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully deleted!")
//
//                onWiDDeleted(true)
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error deleting document", e)
//
//                onWiDDeleted(false)
//            }
//    }
}