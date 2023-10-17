package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.service.WiDService
import andpact.project.wid.util.colorMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun OpacityChartView(date: LocalDate, title: String) {
    val wiDService = WiDService(context = LocalContext.current)
    val wiDList = wiDService.readDailyWiDListByDate(date, title)

    val totalDurationMillis = wiDList.sumOf { it.duration.toMillis() }
    val maxDurationMillis = 10 * 60 * 60 * 1000

    val opacity = when {
        totalDurationMillis == 0L -> 0.0f
        totalDurationMillis <= maxDurationMillis / 5 -> 0.2f
        totalDurationMillis <= 2 * maxDurationMillis / 5 -> 0.4f
        totalDurationMillis <= 3 * maxDurationMillis / 5 -> 0.6f
        totalDurationMillis <= 4 * maxDurationMillis / 5 -> 0.8f
        else -> 1.0f
    }

    val titleColorId = colorMap[title]
    val backgroundColor = titleColorId?.let { id ->
        Color(ContextCompat.getColor(LocalContext.current, id))
    } ?: colorResource(id = R.color.light_gray)

    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .padding(4.dp)
        .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(8.dp))
        .background(
            color = backgroundColor.copy(alpha = opacity),
            shape = RoundedCornerShape(8.dp)
        )
    ) {
        Text(modifier = Modifier.align(Alignment.Center),
            text = date.format(DateTimeFormatter.ofPattern("d")),
            style = TextStyle(fontSize = 12.sp)
        )
    }
}
