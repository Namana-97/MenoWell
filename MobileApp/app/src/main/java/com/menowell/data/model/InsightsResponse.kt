package com.menowell.data.model

import com.google.gson.annotations.SerializedName

data class InsightsResponse(
    @SerializedName("has_enough_data") val hasEnoughData: Boolean,
    @SerializedName("days_until_insights") val daysUntilInsights: Int?,
    @SerializedName("total_days_analyzed") val totalDaysAnalyzed: Int?,
    @SerializedName("average_body") val averageBody: Double?,
    @SerializedName("average_mind") val averageMind: Double?,
    @SerializedName("average_sentiment") val averageSentiment: Double?,
    @SerializedName("insights") val insights: List<InsightItemResponse>?
)

data class InsightItemResponse(
    val type: String,
    val title: String,
    val body: String,
    val strength: Double,
    val icon: String
)
