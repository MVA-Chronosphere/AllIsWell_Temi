package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.R
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

/**
 * Feedback Screen for Temi Hospital Assistant
 * Allows users to provide feedback with name, description, and star rating
 */
@Composable
fun FeedbackScreen(
    robot: Robot? = null,
    onBackPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentLanguage by remember { mutableStateOf("en") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var showConfirmation by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }

    TemiScreenScaffold(
        title = if (currentLanguage == "en")
            stringResource(id = R.string.feedback)
        else
            stringResource(id = R.string.feedback_hi),
        onBackClick = onBackPress,
        modifier = modifier
    ) { contentMod ->
        if (showConfirmation) {
            // Confirmation view
            FeedbackConfirmationView(
                onContinue = {
                    showConfirmation = false
                    name = ""
                    description = ""
                    rating = 0
                    validationError = ""
                },
                currentLanguage = currentLanguage,
                robot = robot,
                modifier = contentMod
            )
        } else {
            // Form view
            Column(
                modifier = contentMod
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Subtitle
                Text(
                    text = if (currentLanguage == "en")
                        stringResource(id = R.string.feedback_subtitle)
                    else
                        stringResource(id = R.string.feedback_subtitle_hi),
                    fontSize = 16.sp,
                    color = Color(0xFFA0A0A0),
                    fontWeight = FontWeight.Normal
                )

                // Form Container Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A2332).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Name Input
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == "en")
                                    stringResource(id = R.string.full_name)
                                else
                                    stringResource(id = R.string.full_name_hi),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00D9FF)
                            )
                            TextField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = {
                                    Text(
                                        text = if (currentLanguage == "en")
                                            "Enter your name"
                                        else
                                            "अपना नाम दर्ज करें",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF0B1220),
                                    unfocusedContainerColor = Color(0xFF0B1220),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF00D9FF),
                                    unfocusedIndicatorColor = Color(0x3300D9FF)
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                                    fontSize = 14.sp
                                )
                            )
                        }

                        // Description Input
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == "en")
                                    "Feedback"
                                else
                                    "प्रतिक्रिया",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00D9FF)
                            )
                            TextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = {
                                    Text(
                                        text = if (currentLanguage == "en")
                                            "Enter your feedback"
                                        else
                                            "अपनी प्रतिक्रिया दर्ज करें",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF0B1220),
                                    unfocusedContainerColor = Color(0xFF0B1220),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF00D9FF),
                                    unfocusedIndicatorColor = Color(0x3300D9FF)
                                ),
                                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                                    fontSize = 14.sp
                                )
                            )
                        }

                        // Star Rating
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (currentLanguage == "en")
                                    "Rate your experience"
                                else
                                    "अपने अनुभव को दर्ज करें",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00D9FF)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(5) { index ->
                                    StarRating(
                                        isSelected = index < rating,
                                        onClick = { rating = index + 1 }
                                    )
                                }
                            }

                            // Rating display text
                            if (rating > 0) {
                                Text(
                                    text = if (currentLanguage == "en")
                                        "$rating out of 5 stars"
                                    else
                                        "$rating में से 5 सितारे",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA0A0A0),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        // Validation Error Message
                        if (validationError.isNotEmpty()) {
                            Text(
                                text = validationError,
                                fontSize = 12.sp,
                                color = Color(0xFFFF6B6B),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        // Validation
                        validationError = when {
                            name.isBlank() -> if (currentLanguage == "en")
                                "Please enter your name"
                            else
                                "कृपया अपना नाम दर्ज करें"
                            description.isBlank() -> if (currentLanguage == "en")
                                "Please enter your feedback"
                            else
                                "कृपया अपनी प्रतिक्रिया दर्ज करें"
                            rating == 0 -> if (currentLanguage == "en")
                                "Please select a rating"
                            else
                                "कृपया रेटिंग चुनें"
                            else -> ""
                        }

                        if (validationError.isEmpty()) {
                            // Show confirmation
                            val confirmationSpeech = if (currentLanguage == "en")
                                "Thank you for your feedback, $name. We appreciate your thoughts."
                            else
                                "आपकी प्रतिक्रिया के लिए धन्यवाद, $name। हम आपके विचारों की सराहना करते हैं।"

                            robot?.speak(
                                TtsRequest.create(
                                    speech = confirmationSpeech,
                                    isShowOnConversationLayer = false
                                )
                            )
                            showConfirmation = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D9FF)
                    )
                ) {
                    Text(
                        text = if (currentLanguage == "en")
                            "Submit Feedback"
                        else
                            "प्रतिक्रिया जमा करें",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B1220)
                    )
                }

                // Language toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                currentLanguage = if (currentLanguage == "en") "hi" else "en"
                            }
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == "en")
                            "हिंदी में स्विच करें"
                        else
                            "Switch to English",
                        fontSize = 12.sp,
                        color = Color(0xFF00D9FF),
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Star rating component
 */
@Composable
fun StarRating(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "Star Rating",
        tint = if (isSelected) Color(0xFFFFD700) else Color(0xFF3A4F5F),
        modifier = modifier
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 6.dp)
    )
}

/**
 * Feedback confirmation view shown after successful submission
 */
@Composable
fun FeedbackConfirmationView(
    onContinue: () -> Unit,
    currentLanguage: String,
    robot: Robot?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Confirmation Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D9FF).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                fontSize = 60.sp,
                color = Color(0xFF00D9FF),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Confirmation Text
        Text(
            text = if (currentLanguage == "en")
                "Thank You!"
            else
                "धन्यवाद!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (currentLanguage == "en")
                "Your feedback has been submitted successfully"
            else
                "आपकी प्रतिक्रिया सफलतापूर्वक जमा हो गई है",
            fontSize = 18.sp,
            color = Color(0xFFA0A0A0),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (currentLanguage == "en")
                "We value your input and will use it to improve our services."
            else
                "हम आपके सुझाव की सराहना करते हैं और इसे हमारी सेवाओं में सुधार करने के लिए उपयोग करेंगे।",
            fontSize = 14.sp,
            color = Color(0xFFA0A0A0),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00D9FF)
            )
        ) {
            Text(
                text = if (currentLanguage == "en")
                    "Continue"
                else
                    "जारी रखें",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1220)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

