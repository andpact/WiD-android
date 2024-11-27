package andpact.project.wid.destinations

import andpact.project.wid.R

sealed class MainViewDestinations(
    val route: String,
    val title: String? = null,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    object HomeViewDestination : MainViewDestinations(
        route = "home_view",
        title = "홈",
        selectedIcon = R.drawable.baseline_home_24,
        unselectedIcon = R.drawable.outline_home_24
    )
    object WiDToolViewDestination : MainViewDestinations(
        route = "wid_tool_view",
        title = "도구",
        selectedIcon = R.drawable.baseline_add_box_24,
        unselectedIcon = R.drawable.outline_add_box_24
    )
    object WiDDisplayViewDestination : MainViewDestinations(
        route = "wid_display_view",
        title = "리스트",
        selectedIcon = R.drawable.baseline_table_rows_24,
        unselectedIcon = R.drawable.outline_table_rows_24
    )
    object MyPageViewDestination : MainViewDestinations(
        route = "my_page_view",
        title = "내 페이지",
        selectedIcon = R.drawable.baseline_account_circle_24,
        unselectedIcon = R.drawable.outline_account_circle_24
    )
}