package com.menowell.data.repository

import com.menowell.data.model.InsightsResponse
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class InsightsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getInsights(): InsightsResponse = apiService.getInsights()
}
