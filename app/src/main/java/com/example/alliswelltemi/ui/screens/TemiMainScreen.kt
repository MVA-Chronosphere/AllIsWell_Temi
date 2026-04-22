package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.intl.Locale
import com.example.alliswelltemi.R
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Main Hospital Assistant Screen for Temi Robot (13.3-inch display)
 * Optimized for 1920x1080 landscape resolution.
 * Matches the requested UI design exactly.
 */
@Composable
fun TemiMainScreen(
    modifier: Modifier = Modifier,
    robot: Robot? = null,
    isThinking: Boolean = false,
    isConversationActive: Boolean = false,
    onNavigate: (String) -> Unit = {}
) {
    var currentLanguage by remember { mutableStateOf("en") }
    val darkBg = colorResource(id = R.color.dark_bg)
    
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()) }
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(horizontal = 64.dp, vertical = 40.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. HEADER (Hospital Logo, NABH Badge, Date/Time, Language Toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Hospital Title + NABH Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (currentLanguage == "en")
                            stringResource(id = R.string.hospital_title)
                        else
                            stringResource(id = R.string.hospital_title_hi),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    // NABH Accreditation Badge
                    Surface(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF00D9FF).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (currentLanguage == "en")
                                stringResource(id = R.string.nabh_accredited)
                            else
                                stringResource(id = R.string.nabh_accredited_hi),
                            color = Color(0xFF00D9FF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Right: Date/Time and Language Toggle
                Row(
                    modifier = Modifier.clickable {
                        currentLanguage = if (currentLanguage == "en") "hi" else "en"
                        onNavigate("language")
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date and Time
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = dateFormatter.format(currentTime),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = timeFormatter.format(currentTime),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == "en")
                            stringResource(id = R.string.english)
                        else
                            stringResource(id = R.string.hindi),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // 2. HERO SECTION (Greeting Title + Subtitle + Avatar)
            // When answering questions, hide greeting text and show animated eyes
            if (!isThinking && !isConversationActive) {
                // NORMAL STATE: Show greeting text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentLanguage == "en") "Namaste!" else "नमस्ते!",
                            color = Color.White,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (currentLanguage == "en")
                                "How can I help you today?"
                            else
                                "आज मैं आपकी कैसे मदद कर सकता हूँ?",
                            color = Color.LightGray.copy(alpha = 0.8f),
                            fontSize = 24.sp,
                            lineHeight = 34.sp
                        )
                    }

                    // Robot Avatar - Normal Smiling Eyes
                    TemiAvatarSmiling()
                }
            } else {
                // ANSWERING STATE: Show animated listening eyes, hide greeting text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Show animated Temi eyes while answering
                    TemiAvatarListening(isListening = isThinking || isConversationActive)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. MAIN MENU TITLE - Hide menu when answering
            if (!isThinking && !isConversationActive) {
                Text(
                    text = if (currentLanguage == "en") "Main Menu" else "मुख्य मेनू",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. MENU GRID (2 columns x 2 rows)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // First Row: Find & Navigate | Doctors & Departments
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MenuCard(
                        title = if (currentLanguage == "en")
                            stringResource(id = R.string.find_navigate)
                        else
                            stringResource(id = R.string.find_navigate_hi),
                        subtitle = if (currentLanguage == "en")
                            stringResource(id = R.string.find_navigate_subtitle)
                        else
                            stringResource(id = R.string.find_navigate_subtitle_hi),
                        icon = Icons.Default.LocationOn,
                        startColor = colorResource(id = R.color.gradient_blue_start),
                        endColor = colorResource(id = R.color.gradient_blue_end),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val speech = if (currentLanguage == "en")
                                "I'll take you there. Where would you like to go?"
                            else
                                "मैं आपको वहां ले जाऊंगा। आप कहां जाना चाहते हैं?"
                            robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                            onNavigate("navigation")
                        }
                    )
                    MenuCard(
                        title = if (currentLanguage == "en")
                            stringResource(id = R.string.doctors_departments)
                        else
                            stringResource(id = R.string.doctors_departments_hi),
                        subtitle = if (currentLanguage == "en")
                            stringResource(id = R.string.doctors_departments_subtitle)
                        else
                            stringResource(id = R.string.doctors_departments_subtitle_hi),
                        icon = Icons.Default.AccountCircle,
                        startColor = colorResource(id = R.color.gradient_teal_start),
                        endColor = colorResource(id = R.color.gradient_teal_end),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val speech = if (currentLanguage == "en")
                                "Finding specialist doctors for you"
                            else
                                "मैं आपके लिए विशेषज्ञ डॉक्टर खोज रहा हूँ"
                            robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                            onNavigate("doctors")
                        }
                    )
                }
                
                // Second Row: Book Appointment | Share Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MenuCard(
                        title = if (currentLanguage == "en")
                            stringResource(id = R.string.book_appointment)
                        else
                            stringResource(id = R.string.book_appointment_hi),
                        subtitle = if (currentLanguage == "en")
                            stringResource(id = R.string.book_appointment_subtitle)
                        else
                            stringResource(id = R.string.book_appointment_subtitle_hi),
                        icon = Icons.Default.DateRange,
                        startColor = colorResource(id = R.color.gradient_purple_start),
                        endColor = colorResource(id = R.color.gradient_purple_end),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val speech = if (currentLanguage == "en")
                                "Let's book an appointment"
                            else
                                "आइए एक अपॉइंटमेंट बुक करते हैं"
                            robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                            onNavigate("appointment")
                        }
                    )
                    MenuCard(
                        title = if (currentLanguage == "en")
                            stringResource(id = R.string.feedback)
                        else
                            stringResource(id = R.string.feedback_hi),
                        subtitle = if (currentLanguage == "en")
                            stringResource(id = R.string.feedback_subtitle)
                        else
                            stringResource(id = R.string.feedback_subtitle_hi),
                        icon = Icons.Default.RateReview,
                        startColor = colorResource(id = R.color.gradient_orange_start),
                        endColor = colorResource(id = R.color.gradient_orange_end),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val speech = if (currentLanguage == "en")
                                "Thank you for your feedback. Please share your thoughts"
                            else
                                "आपकी प्रतिक्रिया के लिए धन्यवाद। कृपया अपने विचार साझा करें"
                            robot?.speak(TtsRequest.create(speech = speech, isShowOnConversationLayer = false))
                            onNavigate("feedback")
                        }
                    )
                }
            }
            }  // Close the if (!isThinking && !isConversationActive) block

            Spacer(modifier = Modifier.weight(1f))

            // 5. BOTTOM VOICE HINT BAR
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp),
                shape = RoundedCornerShape(42.dp),
                color = Color.White.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = if (isThinking) {
                                if (currentLanguage == "en") "Thinking..." else "सोच रहा हूँ..."
                            } else {
                                if (currentLanguage == "en")
                                    stringResource(id = R.string.voice_hint)
                                else
                                    stringResource(id = R.string.voice_hint_hi)
                            },
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp
                        )
                    }

                    // Microphone Action Button
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable {
                                // NEVER call askQuestion during active GPT conversation
                                if (!isThinking && !isConversationActive) {
                                    android.util.Log.d("TemiMainScreen", "Mic button clicked, calling askQuestion")
                                    robot?.askQuestion(
                                        if (currentLanguage == "en")
                                            "How can I help you?"
                                        else
                                            "मैं आपकी कैसे मदद कर सकता हूँ?"
                                    )
                                } else {
                                    android.util.Log.d("GPT_FIX", "BLOCKED askQuestion in TemiMainScreen: conversation active or thinking")
                                }
                            },
                        shape = CircleShape,
                        color = if (isThinking || isConversationActive) Color.Gray else Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isThinking || isConversationActive) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = darkBg,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Gradient Menu Card Composable
 */
@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
            )
        }
    }
}

/**
 * Temi Avatar with Smiling Eyes (Normal State)
 */
@Composable
fun TemiAvatarSmiling() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(start = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Blue glow effect
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00D9FF).copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        )

        // Head shape
        Surface(
            modifier = Modifier.size(160.dp, 120.dp),
            color = Color.Black,
            shape = RoundedCornerShape(36.dp),
            border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row {
                    // Smiling Eye Left
                    Box(
                        modifier = Modifier
                            .size(32.dp, 16.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFF00D9FF))
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    // Smiling Eye Right
                    Box(
                        modifier = Modifier
                            .size(32.dp, 16.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFF00D9FF))
                    )
                }
            }
        }
    }
}

/**
 * Temi Avatar with Listening/Animated Eyes (Answering State)
 */
@Composable
fun TemiAvatarListening(isListening: Boolean = true) {
    var eyeScale by remember { mutableStateOf(1f) }

    // Blinking and pulsing animation
    LaunchedEffect(isListening) {
        if (isListening) {
            while (true) {
                // Blink animation
                kotlinx.coroutines.delay(100)
                eyeScale = 0.7f
                kotlinx.coroutines.delay(150)
                eyeScale = 1f

                // Keep eyes on for a bit
                kotlinx.coroutines.delay(1500)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(start = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Blue glow effect - more intense when listening
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D9FF).copy(alpha = if (isListening) 0.4f else 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Head shape
        Surface(
            modifier = Modifier.size(160.dp, 120.dp),
            color = Color.Black,
            shape = RoundedCornerShape(36.dp),
            border = BorderStroke(2.dp, Color(0xFF00D9FF).copy(alpha = 0.6f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Listening Eye Left - Full circle, animated
                    Box(
                        modifier = Modifier
                            .size((32 * eyeScale).dp, (32 * eyeScale).dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00D9FF))
                    )

                    // Listening Eye Right - Full circle, animated
                    Box(
                        modifier = Modifier
                            .size((32 * eyeScale).dp, (32 * eyeScale).dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00D9FF))
                    )
                }
            }
        }

        // Listening indicator text below
        if (isListening) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Listening...",
                    color = Color(0xFF00D9FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

