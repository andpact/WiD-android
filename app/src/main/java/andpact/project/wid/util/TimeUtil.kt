package andpact.project.wid.util

import andpact.project.wid.R
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
        if (0 < hours) {
            withStyle(style = SpanStyle(fontSize = 70.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                append(hoursText)
            }

            withStyle(style = SpanStyle(fontSize = 20.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                append("h ")
            }
        }

        if (0 < minutes || 0 < hours) {
            withStyle(style = SpanStyle(fontSize = 70.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                append(minutesText)
            }

            withStyle(style = SpanStyle(fontSize = 20.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                append("m ")
            }
        }

        withStyle(style = SpanStyle(fontSize = 70.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
            append(secondsText)
        }

        withStyle(style = SpanStyle(fontSize = 20.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
            append("s")
        }
    }
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

                withStyle(style = SpanStyle(fontSize = 20.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                    append("h\n")
                }
            }

            if (0 < minutes || 0 < hours) {
                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                    append(minutesText)
                }

                withStyle(style = SpanStyle(fontSize = 20.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                    append("m\n")
                }
            }

            withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = FontFamily(Font(R.font.wellfleet_regular)))) {
                append(secondsText)
            }

            withStyle(style = SpanStyle(fontSize = 20.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.ubuntu_mono_regular)))) {
                append("s\n")
            }
        }
    }
}