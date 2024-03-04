package andpact.project.wid.fragment

import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun HomeFragment(homeViewModel: HomeViewModel) {

    DisposableEffect(Unit) {
        // 컴포저블이 생성될 떄
        Log.d("HomeFragment", "HomeFragment is being composed")

        // 컴포저블이 제거될 떄
        onDispose {
            Log.d("HomeFragment", "HomeFragment is being disposed")
        }
    }

    // 날짜
//    val today = LocalDate.now()
//    var startDate by remember { mutableStateOf(getFirstDateOfMonth(today)) }
//    var finishDate by remember { mutableStateOf(getLastDateOfMonth(today)) }
    val startDate = homeViewModel.startDate
    val finishDate = homeViewModel.finishDate

    // WiD
//    val wiDService = WiDService(context = LocalContext.current)
//    val wiDExistenceList by remember { mutableStateOf(wiDService.checkWiDExistence(startDate = startDate, finishDate = finishDate)) }
    val wiDExistenceList = homeViewModel.wiDExistenceList.value

    // 다이어리
//    val diaryService = DiaryService(context = LocalContext.current)
//    val diaryExistenceList by remember { mutableStateOf(diaryService.checkDiaryExistence(startDate = startDate, finishDate = finishDate)) }
    val diaryExistenceList = homeViewModel.diaryExistenceList.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
//                modifier = Modifier
//                    .clickable {
//                        expandDatePicker = true // 월 선택 대화상자 구현해야 함.
//                    },
//                text = getPeriodStringOfMonth(date = today),
                text = "최근 30일",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = DeepSkyBlue,
                        shape = CircleShape
                    )
            )

            Text(
                text = "WiD",
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = OrangeRed,
                        shape = CircleShape
                    )
            )

            Text(
                text = "다이어리",
                style = Typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.surface.copy(0.3f),
//                    shape = RoundedCornerShape(8.dp)
//                ),
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                val daysOfWeek = daysOfWeekFromSunday

                daysOfWeek.forEachIndexed { index, day ->
                    val textColor = when (index) {
                        0 -> OrangeRed
                        6 -> DeepSkyBlue
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = day,
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                }
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(startDate.dayOfWeek.value % 7) {
                    // selectedPeriod가 한달이면 달력의 빈 칸을 생성해줌.
                }

                items(
                    count = ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1
                ) { index: Int ->
                    val indexDate = startDate.plusDays(index.toLong())

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = indexDate.dayOfMonth.toString(), // 날짜를 텍스트로 표시
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = if (wiDExistenceList[indexDate] == true) DeepSkyBlue else MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = if (diaryExistenceList[indexDate] == true) OrangeRed else MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//                .border(
//                    width = 0.5.dp,
//                    color = MaterialTheme.colorScheme.primary,
//                    shape = RoundedCornerShape(8.dp)
//                )
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "총 WiD 수",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Text(
//                    text = "${wiDService.getWiDCount()}",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .weight(1f),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "총 다이어리 수",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Text(
//                    text = "${diaryService.getDiaryCount()}",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
    }
}