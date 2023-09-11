package andpact.project.wid.activity

import andpact.project.wid.R
import andpact.project.wid.ui.theme.WiDTheme
import andpact.project.wid.util.AppOpenAdUtil
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

private const val LOG_TAG = "SplashActivity"

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
        showAppOpenAd()
    }

    private fun showAppOpenAd() {
        val application = application as? AppOpenAdUtil

        if (application == null) {
            Log.e(LOG_TAG, "showAppOpenAd : Failed to cast application to AppOpenAdUtil.")

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            return
        }

        application.showAdIfAvailable(this)

//        application.showAdIfAvailable(
//            this@SplashActivity,
//            object : AppOpenAdUtil.OnShowAdCompleteListener {
//                override fun onShowAdComplete() {
//                    startMainActivity()
//                }
//            })
    }
}

@Composable
fun SplashScreen() {
    WiDTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "WiD",
                style = TextStyle(color = Color.White, textAlign = TextAlign.Center, fontSize = 70.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(
                    Font(R.font.acme_regular)))
            )

//            LinearProgressIndicator()
//
//            CircularProgressIndicator()
        }
    }
}