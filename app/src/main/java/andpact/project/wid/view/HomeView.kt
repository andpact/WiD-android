package andpact.project.wid.view

import andpact.project.wid.model.PlayerState
import andpact.project.wid.ui.theme.AppEmerald
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.time.Duration
import java.time.YearMonth
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

    val now = homeViewModel.now.value

//    val displayName = homeViewModel.firebaseUser.value?.displayName ?: ""
    val email = homeViewModel.user.value?.email
    val level = homeViewModel.user.value?.level
    val currentExp = homeViewModel.user.value?.currentExp ?: 0
    val requiredExp = homeViewModel.levelRequiredExpMap[level] ?: 0
    val expRatio = currentExp.toFloat() / requiredExp.toFloat()

    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    val formattedCurrentExp = numberFormat.format(currentExp)
    val formattedRequiredExp = numberFormat.format(requiredExp)

    val playerState = homeViewModel.playerState

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
                    Text(
                        text = "WIVD",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                        color = AppEmerald
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (playerState.value == PlayerState.STOPPED) {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    onClick =  { null } // TODO: 클릭 반응 없애기
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "새로운 기록을 만들어 보세요!",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onStopwatchClicked()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                            ) {
                                Text(text = "스톱워치")
                            }

                            OutlinedButton(
                                onClick = {
                                    onTimerClicked()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                            ) {
                                Text(text = "타이머")
                            }

                            OutlinedButton(
                                onClick = {
                                    // TODO: 작성
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                            ) {
                                Text(text = "포모도로")
                            }
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
                contentPadding = if (playerState.value == PlayerState.STOPPED) {
                    PaddingValues(top = 8.dp, bottom = (56.dp + 48.dp + 8.dp) + 8.dp) // 헤더(56.dp) + 버튼(48.dp) + 플로팅 여백(8.dp)
                } else {
                    PaddingValues(vertical = 8.dp)
                },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(
                    key = "email",
                    contentType = "header"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "${email}님",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }

                item(
                    key = "level",
                    contentType = "card"
                ) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = "LEVEL $level",
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .padding(horizontal = 16.dp)
                                .clip(shape = MaterialTheme.shapes.extraLarge)
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    shape = MaterialTheme.shapes.extraLarge
                                ),
                            progress = expRatio.coerceIn(0f, 1f),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            trackColor = Transparent,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Text(text = "$formattedCurrentExp / $formattedRequiredExp")

                            Text(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                                    .padding(horizontal = 4.dp),
                                text = "${(expRatio * 100).toInt()}%",
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item(
                    key = "today",
                    contentType = "header"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "오늘 날짜",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item(
                    key = "today-chart",
                    contentType = "today-chart"
                ) {
                    val yearProgress = now.dayOfYear.toFloat() / now.toLocalDate().lengthOfYear().toFloat()
                    val monthProgress = now.dayOfMonth.toFloat() / YearMonth.from(now.toLocalDate()).lengthOfMonth().toFloat()
                    val dayProgress = now.toLocalTime().toSecondOfDay().toFloat() / Duration.ofHours(24).seconds.toFloat()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Year progress
                            Column(
                                modifier = Modifier
                                    .weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f / 1f)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        progress = yearProgress,
                                        strokeWidth = 8.dp,
                                        strokeCap = StrokeCap.Round,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )

                                    Text(
                                        text = "${(yearProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    text = "${now.year}년",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            // Month progress
                            Column(
                                modifier = Modifier
                                    .weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f / 1f)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        progress = monthProgress,
                                        strokeWidth = 8.dp,
                                        strokeCap = StrokeCap.Round,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "${(monthProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    text = "${now.monthValue}월",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            // Day progress
                            Column(
                                modifier = Modifier
                                    .weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f / 1f)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        progress = dayProgress,
                                        strokeWidth = 8.dp,
                                        strokeCap = StrokeCap.Round,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )

                                    Text(
                                        text = "${(dayProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    text = "${now.dayOfMonth}일",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                item(
                    key = "now",
                    contentType = "header"
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "현재 시간",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item(
                    key = "now-chart",
                    contentType = "now-chart"
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 시간(시)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val hourTens = now.hour / 10
                                    val hourOnes = now.hour % 10

                                    // 십의 자리
                                    Text(
                                        text = hourTens.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )

                                    // 일의 자리
                                    Text(
                                        text = hourOnes.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }

                            Text(
                                text = ":",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )

                            // 분
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val minuteTens = now.minute / 10
                                    val minuteOnes = now.minute % 10

                                    // 십의 자리
                                    Text(
                                        text = minuteTens.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )

                                    // 일의 자리
                                    Text(
                                        text = minuteOnes.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }

                            Text(
                                text = ":",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )

                            // 초
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    val secondTens = now.second / 10
                                    val secondOnes = now.second % 10

                                    // 십의 자리
                                    Text(
                                        text = secondTens.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )

                                    // 일의 자리
                                    Text(
                                        text = secondOnes.toString(),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}