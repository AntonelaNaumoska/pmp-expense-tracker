package com.example.expensetracker.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override fun loginWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>> = callbackFlow {
        trySend(Resource.Loading)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let { trySend(Resource.Success(it)) }
            }
            .addOnFailureListener { exception ->
                trySend(Resource.Failure(exception))
            }
        awaitClose()
    }

    override fun registerWithEmail(email: String, password: String): Flow<Resource<FirebaseUser>> = callbackFlow {
        trySend(Resource.Loading)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    val userProfile = hashMapOf(
                        "uid" to user.uid,
                        "email" to user.email,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                    firestore.collection("users").document(user.uid).set(userProfile)
                    trySend(Resource.Success(user))
                }
            }
            .addOnFailureListener { exception ->
                trySend(Resource.Failure(exception))
            }
        awaitClose()
    }

    override fun signInAnonymously(): Flow<Resource<FirebaseUser>> = callbackFlow {
        trySend(Resource.Loading)
        firebaseAuth.signInAnonymously()
            .addOnSuccessListener { result ->
                result.user?.let { trySend(Resource.Success(it)) }
            }
            .addOnFailureListener { exception ->
                trySend(Resource.Failure(exception))
            }
        awaitClose()
    }

    override fun signInWithCredential(credential: AuthCredential): Flow<Resource<FirebaseUser>> = callbackFlow {
        trySend(Resource.Loading)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    val userProfile = hashMapOf(
                        "uid" to user.uid,
                        "email" to user.email,
                        "displayName" to user.displayName,
                        "photoUrl" to user.photoUrl?.toString(),
                        "lastLogin" to com.google.firebase.Timestamp.now()
                    )
                    firestore.collection("users").document(user.uid).set(userProfile, com.google.firebase.firestore.SetOptions.merge())
                    trySend(Resource.Success(user))
                }
            }
            .addOnFailureListener { exception ->
                trySend(Resource.Failure(exception))
            }
        awaitClose()
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}