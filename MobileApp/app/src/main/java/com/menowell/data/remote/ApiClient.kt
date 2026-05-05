package com.menowell.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.menowell.BuildConfig
import com.menowell.core.Constants
import com.menowell.core.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClientModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager = SessionManager(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): Interceptor = Interceptor { chain ->
        val token = runBlocking { sessionManager.tokenFlow.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
        }.build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(sessionManager: SessionManager): Authenticator = object : Authenticator {
        override fun authenticate(route: Route?, response: Response): okhttp3.Request? {
            if (response.request.url.encodedPath.endsWith("/auth/refresh")) return null
            if (responseCount(response) >= 2) return null

            val newToken = runBlocking { sessionManager.refreshAccessToken() } ?: return null
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        sessionManager: SessionManager,
        tokenAuthenticator: Authenticator,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(buildTokenCaptureInterceptor(sessionManager))
            .addInterceptor(logging)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }

    private fun buildTokenCaptureInterceptor(sessionManager: SessionManager): Interceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val path = response.request.url.encodedPath
        if (response.isSuccessful && path.endsWith("/auth/login")) {
            val payload = response.peekBody(Long.MAX_VALUE).string()
            val json = JSONObject(payload)
            val refreshToken = json.optString("refresh_token").takeIf { it.isNotBlank() }
            val accessToken = json.optString("access_token").takeIf { it.isNotBlank() }
            runBlocking {
                if (!accessToken.isNullOrBlank()) {
                    sessionManager.saveToken(accessToken)
                }
                if (!refreshToken.isNullOrBlank()) {
                    sessionManager.saveRefreshToken(refreshToken)
                }
            }
        }
        response
    }
}
