package andpact.project.wid.chartView

import andpact.project.wid.model.Category
import andpact.project.wid.util.Title
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CategoryTitleSelectorView(
    modifier: Modifier = Modifier,
    onCategoryTitleSelected: (Category, Title) -> Unit
) {
    /** 1차로 카테고리 선택 후 2차로 제목 선택하도록 */
}