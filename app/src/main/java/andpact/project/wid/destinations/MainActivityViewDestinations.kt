package andpact.project.wid.destinations

sealed class MainActivityViewDestinations(val route: String) {
    object SplashViewDestination : MainActivityViewDestinations(route = "splash_view")
    object AuthenticationViewDestination : MainActivityViewDestinations(route = "authentication_view")
    object MainViewDestination : MainActivityViewDestinations(route = "main_view")
    object StopwatchViewDestination : MainActivityViewDestinations(route = "stopwatch_view")
    object TimerViewDestination : MainActivityViewDestinations(route = "timer_view")
//    object PomodoroViewDestination : MainActivityViewDestinations(route = "pomodoro_view")
    object WiDViewDestination : MainActivityViewDestinations(route = "wid_view")
    object TitlePickerViewDestination : MainActivityViewDestinations(route = "title_picker_view")
    object TimePickerViewDestination : MainActivityViewDestinations(route = "time_picker_view")
    object CityPickerViewDestination : MainActivityViewDestinations(route = "city_picker_view")
}