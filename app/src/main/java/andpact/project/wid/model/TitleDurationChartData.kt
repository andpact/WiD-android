package andpact.project.wid.model

import andpact.project.wid.util.Title
import java.time.Duration

data class TitleDurationChartData (
    val title: Title,
    val duration: Duration
)