package com.example.alliswelltemi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.ui.components.TemiNavBar
import com.example.alliswelltemi.ui.theme.HospitalColors

/**
 * Available Soon Screen - Placeholder for future features
 * Shows a coming soon message with animated graphics
 */
@Composable
fun AvailableSoonScreen(
    onBackPress: () -> Unit = {},
    currentLanguage: String = "en",
    featureName: String = "This Feature"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Navigation bar at the top
        TemiNavBar(
            currentLanguage = currentLanguage,
            onLogoClick = { onBackPress() }
        )

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HospitalColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(24.dp)
            ) {
                // Animated icon container
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = HospitalColors.SkyBlue.copy(alpha = 0.4f)
                        )
                        .background(
                            color = HospitalColors.SkyBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Coming Soon",
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                        tint = HospitalColors.SkyBlue
                    )
                }

                // Title
                Text(
                    text = if (currentLanguage == "hi") "जल्द ही आ रहा है" else "Coming Soon",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = HospitalColors.SkyBlue
                    ),
                    textAlign = TextAlign.Center
                )

                // Feature name
                Text(
                    text = featureName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HospitalColors.DeepSlate
                    ),
                    textAlign = TextAlign.Center
                )

                // Description
                Text(
                    text = if (currentLanguage == "hi") {
                        "यह सुविधा शीघ्र ही उपलब्ध होगी। कृपया जल्द ही वापस आएं।"
                    } else {
                        "This feature will be available soon. Please check back later."
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 24.sp,
                        color = HospitalColors.DeepSlate.copy(alpha = 0.7f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Spacer
                Spacer(modifier = Modifier.height(24.dp))

                // Back button
                Button(
                    onClick = onBackPress,
                    modifier = Modifier
                        .height(56.dp)
                        .width(240.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HospitalColors.SkyBlue
                    )
                ) {
                    Text(
                        text = if (currentLanguage == "hi") "वापस जाएं" else "Go Back",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = HospitalColors.Background
                        )
                    )
                }
            }
        }
    }
}

