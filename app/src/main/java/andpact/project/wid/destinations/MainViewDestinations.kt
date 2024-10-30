package andpact.project.wid.destinations

import andpact.project.wid.R

sealed class MainViewDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int
) {
    object HomeViewDestination : MainViewDestinations(
        route = "home_view",
        icon = R.drawable.baseline_home_24
    )
    object WiDToolViewDestination : MainViewDestinations(
        route = "wid_tool_view",
        icon = R.drawable.baseline_alarm_24
    )
    object WiDDisplayViewDestination : MainViewDestinations(
        route = "wid_display_view",
        icon = R.drawable.baseline_timelapse_24
    )
//    object DiaryDisplayViewDestination : MainViewDestinations(
//        route = "diary_display_view",
//        icon = R.drawable.baseline_calendar_month_24
//    )
}