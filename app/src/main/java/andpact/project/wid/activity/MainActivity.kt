package andpact.project.wid.activity

import andpact.project.wid.fragment.WiDCreateFragment
import andpact.project.wid.fragment.WiDReadHolderFragment
import andpact.project.wid.fragment.WiDSearchFragment
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import andpact.project.wid.ui.theme.WiDTheme
import andpact.project.wid.fragment.WiDView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Destinations.WiDCreateFragment.route) {
        composable(Destinations.WiDCreateFragment.route) {
            WiDCreateFragment()
        }
        composable(Destinations.WiDReadHolderFragment.route) {
            WiDReadHolderFragment(navController)
        }
        composable(Destinations.WiDSearchFragment.route) {
            WiDSearchFragment(navController)
        }
        composable(Destinations.WiDViewFragment.route + "/{wiDId}") { backStackEntry ->
            val wiDId = backStackEntry.arguments?.getString("wiDId")?.toLongOrNull() ?: -1L
//            Log.d("WiDID", wiDId.toString())
            WiDView(wiDId = wiDId, navController = navController)
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    val screens = listOf(
        Destinations.WiDCreateFragment, Destinations.WiDReadHolderFragment, Destinations.WiDSearchFragment
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.LightGray,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->

            NavigationBarItem(
                label = {
                    Text(text = screen.title!!)
                },
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
                    unselectedTextColor = Color.Gray, selectedTextColor = Color.White
                ),
            )
        }
    }

}

@Composable
fun WiDMainActivity() {
    WiDTheme() {
        val navController: NavHostController = rememberNavController()
//        val bottomBarHeight = 56.dp
//        val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

        val buttonsVisible = remember { mutableStateOf(true) }

        Scaffold(
            bottomBar = {
                BottomBar(
                    navController = navController,
                    state = buttonsVisible,
                    modifier = Modifier
                )
            }) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                NavigationGraph(navController = navController)
            }
        }
    }
}

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object WiDCreateFragment : Destinations(
        route = "wid_create_fragment",
        title = "Create",
        icon = Icons.Filled.Add
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