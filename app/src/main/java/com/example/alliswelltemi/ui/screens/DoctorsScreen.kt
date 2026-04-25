package com.example.alliswelltemi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.ui.components.*
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay

/**
 * Tab enum for Doctors content switching
 */
enum class DoctorsTab {
    BY_DOCTOR,
    BY_DEPARTMENT
}

@Composable
fun DoctorsScreen(
    robot: Robot?,
    viewModel: DoctorsViewModel,
    onBackPress: () -> Unit,
    onSelectDoctor: (Doctor) -> Unit,
    currentLanguage: String = "en"
) {
    val doctors by viewModel.doctors
    val isLoading by viewModel.isLoading
    val departments by viewModel.departments
    val selectedDept by viewModel.selectedDepartment
    val error by viewModel.error
    val selectedDoctorForNav by viewModel.selectedDoctorForNav
    val isNavigating by viewModel.isNavigating

    // Tab state for content switching
    var selectedTab by remember { mutableStateOf(DoctorsTab.BY_DOCTOR) }

    LaunchedEffect(selectedDoctorForNav) {
        selectedDoctorForNav?.let { doctor ->
            try {
                robot?.speak(
                    TtsRequest.create(
                        speech = "Taking you to ${doctor.name} at ${doctor.cabin}",
                        isShowOnConversationLayer = false
                    )
                )
                viewModel.setNavigating(true)
                delay(2000)
                robot?.goTo(doctor.cabin.lowercase())
                onSelectDoctor(doctor)
            } catch (e: Exception) {
                android.util.Log.e("DoctorsScreen", "Navigation error", e)
            } finally {
                viewModel.setNavigating(false)
                viewModel.selectDoctorForNavigation(null)
            }
        }
    }

    // --- Insert TemiNavBar at the top ---
    Column(modifier = Modifier.fillMaxSize()) {
        TemiNavBar(currentLanguage = currentLanguage)

        // Main content below the unified nav bar
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp), // No extra top padding needed
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Toggle Segmented Control
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SegmentedToggleControl(
                        modifier = Modifier.width(440.dp),
                        selectedIndex = if (selectedTab == DoctorsTab.BY_DOCTOR) 0 else 1,
                        onTabChange = {
                            selectedTab = if (it == 0) DoctorsTab.BY_DOCTOR else DoctorsTab.BY_DEPARTMENT
                        },
                        options = listOf("BY DOCTOR", "BY DEPARTMENT")
                    )
                }

                // Content switching
                Crossfade(
                    targetState = selectedTab,
                    animationSpec = tween(400, easing = EaseInOutCubic),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    label = "content_crossfade"
                ) { tab ->
                    when {
                        isLoading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = HospitalColors.RoyalBlue)
                            }
                        }
                        error != null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = error!!,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(onClick = { viewModel.retry() }) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                        doctors.isEmpty() -> {
                            // Empty state - no doctors loaded yet or API returned empty
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "No doctors available",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = HospitalColors.DeepSlate
                                    )
                                    Text(
                                        text = "Please check back later or try refreshing",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = HospitalColors.DeepSlate.copy(alpha = 0.7f)
                                    )
                                    Button(onClick = { viewModel.retry() }) {
                                        Text("Refresh")
                                    }
                                }
                            }
                        }
                        else -> {
                            when (tab) {
                                DoctorsTab.BY_DOCTOR -> {
                                    DoctorsGrid(
                                        doctors = viewModel.filteredDoctors,
                                        onDoctorClick = { doctor ->
                                            viewModel.selectDoctorForNavigation(doctor)
                                        }
                                    )
                                }
                                DoctorsTab.BY_DEPARTMENT -> {
                                    DepartmentsGrid(
                                        departments = departments,
                                        selectedDept = selectedDept,
                                        onDeptClick = { viewModel.filterByDepartment(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Navigation overlay
            if (isNavigating) {
                NavigationOverlayAnimated(
                    title = "Taking you to ${selectedDoctorForNav?.name ?: "Doctor"}"
                )
            }
        }
    }
}

@Composable
private fun DoctorsGrid(
    doctors: List<Doctor>,
    onDoctorClick: (Doctor) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(doctors) { _, doctor ->
            InfoCard(
                title = doctor.name,
                subtitle = doctor.specialization,
                imageUrl = doctor.profileImageUrl,
                icon = Icons.Outlined.Person,
                onClick = { onDoctorClick(doctor) }
            )
        }
    }
}

@Composable
private fun DepartmentsGrid(
    departments: List<String>,
    selectedDept: String?,
    onDeptClick: (String?) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Updated to 3 columns
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp)
    ) {
        // "All" option
        item {
            InfoCard(
                title = "  Departments",
                subtitle = "Comprehensive Healthcare for Everyone",
                icon = Icons.Outlined.Business,
                isSelected = selectedDept == null,
                onClick = { onDeptClick(null) }
            )
        }
        itemsIndexed(departments) { _, dept ->
            InfoCard(
                title = dept,
                subtitle = "Advanced Clinical Care & Specialized Treatment",
                icon = Icons.Outlined.Business,
                isSelected = selectedDept == dept,
                onClick = { onDeptClick(dept) }
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    imageUrl: String? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) HospitalColors.SkyBlue else HospitalColors.PureWhite,
        animationSpec = tween(300),
        label = "bg_color"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else HospitalColors.DeepSlate,
        animationSpec = tween(300),
        label = "text_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // Unified with Navigation card height
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isSelected) 20.dp else 8.dp, // Unified shadow
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp) // Unified padding
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp) // Unified icon box size
                    .background(
                        color = if (isSelected) Color.White.copy(alpha = 0.2f) else HospitalColors.DeepSlate.copy(alpha = 0.05f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null && imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(24.dp) // Unified icon size
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp)) // Unified spacer

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp, // Unified font size
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1, // Reduced to 1 line for consistency
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp, // Unified font size
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color.White else HospitalColors.DeepSlate.copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    ),
                    maxLines = 1, // Reduced to 1 line for consistency
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
