package andpact.project.wid.repository

import andpact.project.wid.model.WiD
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

    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

//    private var wiDCollectionListenerRegistrationOfDate: ListenerRegistration? = null
//    private val wiDCollectionListenerRegistrationList = mutableListOf<ListenerRegistration>()
//    private val wiDCollectionListenerRegistrationMap = mutableMapOf<LocalDate, ListenerRegistration>()


//    fun addSnapshotListenerToWiDCollectionByDate(
//        email: String,
//        collectionDate: LocalDate,
//        onWiDCollectionChanged: (List<WiD>) -> Unit
//    ) {
//        Log.d(TAG, "addSnapshotListenerToWiDCollectionByDate executed")
//
//        wiDCollectionListenerRegistrationOfDate?.remove()
//
//        val collectionDateAsString = collectionDate.toString()
//
//        wiDCollectionListenerRegistrationOfDate = firestore.collection(WID_COLLECTION)
////            .document(email)
//            .document(TMP_EMAIL)
//            .collection(collectionDateAsString)
//            .orderBy(START, Query.Direction.ASCENDING)
//            .addSnapshotListener { querySnapshot, e ->
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                if (querySnapshot != null && !querySnapshot.isEmpty) {
//                    val wiDList = mutableListOf<WiD>()
//
//                    for (document in querySnapshot) {
//                        val iD = document.id
//                        val dateAsString = document.getString(DATE)
//                        val date = LocalDate.parse(dateAsString)
//                        val title = document.getString(TITLE)!!
//                        val startAsTimestamp = document.getTimestamp(START)
//                        val start = startAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                        val finishAsTimestamp = document.getTimestamp(FINISH)
//                        val finish = finishAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                        val durationAsLong = document.getLong(DURATION)!!
//                        val duration = Duration.ofSeconds(durationAsLong)
//
//                        val wiD = WiD(
//                            id = iD,
//                            date = date,
//                            title = title,
//                            start = start,
//                            finish = finish,
//                            duration = duration
//                        )
//
//                        wiDList.add(wiD)
//                    }
//
//                    onWiDCollectionChanged(wiDList)
//                } else {
//                    onWiDCollectionChanged(emptyList()) // 결과가 없으면 빈 리스트 반환
//                }
//            }
//    }

//    fun addSnapshotListenerToWiDCollectionFromFirstDateToLastDate(
//        email: String,
//        collectionFirstDate: LocalDate,
//        collectionLastDate: LocalDate,
//        onWiDCollectionChanged: (List<WiD>) -> Unit
//    ) {
//        Log.d(TAG, "addSnapshotListenerToWiDCollectionFromFirstDateToLastDate executed")
//
//        // 기존에 등록된 리스너가 있으면 모두 제거
//        wiDCollectionListenerRegistrationList.forEach { it.remove() }
//        wiDCollectionListenerRegistrationList.clear()
//
//        // 날짜 범위 내 각 날짜에 대한 리스너 등록
//        var currentDate = collectionFirstDate
//        val wiDListFromFirstDateToLastDate = mutableListOf<WiD>()
//
//        while (!currentDate.isAfter(collectionLastDate)) {
//            val collectionDateAsString = currentDate.toString()
//
//            val listenerRegistration = firestore.collection(WID_COLLECTION)
////                .document(email)
//                .document(TMP_EMAIL)
//                .collection(collectionDateAsString)
//                .orderBy(START, Query.Direction.ASCENDING)
//                .addSnapshotListener { querySnapshot, e ->
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed for date: $collectionDateAsString", e)
//                        return@addSnapshotListener
//                    }
//
//                    if (querySnapshot != null && !querySnapshot.isEmpty) {
//                        val wiDList = mutableListOf<WiD>()
//
//                        for (document in querySnapshot) {
//                            val iD = document.id
//                            val dateAsString = document.getString(DATE)
//                            val date = LocalDate.parse(dateAsString)
//                            val title = document.getString(TITLE)!!
//                            val startAsTimestamp = document.getTimestamp(START)
//                            val start = startAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                            val finishAsTimestamp = document.getTimestamp(FINISH)
//                            val finish = finishAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                            val durationAsLong = document.getLong(DURATION)!!
//                            val duration = Duration.ofSeconds(durationAsLong)
//
//                            val wiD = WiD(
//                                id = iD,
//                                date = date,
//                                title = title,
//                                start = start,
//                                finish = finish,
//                                duration = duration
//                            )
//
//                            wiDList.add(wiD)
//                        }
//
//                        wiDListFromFirstDateToLastDate.addAll(wiDList)
//                        onWiDCollectionChanged(wiDListFromFirstDateToLastDate)
//                    } else {
//                        onWiDCollectionChanged(wiDListFromFirstDateToLastDate)
//                    }
//                }
//
//            wiDCollectionListenerRegistrationList.add(listenerRegistration)
//
//            currentDate = currentDate.plusDays(1)
//        }
//    }

    fun createWiD(
        email: String,
        wid: WiD?,
        onWiDCreated: (String, Boolean) -> Unit
    ) {
        if (wid == null) return

        Log.d(TAG, "createWiD executed")

        val dateAsString = wid.date.toString()
        val title = wid.title

//        val startTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneOffset.UTC).toInstant()))
//        val finishTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneOffset.UTC).toInstant()))

        // 아래 둘 중 뭐가 맞음?
//        val startTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneId.of("Asia/Seoul")).toInstant()))
//        val finishTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneId.of("Asia/Seoul")).toInstant()))

        val startAsTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneId.systemDefault()).toInstant()))
        val finishAsTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneId.systemDefault()).toInstant()))

        val durationAsInt = wid.duration.seconds.toInt()

        // id 필드는 제거하지말고, WiD 문서를 가져왔을 떄, 해당 문서의 자동 생성 ID를 할당해서 사용하자.
        val newWiDDocument = hashMapOf(
            ID to "0", // 문서의 필드에는 0을 할당함.
            DATE to dateAsString,
            TITLE to title,
            START to startAsTimestamp,
            FINISH to finishAsTimestamp,
            DURATION to durationAsInt
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

    // 컬렉션 때문에 날짜를 파라미터로 전달해야 함.
//    fun readWiDByID(date: LocalDate, documentID: String): WiD? {
//        var wid: WiD? = null
//
//        db.collection(TAG)
//            .document(auth.uid!!)
//            .collection(date.toString())
//            .document(documentID)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    wid = document.toObject(WiD::class.java)
//                } else {
//                    Log.d(TAG, "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }
//
//        // 위의 비동기 작업이 완료될 때까지 기다리는 동안 null을 반환합니다.
//        return wid
//    }

//    fun readWiDsByDate(date: LocalDate): List<WiD> {
//        Log.d(TAG, "readWiDsByDate executed")
//
//        val uid = auth.uid!!
//        val collectionDate = date.toString()
//
//        val wiDs = mutableListOf<WiD>()
//
////        db.collection(TAG)
//        val result = db.collection("wids")
//            .document(uid)
//            .collection(collectionDate)
//            .orderBy("start", Query.Direction.ASCENDING) // 시작 시간 기준으로 오름차순 정렬
//            .get()
//            .addOnSuccessListener { result ->
//                Log.d(TAG, "Success getting documents")
//
//                for (document in result) {
////                    val wiD = document.toObject(WiD::class.java)
//
//                    // 아래와 같이 직접 가져와서 WiD에 맞게 타입 변환하기 ??
//                    val documentID = document.getLong("id")!!
//
//                    val documentDateString = document.getString("date")
//                    val documentDate = LocalDate.parse(documentDateString)
//
//                    val documentTitle = document.getString("title")!!
//
//                    val documentStartTimestamp = document.getTimestamp("start")
//                    val documentStart = documentStartTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//
//                    val documentFinishTimestamp = document.getTimestamp("finish")
//                    val documentFinish = documentFinishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//
//                    val documentDurationLong = document.getLong("duration")!!
//                    val documentDuration = Duration.ofSeconds(documentDurationLong)
//
//                    val wiD = WiD(
//                        id = documentID,
//                        date = documentDate,
//                        title = documentTitle,
//                        start = documentStart,
//                        finish = documentFinish,
//                        duration = documentDuration
//                    )
//
//                    wiDs.add(wiD)
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
//            }
//
////            .await()
////
////        for (document in result) {
////            val wiD = document.toObject(WiD::class.java)
////            wiDs.add(wiD)
////            Log.d(TAG, "${document.id} => ${document.data}")
////        }
//
//        Log.d(TAG, "readWiDsByDate return : $wiDs")
//
//        return wiDs
//    }

    fun getWiDListByDate(
        email: String,
        collectionDate: LocalDate,
        onWiDListFetchedByDate: (List<WiD>) -> Unit
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
                    val startAsTimestamp = document.getTimestamp(START)
                    val start = startAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                    val finishAsTimestamp = document.getTimestamp(FINISH)
                    val finish = finishAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                    val durationAsLong = document.getLong(DURATION)!!
                    val duration = Duration.ofSeconds(durationAsLong)

                    val wiD = WiD(
                        id = iD,
                        date = date,
                        title = title,
                        start = start,
                        finish = finish,
                        duration = duration
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

    // 날짜(First -> Last)를 사용해서 컬렉션을 조회하고, 해당 컬렉션 내에 WiD 문서를 시작 시간 기준으로 오름차순 정렬한 후에, 리스트로 만들어서 반환함.
//    fun readWiDListFromFirstDateToLastDate(email: String, firstDate: LocalDate, lastDate: LocalDate, callback: (List<WiD>) -> Unit) {
//        val wiDs = mutableListOf<WiD>()
//
//        var currentDate = firstDate
//        while (currentDate <= lastDate) {
//            val collectionDate = currentDate.toString()
//
//            firestore.collection(COLLECTION)
////                .document(email)
//                .document(TMPEMAIL)
//                .collection(collectionDate)
//                .orderBy("start", Query.Direction.ASCENDING)
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        val documentID = document.id
//                        val documentDateString = document.getString("date")
//                        val documentDate = LocalDate.parse(documentDateString)
//                        val documentTitle = document.getString("title")!!
//                        val documentStartTimestamp = document.getTimestamp("start")
//                        val documentStart = documentStartTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                        val documentFinishTimestamp = document.getTimestamp("finish")
//                        val documentFinish = documentFinishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
//                        val documentDurationLong = document.getLong("duration")!!
//                        val documentDuration = Duration.ofSeconds(documentDurationLong)
//
//                        val wiD = WiD(
//                            id = documentID,
//                            date = documentDate,
//                            title = documentTitle,
//                            start = documentStart,
//                            finish = documentFinish,
//                            duration = documentDuration
//                        )
//
//                        wiDs.add(wiD)
//                        Log.d(TAG, "${document.id} => ${document.data}")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d(TAG, "Error getting documents: ", exception)
//                }
//
//            currentDate = currentDate.plusDays(1) // 다음 날짜로 이동
//        }
//
//        callback(wiDs)
//    }

    fun updateWiD(
        email: String,
        updatedWiD: WiD?,
        onWiDUpdated: (Boolean) -> Unit
    ) {
        if (updatedWiD == null) return

        Log.d(TAG, "updateWiD executed")

        val collectionDateAsString = updatedWiD.date.toString()
        val iD = updatedWiD.id

        val updatedTitle = updatedWiD.title
        val updatedStart = updatedWiD.start
        val updatedFinish = updatedWiD.finish
        val updatedDuration = updatedWiD.duration.seconds.toInt()

        val updatedWiD = hashMapOf(
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
            .set(updatedWiD)
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