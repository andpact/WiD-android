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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun HomeFragment(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 컨텐츠
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                .fillMaxWidth()
            ) {
                // 스톱 워치
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(Destinations.StopWatchFragmentDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(24.dp),
                        painter = painterResource(id = R.drawable.outline_alarm_24),
                        contentDescription = "스톱 워치",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "스톱 워치",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 타이머
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(Destinations.TimerFragmentDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(24.dp),
                        painter = painterResource(id = R.drawable.outline_timer_24),
                        contentDescription = "타이머",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "타이머",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 새로운 WiD
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(Destinations.NewWiDFragmentDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(24.dp),
                        painter = painterResource(id = R.drawable.outline_add_box_24),
                        contentDescription = "새로운 WiD",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "새로운 WiD",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // 날짜 별 조회
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(Destinations.DateBasedFragmentDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(24.dp),
                        painter = painterResource(id = R.drawable.baseline_location_searching_24),
                        contentDescription = "날짜 별 조회",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "날짜 별 조회",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 기간 별 조회
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(Destinations.PeriodBasedFragmentDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(24.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "기간 별 조회",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "기간 별 조회",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 다이어리 검색
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(Destinations.SearchFragmentDestination.route)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(24.dp),
                        painter = painterResource(id = R.drawable.baseline_search_24),
                        contentDescription = "다이어리 검색",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "다이어리 검색",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        /**
         * 하단 바
         */
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//                .align(Alignment.BottomCenter),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "WiD",
//                style = TextStyle(
//                    fontSize = 25.sp,
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = acmeRegular,
//                    color = MaterialTheme.colorScheme.primary
//                ),
//            )
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFragmentPreview() {
    HomeFragment(NavController(LocalContext.current))
}