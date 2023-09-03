package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun WiDCreateFragment(buttonsVisible: MutableState<Boolean>) {
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

    LaunchedEffect(isRunning) {
        while (isRunning) {
            currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime
            delay(1000)
        }
    }

    fun startWiD() {
        date = LocalDate.now()
        start = LocalTime.now()
//        start = LocalTime.now().minusHours(1)

        buttonsVisible.value = false

        startTime = System.currentTimeMillis() - elapsedTime
        buttonText = "중지"
    }

    fun finishWiD() {
        finish = LocalTime.now()
//        finish = LocalTime.now().plusHours(1)

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
        buttonText = "시작"

        buttonsVisible.value = true
    }

    Column(modifier = Modifier
        .fillMaxSize()
//        .background(color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
                .wrapContentSize(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (buttonsVisible.value) {
                        IconButton(modifier = Modifier,
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
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    if (buttonsVisible.value) {
                        IconButton(
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
                    fontSize = 60.sp,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.tektur_variablefont_wdth_wght)),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (!isRunning) {
                                startWiD()
                            } else {
                                finishWiD()
                            }
                            isRunning = !isRunning
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                BorderStroke(1.dp, Color.Black),
                                RoundedCornerShape(0.dp, 0.dp, 0.dp, 8.dp)
                            ),
                    ) {
                        Text(
                            text = buttonText,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            if (!isRunning) {
                                resetWiD()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                BorderStroke(1.dp, Color.Black),
                                RoundedCornerShape(0.dp, 0.dp, 8.dp, 0.dp)
                            ),
                        enabled = !isRunning
                    ) {
                        Text(
                            text = "초기화",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        if (!buttonsVisible.value) {
            Text(modifier = Modifier
                .fillMaxWidth(),
                text = "WiD",
                textAlign = TextAlign.Center,
                fontSize = 63.sp, // Bottom Bar 높이 == 63
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.acme_regular))
            )
        }
    }
}

fun formatTime(time: Long): String {
//    val minutes = (time / 60000).toString().padStart(2, '0')
//    val seconds = ((time % 60000) / 1000).toString().padStart(2, '0')
//    val milliseconds = (time % 1000 / 10).toString().padStart(2, '0')
//    return "$minutes:$seconds.$milliseconds"

    val hours = (time / 3600000).toString().padStart(2, '0')
    val minutes = ((time % 3600000) / 60000).toString().padStart(2, '0')
    val seconds = ((time % 60000) / 1000).toString().padStart(2, '0')
    return "$hours:$minutes:$seconds"
}

@Preview(showBackground = true)
@Composable
fun WiDCreateFragmentPreview() {
    val buttonsVisible = remember { mutableStateOf(false) }
    WiDCreateFragment(buttonsVisible = buttonsVisible)
}