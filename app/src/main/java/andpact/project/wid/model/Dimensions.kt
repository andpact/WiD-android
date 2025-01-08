package andpact.project.wid.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 패딩에서 자주 사용되는 값 4, 8, 16
// 높이에서 자주 사용되는 값 56, 64
// 사이즈에서 자주 사용되는 값 24, 40
data class Dimensions(
    val paddingExtraSmall: Dp = 4.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,

    val listItemHeight: Dp = 72.dp,
    val listItemImageSize: Dp = 56.dp,

    val iconSize: Dp = 24.dp,
    val buttonSize: Dp = 48.dp,
)
