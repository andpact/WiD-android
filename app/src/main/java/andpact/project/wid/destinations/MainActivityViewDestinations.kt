package andpact.project.wid.destinations

sealed class MainActivityViewDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int? = null
) {
    object SplashViewDestination : MainActivityViewDestinations(route = "splash_view")
    object AuthenticationViewDestination : MainActivityViewDestinations(route = "authentication_view")
    object MainViewDestination : MainActivityViewDestinations(route = "main_view")
    object StopwatchViewDestination : MainActivityViewDestinations(route = "stopwatch_view")
    object TimerViewDestination : MainActivityViewDestinations(route = "timer_view")
//    object PomodoroViewDestination : MainActivityViewDestinations(route = "pomodoro_view")
//    object NewWiDViewDestination : MainActivityViewDestinations(route = "newWiD_view")
    object WiDViewDestination : MainActivityViewDestinations(route = "wid_view")
}