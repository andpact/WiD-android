package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.HomePieChartView
import andpact.project.wid.model.CurrentTool
import andpact.project.wid.model.CurrentToolState
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.*

// 익명 가입 시 uid를 제외하고는 null이 할당됨.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    onStopwatchClicked: () -> Unit,
    onTimerClicked: () -> Unit,
//    onPomodoroClicked: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val TAG = "HomeView"

    val today = homeViewModel.today.value
    val now = homeViewModel.now.value

    val displayName = homeViewModel.firebaseUser.value?.displayName ?: ""
    val level = homeViewModel.user.value?.level
    val currentExp = homeViewModel.user.value?.currentExp ?: 0
    val requiredExp = homeViewModel.levelRequiredExpMap[level] ?: 0
    val expRatio = currentExp.toFloat() / requiredExp.toFloat()

    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    val formattedCurrentExp = numberFormat.format(currentExp)
    val formattedRequiredExp = numberFormat.format(requiredExp)

    val firstCurrentWiD = homeViewModel.firstCurrentWiD.value

    val currentToolState = homeViewModel.currentToolState

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed")}
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
//                    Image(
//                        modifier = Modifier
//                            .size(36.dp),
//                        painter = painterResource(id = R.mipmap.ic_main_foreground), // ic_main은 안되네?
//                        contentDescription = "앱 아이콘"
//                    )

                    Text(text = "WIVD")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (currentToolState.value == CurrentToolState.STOPPED) {
                OutlinedCard( // TODO: 테두리 색상 디바이더와 동일하게 만들기
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .padding(horizontal = 16.dp)
                ) {
                    Row {
                        TextButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                onStopwatchClicked()
                            }
                        ) {
                            Text(text = "스톱 워치")
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                        )

                        TextButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                onTimerClicked()
                            }
                        ) {
                            Text(text = "타이머")
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                        )

                        TextButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                // TODO: 포모도로 뷰로 이동
                            }
                        ) {
                            Text(text = "포모도로")
                        }
                    }
                }
            }
        },
        content = { contentPadding: PaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "${displayName}님",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp) // 바깥 패딩
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp), // 안쪽 패딩
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Text(
                            text = "LEVEL $level",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Slider(
                            value = expRatio,
                            onValueChange = {},
                            thumb = {},
                            track = { sliderState: SliderState ->
                                SliderDefaults.Track(
                                    modifier = Modifier.scale(scaleX = 1f, scaleY = 4f), // 기본 높이 4.dp * 4 = 16.dp
                                    sliderState = sliderState
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(
                                text = "$formattedCurrentExp / $formattedRequiredExp",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Text(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(horizontal = 4.dp),
                                text = "${(expRatio * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "오늘",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    HomePieChartView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        today = today,
                        now = now
                    )
                }

                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "현시간",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 시간
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val hourTens = now.hour / 10
                                    val hourOnes = now.hour % 10

                                    // 십의 자리
                                    AnimatedContent(
                                        targetState = hourTens,
                                        transitionSpec = {
                                            slideInVertically(animationSpec = tween(300)) { it } +
                                                    fadeIn(animationSpec = tween(300)) togetherWith
                                                    slideOutVertically(animationSpec = tween(300)) { -it } +
                                                    fadeOut(animationSpec = tween(300))
                                        }
                                    ) { tensDigit ->
                                        Text(
                                            text = tensDigit.toString(),
                                            style = MaterialTheme.typography.displayLarge
                                        )
                                    }

                                    // 일의 자리
                                    AnimatedContent(
                                        targetState = hourOnes,
                                        transitionSpec = {
                                            slideInVertically(animationSpec = tween(300)) { it } +
                                                    fadeIn(animationSpec = tween(300)) togetherWith
                                                    slideOutVertically(animationSpec = tween(300)) { -it } +
                                                    fadeOut(animationSpec = tween(300))
                                        }
                                    ) { onesDigit ->
                                        Text(
                                            text = onesDigit.toString(),
                                            style = MaterialTheme.typography.displayLarge
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.displayLarge,
                        )

                        // 분
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val minuteTens = now.minute / 10
                                    val minuteOnes = now.minute % 10

                                    // 십의 자리
                                    AnimatedContent(
                                        targetState = minuteTens,
                                        transitionSpec = {
                                            slideInVertically(animationSpec = tween(300)) { it } +
                                                    fadeIn(animationSpec = tween(300)) togetherWith
                                                    slideOutVertically(animationSpec = tween(300)) { -it } +
                                                    fadeOut(animationSpec = tween(300))
                                        }
                                    ) { tensDigit ->
                                        Text(
                                            text = tensDigit.toString(),
                                            style = MaterialTheme.typography.displayLarge
                                        )
                                    }

                                    // 일의 자리
                                    AnimatedContent(
                                        targetState = minuteOnes,
                                        transitionSpec = {
                                            slideInVertically(animationSpec = tween(300)) { it } +
                                                    fadeIn(animationSpec = tween(300)) togetherWith
                                                    slideOutVertically(animationSpec = tween(300)) { -it } +
                                                    fadeOut(animationSpec = tween(300))
                                        }
                                    ) { onesDigit ->
                                        Text(
                                            text = onesDigit.toString(),
                                            style = MaterialTheme.typography.displayLarge
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.displayLarge
                        )

                        // 초
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val secondTens = now.second / 10
                                    val secondOnes = now.second % 10

                                    // 십의 자리
                                    AnimatedContent(
                                        targetState = secondTens,
                                        transitionSpec = {
                                            slideInVertically(animationSpec = tween(300)) { it } +
                                                    fadeIn(animationSpec = tween(300)) togetherWith
                                                    slideOutVertically(animationSpec = tween(300)) { -it } +
                                                    fadeOut(animationSpec = tween(300))
                                        }
                                    ) { tensDigit ->
                                        Text(
                                            text = tensDigit.toString(),
                                            style = MaterialTheme.typography.displayLarge
                                        )
                                    }

                                    // 일의 자리
                                    AnimatedContent(
                                        targetState = secondOnes,
                                        transitionSpec = {
                                            slideInVertically(animationSpec = tween(300)) { it } +
                                                    fadeIn(animationSpec = tween(300)) togetherWith
                                                    slideOutVertically(animationSpec = tween(300)) { -it } +
                                                    fadeOut(animationSpec = tween(300))
                                        }
                                    ) { onesDigit ->
                                        Text(
                                            text = onesDigit.toString(),
                                            style = MaterialTheme.typography.displayLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                if (currentToolState.value == CurrentToolState.STOPPED) {
                    item {
                        Spacer(modifier = Modifier.height(72.dp)) // TODO: 플로팅 버튼 높이 만큼 스페이스 만들기
                    }
                }
            }
        }
    )
}