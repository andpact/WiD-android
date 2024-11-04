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
fun getTimeString(time: LocalTime, patten: String): String {
//    Log.d("TimeUtil", "getTimeString executed")

    return time.format(DateTimeFormatter.ofPattern(patten))
}