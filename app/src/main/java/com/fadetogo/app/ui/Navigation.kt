package com.fadetogo.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fadetogo.app.ui.auth.SplashScreen
import com.fadetogo.app.ui.auth.LoginScreen
import com.fadetogo.app.ui.auth.RegisterScreen
import com.fadetogo.app.ui.auth.ForgotPasswordScreen
import com.fadetogo.app.ui.customer.CustomerHomeScreen
import com.fadetogo.app.ui.customer.CustomerBookingScreen
import com.fadetogo.app.ui.customer.CustomerHistoryScreen
import com.fadetogo.app.ui.customer.CustomerInboxScreen
import com.fadetogo.app.ui.barber.BarberDashboardScreen
import com.fadetogo.app.ui.shared.ChatScreen
import com.fadetogo.app.viewmodel.AuthViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val CUSTOMER_HOME = "customer_home"
    const val BARBER_DASHBOARD = "barber_dashboard"
    const val CUSTOMER_BOOKING = "customer_booking"
    const val CUSTOMER_TRACKING = "customer_tracking"
    const val CUSTOMER_HISTORY = "customer_history"
    const val CUSTOMER_INBOX = "customer_inbox"
    const val BARBER_INCOMING = "barber_incoming"
    const val BARBER_ACTIVE_JOB = "barber_active_job"
    const val BARBER_EARNINGS = "barber_earnings"
    const val BARBER_INBOX = "barber_inbox"
    const val BARBER_SCHEDULE = "barber_schedule"
    const val BARBER_SETTINGS = "barber_settings"
    const val CHAT = "chat"
}

@Composable
fun FadeToGoNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // SPLASH SCREEN
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToCustomerHome = {
                    navController.navigate(Routes.CUSTOMER_HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToBarberDashboard = {
                    navController.navigate(Routes.BARBER_DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // LOGIN SCREEN
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onLoginSuccess = { role ->
                    if (role == "barber") {
                        navController.navigate(Routes.BARBER_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.CUSTOMER_HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // REGISTER SCREEN
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                },
                onRegisterSuccess = { role ->
                    if (role == "barber") {
                        navController.navigate(Routes.BARBER_DASHBOARD) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.CUSTOMER_HOME) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // FORGOT PASSWORD SCREEN
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        // CUSTOMER HOME SCREEN
        composable(Routes.CUSTOMER_HOME) {
            CustomerHomeScreen(
                onNavigateToBooking = {
                    navController.navigate(Routes.CUSTOMER_BOOKING)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.CUSTOMER_HISTORY)
                },
                onNavigateToInbox = {
                    navController.navigate(Routes.CUSTOMER_INBOX)
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CUSTOMER_HOME) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // BARBER DASHBOARD SCREEN
        composable(Routes.BARBER_DASHBOARD) {
            BarberDashboardScreen(
                onNavigateToIncoming = {
                    navController.navigate(Routes.BARBER_INCOMING)
                },
                onNavigateToEarnings = {
                    navController.navigate(Routes.BARBER_EARNINGS)
                },
                onNavigateToInbox = {
                    navController.navigate(Routes.BARBER_INBOX)
                },
                onNavigateToSchedule = {
                    navController.navigate(Routes.BARBER_SCHEDULE)
                },
                authViewModel = authViewModel
            )
        }

        // CUSTOMER HISTORY SCREEN
        composable(Routes.CUSTOMER_HISTORY) {
            CustomerHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // CUSTOMER INBOX SCREEN
        composable(Routes.CUSTOMER_INBOX) {
            CustomerInboxScreen(
                onNavigateToChat = { partnerId ->
                    navController.navigate("${Routes.CHAT}/$partnerId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // CUSTOMER BOOKING SCREEN
        composable(Routes.CUSTOMER_BOOKING) {
            CustomerBookingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConfirmation = {
                    navController.navigate(Routes.CUSTOMER_TRACKING)
                }
            )
        }

        // CHAT SCREEN
        composable("${Routes.CHAT}/{partnerId}") { backStackEntry ->
            val partnerId = backStackEntry.arguments?.getString("partnerId") ?: ""
            ChatScreen(
                partnerId = partnerId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}