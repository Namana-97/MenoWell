package com.menowell.data.repository

import com.menowell.data.model.ChatMessageRead
import com.menowell.data.model.ChatResponse
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class ChatRepository @Inject constructor(private val api: ApiService) {
    suspend fun sendMessage(message: String): Result<ChatResponse> = runCatching {
        api.sendMessage(com.menowell.data.model.ChatRequest(message))
    }

    suspend fun history(): Result<List<ChatMessageRead>> = runCatching { api.chatHistory() }
}
