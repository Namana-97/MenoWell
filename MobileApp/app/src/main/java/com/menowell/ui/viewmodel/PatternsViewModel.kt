package com.menowell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.menowell.data.model.CorrelationCard
import com.menowell.data.repository.PatternsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PatternsState(
    val isLoading: Boolean = true,
    val hasData: Boolean = false,
    val mindPoints: List<Double> = emptyList(),
    val bodyPoints: List<Double> = emptyList(),
    val dateLabels: List<String> = emptyList(),
    val correlations: List<CorrelationCard> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PatternsViewModel @Inject constructor(
    private val repository: PatternsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PatternsState())
    val state: StateFlow<PatternsState> = _state

    init {
        loadPatterns()
    }

    fun loadPatterns() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getAnalyticsTrends()
                _state.value = PatternsState(
                    isLoading = false,
                    hasData = response.hasData,
                    mindPoints = response.points?.map { it.mind } ?: emptyList(),
                    bodyPoints = response.points?.map { it.body } ?: emptyList(),
                    dateLabels = response.points?.map { point ->
                        formatDateLabel(point.date)
                    } ?: emptyList(),
                    correlations = response.correlations ?: emptyList(),
                )
            } catch (_: Exception) {
                _state.value = PatternsState(
                    isLoading = false,
                    error = "Could not load patterns right now."
                )
            }
        }
    }

    private fun formatDateLabel(value: String): String {
        return try {
            LocalDate.parse(value).format(DateTimeFormatter.ofPattern("M/d"))
        } catch (_: Exception) {
            value.take(5)
        }
    }
}
