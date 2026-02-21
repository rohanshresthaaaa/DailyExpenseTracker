package com.rohan.dailyexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rohan.dailyexpensetracker.ui.AppRoutes
import com.rohan.dailyexpensetracker.ui.screens.AddExpenseScreen
import com.rohan.dailyexpensetracker.ui.screens.HomeScreen
import com.rohan.dailyexpensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    val nav = rememberNavController()
    val vm: ExpenseViewModel = viewModel()

    NavHost(navController = nav, startDestination = AppRoutes.HOME) {
        composable(AppRoutes.HOME) {
            HomeScreen(
                vm = vm,
                onAddClick = { nav.navigate(AppRoutes.ADD) }
            )
        }
        composable(AppRoutes.ADD) {
            AddExpenseScreen(
                vm = vm,
                onDone = { nav.popBackStack() }
            )
        }
    }
}