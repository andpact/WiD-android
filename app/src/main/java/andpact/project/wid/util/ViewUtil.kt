package andpact.project.wid.util

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun createEmptyView(text: String): @Composable () -> Unit = {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
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
}

@Composable
fun createNoBackgroundEmptyView(text: String): @Composable () -> Unit = {
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