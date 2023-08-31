package andpact.project.wid.util

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

fun formatDuration(duration: Duration, mode: Int): String {
    // mode 1. HH:mm:ss
    // mode 2. H시간 m분
    // mode 3. H시간 m분 s초

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60).toInt()
    val seconds = (duration.seconds % 60).toInt()

    return when (mode) {
        0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        1 -> {
            when {
                hours > 0 && minutes == 0 && seconds == 0 -> String.format("%d시간", hours)
                hours > 0 && minutes > 0 && seconds == 0 -> String.format("%d시간 %d분", hours, minutes)
                hours > 0 && minutes == 0 && seconds > 0 -> String.format("%d시간 %d초", hours, seconds)
                hours > 0 -> String.format("%d시간 %d분", hours, minutes)
                minutes > 0 && seconds == 0 -> String.format("%d분", minutes)
                minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
                else -> String.format("%d초", seconds)
            }
        }
        2 -> {
            when {
                hours > 0 && minutes == 0 && seconds == 0 -> String.format("%d시간", hours)
                hours > 0 && minutes > 0 && seconds == 0 -> String.format("%d시간 %d분", hours, minutes)
                hours > 0 && minutes == 0 && seconds > 0 -> String.format("%d시간 %d초", hours, seconds)
                hours > 0 -> String.format("%d시간 %d분 %d초", hours, minutes, seconds)
                minutes > 0 && seconds == 0 -> String.format("%d분", minutes)
                minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
                else -> String.format("%d초", seconds)
            }
        }
        else -> throw IllegalArgumentException("Invalid mode value")
    }
}

fun getFirstDayOfWeek(date: LocalDate): LocalDate {
    val dayOfWeek = date.dayOfWeek
    val daysToSubtract = (dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

    return date.minusDays(daysToSubtract.toLong())
}

fun getFirstDayOfMonth(date: LocalDate): LocalDate {
    val yearMonth = YearMonth.from(date)
    return yearMonth.atDay(1)
}

fun getWeekNumber(date: LocalDate): Int {
    val weekFields = WeekFields.of(java.util.Locale.getDefault())
    return date.get(weekFields.weekOfWeekBasedYear())
}