package andpact.project.wid.tmp

import andpact.project.wid.R
//import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.Typography
//import andpact.project.wid.viewModel.RandomDiaryViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import java.time.LocalDate

//@Composable
//fun RandomDiaryView(onDiaryClicked: (List<WiD>, Diary) -> Unit) {
//    val TAG = "RandomDiaryView"
//
//    val randomDiaryViewModel: RandomDiaryViewModel = viewModel()
//
//    // 다이어리
//    val totalDiaryCount = randomDiaryViewModel.totalDiaryCount
//    val diaryMap = randomDiaryViewModel.diaryMap.value
//    val diaryDateList = randomDiaryViewModel.diaryDateList.value
//
//    // WiD
//    val wiDMap = randomDiaryViewModel.wiDMap.value
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
//        onDispose {
//            Log.d(TAG, "disposed")
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        if (totalDiaryCount == 0) {
//            Text(
//                text = "조회할 다이어리가 없습니다.",
//                style = Typography.bodyMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else if (diaryDateList.isEmpty()) {
//            Row(
//                modifier = Modifier
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        randomDiaryViewModel.fetchRandomDiaryDates()
//                    },
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "다이어리 불러오기",
//                    style = Typography.bodyMedium,
//                    color = DeepSkyBlue
//                )
//
//                Icon(
//                    modifier = Modifier
//                        .size(24.dp),
//                    painter = painterResource(id = R.drawable.outline_add_box_24),
//                    contentDescription = "다이어리 불러오기",
//                    tint = DeepSkyBlue
//                )
//            }
//        } else {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .height(56.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "다이어리 수 ${diaryMap.size} / $totalDiaryCount",
//                    style = Typography.titleLarge,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Row(
//                    modifier = Modifier
//                        .clickable(
//                            enabled = diaryMap.size < totalDiaryCount,
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null
//                        ) {
//                            randomDiaryViewModel.fetchRandomDiaryDates()
//                        }
//                        .background(
//                            color = if (diaryMap.size < totalDiaryCount) {
//                                MaterialTheme.colorScheme.surface
//                            } else {
//                                DarkGray
//                            },
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "다이어리 추가",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
//
//                    Icon(
//                        modifier = Modifier
//                            .size(24.dp),
//                        painter = painterResource(id = R.drawable.outline_add_box_24),
//                        contentDescription = "새로운 WiD",
//                        tint = MaterialTheme.colorScheme.secondary
//                    )
//                }
//            }
//
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(diaryDateList.size) { index ->
//                    val itemDate = diaryDateList[index]
//                    // 빈 다이어리를 가져오는 경우는 없음.
//                    val diary = diaryMap[itemDate] ?: Diary(id = -1, date = itemDate, title = "", content = "")
//                    val wiDList = wiDMap[itemDate] ?: emptyList()
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp) // 바깥 패딩
//                            .padding(
//                                bottom = if (index == diaryDateList.size - 1) 32.dp else 0.dp
//                            )
//                            .border(
//                                width = 0.5.dp,
//                                color = MaterialTheme.colorScheme.primary,
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                onDiaryClicked(wiDList, diary)
//                            },
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                                .padding(top = 16.dp)
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .aspectRatio(1f / 1f),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    text = getDateStringWith3Lines(date = itemDate),
//                                    style = Typography.titleLarge,
//                                    textAlign = TextAlign.Center,
//                                    fontSize = 20.sp,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Box(
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .aspectRatio(1f / 1f),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                if (wiDList.isEmpty()) {
//                                    getNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
//                                } else {
//                                    DiaryPieChartFragment(wiDList = wiDList)
//                                }
//                            }
//                        }
//
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                                .padding(bottom = 16.dp),
//                            verticalArrangement = Arrangement.spacedBy(16.dp)
//                        ) {
//                            Text(
//                                text = diary.title,
//                                style = Typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//
//                            Text(
//                                text = diary.content,
//                                style = Typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}