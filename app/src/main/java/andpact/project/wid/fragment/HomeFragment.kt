package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalTime
import kotlin.concurrent.fixedRateTimer

/**
 * 정확하지 않다. 다시 배우자.
 * 값 변경 시
 * remember { 값 }-> 재 렌더링 안됨.
 * remember(파라미터) { 값 } 의 파라미터가 변경되면 블록('{ 값 }')을 재 실행하여 재 랜더링 됨.
 * mutableStateOf { 값 }-> 재 렌더링 됨.
 */
@Composable
fun HomeFragment(navController: NavController, stopwatchPlayer: StopwatchPlayer, timerPlayer: TimerPlayer) {
    // 타이머
    val now = LocalTime.now()
    var totalSecondsFromNow by remember { mutableStateOf(now.toSecondOfDay() * 1_000L) }
    val totalSecondsInADay = 24 * 60 * 60 * 1_000L // Millis
    val remainingTime = remember(totalSecondsFromNow) { totalSecondsInADay - totalSecondsFromNow }
//    val remainingTime by remember(totalSecondsFromNow) { mutableStateOf(totalSecondsInADay - totalSecondsFromNow) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = remember { wiDService.readRandomWiDList() }

    DisposableEffect(Unit) {
        val timer = fixedRateTimer("timer", true, 0L, 1000) {
            totalSecondsFromNow += 1_000L // Millis
        }

        onDispose {
            timer.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        /**
         * 광고
         */
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .background(MaterialTheme.colorScheme.tertiary),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                modifier = Modifier
//                    .background(DarkGray)
//                    .padding(16.dp),
//                text = "광고\n이미지"
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Text("광고 제목")
//
//                Text("광고 내용")
//            }
//
//            Icon(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp),
//                imageVector = Icons.Default.KeyboardArrowRight,
//                contentDescription = "광고 클릭",
//                tint = MaterialTheme.colorScheme.primary
//            )
//        }
        
        /**
         * 컨텐츠
         */
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 왼쪽 버튼 열
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 스톱 워치
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary),
                            onClick = {
                                navController.navigate(Destinations.StopWatchFragmentDestination.route)
                            },
                            enabled = timerPlayer.timerState.value == PlayerState.Stopped
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                painter = painterResource(id = R.drawable.outline_alarm_24),
                                contentDescription = "스톱 워치",
                                tint = if (timerPlayer.timerState.value == PlayerState.Stopped)
                                    MaterialTheme.colorScheme.primary
                                else
                                    DarkGray
                            )
                        }

                        Text(
                            text = "스톱 워치",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // 타이머
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary),
                            onClick = {
                                navController.navigate(Destinations.TimerFragmentDestination.route)
                            },
                            enabled = stopwatchPlayer.stopwatchState.value == PlayerState.Stopped
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                painter = painterResource(id = R.drawable.outline_timer_24),
                                contentDescription = "타이머",
                                tint = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped)
                                    MaterialTheme.colorScheme.primary
                                else DarkGray
                            )
                        }

                        Text(
                            text = "타이머",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.5f / 1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary),
                        onClick = {
                            navController.navigate(Destinations.NewWiDFragmentDestination.route)
                        },
                        enabled = stopwatchPlayer.stopwatchState.value == PlayerState.Stopped && timerPlayer.timerState.value == PlayerState.Stopped
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(48.dp),
                            painter = painterResource(id = R.drawable.outline_add_box_24),
                            contentDescription = "새로운 WiD",
                            tint = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped && timerPlayer.timerState.value == PlayerState.Stopped)
                                MaterialTheme.colorScheme.primary
                            else DarkGray
                        )
                    }

                    Text(
                        text = "새로운 WiD",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f / 1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.TopCenter),
                            text = "오늘",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "${remainingTime * 100 / totalSecondsInADay}%",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 50.sp
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomCenter),
                            text = getHorizontalTimeString(remainingTime),
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "남은 시간",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 오른쪽 버튼 열
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f / 1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(vertical = 16.dp)
                    ) {
                        if (wiDList.isEmpty()) {
                            getNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                        } else {
                            DateBasedPieChartFragment(wiDList = wiDList)
                        }
                    }

                    Text(
                        text = "88일 전",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 날짜 조회
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary),
                            onClick = {
                                navController.navigate(Destinations.DateBasedFragmentDestination.route)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                painter = painterResource(id = R.drawable.baseline_location_searching_24),
                                contentDescription = "날짜 조회",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "날짜 조회",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // 기간 조회
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f / 1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary),
                            onClick = {
                                navController.navigate(Destinations.PeriodBasedFragmentDestination.route)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "기간 조회",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "기간 조회",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.5f / 1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary),
                        onClick = {
                            navController.navigate(Destinations.SearchFragmentDestination.route)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(48.dp),
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "다이어리 검색",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

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
//    HomeFragment(NavController(LocalContext.current))
}