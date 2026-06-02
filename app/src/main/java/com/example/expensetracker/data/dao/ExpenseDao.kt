package com.example.expensetracker.data.dao

import androidx.room.*
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.model.ExpenseSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense_table WHERE userId = :userId")
    fun getAllExpense(userId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT title, type, date, amount AS total_amount FROM expense_table WHERE type = 'Expense' AND userId = :userId ORDER BY amount DESC LIMIT 5")
    fun getTopExpenses(userId: String): Flow<List<ExpenseSummary>>

    @Query("SELECT title, type, date, SUM(amount) AS total_amount FROM expense_table WHERE type = :type AND userId = :userId GROUP BY type, date ORDER BY date")
    fun getAllExpenseByDate(userId: String, type: String = "Expense"): Flow<List<ExpenseSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expenseEntity: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expenseEntity: ExpenseEntity)

    @Update
    suspend fun updateExpense(expenseEntity: ExpenseEntity)
}