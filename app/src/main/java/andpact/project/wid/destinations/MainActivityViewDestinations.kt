package andpact.project.wid.destinations

sealed class MainActivityViewDestinations(
    val route: String,
    val title: String? = null,
    val icon: Int? = null
) {
    object SplashViewDestination : MainActivityViewDestinations(route = "splash_view")
    object AuthenticationViewDestination : MainActivityViewDestinations(route = "authentication_view")
//    object SignUpViewDestination : MainActivityViewDestinations(route = "sign_up_view")
//    object SignInViewDestination : MainActivityViewDestinations(route = "sign_in_view")
    object MainViewDestination : MainActivityViewDestinations(route = "main_view")
    object NewWiDViewDestination : MainActivityViewDestinations(route = "newWiD_view")
    object WiDViewDestination : MainActivityViewDestinations(route = "wid_view")
//    object DiaryViewDestination : MainActivityViewDestinations(route = "diary_view")
    object SettingViewDestination : MainActivityViewDestinations(route = "setting_view")
}