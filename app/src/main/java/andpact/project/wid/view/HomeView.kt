package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.chartView.HomePieChartView
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.acmeRegular
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val displayName = homeViewModel.firebaseUser.value?.displayName ?: ""
    val level = homeViewModel.user.value?.level
    val currentExp = homeViewModel.user.value?.currentExp ?: 0
    val requiredExp = homeViewModel.levelRequiredExpMap[level] ?: 0
    val expRatio = currentExp.toFloat() / requiredExp.toFloat()

    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    val formattedCurrentExp = numberFormat.format(currentExp)
    val formattedRequiredExp = numberFormat.format(requiredExp)

    val today = homeViewModel.today.value
    val now = homeViewModel.now.value

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

                    Text(
                        text = "WIVD",
                        fontFamily = acmeRegular,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
        content = { contentPadding: PaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "${displayName}님",
                        style = Typography.titleLarge,
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
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            if (0.01f <= expRatio) { // 1% 미만은 표시 안되고 오류날거임.
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = expRatio)
                                        .height(16.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                )
                            }
                        }

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
                                style = Typography.bodyMedium,
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
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
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
                        text = "기록 만들기",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        content = {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(shape = MaterialTheme.shapes.medium)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clickable(
                                        onClick = {
                                            /** 사용 중인 도구, 상태 받아서 배타적으로 사용해야함. */
                                            onStopwatchClicked()
                                        }
                                    )
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_home_24),
                                    contentDescription = "스톱워치",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = "스톱워치",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(shape = MaterialTheme.shapes.medium)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clickable(
                                        onClick = {
                                            /** 사용 중인 도구, 상태 받아서 배타적으로 사용해야함. */
                                            onTimerClicked()
                                        }
                                    )
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp),
                                    painter = painterResource(id = R.drawable.outline_home_24),
                                    contentDescription = "타이머",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = "타이머",
                                    style = Typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    )
                }

                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "현시간",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = homeViewModel.getTimeString(time = now),
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    val level = 30
//    val currentExp = 453_829
//    val requiredExp = 1_000_000
//    val expRatio = currentExp.toFloat() / requiredExp.toFloat()
//
//    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
//    val formattedCurrentExp = numberFormat.format(currentExp)
//    val formattedRequiredExp = numberFormat.format(requiredExp)
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = MaterialTheme.colorScheme.surface),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(horizontal = 16.dp) // 바깥 패딩
//                .background(
//                    color = MaterialTheme.colorScheme.primary,
//                    shape = MaterialTheme.shapes.medium
//                )
//                .padding(16.dp), // 안쪽 패딩
//            verticalArrangement = Arrangement.spacedBy(32.dp)
//        ) {
//            Text(
//                text = "LEVEL $level",
//                style = Typography.titleLarge,
//                color = MaterialTheme.colorScheme.onPrimary
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(16.dp)
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        shape = MaterialTheme.shapes.medium
//                    )
//            ) {
//                if (0.01f <= expRatio) { // 1% 미만은 표시 안되고 오류날거임.
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth(fraction = expRatio)
//                            .height(16.dp)
//                            .background(
//                                color = MaterialTheme.colorScheme.onPrimary,
//                                shape = MaterialTheme.shapes.medium
//                            )
//                    )
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Spacer(
//                    modifier = Modifier
//                        .weight(1f)
//                )
//
//                Text(
//                    text = "$formattedCurrentExp / $formattedRequiredExp",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//
//                Spacer(
//                    modifier = Modifier
//                        .width(8.dp)
//                )
//
//                Text(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.secondaryContainer,
//                            shape = MaterialTheme.shapes.medium
//                        )
//                        .padding(horizontal = 4.dp),
//                    text = "${(expRatio * 100).toInt()}%",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
//        }
//
//        Text(
//            modifier = Modifier
//                .padding(horizontal = 16.dp),
//            text = "기록 만들기",
//            style = Typography.titleLarge,
//            color = MaterialTheme.colorScheme.onSurface
//        )
//
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp), // 바깥 패딩
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .background(
//                        color = MaterialTheme.colorScheme.secondaryContainer,
//                        shape = MaterialTheme.shapes.medium
//                    )
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(24.dp),
//                    painter = painterResource(id = R.drawable.outline_home_24),
//                    contentDescription = "스톱워치",
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//
//                Text(
//                    text = "스톱워치",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .background(
//                        color = MaterialTheme.colorScheme.secondaryContainer,
//                        shape = MaterialTheme.shapes.medium
//                    )
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(24.dp),
//                    painter = painterResource(id = R.drawable.outline_home_24),
//                    contentDescription = "타이머",
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//
//                Text(
//                    text = "타이머",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .background(
//                        color = MaterialTheme.colorScheme.secondaryContainer,
//                        shape = MaterialTheme.shapes.medium
//                    )
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(24.dp),
//                    painter = painterResource(id = R.drawable.outline_home_24),
//                    contentDescription = "타이머",
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//
//                Text(
//                    text = "포모도로",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
//        }
//    }
//}