package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor

/**
 * SpeechOrchestrator - Centralized intent detection and routing
 *
 * Analyzes user speech input and determines the intent with contextual data.
 * Produces a lightweight Context object for downstream processing.
 */
class SpeechOrchestrator(private val doctors: List<Doctor>) {

    enum class Intent {
        FIND_DOCTOR,      // User asking about doctors or specialties
        NAVIGATE,         // User wants to go to a location
        BOOK,             // User wants to book an appointment
        GENERAL           // General questions about hospital
    }

    data class Context(
        val intent: Intent,
        val query: String,
        val doctor: Doctor? = null,
        val department: String? = null,
        val confidence: Float = 0.5f
    )

    /**
     * Analyze speech input and extract intent + relevant context
     *
     * @param text User's spoken/typed input
     * @return Context with detected intent and relevant data
     */
    fun analyze(text: String): Context {
        if (text.isBlank()) {
            return Context(Intent.GENERAL, text)
        }

        val lower = text.lowercase()

        // Step 0: Remove noise from ASR (common misrecognitions)
        val cleaned = removeASRNoise(lower)

        // Step 1: Try to match a specific doctor by name
        val doctor = doctors.find { doctor ->
            val doctorNameClean = doctor.name
                .lowercase()
                .replace("dr.", "")
                .replace("dr ", "")
                .trim()
            cleaned.contains(doctorNameClean)
        }

        // Step 2: Try to match a department
        val department = doctors
            .map { it.department }
            .distinct()
            .find { dept -> cleaned.contains(dept.lowercase()) }

        // Step 3: Detect intent from keywords
        val intent = when {
            // Navigation-related keywords
            cleaned.contains("navigate") ||
            cleaned.contains("take me") ||
            cleaned.contains("go to") ||
            cleaned.contains("where is") ||
            cleaned.contains("cabin") -> Intent.NAVIGATE

            // Booking-related keywords
            cleaned.contains("book") ||
            cleaned.contains("appointment") ||
            cleaned.contains("schedule") ||
            cleaned.contains("reserve") -> Intent.BOOK

            // Doctor lookup keywords
            doctor != null ||
            department != null ||
            cleaned.contains("doctor") ||
            cleaned.contains("specialist") ||
            cleaned.contains("cardiologist") ||
            cleaned.contains("surgeon") ||
            cleaned.contains("neurologist") ||
            cleaned.contains("pediatrician") -> Intent.FIND_DOCTOR

            // Default to general inquiry
            else -> Intent.GENERAL
        }

        // Calculate confidence (higher if both doctor and department match, or specific keywords found)
        val confidence = when {
            doctor != null && department != null -> 0.95f
            doctor != null || department != null -> 0.85f
            intent != Intent.GENERAL -> 0.75f
            else -> 0.5f
        }

        return Context(
            intent = intent,
            query = text,
            doctor = doctor,
            department = department,
            confidence = confidence
        )
    }

    /**
     * Remove common ASR noise and misrecognitions
     */
    private fun removeASRNoise(text: String): String {
        val noiseWords = listOf(
            "japanese", "indian", "american", "british", "canadian", // nationalities
            "male", "female", // gender descriptors
            "senior", "junior", "sr", "jr", // qualifiers
            "the", "a", "an" // articles
        )

        var cleaned = text
        noiseWords.forEach { noise ->
            cleaned = cleaned.replace(Regex("\\b$noise\\b"), " ")
        }

        return cleaned.replace(Regex("\\s+"), " ").trim()
    }
}

