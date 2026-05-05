package com.menowell.data.remote

import com.menowell.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserRead

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse

    @GET("chat/history")
    suspend fun chatHistory(): List<ChatMessageRead>

    @POST("checkin")
    suspend fun submitCheckIn(@Body request: CheckInRequest): CheckInRead

    @GET("checkin/week")
    suspend fun weekCheckIns(): List<CheckInRead>

    @GET("letter/weekly")
    suspend fun weeklyLetter(): WeeklyLetterResponse

    @GET("profile")
    suspend fun getProfile(): UserProfileRead

    @PUT("profile")
    suspend fun updateProfile(@Body request: UserProfileUpdate): UserProfileRead

    @GET("insights/")
    suspend fun getInsights(): InsightsResponse

    @GET("analytics/trends")
    suspend fun getAnalyticsTrends(): AnalyticsResponse
}
