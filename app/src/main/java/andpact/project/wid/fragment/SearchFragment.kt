package andpact.project.wid.fragment

import andpact.project.wid.activity.MainActivityDestinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.pretendardRegular
import andpact.project.wid.util.getDateString
import andpact.project.wid.util.getEmptyView
import andpact.project.wid.util.getNoBackgroundEmptyViewWithMultipleLines
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SearchFragment(mainActivityNavController: NavController) {

    DisposableEffect(Unit) {
        Log.d("SearchFragment", "SearchFragment is being composed")

        onDispose {
            Log.d("SearchFragment", "SearchFragment is being disposed")
        }
    }

    // 검색
    var searchText by remember { mutableStateOf("") }
//    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemScrollOffset = Int.MAX_VALUE)
    var searchComplete by remember { mutableStateOf(false) }

    // WiD
    val wiDService = WiDService(context = LocalContext.current)

    // 다이어리
    val diaryService = DiaryService(LocalContext.current)
    var diaryList by remember { mutableStateOf(emptyList<Diary>()) }

    // 키보드
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus() // 화면 전환하면 키보드 사라지도록.
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester = focusRequester),
                value = searchText,
                textStyle = TextStyle(
                    fontFamily = pretendardRegular,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                onValueChange = { newText ->
                    searchText = newText
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        diaryList = diaryService.readDiaryListByTitleOrContent(searchText = searchText)
                    }
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(80.dp)
                            )
//                            .shadow(
//                                elevation = 2.dp,
//                                shape = RoundedCornerShape(800.dp),
//                                spotColor = MaterialTheme.colorScheme.primary,
//                            )
//                            .background(MaterialTheme.colorScheme.secondary)
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
                                    style = Typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            innerTextField()
                        }
                    }
                }
            )

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(searchText.isNotBlank()) {
                        searchComplete = true

                        diaryList = diaryService.readDiaryListByTitleOrContent(searchText = searchText)
                    },
                imageVector = Icons.Default.Search,
                contentDescription = "검색"
            )
        }

        /**
         * 컨텐츠
         */
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (diaryList.isEmpty()) {
                item {
                    if (searchComplete) {
                        getEmptyView(text = "검색 결과가 없습니다.")()
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 48.dp)
                        ) {
                            getNoBackgroundEmptyViewWithMultipleLines(text = "과거의 다이어리를 통해\n당신의 성장과 여정을\n다시 살펴보세요.")()
                        }
                    }
                }
            } else {
//                items(diaryList.size) { diary ->
                itemsIndexed(diaryList) { index, diary ->
                    Text(
                        text = getDateString(diary.date),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
//                            .padding(
//                                start = 16.dp,
//                                end = 16.dp,
//                                top = if (index == 0) 16.dp else 0.dp, // 첫 번째 다이어리 위쪽에 16dp 패딩
//                                bottom = if (index == diaryList.size - 1) 16.dp else 0.dp // 마지막 다이어리 아래쪽에 16dp 패딩
//                            )
//                            .shadow(
//                                elevation = 2.dp,
//                                shape = RoundedCornerShape(8.dp),
//                                spotColor = MaterialTheme.colorScheme.primary,
//                            )
//                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable {
                                mainActivityNavController.navigate(MainActivityDestinations.DiaryFragmentDestination.route + "/${diary.date}")
                            },
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val wiDList = wiDService.readDailyWiDListByDate(diary.date)

                            PeriodBasedPieChartFragment(date = diary.date, wiDList = wiDList)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = diary.title,
                                style = Typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = diary.content,
                                style = Typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .size(24.dp),
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "이 다이어리로 전환하기",
                            tint = MaterialTheme.colorScheme.primary
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