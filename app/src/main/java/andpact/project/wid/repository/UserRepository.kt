package andpact.project.wid.repository

import andpact.project.wid.model.User
import andpact.project.wid.model.WiD
import andpact.project.wid.util.*
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

// 파이어 베이스 인증 관련 기능을 담당하는 클래스
// FirebaseUser는 FirebaseAuth 객체에 묶여 있다고 보면 됨.
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val TAG = "UserRepository"
    private val USER_COLLECTION = "UserCollection"
    private val WID_COLLECTION = "WiDCollection"

    private val EMAIL = "email"
    private val SIGNED_UP_ON = "signedUpOn"

    private val CURRENT_TITLE = "currentTitle"
    private val CURRENT_TOOL = "currentTool"
    private val CURRENT_TOOL_STATE = "currentToolState"
    private val STOPWATCH_START_TIME = "stopwatchStartTime"
    private val STOPWATCH_PREV_DURATION = "stopwatchPrevDuration"
    private val TIMER_START_TIME = "timerStartTime"
    private val TIMER_NEXT_SELECTED_TIME = "timerNextSelectedTime"

    private val LEVEL = "level"
    private val LEVEL_UP_HISTORY_MAP = "levelUpHistoryMap"
    private val CURRENT_EXP = "currentExp"
    private val TOTAL_EXP = "totalExp"
    private val WID_TOTAL_EXP = "wiDTotalExp"

    private val TITLE_COUNT_MAP = "titleCountMap"
    private val TITLE_DURATION_MAP = "titleDurationMap"

    init { Log.d(TAG, "created") }
    protected fun finalize() { Log.d(TAG, "destroyed") }

    private val actionCodeSettings = ActionCodeSettings
        .newBuilder()
        .setHandleCodeInApp(true)
//        .setUrl("https://whatidid.page.link/welcometowid")
        .setUrl("https://thewid.page.link/welcometowid")
//        .setDynamicLinkDomain("") // 프로젝트에 여러 동적 링크 중 하나를 지정해서 사용하고 싶을 때.
        .setIOSBundleId("andpact.project.WiD")
        .setAndroidPackageName(
            "andpact.project.wid",
            true,
            null
        )
        .build()

    fun sendAuthenticationLinkToEmail(
        email: String,
        onAuthenticationLinkSentToEmail: (Boolean) -> Unit
    ) {
        Log.d(TAG, "sendAuthenticationLinkToEmail executed")

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")

                    onAuthenticationLinkSentToEmail(true)
                } else {
                    Log.d(TAG, "Email sent: failure.")

                    onAuthenticationLinkSentToEmail(false)
                }
            }
    }

    /**
     * 동적 링크 감지 메서드
     * 계정 없을 때 동적 링크 감지 -> 회원 가입 완료(User Document 생성되도록 해야함)
     * 계정 있을 때 동적 링크 감지 -> 로그인 완료
     * 따라서 회원 가입과 로그인 링크 감지 메서드를 따로 만들어야 함.
     * 동적 링크를 어떻게 회원 가입 용인지, 로그인 용인지 구분할거?
     */
    fun verifyAuthenticationLink(
        email: String,
        dynamicLink: String?,
        onAuthenticationLinkVerified: (Boolean) -> Unit
    ) {
        Log.d(TAG, "verifyAuthenticationLink executed")

        if (!dynamicLink.isNullOrEmpty() && auth.isSignInWithEmailLink(dynamicLink)) { // 동적 링크 감지
            signInWithEmailLink(
                email = email,
                dynamicLink = dynamicLink,
                onSignedInWithEmailLink = { signedInWithEmailLink: Boolean ->
                    onAuthenticationLinkVerified(signedInWithEmailLink)
                }
            )
        } else {
            Log.d(TAG, "isSignInWithEmailLink: failure")

            onAuthenticationLinkVerified(false)
        }
    }

    private fun signInWithEmailLink(
        email: String,
        dynamicLink: String,
        onSignedInWithEmailLink: (Boolean) -> Unit
    ) {
        auth.signInWithEmailLink(email, dynamicLink)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully signed in with email link!")

                    onSignedInWithEmailLink(true)
                } else {
                    Log.e(TAG, "Error signing in with email link", task.exception)

                    onSignedInWithEmailLink(false)
                }
            }
    }

    fun getFirebaseUser(): FirebaseUser? {
        val firebaseUser = auth.currentUser

        Log.d(TAG, "getFirebaseUser called : $firebaseUser")

        return firebaseUser
    }

    /**
     * 이메일 링크 인증 후,
     * 무조건 유저 문서를 가져옴.
     * 회원 가입(첫 링크 인증)이면 유저 문서를 새로 생성하고,
     * 로그인이면 기존 유저 무서를 가져옴.
     */
    fun getUser(
        email: String,
        onUserFetched: (User?) -> Unit
    ) {
        Log.d(TAG, "getUser executed")

        firestore.collection(USER_COLLECTION)
            .whereEqualTo(EMAIL, email)
            .get()
            .addOnSuccessListener { documents ->
                // 회원 가입 시 새로운 문서를 생성함.
                if (documents.isEmpty) {
                    createUser(
                        email = email,
                        onUserCreated = { user: User? ->
                            onUserFetched(user)
                        }
                    )
                } else { // 문서가 존재할 때만 변환 작업을 수행합니다.
                    getExistingUser(
                        documentSnapshot = documents.documents[0],
                        onUserFetched = { user: User? ->
                            onUserFetched(user)
                        }
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)

                onUserFetched(null)
            }
    }

//    fun addSnapshotListenerToUserDocument(
//        email: String,
//        onUserDocumentChanged: (User?) -> Unit
//    ) {
//        Log.d(TAG, "addSnapshotListenerToUserDocument executed")
//
//        firestore.collection(USER_COLLECTION)
//            .document(email)
//            .addSnapshotListener { documentSnapshot, e ->
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    getExistingUser(
//                        documentSnapshot = documentSnapshot,
//                        onUserFetched = { user: User? ->
//                            onUserDocumentChanged(user)
//                        }
//                    )
//                } else {
//                    onUserDocumentChanged(null)
//                }
//            }
//    }

    private fun createUser(
        email: String,
        onUserCreated: (User?) -> Unit
    ) {
        Log.d(TAG, "createUser executed")

        val today = LocalDate.now()
        val todayAsString = today.toString()

        val defaultCurrentTitle = titleNumberStringList[0]
        val defaultCurrentToolAsString = CurrentTool.NONE.name
        val defaultCurrentToolStateAsString = CurrentToolState.STOPPED.name

        val defaultStopwatchStartTimeAsTimestamp = Timestamp(Date.from(LocalTime.MIN.atDate(LocalDate.MIN).atZone(ZoneId.systemDefault()).toInstant()))
        val defaultStopwatchPrevDurationAsInt = 0
        val defaultTimerStartTimeAsTimestamp = Timestamp(Date.from(LocalTime.MIN.atDate(LocalDate.MIN).atZone(ZoneId.systemDefault()).toInstant()))
        val defaultTimerSelectedTimesAsInt = 0

        val defaultLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(defaultLevelUpHistoryMap)
        val defaultTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(defaultTitleNumberStringToTitleDurationMap)

        val newUserDocument = hashMapOf(
            EMAIL to email,
            SIGNED_UP_ON to todayAsString,

            CURRENT_TITLE to defaultCurrentTitle,
            CURRENT_TOOL to defaultCurrentToolAsString,
            CURRENT_TOOL_STATE to defaultCurrentToolStateAsString,
            STOPWATCH_START_TIME to defaultStopwatchStartTimeAsTimestamp,
            STOPWATCH_PREV_DURATION to defaultStopwatchPrevDurationAsInt,
            TIMER_START_TIME to defaultTimerStartTimeAsTimestamp,
            TIMER_NEXT_SELECTED_TIME to defaultTimerSelectedTimesAsInt,

            LEVEL to 1,
            LEVEL_UP_HISTORY_MAP to defaultLevelUpHistoryMapAsString,
            CURRENT_EXP to 0,
            TOTAL_EXP to 0,
            WID_TOTAL_EXP to 0,

            TITLE_COUNT_MAP to defaultTitleNumberStringToTitleCountMap,
            TITLE_DURATION_MAP to defaultTitleDurationMapAsInt
        )

        firestore.collection(USER_COLLECTION)
            .add(newUserDocument)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID : ${documentReference.id}")

                val newUser = User(
                    email = email,
                    signedUpOn = today,

                    currentTitle = defaultCurrentTitle,
                    currentTool = CurrentTool.NONE,
                    currentToolState = CurrentToolState.STOPPED,
                    stopwatchStartTime = LocalTime.MIN,
                    stopwatchPrevDuration = Duration.ZERO,
                    timerStartTime = LocalTime.MIN,
                    timerNextSelectedTime = Duration.ZERO,

                    level = 1,
                    levelUpHistoryMap = defaultLevelUpHistoryMap,
                    currentExp = 0,
                    totalExp = 0,
                    wiDTotalExp = 0,

                    titleCountMap = defaultTitleNumberStringToTitleCountMap,
                    titleDurationMap = defaultTitleNumberStringToTitleDurationMap
                )

                onUserCreated(newUser)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                onUserCreated(null)
            }
    }

    private fun getExistingUser(
        documentSnapshot: DocumentSnapshot,
        onUserFetched: (User?) -> Unit
    ) {
        Log.d(TAG, "getExistingUser executed")

        val userEmail = documentSnapshot.getString(EMAIL) ?: ""

        val signedUpOn = LocalDate.parse(documentSnapshot.getString(SIGNED_UP_ON))

        val currentTitle = documentSnapshot.getString(CURRENT_TITLE) ?: "0"

        val currentTool = CurrentTool.valueOf(documentSnapshot.getString(CURRENT_TOOL) ?: "NONE")
        val currentToolState = CurrentToolState.valueOf(documentSnapshot.getString(CURRENT_TOOL_STATE) ?: "STOPPED")

        val stopwatchStartTimeAsTimestamp = documentSnapshot.getTimestamp(STOPWATCH_START_TIME)
        val stopwatchStartTimeAsLocalTime = stopwatchStartTimeAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!

        val stopwatchPrevDurationAsLong = documentSnapshot.getLong(STOPWATCH_PREV_DURATION) ?: 0L
        val stopwatchPrevDurationAsDuration = Duration.ofSeconds(stopwatchPrevDurationAsLong)

        val timerStartTimeAsTimestamp = documentSnapshot.getTimestamp(TIMER_START_TIME)
        val timerStartTimeAsLocalTime = timerStartTimeAsTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()!!

        val timerNextSelectedTimeAsLong = documentSnapshot.getLong(TIMER_NEXT_SELECTED_TIME) ?: 0L
        val timerNextSelectedTimeAsDuration = Duration.ofSeconds(timerNextSelectedTimeAsLong)

        val level = documentSnapshot.getLong(LEVEL)?.toInt() ?: 1
        val levelUpHistoryMapAsString = documentSnapshot.get(LEVEL_UP_HISTORY_MAP) as? HashMap<String, String> ?: convertLevelUpHistoryMapToString(defaultLevelUpHistoryMap)
        val levelUpHistoryMapAsLocalDate = convertLevelUpHistoryMapToLocalDate(levelUpHistoryMapAsString)

        val currentExp = documentSnapshot.getLong(CURRENT_EXP)?.toInt() ?: 0
        val totalExp = documentSnapshot.getLong(TOTAL_EXP)?.toInt() ?: 0
        val wiDTotalExp = documentSnapshot.getLong(WID_TOTAL_EXP)?.toInt() ?: 0

        val titleCountMap = documentSnapshot.get(TITLE_COUNT_MAP) as? HashMap<String, Int> ?: defaultTitleNumberStringToTitleCountMap
        val titleDurationMapAsInt = documentSnapshot.get(TITLE_DURATION_MAP) as? HashMap<String, Int> ?: convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(defaultTitleNumberStringToTitleDurationMap)
        val titleDurationMapAsDuration = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(titleDurationMapAsInt)

        val user = User(
            email = userEmail,
            signedUpOn = signedUpOn,

            currentTitle = currentTitle,
            currentTool = currentTool,
            currentToolState = currentToolState,
            stopwatchStartTime = stopwatchStartTimeAsLocalTime,
            stopwatchPrevDuration = stopwatchPrevDurationAsDuration,
            timerStartTime = timerStartTimeAsLocalTime,
            timerNextSelectedTime = timerNextSelectedTimeAsDuration,

            level = level,
            levelUpHistoryMap = levelUpHistoryMapAsLocalDate,
            currentExp = currentExp,
            totalExp = totalExp,
            wiDTotalExp = wiDTotalExp,

            titleCountMap = titleCountMap,
            titleDurationMap = titleDurationMapAsDuration,
        )

        onUserFetched(user)
    }

//    fun updateNickname(newNickname: String) {
//        val profileUpdates = userProfileChangeRequest {
//            displayName = "Jane Q. User"
//            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
//        }

//        auth.currentUser.updateProfile()

//    }

    fun startStopwatch(
        email: String,
        newCurrentTitle: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newStopwatchStartDate: LocalDate,
        newStopwatchStartTime: LocalTime,
        onStopwatchStarted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "startStopwatch executed")

        val stopwatchStartTimeAsTimestamp = Timestamp(Date.from(newStopwatchStartTime.atDate(newStopwatchStartDate).atZone(ZoneId.systemDefault()).toInstant()))

        val data = hashMapOf(
            CURRENT_TITLE to newCurrentTitle,
            CURRENT_TOOL to newCurrentTool.name,
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            STOPWATCH_START_TIME to stopwatchStartTimeAsTimestamp
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onStopwatchStarted(updateResult)
            }
        )
    }

    fun pauseStopwatch(
        email: String,
        newCurrentToolState: CurrentToolState,
        newStopwatchPrevDuration: Duration,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onStopwatchPaused: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        val newStopwatchPrevDurationAsInt = newStopwatchPrevDuration.seconds.toInt()
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            STOPWATCH_PREV_DURATION to newStopwatchPrevDurationAsInt,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onStopwatchPaused(updateResult)
            }
        )
    }

    fun pauseStopwatchWithLevelUp(
        email: String,
        newCurrentToolState: CurrentToolState,
        newStopwatchPrevDuration: Duration,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onStopwatchPausedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatchWithLevelUp executed")

        val newStopwatchPrevDurationAsInt = newStopwatchPrevDuration.seconds.toInt()
        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            STOPWATCH_PREV_DURATION to newStopwatchPrevDurationAsInt,
            LEVEL to newLevel, // 레벨 업
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString, // 레벨 업
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onStopwatchPausedWithLevelUp(updateResult)
            }
        )
    }

    fun stopStopwatch(
        email: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        onStopwatchStopped: (Boolean) -> Unit
    ) {
        Log.d(TAG, "stopStopwatch executed")

        val data = hashMapOf(
            CURRENT_TOOL to newCurrentTool.name,
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            STOPWATCH_PREV_DURATION to 0 // 파라미터로 받을 필요 없이 초기화 해줌.
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onStopwatchStopped(updateResult)
            }
        )
    }

    fun startTimer(
        email: String,
        newCurrentTitle: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newTimerStartDate: LocalDate,
        newTimerStartTime: LocalTime,
        onTimerStarted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "startTimer executed")

        val newTimerStartTimeAsTimestamp = Timestamp(Date.from(newTimerStartTime.atDate(newTimerStartDate).atZone(ZoneId.systemDefault()).toInstant()))

        val data = hashMapOf(
            CURRENT_TITLE to newCurrentTitle,
            CURRENT_TOOL to newCurrentTool.name,
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            TIMER_START_TIME to newTimerStartTimeAsTimestamp
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onTimerStarted(updateResult)
            }
        )
    }

    fun pauseTimer(
        email: String,
        newCurrentToolState: CurrentToolState,
        newTimerNextSelectedTime: Duration,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onTimerPaused: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseTimer executed")

        val newTimerNextSelectedTimeAsInt = newTimerNextSelectedTime.seconds.toInt()
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            TIMER_NEXT_SELECTED_TIME to newTimerNextSelectedTimeAsInt,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onTimerPaused(updateResult)
            }
        )
    }

    fun pauseTimerWithLevelUp(
        email: String,
        newCurrentToolState: CurrentToolState,
        newTimerNextSelectedTime: Duration,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onTimerPausedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseTimerWithLevelUp executed")

        val newTimerNextSelectedTimeAsInt = newTimerNextSelectedTime.seconds.toInt()
        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            TIMER_NEXT_SELECTED_TIME to newTimerNextSelectedTimeAsInt,
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onTimerPausedWithLevelUp(updateResult)
            }
        )
    }

    fun stopTimer(
        email: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        onTimerStopped: (Boolean) -> Unit
    ) {
        Log.d(TAG, "stopTimer executed")

        val data = hashMapOf(
            CURRENT_TOOL to newCurrentTool.name,
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            TIMER_NEXT_SELECTED_TIME to 0 // 파라미터로 받을 필요 없이 초기화 해줌.
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onTimerStopped(updateResult)
            }
        )
    }

    fun autoStopTimer(
        email: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onTimerAutoStopped: (Boolean) -> Unit
    ) {
        Log.d(TAG, "autoStopTimer executed")

        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_TOOL to newCurrentTool.name,
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            TIMER_NEXT_SELECTED_TIME to 0, // 파라미터로 받을 필요 없이 초기화 해줌.
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onTimerAutoStopped(updateResult)
            }
        )
    }

    fun autoStopTimerWithLevelUp(
        email: String,
        newCurrentTool: CurrentTool,
        newCurrentToolState: CurrentToolState,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onTimerAutoStoppedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "autoStopTimerWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_TOOL to newCurrentTool.name,
            CURRENT_TOOL_STATE to newCurrentToolState.name,
            TIMER_NEXT_SELECTED_TIME to 0, // 파라미터로 받을 필요 없이 초기화 해줌.
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onTimerAutoStoppedWithLevelUp(updateResult)
            }
        )
    }

    fun createWiD(
        email: String,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
        onCreatedWiD: (Boolean) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onCreatedWiD(updateResult)
            }
        )
    }

    fun createdWiDWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
        onCreatedWiDWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "createdWiDWithLevelUp executed")


        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onCreatedWiDWithLevelUp(updateResult)
            }
        )
    }

    fun updateWiD(
        email: String,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
        onWiDUpdated: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onWiDUpdated(updateResult)
            }
        )
    }

    fun updateWiDWithLevelDown(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
        onWiDUpdatedWithLevelDown: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiDWithLevelDown executed")

        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onWiDUpdatedWithLevelDown(updateResult)
            }
        )
    }

    fun updateWiDWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleDurationMap: Map<String, Duration>,
        onWiDUpdatedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiDWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onWiDUpdatedWithLevelUp(updateResult)
            }
        )
    }

    fun deleteWiD(
        email: String,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onWiDDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onWiDDeleted(updateResult)
            }
        )
    }

    fun deleteWiDWithLevelDown(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newTotalExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        onWiDDeletedWithLevelDown: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiDWithLevelDown executed")

        val newLevelUpHistoryMapAsString = convertLevelUpHistoryMapToString(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleNumberStringToTitleDurationMapToTitleNumberStringToTitleIntMap(newTitleDurationMap)

        val data = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            TOTAL_EXP to newTotalExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            TITLE_COUNT_MAP to newTitleCountMap,
            TITLE_DURATION_MAP to newTitleDurationMapAsInt
        )

        updateUserDocument(
            email = email,
            data = data,
            onUpdateResult = { updateResult: Boolean ->
                onWiDDeletedWithLevelDown(updateResult)
            }
        )
    }

    private fun updateUserDocument(
        email: String,
        data: Map<String, Any>,
        onUpdateResult: (Boolean) -> Unit
    ) {
        firestore.collection(USER_COLLECTION)
            .document(email)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                onUpdateResult(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                onUpdateResult(false)
            }
    }

    // SettingView에서 실행되는 메서드
//    fun updateStatusMessage(email: String, newStatusMessage: String) {
//        Log.d(TAG, "updateStatusMessage executed")
//
//        firestore.collection(COLLECTION)
//            .document(email)
//            .update("statusMessage", newStatusMessage)
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully updated!")
//            }
//            .addOnFailureListener {
//                e -> Log.w(TAG, "Error updating document", e)
//            }
//    }

    // 인증 단계에서 사용하는 메서드니까 인증 이후에 사용되는 정보에 대해서는 반환하면 안됨.
//    private fun isEmailAlreadySignedUp(email: String, isSignedUp: (Boolean) -> Unit) {
//        Log.d(TAG, "isEmailAlreadySignedUp executed")
//
//        // 가입을 확인할 수단이 email 밖에 없음.
////        firestore.collection(COLLECTION)
////            .whereEqualTo("email", email)
////            .get()
////            .addOnSuccessListener { documents -> // 문서를 여러 개 가져오도록 동작하지만, email이 key이기 때문에, 하나의 문서만 가져올 수 있음.
////                // 문서가 존재하면 이미 가입된 이메일이고, 그렇지 않으면 가입되지 않은 이메일입니다.
////                isSignedUp(!documents.isEmpty)
////            }
////            .addOnFailureListener { exception ->
////                Log.w(TAG, "Error getting documents: ", exception)
////                // 오류가 발생하면 기본적으로 가입되지 않은 상태로 처리합니다.
////                isSignedUp(false)
////            }
//
//        auth.fetchSignInMethodsForEmail(email)
//            .addOnSuccessListener { result ->
//                /**
//                 * Firebase -> Authentication -> 설정 -> 사용자 작업 -> 이메일 열거 보호(권장) - 체크 해제해야 signInMethods 가져올 수 있음.
//                 */
//                val signInMethods = result.signInMethods!!
//
//                isSignedUp(signInMethods.isNotEmpty())
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, "Error getting sign in methods for user", exception)
//
//                isSignedUp(false) // 실패 시에도 false를 반환하도록 처리
//            }
//    }

    fun signOut() {
        Log.d(TAG, "signOut executed")

        auth.signOut()
    }

    fun deleteUser(
        email: String,
        onUserDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteUser executed")

        val firebaseUser: FirebaseUser? = getFirebaseUser()

        /** 만약 1, 2, 3 중 삭제 안되는 게 생기면?? */
        firebaseUser!!.delete() // 1. 파이어 베이스 유저 삭제
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")

                    deleteUserDocument( // 2. 유저 문서(전체) 삭제
                        email = email,
                        onUserCollectionDeleted = { deleteUserCollectionSuccess: Boolean ->
                            if (deleteUserCollectionSuccess) {
                                deleteWiDDocument( // 3. WiD 문서(전체) 삭제
                                    email = email,
                                    onWiDCollectionDeleted = { deleteWiDCollectionSuccess: Boolean ->
                                        if (deleteWiDCollectionSuccess) {
                                            onUserDeleted(true)
                                        }
                                    }
                                )
                            }
                        }
                    )
                } else {
                    onUserDeleted(false)
                }
            }
    }

    private fun deleteUserDocument(
        email: String,
        onUserCollectionDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteUserDocument executed")

        firestore.collection(USER_COLLECTION)
            .whereEqualTo(EMAIL, email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        document.reference
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "User collection document deleted successfully.")

                                onUserCollectionDeleted(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error deleting document: ", exception)

                                onUserCollectionDeleted(false)
                                return@addOnFailureListener
                            }
                    }

                    onUserCollectionDeleted(true)
                } else {
                    Log.d(TAG, "No documents found in user collection.")

                    onUserCollectionDeleted(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)

                onUserCollectionDeleted(false)
            }
    }

    private fun deleteWiDDocument(
        email: String,
        onWiDCollectionDeleted: (Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiDDocument executed")

        firestore.collection(WID_COLLECTION)
            .whereEqualTo(EMAIL, email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        document.reference
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "WiD collection document deleted successfully.")

                                onWiDCollectionDeleted(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error deleting document: ", exception)

                                onWiDCollectionDeleted(false)
                                return@addOnFailureListener
                            }
                    }

                    onWiDCollectionDeleted(true)
                } else {
                    Log.d(TAG, "No documents found in WiD collection.")

                    onWiDCollectionDeleted(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)

                onWiDCollectionDeleted(false)
            }
    }

    /** 익명 로그인 */
//    fun signInAnonymously(callback: (Boolean) -> Unit) {
//        Log.d(TAG, "signInAnonymously executed")
//
//        auth.signInAnonymously()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "signInAnonymously:success")
//                    callback(true)
//                } else {
//                    Log.w(TAG, "signInAnonymously:failure", task.exception)
//                    callback(false)
//                }
//            }
//    }
}