package com.menowell.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

class SessionManager(private val context: Context) {
    private val tokenKey = stringPreferencesKey(Constants.TOKEN_KEY)
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val chatClearedThroughIdKey = intPreferencesKey(Constants.CHAT_CLEARED_THROUGH_ID_KEY)

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[tokenKey] }
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { it[refreshTokenKey] }
    val chatClearedThroughIdFlow: Flow<Int> = context.dataStore.data.map { it[chatClearedThroughIdKey] ?: 0 }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[tokenKey] = token }
    }

    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { it[refreshTokenKey] = token }
    }

    suspend fun getRefreshToken(): String? = refreshTokenFlow.firstOrNull()

    suspend fun refreshAccessToken(): String? = withContext(Dispatchers.IO) {
        val refreshToken = getRefreshToken() ?: return@withContext null
        val client = OkHttpClient()
        val json = JSONObject().put("refresh_token", refreshToken).toString()
        val request = Request.Builder()
            .url("${Constants.BASE_URL}auth/refresh")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null
            val body = response.body?.string().orEmpty()
            val accessToken = JSONObject(body).optString("access_token").takeIf { it.isNotBlank() }
                ?: return@withContext null
            saveToken(accessToken)
            accessToken
        }
    }

    suspend fun saveChatClearedThroughId(messageId: Int) {
        context.dataStore.edit { it[chatClearedThroughIdKey] = messageId }
    }

    suspend fun clearChatClearedThroughId() {
        context.dataStore.edit { it.remove(chatClearedThroughIdKey) }
    }

    suspend fun clearToken() {
        context.dataStore.edit {
            it.remove(tokenKey)
            it.remove(refreshTokenKey)
            it.remove(chatClearedThroughIdKey)
        }
    }
}
