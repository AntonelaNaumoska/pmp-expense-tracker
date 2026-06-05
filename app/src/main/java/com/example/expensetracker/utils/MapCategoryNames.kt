package com.example.expensetracker.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.expensetracker.R

@Composable
fun getLocalizedCategoryName(dbKey: String): String {
    val resId = when (dbKey) {
        "salary" -> R.string.category_salary
        "freelance" -> R.string.category_freelance
        "investments" -> R.string.category_investments
        "grocery" -> R.string.category_grocery
        "netflix" -> R.string.category_netflix
        "rent" -> R.string.category_rent
        "shopping" -> R.string.category_shopping
        "transport" -> R.string.category_transport
        "utilities" -> R.string.category_utilities
        "dining_out" -> R.string.category_dining
        "entertainment" -> R.string.category_entertainment
        "healthcare" -> R.string.category_healthcare
        "insurance" -> R.string.category_insurance
        "subscriptions" -> R.string.category_subscriptions
        "education" -> R.string.category_education
        "debt_payments" -> R.string.category_debt
        "gifts_donations" -> R.string.category_gifts
        "travel" -> R.string.category_travel
        else -> R.string.category_other
    }
    return stringResource(id = resId)
}