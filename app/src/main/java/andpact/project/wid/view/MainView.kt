package andpact.project.wid.view

import andpact.project.wid.destinations.MainViewDestinations
import andpact.project.wid.model.CurrentTool
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * 정확하지 않다. 다시 배우자.
 * 값 변경 시
 * remember { 값 }-> 재 렌더링 안됨.
 * remember(파라미터) { 값 } 의 파라미터가 변경되면 블록('{ 값 }')을 재 실행하여 재 랜더링 됨.
 * mutableStateOf { 값 }-> 재 렌더링 됨.
 */
@Composable
fun MainView(
    onStopwatchClicked: () -> Unit,
    onTimerClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    onUserSignedOut: () -> Unit,
    onUserDeleted: (Boolean) -> Unit,
) {
    val TAG = "MainView"

    // 화면
    val mainViewNavController: NavHostController = rememberNavController()
    val navBackStackEntry by mainViewNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
//            .background(MaterialTheme.colorScheme.secondary),
        bottomBar = {
            MainBottomBarView(
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
                onCurrentToolClicked = { currentTool: CurrentTool ->
                    if (currentTool == CurrentTool.STOPWATCH) {
                        onStopwatchClicked()
                    } else if (currentTool == CurrentTool.TIMER) {
                        onTimerClicked()
                    }
                }
            )
        },
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
                        onWiDClicked = {
                            onWiDClicked()
                        },
                    )
                }

                composable(MainViewDestinations.MyPageViewDestination.route) {
                    MyPageView(
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