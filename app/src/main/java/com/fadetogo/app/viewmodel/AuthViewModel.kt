package com.fadetogo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadetogo.app.model.User
import com.fadetogo.app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // StateFlow is how the ViewModel communicates back to the UI
    // The UI observes these flows and reacts when they change

    // holds the currently logged in user, null if nobody is logged in
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // holds any error messages we need to show the user
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // tracks whether something is loading so we can show a spinner
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // tracks if password reset email was sent successfully
    private val _passwordResetSent = MutableStateFlow(false)
    val passwordResetSent: StateFlow<Boolean> = _passwordResetSent

    // tracks if registration was successful
    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val firebaseUser = repository.currentUser
            if (firebaseUser != null) {
                val result = repository.getUserProfile(firebaseUser.uid)
                result.onSuccess { user ->
                    _currentUser.value = user
                }
            }
            _isLoading.value = false
        }
    }

    // REGISTER - called when user fills out registration form and submits
    fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: String
    ) {
        // viewModelScope means this coroutine is tied to the ViewModel
        // if the user leaves the screen it automatically cancels
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.register(email, password, name, phone, role)

            result.onSuccess { user ->
                _currentUser.value = user
                _registrationSuccess.value = true
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Registration failed. Please try again."
            }

            _isLoading.value = false
        }
    }

    // LOGIN - called when user enters email and password and hits login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.login(email, password)

            result.onSuccess { user ->
                _currentUser.value = user
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Login failed. Please check your credentials."
            }

            _isLoading.value = false
        }
    }

    // LOGOUT - clears the current user and signs out of Firebase
    fun logout() {
        repository.logout()
        _currentUser.value = null
    }

    // FORGOT PASSWORD - sends reset email to the provided address
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.sendPasswordResetEmail(email)

            result.onSuccess {
                _passwordResetSent.value = true
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not send reset email. Please try again."
            }

            _isLoading.value = false
        }
    }

    // clears error message after it has been shown to the user
    fun clearError() {
        _errorMessage.value = null
    }

    // clears the password reset flag after showing confirmation to user
    fun clearPasswordResetFlag() {
        _passwordResetSent.value = false
    }
}