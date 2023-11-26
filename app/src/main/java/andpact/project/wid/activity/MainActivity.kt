package andpact.project.wid.activity

import andpact.project.wid.R
import andpact.project.wid.fragment.*
import andpact.project.wid.ui.theme.WiDTheme
import android.os.Bundle
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

        // window inset(시스템 네비게이션 바가 차지하는 공간)을 자동이 아닌 수동으로 설정하겠다.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WiDMainActivity()
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, buttonsVisible: MutableState<Boolean>) {
    NavHost(
        navController = navController,
        startDestination = Destinations.WiDCreateHolderFragment.route,
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
        composable(Destinations.WiDCreateHolderFragment.route) {
            WiDCreateHolderFragment(buttonsVisible)
        }
        composable(Destinations.WiDReadHolderFragment.route) {
            WiDReadHolderFragment(navController, buttonsVisible)
        }
        composable(Destinations.WiDSearchFragment.route) {
            WiDSearchFragment(navController, buttonsVisible)
        }
        composable(Destinations.WiDViewFragment.route + "/{wiDId}") { backStackEntry ->
            val wiDID = backStackEntry.arguments?.getString("wiDId")?.toLongOrNull() ?: -1L
            WiDView(wiDId = wiDID, navController = navController, buttonsVisible = buttonsVisible)
        }
        composable(Destinations.DiaryFragment.route + "/{date}") { backStackEntry ->
            val date = run {
                val dateString = backStackEntry.arguments?.getString("date") ?: ""
                LocalDate.parse(dateString)
            }
            DiaryFragment(date = date, navController = navController, buttonsVisible = buttonsVisible)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier) {
    val screens = listOf(Destinations.WiDCreateHolderFragment, Destinations.WiDReadHolderFragment, Destinations.WiDSearchFragment)

    AnimatedVisibility(
        visible = state.value,
        enter = expandVertically{ 0 },
        exit = shrinkVertically{ 0 },
    ) {
        Column {
            HorizontalDivider()

            NavigationBar(
                modifier = modifier
                    .height(45.dp),
                containerColor = Color.White,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                screens.forEach { screen ->
                    NavigationBarItem(
                        alwaysShowLabel = false,
                        icon = {
                            Icon(painter = painterResource(id = screen.icon!!), contentDescription = "")
                        },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
        val buttonsVisible = remember { mutableStateOf(true) }

        Scaffold(
            bottomBar = {
                BottomBar(
                    modifier = Modifier
//                        .navigationBarsPadding(), // 화면 하단의 시스템 네비게이션 바 만큼 패딩을 적용함.
                        .windowInsetsPadding( // 좀 더 정교한 위와 같은 방식
                            WindowInsets.navigationBars.only(
                                WindowInsetsSides.Vertical
                            )
                        ),
                    navController = navController,
                    state = buttonsVisible,
                )
            }) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                NavigationGraph(navController = navController, buttonsVisible = buttonsVisible)
            }
        }
    }
}

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: Int? = null
) {
    object WiDCreateHolderFragment : Destinations(
        route = "wid_create_holder_fragment",
//        title = "Add",
        icon = R.drawable.baseline_post_add_24
    )
    object WiDReadHolderFragment : Destinations(
        route = "wid_read_holder_fragment",
//        title = "List",
        icon = R.drawable.baseline_format_list_bulleted_24
    )
    object WiDSearchFragment : Destinations(
        route = "wid_search_fragment",
//        title = "Search",
        icon = R.drawable.baseline_search_24
    )
    object WiDViewFragment : Destinations(
        route = "wid_view_fragment",
    )
    object DiaryFragment : Destinations(
        route = "diary_fragment",
    )
}

//@Preview(showBackground = true)
//@Composable
//fun WiDMainActivityPreview() {
//    WiDMainActivity()
//}