package andpact.project.wid.view

import andpact.project.wid.model.City
import andpact.project.wid.model.PreviousView
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.WiDViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDView(
    statusBarHeight: Dp,
    navigationBarHeight: Dp,
    onBackButtonPressed: () -> Unit,
    onTitlePickerClicked: (previousView: PreviousView) -> Unit,
    onDateTimePickerClicked: (previousView: PreviousView) -> Unit,
    onCityPickerClicked: (currentCity: City) -> Unit,
    wiDViewModel: WiDViewModel = hiltViewModel()
) {
    val TAG = "WiDView"

    val user = wiDViewModel.user.value
    val wiDMinLimit = user?.wiDMinLimit ?: Duration.ofMinutes(5)
    val wiDMaxLimit = user?.wiDMaxLimit ?: Duration.ofHours(12)

    // WiD
    val clickedWiD = wiDViewModel.clickedWiD.value
    val clickedWiDCopy = wiDViewModel.clickedWiDCopy.value
    val updateClickedWiDFinishToNow = wiDViewModel.updateClickedWiDFinishToNow.value
    val updateClickedWiDCopyFinishToMaxFinish = wiDViewModel.updateClickedWiDCopyFinishToMaxFinish.value
    val isNewWiD = wiDViewModel.isNewWiD.value
    val isLastNewWiD = wiDViewModel.isLastNewWiD.value

    val titleExist = wiDViewModel.titleExist.value
    val titleModified = wiDViewModel.titleModified.value
    val subTitleModified = wiDViewModel.subTitleModified.value

    val startModified = wiDViewModel.startModified.value
    val finishModified = wiDViewModel.finishModified.value
    val cityModified = wiDViewModel.cityModified.value

    val duration = Duration.between(clickedWiDCopy.start, clickedWiDCopy.finish)
    val durationEnabled = duration in wiDMinLimit..wiDMaxLimit

    val showDeleteWiDDialog = wiDViewModel.showDeleteWiDDialog.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    // TODO: 백 핸들러 작성

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
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
                                if (isLastNewWiD) { // 나갈 때도 확인
                                    wiDViewModel.setUpdateClickedWiDFinishToNow(update = false)
                                    wiDViewModel.setUpdateClickedWiDCopyFinishToNow(update = false)
                                }

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
                            text = if (isNewWiD) "새로운 기록" else "기록",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
            }
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    // TODO: 초기화 버튼 삭제? 
//                    FilledIconButton(
//                        modifier = Modifier
//                            .size(56.dp),
//                        onClick = {
//                            wiDViewModel.setClickedWiDCopy(newClickedWiDCopy = clickedWiD) // 제목, 시작, 종료 초기화
//                            wiDViewModel.setUpdateClickedWiDCopyFinishToNow(update = isLastNewWiD)
//                        },
//                        enabled = clickedWiD != clickedWiDCopy,
//                        shape = RectangleShape,
//                        colors = IconButtonDefaults.filledIconButtonColors(
//                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
//                        )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Refresh,
//                            contentDescription = "초기화",
//                        )
//                    }

                    if (!isNewWiD) {
                        FilledTonalButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            onClick = {
                                wiDViewModel.setShowDeleteWiDDialog(show = true)
                            },
                            shape = RectangleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text(text = "삭제")
                        }
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        onClick = {
                            if (isNewWiD) {
                                wiDViewModel.createWiD()
                            } else {
                                wiDViewModel.updateWiD()
                            }
                            onBackButtonPressed()
                        },
                        enabled = if (isNewWiD) { titleExist && durationEnabled } else { (titleModified || subTitleModified || startModified || finishModified || cityModified) && durationEnabled },
                        shape = RectangleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(text = if (isNewWiD) { "새로운 기록 생성" } else { "수정 완료" })
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(navigationBarHeight)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        },
        content = { contentPadding: PaddingValues ->
            // TODO: 레이지 컬럼으로 바꾸기.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onTitlePickerClicked(PreviousView.CLICKED_WID_TITLE) // 이미 데이터 소스의 클릭된 위드는 갱신된 상태
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "제목",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = clickedWiDCopy.title.kr,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (titleModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Text(
                                text = clickedWiD.title.kr,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "제목 수정 뷰로 이동"
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onTitlePickerClicked(PreviousView.CLICKED_WID_SUB_TITLE) // 이미 데이터 소스의 클릭된 위드는 갱신된 상태
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "부제목",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = clickedWiDCopy.subTitle.kr,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (subTitleModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Text(
                                text = clickedWiD.subTitle.kr,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "제목 수정 뷰로 이동"
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onDateTimePickerClicked(PreviousView.CLICKED_WID_START)
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "시작",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = wiDViewModel.getDateTimeString(dateTime = clickedWiDCopy.start),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (startModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = wiDViewModel.getDateTimeString(dateTime = clickedWiD.start),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "시작 선택 뷰로 이동"
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onDateTimePickerClicked(PreviousView.CLICKED_WID_FINISH)
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "종료",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = wiDViewModel.getDateTimeString(dateTime = clickedWiDCopy.finish),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (updateClickedWiDCopyFinishToMaxFinish) {
                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = 4.dp),
                                    text = "Now",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    if (finishModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = wiDViewModel.getDateTimeString(dateTime = clickedWiD.finish),
                                    style = MaterialTheme.typography.bodyMedium,
                                )

                                if (updateClickedWiDFinishToNow) {
                                    Text(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.errorContainer,
                                                shape = RoundedCornerShape(16)
                                            )
                                            .padding(horizontal = 4.dp),
                                        text = "Now",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "종료 선택 뷰로 이동",
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "소요",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = wiDViewModel.getDurationString(clickedWiDCopy.duration),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (startModified || finishModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = wiDViewModel.getDurationString(clickedWiD.duration),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .alpha(0f),
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "도구",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = clickedWiDCopy.tool.kr,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "경험치",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "${clickedWiD.exp}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .clickable {
                            onCityPickerClicked(clickedWiDCopy.city)
                        }
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "위치",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = clickedWiDCopy.city.kr + ", " + clickedWiDCopy.city.country.kr,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (cityModified) {
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = "변경 전",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = clickedWiD.city.kr + ", " + clickedWiD.city.country.kr,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "도시 선택 뷰로 이동"
                        )
                    }
                }
            }

            if (showDeleteWiDDialog) {
                DatePickerDialog(
                    shape = MaterialTheme.shapes.medium,
                    colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    onDismissRequest = {
                        wiDViewModel.setShowDeleteWiDDialog(false)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                wiDViewModel.deleteWiD()
                                wiDViewModel.setShowDeleteWiDDialog(false)
                                onBackButtonPressed()
                            }
                        ) {
                            Text(text = "삭제")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                wiDViewModel.setShowDeleteWiDDialog(false)
                            }
                        ) {
                            Text(text = "취소")
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = "기록을 삭제하시겠습니까?"
                        )
                    }
                }
            }
        }
    )
}