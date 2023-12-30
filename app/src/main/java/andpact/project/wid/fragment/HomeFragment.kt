package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.titleExampleMap
import andpact.project.wid.util.titleMap
import andpact.project.wid.util.titles
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GhostWhite),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        /**
         * 도구
         */
        item {
            Text(
                modifier = Modifier
                    .background(White)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = "WiD",
                style = TextStyle(
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acmeRegular,
                    textAlign = TextAlign.Center
                ),
            )

            Column(
                modifier = Modifier
                    .background(White)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = "도구",
                    style = Typography.titleMedium
                )

                // 스탑 워치
                Row(
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Destinations.StopWatchFragmentDestination.route)
                            mainTopBottomBarVisible.value = false
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        painter = painterResource(id = R.drawable.outline_alarm_24),
                        contentDescription = "스탑 워치",
                        tint = Black
                    )

                    Column {
                        Text(
                            text = "스탑 워치",
                            style = Typography.bodyMedium
                        )

                        Text(
                            text = "현재 시간부터 기록하기",
                            style = Typography.labelSmall
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "스탑 워치로 전환",
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                // 타이머
                Row(
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Destinations.TimerFragmentDestination.route)
                            mainTopBottomBarVisible.value = false
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        painter = painterResource(id = R.drawable.outline_timer_24),
                        contentDescription = "타이머",
                        tint = Black
                    )

                    Column {
                        Text(
                            text = "타이머",
                            style = Typography.bodyMedium
                        )

                        Text(
                            text = "정해진 시간만큼 기록하기",
                            style = Typography.labelSmall
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "타이머로 전환",
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                // 새로운 WiD
                Row(
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Destinations.NewWiDFragmentDestination.route)
                            mainTopBottomBarVisible.value = false
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        painter = painterResource(id = R.drawable.outline_add_box_24),
                        contentDescription = "새로운 WiD",
                        tint = Black
                    )

                    Column {
                        Text(
                            text = "새로운 WiD",
                            style = Typography.bodyMedium
                        )

                        Text(
                            text = "날짜, 제목, 시작 및 종료 시간을 직접 기록하기",
                            style = Typography.labelSmall
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "새로운 WiD로 전환",
                    )
                }
            }
        }

        /**
         * 선택 가능한 제목
         */
        item {
            Column(
                modifier = Modifier
                    .background(White)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = "선택 가능한 제목",
                    style = Typography.titleMedium
                )

                for ((index, title) in titles.withIndex()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp, 15.dp)
                                .background(color = colorMap[title] ?: LightGray)
                        )

                        Text(
                            titleMap[title] ?: title,
                            style = Typography.bodyMedium
                        )

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        Text(
                            text = titleExampleMap[title] ?: "",
                            style = Typography.labelSmall
                        )
                    }

                    if (index < titles.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
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