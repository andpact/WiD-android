package andpact.project.wid.util

import andpact.project.wid.ui.theme.chivoMonoBlackItalic
import android.util.Log
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import java.time.Duration

/** 소요 시간(Duration)의 형식은 'H시간 m분 s초' */
fun getDurationString(duration: Duration): String {
//    Log.d("DurationUtil", "getDurationString executed")

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60).toInt()
    val seconds = (duration.seconds % 60).toInt()

    return when {
        hours > 0 && minutes == 0 && seconds == 0 -> String.format("%d시간", hours)
        hours > 0 && minutes > 0 && seconds == 0 -> String.format("%d시간 %d분", hours, minutes)
        hours > 0 && minutes == 0 && seconds > 0 -> String.format("%d시간 %d초", hours, seconds)
        hours > 0 -> String.format("%d시간 %d분 %d초", hours, minutes, seconds)
        minutes > 0 && seconds == 0 -> String.format("%d분", minutes)
        minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
        else -> String.format("%d초", seconds)
    }
}

fun getDurationPercentageStringOfDay(duration: Duration): Float {
//    Log.d("DurationUtil", "getDurationPercentageStringOfDay executed")

    // 하루의 총 초
    val totalSecondsInDay = 24 * 60 * 60

    // 주어진 Duration의 초
    val durationInSeconds = duration.seconds

    // 퍼센트 계산 (백분율)
    val percentage = (durationInSeconds.toFloat() / totalSecondsInDay) * 100

    return percentage
}

fun getTimerTimeString(duration: Duration): AnnotatedString {
//    Log.d("DurationUtil", "getTimerTimeString executed")

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

fun getStopWatchTimeString(duration: Duration): AnnotatedString {
//    Log.d("DurationUtil", "getStopWatchTimeString executed")

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