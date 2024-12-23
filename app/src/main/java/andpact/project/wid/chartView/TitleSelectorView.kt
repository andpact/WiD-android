package andpact.project.wid.chartView

import andpact.project.wid.R
import andpact.project.wid.model.Title
import andpact.project.wid.ui.theme.Typography
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TitleSelectorView(
    modifier: Modifier = Modifier,
    currentTitle: Title, // 현재 선택된 제목
    onTitleSelected: (title: Title) -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Text(
                        text = "제목 선택", // 얘를 파라미터로 받아 다르게 사용할 수 있음
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )

            HorizontalDivider()

            LazyColumn(
                content = {
                    items(Title.values().drop(1).size) { index: Int -> // drop(1) 사용하여 첫 번째 요소 제외
                        val itemTitle = Title.values().drop(1)[index] // Title enum에서 첫 번째 값 제외

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    onTitleSelected(itemTitle)
                                }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            content = {
                                Image(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .size(40.dp),
                                    painter = painterResource(id = itemTitle.smallImage),
                                    contentDescription = itemTitle.kr
                                )

                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = itemTitle.kr,
                                    style = Typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                RadioButton(
                                    selected = currentTitle == itemTitle,
                                    onClick = null
                                )
                            }
                        )
                    }
                }
            )
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun TitleSelectorPreview() {
//    TitleSelectorView(
//        currentTitle = Title.EXERCISE,
//        onTitleSelected = {
//
//        }
//    )
//}