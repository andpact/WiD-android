package andpact.project.wid.activity

import andpact.project.wid.fragment.*
import andpact.project.wid.ui.theme.LimeGreen
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.WiDTheme
import andpact.project.wid.util.*
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // window inset(상태 바, 네비게이션 바 패딩)을 수동으로 설정할 때
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainFragment()
        }
    }
}

/**
 * 컴포저블 - Jetpack Compose에서 화면을 그리는 데 사용되는 함수
 * 컴포넌트 - 소프트웨어 개발에서 재사용 가능한 모듈
 */
@Composable
fun MainFragment() {
    WiDTheme() {
        val navController: NavHostController = rememberNavController()
//        val stopwatchPlayer = StopwatchPlayer()

        val stopwatchPlayer: StopwatchPlayer = viewModel()

        val context = LocalContext.current
        val application = context.applicationContext as Application
        val timerPlayer = TimerPlayer(application)

        Scaffold(
            topBar = {
                TopBar(stopwatchPlayer = stopwatchPlayer, timerPlayer = timerPlayer)
            },
//            containerColor = MaterialTheme.colorScheme.secondary
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                NavigationGraph(navController = navController, stopwatchPlayer = stopwatchPlayer, timerPlayer = timerPlayer)
            }
        }
    }
}

@Composable
fun TopBar(stopwatchPlayer: StopwatchPlayer, timerPlayer: TimerPlayer) {
    AnimatedVisibility(
        visible = !stopwatchPlayer.inStopwatchView.value && stopwatchPlayer.stopwatchState.value != PlayerState.Stopped,
        enter = expandVertically{ 0 },
        exit = shrinkVertically{ 0 },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(
                    if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                        LimeGreen
                    } else if (stopwatchPlayer.stopwatchState.value == PlayerState.Paused) {
                        OrangeRed
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                text = "스톱 워치",
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = titleMap[stopwatchPlayer.title.value] ?: "공부",
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
//                text = getHorizontalTimeString(stopwatchPlayer.elapsedTime.value),
                text = getDurationString(stopwatchPlayer.duration.value, 1),
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
        }
    }

    AnimatedVisibility(
        visible = !timerPlayer.inTimerView.value && timerPlayer.timerState.value != PlayerState.Stopped,
        enter = expandVertically{ 0 },
        exit = shrinkVertically{ 0 },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(
                    if (timerPlayer.timerState.value == PlayerState.Started) {
                        LimeGreen
                    } else if (timerPlayer.timerState.value == PlayerState.Paused) {
                        OrangeRed
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                text = "타이머",
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = titleMap[timerPlayer.title.value] ?: "공부",
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                text = getHorizontalTimeString(timerPlayer.remainingTime.value.seconds),
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

//@Composable
//fun BottomBar(navController: NavHostController, mainTopBottomBarVisible: MutableState<Boolean>, modifier: Modifier = Modifier) {
////    val destinationList = listOf(Destinations.HomeFragmentDestination, Destinations.ListFragmentDestination, Destinations.SearchFragmentDestination)
//    val destinationList = listOf(Destinations.HomeFragmentDestination, Destinations.DateBasedFragmentDestination, Destinations.PeriodBasedFragmentDestination, Destinations.SearchFragmentDestination)
//
//    AnimatedVisibility(
//        visible = mainTopBottomBarVisible.value,
//        enter = expandVertically{ 0 },
//        exit = shrinkVertically{ 0 },
//    ) {
////    if (mainTopBottomBarVisible.value) { // 애니메이션 없이 바텀 네비게이션 바를 없애서 하면 전환시 불필요한 애니메이션 없앰.
//        Column {
//            HorizontalDivider()
//
//            NavigationBar(
//                modifier = modifier
//                    .height(50.dp),
//                containerColor = White,
//            ) {
//                val navBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentRoute = navBackStackEntry?.destination?.route
//
//                destinationList.forEach { destination ->
//                    NavigationBarItem(
//                        alwaysShowLabel = false,
//                        icon = { Icon(painter = painterResource(id = destination.icon!!), contentDescription = "") },
//                        selected = currentRoute == destination.route,
//                        onClick = {
//                            navController.navigate(destination.route) {
//                                popUpTo(navController.graph.findStartDestination().id) { // 이동할 때, 파라미터(시작점)을 제외하고는 나머지 스택을 삭제함.
//                                    saveState = true
//                                }
//                                launchSingleTop = true // 같은 곳으로 이동해도, 스택 최상단에 중복으로 스택이 쌓이지 않도록 함.
//                                restoreState = true // 이동할 화면이 이전 화면이면 새로운 스택을 쌓는게 아니라 이전 스택으로 이동함.
//                            }
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            unselectedTextColor = LightGray,
//                            selectedTextColor = Black,
//                            unselectedIconColor = LightGray,
//                            selectedIconColor = Black,
//                            indicatorColor = White
//                        ),
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun NavigationGraph(navController: NavHostController, stopwatchPlayer: StopwatchPlayer, timerPlayer: TimerPlayer) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HomeFragmentDestination.route
    ) {
        // 홈
        composable(Destinations.HomeFragmentDestination.route) {
            HomeFragment(navController = navController, stopwatchPlayer = stopwatchPlayer, timerPlayer = timerPlayer)
        }

        // 스톱 워치
        composable(
            route = Destinations.StopWatchFragmentDestination.route,
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
            StopWatchFragment(
                navController = navController,
                stopwatchPlayer = stopwatchPlayer
            )
        }

        // 타이머
        composable(
            route = Destinations.TimerFragmentDestination.route,
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
            TimerFragment(
                navController = navController,
                timerPlayer = timerPlayer
            )
        }

        // 새로운 WiD
        composable(
            route = Destinations.NewWiDFragmentDestination.route,
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
            NewWiDFragment(navController = navController)
        }

        // 날짜 별 조회
        composable(
            route = Destinations.DateBasedFragmentDestination.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = { null },
            exitTransition = { null },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            },
        ) {
            DateBasedFragment(navController = navController)
        }

        // 기간 별 조회
        composable(
            route = Destinations.PeriodBasedFragmentDestination.route,
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
            PeriodBasedFragment(navController = navController)
        }

        // 다이어리 검색
        composable(
            route = Destinations.SearchFragmentDestination.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = { null },
            exitTransition = { null },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            },
        ) {
            SearchFragment(navController = navController)
        }

        // WiD
        composable(
            route = Destinations.WiDFragmentDestination.route + "/{wiDID}",
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
                navController = navController,
            )
        }

        // 다이어리
        composable(
            route = Destinations.DiaryFragmentDestination.route + "/{date}",
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
                navController = navController,
            )
        }
    }
}

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: Int? = null
) {
    object HomeFragmentDestination : Destinations(
        route = "home_fragment",
    )
    object StopWatchFragmentDestination : Destinations(
        route = "stopwatch_fragment",
    )
    object TimerFragmentDestination : Destinations(
        route = "timer_fragment",
    )
    object NewWiDFragmentDestination : Destinations(
        route = "newWiD_fragment",
    )
    object DateBasedFragmentDestination : Destinations(
        route = "date_based_fragment",
    )
    object PeriodBasedFragmentDestination : Destinations(
        route = "period_based_fragment",
    )
    object SearchFragmentDestination : Destinations(
        route = "search_fragment",
    )
    object WiDFragmentDestination : Destinations(
        route = "wid_fragment",
    )
    object DiaryFragmentDestination : Destinations(
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