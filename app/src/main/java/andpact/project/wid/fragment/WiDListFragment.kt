package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.MainActivityDestinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.WiDListViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDListFragment(mainActivityNavController: NavController, wiDListViewModel: WiDListViewModel) {
    // 날짜
    val today = wiDListViewModel.today
    val currentDate = wiDListViewModel.currentDate.value
    var expandDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() + (9 * 60 * 60 * 1000),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // utcTimeMillis는 KST를 반환하는 듯하고,
                // System.currentTimeMillis()가 (KST -9시간)을 반환하는 듯.
                return utcTimeMillis <= System.currentTimeMillis() + (9 * 60 * 60 * 1000)
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = LocalDate.now().year
                return year <= currentYear
            }
        }
    )

    // WiD
    val fullWiDList = wiDListViewModel.fullWiDList.value

    DisposableEffect(Unit) {
        Log.d("WiDListFragment", "WiDListFragment is being composed")

        // WiD를 삭제하거나 생성한 후 돌아 왔을 때, 리스트가 갱신되도록.
        wiDListViewModel.setCurrentDate(wiDListViewModel.currentDate.value)

        onDispose {
            Log.d("WiDListFragment", "WiDListFragment is being disposed")
        }
    }

    BackHandler(
        enabled = expandDatePicker,
        onBack = {
            expandDatePicker = false
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            /**
             * 상단 바
             */
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            expandDatePicker = true
                        },
                    text = getDateString(currentDate),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.minusDays(1)
                            wiDListViewModel.setCurrentDate(newDate)
                        },
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 날짜",
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            enabled = currentDate != today,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.plusDays(1)
                            wiDListViewModel.setCurrentDate(newDate)
                        },
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 날짜",
                    tint = if (currentDate == today) DarkGray else MaterialTheme.colorScheme.primary
                )
            }

            if (fullWiDList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = "표시할 기록이 없습니다.",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                mainActivityNavController.navigate(MainActivityDestinations.NewWiDFragmentDestination.route + "/${wiDListViewModel.currentDate.value}/${LocalTime.MIN}/${LocalTime.MIN}")
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "새로운 WiD 만들기",
                            style = Typography.bodyMedium,
                            color = DeepSkyBlue
                        )

                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.outline_add_box_24),
                            contentDescription = "새로운 WiD",
                            tint = DeepSkyBlue
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = getTimeString(LocalTime.MIN, "a hh:mm:ss"),
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            HorizontalDivider(thickness = 1.dp)
                        }

                        fullWiDList.forEach { wiD ->
                            if (wiD.id.toInt() == 0) { // 빈 WiD
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8))
                                            .background(MaterialTheme.colorScheme.tertiary)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                mainActivityNavController.navigate(MainActivityDestinations.NewWiDFragmentDestination.route + "/${wiDListViewModel.currentDate.value}/${LocalTime.MIN}/${LocalTime.MIN}")
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondary)
                                                .padding(8.dp)
                                                .size(24.dp),
                                            painter = painterResource(id = R.drawable.baseline_title_24),
                                            contentDescription = "제목",
                                            tint = MaterialTheme.colorScheme.primary
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "기록 없음",
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            Text(
                                                text = getDurationString(wiD.duration, mode = 3),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier
                                                .weight(1f)
                                        )

                                        Icon(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .size(24.dp),
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "이 WiD로 전환하기",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        HorizontalDivider()
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8))
                                            .background(colorMap[wiD.title] ?: DarkGray)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                mainActivityNavController.navigate(
                                                    MainActivityDestinations.WiDFragmentDestination.route + "/${wiD.id}"
                                                )
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondary)
                                                .padding(8.dp)
                                                .size(24.dp),
                                            painter = painterResource(id = titleIconMap[wiD.title] ?: R.drawable.baseline_title_24),
                                            contentDescription = "제목",
                                            tint = MaterialTheme.colorScheme.primary
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "${titleMap[wiD.title]}",
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            Text(
                                                text = getDurationString(wiD.duration, mode = 3),
                                                style = Typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier
                                                .weight(1f)
                                        )

                                        Icon(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .size(24.dp),
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "이 WiD로 전환하기",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(
                                            text = getTimeString(wiD.finish, "a hh:mm:ss"),
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * 날짜 대화상자
         */
//        AnimatedVisibility(
//            modifier = Modifier
//                .fillMaxSize(),
//            visible = expandDatePicker,
//            enter = fadeIn(),
//            exit = fadeOut(),
//        ) {
        if (expandDatePicker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = expandDatePicker,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        expandDatePicker = false
                    }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                    expandDatePicker = false
                                },
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "날짜 메뉴 닫기",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = "날짜 선택",
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
                                    expandDatePicker = false
                                    val newDate = Instant
                                        .ofEpochMilli(datePickerState.selectedDateMillis!!)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    wiDListViewModel.setCurrentDate(newDate)
                                },
                            text = "확인",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider()

                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        title = null,
                        headline = null,
                    )
                }
            }
        }
    }
}