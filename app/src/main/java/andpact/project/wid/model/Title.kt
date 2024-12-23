package andpact.project.wid.model

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import androidx.compose.ui.graphics.Color
import java.time.Duration

enum class Title(
    val kr: String,
    val color: Color,
    val image: Int,
    val smallImage: Int
) {
    UNTITLED(
        kr = "무제",
        color = Color.Gray,
        image = R.drawable.image_untitled,
        smallImage = R.drawable.image_untitled_small
    ),
    STUDY(
        kr = "공부",
        color = LightStudy,
        image = R.drawable.image_study,
        smallImage = R.drawable.image_study_small
    ),
    WORK(
        kr = "노동",
        color = LightWork,
        image = R.drawable.image_work,
        smallImage = R.drawable.image_work_small
    ),
    EXERCISE(
        kr = "운동",
        color = LightExercise,
        image = R.drawable.image_exercise,
        smallImage = R.drawable.image_exercise_small
    ),
    HOBBY(
        kr = "취미",
        color = LightHobby,
        image = R.drawable.image_hobby,
        smallImage = R.drawable.image_hobby_small
    ),
    RELAXATION(
        kr = "휴식",
        color = LightRelaxation,
        image = R.drawable.image_relaxation,
        smallImage = R.drawable.image_relaxation_small
    ),
    MEAL(
        kr = "식사",
        color = LightMeal,
        image = R.drawable.image_meal,
        smallImage = R.drawable.image_meal_small
    ),
    TRAVEL(
        kr = "여행",
        color = LightTravel,
        image = R.drawable.image_travel,
        smallImage = R.drawable.image_travel_small
    ),
    CLEANING(
        kr = "청소",
        color = LightCleaning,
        image = R.drawable.image_cleaning,
        smallImage = R.drawable.image_cleaning_small
    ),
    HYGIENE(
        kr = "위생",
        color = LightHygiene,
        image = R.drawable.image_hygiene,
        smallImage = R.drawable.image_hygiene_small
    ),
    SLEEP(
        kr = "수면",
        color = LightSleep,
        image = R.drawable.image_sleep,
        smallImage = R.drawable.image_sleep_small
    );

    // kr 값으로 Title 찾기
//    companion object {
//        fun fromKr(kr: String): Title? = values().find { it.kr == kr }
//    }
}