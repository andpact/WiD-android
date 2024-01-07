package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate

@Composable
fun DiaryFragment(date: LocalDate, navController: NavController) {
    // 다이어리
    val diaryService = DiaryService(context = LocalContext.current)
    val clickedDiary = diaryService.getDiaryByDate(date)
    var diaryTitle by remember { mutableStateOf(clickedDiary?.title ?: "") }
//    var diaryTitleTextFieldValueState by remember { mutableStateOf(TextFieldValue(text = clickedDiary?.title ?: "", selection = TextRange(clickedDiary?.title?.length ?: 0))) }
    var diaryContent by remember { mutableStateOf(clickedDiary?.content ?: "") }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = wiDService.readDailyWiDListByDate(date)

    // 키보드
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
//    val bringIntoViewRequester = remember { BringIntoViewRequester() }
//    val coroutineScope = rememberCoroutineScope()
//    val scrollState = rememberScrollState()

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    LaunchedEffect(Unit) {
        if (clickedDiary == null) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "다이어리",
                style = Typography.titleLarge
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(diaryTitle.isNotBlank() && diaryContent.isNotBlank()) {
                        keyboardController?.hide()

                        if (clickedDiary == null) {
                            val newDiary = Diary(id = 0, date = date, title = diaryTitle, content = diaryContent)
                            diaryService.createDiary(newDiary)
                        } else {
                            diaryService.updateDiary(id = clickedDiary.id, date = date, title = diaryTitle, content = diaryContent)
                        }

                        navController.popBackStack()
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (diaryTitle.isNotBlank() && diaryContent.isNotBlank()) {
                            LimeGreen
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        }
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                text = "완료",
                style = Typography.bodyMedium,
                color = White
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f / 1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getDayStringWith3Lines(date = date),
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
                if (wiDList.isEmpty()) {
                    createNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                } else {
                    DateBasedPieChartFragment(wiDList = wiDList)
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        // 제목 입력
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester),
            value = diaryTitle,
            textStyle = Typography.bodyMedium,
            placeholder = {
                Text(
                    text = "제목을 입력해 주세요.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                ) },
            onValueChange = { diaryTitle = it } ,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = Transparent,
                unfocusedBorderColor = Transparent,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        // 내용 입력
        OutlinedTextField(
            modifier = Modifier
                .imePadding()
                .fillMaxSize(),
            value = diaryContent,
            textStyle = Typography.labelMedium,
            placeholder = {
                Text(
                    text = "내용을 입력해 주세요.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                ) },
            onValueChange = { diaryContent = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = Transparent,
                unfocusedBorderColor = Transparent,
            ),
        )
    }
}