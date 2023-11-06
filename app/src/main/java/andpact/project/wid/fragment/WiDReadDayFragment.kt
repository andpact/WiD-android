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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
    val wiDList = remember(currentDate) { wiDService.readDailyWiDListByDate(currentDate) }

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
                )

                Text(text = "(")

                Text(
                    text = currentDate.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                    color = when (currentDate.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue
                        DayOfWeek.SUNDAY -> Color.Red
                        else -> Color.Black
                    },
                )

                Text(text = ")")
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

        Text(
            text = "파이 차트",
            style = TextStyle(fontWeight = FontWeight.Bold)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            DayPieChartView(wiDList = wiDList)
        }

        Text(
            text = "WiD 리스트",
            style = TextStyle(fontWeight = FontWeight.Bold)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (wiDList.isEmpty()) {
                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                    ) {
                        Icon(modifier = Modifier.size(16.dp),
                            painter = painterResource(id = R.drawable.baseline_message_24),
                            contentDescription = "detail")

                        Text(text = "표시할 데이터가 없습니다.")
                    }
                }
            } else {
                itemsIndexed(wiDList) { _, wiD ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                border = BorderStroke(1.dp, Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(Destinations.WiDViewFragment.route + "/${wiD.id}")

                                buttonsVisible.value = false
                            },
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                        ) {
                            Row(modifier = Modifier
                                .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(modifier = Modifier.size(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_category_24),
                                    contentDescription = "title")

                                Text(
                                    text = "제목 ",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                Text(text = titleMap[wiD.title] ?: wiD.title)

                                val titleColorId = colorMap[wiD.title]
                                val backgroundColor = if (titleColorId != null) {
                                    Color(ContextCompat.getColor(LocalContext.current, titleColorId))
                                } else {
                                    colorResource(id = R.color.light_gray)
                                }

                                Box(
                                    modifier = Modifier
                                        .width(5.dp)
                                        .height(20.dp)
                                        .border(
                                            BorderStroke(1.dp, Color.LightGray),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            color = backgroundColor,
                                            RoundedCornerShape(8.dp)
                                        )
                                )
                            }

                            Row(modifier = Modifier
                                .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(modifier = Modifier.size(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_timelapse_24),
                                    contentDescription = "duration")

                                Text(
                                    text = "소요 ",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                Text(text = formatDuration(wiD.duration, mode = 1))
                            }
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth(),
                        ) {
                            Row(modifier = Modifier
                                .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(modifier = Modifier.size(16.dp),
                                    painter = painterResource(id = R.drawable.outline_play_arrow_24),
                                    contentDescription = "finish")

                                Text(
                                    text = "시작 ",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                Text(text = wiD.start.format(DateTimeFormatter.ofPattern("a h:mm")))
                            }

                            Row(modifier = Modifier
                                .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(modifier = Modifier.size(16.dp),
                                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                    contentDescription = "finish")

                                Text(
                                    text = "종료 ",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                Text(text = wiD.finish.format(DateTimeFormatter.ofPattern("a h:mm")))
                            }
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val detailText = buildAnnotatedString {
                                wiD.detail.let {
                                    Icon(modifier = Modifier.size(16.dp),
                                        painter = painterResource(id = R.drawable.baseline_message_24),
                                        contentDescription = "detail")

                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold)
                                    ) {
                                        append("설명 ")
                                    }

                                    withStyle(
                                        style = SpanStyle(color = if (it.isBlank()) Color.Gray else Color.Black)
                                    ) {
                                        append(it.ifBlank { "설명 입력.." })
                                    }
                                }
                            }

                            Text(
                                text = detailText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WiDReadDayFragmentPreview() {
    val navController: NavHostController = rememberNavController()
    val buttonsVisible = remember { mutableStateOf(true) }
    WiDReadDayFragment(navController = navController, buttonsVisible)
}
