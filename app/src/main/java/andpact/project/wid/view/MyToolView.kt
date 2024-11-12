package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.MyToolViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Duration

@Composable
fun MyToolView(myToolViewModel: MyToolViewModel = hiltViewModel()) {
    val TAG = "MyToolView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    /** 내림차순으로 정렬해야 될 수도? */
//    val wiDToolCountMap = myToolViewModel.user.value?.wiDToolCountMap ?: defaultToolCountMap
//    val totalWiDToolCount = tmpWiDToolCountMap.values.sum()
//    val wiDToolDurationMap = myToolViewModel.user.value?.wiDToolDurationMap ?: defaultToolDurationMap
//    val totalWiDToolDuration: Duration = wiDToolDurationMap.values
//        .sumOf { it.seconds }
//        .let { Duration.ofSeconds(it) }

    val tmpWiDToolCountMap: Map<CurrentTool, Int> = mapOf(
        CurrentTool.STOPWATCH to 5,
        CurrentTool.TIMER to 3,
        CurrentTool.LIST to 8,
    )

    val totalCount = tmpWiDToolCountMap.values.sum()

    val tmpWiDToolDurationMap: Map<CurrentTool, Duration> = mapOf(
        CurrentTool.STOPWATCH to Duration.ofHours(8L).plusMinutes(41L).plusSeconds(48L),
        CurrentTool.TIMER to Duration.ofHours(9L).plusMinutes(47L).plusSeconds(55L),
        CurrentTool.LIST to Duration.ofHours(8L).plusMinutes(19L).plusSeconds(36L),
    )

    val totalDuration: Duration = tmpWiDToolDurationMap.values
        .sumOf { it.seconds }
        .let { Duration.ofSeconds(it) }

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

        tmpWiDToolCountMap.onEachIndexed { index: Int, (currentTool: CurrentTool, count: Int) ->
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
                            text = currentTool.kr,
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

                if (index < tmpWiDToolCountMap.size - 1) {
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

        tmpWiDToolDurationMap.onEachIndexed { index: Int, (currentTool: CurrentTool, duration: Duration) ->
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
                            text = currentTool.kr,
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 8.dp),
                            text = getDurationString(duration = totalDuration),
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

                if (index < tmpWiDToolDurationMap.size - 1) {
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