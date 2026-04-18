package com.example.alliswelltemi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Header Component - Hospital name, greeting, and language selector
 */
@Composable
fun TemiHeaderComponent(
    onLanguageClick: () -> Unit = {},
    currentLanguage: String = "English",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A2332),
                        Color(0xFF0B1220)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hospital name
        Text(
            text = "All Is Well Hospital",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00D9FF),
            modifier = Modifier.align(Alignment.Start)
        )

        // Language selector button (pill-shaped, right-aligned)
        LanguageSelectorButton(
            currentLanguage = currentLanguage,
            onClick = onLanguageClick,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

/**
 * Language Selector - Pill-shaped button with globe icon
 */
@Composable
fun LanguageSelectorButton(
    currentLanguage: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF2A3F5F) else Color(0xFF1A2332),
        label = "Language button background"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x6600D9FF),
                        Color(0x3300D9FF)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Color(0xFF00D9FF),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Public,
            contentDescription = "Language",
            tint = Color(0xFF00D9FF),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = currentLanguage,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00D9FF)
        )
    }
}

/**
 * Hero/Greeting Section
 */
@Composable
fun TemiGreetingSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Hello! I'm Temi",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFFFF),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Your smart hospital assistant.\nHow can I help you today?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFA0A0A0),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Glowing Robot Avatar (Simplified SVG-like representation)
 */
@Composable
fun TemiAvatarComponent(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.4f at 0
                0.8f at 1000
                0.4f at 2000
            }
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .size(120.dp)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = Color(0xFF00D9FF).copy(alpha = glowAlpha),
                spotColor = Color(0xFF00D9FF).copy(alpha = glowAlpha)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A2332),
                        Color(0xFF0B1220)
                    )
                )
            )
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00D9FF),
                        Color(0xFFB100FF)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Simplified robot face (emoji-like)
        Text(
            text = "🤖",
            fontSize = 60.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Feature Card with Gradient Background
 */
@Composable
fun TemiMenuCard(
    title: String,
    subtitle: String,
    icon: String,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x44000000)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Text(
                    text = icon,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Title
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Subtitle
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFE0E0E0)
                )
            }
        }
    }
}

/**
 * Voice Input Bar (Bottom)
 */
@Composable
fun TemiVoiceBarComponent(
    onMicClick: () -> Unit,
    isListening: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
    val micGlowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isListening) 0.5f else 0.2f,
        targetValue = if (isListening) 1f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                if (isListening) 0.5f else 0.2f at 0
                if (isListening) 1f else 0.2f at 750
                if (isListening) 0.5f else 0.2f at 1500
            }
        ),
        label = "mic_glow_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .height(70.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(35.dp),
                ambientColor = Color(0x4400D9FF),
                spotColor = Color(0x5500D9FF)
            ),
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Waveform icon (simplified animation)
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(if (isListening && index % 2 == 0) 24.dp else 12.dp)
                                .background(
                                    color = Color(0xFF00D9FF),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }

            // Voice hint text
            Text(
                text = "You can also say: 'Take me to Pharmacy' or 'Book an appointment'",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA0A0A0),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            // Microphone button (glowing circular)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(
                        elevation = if (isListening) 16.dp else 8.dp,
                        shape = CircleShape,
                        ambientColor = Color(0xFF00D9FF).copy(alpha = micGlowAlpha),
                        spotColor = Color(0xFF00D9FF).copy(alpha = micGlowAlpha)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00D9FF),
                                Color(0x6600D9FF)
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onMicClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color(0xFF0B1220),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Menu Grid Layout (2x3)
 */
@Composable
fun TemiMenuGridComponent(
    menuItems: List<MenuItemData>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Create 3 rows with 2 columns each
        repeat((menuItems.size + 1) / 2) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val startIndex = rowIndex * 2
                repeat(2) { colIndex ->
                    val itemIndex = startIndex + colIndex
                    if (itemIndex < menuItems.size) {
                        val item = menuItems[itemIndex]
                        Box(modifier = Modifier.weight(1f)) {
                            TemiMenuCard(
                                title = item.title,
                                subtitle = item.subtitle,
                                icon = item.icon,
                                gradient = item.gradient,
                                onClick = { onItemClick(item.id) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Data class for menu items
 */
data class MenuItemData(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val gradient: Brush
)

// Helper function to get gradient brushes
fun getGradientBrush(type: String): Brush {
    return when (type) {
        "blue" -> Brush.linearGradient(
            colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
        )
        "teal" -> Brush.linearGradient(
            colors = listOf(Color(0xFF00695C), Color(0xFF00897B))
        )
        "purple" -> Brush.linearGradient(
            colors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))
        )
        "red" -> Brush.linearGradient(
            colors = listOf(Color(0xFFB71C1C), Color(0xFFD32F2F))
        )
        "orange" -> Brush.linearGradient(
            colors = listOf(Color(0xFFE65100), Color(0xFFF57C00))
        )
        "indigo" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1A237E), Color(0xFF283593))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
        )
    }
}

