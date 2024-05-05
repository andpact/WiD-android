package andpact.project.wid.repository

import andpact.project.wid.model.User
import andpact.project.wid.util.defaultTitleColorMapWithColors
import andpact.project.wid.util.defaultTitleColorMapWithIntegers
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

// 파이어 베이스 인증 관련 기능을 담당하는 클래스
// FirebaseUser는 FirebaseAuth 객체에 묶여 있다고 보면 됨.
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val TAG = "UserRepository"
    private val COLLECTION = "UserCollection"

    init {
        Log.d(TAG, "created")
    }

    protected fun finalize() {
        Log.d(TAG, "destroyed")
    }

//    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
//    private val firestore = FirebaseFirestore.getInstance()

    // User가 여기 있어야 하나?
//    private var firebaseUser: FirebaseUser? = null
//    var user: User? = null

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

//    fun getUid(): String {
//        Log.d(TAG, "getUid executed")
//
//        return getFirebaseUser()?.uid ?: ""
//    }

//    fun getEmail(): String {
//        Log.d(TAG, "getEmail executed")
//
//        return getFirebaseUser()?.email ?: ""
//    }

//    fun getDisplayName(): String {
//        Log.d(TAG, "getDisplayName executed")
//
//        return getFirebaseUser()?.displayName ?: ""
//    }

//    fun getTitleColorMap(): String {
//        Log.d(TAG, "getTitleColorMap executed")
//    }

    // 동적 링크 감지 메서드
    // 계정 없을 때 동적 링크 감지 -> 회원 가입 완료(User Document 생성되도록 해야함)
    // 계정 있을 때 동적 링크 감지 -> 로그인 완료
    // 따라서 회원 가입과 로그인 링크 감지 메서드를 따로 만들어야 함.
    // 동적 링크를 어떻게 회원 가입 용인지, 로그인 용인지 구분할거?
    fun verifyAuthenticationLink(email: String, dynamicLink: String?, onAuthenticationLinkVerified: (Boolean) -> Unit) {
        Log.d(TAG, "verifyAuthenticationLink executed")

        if (!dynamicLink.isNullOrEmpty() && auth.isSignInWithEmailLink(dynamicLink)) {
            auth.signInWithEmailLink(email, dynamicLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully signed in with email link!")

//                        isEmailAlreadySignedUp(email = email) { isSignedUp ->
//                            if (!isSignedUp) { // 링크로 인증에 성공했지만 User Document가 없는 경우(회원가입)에 새로 생성해줌.
//                                val userDocument = mapOf(
//                                    "email" to email,
//                                    "statusMessage" to "상태 메시지를 설정해 주세요."
//                                )
//
//                                firestore.collection(COLLECTION)
//                                    .add(userDocument) // 랜덤 id로 생성됨.
//                                    .addOnSuccessListener { documentReference ->
//                                        Log.d(TAG, "DocumentSnapshot added with ID : ${documentReference.id}")
//
//                                        // 예를 UserDS에 저장하자.
//                                        User(
//                                            email = userDocument["email"] ?: "",
//                                            statusMessage = userDocument["statusMessage"] ?: ""
//                                        )
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Log.w(TAG, "Error adding document", e)
//                                    }
//                            }
//                        }
                        // 성공 시에 true 반환
                        onAuthenticationLinkVerified(true)
                    } else {
                        Log.e(TAG, "Error signing in with email link", task.exception)

                        // 실패 시에 false 반환
                        onAuthenticationLinkVerified(false)
                    }
                }
        } else {
            Log.d(TAG, "isSignInWithEmailLink: failure")
            // 인증 실패 시에도 false 반환
            onAuthenticationLinkVerified(false)
        }
    }

    fun getFirebaseUser(): FirebaseUser? {
        val firebaseUser = auth.currentUser

        Log.d(TAG, "getFirebaseUser called : $firebaseUser")

        return firebaseUser
    }

    fun getUser(email: String, onUserFetched: (User?) -> Unit) {
        Log.d(TAG, "getUser executed")

        firestore.collection(COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val userDocument = hashMapOf(
                        "email" to email,
                        "statusMessage" to "상태 메시지를 설정해 주세요.",
                        "titleColorMap" to defaultTitleColorMapWithIntegers
                    )

                    firestore.collection(COLLECTION)
                        .add(userDocument)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID : ${documentReference.id}")

                            val newUser = User(
                                email = email,
                                statusMessage = "상태 메시지를 설정해 주세요.",
                                titleColorMap = defaultTitleColorMapWithColors
                            )

                            onUserFetched(newUser)
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)

                            // 실패한 경우 null을 콜백합니다.
                            onUserFetched(null)
                        }
                } else { // 문서가 존재할 때만 변환 작업을 수행합니다.
                    val document = documents.documents[0] // 이 부분은 첫 번째 문서를 가져옵니다. 실제로는 email이 고유하다는 가정하에 이 코드를 사용할 수 있습니다.

                    // 문서에서 필드 값을 가져와서 User 객체로 변환합니다.
                    val userEmail = document.getString("email") ?: ""
                    val statusMessage = document.getString("statusMessage") ?: "상태 메시지를 설정해 주세요."
                    val titleColorMap = document.get("titleColorMap") as? HashMap<String, Int>

                    val user = User(
                        email = userEmail,
                        statusMessage = statusMessage,
                        titleColorMap = titleColorMap?.mapValues { Color(it.value) } ?: defaultTitleColorMapWithColors
                    )

                    onUserFetched(user)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)

                onUserFetched(null)
            }
    }

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

    fun sendAuthenticationLinkToEmail(email: String, onAuthenticationLinkSent: (Boolean) -> Unit) {
        Log.d(TAG, "sendAuthenticationLinkToEmail executed")

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")

                    onAuthenticationLinkSent(true)
                } else {
                    Log.d(TAG, "Email sent: failure.")

                    onAuthenticationLinkSent(false)
                }
            }

//        isEmailAlreadySignedUp(email) { isSignedUp ->
//            if (isSignedUp) { // 계정이 있는 경우
//                // 이 경우에 User 문서를 가져올 수 있음. 가져온 User 문서를 UserDS에 저장해야함.
//
//                Log.d(TAG, "User already signed up with this email.")
//
//                sent(false)
//            } else { // 계정이 없는 경우
//                auth.sendSignInLinkToEmail(email, actionCodeSettings)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Log.d(TAG, "Email sent.")
//
//                            sent(true)
//                        } else {
//                            Log.d(TAG, "Email sent: failure.")
//
//                            sent(false)
//                        }
//                    }
//
////                sent(true)
//            }
//        }
    }

//    fun sendSignInLinkToEmail(email: String, onSignInLinkSent: (Boolean) -> Unit) {
//        Log.d(TAG, "sendSignInLinkToEmail executed")
//
//        auth.sendSignInLinkToEmail(email, actionCodeSettings)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Email sent.")
//
//                    onSignInLinkSent(true)
//                } else {
//                    Log.d(TAG, "Email sent: failure.")
//
//                    onSignInLinkSent(false)
//                }
//            }

//        isEmailAlreadySignedUp(email) { isSignedUp ->
//            if (isSignedUp) { // 계정이 있는 경우
//                auth.sendSignInLinkToEmail(email, actionCodeSettings)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Log.d(TAG, "Email sent.")
//
//                            sent(true)
//                        } else {
//                            Log.d(TAG, "Email sent: failure.")
//
//                            sent(false)
//                        }
//                    }
//
////                sent(true)
//            } else { // 계정이 없는 경우
//                Log.d(TAG, "Account for $email doesn't exist")
//
//                sent(false)
//            }
//        }
//    }

    fun signOut() {
        auth.signOut()
    }

//    fun deleteUser(firebaseUser: FirebaseUser?) {
//        firebaseUser!!.delete()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "User account deleted.")
//
//                    // UserDocument도 제거하자.
//                }
//            }
//    }

    fun deleteUser(email: String, onUserDeleted: (Boolean) -> Unit) {
        val firebaseUser: FirebaseUser? = getFirebaseUser()

        firebaseUser!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")

                    firestore.collection(COLLECTION)
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                // 문서가 존재할 경우, 해당 문서의 참조를 가져옵니다.
                                val document = documents.documents[0] // 이 부분은 첫 번째 문서를 가져옵니다. 이메일이 고유하다는 가정하에 사용합니다.

                                // 문서의 참조를 사용하여 문서를 삭제합니다.
                                document.reference
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted")

                                        onUserDeleted(true)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(TAG, "Error deleting document", exception)

                                        onUserDeleted(false)
                                    }
                            } else {
                                // 해당 이메일에 해당하는 문서가 없을 때는 삭제하지 않고 삭제가 완료되었다고 알려줍니다.
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }
                }
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