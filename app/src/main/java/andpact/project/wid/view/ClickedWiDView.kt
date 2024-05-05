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

    val clickedWiD = clickedWiDViewModel.clickedWiD.value

    // 화면
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // 제목
    val showTitleMenu = clickedWiDViewModel.showTitleMenu.value
    val titleColorMap = clickedWiDViewModel.titleColorMap.value

    // 시작 시간
    val showStartPicker = clickedWiDViewModel.showStartPicker.value
    val startTimePickerState = rememberTimePickerState(initialHour = clickedWiD.start.hour, initialMinute = clickedWiD.start.minute, is24Hour = false)
    val startOverlap = clickedWiDViewModel.startOverlap.value

    // 종료 시간
    val showFinishPicker = clickedWiDViewModel.showFinishPicker.value
    val finishTimePickerState = rememberTimePickerState(initialHour = clickedWiD.finish.hour, initialMinute = clickedWiD.finish.minute, is24Hour = false)
    val finishOverlap = clickedWiDViewModel.finishOverlap.value

    // 소요 시간
    val durationExist = clickedWiDViewModel.durationExist.value

    val showDeleteClickedWiDDialog = clickedWiDViewModel.showDeleteClickedWiDDialog.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        val clickedWiDDate = clickedWiD.date
        clickedWiDViewModel.setWiDList(clickedWiDDate)

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
                    .background(color = titleColorMap[clickedWiD.title] ?: Transparent)
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
                    tint = titleColorMap[clickedWiD.title]?.let { textColor ->
                        if (textColor.luminance() > 0.5f)
                            Black
                        else
                            White
                    } ?: MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "WiD",
                    style = Typography.titleLarge,
                    color = titleColorMap[clickedWiD.title]?.let { textColor ->
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
                            clickedWiDViewModel.updateClickedWiD() {
                                onBackButtonPressed()
                            }
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (!startOverlap && !finishOverlap && durationExist) {
                                LimeGreen
                            } else {
                                MaterialTheme.colorScheme.tertiary
                            }
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                    text = "완료",
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
                        text = getDateString(date = clickedWiD.date),
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
                                clickedWiDViewModel.setShowTitleMenu(show = true)
                            },
                        text = clickedWiD.title,
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
                                clickedWiDViewModel.setShowStartPicker(show = true)
                            },
                        text = getTimeString(
                            time = clickedWiD.start,
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
                                clickedWiDViewModel.setShowFinishPicker(show = true)
                            },
                        text = getTimeString(
                            time = clickedWiD.finish,
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
                        text = getDurationString(clickedWiD.duration, mode = 3),
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
//                        .align(Alignment.CenterStart)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            clickedWiDViewModel.setShowDeleteClickedWiDDialog(show = true)
                        },
                    painter = painterResource(id = R.drawable.outline_delete_16),
                    contentDescription = "클릭한 위드 삭제",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showTitleMenu || showStartPicker || showFinishPicker || showDeleteClickedWiDDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        clickedWiDViewModel.setShowTitleMenu(show = false)
                        clickedWiDViewModel.setShowStartPicker(show = false)
                        clickedWiDViewModel.setShowFinishPicker(show = false)
                        clickedWiDViewModel.setShowDeleteClickedWiDDialog(show = false)
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
                                            val date = clickedWiD.date
                                            val title = itemTitle
                                            val start = clickedWiD.start
                                            val finish = clickedWiD.finish
                                            val duration = Duration.between(start, finish)

                                            val updatedClickedWiD = WiD(
                                                id = "",
                                                date = date,
                                                title = title,
                                                start = start,
                                                finish = finish,
                                                duration = duration
                                            )

                                            clickedWiDViewModel.setClickedWiD(clickedWiD = updatedClickedWiD)

                                            clickedWiDViewModel.setShowTitleMenu(show = false)
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
                                        selected = clickedWiD.title == itemTitle,
                                        onClick = {
                                            val date = clickedWiD.date
                                            val title = itemTitle
                                            val start = clickedWiD.start
                                            val finish = clickedWiD.finish
                                            val duration = Duration.between(start, finish)

                                            val updatedClickedWiD = WiD(
                                                id = "",
                                                date = date,
                                                title = title,
                                                start = start,
                                                finish = finish,
                                                duration = duration
                                            )

                                            clickedWiDViewModel.setClickedWiD(clickedWiD = updatedClickedWiD)

                                            clickedWiDViewModel.setShowTitleMenu(show = false)
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
                                        clickedWiDViewModel.setShowStartPicker(show = false)
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
                                        clickedWiDViewModel.setShowStartPicker(show = false)

                                        val date = clickedWiD.date
                                        val title = clickedWiD.title
                                        val newStart = LocalTime.of(
                                            startTimePickerState.hour,
                                            startTimePickerState.minute
                                        )
                                        val finish = clickedWiD.finish
                                        val duration = Duration.between(newStart, finish)

                                        val updatedClickedWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = newStart,
                                            finish = finish,
                                            duration = duration
                                        )

                                        clickedWiDViewModel.setClickedWiD(clickedWiD = updatedClickedWiD)
                                        clickedWiDViewModel.checkNewStartOverlap()
                                        clickedWiDViewModel.checkNewWiDOverlap()
                                        clickedWiDViewModel.checkDurationExist()
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
                                        clickedWiDViewModel.setShowFinishPicker(show = false)
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
                                        clickedWiDViewModel.setShowFinishPicker(show = false)

                                        val date = clickedWiD.date
                                        val title = clickedWiD.title
                                        val start = clickedWiD.start
                                        val newFinish = LocalTime.of(
                                            finishTimePickerState.hour,
                                            finishTimePickerState.minute
                                        )
                                        val duration = Duration.between(start, newFinish)

                                        val updatedClickedWiD = WiD(
                                            id = "",
                                            date = date,
                                            title = title,
                                            start = start,
                                            finish = newFinish,
                                            duration = duration
                                        )

                                        clickedWiDViewModel.setClickedWiD(clickedWiD = updatedClickedWiD)
                                        clickedWiDViewModel.checkNewFinishOverlap()
                                        clickedWiDViewModel.checkNewWiDOverlap()
                                        clickedWiDViewModel.checkDurationExist()
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

                    if (showDeleteClickedWiDDialog) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = "WiD를\n삭제하시겠습니까?",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = "취소",
                                style = Typography.bodyMedium,
                                color = DeepSkyBlue
                            )

                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        clickedWiDViewModel.deleteCLickedWiD() {
                                            onBackButtonPressed()
                                        }
                                    },
                                text = "삭제",
                                style = Typography.bodyMedium,
                                color = OrangeRed
                            )
                        }
                    }
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