package andpact.project.wid.destinations

import andpact.project.wid.R

sealed class MainViewDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int
) {
    object HomeViewDestination : MainViewDestinations(
        route = "home_view",
        title = "홈",
        icon = R.drawable.baseline_home_24
    )
    object WiDToolViewDestination : MainViewDestinations(
        route = "wid_tool_view",
        title = "도구",
        icon = R.drawable.baseline_add_box_24
    )
    object WiDDisplayViewDestination : MainViewDestinations(
        route = "wid_display_view",
        title = "리스트",
        icon = R.drawable.baseline_table_rows_24
    )
    object MyPageViewDestination : MainViewDestinations(
        route = "my_page_view",
        title = "마이 페이지",
        icon = R.drawable.baseline_account_circle_24
    )
}