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
import com.google.firebase.firestore.QuerySnapshot
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

//    private val CURRENT_TITLE = "currentTitle"
//    private val CURRENT_TOOL = "currentTool"
//    private val CURRENT_TOOL_STATE = "currentToolState"
//    private val STOPWATCH_START_TIME = "stopwatchStartTime"
//    private val STOPWATCH_PREV_DURATION = "stopwatchPrevDuration"
//    private val TIMER_START_TIME = "timerStartTime"
//    private val TIMER_NEXT_SELECTED_TIME = "timerNextSelectedTime"

    private val LEVEL = "level"
    private val LEVEL_UP_HISTORY_MAP = "levelUpHistoryMap"
    private val CURRENT_EXP = "currentExp"
    private val WID_TOTAL_EXP = "wiDTotalExp"

    private val WID_TITLE_COUNT_MAP = "wiDTitleCountMap"
    private val WID_TITLE_DURATION_MAP = "wiDTitleDurationMap"
    private val WID_TOOL_COUNT_MAP = "wiDToolCountMap"
    private val WID_TOOL_DURATION_MAP = "wiDToolDurationMap"

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
        onAuthenticationLinkSentToEmail: (authenticationLinkSentToEmail: Boolean) -> Unit
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
        onAuthenticationLinkVerified: (authenticationLinkVerified: Boolean) -> Unit
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
        onSignedInWithEmailLink: (signedInWithEmailLink: Boolean) -> Unit
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
        onUserFetched: (user: User?) -> Unit
    ) {
        Log.d(TAG, "getUser executed")

        firestore.collection(USER_COLLECTION)
            .whereEqualTo(EMAIL, email)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                // 회원 가입 시 새로운 문서를 생성함.
                if (querySnapshot.isEmpty) {
                    createUser(
                        email = email,
                        onUserCreated = { user: User? ->
                            onUserFetched(user)
                        }
                    )
                } else { // 문서가 존재할 때만 변환 작업을 수행합니다.
                    getExistingUser(
                        documentSnapshot = querySnapshot.documents[0],
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

    private fun createUser(
        email: String,
        onUserCreated: (user: User?) -> Unit
    ) {
        Log.d(TAG, "createUser executed")

        val today = LocalDate.now()
        val todayAsString = today.toString()

        val defaultLevelToDateMapForServer = convertLevelToDateMapForServer(defaultLevelToDateMap)
        val defaultTitleDurationMapAsInt = convertTitleToDurationMapForServer(defaultTitleToDurationMap)
        val defaultToolCountMapAsString = convertToolToCountMapForServer(defaultToolToCountMap)
        val defaultToolDurationMapAsString = convertToolToDurationMapForServer(defaultToolToDurationMap)

        val newUserDocument = hashMapOf(
            EMAIL to email,
            SIGNED_UP_ON to todayAsString,

            LEVEL to 1,
            LEVEL_UP_HISTORY_MAP to defaultLevelToDateMapForServer,
            CURRENT_EXP to 0,
            WID_TOTAL_EXP to 0,

            WID_TITLE_COUNT_MAP to defaultTitleToCountMap,
            WID_TITLE_DURATION_MAP to defaultTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to defaultToolCountMapAsString,
            WID_TOOL_DURATION_MAP to defaultToolDurationMapAsString,
        )

        firestore.collection(USER_COLLECTION)
            .add(newUserDocument)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID : ${documentReference.id}")

                val newUser = User(
                    email = email,
                    signedUpOn = today,
                    // 레벨
                    level = 1,
                    levelUpHistoryMap = defaultLevelToDateMap,
                    // 경험치
                    currentExp = 0,
                    wiDTotalExp = 0,
                    // 제목
                    wiDTitleCountMap = defaultTitleToCountMap,
                    wiDTitleDurationMap = defaultTitleToDurationMap,
                    wiDToolCountMap = defaultToolToCountMap,
                    wiDToolDurationMap = defaultToolToDurationMap
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
        onUserFetched: (user: User?) -> Unit
    ) {
        Log.d(TAG, "getExistingUser executed")

        val userEmail = documentSnapshot.getString(EMAIL) ?: ""
        val signedUpOn = LocalDate.parse(documentSnapshot.getString(SIGNED_UP_ON))

        // 레벨
        val level = documentSnapshot.getLong(LEVEL)?.toInt() ?: 1
        val levelUpHistoryMapAsString = documentSnapshot.get(LEVEL_UP_HISTORY_MAP) as? HashMap<String, String> ?: convertLevelToDateMapForServer(defaultLevelToDateMap)
        val levelUpHistoryMapAsLocalDate = convertLevelToDateMapForClient(levelUpHistoryMapAsString)

        // 경험치
        val currentExp = documentSnapshot.getLong(CURRENT_EXP)?.toInt() ?: 0
        val wiDTotalExp = documentSnapshot.getLong(WID_TOTAL_EXP)?.toInt() ?: 0

        // 제목
        val titleCountMap = documentSnapshot.get(WID_TITLE_COUNT_MAP) as? HashMap<String, Int> ?: defaultTitleToCountMap
        val titleDurationMapAsInt = documentSnapshot.get(WID_TITLE_DURATION_MAP) as? HashMap<String, Int> ?: convertTitleToDurationMapForServer(defaultTitleToDurationMap)
        val titleDurationMapAsDuration = convertTitleToDurationMapForClient(titleDurationMapAsInt)
        val toolCountMapAsString = documentSnapshot.get(WID_TITLE_COUNT_MAP) as? HashMap<String, Int> ?: convertToolToCountMapForServer(defaultToolToCountMap)
        val toolCountMapAsCurrentTool = convertToolToCountMapForClient(toolCountMapAsString)
        val toolDurationMapAsString = documentSnapshot.get(WID_TOOL_DURATION_MAP) as? HashMap<String, Int> ?: convertToolToDurationMapForServer(defaultToolToDurationMap)
        val toolDurationMapAsCurrentTool = convertToolToDurationMapForClient(toolDurationMapAsString)

        val user = User(
            email = userEmail,
            signedUpOn = signedUpOn,

            level = level,
            levelUpHistoryMap = levelUpHistoryMapAsLocalDate,
            currentExp = currentExp,
            wiDTotalExp = wiDTotalExp,

            wiDTitleCountMap = titleCountMap,
            wiDTitleDurationMap = titleDurationMapAsDuration,
            wiDToolCountMap = toolCountMapAsCurrentTool,
            wiDToolDurationMap = toolDurationMapAsCurrentTool
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

    fun pauseStopwatch(
        email: String,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onStopwatchPaused: (stopwatchPaused: Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatch executed")

        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )

        /** 복구!! */
//        updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onUserUpdated = { userUpdated: Boolean ->
//                onStopwatchPaused(userUpdated)
//            }
//        )
    }

    fun pauseStopwatchWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onStopwatchPausedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseStopwatchWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelToDateMapForServer(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            LEVEL to newLevel, // 레벨 업
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString, // 레벨 업
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )

        updateUserDocument(
            email = email,
            updatedUserDocument = updatedUserDocument,
            onUserUpdated = { userUpdated: Boolean ->
                onStopwatchPausedWithLevelUp(userUpdated)
            }
        )
    }

    fun pauseTimer(
        email: String,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onTimerPaused: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseTimer executed")

        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )
        /** 복구!! */
//        updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onUserUpdated = { userUpdated: Boolean ->
//                onTimerPaused(userUpdated)
//            }
//        )
    }

    fun pauseTimerWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onTimerPausedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "pauseTimerWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelToDateMapForServer(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )

        updateUserDocument(
            email = email,
            updatedUserDocument = updatedUserDocument,
            onUserUpdated = { userUpdated: Boolean ->
                onTimerPausedWithLevelUp(userUpdated)
            }
        )
    }

    fun autoStopTimer(
        email: String,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onTimerAutoStopped: (Boolean) -> Unit
    ) {
        Log.d(TAG, "autoStopTimer executed")

        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )
        /** 복구!! */
//        updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onUserUpdated = { userUpdated: Boolean ->
//                onTimerAutoStopped(userUpdated)
//            }
//        )
    }

    fun autoStopTimerWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onTimerAutoStoppedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "autoStopTimerWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelToDateMapForServer(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )

        updateUserDocument(
            email = email,
            updatedUserDocument = updatedUserDocument,
            onUserUpdated = { userUpdated: Boolean ->
                onTimerAutoStoppedWithLevelUp(userUpdated)
            }
        )
    }

    fun createWiD(
        email: String,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onCreatedWiD: (Boolean) -> Unit
    ) {
        Log.d(TAG, "createWiD executed")

        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )
        /** 복구!! */
//        updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onUserUpdated = { userUpdated: Boolean ->
//                onCreatedWiD(userUpdated)
//            }
//        )
    }

    fun createdWiDWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onCreatedWiDWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "createdWiDWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelToDateMapForServer(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )

        updateUserDocument(
            email = email,
            updatedUserDocument = updatedUserDocument,
            onUserUpdated = { userUpdated: Boolean ->
                onCreatedWiDWithLevelUp(userUpdated)
            }
        )
    }

    fun updateWiD(
        email: String,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onWiDUpdated: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiD executed")

        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)

        val updatedUserDocument = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_DURATION_MAP to ,
        )
        /** 복구!! */
//        updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onUserUpdated = { userUpdated: Boolean ->
//                onWiDUpdated(userUpdated)
//            }
//        )
    }

    fun updateWiDWithLevelUp(
        email: String,
        newLevel: Int,
        newLevelUpHistoryMap: Map<String, LocalDate>,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onWiDUpdatedWithLevelUp: (Boolean) -> Unit
    ) {
        Log.d(TAG, "updateWiDWithLevelUp executed")

        val newLevelUpHistoryMapAsString = convertLevelToDateMapForServer(newLevelUpHistoryMap)
        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)

        val updatedUserDocument = hashMapOf(
            LEVEL to newLevel,
            LEVEL_UP_HISTORY_MAP to newLevelUpHistoryMapAsString,
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_DURATION_MAP to ,
        )

        updateUserDocument(
            email = email,
            updatedUserDocument = updatedUserDocument,
            onUserUpdated = { userUpdated: Boolean ->
                onWiDUpdatedWithLevelUp(userUpdated)
            }
        )
    }

    fun deleteWiD(
        email: String,
        newCurrentExp: Int,
        newWiDTotalExp: Int,
        newTitleCountMap: Map<String, Int>,
        newTitleDurationMap: Map<String, Duration>,
        newToolCountMap: Map<CurrentTool, Int>,
        newToolDurationMap: Map<CurrentTool, Duration>,
        onWiDDeleted: (wiDDeleted: Boolean) -> Unit
    ) {
        Log.d(TAG, "deleteWiD executed")

        val newTitleDurationMapAsInt = convertTitleToDurationMapForServer(newTitleDurationMap)
        val newToolCountMapAsString = convertToolToCountMapForServer(newToolCountMap)

        val updatedUserDocument = hashMapOf(
            CURRENT_EXP to newCurrentExp,
            WID_TOTAL_EXP to newWiDTotalExp,
            WID_TITLE_COUNT_MAP to newTitleCountMap,
            WID_TITLE_DURATION_MAP to newTitleDurationMapAsInt,
            WID_TOOL_COUNT_MAP to newToolCountMapAsString,
            WID_TOOL_DURATION_MAP to ,
        )
        /** 복구!! */
//        updateUserDocument(
//            email = email,
//            updatedUserDocument = updatedUserDocument,
//            onUserUpdated = { userUpdated: Boolean ->
//                onWiDDeleted(userUpdated)
//            }
//        )
    }

    private fun updateUserDocument(
        email: String,
        updatedUserDocument: Map<String, Any>,
        onUserUpdated: (Boolean) -> Unit
    ) {
        firestore.collection(USER_COLLECTION)
            .document(email)
            .set(updatedUserDocument)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                onUserUpdated(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                onUserUpdated(false)
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