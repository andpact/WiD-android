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

/**
 * MaterialTheme.colorScheme이 @Composable을 사용하기 때문에,
 * 아래 메서드는 @Composable 어노테이션을 적용해야함.
 */
@Composable
fun getDateString(date: LocalDate): AnnotatedString {
    Log.d("DateUtil", "getDateString executed")

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


//@Composable
//fun getDayStringWith2Lines(date: LocalDate): AnnotatedString {
//    return buildAnnotatedString {
//        append(date.format(DateTimeFormatter.ofPattern("yyyy년\n")))
//        append(date.format(DateTimeFormatter.ofPattern("M월 d일 (")))
//
//        withStyle(
//            style = SpanStyle(
//                color = when (date.dayOfWeek) {
//                    DayOfWeek.SATURDAY -> DeepSkyBlue
//                    DayOfWeek.SUNDAY -> OrangeRed
//                    else -> MaterialTheme.colorScheme.primary
//                }
//            )
//        ) {
//            append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
//        }
//        append(")")
//    }
//}

@Composable
fun getDateStringWith3Lines(date: LocalDate): AnnotatedString {
    Log.d("DateUtil", "getDateStringWith3Lines executed")

    return buildAnnotatedString {
//        withStyle(style = ParagraphStyle(lineHeight = 30.sp)) { // @Composable + 문단 스타일 적용하니 에러 발생함.
            if (date.year == LocalDate.now().year) {
                append(date.format(DateTimeFormatter.ofPattern("M월 d일\n")))
            } else {
                append(date.format(DateTimeFormatter.ofPattern("yyyy년\nM월 d일\n")))
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
            append("요일")
//        }
    }
}

@Composable
fun getPeriodStringOfWeek(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
    Log.d("DateUtil", "getPeriodStringOfWeek executed")

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

fun getPeriodStringOfMonth(date: LocalDate): AnnotatedString {
    Log.d("DateUtil", "getPeriodStringOfMonth executed")

    val formattedString = if (date.year == LocalDate.now().year) {
        date.format(DateTimeFormatter.ofPattern("M월"))
    } else {
        date.format(DateTimeFormatter.ofPattern("yyyy년 M월"))
    }

    return buildAnnotatedString {
        append(formattedString)
    }
}

fun getFirstDateOfWeek(date: LocalDate): LocalDate {
    Log.d("DateUtil", "getFirstDateOfWeek executed")

    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun getLastDateOfWeek(date: LocalDate): LocalDate {
    Log.d("DateUtil", "getLastDateOfWeek executed")

    return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
}

//@Composable
//fun getWeekString(date: LocalDate): AnnotatedString {
//    val firstDayOfWeek = getFirstDayOfWeek(date)
//    val lastDayOfWeek = getLastDayOfWeek(date)
//
//    return buildAnnotatedString {
//        append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
//
//        withStyle(
//            style = SpanStyle(
//                color = when (firstDayOfWeek.dayOfWeek) {
//                    DayOfWeek.SATURDAY -> DeepSkyBlue
//                    DayOfWeek.SUNDAY -> OrangeRed
//                    else -> MaterialTheme.colorScheme.primary
//                }
//            )
//        ) {
//            append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
//        }
//
//        append(") ~ ")
//
//        if (firstDayOfWeek.year != lastDayOfWeek.year) {
//            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))
//        } else if (firstDayOfWeek.month != lastDayOfWeek.month) {
//            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("M월 d일 (")))
//        } else {
//            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("d일 (")))
//        }
//
//        withStyle(
//            style = SpanStyle(
//                color = when (lastDayOfWeek.dayOfWeek) {
//                    DayOfWeek.SATURDAY -> DeepSkyBlue
//                    DayOfWeek.SUNDAY -> OrangeRed
//                    else -> MaterialTheme.colorScheme.primary
//                }
//            )
//        ) {
//            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
//        }
//
//        append(")")
//    }
//}

fun getFirstDateOfMonth(date: LocalDate): LocalDate {
    Log.d("DateUtil", "getFirstDateOfMonth executed")

    val yearMonth = YearMonth.from(date)
    return yearMonth.atDay(1)

//    return date.withDayOfMonth(1)
//
//    return date.minusMonths(1).withDayOfMonth(1)
}

fun getLastDateOfMonth(date: LocalDate): LocalDate {
    Log.d("DateUtil", "getLastDateOfMonth executed")

    val yearMonth = YearMonth.from(date)
    return yearMonth.atEndOfMonth()

//    return date.withDayOfMonth(date.lengthOfMonth())
//
//    return date.minusMonths(1).withDayOfMonth(date.minusMonths(1).lengthOfMonth())
}

//fun getDate1yearAgo(date: LocalDate): LocalDate {
//    val oneYearAgo = date.minusDays(364)
////    return oneYearAgo.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
//    return oneYearAgo
//}