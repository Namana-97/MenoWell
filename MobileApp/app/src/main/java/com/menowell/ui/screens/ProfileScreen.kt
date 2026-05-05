package com.menowell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menowell.data.model.UserProfileRead
import com.menowell.data.model.UserProfileUpdate
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoPrimaryButton
import com.menowell.ui.components.MenoSecondaryButton
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.components.menoOutlinedTextFieldColors
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.Cream
import com.menowell.ui.theme.DeepRose
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.TextSoft
import com.menowell.ui.theme.WarmMist
import com.menowell.viewmodel.ProfileUiState
import com.menowell.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var coreWounds by remember { mutableStateOf("") }
    var joyAnchors by remember { mutableStateOf("") }
    var anxietyTriggers by remember { mutableStateOf("") }
    var depressionPatterns by remember { mutableStateOf("") }
    var physicalEmotionalLinks by remember { mutableStateOf("") }
    var strengthNarrative by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(state) {
        val profile = (state as? ProfileUiState.Data)?.profile ?: return@LaunchedEffect
        coreWounds = profile.coreWounds.joinToString(", ")
        joyAnchors = profile.joyAnchors.joinToString(", ")
        anxietyTriggers = profile.anxietyTriggers.joinToString(", ")
        depressionPatterns = profile.depressionPatterns.joinToString(", ")
        physicalEmotionalLinks = profile.physicalEmotionalLinks.joinToString(", ")
        strengthNarrative = profile.strengthNarrative
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MenoSectionLabel("Memory and comfort map")
            Text(
                text = "What Mia should keep close",
                style = MaterialTheme.typography.displayMedium,
                color = TextDeep,
            )
            Text(
                text = "This is the emotional context layer. It helps Mia sound more like someone who knows your patterns, not a stranger.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        }
        MenoCard(containerColor = WarmMist) {
            Text(
                text = "Give the app your real context — Add the joys, triggers, body links, and resilience language that you want reflected back when the week feels especially intense.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextDeep,
            )
        }
        when (val current = state) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileUiState.Error -> {
                Text(
                    text = current.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            is ProfileUiState.Data -> {
                ProfileEditor(
                    profile = current.profile,
                    coreWounds = coreWounds,
                    onCoreWoundsChange = { coreWounds = it },
                    joyAnchors = joyAnchors,
                    onJoyAnchorsChange = { joyAnchors = it },
                    anxietyTriggers = anxietyTriggers,
                    onAnxietyTriggersChange = { anxietyTriggers = it },
                    depressionPatterns = depressionPatterns,
                    onDepressionPatternsChange = { depressionPatterns = it },
                    physicalEmotionalLinks = physicalEmotionalLinks,
                    onPhysicalEmotionalLinksChange = { physicalEmotionalLinks = it },
                    strengthNarrative = strengthNarrative,
                    onStrengthNarrativeChange = { strengthNarrative = it },
                    onSave = {
                        viewModel.update(
                            UserProfileUpdate(
                                coreWounds = commaSeparatedList(coreWounds),
                                joyAnchors = commaSeparatedList(joyAnchors),
                                anxietyTriggers = commaSeparatedList(anxietyTriggers),
                                depressionPatterns = commaSeparatedList(depressionPatterns),
                                physicalEmotionalLinks = commaSeparatedList(physicalEmotionalLinks),
                                strengthNarrative = strengthNarrative.trim(),
                            )
                        )
                    },
                    onLogout = onLogout,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileEditor(
    profile: UserProfileRead,
    coreWounds: String,
    onCoreWoundsChange: (String) -> Unit,
    joyAnchors: String,
    onJoyAnchorsChange: (String) -> Unit,
    anxietyTriggers: String,
    onAnxietyTriggersChange: (String) -> Unit,
    depressionPatterns: String,
    onDepressionPatternsChange: (String) -> Unit,
    physicalEmotionalLinks: String,
    onPhysicalEmotionalLinksChange: (String) -> Unit,
    strengthNarrative: String,
    onStrengthNarrativeChange: (String) -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit,
) {
    MenoCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            if (profile.joyAnchors.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    profile.joyAnchors.forEach { tag ->
                        androidx.compose.material3.Surface(
                            color = BlushPink,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeepRose,
                            )
                        }
                    }
                }
            }

            ProfileField(
                label = "Joy anchors",
                value = joyAnchors,
                onValueChange = onJoyAnchorsChange,
                supporting = "Comma-separated moments, places, rituals, or people that soften the day.",
            )
            ProfileField(
                label = "Anxiety triggers",
                value = anxietyTriggers,
                onValueChange = onAnxietyTriggersChange,
                supporting = "Patterns Mia should treat gently.",
            )
            ProfileField(
                label = "Depression patterns",
                value = depressionPatterns,
                onValueChange = onDepressionPatternsChange,
                supporting = "Warning signs you want remembered.",
            )
            ProfileField(
                label = "Physical-emotional links",
                value = physicalEmotionalLinks,
                onValueChange = onPhysicalEmotionalLinksChange,
                supporting = "Examples: poor sleep, hot flashes, sensory overwhelm, brain fog.",
            )
            ProfileField(
                label = "Core wounds",
                value = coreWounds,
                onValueChange = onCoreWoundsChange,
                supporting = "Optional deeper context Mia should not lose.",
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Strength narrative",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSoft,
                )
                OutlinedTextField(
                    value = strengthNarrative,
                    onValueChange = onStrengthNarrativeChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = menoOutlinedTextFieldColors(),
                    minLines = 4,
                    placeholder = { Text("How you want your resilience reflected back to you.", color = TextSoft) },
                )
                Text(
                    text = "How you want your resilience and identity reflected back to you.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSoft,
                )
            }

            MenoPrimaryButton(text = "Save my context", onClick = onSave)
            MenoSecondaryButton(text = "Log out", onClick = onLogout)
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    supporting: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSoft,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = menoOutlinedTextFieldColors(),
            placeholder = { Text(label, color = TextSoft) },
            minLines = 2,
        )
        Text(
            text = supporting,
            style = MaterialTheme.typography.labelSmall,
            color = TextSoft,
        )
    }
}

private fun commaSeparatedList(value: String): List<String> =
    value.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
