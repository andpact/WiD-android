package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.ClickedWiDViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickedWiDView(
    onBackButtonPressed: () -> Unit,
    clickedWiDViewModel: ClickedWiDViewModel = hiltViewModel()
) {
    val TAG = "ClickedWiDView"

    val updatedWiD = clickedWiDViewModel.updatedWiD.value

    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    val showTitleMenu = clickedWiDViewModel.showTitleMenu.value
    val titleColorMap = clickedWiDViewModel.titleColorMap

    // 시작 시간
    val showStartPicker = clickedWiDViewModel.showStartPicker.value
    val startTimePickerState = rememberTimePickerState(
        initialHour = updatedWiD.start.hour,
        initialMinute = updatedWiD.start.minute,
        is24Hour = false
    )
    val startOverlap = clickedWiDViewModel.startOverlap.value
    val startModified = clickedWiDViewModel.startModified.value

    // 종료 시간
    val showFinishPicker = clickedWiDViewModel.showFinishPicker.value
    val finishTimePickerState = rememberTimePickerState(
        initialHour = updatedWiD.finish.hour,
        initialMinute = updatedWiD.finish.minute,
        is24Hour = false
    )
    val finishOverlap = clickedWiDViewModel.finishOverlap.value
    val finishModified = clickedWiDViewModel.finishModified.value

    // 소요 시간
    val durationExist = clickedWiDViewModel.durationExist.value

    val showDeleteClickedWiDDialog = clickedWiDViewModel.showDeleteClickedWiDDialog.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        val clickedWiDDate = updatedWiD.date
        clickedWiDViewModel.getWiDListByDate(currentDate = clickedWiDDate)

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
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
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "뒤로 가기",
                        )
                    }
                },
                title = {
                    Text(
                        text = "WiD",
                        style = Typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            clickedWiDViewModel.setShowDeleteClickedWiDDialog(show = true)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.outline_delete_16),
                            contentDescription = "클릭한 위드 삭제",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_calendar_month_24),
                    contentDescription = "날짜",
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "날짜",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = getDateString(date = updatedWiD.date),
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        clickedWiDViewModel.setShowTitleMenu(show = true)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_title_24),
                    contentDescription = "제목",
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "제목",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = titleNumberStringToTitleKRStringMap[updatedWiD.title] ?: "",
                        style = Typography.bodyMedium,
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "제목 수정",
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        clickedWiDViewModel.setShowStartPicker(show = true)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_alarm_24),
                    contentDescription = "시작 시간",
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "시작",
                            style = Typography.titleMedium,
                        )

                        if (startModified && startOverlap) {
                            Icon(
                                modifier = Modifier
                                    .size(12.dp),
                                painter = painterResource(R.drawable.baseline_error_outline_24),
                                contentDescription = "시작 시간 사용 불가",
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "사용 불가",
                                style = Typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (startModified) {
                            Icon(
                                modifier = Modifier
                                    .size(12.dp),
                                painter = painterResource(R.drawable.baseline_check_circle_outline_24),
                                contentDescription = "시작 시간 사용 가능",
                            )

                            Text(
                                text = "사용 가능",
                                style = Typography.labelSmall,
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }

                    Text(
                        text = getTimeString(
                            time = updatedWiD.start,
                            patten = "a hh:mm:ss"
                        ),
                        style = Typography.bodyMedium,
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "시작 시간 수정",
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        clickedWiDViewModel.setShowFinishPicker(show = true)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_alarm_on_24),
                    contentDescription = "종료 시간",
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "종료",
                            style = Typography.titleMedium,
                        )

                        if (finishModified && finishOverlap) {
                            Icon(
                                modifier = Modifier
                                    .size(12.dp),
                                painter = painterResource(R.drawable.baseline_error_outline_24),
                                contentDescription = "종료 시간 사용 불가",
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "사용 불가",
                                style = Typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (finishModified) {
                            Icon(
                                modifier = Modifier
                                    .size(12.dp),
                                painter = painterResource(R.drawable.baseline_check_circle_outline_24),
                                contentDescription = "종료 시간 사용 가능",
                            )

                            Text(
                                text = "사용 가능",
                                style = Typography.labelSmall,
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }

                    Text(
                        text = getTimeString(
                            time = updatedWiD.finish,
                            patten = "a hh:mm:ss"
                        ),
                        style = Typography.bodyMedium,
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "종료 시간 수정",
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_timelapse_24),
                    contentDescription = "소요 시간",
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "소요",
                            style = Typography.titleMedium,
                        )

                        if (!durationExist) {
                            Icon(
                                modifier = Modifier
                                    .size(12.dp),
                                painter = painterResource(R.drawable.baseline_error_outline_24),
                                contentDescription = "소요 시간 부족",
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "소요 시간 부족",
                                style = Typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }

                    Text(
                        text = getDurationString(updatedWiD.duration, mode = 3),
                        style = Typography.bodyMedium,
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            FilledIconButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    clickedWiDViewModel.updateWiD() { updateWiDSuccess: Boolean ->
                        if (updateWiDSuccess) {
                            onBackButtonPressed()
                        }
                    }
                },
                enabled = !startOverlap && !finishOverlap && durationExist,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_done_24),
                        contentDescription = "수정 완료",
                    )

                    Text(
                        text = "수정 완료",
                        style = Typography.bodyMedium
                    )
                }
            }
        }

        if (showTitleMenu) {
            AlertDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                onDismissRequest = {
                    clickedWiDViewModel.setShowTitleMenu(show = false)
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "제목 선택",
                        style = Typography.titleLarge
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight / 2)
                    ) {
                        items(titleColorMap.size) { index ->
                            val itemTitle = titleColorMap.keys.elementAt(index)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        val date = updatedWiD.date
                                        val title = itemTitle
                                        val start = updatedWiD.start
                                        val finish = updatedWiD.finish
                                        val duration = Duration.between(start, finish)

                                        val updatedWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = start,
                                            finish = finish,
                                            duration = duration
                                        )

                                        clickedWiDViewModel.setUpdatedWiD(updatedWiD = updatedWiD)

                                        clickedWiDViewModel.setShowTitleMenu(show = false)
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    text = itemTitle,
                                    style = Typography.bodyMedium,
                                )

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                )

                                RadioButton(
                                    selected = updatedWiD.title == itemTitle,
                                    onClick = {
                                        val date = updatedWiD.date
                                        val title = itemTitle
                                        val start = updatedWiD.start
                                        val finish = updatedWiD.finish
                                        val duration = Duration.between(start, finish)

                                        val updatedWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = start,
                                            finish = finish,
                                            duration = duration
                                        )

                                        clickedWiDViewModel.setUpdatedWiD(updatedWiD = updatedWiD)

                                        clickedWiDViewModel.setShowTitleMenu(show = false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showStartPicker) {
            DatePickerDialog(
                onDismissRequest = {
                    clickedWiDViewModel.setShowStartPicker(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val date = updatedWiD.date
                            val title = updatedWiD.title
                            val newStart = LocalTime.of(
                                startTimePickerState.hour,
                                startTimePickerState.minute
                            )
                            val finish = updatedWiD.finish
                            val duration = Duration.between(newStart, finish)

                            val updatedWiD = WiD(
                                id = "",
                                date = date,
                                title = title,
                                start = newStart,
                                finish = finish,
                                duration = duration
                            )

                            clickedWiDViewModel.setUpdatedWiD(updatedWiD = updatedWiD)
                            clickedWiDViewModel.checkNewStartOverlap()
                            clickedWiDViewModel.checkNewWiDOverlap()
                            clickedWiDViewModel.checkDurationExist()

                            clickedWiDViewModel.setStartModified(modified = true)
                            clickedWiDViewModel.setShowStartPicker(show = false)
                        }) {
                        Text(
                            text = "확인",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            clickedWiDViewModel.setShowStartPicker(show = false)
                        }
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyMedium
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "시작 시간 선택",
                        style = Typography.titleLarge
                    )

                    TimePicker(state = startTimePickerState)
                }
            }
        }

        if (showFinishPicker) {
            DatePickerDialog(
                onDismissRequest = {
                    clickedWiDViewModel.setShowFinishPicker(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val date = updatedWiD.date
                            val title = updatedWiD.title
                            val start = updatedWiD.start
                            val newFinish = LocalTime.of(
                                finishTimePickerState.hour,
                                finishTimePickerState.minute
                            )
                            val duration = Duration.between(start, newFinish)

                            val updatedWiD = WiD(
                                id = "",
                                date = date,
                                title = title,
                                start = start,
                                finish = newFinish,
                                duration = duration
                            )

                            clickedWiDViewModel.setUpdatedWiD(updatedWiD = updatedWiD)
                            clickedWiDViewModel.checkNewFinishOverlap()
                            clickedWiDViewModel.checkNewWiDOverlap()
                            clickedWiDViewModel.checkDurationExist()

                            clickedWiDViewModel.setFinishModified(modified = true)
                            clickedWiDViewModel.setShowFinishPicker(show = false)
                        }) {
                        Text(
                            text = "확인",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            clickedWiDViewModel.setShowFinishPicker(show = false)
                        }
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyMedium
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "종료 시간 선택",
                        style = Typography.titleLarge
                    )

                    TimePicker(state = finishTimePickerState)
                }
            }
        }

        if (showDeleteClickedWiDDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    clickedWiDViewModel.setShowDeleteClickedWiDDialog(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clickedWiDViewModel.deleteWiD() { deleteWiDSuccess: Boolean ->
                                if (deleteWiDSuccess) {
                                    onBackButtonPressed()
                                }
                            }

                            clickedWiDViewModel.setShowDeleteClickedWiDDialog(false)
                        }
                    ) {
                        Text(
                            text = "삭제",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            clickedWiDViewModel.setShowDeleteClickedWiDDialog(false)
                        }
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyMedium
                        )
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
                        text = "WiD를 삭제하시겠습니까?",
                        style = Typography.bodyMedium
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WiDFragmentPreview() {
//    WiDView(0, NavController(LocalContext.current))
//}