package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.util.getDayStringWith2Lines
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WiDSearchFragment(navController: NavController, buttonsVisible: MutableState<Boolean>) {
    // 검색
    var searchText by remember { mutableStateOf("") }
    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    // 다이어리
    val diaryService = DiaryService(LocalContext.current)
    val diaryList = remember(searchText) { diaryService.getDiaryListByTitleOrContent(searchText = searchText) }

    // 전체화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white)),
//            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 검색창
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            shape = CircleShape,
//            value = searchText,
//            onValueChange = { newText ->
//                searchText = newText
//            },
//            placeholder = {
//                Text(text = "제목 또는 내용으로 검색..")
//            },
//            leadingIcon = {
//                Icon(imageVector = Icons.Default.Search, contentDescription = "search")
//            },
//            singleLine = true,
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color.White,
//                unfocusedContainerColor = Color.White,
//                disabledContainerColor = Color.White,
//                errorContainerColor = Color.White
//            )
//        )

        BasicTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
            },
            singleLine = true,
            decorationBox = { innerTextField ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .height(45.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "search")

                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (searchText.isBlank()) {
                                Text(
                                    text = "제목 또는 내용으로 검색..",
                                    style = TextStyle(color = Color.Gray)
                                )
                            }

                            innerTextField()
                        }
                    }

                    HorizontalDivider()
                }
            }
        )

        // 겸색 결과
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
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
                    modifier = Modifier
                        .fillMaxSize(),
                    state = lazyGridState,
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(diaryList) { index: Int, diary: Diary ->
                        Column(
                            modifier = Modifier
                                .padding(bottom = if (diaryList.size - 1 == index) 16.dp else 0.dp) // 마지막 아이템에 아래쪽 패딩을 설정함.
                                .fillMaxWidth()
                                .aspectRatio(0.5f)
                                .clickable {
                                    navController.navigate(Destinations.DiaryFragment.route + "/${diary.date}")

                                    buttonsVisible.value = false
                                },
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = diary.title,
                                        style = TextStyle(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = diary.content,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = getDayStringWith2Lines(diary.date),
                                    style = TextStyle(fontSize = 12.sp),
                                    overflow = TextOverflow.Ellipsis
                                )

                                Icon(
                                    modifier = Modifier
                                        .scale(0.6f),
                                    painter = painterResource(id = R.drawable.baseline_edit_24),
                                    contentDescription = "Edit diary",
                                    tint = colorResource(id = R.color.deep_sky_blue)
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