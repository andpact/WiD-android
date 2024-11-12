package andpact.project.wid.view

import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.MyTitleViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Duration

@Composable
fun MyTitleView(myTitleViewModel: MyTitleViewModel = hiltViewModel()) {
    val TAG = "MyTitleView"

//    val wiDTitleCountMap = myTitleViewModel.user.value?.wiDTitleCountMap ?: defaultTitleCountMap
//    val totalTitleCount = wiDTitleCountMap.values.sum()
//    val wiDTitleDurationMap = myTitleViewModel.user.value?.wiDTitleDurationMap ?: defaultTitleDurationMap
//    val totalTitleDuration = wiDTitleDurationMap.values.sumOf { it.seconds }

// String 키를 Title 타입으로 변경한 맵 생성
    val tmpWiDTitleCountMap: Map<Title, Int> = mapOf(
        Title.STUDY to 3,
        Title.WORK to 8,
        Title.EXERCISE to 2,
        Title.HOBBY to 4,
        Title.RELAXATION to 6,
        Title.MEAL to 7,
        Title.TRAVEL to 1,
        Title.CLEANING to 3,
        Title.HYGIENE to 9,
        Title.SLEEP to 5
    )
    val totalCount = tmpWiDTitleCountMap.values.sum()

    val tmpWiDTitleDurationMap: Map<Title, Duration> = mapOf(
        Title.STUDY to Duration.ofHours(9L).plusMinutes(47L).plusSeconds(55L),
        Title.WORK to Duration.ofHours(8L).plusMinutes(19L).plusSeconds(36L),
        Title.EXERCISE to Duration.ofHours(1L).plusMinutes(50L).plusSeconds(40L),
        Title.HOBBY to Duration.ofHours(10L).plusMinutes(57L).plusSeconds(55L),
        Title.RELAXATION to Duration.ofHours(4L).plusMinutes(24L).plusSeconds(10L),
        Title.MEAL to Duration.ofHours(10L).plusMinutes(59L).plusSeconds(19L),
        Title.TRAVEL to Duration.ofHours(1L).plusMinutes(41L).plusSeconds(39L),
        Title.CLEANING to Duration.ofHours(1L).plusMinutes(10L).plusSeconds(33L),
        Title.HYGIENE to Duration.ofHours(7L).plusMinutes(41L).plusSeconds(42L),
        Title.SLEEP to Duration.ofHours(8L).plusMinutes(41L).plusSeconds(48L),
    )
    val totalDuration: Duration = tmpWiDTitleDurationMap.values
        .sumOf { it.seconds }
        .let { Duration.ofSeconds(it) }

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = "개수 순위",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        tmpWiDTitleCountMap.onEachIndexed { index: Int, (title: Title, count: Int) ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                    )

                    Column {
                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 4.dp),
                            text = title.kr,
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 8.dp),
                            text = "${count}개",
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = getCountPercentageString(
                            count = count,
                            totalCount = totalCount
                        ),
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (index < tmpWiDTitleCountMap.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = (16 + 40 + 8).dp, end = 16.dp),
                        thickness = 0.5.dp
                    )
                }
            }
        }

        item {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = "시간 순위",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        tmpWiDTitleDurationMap.onEachIndexed { index: Int, (title: Title, duration: Duration) ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = Typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                    )

                    Column {
                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 4.dp),
                            text = title.kr,
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 8.dp),
                            text = getDurationString(duration = duration),
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = getDurationPercentageStringOfTotalDuration(
                            duration = duration,
                            totalDuration = totalDuration
                        ),
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (index < tmpWiDTitleDurationMap.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = (16 + 40 + 8).dp, end = 16.dp),
                        thickness = 0.5.dp
                    )
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
        }
    }
}

//@Composable
//@Preview(showBackground = true)
//fun MyTitlePreview() {
//
//}