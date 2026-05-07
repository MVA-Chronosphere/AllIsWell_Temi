package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.Location
import com.example.alliswelltemi.data.LocationData
import com.example.alliswelltemi.data.DoctorData
import android.util.Log
import com.robotemi.sdk.Robot

/**
 * SpeechOrchestrator - Centralized intent detection and routing
 *
 * Analyzes user speech input and determines the intent with contextual data.
 * Produces a lightweight Context object for downstream processing.
 * Supports location-based navigation in addition to doctor and appointment intents.
 */
class SpeechOrchestrator(
    private val doctors: List<Doctor>,
    private var robot: Robot? = null
) {

    enum class Intent {
        FIND_DOCTOR,      // User asking about doctors or specialties
        NAVIGATE,         // User wants to go to a location or doctor's cabin
        BOOK,             // User wants to book an appointment
        DANCE,            // User asking robot to dance
        GENERAL           // General questions about hospital
    }

    data class Context(
        val intent: Intent,
        val query: String,
        val doctor: Doctor? = null,
        val department: String? = null,
        val location: Location? = null,  // Location to navigate to (new field)
        val confidence: Float = 0.5f,
        val danceMove: DanceService.DanceMove? = null  // Specific dance type if DANCE intent
    )

    private val tag = "SpeechOrchestrator"

    /**
     * Update robot instance (useful for when robot becomes available after initialization)
     */
    fun setRobot(newRobot: Robot?) {
        this.robot = newRobot
        Log.d(tag, "Robot instance updated for location validation")
    }

    /**
     * Analyze speech input and extract intent + relevant context
     * Supports both English and Hindi keywords
     * Detects location names for navigation commands
     * 
     * CRITICAL FIX: Properly distinguishes information queries from navigation commands
     * - "What is pathology?" → GENERAL (info query)
     * - "Take me to pathology" → NAVIGATE (action request)
     * - "Show pathology doctors" → FIND_DOCTOR (department filter)
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

        // CRITICAL FIX: Detect if this is an INFORMATION QUERY (info keywords present)
        // Info queries should NOT trigger navigation even if they contain location names
        val isInfoQuery = isInformationQuery(cleaned)
        Log.d(tag, "🔍 Is information query? $isInfoQuery (cleaned: '$cleaned')")

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
            ?: run {
                // Also try to match Hindi department names
                val hindiDept = DoctorData.findDepartmentByName(cleaned)
                if (hindiDept != null) {
                    Log.d(tag, "📋 Matched Hindi department: $hindiDept")
                    hindiDept
                } else {
                    null
                }
            }

        // Step 3: Try to match a hospital location (for navigation)
        // CRITICAL: Skip location extraction if this is an info query
        val location = if (!isInfoQuery) extractLocation(cleaned) else null
        
        if (isInfoQuery && location != null) {
            Log.d(tag, "⚠️ Info query detected - ignoring location match: ${location.name}")
        }

        // Step 4: Detect dance intent (MUST be before other intents)
        val danceMove = detectDanceIntent(cleaned)

        // Step 5: Detect intent from keywords (English + Hindi)
        // CRITICAL: Check info query FIRST to prevent misclassification
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

            // Information/General Query (MUST be before navigation check!)
            // This prevents "What is X?" from triggering NAVIGATE
            isInfoQuery -> Intent.GENERAL

            // Navigation-related keywords (English + Hindi)
             // Priority: Location matching first, then doctor/cabin
             // ONLY if NOT an info query
             location != null ||
             cleaned.contains("navigate") ||
             cleaned.contains("take me") ||
             cleaned.contains("go to") ||
             cleaned.contains("where is") ||
             cleaned.contains("cabin") ||
             cleaned.contains("ले चलो") ||
             cleaned.contains("ले जाओ") ||
            cleaned.contains("लेके चलो") ||
             cleaned.contains("कहां है") ||
             cleaned.contains("कहाँ है") ||
             cleaned.contains("किधर") ||
             cleaned.contains("कहा है") ||
             cleaned.contains("केबिन") ||
             cleaned.contains("दिशा") ||
             cleaned.contains("रास्ता") ||
             cleaned.contains("पता") -> Intent.NAVIGATE

            // Booking-related keywords (English + Hindi)
            cleaned.contains("book") ||
            cleaned.contains("appointment") ||
            cleaned.contains("schedule") ||
            cleaned.contains("reserve") ||
            cleaned.contains("बुक") ||
            cleaned.contains("अपॉइंटमेंट") ||
            cleaned.contains("अपोइंटमेंट") ||
            cleaned.contains("समय") ||
            cleaned.contains("मिलना") ||
            cleaned.contains("परामर्श") -> Intent.BOOK

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
            cleaned.contains("डाक्टर") ||
            cleaned.contains("विशेषज्ञ") ||
            cleaned.contains("चिकित्सक") ||
            cleaned.contains("सर्जन") ||
            cleaned.contains("डाक्टर कहां") ||
            cleaned.contains("कौन डॉक्टर") -> Intent.FIND_DOCTOR

            // Default to general inquiry
            else -> Intent.GENERAL
        }

        // Calculate confidence
        val confidence = when {
            intent == Intent.DANCE -> 0.95f  // High confidence for dance
            location != null && (cleaned.contains("navigate") || cleaned.contains("take me") || cleaned.contains("go to")) -> 0.95f  // High confidence for location navigation
            doctor != null && department != null -> 0.95f
            doctor != null || department != null -> 0.85f
            intent != Intent.GENERAL -> 0.75f
            else -> 0.5f
        }

        Log.d(tag, "🗣️ Analyzed: intent=$intent, location=${location?.name}, doctor=${doctor?.name}, confidence=$confidence")

        return Context(
            intent = intent,
            query = text,
            doctor = doctor,
            department = department,
            location = location,  // Include extracted location
            confidence = confidence,
            danceMove = danceMove
        )
    }

    /**
     * Extract location from cleaned query text
     * Supports both English and Hindi location names
     * Handles synonyms like "entrance", "exit", "gate", "front", "back", etc.
     * Validates that locations exist on the robot's actual saved map
     * Returns null if no location is found
     */
    private fun extractLocation(cleaned: String): Location? {
        // Get robot's saved locations - CRITICAL for actual navigation
        val robotSavedLocations = if (robot != null) {
            try {
                MapLocationService.fetchSavedLocations(robot)
            } catch (e: Exception) {
                Log.e(tag, "Error fetching robot locations: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }

        Log.d(tag, "🤖 Robot has ${robotSavedLocations.size} saved locations")
        if (robotSavedLocations.isNotEmpty()) {
            Log.d(tag, "📍 Available locations on robot: ${robotSavedLocations.map { it.name }.joinToString(", ")}")
        }

        // PRIORITY 1: Try matching against robot's actual saved locations directly
         // This is the MOST IMPORTANT - only navigate to locations the robot actually has
         if (robotSavedLocations.isNotEmpty()) {
             for (robotLocation in robotSavedLocations) {
                 val robotNameLower = robotLocation.name.lowercase()
                 val robotMapNameLower = robotLocation.mapName.lowercase()

                 // Try exact substring match
                 if (cleaned.contains(robotNameLower) || cleaned.contains(robotMapNameLower)) {
                     Log.d(tag, "✅ MATCHED from robot map: ${robotLocation.name} (exact match)")
                     return robotLocation
                 }

                 // Try fuzzy match against robot location
                 val nameDistance = levenshteinDistance(cleaned, robotNameLower)
                 val mapDistance = levenshteinDistance(cleaned, robotMapNameLower)
                 if (nameDistance <= 2 || mapDistance <= 2) {
                     Log.d(tag, "✅ MATCHED from robot map: ${robotLocation.name} (fuzzy match, distance=$nameDistance)")
                     return robotLocation
                 }
             }
         }

          // PRIORITY 1B: Try matching Hindi location names (for "फार्मेसी" etc.)
          // Enhanced bilingual matching for locations with Hindi names
          for (location in LocationData.ALL_LOCATIONS) {
              if (location.nameHi.isNotEmpty()) {
                  // Direct substring match for Hindi (case-sensitive but exact)
                  if (cleaned.contains(location.nameHi)) {
                      Log.d(tag, "✅ MATCHED Hindi location name: ${location.name} (Hindi: ${location.nameHi})")
                      // robotSavedLocations are Strings, so just return the location if found
                      return location
                  }

                  // Partial Hindi match (important for "कार्डियो" matching "कार्डियोलॉजी")
                  if (location.nameHi.contains(cleaned) && cleaned.length >= 3) {
                      Log.d(tag, "✅ PARTIAL MATCHED Hindi location: ${location.name} (Hindi partial: ${location.nameHi})")
                      return location
                  }
              }
          }
          
           // PRIORITY 1C: Check if input contains a department name (Hindi or English)
           // If yes, find and navigate to that department location
           val matchedDept = DoctorData.findDepartmentByName(cleaned)
           if (matchedDept != null) {
               Log.d(tag, "🏥 Department name detected: '$matchedDept' (from Hindi/English input)")

               // Find location matching this department
               val deptLocation = LocationData.ALL_LOCATIONS.find { location ->
                   location.name.lowercase().contains(matchedDept.lowercase()) ||
                   location.id.lowercase().contains(matchedDept.lowercase().replace(" ", "_"))
               }

               if (deptLocation != null) {
                   Log.d(tag, "✅ MATCHED Department Location: ${deptLocation.name}")
                   return deptLocation
               }
           }

           // PRIORITY 1D: Partial word matching for location names (e.g., "pharmacy" → "MAIN PHARMACY")
           // This allows "pharmacy" to match "MAIN PHARMACY", "aud" to match "opd", etc.
           for (location in LocationData.ALL_LOCATIONS) {
               val locationNameLower = location.name.lowercase()
               val locationIdLower = location.id.lowercase()
               val locationMapNameLower = location.mapName.lowercase()

               // Extract individual words from the location name
               val locationWords = locationNameLower.split("\\s+".toRegex())
               val cleanedWords = cleaned.split("\\s+".toRegex()).filter { it.length >= 2 }

               // Check if any word in the input matches any word in the location name
               for (inputWord in cleanedWords) {
                   for (locWord in locationWords) {
                       // Exact word match
                       if (inputWord == locWord) {
                           Log.d(tag, "✅ PARTIAL MATCHED location: ${location.name} (word: '$inputWord' matches '$locWord')")
                           return location
                       }
                       // Prefix match (e.g., "phar" matches "pharmacy")
                       if (locWord.startsWith(inputWord) && inputWord.length >= 3) {
                           Log.d(tag, "✅ PREFIX MATCHED location: ${location.name} (word: '$inputWord' is prefix of '$locWord')")
                           return location
                       }
                   }
               }
           }

        // PRIORITY 2: Try synonym-based matching for common entrance/exit patterns
        // Handle patterns like "back entrance", "front entrance", "side exit", etc.
        val entranceMatches = listOf(
            Pair(Regex("\\b(back|rear|side)\\s+(entrance|door|gate|entry|way)\\b"), "entrance"),
            Pair(Regex("\\b(front|main)\\s+(entrance|door|gate|entry|way)\\b"), "entrance"),
            Pair(Regex("\\b(exit|leave|out)\\s*(way)?\\b"), "exit")
        )

        for ((pattern, keyword) in entranceMatches) {
            if (pattern.containsMatchIn(cleaned)) {
                Log.d(tag, "🔍 Pattern matched: '$keyword' from input")
                // Try to find a matching location on the robot's map
                if (robotSavedLocations.isNotEmpty()) {
                    val matchedLocation = robotSavedLocations.find {
                        it.name.lowercase().contains(keyword) ||
                        it.mapName.lowercase().contains(keyword)
                    }
                    if (matchedLocation != null) {
                        Log.d(tag, "✅ SYNONYM MATCHED from robot map: ${matchedLocation.name}")
                        return matchedLocation
                    } else {
                        Log.d(tag, "⚠️ Pattern '$keyword' found but no matching location on robot map")
                    }
                }
            }
        }

        // PRIORITY 3: Try matching against predefined locations (fallback)
        // Only use if robot doesn't have a better match
        if (robotSavedLocations.isEmpty()) {
            for (location in LocationData.ALL_LOCATIONS) {
                val nameMatch = location.name.lowercase()
                val nameHiMatch = location.nameHi.lowercase()
                val idMatch = location.id.lowercase()

                if (cleaned.contains(nameMatch) ||
                    cleaned.contains(nameHiMatch) ||
                    cleaned.contains(idMatch)) {
                    Log.d(tag, "⚠️ FALLBACK: Using predefined location (robot map unavailable): ${location.name}")
                    return location
                }
            }
        }

        Log.d(tag, "❌ No location matched in extracted text: '$cleaned'")
        return null
    }

    /**
     * Calculate Levenshtein distance for fuzzy location matching
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        if (s1.isEmpty()) return s2.length
        if (s2.isEmpty()) return s1.length

        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }

        return dp[s1.length][s2.length]
    }

    /**
     * Detect if this is an information query (asking about something, not requesting action)
     * Examples:
     * - "What is pathology?" → Info query (should NOT navigate)
     * - "Who is Dr. Sharma?" → Info query
     * - "Tell me about cardiology" → Info query
     * - "Take me to pathology" → NOT info query (action request)
     */
    private fun isInformationQuery(cleaned: String): Boolean {
        val infoKeywords = listOf(
            // English info keywords
            "what is", "what are", "what's", "tell me", "who is", "who are", "who's",
            "how do i", "how to", "how is", "where can i", "where can i find",
            "about", "information", "info", "explain", "describe", "definition",
            "know about", "learn about", "curious about", "understand",
            // Hindi info keywords
            "क्या है", "कौन है", "कहानी", "बताओ", "जानकारी", "समझाओ",
            "बारे में", "विवरण", "परिभाषा", "कैसे है", "क्यों है"
        )
        
        val actionKeywords = listOf(
            // Action keywords that indicate navigation/booking (NOT info query)
            "navigate", "take me", "go to", "lead me", "bring me", "move", "walk",
            "accompany", "guide me", "show me the way", "directions",
            "book", "appointment", "schedule", "reserve",
            "ले चलो", "ले जाओ", "लेके चलो", "दिखाओ रास्ता",
            "बुक", "अपॉइंटमेंट"
        )
        
        // If action keywords present, it's NOT an info query
        if (actionKeywords.any { cleaned.contains(it) }) {
            return false
        }
        
        // If info keywords present, it IS an info query
        return infoKeywords.any { cleaned.contains(it) }
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
