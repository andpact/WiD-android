package andpact.project.wid.fragment

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun WiDReadHolderFragment(navController: NavController, buttonsVisible: MutableState<Boolean>) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = buttonsVisible.value,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(500)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(500)
            )
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
//                    color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Day",
                            style = TextStyle(color = if (selectedTab == 0) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Week",
                            style = TextStyle(color = if (selectedTab == 1) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "Month",
                            style = TextStyle(color = if (selectedTab == 2) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> WiDReadDayFragment(navController = navController, buttonsVisible = buttonsVisible)
            1 -> WiDReadWeekFragment()
            2 -> WiDReadMonthFragment()
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun WiDReadHolderFragmentPreview() {
//    WiDReadHolderFragment()
//}