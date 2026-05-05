package com.menowell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.core.SessionManager
import com.menowell.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface SessionUiState {
    data object Loading : SessionUiState
    data object Authenticated : SessionUiState
    data object Unauthenticated : SessionUiState
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<SessionUiState>(SessionUiState.Loading)
    val state: StateFlow<SessionUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.tokenFlow.collectLatest { token ->
                _state.value = if (token.isNullOrBlank()) {
                    SessionUiState.Unauthenticated
                } else {
                    SessionUiState.Authenticated
                }
            }
        }
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
    }
}
