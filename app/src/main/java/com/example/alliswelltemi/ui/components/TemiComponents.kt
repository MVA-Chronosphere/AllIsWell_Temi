package com.example.alliswelltemi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.platform.LocalContext
import androidx.webkit.WebViewAssetLoader

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
            text = "Hello! I'm Chronexa",
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

/**
 * Reusable Navigation Bar - Logo, Daily Quote, and Live Date/Time
 */
@Composable
fun TemiNavBar(
    currentLanguage: String,
    modifier: Modifier = Modifier,
    onLogoClick: () -> Unit = {}
) {
    // Logo
    val logoPainter = painterResource(id = com.example.alliswelltemi.R.drawable.hospital_logo)

    // Daily quote selection (simple: rotate by day of year)
    val quotes: List<Pair<Int, Int>> = listOf(
        Pair(com.example.alliswelltemi.R.string.quote_1, com.example.alliswelltemi.R.string.quote_1_hi),
        Pair(com.example.alliswelltemi.R.string.quote_2, com.example.alliswelltemi.R.string.quote_2_hi),
        Pair(com.example.alliswelltemi.R.string.quote_3, com.example.alliswelltemi.R.string.quote_3_hi)
    )
    val dayOfYear = LocalDateTime.now().dayOfYear
    val quoteIdx = dayOfYear % quotes.size
    val quoteRes = if (currentLanguage == "hi") quotes[quoteIdx].second else quotes[quoteIdx].first
    val quote = stringResource(id = quoteRes)

    // Date/time state
    var dateTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            dateTime = now.format(DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm"))
            delay(1000)
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth().height(72.dp)
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo left (now clickable)
            Image(
                painter = logoPainter,
                contentDescription = "Hospital Logo",
                modifier = Modifier
                    .size(192.dp)
                    .clickable(onClick = onLogoClick)
            )
            Spacer(Modifier.width(32.dp))
            // Quote center
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = quote,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            // Date/time right
            Text(
                text = dateTime,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

/**
 * 3D Model Viewer Component - Displays a 3D GLB model using WebView with ModelViewer library
 * Features: Auto-rotation, touch-disabled (kiosk mode), responsive sizing
 */
@Composable
fun Model3DViewer(
    modifier: Modifier = Modifier,
    modelPath: String = "models/indian_doctor_lipsync.glb",
    viseme: String = "viseme_sil",
    intensity: Float = 0f
) {
    // Context for WebView
    val context = LocalContext.current

    // Keep track of the WebView instance to send JavaScript commands
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var webViewReady by remember { mutableStateOf(false) }

    // Update visemes in the 3D model when they change
    // This is called frequently (~30 FPS) from TtsLipSyncManager
    LaunchedEffect(viseme, intensity) {
        if (webViewReady && webViewInstance != null) {
            // Escape viseme name to ensure safe JavaScript execution
            val safeViseme = viseme.replace("'", "\\'")
            webViewInstance?.evaluateJavascript(
                "if(typeof window.updateViseme === 'function') { window.updateViseme('$safeViseme', $intensity); }",
                null
            )
            android.util.Log.d("Model3DViewer", "✓ Viseme update sent: $viseme, intensity: $intensity")
        }
    }

    // Set up WebViewAssetLoader to serve local assets via https://
    // This bypasses Fetch API restrictions on file:/// URLs
    val assetLoader = remember {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    // Virtual URL for the model
    val modelUrl = "https://appassets.androidplatform.net/assets/$modelPath"

    // HTML template using Three.js logic from avatar-view.html
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <style>
            * { margin: 0; padding: 0; }
            body { background: transparent; overflow: hidden; width: 100vw; height: 100vh; }
            canvas { display: block; width: 100%; height: 100%; }
          </style>
        </head>
        <body>
          <script type="importmap">
          {
            "imports": {
              "three": "https://appassets.androidplatform.net/assets/scripts/three.module.js",
              "three/examples/jsm/loaders/GLTFLoader": "https://appassets.androidplatform.net/assets/scripts/GLTFLoader.js",
              "three/examples/jsm/utils/BufferGeometryUtils": "https://appassets.androidplatform.net/assets/utils/BufferGeometryUtils.js"
            }
          }
          </script>
          <script type="module">
            import * as THREE from 'three';
            import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';

            const scene = new THREE.Scene();
            // Transparent background to match Compose theme
            scene.background = null; 
            
            const camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 0.1, 200);
            const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
            renderer.setSize(window.innerWidth, window.innerHeight);
            renderer.setPixelRatio(window.devicePixelRatio);
            renderer.outputColorSpace = THREE.SRGBColorSpace;
            document.body.appendChild(renderer.domElement);

            // Lighting — matched to original setup
            scene.add(new THREE.AmbientLight(0xffffff, 2.2));
            const dirLight = new THREE.DirectionalLight(0xffffff, 0.6);
            dirLight.position.set(0.5, 1.5, 2);
            scene.add(dirLight);

            let mixer = null;
            let model = null;
            let clock = new THREE.Clock();

            let morphMeshes = [];
            let teethMeshes = [];
            let headBone = null;
            let neckBone = null;
            let rightUpperArm = null;
            let leftUpperArm = null;
            let rightForeArm = null;
            let leftForeArm = null;
            let rightHand = null;
            let leftHand = null;
            let restPose = {};
            let idlePoseQuats = {};

            // Idle Pose Configuration
            const IDLE_POSE = {
              rightUpper: [1.36, 0, -0.12],
              rightFore:  [0.10, 0, 0],
              rightHand:  [0, 0, 0],
              leftUpper:  [1.36, 0, 0.12],
              leftFore:   [0.10, 0, 0],
              leftHand:   [0, 0, 0],
            };

            // Viseme smoothing state
            const currentWeights = {};
            const targetWeights = {};
            const OCULUS_VISEMES = [
              'viseme_aa', 'viseme_E', 'viseme_I', 'viseme_O', 'viseme_U',
              'viseme_PP', 'viseme_SS', 'viseme_TH', 'viseme_DD', 'viseme_FF',
              'viseme_kk', 'viseme_nn', 'viseme_RR', 'viseme_CH', 'viseme_sil'
            ];
            OCULUS_VISEMES.forEach(v => { currentWeights[v] = 0; targetWeights[v] = 0; });
            const LERP_SPEED = 0.10;  // Reduced for softer transitions
             const VISEME_MAX = {
               'viseme_sil': 0.00,
               'viseme_PP':  0.30,
               'viseme_FF':  0.22,
               'viseme_TH':  0.18,
               'viseme_DD':  0.20,
               'viseme_kk':  0.16,
               'viseme_CH':  0.18,
               'viseme_SS':  0.12,
               'viseme_nn':  0.15,
               'viseme_RR':  0.15,
               'viseme_aa':  0.28,
               'viseme_E':   0.04,
               'viseme_I':   0.03,
               'viseme_O':   0.22,
               'viseme_U':   0.20,
             };

            function initIdlePose() {
              const idleMap = [
                ['RightArm_039', IDLE_POSE.rightUpper, 'rightUpper'],
                ['RightForeArm_040', IDLE_POSE.rightFore, 'rightFore'],
                ['RightHand_043', IDLE_POSE.rightHand, 'rightHand'],
                ['LeftArm_013', IDLE_POSE.leftUpper, 'leftUpper'],
                ['LeftForeArm_014', IDLE_POSE.leftFore, 'leftFore'],
                ['LeftHand_017', IDLE_POSE.leftHand, 'leftHand'],
              ];
              
              idleMap.forEach(([boneName, euler, key]) => {
                const bone = model.getObjectByName(boneName);
                if (bone && restPose[boneName]) {
                  const q = restPose[boneName].clone();
                  const g = new THREE.Quaternion().setFromEuler(new THREE.Euler(...euler));
                  idlePoseQuats[boneName] = q.multiply(g);
                  
                  if (key === 'rightUpper') rightUpperArm = bone;
                  if (key === 'leftUpper') leftUpperArm = bone;
                  if (key === 'rightFore') rightForeArm = bone;
                  if (key === 'leftFore') leftForeArm = bone;
                  if (key === 'rightHand') rightHand = bone;
                  if (key === 'leftHand') leftHand = bone;
                }
              });
            }

            function updateIdleGesture() {
              const it = Date.now() / 1000;
              // Subtle breathing/sway
              const armSwayR = Math.sin(it * 0.6) * 0.015;
              const armSwayL = Math.sin(it * 0.6 + 0.5) * 0.015;

              [rightUpperArm, leftUpperArm, rightForeArm, leftForeArm, rightHand, leftHand].forEach(bone => {
                if (bone && idlePoseQuats[bone.name]) {
                  bone.quaternion.slerp(idlePoseQuats[bone.name], 0.05);
                }
              });
              
              // Head idle
              if (headBone && restPose[headBone.name]) {
                const breathX = Math.sin(it * 0.8) * 0.012;
                const hq = restPose[headBone.name].clone();
                const hg = new THREE.Quaternion().setFromEuler(new THREE.Euler(breathX, 0, 0));
                headBone.quaternion.slerp(hq.multiply(hg), 0.06);
              }
            }

            window.updateViseme = function(viseme, intensity) {
                if (!OCULUS_VISEMES.includes(viseme)) {
                  console.log('⚠️ Unknown viseme:', viseme);
                  return;
                }
                
                 const maxWeight = VISEME_MAX[viseme] || 0.5;
                 const adjustedIntensity = intensity * maxWeight;
                 
                 // Reset other target weights
                 OCULUS_VISEMES.forEach(v => { targetWeights[v] = 0; });
                 
                 // NO idle smile - remove all idle morph application
                 
                 // Apply jaw damping to viseme_aa (REDUCED: was 0.55, now allows fuller opening)
                 let finalIntensity = adjustedIntensity;
                 if (viseme === 'viseme_aa') {
                     finalIntensity *= 0.75;  // Increased from 0.55 to allow better mouth opening
                 }
                 
                 targetWeights[viseme] = finalIntensity;
                
                // Prevent conflicting vowel visemes
                const vowelVisemes = [
                  'viseme_aa',
                  'viseme_E',
                  'viseme_I',
                  'viseme_O',
                  'viseme_U'
                ];

                let strongest = null;
                let strongestWeight = 0;

                // Find strongest vowel
                vowelVisemes.forEach(v => {
                  if (targetWeights[v] > strongestWeight) {
                    strongestWeight = targetWeights[v];
                    strongest = v;
                  }
                });

                // Suppress weaker vowels
                vowelVisemes.forEach(v => {
                  if (v !== strongest) {
                    targetWeights[v] *= 0.15;
                  }
                });
                
                // Log significant viseme changes
                if (intensity > 0.1) {
                  console.log('🎤 Viseme update:', viseme, 'intensity:', intensity.toFixed(2), 'adjusted:', adjustedIntensity.toFixed(2));
                }
            };

             function applySmoothedWeights() {
               for (const name of OCULUS_VISEMES) {
                 currentWeights[name] += (targetWeights[name] - currentWeights[name]) * LERP_SPEED;
                 if (currentWeights[name] < 0.001) currentWeights[name] = 0;
               }

               // Prevent smile stretching during jaw open
               const jawOpen = currentWeights['viseme_aa'] || 0;

               if (jawOpen > 0.15) {
                 currentWeights['viseme_I'] *= 0.2;
                 currentWeights['viseme_E'] *= 0.3;
               }

               // Upper lip anti-stretch correction
               const aa = currentWeights['viseme_aa'] || 0;
               const O  = currentWeights['viseme_O'] || 0;
               const U  = currentWeights['viseme_U'] || 0;

               const openAmount = aa + (O * 0.6) + (U * 0.4);

               // suppress smile/stretch morphs LESS aggressively with dynamic suppression
               if (openAmount > 0.08) {
                   // Scale suppression based on opening degree (less suppression for moderate opens)
                   const suppressScale = Math.min(1.0, openAmount / 0.25);  // Full suppression only when very open
                   currentWeights['viseme_I'] *= Math.max(0.2, 1.0 - suppressScale * 0.8);
                   currentWeights['viseme_E'] *= Math.max(0.25, 1.0 - suppressScale * 0.75);
                   currentWeights['viseme_SS'] *= 0.4;
                   currentWeights['viseme_CH'] *= 0.5;
               }

               // Stabilize lips during speech
               if (openAmount > 0.10) {
                   currentWeights['viseme_PP'] += 0.03;
               }

               // Relax hard clamps to allow better mouth opening - use calibrated values from VISEME_MAX
               currentWeights['viseme_aa'] = Math.min(currentWeights['viseme_aa'], 0.28);

               // Add morph influence easing clamp (raised from 0.18 to 0.25 to allow fuller shapes)
               for (const name of OCULUS_VISEMES) {
                 currentWeights[name] = Math.max(0, Math.min(currentWeights[name], 0.25));
               }

               // Teeth visibility logic based on mouth openness (INCREASED opacity)
               const mouthOpen = (currentWeights['viseme_aa'] || 0) +
                                  (currentWeights['viseme_O'] || 0) +
                                  (currentWeights['viseme_E'] || 0) +
                                  (currentWeights['viseme_DD'] || 0);
               
               const teethOpacity = Math.min(0.65, mouthOpen * 2.0);  // Increased from 0.45 with better scaling


              // Apply teeth properties to cached meshes
              for (const mesh of teethMeshes) {
                const mats = Array.isArray(mesh.material) ? mesh.material : [mesh.material];
                mats.forEach(m => { 
                    m.opacity = teethOpacity;
                    m.transparent = teethOpacity < 0.9;
                    m.depthWrite = true;
                });
              }

              // Apply morph targets to facial meshes
              let appliedCount = 0;
              if (morphMeshes.length === 0) {
                // Log warning once every 5 seconds
                const now = Date.now();
                if (!window.lastMorphWarning || now - window.lastMorphWarning > 5000) {
                  console.warn('⚠️ No morph meshes found - lip-sync will not work!');
                  window.lastMorphWarning = now;
                }
              }
              
              for (const mesh of morphMeshes) {
                if (!mesh.morphTargetInfluences || !mesh.morphTargetDictionary) {
                  console.warn('⚠️ Mesh missing morph data:', mesh.name);
                  continue;
                }
                
                for (const name of OCULUS_VISEMES) {
                  const idx = mesh.morphTargetDictionary[name];
                  if (idx !== undefined) {
                    mesh.morphTargetInfluences[idx] = currentWeights[name];
                    if (currentWeights[name] > 0.1) {
                      appliedCount++;
                    }
                  }
                }
              }
              
              // Log active morph targets periodically
              if (appliedCount > 0 && Math.random() < 0.01) {
                const activeVisemes = OCULUS_VISEMES
                  .filter(v => currentWeights[v] > 0.1)
                  .map(v => v + ':' + currentWeights[v].toFixed(2));
                console.log('👄 Active visemes:', activeVisemes.join(', '));
              }

              // Head tilt coupling - make it extremely subtle to avoid "bobbing" look
              if (headBone && restPose[headBone.name]) {
                const jawInfluence = (currentWeights['viseme_aa'] || 0) * 0.6 +
                                     (currentWeights['viseme_O'] || 0) * 0.4 +
                                     (currentWeights['viseme_E'] || 0) * 0.2;
                if (jawInfluence > 0.02) {
                  const tiltBack = jawInfluence * 0.06; 
                  const jq = new THREE.Quaternion().setFromEuler(new THREE.Euler(-tiltBack, 0, 0));
                  const targetQuat = restPose[headBone.name].clone().multiply(jq);
                  headBone.quaternion.slerp(targetQuat, 0.1);
                } else {
                   headBone.quaternion.slerp(restPose[headBone.name], 0.08);
                }
              }
            }

            const loader = new GLTFLoader();
            loader.load('$modelUrl', (gltf) => {
                model = gltf.scene;
                scene.add(model);

                model.traverse((node) => {
                  if (node.isMesh) {
                    node.frustumCulled = false;
                    
                    // Face/Head material fix - AvatarHead contains the lips and facial morphs
                    if (node.name === 'AvatarHead' || node.name === 'Object_9') {
                      console.log('✓ Found face mesh:', node.name);
                      if (Array.isArray(node.material)) {
                        node.material.forEach(m => { m.side = THREE.FrontSide; });
                      } else {
                        node.material.side = THREE.FrontSide;
                      }
                      // AvatarHead should have morph targets for visemes
                      if (node.morphTargetDictionary) {
                        console.log('✓ Face mesh has morph targets:', Object.keys(node.morphTargetDictionary));
                        morphMeshes.push(node);
                      }
                    }

                    // Upper teeth material fix
                    if (node.name === 'AvatarTeethUpper' || node.name === 'Object_14') {
                      console.log('✓ Found upper teeth:', node.name);
                      const mats = Array.isArray(node.material) ? node.material : [node.material];
                      mats.forEach(m => {
                        m.side = THREE.DoubleSide;
                        m.transparent = true;
                        m.opacity = 0;
                        m.depthWrite = true;
                      });
                      node.renderOrder = 30;
                      node.position.z += 0.02;
                      teethMeshes.push(node);
                    }

                    // Lower teeth material fix
                    if (node.name === 'AvatarTeethLower' || node.name === 'Object_15') {
                      console.log('✓ Found lower teeth:', node.name);
                      const mats = Array.isArray(node.material) ? node.material : [node.material];
                      mats.forEach(m => {
                        m.side = THREE.DoubleSide;
                        m.transparent = true;
                        m.opacity = 0;
                        m.depthWrite = true;
                      });
                      node.renderOrder = 30;
                      node.position.z += 0.02;
                      teethMeshes.push(node);
                    }

                    // Catch any other meshes with morph targets
                    if (node.morphTargetDictionary && !morphMeshes.includes(node)) {
                      console.log('✓ Found mesh with morph targets:', node.name, Object.keys(node.morphTargetDictionary));
                      morphMeshes.push(node);
                    }
                  }
                  
                  if (node.isBone) {
                    // Look for Head_08 bone specifically
                    if (node.name === 'Head_08' || node.name.toLowerCase().includes('head')) {
                      headBone = node;
                      console.log('✓ Found head bone:', node.name);
                    }
                    if (node.name === 'Neck2_07' || node.name.toLowerCase().includes('neck')) {
                      neckBone = node;
                      console.log('✓ Found neck bone:', node.name);
                    }
                    restPose[node.name] = node.quaternion.clone();
                  }
                });
                
                initIdlePose();

                // Diagnostic summary
                console.log('═══════════════════════════════════════');
                console.log('📊 MODEL LOAD SUMMARY');
                console.log('═══════════════════════════════════════');
                console.log('✓ Morph meshes found:', morphMeshes.length);
                morphMeshes.forEach(m => {
                  console.log('  - ' + m.name + ' has ' + Object.keys(m.morphTargetDictionary || {}).length + ' morph targets');
                  if (m.morphTargetDictionary) {
                    console.log('    Visemes:', Object.keys(m.morphTargetDictionary).filter(k => k.includes('viseme')).join(', '));
                  }
                });
                console.log('✓ Teeth meshes found:', teethMeshes.length);
                teethMeshes.forEach(m => console.log('  - ' + m.name));
                console.log('✓ Head bone:', headBone ? headBone.name : 'NOT FOUND');
                console.log('✓ Neck bone:', neckBone ? neckBone.name : 'NOT FOUND');
                console.log('═══════════════════════════════════════');

                // Camera framing based on provided configuration
                const target = new THREE.Vector3(0.05, 2.75, 0.12);
                const yaw = THREE.MathUtils.degToRad(12.54);
                const pitch = THREE.MathUtils.degToRad(88.48);
                const radius = 2.0; // Distance adjusted to show head down to chest

                // Convert spherical (yaw/pitch) to Cartesian coordinates
                const camX = target.x + radius * Math.sin(pitch) * Math.sin(yaw);
                const camY = target.y + radius * Math.cos(pitch);
                const camZ = target.z + radius * Math.sin(pitch) * Math.cos(yaw);
                
                camera.position.set(camX, camY, camZ);
                camera.lookAt(target);
                camera.fov = 40; // Balanced FOV for a natural chest-up portrait
                camera.updateProjectionMatrix();

                // Animation
                if (gltf.animations.length > 0) {
                  mixer = new THREE.AnimationMixer(model);
                  // Filter out head/neck/arm tracks if we wanted custom gestures later
                  const action = mixer.clipAction(gltf.animations[0]);
                  action.play();
                }
                
                console.log('Three.js Model Loaded');
            });

            function animate() {
              requestAnimationFrame(animate);
              const dt = clock.getDelta();
              if (mixer) mixer.update(dt);
              applySmoothedWeights();
              updateIdleGesture();
              renderer.render(scene, camera);
            }
            animate();

            window.addEventListener('resize', () => {
              camera.aspect = window.innerWidth / window.innerHeight;
              camera.updateProjectionMatrix();
              renderer.setSize(window.innerWidth, window.innerHeight);
            });
          </script>
        </body>
        </html>
    """.trimIndent()

    // AndroidView to host the WebView
    AndroidView(
        factory = {
            WebView(context).apply {
                webViewInstance = this
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Set up WebViewAssetLoader to serve local assets via https://
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    allowFileAccessFromFileURLs = true
                    allowUniversalAccessFromFileURLs = true
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE

                    // Disable built-in zoom controls
                    builtInZoomControls = false
                    displayZoomControls = false
                    setSupportZoom(false)
                }

                // Set WebChromeClient to see console logs in logcat
                webChromeClient = android.webkit.WebChromeClient()

                // Set WebViewClient to handle navigation and intercept asset requests
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        android.util.Log.d("Model3DViewer", "Page finished loading: $url")
                        // Ensure it's not hidden
                        view?.visibility = android.view.View.VISIBLE
                        // LIPSYNC FIX 2: Mark WebView as ready to receive viseme updates
                        webViewReady = true
                        android.util.Log.d("Model3DViewer", "✓ WebView ready for viseme updates")
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: android.webkit.WebResourceRequest?,
                        error: android.webkit.WebResourceError?
                    ) {
                        android.util.Log.e("Model3DViewer", "WebView error: ${error?.description}")
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: android.webkit.WebResourceRequest?
                    ): android.webkit.WebResourceResponse? {
                        // Use AssetLoader to handle the request if it matches the virtual domain
                        return request?.url?.let { assetLoader.shouldInterceptRequest(it) }
                    }
                }

                // Set a transparent background
                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                // Enable remote debugging (Chrome DevTools)
                WebView.setWebContentsDebuggingEnabled(true)

                // Load the HTML content with virtual https base URL
                loadDataWithBaseURL(
                    "https://appassets.androidplatform.net/assets/",
                    htmlContent,
                    "text/html",
                    "utf-8",
                    null
                )

                android.util.Log.d("Model3DViewer", "WebView initialized with model: $modelPath")
            }
        },
        update = { webView ->
            // If the model URL changes, we should reload it
            // For now, we mainly use the update block to keep the instance updated
            webViewInstance = webView
        },
        modifier = modifier
    )
}

