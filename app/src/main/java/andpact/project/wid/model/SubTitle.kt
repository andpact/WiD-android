package andpact.project.wid.model

enum class SubTitle(
    val kr: String,
    val title: Title,
//    val image: Int, // R.drawable.image_untitled
) {
    /**
     * 제목 기준 : What(무엇)
     * 버스를 했다(X) 출근을 했다(O)
     */

    UNSELECTED_UNTITLED(kr = "선택 안됨", title = Title.UNTITLED),

    UNSELECTED_WORK(kr = "선택 안함", title = Title.WORK),
    PROGRAMMING(kr = "프로그래밍", title = Title.WORK),
    SERVING(kr = "서빙", title = Title.WORK),
    COUNTER(kr = "카운터", title = Title.WORK),
    STORE_MANAGEMENT(kr = "매장 관리", title = Title.WORK),
    TRADING(kr = "트레이딩", title = Title.WORK),
    ERRAND(kr = "심부름", title = Title.WORK),
    DELIVERY(kr = "배달", title = Title.WORK),
    EDUCATION(kr = "교육", title = Title.WORK),
    FLOOR_CLEANING(kr = "바닥 청소", title = Title.WORK),
    LAUNDRY(kr = "빨래", title = Title.WORK),
    WASHING_DISH(kr = "설거지", title = Title.WORK),
    ETC_WORK(kr = "기타", title = Title.WORK),

    UNSELECTED_STUDY(kr = "선택 안함", title = Title.STUDY),
    MATH(kr = "수학", title = Title.STUDY),
    KOREAN(kr = "국어", title = Title.STUDY),
    ENGLISH(kr = "영어", title = Title.STUDY),
    SOCIAL_STUDIES(kr = "사회", title = Title.STUDY),
    SCIENCE(kr = "과학", title = Title.STUDY),
    PHYSICS(kr = "물리", title = Title.STUDY),
    BIOLOGY(kr = "생물", title = Title.STUDY),
    ECONOMICS(kr = "경제학", title = Title.STUDY),
    BUSINESS_ADMINISTRATION(kr = "경영학", title = Title.STUDY),
    ETC_STUDY(kr = "기타", title = Title.STUDY),

    UNSELECTED_EXERCISE(kr = "선택 안함", title = Title.EXERCISE),
    MARATHON(kr = "마라톤", title = Title.EXERCISE),
    WEIGHT_TRAINING(kr = "웨이트 트레이닝", title = Title.EXERCISE),
    YOGA(kr = "요가", title = Title.EXERCISE),
    PILATES(kr = "필라테스", title = Title.EXERCISE),
    HIKING(kr = "등산", title = Title.EXERCISE),
    ETC_EXERCISE(kr = "기타", title = Title.EXERCISE),

    UNSELECTED_HOBBY(kr = "선택 안함", title = Title.HOBBY),
    GAME(kr = "게임", title = Title.HOBBY),
    DIARY_WRITING(kr = "다이어리 작성", title = Title.HOBBY),
    ETC_HOBBY(kr = "기타", title = Title.HOBBY),

    UNSELECTED_RELAXATION(kr = "선택 안함", title = Title.RELAXATION),
    SOCIAL_NETWORK(kr = "소셜 네트워크", title = Title.RELAXATION),
    YOUTUBE(kr = "유튜브", title = Title.RELAXATION),
    NAP(kr = "낮잠", title = Title.RELAXATION),
    SLEEP(kr = "수면", title = Title.RELAXATION),
    MOVIE_DRAMA(kr = "영화, 드라마", title = Title.RELAXATION),
    MEDITATION(kr = "명상", title = Title.RELAXATION),
    SMOKING(kr = "흡연", title = Title.RELAXATION),
    MASTURBATION(kr = "자위", title = Title.RELAXATION),
    ETC_RELAXATION(kr = "기타", title = Title.RELAXATION),

    UNSELECTED_MEAL(kr = "선택 안함", title = Title.MEAL),
    BREAKFAST(kr = "아침 식사", title = Title.MEAL),
    LUNCH(kr = "점심 식사", title = Title.MEAL),
    DINNER(kr = "저녁 식사", title = Title.MEAL),
    LATE_NIGHT_SNACK(kr = "야식", title = Title.MEAL),
    SNACK(kr = "간식", title = Title.MEAL),
    DRINKING(kr = "음주", title = Title.MEAL),
    ETC_MEAL(kr = "기타", title = Title.MEAL),

    UNSELECTED_TRAVEL(kr = "선택 안함", title = Title.TRAVEL),
    COMMUTE(kr = "출근", title = Title.TRAVEL),
    LEAVING_WORK(kr = "퇴근", title = Title.TRAVEL),
    GOING_TO_SCHOOL(kr = "등교", title = Title.TRAVEL),
    LEAVING_SCHOOL(kr = "하교", title = Title.TRAVEL),
    MOVING_TO_APPOINTMENT(kr = "약속 장소 이동", title = Title.TRAVEL),
    TOURISM(kr = "관광", title = Title.TRAVEL),
    // 관광 말고 구경
    HOMECOMING(kr = "귀성", title = Title.TRAVEL),
    RETURNING(kr = "귀경", title = Title.TRAVEL),
    GOING_HOME(kr = "귀가", title = Title.TRAVEL),
    ETC_TRAVEL(kr = "기타", title = Title.TRAVEL),

    UNSELECTED_HYGIENE(kr = "선택 안함", title = Title.HYGIENE),
    WASHING_FACE(kr = "세수", title = Title.HYGIENE),
    WASHING_FEET(kr = "세족", title = Title.HYGIENE),
    BATHING(kr = "목욕", title = Title.HYGIENE),
    WASHING_HAIR(kr = "머리 감기", title = Title.HYGIENE),
    SHOWER(kr = "샤워", title = Title.HYGIENE),
    ETC_HYGIENE(kr = "기타", title = Title.HYGIENE);

    companion object {
        fun filterSubTitlesByTitle(targetTitle: Title): List<SubTitle> {
            return values().filter { it.title == targetTitle }
        }

        fun getSubTitleCountByTitle(targetTitle: Title): Int {
            return filterSubTitlesByTitle(targetTitle).size
        }
    }
}