package com.menowell.data.repository

import com.menowell.data.model.WeeklyLetterResponse
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class LetterRepository @Inject constructor(private val api: ApiService) {
    suspend fun weeklyLetter(): Result<WeeklyLetterResponse> = runCatching { api.weeklyLetter() }
}
