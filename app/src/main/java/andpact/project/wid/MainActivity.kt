package andpact.project.wid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import andpact.project.wid.ui.theme.WiDTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
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
            WiDMainScreen()
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Destinations.WiDCreateScreen.route) {
        composable(Destinations.WiDCreateScreen.route) {
            WiDCreateScreen()
        }
        composable(Destinations.WiDReadScreen.route) {
            WiDReadScreen()
        }
        composable(Destinations.WiDSearchScreen.route) {
            WiDSearchScreen()
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController, state: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    val screens = listOf(
        Destinations.WiDCreateScreen, Destinations.WiDReadScreen, Destinations.WiDSearchScreen
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
fun WiDMainScreen() {
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
    object WiDCreateScreen : Destinations(
        route = "wid_create_screen",
        title = "Create",
        icon = Icons.Filled.Add
    )

    object WiDReadScreen : Destinations(
        route = "wid_read_screen",
        title = "Read",
        icon = Icons.Filled.List
    )

    object WiDSearchScreen : Destinations(
        route = "wid_search_screen",
        title = "Search",
        icon = Icons.Filled.Search
    )

}

@Preview(showBackground = true)
@Composable
fun WiDMainScreenPreview() {
    WiDMainScreen()
}