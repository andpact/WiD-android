package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import android.widget.NumberPicker
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.IconCompat
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDCreateTimerFragment(buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)

    var date: LocalDate = LocalDate.now()

    var titleMenuExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(titles[0]) }

    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    var isRunning by remember { mutableStateOf(false) }

    var finishTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var remainingTime by remember { mutableStateOf(0L) }

    var buttonText by remember { mutableStateOf("시작") }

    // 버튼 활성화 여부를 나타내는 상태 변수 추가
//    var minusButtonEnabled by remember { mutableStateOf(false) }
//    var plusButtonEnabled by remember { mutableStateOf(true) }

    fun startWiD() {
        isRunning = true

        date = LocalDate.now()
        start = LocalTime.now()

        buttonsVisible.value = false

        buttonText = "중지"

        finishTime = System.currentTimeMillis() + remainingTime
    }

    fun finishWiD() {
        isRunning = false

        finish = LocalTime.now()

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
                detail = ""
            )
            wiDService.createWiD(firstWiD)

            val secondWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = midnight,
                finish = finish,
                duration = Duration.between(midnight, finish),
                detail = ""
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
                detail = ""
            )
            wiDService.createWiD(newWiD)
        }
    }

    fun resetWiD() {
        finishTime = 0
        currentTime = 0
        remainingTime = 0

        buttonText = "시작"

        buttonsVisible.value = true
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            if (finishTime > System.currentTimeMillis()) {
                currentTime = System.currentTimeMillis()
                remainingTime = finishTime - currentTime
            } else {
                finishWiD()
                resetWiD()
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    // 전체 화면
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    ) {
        // 타이머 남은 시간 텍스트 뷰
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(bottom = 140.dp))
            .align(Alignment.Center),
            text = formatTimerTime(time = remainingTime),
            style = TextStyle(fontSize = 50.sp, textAlign = TextAlign.Center, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .size(10.dp)
                .background(color = colorResource(id = colorMap[title] ?: R.color.light_gray))
            )

            ExposedDropdownMenuBox(modifier = Modifier
                .weight(1f),
                expanded = titleMenuExpanded,
                onExpandedChange = { if (!isRunning && buttonsVisible.value) titleMenuExpanded = !titleMenuExpanded },
            ) {
                TextField(modifier = Modifier
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

            TextButton(modifier = Modifier
                .weight(1f)
                .background(
                    if (!(!isRunning && !buttonsVisible.value)) Color.LightGray else Color.Black,
                    shape = RoundedCornerShape(8.dp)
                ),
                onClick = { if (!isRunning) { resetWiD() } },
                enabled = !isRunning && !buttonsVisible.value
            ) {
                Text(text = "초기화",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TextButton(modifier = Modifier
                .weight(1f)
                .background(
                    if (buttonText == "중지") {
                        colorResource(id = R.color.orange_red)
                    } else if (buttonText == "계속") {
                        colorResource(id = R.color.lime_green)
                    } else {
                        colorResource(id = R.color.deep_sky_blue)
                    }, shape = RoundedCornerShape(8.dp)
                ),
                onClick = {
                    if (!isRunning) startWiD() else finishWiD()
                },
            ) {
                Text(text = buttonText,
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateTimerFragmentPreview() {
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDCreateTimerFragment(buttonsVisible = buttonsVisible)
}