package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.activity.MainActivityDestinations
import andpact.project.wid.service.WiDService
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiDListFragment(mainActivityNavController: NavController) {
    // 날짜
    val currentTime: LocalTime = LocalTime.now()
    val today: LocalDate = LocalDate.now()
    var currentDate by remember { mutableStateOf(today) }
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
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList by remember(currentDate) { mutableStateOf(wiDService.readDailyWiDListByDate(currentDate)) }
    val fullWiDList by remember(currentDate, wiDList) { mutableStateOf(getFullWiDListFromWiDList(date = currentDate, currentTime = currentTime, wiDList = wiDList)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.surface)
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
                    .clickable {
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
                    .clickable {
                        currentDate = currentDate.minusDays(1)
                    },
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "이전 날짜",
                tint = MaterialTheme.colorScheme.primary
            )

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = currentDate != today) {
                        currentDate = currentDate.plusDays(1)
                    },
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "다음 날짜",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (wiDList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
//                getEmptyView(text = "표시할 WiD가 없습니다.")()

                Text(
                    text = "표시할 기록이 없습니다.",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier
                        .clickable {
                            mainActivityNavController.navigate(MainActivityDestinations.NewWiDFragmentDestination.route + "/${LocalTime.MIN}/${LocalTime.MIN}")
                        }
                ) {
                    Text(
                        text = "새로운 WiD 만들기",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "New WiD",
                        tint = MaterialTheme.colorScheme.primary
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
                                        .clickable {
                                            mainActivityNavController.navigate(MainActivityDestinations.NewWiDFragmentDestination.route + "/${wiD.start}/${wiD.finish}")
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
//                                        modifier = Modifier
//                                            .padding(16.dp),
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
                                        .clickable {
                                            mainActivityNavController.navigate(MainActivityDestinations.WiDFragmentDestination.route + "/${wiD.id}")
                                        },
//                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
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
//                                        modifier = Modifier
//                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
//                                            text = "${getTimeString(wiD.start, "a hh:mm:ss")} ~ ${getTimeString(wiD.finish, "a hh:mm:ss")}",
                                            text = "${titleMap[wiD.title]}",
                                            style = Typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
//                                            text = "${titleMap[wiD.title]} • ${getDurationString(wiD.duration, mode = 3)}",
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

//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min)
//                                .padding(horizontal = 16.dp),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .width(8.dp)
//                                    .fillMaxHeight()
//                                    .background(colorMap[wiD.title] ?: DarkGray)
//                            )
//
//                            Row(
//                                modifier = Modifier
//                                    .shadow(
//                                        elevation = 2.dp,
//                                        shape = RoundedCornerShape(8.dp),
//                                        spotColor = MaterialTheme.colorScheme.primary,
//                                    )
//                                    .background(MaterialTheme.colorScheme.secondary)
//                                    .clickable {
//                                        navController.navigate(Destinations.WiDFragmentDestination.route + "/${wiD.id}")
//                                    },
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .padding(16.dp),
//                                    verticalArrangement = Arrangement.spacedBy(4.dp)
//                                ) {
//                                    Text(
//                                        text = "${getTimeString(wiD.start, "a hh:mm:ss")} ~ ${getTimeString(wiD.finish, "a hh:mm:ss")}",
//                                        style = Typography.bodyMedium,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
//
//                                    Text(
//                                        text = "${titleMap[wiD.title]} • ${getDurationString(wiD.duration, mode = 3)}",
//                                        style = Typography.bodyMedium,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
//                                }
//
//                                Spacer(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                )
//
//                                Icon(
//                                    modifier = Modifier
//                                        .padding(horizontal = 16.dp)
//                                        .size(24.dp),
//                                    imageVector = Icons.Default.KeyboardArrowRight,
//                                    contentDescription = "이 WiD로 전환하기",
//                                    tint = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
                    }
                }
            }
        }
    }

    /**
     * 대화상자
     */
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = expandDatePicker,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(expandDatePicker) {
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
                        .clickable(false) {}
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .clickable {
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
                            .clickable {
                                expandDatePicker = false
                                currentDate = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(
                                    ZoneId.systemDefault()).toLocalDate()
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