package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeFragment(mainActivityNavController: NavController) {
    // 뷰 모델
    val homeViewModel: HomeViewModel = viewModel()

    // WiD
    val lastWiD = homeViewModel.lastWiD.value

    // 다이어리
    val lastDiary = homeViewModel.lastDiary.value
    val wiDList = homeViewModel.wiDList.value // 다이어리 용도

    DisposableEffect(Unit) {
        // 컴포저블이 생성될 떄
        Log.d("HomeFragment", "HomeFragment is being composed")

        // WiD나 다이어리 생성 후 돌아오면 갱신되도록.
        homeViewModel.setLastWiD()
        homeViewModel.setLastDiary()

        // 컴포저블이 제거될 떄
        onDispose {
            Log.d("HomeFragment", "HomeFragment is being disposed")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "홈",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

//            Icon(
//                modifier = Modifier
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        mainActivityNavController.navigate(MainActivityDestinations.SettingFragmentDestination.route)
//                    }
//                    .size(24.dp),
//                painter = painterResource(id = R.drawable.baseline_settings_24),
//                contentDescription = "환경 설정"
//            )
        }

        // 최근 WiD나 다이어리가 없을 때
        if (lastWiD == null || lastDiary == null) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = "WiD = What I Did",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "WiD로 행동을 기록하고,\n다이어리로 생각을 기록하세요.\n기록된 모든 순간을 통해\n본인의 여정을 파악하세요.",
                    style = Typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 최근 WiD
                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "최근 활동",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = MaterialTheme.colorScheme.secondary)
                    ) {
                        // 날짜
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "날짜",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "날짜",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getDateString(lastWiD.date),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        // 제목
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(titleIconMap[lastWiD.title] ?: R.drawable.baseline_menu_book_16),
                                contentDescription = "제목",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "제목",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = titleMap[lastWiD.title] ?: "공부",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        // 시작 시간
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_alarm_24),
                                contentDescription = "시작 시간",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "시작",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getTimeString(lastWiD.start, "a hh:mm:ss"),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        // 종료 시간
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .height(IntrinsicSize.Min)
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                                contentDescription = "종료 시간",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "종료",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getTimeString(lastWiD.finish, "a hh:mm:ss"),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        // 소요 시간
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.baseline_timelapse_24),
                                contentDescription = "소요 시간",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "소요",
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = getDurationString(lastWiD.duration, mode = 3),
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // 최근 다이어리
                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = "최근 다이어리",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) // 바깥 패딩
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f / 1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getDateStringWith3Lines(date = lastDiary.date),
                                    style = Typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f / 1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (wiDList.isEmpty()) {
                                    getNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                                } else {
                                    DiaryPieChartFragment(wiDList = wiDList)
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = lastDiary.title,
                                style = Typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = lastDiary.title,
                                style = Typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}