package andpact.project.wid.repository

import andpact.project.wid.model.WiD
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

/**
 * 문서를 가져오는 작업은 비동기로 수행되므로, 콜백을 통해서 문서를 가져왔을 때, 해당 문서를 반환하고, 메서드가 종료되도록 해야 함.
 */
class WiDRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val TAG = "WiDRepository"
    private val COLLECTION = "WiDCollection"
    private val TMPEMAIL = "andpact@gmail.com"

    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

    fun createWiD(email: String, wid: WiD?, onCreateWiDSuccess: (String) -> Unit) {
        if (wid == null) return

        // 저장할 때 LocalDate -> String
        val date = wid.date.toString() // (wid.date.toString() -> "yyyy-MM-dd" 형식으로 생성됨.)
        val title = wid.title

        // 저장할 때 LocalTime -> Timestamp
//        val startTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneOffset.UTC).toInstant()))
//        val finishTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneOffset.UTC).toInstant()))

        val startTimestamp = Timestamp(Date.from(wid.start.atDate(wid.date).atZone(ZoneId.of("Asia/Seoul")).toInstant()))
        val finishTimestamp = Timestamp(Date.from(wid.finish.atDate(wid.date).atZone(ZoneId.of("Asia/Seoul")).toInstant()))

        // 저장할 때 Duration -> Int
        val duration = wid.duration.seconds

        // 문서 스타일로 변환해야 함.
        // id 필드는 제거하지말고, WiD 문서를 가져왔을 떄, 해당 문서의 자동 생성 ID를 할당해서 사용하자.
        val wiDDocument = hashMapOf(
            "id" to "0", // 문서의 필드에는 0을 할당함.
            "date" to date, // 가져올 때 String -> LocalDate
            "title" to title,
            "start" to startTimestamp, // 가져올 때 Timestamp -> LocalTime
            "finish" to finishTimestamp, // 가져올 때 Timestamp -> LocalTime
            "duration" to duration
        )

        firestore.collection(COLLECTION)
//            .document(email)
            .document(TMPEMAIL)
            .collection(date)
            .add(wiDDocument)
            .addOnSuccessListener { documentReference ->
                onCreateWiDSuccess(documentReference.id)

                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference.id")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
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

    fun readWiDListByDate(email: String, date: LocalDate, onReadWiDListByDateSuccess: (List<WiD>) -> Unit) {
        Log.d(TAG, "readWiDsByDate executed")

        val collectionDate = date.toString()

        val wiDs = mutableListOf<WiD>()

        firestore.collection(COLLECTION)
//            .document(email)
            .document(TMPEMAIL)
            .collection(collectionDate)
            .orderBy("start", Query.Direction.ASCENDING) // 시작 시간 기준으로 오름차순 정렬
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Success getting documents")

                for (document in result) {
                    val documentID = document.id
                    val documentDateString = document.getString("date")
                    val documentDate = LocalDate.parse(documentDateString)
                    val documentTitle = document.getString("title")!!
                    val documentStartTimestamp = document.getTimestamp("start")
                    val documentStart = documentStartTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                    val documentFinishTimestamp = document.getTimestamp("finish")
                    val documentFinish = documentFinishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                    val documentDurationLong = document.getLong("duration")!!
                    val documentDuration = Duration.ofSeconds(documentDurationLong)

                    val wiD = WiD(
                        id = documentID,
                        date = documentDate,
                        title = documentTitle,
                        start = documentStart,
                        finish = documentFinish,
                        duration = documentDuration
                    )

                    wiDs.add(wiD)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }

                onReadWiDListByDateSuccess(wiDs) // 완성된 리스트를 콜백으로 반환
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                onReadWiDListByDateSuccess(emptyList()) // 실패할 경우 빈 리스트를 콜백으로 반환
            }
    }


    // 날짜(First -> Last)를 사용해서 컬렉션을 조회하고, 해당 컬렉션 내에 WiD 문서를 시작 시간 기준으로 오름차순 정렬한 후에, 리스트로 만들어서 반환함.
    fun readWiDListFromFirstDateToLastDate(email: String, firstDate: LocalDate, lastDate: LocalDate, callback: (List<WiD>) -> Unit) {
        val wiDs = mutableListOf<WiD>()

        var currentDate = firstDate
        while (currentDate <= lastDate) {
            val collectionDate = currentDate.toString()

            firestore.collection(COLLECTION)
//                .document(email)
                .document(TMPEMAIL)
                .collection(collectionDate)
                .orderBy("start", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val documentID = document.id
                        val documentDateString = document.getString("date")
                        val documentDate = LocalDate.parse(documentDateString)
                        val documentTitle = document.getString("title")!!
                        val documentStartTimestamp = document.getTimestamp("start")
                        val documentStart = documentStartTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                        val documentFinishTimestamp = document.getTimestamp("finish")
                        val documentFinish = documentFinishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!
                        val documentDurationLong = document.getLong("duration")!!
                        val documentDuration = Duration.ofSeconds(documentDurationLong)

                        val wiD = WiD(
                            id = documentID,
                            date = documentDate,
                            title = documentTitle,
                            start = documentStart,
                            finish = documentFinish,
                            duration = documentDuration
                        )

                        wiDs.add(wiD)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }

            currentDate = currentDate.plusDays(1) // 다음 날짜로 이동
        }

        callback(wiDs)
    }

    // update - 기존 문서의 일부 필드만 업데이트하기 위해 사용
    fun updateWiD(email: String, updatedWiD: WiD?, onUpdateWiDSuccess: () -> Unit) {
        if (updatedWiD == null) return

        firestore.collection(COLLECTION)
//            .document(email)
            .document(TMPEMAIL)
            .collection(updatedWiD.date.toString())
            .document(updatedWiD.id)
            .update(
//                "date", updatedWiD.date,
                "title", updatedWiD.title,
                "start", updatedWiD.start,
                "finish", updatedWiD.finish,
                "duration", updatedWiD.duration.seconds
            )
            .addOnSuccessListener {
//                Log.d(TAG, "Document $widId successfully updated")

                onUpdateWiDSuccess()
            }
            .addOnFailureListener { e ->
//                Log.w(TAG, "Error updating document $widId", e)
            }
    }

    fun deleteWiD(email: String, clickedWiD: WiD?, onDeleteWiDSuccess: () -> Unit) {
        if (clickedWiD == null) return

        firestore.collection(COLLECTION)
//            .document(email)
            .document(TMPEMAIL)
            .collection(clickedWiD.date.toString())
            .document(clickedWiD.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")

                onDeleteWiDSuccess()
            }
            .addOnFailureListener {
                e -> Log.w(TAG, "Error deleting document", e)
            }
    }
}