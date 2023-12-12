package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.util.getDayStringWith2Lines
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SearchFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 검색
    var searchText by remember { mutableStateOf("") }
    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    // 다이어리
    val diaryService = DiaryService(LocalContext.current)
    val diaryList = remember(searchText) { diaryService.getDiaryListByTitleOrContent(searchText = searchText) }

    // 키보드
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // 전체화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 검색창
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester = focusRequester),
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
                            .height(50.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")

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
                        .fillMaxWidth()
                        .padding(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_sms_16),
                        contentDescription = "No diary",
                        tint = Color.Gray
                    )

                    Text(
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
                                .fillMaxWidth()
                                .aspectRatio(1f / 2f) // 가로 1, 세로 2 비율
                                .clickable {
                                    navController.navigate(Destinations.DiaryFragmentDestination.route + "/${diary.date}")

                                    mainTopBottomBarVisible.value = false
                                },
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(1.dp),
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
                                    painter = painterResource(id = R.drawable.baseline_edit_16),
                                    contentDescription = "Edit diary",
                                    tint = colorResource(id = R.color.deep_sky_blue)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SearchFragmentPreview() {
//    SearchFragment()
//}