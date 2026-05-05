package com.menowell.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.menowell.data.model.CorrelationCard
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.WarmMist
import com.menowell.ui.viewmodel.PatternsViewModel

@Composable
fun PatternsScreen(viewModel: PatternsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp),
    ) {
        item {
            Column {
                MenoSectionLabel("Your patterns")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "What the data holds",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextDeep,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Thirty days of check-ins, read together. Not a report card — a mirror.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMid,
                )
            }
        }

        when {
            state.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = DustyRose)
                    }
                }
            }

            state.error != null -> {
                item {
                    MenoCard {
                        Text(
                            text = state.error ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            !state.hasData -> {
                item {
                    MenoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Check in for a few more days",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextDeep,
                            )
                            Text(
                                text = "Patterns become visible after at least 3 check-ins.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMid,
                            )
                        }
                    }
                }
            }

            else -> {
                item {
                    MenoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "30-day mood & body trend",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextDeep,
                            )
                            AndroidView(
                                factory = { context ->
                                    LineChart(context).apply {
                                        description.isEnabled = false
                                        legend.isEnabled = true
                                        setTouchEnabled(false)
                                        xAxis.apply {
                                            position = XAxis.XAxisPosition.BOTTOM
                                            granularity = 1f
                                            setDrawGridLines(false)
                                            textColor = android.graphics.Color.parseColor("#5F534C")
                                            textSize = 10f
                                            valueFormatter = IndexAxisValueFormatter(state.dateLabels.toTypedArray())
                                            labelRotationAngle = -45f
                                        }
                                        axisLeft.apply {
                                            axisMinimum = 0f
                                            axisMaximum = 5.5f
                                            granularity = 1f
                                            textColor = android.graphics.Color.parseColor("#5F534C")
                                        }
                                        axisRight.isEnabled = false
                                    }
                                },
                                update = { chart ->
                                    chart.xAxis.valueFormatter =
                                        IndexAxisValueFormatter(state.dateLabels.toTypedArray())
                                    val mindEntries = state.mindPoints.mapIndexed { index, value ->
                                        Entry(index.toFloat(), value.toFloat())
                                    }
                                    val bodyEntries = state.bodyPoints.mapIndexed { index, value ->
                                        Entry(index.toFloat(), value.toFloat())
                                    }
                                    val mindDataset = LineDataSet(mindEntries, "Mind").apply {
                                        color = android.graphics.Color.parseColor("#C87B61")
                                        setCircleColor(android.graphics.Color.parseColor("#C87B61"))
                                        circleRadius = 3f
                                        lineWidth = 2f
                                        setDrawValues(false)
                                        mode = LineDataSet.Mode.CUBIC_BEZIER
                                    }
                                    val bodyDataset = LineDataSet(bodyEntries, "Body").apply {
                                        color = android.graphics.Color.parseColor("#7E9B86")
                                        setCircleColor(android.graphics.Color.parseColor("#7E9B86"))
                                        circleRadius = 3f
                                        lineWidth = 2f
                                        setDrawValues(false)
                                        mode = LineDataSet.Mode.CUBIC_BEZIER
                                    }
                                    chart.data = LineData(mindDataset, bodyDataset)
                                    chart.invalidate()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                            )
                        }
                    }
                }

                items(state.correlations) { correlation ->
                    MenoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = correlation.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = DustyRose,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                CorrelationStat(
                                    value = correlation.statA,
                                    label = correlation.statALabel,
                                    modifier = Modifier.weight(1f),
                                )
                                CorrelationStat(
                                    value = correlation.statB,
                                    label = correlation.statBLabel,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Text(
                                text = correlation.interpretation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMid,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CorrelationStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = WarmMist,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = TextDeep,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMid,
            )
        }
    }
}
