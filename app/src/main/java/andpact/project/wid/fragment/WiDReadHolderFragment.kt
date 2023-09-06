package andpact.project.wid.fragment

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
//                    .border(BorderStroke(1.dp, Color.Black))
                    .height(55.dp),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.Black
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    text = {
                        Text(text = "Day")
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    text = {
                        Text(text = "Week")
                    }
                )

                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    text = {
                        Text(text = "Month")
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