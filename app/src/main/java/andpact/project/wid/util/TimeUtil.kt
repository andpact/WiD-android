package andpact.project.wid.util

import andpact.project.wid.ui.theme.chivoMonoBlackItalic
import android.util.Log
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 시간의 형식은 'HH:mm:ss'
 * TimeUtil의 단위는 MilliSecond를 사용 중
 */
//fun getTimeString(time: Long, pattern: String): String {
//    Log.d("TimeUtil", "getTimeString executed")
//
//    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
//    return dateFormat.format(Date(time))
//}

fun getTimeString(time: LocalTime, patten: String): String {
    Log.d("TimeUtil", "getTimeString executed")

    return time.format(DateTimeFormatter.ofPattern(patten))
}

//fun formatTimerTime(time: Long): AnnotatedString {
//    val hours = (time / 3_600_000).toInt()
//    val minutes = ((time % 3_600_000) / 60_000).toInt()
//    val seconds = ((time % 60_000) / 1_000).toInt()
//
//    val hoursText = hours.toString()
//    val minutesText = minutes.toString().padStart(2, '0')
//    val secondsText = seconds.toString().padStart(2, '0')
//
//    return buildAnnotatedString {
//        withStyle(style = SpanStyle(fontSize = 60.sp, fontFamily = chivoMonoBlackItalic)) {
//            append(hoursText)
//            append(":")
//            append(minutesText)
//            append(":")
//            append(secondsText)
//        }
//    }
//}

fun getHorizontalTimeString(time: Long): String {
    Log.d("TimeUtil", "getHorizontalTimeString executed")

    val hours = (time / 3_600_000).toInt()
    val minutes = ((time % 3_600_000) / 60_000).toInt()
    val seconds = ((time % 60_000) / 1_000).toInt()

    val hoursText = hours.toString()
    val minutesText = minutes.toString().padStart(2, '0')
    val secondsText = seconds.toString().padStart(2, '0')

    return "$hoursText:$minutesText:$secondsText"
}

fun getTimerTimeString(duration: Duration): AnnotatedString {
    Log.d("TimeUtil", "getTimerTimeString executed")

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60).toInt()
    val seconds = (duration.seconds % 60).toInt()

    val hoursText = hours.toString()
    val minutesText = minutes.toString().padStart(2, '0')
    val secondsText = seconds.toString().padStart(2, '0')

    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 60.sp, fontFamily = chivoMonoBlackItalic)) {
            append("$hoursText:$minutesText:$secondsText")
        }
    }
}

//fun getStopWatchTimeString(time: Long): AnnotatedString {
//    Log.d("TimeUtil", "getStopWatchTimeString executed")
//
//    val hours = (time / 3_600_000).toInt()
//    val minutes = ((time % 3_600_000) / 60_000).toInt()
//    val seconds = ((time % 60_000) / 1_000).toInt()
//
//    val hoursText = hours.toString()
//    val minutesText = if (0 < hours) {
//        minutes.toString().padStart(2, '0')
//    } else {
//        minutes.toString().padStart(1, '0')
//    }
//    val secondsText = if (0 < minutes || 0 < hours) {
//        seconds.toString().padStart(2, '0')
//    } else {
//        seconds.toString().padStart(1, '0')
//    }
//
//    return buildAnnotatedString {
//        withStyle(style = ParagraphStyle(lineHeight = 80.sp)) {
//            if (0 < hours) {
//                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
//                    append(hoursText + "\n")
//                }
//            }
//
//            if (0 < minutes || 0 < hours) {
//                withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
//                    append(minutesText + "\n")
//                }
//            }
//
//            withStyle(style = SpanStyle(fontSize = 100.sp, fontFamily = chivoMonoBlackItalic)) {
//                append(secondsText + "\n")
//            }
//        }
//    }
//}

fun getStopWatchTimeString(duration: Duration): AnnotatedString {
    Log.d("TimeUtil", "getStopWatchTimeString executed")

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60).toInt()
    val seconds = (duration.seconds % 60).toInt()

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