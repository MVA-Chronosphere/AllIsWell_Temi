package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

/**
 * Base screen scaffold for Temi hospital screens
 * Provides consistent header and back navigation
 */
@Composable
fun TemiScreenScaffold(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1220),
                        Color(0xFF1A2332)
                    )
                )
            )
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A2332),
                            Color(0xFF0B1220)
                        )
                    )
                )
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF00D9FF),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Spacer to balance the layout
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Divider
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color(0x2200D9FF)
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}

/**
 * Navigation Screen (Directions)
 */
@Composable
fun NavigationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TemiScreenScaffold(
        title = "Navigate to Location",
        onBackClick = onBackClick,
        modifier = modifier
    ) { contentMod ->
        Column(
            modifier = contentMod,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select a destination",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // TODO: Add location list here
            Text(
                text = "Coming soon...",
                fontSize = 16.sp,
                color = Color(0xFFA0A0A0)
            )
        }
    }
}

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
        title = "Hospital Information",
        onBackClick = onBackClick,
        modifier = modifier
    ) { contentMod ->
        Column(
            modifier = contentMod,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "About All Is Well Hospital",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoSection(
                title = "Services",
                content = listOf(
                    "Emergency Care",
                    "24/7 Pharmacy",
                    "Imaging & Diagnostics",
                    "Cardiac Care",
                    "Neurology",
                    "Orthopedic Surgery"
                )
            )

            InfoSection(
                title = "Contact",
                content = listOf(
                    "Emergency: 999",
                    "General Line: +91-1234-567890",
                    "Appointment: +91-1234-567891"
                )
            )
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1A2332).copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00D9FF)
        )

        content.forEach { item ->
            Text(
                text = "• $item",
                fontSize = 14.sp,
                color = Color(0xFFC0C0C0),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
