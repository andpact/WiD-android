package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.EmptyWiDViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyWiDView(
    onBackButtonPressed: () -> Unit,
    emptyWiDViewModel: EmptyWiDViewModel = hiltViewModel()
) {
    val TAG = "EmptyWiDView"

    // WiD
    val emptyWiD = emptyWiDViewModel.emptyWiD.value

    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    val showTitleMenu = emptyWiDViewModel.showTitleMenu.value
    val titleColorMap = emptyWiDViewModel.titleColorMap

    // 시작 시간
    val showStartPicker = emptyWiDViewModel.showStartPicker.value
    val startTimePickerState = rememberTimePickerState(
        initialHour = emptyWiD.start.hour,
        initialMinute = emptyWiD.start.minute,
        is24Hour = false
    )
    val startOverlap = emptyWiDViewModel.startOverlap.value
    val startModified = emptyWiDViewModel.startModified.value

    // 종료 시간
    val showFinishPicker = emptyWiDViewModel.showFinishPicker.value
    val finishTimePickerState = rememberTimePickerState(
        initialHour = emptyWiD.finish.hour,
        initialMinute = emptyWiD.finish.minute,
        is24Hour = false
    )
    val finishOverlap = emptyWiDViewModel.finishOverlap.value
    val finishModified = emptyWiDViewModel.finishModified.value

    // 소요 시간
    val durationExist = emptyWiDViewModel.durationExist.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        val emptyWiDDate = emptyWiD.date
        emptyWiDViewModel.getWiDListByDate(collectionDate = emptyWiDDate)

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
                        text = "새로운 WiD",
                        style = Typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { contentPadding -> // Scaffold 내부는 박스로 되어 있음.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
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
                        text = getDateString(date = emptyWiD.date),
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        emptyWiDViewModel.setShowTitleMenu(show = true)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
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
                        text = titleNumberStringToTitleKRStringMap[emptyWiD.title] ?: "",
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
                    .padding(16.dp)
                    .clickable {
                        emptyWiDViewModel.setShowStartPicker(show = true)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
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
                            time = emptyWiD.start,
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
                    .padding(16.dp)
                    .clickable {
                        emptyWiDViewModel.setShowFinishPicker(show = true)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
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
                            time = emptyWiD.finish,
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
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
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

                        if (startModified && finishModified && !durationExist) {
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
                        text = getDurationString(emptyWiD.duration, mode = 3),
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
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    emptyWiDViewModel.createWiD() { createWiDSuccess: Boolean ->
                        if (createWiDSuccess) {
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
                        painter = painterResource(R.drawable.outline_add_box_24),
                        contentDescription = "새로운 WiD 만들기",
                    )

                    Text(
                        text = "새로운 WiD 만들기",
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
                    emptyWiDViewModel.setShowTitleMenu(show = false)
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
                                        val date = emptyWiD.date
                                        val title = itemTitle
                                        val start = emptyWiD.start
                                        val finish = emptyWiD.finish
                                        val duration = Duration.between(start, finish)

                                        val newWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = start,
                                            finish = finish,
                                            duration = duration
                                        )

                                        emptyWiDViewModel.setEmptyWiD(newWiD = newWiD)

                                        emptyWiDViewModel.setShowTitleMenu(show = false)
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
                                    selected = emptyWiD.title == itemTitle,
                                    onClick = {
                                        val date = emptyWiD.date
                                        val title = itemTitle
                                        val start = emptyWiD.start
                                        val finish = emptyWiD.finish
                                        val duration = Duration.between(start, finish)

                                        val newWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = start,
                                            finish = finish,
                                            duration = duration
                                        )

                                        emptyWiDViewModel.setEmptyWiD(newWiD = newWiD)

                                        emptyWiDViewModel.setShowTitleMenu(show = false)
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
                    emptyWiDViewModel.setShowStartPicker(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val date = emptyWiD.date
                            val title = emptyWiD.title
                            val newStart = LocalTime.of(
                                startTimePickerState.hour,
                                startTimePickerState.minute
                            )
                            val finish = emptyWiD.finish
                            val duration = Duration.between(newStart, finish)

                            val newWiD = WiD(
                                id = "",
                                date = date,
                                title = title,
                                start = newStart,
                                finish = finish,
                                duration = duration
                            )

                            emptyWiDViewModel.setEmptyWiD(newWiD = newWiD)

                            emptyWiDViewModel.setStartModified(modified = true)
                            emptyWiDViewModel.setShowStartPicker(show = false)
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
                            emptyWiDViewModel.setShowStartPicker(show = false)
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
                    emptyWiDViewModel.setShowFinishPicker(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val date = emptyWiD.date
                            val title = emptyWiD.title
                            val start = emptyWiD.start
                            val newFinish = LocalTime.of(
                                finishTimePickerState.hour,
                                finishTimePickerState.minute
                            )
                            val duration = Duration.between(start, newFinish)

                            val newWiD = WiD(
                                id = "",
                                date = date,
                                title = title,
                                start = start,
                                finish = newFinish,
                                duration = duration
                            )

                            emptyWiDViewModel.setEmptyWiD(newWiD = newWiD)

                            emptyWiDViewModel.setFinishModified(modified = true)
                            emptyWiDViewModel.setShowFinishPicker(show = false)
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
                            emptyWiDViewModel.setShowFinishPicker(show = false)
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
    }
}