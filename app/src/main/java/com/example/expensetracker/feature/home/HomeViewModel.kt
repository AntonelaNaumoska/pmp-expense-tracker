package com.example.expensetracker.feature.home

import androidx.lifecycle.viewModelScope
import com.example.expensetracker.base.BaseViewModel
import com.example.expensetracker.base.HomeNavigationEvent
import com.example.expensetracker.base.UiEvent
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val dao: ExpenseDao,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val currentUserId: String = authRepository.currentUser?.uid ?: ""

    val expenses: Flow<List<ExpenseEntity>> = if (currentUserId.isNotBlank()) {
        dao.getAllExpense(currentUserId)
    } else {
        emptyFlow()
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddExpense)
                }
            }

            is HomeUiEvent.OnAddIncomeClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddIncome)
                }
            }

            is HomeUiEvent.OnSeeAllClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToSeeAll)
                }
            }
        }
    }

    fun getTotalExpense(list: List<ExpenseEntity>): Double {
        var total = 0.0
        for (expense in list) {
            if (expense.type != "Income") {
                total += expense.amount
            }
        }
        return total
    }

    fun getTotalIncome(list: List<ExpenseEntity>): Double {
        var total = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                total += expense.amount
            }
        }
        return total
    }

    fun getBalance(list: List<ExpenseEntity>): Double {
        val income = getTotalIncome(list)
        val expense = getTotalExpense(list)
        return income - expense
    }
}

sealed class HomeUiEvent : UiEvent() {
    data object OnAddExpenseClicked : HomeUiEvent()
    data object OnAddIncomeClicked : HomeUiEvent()
    data object OnSeeAllClicked : HomeUiEvent()
}