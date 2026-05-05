package com.menowell.data.model

data class ChatRequest(val message: String)

data class ChatResponse(
    val reply: String,
    val depression_level: Int,
    val crisis: Boolean = false,
)

data class ChatMessageRead(
    val id: Int,
    val role: String,
    val content: String,
    val depression_level: Int,
    val created_at: String,
)
