package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WiDView(wiDId: Long, navController: NavController, buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)
    val wiD = wiDService.readWiDById(wiDId)

    if (wiD == null) {
        Text(modifier = Modifier
            .fillMaxSize(),
            text = "WiD not found",
            textAlign = TextAlign.Center)
        return
    }

    var updatedDetail by remember { mutableStateOf(wiD.detail) }
    var isEditing by remember { mutableStateOf(false) }

    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isDeleteButtonPressed) {
        if (isDeleteButtonPressed) {
            delay(2000L)
            isDeleteButtonPressed = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0f),
                    text = "WiD",
                    style = TextStyle(fontSize = 30.sp,
                        fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.acme_regular)))
                )

                val titleColorId = colorMap[wiD.title]
                if (titleColorId != null) {
                    val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                            .background(color = backgroundColor, shape = RoundedCornerShape(18.dp))
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "날짜",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wiD.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd ")),
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )

                    Text(text = "(",
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )

                    Text(
                        text = wiD.date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                        color = when (wiD.date.dayOfWeek) {
                            DayOfWeek.SATURDAY -> Color.Blue
                            DayOfWeek.SUNDAY -> Color.Red
                            else -> Color.Black
                        },
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )

                    Text(text = ")",
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "제목",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
                    text = titleMap[wiD.title] ?: wiD.title,
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "시작",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
                    text = wiD.start.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "종료",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
                    text = wiD.finish.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "경과",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
                    text = formatDuration(wiD.duration, mode = 2),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1.0F)
                        .padding(8.dp),
                    text = "설명",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Start)
                )

                IconToggleButton(
                    checked = isEditing,
                    onCheckedChange = { newValue ->
                        if (!newValue) {
                            val newDetail = updatedDetail
                            wiDService.updateWiDDetail(wiD.id, newDetail)
                        }
                        isEditing = newValue
                    },
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                ) {
//                    Icon(
//                        imageVector = if (isEditing) Icons.Filled.Done else Icons.Filled.Edit,
//                        contentDescription = if (isEditing) "Done" else "Edit",
//                        tint = Color.Blue // 아이콘 색상 설정
//                    )

                    Text(
                        text = if (isEditing) "완료" else "수정",
                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        color = Color.Blue
                    )
                }

            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = if (isEditing) updatedDetail else wiD.detail,
                onValueChange = { newText ->
                    if (isEditing) {
                        updatedDetail = newText
                    }
                },
                minLines = 3,
                placeholder = {
                    Text(style = TextStyle(Color.Black),
                        text = "설명 입력..")
                },
                readOnly = !isEditing,
            )
        }

        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isDeleteButtonPressed) {
                        wiDService.deleteWiDById(id = wiDId)
                        navController.popBackStack()
                        buttonsVisible.value = true
                    } else {
                        isDeleteButtonPressed = true
                    }
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = if (isDeleteButtonPressed) "한번 더 눌러 삭제" else "삭제",
                    color = if (isDeleteButtonPressed) Color.Red else Color.Unspecified,
                    style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center))
            }

            IconButton(
                onClick = {
                    navController.popBackStack()

                    buttonsVisible.value = true
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = "뒤로 가기",
                    style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDViewPreview() {
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDView(0, NavController(LocalContext.current), buttonsVisible)
}