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
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WiDMainActivity()
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, buttonsVisible: MutableState<Boolean>) {
    NavHost(navController = navController, startDestination = Destinations.WiDCreateHolderFragment.route) {
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
            val wiDId = backStackEntry.arguments?.getString("wiDId")?.toLongOrNull() ?: -1L
//            Log.d("WiDID", wiDId.toString())

            WiDView(wiDId = wiDId, navController = navController, buttonsVisible = buttonsVisible)
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    val screens = listOf(
        Destinations.WiDCreateHolderFragment, Destinations.WiDReadHolderFragment, Destinations.WiDSearchFragment
    )

    AnimatedVisibility(
        visible = state.value,
        enter = expandVertically{ 0 },
        exit = shrinkVertically{ 0 },
    ) {
        NavigationBar(
            modifier = modifier
//                .border(BorderStroke(1.dp, Color.Black))
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
                        Icon(imageVector = screen.icon!!, contentDescription = "")
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
    val icon: ImageVector? = null
) {
    object WiDCreateHolderFragment : Destinations(
        route = "wid_create_holder_fragment",
        title = "Create",
        icon = Icons.Filled.Edit
    )

    object WiDReadHolderFragment : Destinations(
        route = "wid_read_holder_fragment",
        title = "Read",
        icon = Icons.Filled.List
    )

    object WiDSearchFragment : Destinations(
        route = "wid_search_fragment",
        title = "Search",
        icon = Icons.Filled.Search
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