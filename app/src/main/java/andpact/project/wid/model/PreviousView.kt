package andpact.project.wid.model

enum class PreviousView(
    val kr: String
) {
    STOPWATCH(kr = "스톱워치"), // -> 제목 변경
    TIMER(kr = "타이머"), // -> 제목 변경
    CLICKED_WID_TITLE(kr = "기록"), // -> 제목 변경
    CLICKED_WID_SUB_TITLE(kr = "기록"), // -> 부 제목 변경
    CLICKED_WID_START(kr = "기록 시작"), // -> 시작 변경
    CLICKED_WID_FINISH(kr = "기록 종료"), // -> 종료 변경
    CLICKED_WID_CITY(kr = "기록"), // -> 도시 변경
    USER_CITY(kr = "유저"); // -> 도시 변경
}