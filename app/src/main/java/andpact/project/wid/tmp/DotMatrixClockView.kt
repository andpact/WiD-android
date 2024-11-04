package andpact.project.wid.chartView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalTime

@Composable
fun DotMatrixClockView() {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update the time every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = LocalTime.now()
        }
    }

    // Generate the dot matrix for the current time
    val dotMatrix = getClockDotMatrix(currentTime.hour, currentTime.minute, currentTime.second)

    // Display the dot matrix
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        dotMatrix.forEach { row ->
            Row(
                modifier = Modifier.height(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { isActive ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (isActive) Color.Black else Color.Gray)
                    )
                }
            }
        }
    }
}

// Function to generate the clock dot matrix based on current time
fun getClockDotMatrix(hour: Int, minute: Int, second: Int): Array<Array<Boolean>> {
    val size = 11
    val matrix = Array(size) { Array(size) { false } }

    // Clock positions representing each hour in a circular pattern
    val clockPositions = listOf(
        Pair(5, 0), Pair(8, 2), Pair(10, 5), Pair(8, 8),
        Pair(5, 10), Pair(2, 8), Pair(0, 5), Pair(2, 2),
        Pair(5, 0), Pair(8, 2), Pair(0, 8), Pair(2, 8)
    )

    // Calculate hour, minute, and second positions
    val hourPosition = clockPositions[hour % 12]
    val minutePosition = clockPositions[minute / 5]
    val secondPosition = clockPositions[second / 5]

    // Set positions in the matrix for hour, minute, and second hands
    matrix[hourPosition.first][hourPosition.second] = true
    matrix[minutePosition.first][minutePosition.second] = true
    matrix[secondPosition.first][secondPosition.second] = true

    return matrix
}

@Preview(showBackground = true)
@Composable
fun DotMatrixClockPreview() {
    DotMatrixClockView()
}