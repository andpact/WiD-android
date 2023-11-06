package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WiDSearchFragment(navController: NavController, buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)

    var searchText by remember { mutableStateOf("") }
    var wiDList by remember { mutableStateOf(emptyList<WiD>()) }

    val sortedWiDList = wiDList.sortedWith( // 날짜를 내림 차순, 동일한 날짜 안에서 시간은 오름 차순
        compareByDescending<WiD> { it.date }
            .thenBy { it.start }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                // Call the method to update the list based on the searchText
                wiDList = wiDService.readWiDListByDetail(newText)
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(text = "검색..")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search")
            },
            singleLine = true
        )

        if (searchText.isEmpty()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(modifier = Modifier
                    .scale(0.8f),
                    painter = painterResource(id = R.drawable.baseline_message_24),
                    contentDescription = "detail",
                    tint = Color.Gray
                )

                Text(
                    text = "설명으로 WiD를 검색해 보세요.",
                    style = TextStyle(color = Color.Gray)
                )
            }
        } else {
            var currentDate: LocalDate? = null

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(sortedWiDList) { index, wiD ->
                    if (currentDate != wiD.date) {
                        currentDate = wiD.date

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            if (currentDate == LocalDate.now()) {
                                Text(
                                    text = "오늘",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            } else if (currentDate == LocalDate.now().minusDays(1)) {
                                Text(
                                    text = "어제",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            } else {
                                val dateText = buildAnnotatedString {
                                    wiD.date.let {
                                        append(it.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 ")))
                                        append("(")
                                        withStyle(
                                            style = SpanStyle(
                                                color = when (it.dayOfWeek) {
                                                    DayOfWeek.SATURDAY -> Color.Blue
                                                    DayOfWeek.SUNDAY -> Color.Red
                                                    else -> Color.Unspecified
                                                }
                                            )
                                        ) {
                                            append(it.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
                                        }
                                        append(")")
                                    }
                                }

                                Text(
                                    text = dateText,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

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
                                Icon(modifier = Modifier
                                    .scale(0.8f),
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
                                Icon(modifier = Modifier
                                    .scale(0.8f),
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

//@Preview(showBackground = true)
//@Composable
//fun WiDSearchFragmentPreview() {
//    WiDSearchFragment()
//}