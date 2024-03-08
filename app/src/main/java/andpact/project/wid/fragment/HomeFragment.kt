package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.MainActivityDestinations
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun HomeFragment(mainActivityNavController: NavController) {
    // 뷰 모델
    val homeViewModel: HomeViewModel = viewModel()

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
    val lastWiD = homeViewModel.lastWiD.value

    // 다이어리
//    val diaryService = DiaryService(context = LocalContext.current)
//    val diaryExistenceList by remember { mutableStateOf(diaryService.checkDiaryExistence(startDate = startDate, finishDate = finishDate)) }
    val diaryExistenceList = homeViewModel.diaryExistenceList.value

    DisposableEffect(Unit) {
        // 컴포저블이 생성될 떄
        Log.d("HomeFragment", "HomeFragment is being composed")

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
//        verticalArrangement = Arrangement.Center
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.secondary)
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

            Icon(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        mainActivityNavController.navigate(MainActivityDestinations.SettingFragmentDestination.route)
                    }
                    .size(24.dp),
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = "환경 설정"
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "최근 30일",
                        style = Typography.bodyMedium,
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
//                            .padding(bottom = 16.dp)
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
                                                color = if (wiDExistenceList[indexDate] == true) DeepSkyBlue else MaterialTheme.colorScheme.tertiary,
                                                shape = CircleShape
                                            )
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                color = if (diaryExistenceList[indexDate] == true) OrangeRed else MaterialTheme.colorScheme.tertiary,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (lastWiD != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "최근 활동",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
    //                .shadow(
    //                    elevation = 2.dp,
    //                    shape = RoundedCornerShape(8.dp),
    //                    spotColor = MaterialTheme.colorScheme.primary,
    //                )
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
    //                            .clickable(
    //                                interactionSource = remember { MutableInteractionSource() },
    //                                indication = null
    //                            ) {
    //                                titleMenuExpanded = !titleMenuExpanded
    //                            },
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
                                    text = titleMap[lastWiD.title] ?: lastWiD.title,
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = if (titleMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "제목 메뉴 펼치기",
//                            )
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
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    expandStartPicker = !expandStartPicker
//                                },
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

//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = if (expandStartPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "시작 시간 선택 도구 펼치기",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
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
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    expandFinishPicker = !expandFinishPicker
//                                },
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

//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = if (expandFinishPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "종료 시간 선택 도구 펼치기",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
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
            }
        }
    }
}