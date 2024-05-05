package andpact.project.wid.view

import andpact.project.wid.activity.MainActivityViewDestinations
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
import java.time.LocalDate
import java.time.LocalTime

/**
 * 컴포저블 - Jetpack Compose에서 화면을 그리는 데 사용되는 함수,
 * 컴포넌트 - 서비스을 구성하는 블록(ui과 관련이 있는 액티비티와 그렇지 않은 서비스 클래스까지 포함)
 */
@Composable // 네비게이션 그래프에는 NavController가 아닌 NavHostController가 파라미터로 들어감.
fun MainActivityView(dynamicLink: String?) {
    // 네비게이션
    val mainActivityViewNavController: NavHostController = rememberNavController()

    // 메인 화면으로 전환 시 상태 바, 네비게이션 바 색상 변경해줌.
//    changeStatusBarAndNavigationBarColor(color = MaterialTheme.colorScheme.tertiary) // Gray - DarkGray

    var hideMainActivityBar by remember { mutableStateOf(false) }

    // LaunchedEffect 사용해도 될려나? -> 안됨.
    // 스톱워치나 타이머 상하단 바 제거할 때, 네비게이션 바 색상도 같이 변경함.
//    if (stopwatchTopBottomBarVisible && timerTopBottomBarVisible) {
//        changeStatusBarAndNavigationBarColor(color = MaterialTheme.colorScheme.tertiary) // Gray - DarkGray
//    } else {
//        changeStatusBarAndNavigationBarColor(color = MaterialTheme.colorScheme.secondary) // White - Black
//    }

    val navBackStackEntry by mainActivityViewNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 화면 전환 시 상태 바, 네비게이션 바 색상을 변경함.
//    if (currentRoute == MainActivityViewDestinations.MainViewDestination.route || currentRoute == MainActivityViewDestinations.SettingViewDestination.route) {
//        changeStatusBarAndNavigationBarColor(color = MaterialTheme.colorScheme.tertiary) // Gray - DarkGray
//    } else {
//        changeStatusBarAndNavigationBarColor(color = MaterialTheme.colorScheme.secondary) // White - Black
//    }

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
                onEmailLinkVerified = { emailLinkVerified ->
                    if (emailLinkVerified) { // 이메일 링크 인증 완료
                        mainActivityViewNavController.navigate(MainActivityViewDestinations.MainViewDestination.route)
                    } else { // 이메일 링크 인증 실패
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

        // 회원가입 뷰
//        composable(
//            route = MainActivityViewDestinations.SignUpViewDestination.route,
//        ) {
//            SignUpView(
//                onSignInButtonPressed = {
//                    mainActivityViewNavController.navigate(MainActivityViewDestinations.SignInViewDestination.route)
//                },
//            )
//        }

        // 로그인 뷰
//        composable(
//            route = MainActivityViewDestinations.SignInViewDestination.route,
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
//        ) {
//            SignInView(
//                onBackButtonPressed = {
//                    mainActivityViewNavController.popBackStack()
//                },
//            )
//        }

        // 메인 뷰
        composable(
            route = MainActivityViewDestinations.MainViewDestination.route,
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
        ) {
            MainView(
                onSettingButtonPressed = {
                    mainActivityViewNavController.navigate(MainActivityViewDestinations.SettingViewDestination.route)
                },
                onEmptyWiDClicked = {
                    mainActivityViewNavController.navigate(MainActivityViewDestinations.NewWiDViewDestination.route)
                },
                onWiDClicked = {
                    mainActivityViewNavController.navigate(MainActivityViewDestinations.WiDViewDestination.route)
                },
                onDiaryClicked = {
                    wiDList, diary -> mainActivityViewNavController.navigate(MainActivityViewDestinations.WiDViewDestination.route + "/${wiDList}" + "/${diary}")
                },
                onHideMainViewBarChanged = { hide ->
                    hideMainActivityBar = hide
                },
//                onStopwatchTopBottomBarVisible = {
//                    visible -> stopwatchTopBottomBarVisible = visible
//                },
//                onTimerTopBottomBarVisibleChanged = {
//                    visible -> timerTopBottomBarVisible = visible
//                }
            )
        }

        // 새로운 WiD 뷰
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
            EmptyWiDView(
                onBackButtonPressed = {
                    mainActivityViewNavController.popBackStack()
                },
            )
        }

        // 클릭된 WiD 뷰
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
            ClickedWiDView(
                onBackButtonPressed = {
                    mainActivityViewNavController.popBackStack()
                }
            )
        }

        // 다이어리 뷰
        composable(
            route = MainActivityViewDestinations.DiaryViewDestination.route + "/{wiDList}/{diary}",
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
        ) { backStackEntry ->
            // 어떻게 역직렬화 시킬 건가?
            val wiDListString = backStackEntry.arguments?.getString("wiDList")
            val diaryString = backStackEntry.arguments?.getString("diary")

            val date = run {
                val dateString = backStackEntry.arguments?.getString("date") ?: ""
                LocalDate.parse(dateString)
            }
            DiaryView(
                onBackButtonPressed = { mainActivityViewNavController.popBackStack() },
                date = date,
            )
        }

        // 환경설정 뷰
        composable(
            route = MainActivityViewDestinations.SettingViewDestination.route,
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
            SettingView(
                onBackButtonPressed = {
                    mainActivityViewNavController.popBackStack()
                },
                onUserSignedOut = { userSignedOut: Boolean ->
                    if (userSignedOut) {
                        mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                    }
                },
                onUserDeleted = { userDeleted: Boolean ->
                    if (userDeleted) {
                        mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                    }
                }
            )
        }
    }
}