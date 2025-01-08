package andpact.project.wid.view

import andpact.project.wid.model.City
import andpact.project.wid.model.PreviousView
import andpact.project.wid.viewModel.CityPickerViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerView(
    previousView: PreviousView, // 기록 또는 유저
    currentCity: City,
    onBackButtonPressed: () -> Unit,
    cityPickerViewModel: CityPickerViewModel = hiltViewModel()
) {
    val TAG = "CityPickerView"

    val cityArray = cityPickerViewModel.cityArray

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface, // Scaffold 중첩 시 배경색을 자식 뷰도 지정해야함.
        topBar = {
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
                    Text(text = "${previousView.kr} > 도시 선택")
                },
            )
        }
    ) { contentPadding: PaddingValues ->
        LazyColumn(
            modifier =Modifier
                .padding(contentPadding),
            content = {
                items(cityArray.size) { index: Int ->
                    val itemCity = cityArray[index]

                    ListItem(
                        modifier = Modifier
                            .clickable {
                                if (previousView == PreviousView.CLICKED_WID) { // 기록의 도시 변경
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
        )
    }
}