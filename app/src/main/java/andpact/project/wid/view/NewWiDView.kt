package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.NewWiDViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWiDView(
    onBackButtonPressed: () -> Unit,
    newWiDViewModel: NewWiDViewModel = hiltViewModel()
) {
    val TAG = "NewWiDView"

    // WiD
    val newWiD = newWiDViewModel.newWiD.value // 변경 전
    val updatedNewWiD = newWiDViewModel.updatedNewWiD.value // 변경 후
    val isLastUpdatedNewWiDTimerRunning = newWiDViewModel.isLastUpdatedNewWiDTimerRunning.value

    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    val showTitleMenu = newWiDViewModel.showTitleMenu.value

    /** 시작 시간을 변경하지 않으면, 기존의 State를 유지하도록 해야 함. */
    // 시작 시간
    val showStartPicker = newWiDViewModel.showStartPicker.value
    val startTimePickerState = rememberTimePickerState(
        initialHour = updatedNewWiD.start.hour,
        initialMinute = updatedNewWiD.start.minute,
        is24Hour = false
    )
    val startOverlap = newWiDViewModel.startOverlap.value
    val startModified = newWiDViewModel.startModified.value

    // 종료 시간
    val showFinishPicker = newWiDViewModel.showFinishPicker.value
    val finishTimePickerState = rememberTimePickerState(
        initialHour = updatedNewWiD.finish.hour,
        initialMinute = updatedNewWiD.finish.minute,
        is24Hour = false
    )
    val finishOverlap = newWiDViewModel.finishOverlap.value
    val finishModified = newWiDViewModel.finishModified.value

    // 소요 시간
    val durationExist = newWiDViewModel.durationExist.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        val newWiDDate = newWiD.date
        newWiDViewModel.getWiDListByDate(collectionDate = newWiDDate)

        if (newWiD.id == "lastNewWiD") {
            newWiDViewModel.startLastNewWiDTimer()
            newWiDViewModel.startLastUpdatedNewWiDTimer()
        }

        onDispose {
            Log.d(TAG, "disposed")

            newWiDViewModel.stopLastNewWiDTimer()
            newWiDViewModel.stopLastUpdatedNewWiDTimer()
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
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_calendar_month_24),
                    contentDescription = "날짜",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "날짜",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = getDateString(date = updatedNewWiD.date),
                        style = Typography.bodyMedium,
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
                    .clickable {
                        newWiDViewModel.setShowTitleMenu(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_title_24),
                    contentDescription = "제목",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "제목",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = updatedNewWiD.title.kr,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "제목 수정",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        newWiDViewModel.setShowStartPicker(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_play_arrow_24),
                    contentDescription = "시작 시간",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                            time = updatedNewWiD.start,
                            patten = "a hh:mm:ss"
                        ),
                        style = Typography.bodyMedium,
                    )
                }

                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "시작 시간 수정",
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        newWiDViewModel.setShowFinishPicker(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_stop_24),
                    contentDescription = "종료 시간",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = getTimeString(
                                time = updatedNewWiD.finish,
                                patten = "a hh:mm:ss"
                            ),
                            style = Typography.bodyMedium,
                        )

                        if (isLastUpdatedNewWiDTimerRunning) {
                            Text(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                text = "LIVE",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Icon(
                    modifier = Modifier
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "종료 시간 수정",
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_timelapse_24),
                    contentDescription = "소요 시간",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                        text = getDurationString(updatedNewWiD.duration),
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
                    newWiDViewModel.createWiD() { createWiDSuccess: Boolean ->
                        if (createWiDSuccess) {
                            onBackButtonPressed()
                        }
                    }
                },
                enabled = !startOverlap && !finishOverlap && durationExist,
                shape = RectangleShape
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
                    newWiDViewModel.setShowTitleMenu(show = false)
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                        items(Title.values().drop(1).size) { index -> // drop(1) 사용하여 첫 번째 요소 제외
                            val itemTitle = Title.values().drop(1)[index] // Title enum에서 첫 번째 값 제외

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val updatedNewWiD = WiD(
                                            id = updatedNewWiD.id,
                                            date = updatedNewWiD.date,
                                            title = itemTitle, // Title enum 타입으로 변경
                                            start = updatedNewWiD.start,
                                            finish = updatedNewWiD.finish,
                                            duration = Duration.between(updatedNewWiD.start, updatedNewWiD.finish),
                                            createdBy = updatedNewWiD.createdBy
                                        )

                                        newWiDViewModel.setUpdateNewWiD(updatedNewWiD = updatedNewWiD)
                                        newWiDViewModel.setShowTitleMenu(show = false)
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .width(8.dp)
                                )

                                Image(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    painter = painterResource(id = itemTitle.smallImage),
                                    contentDescription = "앱 아이콘"
                                )

                                Spacer(
                                    modifier = Modifier
                                        .width(8.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(top = 8.dp, bottom = 4.dp),
                                        text = itemTitle.kr,
                                        style = Typography.bodyMedium
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(top = 4.dp, bottom = 8.dp),
                                        text = itemTitle.description,
                                        style = Typography.labelMedium,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }

                                RadioButton(
                                    selected = updatedNewWiD.title == itemTitle,
                                    onClick = {
                                        val updatedNewWiD = WiD(
                                            id = updatedNewWiD.id,
                                            date = updatedNewWiD.date,
                                            title = itemTitle, // Title enum 타입으로 변경
                                            start = updatedNewWiD.start,
                                            finish = updatedNewWiD.finish,
                                            duration = Duration.between(updatedNewWiD.start, updatedNewWiD.finish),
                                            createdBy = updatedNewWiD.createdBy
                                        )

                                        newWiDViewModel.setUpdateNewWiD(updatedNewWiD = updatedNewWiD)
                                        newWiDViewModel.setShowTitleMenu(show = false)
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
                    newWiDViewModel.setShowStartPicker(show = false)
                },
                confirmButton = { // 확인, 취소 버튼은 항상 제일 아래에 위치함.
                    FilledTonalButton(
                        onClick = {
                            val newStart = LocalTime.of(
                                startTimePickerState.hour,
                                startTimePickerState.minute
                            )

                            val updatedNewWiD = WiD(
                                id = updatedNewWiD.id,
                                date = updatedNewWiD.date,
                                title = updatedNewWiD.title,
                                start = newStart,
                                finish = updatedNewWiD.finish,
                                duration = Duration.between(newStart, updatedNewWiD.finish),
                                createdBy = updatedNewWiD.createdBy
                            )

                            newWiDViewModel.setUpdateNewWiD(updatedNewWiD = updatedNewWiD)
                            newWiDViewModel.setStartModified(modified = true)
                            newWiDViewModel.setShowStartPicker(show = false)
                        }
                    ) {
                        Text(
                            text = "확인",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = { // 시작 시간을 LIVE로 할 일이 없다.
                    TextButton(
                        onClick = {
                            newWiDViewModel.setShowStartPicker(show = false)
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "선택 가능 시간",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = getTimeString(
                                    time = newWiD.start,
                                    patten = "a hh:mm"
                                ),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = "~",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = getTimeString(
                                    time = updatedNewWiD.finish,
                                    patten = "a hh:mm"
                                ),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        if (showFinishPicker) {
            DatePickerDialog(
                onDismissRequest = {
                    newWiDViewModel.setShowFinishPicker(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            newWiDViewModel.stopLastUpdatedNewWiDTimer() // 수정하면 타이머 정지

                            val newFinish = LocalTime.of(
                                finishTimePickerState.hour,
                                finishTimePickerState.minute
                            )

                            val updatedNewWiD = WiD(
                                id = updatedNewWiD.id,
                                date = updatedNewWiD.date,
                                title = updatedNewWiD.title,
                                start = updatedNewWiD.start,
                                finish = newFinish,
                                duration = Duration.between(updatedNewWiD.start, newFinish),
                                createdBy = updatedNewWiD.createdBy
                            )

                            newWiDViewModel.setUpdateNewWiD(updatedNewWiD = updatedNewWiD)
                            newWiDViewModel.setFinishModified(modified = true)
                            newWiDViewModel.setShowFinishPicker(show = false)
                        }
                    ) {
                        Text(
                            text = "확인",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                    if (newWiD.id == "lastNewWiD") {
                        TextButton(
                            onClick = {
                                newWiDViewModel.startLastUpdatedNewWiDTimer()

                                newWiDViewModel.setShowFinishPicker(show = false)
                            },
                        ) {
                            Text(
                                text = "LIVE",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            newWiDViewModel.setShowFinishPicker(show = false)
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "선택 가능 시간",
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = getTimeString(
                                    time = updatedNewWiD.start,
                                    patten = "a hh:mm"
                                ),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = "~",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = getTimeString(
                                    time = newWiD.finish,
                                    patten = "a hh:mm"
                                ),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NewWiDPreview() {
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
    ) { contentPadding ->
        AlertDialog(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            onDismissRequest = {
            },
        ) {

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_calendar_month_24),
                    contentDescription = "날짜",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "날짜",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "2024년 10월 10일(수)",
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_calendar_month_24),
                    contentDescription = "제목",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "제목",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "공부",
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_play_arrow_24),
                    contentDescription = "시작",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "시작",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "오전 00시 00분 00초",
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_stop_24),
                    contentDescription = "날짜",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "종료",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "오전 00시 00분 00초",
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(16.dp),
                    painter = painterResource(R.drawable.baseline_timelapse_24),
                    contentDescription = "소요",
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "소요",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "1시간 10분 10초",
                        style = Typography.bodyMedium,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}