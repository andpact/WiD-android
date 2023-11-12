package andpact.project.wid.fragment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WheelPicker(
    minValue: Int,
    maxValue: Int,
    initialValue: Int,
    onValueChange: (Int) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }

    val context = LocalContext.current
    val density = LocalDensity.current.density

    // Calculate the item height based on density
    val itemHeight = 48.dp

    // Calculate the half visible item count
    val halfVisibleItemCount = 3

    // Calculate the visible item count
    val visibleItemCount = (2 * halfVisibleItemCount) + 1

    // Calculate the total height of the picker
    val pickerHeight = itemHeight * visibleItemCount

    // Scroll state to track the current scroll position
//    val scrollState = rememberLazyListState(initialItem = initialValue - minValue)
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = minValue)

    Box(
        modifier = Modifier
//            .height(pickerHeight)
            .fillMaxHeight()
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
//                .fillMaxSize()
                .fillMaxSize()
        ) {
            itemsIndexed((minValue..maxValue).toList()) { index, item ->
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .background(
                            if (index % 2 == 0) Color.White
                            else Color.LightGray
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toString(),
                        modifier = Modifier
                            .background(
                                if (item == value) Color.Blue
                                else Color.Transparent
                            ),
                        color = if (item == value) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            }
        }

        // Center the selected item in the wheel
        LaunchedEffect(value) {
            scrollState.scrollToItem(value - minValue)
        }
    }
}

@Composable
fun WheelPickerScreen() {
    var selectedValue by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
//            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WheelPicker(
            minValue = 1,
            maxValue = 30,
            initialValue = selectedValue,
            onValueChange = {
                selectedValue = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Value: $selectedValue", fontSize = 20.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun WheelPickerPreview() {
    WheelPickerScreen()
}
