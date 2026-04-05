package com.aarogyam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.aarogyam.data.datastore.PreferencesRepository

enum class AppTheme { DARK, LIGHT, FOREST }

// DARK theme (original charcoal+amber)
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

// LIGHT theme (white bg + amber)
private val LightColorScheme = lightColorScheme(
    primary = Amber400,
    onPrimary = Color(0xFF1A1A1E),
    primaryContainer = Color(0xFFFFF3E0),
    onPrimaryContainer = Color(0xFF5D3A00),
    secondary = Color(0xFF757575),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = Color(0xFF212121),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1A1E),
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF1A1A1E),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF757575),
    error = Danger,
    onError = Color(0xFFFFFFFF)
)

// FOREST theme (dark green bg + teal accent)
private val ForestBg = Color(0xFF2E7D32)
private val ForestAccent = Color(0xFF4CAF50)
private val ForestSurface = Color(0xFF1B5E20)
private val ForestSurfaceVariant = Color(0xFF388E3C)
private val ForestOnSurface = Color(0xFFE8F5E9)
private val ForestOnSurfaceMuted = Color(0xFFA5D6A7)

private val ForestColorScheme = darkColorScheme(
    primary = ForestAccent,
    onPrimary = Color(0xFF1A1A1E),
    primaryContainer = ForestSurfaceVariant,
    onPrimaryContainer = ForestOnSurface,
    secondary = ForestSurfaceVariant,
    onSecondary = ForestOnSurface,
    secondaryContainer = ForestSurface,
    onSecondaryContainer = ForestOnSurface,
    background = ForestBg,
    onBackground = ForestOnSurface,
    surface = ForestSurface,
    onSurface = ForestOnSurface,
    surfaceVariant = ForestSurfaceVariant,
    onSurfaceVariant = ForestOnSurfaceMuted,
    error = Danger,
    onError = ForestOnSurface
)

@Composable
fun AarogyamTheme(
    appTheme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.DARK -> DarkColorScheme
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.FOREST -> ForestColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun AarogyamThemeFromDataStore(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val prefsRepo = PreferencesRepository(context)
    val themeStr by prefsRepo.appTheme.collectAsState(initial = "DARK")
    val appTheme = when (themeStr) {
        "LIGHT" -> AppTheme.LIGHT
        "FOREST" -> AppTheme.FOREST
        else -> AppTheme.DARK
    }
    AarogyamTheme(appTheme = appTheme, content = content)
}
