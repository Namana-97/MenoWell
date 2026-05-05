package com.menowell.viewmodel

import com.menowell.core.SessionManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.model.ChatMessageRead
import com.menowell.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Data(val messages: List<ChatMessageRead>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _state = MutableStateFlow<ChatUiState>(ChatUiState.Data(emptyList()))
    val state: StateFlow<ChatUiState> = _state.asStateFlow()
    private var latestHistoryMessageId: Int = 0

    fun loadHistory() = viewModelScope.launch {
        repository.history()
            .onSuccess { history ->
                latestHistoryMessageId = history.maxOfOrNull(ChatMessageRead::id) ?: 0
                val clearedThroughId = sessionManager.chatClearedThroughIdFlow.first()
                _state.value = ChatUiState.Data(history.filter { it.id > clearedThroughId })
            }
            .onFailure { _state.value = ChatUiState.Error(it.message ?: "Unable to load chat history") }
    }

    fun send(message: String) = viewModelScope.launch {
        val current = (_state.value as? ChatUiState.Data)?.messages.orEmpty()
        _state.value = ChatUiState.Data(current + ChatMessageRead(0, "user", message, 0, "now"))
        repository.sendMessage(message)
            .onSuccess { response ->
                val updated = (_state.value as? ChatUiState.Data)?.messages.orEmpty() +
                    ChatMessageRead(0, "assistant", response.reply, response.depression_level, "now")
                _state.value = ChatUiState.Data(updated)
                loadHistory()
            }
            .onFailure { _state.value = ChatUiState.Error(it.message ?: "Message failed") }
    }

    fun clearVisibleChat() = viewModelScope.launch {
        sessionManager.saveChatClearedThroughId(latestHistoryMessageId)
        _state.value = ChatUiState.Data(emptyList())
    }
}
