package andpact.project.wid.tmp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalTime

//@Composable
//fun HomeViewHorizontalBarChartView(
//    now: LocalTime,
//    modifier: Modifier = Modifier
//) {
//    val hourProgress = now.hour / 24f
//    val minuteProgress = now.minute / 60f
//    val secondProgress = now.second / 60f
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                modifier = Modifier
//                    .width(60.dp),
//                text = "${now.hour}시",
//                style = MaterialTheme.typography.titleLarge
//            )
//
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(16.dp),
//                progress = hourProgress,
//                color = Color.Blue,
//                strokeCap = StrokeCap.Round
//            )
//
//            Text(
//                modifier = Modifier
//                    .width(60.dp),
//                text = "${(hourProgress * 100).toInt()}%",
//                style = MaterialTheme.typography.titleLarge,
//                textAlign = TextAlign.Right
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                modifier = Modifier
//                    .width(60.dp),
//                text = "${now.minute}분",
//                style = MaterialTheme.typography.titleLarge
//            )
//
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(16.dp),
//                progress = minuteProgress,
//                color = Color.Green,
//                strokeCap = StrokeCap.Round
//            )
//
//            Text(
//                modifier = Modifier
//                    .width(60.dp),
//                text = "${(minuteProgress * 100).toInt()}%",
//                style = MaterialTheme.typography.titleLarge,
//                textAlign = TextAlign.Right
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                modifier = Modifier
//                    .width(60.dp),
//                text = "${now.second}초",
//                style = MaterialTheme.typography.titleLarge
//            )
//
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(16.dp),
//                progress = secondProgress,
//                color = Color.Red,
//                strokeCap = StrokeCap.Round
//            )
//
//            Text(
//                modifier = Modifier
//                    .width(60.dp),
//                text = "${(secondProgress * 100).toInt()}%",
//                style = MaterialTheme.typography.titleLarge,
//                textAlign = TextAlign.Right
//            )
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun HomeViewHorizontalBarChartPreview() {
//    val now = LocalTime.now()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        HomeViewHorizontalBarChartView(now = now)
//    }
//}