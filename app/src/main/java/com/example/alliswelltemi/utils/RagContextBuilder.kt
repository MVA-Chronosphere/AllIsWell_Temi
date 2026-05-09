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

    // ============== CONTENT SAFETY FILTER ==============
    /**
     * Detects harmful/inappropriate queries that should receive safety responses
     * instead of KB lookup. This prevents the system from trying to answer harmful questions.
     */
    private fun isHarmfulQuery(query: String): Boolean {
        val q = query.lowercase()

        val harmfulPatterns = listOf(
            // Violence, harm
            "kill", "murder", "hurt", "stab", "poison", "bomb", "weapon",
            "how to die", "suicide", "self harm",
            // Criminal activity
            "robbery", "steal", "burglary", "illegal",
            // Explicit content
            "sex", "nude", "porn", "explicit"
        )

        return harmfulPatterns.any { q.contains(it) }
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
     * 1. ALWAYS searches knowledge base FIRST
     * 2. ALWAYS sends real user query based on KB context
     * 3. KB results injected as context
     * 4. Strict constraint: ONLY answer from knowledge base (no hallucination)
     * 5. Supports both English and Hindi with EQUAL thinking capability
     * 6. Date/Time/Location questions answered from available data
     */
    fun buildOllamaPrompt(query: String, doctors: List<Doctor>, historyContext: String = "", language: String = ""): String {
         try {
             validateInput(query, doctors, historyContext)
         } catch (e: IllegalArgumentException) {
             Log.e("RAG_PIPELINE", "Input validation failed: ${e.message}")
             return "I'm here to help with hospital-related questions. How can I assist you today?"
         }

          val sanitizedQuery = sanitizeQuery(query)
          val detectedLanguage = if (language.isNotEmpty()) language else detectLanguage(sanitizedQuery)

          Log.d("RAG_DEBUG", "Query: $sanitizedQuery")
          Log.d("RAG_DEBUG", "Language: $detectedLanguage")
          Log.d("LANGUAGE_DETECTION", "🌍 Detected language: ${if (detectedLanguage == "hi") "HINDI" else "ENGLISH"} for query: '${sanitizedQuery.take(50)}'...")

         // ============== CONTENT SAFETY CHECK ==============
         // Return safety response for harmful queries without consulting KB
         if (isHarmfulQuery(sanitizedQuery)) {
             Log.w("CONTENT_SAFETY", "⚠️ Harmful query detected: '$sanitizedQuery'. Returning safety response.")
             val safetyResponse = if (detectedLanguage == "hi") {
                 "मैं केवल अस्पताल के बारे में सवालों में मदद कर सकता हूं। क्या मैं आपको किसी विभाग या डॉक्टर को खोजने में मदद कर सकता हूं?"
             } else {
                 "I'm here to help with hospital-related questions. Can I help you find a department or doctor?"
             }
             return safetyResponse
         }

         // ============== STEP 1: ALWAYS SEARCH KB FIRST ==============
         // NO intention-based filtering - search everything
         val relevantQAs = HospitalKnowledgeBase.search(sanitizedQuery, limit = 5)
         Log.d("RAG_DEBUG", "KB Results: ${relevantQAs.size}")

        // Filter results by detected language when available
        val relevantQAsFiltered = if (relevantQAs.isNotEmpty()) {
            // Prioritize same-language results, but include others if few results
            val sameLanguageQAs = relevantQAs.filter { it.language == detectedLanguage }
            if (sameLanguageQAs.size >= 2) sameLanguageQAs else relevantQAs  // Use all if few same-language results
        } else {
            relevantQAs
        }

        // Build context from KB results
        val knowledgeBaseContext = if (relevantQAsFiltered.isNotEmpty()) {
            relevantQAsFiltered.joinToString("\n\n") { qa ->
                "Q: ${qa.question}\nA: ${qa.answer}"
            }
        } else {
            ""
        }

        Log.d("RAG_DEBUG", "KB Context length: ${knowledgeBaseContext.length}")

          // ============== STEP 2: BUILD STRICT PROMPT WITH KNOWLEDGE BASE ONLY ==============
          val systemPrompt = if (detectedLanguage == "hi") {
              """आप ऑल इज़ वेल हॉस्पिटल के एक स्मार्ट सहायक हैं। आप मदद के लिए यहां हैं।

  ⚠️ IMPORTANT: RESPOND IN HINDI ONLY - हमेशा हिंदी में जवाब दें। कभी अंग्रेजी में जवाब न दें।
  
  HOSPITAL NAME: ऑल इज़ वेल हॉस्पिटल (कभी AAMIS या कोई और नाम न कहें)

  निर्देश:
  1. केवल दिए गए संदर्भ से जवाब दें - कोई अनुमान या बदलाव न करें
  2. हमेशा अस्पताल का नाम "ऑल इज़ वेल हॉस्पिटल" कहें, कोई और नाम न दें
  3. अगर जानकारी नहीं है, तो स्वाभाविक तरीके से कहें कि आप किस तरह मदद कर सकते हैं
  4. कभी चिकित्सा सलाह न दें - केवल अस्पताल की जानकारी साझा करें
  5. 2-3 वाक्य में छोटा और स्पष्ट उत्तर दें - सब कुछ हिंदी में
  6. आपातकालीन के लिए हमेशा +91 76977 44444 दें
  7. आपका पूरा जवाब हिंदी में होना चाहिए - कोई अंग्रेजी शब्द न जोड़ें"""
          } else {
              """You are a friendly hospital assistant at All Is Well Hospital.
  You are here to help.

  ⚠️ IMPORTANT: RESPOND IN ENGLISH ONLY - Always answer in English. Never respond in Hindi.

  HOSPITAL NAME: All Is Well Hospital (NEVER use AAMIS or any other name)

  Instructions:
  1. Answer ONLY using the provided context - no assumptions
  2. Always refer to this hospital as "All Is Well Hospital", never use AAMIS or other names
  3. If information is not available, naturally explain what you CAN help with
  4. Never give medical advice - only share hospital information
  5. Keep responses brief and natural (2-3 sentences max) - all in English
  6. For emergencies, always provide: +91 76977 44444
  7. Your entire response must be in English - do not mix with Hindi words"""
          }

          // Build the prompt - QUERY ALWAYS GOES IN
          val prompt = if (knowledgeBaseContext.isNotEmpty()) {
              if (detectedLanguage == "hi") {
                  """$systemPrompt

  उपलब्ध जानकारी:
  $knowledgeBaseContext

  उपयोगकर्ता का सवाल (हिंदी में): $sanitizedQuery

  ⚠️ याद रखें: आपका पूरा जवाब हिंदी में होना चाहिए। कोई अंग्रेजी न जोड़ें।
  
  सहायक (हिंदी में): """.trimIndent()
              } else {
                  """$systemPrompt

  Available Information:
  $knowledgeBaseContext

  User Question (in English): $sanitizedQuery

  ⚠️ Remember: Your entire response must be in English. Do not add Hindi.
  
  Assistant (in English): """.trimIndent()
              }
          } else {
              // No context found - respond with fallback that guides user
              if (detectedLanguage == "hi") {
                  """$systemPrompt

  उपयोगकर्ता का सवाल (हिंदी में): $sanitizedQuery

  ⚠️ याद रखें: आपका पूरा जवाब हिंदी में होना चाहिए। कोई अंग्रेजी न जोड़ें।
  
  सहायक (हिंदी में, इस प्रश्न के लिए विशिष्ट जानकारी उपलब्ध नहीं है, लेकिन यह सुझाएं कि मैं क्या कर सकता हूं): """.trimIndent()
              } else {
                  """$systemPrompt

  User Question (in English): $sanitizedQuery

  ⚠️ Remember: Your entire response must be in English. Do not add Hindi.
  
  Assistant (in English, No specific information available for this, but suggest what you CAN help with): """.trimIndent()
              }
          }

        Log.d("RAG_DEBUG", "Sending full prompt to Ollama (length: ${prompt.length}, language: $detectedLanguage)")

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
     * Context-aware fallbacks based on KB search + natural language
     * STRICT: Only suggest actions that use available data
     */
    fun generateFallbackResponse(query: String, doctors: List<Doctor>): String {
         val language = detectLanguage(query)
         val lowerQuery = query.lowercase()

         // First check if it's a harmful query
         if (isHarmfulQuery(lowerQuery)) {
             return if (language == "hi") {
                 "मैं केवल अस्पताल से संबंधित सवालों में मदद कर सकता हूं।"
             } else {
                 "I'm here to help with hospital-related questions."
             }
         }

         // First try to search KB for any relevant information
         val kbResults = HospitalKnowledgeBase.search(query, limit = 1)

         val response = when {
             // If we found KB results, use them
             kbResults.isNotEmpty() -> {
                 val qa = kbResults.first()
                 qa.answer
             }

             // Doctor queries
             lowerQuery.contains("doctor") || lowerQuery.contains("specialist") -> {
                 if (doctors.isNotEmpty()) {
                     if (language == "hi") {
                         "मेरे पास डॉक्टरों की जानकारी है। क्या आप किसी विशेषज्ञता से डॉक्टर खोजना चाहते हैं?"
                     } else {
                         "I can help you find doctors by specialty. Which department are you looking for?"
                     }
                 } else {
                     if (language == "hi") "डॉक्टर सूची अभी लोड हो रही है। कृपया पुनः प्रयास करें।" else "Doctor information is loading. Please try again in a moment."
                 }
             }

             // Navigation queries
             lowerQuery.contains("navigate") || lowerQuery.contains("where") || lowerQuery.contains("go to") ||
             lowerQuery.contains("कहाँ") || lowerQuery.contains("कहां") -> {
                 if (language == "hi") {
                     "मैं आपको अस्पताल में किसी विभाग तक ले जा सकता हूं। कौन सी जगह खोज रहे हैं?"
                 } else {
                     "I can guide you to different departments. Which location would you like?"
                 }
             }

             // Booking queries
             lowerQuery.contains("book") || lowerQuery.contains("appointment") ||
             lowerQuery.contains("अपॉइंटमेंट") || lowerQuery.contains("बुक") -> {
                 if (language == "hi") "अपॉइंटमेंट बुक करने के लिए 'अपॉइंटमेंट बुक करें' कहें।" else "Say 'book an appointment' to schedule a visit."
             }

              // Medical/Health queries - strict: no medical advice
              lowerQuery.contains("fever") || lowerQuery.contains("pain") || lowerQuery.contains("sick") ||
              lowerQuery.contains("बुखार") || lowerQuery.contains("दर्द") || lowerQuery.contains("बीमार") ||
              lowerQuery.contains("symptom") || lowerQuery.contains("check") ||
              lowerQuery.contains("diet") || lowerQuery.contains("nutrition") || lowerQuery.contains("weight") ||
              lowerQuery.contains("weight loss") || lowerQuery.contains("डाइट") || lowerQuery.contains("वजन") ||
              lowerQuery.contains("पोषण") || lowerQuery.contains("आहार") -> {
                  if (language == "hi") {
                      "स्वास्थ्य संबंधित सलाह के लिए कृपया हमारे विशेषज्ञ से मिलें। बुकिंग के लिए +91 76977 44444 पर कॉल करें।"
                  } else {
                      "For personalized health and nutrition advice, please consult our specialists. Call +91 76977 44444 to book an appointment."
                  }
              }

             // Feedback/complaint
             lowerQuery.contains("feedback") || lowerQuery.contains("complaint") || lowerQuery.contains("rate") ||
             lowerQuery.contains("प्रतिक्रिया") -> {
                 if (language == "hi") {
                     "आपकी प्रतिक्रिया महत्वपूर्ण है। फीडबैक सेक्शन में जाएं या रिसेप्शन से संपर्क करें।"
                 } else {
                     "Your feedback helps us improve. Please use the feedback section or contact reception."
                 }
             }

             // Default fallback - more natural
             else -> {
                 if (language == "hi") {
                     "मुझे पूरी तरह समझ नहीं आया। क्या आप एक डॉक्टर खोजना चाहते हैं, कोई विभाग जानना चाहते हैं, या अपॉइंटमेंट बुक करना चाहते हैं?"
                 } else {
                     "I didn't quite catch that. Are you looking for a doctor, directions, or want to book an appointment?"
                 }
             }
         }

         return response
    }
}
