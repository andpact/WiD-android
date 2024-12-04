package andpact.project.wid.util

import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

/** 이넘 클래스로 만들기? */
val daysOfWeekFromMonday = listOf(
    "월",
    "화",
    "수",
    "목",
    "금",
    "토",
    "일"
)

/**
 * MaterialTheme.colorScheme이 @Composable을 사용하기 때문에,
 * 아래 메서드는 @Composable 어노테이션을 적용해야함.
 */
@Composable
fun getDateString(date: LocalDate): AnnotatedString {
//    Log.d("DateUtil", "getDateString executed")

    val formattedString = buildAnnotatedString {
        if (date.year == LocalDate.now().year) {
            append(date.format(DateTimeFormatter.ofPattern("M월 d일 (")))
        } else {
            append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
        }

        withStyle(
            style = SpanStyle(
                color = when (date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> DeepSkyBlue
                    DayOfWeek.SUNDAY -> OrangeRed
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        ) {
            append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
        }
        append(")")
    }

    return formattedString
}

@Composable
fun getPeriodStringOfWeek(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
//    Log.d("DateUtil", "getPeriodStringOfWeek executed")

    return buildAnnotatedString {
        if (firstDayOfWeek.year == LocalDate.now().year) {
            append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
        } else {
            append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
        }

        withStyle(
            style = SpanStyle(
                color = when (firstDayOfWeek.dayOfWeek) {
                    DayOfWeek.SATURDAY -> DeepSkyBlue
                    DayOfWeek.SUNDAY -> OrangeRed
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        ) {
            append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
        }

        append(") ~ ")

        if (firstDayOfWeek.year != lastDayOfWeek.year) {
            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
        } else if (firstDayOfWeek.month != lastDayOfWeek.month) {
            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
        } else {
            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일 (")))
        }

        withStyle(
            style = SpanStyle(
                color = when (lastDayOfWeek.dayOfWeek) {
                    DayOfWeek.SATURDAY -> DeepSkyBlue
                    DayOfWeek.SUNDAY -> OrangeRed
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        ) {
            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
        }

        append(")")
    }
}

fun getFirstDateOfWeek(date: LocalDate): LocalDate {
//    Log.d("DateUtil", "getFirstDateOfWeek executed")

    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun getLastDateOfWeek(date: LocalDate): LocalDate {
//    Log.d("DateUtil", "getLastDateOfWeek executed")

    return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
}
