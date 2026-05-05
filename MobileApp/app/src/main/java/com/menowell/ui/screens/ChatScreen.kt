package com.menowell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menowell.data.model.ChatMessageRead
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoSecondaryButton
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.components.menoOutlinedTextFieldColors
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.Cream
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.LoraFamily
import com.menowell.ui.theme.SoftWhite
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.TextSoft
import com.menowell.ui.theme.WarmMist
import com.menowell.viewmodel.ChatUiState
import com.menowell.viewmodel.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val canClearChat = (state as? ChatUiState.Data)?.messages?.isNotEmpty() == true

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MenoSectionLabel("Mia is listening")
            MenoSecondaryButton(
                text = "Clear chat",
                onClick = { viewModel.clearVisibleChat() },
                enabled = canClearChat,
                modifier = Modifier.widthIn(min = 116.dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Talk to Mia",
                style = MaterialTheme.typography.displaySmall,
                color = TextDeep,
            )
            Text(
                text = "Say it the way it actually feels. Irritable, raw, invisible, overstimulated, tired, relieved, angry, lost. Mia can handle plain truth.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("I feel anxious", "I need to breathe", "Help me sleep")) { prompt ->
                EmotionChip(prompt) {
                    message = prompt
                    viewModel.send(prompt)
                    message = ""
                }
            }
        }

            MenoCard(containerColor = WarmMist) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                Text(
                    text = "No need to sound composed",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                    color = TextMid,
                )
                Text(
                    text = "This space is for emotional swings, body frustration, sleep grief, and the quiet question of who you are becoming now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
        }

        when (val s = state) {
            is ChatUiState.Error -> {
                MenoCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(24.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = s.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            is ChatUiState.Loading -> {
                MenoCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(24.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = DustyRose)
                    }
                }
            }
            is ChatUiState.Data -> {
                LaunchedEffect(s.messages.size) {
                    if (s.messages.isNotEmpty()) {
                        listState.animateScrollToItem(s.messages.lastIndex)
                    }
                }

                MenoCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(s.messages) { item ->
                            ChatRow(item)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { Text("Write to Mia...", color = TextSoft) },
                shape = RoundedCornerShape(28.dp),
                colors = menoOutlinedTextFieldColors(),
                maxLines = 4,
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DustyRose)
                    .clickable {
                        if (message.isNotBlank()) {
                            viewModel.send(message.trim())
                            message = ""
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = SoftWhite,
                )
            }
        }
    }
}

@Composable
private fun ChatRow(message: ChatMessageRead) {
    val isUser = message.role.equals("user", ignoreCase = true)
    Box(modifier = Modifier.fillMaxWidth()) {
        if (isUser) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    color = DustyRose,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 4.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp,
                    ),
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftWhite,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Mia",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextSoft,
                )
                androidx.compose.material3.Surface(
                    color = BlushPink,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp,
                    ),
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = LoraFamily),
                        color = TextDeep,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionChip(
    text: String,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Surface(
        color = BlushPink,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = com.menowell.ui.theme.DeepRose,
        )
    }
}
