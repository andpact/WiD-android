package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.Destinations
import andpact.project.wid.model.Diary
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.White
import andpact.project.wid.ui.theme.pretendardRegular
import andpact.project.wid.ui.theme.pretendardSemiBold
import andpact.project.wid.util.createEmptyView
import andpact.project.wid.util.createNoBackgroundEmptyViewWithMultipleLines
import andpact.project.wid.util.getDayString
import andpact.project.wid.util.getDayStringWith3Lines
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SearchFragment(navController: NavController) {
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
            Icon(
                modifier = Modifier
                    .clickable {
                        navController.popBackStack()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "뒤로 가기",
                tint = MaterialTheme.colorScheme.primary
            )

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
                        // 검색 버튼이 눌렸을 때 수행할 동작
                        diaryList = diaryService.getDiaryListByTitleOrContent(searchText = searchText)
                    }
                ),
                decorationBox = { innerTextField ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                    ) {
                        Row(
                            modifier = Modifier
                                .height(56.dp)
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
                }
            )

            Icon(
                modifier = Modifier
                    .clickable(searchText.isNotBlank()) {
                        diaryList = diaryService.getDiaryListByTitleOrContent(searchText = searchText)
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
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (diaryList.isEmpty()) {
                item {
                    createEmptyView(text = "검색으로 다이어리를 찾아보세요.")()
                }
            } else {
                items(diaryList) { diary ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Destinations.DiaryFragmentDestination.route + "/${diary.date}")
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp),
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
                                text = getDayString(diary.date),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = diary.title,
                                style = Typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = diary.content,
                                style = Typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Icon(
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