package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.formatTime
import andpact.project.wid.util.titleMap
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
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

    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
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

            val firstWiD = WiD(
                id = 0,
                date = date,
                title = title,
                start = start,
                finish = midnight.plusSeconds(-1),
                duration = Duration.between(start, midnight.plusSeconds(-1)),
                detail = ""
            )
            wiDService.createWiD(firstWiD)

            val nextDate = date.plusDays(1)

            val secondWiD = WiD(
                id = 0,
                date = nextDate,
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

    Box() {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(32.dp)
                .wrapContentSize(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            titleIndex = (titleIndex - 1 + titles.size) % titles.size
                            title = titles[titleIndex]
                        },
                    ) {
                        Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "prevTitle")
                    }
                }

                Text(
                    modifier = Modifier
                        .weight(1.0f),
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
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            titleIndex = (titleIndex + 1) % titles.size
                            title = titles[titleIndex]
                        },
                    ) {
                        Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "nextTitle")
                    }
                }
            }

            Text(
                text = formatTime(elapsedTime),
                modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(fontSize = 50.sp, textAlign = TextAlign.Center, fontFamily = FontFamily(Font(R.font.tektur_variablefont_wdth_wght)))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (!isRunning) startWiD() else finishWiD()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = buttonText,
                        color = when (buttonText) {
                            "중지" -> Color.Red
                            "계속" -> colorResource(id = R.color.exercise)
                            else -> Color.Unspecified
                        },
                        style = TextStyle(fontSize = 20.sp)
                    )
                }

                IconButton(
                    onClick = {
                        if (!isRunning) {
                            resetWiD()
                        }
                    },
                    modifier = Modifier
                        .weight(1f),
                    enabled = !isRunning && !buttonsVisible.value
                ) {
                    Text(
                        text = "초기화",
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            AnimatedVisibility(
                visible = !buttonsVisible.value,
                enter = expandVertically{ 0 },
                exit = shrinkVertically{ 0 },
            ) {
                Text(modifier = Modifier
                    .fillMaxWidth(),
                    text = "WiD", // Bottom Bar 높이 == 63.sp
                    style = TextStyle(textAlign = TextAlign.Center, fontSize = 50.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.acme_regular)))
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WiDCreateStopWatchFragmentPreview() {
//    val buttonsVisible = remember { mutableStateOf(true) }
//    WiDCreateStopWatchFragment(buttonsVisible = buttonsVisible)
//}