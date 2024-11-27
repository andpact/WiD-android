package andpact.project.wid.util

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import androidx.compose.ui.graphics.Color
import java.time.Duration

enum class Title(
    val kr: String,
    val description: String,
    val color: Color,
    val image: Int,
    val smallImage: Int,
    val defaultCount: Int = 0,
    val defaultDuration: Duration = Duration.ZERO
) {
    UNTITLED(
        kr = "무제",
        description = "특정한 활동을 정의하지 않음",
        color = Color.Gray,
        image = R.drawable.image_untitled,
        smallImage = R.drawable.image_untitled_small
    ),
    STUDY(
        kr = "공부",
        description = "중간/기말 고사, 자격증 공부, 자기 개발 등 학습 활동",
        color = LightStudy,
        image = R.drawable.image_study,
        smallImage = R.drawable.image_study_small
    ),
    WORK(
        kr = "노동",
        description = "본업, 아르바이트, 인턴십 등 직업적 활동",
        color = LightWork,
        image = R.drawable.image_work,
        smallImage = R.drawable.image_work_small
    ),
    EXERCISE(
        kr = "운동",
        description = "웨이트 트레이닝, 러닝, 요가, 필라테스 등 신체 활동",
        color = LightExercise,
        image = R.drawable.image_exercise,
        smallImage = R.drawable.image_exercise_small
    ),
    HOBBY(
        kr = "취미",
        description = "즐거움을 추구하는 활동. 게임, 독서, 창작 활동 등 동적인 취미 활동",
        color = LightHobby,
        image = R.drawable.image_hobby,
        smallImage = R.drawable.image_hobby_small
    ),
    RELAXATION(
        kr = "휴식",
        description = "피로 회복을 위한 활동. TV 시청, 음악 감상 등의 정적인 활동",
        color = LightRelaxation,
        image = R.drawable.image_relaxation,
        smallImage = R.drawable.image_relaxation_small
    ),
    MEAL(
        kr = "식사",
        description = "아침, 점심, 저녁, 간식, 야식 등 음식 섭취",
        color = LightMeal,
        image = R.drawable.image_meal,
        smallImage = R.drawable.image_meal_small
    ),
    TRAVEL(
        kr = "여행",
        description = "출근 및 퇴근, 등교 및 하교 등 장소 이동",
        color = LightTravel,
        image = R.drawable.image_travel,
        smallImage = R.drawable.image_travel_small
    ),
    CLEANING(
        kr = "청소",
        description = "청소, 빨래, 집안일 등 가정 및 개인 공간 정리",
        color = LightCleaning,
        image = R.drawable.image_cleaning,
        smallImage = R.drawable.image_cleaning_small
    ),
    HYGIENE(
        kr = "위생",
        description = "샤워, 목욕, 세안, 양치질, 손 씻기 등 개인 위생 관리",
        color = LightHygiene,
        image = R.drawable.image_hygiene,
        smallImage = R.drawable.image_hygiene_small
    ),
    SLEEP(
        kr = "수면",
        description = "밤잠, 낮잠 등 수면과 관련된 모든 활동",
        color = LightSleep,
        image = R.drawable.image_sleep,
        smallImage = R.drawable.image_sleep_small
    );

    // kr 값으로 Title 찾기
    companion object {
        fun fromKr(kr: String): Title? = values().find { it.kr == kr }
    }
}

/**
 * 서버에는 "0", "1", "2" 형태로 제목을 저장하고,
 * (문자열 형태로 저장해야 하는 이유는 Map(TitleDurationMap, TitleCountMap)을 서버에 저장할 때, 키 값이 문자열 밖에 안되니까)
 * 불러올 때 Map을 통해서 공부 or Study로 변환해서 표시.
 */
val defaultTitleCountMap: Map<Title, Int> = Title.values().associateWith { it.defaultCount }
val defaultTitleDurationMap: Map<Title, Duration> = Title.values().associateWith { it.defaultDuration }