package andpact.project.wid.util

import andpact.project.wid.ui.theme.Typography
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun getEmptyView(text: String): @Composable () -> Unit = {
    Log.d("ViewUtil", "getEmptyView executed")

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = text,
                style = Typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun getNoBackgroundEmptyViewWithMultipleLines(text: String): @Composable () -> Unit = {
    Log.d("ViewUtil", "getNoBackgroundEmptyViewWithMultipleLines executed")

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val lines = text.split("\n")
        lines.forEachIndexed { _, line ->
            Text(
                text = line,
                style = Typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}