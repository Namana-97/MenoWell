package com.menowell.data.model

import com.google.gson.annotations.SerializedName

data class CheckInRequest(
    @SerializedName("in_one_word") val inOneWord: String? = null,
    @SerializedName("body_score") val bodyScore: Int? = null,
    @SerializedName("mind_score") val mindScore: Int? = null,
    @SerializedName("hurt_today") val hurtToday: String? = null,
    @SerializedName("helped_today") val helpedToday: String? = null,
    @SerializedName("hot_flashes") val hotFlashes: Int = 0,
    @SerializedName("supplements_taken") val supplementsTaken: Boolean? = null,
    @SerializedName("checkin_date") val checkinDate: String? = null,
)

data class CheckInRead(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("checkin_date") val checkinDate: String,
    @SerializedName("in_one_word") val inOneWord: String? = null,
    @SerializedName("body_score") val bodyScore: Int? = null,
    @SerializedName("mind_score") val mindScore: Int? = null,
    @SerializedName("hurt_today") val hurtToday: String? = null,
    @SerializedName("helped_today") val helpedToday: String? = null,
    @SerializedName("hot_flashes") val hotFlashes: Int,
    @SerializedName("supplements_taken") val supplementsTaken: String? = null,
    @SerializedName("created_at") val createdAt: String,
)
