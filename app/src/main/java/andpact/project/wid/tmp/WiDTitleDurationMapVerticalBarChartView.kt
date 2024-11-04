package andpact.project.wid.tmp

import andpact.project.wid.ui.theme.Transparent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Duration

//@Composable
//fun WiDTitleDurationMapVerticalBarChartView(
//    titleToDurationMap: Map<String, Duration>,
//    modifier: Modifier = Modifier
//) {
//    val TAG = "WiDTitleDurationMapVerticalBarChartView"
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//        onDispose { Log.d(TAG, "disposed") }
//    }
//
//    val maxDuration = titleToDurationMap.values.maxByOrNull { it.seconds } ?: Duration.ZERO
//    val sortedTitleToDurationMap = titleToDurationMap.entries.sortedByDescending { it.value.seconds }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(2f / 1f), // 가로 2 : 세로 1 비율
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // 소요 시간 표시
//            Column(
//                modifier = Modifier
//                    .fillMaxHeight(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                val interval = maxDuration.seconds / 4 // 5개의 레이블 표시하기 위해 4등분
//
//                // 소요 시간 레이블 5개 (초 단위)
//                for (i in 4 downTo 0) { // 4부터 0까지 거꾸로 루프
//                    val duration = Duration.ofSeconds(interval * i)
//                    Text(
//                        text = getDurationString(duration, mode = 1),
//                        style = Typography.bodyMedium,
//                        textAlign = TextAlign.Center,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                }
//            }
//
//            sortedTitleToDurationMap.forEach { (title: String, duration: Duration) ->
//                Column(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight()
//                        .padding(vertical = 7.dp),
//                    verticalArrangement = Arrangement.Bottom
//                ) {
//                    /** fraction이 0.01 미만이면 안되지 않나? */
//                    val fraction = duration.seconds.toFloat() / maxDuration.seconds.toFloat()
//
//                    // 막대 표시
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight(fraction = fraction)
//                            .padding(horizontal = 5.dp)
//                            .background(
//                                color = titleColorMap[title]
//                                    ?: MaterialTheme.colorScheme.secondaryContainer,
//                                shape = MaterialTheme.shapes.extraSmall
//                            )
//                    )
//                }
//            }
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            // 더미 텍스트 (투명)
//            Text(
//                text = getDurationString(maxDuration, mode = 1),
//                style = Typography.bodyMedium,
//                textAlign = TextAlign.Center,
//                color = Transparent
//            )
//
//            // 제목 표시
//            sortedTitleToDurationMap.forEach { (title, _) ->
//                Text(
//                    modifier = Modifier
//                        .weight(1f),
//                    text = titleKRMap[title] ?: "",
//                    style = Typography.bodyMedium,
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TitleDurationMapVerticalBarChartPreview() {
//    val tmpTitleToDurationMap: Map<String, Duration> = mapOf(
//        "0" to Duration.ofHours(8L).plusMinutes(41L).plusSeconds(48L),
//        "1" to Duration.ofHours(9L).plusMinutes(47L).plusSeconds(55L),
//        "2" to Duration.ofHours(8L).plusMinutes(19L).plusSeconds(36L),
//        "3" to Duration.ofHours(1L).plusMinutes(50L).plusSeconds(40L),
//        "4" to Duration.ofHours(10L).plusMinutes(57L).plusSeconds(55L),
//        "5" to Duration.ofHours(4L).plusMinutes(24L).plusSeconds(10L),
//        "6" to Duration.ofHours(10L).plusMinutes(59L).plusSeconds(19L),
//        "7" to Duration.ofHours(1L).plusMinutes(41L).plusSeconds(39L),
//        "8" to Duration.ofHours(1L).plusMinutes(10L).plusSeconds(33L),
//        "9" to Duration.ofHours(7L).plusMinutes(41L).plusSeconds(42L)
////        "0" to Duration.ZERO, // 공부 (Studying) - STUDY
////        "1" to Duration.ZERO, // 일 (Working) - WORK
////        "2" to Duration.ZERO, // 운동 (Exercising) - EXERCISE
////        "3" to Duration.ZERO, // 취미 (Hobbies) - HOBBY
////        "4" to Duration.ZERO, // 휴식 (Relaxation) - RELAXATION
////        "5" to Duration.ZERO, // 식사 (Eating) - MEAL
////        "6" to Duration.ZERO, // 이동 (Commuting/Traveling) - TRAVEL
////        "7" to Duration.ZERO, // 정리 (Cleaning) - CLEANING
////        "8" to Duration.ZERO, // 위생 (Hygiene) - HYGIENE
////        "9" to Duration.ZERO  // 수면 (Sleeping) - SLEEP
//    )
//
//    WiDTitleDurationMapVerticalBarChartView(titleToDurationMap = tmpTitleToDurationMap)
//}