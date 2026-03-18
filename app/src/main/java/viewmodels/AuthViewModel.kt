package com.rohan.dailyexpensetracker.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(auth.currentUser != null)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _errorMessage.value = null
        _infoMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _authState.value = true
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.message ?: "Login failed"
                }
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _errorMessage.value = null
        _infoMessage.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _authState.value = true
                    onSuccess()
                } else {
                    _errorMessage.value = task.exception?.message ?: "Registration failed"
                }
            }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Enter your email first"
            return
        }

        _loading.value = true
        _errorMessage.value = null
        _infoMessage.value = null

        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _infoMessage.value = "Password reset email sent"
                } else {
                    _errorMessage.value = task.exception?.message ?: "Could not send reset email"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = false
    }

    fun clearMessages() {
        _errorMessage.value = null
        _infoMessage.value = null
    }

    fun clearError() {
        clearMessages()
    }
}