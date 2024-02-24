package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun StopWatchFragment(stopwatchPlayer: StopwatchPlayer) {
    // WiD
//    val wiDService = WiDService(context = LocalContext.current)

    // 화면
//    var stopwatchTopBottomBarVisible by remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    var titleMenuExpanded by remember { mutableStateOf(false) }

//    DisposableEffect(Unit) {
//        // Fragment가 나타날 때
//        stopwatchPlayer.setInStopwatchView(true)
//
//        onDispose {
//            // Fragment가 사라질 때
//            stopwatchPlayer.setInStopwatchView(false)
//        }
//    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
//    BackHandler(enabled = true) {
//        navController.popBackStack()
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(enabled = stopwatchPlayer.stopwatchState.value == PlayerState.Started) { // 스톱 워치가 시작된 상태에서만 상, 하단 바 숨길 수 있도록
//                stopwatchPlayer.setStopwatchTopBottomBarVisible(!stopwatchPlayer.stopwatchTopBottomBarVisible.value)
                if (stopwatchPlayer.stopwatchTopBottomBarVisible.value) {
                    stopwatchPlayer.setStopwatchTopBottomBarVisible(false)
                } else {
                    stopwatchPlayer.setStopwatchTopBottomBarVisible(true)
                }
            }
    ) {
        /**
         * 컨텐츠
         */
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = screenHeight / 2),
            text = getStopWatchTimeString(stopwatchPlayer.duration.value),
            style = TextStyle(
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary
            )
        )

        /**
         * 하단 바
         */
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = stopwatchPlayer.stopwatchTopBottomBarVisible.value,
            enter = expandVertically{ 0 },
            exit = shrinkVertically{ 0 },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) {
                            titleMenuExpanded = true
                        }
                        .background(
                            color = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped)
                                AppIndigo
                            else
                                DarkGray
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(titleIconMap[stopwatchPlayer.title.value] ?: R.drawable.baseline_menu_book_16),
                    contentDescription = "제목",
                    tint = White
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (stopwatchPlayer.stopwatchState.value == PlayerState.Paused) {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(stopwatchPlayer.stopwatchState.value == PlayerState.Paused) {
                                stopwatchPlayer.stopStopwatch()
                            }
                            .background(color = DeepSkyBlue)
                            .padding(16.dp)
                            .size(32.dp),
                        painter = painterResource(id = R.drawable.baseline_refresh_24),
                        contentDescription = "스톱워치 초기화",
                        tint = White
                    )
                }

                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { // 스톱 워치 모든 상태일 때 클릭 가능
                            if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) { // 스톱 워치 시작 상태
                                stopwatchPlayer.pauseStopwatch()
                            } else { // 스톱 워치 중지, 정지 상태
                                stopwatchPlayer.startStopwatch()
                            }
                        }
                        .background(
                            color = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.primary
                            else if (stopwatchPlayer.stopwatchState.value == PlayerState.Paused) LimeGreen
                            else OrangeRed
                        )
                        .padding(16.dp)
                        .size(32.dp),
                    painter = painterResource(
                        id = if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
                            R.drawable.baseline_pause_24
                        } else {
                            R.drawable.baseline_play_arrow_24
                        }
                    ),
                    contentDescription = "스톱 워치 시작 및 중지",
                    tint = if (stopwatchPlayer.stopwatchState.value == PlayerState.Stopped) MaterialTheme.colorScheme.secondary else White
                )
            }
        }

        // 컴포저블 안에 기본적으로 박스가 감싸고 있는 구조인 듯.
        // 위 전체 화면 박스에 배경색을 넣어놔서 아래 박스는 배경 색을 넣을 필요 없음.
        /**
         * 제목 바텀 시트
         */
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxSize(),
            visible = titleMenuExpanded,
            enter = fadeIn(), // 위치 이동하는 애니메이션은 적용이 안되네?
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(titleMenuExpanded) {
                        titleMenuExpanded = false
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(screenHeight / 2)
                        .padding(16.dp)
//                        .shadow(
//                            elevation = 2.dp,
//                            shape = RoundedCornerShape(8.dp),
//                            spotColor = MaterialTheme.colorScheme.primary,
//                        )
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(false) {} // 클릭 불가능하게 해서 제목 메뉴 닫힘 방지
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clickable {
                                    titleMenuExpanded = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "제목 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "제목 선택",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

//                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(titles.size) { index ->
                            val itemTitle = titles[index]
                            val iconResourceId = titleIconMap[itemTitle] ?: R.drawable.baseline_calendar_month_24 // 기본 아이콘

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        stopwatchPlayer.setTitle(itemTitle)
                                        titleMenuExpanded = false
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    painter = painterResource(id = iconResourceId),
                                    contentDescription = "제목",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = titleMap[itemTitle] ?: "공부",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                if (itemTitle == stopwatchPlayer.title.value) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = "선택됨",
                                        style = Typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun StopWatchFragmentPreview() {
//    val dummyNavController = rememberNavController()
//    val stopwatchViewModel = StopwatchPlayer()
//
//    StopWatchFragment(navController = dummyNavController, stopwatchPlayer = stopwatchViewModel)
//}
