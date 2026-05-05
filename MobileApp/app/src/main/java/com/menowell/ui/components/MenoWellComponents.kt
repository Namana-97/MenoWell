package com.menowell.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.BorderSoft
import com.menowell.ui.theme.DeepRose
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.SoftWhite
import com.menowell.ui.theme.TextDeep
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.TextSoft

@Composable
fun MenoCard(
    modifier: Modifier = Modifier,
    containerColor: Color = SoftWhite,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content,
        )
    }
}

@Composable
fun MenoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DustyRose,
            contentColor = SoftWhite,
            disabledContainerColor = DustyRose.copy(alpha = 0.5f),
            disabledContentColor = SoftWhite.copy(alpha = 0.8f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            )
        )
    }
}

@Composable
fun MenoSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, BorderSoft),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SoftWhite,
            contentColor = DustyRose,
            disabledContainerColor = SoftWhite,
            disabledContentColor = DustyRose.copy(alpha = 0.5f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = DustyRose,
            )
        )
    }
}

@Composable
fun MenoSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = BlushPink,
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = DeepRose,
        )
    }
}

@Composable
fun MenoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = DustyRose,
                activeTrackColor = DustyRose,
                inactiveTrackColor = BlushPink,
                activeTickColor = DustyRose,
                inactiveTickColor = BlushPink,
            ),
        )
        Text(
            text = valueLabel,
            style = MaterialTheme.typography.labelLarge,
            color = TextMid,
        )
    }
}

data class MenoBottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun MenoBottomNav(
    items: List<MenoBottomNavItem>,
    currentRoute: String?,
    onItemClick: (MenoBottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = SoftWhite,
            border = BorderStroke(1.dp, BorderSoft),
            shadowElevation = 8.dp,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    BottomNavItem(
                        item = item,
                        selected = selected,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    item: MenoBottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (selected) DustyRose else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selected) SoftWhite else TextSoft,
                )
            }
            if (selected) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = DustyRose,
                )
            } else {
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.Transparent,
                )
            }
        }
    }
}

@Composable
fun menoOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BorderSoft,
    unfocusedBorderColor = BorderSoft,
    disabledBorderColor = BorderSoft,
    focusedContainerColor = SoftWhite,
    unfocusedContainerColor = SoftWhite,
    disabledContainerColor = SoftWhite,
    cursorColor = DeepRose,
    focusedTextColor = TextDeep,
    unfocusedTextColor = TextDeep,
    disabledTextColor = TextDeep,
    focusedLabelColor = TextMid,
    unfocusedLabelColor = TextMid,
    focusedPlaceholderColor = TextSoft,
    unfocusedPlaceholderColor = TextSoft,
    focusedSupportingTextColor = TextSoft,
    unfocusedSupportingTextColor = TextSoft,
    focusedTrailingIconColor = TextSoft,
    unfocusedTrailingIconColor = TextSoft,
)

@Composable
fun menoFilledTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = SoftWhite,
    unfocusedContainerColor = SoftWhite,
    disabledContainerColor = SoftWhite,
    cursorColor = DeepRose,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedTextColor = TextDeep,
    unfocusedTextColor = TextDeep,
    focusedPlaceholderColor = TextSoft,
    unfocusedPlaceholderColor = TextSoft,
)
