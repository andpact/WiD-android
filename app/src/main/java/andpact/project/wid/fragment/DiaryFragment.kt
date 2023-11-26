package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.util.getDayString
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate

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
            .background(colorResource(id = R.color.ghost_white))
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
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

                Text(text = "${getDayString(date)}의 다이어리")
            }

            TextButton(
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