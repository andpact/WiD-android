package andpact.project.wid.repository

import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.Title
import andpact.project.wid.model.WiD
import andpact.project.wid.model.YearlyWiDListMap
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.*
import java.util.*
import javax.inject.Inject

/*
DATA 컬렉션 안에서 이메일로 문서를 선택한 후 WiD 컬렉션으로 들어감.
 */
class WiDRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    private val TAG = "WiDRepository"
    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

//    @Volatile
//    private var serverCallCount = 0

    private val TMP_EMAIL = "andpact@gmail.com"

    // 컬렉션
    private val DATA_COLLECTION = "DATA"
    private val WID_COLLECTION = "WiDCollection"

    // 문서 필드
    private val ID = "id"
    private val DATE = "date"
    private val TITLE = "title"
    private val START = "start"
    private val FINISH = "finish"
    private val DURATION = "duration"
    private val EXP = "exp"
    private val CREATED_BY = "createdBy"

    fun getYearlyWiDListMap(
        email: String,
        year: Year,
        onYearlyWiDListMapFetched: (yearlyWiDListMapFetched: YearlyWiDListMap) -> Unit,
    ) {
        Log.d(TAG, "getYearlyWiDListMap executed")

        firestore.collection(DATA_COLLECTION)
//            .document(email)
            .document(TMP_EMAIL)
            .collection(WID_COLLECTION)
            .document(year.toString())
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val yearlyWiDList = snapshot.data?.toYearlyWiDListMap() ?: YearlyWiDListMap.default()
                    Log.d(TAG, "Yearly document fetched successfully")
                    onYearlyWiDListMapFetched(yearlyWiDList)
                } else {
                    Log.d(TAG, "Yearly document does not exist. Creating a new one.")
                    onYearlyWiDListMapFetched(YearlyWiDListMap.default())
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to fetch yearly document: ${exception.message}")
            }
    }

    /** 연도 문서가 없으면 생성되도록 아니면 get 할 떄 생성되도록 하거나*/
    fun setYearlyWiDListMap(
        email: String,
        year: Year,
        dateWiDListMap: Map<LocalDate, List<WiD>>,
        onComplete: (Boolean) -> Unit
    ) {
        Log.d(TAG, "setYearlyWiDListMap executed")

        if (dateWiDListMap.isEmpty()) {
            onComplete(false)
            return
        }

        val operations = mutableMapOf<String, Any>() // Firebase에 적용할 업데이트 맵

        dateWiDListMap.forEach { (date, wiDList) ->
            val dateKey = date.toString()
            operations[dateKey] = wiDList.map { it.toDocument() } // WiD 리스트를 문서 형식으로 변환
        }

        firestore.collection(DATA_COLLECTION)
            .document(email)
            .collection(WID_COLLECTION)
            .document(year.toString())
            .set(operations, SetOptions.merge()) // 병합 업데이트
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    // **************************************** 유틸 메서드 ****************************************
    private fun WiD.toDocument(): Map<String, Any> {
        return mapOf(
            ID to id,
            DATE to date.toString(),
            TITLE to title.name,
            START to Timestamp(Date.from(start.atDate(date).atZone(ZoneId.systemDefault()).toInstant())),
            FINISH to Timestamp(Date.from(finish.atDate(date).atZone(ZoneId.systemDefault()).toInstant())),
            DURATION to duration.seconds.toInt(),
            EXP to exp,
            CREATED_BY to createdBy.name
        )
    }

    private fun Map<String, Any>.toWiD(): WiD {
        val startTimestamp = this[START] as? Timestamp
        val finishTimestamp = this[FINISH] as? Timestamp

        return WiD(
            id = this[ID] as String,
            date = LocalDate.parse(this[DATE] as String),
            title = Title.valueOf(this[TITLE] as String),
            start = startTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime() ?: LocalTime.MIDNIGHT,
            finish = finishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime() ?: LocalTime.MIDNIGHT,
            duration = Duration.ofSeconds(this[DURATION] as Long),
            exp = this[EXP] as Int,
            createdBy = CurrentTool.valueOf(this[CREATED_BY] as String)
        )
    }

    private fun Map<String, Any>.toYearlyWiDListMap(): YearlyWiDListMap {
        val wiDListMap = mutableMapOf<LocalDate, MutableList<WiD>>()

        this.keys.forEach { dateKey: String -> // 각 날짜 필드를 순회하여 WiD 객체를 수집
            val wiDList = (this[dateKey] as? List<Map<String, Any>>)?.map { it.toWiD() } ?: emptyList()

            val date = LocalDate.parse(dateKey) // 날짜 문자열을 LocalDate로 변환

            wiDListMap[date] = wiDList.toMutableList() // 날짜에 해당하는 WiD 리스트를 추가
        }

        return YearlyWiDListMap(wiDListMap = wiDListMap)
    }
}