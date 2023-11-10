package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.WiD
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
fun OpacityChartView(date: LocalDate, wiDList: List<WiD>) {
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

    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .padding(4.dp)
        .background(
            color = if (wiDList.isEmpty()) {
                Color.Unspecified
            } else {
                colorResource(id = colorMap[wiDList[0].title] ?: R.color.light_gray).copy(alpha = opacity)
            },
        )
    ) {
        Text(modifier = Modifier.align(Alignment.Center),
            text = date.format(DateTimeFormatter.ofPattern("d")),
            style = TextStyle(fontSize = 12.sp)
        )
    }
}