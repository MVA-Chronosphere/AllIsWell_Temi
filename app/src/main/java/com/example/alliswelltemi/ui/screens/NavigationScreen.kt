package com.example.alliswelltemi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.ui.components.*
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.text.intl.Locale
import com.example.alliswelltemi.data.Location
import com.example.alliswelltemi.ui.theme.HospitalColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.AnnotatedString
import com.example.alliswelltemi.viewmodel.NavigationViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.SttLanguage
import kotlinx.coroutines.delay

/**
 * Tab enum for content switching
 */
enum class NavigationTab {
    MOST_USED,
    ALL_LOCATIONS
}

/**
 * Enhanced Navigation Screen with Smooth Animations
 * Features entry animations, interactive feedback, and navigation state overlay
 */
@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier,
    robot: Robot? = null,
    viewModel: NavigationViewModel = remember { NavigationViewModel() },
    isThinking: Boolean = false,
    isConversationActive: Boolean = false,
    onBackPress: () -> Unit = {},
    onNavigationComplete: (Location) -> Unit = {}
) {
    val filteredPopularLocations by viewModel.filteredPopularLocations
    val filteredAllLocations by viewModel.filteredAllLocations
    val isLoading by viewModel.isLoading
    val selectedLocation by viewModel.selectedLocation

    // Tab state for content switching
    var selectedTab by remember { mutableStateOf(NavigationTab.MOST_USED) }
    var currentLanguage by remember { mutableStateOf("en") } // Replace with actual language state if lifted

    // Load locations from Temi's saved map when screen is first composed
    LaunchedEffect(robot) {
        if (robot != null) {
            viewModel.loadLocationsFromMap(robot)
        }
    }

    // Handle navigation when a location is selected
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            try {
                robot?.speak(
                    TtsRequest.create(
                        speech = "Taking you to ${location.name}",
                        isShowOnConversationLayer = false
                    )
                )
                viewModel.setLoading(true)
                delay(2000)
                robot?.goTo(location.name)
                onNavigationComplete(location)
            } catch (e: Exception) {
                android.util.Log.e("NavigationScreen", "Navigation error", e)
                robot?.speak(
                    TtsRequest.create(
                        speech = "Unable to navigate. Please try again.",
                        isShowOnConversationLayer = false
                    )
                )
            } finally {
                viewModel.setLoading(false)
                viewModel.clearSelection()
            }
        }
    }

    TemiScreenScaffold(
        title = "Navigation",
        onBackClick = onBackPress,
        modifier = modifier
    ) { contentMod ->
        Column(
            modifier = contentMod,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Toggle Segmented Control (Active: Royal Blue)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SegmentedToggleControl(
                    modifier = Modifier.width(440.dp),
                    selectedIndex = if (selectedTab == NavigationTab.MOST_USED) 0 else 1,
                    onTabChange = { 
                        selectedTab = if (it == 0) NavigationTab.MOST_USED else NavigationTab.ALL_LOCATIONS 
                    },
                    options = listOf("MOSTLY USED", "ALL LOCATIONS")
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
                when (tab) {
                    NavigationTab.MOST_USED -> {
                        MostlyUsedGrid(
                            locations = filteredPopularLocations,
                            selectedLocation = selectedLocation,
                            onLocationClick = { viewModel.selectLocation(it) }
                        )
                    }
                    NavigationTab.ALL_LOCATIONS -> {
                        NavigationGrid(
                            locations = filteredAllLocations,
                            selectedLocation = selectedLocation,
                            onLocationClick = { viewModel.selectLocation(it) }
                        )
                    }
                }
            }
        }

        // Navigation overlay animation
        if (isLoading) {
            NavigationOverlayAnimated(
                title = "Taking you to ${selectedLocation?.name ?: "Destination"}"
            )
        }
    }

    TemiNavBar(
        currentLanguage = currentLanguage,
        onLogoClick = { onBackPress() } // Go to home/main on logo click
    )
}


/**
 * Grid for "Mostly Used" locations - 3 cards per row
 */
@Composable
private fun MostlyUsedGrid(
    locations: List<Location>,
    selectedLocation: Location?,
    onLocationClick: (Location) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(locations) { _, location ->
            val isSelected = selectedLocation?.name == location.name
            
            MostlyUsedCard(
                location = location,
                isSelected = isSelected,
                onClick = { onLocationClick(location) }
            )
        }
    }
}

@Composable
private fun MostlyUsedCard(
    location: Location,
    isSelected: Boolean,
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
            .height(140.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isSelected) 20.dp else 8.dp,
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
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isSelected) Color.White.copy(alpha = 0.2f) else HospitalColors.DeepSlate.copy(alpha = 0.05f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getLucideIcon(location.id),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = location.name.toUpperCase(Locale.current),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        letterSpacing = 0.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "FLOOR ${location.name.length % 3}".toUpperCase(Locale.current),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else HospitalColors.DeepSlate.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Grid of location cards - Unified with Mostly Used design
 */
@Composable
private fun NavigationGrid(
    locations: List<Location>,
    selectedLocation: Location?,
    onLocationClick: (Location) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(locations) { _, location ->
            val isSelected = selectedLocation?.name == location.name
            LocationCard(
                location = location,
                isSelected = isSelected,
                onClick = { onLocationClick(location) }
            )
        }
    }
}

@Composable
private fun LocationCard(
    location: Location,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    MostlyUsedCard(
        location = location,
        isSelected = isSelected,
        onClick = onClick
    )
}

/**
 * Helper to map location IDs to Lucide-style Outlined Material Icons
 */
@Composable
private fun getLucideIcon(locationId: String): ImageVector {
    return when (locationId.lowercase()) {
        "pharmacy" -> Icons.Outlined.MedicalServices
        "reception" -> Icons.Outlined.Notifications
        "icu" -> Icons.Outlined.LocalHospital
        "laboratory" -> Icons.Outlined.Science
        "cardiology" -> Icons.Outlined.FavoriteBorder
        "neurology" -> Icons.Outlined.Psychology
        "orthopedics" -> Icons.Outlined.Healing
        "imaging" -> Icons.Outlined.PhotoCamera
        "emergency" -> Icons.Outlined.Emergency
        "general_ward" -> Icons.Outlined.KingBed
        "private_ward" -> Icons.Outlined.Hotel
        "cafeteria" -> Icons.Outlined.Coffee
        "washroom" -> Icons.Outlined.Wc
        "waiting_area" -> Icons.Outlined.EventSeat
        "exit" -> Icons.Outlined.Logout
        else -> Icons.Outlined.Place
    }
}
