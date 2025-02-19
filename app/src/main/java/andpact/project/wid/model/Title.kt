package andpact.project.wid.model

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import androidx.compose.ui.graphics.Color
import java.time.Duration

enum class Title(
    val kr: String,
    val color: Color
) {
    UNTITLED(kr = "무제", color = Color.Gray), // 컬러 사용될 일은 없음.

    // 1
    STUDY(kr = "공부", color = LightStudy),

    // 2 직업 및 집안 일
    WORK(kr = "노동", color = LightWork),

    // 3
    EXERCISE(kr = "운동", color = LightExercise),

    // 4
    HOBBY(kr = "취미", color = LightHobby),

    // 5
    RELAXATION(kr = "휴식", color = LightRelaxation),


    MEAL(kr = "식사", color = LightMeal),
    TRAVEL(kr = "이동", color = LightTravel),
//    CLEANING(kr = "청소", color = LightCleaning),
    HYGIENE(kr = "위생", color = LightHygiene),
//    SLEEP(kr = "수면", color = LightSleep);
}