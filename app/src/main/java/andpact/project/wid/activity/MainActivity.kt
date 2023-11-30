package andpact.project.wid.activity

import andpact.project.wid.R
import andpact.project.wid.fragment.*
import andpact.project.wid.ui.theme.WiDTheme
import android.app.PendingIntent.getActivity
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
fun NavigationGraph(navController: NavHostController, mainTopBottomBarVisible: MutableState<Boolean>) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HomeFragmentDestination.route,
//        enterTransition = {
//            slideIntoContainer(
//                AnimatedContentTransitionScope.SlideDirection.Left,
//                animationSpec = tween(500)
//            )
//        },
//        exitTransition = {
//            slideOutOfContainer(
//                AnimatedContentTransitionScope.SlideDirection.Right,
//                animationSpec = tween(500)
//            )
//        }
    ) {
        composable(Destinations.HomeFragmentDestination.route) {
            HomeFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.StopWatchFragmentDestination.route) {
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
        composable(Destinations.ManualFragmentDestination.route) {
            ManualFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
        }
        composable(Destinations.ListFragmentDestination.route) {
            ListFragment(
                navController = navController,
                mainTopBottomBarVisible = mainTopBottomBarVisible
            )
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

@Composable
fun BottomBar(navController: NavHostController, mainTopBottomBarVisible: MutableState<Boolean>, modifier: Modifier = Modifier) {
    val destinationList = listOf(Destinations.HomeFragmentDestination, Destinations.ListFragmentDestination, Destinations.SearchFragmentDestination)

    AnimatedVisibility(
        visible = mainTopBottomBarVisible.value,
        enter = expandVertically{ 0 },
        exit = shrinkVertically{ 0 },
    ) {
        Column {
            HorizontalDivider()

            NavigationBar(
                modifier = modifier
                    .height(50.dp),
                containerColor = Color.White,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                destinationList.forEach { destination ->
                    NavigationBarItem(
                        alwaysShowLabel = false,
                        icon = { Icon(painter = painterResource(id = destination.icon!!), contentDescription = "") },
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { // 이동할 때, 파라미터(시작점)을 제외하고는 나머지 스택을 삭제함.
                                    saveState = true
                                }
                                launchSingleTop = true // 같은 곳으로 이동해도, 스택 최상단에 중복으로 스택이 쌓이지 않도록 함.
                                restoreState = true // 이동할 화면이 이전 화면이면 새로운 스택을 쌓는게 아니라 이전 스택으로 이동함.
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedTextColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.LightGray,
                            selectedIconColor = Color.Black,
                            indicatorColor = colorResource(id = R.color.transparent)
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun WiDMainActivity() {
    WiDTheme() {
        val navController: NavHostController = rememberNavController()
        val mainTopBottomBarVisible = remember { mutableStateOf(true) }

        Scaffold(
            bottomBar = {
                BottomBar(
//                    modifier = Modifier
//                        .navigationBarsPadding(), // 화면 하단의 시스템 네비게이션 바 만큼 패딩을 적용함.
                    navController = navController,
                    mainTopBottomBarVisible = mainTopBottomBarVisible,
                )
            }) { paddingValues ->
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
    object ManualFragmentDestination : Destinations(
        route = "manual_fragment",
    )
    object ListFragmentDestination : Destinations(
        route = "list_fragment",
        icon = R.drawable.baseline_format_list_bulleted_24
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