package andpact.project.wid.view

import andpact.project.wid.model.PreviousView
import andpact.project.wid.model.SubTitle
import andpact.project.wid.ui.theme.Transparent
import andpact.project.wid.viewModel.TitlePickerViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitlePickerView(
    previousView: PreviousView,
    onBackButtonPressed: () -> Unit,
    titlePickerViewModel: TitlePickerViewModel = hiltViewModel()
) {
    val TAG = "TitlePickerView"

    val titleArray = titlePickerViewModel.titleArray
    val selectedTitle = titlePickerViewModel.selectedTitle.value
    val filteredSubTitleArray = titlePickerViewModel.sutTitleArray.filter { it.title == selectedTitle }

    val searchedSubTitleList = titlePickerViewModel.searchedSubTitleList.value
    val isSearchMode = titlePickerViewModel.isSearchMode.value
    val searchText = titlePickerViewModel.searchText.value

    val clickedWiDCopy = titlePickerViewModel.clickedWiDCopy.value

    val firstCurrentWiD = titlePickerViewModel.firstCurrentWiD.value
    val secondCurrentWiD = titlePickerViewModel.secondCurrentWiD.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) { // 최초 제목 초기화
            titlePickerViewModel.setSelectedTitle(newSelectedTitle = firstCurrentWiD.title)
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
//                    if (isSearchMode) {
//                        OutlinedTextField(
//                            value = searchText,
//                            onValueChange = {
//                                titlePickerViewModel.updateSearchText(newSearchText = it)
//                            },
//                            colors = TextFieldDefaults.colors(
//                                unfocusedContainerColor = Transparent,
//                                focusedContainerColor = Transparent,
//                                unfocusedIndicatorColor = Transparent,
//                                focusedIndicatorColor = Transparent
//                            ),
//                            placeholder = {
//                                Text(text = "검색어를 입력하세요")
//                            }
//                        )
//                    } else {
//                        Text(text = "${previousView.kr} > 제목 선택")
//                    }

                    AnimatedContent(
                        targetState = isSearchMode,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut() // 페이드 인/아웃 애니메이션 적용
                        }
                    ) { targetState: Boolean ->
                        if (targetState) { // 검색 모드
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { newSearchText: String ->
                                    titlePickerViewModel.updateSearchText(newSearchText = newSearchText)
                                },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Transparent,
                                    focusedContainerColor = Transparent,
                                    unfocusedIndicatorColor = Transparent,
                                    focusedIndicatorColor = Transparent
                                ),
                                placeholder = {
                                    Text(text = "검색어를 입력하세요")
                                }
                            )
                        } else { // 일반 텍스트 모드
                            Text(text = "${previousView.kr} > 제목 선택")
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isSearchMode) {
                                titlePickerViewModel.setIsSearchMode(set = false)
                            } else {
                                titlePickerViewModel.setIsSearchMode(set = true)
                            }
                        }
                    ) {
                        if (isSearchMode) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "리스트"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "검색"
                            )
                        }
                    }
                }
            )
        }
    ) { contentPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            if (isSearchMode) { // TODO: 검색 모드 일 때
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
//                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(searchedSubTitleList.size) { index: Int ->
                        val itemSubTitle = searchedSubTitleList[index]

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
                            supportingContent = {
                                Text(text = itemSubTitle.title.kr)
                            },
                            trailingContent = {
                                val isSelected = if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
                                    firstCurrentWiD.subTitle == itemSubTitle
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
            } else { // TODO: 검색 모드 아닐 때
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(titleArray.size) { index: Int ->
                        val itemTitle = titleArray[index]

                        FilterChip(
                            selected = selectedTitle == itemTitle,
                            onClick = {
                                onBackButtonPressed()

                                titlePickerViewModel.setSelectedTitle(newSelectedTitle = itemTitle)
                            },
                            label = {
                                Text(text = itemTitle.kr)
                            }
                        )
                    }
                }

                LazyColumn(
                    content = {
                        items(filteredSubTitleArray.size) { index: Int ->
                            val itemSubTitle = filteredSubTitleArray[index]

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
//                                leadingContent = {
//                                    Image(
//                                        painter = painterResource(id = subTitle.title.smallImage),
//                                        contentDescription = subTitle.kr,
//                                        modifier = Modifier
//                                            .clip(MaterialTheme.shapes.medium)
//                                            .size(56.dp)
//                                    )
//                                },
                                headlineContent = {
                                    Text(text = itemSubTitle.kr)
                                },
                                trailingContent = {
                                    val isSelected = if (previousView == PreviousView.STOPWATCH || previousView == PreviousView.TIMER) {
                                        firstCurrentWiD.subTitle == itemSubTitle
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
                )
            }
        }
    }
}