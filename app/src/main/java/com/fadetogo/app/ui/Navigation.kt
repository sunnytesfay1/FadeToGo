package com.fadetogo.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fadetogo.app.ui.auth.SplashScreen
import com.fadetogo.app.ui.auth.LoginScreen
import com.fadetogo.app.ui.auth.RegisterScreen
import com.fadetogo.app.ui.auth.ForgotPasswordScreen
import com.fadetogo.app.ui.customer.CustomerHomeScreen
import com.fadetogo.app.ui.barber.BarberDashboardScreen
import com.fadetogo.app.ui.customer.CustomerBookingScreen
import com.fadetogo.app.ui.customer.CustomerHistoryScreen
import com.fadetogo.app.ui.customer.CustomerInboxScreen
import com.fadetogo.app.ui.shared.ChatScreen
import com.fadetogo.app.viewmodel.AuthViewModel

// defines all possible screen routes in the app as constants
// using constants prevents typos when navigating between screens
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
    const val CHAT = "chat"
}

@Composable
fun FadeToGoNavigation() {
    // creates the navigation controller that manages the back stack
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    // NavHost is the container that swaps screens based on the current route
    // startDestination is the first screen shown when the app launches
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
                }
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
                    // route to different home screen based on role
                    if (role == "barber") {
                        navController.navigate(Routes.BARBER_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.CUSTOMER_HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
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
                }
            )
        }

        // FORGOT PASSWORD SCREEN
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // CUSTOMER HOME SCREEN - placeholder until we build it
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
                }
            )
        }

        // BARBER DASHBOARD SCREEN - placeholder until we build it
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
                }
            )
        }

        // CUSTOMER HISTORY SCREEN - placeholder
        composable(Routes.CUSTOMER_HISTORY) {
            CustomerHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // CUSTOMER INBOX SCREEN - placeholder
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

        // CUSTOMER BOOKING SCREEN - placeholder
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

        // CHAT SCREEN - shared between barber and customer
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