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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WiDView(wiDId: Long, navController: NavController, buttonsVisible: MutableState<Boolean>) {
    val wiDService = WiDService(context = LocalContext.current)
    val wiD = wiDService.readWiDById(wiDId)

    if (wiD == null) {
        Text(text = "WiD not found")
        return
    }

    var updatedDetail by remember { mutableStateOf(wiD.detail) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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

                Row(
                    modifier = Modifier.weight(1f)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                        text = wiD.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd ")),
                    )

                    Text(text = "(")

                    Text(
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp)),
                        text = wiD.date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN)),
                        color = when (wiD.date.dayOfWeek) {
                            DayOfWeek.SATURDAY -> Color.Blue
                            DayOfWeek.SUNDAY -> Color.Red
                            else -> Color.Black
                        }
                    )

                    Text(text = ")")
                }
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
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
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
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
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
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
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
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1.0F),
                    text = formatDuration(wiD.duration, mode = 2),
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1.0F)
                        .padding(8.dp),
                    text = "설명",
                    style = TextStyle(fontSize = 30.sp),
                    textAlign = TextAlign.Start
                )

                IconToggleButton(
                    checked = isEditing,
                    onCheckedChange = { newValue ->
                        if (!newValue) {
                            val newDetail = updatedDetail
                            wiDService.updateWiDDetail(wiD.id, newDetail)
                        }
                        isEditing = newValue
                    },
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
                ) {
                    if (isEditing) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = "Done")
                    } else {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                    }
                }

            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = wiD.detail,
                onValueChange = { newText ->
                    updatedDetail = newText
                },
                minLines = 3,
                placeholder = {
                    Text(style = TextStyle(Color.Black),
                        text = "설명 입력..")
                },
//                readOnly = !isEditing,
                enabled = isEditing
            )
        }

        Row(
            modifier = Modifier
                .padding(0.dp, 8.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    wiDService.deleteWiDById(id = wiDId)
                    navController.popBackStack()

                    buttonsVisible.value = true
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

                    buttonsVisible.value = true
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
//    WiDView(0, NavController(LocalContext.current))
//}