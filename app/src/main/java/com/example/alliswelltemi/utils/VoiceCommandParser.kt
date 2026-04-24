package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import android.util.Log

/**
 * Voice command parser for understanding doctor-related voice queries
 * Handles patterns like: "Find Dr. Sharma", "Show cardiology doctors", "Book Dr. Patel"
 */
object VoiceCommandParser {
    private val tag = "VoiceCommandParser"

    data class ParsedCommand(
        val type: CommandType,
        val targetName: String? = null,      // Doctor name or department
        val action: String? = null,           // "find", "book", "navigate"
        val confidence: Float = 1f            // Confidence score (0-1)
    )

    enum class CommandType {
        FIND_DOCTOR,           // "Find Dr. Sharma", "Show me Dr. Patel"
        FILTER_DEPARTMENT,     // "Show cardiology doctors", "List neurology"
        NAVIGATE_TO_DOCTOR,    // "Take me to Dr. Sharma", "Navigate to orthopedics"
        BOOK_DOCTOR,           // "Book Dr. Patel", "Appointment with Dr. Sharma"
        GET_INFO,              // "Tell me about Dr. Sharma", "Info on cardiology"
        UNKNOWN
    }

    /**
     * Parse voice query and extract intent + target
     */
    fun parseCommand(query: String, doctors: List<Doctor>): ParsedCommand {
        val normalized = normalizeQuery(query)
        Log.d(tag, "Parsing: $normalized")

        // Check for action keywords
        return when {
            containsBookingIntent(normalized) -> parseBookingIntent(normalized, doctors)
            containsNavigationIntent(normalized) -> parseNavigationIntent(normalized, doctors)
            containsFilterIntent(normalized) -> parseFilterIntent(normalized, doctors)
            containsInfoIntent(normalized) -> parseInfoIntent(normalized, doctors)
            containsDoctorName(normalized, doctors) -> parseDoctorQuery(normalized, doctors)
            containsDepartment(normalized, doctors) -> parseDepartmentQuery(normalized, doctors)
            else -> ParsedCommand(CommandType.UNKNOWN)
        }
    }

    /**
     * Normalize query for matching
     */
    fun normalizeQuery(query: String): String {
        return query.lowercase().trim()
            .replace("dr.", "doctor")
            .replace("dr ", "doctor ")
            .replace("sr.", "senior")
            .replace("jr.", "junior")
            .replace("show me", "show")
            .replace("please", "")
            .trim()
    }

    /**
     * Check if query contains booking intent
     */
    private fun containsBookingIntent(normalized: String): Boolean {
        val bookingKeywords = listOf("book", "appointment", "schedule", "reserve")
        return bookingKeywords.any { normalized.contains(it) }
    }

    /**
     * Parse booking command
     */
    private fun parseBookingIntent(normalized: String, doctors: List<Doctor>): ParsedCommand {
        val doctorName = extractDoctorName(normalized, doctors)
        
        // If no doctor found, treat as unknown
        if (doctorName == null) {
            return ParsedCommand(
                type = CommandType.UNKNOWN,
                targetName = null,
                action = "book",
                confidence = 0.3f
            )
        }
        
        return ParsedCommand(
            type = CommandType.BOOK_DOCTOR,
            targetName = doctorName,
            action = "book",
            confidence = 1f
        )
    }

    /**
     * Check if query contains navigation intent
     */
    private fun containsNavigationIntent(normalized: String): Boolean {
        val navKeywords = listOf("navigate", "go to", "take me to", "direction", "way to")
        return navKeywords.any { normalized.contains(it) }
    }

    /**
     * Parse navigation command
     */
    private fun parseNavigationIntent(normalized: String, doctors: List<Doctor>): ParsedCommand {
        val targetName = extractDoctorName(normalized, doctors)
            ?: extractDepartment(normalized, doctors)

        // If no match found, treat as unknown
        if (targetName == null) {
            return ParsedCommand(
                type = CommandType.UNKNOWN,
                targetName = null,
                action = "navigate",
                confidence = 0.3f
            )
        }

        return ParsedCommand(
            type = CommandType.NAVIGATE_TO_DOCTOR,
            targetName = targetName,
            action = "navigate",
            confidence = if (targetName != null) 1f else 0.6f
        )
    }

    /**
     * Check if query contains filter intent
     */
    private fun containsFilterIntent(normalized: String): Boolean {
        val filterKeywords = listOf("show", "list", "find", "doctors in", "department of")
        return filterKeywords.any { normalized.contains(it) }
    }

    /**
     * Parse filter/search command
     */
    private fun parseFilterIntent(normalized: String, doctors: List<Doctor>): ParsedCommand {
        val department = extractDepartment(normalized, doctors)
        return if (department != null) {
            ParsedCommand(
                type = CommandType.FILTER_DEPARTMENT,
                targetName = department,
                action = "filter",
                confidence = 1f
            )
        } else {
            val doctorName = extractDoctorName(normalized, doctors)
            if (doctorName != null) {
                ParsedCommand(
                    type = CommandType.FIND_DOCTOR,
                    targetName = doctorName,
                    action = "find",
                    confidence = 1f
                )
            } else {
                // Couldn't match anything, treat as unknown
                ParsedCommand(
                    type = CommandType.UNKNOWN,
                    targetName = null,
                    action = "filter",
                    confidence = 0.3f
                )
            }
        }
    }

    /**
     * Check if query contains info intent
     */
    private fun containsInfoIntent(normalized: String): Boolean {
        val infoKeywords = listOf("who is", "who's", "what is", "where is", "tell me", "info", "about", "details", "experience", "specialization")
        return infoKeywords.any { normalized.contains(it) }
    }

    /**
     * Parse info request command
     */
    private fun parseInfoIntent(normalized: String, doctors: List<Doctor>): ParsedCommand {
        val targetName = extractDoctorName(normalized, doctors)
            ?: extractDepartment(normalized, doctors)

        // If no doctor or department found, treat as unknown instead
        if (targetName == null) {
            return ParsedCommand(
                type = CommandType.UNKNOWN,
                targetName = null,
                action = "info",
                confidence = 0.3f
            )
        }

        return ParsedCommand(
            type = CommandType.GET_INFO,
            targetName = targetName,
            action = "info",
            confidence = if (targetName != null) 1f else 0.6f
        )
    }

    /**
     * Parse generic doctor query
     */
    private fun parseDoctorQuery(normalized: String, doctors: List<Doctor>): ParsedCommand {
        val doctorName = extractDoctorName(normalized, doctors)
        return ParsedCommand(
            type = CommandType.FIND_DOCTOR,
            targetName = doctorName,
            confidence = 1f
        )
    }

    /**
     * Parse generic department query
     */
    private fun parseDepartmentQuery(normalized: String, doctors: List<Doctor>): ParsedCommand {
        val department = extractDepartment(normalized, doctors)
        return ParsedCommand(
            type = CommandType.FILTER_DEPARTMENT,
            targetName = department,
            confidence = 1f
        )
    }

    /**
     * Extract doctor name from query with fuzzy matching and noise filtering
     */
    private fun extractDoctorName(normalized: String, doctors: List<Doctor>): String? {
        // Remove common noise words that might appear in ASR results
        val cleaned = removeNoiseWords(normalized)

        // First try exact matching on cleaned query
        doctors.find { doctor ->
            val fullName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
            val lastNameParts = fullName.split(" ").filter { it.isNotEmpty() }

            cleaned.contains(fullName) ||
            fullName.contains(cleaned) ||
            lastNameParts.any { part ->
                (cleaned.contains(part) || part.contains(cleaned)) && part.length > 2
            }
        }?.let { return it.name }

        // If exact match fails, try fuzzy matching for similar names
        doctors.forEach { doctor ->
            val fullName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
            val nameParts = fullName.split(" ").filter { it.isNotEmpty() }
            
            nameParts.forEach { part ->
                if (part.length > 2 && levenshteinDistance(part, extractQueryName(cleaned)) <= 2) {
                    return doctor.name
                }
            }
        }

        return null
    }

    /**
     * Remove common ASR noise words from query
     */
    private fun removeNoiseWords(query: String): String {
        val noiseWords = listOf(
            "japanese", "indian", "american", "british", // nationalities
            "male", "female", "doctor", "dr", // titles that don't help matching
            "specialist", "surgeon", "physician", // general titles
            "senior", "junior", "sr", "jr", // qualifiers
            "the", "a", "an" // articles
        )

        var result = query
        noiseWords.forEach { noise ->
            result = result.replace(Regex("\\b$noise\\b"), " ")
        }

        return result.replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Extract the doctor name portion from query (handles "who is", "show me", etc.)
     */
    private fun extractQueryName(normalized: String): String {
        val cleanQuery = normalized
            .replace("who is", "")
            .replace("who's", "")
            .replace("what is", "")
            .replace("where is", "")
            .replace("tell me about", "")
            .replace("show me", "")
            .replace("find", "")
            .replace("doctor", "")
            .replace("dr", "")
            .trim()
        return cleanQuery
    }

    /**
     * Calculate Levenshtein distance for fuzzy matching
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val s1Lower = s1.lowercase()
        val s2Lower = s2.lowercase()
        
        if (s1Lower == s2Lower) return 0
        if (s1Lower.isEmpty()) return s2Lower.length
        if (s2Lower.isEmpty()) return s1Lower.length

        val dp = Array(s1Lower.length + 1) { IntArray(s2Lower.length + 1) }

        for (i in 0..s1Lower.length) dp[i][0] = i
        for (j in 0..s2Lower.length) dp[0][j] = j

        for (i in 1..s1Lower.length) {
            for (j in 1..s2Lower.length) {
                val cost = if (s1Lower[i - 1] == s2Lower[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }

        return dp[s1Lower.length][s2Lower.length]
    }

    /**
     * Check if query contains doctor name
     */
    private fun containsDoctorName(normalized: String, doctors: List<Doctor>): Boolean {
        return doctors.any { doctor ->
            val fullName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
            normalized.contains(fullName)
        }
    }

    /**
     * Extract department from query
     */
    private fun extractDepartment(normalized: String, doctors: List<Doctor>): String? {
        val departments = doctors.map { it.department }.distinct()
        return departments.find { dept ->
            val normalizedDept = dept.lowercase().trim()
            normalized.contains(normalizedDept)
        }
    }

    /**
     * Check if query contains department
     */
    private fun containsDepartment(normalized: String, doctors: List<Doctor>): Boolean {
        val departments = doctors.map { it.department }.distinct()
        return departments.any { dept ->
            normalized.contains(dept.lowercase())
        }
    }

    /**
     * Get human-readable summary of parsed command
     */
    fun summarizeCommand(command: ParsedCommand): String {
        return when (command.type) {
            CommandType.FIND_DOCTOR -> "Finding doctor: ${command.targetName ?: "Unknown"}"
            CommandType.FILTER_DEPARTMENT -> "Filtering department: ${command.targetName ?: "Unknown"}"
            CommandType.NAVIGATE_TO_DOCTOR -> "Navigating to: ${command.targetName ?: "Unknown"}"
            CommandType.BOOK_DOCTOR -> "Booking with: ${command.targetName ?: "Unknown"}"
            CommandType.GET_INFO -> "Getting info on: ${command.targetName ?: "Unknown"}"
            CommandType.UNKNOWN -> "Could not understand command"
        }
    }
}

