package com.example.alliswelltemi.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Blue Tech Theme Color Palette - "Hospital Appointment Booking Interface"
 * Clinical, trustworthy, high-tech, and minimal aesthetic
 */
object HospitalColors {
    // ============================================
    // PRIMARY BLUE TECH PALETTE
    // ============================================
    // Primary (Chai): #2563EB (Royal Blue) – Used for action buttons and active states.
    val Chai = Color(0xFF2563EB)

    // Secondary (Pistache): #6366F1 (Indigo) – Used for highlighting and secondary buttons.
    val Pistache = Color(0xFF6366F1)

    // Accent (Matcha): #0EA5E9 (Sky Blue) – Used for subtle accents and indicators.
    val Matcha = Color(0xFF0EA5E9)

    // Text (Carob): #0F172A (Deep Slate/Navy) – Used for high-contrast typography.
    val Carob = Color(0xFF0F172A)

    // Background (Almond): #EDE7D8 (New Hospital Beige) – Used as the base app background.
    val Almond = Color(0xFFEDE7D8)

    // Surface (Vanilla): #FFFFFF (Pure White) – Used for cards and input fields.
    val Vanilla = Color(0xFFFFFFFF)

    // ============================================
    // SEMANTIC & LEGACY ALIASES (For backward compatibility)
    // ============================================
    val RoyalBlue = Chai                    // Primary buttons
    val Indigo = Pistache                   // Highlighting
    val SkyBlue = Matcha                    // Accents
    val DeepSlate = Carob                   // Text
    val LightSlate = Almond                 // Background
    val PureWhite = Vanilla                 // Surfaces
    val PastelWhite = Vanilla               // Pastel White
    val CreamBackground = Almond            // Custom background

    // Semantic Aliases
    val Background = Almond
    val Surface = Vanilla
    val TextPrimary = Carob
    val TextOnPrimary = Vanilla
    val Accent = Chai
    val White = Vanilla

    // Error & Semantic Colors
    val ErrorRed = Color(0xFFDC2626)
    val SuccessGreen = Color(0xFF16A34A)
    val ConfirmGreen = SuccessGreen
    val CancelRed = ErrorRed
    val NextBlue = Chai
    val BackDark = Carob

    // Helper functions
    fun getCardColor(index: Int): Color = if (index % 2 == 0) Matcha else Pistache
}
