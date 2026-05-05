package com.menowell.data.repository

import com.menowell.core.SessionManager
import com.menowell.data.model.*
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val sessionManager: SessionManager,
) {
    suspend fun register(email: String, password: String, fullName: String?): Result<TokenResponse> = runCatching {
        api.register(RegisterRequest(email, password, fullName))
        api.login(LoginRequest(email, password))
    }.onSuccess { sessionManager.saveToken(it.accessToken) }

    suspend fun login(email: String, password: String): Result<TokenResponse> = runCatching {
        api.login(LoginRequest(email, password))
    }.onSuccess { sessionManager.saveToken(it.accessToken) }

    suspend fun logout() = sessionManager.clearToken()
}
