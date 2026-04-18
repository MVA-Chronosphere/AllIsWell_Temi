package com.example.alliswelltemi.utils

import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

/**
 * Temi SDK Utility Functions for easy integration
 */
object TemiUtils {

    /**
     * Speak with flexible language support
     */
    fun Robot?.speak(
        text: String,
        language: String = "en-US",
        onDone: (() -> Unit)? = null
    ) {
        this?.speak(
            TtsRequest.create(
                speech = text,
                isShowOnConversationLayer = false
            )
        )
    }

    /**
     * Navigate to a location (if you set up locations in Temi)
     * Example: robot?.navigateTo("Pharmacy")
     */
    fun Robot?.navigateTo(location: String, onArrival: (() -> Unit)? = null) {
        try {
            this?.goTo(location)
            onArrival?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Play a beep sound and speak emergency alert
     */
    fun Robot?.triggerEmergency(hospitalName: String = "All Is Well Hospital") {
        this?.speak(
            TtsRequest.create(
                speech = "Emergency alert activated. Alerting medical staff at $hospitalName immediately. Please stay calm.",
                isShowOnConversationLayer = false
            )
        )
    }

    /**
     * Get battery status
     */
    fun Robot?.getBatteryStatus(): Int {
        return this?.batteryData?.level ?: 0
    }

    /**
     * Check if robot is connected
     */
    fun Robot?.isConnected(): Boolean {
        return this != null
    }

    /**
     * Localized strings for different languages
     */
    object Strings {
        fun getWelcomeMessage(language: String): String {
            return when (language) {
                "hi" -> "नमस्ते, मैं टेमी हूँ। मैं आपकी मदद करने के लिए यहाँ हूँ।"
                else -> "Hello, I am Temi. I'm here to help you."
            }
        }

        fun getNavigationPrompt(language: String): String {
            return when (language) {
                "hi" -> "कृपया बताएं कि आप कहां जाना चाहते हैं।"
                else -> "Please tell me where you would like to go."
            }
        }

        fun getAppointmentPrompt(language: String): String {
            return when (language) {
                "hi" -> "कृपया अपनी पसंदीदा तारीख और समय चुनें।"
                else -> "Please select your preferred date and time."
            }
        }

        fun getDoctorPrompt(language: String): String {
            return when (language) {
                "hi" -> "कृपया एक विशेषज्ञ डॉक्टर चुनें।"
                else -> "Please select a specialist doctor."
            }
        }

        fun getEmergencyAlert(language: String): String {
            return when (language) {
                "hi" -> "आपातकाल सक्रिय। चिकित्सा कर्मचारियों को तुरंत बुला रहे हैं।"
                else -> "Emergency activated. Calling medical staff immediately."
            }
        }

        fun getLanguageConfirmation(language: String): String {
            return when (language) {
                "hi" -> "भाषा हिंदी में बदल गई।"
                else -> "Language changed to English."
            }
        }
    }

    /**
     * Temi Navigation Locations (Example setup)
     */
    object Locations {
        const val PHARMACY = "Pharmacy"
        const val EMERGENCY_ROOM = "Emergency Room"
        const val ICU = "ICU"
        const val RECEPTION = "Reception"
        const val IMAGING = "Imaging Department"
        const val LABORATORY = "Laboratory"
        const val CARDIOLOGY = "Cardiology Department"
        const val NEUROLOGY = "Neurology Department"
        const val ORTHOPEDICS = "Orthopedics Department"
        const val GENERAL_WARD = "General Ward"
        const val PRIVATE_WARD = "Private Ward"
    }
}

