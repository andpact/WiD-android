package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.viewModel.SplashViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    dynamicLink: String?,
    onEmailLinkVerified: (Boolean) -> Unit,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val TAG = "SplashView"

    // 인터넷
//    val isInternetConnected = splashViewModel.isInternetConnected.value

//    LaunchedEffect(isInternetConnected) {
    LaunchedEffect(true) {
        // 인터넷이 연결되지 않은 경우 화면을 넘기지 않음.
//        if (!isInternetConnected) {
//            return@LaunchedEffect
//        }

        delay(1500)

        // 기존 로그인 기록이 있는 경우와 다이나믹 링크를 감지하는 경우가 배타적인가?
        // 동적 링크 감지와 기존 로그인 기록 확인 중 무엇이 먼저인지?
        // 인증(회원 가입, 로그인)이 되었다는 것은 Firebase Auth 객체를 가지고 있다는 것.
        // 기존 로그인 기록이 없는 상황에서 다이나믹 링크를 전송함.
        // 앱을 실행하고, 다이나믹 링크로 앱을 실행함.
        splashViewModel.verifyAuthenticationLink(
            dynamicLink = dynamicLink,
            onAuthenticationLinkVerified = { authenticationLinkVerified: Boolean ->
                if (authenticationLinkVerified || splashViewModel.hasFirebaseUser()) { // 동적 링크가 검증되거나 기존 로그인 유저 있거나.
                    splashViewModel.setFirebaseUserAndUser() // Firebase User 및 User 문서 가져옴.

                    onEmailLinkVerified(true) // 메인 화면 전환
                } else { // 기존 로그인 기록이 없는 경우
                    onEmailLinkVerified(false) // 인증 화면 전환
                }
            }
        )
    }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                //                    modifier = Modifier
                //                        .size(96.dp),
                painter = painterResource(id = R.mipmap.ic_main_foreground), // ic_main, ic_main_rounded는 안되네?
                contentDescription = "앱 아이콘"
            )

            //                Text(
            //                    text = "WiD",
            //                    style = TextStyle(
            //                        color = MaterialTheme.colorScheme.primary,
            //                        textAlign = TextAlign.Center,
            //                        fontSize = 70.sp,
            //                        fontWeight = FontWeight.Bold,
            //                        fontFamily = acmeRegular
            //                    )
            //                )
        }
        /** 인터넷 연결 확인 메시지는 대화상자로 띄우자. */
        //            if (!isInternetConnected) {
        //                Column(modifier = Modifier
        //                    .fillMaxSize()
        //                    .padding(bottom = 100.dp),
        //                    verticalArrangement = Arrangement.Center,
        //                    horizontalAlignment = Alignment.CenterHorizontally
        //                ) {
        //                    Text(
        //                        text = "인터넷 연결을 확인하세요.",
        //                        style = Typography.bodyMedium,
        //                        color = MaterialTheme.colorScheme.primary
        //                    )
        //                }
        //            }
    }
}