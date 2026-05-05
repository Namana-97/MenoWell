package com.menowell.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val MenoWellLightColorScheme = lightColorScheme(
    primary = DustyRose,
    onPrimary = SoftWhite,
    primaryContainer = BlushPink,
    onPrimaryContainer = DeepRose,
    secondary = SageGreen,
    onSecondary = SoftWhite,
    secondaryContainer = SageMist,
    background = Cream,
    onBackground = TextDeep,
    surface = SoftWhite,
    onSurface = TextDeep,
    surfaceVariant = WarmMist,
    onSurfaceVariant = TextMid,
    outline = BorderSoft,
    error = CrisisWarm,
    onError = SoftWhite
)

private val AppShapes = androidx.compose.material3.Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MenoWellTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MenoWellLightColorScheme,
        typography = MenoWellTypography,
        shapes = AppShapes,
        content = content
    )
}
