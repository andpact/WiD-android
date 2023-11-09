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
import androidx.compose.ui.draw.scale
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
//        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // 날짜 표시 및 날짜 변경
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateText = buildAnnotatedString {
                currentDate.let {
                    append(it.format(DateTimeFormatter.ofPattern("M월 d일 (")))
                    withStyle(
                        style = SpanStyle(
                            color = when (it.dayOfWeek) {
                                DayOfWeek.SATURDAY -> Color.Blue
                                DayOfWeek.SUNDAY -> Color.Red
                                else -> Color.Black
                            }
                        )
                    ) {
                        append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                    }
                    append(")")
                }
            }

            Text(modifier = Modifier
                .weight(1f),
                text = dateText,
                textAlign = TextAlign.Center
            )

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

        Column(modifier = Modifier
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column {
                Text(
                    text = "파이 차트",
                    style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Row {
                        Box(modifier = Modifier
                            .weight(2f)
                        ) {
                            DayPieChartView(wiDList = wiDList)
                        }

                        Text(modifier = Modifier
                            .weight(1f),
                            text = "자료"
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "WiD 리스트",
                    style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (wiDList.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 0.dp, 0.dp, 16.dp),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 4.dp
                            ) {
                                Row(modifier = Modifier
                                    .padding(vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(modifier = Modifier
                                        .scale(0.8f),
                                        painter = painterResource(id = R.drawable.outline_textsms_24),
                                        tint = Color.Gray,
                                        contentDescription = "detail")

                                    Text(text = "표시할 데이터가 없습니다.",
                                        style = TextStyle(color = Color.Gray)
                                    )
                                }
                            }
                        }
                    } else {
                        itemsIndexed(wiDList) { index, wiD ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(if (index == wiDList.size - 1) {
                                        Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)
                                    } else {
                                        Modifier
                                    }),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 4.dp
                            ) {
                                Column(
                                    modifier = Modifier
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
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_subtitles_24),
                                                contentDescription = "title")

                                            Text(
                                                text = "제목 ",
                                                style = TextStyle(fontWeight = FontWeight.Bold)
                                            )

                                            Text(text = titleMap[wiD.title] ?: wiD.title)

                                            Box(
                                                modifier = Modifier
                                                    .width(5.dp)
                                                    .height(20.dp)
                                                    .border(
                                                        BorderStroke(1.dp, Color.LightGray),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .background(
                                                        color = colorResource(id = colorMap[wiD.title]!!),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                            )
                                        }

                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
                                                painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                                contentDescription = "duration")

                                            Text(
                                                text = "소요 ",
                                                style = TextStyle(fontWeight = FontWeight.Bold)
                                            )

                                            Text(text = formatDuration(wiD.duration, mode = 2))
                                        }
                                    }

                                    Row(modifier = Modifier
                                        .fillMaxWidth(),
                                    ) {
                                        Row(modifier = Modifier
                                            .weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
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
                                            Icon(modifier = Modifier
                                                .scale(0.8f),
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
                                                Icon(modifier = Modifier
                                                    .scale(0.8f),
                                                    painter = painterResource(id = R.drawable.outline_message_24),
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
