package andpact.project.wid.view

import andpact.project.wid.destinations.MainActivityViewDestinations
import andpact.project.wid.model.City
import andpact.project.wid.model.PreviousView
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// FIXME
// TODO:
@Composable
fun MainActivityView(dynamicLink: String?) {
    val TAG = "MainActivityView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val mainActivityViewNavController: NavHostController = rememberNavController() // 네비게이션 그래프에는 NavController가 아닌 NavHostController가 파라미터로 들어감.

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
                    AuthenticationView()
                }
            )

            composable(
                route = MainActivityViewDestinations.MainViewDestination.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = 500))
                },
                content = {
                    MainView(
                        onStopwatchClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.StopwatchViewDestination.route)
                        },
                        onTimerClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TimerViewDestination.route)
                        },
                        onWiDClicked = {
                            mainActivityViewNavController.navigate(MainActivityViewDestinations.WiDViewDestination.route)
                        },
                        onCityPickerClicked = { clickedCity: City ->
                            val previousViewString = PreviousView.USER_CITY.name
                            val currentCityString = clickedCity.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.CityPickerViewDestination.route + "/$previousViewString/$currentCityString")
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
                        animationSpec = tween(200)
                    )
                },
                content = {
                    StopwatchView(
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
                        animationSpec = tween(200)
                    )
                },
                content = {
                    TimerView(
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
                route = MainActivityViewDestinations.WiDViewDestination.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(200)
                    )
                },
                content = {
                    WiDView(
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        },
                        onTitlePickerClicked = { previousView: PreviousView -> // TODO: 제목 변경인지 부제목 변경인지
                            val previousViewString = previousView.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TitlePickerViewDestination.route + "/$previousViewString")
                        },
                        onTimePickerClicked = { previousView: PreviousView ->
                            val previousViewString = previousView.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TimePickerViewDestination.route + "/$previousViewString")
                        },
                        onCityPickerClicked = { clickedCity: City ->
                            val previousViewString = PreviousView.CLICKED_WID_CITY.name
                            val currentCityString = clickedCity.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.CityPickerViewDestination.route + "/$previousViewString/$currentCityString")
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
                        animationSpec = tween(200)
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
                        previousView = previousView,
                        onBackButtonPressed = {
                            mainActivityViewNavController.popBackStack()
                        }
                    )
                }
            )

            composable(
                route = MainActivityViewDestinations.TimePickerViewDestination.route + "/{previousViewString}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(200)
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

                    TimePickerView(
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
                        animationSpec = tween(200)
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                     IconButton(onClick = { /*TODO*/ }) {
                         Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                     }
                },
                title = {
                    Text(text = "title")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar() {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(horizontal = 16.dp)
//            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(),
                progress = 0.5f
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous Month"
                    )
                }

                Text(
                    text = "${2023}년 ${10}월",
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(
                    enabled = false,
                    onClick = {
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Month"
                    )
                }
            }
        }
    }
}