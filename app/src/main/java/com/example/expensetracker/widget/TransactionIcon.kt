package com.example.expensetracker.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TransactionIcon(isIncome: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val iconColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
    val iconImage = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Box(
        modifier = modifier
            .size(40.dp)
            .background(backgroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconImage,
            contentDescription = if (isIncome) "Income Icon" else "Expense Icon",
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}