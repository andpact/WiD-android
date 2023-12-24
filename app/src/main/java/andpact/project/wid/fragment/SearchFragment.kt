package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SearchFragment(navController: NavController, mainTopBottomBarVisible: MutableState<Boolean>) {
    // 검색
    var searchText by remember { mutableStateOf("") }
//    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)

    // WiD
    val wiDService = WiDService(context = LocalContext.current)

    // 다이어리
    val diaryService = DiaryService(LocalContext.current)
    var diaryList by remember { mutableStateOf(emptyList<Diary>()) }

    // 키보드
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.ghost_white)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        /**
         * 검색 창
         */
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
                                    style = Typography.labelMedium
                                )
                            }

                            innerTextField()
                        }

                        Icon(
                            modifier = Modifier
                                .clickable(searchText.isNotBlank()) {
                                    diaryList = diaryService.getDiaryListByTitleOrContent(searchText = searchText)
                                },
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색"
                        )
                    }

                    HorizontalDivider()
                }
            }
        )

        /**
         * 검색 결과
         */
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp) // item 간에 8.Dp의 공간이 설정됨.
        ) {
            if (diaryList.isEmpty()) {
                item {
                    createNoBackgroundEmptyView(text = "검색으로 다이어리를 찾아보세요.")()
                }
            } else {
                itemsIndexed(diaryList) { index: Int, diary: Diary ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f / 1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getDayStringWith3Lines(date = diary.date),
                                    style = Typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f / 1f),
                                contentAlignment = Alignment.Center
                            ) {
                                val wiDList = wiDService.readDailyWiDListByDate(diary.date)

                                if (wiDList.isEmpty()) {
                                    createNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                                } else {
                                    DateBasedPieChartFragment(wiDList = wiDList)
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Destinations.DiaryFragmentDestination.route + "/${diary.date}")

                                    mainTopBottomBarVisible.value = false
                                }
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                text = diary.title,
                                style = Typography.bodyMedium,
                                minLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                text = diary.content,
                                style = Typography.labelMedium,
                                minLines = 10,
                                overflow = TextOverflow.Ellipsis,
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
//fun SearchFragmentPreview() {
//    SearchFragment()
//}