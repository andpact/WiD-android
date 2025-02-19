package andpact.project.wid.activity

import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.ui.theme.WiDTheme
import andpact.project.wid.view.MainActivityView
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint

/**
 * 이메일 링크는 6시간의 유효 기간이 설정되어 있는 듯 하고,
 * 한 번 사용되면 재사용 할 수 없고,
 * 여러 개의 링크를 한 번에 보내면 마지막으로 보낸 링크만 사용이 가능한 듯 하다.
 * 인증에 한도가 존재함. (한 번 인증 후, 동일한 이메일로 다시 인증 하려니 안됨. 한도 초과라고 나옴.)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")

        /**
         * 시스템 바 설정 적용 순서
         * 1. themes.xml
         * 2. MainActivity
         * 3. Theme.kt
         */
        actionBar?.hide() // 테마에서 액션바를 없애기 전에 액션바를 없앰.
//        window.apply {
//            WindowCompat.setDecorFitsSystemWindows(this, false) // 시스템 바 영역까지 UI 확장
//            statusBarColor = Transparent.toArgb() // 상태 표시줄 투명
//            navigationBarColor = Transparent.toArgb() // 탐색 메뉴 투명
//        }

        val intent: Intent = intent

        setContent {
            val dynamicLink = remember { mutableStateOf<String?>(null) }

            // 동적 링크를 Repository 객체를 통해서 감지할 필요가 없음.
            FirebaseDynamicLinks
                .getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener { pendingDynamicLinkData ->
                    if (pendingDynamicLinkData == null) {
                        dynamicLink.value = null
                    } else {
                        dynamicLink.value = pendingDynamicLinkData.link.toString()
                        Log.d("dynamicLink", dynamicLink.value.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "getDynamicLink:onFailure", e)
                }

            WiDTheme {
                MainActivityView(dynamicLink.value)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}

/**
 * 사용할 거면 액티비티 안에 작성해서 사용하자.
 */
//    private fun showAppOpenAd() {
//        val application = application as? AppOpenAdUtil
//
//        if (application == null) {
//            Log.e(LOG_TAG, "showAppOpenAd : Failed to cast application to AppOpenAdUtil.")
//
//            startMainActivity()
//
//            return
//        }
//
//        application.showAdIfAvailable(this)

//        application.showAdIfAvailable(
//            this@SplashActivity,
//            object : AppOpenAdUtil.OnShowAdCompleteListener {
//                override fun onShowAdComplete() {
//                    startMainActivity()
//                }
//            }
//        )
//    }