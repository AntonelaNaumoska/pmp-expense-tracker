package com.example.expensetracker.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?

    fun loginWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>>

    fun registerWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>>

    fun signInAnonymously(): Flow<Resource<FirebaseUser>>

    fun signInWithCredential(credential: AuthCredential): Flow<Resource<FirebaseUser>>

    fun logout()
}