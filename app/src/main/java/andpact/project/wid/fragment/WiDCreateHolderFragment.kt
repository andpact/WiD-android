package andpact.project.wid.fragment

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun WiDCreateHolderFragment(buttonsVisible: MutableState<Boolean>) {
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
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "스탑워치",
                            style = TextStyle(color = if (selectedTab == 0) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "타이머",
                            style = TextStyle(color = if (selectedTab == 1) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
                        )
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