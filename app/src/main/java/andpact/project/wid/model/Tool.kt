package andpact.project.wid.model

enum class Tool(val kr: String) {
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