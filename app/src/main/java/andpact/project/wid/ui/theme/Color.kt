package andpact.project.wid.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * xml 파일에서는 xml 컬러 파일에만 접근 가능하고,
 * 코틀린 파일에서는 코틀린 컬러 파일과 xml 컬러 파일 모두에 접근이 가능하다.
 *
 * 한 가지 색을 라이트/다크 모두에서 동일하게 사용하는 게 아니라,
 * 모든 색을 색조(Tone)으로 분류하고, 라이트 모드는 약간 어두운 색, 다크 모드는 약간 밝은 색으로 대응.
 */
// Primary
val light_primary = Color(0xFF4b5c92)
val dark_primary = Color(0xFFb4c5ff)
val light_on_primary = Color(0xFFffffff)
val dark_on_primary = Color(0xFF1a2d60)
val light_primary_container = Color(0xFFdbe1ff)
val dark_primary_container = Color(0xFF334478)
val light_on_primary_container = Color(0xFF00174b)
val dark_on_primary_container = Color(0xFFdbe1ff)
val light_inverse_primary = Color(0xFFb4c5ff)
val dark_inverse_primary = Color(0xFF4b5c92)
// Secondary
val light_secondary = Color(0xFF595e72)
val dark_secondary = Color(0xFFc1c5dd)
val light_on_secondary = Color(0xFFffffff)
val dark_on_secondary = Color(0xFF2b3042)
val light_secondary_container = Color(0xFFdde1f9)
val dark_secondary_container = Color(0xFF414659)
val light_on_secondary_container = Color(0xFF161b2c)
val dark_on_secondary_container = Color(0xFFdde1f9)
// Tertiary
val light_tertiary = Color(0xFF745470)
val dark_tertiary = Color(0xFFe2bbdb)
val light_on_tertiary = Color(0xFFffffff)
val dark_on_tertiary = Color(0xFF422740)
val light_tertiary_container = Color(0xFFffd6f8)
val dark_tertiary_container = Color(0xFF5a3d58)
val light_on_tertiary_container = Color(0xFF2b122b)
val dark_on_tertiary_container = Color(0xFFffd6f8)
// Error
val light_error = Color(0xFFba1a1a)
val dark_error = Color(0xFFffb4ab)
val light_on_error = Color(0xFFffffff)
val dark_on_error = Color(0xFF690005)
val light_error_container = Color(0xFFffdad6)
val dark_error_container = Color(0xFF93000a)
val light_on_error_container = Color(0xFF410002)
val dark_on_error_container = Color(0xFFffdad6)
// Surface
val light_surface_dim = Color(0xFFdad9e0)
val dark_surface_dim = Color(0xFF121318)
val light_surface = Color(0xFFfaf8ff)
val dark_surface = Color(0xFF121318)
val light_surface_bright = Color(0xFFfaf8ff)
val dark_surface_bright = Color(0xFF38393f)
val light_surface_container_lowest = Color(0xFFffffff)
val dark_surface_container_lowest = Color(0xFF0d0e13)
val light_surface_container_low = Color(0xFFf4f3fa)
val dark_surface_container_low = Color(0xFF1a1b21)
val light_surface_container = Color(0xFFeeedf4)
val dark_surface_container = Color(0xFF1e1f25)
val light_surface_container_high = Color(0xFFe8e7ef)
val dark_surface_container_high = Color(0xFF292a2f)
val light_surface_container_highest = Color(0xFFe3e2e9)
val dark_surface_container_highest = Color(0xFF34343a)
val light_inverse_surface = Color(0xFF2f3036)
val dark_inverse_surface = Color(0xFFe3e2e9)
val light_inverse_on_surface = Color(0xFFf1f0f7)
val dark_inverse_on_surface = Color(0xFF2f3036)
val light_on_surface = Color(0xFF1a1b21)
val dark_on_surface = Color(0xFFe3e2e9)
val light_on_surface_variant = Color(0xFF45464f)
val dark_on_surface_variant = Color(0xFFc5c6d0)
// Outline
val light_outline = Color(0xFF757680)
val dark_outline = Color(0xFF8f909a)
val light_outline_variant = Color(0xFFc5c6d0)
val dark_outline_variant = Color(0xFF45464f)
// Etc
val light_scrim = Color(0xFF000000)
val dark_scrim = Color(0xFF000000)
val light_shadow = Color(0xFF000000) // 사용 안되네?
val dark_shadow = Color(0xFF000000) // 사용 안하네





val Black = Color(0xFF000000)
val Gray = Color(0xFF1F1F1F)
val DarkGray = Color(0xFF7E7E7E)
val LightGray = Color(0xFFDFDFDF)
val White = Color(0xFFFFFFFF)
val Transparent = Color(0x00FFFFFF)

val DeepSkyBlue = Color(0xFF00BFFF)     // 진한 하늘색(파란색 버튼용)
val OrangeRed = Color(0xFFFF4500)       // 붉은 색(빨간 버튼용)
val LimeGreen = Color(0xFF32CD32)       // 라임 녹색(초록 버튼용)

val AppYellow = Color(0xFFFAEF5D)
val AppIndigo = Color(0xFF1D2B53)

val Study = Color(0xFFFF0000)    // 빨강 (Red)
val Work = Color(0xFFFF7F00)     // 주황 (Orange)
val Exercise = Color(0xFFFFFF00) // 노랑 (Yellow)
val Hobby = Color(0xFF00FF00)    // 연두 (Lime Green)
val Relaxation = Color(0xFF007F00) // 녹색 (Dark Green)
val Meal = Color(0xFF00FFFF)     // 청록 (Aqua)
val Travel = Color(0xFF0000FF)   // 파랑 (Blue)
val Cleaning = Color(0xFF00007F) // 남색 (Navy Blue)
val Hygiene = Color(0xFF7F00FF) // 보라 (Purple)
val Sleep = Color(0xFF7F007F)    // 자주색 (Dark Magenta)

// Light / Dark Mode Colors
val LightStudy = Color(0xFFFF6666)           // 밝은 빨강
val LightWork = Color(0xFFFFB266)            // 밝은 주황
val LightExercise = Color(0xFFFFFF66)        // 밝은 노랑
val LightHobby = Color(0xFF99FF99)           // 밝은 연두
val LightRelaxation = Color(0xFF66CC66)      // 밝은 녹색
val LightMeal = Color(0xFF66FFFF)            // 밝은 청록
val LightTravel = Color(0xFF66B2FF)          // 밝은 파랑
val LightCleaning = Color(0xFFB3B3FF)        // 밝은 남색
val LightHygiene = Color(0xFFCC99FF)         // 밝은 보라
val LightSleep = Color(0xFFFF80FF)           // 밝은 자주색