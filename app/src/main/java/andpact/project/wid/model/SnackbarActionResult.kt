package andpact.project.wid.model

enum class SnackbarActionResult(val message: String) {
    SUCCESS_CREATE_WID("기록이 생성되었습니다."),
//    SUCCESS_READ_WID("WiD가 성공적으로 생성되었습니다."),
    SUCCESS_UPDATE_WID("기록이 갱신되었습니다."),
    SUCCESS_DELETE_WID("기록이 삭제되었습니다."),
    FAIL_CLIENT_ERROR("클라이언트에서 오류가 발생했습니다."),
    FAIL_SERVER_ERROR("서버에서 오류가 발생했습니다."),
    FAIL_TIME_LIMIT("기록의 소요 시간이 부족합니다.")
}