package com.menowell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menowell.data.model.CheckInRequest
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoPrimaryButton
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.components.MenoSlider
import com.menowell.ui.components.menoOutlinedTextFieldColors
import com.menowell.ui.theme.Cream
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.SageGreen
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.WarmMist
import com.menowell.viewmodel.CheckInUiState
import com.menowell.viewmodel.CheckInViewModel
import kotlin.math.roundToInt

@Composable
fun CheckInScreen(viewModel: CheckInViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var oneWord by remember { mutableStateOf("") }
    var body by remember { mutableStateOf(3f) }
    var mind by remember { mutableStateOf(3f) }
    var hurt by remember { mutableStateOf("") }
    var helped by remember { mutableStateOf("") }
    var flashes by remember { mutableStateOf(0f) }
    var supplements by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MenoSectionLabel("Daily check-in")
            Text(
                text = "How did today live in your body?",
                style = MaterialTheme.typography.displayMedium,
                color = TextDeep,
            )
            Text(
                text = "Capture the emotional weather, the physical spikes, and the one small thing that made the day easier.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMid,
            )
        }
        MenoCard(containerColor = WarmMist) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "A tiny ritual for the hard days",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid,
                )
                Text(
                    text = "This is for the days that feel irritable, foggy, sweaty, sleepless, or unexpectedly sad. Keep it short. Just tell the truth.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDeep,
                )
            }
        }
        MenoCard {
            OutlinedTextField(
                value = oneWord,
                onValueChange = { oneWord = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("In one word, today feels...") },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = menoOutlinedTextFieldColors(),
                singleLine = true,
            )
        }
        SliderSection(title = "Body today", value = body, valueRange = 1f..5f) { body = it }
        SliderSection(title = "Mind today", value = mind, valueRange = 1f..5f) { mind = it }
        SliderSection(title = "Hot flashes", value = flashes, valueRange = 0f..10f) { flashes = it }
        MenoCard {
            OutlinedTextField(
                value = hurt,
                onValueChange = { hurt = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Something that hurt today") },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = menoOutlinedTextFieldColors(),
                minLines = 3,
            )
        }
        MenoCard {
            OutlinedTextField(
                value = helped,
                onValueChange = { helped = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Something that helped today") },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = menoOutlinedTextFieldColors(),
                minLines = 3,
            )
        }
        MenoCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Did I take my supplements?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDeep,
                )
                Switch(
                    checked = supplements,
                    onCheckedChange = { supplements = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Cream,
                        checkedTrackColor = SageGreen,
                    ),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            MenoPrimaryButton(
                text = "Save today's check-in",
                onClick = {
                    viewModel.submit(
                        CheckInRequest(
                            inOneWord = oneWord.ifBlank { null },
                            bodyScore = body.roundToInt(),
                            mindScore = mind.roundToInt(),
                            hurtToday = hurt.ifBlank { null },
                            helpedToday = helped.ifBlank { null },
                            hotFlashes = flashes.roundToInt(),
                            supplementsTaken = supplements,
                        )
                    )
                },
            )
            when (val s = state) {
                is CheckInUiState.Loading -> CircularProgressIndicator(color = DustyRose)
                is CheckInUiState.Success -> {
                    Text(
                        text = "Saved for ${s.record.checkinDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMid,
                    )
                }
                is CheckInUiState.Error -> {
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun SliderSection(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    MenoCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextDeep,
            )
            MenoSlider(
                value = value,
                onValueChange = onChange,
                valueRange = valueRange,
                steps = (valueRange.endInclusive - valueRange.start).toInt() - 1,
                valueLabel = value.roundToInt().toString(),
            )
        }
    }
}
