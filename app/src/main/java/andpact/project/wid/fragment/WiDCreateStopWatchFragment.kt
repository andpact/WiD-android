package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.formatTime
import andpact.project.wid.util.titleMap
import andpact.project.wid.util.titles
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun WiDCreateStopWatchFragment(buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)

    var date: LocalDate = LocalDate.now()

    var titleIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf(titles[titleIndex]) }

    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

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
        elapsedTime = 0

        buttonsVisible.value = true

        buttonText = "시작"
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime
            if (60 * 60 * 12 * 1000 <= elapsedTime) {
                finishWiD()
                resetWiD()
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .wrapContentSize(Alignment.Center),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Row(modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = buttonsVisible.value,
                enter = fadeIn(
                    initialAlpha = 0.1f,
                    animationSpec = tween(500)
                ),
                exit = fadeOut(
                    targetAlpha = 0.1f,
                    animationSpec = tween(500)
                )
            ) {
                IconButton(modifier = Modifier
                    .padding(horizontal = 8.dp),
                    onClick = {
                        titleIndex = (titleIndex - 1 + titles.size) % titles.size
                        title = titles[titleIndex]
                    },
                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous title")
                }
            }

            Text(modifier = Modifier
                    .weight(1f),
                text = titleMap[title] ?: title,
                style = TextStyle(textAlign = TextAlign.Center, fontSize = 40.sp)
            )

            AnimatedVisibility(
                visible = buttonsVisible.value,
                enter = fadeIn(
                    initialAlpha = 0.1f,
                    animationSpec = tween(500)
                ),
                exit = fadeOut(
                    targetAlpha = 0.1f,
                    animationSpec = tween(500)
                )
            ) {
                IconButton(modifier = Modifier
                    .padding(horizontal = 8.dp),
                    onClick = {
                        titleIndex = (titleIndex + 1) % titles.size
                        title = titles[titleIndex]
                    },
                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "Next title")
                }
            }
        }

        Text(modifier = Modifier
                .fillMaxWidth(),
            text = formatTime(elapsedTime),
            style = TextStyle(fontSize = 50.sp, textAlign = TextAlign.Center, fontFamily = FontFamily(Font(R.font.tektur_variablefont_wdth_wght)))
        )

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TextButton(modifier = Modifier
                .size(50.dp)
                .background(if (!(!isRunning && !buttonsVisible.value)) Color.Gray else Color.Black, shape = CircleShape),
                onClick = { if (!isRunning) { resetWiD() } },
                enabled = !isRunning && !buttonsVisible.value
            ) {
                Text(text = "초기화",
                    style = TextStyle(color = Color.White)
                )
            }

            TextButton(modifier = Modifier
                .size(50.dp)
                .background(color = when (buttonText) {
                    "중지" -> colorResource(id = R.color.orange_red)
                    "계속" -> colorResource(id = R.color.lime_green)
                    else -> Color.Black
                }, shape = CircleShape),
                onClick = {
                    if (!isRunning) startWiD() else finishWiD()
                }
            ) {
                Text(text = buttonText,
                    style = TextStyle(color = Color.White)
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