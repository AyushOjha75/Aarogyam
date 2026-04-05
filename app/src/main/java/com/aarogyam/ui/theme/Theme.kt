package com.aarogyam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Amber400,
    onPrimary = Charcoal950,
    primaryContainer = Charcoal800,
    onPrimaryContainer = Amber400,
    secondary = Charcoal600,
    onSecondary = OnSurface,
    secondaryContainer = Charcoal800,
    onSecondaryContainer = OnSurface,
    background = Charcoal950,
    onBackground = OnSurface,
    surface = Charcoal800,
    onSurface = OnSurface,
    surfaceVariant = Charcoal600,
    onSurfaceVariant = OnSurfaceMuted,
    error = Danger,
    onError = OnSurface
)

@Composable
fun AarogyamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
