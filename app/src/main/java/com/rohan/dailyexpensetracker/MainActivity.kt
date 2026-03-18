package com.rohan.dailyexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rohan.dailyexpensetracker.ui.AppRoutes
import com.rohan.dailyexpensetracker.ui.screens.AddExpenseScreen
import com.rohan.dailyexpensetracker.ui.screens.ForgotPasswordScreen
import com.rohan.dailyexpensetracker.ui.screens.HomeScreen
import com.rohan.dailyexpensetracker.ui.screens.LoginScreen
import com.rohan.dailyexpensetracker.ui.screens.MonthlyTrendScreen
import com.rohan.dailyexpensetracker.ui.screens.RegisterScreen
import com.rohan.dailyexpensetracker.ui.theme.DailyExpenseTrackerTheme
import com.rohan.dailyexpensetracker.viewmodel.AuthViewModel
import com.rohan.dailyexpensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    val nav = rememberNavController()
    val expenseVm: ExpenseViewModel = viewModel()
    val authVm: AuthViewModel = viewModel()
    val isLoggedIn by authVm.authState.collectAsState()
    var darkTheme by rememberSaveable { mutableStateOf(false) }

    DailyExpenseTrackerTheme(darkTheme = darkTheme) {
        NavHost(
            navController = nav,
            startDestination = if (isLoggedIn) AppRoutes.HOME else AppRoutes.LOGIN
        ) {
            composable(AppRoutes.LOGIN) {
                LoginScreen(
                    authViewModel = authVm,
                    onLoginSuccess = {
                        nav.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onGoToRegister = {
                        nav.navigate(AppRoutes.REGISTER)
                    },
                    onGoToForgotPassword = {
                        nav.navigate(AppRoutes.FORGOT_PASSWORD)
                    }
                )
            }

            composable(AppRoutes.REGISTER) {
                RegisterScreen(
                    authViewModel = authVm,
                    onRegisterSuccess = {
                        nav.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onGoToLogin = {
                        nav.popBackStack()
                    }
                )
            }

            composable(AppRoutes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    authViewModel = authVm,
                    onBackToLogin = {
                        nav.popBackStack()
                    }
                )
            }

            composable(AppRoutes.HOME) {
                HomeScreen(
                    vm = expenseVm,
                    onAddClick = { nav.navigate(AppRoutes.ADD) },
                    onExpenseClick = { expense ->
                        nav.navigate(AppRoutes.editRoute(expense.id))
                    },
                    onLogoutClick = {
                        authVm.logout()
                        nav.navigate(AppRoutes.LOGIN) {
                            popUpTo(AppRoutes.HOME) { inclusive = true }
                        }
                    },
                    onTrendClick = {
                        nav.navigate(AppRoutes.TREND)
                    },
                    onToggleTheme = {
                        darkTheme = !darkTheme
                    },
                    isDarkTheme = darkTheme
                )
            }

            composable(AppRoutes.ADD) {
                AddExpenseScreen(
                    vm = expenseVm,
                    expenseId = null,
                    onDone = { nav.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.EDIT,
                arguments = listOf(
                    navArgument("expenseId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getLong("expenseId")

                AddExpenseScreen(
                    vm = expenseVm,
                    expenseId = expenseId,
                    onDone = { nav.popBackStack() }
                )
            }

            composable(AppRoutes.TREND) {
                MonthlyTrendScreen(
                    vm = expenseVm,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}