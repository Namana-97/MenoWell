package com.menowell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.model.UserProfileRead
import com.menowell.data.model.UserProfileUpdate
import com.menowell.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Data(val profile: UserProfileRead) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {
    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        repository.getProfile()
            .onSuccess { _state.value = ProfileUiState.Data(it) }
            .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Unable to load profile") }
    }

    fun update(request: UserProfileUpdate) = viewModelScope.launch {
        repository.updateProfile(request)
            .onSuccess { _state.value = ProfileUiState.Data(it) }
            .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Unable to update profile") }
    }
}
