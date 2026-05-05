package com.menowell.data.model

import com.google.gson.annotations.SerializedName

data class AnalyticsResponse(
    @SerializedName("has_data") val hasData: Boolean,
    @SerializedName("total_days") val totalDays: Int?,
    @SerializedName("overall_body_avg") val overallBodyAvg: Double?,
    @SerializedName("overall_mind_avg") val overallMindAvg: Double?,
    @SerializedName("points") val points: List<TrendPoint>?,
    @SerializedName("correlations") val correlations: List<CorrelationCard>?
)

data class TrendPoint(
    val date: String,
    val body: Double,
    val mind: Double,
    @SerializedName("hot_flashes") val hotFlashes: Int
)

data class CorrelationCard(
    val label: String,
    @SerializedName("stat_a") val statA: String,
    @SerializedName("stat_a_label") val statALabel: String,
    @SerializedName("stat_b") val statB: String,
    @SerializedName("stat_b_label") val statBLabel: String,
    val interpretation: String
)
