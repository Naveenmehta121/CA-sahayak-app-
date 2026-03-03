package com.casahayak.app.ui.navigation

/**
 * All navigation routes in the app as sealed objects.
 * Using objects (not data classes) for routes without parameters,
 * and sealed classes with companion objects for routes that carry arguments.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Generator : Screen("generator/{featureType}") {
        fun createRoute(featureType: String) = "generator/$featureType"
    }
    object History : Screen("history")
    object Account : Screen("account")
    object Upgrade : Screen("upgrade")
}
