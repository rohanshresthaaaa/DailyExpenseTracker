package com.rohan.dailyexpensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.dailyexpensetracker.data.db.AppDatabase
import com.rohan.dailyexpensetracker.data.model.Expense
import com.rohan.dailyexpensetracker.utils.DateRanges
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyTrendItem(
    val label: String,
    val total: Double,
    val dayStartMillis: Long
)

class ExpenseViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).expenseDao()

    val expenses = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTotal = dao.observeTotalBetween(
        DateRanges.todayRange().first,
        DateRanges.todayRange().second
    ).map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val weekTotal = dao.observeTotalBetween(
        DateRanges.weekRange().first,
        DateRanges.weekRange().second
    ).map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthTotal = dao.observeTotalBetween(
        DateRanges.monthRange().first,
        DateRanges.monthRange().second
    ).map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentMonthTrend = expenses
        .map { list ->
            val start = DateRanges.monthRange().first
            val end = DateRanges.monthRange().second
            val sdf = SimpleDateFormat("MMM d", Locale.US)

            list.filter { it.createdAt in start until end }
                .groupBy { startOfDay(it.createdAt) }
                .toSortedMap()
                .map { (dayMillis, dayExpenses) ->
                    DailyTrendItem(
                        label = sdf.format(Date(dayMillis)),
                        total = dayExpenses.sumOf { it.amount },
                        dayStartMillis = dayMillis
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            dao.update(expense)
        }
    }

    fun getExpenseById(id: Long): Expense? {
        return expenses.value.firstOrNull { it.id == id }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            dao.delete(expense)
        }
    }

    private fun startOfDay(timeMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}