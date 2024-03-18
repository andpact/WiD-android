package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.MainActivityDestinations
import andpact.project.wid.service.DiaryService
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.getDateStringWith3Lines
import andpact.project.wid.util.getNoBackgroundEmptyViewWithMultipleLines
import andpact.project.wid.viewModel.DayDiaryViewModel
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDiaryFragment(mainActivityNavController: NavController, dayDiaryViewModel: DayDiaryViewModel) {
    // 날짜
    val today = dayDiaryViewModel.today
    val currentDate = dayDiaryViewModel.currentDate.value
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
    val wiDList = dayDiaryViewModel.wiDList.value

    // 다이어리
    val diary = dayDiaryViewModel.diary.value

    DisposableEffect(Unit) {
        Log.d("DayDiaryFragment", "DayDiaryFragment is being composed")

        dayDiaryViewModel.setCurrentDate(dayDiaryViewModel.currentDate.value)

        onDispose {
            Log.d("DayDiaryFragment", "DayDiaryFragment is being disposed")
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
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            mainActivityNavController.navigate(MainActivityDestinations.DiaryFragmentDestination.route + "/${currentDate}")
                        },
                    text = "다이어리 수정",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Icon(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.minusDays(1)
                            dayDiaryViewModel.setCurrentDate(newDate)
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 날짜",
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    modifier = Modifier
                        .clickable(
                            enabled = currentDate != today,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val newDate = currentDate.plusDays(1)
                            dayDiaryViewModel.setCurrentDate(newDate)
                        }
                        .size(24.dp),
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 날짜",
                    tint = if (currentDate != today) MaterialTheme.colorScheme.primary else DarkGray
                )
            }

            /**
             * 다이어리
             */
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        expandDatePicker = true
                                    },
                                text = getDateStringWith3Lines(date = currentDate),
                                style = Typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f / 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (wiDList.isEmpty()) {
                                getNoBackgroundEmptyViewWithMultipleLines(text = "표시할\n타임라인이\n없습니다.")()
                            } else {
                                DiaryPieChartFragment(wiDList = wiDList)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = diary?.title ?: "",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = diary?.content ?: "당신이 이 날 무엇을 하고,\n그 속에서 어떤 생각과 감정을 느꼈는지\n주체적으로 기록해보세요.",
                            textAlign = if (diary == null) TextAlign.Center else null,
                            style = if (diary == null) Typography.bodyMedium else Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 30.sp
                        )
                    }
                }
            }
        }

        /**
         * 대화상자
         */
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
                        .align(Alignment.BottomCenter)
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
                            .clickable(
                                enabled = false,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
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

                                    dayDiaryViewModel.setCurrentDate(newDate)
                                },
                            text = "확인",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

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