package andpact.project.wid.repository

import andpact.project.wid.model.WiD
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.Title
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

class WiDRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    private val TAG = "WiDRepository"
    private val WID_COLLECTION = "WiDCollection"
    private val TMP_EMAIL = "andpact@gmail.com"

    private val ID = "id"
    private val DATE = "date"
    private val TITLE = "title"
    private val START = "start"
    private val FINISH = "finish"
    private val DURATION = "duration"
    private val CREATED_BY = "createdBy"

    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

    fun createWiD(
        email: String,
        wid: WiD?,
        onWiDCreated: (createdWiDID: String, wiDCreated: Boolean) -> Unit
    ) {
        if (wid == null) return

        Log.d(TAG, "createWiD executed")

        val dateAsString = wid.date.toString()
        val titleAsString = wid.title.name
        val startAsTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneId.systemDefault()).toInstant()))
        val finishAsTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneId.systemDefault()).toInstant()))
        val durationAsInt = wid.duration.seconds.toInt()
        val createdByAsString = wid.createdBy.name

        // id 필드는 제거하지말고, WiD 문서를 가져왔을 떄, 해당 문서의 자동 생성 ID를 할당해서 사용하자.
        val newWiDDocument = hashMapOf(
            ID to "0", // 문서의 필드에는 0을 할당함.
            DATE to dateAsString,
            TITLE to titleAsString,
            START to startAsTimestamp,
            FINISH to finishAsTimestamp,
            DURATION to durationAsInt,
            CREATED_BY to createdByAsString
        )

        firestore.collection(WID_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(dateAsString)
            .add(newWiDDocument)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                onWiDCreated(documentReference.id, true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)

                onWiDCreated("..", false)
            }
    }

    fun getWiDListByDate(
        email: String,
        collectionDate: LocalDate,
        onWiDListFetchedByDate: (wiDList: List<WiD>) -> Unit
    ) {
        Log.d(TAG, "getWiDListByDate executed")

        val collectionDateAsString = collectionDate.toString()
        val wiDList = mutableListOf<WiD>()

        firestore.collection(WID_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(collectionDateAsString)
            .orderBy(START, Query.Direction.ASCENDING) // 시작 시간 기준으로 오름차순 정렬
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Success getting documents")

                for (document in result) {
                    val iD = document.id
                    val dateAsString = document.getString(DATE)
                    val date = LocalDate.parse(dateAsString)
                    val title = document.getString(TITLE)!!
                    val titleAsTitle = Title.valueOf(title)
                    val startAsTimestamp = document.getTimestamp(START)
                    val start = startAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                    val finishAsTimestamp = document.getTimestamp(FINISH)
                    val finish = finishAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                    val durationAsLong = document.getLong(DURATION)!!
                    val duration = Duration.ofSeconds(durationAsLong)
                    val createdByAsString = document.getString(CREATED_BY)!!
                    val createdBy = CurrentTool.valueOf(createdByAsString)

                    val wiD = WiD(
                        id = iD,
                        date = date,
                        title = titleAsTitle,
                        start = start,
                        finish = finish,
                        duration = duration,
                        createdBy = createdBy
                    )

                    wiDList.add(wiD)

                    Log.d(TAG, "${document.id} => ${document.data}")
                }

                onWiDListFetchedByDate(wiDList) // 완성된 리스트를 콜백으로 반환
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)

                onWiDListFetchedByDate(emptyList()) // 실패할 경우 빈 리스트를 콜백으로 반환
            }
    }

    fun updateWiD(
        email: String,
        updatedWiD: WiD?,
        onWiDUpdated: (Boolean) -> Unit
    ) {
        if (updatedWiD == null) return

        Log.d(TAG, "updateWiD executed")

        val collectionDateAsString = updatedWiD.date.toString()
        val iD = updatedWiD.id

        val updatedTitle = updatedWiD.title.name
        val updatedStart = updatedWiD.start
        val updatedFinish = updatedWiD.finish
        val updatedDuration = updatedWiD.duration.seconds.toInt()

        val updatedWiDDocument = hashMapOf(
            TITLE to updatedTitle,
            START to updatedStart,
            FINISH to updatedFinish,
            DURATION to updatedDuration
        )

        firestore.collection(WID_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(collectionDateAsString)
            .document(iD)
            .set(updatedWiDDocument)
            .addOnSuccessListener {
                Log.d(TAG, "Document successfully updated")

                onWiDUpdated(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)

                /** 업데이트 실패 이유를 화면에 띄우면 좋을 듯? 예를 들어 인터넷이 안된다거나.. */
                onWiDUpdated(false)
            }
    }

    fun deleteWiD(
        email: String,
        wiD: WiD?,
        onWiDDeleted: (Boolean) -> Unit
    ) {
        // 위드가 비어 있으면 "잘못된 접근입니다." 알려주기?
        if (wiD == null) return

        Log.d(TAG, "deleteWiD executed")

        val collectionDateAsString = wiD.date.toString()
        val iD = wiD.id

        firestore.collection(WID_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(collectionDateAsString)
            .document(iD)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")

                onWiDDeleted(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)

                onWiDDeleted(false)
            }
    }
}