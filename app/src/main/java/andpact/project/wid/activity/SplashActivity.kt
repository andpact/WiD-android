package andpact.project.wid.activity

import andpact.project.wid.ui.theme.Black
import andpact.project.wid.ui.theme.SplashTheme
import andpact.project.wid.ui.theme.White
import andpact.project.wid.ui.theme.acmeRegular
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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

        SplashTheme() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.secondary),
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
                            color = MaterialTheme.colorScheme.primary,
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
    //                    style = TextStyle(color = White, textAlign = TextAlign.Center, fontSize = 24.sp)
    //                )
    //            }
    //        }
            }
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

