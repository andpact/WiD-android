package andpact.project.wid.util

import android.util.Log
import java.time.Duration

enum class TitleDurationMap(val kr: String) {
    TOTAL("총합 기록"),
    AVERAGE("평균 기록"),
    MAX("최대 기록"),
    MIN("최소 기록");

    // 한국어로 CurrentTool 찾을 때
//    companion object {
//        fun fromKr(kr: String): TitleDurationMap? {
//            return values().find { it.kr == kr }
//        }
//    }
}

fun <K, V : Comparable<V>> sortMapDescending(map: Map<K, V>): Map<K, V> {
//    Log.d("MapUtil", "sortMapDescending executed")

    return map.toList()
        .sortedByDescending { (_, value) -> value }
        .toMap()
}

fun convertTitleCountMapForServer(map: Map<Title, Int>): Map<String, Int> {
//    Log.d("MapUtil", "convertTitleCountMapForServer executed")

    return map.mapKeys { it.key.name }
}

fun convertTitleCountMapForClient(map: Map<String, Int>): Map<Title, Int> {
//    Log.d("MapUtil", "convertTitleCountMapForClient executed")

    return map.mapKeys { enumValueOf(it.key) }
}

fun convertTitleDurationMapForServer(map: Map<Title, Duration>): Map<String, Int> {
//    Log.d("MapUtil", "convertTitleDurationMapForServer executed")

    return map.mapKeys { entry -> entry.key.kr } // Title을 String(kr)으로 변환
        .mapValues { entry -> entry.value.seconds.toInt() } // Duration을 Int(초 단위)로 변환
}

fun convertTitleDurationMapForClient(map: Map<String, Int>): Map<Title, Duration> {
//    Log.d("MapUtil", "convertTitleDurationMapForClient executed")

    return map.mapKeys { entry -> Title.values().find { it.kr == entry.key } ?: Title.UNTITLED } // String을 Title로 변환
        .mapValues { entry -> Duration.ofSeconds(entry.value.toLong()) } // Int를 Duration으로 변환
}


/**
 * Color(0xFFFF0000) -> String("#FF0000")
 * 불투명도 제외하고 저장됨.
 */
//fun convertTitleColorMapToString(colorMap: Map<String, Color>): Map<String, String> {
//    // Color 객체를 HEX 문자열로 변환하는 내부 메서드
//    fun colorToHex(color: Color): String {
//        return String.format("#%06X", (0xFFFFFF and color.toArgb()))
//    }
//
//    // 색상 맵을 HEX 문자열로 변환하여 반환
//    return colorMap.mapValues { (_, color) -> colorToHex(color) }
//}

/**
 * String("#FF0000") -> Color(0xFFFF0000)
 */
//fun convertTitleColorMapToColor(colorMap: Map<String, String>): Map<String, Color> {
//    fun hexToColor(hex: String): Color {
//        // HEX 문자열에서 '#' 기호를 제거합니다.
//        val cleanHex = hex.removePrefix("#")
//
//        // HEX 문자열을 16진수로 변환합니다.
//        val colorInt = cleanHex.toIntOrNull(16) ?: throw IllegalArgumentException("Invalid HEX color string")
//
//        // ARGB 값을 추출합니다.
//        val alpha = if (cleanHex.length == 8) (colorInt shr 24) and 0xFF else 255
//        val red = (colorInt shr 16) and 0xFF
//        val green = (colorInt shr 8) and 0xFF
//        val blue = colorInt and 0xFF
//
//        // androidx.compose.ui.graphics.Color 객체를 생성합니다.
//        return Color(red, green, blue, alpha)
//    }
//
//    // HEX 문자열을 Color 객체로 변환하여 반환
//    return colorMap.mapValues { (_, hex) -> hexToColor(hex) }
//}