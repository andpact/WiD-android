package andpact.project.wid.tmp

import andpact.project.wid.model.ChartData
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.getDurationString
import andpact.project.wid.util.titleColorMap
import andpact.project.wid.util.titleKRMap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Duration

//@Composable
//fun WiDTitleDurationMapHorizontalBarChartView(
//    titleToDurationMap: Map<String, Duration>,
//    modifier: Modifier = Modifier
//) {
//    val TAG = "WiDTitleDurationMapHorizontalBarChartView"
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//        onDispose { Log.d(TAG, "disposed") }
//    }
//
//    val maxDuration = titleToDurationMap.values.maxByOrNull { it.seconds } ?: Duration.ZERO
//    val chartDataList = titleToDurationMap.map { ChartData(it.key, it.value) }
//        .sortedByDescending { it.duration.seconds }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 4.dp)
//        ) {
//            Text(
//                modifier = Modifier
//                    .width(50.dp),
//                text = "제목",
//                style = Typography.bodyMedium,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(
//                modifier = Modifier
//                    .weight(1f)
//            )
//
//            Text(
//                modifier = Modifier
//                    .width(50.dp),
//                text = "시간",
//                style = Typography.bodyMedium,
//                textAlign = TextAlign.Center
//            )
//        }
//
//        chartDataList.forEach { data ->
//            val fraction = data.duration.seconds.toFloat() / maxDuration.seconds.toFloat()
//
//            if (0.01f <= fraction) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .width(50.dp),
//                        text = titleKRMap[data.title] ?: "",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer,
//                        textAlign = TextAlign.Center
//                    )
//
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth(fraction)
//                                .height(24.dp)
//                                .background(
//                                    color = titleColorMap[data.title]
//                                        ?: MaterialTheme.colorScheme.secondaryContainer,
//                                    shape = MaterialTheme.shapes.extraSmall
//                                )
//                        )
//                    }
//
//                    Text(
//                        modifier = Modifier
//                            .width(50.dp),
//                        text = getDurationString(data.duration, mode = 1),
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer,
//                        textAlign = TextAlign.Center,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//        }
//    }
//}