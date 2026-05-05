package com.menowell.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menowell.ui.theme.Apricot
import com.menowell.ui.theme.Butter
import com.menowell.ui.theme.Dawn
import com.menowell.ui.theme.Ink
import com.menowell.ui.theme.Mist
import com.menowell.ui.theme.Moss
import com.menowell.ui.theme.Mulberry
import com.menowell.ui.theme.Petal
import com.menowell.ui.theme.Rose
import com.menowell.ui.theme.SoftText
import com.menowell.ui.theme.SurfaceSoft

@Composable
fun MenoWellBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Dawn, Petal.copy(alpha = 0.9f), Mist),
                )
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 12.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Apricot.copy(alpha = 0.42f), Color.Transparent),
                    )
                )
                .fillMaxSize(0.34f),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 0.dp, bottom = 36.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Moss.copy(alpha = 0.22f), Color.Transparent),
                    )
                )
                .fillMaxSize(0.42f),
        )
        content()
    }
}

@Composable
fun MenoWellScrollScreen(
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    var viewportHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp, bottom = 28.dp)
            .onSizeChanged { viewportHeightPx = it.height },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = scrollState,
                    flingBehavior = ScrollableDefaults.flingBehavior(),
                )
                .padding(end = 10.dp),
                verticalArrangement = verticalArrangement,
                content = content,
        )

        if (scrollState.maxValue > 0 && viewportHeightPx > 0) {
            val viewportHeight = viewportHeightPx.toFloat()
            val contentHeight = viewportHeight + scrollState.maxValue
            val thumbHeight = (viewportHeight * viewportHeight / contentHeight)
                .coerceAtLeast(with(density) { 36.dp.toPx() })
            val maxOffset = (viewportHeight - thumbHeight).coerceAtLeast(0f)
            val offsetRatio = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
            val thumbOffset = maxOffset * offsetRatio

            VerticalScrollbar(
                thumbOffset = with(density) { thumbOffset.toDp() },
                thumbHeight = with(density) { thumbHeight.toDp() },
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
fun VerticalScrollbar(
    thumbOffset: androidx.compose.ui.unit.Dp,
    thumbHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(6.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.34f)),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = thumbOffset)
                .height(thumbHeight)
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(Mulberry.copy(alpha = 0.95f)),
        )
    }
}

@Composable
fun SectionTitle(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Ink,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftText,
            )
        }
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.55f),
                shape = RoundedCornerShape(30.dp),
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
    ) {
        content()
    }
}

@Composable
fun HighlightCard(
    title: String,
    body: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(tint.copy(alpha = 0.95f), tint.copy(alpha = 0.55f)),
                    )
                )
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.SemiBold)
                Text(body, style = MaterialTheme.typography.bodyMedium, color = Ink.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun PrimaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Mulberry, contentColor = SurfaceSoft),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SecondaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Butter, contentColor = Ink),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MoodPill(text: String, tint: Color = Rose) {
    Card(
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.24f)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            color = Ink,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    note: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    SoftCard(modifier = modifier) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = SoftText)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Ink)
            Text(note, style = MaterialTheme.typography.bodySmall, color = SoftText)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(tint.copy(alpha = 0.85f), tint.copy(alpha = 0.35f)),
                        )
                    )
            )
        }
    }
}

@Composable
fun FriendlyHeader(
    title: String,
    subtitle: String,
    accentLabel: String? = null,
) {
    SoftCard {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (accentLabel != null) {
                MoodPill(accentLabel, tint = Apricot)
            }
            Text(title, style = MaterialTheme.typography.headlineMedium, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = SoftText)
        }
    }
}

@Composable
fun MessageBubble(
    text: String,
    isUser: Boolean,
    meta: String? = null,
) {
    val bg = if (isUser) Mulberry else SurfaceSoft
    val fg = if (isUser) SurfaceSoft else Ink
    Card(
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = if (isUser) 24.dp else 8.dp,
            bottomEnd = if (isUser) 8.dp else 24.dp,
        ),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (meta != null) {
                Text(meta, style = MaterialTheme.typography.labelMedium, color = fg.copy(alpha = 0.72f))
            }
            Text(text, style = MaterialTheme.typography.bodyMedium, color = fg)
        }
    }
}
