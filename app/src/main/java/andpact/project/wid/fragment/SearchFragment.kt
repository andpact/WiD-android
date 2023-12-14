package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.util.createEmptyView
import andpact.project.wid.util.getDayString
import andpact.project.wid.util.getDayStringWith4Lines
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }

                    HorizontalDivider()
                }
            }
        )

        // 겸색 결과
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // item 간에 8.Dp의 공간이 설정됨.
        ) {
            if (diaryList.isEmpty()) {
                item {
                    createEmptyView(text = "검색으로 다이어리를 찾아보세요.")()
                }
            } else {
                itemsIndexed(diaryList) { index: Int, diary: Diary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = getDayString(diary.date),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(Destinations.DiaryFragmentDestination.route + "/${diary.date}")

                                        mainTopBottomBarVisible.value = false
                                    },
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colorResource(id = R.color.light_gray))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = diary.title,
                                        style = TextStyle(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Navigate to this diary",
                                    )
                                }

                                HorizontalDivider()

                                Text(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    text = diary.content,
                                    minLines = 5,
                                    maxLines = 10,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
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

//@Preview(showBackground = true)
//@Composable
//fun SearchFragmentPreview() {
//    SearchFragment()
//}