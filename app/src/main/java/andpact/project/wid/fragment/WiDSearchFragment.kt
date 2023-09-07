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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
//            singleLine = true,
            onValueChange = { newText ->
                searchText = newText
                // Call the method to update the list based on the searchText
                wiDList = wiDService.readWiDListByDetail(newText)
            },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(text = "설명으로 검색..")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search")
            },
        )

        if (searchText.isEmpty()) {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                text = "표시할 WiD가 없습니다.",
                style = TextStyle(textAlign = TextAlign.Center, color = Color.LightGray)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                var currentDate: LocalDate? = null

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(sortedWiDList) { _, wiD ->
                        val wiDDate = wiD.date

                        if (currentDate != wiDDate) {
                            currentDate = wiDDate

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                if (currentDate == LocalDate.now()) {
                                    Text(text = "오늘")
                                } else if (currentDate == LocalDate.now().minusDays(1)) {
                                    Text(text = "어제")
                                } else {
                                    Text(
                                        text = wiDDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd ")),
                                    )

                                    Text(text = "(")

                                    Text(
                                        text = wiDDate.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                                        color = when (wiDDate.dayOfWeek) {
                                            DayOfWeek.SATURDAY -> Color.Blue
                                            DayOfWeek.SUNDAY -> Color.Red
                                            else -> Color.Black
                                        }
                                    )

                                    Text(text = ")")
                                }
                            }
                        }

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
//                            Log.d("Navigation", Destinations.WiDViewFragment.route + "/${wiD.id}")

                                        buttonsVisible.value = false
                                    },
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = titleMap[wiD.title] ?: wiD.title,
                                        modifier = Modifier
                                            .weight(0.4f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = wiD.start.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        modifier = Modifier
                                            .weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = wiD.finish.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        modifier = Modifier
                                            .weight(1f),
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
                                        textAlign = TextAlign.Center
                                    )

                                    Text(modifier = Modifier
                                        .weight(3f),
                                        text = ": " + wiD.detail.ifBlank { "설명 입력.." },
                                        style = TextStyle(color = if (wiD.detail.isBlank()) Color.Gray else Color.Black, textAlign = TextAlign.Justify),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(modifier = Modifier
                        .background(Color.DarkGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp),
                        text = "검색된 WiD ${wiDList.size}개",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 12.sp
                    )
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