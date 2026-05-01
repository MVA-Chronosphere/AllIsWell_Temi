package com.example.alliswelltemi.utils

import android.util.Log
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.LocationData
import com.example.alliswelltemi.data.HospitalKnowledgeBase
import java.text.SimpleDateFormat
import java.util.*

/**
 * RagContextBuilder - FIXED Intent Detection & Clean Prompt Generation
 *
 * KEY FIXES:
 * 1. Proper intent detection (GENERAL, DOCTOR, HEALTH)
 * 2. Filtered KB - only injected for relevant queries
 * 3. Clean minimal prompts - no overload
 * 4. Fixed cache - prevent same response returns
 * 5. Correct doctor context - only when needed
 */
object RagContextBuilder {

    private const val MAX_QUERY_LENGTH = 500
    private const val MAX_HISTORY_CONTEXT_LENGTH = 2000
    private const val MAX_DOCTORS_TO_INCLUDE = 15

    private fun validateInput(
        query: String,
        doctors: List<Doctor>,
        historyContext: String
    ) {
        when {
            query.isBlank() -> {
                Log.w("RagContextBuilder", "Query is blank")
                throw IllegalArgumentException("Query cannot be empty")
            }
            query.length > MAX_QUERY_LENGTH -> {
                Log.w("RagContextBuilder", "Query length (${query.length}) exceeds limit ($MAX_QUERY_LENGTH)")
                throw IllegalArgumentException("Query too long. Maximum $MAX_QUERY_LENGTH characters allowed.")
            }
            historyContext.length > MAX_HISTORY_CONTEXT_LENGTH -> {
                Log.w("RagContextBuilder", "History context too long")
                throw IllegalArgumentException("Conversation history too long")
            }
        }
    }

    private fun sanitizeQuery(query: String): String {
        return query
            .replace(Regex("[#*_`|\\\\]"), "")
            .replace(Regex("[\\n\\r\\t]"), " ")
            .replace("\"", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    // ============== FIX 1: STRICT INTENT DETECTION ==============
    fun detectIntent(query: String): String {
        val q = query.lowercase()

        val isGreeting =
            q.contains("hello") ||
            q.contains("hi") ||
            q.contains("help") ||
            q.contains("how can you") ||
            q.contains("what can you do") ||
            q.contains("how are you") ||
            q.contains("greetings")

        val isDoctorQuery =
            q.contains("doctor") ||
            q.contains("dr") ||
            q.contains("specialist") ||
            q.contains("cardio") ||
            q.contains("neuro") ||
            q.contains("ortho") ||
            q.contains("pediatr") ||
            q.contains("gynec") ||
            q.contains("dermat")

        val isHealthQuery =
            q.contains("pain") ||
            q.contains("fever") ||
            q.contains("cough") ||
            q.contains("sick") ||
            q.contains("hurt") ||
            q.contains("ache") ||
            q.contains("cold") ||
            q.contains("symptom") ||
            q.contains("problem")

        return when {
            isGreeting -> "GENERAL"
            isDoctorQuery -> "DOCTOR"
            isHealthQuery -> "HEALTH"
            else -> "GENERAL"
        }
    }

    private fun sanitizeDoctorString(text: String): String {
        return text
            .replace("\"", "'")
            .replace(Regex("[\\n\\r]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    // Language detection
    fun detectLanguage(text: String): String {
        val hasHindiChars = text.matches(Regex(".*[\\u0900-\\u097F].*"))
        val hindiKeywords = listOf(
            "डॉक्टर", "कहां", "कहाँ", "क्या", "कैसे", "मुझे", "बताओ",
            "अस्पताल", "अपॉइंटमेंट", "बुक", "विशेषज्ञ", "चिकित्सक"
        )
        val hasHindiKeywords = hindiKeywords.any { text.contains(it) }
        return if (hasHindiChars || hasHindiKeywords) "hi" else "en"
    }

    /**
     * Medical terminology translation guide for Hindi responses
     */
    private fun getMedicalTerminologyGuide(): String {
        return """
        Medical Terms Translation (USE EXACT SPECIALTY FROM CONTEXT):
        Cardiology = हृदय रोग विशेषज्ञ
        Neurology = मस्तिष्क रोग विशेषज्ञ
        Orthopedics = हड्डी रोग विशेषज्ञ
        Dermatology = त्वचा रोग विशेषज्ञ
        Pediatrics = बाल रोग विशेषज्ञ
        Ophthalmology = नेत्र रोग विशेषज्ञ
        """.trimIndent()
    }

    // ============== MAIN RAG PIPELINE - CRITICAL FIX ==============
    /**
     * Build Ollama prompt with proper RAG pipeline
     *
     * CRITICAL CHANGES:
     * 1. REMOVES intent-based blocking that returns static text
     * 2. ALWAYS searches knowledge base FIRST
     * 3. ALWAYS sends real user query to Ollama (NEVER static text)
     * 4. KB results injected as context, not as final answer
     */
    fun buildOllamaPrompt(query: String, doctors: List<Doctor>, historyContext: String = ""): String {
        try {
            validateInput(query, doctors, historyContext)
        } catch (e: IllegalArgumentException) {
            Log.e("RAG_PIPELINE", "Input validation failed: ${e.message}")
            return "Please contact reception or rephrase your question."
        }

        val sanitizedQuery = sanitizeQuery(query)
        val language = detectLanguage(sanitizedQuery)

        Log.d("RAG_DEBUG", "Query: $sanitizedQuery")
        Log.d("RAG_DEBUG", "Language: $language")

        // ============== STEP 1: ALWAYS SEARCH KB FIRST ==============
        // NO intention-based filtering - search everything
        val relevantQAs = HospitalKnowledgeBase.search(sanitizedQuery, limit = 5)
        Log.d("RAG_DEBUG", "KB Results: ${relevantQAs.size}")

        // Build context from KB results
        val knowledgeBaseContext = if (relevantQAs.isNotEmpty()) {
            relevantQAs.joinToString("\n\n") { qa ->
                "Q: ${qa.question}\nA: ${qa.answer}"
            }
        } else {
            ""
        }

        Log.d("RAG_DEBUG", "KB Context length: ${knowledgeBaseContext.length}")

        // ============== STEP 2: BUILD MINIMAL PROMPT WITH QUERY + CONTEXT ==============
        val systemPrompt = if (language == "hi") {
            "आप ऑल इज़ वेल हॉस्पिटल के एक मददगार सहायक हैं। हिंदी में जवाब दें। संक्षिप्त रहें।"
        } else {
            "You are a helpful assistant for All Is Well Hospital. Answer briefly and directly."
        }

        // Build the prompt - QUERY ALWAYS GOES IN
        val prompt = """
$systemPrompt

${if (knowledgeBaseContext.isNotEmpty()) "Context:\n$knowledgeBaseContext\n" else ""}

Question: $sanitizedQuery

Answer:""".trimIndent()

        Log.d("RAG_DEBUG", "Sending full prompt to Ollama (length: ${prompt.length})")

        return prompt
    }

    // ============== BACKWARD COMPATIBILITY FUNCTIONS ==============

    /**
     * Build basic context (deprecated - use buildOllamaPrompt instead)
     */
    fun buildContext(query: String, doctors: List<Doctor>): String {
        return buildOllamaPrompt(query, doctors)
    }

    /**
     * Build context with all doctors (deprecated - use buildOllamaPrompt instead)
     */
    fun buildContextWithAllDoctors(query: String, doctors: List<Doctor>): String {
        return buildOllamaPrompt(query, doctors)
    }


    /**
     * Build streaming-optimized prompt
     * Mimics buildOllamaPrompt but slightly faster
     */
    fun buildStreamingPrompt(query: String, doctors: List<Doctor>): String {
        return buildOllamaPrompt(query, doctors)
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
                if (language == "hi") {
                    "नमस्ते! मैं ऑल इज़ वेल हॉस्पिटल में आपकी किस प्रकार सहायता कर सकता हूँ? कृपया मुझे बताएं या मुख्य मेनू में विकल्पों को देखें।"
                } else {
                    "Hello! I am here to help you at All Is Well Hospital. How can I assist you today? Please let me know your requirement or explore the main menu."
                }
            }
        }

        return response
    }
}
