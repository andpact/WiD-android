package andpact.project.wid.view

import andpact.project.wid.destinations.MainActivityViewDestinations
import andpact.project.wid.ui.theme.changeNavigationBarColor
import andpact.project.wid.ui.theme.changeStatusBarColor
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * 컴포저블 - Jetpack Compose에서 화면을 그리는 데 사용되는 함수,
 * 컴포넌트 - 서비스을 구성하는 블록(ui과 관련이 있는 액티비티와 그렇지 않은 서비스 클래스까지 포함)
 */
@Composable // 네비게이션 그래프에는 NavController가 아닌 NavHostController가 파라미터로 들어감.
fun MainActivityView(dynamicLink: String?) {
    // 네비게이션
    val mainActivityViewNavController: NavHostController = rememberNavController()

    var mainActivityBarVisible by remember { mutableStateOf(true) }

    if (mainActivityBarVisible) {
        changeStatusBarColor(color = MaterialTheme.colorScheme.secondaryContainer)
        changeNavigationBarColor(color = MaterialTheme.colorScheme.surfaceContainer)
    } else {
        changeStatusBarColor(color = MaterialTheme.colorScheme.surface)
        changeNavigationBarColor(color = MaterialTheme.colorScheme.surface)
    }

//    val navBackStackEntry by mainActivityViewNavController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = mainActivityViewNavController,
//        startDestination = MainActivityViewDestinations.SplashViewDestination.route
        startDestination = MainActivityViewDestinations.MainViewDestination.route
    ) {
        // 스플래쉬 뷰
        composable(
            route = MainActivityViewDestinations.SplashViewDestination.route,
        ) {
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

        // 인증 뷰
        composable(
            route = MainActivityViewDestinations.AuthenticationViewDestination.route,
        ) {
            AuthenticationView()
        }

        // 메인 뷰
        composable(
            route = MainActivityViewDestinations.MainViewDestination.route,
        ) {
            MainView(
                onNewWiDClicked = {
                    mainActivityViewNavController.navigate(MainActivityViewDestinations.NewWiDViewDestination.route)
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
                },
                onMainViewBarVisibleChanged = { visible ->
                    mainActivityBarVisible = visible
                },
            )
        }

        // NewWiDView
        composable(
            route = MainActivityViewDestinations.NewWiDViewDestination.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            }
        ) {
            NewWiDView(
                onBackButtonPressed = {
                    mainActivityViewNavController.popBackStack()
                },
            )
        }

        // WiD 뷰
        composable(
            route = MainActivityViewDestinations.WiDViewDestination.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            }
        ) {
            WiDView(
                onBackButtonPressed = {
                    mainActivityViewNavController.popBackStack()
                }
            )
        }

        // 다이어리 뷰
//        composable(
//            route = MainActivityViewDestinations.DiaryViewDestination.route + "/{wiDList}/{diary}",
//            enterTransition = {
//                slideIntoContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Left,
//                    animationSpec = tween(500)
//                )
//            },
//            exitTransition = {
//                slideOutOfContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Right,
//                    animationSpec = tween(500)
//                )
//            }
//        ) { backStackEntry ->
//            // 어떻게 역직렬화 시킬 건가?
//            val wiDListString = backStackEntry.arguments?.getString("wiDList")
//            val diaryString = backStackEntry.arguments?.getString("diary")
//
//            val date = run {
//                val dateString = backStackEntry.arguments?.getString("date") ?: ""
//                LocalDate.parse(dateString)
//            }
//            DiaryView(
//                onBackButtonPressed = { mainActivityViewNavController.popBackStack() },
//                date = date,
//            )
//        }
    }
}