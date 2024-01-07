package andpact.project.wid.util

import andpact.project.wid.ui.theme.DeepSkyBlue
import andpact.project.wid.ui.theme.OrangeRed
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

@Composable
fun getDayString(date: LocalDate): AnnotatedString {
    return buildAnnotatedString {
        append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

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
fun getDayStringWith3Lines(date: LocalDate): AnnotatedString {
    return buildAnnotatedString {
//        withStyle(style = ParagraphStyle(lineHeight = 30.sp)) { // @Composable + 문단 스타일 적용하니 에러 발생함.
            append(date.format(DateTimeFormatter.ofPattern("yyyy년\nM월 d일\n")))

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

fun getFirstDayOfWeek(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun getLastDayOfWeek(date: LocalDate): LocalDate {
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

@Composable
fun getWeekString(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
    return buildAnnotatedString {
        append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

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

fun getFirstDayOfMonth(date: LocalDate): LocalDate {
    val yearMonth = YearMonth.from(date)
    return yearMonth.atDay(1)
}

fun getLastDayOfMonth(date: LocalDate): LocalDate {
    val yearMonth = YearMonth.from(date)
    return yearMonth.atEndOfMonth()
}

fun getMonthString(date: LocalDate): AnnotatedString {
    return buildAnnotatedString {
        append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월")))
    }
}

//fun getDate1yearAgo(date: LocalDate): LocalDate {
//    val oneYearAgo = date.minusDays(364)
////    return oneYearAgo.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
//    return oneYearAgo
//}