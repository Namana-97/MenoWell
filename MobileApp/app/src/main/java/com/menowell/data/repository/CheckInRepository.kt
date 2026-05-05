package com.menowell.data.repository

import com.menowell.data.model.CheckInRead
import com.menowell.data.model.CheckInRequest
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class CheckInRepository @Inject constructor(private val api: ApiService) {
    suspend fun submit(request: CheckInRequest): Result<CheckInRead> = runCatching { api.submitCheckIn(request) }
    suspend fun week(): Result<List<CheckInRead>> = runCatching { api.weekCheckIns() }
}
