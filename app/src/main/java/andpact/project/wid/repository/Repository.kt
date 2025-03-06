package andpact.project.wid.repository

import andpact.project.wid.model.*
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val TAG = "Repository"
    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

    private val TMP_EMAIL = "andpact@gmail.com"

    private val USER_COLLECTION = "UserCollection"

    private val EMAIL = "email"
    private val SIGNED_UP_ON = "signedUpOn"
    val LEVEL = "level"
    val CURRENT_EXP = "currentExp"
    val WID_TOTAL_EXP = "wiDTotalExp"
    val WID_MIN_LIMIT = "wiDMinLimit"
    val WID_MAX_LIMIT = "wiDMaxLimit"

    private val DATA_COLLECTION = "DATA"
    private val WID_COLLECTION = "WiDCollection"

    private val ID = "id"
    private val TITLE = "title"
    private val SUB_TITLE = "subTitle"
    private val START = "start"
    private val FINISH = "finish"
    private val DURATION = "duration"
    val CITY = "city"
    private val EXP = "exp"
    private val TOOL = "tool"

//    fun createWiD(
//        email: String,
//        wiD: WiD,
//        updatedUserDocument: Map<String, Any>?,
//        onResult: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "createWiD executed")
//
//        val widId = wiD.id // WiD의 ID를 필드명으로 사용
//        val widDocument = wiD.toDocument() // WiD를 Firestore 문서 형식으로 변환 (이 과정에서 UTC 변환됨)
//
//        // UTC 적용 후의 start와 finish를 가져오기
//        val utcStart = (widDocument[START] as Timestamp).toDate().toInstant()
//        val utcFinish = (widDocument[FINISH] as Timestamp).toDate().toInstant()
//
//        // UTC 변환 후의 시간을 기준으로 추가적인 시간 조정 적용
//        val adjustedStartYear = utcStart.minus(14, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year // 시작 시간의 최대 차이
//        var adjustedFinishYear = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year // 종료 시간의 최대 차이
//
//        // UTC 변환 후 종료 시간이 정확히 다음 년도의 1/1 00:00:00 UTC라면 다음 년도에 저장할 필요 없음
//        val adjustedFinishTime = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC)
//        if (adjustedFinishTime.hour == 0 && adjustedFinishTime.minute == 0 && adjustedFinishTime.second == 0) {
//            adjustedFinishYear = utcFinish.atZone(ZoneOffset.UTC).year // 기존 utcFinish의 연도로 제한
//        }
//
//        val batch = firestore.batch() // 통신 전체 성공 or 실패
//
//        // 조정된 연도 범위 내 모든 연도에 문서 저장
//        for (year in adjustedStartYear..adjustedFinishYear) {
//            val yearRef = firestore
//                .collection(DATA_COLLECTION)
////                .document(email)
//                .document(TMP_EMAIL)
//                .collection(WID_COLLECTION)
//                .document(year.toString())
//
//            batch.set(yearRef, mapOf(widId to widDocument), SetOptions.merge())
//        }
//
//        if (updatedUserDocument != null) { // 플레이어로 생성했을 때만 유저 문서 갱신
//            val userRef = firestore.collection(USER_COLLECTION)
//                .document(email)
//            batch.set(userRef, updatedUserDocument, SetOptions.merge())
//        }
//
//        batch.commit()
//            .addOnSuccessListener {
//                onResult(true)
//            }
//            .addOnFailureListener {
//                onResult(false)
//            }
//    }
//
//    fun getWiD(
//        email: String,
//        year: Year,
//        onDateWiDListMapFetched: (Map<LocalDate, List<WiD>>) -> Unit
//    ) {
//        Log.d(TAG, "getWiD executed")
//
//        firestore.collection(DATA_COLLECTION)
////            .document(email)
//            .document(TMP_EMAIL)
//            .collection(WID_COLLECTION)
//            .document(year.toString())
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    val data = documentSnapshot.data ?: emptyMap()
//                    val dateWiDListMap = mutableMapOf<LocalDate, MutableList<WiD>>()
//
//                    data.forEach { (_, value) -> // Key가 WiD의 ID이므로 무시하고 Value만 처리
//                        try {
//                            val wiD = (value as? Map<String, Any>)?.toWiD() ?: return@forEach // 로컬로 변환
//
//                            val startDate = wiD.start.toLocalDate()
//                            val finishDate = wiD.finish.toLocalDate()
//
//                            // 조회 년도를 벗어난 기록은 무시함. (특정 로컬에서 이런 기록이 만들어질 수 있음)
//                            if (startDate.year != year.value && finishDate.year != year.value) {
//                                return@forEach
//                            }
//
//                            // 시작과 종료 날짜가 같으면 그대로 해당 날짜에 추가, 종료 시간이 자정이면 시작 날짜에만 추가
//                            if (startDate == finishDate || wiD.finish.toLocalTime() == LocalTime.MIDNIGHT) {
//                                dateWiDListMap.getOrPut(startDate) { mutableListOf() }.add(wiD)
//                            } else { // 시작과 종료 날짜가 다르면 각각의 날짜에 WiD를 추가 (자르지 않음)
//                                dateWiDListMap.getOrPut(startDate) { mutableListOf() }.add(wiD)
//                                dateWiDListMap.getOrPut(finishDate) { mutableListOf() }.add(wiD)
//                            }
//                        } catch (e: Exception) {
//                            Log.e(TAG, "Error parsing WiD data", e)
//                        }
//                    }
//
//                    dateWiDListMap.forEach { (_, wiDList) ->
//                        wiDList.sortBy { it.start }  // WiD의 시작 시간을 기준으로 정렬
//                    }
//
//                    onDateWiDListMapFetched(dateWiDListMap)
//                } else {
//                    onDateWiDListMapFetched(emptyMap())
//                }
//            }
//            .addOnFailureListener {
//                Log.e(TAG, "Failed to fetch WiD document", it)
//                onDateWiDListMapFetched(emptyMap())
//            }
//    }
//
//    fun updateWiD(
//        email: String,
//        wiD: WiD,
//        onResult: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "updateWiD executed")
//
//        val widId = wiD.id
//        val widDocument = wiD.toDocument()
//
//        val utcStart = (widDocument[START] as Timestamp).toDate().toInstant()
//        val utcFinish = (widDocument[FINISH] as Timestamp).toDate().toInstant()
//
//        // UTC 변환 후 최대로 조정한 연도 확인
//        val adjustedStartYear = utcStart.minus(14, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year // 시작 시간의 최대 차이
//        var adjustedFinishYear = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year // 종료 시간의 최대 차이
//
//        val adjustedFinishTime = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC)
//        if (adjustedFinishTime.hour == 0 && adjustedFinishTime.minute == 0 && adjustedFinishTime.second == 0) {
//            adjustedFinishYear = utcFinish.atZone(ZoneOffset.UTC).year // 기존 utcFinish의 연도로 제한
//        }
//
//        val batch = firestore.batch()
//
//        // 모든 해당 연도의 문서 업데이트
//        for (year in adjustedStartYear..adjustedFinishYear) {
//            val yearRef = firestore
//                .collection(DATA_COLLECTION)
//                .document(email)
//                .collection(WID_COLLECTION)
//                .document(year.toString())
//
//            batch.set(yearRef, mapOf(widId to widDocument), SetOptions.merge())
//        }
//
//        batch.commit()
//            .addOnSuccessListener {
//                onResult(true)
//            }
//            .addOnCanceledListener {
//                onResult(false)
//            }
//    }
//
//    fun deleteWiD(
//        email: String,
//        wiD: WiD,
//        updatedUserDocument: Map<String, Any>?, // 유저 문서 갱신 용
//        onResult: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "deleteWiD executed")
//
//        val widId = wiD.id
//
//        // WiD를 Firestore 문서 형식으로 변환 (이 과정에서 UTC 변환됨)
//        val widDocument = wiD.toDocument()
//
//        // UTC 적용 후의 start와 finish를 가져오기
//        val utcStart = (widDocument[START] as Timestamp).toDate().toInstant()
//        val utcFinish = (widDocument[FINISH] as Timestamp).toDate().toInstant()
//
//        // UTC 변환 후의 시간을 기준으로 최대로 조정한 연도 범위
//        val adjustedStartYear = utcStart.minus(14, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year
//        val adjustedFinishYear = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year
//
//        val batch = firestore.batch()
//
//        // 조정된 연도 범위 내 모든 연도에서 해당 WiD를 삭제
//        for (year in adjustedStartYear..adjustedFinishYear) {
//            val yearRef = firestore
//                .collection(DATA_COLLECTION)
////                .document(email)
//                .document(TMP_EMAIL)
//                .collection(WID_COLLECTION)
//                .document(year.toString())
//
//            batch.update(yearRef, mapOf(widId to FieldValue.delete())) // 특정 필드(WiD ID) 삭제
//        }
//
//        if (updatedUserDocument != null) {
//            val userRef = firestore
//                .collection(USER_COLLECTION)
//                .document(email)
//            batch.set(userRef, updatedUserDocument, SetOptions.merge())
//        }
//
//        batch.commit()
//            .addOnSuccessListener {
//                onResult(true)
//            }
//            .addOnFailureListener {
//                onResult(false)
//            }
//    }
//
//    fun getFirebaseUser(): FirebaseUser? {
//        val firebaseUser = auth.currentUser
//
//        Log.d(TAG, "getFirebaseUser called : $firebaseUser")
//
//        return firebaseUser
//    }
//
//    fun createUser(
//        email: String,
//        onUserCreated: (userCreated: User?) -> Unit
//    ) {
//        Log.d(TAG, "createUser executed")
//
//        val newUser = User.default().copy(email = email)
//        val newUserDocument = newUser.toDocument()
//
//        firestore.collection(USER_COLLECTION)
//            .add(newUserDocument)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with ID : ${documentReference.id}")
//
//                onUserCreated(newUser)
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//                onUserCreated(null)
//            }
//    }
//
//    fun getUser(
//        email: String,
//        onUserFetched: (userFetched: User?) -> Unit
//    ) {
//        Log.d(TAG, "getUser executed")
//
//        firestore.collection(USER_COLLECTION)
//            .whereEqualTo(EMAIL, email)
//            .get()
//            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
//                if (querySnapshot.isEmpty) { // 회원 가입 시 새로운 문서를 생성함.
//                    // TODO: 유저 문서 없을 때.??
//                    onUserFetched(null)
//                } else { // 문서가 존재할 때만 변환 작업을 수행합니다.
//                    val user = querySnapshot.documents.first().toUserOrNull()
//                    onUserFetched(user)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//                onUserFetched(null)
//            }
//    }
//
//    fun updateUserDocument(
//        email: String,
//        updatedUserDocument: Map<String, Any>,
//        onResult: (Boolean) -> Unit
//    ) {
//        firestore.collection(USER_COLLECTION)
//            .document(email)
//            .set(updatedUserDocument)
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully updated!")
//                onResult(true)
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error updating document", e)
//                onResult(false)
//            }
//    }
//
//    fun signOut() {
//        Log.d(TAG, "signOut executed")
//
//        auth.signOut()
//    }
//
//    private fun deleteFirebaseUser() {
//        Log.d(TAG, "deleteFirebaseUser executed")
//
//        val firebaseUser: FirebaseUser? = getFirebaseUser()
//
//        firebaseUser!!.delete() // 파이어베이스 유저 삭제
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "User account deleted.")
//                }
//            }
//    }
//
//    fun deleteUserAndDataDocument(
//        email: String,
//        onResult: (Boolean) -> Unit
//    ) {
//        Log.d(TAG, "deleteUserAndDataDocument executed")
//
//        val batch = firestore.batch()
//
//        val userRef = firestore.collection(USER_COLLECTION)
//            .document(email)
//        batch.delete(userRef)
//
//        val dataRef = firestore.collection(DATA_COLLECTION)
//            .document(email)
//        batch.delete(dataRef)
//
//        batch.commit()
//            .addOnSuccessListener {
//                onResult(true)
//                deleteFirebaseUser()
//            }  // 둘 다 삭제 성공
//            .addOnFailureListener { onResult(false) } // 하나라도 실패하면 전체 실패
//    }

    private fun WiD.toDocument(): Map<String, Any> {
//        val localStart = start.atZone(ZoneId.systemDefault()) // 로컬 시간 명시적으로 지정
//        val localFinish = finish.atZone(ZoneId.systemDefault()) // 로컬 시간 명시적으로 지정

        return mapOf(
            ID to id,
            TITLE to title.name,
            SUB_TITLE to subTitle.name,
            START to Timestamp(Date.from(start.toInstant(ZoneOffset.UTC))), // UTC 변환
            FINISH to Timestamp(Date.from(finish.toInstant(ZoneOffset.UTC))), // UTC 변환
            DURATION to duration.seconds,
            CITY to city.name,
            EXP to exp,
            TOOL to tool.name
        )
    }

    private fun Map<String, Any>.toWiD(): WiD {
        val startTimestamp = this[START] as? Timestamp
        val finishTimestamp = this[FINISH] as? Timestamp

        return WiD(
            id = this[ID] as String,
            title = Title.valueOf(this[TITLE] as String),
            subTitle = SubTitle.valueOf(this[SUB_TITLE] as String),
            start = startTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime() ?: LocalDateTime.MIN, // 로컬 시간으로 변환
            finish = finishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime() ?: LocalDateTime.MIN, // 로컬 시간으로 변환
//            start = startTimestamp?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime() ?: LocalDateTime.MIN, // UTC 그대로 유지
//            finish = finishTimestamp?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime() ?: LocalDateTime.MIN, // UTC 그대로 유지
            duration = Duration.ofSeconds(this[DURATION] as Long),
            city = City.valueOf(this[CITY] as String),
            exp = this[EXP] as Int,
            tool = Tool.valueOf(this[TOOL] as String)
        )
    }

    private fun User.toDocument(): Map<String, Any> { // User -> Map
        return mapOf(
            EMAIL to email,
            SIGNED_UP_ON to signedUpOn.toString(),
            CITY to city.toString(),
            LEVEL to level,
            CURRENT_EXP to currentExp,
            WID_TOTAL_EXP to wiDTotalExp,
            WID_MIN_LIMIT to wiDMinLimit.seconds, // Duration을 초 단위로 변환
            WID_MAX_LIMIT to wiDMaxLimit.seconds  // Duration을 초 단위로 변환
        )
    }

    private fun DocumentSnapshot.toUserOrNull(): User? {
        return try {
            User(
                email = getString(EMAIL) ?: return null, // 잘못된 접근
                signedUpOn = getString(SIGNED_UP_ON)?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                city = getString(CITY)?.let { City.valueOf(it) } ?: City.SEOUL,
                level = getLong(LEVEL)?.toInt() ?: 1,
                currentExp = getLong(CURRENT_EXP)?.toInt() ?: 0,
                wiDTotalExp = getLong(WID_TOTAL_EXP)?.toInt() ?: 0,
                wiDMinLimit = Duration.ofSeconds(getLong(WID_MIN_LIMIT) ?: 0), // 초 단위로 Duration 변환
                wiDMaxLimit = Duration.ofSeconds(getLong(WID_MAX_LIMIT) ?: 0)  // 초 단위로 Duration 변환
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting document to User", e)
            null
        }
    }
}