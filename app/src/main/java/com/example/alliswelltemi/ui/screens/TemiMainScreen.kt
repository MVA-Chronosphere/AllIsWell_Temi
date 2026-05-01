package com.example.alliswelltemi.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.example.alliswelltemi.ui.components.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.R
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

/**
 * Main Hospital Assistant Screen for Temi Robot (13.3-inch display)
 * Optimized for 1920x1080 landscape resolution.
 * Premium Soft Purple Theme Applied
 */
@Composable
fun TemiMainScreen(
    modifier: Modifier = Modifier,
    robot: Robot? = null,
    onNavigate: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HospitalColors.Background)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // LEFT CONTENT COLUMN
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
            ) {
                var currentLanguage by remember { mutableStateOf("en") }
                TemiNavBar(
                    currentLanguage = currentLanguage,
                    onLogoClick = { onNavigate("main") }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // HERO SECTION
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "NAMASTE",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-2).sp,
                                color = HospitalColors.DeepSlate
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "I am Chronexa, your medical assistant. How can I help you?",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = HospitalColors.DeepSlate.copy(alpha = 0.7f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // NAVIGATION GRID
                    val gridItems = remember {
                        listOf(
                            MenuItem("navigation", "Find & \nNavigate", "RADIOLOGY, LAB, PHARMACY", HospitalColors.SkyBlue, Icons.Default.LocationOn),
                            MenuItem("doctors", "Doctors & \nDepartments", "124 SPECIALISTS ON DUTY", Color(0xFF7B61FF), Icons.Default.Groups),
                            MenuItem("appointment", "Book \nAppointment", "NEXT AVAILABLE: 2:30 PM", Color(0xFF7B61FF), Icons.Default.CalendarToday),
                            MenuItem("feedback", "Patient \nFeedback", "RATE YOUR EXPERIENCE", HospitalColors.SkyBlue, Icons.Default.ChatBubbleOutline)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            HeroMenuCard(gridItems[0], Modifier.weight(1f), { onNavigate("navigation") })
                            HeroMenuCard(gridItems[1], Modifier.weight(1f), { onNavigate("doctors") })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            HeroMenuCard(gridItems[2], Modifier.weight(1f), { onNavigate("appointment") })
                            HeroMenuCard(gridItems[3], Modifier.weight(1f), { onNavigate("feedback") })
                        }
                    }
                    Spacer(modifier = Modifier.height(140.dp)) // Extra space for voice footer
                }
            }

            // RIGHT SIDE - 3D ASSISTANT
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Model3DViewer(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating Voice Footer
        VoiceFooterOverlay(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            isListening = false,
            onMicClick = {}
        )
    }
}

data class MenuItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: Color,
    val icon: ImageVector
)

@Composable
fun HeroMenuCard(
    item: MenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(220.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            )
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = item.backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-1).sp,
                        lineHeight = 36.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = item.subtitle.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                )
            }

            // Bottom-right icon in circle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(48.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DotPatternOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val dotRadius = 2.dp.toPx()
        val spacing = 24.dp.toPx()
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            for (y in 0..size.height.toInt() step spacing.toInt()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = dotRadius,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }
    }
}

@Composable
fun VoiceFooter(
    modifier: Modifier = Modifier,
    isListening: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(110.dp)
            .shadow(
                elevation = 40.dp,
                shape = RoundedCornerShape(55.dp),
                spotColor = HospitalColors.RoyalBlue.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(55.dp),
        color = HospitalColors.PureWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Voice Command: \"Take me to the Cardiology Department\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = HospitalColors.DeepSlate.copy(alpha = 0.6f)
                )
            )

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .offset(x = 40.dp) // Pop out slightly to match "floating" feel if needed or just align
                    .background(HospitalColors.RoyalBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
    }
}

/**
 * Animated Menu Card with Press Animation + Ripple + Entry Animation
 * Fixed size (320dp x 110dp), gradient background, smooth interactions
 */
@Composable
fun AnimatedMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Press scale animation
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_press_scale"
    )

    // Entry animation (fade + slide in)
    val entryAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500, delayMillis = delayMillis, easing = EaseOutCubic),
        label = "card_entry_alpha"
    )

    val entryOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(500, delayMillis = delayMillis, easing = EaseOutCubic),
        label = "card_entry_offset"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = entryAlpha
                translationY = entryOffset * 50f
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            )
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE2D2CB),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = title,
                    color = Color(0xFFE2D2CB),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFFE2D2CB).copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFE2D2CB).copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp)
            )
        }
    }
}
