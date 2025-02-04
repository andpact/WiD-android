package andpact.project.wid.repository

import andpact.project.wid.model.City
import andpact.project.wid.model.User
import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

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
    val LEVEL = "level"
    val CURRENT_EXP = "currentExp"
    val WID_TOTAL_EXP = "wiDTotalExp"
    val WID_MIN_LIMIT = "wiDMinLimit"
    val WID_MAX_LIMIT = "wiDMaxLimit"
    val CITY = "city"

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
        onUserFetched: (userFetched: User?) -> Unit
    ) {
        Log.d(TAG, "getUser executed")

        firestore.collection(USER_COLLECTION)
            .whereEqualTo(EMAIL, email)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (querySnapshot.isEmpty) { // 회원 가입 시 새로운 문서를 생성함.
                    createUser(
                        email = email,
                        onUserCreated = { user: User? ->
                            onUserFetched(user)
                        }
                    )
                } else { // 문서가 존재할 때만 변환 작업을 수행합니다.
                    val user = querySnapshot.documents.first().toUserOrNull()

                    onUserFetched(user)
//                    getExistingUser(
//                        documentSnapshot = querySnapshot.documents.first(),
//                        onUserFetched = { user: User? ->
//                            onUserFetched(user)
//                        }
//                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)

                onUserFetched(null)
            }
    }

    private fun createUser(
        email: String,
        onUserCreated: (userCreated: User?) -> Unit
    ) {
        Log.d(TAG, "createUser executed")

        val newUser = User.default().copy(email = email)
        val newUserDocument = newUser.toDocument()

        firestore.collection(USER_COLLECTION)
            .add(newUserDocument)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID : ${documentReference.id}")

                onUserCreated(newUser)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                onUserCreated(null)
            }
    }

//    fun updateNickname(newNickname: String) {
//        val profileUpdates = userProfileChangeRequest {
//            displayName = "Jane Q. User"
//            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
//        }

//        auth.currentUser.updateProfile()

//    }

    fun setUserDocument(
        email: String,
        updatedUserDocument: Map<String, Any>,
        onComplete: (Boolean) -> Unit
    ) {
        firestore.collection(USER_COLLECTION)
            .document(email)
            .set(updatedUserDocument)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                onComplete(false)
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

    // TODO: WiDRepository에서 실행되야지
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

    // **************************************** 유틸 메서드 ****************************************
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

//    private fun Map<String, Any>.toUser(): User { // Map -> User
//        return User(
//            email = this[EMAIL] as String,
//            signedUpOn = LocalDate.parse(this[SIGNED_UP_ON] as String),
//            level = (this[LEVEL] as Long).toInt(), // Firebase 숫자는 Long으로 반환됨
//            currentExp = (this[CURRENT_EXP] as Long).toInt(),
//            wiDTotalExp = (this[WID_TOTAL_EXP] as Long).toInt(),
//            wiDMinLimit = (this[WID_MIN_LIMIT] as Long).toInt(),
//            wiDMaxLimit = (this[WID_MAX_LIMIT] as Long).toInt()
//        )
//    }

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