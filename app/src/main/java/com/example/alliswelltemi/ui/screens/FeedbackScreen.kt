package com.example.alliswelltemi.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alliswelltemi.ui.components.*
import com.example.alliswelltemi.ui.theme.HospitalColors
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase

/**
 * Refactored Feedback Screen - Elevated Clinical Design
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HospitalColors.Background),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TemiNavBar - Full Width
            TemiNavBar(
                currentLanguage = currentLanguage,
                onLogoClick = onBackPress
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp, vertical = 20.dp)
                    .weight(1f)
            ) {
                if (showConfirmation) {
                    FeedbackConfirmationView(
                        onContinue = onBackPress,
                        currentLanguage = currentLanguage
                    )
                } else {
                    // Title with Back Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = onBackPress,
                            modifier = Modifier
                                .size(64.dp)
                                .background(HospitalColors.Indigo.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = HospitalColors.Indigo,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        // ✅ CENTERED: Feedback heading centered
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (currentLanguage == "en") "SHARE" else "साझा करें",
                                style = MaterialTheme.typography.displayLarge,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = (if (currentLanguage == "en") "FEEDBACK" else "प्रतिक्रिया").toUpperCase(Locale.current),
                                style = MaterialTheme.typography.displayLarge,
                                color = HospitalColors.Indigo,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(96.dp))
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        ClinicalTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = if (currentLanguage == "en") "FULL NAME" else "पूरा नाम",
                            robot = robot
                        )

                        ClinicalTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = if (currentLanguage == "en") "YOUR EXPERIENCE" else "आपका अनुभव",
                            isLarge = true,
                            robot = robot
                        )

                        // Rating
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = (if (currentLanguage == "en") "RATE YOUR EXPERIENCE" else "अपने अनुभव को रेटिंग दें").toUpperCase(Locale.current),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                repeat(5) { index ->
                                    val isSelected = index < rating
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .shadow(
                                                elevation = if (isSelected) 10.dp else 0.dp,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .background(
                                                if (isSelected) HospitalColors.Indigo else HospitalColors.Indigo.copy(alpha = 0.05f),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { rating = index + 1 },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else HospitalColors.Indigo,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (validationError.isNotEmpty()) {
                            Text(text = validationError, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ClinicalButton(
                            text = if (currentLanguage == "en") "SUBMIT FEEDBACK" else "प्रतिक्रिया जमा करें",
                            onClick = {
                                if (name.isBlank() || description.isBlank() || rating == 0) {
                                    validationError = "Please complete all fields"
                                } else {
                                    robot?.speak(TtsRequest.create("Thank you for your feedback!", false))
                                    showConfirmation = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackConfirmationView(
    onContinue: () -> Unit,
    currentLanguage: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = HospitalColors.SkyBlue,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = (if (currentLanguage == "en") "THANK YOU" else "धन्यवाद").toUpperCase(Locale.current),
            style = MaterialTheme.typography.displayLarge,
            color = HospitalColors.Indigo
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (currentLanguage == "en") "WE APPRECIATE YOUR FEEDBACK" else "हम आपकी प्रतिक्रिया की सराहना करते हैं",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(64.dp))

        ClinicalButton(
            text = "CONTINUE",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
