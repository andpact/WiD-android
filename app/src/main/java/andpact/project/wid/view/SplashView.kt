package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.viewModel.SplashViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight)
                    .background(color = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navigationBarHeight)
                    .background(color = MaterialTheme.colorScheme.surface)
            )
        },
    ) { contentPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_main_foreground), // ic_main, ic_main_rounded는 안되네?
                contentDescription = "앱 아이콘"
            )
        }
    }
}