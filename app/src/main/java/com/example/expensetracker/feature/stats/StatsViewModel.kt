package com.example.expensetracker.feature.stats

import com.example.expensetracker.base.BaseViewModel
import com.example.expensetracker.base.UiEvent
import com.example.expensetracker.data.model.ExpenseSummary
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.utils.Utils
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : BaseViewModel() {

    val entries: Flow<List<ExpenseSummary>> =
        repository.getExpensesByDate()

    val topEntries: Flow<List<ExpenseSummary>> =
        repository.getTopExpenses()

    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        return entries.map {
            Entry(
                Utils.getMillisFromDate(it.date).toFloat(),
                it.total_amount.toFloat()
            )
        }
    }

    override fun onEvent(event: UiEvent) {}
}