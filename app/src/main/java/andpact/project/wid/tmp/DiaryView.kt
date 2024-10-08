package andpact.project.wid.tmp

import andpact.project.wid.R
//import andpact.project.wid.model.Diary
//import andpact.project.wid.service.DiaryService
//import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.*
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate

//@Composable
//fun DiaryView(
//    onBackButtonPressed: () -> Unit,
//    date: LocalDate,
////    stopwatchViewModel: StopwatchViewModel,
////    timerViewModel: TimerViewModel
//) {
//    val TAG = "DiaryView"
//
//    // 다이어리
//    val diaryService = DiaryService(context = LocalContext.current)
//    val clickedDiary = diaryService.readDiaryByDate(date)
//    var diaryTitle by remember { mutableStateOf(clickedDiary?.title ?: "") }
////    var diaryTitleTextFieldValueState by remember { mutableStateOf(TextFieldValue(text = clickedDiary?.title ?: "", selection = TextRange(clickedDiary?.title?.length ?: 0))) }
//    var diaryContent by remember { mutableStateOf(clickedDiary?.content ?: "") }
//
//    // WiD
//    val wiDService = WiDService(context = LocalContext.current)
//    val wiDList = wiDService.readDailyWiDListByDate(date)
//
//    // 키보드
//    val focusRequester = remember { FocusRequester() }
//    val focusManager = LocalFocusManager.current
//    val keyboardController = LocalSoftwareKeyboardController.current
////    val bringIntoViewRequester = remember { BringIntoViewRequester() }
////    val coroutineScope = rememberCoroutineScope()
////    val scrollState = rememberScrollState()
//
//    // 휴대폰 뒤로 가기 버튼 클릭 시
////    BackHandler(enabled = true) {
////        mainActivityViewNavController.popBackStack()
////    }
//
//    LaunchedEffect(Unit) {
//        if (clickedDiary == null) {
//            focusRequester.requestFocus()
//        }
//    }
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
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.secondary)
//    ) {
//        /**
//         * 상단 바
//         */
//        Box(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//                .fillMaxWidth()
//                .height(56.dp)
//        ) {
//            Icon(
//                modifier = Modifier
//                    .align(Alignment.CenterStart)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        onBackButtonPressed()
//                    },
//                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                contentDescription = "뒤로 가기",
//                tint = MaterialTheme.colorScheme.primary
//            )
//
////            if (stopwatchViewModel.stopwatchState.value != PlayerState.Stopped) {
////                Row(
////                    modifier = Modifier
////                        .align(Alignment.Center)
////                        .background(
////                            color = if (stopwatchViewModel.stopwatchState.value == PlayerState.Started) {
////                                LimeGreen
////                            } else {
////                                OrangeRed
////                            },
////                            shape = RoundedCornerShape(8.dp)
////                        )
////                        .padding(horizontal = 8.dp, vertical = 4.dp)
////                ) {
////                    Text(
////                        text = titleMap[stopwatchViewModel.title.value] ?: "공부",
////                        style = Typography.labelMedium,
////                        color = White
////                    )
////
////                    Text(
////                        text = getDurationString(stopwatchViewModel.duration.value, 0),
////                        style = Typography.labelMedium,
////                        color = White,
////                        fontFamily = FontFamily.Monospace
////                    )
////                }
////            } else if (timerViewModel.timerState.value != PlayerState.Stopped) {
////                Row(
////                    modifier = Modifier
////                        .align(Alignment.Center)
////                        .background(
////                            color = if (timerViewModel.timerState.value == PlayerState.Started) {
////                                LimeGreen
////                            } else {
////                                OrangeRed
////                            },
////                            shape = RoundedCornerShape(8.dp)
////                        )
////                        .padding(horizontal = 8.dp, vertical = 4.dp)
////                ) {
////                    Text(
////                        text = titleMap[timerViewModel.title.value] ?: "공부",
////                        style = Typography.labelMedium,
////                        color = White
////                    )
////
////                    Text(
////                        text = getDurationString(timerViewModel.remainingTime.value, 0),
////                        style = Typography.labelMedium,
////                        color = White,
////                        fontFamily = FontFamily.Monospace
////                    )
////                }
////            } else {
////                Text(
////                    modifier = Modifier
////                        .align(Alignment.Center),
////                    text = "다이어리",
////                    style = Typography.titleLarge
////                )
////            }
//
//
//            Text(
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//                    .clickable(
//                        enabled = diaryTitle.isNotBlank() && diaryContent.isNotBlank(),
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        keyboardController?.hide()
//
//                        if (clickedDiary == null) {
//                            val newDiary = Diary(id = 0, date = date, title = diaryTitle, content = diaryContent)
//                            diaryService.createDiary(newDiary)
//                        } else {
//                            diaryService.updateDiary(id = clickedDiary.id, date = date, title = diaryTitle, content = diaryContent)
//                        }
//
//                        onBackButtonPressed()
//                    }
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(
//                        if (diaryTitle.isNotBlank() && diaryContent.isNotBlank()) {
//                            LimeGreen
//                        } else {
//                            MaterialTheme.colorScheme.tertiary
//                        }
//                    )
//                    .padding(
//                        horizontal = 8.dp,
//                        vertical = 4.dp
//                    ),
//                text = "완료",
//                style = Typography.bodyMedium,
//                color = White
//            )
//        }
//
////        HorizontalDivider()
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//                .height(IntrinsicSize.Min)
//        ) {
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f / 1f),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = getDateStringWith3Lines(date = date),
//                    style = Typography.titleLarge,
//                    textAlign = TextAlign.Center,
//                    fontSize = 20.sp,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f / 1f),
//                contentAlignment = Alignment.Center
//            ) {
////                if (wiDList.isEmpty()) {
////                    getNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
////                } else {
////                    DiaryPieChartFragment(wiDList = wiDList)
////                }
//            }
//        }
//
////        HorizontalDivider(
////            modifier = Modifier
////                .padding(horizontal = 16.dp)
////        )
//
//        // 제목 입력
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .focusRequester(focusRequester = focusRequester),
//            value = diaryTitle,
//            textStyle = Typography.bodyLarge,
//            placeholder = {
//                Text(
//                    text = "제목을 입력해 주세요.",
//                    style = Typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.primary
//                ) },
//            onValueChange = { diaryTitle = it } ,
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedTextColor = MaterialTheme.colorScheme.primary,
//                unfocusedTextColor = MaterialTheme.colorScheme.primary,
//                focusedBorderColor = Transparent,
//                unfocusedBorderColor = Transparent,
//            ),
//            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
//            keyboardActions = KeyboardActions(
//                onNext = {
//                    focusManager.moveFocus(FocusDirection.Down)
//                }
//            ),
//        )
//
////        HorizontalDivider(
////            modifier = Modifier
////                .padding(horizontal = 16.dp)
////        )
//
//        // 내용 입력
//        OutlinedTextField(
//            modifier = Modifier
//                .imePadding()
//                .fillMaxSize(),
//            value = diaryContent,
//            textStyle = Typography.bodyLarge,
//            placeholder = {
//                Text(
//                    text = "내용을 입력해 주세요.",
//                    style = Typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.primary
//                ) },
//            onValueChange = { diaryContent = it },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedTextColor = MaterialTheme.colorScheme.primary,
//                unfocusedTextColor = MaterialTheme.colorScheme.primary,
//                focusedBorderColor = Transparent,
//                unfocusedBorderColor = Transparent,
//            ),
//        )
//    }
//}