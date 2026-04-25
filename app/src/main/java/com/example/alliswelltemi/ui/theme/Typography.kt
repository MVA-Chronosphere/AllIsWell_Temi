package com.example.alliswelltemi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val TemiTypography = Typography(
    // Headings: Weight 900 (Black), Uppercase, Letter Spacing -1px, Line-height 0.9
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 38.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp,
        color = HospitalColors.DeepSlate
    ),
    // Subtitles: Weight 700 (Bold), Uppercase, Letter Spacing 1px
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 1.sp,
        color = HospitalColors.DeepSlate.copy(alpha = 0.6f)
    ),
    // Standard Headline (Screen Titles)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.5).sp,
        color = HospitalColors.DeepSlate
    ),
    // Card Titles (White)
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.5).sp,
        color = HospitalColors.PureWhite
    ),
    // Doctor/Department Name / Form Input
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        color = HospitalColors.DeepSlate
    ),
    // Button Labels / Navigation (Weight 900)
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = HospitalColors.PureWhite
    ),
    // Input labels (Weight 700)
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp,
        color = HospitalColors.DeepSlate.copy(alpha = 0.6f)
    ),
    // Summary Values / Standard Text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = HospitalColors.DeepSlate
    ),
    // Summary Labels / Specialty
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = HospitalColors.SkyBlue
    )
)

