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
     */
    fun detectLanguage(text: String): String {
        return if (text.matches(Regex(".*[\\u0900-\\u097F].*"))) "hi" else "en"
    }

    /**
     * Build comprehensive RAG context with hospital data
     * Optimized for Ollama: limit to 2-3 most relevant items, keep under 400 chars
     */
    fun buildContext(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val lowerQuery = query.lowercase()

        // Find relevant doctors (max 2-3 for performance)
        val relevantDoctors = doctors.filter { doctor ->
            val doctorName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
            val department = doctor.department.lowercase()
            val specialty = doctor.specialization?.lowercase() ?: ""

            lowerQuery.contains(doctorName) ||
            lowerQuery.contains(department) ||
            lowerQuery.contains(specialty) ||
            lowerQuery.contains("doctor") ||
            lowerQuery.contains("specialist")
        }.take(2) // Limit for performance

        // Build doctor context
        val doctorContext = if (relevantDoctors.isNotEmpty()) {
            relevantDoctors.joinToString("\n") { doctor ->
                val name = if (doctor.name.startsWith("Dr", ignoreCase = true))
                    doctor.name else "Dr. ${doctor.name}"
                "${name} - ${doctor.department}, ${doctor.yearsOfExperience}y exp, Cabin ${doctor.cabin}"
            }
        } else {
            // Fallback: show 2 general doctors
            doctors.take(2).joinToString("\n") { doctor ->
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
     * Build optimized Ollama prompt with smart knowledge base retrieval
     * Uses RAG (Retrieval Augmented Generation) to fetch only relevant Q&As
     * Language-aware, concise, cheerful and respectful tone, NO follow-up suggestions
     */
    fun buildOllamaPrompt(query: String, doctors: List<Doctor>): String {
        val language = detectLanguage(query)
        val context = buildContext(query, doctors)

        val langInstruction = if (language == "hi") {
            "आप एक अस्पताल सहायक हैं। बहुत ही खुशदिल और सम्मानजनक तरीके से जवाब दीजिए।"
        } else {
            "You are a cheerful and respectful hospital assistant. Respond warmly and with care."
        }

        // SMART RETRIEVAL: Get only relevant Q&As from knowledge base (max 2)
        // This avoids sending 300+ Q&As in every prompt - only relevant ones
        val relevantQAs = HospitalKnowledgeBase.search(query, limit = 2)
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

        $context

        User: $query

        IMPORTANT INSTRUCTIONS:
        1. Use ONLY the information provided above to answer
        2. If the exact answer is in the "Relevant Hospital Information", use that directly
        3. Answer clearly and cheerfully in 1-2 sentences only
        4. Be warm, respectful, and helpful
        5. NEVER make up information or invent answers
        6. If you don't have the information provided, say "I don't have that specific information, but I can help you at the hospital."
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
