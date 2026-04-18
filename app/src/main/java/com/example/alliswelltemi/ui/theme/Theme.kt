package com.example.alliswelltemi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark Theme Colors for Temi
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D9FF),
    onPrimary = Color(0xFF0B1220),
    secondary = Color(0xFFB100FF),
    onSecondary = Color(0xFF0B1220),
    tertiary = Color(0xFF00FF41),
    onTertiary = Color(0xFF0B1220),
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1A2332),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF242E3F),
    onSurfaceVariant = Color(0xFFA0A0A0),
    error = Color(0xFFFF006E),
    onError = Color(0xFF0B1220)
)

// Light Theme Colors (fallback)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A8A),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF7B1FA2),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF00897B),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF1F2937),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun TemiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TemiTypography,
        content = content
    )
}

