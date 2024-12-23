package andpact.project.wid.view

import andpact.project.wid.destinations.MainActivityViewDestinations
import andpact.project.wid.ui.theme.changeStatusBarColor
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * 컴포저블 - Jetpack Compose에서 화면을 그리는 데 사용되는 함수,
 * 컴포넌트 - 서비스을 구성하는 블록(ui과 관련이 있는 액티비티와 그렇지 않은 서비스 클래스까지 포함)
 */
@Composable
fun MainActivityView(dynamicLink: String?) {
    val TAG = "MainActivity"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val mainActivityViewNavController: NavHostController = rememberNavController() // 네비게이션 그래프에는 NavController가 아닌 NavHostController가 파라미터로 들어감.

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = mainActivityViewNavController,
        startDestination = MainActivityViewDestinations.SplashViewDestination.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        builder = {
            composable(
                route = MainActivityViewDestinations.SplashViewDestination.route,
//                enterTransition = { null },
//                exitTransition = { null },
                content = {
                    SplashView(
                        dynamicLink = dynamicLink,
                        onEmailLinkVerified = { emailLinkVerified: Boolean ->
                            if (emailLinkVerified) { // 이메일 링크 인증 완료 -> 메인 뷰로 전환
                                mainActivityViewNavController.navigate(MainActivityViewDestinations.MainViewDestination.route)
                            } else { // 이메일 링크 인증 실패 -> 인증 뷰로 전환
                                mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                            }
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.AuthenticationViewDestination.route,
//                enterTransition = { null },
//                exitTransition = { null },
                content = {
                    AuthenticationView()
                }
            )

            composable(
                route = MainActivityViewDestinations.MainViewDestination.route,
//                enterTransition = { null },
//                exitTransition = { null },
                content = {
                    changeStatusBarColor(color = MaterialTheme.colorScheme.secondaryContainer)

                    MainView(
                        onStopwatchClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.StopwatchViewDestination.route)
                        },
                        onTimerClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TimerViewDestination.route)
                        },
                        onWiDClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.WiDViewDestination.route)
                        },
                        onUserSignedOut = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                        },
                        onUserDeleted = { userDeleted: Boolean ->
                            if (userDeleted) {
                                mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                            }
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.StopwatchViewDestination.route,
                content = {
                    changeStatusBarColor(color = MaterialTheme.colorScheme.surface)

                    StopwatchView(
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.TimerViewDestination.route,
                content = {
                    changeStatusBarColor(color = MaterialTheme.colorScheme.surface)

                    TimerView(
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.WiDViewDestination.route,
                content = {
                    WiDView(
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )
        }
    )
}