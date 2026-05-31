package com.example.expensetracker.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.AuthRepositoryImpl
import com.example.expensetracker.data.repository.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val authState: StateFlow<Resource<FirebaseUser>?> = _authState.asStateFlow()

    val isUserLoggedIn: Boolean
        get() = authRepository.currentUser != null

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmail(email, password).collect {
                _authState.value = it
            }
        }
    }

    fun signUpUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.registerWithEmail(email, password).collect {
                _authState.value = it
            }
        }
    }

    fun loginAnonymously() {
        viewModelScope.launch {
            authRepository.signInAnonymously().collect {
                _authState.value = it
            }
        }
    }

    fun loginWithSocialCredential(credential: AuthCredential) {
        viewModelScope.launch {
            authRepository.signInWithCredential(credential).collect {
                _authState.value = it
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            com.facebook.login.LoginManager.getInstance().logOut()
            onLogoutSuccess()
        }
    }

    fun clearState() {
        _authState.value = null
    }
}