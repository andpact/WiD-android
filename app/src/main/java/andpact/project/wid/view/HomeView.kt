package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Black
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.White
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.text.Typography

// 익명 가입 시 uid를 제외하고는 null이 할당됨.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    onSettingButtonPressed: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val TAG = "HomeView"

    val displayName = homeViewModel.firebaseUser.value?.displayName ?: ""
//    val statusMessage = homeViewModel.user.value?.statusMessage ?: ""
    val level = homeViewModel.user.value?.level
    val currentExp = homeViewModel.user.value?.currentExp ?: 0

    val numberToDurationMap = homeViewModel.numberToDurationMap

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        modifier = Modifier
                            .size(36.dp),
                        painter = painterResource(id = R.mipmap.ic_main_foreground), // ic_main은 안되네?
                        contentDescription = "앱 아이콘"
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSettingButtonPressed()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_settings_24),
                            contentDescription = "환경 설정"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { contentPadding ->
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

//            item {
//                Text(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp),
//                    text = statusMessage,
//                    style = Typography.bodyMedium,
//                )
//            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 바깥 패딩
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp), // 안쪽 패딩
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "$level",
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

//                    val requiredExp = levelRequiredExpMap[user?.level] ?: 0
                    val requiredExp = 86400
//                    val currentExp = user?.currentExp ?: 0
//                    val currentExp = 40000 // tmp
//                    val expRatio = if (requiredExp > 0) currentExp.toFloat() / requiredExp else 0f
                    val expRatio = currentExp.toFloat() / requiredExp.toFloat()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = expRatio) // 1% 미만은 표시 안되고 오류날거임.
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        Text(
                            text = "$currentExp / $requiredExp",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = "제목 별 소요 시간 순위",
                    style = Typography.titleLarge
                )
            }

            numberToDurationMap?.entries?.forEachIndexed { index, (title, duration) ->
                item {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(24.dp),
                            painter = painterResource(
                                id = titleNumberStringToTitleIconMap[title] ?: R.drawable.baseline_title_24
                            ),
                            contentDescription = "제목",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = titleNumberStringToTitleKRStringMap[title] ?: "",
                                style = Typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Text(
                                text = getDurationString(duration = duration, mode = 3),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = (index + 1).toString(), // index는 0부터 시작하므로 +1
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(0.dp)
                )
            }

//            item {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    numberToDurationMap?.forEach { (title, duration) ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp) // 항목 간의 간격 추가
//                        ) {
//                            Text(
//                                text = title, // title을 표시
//                                modifier = Modifier.weight(1f) // Text가 나란히 정렬되도록 가중치를 설정
//                            )
//                            Text(
//                                text = duration.toString(), // Duration을 표시
//                                modifier = Modifier.weight(1f)
//                            )
//                        }
//                    }
//                }
//            }
        }
    }
}