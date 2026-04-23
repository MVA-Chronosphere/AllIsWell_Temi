package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.LocationData
import com.example.alliswelltemi.data.HospitalKnowledgeBase
import java.text.SimpleDateFormat
import java.util.*

/**
 * RagContextBuilder - Enhanced RAG (Retrieval Augmented Generation) Context Builder
 * Builds optimized prompts with filtered, relevant context for Ollama LLM
 * Includes language detection, smart follow-ups, and performance optimizations
 */
object RagContextBuilder {

    /**
     * Detect language from user input text
     * Returns "hi" for Hindi, "en" for English
     * Enhanced with logging and better detection
     */
    fun detectLanguage(text: String): String {
        // Check for Devanagari script (Hindi Unicode range: U+0900 to U+097F)
        val hasHindiChars = text.matches(Regex(".*[\\u0900-\\u097F].*"))

        // Additional check for common Hindi words
        val hindiKeywords = listOf(
            "डॉक्टर", "कहां", "कहाँ", "क्या", "कैसे", "मुझे", "बताओ", "बताइए",
            "अस्पताल", "अपॉइंटमेंट", "बुक", "विशेषज्ञ", "चिकित्सक"
        )
        val hasHindiKeywords = hindiKeywords.any { text.contains(it) }

        val detectedLanguage = if (hasHindiChars || hasHindiKeywords) "hi" else "en"

        android.util.Log.d("RagContextBuilder", "========== LANGUAGE DETECTION ==========")
        android.util.Log.d("RagContextBuilder", "Input text: '$text'")
        android.util.Log.d("RagContextBuilder", "Has Hindi chars: $hasHindiChars")
        android.util.Log.d("RagContextBuilder", "Has Hindi keywords: $hasHindiKeywords")
        android.util.Log.d("RagContextBuilder", "Detected language: $detectedLanguage")
        android.util.Log.d("RagContextBuilder", "========================================")

        return detectedLanguage
    }

    /**
     * Build comprehensive RAG context with hospital data
     * For general doctor queries, include ALL doctors
     * For specific queries, filter to relevant doctors
     */
    fun buildContext(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val lowerQuery = query.lowercase()

        android.util.Log.d("RagContextBuilder", "Building context for query: '$query' with ${doctors.size} total doctors")

        // Check if this is a general "tell me doctors" type query
        val isGeneralDoctorQuery = isGeneralDoctorQuery(lowerQuery)
        val isFollowUp = isFollowUpQuery(lowerQuery)

        // For general queries, include ALL doctors. For specific queries, filter to relevant ones
        val relevantDoctors = if (isGeneralDoctorQuery) {
            // General "tell me doctors" query - include ALL
            doctors
        } else {
            // Specific query - filter to relevant doctors
            val filtered = doctors.filter { doctor ->
                val doctorName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
                val department = doctor.department.lowercase()
                val specialty = doctor.specialization?.lowercase() ?: ""

                lowerQuery.contains(doctorName) ||
                lowerQuery.contains(department) ||
                lowerQuery.contains(specialty)
            }
            
            if (filtered.isEmpty() && (lowerQuery.contains("doctor") || lowerQuery.contains("specialist"))) {
                if (isFollowUp) {
                    // For follow-ups like "repeat the doctor name", don't flood with all doctors
                    // Let the conversation history handle the context
                    listOf()
                } else {
                    doctors // If they just asked for "doctors" without specific name/dept, show all
                }
            } else {
                filtered.take(10) // Increase limit from 5 to 10 for better context
            }
        }

        android.util.Log.d("RagContextBuilder", "Found ${relevantDoctors.size} relevant doctors for query (general=$isGeneralDoctorQuery, followUp=$isFollowUp)")
        relevantDoctors.forEach { doctor ->
            android.util.Log.d("RagContextBuilder", "  - ${doctor.name} (${doctor.department})")
        }

        // Build doctor context
        val doctorContext = if (relevantDoctors.isNotEmpty()) {
            relevantDoctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true))
                    doctor.name else "Dr. ${doctor.name}"
                "${name} - ${doctor.department}, ${doctor.yearsOfExperience}y exp, Cabin ${doctor.cabin}"
            }
        } else if (isFollowUp) {
            "Relevant doctor mentioned in previous turn."
        } else {
            // Fallback: show all doctors if available
            doctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true))
                    doctor.name else "Dr. ${doctor.name}"
                "${name} - ${doctor.department}"
            }
        }

        // Build location context (popular locations only)
        val locationContext = LocationData.ALL_LOCATIONS
            .filter { it.isPopular }
            .take(3)
            .joinToString(", ") { it.name }

        // Hospital info (keep brief)
        val today = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val hospitalInfo = "All Is Well Hospital | 9AM-5PM | Emergency: 24/7 | $today"

        return """
        Hospital: $hospitalInfo
        Popular Locations: $locationContext
        Doctors: $doctorContext
        """.trimIndent()
    }

    /**
     * Build context with ALL doctors (for comprehensive doctor list queries)
     */
    fun buildContextWithAllDoctors(query: String, doctors: List<Doctor>): String {
        android.util.Log.d("RagContextBuilder", "Building context with ALL ${doctors.size} doctors for query: '$query'")

        // Build doctor context with ALL doctors
        val doctorContext = if (doctors.isNotEmpty()) {
            doctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true))
                    doctor.name else "Dr. ${doctor.name}"
                "${name} - ${doctor.department}, ${doctor.yearsOfExperience}y exp, Cabin ${doctor.cabin}"
            }
        } else {
            "No doctors available"
        }

        // Build location context
        val locationContext = LocationData.ALL_LOCATIONS
            .filter { it.isPopular }
            .take(3)
            .joinToString(", ") { it.name }

        // Hospital info
        val today = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val hospitalInfo = "All Is Well Hospital | 9AM-5PM | Emergency: 24/7 | $today"

        return """
        Hospital: $hospitalInfo
        Popular Locations: $locationContext
        Doctors (Total: ${doctors.size}): $doctorContext
        """.trimIndent()
    }

    /**
     * Helper to detect if query is asking for a list of doctors
     * Refined to avoid treating specialty queries as general lists
     */
    private fun isGeneralDoctorQuery(query: String): Boolean {
        val q = query.lowercase()
        
        // If it's a follow-up query, it's NOT a general query
        if (isFollowUpQuery(q)) return false
        
        // If it contains a specific department or doctor name, it's NOT a general query
        val specificSpecialties = listOf("eye", "ophthalmologist", "heart", "cardiologist", "dental", "dentist", "skin", "dermatologist", "bone", "orthopedic")
        if (specificSpecialties.any { q.contains(it) }) return false
        
        return (q.contains("list") || q.contains("show") || q.contains("tell") || q.contains("who are") || q.contains("available")) && 
               (q.contains("doctor") || q.contains("specialist") || q.contains("physician"))
    }

    /**
     * Helper to detect if query is a follow-up to previous conversation
     */
    private fun isFollowUpQuery(query: String): Boolean {
        val q = query.lowercase()
        return q.contains("repeat") || q.contains("that") || q.contains("him") || q.contains("her") || 
               q.contains("his") || q.contains("hers") || q.contains("they") || q.contains("them") ||
               q.contains("previous") || q.contains("last") || q.contains("again") || q.contains("who is he") ||
               q.contains("who is she")
    }

    /**
     * Build optimized Ollama prompt with smart knowledge base retrieval
     * For "tell me doctors" queries, includes ALL doctors
     * For other queries, uses filtered context
     * Now includes conversation history for context-aware responses
     */
    fun buildOllamaPrompt(query: String, doctors: List<Doctor>, historyContext: String = ""): String {
        val language = detectLanguage(query)

        // Check if this is a general doctor list query
        val isGeneralDoctorQuery = isGeneralDoctorQuery(query)

        // Use all doctors context for general queries, filtered for specific ones
        val context = if (isGeneralDoctorQuery) {
            buildContextWithAllDoctors(query, doctors)
        } else {
            buildContext(query, doctors)
        }

        val langInstruction = if (language == "hi") {
            """
            आप एक अस्पताल सहायक हैं। बहुत ही खुशदिल और सम्मानजनक तरीके से जवाब दीजिए।
            
            CRITICAL: आपको ONLY हिंदी (Hindi/Devanagari script) में ही जवाब देना है।
            NEVER respond in English when user asks in Hindi.
            यूज़र ने हिंदी में पूछा है, तो जवाब भी हिंदी में ही दीजिए।
            """.trimIndent()
        } else {
            """
            You are a cheerful and respectful hospital assistant. Respond warmly and with care.
            
            CRITICAL: You MUST respond ONLY in English.
            NEVER respond in Hindi when user asks in English.
            """.trimIndent()
        }

        val fallbackPhrase = if (language == "hi") {
            "मेरे पास वह विशिष्ट जानकारी नहीं है, लेकिन मैं अस्पताल में आपकी सहायता कर सकता हूँ।"
        } else {
            "I don't have that specific information, but I can help you at the hospital."
        }

        // SMART RETRIEVAL: Get only relevant Q&As from knowledge base
        val relevantQAs = HospitalKnowledgeBase.search(query, limit = 3)
        val knowledgeBaseContext = if (relevantQAs.isNotEmpty()) {
            val qaText = relevantQAs.joinToString("\n\n") { qa ->
                "Q: ${qa.question}\nA: ${qa.answer}"
            }
            """
            Relevant Hospital Information:
            $qaText
            """.trimIndent()
        } else {
            ""  // Empty if no relevant Q&As found
        }

        return """
        $langInstruction

        ${if (knowledgeBaseContext.isNotEmpty()) knowledgeBaseContext + "\n" else ""}
        
        ${if (historyContext.isNotEmpty()) historyContext + "\n" else ""}

        $context

        User: $query

        IMPORTANT INSTRUCTIONS (CRITICAL):
        1. LANGUAGE REQUIREMENT: Answer ONLY in ${if (language == "hi") "Hindi (हिंदी में जवाब दीजिए)" else "English"}. DO NOT mix languages.
        2. LANGUAGE DETECTION: User asked in ${if (language == "hi") "Hindi" else "English"}, so respond in ${if (language == "hi") "Hindi" else "English"} ONLY.
        3. Use ONLY the information provided above (Hospital Information, Previous Context, and Doctors list) to answer.
        4. For follow-up questions (like "repeat that", "who is he", "what did you say"), refer primarily to the "Previous Conversation Context" provided above.
        5. DO NOT make assumptions or notes about expertise not explicitly stated.
        6. If a doctor's specialty does not match the user's request, DO NOT include them in the answer.
        7. Answer clearly and cheerfully in 1-3 sentences.
        8. NEVER make up information or invent answers.
        9. For doctor lists, include their name, specialty, and cabin number ONLY.
        10. If you don't have the information provided, say "$fallbackPhrase"
        11. Be concise. Start your response directly with the answer.
        12. REMEMBER: ${if (language == "hi") "हिंदी में जवाब दें (Answer in Hindi)" else "Answer in English"}.
        """.trimIndent()
    }

    /**
     * Build streaming-optimized prompt (shorter for faster initial response)
     * Cheerful and respectful tone, NO follow-up suggestions
     */
    fun buildStreamingPrompt(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val context = buildContext(query, doctors)

        val langInstruction = if (language == "hi") {
            "आप एक खुशदिल और सम्मानजनक अस्पताल सहायक हैं।"
        } else {
            "You are a cheerful and respectful hospital assistant."
        }

        // Even more concise for streaming, no follow-ups
        return """
        $langInstruction

        $context

        User: $query

        Answer briefly and warmly.
        """.trimIndent()
    }

    /**
     * Generate fallback response when Ollama fails
     * Context-aware fallbacks based on detected intent
     */
    fun generateFallbackResponse(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val lowerQuery = query.lowercase()

        val response = when {
            // Doctor queries
            lowerQuery.contains("doctor") || lowerQuery.contains("specialist") -> {
                if (doctors.isNotEmpty()) {
                    if (language == "hi") {
                        "मैं आपको डॉक्टर ढूंढने में मदद कर सकता हूं। हमारे पास ${doctors.size} डॉक्टर हैं। डॉक्टर सूची देखें।"
                    } else {
                        "I can help you find doctors. We have ${doctors.size} doctors available. Please check the doctors section."
                    }
                } else {
                    if (language == "hi") "डॉक्टर सूची अभी लोड हो रही है। कृपया पुनः प्रयास करें।" else "Doctor list is loading. Please try again."
                }
            }

            // Navigation queries
            lowerQuery.contains("navigate") || lowerQuery.contains("where") || lowerQuery.contains("go to") -> {
                if (language == "hi") {
                    "मैं आपको नेविगेट करने में मदद कर सकता हूं। हमारे पास फार्मेसी, ICU, पैथोलॉजी लैब, बिलिंग काउंटर और OPD हैं।"
                } else {
                    "I can help you navigate. We have pharmacy, ICU, pathology lab, billing counter, and OPD areas."
                }
            }

            // Booking queries
            lowerQuery.contains("book") || lowerQuery.contains("appointment") -> {
                if (language == "hi") "मैं आपको अपॉइंटमेंट बुक करने में मदद कर सकता हूं। अपॉइंटमेंट सेक्शन देखें।" else "I can help you book an appointment. Please visit the appointment section."
            }

            // Default fallback
            else -> {
                if (language == "hi") "मैं आपकी मदद करने के लिए यहां हूं। कृपया मुख्य मेनू देखें।" else "I'm here to help. Please check the main menu for options."
            }
        }

        return response
    }
}
