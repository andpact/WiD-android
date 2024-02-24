package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.ui.theme.LimeGreen
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.White
import andpact.project.wid.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingFragment() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
////                .background(MaterialTheme.colorScheme.background)
//                .padding(horizontal = 16.dp)
//                .height(56.dp)
//        ) {
//            Icon(
//                modifier = Modifier
//                    .size(24.dp)
//                    .align(Alignment.CenterStart)
//                    .clickable {
//                        navController.popBackStack()
//                    },
//                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                contentDescription = "뒤로 가기",
//                tint = MaterialTheme.colorScheme.primary
//            )
//
//            if (stopwatchPlayer.stopwatchState.value != PlayerState.Stopped) {
//                Row(
//                    modifier = Modifier
//                        .padding(horizontal = 8.dp, vertical = 4.dp)
//                        .align(Alignment.Center)
//                        .background(
//                            color = if (stopwatchPlayer.stopwatchState.value == PlayerState.Started) {
//                                LimeGreen
//                            } else {
//                                OrangeRed
//                            },
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                ) {
//                    Text(
//                        text = titleMap[stopwatchPlayer.title.value] ?: "공부",
//                        style = Typography.labelMedium,
//                        color = White
//                    )
//
//                    Text(
//                        text = getDurationString(stopwatchPlayer.duration.value, 0),
//                        style = Typography.labelMedium,
//                        color = White,
//                        fontFamily = FontFamily.Monospace
//                    )
//                }
//            } else if (timerPlayer.timerState.value != PlayerState.Stopped) {
//                Row(
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .background(
//                            color = if (timerPlayer.timerState.value == PlayerState.Started) {
//                                LimeGreen
//                            } else {
//                                OrangeRed
//                            },
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                ) {
//                    Text(
//                        text = titleMap[timerPlayer.title.value] ?: "공부",
//                        style = Typography.labelMedium,
//                        color = White
//                    )
//
//                    Text(
//                        text = getDurationString(timerPlayer.remainingTime.value, 0),
//                        style = Typography.labelMedium,
//                        color = White,
//                        fontFamily = FontFamily.Monospace
//                    )
//                }
//            } else {
//                Text(
//                    modifier = Modifier
//                        .align(Alignment.Center),
//                    text = "환경 설정",
//                    style = Typography.titleLarge,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            item {
                Text(
                    text = "계정",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    text = "일반",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    text = "스톱 워치",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    text = "타이머",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}