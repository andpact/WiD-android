package andpact.project.wid.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return buildAnnotatedString {
        if (date == today) {
            append("오늘")
        } else if (date == yesterday) {
            append("어제")
        } else {
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
}

fun getFirstDayOfWeek(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun getLastDayOfWeek(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
}

@Composable
fun getWeekString(date: LocalDate): AnnotatedString {
    val today = LocalDate.now()

    val firstDayOfThisWeek = getFirstDayOfWeek(today)
    val lastDayOfThisWeek = getLastDayOfWeek(today)

    val firstDayOfLastWeek = firstDayOfThisWeek.minusWeeks(1)
    val lastDayOfLastWeek = lastDayOfThisWeek.minusWeeks(1)

    val firstDayOfWeek = getFirstDayOfWeek(date)
    val lastDayOfWeek = getLastDayOfWeek(date)

    return buildAnnotatedString {
        if (date.isAfter(firstDayOfThisWeek) && date.isBefore(lastDayOfThisWeek)) {
            append("이번 주")
        } else if (date.isAfter(firstDayOfLastWeek) && date.isBefore(lastDayOfLastWeek)) {
            append("지난주")
        } else {
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
}

@Composable
fun getWeekString(firstDayOfWeek: LocalDate, lastDayOfWeek: LocalDate): AnnotatedString {
    val today = LocalDate.now()

    val firstDayOfThisWeek = getFirstDayOfWeek(today)
    val lastDayOfThisWeek = getLastDayOfWeek(today)

    val firstDayOfLastWeek = firstDayOfThisWeek.minusWeeks(1)
    val lastDayOfLastWeek = lastDayOfThisWeek.minusWeeks(1)

    return buildAnnotatedString {
        if (firstDayOfWeek == firstDayOfThisWeek && lastDayOfWeek == lastDayOfThisWeek) {
            append("이번 주")
        } else if (firstDayOfWeek == firstDayOfLastWeek && lastDayOfWeek == lastDayOfLastWeek) {
            append("지난주")
        } else {
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
fun getDate1yearAgo(date: LocalDate): LocalDate {
    val oneYearAgo = date.minusDays(364)
//    return oneYearAgo.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    return oneYearAgo
}