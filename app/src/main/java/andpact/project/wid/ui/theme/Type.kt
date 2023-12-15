package andpact.project.wid.ui.theme

import andpact.project.wid.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val acmeRegular = FontFamily(
    Font(R.font.acme_regular)
)

val agbalumoRegular = FontFamily(
    Font(R.font.agbalumo_regular)
)

val pretendardThin = FontFamily(
    Font(R.font.pretendard_thin)
)

val pretendardExtraLight = FontFamily(
    Font(R.font.pretendard_extra_light)
)

val pretendardLight = FontFamily(
    Font(R.font.pretendard_light)
)

val pretendardRegular = FontFamily(
    Font(R.font.pretendard_regular)
)

val pretendardMedium = FontFamily(
    Font(R.font.pretendard_medium)
)

val pretendardSemiBold = FontFamily(
    Font(R.font.pretendard_semi_bold)
)

val pretendardBold = FontFamily(
    Font(R.font.pretendard_bold)
)

val pretendardExtraBold = FontFamily(
    Font(R.font.pretendard_extra_bold)
)

val pretendardBlack = FontFamily(
    Font(R.font.pretendard_black)
)

val pyeongChangPeaceBold = FontFamily(
    Font(R.font.pyeong_chang_peace_bold)
)

// Set of Material typography styles to start with
//
/*
    Title - Bold
    Body - Medium
    Label - Light

    Large - 18.dp
    Medium - 16.dp
    Small - 14.dp

    자주 안쓰이는 스타일은 직접 설정함.
 */
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = pretendardLight,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = pretendardLight,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = pretendardLight,
        fontSize = 14.sp
    ),

    /* Other default text styles to override
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)