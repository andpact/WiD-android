package andpact.project.wid.activity

import andpact.project.wid.R
import andpact.project.wid.fragment.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import andpact.project.wid.ui.theme.WiDTheme
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            WiDMainActivity()
//        }
        setContent {
            SplashScreen {
                setContent {
                    WiDMainActivity()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashScreenFinished: () -> Unit) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        visible = false
        onSplashScreenFinished()
    }

    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "WiD",
                style = TextStyle(textAlign = TextAlign.Center, fontSize = 70.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(
                    Font(R.font.acme_regular)))
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, buttonsVisible: MutableState<Boolean>) {
    NavHost(navController = navController, startDestination = Destinations.WiDCreateHolderFragment.route) {
        composable(Destinations.WiDCreateHolderFragment.route) {
            WiDCreateHolderFragment(buttonsVisible)
        }
//        composable(Destinations.WiDCreateStopWatchFragment.route) {
//            WiDCreateStopWatchFragment(buttonsVisible = buttonsVisible)
//        }
//        composable(Destinations.WiDCreateTimerFragment.route) {
//            WiDCreateTimerFragment(buttonsVisible = buttonsVisible)
//        }

        composable(Destinations.WiDReadHolderFragment.route) {
            WiDReadHolderFragment(navController, buttonsVisible)
        }
//        composable(Destinations.WiDReadDayFragment.route) {
//            WiDReadDayFragment(navController = navController, buttonsVisible = buttonsVisible)
//        }
//        composable(Destinations.WiDReadWeekFragment.route) {
//            WiDReadWeekFragment()
//        }
//        composable(Destinations.WiDReadMonthFragment.route) {
//            WiDReadMonthFragment()
//        }

        composable(Destinations.WiDSearchFragment.route) {
            WiDSearchFragment(navController, buttonsVisible)
        }
        composable(Destinations.WiDViewFragment.route + "/{wiDId}") { backStackEntry ->
            val wiDId = backStackEntry.arguments?.getString("wiDId")?.toLongOrNull() ?: -1L
            WiDView(wiDId = wiDId, navController = navController, buttonsVisible = buttonsVisible)
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
        NavigationBar(
            modifier = modifier
                .height(55.dp),
            containerColor = Color.Transparent,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            screens.forEach { screen ->
                NavigationBarItem(
                    label = {
                        Text(text = screen.title!!)
                    },
                    alwaysShowLabel = false,
                    icon = {
//                        Icon(imageVector = screen.icon!!, contentDescription = "")
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
                        unselectedTextColor = Color.LightGray, selectedTextColor = Color.Black,
                        unselectedIconColor = Color.LightGray, selectedIconColor = Color.Black,
                        indicatorColor = colorResource(id = R.color.transparent)
                    ),
                )
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
                    navController = navController,
                    state = buttonsVisible,
                )
            }) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
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
        title = "Add",
        icon = R.drawable.baseline_post_add_24
    )

//    object WiDCreateStopWatchFragment : Destinations(
//        route = "wid_create_stop_watch_fragment",
//    )
//
//    object WiDCreateTimerFragment : Destinations(
//        route = "wid_create_timer_fragment",
//    )

    object WiDReadHolderFragment : Destinations(
        route = "wid_read_holder_fragment",
        title = "List",
        icon = R.drawable.baseline_format_list_bulleted_24
    )

//    object WiDReadDayFragment : Destinations(
//        route = "wid_read_day_fragment",
//    )
//
//    object WiDReadWeekFragment : Destinations(
//        route = "wid_read_week_fragment",
//    )
//
//    object WiDReadMonthFragment : Destinations(
//        route = "wid_read_month_fragment",
//    )

    object WiDSearchFragment : Destinations(
        route = "wid_search_fragment",
        title = "Search",
        icon = R.drawable.baseline_search_24
    )

    object WiDViewFragment : Destinations(
        route = "wid_view_fragment",
    )
}

@Preview(showBackground = true)
@Composable
fun WiDMainActivityPreview() {
    WiDMainActivity()
}