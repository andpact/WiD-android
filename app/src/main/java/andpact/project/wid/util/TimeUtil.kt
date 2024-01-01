package andpact.project.wid.util

import andpact.project.wid.R
import andpact.project.wid.ui.theme.chivoMonoBlackItalic
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import java.time.Duration

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

fun formatTime(time: Long, pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(Date(time))
}

fun formatTime(time: LocalTime, patten: String): String {
    return time.format(DateTimeFormatter.ofPattern(patten))
}

fun formatTimerTime(time: Long): AnnotatedString {
    val hours = (time / 3_600_000).toInt()
    val minutes = ((time % 3_600_000) / 60_000).toInt()
    val seconds = ((time % 60_000) / 1_000).toInt()

    val hoursText = hours.toString()
    val minutesText = minutes.toString().padStart(2, '0')
    val secondsText = seconds.toString().padStart(2, '0')

    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 60.sp, fontFamily = chivoMonoBlackItalic)) {
            append(hoursText)
            append(":")
            append(minutesText)
            append(":")
            append(secondsText)
        }
    }
}

fun formatStopWatchTime(time: Long): AnnotatedString {
    val hours = (time / 3_600_000).toInt()
    val minutes = ((time % 3_600_000) / 60_000).toInt()
    val seconds = ((time % 60_000) / 1_000).toInt()

    val hoursText = hours.toString()
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
                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                    append(hoursText + "\n")
                }
            }

            if (0 < minutes || 0 < hours) {
                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                    append(minutesText + "\n")
                }
            }

            withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
                append(secondsText + "\n")
            }
        }
    }
}