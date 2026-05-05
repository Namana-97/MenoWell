package com.menowell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.repository.AuthRepository
import com.menowell.data.remote.toReadableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        validateAuthInput(email, password)?.let {
            _uiState.value = AuthUiState.Error(it)
            return@launch
        }
        _uiState.value = AuthUiState.Loading
        repository.login(email, password)
            .onSuccess { _uiState.value = AuthUiState.Success }
            .onFailure { _uiState.value = AuthUiState.Error(it.toReadableMessage("Login failed")) }
    }

    fun register(email: String, password: String, fullName: String?) = viewModelScope.launch {
        validateAuthInput(email, password)?.let {
            _uiState.value = AuthUiState.Error(it)
            return@launch
        }
        _uiState.value = AuthUiState.Loading
        repository.register(email, password, fullName)
            .onSuccess { _uiState.value = AuthUiState.Success }
            .onFailure { _uiState.value = AuthUiState.Error(it.toReadableMessage("Registration failed")) }
    }

    private fun validateAuthInput(email: String, password: String): String? {
        if (email.isBlank()) return "Email is required"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Enter a valid email address"
        if (password.length < 6) return "Password must be at least 6 characters"
        return null
    }
}
