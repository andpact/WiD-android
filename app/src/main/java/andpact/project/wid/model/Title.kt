package andpact.project.wid.model

import andpact.project.wid.ui.theme.*
import androidx.compose.ui.graphics.Color

enum class Title(
    val kr: String,
//    val en: String,
    val lightColor: Color,
    val darkColor: Color
) {
    UNTITLED(kr = "무제", lightColor = light_surface_container, darkColor = dark_surface_container),

    STUDY(kr = "공부", lightColor = light_study, darkColor = dark_study),             // 1. 학습 활동
    WORK(kr = "노동", lightColor = light_work, darkColor = dark_work),                // 2. 경제 활동
    EXERCISE(kr = "운동", lightColor = light_exercise, darkColor = dark_exercise),    // 3. 신체 활동
    HOBBY(kr = "취미", lightColor = light_hobby, darkColor = dark_hobby),             // 4. 휴식 활동
    DAILY(kr = "일상", lightColor = light_daily, darkColor = dark_daily),             // 5. 보통, 평소, 평범 활동
    ESSENTIAL(kr = "필수", lightColor = light_essential, darkColor = dark_essential), // 6. 수면, 위생, 청소, 식사
    OTHER(kr = "기타", lightColor = light_other, darkColor = dark_other),             // 7. 그 외
}