package com.example.expensetracker.data.repository

import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.model.ExpenseSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: "anonymous"

    fun getLocalExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpense(currentUserId)
    }

    fun getExpensesByDate(): Flow<List<ExpenseSummary>> {
        return expenseDao.getAllExpenseByDate(currentUserId)
    }

    fun getTopExpenses(): Flow<List<ExpenseSummary>> {
        return expenseDao.getTopExpenses(currentUserId)
    }

    suspend fun addTransaction(expense: ExpenseEntity) {
        val mappedEntity = expense.copy(userId = currentUserId)

        expenseDao.insertExpense(mappedEntity)

        val firestoreDocument = hashMapOf(
            "title" to mappedEntity.title,
            "amount" to mappedEntity.amount,
            "date" to mappedEntity.date,
            "type" to mappedEntity.type,
            "userId" to currentUserId
        )

        try {
            firestore.collection("users")
                .document(currentUserId)
                .collection("transactions")
                .add(firestoreDocument)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}