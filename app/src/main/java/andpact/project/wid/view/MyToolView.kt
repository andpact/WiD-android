package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.CurrentTool
import andpact.project.wid.util.defaultToolCountMap
import andpact.project.wid.util.defaultToolDurationMap
import andpact.project.wid.viewModel.MyToolViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyToolView(myToolViewModel: MyToolViewModel = hiltViewModel()) {
    val TAG = "MyToolView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    val wiDToolCountMap = myToolViewModel.user.value?.wiDToolCountMap ?: defaultToolCountMap
//    val wiDToolDurationMap = myToolViewModel.user.value?.wiDToolDurationMap ?: defaultToolDurationMap

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            val sortedToolCountList = wiDToolCountMap.toList().sortedByDescending { (_, count) -> count }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
            ) {
                sortedToolCountList.forEach { (tool, count) ->
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        Text(
                            text = tool.name, // CurrentTool의 이름
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Icon(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(16.dp)
                                .size(24.dp),
                            painter = painterResource(
                                id = when (tool) {
                                    CurrentTool.STOPWATCH -> R.drawable.baseline_alarm_24
                                    CurrentTool.TIMER -> R.drawable.outline_timer_24
                                    CurrentTool.LIST -> R.drawable.baseline_table_rows_24
                                    else -> R.drawable.baseline_done_24 // 기본 아이콘으로 대체
                                }
                            ),
                            contentDescription = "${tool.name} 아이콘",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = count.toString(), // 해당 도구의 횟수
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}