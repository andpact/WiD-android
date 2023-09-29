package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    val date by remember { mutableStateOf(wiD.date) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var title by remember { mutableStateOf(wiD.title) }

    var start by remember { mutableStateOf(wiD.start) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute)
    var isStartOverlap by remember { mutableStateOf(false) }

    var finish by remember { mutableStateOf(wiD.finish) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute)
    var isFinishOverlap by remember { mutableStateOf(false) }

    var duration by remember { mutableStateOf(wiD.duration) }
    var isDurationMinOrMax by remember { mutableStateOf(false) }

    var detail by remember { mutableStateOf(wiD.detail) }

    var isEditing by remember { mutableStateOf(false) }

    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    var wiDList by remember { mutableStateOf(wiDService.readWiDListByDate(date)) }
    val currentIndex = wiDList.indexOf(wiD)

    LaunchedEffect(isDeleteButtonPressed) {
        if (isDeleteButtonPressed) {
            delay(2000L)
            isDeleteButtonPressed = false
        }
    }

    BackHandler(enabled = true) { // 휴대폰 뒤로 가기 버튼 클릭 시
        navController.popBackStack()
        buttonsVisible.value = true
    }

//    fun updateWiDListAndOverlapFlags() {
//        duration = Duration.between(start, finish)
//
//        isDurationMinOrMax = Duration.ofHours(12) < duration || duration <= Duration.ZERO
//
//        for (existingWiD in wiDList) {
//            if (existingWiD.start <= start && start <= existingWiD.finish) {
//                isStartOverlap = true
//                break
//            } else {
//                isStartOverlap = false
//            }
//        }
//
//        for (existingWiD in wiDList) {
//            if (existingWiD.start <= finish && finish <= existingWiD.finish) {
//                isFinishOverlap = true
//                break
//            } else {
//                isFinishOverlap = false
//            }
//        }
//
//        for (existingWiD in wiDList) {
//            if (start <= existingWiD.start && existingWiD.finish <= finish) {
//                isStartOverlap = true
//                isFinishOverlap = true
//                break
//            }
//        }
//    }
//
//    updateWiDListAndOverlapFlags()

    Box(modifier = Modifier
        .fillMaxSize())
    {
        if (showStartPicker) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
                onDismissRequest = { showStartPicker = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.LightGray.copy(alpha = 0.3f))
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = startTimePickerState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showStartPicker = false }
                        ) {
                            Text(text = "취소")
                        }

                        TextButton(
                            onClick = {
                                showStartPicker = false
//                                val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute).withSecond(0)
                                val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                if (newStart.isAfter(finish)) {
                                    finish = newStart

                                    if (currentIndex < wiDList.size - 1) {
                                        val nextWiD = wiDList[currentIndex + 1]
                                        isFinishOverlap = finish.isAfter(nextWiD.start) || finish == nextWiD.start
                                    }

                                }

                                start = newStart

                                val today = LocalDate.now()
                                if (date == today && currentTime < start) {
                                    start = currentTime
                                    finish = currentTime

                                    if (currentIndex < wiDList.size - 1) {
                                        val nextWiD = wiDList[currentIndex + 1]
                                        isFinishOverlap = finish.isAfter(nextWiD.start) || finish == nextWiD.start
                                    }

                                }

                                duration = Duration.between(start, finish)

                                if (currentIndex > 0) {
                                    val previousWiD = wiDList[currentIndex - 1]
                                    isStartOverlap = start.isBefore(previousWiD.finish) || start == previousWiD.finish
                                }
                            }
                        ) {
                            Text(text = "확인")
                        }
                    }
                }
            }
        }

        if (showFinishPicker) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.light_gray), shape = RoundedCornerShape(8.dp)),
                onDismissRequest = { showFinishPicker = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = finishTimePickerState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showFinishPicker = false }
                        ) {
                            Text(text = "취소")
                        }

                        TextButton(
                            onClick = {
                                showFinishPicker = false
//                                val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute).withSecond(0)
                                val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute)

                                if (newFinish.isBefore(start)) {
                                    start = newFinish

                                    if (currentIndex > 0) {
                                        val previousWiD = wiDList[currentIndex - 1]
                                        isStartOverlap = start.isBefore(previousWiD.finish) || start == previousWiD.finish
                                    }
                                }

                                finish = newFinish

                                val today = LocalDate.now()
                                if (date == today && currentTime < finish) {
                                    finish = currentTime
                                }

                                duration = Duration.between(start, finish)

                                if (currentIndex < wiDList.size - 1) {
                                    val nextWiD = wiDList[currentIndex + 1]
                                    isFinishOverlap = finish.isAfter(nextWiD.start) || finish == nextWiD.start
                                }

                            }
                        ) {
                            Text(text = "확인")
                        }
                    }
                }
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

                    val titleColorId = colorMap[title]
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
                            text = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd ")),
                            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                        )

                        Text(text = "(",
                            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                        )

                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                            color = when (date.dayOfWeek) {
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
                        modifier = Modifier.clickable(enabled = isEditing) { titleMenuExpanded = true }
                            .padding(8.dp)
                            .weight(1.0F),
                        text = titleMap[title] ?: title,
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )

                    DropdownMenu(modifier = Modifier
                        .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(8.dp)),
                        offset = DpOffset(150.dp, 0.dp),
                        expanded = titleMenuExpanded,
                        onDismissRequest = {
                            titleMenuExpanded = false
                        }
                    ) {
                        titles.forEach { menuItem ->
                            DropdownMenuItem(
                                onClick = {
                                    title = menuItem
                                    titleMenuExpanded = true
                                },
                                text = {
                                    Text(text = titleMap[menuItem] ?: menuItem)
                                }
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "시작",
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.clickable(enabled = isEditing) { showStartPicker = true },
                            text = start.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                        )

                        if (isStartOverlap) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_priority_high_24),
                                contentDescription = "StartOverlap",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = Color.Red
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "종료",
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.clickable(enabled = isEditing) { showFinishPicker = true },
                            text = finish.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                        )

                        if (isFinishOverlap) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_priority_high_24),
                                contentDescription = "FinishOverlap",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = Color.Red
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "경과",
                        style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatDuration(duration, mode = 2),
                            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                        )

                        if (isDurationMinOrMax) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_priority_high_24),
                                contentDescription = "DurationMinOrMax",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = Color.Red
                            )
                        }
                    }
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
                }

                if (isEditing) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                        value = detail,
                        onValueChange = { newText ->
                            detail = newText
                        },
                        minLines = 5,
                        placeholder = {
                            Text(style = TextStyle(Color.Black),
                                text = "설명 입력..")
                        },
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        text = detail.ifEmpty { "설명 입력.." },
                        minLines = 5
                    )
                }
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
                        if (isEditing) {
                            wiDService.updateWiD(id = wiDId, newStartTime = start, newFinishTime = finish, newDetail = detail)

                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = !(isStartOverlap || isFinishOverlap || isDurationMinOrMax)
                ) {
                    Text(text = if (isEditing) "완료" else "수정",
                        color = if (isEditing) Color.Blue else Color.Unspecified,
                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                    )
                }

//                IconButton(
//                    onClick = {
//                        title = wiD.title
//                        start = wiD.start
//                        finish = wiD.finish
//                        duration = wiD.duration
//                        detail = wiD.detail
//                    },
//                    modifier = Modifier
//                        .weight(1f)
//                ) {
//                    Text(text = "초기화",
//                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
//                    )
//                }

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