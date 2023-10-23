package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import andpact.project.wid.util.titles
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
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

    val today: LocalDate = LocalDate.now()
    val date by remember { mutableStateOf(wiD.date) }

    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(wiD.title) }

    val currentTime: LocalTime = LocalTime.now().withSecond(0)

    var start by remember { mutableStateOf(wiD.start) }
    var showStartPicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = start.hour, initialMinute = start.minute)
    var isStartOverlap by remember { mutableStateOf(false) }
    var isStartOverCurrentTime by remember { mutableStateOf(false) }

    var finish by remember { mutableStateOf(wiD.finish) }
    var showFinishPicker by remember { mutableStateOf(false) }
    val finishTimePickerState = rememberTimePickerState(initialHour = finish.hour, initialMinute = finish.minute)
    var isFinishOverlap by remember { mutableStateOf(false) }
    var isFinishOverCurrentTime by remember { mutableStateOf(false) }

    var duration by remember { mutableStateOf(wiD.duration) }
    var isDurationUnderMin by remember { mutableStateOf(false) }
    var isDurationOverMax by remember { mutableStateOf(false) }

    var detail by remember { mutableStateOf(wiD.detail) }

    var isEditing by remember { mutableStateOf(false) }

    var isDeleteButtonPressed by remember { mutableStateOf(false) }

    var wiDList by remember { mutableStateOf(wiDService.readDailyWiDListByDate(date)) }
//    val currentIndex = wiDList.indexOf(wiD)

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
                                val newStart = LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)

                                start = newStart

                                for (existingWiD in wiDList) {
                                    if (existingWiD == wiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == wiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                        isFinishOverlap = true
                                        break
                                    } else {
                                        isFinishOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == wiD) {
                                        continue
                                    }

                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                        isStartOverlap = true
                                        isFinishOverlap = true
                                        break
                                    }
                                }

                                isStartOverCurrentTime = date == today && currentTime < start

                                duration = Duration.between(start, finish)
                                isDurationUnderMin = duration <= Duration.ZERO
                                isDurationOverMax = Duration.ofHours(12) < duration
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
                                val newFinish = LocalTime.of(finishTimePickerState.hour, finishTimePickerState.minute)

                                finish = newFinish

                                for (existingWiD in wiDList) {
                                    if (existingWiD == wiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= start && start <= existingWiD.finish) {
                                        isStartOverlap = true
                                        break
                                    } else {
                                        isStartOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == wiD) {
                                        continue
                                    }

                                    if (existingWiD.start <= finish && finish <= existingWiD.finish) {
                                        isFinishOverlap = true
                                        break
                                    } else {
                                        isFinishOverlap = false
                                    }
                                }

                                for (existingWiD in wiDList) {
                                    if (existingWiD == wiD) {
                                        continue
                                    }

                                    if (start <= existingWiD.start && existingWiD.finish <= finish) {
                                        isStartOverlap = true
                                        isFinishOverlap = true
                                        break
                                    }
                                }

                                isFinishOverCurrentTime = date == today && currentTime < finish

                                duration = Duration.between(start, finish)
                                isDurationUnderMin = duration <= Duration.ZERO
                                isDurationOverMax = Duration.ofHours(12) < duration
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
                val titleColorId = colorMap[title]
                val backgroundColor = if (titleColorId != null) {
                    Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                } else {
                    colorResource(id = R.color.light_gray)
                }

                Box(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                        )
                )

                Row(
                    modifier = Modifier
                        .padding(24.dp, 8.dp, 24.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "날짜",
                        style = TextStyle(fontSize = 14.sp))
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 8.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "date")

                    val dateText = buildAnnotatedString {
                        date.let {
                            append(it.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 ")))
                            append("(")
                            withStyle(
                                style = SpanStyle(
                                    color = when (it.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Unspecified
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
                            .padding(16.dp)
                            .weight(1f),
                        text = dateText,
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "제목",
                        style = TextStyle(fontSize = 14.sp))
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 8.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier
                        .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        contentDescription = "title")

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = titleMap[title] ?: title,
                    )

                    DropdownMenu(modifier = Modifier
//                        .padding(horizontal = 32.dp)
                        .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(8.dp)),
                        expanded = titleMenuExpanded,
                        onDismissRequest = {
                            titleMenuExpanded = false
                        },
                    ) {
                        titles.forEach { menuItem ->
                            DropdownMenuItem(modifier = Modifier,
                                onClick = {
                                    title = menuItem
                                    titleMenuExpanded = false
                                },
                                text = {
                                    Text(text = titleMap[menuItem] ?: menuItem)
                                }
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { titleMenuExpanded = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "titleMenuExpanded")
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = "시작 시간",
                        style = TextStyle(fontSize = 14.sp))

                    AnimatedVisibility(
                        visible = isStartOverlap,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "이미 등록된 시간입니다.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red)
                        )
                    }

                    AnimatedVisibility(
                        visible = isStartOverCurrentTime,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "${currentTime.format(DateTimeFormatter.ofPattern("a h:mm"))} 이전 시간이 필요합니다.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red)
                        )
                    }

//                    if (isStartOverlap) {
//                        Text(text = "이미 등록된 시간입니다.",
//                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
//                    } else if (isStartOverCurrentTime) {
//                        Text(text = "${currentTime.format(DateTimeFormatter.ofPattern("a h:mm"))} 이전 시간이 필요합니다.",
//                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
//                    }
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 8.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_access_time_24),
                        contentDescription = "start")

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = start.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                    )

                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { showStartPicker = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "showStartPicker")
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = "종료 시간",
                        style = TextStyle(fontSize = 14.sp))

                    AnimatedVisibility(
                        visible = isFinishOverlap,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "이미 등록된 시간입니다.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red)
                        )
                    }

                    AnimatedVisibility(
                        visible = isFinishOverCurrentTime,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "${currentTime.format(DateTimeFormatter.ofPattern("a h:mm"))} 이전 시간이 필요합니다.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red)
                        )
                    }

//                    if (isFinishOverlap) {
//                        Text(text = "이미 등록된 시간입니다.",
//                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
//                    } else if (isFinishOverCurrentTime) {
//                        Text(text = "${currentTime.format(DateTimeFormatter.ofPattern("a h:mm"))} 이전 시간이 필요합니다.",
//                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
//                    }
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 8.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_access_time_filled_24),
                        contentDescription = "finish")

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = finish.format(DateTimeFormatter.ofPattern("a h:mm:ss")),
                    )

                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { showFinishPicker = true },
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "showFinishPicker")
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = "지속 시간",
                        style = TextStyle(fontSize = 14.sp))

                    AnimatedVisibility(
                        visible = isDurationUnderMin,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(text = "1분 이상의 시간이 필요합니다.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
                    }

                    AnimatedVisibility(
                        visible = isDurationOverMax,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(text = "12시간 이하의 시간이 필요합니다.",
                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
                    }

//                    if (isDurationUnderMin) {
//                        Text(text = "1분 이상의 시간이 필요합니다.",
//                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
//                    } else if (isDurationOverMax) {
//                        Text(text = "12시간 이하의 시간이 필요합니다.",
//                            style = TextStyle(fontSize = 14.sp, color = Color.Red))
//                    }
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 16.dp, 8.dp)
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_timelapse_24),
                        contentDescription = "duration")

                    Text(modifier = Modifier
                        .padding(16.dp),
                        text = formatDuration(duration, mode = 2),
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "설명",
                        style = TextStyle(fontSize = 14.sp)
                    )
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp),
                        painter = painterResource(id = R.drawable.baseline_message_24),
                        contentDescription = "detail")

                    if (isEditing) {
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1f),
                            value = detail,
                            onValueChange = { newText ->
                                detail = newText
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            text = detail.ifEmpty { "설명 입력.." },
                        )
                    }

                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        VerticalDivider()

                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    detail = ""
                                },
                            painter = painterResource(id = R.drawable.baseline_clear_24),
                            contentDescription = "detailClear"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    AnimatedVisibility(
                        visible = !isDeleteButtonPressed,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "삭제",
                            color = Color.Unspecified,
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        )
                    }

                    AnimatedVisibility(
                        visible = isDeleteButtonPressed,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "한번 더 눌러 삭제",
                            color = Color.Red,
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        )
                    }
//                    Text(text = if (isDeleteButtonPressed) "한번 더 눌러 삭제" else "삭제",
//                        color = if (isDeleteButtonPressed) Color.Red else Color.Unspecified,
//                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center))
                }

                IconButton(
                    onClick = {
                        if (isEditing) {
                            wiDService.updateWiD(id = wiDId, date = date, title = title, start = start, finish = finish, duration = duration, detail = detail)

                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = !(isStartOverlap || isStartOverCurrentTime || isFinishOverlap || isFinishOverCurrentTime || isDurationUnderMin || isDurationOverMax || duration == Duration.ZERO)
                ) {
                    AnimatedVisibility(
                        visible = !isEditing,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "수정",
                            color = Color.Unspecified,
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        )
                    }

                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn(
                            initialAlpha = 0.1f,
                            animationSpec = tween(500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 0.1f,
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = "완료",
                            color = Color.Blue,
                            style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        )
                    }
//                    Text(text = if (isEditing) "완료" else "수정",
//                        color = if (isEditing) Color.Blue else Color.Unspecified,
//                        style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
//                    )
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