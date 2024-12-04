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
fun getTimeString(time: LocalTime): String {
//    Log.d("TimeUtil", "getTimeString executed")

    return when (time) {
        LocalTime.MIDNIGHT -> "Start" // 00:00:00일 때
        LocalTime.MAX -> "End"       // 23:59:59일 때
        else -> time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) // 다른 경우 일반 시간 형식
    }
}