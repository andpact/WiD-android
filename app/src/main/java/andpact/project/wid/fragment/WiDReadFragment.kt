package andpact.project.wid.fragment

import andpact.project.wid.service.WiDService
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WiDReadFragment() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    val wiDService = WiDService(context = LocalContext.current)

    // Fetch the list of WiDs based on the selected date
    val wiDList = remember(currentDate) {
        wiDService.readWiDListByDate(currentDate)
    }

    Log.d("wiDList : ", wiDList.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
            .wrapContentSize(Alignment.TopCenter),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDate.format(DateTimeFormatter.ofPattern("MM.dd (E)")),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Button(
                onClick = {
                    currentDate = LocalDate.now()
                },
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "previousDay")
            }

            Button(
                onClick = {
                    currentDate = currentDate.minusDays(1)
                },
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "previousDay")
            }

            Button(
                onClick = {
                    currentDate = currentDate.plusDays(1)
                },
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "nextDay")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "순서",
                modifier = Modifier.weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
            Text(text = "제목",
                modifier = Modifier.weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
            Text(text = "시작",
                modifier = Modifier.weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
            Text(text = "종료",
                modifier = Modifier.weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
            Text(text = "소요",
                modifier = Modifier.weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
            Text(text = "설명",
                modifier = Modifier.weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(wiDList) { wiD ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = wiD.id.toString(),
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = wiD.title,
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = wiD.start.toString(),
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = wiD.finish.toString(),
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = formatDuration(wiD.duration),
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDReadFragmentPreview() {
    WiDReadFragment()
}