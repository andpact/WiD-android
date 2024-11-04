package andpact.project.wid.util

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.Duration
import java.time.LocalDate
import kotlin.random.Random

/**
 * map -> 불변
 * hashMap -> 가변
 *
 * 레벨 당 경험치 총량(레벨 업을 위해 필요한 경험치)
 * 1 레벨에 경험치 10을 모았다 -> 레벨 업
 * 초 단위
 *
 * 최고 레벨, 증가 비율 생각해 보자.!!!!!!!!
 *
 * 동시에 2레벨 업 안되도록!!
 */
val levelRequiredExpMap: Map<Int, Int> = mapOf(
    1 to 86_400,
    2 to 172_800,
    3 to 259_200,
    4 to 345_600,
    5 to 432_000,
    6 to 518_400,
    7 to 604_800,
    8 to 691_200,
    9 to 777_600,
    10 to 864_000,
    11 to 950_400,   // 86,400 + 10 * 86,400
    12 to 1_036_800, // 86,400 + 11 * 86,400
    13 to 1_123_200, // 86,400 + 12 * 86,400
    14 to 1_209_600, // 86,400 + 13 * 86,400
    15 to 1_296_000, // 86,400 + 14 * 86,400
    16 to 1_382_400, // 86,400 + 15 * 86,400
    17 to 1_468_800, // 86,400 + 16 * 86,400
    18 to 1_555_200, // 86,400 + 17 * 86,400
    19 to 1_641_600, // 86,400 + 18 * 86,400
    20 to 1_728_000, // 86,400 + 19 * 86,400
    21 to 1_814_400, // 86,400 + 20 * 86,400
    22 to 1_900_800, // 86,400 + 21 * 86,400
    23 to 1_987_200, // 86,400 + 22 * 86,400
    24 to 2_073_600, // 86,400 + 23 * 86,400
    25 to 2_160_000, // 86,400 + 24 * 86,400
    26 to 2_246_400, // 86,400 + 25 * 86,400
    27 to 2_332_800, // 86,400 + 26 * 86,400
    28 to 2_419_200, // 86,400 + 27 * 86,400
    29 to 2_505_600, // 86,400 + 28 * 86,400
    30 to 2_592_000  // 86,400 + 29 * 86,400
)

//val levelToTotalExpMap: Map<Int, Int> = mapOf(
//    1 to 86_400,
//    2 to 259_200,    // 86,400 + 172,800
//    3 to 518_400,    // 86,400 + 172,800 + 259,200
//    4 to 864_000,    // 86,400 + 172,800 + 259,200 + 345,600
//    5 to 1_296_000,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000
//    6 to 1_818_400,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400
//    7 to 2_433_600,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800
//    8 to 3_144_800,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800 + 691,200
//    9 to 3_955_200,  // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800 + 691,200 + 777,600
//    10 to 4_864_000, // 86,400 + 172,800 + 259,200 + 345,600 + 432,000 + 518,400 + 604,800 + 691,200 + 777,600 + 864,000
//    11 to 5_870_400, // 누적합 + 950,400
//    12 to 6_973_200, // 누적합 + 1,036,800
//    13 to 8_171_600, // 누적합 + 1,123,200
//    14 to 9_465_600, // 누적합 + 1,209,600
//    15 to 10_855_600, // 누적합 + 1,296,000
//    16 to 12_341_600, // 누적합 + 1,382,400
//    17 to 13_923_600, // 누적합 + 1,468,800
//    18 to 15_601_600, // 누적합 + 1,555,200
//    19 to 17_375_600, // 누적합 + 1,641,600
//    20 to 19_245_600, // 누적합 + 1,728,000
//    21 to 21_211_600, // 누적합 + 1,814,400
//    22 to 23_273_600, // 누적합 + 1,900,800
//    23 to 25_441_600, // 누적합 + 1,987,200
//    24 to 27_715_600, // 누적합 + 2,073,600
//    25 to 30_095_600, // 누적합 + 2,160,000
//    26 to 32_581_600, // 누적합 + 2,246,400
//    27 to 35_173_600, // 누적합 + 2,332,800
//    28 to 37_871_600, // 누적합 + 2,419,200
//    29 to 40_675_600, // 누적합 + 2,505,600
//    30 to 43_585_600  // 누적합 + 2,592,000
//)

/**
 * 레벨 업이 진행되면 밸류에 날짜에 해당하는 값을 넣자.
 * 계정을 생성했을 때, 클라이언트에서 사용할 map<Int, LocalDate>와
 * 서버에 저장할 map<String, String> 둘 다 필요하다.
 *
 * 기존 계정의 경우 서버에서 map<Int, String>을 가져와서 map<Int, LocalDate>로 변경 후 사용하면 된다.
 */
val defaultLevelDateMap: Map<String, LocalDate> = mapOf(
    "1" to LocalDate.now()
)

/** 클라이언트 : Map<String, LocalDate> -> 서버 : Map<String, String> */
fun convertLevelToDateMapForServer(map: Map<String, LocalDate>): Map<String, String> {
//    Log.d("DataUtil", "convertLevelToDateMapForServer executed")

    return map.mapValues { it.value.toString() }
}

/** 서버 : Map<String, String> -> 클라이언트 : Map<String, LocalDate> */
fun convertLevelToDateMapForClient(map: Map<String, String>): Map<String, LocalDate> {
//    Log.d("DataUtil", "convertLevelToDateMapForClient executed")

    return map.mapValues { LocalDate.parse(it.value) }
}

val titleList: List<String> = listOf(
    "0",        // STUDY
    "1",        // WORK
    "2",        // EXERCISE
    "3",        // HOBBY
    "4",        // RELAXATION
    "5",        // MEAL
    "6",        // TRAVEL
    "7",        // CLEANING
    "8",        // HYGIENE
    "9"         // SLEEP
)

/**
 * 서버에는 "0", "1", "2" 형태로 제목을 저장하고,
 * (문자열 형태로 저장해야 하는 이유는 Map(TitleDurationMap, TitleCountMap)을 서버에 저장할 때, 키 값이 문자열 밖에 안되니까)
 * 불러올 때 Map을 통해서 공부 or Study로 변환해서 표시.
 */
val titleKRMap: Map<String, String> = mapOf(
    "0" to "공부",        // STUDY
    "1" to "노동",        // WORK
    "2" to "운동",        // EXERCISE
    "3" to "취미",        // HOBBY
    "4" to "휴식",        // RELAXATION
    "5" to "식사",        // MEAL
    "6" to "이동",        // TRAVEL
    "7" to "정리",        // CLEANING
    "8" to "위생",        // HYGIENE
    "9" to "수면"         // SLEEP
)

//val titleNumberStringToTitleExampleKRStringMap: Map<String, String> = mapOf(
//    "0" to "중간/기말 고사 준비, 자격증 공부, 자기 개발 등 학습 활동", // STUDY
//    "1" to "본업, 아르바이트, 인턴십 등 직업적 활동",              // WORK
//    "2" to "웨이트 트레이닝, 러닝, 요가, 필라테스 등 신체 활동",    // EXERCISE
//    "3" to "즐거움을 추구하는 활동. 게임, 독서, 창작 활동 등 동적인 취미 활동", // HOBBY
//    "4" to "피로 회복을 위한 활동. TV 시청, 음악 감상 등의 정적인 활동",       // RELAXATION
//    "5" to "아침, 점심, 저녁, 간식, 야식 등 음식 섭취",            // MEAL
//    "6" to "출근 및 퇴근, 등교 및 하교 등 장소 이동",              // TRAVEL
//    "7" to "청소, 빨래, 집안일 등 가정 및 개인 공간 정리",        // CLEANING
//    "8" to "샤워, 목욕, 세안, 양치질, 손 씻기 등 개인 위생 관리", // HYGIENE
//    "9" to "밤잠, 낮잠 등 수면과 관련된 모든 활동"                // SLEEP
//)

val titleColorMap: Map<String, Color> = mapOf(
//    "0" to Study,           // 빨강 (Red) - STUDY
//    "1" to Work,            // 주황 (Orange) - WORK
//    "2" to Exercise,        // 노랑 (Yellow) - EXERCISE
//    "3" to Hobby,           // 연두 (Lime Green) - HOBBY
//    "4" to Relaxation,      // 녹색 (Dark Green) - RELAXATION
//    "5" to Meal,            // 청록 (Aqua) - MEAL
//    "6" to Travel,          // 파랑 (Blue) - TRAVEL
//    "7" to Cleaning,        // 남색 (Navy Blue) - CLEANING
//    "8" to Hygiene,         // 보라 (Purple) - HYGIENE
//    "9" to Sleep            // 자주색 (Dark Magenta) - SLEEP
    "0" to LightStudy,
    "1" to LightWork,
    "2" to LightExercise,
    "3" to LightHobby,
    "4" to LightRelaxation,
    "5" to LightMeal,
    "6" to LightTravel,
    "7" to LightCleaning,
    "8" to LightHygiene,
    "9" to LightSleep
)

val titleImageMap: Map<String, Int> = mapOf(
    "0" to R.drawable.image_study,
    "1" to R.drawable.image_work,
    "2" to R.drawable.image_exercise,
    "3" to R.drawable.image_hobby,
    "4" to R.drawable.image_relaxation,
    "5" to R.drawable.image_meal,
    "6" to R.drawable.image_travel,
    "7" to R.drawable.image_cleaning,
    "8" to R.drawable.image_hygiene,
    "9" to R.drawable.image_sleep,
)

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

val defaultTitleCountMap: Map<String, Int> = mapOf(
    "0" to 0, // 공부 (Studying) - STUDY
    "1" to 0, // 일 (Working) - WORK
    "2" to 0, // 운동 (Exercising) - EXERCISE
    "3" to 0, // 취미 (Hobbies) - HOBBY
    "4" to 0, // 휴식 (Relaxation) - RELAXATION
    "5" to 0, // 식사 (Eating) - MEAL
    "6" to 0, // 이동 (Commuting/Traveling) - TRAVEL
    "7" to 0, // 정리/관리 (Cleaning and Organizing) - CLEANING
    "8" to 0, // 개인 위생 (Personal Hygiene) - HYGIENE
    "9" to 0  // 수면 (Sleeping) - SLEEP
)

val defaultTitleDurationMap: Map<String, Duration> = mapOf(
    "0" to Duration.ZERO, // 공부 (Studying) - STUDY
    "1" to Duration.ZERO, // 일 (Working) - WORK
    "2" to Duration.ZERO, // 운동 (Exercising) - EXERCISE
    "3" to Duration.ZERO, // 취미 (Hobbies) - HOBBY
    "4" to Duration.ZERO, // 휴식 (Relaxation) - RELAXATION
    "5" to Duration.ZERO, // 식사 (Eating) - MEAL
    "6" to Duration.ZERO, // 이동 (Commuting/Traveling) - TRAVEL
    "7" to Duration.ZERO, // 정리 (Cleaning) - CLEANING
    "8" to Duration.ZERO, // 위생 (Hygiene) - HYGIENE
    "9" to Duration.ZERO  // 수면 (Sleeping) - SLEEP
)

val defaultToolCountMap: Map<CurrentTool, Int> = mapOf(
    CurrentTool.STOPWATCH to 0,
    CurrentTool.TIMER to 0,
    CurrentTool.LIST to 0,
)

val defaultToolDurationMap: Map<CurrentTool, Duration> = mapOf(
    CurrentTool.STOPWATCH to Duration.ZERO,
    CurrentTool.TIMER to Duration.ZERO,
    CurrentTool.LIST to Duration.ZERO,
)

fun convertTitleToDurationMapForServer(map: Map<String, Duration>): Map<String, Int> {
//    Log.d("DataUtil", "convertTitleToDurationMapForServer executed")

    return map.mapValues { entry -> entry.value.seconds.toInt() }
}

fun convertTitleToDurationMapForClient(map: Map<String, Int>): Map<String, Duration> {
//    Log.d("DataUtil", "convertTitleToDurationMapForClient executed")

    return map.mapValues { entry -> Duration.ofSeconds(entry.value.toLong()) }
}

fun convertToolToCountMapForServer(map: Map<CurrentTool, Int>): Map<String, Int> {
//    Log.d("DataUtil", "convertToolToCountMapForServer executed")

    return map.mapKeys { it.key.name }
}

fun convertToolToCountMapForClient(map: Map<String, Int>): Map<CurrentTool, Int> {
//    Log.d("DataUtil", "convertToolToCountMapForClient executed")

    return map.mapKeys { enumValueOf(it.key) }
}

fun convertToolToDurationMapForServer(map: Map<CurrentTool, Duration>): Map<String, Int> {
//    Log.d("DataUtil", "convertToolToDurationMapForServer executed")

    return map.mapKeys { it.key.name }
        .mapValues { it.value.seconds.toInt() }
}

fun convertToolToDurationMapForClient(map: Map<String, Int>): Map<CurrentTool, Duration> {
//    Log.d("DataUtil", "convertToolToDurationMapForClient executed")

    return map.mapKeys { enumValueOf<CurrentTool>(it.key) }
        .mapValues { Duration.ofSeconds(it.value.toLong()) }
}

/** 영어권위해서 0 : "부지런한 고양이" 같이 만들어야 하나? */
val tmpNicknameList: List<String> = listOf(
    "부지런한 고양이",
    "활기찬 햇살",
    "즐거운 토끼",
    "웃음 가득한 나비",
    "활동적인 다람쥐",
    "상큼한 레몬",
    "밝은 별빛",
    "행복한 새벽",
    "긍정적인 물결",
    "힘찬 파랑새",
    "활력 넘치는 해바라기"
)

fun getRandomNickname(): String {
    //    Log.d("DataUtil", "getRandomNickname executed")

    return tmpNicknameList[Random.nextInt(tmpNicknameList.size)]
}

val daysOfWeekFromMonday = listOf(
    "월",
    "화",
    "수",
    "목",
    "금",
    "토",
    "일"
)

enum class CurrentTool {
    NONE,
    STOPWATCH,
    TIMER,
//    POMODORO,
    LIST
}

val toolKRMap :Map<CurrentTool, String> = mapOf(
    CurrentTool.STOPWATCH to "스톱워치",
    CurrentTool.TIMER to "타이머",
    CurrentTool.LIST to "리스트",
)

enum class CurrentToolState {
    STOPPED,
    STARTED,
    PAUSED,
}