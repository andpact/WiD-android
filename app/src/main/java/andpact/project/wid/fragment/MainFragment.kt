package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.changeStatusBarAndNavigationBarColor
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.HomeViewModel
import andpact.project.wid.viewModel.StopwatchViewModel
import andpact.project.wid.viewModel.TimerViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
fun MainFragment(mainActivityNavController: NavController, stopwatchViewModel: StopwatchViewModel, timerViewModel: TimerViewModel) {

    DisposableEffect(Unit) {
        Log.d("MainFragment", "MainFragment is being composed")

        onDispose {
            Log.d("MainFragment", "MainFragment is being disposed")
        }
    }

    // 화면
    val mainFragmentNavController: NavHostController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
//        topBar = {
//            MainFragmentTopBar(
//                mainFragmentNavController = mainFragmentNavController,
//                stopwatchViewModel = stopwatchViewModel,
//                timerViewModel = timerViewModel
//            )},
        bottomBar = {
            MainFragmentBottomBar(
                mainFragmentNavController = mainFragmentNavController,
                stopwatchViewModel = stopwatchViewModel,
                timerViewModel = timerViewModel
            )},
    ) { contentPadding -> // 이 패딩을 적용하지 않으면 네비게이션 바가 내용물을 덮음.
        Box(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            MainFragmentNavigationGraph(
                mainActivityNavController = mainActivityNavController,
                mainFragmentNavController = mainFragmentNavController,
                stopwatchViewModel = stopwatchViewModel,
                timerViewModel = timerViewModel
            )
        }
    }
}

//@Composable
//fun MainFragmentTopBar(mainFragmentNavController: NavController, stopwatchViewModel: StopwatchViewModel, timerViewModel: TimerViewModel) {
//    val navBackStackEntry by mainFragmentNavController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    val title = when (currentRoute) {
//        MainFragmentDestinations.HomeFragmentDestination.route -> "홈"
//        MainFragmentDestinations.WiDToolFragmentDestination.route -> "WiD 도구"
//        MainFragmentDestinations.WiDDisplayFragmentDestination.route -> "WiD 조회"
//        MainFragmentDestinations.DiaryDisplayFragmentDestination.route -> "다이어리 조회"
//        MainFragmentDestinations.SettingFragmentDestination.route -> "환경 설정"
//        else -> "" // 이 경우에 대한 기본값 설정
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp)
//            .background(MaterialTheme.colorScheme.secondary)
//            .alpha(
//                if (stopwatchViewModel.stopwatchTopBottomBarVisible.value && timerViewModel.timerTopBottomBarVisible.value) {
//                    1f
//                } else {
//                    0f
//                }
//            )
//            .padding(horizontal = 16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = title,
//            style = Typography.titleLarge,
//            color = MaterialTheme.colorScheme.primary
//        )
//    }
//}

@Composable
fun MainFragmentBottomBar(mainFragmentNavController: NavController, stopwatchViewModel: StopwatchViewModel, timerViewModel: TimerViewModel) {
    val navBackStackEntry by mainFragmentNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val destinationList = listOf(
        MainFragmentDestinations.HomeFragmentDestination,
        MainFragmentDestinations.WiDToolFragmentDestination,
        MainFragmentDestinations.WiDDisplayFragmentDestination,
        MainFragmentDestinations.DiaryDisplayFragmentDestination,
//        MainFragmentDestinations.SettingFragmentDestination
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .alpha(
                if (stopwatchViewModel.stopwatchTopBottomBarVisible.value && timerViewModel.timerTopBottomBarVisible.value) {
                    1f
                } else {
                    0f
                }
            )
    ) {
        if (stopwatchViewModel.stopwatchState.value != PlayerState.Stopped && currentRoute != MainFragmentDestinations.WiDToolFragmentDestination.route) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorMap[stopwatchViewModel.title.value] ?: DarkGray)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
//                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(8.dp)
                        .size(24.dp),
                    painter = painterResource(titleIconMap[stopwatchViewModel.title.value] ?: R.drawable.baseline_menu_book_16),
                    contentDescription = "제목",
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = titleMap[stopwatchViewModel.title.value] ?: "공부",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getDurationString(stopwatchViewModel.duration.value, 0),
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (stopwatchViewModel.stopwatchState.value == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                enabled = stopwatchViewModel.stopwatchState.value == PlayerState.Paused,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                stopwatchViewModel.stopStopwatch()
                            }
//                            .padding(16.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "스톱워치 초기화",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { // 스톱 워치 모든 상태일 때 클릭 가능
                            if (stopwatchViewModel.stopwatchState.value == PlayerState.Started) { // 스톱 워치 시작 상태
                                stopwatchViewModel.pauseStopwatch()
                            } else { // 스톱 워치 중지, 정지 상태
                                stopwatchViewModel.startStopwatch()
                            }
                        }
//                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(
                        id = if (stopwatchViewModel.stopwatchState.value == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "스톱 워치 시작 및 중지",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else if (timerViewModel.timerState.value != PlayerState.Stopped && currentRoute != MainFragmentDestinations.WiDToolFragmentDestination.route) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorMap[timerViewModel.title.value] ?: DarkGray)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
//                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(8.dp)
                        .size(24.dp),
                    painter = painterResource(titleIconMap[timerViewModel.title.value] ?: R.drawable.baseline_menu_book_16),
                    contentDescription = "제목",
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = titleMap[timerViewModel.title.value] ?: "공부",
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = getDurationString(timerViewModel.remainingTime.value, 0),
                        style = Typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (timerViewModel.timerState.value == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                enabled = timerViewModel.timerState.value == PlayerState.Paused,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                timerViewModel.stopTimer()
                            }
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = "타이머 초기화",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { // 타이머 모든 상태일 때 클릭 가능
                            if (timerViewModel.timerState.value == PlayerState.Started) { // 타이머 시작 상태
                                timerViewModel.pauseTimer()
                            } else { // 타이머 중지, 정지 상태
                                timerViewModel.startTimer()
                            }
                        }
                        .size(24.dp),
                    painter = painterResource(
                        id = if (timerViewModel.timerState.value == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "타이머 시작 및 중지",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) { // 리플 제거용
            NavigationBar(
                modifier = Modifier
                    .height(56.dp),
    //            containerColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.tertiary,
            ) {
//                val navBackStackEntry by mainFragmentNavController.currentBackStackEntryAsState()
//                val currentRoute = navBackStackEntry?.destination?.route

                destinationList.forEach { destination ->
                    NavigationBarItem(
                        alwaysShowLabel = false,
                        icon = {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                painter = painterResource(id = destination.icon),
                                contentDescription = ""
                            ) },
                        selected = currentRoute == destination.route,
                        onClick = {
                            mainFragmentNavController.navigate(destination.route) {
                                popUpTo(mainFragmentNavController.graph.findStartDestination().id) { // 이동할 때, 파라미터(시작점)을 제외하고는 나머지 스택을 삭제함.
                                    saveState = true
                                }
                                launchSingleTop = true // 같은 곳으로 이동해도, 스택 최상단에 중복으로 스택이 쌓이지 않도록 함.
                                restoreState = true // 이동할 화면이 이전 화면이면 새로운 스택을 쌓는게 아니라 이전 스택으로 이동함.
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedTextColor = DarkGray,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = DarkGray,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary
                        ),
                        enabled = stopwatchViewModel.stopwatchTopBottomBarVisible.value && timerViewModel.timerTopBottomBarVisible.value
                    )
                }
            }
        }
    }
}

@Composable
fun MainFragmentNavigationGraph(
    mainActivityNavController: NavController,
    mainFragmentNavController: NavHostController,
    stopwatchViewModel: StopwatchViewModel,
    timerViewModel: TimerViewModel
) {
    NavHost(
        navController = mainFragmentNavController,
        startDestination = MainFragmentDestinations.HomeFragmentDestination.route
    ) {
        // 홈
        composable(MainFragmentDestinations.HomeFragmentDestination.route) {
            HomeFragment(mainActivityNavController = mainActivityNavController)
        }

        // WiD 도구
        composable(MainFragmentDestinations.WiDToolFragmentDestination.route) {
            WiDToolFragment(
                mainActivityNavController = mainActivityNavController,
                stopwatchViewModel = stopwatchViewModel,
                timerViewModel = timerViewModel
            )
        }

        // WiD 조회
        composable(MainFragmentDestinations.WiDDisplayFragmentDestination.route) {
            WiDDisplayFragment()
        }

        // 다이어리 조회
        composable(MainFragmentDestinations.DiaryDisplayFragmentDestination.route) {
            DiaryDisplayFragment(mainActivityNavController = mainActivityNavController)
        }
    }
}

sealed class MainFragmentDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int
) {
    object HomeFragmentDestination : MainFragmentDestinations(
        icon = R.drawable.baseline_home_24,
        route = "home_fragment",
    )
    object WiDToolFragmentDestination : MainFragmentDestinations(
        icon = R.drawable.outline_add_box_24,
        route = "wid_tool_fragment",
    )
    object WiDDisplayFragmentDestination : MainFragmentDestinations(
        icon = R.drawable.baseline_alarm_24,
        route = "wid_display_fragment",
    )
    object DiaryDisplayFragmentDestination : MainFragmentDestinations(
        icon = R.drawable.baseline_calendar_month_24,
        route = "diary_display_fragment",
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