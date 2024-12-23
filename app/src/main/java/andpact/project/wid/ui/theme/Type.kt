package andpact.project.wid.ui.theme

import andpact.project.wid.R
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val acmeRegular = FontFamily(
    Font(R.font.acme_regular)
)

val pretendardRegular = FontFamily(
    Font(R.font.pretendard_regular)
)

val pretendardSemiBold = FontFamily(
    Font(R.font.pretendard_semi_bold)
)

val pretendardExtraBold = FontFamily(
    Font(R.font.pretendard_extra_bold)
)

val chivoMonoBlackItalic = FontFamily(
    Font(R.font.chivo_mono_black_italic)
)

// Set of Material typography styles to start with
//

/**
    Title - ExtraBold
    Body - SemiBold
    Label - Regular

    Large - 18.dp
    Medium - 16.dp
    Small - 14.dp

    자주 안쓰이는 스타일은 직접 설정함.
 */
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = pretendardRegular,
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