package com.menowell.data.repository

import com.menowell.data.model.UserProfileRead
import com.menowell.data.model.UserProfileUpdate
import com.menowell.data.remote.ApiService
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val api: ApiService) {
    suspend fun getProfile(): Result<UserProfileRead> = runCatching { api.getProfile() }
    suspend fun updateProfile(request: UserProfileUpdate): Result<UserProfileRead> = runCatching { api.updateProfile(request) }
}
