package com.menowell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.Cream
import com.menowell.ui.theme.DeepRose
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.LoraFamily
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.WarmMist
import com.menowell.viewmodel.LetterUiState
import com.menowell.viewmodel.LetterViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeeklyLetterScreen(viewModel: LetterViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MenoSectionLabel("Weekly reflection")
            Text(
                text = "Mia's note back to you",
                style = MaterialTheme.typography.displayMedium,
                color = TextDeep,
            )
            Text(
                text = "A softer way to look at the week, especially when hormones, sleep, and emotions all start talking at once.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        }
        MenoCard(containerColor = WarmMist) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "A slower summary",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid,
                )
                Text(
                    text = "This letter is meant to feel like a caring recap, not a report card. It notices themes without judging you for them.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDeep,
                )
            }
        }
        when (val current = state) {
            is LetterUiState.Loading -> {
                MenoCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = DustyRose)
                    }
                }
            }
            is LetterUiState.Error -> {
                Text(
                    text = current.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            is LetterUiState.Data -> {
                val letter = current.letter
                val avgBody = readMemberText(letter, "avgBody", "avg_body", "averageBody", "average_body_score")
                val avgMind = readMemberText(letter, "avgMind", "avg_mind", "averageMind", "average_mind_score")
                val hotFlashes = readMemberText(letter, "hotFlashes", "hot_flashes", "hotFlashCount", "hot_flash_count")
                val closingQuestion = readMemberText(letter, "closingQuestion", "closing_question", "question", "reflectionQuestion")

                MenoCard {
                    Text(
                        text = letter.letter,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = LoraFamily,
                            lineHeight = 28.sp,
                        ),
                        color = TextDeep,
                    )
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatChip("Avg body: ${avgBody ?: "—"}/5")
                    StatChip("Avg mind: ${avgMind ?: "—"}/5")
                    StatChip("Hot flashes: ${hotFlashes ?: "—"}")
                }
                if (!closingQuestion.isNullOrBlank()) {
                    Text(
                        text = closingQuestion,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = LoraFamily,
                            fontStyle = FontStyle.Italic,
                        ),
                        color = TextMid,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(text: String) {
    androidx.compose.material3.Surface(
        color = BlushPink,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = DeepRose,
        )
    }
}

private fun readMemberText(target: Any, vararg names: String): String? {
    names.forEach { name ->
        target.javaClass.methods.firstOrNull { method ->
            method.parameterCount == 0 && (
                method.name.equals(name, ignoreCase = true) ||
                    method.name.equals("get${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}", ignoreCase = true)
                )
        }?.invoke(target)?.toString()?.takeIf { it.isNotBlank() }?.let { return it }

        target.javaClass.declaredFields.firstOrNull { field ->
            field.name.equals(name, ignoreCase = true)
        }?.let { field ->
            field.isAccessible = true
            field.get(target)?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
        }
    }
    return null
}
