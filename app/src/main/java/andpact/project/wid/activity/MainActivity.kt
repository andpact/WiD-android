package andpact.project.wid.activity

import andpact.project.wid.fragment.DiaryFragment
import andpact.project.wid.fragment.MainFragment
import andpact.project.wid.fragment.NewWiDFragment
import andpact.project.wid.fragment.WiDFragment
import andpact.project.wid.ui.theme.WiDTheme
import andpact.project.wid.viewModel.StopwatchViewModel
import andpact.project.wid.viewModel.TimerViewModel
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // window inset(상태 바, 네비게이션 바 패딩)을 수동으로 설정할 때
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainScreen()
        }
    }
}

/**
 * 컴포저블 - Jetpack Compose에서 화면을 그리는 데 사용되는 함수
 * 컴포넌트 - 소프트웨어 개발에서 재사용 가능한 모듈
 */
@Composable
fun MainScreen() {
    WiDTheme() {
        val mainActivityNavController: NavHostController = rememberNavController()

        // 아래 두 방식의 차이가 없다?
//        val stopwatchViewModel = StopwatchViewModel()
        val stopwatchViewModel: StopwatchViewModel = viewModel()

//        val context = LocalContext.current
//        val application = context.applicationContext as Application
//        val timerViewModel = TimerViewModel(application)
        val timerViewModel: TimerViewModel = viewModel()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MainActivityNavigationGraph(mainActivityNavController = mainActivityNavController, stopwatchViewModel = stopwatchViewModel, timerViewModel = timerViewModel)
        }
    }
}

@Composable // 네비게이션 그래프에는 NavController가 아닌 NavHostController가 파라미터로 들어감.
fun MainActivityNavigationGraph(mainActivityNavController: NavHostController, stopwatchViewModel: StopwatchViewModel, timerViewModel: TimerViewModel) {
    NavHost(
        navController = mainActivityNavController,
        startDestination = MainActivityDestinations.MainFragmentDestination.route
    ) {
        // 메인 프래그먼트
        composable(MainActivityDestinations.MainFragmentDestination.route) {
            MainFragment(mainActivityNavController = mainActivityNavController, stopwatchViewModel = stopwatchViewModel, timerViewModel = timerViewModel)
        }

        // 새로운 WiD
        composable(
            route = MainActivityDestinations.NewWiDFragmentDestination.route + "/{startParam}/{finishParam}",
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
            val startParam = run {
                val startString = backStackEntry.arguments?.getString("startParam") ?: LocalTime.MIN.toString()
                LocalTime.parse(startString)
            }
            val finishParam = run {
                val finishString = backStackEntry.arguments?.getString("finishParam") ?: LocalTime.MIN.toString()
                LocalTime.parse(finishString)
            }

            NewWiDFragment(
                mainActivityNavController = mainActivityNavController,
                startParam = startParam,
                finishParam = finishParam
            )
        }

        // WiD
        composable(
            route = MainActivityDestinations.WiDFragmentDestination.route + "/{wiDID}",
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
            val wiDID = backStackEntry.arguments?.getString("wiDID")?.toLongOrNull() ?: -1L
            WiDFragment(
                wiDId = wiDID,
                mainActivityNavController = mainActivityNavController,
            )
        }

        // 다이어리
        composable(
            route = MainActivityDestinations.DiaryFragmentDestination.route + "/{date}",
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
            val date = run {
                val dateString = backStackEntry.arguments?.getString("date") ?: ""
                LocalDate.parse(dateString)
            }
            DiaryFragment(
                date = date,
                mainActivityNavController = mainActivityNavController,
                stopwatchViewModel = stopwatchViewModel,
                timerViewModel = timerViewModel
            )
        }
    }
}

sealed class MainActivityDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int? = null
) {
    object MainFragmentDestination : MainActivityDestinations(
        route = "main_fragment",
    )
    object NewWiDFragmentDestination : MainActivityDestinations(
        route = "newWiD_fragment",
    )
    object WiDFragmentDestination : MainActivityDestinations(
        route = "wid_fragment",
    )
    object DiaryFragmentDestination : MainActivityDestinations(
        route = "diary_fragment",
    )
}

//@Composable
//fun SplashFragment() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.secondary),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = "WiD",
//            style = TextStyle(
//                color = MaterialTheme.colorScheme.primary,
//                fontSize = 70.sp,
//                fontWeight = FontWeight.Bold,
//                fontFamily = acmeRegular
//            )
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun WiDMainActivityPreview() {
//    WiDMainActivity()
//}