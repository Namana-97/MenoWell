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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.DeepRose
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.TextSoft
import com.menowell.ui.viewmodel.InsightItem
import com.menowell.ui.viewmodel.InsightsViewModel

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
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
                MenoSectionLabel("Mia noticed something")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "What your data says across the month",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextDeep,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "These patterns come from reading all your check-ins together — not just today.",
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

            !state.hasEnoughData -> {
                item {
                    MenoCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Not enough data yet",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextDeep,
                            )
                            Text(
                                text = "Check in for ${state.daysUntilInsights} more day${if (state.daysUntilInsights != 1) "s" else ""} and Mia will start seeing patterns in your data.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMid,
                            )
                        }
                    }
                }
            }

            else -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        StatChip(
                            label = "Avg body",
                            value = "${state.averageBody}/5",
                            modifier = Modifier.weight(1f),
                        )
                        StatChip(
                            label = "Avg mind",
                            value = "${state.averageMind}/5",
                            modifier = Modifier.weight(1f),
                        )
                        StatChip(
                            label = "Days read",
                            value = state.totalDays.toString(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                items(state.insights) { insight ->
                    InsightCard(insight = insight)
                }

                item {
                    Text(
                        text = "Patterns are observations, not diagnoses. Bring anything that feels significant to your doctor.",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSoft,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun InsightCard(insight: InsightItem) {
    MenoCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = insight.title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextDeep,
            )
            Text(
                text = insight.body,
                style = MaterialTheme.typography.bodyLarge,
                color = TextMid,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            )
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = BlushPink,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextMid,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = DeepRose,
            )
        }
    }
}
