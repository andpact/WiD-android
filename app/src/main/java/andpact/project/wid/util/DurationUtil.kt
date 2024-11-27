package andpact.project.wid.util

import andpact.project.wid.ui.theme.chivoMonoBlackItalic
import android.util.Log
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp
import java.time.Duration
import kotlin.random.Random

fun getStopwatchDurationString(duration: Duration): AnnotatedString {
//    Log.d("DurationUtil", "getStopwatchDurationString executed")

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

fun getTimerDurationString(duration: Duration): AnnotatedString {
//    Log.d("DurationUtil", "getTimerDurationString executed")

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

fun getDurationString(duration: Duration): String {
//    Log.d("DurationUtil", "getDurationString executed")
//    "H시간 m분 s초"

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

fun getDurationStringEN(duration: Duration): String {
//    Log.d("DurationUtil", "getDurationStringEN executed")
//    "Hh mm ss"

    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)
    val seconds = (duration.seconds % 60)

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        if (seconds > 0) append("${seconds}s")
    }.trim()
}

fun getDurationPercentageStringOfDay(duration: Duration): String {
//    Log.d("DurationUtil", "getDurationPercentageStringOfDay executed")

    val totalSecondsInDay = 24 * 60 * 60
    val durationInSeconds = duration.seconds

    val percentage = (durationInSeconds.toFloat() / totalSecondsInDay) * 100

    return if (percentage % 1.0 == 0.0) {
        "${percentage.toInt()}%"
    } else {
        "${String.format("%.1f", percentage)}%"
    }
}

fun getDurationPercentageStringOfWeek(duration: Duration): String {
//    Log.d("DurationUtil", "getDurationPercentageStringOfWeek executed")

    val totalSecondsInWeek = 7 * 24 * 60 * 60
    val durationInSeconds = duration.seconds

    val percentage = (durationInSeconds.toFloat() / totalSecondsInWeek) * 100

    return if (percentage % 1.0 == 0.0) {
        "${percentage.toInt()}%"
    } else {
        "${String.format("%.1f", percentage)}%"
    }
}

fun getCountPercentageString(count: Int, totalCount: Int): String {
//    Log.d("DurationUtil", "getCountPercentageString executed")

    if (totalCount == 0) return "0%"

    val percentage = (count.toFloat() / totalCount) * 100

    return if (percentage % 1 == 0f) {
        "${percentage.toInt()}%"
    } else {
        "${String.format("%.1f", percentage).trimEnd('0').trimEnd('.')}%"
    }
}

fun getDurationPercentageStringOfTotalDuration(duration: Duration, totalDuration: Duration): String {
//    Log.d("DurationUtil", "getDurationPercentageStringOfTotalDuration executed")

    if (totalDuration.isZero) return "0%"

    val durationInSeconds = duration.seconds
    val totalDurationInSeconds = totalDuration.seconds

    // 퍼센트 계산
    val percentage = (durationInSeconds.toFloat() / totalDurationInSeconds) * 100

    // 소수점 첫째 자리까지 표시, 불필요한 0 제거
    return if (percentage % 1 == 0f) {
        "${percentage.toInt()}%"
    } else {
        "${String.format("%.1f", percentage).trimEnd('0').trimEnd('.')}%"
    }
}