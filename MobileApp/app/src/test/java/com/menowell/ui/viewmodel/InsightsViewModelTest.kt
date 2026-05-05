package com.menowell.ui.viewmodel

import com.menowell.data.model.InsightItemResponse
import com.menowell.data.model.InsightsResponse
import com.menowell.data.remote.ApiService
import com.menowell.data.repository.InsightsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var repository: InsightsRepository
    private lateinit var viewModel: InsightsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        apiService = mock()
        repository = InsightsRepository(apiService)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when not enough data, state reflects that`() = runTest {
        whenever(apiService.getInsights()).thenReturn(
            InsightsResponse(
                hasEnoughData = false,
                daysUntilInsights = 3,
                totalDaysAnalyzed = null,
                averageBody = null,
                averageMind = null,
                averageSentiment = null,
                insights = null,
            )
        )

        viewModel = InsightsViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.hasEnoughData)
        assertEquals(3, viewModel.state.value.daysUntilInsights)
    }

    @Test
    fun `when data available, insights are mapped correctly`() = runTest {
        val fakeInsight = InsightItemResponse(
            type = "correlation",
            title = "Test insight",
            body = "Test body",
            strength = 0.8,
            icon = "flash",
        )
        whenever(apiService.getInsights()).thenReturn(
            InsightsResponse(
                hasEnoughData = true,
                daysUntilInsights = null,
                totalDaysAnalyzed = 12,
                averageBody = 3.2,
                averageMind = 2.8,
                averageSentiment = 0.55,
                insights = listOf(fakeInsight),
            )
        )

        viewModel = InsightsViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.hasEnoughData)
        assertEquals(1, viewModel.state.value.insights.size)
        assertEquals("Test insight", viewModel.state.value.insights[0].title)
    }

    @Test
    fun `on repository error, error state is set`() = runTest {
        whenever(apiService.getInsights()).thenThrow(RuntimeException("Network error"))

        viewModel = InsightsViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.error)
    }
}
