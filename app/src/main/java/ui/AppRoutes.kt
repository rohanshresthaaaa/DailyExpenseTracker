package com.rohan.dailyexpensetracker.ui

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val ADD = "add"
    const val EDIT = "edit/{expenseId}"
    const val TREND = "trend"

    fun editRoute(expenseId: Long): String = "edit/$expenseId"
}