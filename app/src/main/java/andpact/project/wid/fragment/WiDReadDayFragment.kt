package andpact.project.wid.fragment

import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WiDReadDayFragment(navController: NavController) {
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
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
        ) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 25.dp)
                    .background(Color.Transparent)
            )

            Text(text = "순서",
                modifier = Modifier
                    .weight(0.5f)
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
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(wiDList) { index, wiD ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val titleColorId = colorMap[wiD.title]
                    if (titleColorId != null) {
                        val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                        Box(
                            modifier = Modifier
                                .size(width = 10.dp, height = 50.dp)
                                .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Destinations.WiDViewFragment.route + "/${wiD.id}")
//                            Log.d("Navigation", Destinations.WiDViewFragment.route + "/${wiD.id}")
                            },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(), // Adjust this modifier as needed
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                modifier = Modifier.weight(0.5f)
                                    .border(1.dp, Color.Black),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = titleMap[wiD.title] ?: wiD.title,
                                modifier = Modifier.weight(1f)
                                    .border(1.dp, Color.Black),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = wiD.start.format(DateTimeFormatter.ofPattern("HH:mm")),
                                modifier = Modifier.weight(1f)
                                    .border(1.dp, Color.Black),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = wiD.finish.format(DateTimeFormatter.ofPattern("HH:mm")),
                                modifier = Modifier.weight(1f)
                                    .border(1.dp, Color.Black),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = formatDuration(wiD.duration, mode = 1),
                                modifier = Modifier.weight(1f)
                                    .border(1.dp, Color.Black),
                                textAlign = TextAlign.Center
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "설명 : ",
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = wiD.detail.ifBlank { "설명 입력.." },
                                modifier = Modifier.weight(1f),
                                style = TextStyle(color = if (wiD.detail.isBlank()) Color.Gray else Color.Black, textAlign = TextAlign.Justify),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WiDReadDayFragmentPreview() {
//    WiDReadDayFragment()
//}