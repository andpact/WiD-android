package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.WiD
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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

    val lazyListState = rememberLazyListState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    ) {
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth(),
            value = searchText,
            onValueChange = { newText ->
                searchText = newText

                wiDList = wiDService.readWiDListByDetail(newText)
            },
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
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Icon(modifier = Modifier
                    .scale(0.8f),
                    painter = painterResource(id = R.drawable.outline_textsms_24),
                    contentDescription = "detail",
                    tint = Color.Gray
                )

                Text(text = "설명으로 WiD를 검색해 보세요.",
                    style = TextStyle(color = Color.Gray)
                )
            }
        } else {
            var currentDate: LocalDate? = null

            LazyColumn(modifier = Modifier
                .fillMaxWidth(),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                itemsIndexed(wiDList) { index, wiD ->
                    if (currentDate != wiD.date) {
                        currentDate = wiD.date

                        if (currentDate == LocalDate.now()) {
                            Text(text = "오늘",
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            )
                        } else if (currentDate == LocalDate.now().minusDays(1)) {
                            Text(text = "어제",
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
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

                            Text(text = dateText,
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Surface(modifier = Modifier
                        .fillMaxWidth()
                        .then(if (index == wiDList.size - 1) {
                            Modifier.padding(PaddingValues(bottom = 16.dp))
                        } else {
                            Modifier
                        }),
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(modifier = Modifier
                                        .scale(0.8f),
                                        painter = painterResource(id = R.drawable.outline_subtitles_24),
                                        contentDescription = "title")

                                    Text(text = "제목",
                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                    )

                                    Text(text = titleMap[wiD.title] ?: wiD.title)

                                    Box(modifier = Modifier
                                        .clip(CircleShape)
                                        .size(10.dp)
                                        .background(color = colorResource(id = colorMap[wiD.title] ?: R.color.light_gray))
                                    )
                                }

                                Row(modifier = Modifier
                                    .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(modifier = Modifier
                                        .scale(0.8f),
                                        painter = painterResource(id = R.drawable.outline_hourglass_empty_24),
                                        contentDescription = "duration")

                                    Text(text = "소요",
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(modifier = Modifier
                                        .scale(0.8f),
                                        painter = painterResource(id = R.drawable.outline_play_arrow_24),
                                        contentDescription = "finish")

                                    Text(text = "시작",
                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                    )

                                    Text(text = wiD.start.format(DateTimeFormatter.ofPattern("a h:mm")))
                                }

                                Row(modifier = Modifier
                                    .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(modifier = Modifier
                                        .scale(0.8f),
                                        painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                        contentDescription = "finish")

                                    Text(text = "종료",
                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                    )

                                    Text(text = wiD.finish.format(DateTimeFormatter.ofPattern("a h:mm")))
                                }
                            }

                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(modifier = Modifier
                                    .scale(0.8f),
                                    painter = painterResource(id = R.drawable.outline_message_24),
                                    contentDescription = "detail")

                                Text(text = "설명",
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                Text(text = wiD.detail.ifBlank { "설명 입력.." },
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
//fun WiDSearchFragmentPreview() {
//    WiDSearchFragment()
//}