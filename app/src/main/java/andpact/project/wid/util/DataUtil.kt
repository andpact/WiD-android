package andpact.project.wid.util

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import androidx.compose.ui.res.painterResource

val titles = arrayOf(
    "STUDY", // 공부
    "WORK", // 노동
    "EXERCISE", // 운동
    "HOBBY", // 취미
    "PLAY", // 놀기
    "MEAL", // 식사
    "SHOWER", // 샤워
    "TRAVEL", // 이동
    "SLEEP", // 취침
    "ETC" // 기타
)

val titleMap = mapOf(
    "STUDY" to "공부",
    "WORK" to "노동",
    "EXERCISE" to "운동",
    "HOBBY" to "취미",
    "PLAY" to "놀기",
    "MEAL" to "식사",
    "SHOWER" to "샤워",
    "TRAVEL" to "이동",
    "SLEEP" to "수면",
    "ETC" to "기타"
)

val titleIconMap = mapOf(
    "ALL" to R.drawable.baseline_title_24,
    "STUDY" to R.drawable.baseline_menu_book_16,
    "WORK" to R.drawable.baseline_construction_16,
    "EXERCISE" to R.drawable.baseline_fitness_center_16,
    "HOBBY" to R.drawable.outline_brush_24,
    "PLAY" to R.drawable.outline_videogame_asset_16,
    "MEAL" to R.drawable.baseline_local_dining_16,
    "SHOWER" to R.drawable.outline_shower_16,
    "TRAVEL" to R.drawable.baseline_commute_16,
    "SLEEP" to R.drawable.outline_bed_16,
    "ETC" to R.drawable.baseline_more_horiz_16
)

//val titleExampleMap = mapOf(
//    "STUDY" to "중간 및 기말고사, 자격증, 공시, 승진시험",
//    "WORK" to "직장, 부업, 알바",
//    "EXERCISE" to "헬스, 홈트, 요가, 필라테스, 런닝, 조깅",
//    "HOBBY" to "유튜브, 영화, 드라마 시청, 독서",
//    "PLAY" to "친구 만남, 데이트",
//    "MEAL" to "아침, 점심, 저녁",
//    "SHOWER" to "세안, 샤워, 목욕",
//    "TRAVEL" to "등하교, 출퇴근, 버스, 지하철, 도보",
//    "SLEEP" to "낮잠",
//    "ETC" to "그 외 기타 활동"
//)

val titlesWithAll = arrayOf(
    "ALL", // 전체
    "STUDY", // 공부
    "WORK", // 노동
    "EXERCISE", // 운동
    "HOBBY", // 취미
    "PLAY", // 놀기
    "MEAL", // 식사
    "SHOWER", // 샤워
    "TRAVEL", // 이동
    "SLEEP", // 취침
    "ETC" // 기타
)

val titleMapWithAll = mapOf(
    "ALL" to "전체",
    "STUDY" to "공부",
    "WORK" to "노동",
    "EXERCISE" to "운동",
    "HOBBY" to "취미",
    "PLAY" to "놀기",
    "MEAL" to "식사",
    "SHOWER" to "샤워",
    "TRAVEL" to "이동",
    "SLEEP" to "수면",
    "ETC" to "기타"
)

val periods = arrayOf(
    "WEEK",
    "MONTH"
)

val periodMap = mapOf(
    "WEEK" to "일주일",
    "MONTH" to "한 달",
)


val colorMap = mapOf(
    "ALL" to Black,
    "STUDY" to Study,
    "WORK" to Work,
    "EXERCISE" to Exercise,
    "HOBBY" to Hobby,
    "PLAY" to Play,
    "MEAL" to Meal,
    "SHOWER" to Shower,
    "TRAVEL" to Travel,
    "SLEEP" to Sleep,
    "ETC" to Etc
)

val daysOfWeekFromSunday = listOf(
    "일",
    "월",
    "화",
    "수",
    "목",
    "금",
    "토"
)

val daysOfWeekFromMonday = listOf(
    "월",
    "화",
    "수",
    "목",
    "금",
    "토",
    "일"
)

enum class PlayerState {
    Started,
    Paused,
    Stopped
}