package andpact.project.wid.fragment

import andpact.project.wid.activity.MainActivityDestinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.getDateStringWith3Lines
import andpact.project.wid.util.getFirstDateOfMonth
import andpact.project.wid.util.getLastDateOfMonth
import andpact.project.wid.util.getNoBackgroundEmptyViewWithMultipleLines
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate

@Composable
fun RandomDiaryFragment(mainActivityNavController: NavController) {

    DisposableEffect(Unit) {
        Log.d("RandomDiaryFragment", "RandomDiaryFragment is being composed")

        onDispose {
            Log.d("RandomDiaryFragment", "RandomDiaryFragment is being disposed")
        }
    }

    // 다이어리
    val diaryService = DiaryService(LocalContext.current)
    var diaryList by remember { mutableStateOf(emptyList<Diary>()) }
    val totalDiaryCount = diaryService.getDiaryCount()

    // WiD
    val wiDService = WiDService(context = LocalContext.current)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (diaryList.isEmpty()) {
            Row(
                modifier = Modifier
                    .clickable {
                        diaryList = diaryService.readRandomDiaries(diaryList)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다이어리 불러오기",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다이어리 수 ${diaryList.size} / $totalDiaryCount",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier
                        .clickable(diaryList.size <= totalDiaryCount) {
                            diaryList = diaryService.readRandomDiaries(diaryList)
                        }
                        .background(
                            color = if (diaryList.size <= totalDiaryCount) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                DarkGray
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "다이어리 추가",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(diaryList.size) { index ->
                    val diary = diaryList[index]
                    val diaryDate = diary.date

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp) // 바깥 패딩
                            .padding(
                                bottom = if (index == diaryList.size - 1) 32.dp else 0.dp
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ),
//                            .border(
//                                width = 0.5.dp,
//                                color = MaterialTheme.colorScheme.primary,
//                                shape = RoundedCornerShape(8.dp)
//                            ),
//                            .clickable {
//                                mainActivityNavController.navigate(MainActivityDestinations.DiaryFragmentDestination.route + "/${diaryDate}")
//                            },
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                    text = getDateStringWith3Lines(date = diaryDate),
                                    style = Typography.titleLarge,
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
                                val wiDList = wiDService.readDailyWiDListByDate(diaryDate)

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
                                text = diary.title,
                                style = Typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = diary.content,
                                style = Typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}