package andpact.project.wid.activity

import andpact.project.wid.R
import andpact.project.wid.ui.theme.WiDTheme
import andpact.project.wid.ui.theme.acmeRegular
import andpact.project.wid.util.AppOpenAdUtil
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Window
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val LOG_TAG = "SplashActivity"

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val isInternetConnected = isInternetConnected()

        setContent {
//            SplashScreen(isInternetConnected)
            SplashScreen()
        }

//        if (isInternetConnected) {
//            showAppOpenAd()
//        }
    }

//    private fun isInternetConnected(): Boolean {
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//
//        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
//    }

    private fun startMainActivity() {
        Log.d("SplashActivity", "Start MainActivity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Composable
//fun SplashScreen(isInternetConnected: Boolean) {
    fun SplashScreen() {
        LaunchedEffect(true) {
            delay(2000)

            startMainActivity()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WiD",
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = acmeRegular
                    )
                )
            }

//        if (!isInternetConnected) {
//            Column(modifier = Modifier
//                .fillMaxSize()
//                .padding(0.dp, 100.dp, 0.dp, 0.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "인터넷 연결을 확인하세요.",
//                    style = TextStyle(color = Color.White, textAlign = TextAlign.Center, fontSize = 24.sp)
//                )
//            }
//        }
        }
    }

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
}

