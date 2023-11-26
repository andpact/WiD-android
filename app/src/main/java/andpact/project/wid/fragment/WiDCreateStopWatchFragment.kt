package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDCreateStopWatchFragment(buttonsVisible: MutableState<Boolean>) {
    // WiD
    val wiDService = WiDService(context = LocalContext.current)

    // 날짜
    var date: LocalDate = LocalDate.now()

    // 제목
    var title by remember { mutableStateOf(titles[0]) }
    var titleMenuExpanded by remember { mutableStateOf(false) }

    // 시작 시간
    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    // 종료 시간
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    // 스톱워치
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var buttonText by remember { mutableStateOf("시작") }

    fun startWiD() {
        isRunning = true

        date = LocalDate.now()
        start = LocalTime.now()

        startTime = System.currentTimeMillis() - elapsedTime

        buttonsVisible.value = false

        buttonText = "중지"
    }

    fun finishWiD() {
        isRunning = false

        finish = LocalTime.now()
//        finish = LocalTime.now().plusHours(10).plusMinutes(33).plusSeconds(33)

        buttonText = "계속"

        if (finish.isBefore(start)) {
            val midnight = LocalTime.MIDNIGHT

            val previousDate = date.minusDays(1)

            val firstWiD = WiD(
                id = 0,
                date = previousDate,
                title = title,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = Duration.between(start, midnight.plusSeconds(-1)),
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish),
            )
            wiDService.createWiD(secondWiD)
        } else {
            val newWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = start,
                finish = finish,
                duration = Duration.between(start, finish),
            )
            wiDService.createWiD(newWiD)
        }
    }

    fun resetWiD() {
        elapsedTime = 0

        buttonsVisible.value = true

        buttonText = "시작"
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime

            // 12시간이 넘어가면 자동으로 WiD가 등록되도록 함.
            if (60 * 60 * 12 * 1000 <= elapsedTime) {
                finishWiD()
                resetWiD()
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    // 전체 화면
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white))
            .padding(16.dp)
    ) {
        // 스톱워치 시간 표시
        Text(
            modifier = Modifier
                .padding(PaddingValues(bottom = 140.dp))
                .align(Alignment.Center),
            text = formatStopWatchTime(elapsedTime),
            style = TextStyle(textAlign = TextAlign.End)
        )

        // 제목 선택 및 버튼 모음
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(10.dp)
                    .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
            )

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .weight(1f),
                expanded = titleMenuExpanded,
                onExpandedChange = { if (!isRunning && buttonsVisible.value) titleMenuExpanded = !titleMenuExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(),
                    readOnly = true,
                    value = titleMap[title] ?: "공부",
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    onValueChange = {},
                    trailingIcon = { if (!isRunning && buttonsVisible.value) ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                ExposedDropdownMenu(
                    expanded = titleMenuExpanded,
                    onDismissRequest = { titleMenuExpanded = false }
                ) {
                    titles.forEach { menuTitle ->
                        DropdownMenuItem(
                            text = { Text(text = titleMap[menuTitle] ?: "공부") },
                            onClick = {
                                title = menuTitle
                                titleMenuExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (!(!isRunning && !buttonsVisible.value)) Color.LightGray else Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    ),
                onClick = { if (!isRunning) { resetWiD() } },
                enabled = !isRunning && !buttonsVisible.value
            ) {
                Text(
                    text = "초기화",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = when (buttonText) {
                            "중지" -> colorResource(id = R.color.orange_red)
                            "계속" -> colorResource(id = R.color.lime_green)
                            else -> colorResource(id = R.color.deep_sky_blue)
                        }, shape = RoundedCornerShape(8.dp)
                    ),
                onClick = {
                    if (!isRunning) startWiD() else finishWiD()
                }
            ) {
                Text(
                    text = buttonText,
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateStopWatchFragmentPreview() {
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDCreateStopWatchFragment(buttonsVisible = buttonsVisible)
}