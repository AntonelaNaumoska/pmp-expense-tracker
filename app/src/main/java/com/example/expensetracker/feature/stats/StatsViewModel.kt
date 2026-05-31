package com.example.expensetracker.feature.stats

import com.example.expensetracker.base.BaseViewModel
import com.example.expensetracker.base.UiEvent
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.model.ExpenseSummary
import com.example.expensetracker.data.repository.AuthRepository
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    val dao: ExpenseDao,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val currentUserId: String = authRepository.currentUser?.uid ?: ""

    val entries: Flow<List<ExpenseSummary>> = if (currentUserId.isNotBlank()) {
        dao.getAllExpenseByDate(userId = currentUserId)
    } else {
        emptyFlow()
    }

    val topEntries: Flow<List<ExpenseSummary>> = if (currentUserId.isNotBlank()) {
        dao.getTopExpenses(currentUserId)
    } else {
        emptyFlow()
    }

    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }

    override fun onEvent(event: UiEvent) {
    }
}