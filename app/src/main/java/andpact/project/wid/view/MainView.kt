package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onSettingButtonPressed: () -> Unit,
    onEmptyWiDClicked: () -> Unit,
    onWiDClicked: () -> Unit,
    onDiaryClicked: (List<WiD>, Diary) -> Unit,
    onHideMainViewBarChanged: (Boolean) -> Unit // Main Activity View의 시스템 상태바, 네비게이션 바 색상 변경 용 콜백
) {
    val TAG = "MainView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    // 화면
    val mainViewNavController: NavHostController = rememberNavController()
    val navBackStackEntry by mainViewNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // MainViewBottomBar에 전달하기 위한 변수(실제 값)
    var hideMainViewBar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        bottomBar = {
            MainBottomBarView(
                currentRoute = currentRoute,
                hideMainViewBar = hideMainViewBar,
                onDestinationChanged = { route ->
                    mainViewNavController.navigate(route) {
                        popUpTo(mainViewNavController.graph.findStartDestination().id) { // 이동할 때, 파라미터(시작점)을 제외하고는 나머지 스택을 삭제함.
                            saveState = true
                        }
                        launchSingleTop = true // 같은 곳으로 이동해도, 스택 최상단에 중복으로 스택이 쌓이지 않도록 함.
                        restoreState = true // 이동할 화면이 이전 화면이면 새로운 스택을 쌓는게 아니라 이전 스택으로 이동함.
                    }
                }
            )
        },
    ) { contentPadding -> // 이 패딩을 적용하지 않으면 네비게이션 바가 내용물을 덮음.
        NavHost(
            modifier = Modifier
                .padding(contentPadding),
            navController = mainViewNavController,
            startDestination = MainViewDestinations.HomeViewDestination.route
        ) {
            // 홈
            composable(MainViewDestinations.HomeViewDestination.route) {
                HomeView(onSettingButtonPressed = {
                    onSettingButtonPressed()
                })
            }

            // WiD 도구
            composable(MainViewDestinations.WiDToolViewDestination.route) {
                WiDToolView(
                    onEmptyWiDClicked = {
                        onEmptyWiDClicked()
                    },
                    onWiDClicked = {
                        onWiDClicked()
                    },
                    onHideWiDToolViewBarChanged =  { hide ->
                        hideMainViewBar = hide
                        onHideMainViewBarChanged(hide)
                    },
                )
            }

            // WiD 조회
            composable(MainViewDestinations.WiDDisplayViewDestination.route) { WiDDisplayView() }

            // 다이어리 조회
            composable(MainViewDestinations.DiaryDisplayViewDestination.route) { DiaryDisplayView(onDiaryClicked = { wiDList, diary -> onDiaryClicked(wiDList, diary) }) }
        }
    }
}

sealed class MainViewDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int
) {
    object HomeViewDestination : MainViewDestinations(
        route = "home_view",
        icon = R.drawable.baseline_home_24
    )
    object WiDToolViewDestination : MainViewDestinations(
        route = "wid_tool_view",
        icon = R.drawable.outline_add_box_24
    )
    object WiDDisplayViewDestination : MainViewDestinations(
        route = "wid_display_view",
        icon = R.drawable.baseline_alarm_24
    )
    object DiaryDisplayViewDestination : MainViewDestinations(
        route = "diary_display_view",
        icon = R.drawable.baseline_calendar_month_24
    )
}

object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}

//@Preview(showBackground = true)
//@Composable
//fun HomeFragmentPreview() {
//    val stopwatchViewModel: StopwatchViewModel = viewModel()
//
//    val context = LocalContext.current
//    val application = context.applicationContext as Application
//    val timerViewModel = TimerViewModel(application)
//
//    HomeFragment(NavController(LocalContext.current), stopwatchViewModel, timerViewModel)
//}