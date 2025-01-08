package andpact.project.wid.model

enum class SubTitle(
    val kr: String,
    val title: Title,
//    val image: Int,
) {
    UNSELECTED(kr = "선택 안됨", title = Title.UNTITLED),
    PROGRAMMING(kr = "프로그래밍", title = Title.WORK),
    MATH(kr = "수학", title = Title.STUDY),
    MARATHON(kr = "마라톤", title = Title.EXERCISE),
    GAME(kr = "게임", title = Title.HOBBY),
    YOUTUBE(kr = "유튜브", title = Title.RELAXATION),
    DINNER(kr = "저녁 식사", title = Title.MEAL),
    COMMUTE(kr = "출근", title = Title.TRAVEL),
    WASHING_DISH(kr = "설거지", title = Title.CLEANING),
    SHOWER(kr = "샤워", title = Title.HYGIENE),
    NAP(kr = "낮잠", title = Title.SLEEP),


    // TODO: 추가할 예정
}