package com.example.alliswelltemi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.R
import com.example.alliswelltemi.data.Location
import com.example.alliswelltemi.viewmodel.NavigationViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

/**
 * Enhanced Navigation Screen with Smooth Animations
 * Features entry animations, interactive feedback, and navigation state overlay
 */
@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier,
    robot: Robot? = null,
    viewModel: NavigationViewModel = remember { NavigationViewModel() },
    onBackPress: () -> Unit = {},
    onNavigationComplete: (Location) -> Unit = {}
) {
    val darkBg = colorResource(id = R.color.dark_bg)

    val searchText by viewModel.searchText
    val isListening by viewModel.isListening
    val filteredPopularLocations by viewModel.filteredPopularLocations
    val filteredAllLocations by viewModel.filteredAllLocations
    val isLoading by viewModel.isLoading
    val selectedLocation by viewModel.selectedLocation

    // Screen entry animation state
    var isScreenVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isScreenVisible = true
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            robot?.speak(
                TtsRequest.create(
                    speech = "Taking you to ${location.name}",
                    isShowOnConversationLayer = false
                )
            )
            viewModel.setLoading(true)
            // Allow animation to play before navigation
            kotlinx.coroutines.delay(2000)
            try {
                robot?.goTo(location.name)
            } catch (e: Exception) {
                e.printStackTrace()
                robot?.speak(
                    TtsRequest.create(
                        speech = "Unable to navigate to ${location.name}. Please try again.",
                        isShowOnConversationLayer = false
                    )
                )
            }
            viewModel.setLoading(false)
            viewModel.clearSelection()
            onNavigationComplete(location)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(horizontal = 40.dp, vertical = 30.dp)
    ) {
        // Main content with entry animation
        AnimatedVisibility(
            visible = isScreenVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { 100 },
                animationSpec = tween(600, easing = EaseOutCubic)
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with back button animation
                HeaderRowAnimated(onBackPress = onBackPress)

                Spacer(modifier = Modifier.height(30.dp))

                // Title and subtitle with staggered animation
                TitleSectionAnimated()

                Spacer(modifier = Modifier.height(24.dp))

                // Search bar with enhanced interaction
                SearchBarAnimated(
                    searchText = searchText,
                    onSearchTextChanged = { viewModel.onSearchTextChanged(it) },
                    onClearSearch = { viewModel.clearSearch() },
                    onVoiceClick = {
                        viewModel.setListening(true)
                        robot?.askQuestion("Where would you like to go?")
                    },
                    isListening = isListening
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Most used locations with staggered animation
                if (filteredPopularLocations.isNotEmpty()) {
                    LocationSectionAnimated(
                        locations = filteredPopularLocations,
                        onLocationClick = { viewModel.selectLocation(it) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // All locations with animated list items
                AllLocationsSectionAnimated(
                    locations = filteredAllLocations,
                    onLocationClick = { viewModel.selectLocation(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Navigation overlay animation
        if (isLoading) {
            NavigationOverlayAnimated(selectedLocation)
        }
    }
}

/**
 * Header with back button animation
 */
@Composable
private fun HeaderRowAnimated(onBackPress: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "header_scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackPress
                ),
            color = Color.White.copy(alpha = 0.1f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = "Hospital Navigation",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.size(48.dp))
    }
}

/**
 * Title section with staggered animation
 */
@Composable
private fun TitleSectionAnimated() {
    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 100, easing = EaseOutCubic),
        label = "title_alpha"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 200, easing = EaseOutCubic),
        label = "subtitle_alpha"
    )

    Text(
        text = "Find & Navigate",
        color = Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.graphicsLayer { alpha = titleAlpha }
    )

    Text(
        text = "Search for any location in the hospital",
        color = Color.White.copy(alpha = 0.7f * subtitleAlpha),
        fontSize = 16.sp,
        modifier = Modifier
            .padding(top = 8.dp)
            .graphicsLayer { alpha = subtitleAlpha }
    )
}

/**
 * Enhanced search bar with animations
 */
@Composable
private fun SearchBarAnimated(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onVoiceClick: () -> Unit,
    isListening: Boolean
) {
    val darkBgSecondary = colorResource(id = R.color.dark_bg_secondary)

    // Glow animation
    val glowAlpha by animateFloatAsState(
        targetValue = if (isListening) 1f else 0.3f,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "search_glow"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFF00D9FF) else Color.White.copy(alpha = 0.1f),
        animationSpec = tween(300),
        label = "search_border"
    )

    val shadowElevation by animateFloatAsState(
        targetValue = if (isListening) 16f else 8f,
        animationSpec = tween(300),
        label = "search_shadow"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = shadowElevation.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF00D9FF).copy(alpha = glowAlpha * 0.3f)
            ),
        shape = RoundedCornerShape(28.dp),
        color = darkBgSecondary,
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Search location...",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Animated clear button
            AnimatedVisibility(
                visible = searchText.isNotEmpty(),
                enter = fadeIn(tween(200)) + scaleIn(tween(200)),
                exit = fadeOut(tween(200)) + scaleOut(tween(200))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClearSearch
                        )
                )
            }

            // Voice button with listening animation
            VoiceButtonAnimated(
                isListening = isListening,
                onVoiceClick = onVoiceClick
            )
        }
    }
}

/**
 * Animated voice button with pulsing effect when listening
 */
@Composable
private fun VoiceButtonAnimated(
    isListening: Boolean,
    onVoiceClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (isListening) 1f else 0f,
        targetValue = if (isListening) 1.1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_pulse_scale"
    )

    val voiceColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFF00D9FF) else Color(0xFF00D9FF).copy(alpha = 0.2f),
        animationSpec = tween(300),
        label = "voice_color"
    )

    Box(contentAlignment = Alignment.Center) {
        // Pulse background
        if (isListening) {
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .scale(pulseScale)
                    .clip(CircleShape),
                color = Color(0xFF00D9FF).copy(alpha = 0.1f),
                shape = CircleShape
            ) {}
        }

        Surface(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onVoiceClick
                ),
            color = voiceColor,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) Color.Black else Color(0xFF00D9FF),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Location section with staggered animation for grid cards
 */
@Composable
private fun LocationSectionAnimated(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit
) {
    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 300, easing = EaseOutCubic),
        label = "section_title_alpha"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Most Used",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.graphicsLayer { alpha = titleAlpha }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(locations) { index, location ->
                LocationGridCardAnimated(
                    location = location,
                    onClick = { onLocationClick(location) },
                    delayMillis = 400 + (index * 100)
                )
            }
        }
    }
}

/**
 * Animated grid card with scale and fade in
 */
@Composable
private fun LocationGridCardAnimated(
    location: Location,
    onClick: () -> Unit,
    delayMillis: Int = 0
) {
    var isPressed by remember { mutableStateOf(false) }

    val cardAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500, delayMillis = delayMillis, easing = EaseOutCubic),
        label = "grid_card_alpha"
    )

    val cardScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500, delayMillis = delayMillis, easing = EaseOutCubic),
        label = "grid_card_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF00D9FF).copy(alpha = 0.3f) else Color(0xFF1A2332),
        animationSpec = tween(200),
        label = "grid_card_bg"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "grid_press_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .graphicsLayer {
                alpha = cardAlpha
                scaleX = cardScale * pressScale
                scaleY = cardScale * pressScale
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = !isPressed
                    onClick()
                }
            ),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = Color(0xFF00D9FF).copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = location.icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = location.name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

/**
 * All locations section with animated list
 */
@Composable
private fun AllLocationsSectionAnimated(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit
) {
    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 400, easing = EaseOutCubic),
        label = "all_title_alpha"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "All Locations",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.graphicsLayer { alpha = titleAlpha }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(locations) { index, location ->
                LocationListItemAnimated(
                    location = location,
                    onClick = { onLocationClick(location) },
                    delayMillis = 450 + (index * 50)
                )
            }
        }
    }
}

/**
 * Animated list item with fade and slide in
 */
@Composable
private fun LocationListItemAnimated(
    location: Location,
    onClick: () -> Unit,
    delayMillis: Int = 0
) {
    var isPressed by remember { mutableStateOf(false) }

    val itemAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500, delayMillis = delayMillis, easing = EaseOutCubic),
        label = "list_item_alpha"
    )

    val itemOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(500, delayMillis = delayMillis, easing = EaseOutCubic),
        label = "list_item_offset"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF00D9FF).copy(alpha = 0.2f) else Color(0xFF1A2332),
        animationSpec = tween(200),
        label = "list_item_bg"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "list_press_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                alpha = itemAlpha
                translationX = itemOffset
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = !isPressed
                    onClick()
                }
            ),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = location.icon, fontSize = 20.sp)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = location.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Navigation overlay animation with animated path/route visualization
 */
@Composable
private fun NavigationOverlayAnimated(selectedLocation: Location?) {
    val infiniteTransition = rememberInfiniteTransition(label = "nav_overlay")

    // Pulsing dot animation
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    // Arrow animation (moving forward)
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "arrow_movement"
    )

    // Wave animation
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated wave circles
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer wave circle
                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            scaleX = 0.5f + (waveScale * 0.8f)
                            scaleY = 0.5f + (waveScale * 0.8f)
                            alpha = 1f - waveScale
                        },
                    shape = CircleShape,
                    color = Color(0xFF00D9FF).copy(alpha = 0.3f)
                ) {}

                // Middle wave circle
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            scaleX = 0.6f + (waveScale * 0.6f)
                            scaleY = 0.6f + (waveScale * 0.6f)
                            alpha = 1f - waveScale
                        },
                    shape = CircleShape,
                    color = Color(0xFF00D9FF).copy(alpha = 0.4f)
                ) {}

                // Central pulsing dot
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .scale(dotScale),
                    shape = CircleShape,
                    color = Color(0xFF00D9FF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Navigating",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Location name
            Text(
                text = "Taking you to ${selectedLocation?.name ?: "destination"}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Animated progress text
            Text(
                text = "Robot is moving...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Arrow animation
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF00D9FF),
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                translationX = arrowOffset - (index * 8f)
                                alpha = 0.3f + (0.7f * (1f - (index * 0.2f)))
                            }
                    )
                }
            }
        }
    }
}
