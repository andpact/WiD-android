package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    val stopWatchButtonBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFFE91E63)
        )
    )

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "WiD",
                style = TextStyle(
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.acme_regular))
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    TextButton(
                        shape = RectangleShape,
                        onClick = {
                            navController.navigate(Destinations.StopWatchFragmentDestination.route)
                            mainTopBottomBarVisible.value = false
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "스탑워치",
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                                )
                            )

                            Icon(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd),
                                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                contentDescription = "StopWatch",
                                tint = Color.Black
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    TextButton(
                        shape = RectangleShape,
                        onClick = {
                            navController.navigate(Destinations.TimerFragmentDestination.route)
                            mainTopBottomBarVisible.value = false
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "타이머",
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                                )
                            )

                            Icon(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd),
                                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                contentDescription = "Timer",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 2.dp
            ) {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5f),
                    shape = RectangleShape,
                    onClick = {
                        navController.navigate(Destinations.ManualFragmentDestination.route)
                        mainTopBottomBarVisible.value = false
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterStart),
                            text = "직접 입력",
                            style = TextStyle(
                                fontSize = 25.sp,
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                            )
                        )

                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterEnd),
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                            contentDescription = "Create manual",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFragmentPreview() {
    val mainTopBottomBarVisible = remember { mutableStateOf(true) }
    HomeFragment(NavController(LocalContext.current), mainTopBottomBarVisible = mainTopBottomBarVisible)
}