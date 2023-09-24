package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WiDReadDayFragment(navController: NavController, buttonsVisible: MutableState<Boolean>) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    val wiDService = WiDService(context = LocalContext.current)

    val wiDList = remember(currentDate) {
        wiDService.readWiDListByDate(currentDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "WiD",
                style = TextStyle(textAlign = TextAlign.Center, fontSize = 22.sp,
                    fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.acme_regular)))
            )

            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentDate.format(DateTimeFormatter.ofPattern("M월 d일 ")),
//                    style = TextStyle(fontSize = 20.sp)
                )

                Text(text = "(",
//                    style = TextStyle(fontSize = 20.sp)
                )

                Text(
                    text = currentDate.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                    color = when (currentDate.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue
                        DayOfWeek.SUNDAY -> Color.Red
                        else -> Color.Black
                    },
//                    style = TextStyle(fontSize = 20.sp)
                )

                Text(text = ")",
//                    style = TextStyle(fontSize = 20.sp)
                )
            }

            IconButton(
                onClick = {
                    currentDate = LocalDate.now()
                },
            ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Today")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.minusDays(1)
                },
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "prevDay")
            }

            IconButton(
                onClick = {
                    currentDate = currentDate.plusDays(1)
                },
                enabled = currentDate != LocalDate.now(),
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "nextDay")
            }
        }

        // 파이 차트 표시
        PieChartView(date = currentDate, forReadDay = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .background(
                    color = colorResource(id = R.color.light_gray),
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 10.dp, height = 25.dp)
            )

            Text(text = "순서",
                modifier = Modifier
                    .weight(0.4f),
                textAlign = TextAlign.Center,
            )

            Text(text = "제목",
                modifier = Modifier
                    .weight(0.4f),
                textAlign = TextAlign.Center)

            Text(text = "시작",
                modifier = Modifier
                    .weight(0.7f),
                textAlign = TextAlign.Center)

            Text(text = "종료",
                modifier = Modifier
                    .weight(0.7f),
                textAlign = TextAlign.Center)

            Text(text = "소요",
                modifier = Modifier
                    .weight(1f),
                textAlign = TextAlign.Center)
        }

        if (wiDList.isEmpty()) {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                text = "표시할 WiD가 없습니다.",
                style = TextStyle(textAlign = TextAlign.Center, color = Color.LightGray)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(wiDList) { index, wiD ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(id = R.color.light_gray),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val titleColorId = colorMap[wiD.title]
                        if (titleColorId != null) {
                            val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                            Box(
                                modifier = Modifier
                                    .size(width = 10.dp, height = 50.dp)
                                    .background(
                                        color = backgroundColor,
                                        shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                                    )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Destinations.WiDViewFragment.route + "/${wiD.id}")

                                    buttonsVisible.value = false
                                },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(), // Adjust this modifier as needed
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    modifier = Modifier
                                        .weight(0.4f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = titleMap[wiD.title] ?: wiD.title,
                                    modifier = Modifier
                                        .weight(0.4f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = wiD.start.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    modifier = Modifier
                                        .weight(0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = wiD.finish.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    modifier = Modifier
                                        .weight(0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = formatDuration(wiD.duration, mode = 2),
                                    modifier = Modifier
                                        .weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(horizontal = 8.dp)
                                    .background(color = Color.Gray)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(modifier = Modifier
                                    .weight(0.4f),
                                    text = "설명",
                                    textAlign = TextAlign.Center,
                                )

                                Text(modifier = Modifier
                                    .weight(2.8f),
                                    text = ": " + wiD.detail.ifBlank { "설명 입력.." },
                                    style = TextStyle(color = if (wiD.detail.isBlank()) Color.Gray else Color.Black),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
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
//    val navController: NavHostController = rememberNavController()
//    WiDReadDayFragment(navController = navController)
//}