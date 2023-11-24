package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodBasedFragment() {
    // 날짜
    val today = LocalDate.now()
    var startDate by remember { mutableStateOf(getFirstDayOfWeek(today)) }
    var finishDate by remember { mutableStateOf(getLastDayOfWeek(today)) }


    // 제목
    val coroutineScope = rememberCoroutineScope()
    var selectedTitle by remember { mutableStateOf(titlesWithAll[0]) }
    var showTitleBottomSheet by rememberSaveable { mutableStateOf(false) }
    val titleBottomSheetState = rememberModalBottomSheetState()

    // WiD
    val wiDService = WiDService(context = LocalContext.current)
//    var wiDList = remember(startDate, finishDate) { wiDService.readWiDListByDateRange(startDate, finishDate) }
    var wiDList by remember(startDate, finishDate) { mutableStateOf(wiDService.readWiDListByDateRange(startDate, finishDate)) }
    var filteredWiDListByTitle by remember(wiDList, selectedTitle) { mutableStateOf(wiDList.filter { it.title == selectedTitle }) }

    // 기간
    var selectedPeriod by remember { mutableStateOf(periods[0]) }
    var showPeriodBottomSheet by rememberSaveable { mutableStateOf(false) }
    val periodBottomSheetState = rememberModalBottomSheetState()

    // 합계
//    var totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)
    var totalDurationMap by remember(wiDList) { mutableStateOf(getTotalDurationMapByTitle(wiDList = wiDList)) }

//    LaunchedEffect(Unit) {
//        titleBottomSheetState.hide()
//        periodBottomSheetState.hide()
//    }

    // 제목 변경
    if (showTitleBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    titleBottomSheetState.hide()

                    if (!titleBottomSheetState.isVisible) {
                        showTitleBottomSheet = false
                    }
                }
            },
            containerColor = Color.White,
            sheetState = titleBottomSheetState,
            dragHandle = null,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "조회할 제목 선택",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            titleBottomSheetState.hide()

                            if (!titleBottomSheetState.isVisible) {
                                showTitleBottomSheet = false
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_clear_24),
                        contentDescription = "Close title bottom sheet",
                        tint = Color.Black
                    )
                }
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(
                            WindowInsetsSides.Vertical
                        )
                    ),
                columns = GridCells.Fixed(2)
            ) {
                items(titlesWithAll.size + 1) { index ->
                    if (index == 1) {
                        return@items
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 기존 제목과 다른 제목을 선택하면 selectedTitle이 갱신됨.
                                if (selectedTitle != titlesWithAll[if (1 < index) index - 1 else index]) {
                                    selectedTitle = titlesWithAll[if (1 < index) index - 1 else index]

//                                    filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }
                                }

                                coroutineScope.launch {
                                    titleBottomSheetState.hide()

                                    if (!titleBottomSheetState.isVisible) {
                                        showTitleBottomSheet = false
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = titleMapWithAll[titlesWithAll[if (1 < index) index - 1 else index]] ?: "")

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(10.dp)
                                    .background(
                                        color = colorResource(
                                            id = colorMap[titlesWithAll[if (1 < index) index - 1 else index]]
                                                ?: R.color.light_gray
                                        )
                                    )
                            )
                        }

                        RadioButton(
                            selected = selectedTitle == titlesWithAll[if (1 < index) index - 1 else index],
                            onClick = { // 라디오 버튼도 온 클릭을 지정해줘야 함.
                                // 기존 제목과 다른 제목을 선택하면 selectedTitle이 갱신됨.
                                if (selectedTitle != titlesWithAll[if (1 < index) index - 1 else index]) {
                                    selectedTitle = titlesWithAll[if (1 < index) index - 1 else index]

//                                    filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }
                                }

                                coroutineScope.launch {
                                    titleBottomSheetState.hide()

                                    if (!titleBottomSheetState.isVisible) {
                                        showTitleBottomSheet = false
                                    }
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Black
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // 기간 변경
    if (showPeriodBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    periodBottomSheetState.hide()

                    if (!periodBottomSheetState.isVisible) {
                        showPeriodBottomSheet = false
                    }
                }
            },
            containerColor = Color.White,
            sheetState = periodBottomSheetState,
            dragHandle = null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "조회할 기간 선택",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            periodBottomSheetState.hide()

                            if (!periodBottomSheetState.isVisible) {
                                showPeriodBottomSheet = false
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_clear_24),
                        contentDescription = "Close period bottom sheet",
                        tint = Color.Black
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(
                            WindowInsetsSides.Vertical
                        )
                    ),
            ) {
                items(periods.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 선택된 기간을 다시 누르면 아무 반응 없도록.
                                if (selectedPeriod == periods[index]) {
                                    coroutineScope.launch {
                                        periodBottomSheetState.hide()

                                        if (!periodBottomSheetState.isVisible) {
                                            showPeriodBottomSheet = false
                                        }
                                    }

                                    return@clickable
                                }

                                selectedPeriod = periods[index]

                                if (selectedPeriod == periods[0]) { // 일주일
                                    startDate = getFirstDayOfWeek(today)
                                    finishDate = getLastDayOfWeek(today)
                                } else if (selectedPeriod == periods[1]) { // 한달
                                    startDate = getFirstDayOfMonth(today)
                                    finishDate = getLastDayOfMonth(today)
                                }

//                                wiDList = wiDService.readWiDListByDateRange(startDate, finishDate)
//                                totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

//                                filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }

                                coroutineScope.launch {
                                    periodBottomSheetState.hide()

                                    if (!periodBottomSheetState.isVisible) {
                                        showPeriodBottomSheet = false
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = periodMap[periods[index]] ?: "")

                        RadioButton(
                            selected = selectedPeriod == periods[index],
                            onClick = {
                                // 선택된 기간을 다시 누르면 아무 반응 없도록.
                                if (selectedPeriod == periods[index]) {
                                    coroutineScope.launch {
                                        periodBottomSheetState.hide()

                                        if (!periodBottomSheetState.isVisible) {
                                            showPeriodBottomSheet = false
                                        }
                                    }

                                    return@RadioButton
                                }

                                selectedPeriod = periods[index]

                                if (selectedPeriod == periods[0]) { // 일주일
                                    startDate = getFirstDayOfWeek(today)
                                    finishDate = getLastDayOfWeek(today)
                                } else if (selectedPeriod == periods[1]) { // 한달
                                    startDate = getFirstDayOfMonth(today)
                                    finishDate = getLastDayOfMonth(today)
                                }

//                                wiDList = wiDService.readWiDListByDateRange(startDate, finishDate)
//                                totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

//                                filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }

                                coroutineScope.launch {
                                    periodBottomSheetState.hide()

                                    if (!periodBottomSheetState.isVisible) {
                                        showPeriodBottomSheet = false
                                    }
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }

    // 전체 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 기간 및 제목 선택
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        coroutineScope.launch{
                            showTitleBottomSheet = true
                            titleBottomSheetState.partialExpand()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .scale(0.8f),
                            painter = painterResource(id = R.drawable.outline_subtitles_24),
                            contentDescription = "Title"
                        )

                        Text(text = titleMapWithAll[selectedTitle] ?: "")

                        Box(modifier = Modifier
                            .clip(CircleShape)
                            .size(10.dp)
                            .background(
                                color = colorResource(
                                    id = colorMap[selectedTitle] ?: R.color.light_gray
                                )
                            )
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                            contentDescription = "Show title bottom sheet"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        coroutineScope.launch{
                            showPeriodBottomSheet = true
                            periodBottomSheetState.partialExpand()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .scale(0.8f),
                            painter = painterResource(id = R.drawable.baseline_edit_calendar_24),
                            contentDescription = "Period"
                        )

                        Text(text = periodMap[selectedPeriod] ?: "")

                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                            contentDescription = "Show period bottom sheet"
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Text(
                    text = "시간 기록",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                    )
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    if (selectedTitle == titlesWithAll[0]) { // 제목이 "전체" 일 때 파이차트 표시
                        Column { // Surface는 Box와 같기 때문에 Column으로 한 번 감싸야 한다.
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                val daysOfWeek = if (selectedPeriod == periods[0]) daysOfWeekFromMonday else daysOfWeekFromSunday

                                daysOfWeek.forEachIndexed { index, day ->
                                    val textColor = when (index) {
                                        0 -> if (selectedPeriod == periods[1]) Color.Red else Color.Unspecified
                                        5 -> if (selectedPeriod == periods[0]) Color.Blue else Color.Unspecified
                                        6 -> if (selectedPeriod == periods[0]) Color.Red else if (selectedPeriod == periods[1]) Color.Blue else Color.Unspecified
                                        else -> Color.Unspecified
                                    }

                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = day,
                                        style = TextStyle(textAlign = TextAlign.Center, color = textColor)
                                    )
                                }
                            }

                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 700.dp), // lazy 뷰 안에 lazy 뷰를 넣기 위해서 높이를 지정해줘야 함. 최대 높이까지는 그리드 아이템을 감싸도록 함.
                                columns = GridCells.Fixed(7)
                            ) {
                                if (selectedPeriod == periods[1]) {
                                    items(startDate.dayOfWeek.value % 7) {
                                        // selectedPeriod가 한달이면 달력의 빈 칸을 생성해줌.
                                    }
                                }

                                items(ChronoUnit.DAYS.between(startDate, finishDate).toInt() + 1) { index: Int ->
                                    val indexDate = startDate.plusDays(index.toLong())
                                    val filteredWiDListByDate = wiDList.filter { it.date == indexDate }

                                    PeriodBasedPieChartFragment(date = indexDate, wiDList = filteredWiDListByDate)
                                }
                            }
                        }
                    } else { // 제목이 "전체"가 아닐 때
                        LineChartFragment(wiDList = filteredWiDListByTitle, startDate = startDate, finishDate = finishDate)
                    }
                }
            }

            item {
                if (selectedTitle == titlesWithAll[0]) {
                    // 합계
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "합계 기록",
                            style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.black_han_sans_regular)))
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (totalDurationMap.isEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(vertical = 32.dp)
                                                .scale(0.8f),
                                            painter = painterResource(id = R.drawable.outline_textsms_24),
                                            contentDescription = "No week total",
                                            tint = Color.Gray
                                        )

                                        Text(
                                            modifier = Modifier
                                                .padding(vertical = 32.dp),
                                            text = "표시할 합계 기록이 없습니다.",
                                            style = TextStyle(color = Color.Gray)
                                        )
                                    }
                                } else {
                                    for ((title, totalDuration) in totalDurationMap) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(text = titleMap[title] ?: title)

                                                Box(
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .size(10.dp)
                                                        .background(
                                                            color = colorResource(
                                                                id = colorMap[title]
                                                                    ?: R.color.light_gray
                                                            )
                                                        )
                                                )
                                            }

                                            Text(text = formatDuration(totalDuration, mode = 2))
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("각 제목별 데이터")
                }
            }
        }

        // 기간 표시 및 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = when (selectedPeriod) {
                    periods[0] -> getWeekString(firstDayOfWeek = startDate, lastDayOfWeek = finishDate)
                    periods[1] -> getMonthString(date = startDate)
                    else -> buildAnnotatedString { append("") } // 다른 경우에 대한 처리 추가
                },
                overflow = TextOverflow.Ellipsis
            )

            Row{
                IconButton(
                    onClick = {
                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = getFirstDayOfWeek(today)
                                finishDate = getLastDayOfWeek(today)
                            }
                            periods[1] -> {
                                startDate = getFirstDayOfMonth(today)
                                finishDate = getLastDayOfMonth(today)
                            }
                        }

//                        wiDList = wiDService.readWiDListByDateRange(startDate = startDate, finishDate = finishDate)
//                        totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

//                        filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }
                    },
                    enabled = when (selectedPeriod) {
                        periods[0] -> !(startDate == getFirstDayOfWeek(today) && finishDate == getLastDayOfWeek(today))
                        periods[1] -> !(startDate == getFirstDayOfMonth(today) && finishDate == getLastDayOfMonth(today))
                        else -> false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset period"
                    )
                }

                IconButton(
                    onClick = {
                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = startDate.minusWeeks(1)
                                finishDate = finishDate.minusWeeks(1)
                            }
                            periods[1] -> {
                                startDate = getFirstDayOfMonth(startDate.minusDays(15))
                                finishDate = getLastDayOfMonth(finishDate.minusDays(45))
                            }
                        }

//                        wiDList = wiDService.readWiDListByDateRange(startDate = startDate, finishDate = finishDate)
//                        totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

//                        filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous period"
                    )
                }

                IconButton(
                    onClick = {
                        when (selectedPeriod) {
                            periods[0] -> {
                                startDate = startDate.plusWeeks(1)
                                finishDate = finishDate.plusWeeks(1)
                            }
                            periods[1] -> {
                                startDate = getFirstDayOfMonth(startDate.plusDays(45))
                                finishDate = getLastDayOfMonth(finishDate.plusDays(15))
                            }
                        }

//                        wiDList = wiDService.readWiDListByDateRange(startDate = startDate, finishDate = finishDate)
//                        totalDurationMap = getTotalDurationMapByTitle(wiDList = wiDList)

//                        filteredWiDListByTitle = wiDList.filter { it.title == selectedTitle }
                    },
                    enabled = when (selectedPeriod) {
                        periods[0] -> !(startDate == getFirstDayOfWeek(today) && finishDate == getLastDayOfWeek(today))
                        periods[1] -> !(startDate == getFirstDayOfMonth(today) && finishDate == getLastDayOfMonth(today))
                        else -> false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next period"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PeriodBasedFragmentPreview() {
    PeriodBasedFragment()
}