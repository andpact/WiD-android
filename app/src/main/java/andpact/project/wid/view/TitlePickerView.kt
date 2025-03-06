package andpact.project.wid.view

import andpact.project.wid.model.PreviousView
import andpact.project.wid.model.SubTitle
import andpact.project.wid.model.Title
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.TitlePickerViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TitlePickerView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    previousView: PreviousView,
    onBackButtonPressed: () -> Unit,
    titlePickerViewModel: TitlePickerViewModel = hiltViewModel()
) {
    val TAG = "TitlePickerView"

    val titleList = titlePickerViewModel.titleList
    val selectedTitle = titlePickerViewModel.selectedTitle.value
    val filteredSubTitleList = SubTitle.filterSubTitlesByTitle(targetTitle = selectedTitle)

//    val searchedSubTitleList = titlePickerViewModel.searchedSubTitleList.value
//    val isSearchMode = titlePickerViewModel.isSearchMode.value
//    val searchText = titlePickerViewModel.searchText.value

    val clickedWiDCopy = titlePickerViewModel.clickedWiDCopy.value

    val currentWiD = titlePickerViewModel.currentWiD.value

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = if (previousView == PreviousView.CLICKED_WID_SUB_TITLE) 1 else 0, pageCount = { 2 })

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) { // 최초 제목 초기화
            titlePickerViewModel.setSelectedTitle(newSelectedTitle = currentWiD.title)
        } else {
            titlePickerViewModel.setSelectedTitle(newSelectedTitle = clickedWiDCopy.title)
        }

        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // Scaffold 중첩 시 배경색을 자식 뷰도 지정해야함.
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(statusBarHeight)
                        .background(MaterialTheme.colorScheme.surface)
                )

                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onBackButtonPressed()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로 가기",
                            )
                        }
                    },
                    title = {
//                    AnimatedContent(
//                        targetState = isSearchMode,
//                        transitionSpec = {
//                            fadeIn() togetherWith fadeOut() // 페이드 인/아웃 애니메이션 적용
//                        }
//                    ) { targetState: Boolean ->
//                        if (targetState) { // 검색 모드
//                            OutlinedTextField(
//                                value = searchText,
//                                onValueChange = { newSearchText: String ->
//                                    titlePickerViewModel.updateSearchText(newSearchText = newSearchText)
//                                },
//                                colors = TextFieldDefaults.colors(
//                                    unfocusedContainerColor = Transparent,
//                                    focusedContainerColor = Transparent,
//                                    unfocusedIndicatorColor = Transparent,
//                                    focusedIndicatorColor = Transparent
//                                ),
//                                placeholder = {
//                                    Text(text = "검색어를 입력하세요")
//                                }
//                            )
//                        } else { // 일반 텍스트 모드
//                            Text(text = "${previousView.kr} > 제목 선택")
//                        }
//                    }

                        Text(
                            text = "${previousView.kr} > 제목 선택",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
//                actions = {
//                    IconButton(
//                        onClick = {
//                            if (isSearchMode) {
//                                titlePickerViewModel.setIsSearchMode(set = false)
//                            } else {
//                                titlePickerViewModel.setIsSearchMode(set = true)
//                            }
//                        }
//                    ) {
//                        if (isSearchMode) {
//                            Icon(
//                                imageVector = Icons.Default.List,
//                                contentDescription = "리스트"
//                            )
//                        } else {
//                            Icon(
//                                imageVector = Icons.Default.Search,
//                                contentDescription = "검색"
//                            )
//                        }
//                    }
//                }
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navigationBarHeight)
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            val currentPage = pagerState.currentPage

            TabRow(
                selectedTabIndex = currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[currentPage]),
                        width = tabPositions[currentPage].contentWidth
                    )
                }
            ) {
                Tab(
                    selected = currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text(text = "제목 선택")}
                )

                Tab(
                    selected = currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text(text = "부 제목 선택")}
                )
            }

            HorizontalPager(
                state = pagerState
            ) { page: Int ->
                when (page) {
                    0 -> { // 제목 수정
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            itemsIndexed(
                                items = titleList,
                                key = { index, _ -> "title-$index" }, // 인덱스를 포함하여 고유 키 생성
                                contentType = { _, _ -> "title-list-item" }
                            ) { _, itemTitle: Title -> // 인덱스를 포함한 매개변수
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                            titlePickerViewModel.setSelectedTitle(newSelectedTitle = itemTitle)
                                        },
                                    headlineContent = {
                                        Text(text = itemTitle.kr)
                                    },
                                    supportingContent = {
                                        Text(text = "${SubTitle.getSubTitleCountByTitle(itemTitle)}")
                                    },
                                    trailingContent = {
//                                        val isSelected = if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
//                                            firstCurrentWiD.title == itemTitle
//                                        } else {
//                                            clickedWiDCopy.title == itemTitle
//                                        }
//
//                                        RadioButton(
//                                            selected = isSelected,
//                                            onClick = null
//                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "부 제목 선택 이동")
                                        }
                                    }
                                )
                            }
                        }
                    }
                    1 -> { // 부 제목 수정
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            itemsIndexed(
                                items = filteredSubTitleList,
                                key = { index, _ -> "sub-title-$index" }, // 인덱스를 포함하여 고유 키 생성
                                contentType = { _, _ -> "list-item" }
                            ) { _, itemSubTitle: SubTitle -> // 인덱스를 포함한 매개변수
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            onBackButtonPressed()

                                            if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
                                                titlePickerViewModel.setCurrentWiDTitleAndSubTitle(
                                                    newTitle = itemSubTitle.title,
                                                    newSubTitle = itemSubTitle
                                                )
                                            } else {
                                                titlePickerViewModel.setClickedWiDCopyTitleAndSubTitle(
                                                    newTitle = itemSubTitle.title,
                                                    newSubTitle = itemSubTitle
                                                )
                                            }
                                        },
                                    headlineContent = {
                                        Text(text = itemSubTitle.kr)
                                    },
                                    trailingContent = {
                                        val isSelected = if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
                                            currentWiD.subTitle == itemSubTitle
                                        } else {
                                            clickedWiDCopy.subTitle == itemSubTitle
                                        }

                                        RadioButton(
                                            selected = isSelected,
                                            onClick = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

//            if (isSearchMode) { // 검색 모드 일 때
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentPadding = PaddingValues(vertical = 8.dp),
//                ) {
//                    itemsIndexed(
//                        items = searchedSubTitleList,
//                        key = { index, _ -> "sub-title-$index" }, // 인덱스를 포함한 고유 키 설정
//                        contentType = { _, _ -> "searched-sub-title-list-item" }
//                    ) { _, itemSubTitle: SubTitle ->
//                        ListItem(
//                            modifier = Modifier
//                                .clickable {
//                                    onBackButtonPressed()
//
//                                    if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
//                                        titlePickerViewModel.setCurrentWiDTitleAndSubTitle(
//                                            newTitle = itemSubTitle.title,
//                                            newSubTitle = itemSubTitle
//                                        )
//                                    } else {
//                                        titlePickerViewModel.setClickedWiDCopyTitleAndSubTitle(
//                                            newTitle = itemSubTitle.title,
//                                            newSubTitle = itemSubTitle
//                                        )
//                                    }
//                                },
//                            headlineContent = {
//                                Text(text = itemSubTitle.kr)
//                            },
//                            supportingContent = {
//                                Text(text = itemSubTitle.title.kr)
//                            },
//                            trailingContent = {
//                                val isSelected = if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
//                                    firstCurrentWiD.subTitle == itemSubTitle
//                                } else {
//                                    clickedWiDCopy.subTitle == itemSubTitle
//                                }
//
//                                RadioButton(
//                                    selected = isSelected,
//                                    onClick = null
//                                )
//                            }
//                        )
//                    }
//                }
//            } else { // 검색 모드 아닐 때
//                LazyRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    contentPadding = PaddingValues(horizontal = 16.dp)
//                ) {
//                    itemsIndexed(
//                        items = titleList,
//                        key = { index, _ -> "title-$index" }, // 인덱스를 포함하여 고유 키 생성
//                        contentType = { _, _ -> "filter-chip" }
//                    ) { _, itemTitle: Title -> // 인덱스를 포함한 매개변수
//                        FilterChip(
//                            selected = selectedTitle == itemTitle,
//                            onClick = {
//                                titlePickerViewModel.setSelectedTitle(newSelectedTitle = itemTitle)
//                            },
//                            label = {
//                                Text(text = itemTitle.kr)
//                            }
//                        )
//                    }
//                }
//
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentPadding = PaddingValues(vertical = 8.dp),
//                    content = {
//                        itemsIndexed(
//                            items = filteredSubTitleList,
//                            key = { index, _ -> "sub-title-$index" }, // 인덱스를 포함하여 고유 키 생성
//                            contentType = { _, _ -> "list-item" }
//                        ) { _, itemSubTitle: SubTitle -> // 인덱스를 포함한 매개변수
//                            ListItem(
//                                modifier = Modifier
//                                    .clickable {
//                                        onBackButtonPressed()
//
//                                        if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
//                                            titlePickerViewModel.setCurrentWiDTitleAndSubTitle(
//                                                newTitle = itemSubTitle.title,
//                                                newSubTitle = itemSubTitle
//                                            )
//                                        } else {
//                                            titlePickerViewModel.setClickedWiDCopyTitleAndSubTitle(
//                                                newTitle = itemSubTitle.title,
//                                                newSubTitle = itemSubTitle
//                                            )
//                                        }
//                                    },
//                            //        leadingContent = {
//                            //            Image(
//                            //                painter = painterResource(id = itemSubTitle.title.smallImage),
//                            //                contentDescription = itemSubTitle.kr,
//                            //                modifier = Modifier
//                            //                    .clip(MaterialTheme.shapes.medium)
//                            //                    .size(56.dp)
//                            //            )
//                            //        },
//                                headlineContent = {
//                                    Text(text = itemSubTitle.kr)
//                                },
//                                trailingContent = {
//                                    val isSelected = if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
//                                        firstCurrentWiD.subTitle == itemSubTitle
//                                    } else {
//                                        clickedWiDCopy.subTitle == itemSubTitle
//                                    }
//
//                                    RadioButton(
//                                        selected = isSelected,
//                                        onClick = null
//                                    )
//                                }
//                            )
//                        }
//                    }
//                )
//            }
        }
    }
}