package com.casahayak.app.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.ui.account.AccountScreen
import com.casahayak.app.ui.auth.LoginScreen
import com.casahayak.app.ui.auth.RegisterScreen
import com.casahayak.app.ui.dashboard.DashboardScreen
import com.casahayak.app.ui.generator.GeneratorScreen
import com.casahayak.app.ui.history.HistoryScreen
import com.casahayak.app.ui.onboarding.OnboardingScreen
import com.casahayak.app.ui.splash.SplashScreen
import com.casahayak.app.ui.upgrade.UpgradeScreen
import javax.inject.Inject

/**
 * Root navigation graph for CA Sahayak.
 *
 * Flow:
 *   Splash → (user logged in?) → Dashboard
 *                              → Onboarding → Login / Register → Dashboard
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ──────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ───────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ────────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // ── Register ─────────────────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ── Dashboard ────────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToGenerator = { featureType ->
                    navController.navigate(Screen.Generator.createRoute(featureType))
                },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                onNavigateToUpgrade = { navController.navigate(Screen.Upgrade.route) }
            )
        }

        // ── Generator ────────────────────────────────────────────────────────
        composable(Screen.Generator.route) { backStackEntry ->
            val featureType = backStackEntry.arguments?.getString("featureType") ?: ""
            GeneratorScreen(
                featureType = featureType,
                onBack = { navController.popBackStack() },
                onNavigateToUpgrade = { navController.navigate(Screen.Upgrade.route) }
            )
        }

        // ── History ──────────────────────────────────────────────────────────
        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        // ── Account ──────────────────────────────────────────────────────────
        composable(Screen.Account.route) {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUpgrade = { navController.navigate(Screen.Upgrade.route) }
            )
        }

        // ── Upgrade ──────────────────────────────────────────────────────────
        composable(Screen.Upgrade.route) {
            UpgradeScreen(onBack = { navController.popBackStack() })
        }
    }
}
