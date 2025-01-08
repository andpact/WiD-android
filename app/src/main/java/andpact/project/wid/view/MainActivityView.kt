package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.destinations.MainActivityViewDestinations
import andpact.project.wid.model.*
import andpact.project.wid.ui.theme.*
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

// FIXME
// TODO: 이전 뷰를 문자열 말고 이넘 클래스로 만들어서 사용하기 
@Composable
fun MainActivityView(dynamicLink: String?) {
    val TAG = "MainActivity"

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
                        onCityPickerClicked = {
                            val previousViewString = PreviousView.USER.name
                            val currentCityString = it.name

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
                        onTitlePickerClicked = {
                            val previousViewString = PreviousView.CLICKED_WID.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TitlePickerViewDestination.route + "/$previousViewString")
                        },
                        onTimePickerClicked = {
                            val previousViewString = it.name

                            mainActivityViewNavController.navigate(MainActivityViewDestinations.TimePickerViewDestination.route + "/$previousViewString")
                        },
                        onCityPickerClicked = {
                            val previousViewString = PreviousView.CLICKED_WID.name
                            val currentCityString = it.name

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
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                        }
                    ) {
                        Text(
                            text = getDateString(LocalDate.now()),
                            style = MaterialTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = {
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "이전 날짜",
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                        },
                        enabled = true
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "다음 날짜",
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                FilledIconButton(
                    modifier = Modifier
                        .size(56.dp),
                    onClick = {
                    },
                    enabled = true,
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "초기화",
                    )
                }

                FilledIconButton(
                    modifier = Modifier
                        .size(56.dp),
                    onClick = {
                    },
                    enabled = true,
                    shape = RectangleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "초기화",
                    )
                }

                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    onClick = {
                    },
                    enabled = true,
                    shape = RectangleShape
                ) {
                    Text(text = "생성 완료")
                }
            }
        },
//        floatingActionButtonPosition = FabPosition.Center,
//        floatingActionButton = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(intrinsicSize = IntrinsicSize.Min)
//                    .padding(horizontal = 16.dp)
//                    .border(
//                        width = 0.5.dp,
//                        shape = MaterialTheme.shapes.medium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//            ) {
//                TextButton(
//                    modifier = Modifier
//                        .weight(1f),
//                    onClick = {
//
//                    }
//                ) {
//                    Text(text = "스톱 워치")
//                }
//
//                VerticalDivider(
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                )
//
//                TextButton(
//                    modifier = Modifier
//                        .weight(1f),
//                    onClick = {
//
//                    }
//                ) {
//                    Text(text = "타이머")
//                }
//
//                VerticalDivider(
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                )
//
//                TextButton(
//                    modifier = Modifier
//                        .weight(1f),
//                    onClick = {
//
//                    }
//                ) {
//                    Text(text = "포모도로")
//                }
//            }
//        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {

        }
    }
}

@Composable
fun getDateString(date: LocalDate): AnnotatedString {
    val formattedString = buildAnnotatedString {
        if (date.year == LocalDate.now().year) {
            append(date.format(DateTimeFormatter.ofPattern("M월 d일 (")))
        } else {
            append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
        }

        withStyle(
            style = SpanStyle(
                color = when (date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> DeepSkyBlue
                    DayOfWeek.SUNDAY -> OrangeRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        ) {
            append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
        }
        append(")")
    }

    return formattedString
}