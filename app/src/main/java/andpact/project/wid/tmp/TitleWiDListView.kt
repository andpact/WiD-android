package andpact.project.wid.tmp

//import andpact.project.wid.R
//import andpact.project.wid.chartView.TitleWiDListLineChartView
//import andpact.project.wid.model.Title
//import andpact.project.wid.ui.theme.Typography
//import andpact.project.wid.util.*
//import andpact.project.wid.viewModel.TitleWiDListViewModel
//import android.util.Log
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowLeft
//import androidx.compose.material.icons.filled.KeyboardArrowRight
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import java.time.Duration
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TitleWiDListView(
//    title: Title, // 예는 수정할 때만 사용함, ui에는 사용 안함.
//    titleWiDListViewModel: TitleWiDListViewModel = hiltViewModel()
//) {
//    val TAG = "TitleWiDListView"
//
//    // 날짜
//    val today = titleWiDListViewModel.today.value
//    val startDate = titleWiDListViewModel.startDate.value // 조회 시작 날짜
//    val finishDate = titleWiDListViewModel.finishDate.value // 조회 종료 날짜
//    val weekPickerExpanded = titleWiDListViewModel.weekPickerExpanded.value
//
//    // WiD
//    val wiDListFetched = titleWiDListViewModel.wiDListFetched.value
//    val wiDList = titleWiDListViewModel.wiDList.value
//
//    // 맵
//    val totalDurationMap = titleWiDListViewModel.totalDurationMap.value
//    val averageDurationMap = titleWiDListViewModel.averageDurationMap.value
//    val maxDurationMap = titleWiDListViewModel.maxDurationMap.value
//    val minDurationMap = titleWiDListViewModel.minDurationMap.value
//    val titleDateCountMap = titleWiDListViewModel.titleDateCountMap.value
//    val titleMaxDateMap = titleWiDListViewModel.titleMaxDateMap.value
//    val titleMinDateMap = titleWiDListViewModel.titleMinDateMap.value
//
//    // 제목
//    val currentTitle = titleWiDListViewModel.currentTitle.value
//
//    // Current WiD
//    val firstCurrentWiD = titleWiDListViewModel.firstCurrentWiD.value
//    val secondCurrentWiD = titleWiDListViewModel.secondCurrentWiD.value
//
//    // 조회할 제목 변경
//    LaunchedEffect(title) {
//        titleWiDListViewModel.setCurrentTitle(newTitle = title)
//    }
//
//    LaunchedEffect(
//        key1 = firstCurrentWiD,
//        block = {
//            Log.d(TAG, "LaunchedEffect: firstCurrentWiD update")
//
//            if (firstCurrentWiD.date in startDate..finishDate) {
//                titleWiDListViewModel.setStartDateAndFinishDate(
//                    startDate = startDate,
//                    finishDate = finishDate
//                )
//            }
//        }
//    )
//
//    LaunchedEffect(
//        key1 = secondCurrentWiD,
//        block = {
//            Log.d(TAG, "LaunchedEffect: secondCurrentWiD update")
//
//            if (secondCurrentWiD.date in startDate..finishDate) {
//                titleWiDListViewModel.setStartDateAndFinishDate(
//                    startDate = startDate,
//                    finishDate = finishDate
//                )
//            }
//        }
//    )
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
//        titleWiDListViewModel.setStartDateAndFinishDate(
//            startDate = titleWiDListViewModel.startDate.value,
//            finishDate = titleWiDListViewModel.finishDate.value
//        )
//
//        onDispose { Log.d(TAG, "disposed") }
//    }
//
//    BackHandler(
//        enabled = weekPickerExpanded,
//        onBack = {
//            titleWiDListViewModel.setWeekPickerExpanded(false)
//        }
//    )
//
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize(),
//        containerColor = MaterialTheme.colorScheme.surface,
//        topBar = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .height(56.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                ) {
//                    TextButton(
//                        onClick = {
//                            titleWiDListViewModel.setWeekPickerExpanded(true)
//                        }
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                            contentDescription = "날짜",
//                            tint = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Text(
//                            text = getPeriodStringOfWeek(
//                                firstDayOfWeek = startDate,
//                                lastDayOfWeek = finishDate
//                            ),
//                            style = Typography.titleLarge,
//                            color = MaterialTheme.colorScheme.onSurface,
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1
//                        )
////                        Row(
////                            modifier = Modifier
////                                .weight(1f),
////                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
////                        ) {
////                        }
//                    }
//                }
//
//                FilledTonalIconButton(
//                    onClick = {
//                        val newStartDate = startDate.minusWeeks(1)
//                        val newFinishDate = finishDate.minusWeeks(1)
//
//                        titleWiDListViewModel.setStartDateAndFinishDate(
//                            startDate = newStartDate,
//                            finishDate = newFinishDate
//                        )
//                    },
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowLeft,
//                        contentDescription = "이전 날짜",
//                    )
//                }
//
//                FilledTonalIconButton(
//                    onClick = {
//                        val newStartDate = startDate.plusWeeks(1)
//                        val newFinishDate = finishDate.plusWeeks(1)
//
//                        titleWiDListViewModel.setStartDateAndFinishDate(
//                            startDate = newStartDate,
//                            finishDate = newFinishDate
//                        )
//                    },
//                    enabled = !(startDate == getFirstDateOfWeek(today) && finishDate == getLastDateOfWeek(today))
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowRight,
//                        contentDescription = "다음 날짜",
//                    )
//                }
//            }
//        },
//        content = { contentPadding: PaddingValues -> // BoxScope
//            if (totalDurationMap[currentTitle]?.isZero != false) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Icon(
//                        modifier = Modifier
//                            .size(100.dp),
//                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
//                        contentDescription = "다음 날짜",
//                    )
//
//                    Text(
//                        text = "표시할 데이터가 없습니다.",
//                        style = Typography.bodyMedium,
//                    )
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(contentPadding),
//                    content = {
//                        item {
//                            TitleWiDListLineChartView(
//                                modifier = Modifier
//                                    .padding(32.dp),
//                                startDate = startDate,
//                                finishDate = finishDate,
//                                currentTitle = currentTitle,
//                                wiDList = wiDList
//                            )
//
//                            Text(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                                text = "${currentTitle.kr} 기록",
//                                style = Typography.titleLarge,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Spacer(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                )
//
//                                Column {
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 8.dp, bottom = 4.dp),
//                                        text = TitleDurationMap.TOTAL.kr,
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 4.dp, bottom = 8.dp),
//                                        text = getDurationString(duration = totalDurationMap[currentTitle] ?: Duration.ZERO),
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//                                }
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .background(
//                                            color = MaterialTheme.colorScheme.secondaryContainer,
//                                            shape = MaterialTheme.shapes.medium
//                                        )
//                                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                                    text = getDurationPercentageStringOfWeek(duration = totalDurationMap[currentTitle] ?: Duration.ZERO),
//                                    style = Typography.bodyLarge,
//                                    color = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//
//                            HorizontalDivider(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp),
//                                thickness = 0.5.dp
//                            )
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Spacer(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                )
//
//                                Column {
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 8.dp, bottom = 4.dp),
//                                        text = TitleDurationMap.AVERAGE.kr,
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 4.dp, bottom = 8.dp),
//                                        text = getDurationString(duration = averageDurationMap[currentTitle] ?: Duration.ZERO),
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//                                }
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .background(
//                                            color = MaterialTheme.colorScheme.secondaryContainer,
//                                            shape = MaterialTheme.shapes.medium
//                                        )
//                                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                                    text = "총 ${titleDateCountMap[currentTitle]}일 기준",
//                                    style = Typography.bodyLarge,
//                                    color = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//
//                            HorizontalDivider(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp),
//                                thickness = 0.5.dp
//                            )
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Spacer(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                )
//
//                                Column {
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 8.dp, bottom = 4.dp),
//                                        text = TitleDurationMap.MAX.kr,
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 4.dp, bottom = 8.dp),
//                                        text = getDurationString(duration = maxDurationMap[currentTitle] ?: Duration.ZERO),
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//                                }
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .background(
//                                            color = MaterialTheme.colorScheme.secondaryContainer,
//                                            shape = MaterialTheme.shapes.medium
//                                        )
//                                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                                    text = "${titleMaxDateMap[currentTitle]?.dayOfMonth}일",
//                                    style = Typography.bodyLarge,
//                                    color = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//
//                            HorizontalDivider(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp),
//                                thickness = 0.5.dp
//                            )
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Spacer(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                )
//
//                                Column {
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 8.dp, bottom = 4.dp),
//                                        text = TitleDurationMap.MIN.kr,
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(top = 4.dp, bottom = 8.dp),
//                                        text = getDurationString(duration = minDurationMap[currentTitle] ?: Duration.ZERO),
//                                        style = Typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.onSurface
//                                    )
//                                }
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .background(
//                                            color = MaterialTheme.colorScheme.secondaryContainer,
//                                            shape = MaterialTheme.shapes.medium
//                                        )
//                                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                                    text = "${titleMinDateMap[currentTitle]?.dayOfMonth}일",
//                                    style = Typography.bodyLarge,
//                                    color = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//
//                            Spacer(
//                                modifier = Modifier
//                                    .height(8.dp)
//                            )
//                        }
//                    }
//                )
//            }
//
//            if (!wiDListFetched) {
//                /** 대화상자 넣기 */
//            }
//
//            if (weekPickerExpanded) {
//                AlertDialog(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
//                            shape = MaterialTheme.shapes.extraLarge
//                        ),
//                    onDismissRequest = {
//                        titleWiDListViewModel.setWeekPickerExpanded(false)
//                    },
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Text(
//                            modifier = Modifier
//                                .padding(16.dp),
//                            text = "기간 선택",
//                            style = Typography.titleLarge
//                        )
//
//                        Column(
//                            verticalArrangement = Arrangement.Center,
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            repeat(5) { index -> // 0부터 시작
//                                val reverseIndex = 4 - index // 역순 인덱스 계산
//
//                                val firstDayOfWeek = getFirstDateOfWeek(today).minusWeeks(reverseIndex.toLong())
//                                val lastDayOfWeek = getLastDateOfWeek(today).minusWeeks(reverseIndex.toLong())
//
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clickable {
//                                            titleWiDListViewModel.setStartDateAndFinishDate(
//                                                startDate = firstDayOfWeek,
//                                                finishDate = lastDayOfWeek
//                                            )
//
//                                            titleWiDListViewModel.setWeekPickerExpanded(false)
//                                        },
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        modifier = Modifier
//                                            .padding(16.dp),
//                                        text = getPeriodStringOfWeek(firstDayOfWeek = firstDayOfWeek, lastDayOfWeek = lastDayOfWeek),
//                                        style = Typography.bodyMedium,
//                                    )
//
//                                    Spacer(
//                                        modifier = Modifier
//                                            .weight(1f)
//                                    )
//
//                                    RadioButton(
//                                        selected = startDate == firstDayOfWeek && finishDate == lastDayOfWeek,
//                                        onClick = {
//                                            titleWiDListViewModel.setStartDateAndFinishDate(
//                                                startDate = firstDayOfWeek,
//                                                finishDate = lastDayOfWeek
//                                            )
//
//                                            titleWiDListViewModel.setWeekPickerExpanded(false)
//                                        },
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    )
//}