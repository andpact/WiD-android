package andpact.project.wid.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

fun getDayString(date: LocalDate): AnnotatedString {
    return buildAnnotatedString {
        append(date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

        withStyle(
            style = SpanStyle(
                color = when (date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
                }
            )
        ) {
            append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
        }
        append(")")
    }
}

//fun getDayStringWith2Lines(date: LocalDate): AnnotatedString {
//    return buildAnnotatedString {
//        append(date.format(DateTimeFormatter.ofPattern("yyyy년\n")))
//        append(date.format(DateTimeFormatter.ofPattern("M월 d일 (")))
//
//        withStyle(
//            style = SpanStyle(
//                color = when (date.dayOfWeek) {
//                    DayOfWeek.SATURDAY -> Color.Blue
//                    DayOfWeek.SUNDAY -> Color.Red
//                    else -> Color.Black
//                }
//            )
//        ) {
//            append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
//        }
//        append(")")
//    }
//}

fun getDayStringWith3Lines(date: LocalDate): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = ParagraphStyle(lineHeight = 30.sp)) {
            append(date.format(DateTimeFormatter.ofPattern("yyyy년\nM월 d일\n")))

            withStyle(
                style = SpanStyle(
                    color = when (date.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue
                        DayOfWeek.SUNDAY -> Color.Red
                        else -> Color.Black
                    }
                )
            ) {
                append(date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
            }
            append("요일")
        }
    }
}

fun getFirstDayOfWeek(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun getLastDayOfWeek(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
}

fun getWeekString(date: LocalDate): AnnotatedString {
    val firstDayOfWeek = getFirstDayOfWeek(date)
    val lastDayOfWeek = getLastDayOfWeek(date)

    return buildAnnotatedString {
        append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

        withStyle(
            style = SpanStyle(
                color = when (firstDayOfWeek.dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
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
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
                }
            )
        ) {
            append(lastDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)))
        }

        append(")")
    }
}

fun getWeekString(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
    return buildAnnotatedString {
        append(firstDayOfWeek.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")))

        withStyle(
            style = SpanStyle(
                color = when (firstDayOfWeek.dayOfWeek) {
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
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
                    DayOfWeek.SATURDAY -> Color.Blue
                    DayOfWeek.SUNDAY -> Color.Red
                    else -> Color.Black
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