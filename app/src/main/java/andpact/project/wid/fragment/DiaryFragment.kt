package andpact.project.wid.fragment

import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
    var diaryContent by remember { mutableStateOf(clickedDiary?.content ?: "") }

    BackHandler(enabled = true) { // 휴대폰 뒤로 가기 버튼 클릭 시
        navController.popBackStack()
        buttonsVisible.value = true
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    navController.popBackStack()
                    buttonsVisible.value = true
                },
            ) {
                Text(text = "뒤로")
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
                    .weight(1f),
                text = dateText,
                style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            )

            TextButton(
                onClick = {
                    if (clickedDiary == null) {
                        val newDiary = Diary(id = 0, date = date, title = diaryTitle, content = diaryContent)
                        diaryService.createDiary(newDiary)
                    } else {
                        diaryService.updateDiary(
                            id = clickedDiary.id,
                            date = date,
                            title = diaryTitle,
                            content = diaryContent
                        )
                    }

                    navController.popBackStack()
                    buttonsVisible.value = true
                },
                enabled = diaryTitle.isNotEmpty() && diaryContent.isNotEmpty()
            ) {
                Text(text = "완료")
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(vertical = 8.dp),
            value = diaryTitle,
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            placeholder = { Text(text = "제목을 입력해 주세요.") },
            singleLine = true,
            onValueChange = { diaryTitle = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )

        HorizontalDivider()

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(vertical = 8.dp),
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