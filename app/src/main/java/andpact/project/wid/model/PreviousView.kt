package andpact.project.wid.model

enum class PreviousView(
    val kr: String
) {
    STOPWATCH(kr = "스톱워치"), // -> 제목 변경
    TIMER(kr = "타이머"), // -> 제목 변경
    CLICKED_WID(kr = "기록"), // -> 제목, 시작, 종료, 도시 변경
    CLICKED_WID_START(kr = "시작"), // -> 시작 변경
    CLICKED_WID_FINISH(kr = "종료"), // -> 종료 변경
    USER(kr = "사용자"); // -> 도시 변경
}