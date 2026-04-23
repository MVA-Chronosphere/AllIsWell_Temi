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
        DANCE,            // User asking robot to dance
        GENERAL           // General questions about hospital
    }

    data class Context(
        val intent: Intent,
        val query: String,
        val doctor: Doctor? = null,
        val department: String? = null,
        val confidence: Float = 0.5f,
        val danceMove: DanceService.DanceMove? = null  // Specific dance type if DANCE intent
    )

    /**
     * Analyze speech input and extract intent + relevant context
     * Supports both English and Hindi keywords
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

        // Step 3: Detect dance intent (MUST be before other intents)
        val danceMove = detectDanceIntent(cleaned)

        // Step 4: Detect intent from keywords (English + Hindi)
        val intent = when {
            // Dance-related keywords (FIRST priority)
            danceMove != null ||
            cleaned.contains("dance") ||
            cleaned.contains("dancing") ||
            cleaned.contains("move") ||
            cleaned.contains("groove") ||
            cleaned.contains("boogie") ||
            cleaned.contains("spin") ||
            cleaned.contains("नाच") ||
            cleaned.contains("नृत्य") ||
            cleaned.contains("हिलना") ||
            cleaned.contains("घूमना") -> Intent.DANCE

            // Navigation-related keywords (English + Hindi)
            cleaned.contains("navigate") ||
            cleaned.contains("take me") ||
            cleaned.contains("go to") ||
            cleaned.contains("where is") ||
            cleaned.contains("cabin") ||
            cleaned.contains("ले चलो") ||
            cleaned.contains("ले जाओ") ||
            cleaned.contains("कहां है") ||
            cleaned.contains("कहाँ है") ||
            cleaned.contains("केबिन") -> Intent.NAVIGATE

            // Booking-related keywords (English + Hindi)
            cleaned.contains("book") ||
            cleaned.contains("appointment") ||
            cleaned.contains("schedule") ||
            cleaned.contains("reserve") ||
            cleaned.contains("बुक") ||
            cleaned.contains("अपॉइंटमेंट") ||
            cleaned.contains("अपोइंटमेंट") -> Intent.BOOK

            // Doctor lookup keywords (English + Hindi)
            doctor != null ||
            department != null ||
            cleaned.contains("doctor") ||
            cleaned.contains("specialist") ||
            cleaned.contains("cardiologist") ||
            cleaned.contains("surgeon") ||
            cleaned.contains("neurologist") ||
            cleaned.contains("pediatrician") ||
            cleaned.contains("डॉक्टर") ||
            cleaned.contains("विशेषज्ञ") ||
            cleaned.contains("चिकित्सक") -> Intent.FIND_DOCTOR

            // Default to general inquiry
            else -> Intent.GENERAL
        }

        // Calculate confidence (higher if both doctor and department match, or specific keywords found)
        val confidence = when {
            intent == Intent.DANCE -> 0.95f  // High confidence for dance
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
            confidence = confidence,
            danceMove = danceMove
        )
    }

    /**
     * Detect which type of dance the user is requesting
     * Returns null if no specific dance detected
     */
    private fun detectDanceIntent(cleaned: String): DanceService.DanceMove? {
        return when {
            cleaned.contains("spin") || cleaned.contains("घूमना") -> DanceService.DanceMove.SPIN_DANCE
            cleaned.contains("hip hop") || cleaned.contains("hip-hop") || cleaned.contains("हिप हॉप") -> DanceService.DanceMove.HIP_HOP
            cleaned.contains("disco") || cleaned.contains("डिस्को") -> DanceService.DanceMove.DISCO_FEVER
            cleaned.contains("robot") || cleaned.contains("boogie") || cleaned.contains("रोबोट") -> DanceService.DanceMove.ROBOT_BOOGIE
            cleaned.contains("smooth") || cleaned.contains("groove") || cleaned.contains("सुचारु") -> DanceService.DanceMove.SMOOTH_GROOVE
            // If just "dance" without specifying which, pick a random one
            cleaned.contains("dance") || cleaned.contains("नाच") || cleaned.contains("नृत्य") -> {
                DanceService.DanceMove.values().random()
            }
            else -> null
        }
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
