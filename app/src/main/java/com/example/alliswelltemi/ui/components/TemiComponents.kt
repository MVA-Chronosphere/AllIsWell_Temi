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
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

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

/**
 * Clinical-style button for appointment and feedback screens
 */
@Composable
fun ClinicalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = HospitalColors.Chai,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
    }
}

/**
 * Clinical-style text field for appointment and feedback screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isLarge: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Text
    ),
    robot: Robot? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = HospitalColors.Chai
                )
            }
        } else null,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HospitalColors.Chai,
            unfocusedBorderColor = HospitalColors.Carob.copy(alpha = 0.3f),
            focusedLabelColor = HospitalColors.Chai,
            unfocusedLabelColor = HospitalColors.Carob.copy(alpha = 0.6f),
            cursorColor = HospitalColors.Chai,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        maxLines = if (isLarge) 5 else 1,
        minLines = if (isLarge) 3 else 1
    )
}


/**
 * Global Status Bar - Hospital branding and time (top of screen)
 */
@Composable
fun GlobalStatusBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = HospitalColors.PureWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 60.dp, vertical = 16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hospital name
            Text(
                text = "ALL IS WELL HOSPITAL",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp,
                    color = HospitalColors.DeepSlate
                )
            )

            // Date/Time (optional - you can add live time here)
            Text(
                text = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = HospitalColors.DeepSlate.copy(alpha = 0.7f)
                )
            )
        }
    }
}

/**
 * Voice Footer Overlay - Floating microphone and voice hints
 */
@Composable
fun VoiceFooterOverlay(
    modifier: Modifier = Modifier,
    isListening: Boolean = false,
    onMicClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(100.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(50.dp),
                spotColor = HospitalColors.RoyalBlue.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(50.dp),
        color = HospitalColors.PureWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Voice Command: \"Take me to the Cardiology Department\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = HospitalColors.DeepSlate.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 32.dp)
                    .background(HospitalColors.RoyalBlue, CircleShape)
                    .clickable(onClick = onMicClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

/**
 * Navigation Overlay - Full-screen overlay when robot is navigating
 */
@Composable
fun NavigationOverlayAnimated(
    title: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nav_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                0.6f at 0 with FastOutSlowInEasing
                1f at 750 with FastOutSlowInEasing
                0.6f at 1500 with FastOutSlowInEasing
            }
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = null,
                tint = HospitalColors.SkyBlue.copy(alpha = pulseAlpha),
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )

            CircularProgressIndicator(
                color = HospitalColors.SkyBlue,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/**
 * Segmented Toggle Control - Two-option toggle for screens
 */
@Composable
fun SegmentedToggleControl(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onTabChange: (Int) -> Unit,
    options: List<String>
) {
    Surface(
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(34.dp),
        color = Color(0xFFF1F5F9),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedIndex == index
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) HospitalColors.RoyalBlue else Color.Transparent,
                    label = "toggle_bg_$index"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else HospitalColors.DeepSlate,
                    label = "toggle_text_$index"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(bgColor, RoundedCornerShape(30.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabChange(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}


