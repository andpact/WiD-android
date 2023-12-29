package andpact.project.wid.util

import andpact.project.wid.ui.theme.Typography
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun createEmptyView(text: String): @Composable () -> Unit = {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        Text(
            text = text,
            style = Typography.labelSmall
        )
    }
}

@Composable
fun createNoBackgroundEmptyViewWithMultipleLines(text: String): @Composable () -> Unit = {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val lines = text.split("\n")
        lines.forEachIndexed { _, line ->
            Text(
                text = line,
                style = Typography.labelSmall
            )
        }
    }
}