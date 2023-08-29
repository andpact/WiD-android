package andpact.project.wid.fragment

import andpact.project.wid.service.WiDService
import andpact.project.wid.util.DataMapsUtil
import andpact.project.wid.util.PieChartView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WiDReadDayFragment() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    val wiDService = WiDService(context = LocalContext.current)

    val wiDList = remember(currentDate) {
        wiDService.readWiDListByDate(currentDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd (E)")),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = {
                    currentDate = LocalDate.now()
                },
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Today")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.minusDays(1)
                },
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "prevDay")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.plusDays(1)
                },
                enabled = currentDate != LocalDate.now(),
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "nextDay")
            }
        }

        // 파이 차트 표시
        PieChartView(date = currentDate, forReadDay = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 20.dp)
                    .background(Color.Black)
            )

            Text(text = "순서",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center,
            )

            Text(text = "제목",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)

            Text(text = "시작",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)

            Text(text = "종료",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)

            Text(text = "소요",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)

            Text(text = "설명",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black),
                textAlign = TextAlign.Center)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(wiDList) { index, wiD ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                ) {
                    val titleColorId = DataMapsUtil.colorMap[wiD.title]
                    if (titleColorId != null) {
                        val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                        Box(
                            modifier = Modifier
                                .size(width = 10.dp, height = 20.dp)
                                .background(backgroundColor)
                        )
                    }


                    Text(
                        text = (index + 1).toString(),
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = DataMapsUtil.titleMap[wiD.title] ?: wiD.title,
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = wiD.start.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Black),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = wiD.finish.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
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
                    Text(
                        text = wiD.detail.length.toString(),
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
fun WiDReadDayFragmentPreview() {
    WiDReadDayFragment()
}