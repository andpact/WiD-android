package andpact.project.wid.fragment

import andpact.project.wid.service.WiDService
import andpact.project.wid.util.formatDuration
import andpact.project.wid.util.titleMap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.format.DateTimeFormatter

@Composable
fun WiDView(wiDId: Long, navController: NavController) {
    val wiDService = WiDService(context = LocalContext.current)
    val wiD = wiDService.readWiDById(wiDId)

    if (wiD == null) {
        Text(text = "WiD not found")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "WiD",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )

//                val titleColorId = DataMapsUtil.colorMap[wiD.title]
//                if (titleColorId != null) {
//                    val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, titleColorId))
//                    Box(
//                        modifier = Modifier
//                            .size(20.dp)
//                            .background(color = backgroundColor, shape = RoundedCornerShape(18.dp))
//                    )
//                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "날짜",
                    style = TextStyle(fontSize = 30.sp)
                )
                Text(
                    modifier = Modifier.padding(8.dp).weight(1.0F),
                    text = wiD.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd (E)")),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "제목",
                    style = TextStyle(fontSize = 30.sp)
                )
                Text(
                    modifier = Modifier.padding(8.dp).weight(1.0F),
                    text = titleMap[wiD.title] ?: wiD.title,
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "시작",
                    style = TextStyle(fontSize = 30.sp)
                )
                Text(
                    modifier = Modifier.padding(8.dp).weight(1.0F),
                    text = wiD.start.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "종료",
                    style = TextStyle(fontSize = 30.sp)
                )
                Text(
                    modifier = Modifier.padding(8.dp).weight(1.0F),
                    text = wiD.finish.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "경과",
                    style = TextStyle(fontSize = 30.sp)
                )
                Text(
                    modifier = Modifier.padding(8.dp).weight(1.0F),
                    text = formatDuration(wiD.duration, mode = 2),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }
        }

        Row(
            modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                },
                modifier = Modifier
                    .weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete")
            }

            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .weight(1f)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WiDViewPreview() {
//    val sampleWiD = WiD(
//        id = 0,
//        date = LocalDate.now(),
//        title = "STUDY",
//        start = LocalTime.of(10, 0, 0),
//        finish = LocalTime.of(12, 0, 0),
//        durationMillis = 2 * 60 * 60 * 1000, // 2 hours in milliseconds
//        detail = "Studying for exams"
//    )
//
//    WiDView(0)
//}