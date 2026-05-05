package com.menowell.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileRead(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("core_wounds") val coreWounds: List<String> = emptyList(),
    @SerializedName("joy_anchors") val joyAnchors: List<String> = emptyList(),
    @SerializedName("anxiety_triggers") val anxietyTriggers: List<String> = emptyList(),
    @SerializedName("depression_patterns") val depressionPatterns: List<String> = emptyList(),
    @SerializedName("physical_emotional_links") val physicalEmotionalLinks: List<String> = emptyList(),
    @SerializedName("strength_narrative") val strengthNarrative: String = "",
)

data class UserProfileUpdate(
    @SerializedName("core_wounds") val coreWounds: List<String> = emptyList(),
    @SerializedName("joy_anchors") val joyAnchors: List<String> = emptyList(),
    @SerializedName("anxiety_triggers") val anxietyTriggers: List<String> = emptyList(),
    @SerializedName("depression_patterns") val depressionPatterns: List<String> = emptyList(),
    @SerializedName("physical_emotional_links") val physicalEmotionalLinks: List<String> = emptyList(),
    @SerializedName("strength_narrative") val strengthNarrative: String = "",
)
