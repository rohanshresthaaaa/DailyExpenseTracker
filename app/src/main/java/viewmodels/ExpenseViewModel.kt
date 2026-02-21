package com.rohan.dailyexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.dailyexpensetracker.data.db.AppDatabase
import com.rohan.dailyexpensetracker.data.model.Expense
import com.rohan.dailyexpensetracker.utils.DateRanges
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).expenseDao()

    val expenses = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotal = dao.observeTotalBetween(
        DateRanges.todayRange().first,
        DateRanges.todayRange().second
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val weekTotal = dao.observeTotalBetween(
        DateRanges.weekRange().first,
        DateRanges.weekRange().second
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addExpense(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            dao.insert(
                Expense(
                    amount = amount,
                    category = category,
                    description = description,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            dao.delete(expense)
        }
    }
}