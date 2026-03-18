package com.rohan.dailyexpensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohan.dailyexpensetracker.data.model.Expense
import com.rohan.dailyexpensetracker.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    vm: ExpenseViewModel,
    expenseId: Long?,
    onDone: () -> Unit
) {
    val editingExpense = remember(expenseId, vm.expenses.value) {
        expenseId?.let { id -> vm.getExpenseById(id) }
    }

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }
    var categoryError by remember { mutableStateOf("") }

    LaunchedEffect(editingExpense) {
        if (editingExpense != null) {
            amount = editingExpense.amount.toString()
            category = editingExpense.category
            description = editingExpense.description
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (expenseId == null) "Add Expense" else "Edit Expense")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            amountError = ""
                        },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = amountError.isNotBlank(),
                        singleLine = true
                    )

                    if (amountError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(amountError)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = category,
                        onValueChange = {
                            category = it
                            categoryError = ""
                        },
                        label = { Text("Category (Food, Transport, Bills, etc.)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = categoryError.isNotBlank(),
                        singleLine = true
                    )

                    if (categoryError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(categoryError)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val parsedAmount = amount.toDoubleOrNull()

                            if (parsedAmount == null || parsedAmount <= 0.0) {
                                amountError = "Enter a valid amount"
                                return@Button
                            }

                            if (category.isBlank()) {
                                categoryError = "Enter a category"
                                return@Button
                            }

                            if (editingExpense == null) {
                                vm.addExpense(
                                    amount = parsedAmount,
                                    category = category.trim(),
                                    description = description.trim()
                                )
                            } else {
                                vm.updateExpense(
                                    Expense(
                                        id = editingExpense.id,
                                        amount = parsedAmount,
                                        category = category.trim(),
                                        description = description.trim(),
                                        createdAt = editingExpense.createdAt
                                    )
                                )
                            }

                            onDone()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (expenseId == null) "Save Expense" else "Update Expense")
                    }
                }
            }
        }
    }
}