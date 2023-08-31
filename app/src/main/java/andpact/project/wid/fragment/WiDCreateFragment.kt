package andpact.project.wid.fragment

import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun WiDCreateFragment() {
    val wiDService = WiDService(context = LocalContext.current)

    var date: LocalDate = LocalDate.now()

    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var titleIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf(titles[titleIndex]) }

    var start: LocalTime by remember { mutableStateOf(LocalTime.now()) }
    var finish: LocalTime by remember { mutableStateOf(LocalTime.now()) }

    var runningTime by remember { mutableStateOf(Duration.ZERO) }

    var isStarted by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var isReset by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    fun finishWiD() {
        coroutineScope.launch {
            isStarted = !isStarted // true to false
            isFinished = !isFinished  // false to true

            finish = LocalTime.now()
//            finish = LocalTime.now().plusHours(1)

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
    }

    fun runningTimer(scope: CoroutineScope) {
        scope.launch {
            while (isStarted) {
                delay(1000)
                runningTime = runningTime.plusSeconds(1)
            }
        }
    }

    fun startWiD() {
        isStarted = !isStarted // false to true
        isReset = !isReset // true to false

        if (isStarted) {
            date = LocalDate.now()
            start = LocalTime.now()
//            start = LocalTime.now().minusHours(1)
            runningTime = Duration.ZERO
            runningTimer(coroutineScope)
        } else {
            coroutineScope.launch { coroutineContext.cancelChildren() }
        }
    }

    fun resetWiD() {
        isReset = !isReset // false to true
        coroutineScope.launch {
            runningTime = Duration.ZERO
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .wrapContentSize(Alignment.Center)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Add horizontal spacing between buttons
        ) {
            IconButton(
                onClick = {
                    titleIndex = (titleIndex - 1 + titles.size) % titles.size
                    title = titles[titleIndex]
                },
                modifier = Modifier.weight(1.0f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                enabled = isReset
            ) {
                Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "prevTitle")
            }

            Text(
                modifier = Modifier.weight(1.0f)
                    .align(Alignment.CenterVertically),
                text = titleMap[title] ?: title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = {
                    titleIndex = (titleIndex + 1) % titles.size
                    title = titles[titleIndex]
                },
                modifier = Modifier.weight(1.0f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                enabled = isReset
            ) {
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "nextTitle")
            }
        }

        Text( // Duration TextView
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = formatDuration(duration = runningTime, mode = 0),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Add horizontal spacing between buttons
        ) {
            IconButton(
                onClick = {
                    startWiD()
                },
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                enabled = isReset
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "start")
            }

            IconButton(
                onClick = {
                    finishWiD()
                },
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                enabled = isStarted
            ) {
                Icon(imageVector = Icons.Filled.Done, contentDescription = "finish")
            }

            IconButton(
                onClick = {
                    resetWiD()
                },
                modifier = Modifier.weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                enabled = !isReset && isFinished
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "reset")
            }

//            Button(
//                onClick = {
//                    wiDService.deleteAllWiD()
//                },
//            ) {
//                Icon(imageVector = Icons.Filled.Delete, contentDescription = "deleteAllWiD")
//            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateFragmentPreview() {
    WiDCreateFragment()
}