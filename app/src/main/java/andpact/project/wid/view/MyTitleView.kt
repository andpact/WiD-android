package andpact.project.wid.view

import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.*
import andpact.project.wid.viewModel.MyTitleViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val wiDTitleCountMap = myTitleViewModel.user.value?.wiDTitleCountMap ?: defaultTitleCountMap
    val wiDTitleDurationMap = myTitleViewModel.user.value?.wiDTitleDurationMap ?: defaultTitleDurationMap

    val tmpWiDTitleCountMap: Map<String, Int> = mapOf(
        "0" to 5,
        "1" to 3,
        "2" to 8,
        "3" to 2,
        "4" to 4,
        "5" to 6,
        "6" to 7,
        "7" to 1,
        "8" to 3,
        "9" to 9
    )

    val tmpWiDTitleDurationMap: Map<String, Duration> = mapOf(
        "0" to Duration.ofHours(8L).plusMinutes(41L).plusSeconds(48L),
        "1" to Duration.ofHours(9L).plusMinutes(47L).plusSeconds(55L),
        "2" to Duration.ofHours(8L).plusMinutes(19L).plusSeconds(36L),
        "3" to Duration.ofHours(1L).plusMinutes(50L).plusSeconds(40L),
        "4" to Duration.ofHours(10L).plusMinutes(57L).plusSeconds(55L),
        "5" to Duration.ofHours(4L).plusMinutes(24L).plusSeconds(10L),
        "6" to Duration.ofHours(10L).plusMinutes(59L).plusSeconds(19L),
        "7" to Duration.ofHours(1L).plusMinutes(41L).plusSeconds(39L),
        "8" to Duration.ofHours(1L).plusMinutes(10L).plusSeconds(33L),
        "9" to Duration.ofHours(7L).plusMinutes(41L).plusSeconds(42L)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
//        tmpWiDTitleCountMap.onEachIndexed { index: Int, (title: String, totalCount: Int) ->
//            item {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .width(40.dp)
//                            .aspectRatio(1f / 1f)
//                            .background(
//                                color = MaterialTheme.colorScheme.secondaryContainer,
//                                shape = MaterialTheme.shapes.medium
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "${index + 1}",
//                            style = Typography.titleLarge,
//                            color = MaterialTheme.colorScheme.onSecondaryContainer
//                        )
//                    }
//
//                    Spacer(
//                        modifier = Modifier
//                            .width(8.dp)
//                    )
//
//                    Column {
//                        Text(
//                            modifier = Modifier
//                                .padding(top = 8.dp, bottom = 4.dp),
//                            text = titleKRMap[title] ?: "기록 없음",
//                            style = Typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Text(
//                            modifier = Modifier
//                                .padding(top = 4.dp, bottom = 8.dp),
//                            text = "$totalCount",
//                            style = Typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//
//                    Spacer(
//                        modifier = Modifier
//                            .weight(1f)
//                    )
//
//                    Text(
//                        modifier = Modifier
//                            .background(
//                                color = MaterialTheme.colorScheme.secondaryContainer,
//                                shape = MaterialTheme.shapes.medium
//                            )
//                            .padding(horizontal = 8.dp, vertical = 4.dp),
//                        text = "dd",
//                        style = Typography.bodyLarge,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                }
//            }
//        }

        tmpWiDTitleDurationMap.onEachIndexed { index: Int, (title: String, totalDuration: Duration) ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .aspectRatio(1f / 1f)
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
                            text = titleKRMap[title] ?: "기록 없음",
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
                        text = "${"%.1f".format(getDurationPercentageStringOfDay(totalDuration))}%",
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MyTitlePreview() {

}