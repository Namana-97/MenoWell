package com.menowell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.repository.InsightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InsightItem(
    val type: String,
    val title: String,
    val body: String,
    val strength: Double,
    val icon: String
)

data class InsightsState(
    val isLoading: Boolean = true,
    val hasEnoughData: Boolean = false,
    val daysUntilInsights: Int = 5,
    val totalDays: Int = 0,
    val averageBody: String = "—",
    val averageMind: String = "—",
    val insights: List<InsightItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: InsightsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state

    init {
        loadInsights()
    }

    fun loadInsights() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getInsights()
                _state.value = InsightsState(
                    isLoading = false,
                    hasEnoughData = response.hasEnoughData,
                    daysUntilInsights = response.daysUntilInsights ?: 5,
                    totalDays = response.totalDaysAnalyzed ?: 0,
                    averageBody = response.averageBody?.toString() ?: "—",
                    averageMind = response.averageMind?.toString() ?: "—",
                    insights = response.insights?.map {
                        InsightItem(it.type, it.title, it.body, it.strength, it.icon)
                    } ?: emptyList(),
                )
            } catch (_: Exception) {
                _state.value = InsightsState(
                    isLoading = false,
                    error = "Could not load insights right now."
                )
            }
        }
    }
}
