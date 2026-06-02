package com.example.expensetracker.data.model

data class ExpenseSummary(
    val title: String,
    val type: String,
    val date: String,
    val total_amount: Double
)