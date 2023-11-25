package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.WiD
import andpact.project.wid.service.DiaryService
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
    // 검색
    var searchText by remember { mutableStateOf("") }
//    val lazyListState = rememberLazyListState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    // 다이어리
    val diaryService = DiaryService(LocalContext.current)
    val diaryList = remember(searchText) { diaryService.getDiaryListByContent(content = searchText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
            },
            placeholder = {
                Text(text = "다이어리 검색..")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search")
            },
            singleLine = true
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            if (diaryList.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .scale(0.8f),
                        painter = painterResource(id = R.drawable.outline_textsms_24),
                        contentDescription = "No diary list",
                        tint = Color.Gray
                    )

                    Text(
                        modifier = Modifier
                            .padding(vertical = 32.dp),
                        text = "검색으로 다이어리를 찾아보세요.",
                        style = TextStyle(color = Color.Gray)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 3 columns
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(diaryList.size) { index ->
                        val diary = diaryList[index]

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable {

                                },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "${diary.date}",
                                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                )

                                Text(
                                    text = diary.title,
                                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                )

                                Text(
                                    text = diary.content,
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
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