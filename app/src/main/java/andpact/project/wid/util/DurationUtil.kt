package andpact.project.wid.util

import android.util.Log
import java.time.Duration

/**
 * 소요 시간(Duration)의 형식은 'H시간 m분 s초'
 */
fun getDurationString(duration: Duration, mode: Int): String {
    Log.d("DurationUtil", "getDurationString executed")

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