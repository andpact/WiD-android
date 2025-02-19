package andpact.project.wid.view

import andpact.project.wid.destinations.MainActivityViewDestinations
import andpact.project.wid.model.City
import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.*
import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun MainActivityView(dynamicLink: String?) {
    val TAG = "MainActivityView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val mainActivityViewNavController: NavHostController = rememberNavController() // 네비게이션 그래프에는 NavController가 아닌 NavHostController가 파라미터로 들어감.

    val (statusBarHeight, navigationBarHeight) = rememberSystemBarHeights()

    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = mainActivityViewNavController,
        startDestination = MainActivityViewDestinations.SplashViewDestination.route,
        builder = {
            composable(
                route = MainActivityViewDestinations.SplashViewDestination.route,
                content = {
                    SplashView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        dynamicLink = dynamicLink,
                        onEmailLinkVerified = { emailLinkVerified: Boolean ->
                            if (emailLinkVerified) { // 이메일 링크 인증 완료 -> 메인 뷰로 전환
                                mainActivityViewNavController.navigate(MainActivityViewDestinations.MainViewDestination.route)
                            } else { // 이메일 링크 인증 실패 -> 인증 뷰로 전환
                                mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                            }
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.AuthenticationViewDestination.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = 500))
                },
                content = {
                    AuthenticationView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.MainViewDestination.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = 500))
                },
                content = {
                    MainView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        onStopwatchClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.StopwatchViewDestination.route)
                        },
                        onTimerClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TimerViewDestination.route)
                        },
                        onWiDClicked = { currentDate: LocalDate ->
                            val currentDateString = currentDate.toString()
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.WiDViewDestination.route + "/$currentDateString")
                        },
                        onCityPickerClicked = { clickedCity: City ->
                            val previousViewString = PreviousView.USER_CITY.name
                            val currentCityString = clickedCity.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.CityPickerViewDestination.route + "/$previousViewString" + "/$currentCityString")
                        },
                        onUserSignedOut = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                        },
                        onUserDeleted = { userDeleted: Boolean ->
                            if (userDeleted) {
                                mainActivityViewNavController.navigate(MainActivityViewDestinations.AuthenticationViewDestination.route)
                            }
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.StopwatchViewDestination.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                content = {
                    StopwatchView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        },
                        onTitlePickerClicked = {
                            val previousViewString = PreviousView.STOPWATCH.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TitlePickerViewDestination.route + "/$previousViewString")
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.TimerViewDestination.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                content = {
                    TimerView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        },
                        onTitlePickerClicked = {
                            val previousViewString = PreviousView.STOPWATCH.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TitlePickerViewDestination.route + "/$previousViewString")
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.WiDViewDestination.route + "/{currentDateString}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                arguments = listOf(
                    navArgument("currentDateString") {
                        type = NavType.StringType
                        defaultValue = "..."
                    }
                ),
                content = {
                    val currentDateString = it.arguments?.getString("currentDateString") ?: "..."

                    // WiDView로 조회 날짜를 전달할 필요가 없음.
                    WiDView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        },
                        onTitlePickerClicked = { previousView: PreviousView ->
                            val previousViewString = previousView.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TitlePickerViewDestination.route + "/$previousViewString")
                        },
                        onDateTimePickerClicked = { previousView: PreviousView ->
                            val previousViewString = previousView.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.DateTimePickerViewDestination.route + "/$currentDateString" + "/$previousViewString")
                        },
                        onCityPickerClicked = { clickedCity: City ->
                            val previousViewString = PreviousView.CLICKED_WID_CITY.name
                            val currentCityString = clickedCity.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.CityPickerViewDestination.route + "/$previousViewString" + "/$currentCityString")
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.TitlePickerViewDestination.route + "/{previousViewString}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                arguments = listOf(
                    navArgument("previousViewString") {
                        type = NavType.StringType
                        defaultValue = "..."
                    }
                ),
                content = {
                    val previousViewString = it.arguments?.getString("previousViewString") ?: "..."
                    val previousView = PreviousView.valueOf(previousViewString)

                    TitlePickerView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        previousView = previousView,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.DateTimePickerViewDestination.route + "/{currentDateString}" + "/{previousViewString}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                arguments = listOf(
                    navArgument("currentDateString") {
                        type = NavType.StringType
                        defaultValue = "..."
                    },
                    navArgument("previousViewString") {
                        type = NavType.StringType
                        defaultValue = "..."
                    }
                ),
                content = {
                    val currentDateString = it.arguments?.getString("currentDateString") ?: "..."
                    val currentDate = LocalDate.parse(currentDateString)

                    val previousViewString = it.arguments?.getString("previousViewString") ?: "..."
                    val previousView = PreviousView.valueOf(previousViewString)

                    DateTimePickerView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        currentDate = currentDate,
                        previousView = previousView,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.CityPickerViewDestination.route + "/{previousViewString}/{currentCityString}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                arguments = listOf(
                    navArgument("previousViewString") {
                        type = NavType.StringType
                        defaultValue = "..."
                    },
                    navArgument("currentCityString") {
                        type = NavType.StringType
                        defaultValue = "..."
                    }
                ),
                content = {
                    val previousViewString = it.arguments?.getString("previousViewString") ?: "..."
                    val previousView = PreviousView.valueOf(previousViewString) // PreviousView 타입으로 변환
                    val currentCityString = it.arguments?.getString("currentCityString") ?: "..."
                    val currentCity = City.valueOf(value = currentCityString) // City 타입으로 변환

                    CityPickerView(
                        statusBarHeight = statusBarHeight,
                        navigationBarHeight = navigationBarHeight,
                        previousView = previousView,
                        currentCity = currentCity,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )
        }
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
//@Composable
//fun MainActivityPreview() {
//    Scaffold(
//        contentWindowInsets = WindowInsets.systemBars,
//        modifier = Modifier
//            .fillMaxSize(),
//        topBar = {
//            TopAppBar(
//                navigationIcon = {
//                    IconButton(onClick = {  }) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
//                    }
//                },
//                title = {
//                    Text(text = "title")
//                },
//                actions = {
//                    IconButton(onClick = {  }) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
//                    }
//                },
////                    colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangeRed)
//            )
//        },
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                onClick = {
//
//                }
//            ) {
//
//            }
//        },
//        bottomBar = {
//            BottomAppBar() {
//                IconButton(onClick = { }) {
//                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
//                }
//
//                IconButton(onClick = {  }) {
//                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
//                }
//
//                IconButton(onClick = {  }) {
//                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
//                }
//            }
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(it)
//        ) {
//            val count = 10
//            val limit = 24
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "기록 개수 제한",
//                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
//                )
//
//                Text(
//                    text = "$count / $limit",
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.spacedBy(2.dp)
//            ) {
//                for (i in 0 until limit) {
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(8.dp)
//                            .background(
//                                shape = RoundedCornerShape(16),
//                                color = if (i < count) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.secondaryContainer.copy(
//                                    0.1f
//                                )
//                            )
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            val progress = 0.3f
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "기록률",
//                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
//                )
//
//                Text(
//                    text = "${(progress * 100).toInt()}%",
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//                    .padding(horizontal = 16.dp)
//                    .clip(shape = RoundedCornerShape(16)),
//                progress = progress.coerceIn(0f, 1f),
//                color = MaterialTheme.colorScheme.onPrimaryContainer,
//                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(0.1f),
//            )
//        }
//    }
//}