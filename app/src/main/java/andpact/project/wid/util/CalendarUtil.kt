package andpact.project.wid.util

import andpact.project.wid.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth

fun formatTimerTime(time: Long): String {
    val hours = (time / 3600000).toString().padStart(1, '0')
    val minutes = ((time % 3600000) / 60000).toString().padStart(2, '0')
    val seconds = ((time % 60000) / 1000).toString().padStart(2, '0')
//    val milliseconds = (time % 1000 / 10).toString().padStart(2, '0')
    return "$hours:$minutes:$seconds"
}

fun formatStopWatchTime(time: Long): AnnotatedString {
    val hours = (time / 3_600_000).toInt()
    val minutes = ((time % 3_600_000) / 60_000).toInt()
    val seconds = ((time % 60_000) / 1_000).toInt()

    val hoursText = hours.toString().padStart(1, '0')

    val minutesText = if (0 < hours) {
        minutes.toString().padStart(2, '0')
    } else {
        minutes.toString().padStart(1, '0')
    }

    val secondsText = if (0 < minutes || 0 < hours) {
        seconds.toString().padStart(2, '0')
    } else {
        seconds.toString().padStart(1, '0')
    }

    return buildAnnotatedString {
        withStyle(style = ParagraphStyle(lineHeight = 80.sp)) {
            if (0 < hours) {
                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                    append(hoursText)
                }

                withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                    append("h\n")
                }
            }

            if (0 < minutes || 0 < hours) {
                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                    append(minutesText)
                }

                withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                    append("m\n")
                }
            }

            withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                append(secondsText)
            }

            withStyle(style = SpanStyle(fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                append("s\n")
            }
        }
    }
}

fun formatDuration(duration: Duration, mode: Int): String {
    // mode 0. HH:mm:ss (10:30:30)
    // mode 1. H시간 (10.5시간), m분 (30분)
    // mode 2. H시간 m분 (10시간 30분)
    // mode 3. H시간 m분 s초 (10시간 30분 30초)

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60).toInt()
    val seconds = (duration.seconds % 60).toInt()

    return when (mode) {
        0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        1 -> {
            val totalHours = hours + (minutes / 60.0)
            when {
                hours >= 1 && minutes == 0 -> "${hours}시간"
                hours >= 1000 -> "${String.format("%.2f", totalHours).substring(0, 6)}시간" // 시간의 자리 수가 늘어남에 따라 문자열을 다르게 잘라냄.
                hours >= 100 -> "${String.format("%.2f", totalHours).substring(0, 5)}시간"
                hours >= 10 -> "${String.format("%.2f", totalHours).substring(0, 4)}시간"
                hours >= 1 -> "${String.format("%.2f", totalHours).substring(0, 3)}시간"
                minutes >= 1 -> "${minutes}분"
                seconds >= 1 -> "${seconds}초"
                else -> "기록 없음"
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

fun getFirstDayOfMonth(date: LocalDate): LocalDate {
    val yearMonth = YearMonth.from(date)
    return yearMonth.atDay(1)
}

fun getDate1yearAgo(date: LocalDate): LocalDate {
    val oneYearAgo = date.minusDays(364)
//    return oneYearAgo.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    return oneYearAgo
}