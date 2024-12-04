package andpact.project.wid.util

import java.time.Duration

enum class CurrentTool(val kr: String) {
    NONE(kr = "없음"),
    STOPWATCH(kr = "스톱워치"),
    TIMER(kr = "타이머"),
    // POMODORO("포모도로"),
    LIST(kr = "리스트")

    // 한국어로 CurrentTool 찾을 때
//    companion object {
//        fun fromKr(kr: String): CurrentTool? {
//            return values().find { it.kr == kr }
//        }
//    }
}

enum class CurrentToolState {
    STOPPED,
    STARTED,
    PAUSED
}

//val defaultToolCountMap: Map<CurrentTool, Int> = CurrentTool.values()
//    .filter { it != CurrentTool.NONE } // NONE을 제외하고 싶을 경우 사용
//    .associateWith { 0 }
//
//val defaultToolDurationMap: Map<CurrentTool, Duration> = CurrentTool.values()
//    .filter { it != CurrentTool.NONE }
//    .associateWith { Duration.ZERO }
//
//fun convertToolToCountMapForServer(map: Map<CurrentTool, Int>): Map<String, Int> {
////    Log.d("DataUtil", "convertToolToCountMapForServer executed")
//
//    return map.mapKeys { it.key.name }
//}
//
//fun convertToolToCountMapForClient(map: Map<String, Int>): Map<CurrentTool, Int> {
////    Log.d("DataUtil", "convertToolToCountMapForClient executed")
//
//    return map.mapKeys { enumValueOf(it.key) }
//}
//
//fun convertToolToDurationMapForServer(map: Map<CurrentTool, Duration>): Map<String, Int> {
////    Log.d("DataUtil", "convertToolToDurationMapForServer executed")
//
//    return map.mapKeys { it.key.name }
//        .mapValues { it.value.seconds.toInt() }
//}
//
//fun convertToolToDurationMapForClient(map: Map<String, Int>): Map<CurrentTool, Duration> {
////    Log.d("DataUtil", "convertToolToDurationMapForClient executed")
//
//    return map.mapKeys { enumValueOf<CurrentTool>(it.key) }
//        .mapValues { Duration.ofSeconds(it.value.toLong()) }
//}