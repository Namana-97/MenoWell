package com.menowell.data.repository

import com.menowell.data.model.AnalyticsResponse
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class PatternsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAnalyticsTrends(): AnalyticsResponse = apiService.getAnalyticsTrends()
}
