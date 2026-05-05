package com.menowell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.model.CheckInRead
import com.menowell.data.model.CheckInRequest
import com.menowell.data.repository.CheckInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CheckInUiState {
    data object Idle : CheckInUiState
    data object Loading : CheckInUiState
    data class Success(val record: CheckInRead) : CheckInUiState
    data class Error(val message: String) : CheckInUiState
}

@HiltViewModel
class CheckInViewModel @Inject constructor(private val repository: CheckInRepository) : ViewModel() {
    private val _state = MutableStateFlow<CheckInUiState>(CheckInUiState.Idle)
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()

    fun submit(request: CheckInRequest) = viewModelScope.launch {
        _state.value = CheckInUiState.Loading
        repository.submit(request)
            .onSuccess { _state.value = CheckInUiState.Success(it) }
            .onFailure { _state.value = CheckInUiState.Error(it.message ?: "Unable to submit check-in") }
    }
}
