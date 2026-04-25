package com.example.alliswelltemi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================
// NEW MATCHA/ALMOND COLOR PALETTE (FINAL)
// ============================================
val Matcha = Color(0xFF809671)          // Primary accent, Card A, Active state
val Almond = Color(0xFFE5E0D8)          // Main background (light)
val Pistache = Color(0xFFB3B792)        // Card B, Borders
val Chai = Color(0xFFD2AB80)            // Primary buttons, secondary accent
val Carob = Color(0xFF725C3A)           // Text, headings
val Vanilla = Color(0xFFE5D2B8)         // Secondary surfaces, toggle background
val White = Color(0xFFFFFFFF)           // White text on dark cards

// Light Theme - Hospital Premium Design
private val LightColorScheme = lightColorScheme(
    primary = Matcha,
    onPrimary = White,
    secondary = Chai,
    onSecondary = White,
    tertiary = Pistache,
    onTertiary = Carob,
    background = Almond,
    onBackground = Carob,
    surface = Vanilla,
    onSurface = Carob,
    surfaceVariant = Pistache,
    onSurfaceVariant = Carob,
    error = Color(0xFFC85A54),
    onError = White
)

@Composable
fun TemiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme  // Always use light theme for hospital app

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TemiTypography,
        content = content
    )
}

