package com.menowell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.model.WeeklyLetterResponse
import com.menowell.data.repository.LetterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LetterUiState {
    data object Loading : LetterUiState
    data class Data(val letter: WeeklyLetterResponse) : LetterUiState
    data class Error(val message: String) : LetterUiState
}

@HiltViewModel
class LetterViewModel @Inject constructor(private val repository: LetterRepository) : ViewModel() {
    private val _state = MutableStateFlow<LetterUiState>(LetterUiState.Loading)
    val state: StateFlow<LetterUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        repository.weeklyLetter()
            .onSuccess { _state.value = LetterUiState.Data(it) }
            .onFailure { _state.value = LetterUiState.Error(it.message ?: "Unable to load letter") }
    }
}
