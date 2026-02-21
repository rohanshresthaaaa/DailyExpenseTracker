package com.rohan.dailyexpensetracker.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohan.dailyexpensetracker.data.model.Expense
import com.rohan.dailyexpensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: ExpenseViewModel,
    onAddClick: () -> Unit
) {
    val expenses by vm.expenses.collectAsState()
    val todayTotal by vm.todayTotal.collectAsState(initial = 0.0)
    val weekTotal by vm.weekTotal.collectAsState(initial = 0.0)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daily Expense Tracker") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Text("+") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            SummaryCard(
                todayTotal = todayTotal ?: 0.0,
                weekTotal = weekTotal ?: 0.0
            )

            Spacer(Modifier.height(12.dp))

            Text("Recent Expenses", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No expenses yet. Tap + to add one.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(expenses, key = { it.id }) { e ->
                        ExpenseRow(
                            expense = e,
                            onDelete = { vm.deleteExpense(e) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(todayTotal: Double, weekTotal: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Summary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Today:  $${"%.2f".format(todayTotal)}")
            Text("This week: $${"%.2f".format(weekTotal)}")
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense, onDelete: () -> Unit) {
    val sdf = remember { SimpleDateFormat("MMM d, h:mm a", Locale.US) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${expense.category} • $${"%.2f".format(expense.amount)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    expense.description.ifBlank { "(no description)" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    sdf.format(Date(expense.createdAt)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}