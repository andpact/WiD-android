package andpact.project.wid.fragment

import andpact.project.wid.R
import andpact.project.wid.model.Diary
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun PeriodBasedFragment() {
    // 날짜
    val today = LocalDate.now()
    var currentDate by remember { mutableStateOf(today) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var finishDate by remember { mutableStateOf(LocalDate.now()) }

    // 기간(0 : 일주일, 1: 한달)
    var selectedPeriod by remember { mutableStateOf(0) }

    // 전체 화면
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "기간 표시",
                    overflow = TextOverflow.Ellipsis
                )

                Row{
                    IconButton(
                        onClick = {
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                            contentDescription = "date"
                        )
                    }

                    IconButton(
                        onClick = {
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_textsms_24),
                            contentDescription = "date"
                        )
                    }

                    IconButton(
                        onClick = {
                            currentDate = today
                        },
                        enabled = currentDate != today,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Today"
                        )
                    }

                    IconButton(
                        onClick = {
                            currentDate = currentDate.minusDays(1)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous day"
                        )
                    }

                    IconButton(
                        onClick = {
                            currentDate = currentDate.plusDays(1)
                        },
                        enabled = currentDate != today
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next day"
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "시간 기록",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.black_han_sans_regular))
                    )
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(text = "차트")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                },
            ) {
                Text(text = "일주일")
            }

            TextButton(
                onClick = {
                },
            ) {
                Text(text = "제목")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PeriodBasedFragmentPreview() {
    PeriodBasedFragment()
}