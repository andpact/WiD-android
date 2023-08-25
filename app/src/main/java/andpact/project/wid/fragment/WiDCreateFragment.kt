package andpact.project.wid.fragment

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun WiDCreateFragment() {
    var date by remember { mutableStateOf(LocalDate.now()) }
    val titles = arrayOf("STUDY", "WORK", "READING", "EXERCISE", "HOBBY", "TRAVEL", "SLEEP")
    var titleIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf(titles[titleIndex]) }
    val currentTime: LocalTime = LocalTime.now()
    var start by remember { mutableStateOf(currentTime) }
    var finish by remember { mutableStateOf(currentTime) }
    val duration: Duration = Duration.between(start, finish)
    val durationFormatted = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes(), duration.seconds % 60)
    var detail: String

    var isAfterStart by remember { mutableStateOf(false) }
    var isAfterFinish by remember { mutableStateOf(false) }

    val updateInterval = 1000L // 1 second in milliseconds
    val startHandler by remember { mutableStateOf(Handler(Looper.getMainLooper())) }
    val startRunnable = remember {
        object : Runnable {
            override fun run() {
                date = LocalDate.now()
                start = LocalTime.now()
                finish = start
                startHandler.postDelayed(this, updateInterval)
            }
        }
    }

    val finishHandler by remember { mutableStateOf(Handler(Looper.getMainLooper())) }
    val finishRunnable = remember {
        object : Runnable {
            override fun run() {
                finish = LocalTime.now()
                finishHandler.postDelayed(this, updateInterval)
            }
        }
    }

    LaunchedEffect(isAfterStart, isAfterFinish) {
        if (isAfterStart) { // 시작 버튼 누른 후
            startHandler.removeCallbacks(startRunnable)
            finishHandler.post(finishRunnable)
        } else if (isAfterFinish) { // 종료 버튼 누른 후
            finishHandler.removeCallbacks(finishRunnable)
        } else { // 초기 상태
            startHandler.post(startRunnable)
            finishHandler.removeCallbacks(finishRunnable)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .wrapContentSize(Alignment.Center)
            .padding(16.dp), // Add padding for spacing
        verticalArrangement = Arrangement.spacedBy(16.dp) // Add vertical spacing between buttons
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Add horizontal spacing between buttons
        ) {
            Button(
                onClick = {
                    titleIndex = (titleIndex - 1 + titles.size) % titles.size
                    title = titles[titleIndex]
                },
                modifier = Modifier.weight(1f),
                enabled = !isAfterStart && !isAfterFinish
            ) {
                Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "previousTitle")
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
            )

            Button(
                onClick = {
                    titleIndex = (titleIndex + 1) % titles.size
                    title = titles[titleIndex]
                },
                modifier = Modifier.weight(1f),
                enabled = !isAfterStart && !isAfterFinish
            ) {
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "nextTitle")
            }
        }

        Text( // Duration TextView
            text = durationFormatted,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Add horizontal spacing between buttons
        ) {
            Button(
                onClick = {
                    isAfterStart = true
                },
                modifier = Modifier.weight(1f),
                enabled = !isAfterStart && !isAfterFinish
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "start")
            }

            Button(
                onClick = {
                    isAfterStart = false
                    isAfterFinish = true
                },
                modifier = Modifier.weight(1f),
                enabled = isAfterStart && !isAfterFinish
            ) {
                Icon(imageVector = Icons.Filled.Done, contentDescription = "finish")
            }

            Button(
                onClick = {
                    isAfterFinish = false
                },
                modifier = Modifier.weight(1f),
                enabled = !isAfterStart && isAfterFinish
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "reset")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDCreateFragmentPreview() {
    WiDCreateFragment()
}