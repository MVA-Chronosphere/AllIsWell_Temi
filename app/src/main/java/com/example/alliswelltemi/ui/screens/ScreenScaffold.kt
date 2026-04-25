package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.ui.components.*
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Base screen scaffold for Temi hospital screens
 * Provides consistent header and back navigation in Elevated Clinical style
 */
@Composable
fun TemiScreenScaffold(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showVoiceFooter: Boolean = false,
    content: @Composable (Modifier) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HospitalColors.Background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .align(Alignment.TopStart)
                .widthIn(max = 1100.dp)
        ) {
            GlobalStatusBar()

            // Header with back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(64.dp)
                        .background(HospitalColors.Indigo.copy(alpha = 0.1f), CircleShape)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = HospitalColors.Indigo,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = title.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = HospitalColors.DeepSlate,
                    textAlign = TextAlign.Center
                )
            }

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp)
                    .padding(bottom = if (showVoiceFooter) 140.dp else 40.dp)
            ) {
                content(Modifier.fillMaxSize())
            }
        }

        if (showVoiceFooter) {
            VoiceFooterOverlay(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
    }
}

/**
 * Navigation Screen (Directions) - Legacy placeholder removed. 
 * Use the standalone NavigationScreen.kt instead.
 */

/**
 * Doctors & Departments Screen wrapper
 */
@Composable
fun DoctorsScreen(
    robot: Robot?,
    viewModel: DoctorsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    com.example.alliswelltemi.ui.screens.DoctorsScreen(
        robot = robot,
        viewModel = viewModel,
        onBackPress = onBackClick,
        onSelectDoctor = { doctor ->
            // Navigation command: "take me to dr{name}"
            val location = if (doctor.cabin.isNotBlank()) doctor.cabin else "dr ${doctor.name.lowercase()}"
            robot?.goTo(location)
            robot?.speak(TtsRequest.create("Sure, taking you to Dr. ${doctor.name}", false))
        }
    )
}

/**
 * Appointment Booking Screen
 */
@Composable
fun AppointmentScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TemiScreenScaffold(
        title = "Book an Appointment",
        onBackClick = onBackClick,
        modifier = modifier
    ) { contentMod ->
        Column(
            modifier = contentMod,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Book your appointment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // TODO: Add appointment booking form here
            Text(
                text = "Coming soon...",
                fontSize = 16.sp,
                color = Color(0xFFA0A0A0)
            )
        }
    }
}

/**
 * Hospital Information Screen
 */
@Composable
fun HospitalInfoScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TemiScreenScaffold(
        title = "Hospital Info",
        onBackClick = onBackClick,
        modifier = modifier
    ) { contentMod ->
        Column(
            modifier = contentMod,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = "ABOUT ALL IS WELL",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                InfoSection(
                    title = "Clinical Services",
                    content = listOf(
                        "Emergency Care (24/7)",
                        "Advanced Cardiology",
                        "Neurology & Stroke Center",
                        "Orthopedic Surgery",
                        "Imaging & Diagnostics"
                    ),
                    modifier = Modifier.weight(1f)
                )

                InfoSection(
                    title = "Facility Contacts",
                    content = listOf(
                        "Emergency: 999",
                        "General Line: +91-1234-567890",
                        "Appointment: +91-1234-567891",
                        "Pharmacy: Ext 402"
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Info section component for hospital info
 */
@Composable
fun InfoSection(
    title: String,
    content: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title.toUpperCase(Locale.current),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = HospitalColors.Indigo
                )
            )

            content.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(HospitalColors.SkyBlue, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = HospitalColors.DeepSlate
                        )
                    )
                }
            }
        }
    }
}
