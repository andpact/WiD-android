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
    object WiDListViewDestination : MainViewDestinations(
        route = "wid_list_view",
        title = "리스트",
        selectedIcon = R.drawable.baseline_article_24,
        unselectedIcon = R.drawable.outline_article_24
    )
    object MyPageViewDestination : MainViewDestinations(
        route = "my_page_view",
        title = "내 페이지",
        selectedIcon = R.drawable.baseline_account_circle_24,
        unselectedIcon = R.drawable.outline_account_circle_24
    )
}