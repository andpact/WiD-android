package andpact.project.wid.tmp

//import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.ui.theme.pretendardRegular
import andpact.project.wid.util.getDateString
//import andpact.project.wid.util.searchFilterMap
//import andpact.project.wid.viewModel.SearchViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

/**
 * 컴포저블이 생성될 때 초기화되어야 하는 변수는 컴포저블 안에 작성하고,
 * 그 외 유지되어야 할 변수는 뷰 모델에 작성함.
 */
//@Composable
//fun SearchDiaryView(
//    onSearchFilterChanged: String,
//    onDiaryClicked: (List<WiD>, Diary) -> Unit
//) {
//    val TAG = "SearchDiaryView"
//
//    val searchViewModel: SearchViewModel = viewModel()
//
//    // 검색
//    val searchText = searchViewModel.searchText.value
//    val searchComplete = searchViewModel.searchComplete.value
//    val searchFilter = searchViewModel.searchFilter.value
//
//    // WiD
//    val wiDMap = searchViewModel.wiDMap.value
//
//    // 다이어리
//    val diaryDateList = searchViewModel.diaryDateList.value
//    val diaryMap = searchViewModel.diaryMap.value
//
//    // 키보드
//    val focusRequester = remember { FocusRequester() }
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
//        onDispose {
//            Log.d(TAG, "disposed")
//        }
//    }
//
//    LaunchedEffect(onSearchFilterChanged) {
//        searchViewModel.setSearchFilter(onSearchFilterChanged)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.secondary)
//    ) {
//        /**
//         * 상단 바
//         */
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//                .fillMaxWidth()
//                .height(56.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            BasicTextField(
//                modifier = Modifier
//                    .weight(1f)
//                    .focusRequester(focusRequester = focusRequester),
//                value = searchText,
//                textStyle = TextStyle(
//                    fontFamily = pretendardRegular,
//                    fontSize = 18.sp,
//                    color = MaterialTheme.colorScheme.primary
//                ),
//                onValueChange = { newText ->
//                    searchViewModel.setSearchText(newText)
//                },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Search
//                ),
//                keyboardActions = KeyboardActions(
//                    onSearch = {
//                        searchViewModel.fetchDiaryDates()
//                    }
//                ),
//                decorationBox = { innerTextField ->
//                    Row(
//                        modifier = Modifier
//                            .height(40.dp)
//                            .border(
//                                width = 0.5.dp,
//                                color = MaterialTheme.colorScheme.primary,
//                                shape = RoundedCornerShape(80.dp)
//                            )
//                            .padding(horizontal = 16.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                        ) {
//                            if (searchText.isBlank()) {
//                                Text(
//                                    text = "${searchFilterMap[searchFilter]}으로 검색..",
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            innerTextField()
//                        }
//                    }
//                }
//            )
//
//            Icon(
//                modifier = Modifier
//                    .size(24.dp)
//                    .clickable(
//                        enabled = searchText.isNotBlank(),
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        searchViewModel.setSearchComplete(isComplete = true)
//
//                        searchViewModel.fetchDiaryDates()
//                    },
//                imageVector = Icons.Default.Search,
//                contentDescription = "검색"
//            )
//        }
//
//        /**
//         * 컨텐츠
//         */
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            if (searchComplete) { // 검색 후
//                if (diaryDateList.isEmpty()) {
//                    item {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "검색 결과가 없습니다.",
//                                style = Typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                } else {
//                    items(diaryDateList.size) { index: Int ->
//                        val itemDate = diaryDateList[index]
//                        val diary = diaryMap[itemDate] ?: Diary(id = -1, date = itemDate, title = "", content = "")
//                        val wiDList = wiDMap[itemDate] ?: emptyList()
//
//                        Text(
//                            text = getDateString(diary.date),
//                            style = Typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.primary,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis,
//                        )
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .border(
//                                    width = 0.5.dp,
//                                    color = MaterialTheme.colorScheme.primary,
//                                    shape = RoundedCornerShape(8.dp)
//                                )
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    onDiaryClicked(wiDList, diary)
//                                },
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .padding(8.dp)
//                                    .size(60.dp),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                SearchPieChartFragment(wiDList = wiDList)
//                            }
//
//                            Column(
//                                modifier = Modifier
//                                    .weight(1f),
//                                verticalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Text(
//                                    text = diary.title,
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                )
//
//                                Text(
//                                    text = diary.content,
//                                    style = Typography.labelMedium,
//                                    color = MaterialTheme.colorScheme.primary,
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis,
//                                )
//                            }
//
//                            Icon(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .size(24.dp),
//                                imageVector = Icons.Default.KeyboardArrowRight,
//                                contentDescription = "이 다이어리로 전환하기",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            } else { // 검색 전
//                item {
//                    Box(
//                        modifier = Modifier
//                            .padding(vertical = 48.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "과거의 다이어리를 통해\n당신의 성장과 여정을\n다시 살펴보세요.",
//                                style = Typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.primary,
//                                textAlign = TextAlign.Center,
//                                lineHeight = 30.sp
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}