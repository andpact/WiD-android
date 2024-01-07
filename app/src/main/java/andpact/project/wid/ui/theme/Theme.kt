package andpact.project.wid.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

/**
 * 라이트 모드, 다크 모드에서 변경되어야 하는 색상을 아래에 작성함. 변경 없는 색은 직접 가져가서 사용함.
 * primary   -> Black-White
 * secondary -> White-Black
 * tertiary  -> LightGray-Gray
 *
 * 안드로이드 및 iOS 모두 다크 모드일 때 배경, 텍스트, 아이콘의 색을 자동으로 변경해줌.
 * 하지만 안드로이드는 라이트 모드, 다크 모드에 적용되는 기본 색이 흰 색, 검은 색이 아니기 때문에 명시적으로 흰 색과 검은 색을 지정해줌.
 */
private val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = White,
    tertiary = LightGray
)

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = Black,
    tertiary = Gray
)

@Composable
fun WiDTheme( // 메인 액티비티에 적용되는 테마
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> { // 기존에 있던 코드인데, 이 코드있으니까 다크, 라이트모드 컬러 적용이 안되네.
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme

            (view.context as Activity).window.statusBarColor = colorScheme.secondary.toArgb() // 상태 바 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = !darkTheme // 상태바 의 컨텐츠를 어둡게 함.

            (view.context as Activity).window.navigationBarColor = colorScheme.secondary.toArgb() // 네비게이션 바 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = !darkTheme // 네비게이션 바 의 컨텐츠를 어둡게 함.
        }
    }

    // WiD 테마가 Material 테마를 감싸고 있음.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SplashTheme( // 스플래쉬 액티비티에 적용되는 테마
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.secondary.toArgb() // 상태 바 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = !darkTheme // 상태바 의 컨텐츠를 어둡게 함.

            (view.context as Activity).window.navigationBarColor = colorScheme.secondary.toArgb() // 네비게이션 바 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = !darkTheme // 네비게이션 바 의 컨텐츠를 어둡게 함.
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}