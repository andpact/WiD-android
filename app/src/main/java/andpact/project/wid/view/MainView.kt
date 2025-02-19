package andpact.project.wid.view

import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.City
import andpact.project.wid.model.Tool
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate

@Composable
fun MainView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    onStopwatchClicked: () -> Unit,
    onTimerClicked: () -> Unit,
    onWiDClicked: (currentDate: LocalDate) -> Unit,
    onCityPickerClicked: (currentCity: City) -> Unit,
    onUserSignedOut: () -> Unit,
    onUserDeleted: (Boolean) -> Unit,
) {
    val TAG = "MainView"

    // 화면
    val mainViewNavController: NavHostController = rememberNavController()
    val navBackStackEntry by mainViewNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            // 상태 표시줄 여백
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight)
//                    .background(MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Column {
                MainBottomBarView(
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    currentRoute = currentRoute,
                    onDestinationChanged = { route: String ->
                        mainViewNavController.navigate(route) {
                            popUpTo(mainViewNavController.graph.findStartDestination().id) { // 이동할 때, 파라미터(시작점)을 제외하고는 나머지 스택을 삭제함.
                                saveState = true
                            }
                            launchSingleTop = true // 같은 곳으로 이동해도, 스택 최상단에 중복으로 스택이 쌓이지 않도록 함.
                            restoreState = true // 이동할 화면이 이전 화면이면 새로운 스택을 쌓는게 아니라 이전 스택으로 이동함.
                        }
                    },
                    onCurrentToolClicked = { currentTool: Tool ->
                        if (currentTool == Tool.STOPWATCH) {
                            onStopwatchClicked()
                        } else if (currentTool == Tool.TIMER) {
                            onTimerClicked()
                        }
                    }
                )

                // 탐색 메뉴 여백
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navigationBarHeight)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { contentPadding: PaddingValues -> // 이 패딩을 적용하지 않으면 네비게이션 바가 내용물을 덮음.
            NavHost(
                modifier = Modifier
                    .padding(contentPadding),
                navController = mainViewNavController,
                startDestination = MainViewDestinations.HomeViewDestination.route
            ) {
                composable(MainViewDestinations.HomeViewDestination.route) {
                    HomeView(
                        onStopwatchClicked = {
                            onStopwatchClicked()
                        },
                        onTimerClicked = {
                            onTimerClicked()
                        }
                    )
                }

                composable(MainViewDestinations.WiDListViewDestination.route) {
                    WiDListView(
                        onWiDClicked = { currentDate: LocalDate ->
                            onWiDClicked(currentDate)
                        }
                    )
                }

                composable(MainViewDestinations.MyPageViewDestination.route) {
                    MyPageView(
                        onCityPickerClicked = { clickedCity: City ->
                            onCityPickerClicked(clickedCity)
                        },
                        onUserSignedOut = {
                            onUserSignedOut()
                        },
                        onUserDeleted = { userDeleted: Boolean ->
                            onUserDeleted(userDeleted)
                        }
                    )
                }
            }
        }
    )
}