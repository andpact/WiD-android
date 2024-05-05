package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.EmptyWiDViewModel
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
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
    val titleColorMap = emptyWiDViewModel.titleColorMap.value

    // 시작 시간
    val showStartPicker = emptyWiDViewModel.showStartPicker.value
    val startTimePickerState = rememberTimePickerState(initialHour = emptyWiD.start.hour, initialMinute = emptyWiD.start.minute, is24Hour = false)
    val startOverlap = emptyWiDViewModel.startOverlap.value

    // 종료 시간
    val showFinishPicker = emptyWiDViewModel.showFinishPicker.value
    val finishTimePickerState = rememberTimePickerState(initialHour = emptyWiD.finish.hour, initialMinute = emptyWiD.finish.minute, is24Hour = false)
    val finishOverlap = emptyWiDViewModel.finishOverlap.value

    // 소요 시간
    val durationExist = emptyWiDViewModel.durationExist.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        val emptyWiDDate = emptyWiD.date
        emptyWiDViewModel.setWiDList(emptyWiDDate)

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            /**
             * 상단 바
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = titleColorMap[emptyWiD.title] ?: Transparent)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterStart)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onBackButtonPressed()
                        },
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "뒤로 가기",
                    tint = titleColorMap[emptyWiD.title]?.let { textColor ->
                        if (textColor.luminance() > 0.5f)
                            Black
                        else
                            White
                    } ?: MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "새로운 WiD",
                    style = Typography.titleLarge,
                    color = titleColorMap[emptyWiD.title]?.let { textColor ->
                        if (textColor.luminance() > 0.5f)
                            Black
                        else
                            White
                    } ?: MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(
                            enabled = !startOverlap && !finishOverlap && durationExist,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            emptyWiDViewModel.createWiD() {
                                onBackButtonPressed()
                            }
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (!startOverlap && !finishOverlap && durationExist) {
                                DeepSkyBlue
                            } else {
                                MaterialTheme.colorScheme.tertiary
                            }
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                    text = "등록",
                    style = Typography.bodyMedium,
                    color = White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "날짜",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = getDateString(date = emptyWiD.date),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "제목",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepSkyBlue.copy(alpha = 0.2f))
                            .padding(16.dp)
                            .clickable {
                                emptyWiDViewModel.setShowTitleMenu(show = true)
                            },
                        text = emptyWiD.title,
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "시작",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    if (startOverlap) {
                        Text(
                            text = "시작 시간 겹침",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepSkyBlue.copy(alpha = 0.2f))
                            .padding(16.dp)
                            .clickable {
                                emptyWiDViewModel.setShowStartPicker(show = true)
                            },
                        text = getTimeString(
                            time = emptyWiD.start,
                            patten = "a hh:mm:ss"
                        ),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "종료",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    if (finishOverlap) {
                        Text(
                            text = "종료 시간 겹침",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepSkyBlue.copy(alpha = 0.2f))
                            .padding(16.dp)
                            .clickable {
                                emptyWiDViewModel.setShowFinishPicker(show = true)
                            },
                        text = getTimeString(
                            time = emptyWiD.finish,
                            patten = "a hh:mm:ss"
                        ),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = "소요",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = getDurationString(emptyWiD.duration, mode = 3),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        /**
         * 제목, 시작 시간, 종료 시간 선택 대화 상자
         */
//    AnimatedVisibility(
//        modifier = Modifier
//            .fillMaxSize(),
////        visible = expandDatePicker || titleMenuExpanded || expandStartPicker || expandFinishPicker,
//        visible = titleMenuExpanded || expandStartPicker || expandFinishPicker,
//        enter = fadeIn(),
//        exit = fadeOut(),
//    ) {
        if (showTitleMenu || showStartPicker || showFinishPicker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        emptyWiDViewModel.setShowTitleMenu(show = false)
                        emptyWiDViewModel.setShowStartPicker(show = false)
                        emptyWiDViewModel.setShowFinishPicker(show = false)
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .then(if (showTitleMenu) Modifier.height(screenHeight / 2) else Modifier)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = MaterialTheme.colorScheme.primary,
                        )
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showTitleMenu) {
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

                                            val newEmptyWiD = WiD(
                                                id = "",
                                                date = date,
                                                title = title,
                                                start = start,
                                                finish = finish,
                                                duration = duration
                                            )

                                            emptyWiDViewModel.setEmptyWiD(emptyWiD = newEmptyWiD)

                                            emptyWiDViewModel.setShowTitleMenu(show = false)
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        text = itemTitle,
                                        style = Typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
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

                                            val newEmptyWiD = WiD(
                                                id = "",
                                                date = date,
                                                title = title,
                                                start = start,
                                                finish = finish,
                                                duration = duration
                                            )

                                            emptyWiDViewModel.setEmptyWiD(emptyWiD = newEmptyWiD)

                                            emptyWiDViewModel.setShowTitleMenu(show = false)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showStartPicker) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = false,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp)
                                    .align(Alignment.CenterStart)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        emptyWiDViewModel.setShowStartPicker(show = false)
                                    },
                                painter = painterResource(id = R.drawable.baseline_close_24),
                                contentDescription = "시작 시간 메뉴 닫기",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                text = "시작 시간 선택",
                                style = Typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(horizontal = 16.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        emptyWiDViewModel.setShowStartPicker(show = false)

                                        val date = emptyWiD.date
                                        val title = emptyWiD.title
                                        val newStart = LocalTime.of(
                                            startTimePickerState.hour,
                                            startTimePickerState.minute
                                        )
                                        val finish = emptyWiD.finish
                                        val duration = Duration.between(newStart, finish)

                                        val newEmptyWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = newStart,
                                            finish = finish,
                                            duration = duration
                                        )

                                        emptyWiDViewModel.setEmptyWiD(emptyWiD = newEmptyWiD)
                                    },
                                text = "확인",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        HorizontalDivider()

                        TimePicker(
                            modifier = Modifier
                                .padding(vertical = 16.dp),
                            state = startTimePickerState
                        )
                    }

                    if (showFinishPicker) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = false,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {

                                }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp)
                                    .align(Alignment.CenterStart)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        emptyWiDViewModel.setShowFinishPicker(show = false)
                                    },
                                painter = painterResource(id = R.drawable.baseline_close_24),
                                contentDescription = "종료 시간 메뉴 닫기",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                text = "종료 시간 선택",
                                style = Typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(horizontal = 16.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        emptyWiDViewModel.setShowFinishPicker(show = false)

                                        val date = emptyWiD.date
                                        val title = emptyWiD.title
                                        val start = emptyWiD.start
                                        val newFinish = LocalTime.of(
                                            finishTimePickerState.hour,
                                            finishTimePickerState.minute
                                        )
                                        val duration = Duration.between(start, newFinish)

                                        val newEmptyWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = start,
                                            finish = newFinish,
                                            duration = duration
                                        )

                                        emptyWiDViewModel.setEmptyWiD(emptyWiD = newEmptyWiD)
                                    },
                                text = "확인",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        HorizontalDivider()

                        TimePicker(
                            modifier = Modifier
                                .padding(vertical = 16.dp),
                            state = finishTimePickerState
                        )
                    }
                }
            }
        }
    }
}