package andpact.project.wid.activity

import andpact.project.wid.R
import andpact.project.wid.fragment.*
import andpact.project.wid.ui.theme.WiDTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // window inset을 수동으로 설정할 때
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WiDMainActivity()
        }
    }
}

@Composable
fun WiDMainActivity() {
    WiDTheme() {
        val navController: NavHostController = rememberNavController()
        val mainTopBottomBarVisible = remember { mutableStateOf(true) }

        Scaffold(
//            bottomBar = {
//                BottomBar(
//                    modifier = Modifier
//                        .imePadding(),
////                        .navigationBarsPadding(), // 화면 하단의 시스템 네비게이션 바 만큼 패딩을 적용함.
//                    navController = navController,
//                    mainTopBottomBarVisible = mainTopBottomBarVisible,
//                )
//            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                NavigationGraph(
                    navController = navController,
                    mainTopBottomBarVisible = mainTopBottomBarVisible
                )
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, mainTopBottomBarVisible: MutableState<Boolean>) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HomeFragmentDestination.route
    ) {
        composable(Destinations.HomeFragmentDestination.route) {
            HomeFragment(navController = navController)
        }
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
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.TimerFragmentDestination.route) {
            TimerFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.NewWiDFragmentDestination.route) {
            NewWiDFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.DateBasedFragmentDestination.route) {
            DateBasedFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.PeriodBasedFragmentDestination.route) {
            PeriodBasedFragment()
        }
        composable(Destinations.SearchFragmentDestination.route) {
            SearchFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.WiDFragmentDestination.route + "/{wiDID}") { backStackEntry ->
            val wiDID = backStackEntry.arguments?.getString("wiDID")?.toLongOrNull() ?: -1L
            WiDFragment(
                wiDId = wiDID,
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.DiaryFragmentDestination.route + "/{date}") { backStackEntry ->
            val date = run {
                val dateString = backStackEntry.arguments?.getString("date") ?: ""
                LocalDate.parse(dateString)
            }
            DiaryFragment(
                date = date,
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
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



sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: Int? = null
) {
    object HomeFragmentDestination : Destinations(
        route = "home_fragment",
        icon = R.drawable.baseline_home_24
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
        icon = R.drawable.baseline_table_rows_24
    )
    object PeriodBasedFragmentDestination : Destinations(
        route = "period_based_fragment",
        icon = R.drawable.baseline_window_24
    )
    object SearchFragmentDestination : Destinations(
        route = "search_fragment",
        icon = R.drawable.baseline_search_24
    )
    object WiDFragmentDestination : Destinations(
        route = "wid_fragment",
    )
    object DiaryFragmentDestination : Destinations(
        route = "diary_fragment",
    )
}

//@Preview(showBackground = true)
//@Composable
//fun WiDMainActivityPreview() {
//    WiDMainActivity()
//}