package andpact.project.wid.util

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

fun formatTime(time: Long): String {
    val hours = (time / 3600000).toString().padStart(2, '0')
    val minutes = ((time % 3600000) / 60000).toString().padStart(2, '0')
    val seconds = ((time % 60000) / 1000).toString().padStart(2, '0')
//    val milliseconds = (time % 1000 / 10).toString().padStart(2, '0')
    return "$hours:$minutes:$seconds"
}

fun formatDuration(duration: Duration, mode: Int): String {
    // mode 0. HH:mm:ss (10:30:30)
    // mode 1. H시간 (10.5시간), m분 (30.5분)
    // mode 2. H시간 m분 (10시간 30분)
    // mode 3. H시간 m분 s초 (10시간 30분 30초)

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60).toInt()
    val seconds = (duration.seconds % 60).toInt()

    return when (mode) {
        0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)

//        1 -> {
//            val totalHours = hours.toDouble() + (minutes.toDouble() / 60.0)
//            val totalMinutes = minutes.toDouble() + (seconds.toDouble() / 60.0)
//
//            when {
//                totalHours >= 1.1 -> String.format("%.1f시간", totalHours)
//                totalHours >= 1.0 -> String.format("%d시간", hours)
//                totalMinutes >= 1.1 -> String.format("%.1f분", totalMinutes)
//                totalMinutes >= 1.0 -> String.format("%d분", minutes)
//                else -> String.format("%d초", seconds)
//            }
//        }

        1 -> {
            val totalHours = hours.toDouble() + (minutes.toDouble() / 60.0)
            val totalMinutes = minutes.toDouble() + (seconds.toDouble() / 60.0)

            when {
                totalHours >= 1.1 -> {
                    val formattedTotalHours = if (totalHours % 1.0 == 0.0) {
                        totalHours.toInt().toString()
                    } else {
                        String.format("%.1f", totalHours)
                    }
                    "${formattedTotalHours}시간"
                }
                totalHours >= 1.0 -> "${hours}시간"
                totalMinutes >= 1.1 -> {
                    val formattedTotalMinutes = if (totalMinutes % 1.0 == 0.0) {
                        totalMinutes.toInt().toString()
                    } else {
                        String.format("%.1f", totalMinutes)
                    }
                    "${formattedTotalMinutes}분"
                }
                totalMinutes >= 1.0 -> "${minutes}분"
                else -> "${seconds}초"
            }
        }

        2 -> {
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
        3 -> {
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

fun getLastDayOfWeek(date: LocalDate): LocalDate {
    val firstDayOfWeek = getFirstDayOfWeek(date)
    return firstDayOfWeek.plusDays(6)
}

fun getFirstDayOfMonth(date: LocalDate): LocalDate {
    val yearMonth = YearMonth.from(date)
    return yearMonth.atDay(1)
}

fun getWeekNumber(date: LocalDate): Int {
    val weekFields = WeekFields.of(java.util.Locale.getDefault())
    return date.get(weekFields.weekOfWeekBasedYear())
}