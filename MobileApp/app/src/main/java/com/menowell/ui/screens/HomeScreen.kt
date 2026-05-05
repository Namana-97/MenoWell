package com.menowell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.menowell.ui.components.MenoCard
import com.menowell.ui.components.MenoPrimaryButton
import com.menowell.ui.components.MenoSecondaryButton
import com.menowell.ui.components.MenoSectionLabel
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.Cream
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.SageMist
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.WarmMist

@Composable
fun HomeScreen(
    onGoChat: () -> Unit,
    onGoCheckIn: () -> Unit,
    onGoLetter: () -> Unit,
    onGoProfile: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MenoSectionLabel("For women moving through menopause")
        MenoCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "A soft place to land",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextDeep,
                )
                Text(
                    text = "Built for menopause days that feel heavy, foggy, restless, or unexpectedly emotional. Mia stays warm without sounding clinical.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMid,
                )
            }
        }
        MenoCard(containerColor = WarmMist) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Today's gentle starting point",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid,
                )
                Text(
                    text = "You do not need to be productive here. Start with the part of today that felt tender, sharp, or exhausting.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDeep,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MenoPrimaryButton(
                        text = "Talk to Mia",
                        onClick = onGoChat,
                        modifier = Modifier.weight(1f),
                    )
                    MenoSecondaryButton(
                        text = "Check in",
                        onClick = onGoCheckIn,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeInfoCard(
                title = "Support",
                headline = "Tender",
                body = "Made to calm the nervous system",
                accentColor = DustyRose,
                modifier = Modifier.weight(1f),
            )
            HomeInfoCard(
                title = "Memory",
                headline = "Private",
                body = "Mia keeps the thread",
                accentColor = SageMist,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = "Quick actions",
            style = MaterialTheme.typography.headlineMedium,
            color = TextDeep,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                icon = Icons.Default.ChatBubbleOutline,
                title = "Open Mia",
                subtitle = "Start a calm conversation",
                onClick = onGoChat,
            )
            QuickActionCard(
                icon = Icons.Default.CalendarMonth,
                title = "Daily check-in",
                subtitle = "A quick 60-second reflection",
                onClick = onGoCheckIn,
            )
            QuickActionCard(
                icon = Icons.Default.Spa,
                title = "Weekly letter",
                subtitle = "Read Mia’s kind summary",
                onClick = onGoLetter,
            )
            QuickActionCard(
                icon = Icons.Default.PersonOutline,
                title = "My profile",
                subtitle = "See what Mia remembers",
                onClick = onGoProfile,
            )
        }
        MenoCard(containerColor = WarmMist) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Your body is not betraying you",
                    style = MaterialTheme.typography.displaySmall.copy(fontStyle = FontStyle.Italic),
                    color = TextDeep,
                )
                Text(
                    text = "This space is designed for women dealing with heat surges, insomnia, irritability, grief, and the strange loneliness that can come with hormonal change.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMid,
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    MenoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(BlushPink),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = DustyRose)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(title, style = MaterialTheme.typography.headlineSmall, color = TextDeep)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMid,
                )
            }
            MenoSecondaryButton(
                text = "Open",
                onClick = onClick,
                modifier = Modifier.widthIn(min = 92.dp),
            )
        }
    }
}

@Composable
private fun HomeInfoCard(
    title: String,
    headline: String,
    body: String,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    MenoCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextMid,
            )
            Text(
                text = headline,
                style = MaterialTheme.typography.displaySmall,
                color = TextDeep,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMid,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                        .height(8.dp)
                ) {
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.5f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f),
                    )
                }
            }
        }
    }
}
