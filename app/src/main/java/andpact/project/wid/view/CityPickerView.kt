package andpact.project.wid.view

import andpact.project.wid.model.City
import andpact.project.wid.model.Country
import andpact.project.wid.model.PreviousView
import andpact.project.wid.viewModel.CityPickerViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CityPickerView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    previousView: PreviousView, // 기록 또는 유저
    currentCity: City,
    onBackButtonPressed: () -> Unit,
    cityPickerViewModel: CityPickerViewModel = hiltViewModel()
) {
    val TAG = "CityPickerView"

    val countryList = cityPickerViewModel.countryList
    val selectedCountry = cityPickerViewModel.selectedCountry.value
    val filteredCityList = City.filterCitiesByCountry(targetCountry = selectedCountry)

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    val currentWiD = cityPickerViewModel.currentWiD.value

    val user = cityPickerViewModel.user.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

       if (previousView == PreviousView.CLICKED_WID_CITY) { // 기록 도시 초기화
            cityPickerViewModel.setSelectedCountry(newSelectedCountry = currentWiD.city.country)
        } else { // 유저 도시 초기화
            cityPickerViewModel.setSelectedCountry(newSelectedCountry = (user?.city ?: City.SEOUL).country)
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
                    modifier = Modifier
                        .fillMaxWidth(),
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
                        Text(
                            text = "${previousView.kr} > 위치 선택",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
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
                    text = { Text(text = "국가 선택")}
                )

                Tab(
                    selected = currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text(text = "도시 선택")}
                )
            }

            HorizontalPager(
                state = pagerState
            ) { page: Int ->
                when (page) {
                    0 -> { // 국가 선택
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            itemsIndexed(
                                items = countryList,
                                key = { index, _ -> "country-$index" }, // 인덱스를 포함하여 고유 키 생성
                                contentType = { _, _ -> "country-list-item" }
                            ) { _, itemCountry: Country -> // 인덱스를 포함한 매개변수
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                            cityPickerViewModel.setSelectedCountry(newSelectedCountry = itemCountry)
                                        },
                                    headlineContent = {
                                        Text(text = itemCountry.kr)
                                    },
                                    supportingContent = {
                                        Text(text = "${City.getCityCountByCountry(itemCountry)}")
                                    },
                                    trailingContent = {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "도시 선택 이동")
                                        }
                                    }
                                )
                            }
                        }
                    }
                    1 -> { // 도시 선택
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            itemsIndexed(
                                items = filteredCityList,
                                key = { index: Int, _: City -> "city-${index}" }, // 고유 키 설정 (예: 도시 ID 사용)
                                contentType = { _, _ -> "city-list-item" } // 고정된 컨텐츠 타입
                            ) { _, itemCity: City ->
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            if (previousView == PreviousView.CLICKED_WID_CITY) { // 기록의 도시 변경
                                                cityPickerViewModel.setClickedWiDCopyCity(updatedCity = itemCity)
                                            } else { // 유저의 도시 변경
                                                cityPickerViewModel.setUserCity(updatedCity = itemCity)
                                            }

                                            onBackButtonPressed()
                                        },
                                    headlineContent = {
                                        Text(text = itemCity.kr)
                                    },
                                    trailingContent = {
                                        RadioButton(
                                            selected = currentCity == itemCity,
                                            onClick = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}