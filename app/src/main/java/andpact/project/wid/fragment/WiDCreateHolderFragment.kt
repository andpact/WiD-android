package andpact.project.wid.fragment

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WiDCreateHolderFragment(buttonsVisible: MutableState<Boolean>) {
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
                    TabRowDefaults.SecondaryIndicator(
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
                        Text(text = "스탑워치")
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    text = {
                        Text(text = "타이머")
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> WiDCreateStopWatchFragment(buttonsVisible = buttonsVisible)
            1 -> WiDCreateTimerFragment(buttonsVisible = buttonsVisible)
        }
    }
}