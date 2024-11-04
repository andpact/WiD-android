package andpact.project.wid.chartView

import andpact.project.wid.ui.theme.Transparent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DotMatrixNumberView(number: Int) {
    // Define a 5x7 dot matrix for the digit '5' as an example
    val dotMatrix: Array<Array<Boolean>> = when (number) {
        0 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, true, true, true, true)
        )
        1 -> arrayOf(
            arrayOf(false, false, true, false, false),
            arrayOf(false, true, true, false, false),
            arrayOf(true, false, true, false, false),
            arrayOf(false, false, true, false, false),
            arrayOf(false, false, true, false, false),
            arrayOf(false, false, true, false, false),
            arrayOf(true, true, true, true, true)
        )
        2 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, false),
            arrayOf(true, false, false, false, false),
            arrayOf(true, true, true, true, true)
        )
        3 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(true, true, true, true, true)
        )
        4 -> arrayOf(
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true)
        )
        5 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, false),
            arrayOf(true, false, false, false, false),
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(true, true, true, true, true)
        )
        6 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, false),
            arrayOf(true, false, false, false, false),
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, true, true, true, true)
        )
        7 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true)
        )
        8 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, true, true, true, true)
        )
        9 -> arrayOf(
            arrayOf(true, true, true, true, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, false, false, false, true),
            arrayOf(true, true, true, true, true),
            arrayOf(false, false, false, false, true),
            arrayOf(false, false, false, false, true),
            arrayOf(true, true, true, true, true)
        )
        else -> Array(7) { Array(5) { false } }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        for (row in dotMatrix) {
            Row {
                for (isDotOn in row) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .padding(1.dp)
                            .background(
                                color = if (isDotOn) Color.Black else Transparent,
//                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DotMatrixNumberPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DotMatrixNumberView(number = 1)
        DotMatrixNumberView(number = 2)
        DotMatrixNumberView(number = 3)
    }
}