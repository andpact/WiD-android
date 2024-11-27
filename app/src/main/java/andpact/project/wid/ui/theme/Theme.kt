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
 * 안드로이드 및 iOS 모두 다크 모드일 때 배경, 텍스트, 아이콘의 색을 자동으로 변경해줌.
 */
private val LightColorScheme = lightColorScheme(
    primary = light_primary,                             // 1
    onPrimary = light_on_primary,                        // 2
    primaryContainer = light_primary_container,          // 3
    onPrimaryContainer = light_on_primary_container,     // 4
    inversePrimary = light_inverse_primary,              // 5
    secondary = light_secondary,                         // 6
    onSecondary = light_on_secondary,                    // 7
    secondaryContainer = light_secondary_container,      // 8 ("기록 없음" 색상)
    onSecondaryContainer = light_on_secondary_container, // 9
    tertiary = light_tertiary,                           // 10
    onTertiary = light_on_tertiary,                      // 11
    tertiaryContainer = light_tertiary_container,        // 12
    onTertiaryContainer = light_on_tertiary_container,   // 13
    surface = light_surface,                             // 14
    onSurface = light_on_surface,                        // 15
    onSurfaceVariant = light_on_surface_variant,         // 16
    inverseSurface = light_inverse_surface,              // 17
    inverseOnSurface = light_inverse_on_surface,         // 18
    error = light_error,                                 // 19
    onError = light_on_error,                            // 20
    errorContainer = light_error_container,              // 21
    onErrorContainer = light_on_error_container,         // 22
    outline = light_outline,                             // 23
    outlineVariant = light_outline_variant,              // 24
    scrim = light_scrim,                                 // 25
    surfaceBright = light_surface_bright,                // 26
    surfaceContainer = light_surface_container,          // 27
    surfaceContainerHigh = light_surface_container_high, // 28
    surfaceContainerHighest = light_surface_container_highest, // 29
    surfaceContainerLow = light_surface_container_low,   // 30
    surfaceContainerLowest = light_surface_container_lowest, // 31
    surfaceDim = light_surface_dim                       // 32
)

private val DarkColorScheme = darkColorScheme(
    primary = dark_primary,                             // 1
    onPrimary = dark_on_primary,                        // 2
    primaryContainer = dark_primary_container,          // 3
    onPrimaryContainer = dark_on_primary_container,     // 4
    inversePrimary = dark_inverse_primary,              // 5
    secondary = dark_secondary,                         // 6
    onSecondary = dark_on_secondary,                    // 7
    secondaryContainer = dark_secondary_container,      // 8
    onSecondaryContainer = dark_on_secondary_container, // 9
    tertiary = dark_tertiary,                           // 10
    onTertiary = dark_on_tertiary,                      // 11
    tertiaryContainer = dark_tertiary_container,        // 12
    onTertiaryContainer = dark_on_tertiary_container,   // 13
    surface = dark_surface,                             // 14
    onSurface = dark_on_surface,                        // 15
    onSurfaceVariant = dark_on_surface_variant,         // 16
    inverseSurface = dark_inverse_surface,              // 17
    inverseOnSurface = dark_inverse_on_surface,         // 18
    error = dark_error,                                 // 19
    onError = dark_on_error,                            // 20
    errorContainer = dark_error_container,              // 21
    onErrorContainer = dark_on_error_container,         // 22
    outline = dark_outline,                             // 23
    outlineVariant = dark_outline_variant,              // 24
    scrim = dark_scrim,                                 // 25
    surfaceBright = dark_surface_bright,                // 26
    surfaceContainer = dark_surface_container,          // 27
    surfaceContainerHigh = dark_surface_container_high, // 28
    surfaceContainerHighest = dark_surface_container_highest, // 29
    surfaceContainerLow = dark_surface_container_low,   // 30
    surfaceContainerLowest = dark_surface_container_lowest, // 31
    surfaceDim = dark_surface_dim                       // 32
)

@Composable
fun WiDTheme( // 메인 액티비티에 적용되는 테마
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true, // 다이나믹 컬러는 휴대폰 배경화면의 색상에 따라 앱의 색상을 자동으로 변경해주는 걸 말함.
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
            // 상태 표시줄 + 탐색 메뉴 = 시스템 표시줄

//            (view.context as Activity).window.statusBarColor = colorScheme.surface.toArgb() // 상태 표시줄
            (view.context as Activity).window.statusBarColor = colorScheme.secondaryContainer.toArgb() // 상태 표시줄 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = !darkTheme // 상태 표시줄 의 컨텐츠를 어둡게 함.
//
            (view.context as Activity).window.navigationBarColor = colorScheme.surface.toArgb() // 탐색 메뉴 색상
//            (view.context as Activity).window.navigationBarColor = colorScheme.surfaceContainer.toArgb() // 탐색 메뉴 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = !darkTheme // 탐색 메뉴의 컨텐츠를 어둡게 함.
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
fun SplashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            // 상태 표시줄 + 탐색 메뉴 = 시스템 표시줄

            (view.context as Activity).window.statusBarColor = colorScheme.surface.toArgb() // 상태 표시줄
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = !darkTheme // 상태 표시줄 의 컨텐츠를 어둡게 함.
//
            (view.context as Activity).window.navigationBarColor = colorScheme.surface.toArgb() // 탐색 메뉴 색상
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = !darkTheme // 탐색 메뉴의 컨텐츠를 어둡게 함.
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
fun changeStatusBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = color.toArgb()
        }
    }
}

@Composable
fun changeNavigationBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.navigationBarColor = color.toArgb()
        }
    }
}


//@Composable
//fun SplashTheme( // 스플래쉬 액티비티에 적용되는 테마
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
////        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
////            val context = LocalContext.current
////            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
////        }
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.secondary.toArgb() // 상태 바 색상
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = !darkTheme // 상태바 의 컨텐츠를 어둡게 함.
//
//            (view.context as Activity).window.navigationBarColor = colorScheme.secondary.toArgb() // 네비게이션 바 색상
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars = !darkTheme // 네비게이션 바 의 컨텐츠를 어둡게 함.
//        }
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}