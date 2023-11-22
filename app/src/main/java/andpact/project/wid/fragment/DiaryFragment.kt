package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DiaryFragment(date: LocalDate, navController: NavController, buttonsVisible: MutableState<Boolean>) {
    // 다이어리
    val diaryService = DiaryService(context = LocalContext.current)
    val clickedDiary = diaryService.getDiaryByDate(date)
    var diaryTitle by remember { mutableStateOf(clickedDiary?.title ?: "") }
//    var diaryTitleTextFieldValueState by remember { mutableStateOf(TextFieldValue(text = clickedDiary?.title ?: "", selection = TextRange(clickedDiary?.title?.length ?: 0))) }
    var diaryContent by remember { mutableStateOf(clickedDiary?.content ?: "") }

    // 키보드
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = true) {
        navController.popBackStack()
        buttonsVisible.value = true
    }

    LaunchedEffect(Unit) {
        if (clickedDiary == null) {
            focusRequester.requestFocus()
        }
    }

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 날짜
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextButton(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                onClick = {
                    keyboardController?.hide()

                    navController.popBackStack()
                    buttonsVisible.value = true
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            val dateText = buildAnnotatedString {
                date.let {
                    append(it.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
                    withStyle(
                        style = SpanStyle(
                            color = when (it.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            }
                        )
                    ) {
                        append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                    }
                    append(")")
                }
            }

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = dateText,
            )

            TextButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = {
                    keyboardController?.hide()

                    if (clickedDiary == null) {
                        val newDiary = Diary(id = 0, date = date, title = diaryTitle, content = diaryContent)
                        diaryService.createDiary(newDiary)
                    } else {
                        diaryService.updateDiary(id = clickedDiary.id, date = date, title = diaryTitle, content = diaryContent)
                    }

                    navController.popBackStack()
                    buttonsVisible.value = true
                },
                enabled = diaryTitle.isNotBlank() && diaryContent.isNotBlank()
            ) {
                Text(text = "완료")
            }
        }

        // 제목 입력
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester),
            value = diaryTitle,
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            placeholder = { Text(text = "제목을 입력해 주세요.") },
            singleLine = true,
            onValueChange = { diaryTitle = it } ,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )

        // 내용 입력
        OutlinedTextField(
            modifier = Modifier
                .fillMaxSize(),
            value = diaryContent,
            placeholder = { Text(text = "내용을 입력해 주세요.") },
            onValueChange = { diaryContent = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )
    }
}