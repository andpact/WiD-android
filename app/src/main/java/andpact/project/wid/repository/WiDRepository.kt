package andpact.project.wid.repository

import andpact.project.wid.model.*
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

/*
DATA 컬렉션 안에서 이메일로 문서를 선택한 후 WiD 컬렉션으로 들어감.
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
    private val ID = "id"
    private val TITLE = "title"
    private val SUB_TITLE = "subTitle"
    val START = "start"
    val FINISH = "finish"
    private val DURATION = "duration"
    private val CITY = "city"
    private val EXP = "exp"
    private val TOOL = "tool"

    fun createWiD(
        email: String,
        wiD: WiD,
        onResult: (SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        val widId = wiD.id // WiD의 ID를 필드명으로 사용
        val widDocument = wiD.toDocument() // WiD를 Firestore 문서 형식으로 변환 (이 과정에서 UTC 변환됨)

        // UTC 적용 후의 start와 finish를 가져오기
        val utcStart = (widDocument[START] as Timestamp).toDate().toInstant()
        val utcFinish = (widDocument[FINISH] as Timestamp).toDate().toInstant()

        // UTC 변환 후의 시간을 기준으로 최대로 조정한 연도 범위
        val adjustedStartYear = utcStart.minus(14, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year
        val adjustedFinishYear = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year

        val batch = firestore.batch()

        // 조정된 연도 범위 내 모든 연도에 문서 저장
        for (year in adjustedStartYear..adjustedFinishYear) {
            val yearRef = firestore.collection(DATA_COLLECTION)
//                .document(email)
                .document(TMP_EMAIL)
                .collection(WID_COLLECTION)
                .document(year.toString())

            batch.set(yearRef, mapOf(widId to widDocument), SetOptions.merge())
        }

        batch.commit()
            .addOnSuccessListener {
                onResult(SnackbarActionResult.SUCCESS_CREATE_WID)
            }
            .addOnFailureListener {
                onResult(SnackbarActionResult.FAIL_SERVER_ERROR)
            }
    }

    fun getWiD(
        email: String,
        year: Year,
        onResult: (SnackbarActionResult) -> Unit,
        onDateWiDListMapFetched: (Map<LocalDate, List<WiD>>) -> Unit
    ) {
        Log.d(TAG, "getWiD executed")

        firestore.collection(DATA_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(WID_COLLECTION)
            .document(year.toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data ?: emptyMap()
                    val dateWiDListMap = mutableMapOf<LocalDate, MutableList<WiD>>()

                    data.forEach { (_, value) -> // Key가 WiD의 ID이므로 무시하고 Value만 처리
                        try {
                            val wiD = (value as? Map<String, Any>)?.toWiD() ?: return@forEach // 로컬로 변환

                            // TODO: 불러온 연도와 다른 연도의 기록은 클라에 추가하지 않아야 함. 
                            
                            // WiD의 start와 finish 날짜 가져오기
                            val startDate = wiD.start.toLocalDate()
                            val finishDate = wiD.finish.toLocalDate()

                            if (startDate == finishDate) {
                                // 시작과 종료 날짜가 같으면 그대로 해당 날짜에 추가
                                dateWiDListMap.getOrPut(startDate) { mutableListOf() }.add(wiD)
                            } else {
                                // 시작과 종료 날짜가 다르면 각각의 날짜에 WiD를 추가 (자르지 않음)
                                dateWiDListMap.getOrPut(startDate) { mutableListOf() }.add(wiD)
                                dateWiDListMap.getOrPut(finishDate) { mutableListOf() }.add(wiD)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing WiD data", e)
                        }
                    }

                    onDateWiDListMapFetched(dateWiDListMap)
//                    onResult(SnackbarActionResult.SUCCESS_FETCH_WID)
                } else {
//                    onDateWiDListMapFetched(emptyMap())
                    onDateWiDListMapFetched(emptyMap())
//                    onResult(SnackbarActionResult.FAIL_NO_DATA)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to fetch WiD document", it)
                onDateWiDListMapFetched(emptyMap())
                onResult(SnackbarActionResult.FAIL_SERVER_ERROR)
            }
    }

    fun updateWiD(
        email: String,
        wiD: WiD,
        onResult: (SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        val widId = wiD.id
        val widDocument = wiD.toDocument()

        // UTC 변환 후 최대로 조정한 연도 확인
        val adjustedStartYear = wiD.start.minusHours(14).year // UTC-12 고려
        val adjustedFinishYear = wiD.finish.plusHours(12).year // UTC+14 고려

        val batch = firestore.batch()

        // 모든 해당 연도의 문서 업데이트
        for (year in adjustedStartYear..adjustedFinishYear) {
            val yearRef = firestore.collection(DATA_COLLECTION)
                .document(email)
                .collection(WID_COLLECTION)
                .document(year.toString())

            batch.set(yearRef, mapOf(widId to widDocument), SetOptions.merge())
        }

        batch.commit()
            .addOnSuccessListener {
                onResult(SnackbarActionResult.SUCCESS_UPDATE_WID)
            }
            .addOnFailureListener {
                onResult(SnackbarActionResult.FAIL_SERVER_ERROR)
            }
    }

    fun deleteWiD(
        email: String,
        wiD: WiD,
        onResult: (snackbarActionResult: SnackbarActionResult) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        val widId = wiD.id

        // WiD를 Firestore 문서 형식으로 변환 (이 과정에서 UTC 변환됨)
        val widDocument = wiD.toDocument()

        // UTC 적용 후의 start와 finish를 가져오기
        val utcStart = (widDocument[START] as Timestamp).toDate().toInstant()
        val utcFinish = (widDocument[FINISH] as Timestamp).toDate().toInstant()

        // UTC 변환 후의 시간을 기준으로 최대로 조정한 연도 범위
        val adjustedStartYear = utcStart.minus(14, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year
        val adjustedFinishYear = utcFinish.plus(12, ChronoUnit.HOURS).atZone(ZoneOffset.UTC).year

        val batch = firestore.batch()

        // 조정된 연도 범위 내 모든 연도에서 해당 WiD를 삭제
        for (year in adjustedStartYear..adjustedFinishYear) {
            val yearRef = firestore.collection(DATA_COLLECTION)
//                .document(email)
                .document(TMP_EMAIL)
                .collection(WID_COLLECTION)
                .document(year.toString())

            batch.update(yearRef, mapOf(widId to FieldValue.delete())) // 특정 필드(WiD ID) 삭제
        }

        batch.commit()
            .addOnSuccessListener {
                onResult(SnackbarActionResult.SUCCESS_DELETE_WID)
            }
            .addOnFailureListener {
                onResult(SnackbarActionResult.FAIL_SERVER_ERROR)
            }
    }

    // **************************************** 유틸 메서드 ****************************************

    // TODO: 로컬에서 시간을 특정할 수 없어 보이는데? UTC 변환이 제대로 이루어지는지?
    private fun WiD.toDocument(): Map<String, Any> {
        return mapOf(
            ID to id,
            TITLE to title.name,
            SUB_TITLE to subTitle.name,
            START to Timestamp(Date.from(start.toInstant(ZoneOffset.UTC))), // UTC 변환
            FINISH to Timestamp(Date.from(finish.toInstant(ZoneOffset.UTC))), // UTC 변환
//            START to Timestamp(Date.from(start.atZone(ZoneId.systemDefault()).toInstant())), // 로컬 시간 변환
//            FINISH to Timestamp(Date.from(finish.atZone(ZoneId.systemDefault()).toInstant())), // 로컬 시간 변환
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
}