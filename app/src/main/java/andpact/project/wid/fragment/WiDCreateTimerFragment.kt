package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.titleMap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.IconCompat
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun WiDCreateTimerFragment(buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)

    var date: LocalDate = LocalDate.now()

    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var titleIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf(titles[titleIndex]) }

    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    var isRunning by remember { mutableStateOf(false) }

    var finishTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var remainingTime by remember { mutableStateOf(0L) }

    var buttonText by remember { mutableStateOf("시작") }

    // 버튼 활성화 여부를 나타내는 상태 변수 추가
    var minusButtonEnabled by remember { mutableStateOf(false) }
    var plusButtonEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            if (finishTime > System.currentTimeMillis()) {
                currentTime = System.currentTimeMillis()
                remainingTime = finishTime - currentTime
            } else {
                isRunning = false
            }
            delay(1000) // 1.000초에 한 번씩 while문이 실행되어 초기화됨.
        }
    }

    fun startWiD() {
        date = LocalDate.now()
        start = LocalTime.now()

        buttonsVisible.value = false

        buttonText = "중지"

        finishTime = System.currentTimeMillis() + remainingTime
        isRunning = true
    }

    fun finishWiD() {
        finish = LocalTime.now()

        isRunning = false

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
        finishTime = 0
        currentTime = 0
        remainingTime = 0
        buttonText = "시작"

        buttonsVisible.value = true

        minusButtonEnabled = false
    }

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        if (!buttonsVisible.value) {
            Text(modifier = Modifier
                .fillMaxWidth(),
                text = "",  // Tap Bar 높이 == 39.sp
                style = TextStyle(textAlign = TextAlign.Center, fontSize = 39.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.acme_regular)),)
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
                .wrapContentSize(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
//                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = buttonsVisible.value,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(500)
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(500)
                        )
                    ) {
                        IconButton(
                            modifier = Modifier.padding(8.dp),
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
                        style = TextStyle(textAlign = TextAlign.Center, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    )

                    AnimatedVisibility(
                        visible = buttonsVisible.value,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(500)
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(500)
                        )
                    ) {
                        IconButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                titleIndex = (titleIndex + 1) % titles.size
                                title = titles[titleIndex]
                            },
                        ) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "nextTitle")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = buttonsVisible.value,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(500)
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(500)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                if (remainingTime > 0) {
                                    finishTime -= 3_600_000
                                    remainingTime = finishTime - currentTime
                                }
                                if (remainingTime <= 0) {
                                    minusButtonEnabled = false
                                }
                                if (remainingTime < 12 * 3_600_000) {
                                    plusButtonEnabled = true
                                }
                            },
                            enabled = minusButtonEnabled
                        ) {
                            Icon(painter = painterResource(id = R.drawable.baseline_remove_24), contentDescription = "minus1Hour")
                        }
                    }

                    Text( // 남은 시간 텍스트 뷰
                        text = formatTime(time = remainingTime),
                        modifier = Modifier.weight(1.0f),
                        style = TextStyle(fontSize = 50.sp, textAlign = TextAlign.Center, fontFamily = FontFamily(Font(R.font.tektur_variablefont_wdth_wght)))
                    )

                    AnimatedVisibility(
                        visible = buttonsVisible.value,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(500)
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(500)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                finishTime += 3_600_000
                                remainingTime = finishTime - currentTime
                                if (remainingTime > 0) {
                                    minusButtonEnabled = true
                                }
                                if (remainingTime >= 12 * 3_600_000) {
                                    plusButtonEnabled = false
                                }
                            },
                            enabled = plusButtonEnabled
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "plus1Hour")
                        }
                    }
                }

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
                        },
                        modifier = Modifier
                            .weight(1f),
                        enabled = minusButtonEnabled
                    ) {
                        Text(
                            text = buttonText,
                            color = when (buttonText) {
                                "중지" -> Color.Red
                                "계속" -> Color.Green
                                else -> Color.Unspecified
                            },
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                fontSize = 63.sp, // Bottom Bar 높이 == 63.sp
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.acme_regular))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateTimerFragmentPreview() {
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDCreateTimerFragment(buttonsVisible = buttonsVisible)
}