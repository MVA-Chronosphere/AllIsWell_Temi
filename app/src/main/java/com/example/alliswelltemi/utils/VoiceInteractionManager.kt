package com.example.alliswelltemi.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.alliswelltemi.network.OllamaClient
import com.example.alliswelltemi.network.OllamaRequest
import com.example.alliswelltemi.network.VoiceResult
import com.example.alliswelltemi.network.VoiceState
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Voice Interaction Manager - Production-grade voice pipeline
 *
 * Pipeline Flow:
 * 1. User speaks → Android SpeechRecognizer captures audio
 * 2. Speech converted to text → RecognitionListener callback
 * 3. Text sent to Ollama LLM → Local processing (no external API)
 * 4. LLM response received → Temi speaks response via TTS
 *
 * Design:
 * - Non-blocking: Uses coroutines for network calls
 * - Safe: Null-checks for Robot instance
 * - Clean: Separates concerns (speech, LLM, TTS)
 * - Monitorable: Logs all pipeline stages
 */
class VoiceInteractionManager(
    private val context: Context,
    private val robot: Robot?,
    private val coroutineScope: CoroutineScope
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    // Conversation context - maintains chat history
    private val conversationContext = ConversationContext(
        maxHistoryItems = 5,    // Remember last 5 Q&A pairs
        maxContextLength = 2000 // Max context length
    )

    // State callbacks
    private var onStateChanged: ((VoiceState) -> Unit)? = null
    private var onVoiceResultReady: ((VoiceResult) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    init {
        initializeSpeechRecognizer()
    }

    /**
     * Initialize Android SpeechRecognizer
     */
    private fun initializeSpeechRecognizer() {
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Log.e(TAG, "Speech recognition not available on this device")
                onError?.invoke("Speech recognition unavailable")
                return
            }

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognitionListener)
            Log.d(TAG, "SpeechRecognizer initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing SpeechRecognizer", e)
            onError?.invoke("Failed to initialize speech recognition")
        }
    }

    /**
     * Start listening for user speech
     */
    fun startListening() {
        if (isListening) {
            Log.d(TAG, "Already listening - ignoring duplicate request")
            return
        }

        try {
            if (speechRecognizer == null) {
                initializeSpeechRecognizer()
            }

            isListening = true
            updateState(VoiceState.LISTENING)

            val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(android.speech.RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500)
            }

            speechRecognizer?.startListening(intent)
            Log.d(TAG, "SpeechRecognizer started - listening for speech")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting listening", e)
            isListening = false
            updateState(VoiceState.ERROR)
            onError?.invoke("Failed to start listening: ${e.message}")
        }
    }

    /**
     * Stop listening (manual cancel)
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            isListening = false
            Log.d(TAG, "SpeechRecognizer stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping listening", e)
        }
    }

    /**
     * Set state change callback
     */
    fun setOnStateChanged(callback: (VoiceState) -> Unit) {
        this.onStateChanged = callback
    }

    /**
     * Set result ready callback
     */
    fun setOnVoiceResultReady(callback: (VoiceResult) -> Unit) {
        this.onVoiceResultReady = callback
    }

    /**
     * Set error callback
     */
    fun setOnError(callback: (String) -> Unit) {
        this.onError = callback
    }

    /**
     * Update voice state and notify listeners
     */
    private fun updateState(newState: VoiceState) {
        Log.d(TAG, "Voice state changed: $newState")
        onStateChanged?.invoke(newState)
    }

    /**
     * Speech recognition listener - handles speech to text conversion
     */
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "onReadyForSpeech")
            updateState(VoiceState.LISTENING)
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech - user started speaking")
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Audio level changes - can be used for visual feedback
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Log.d(TAG, "onBufferReceived - ${buffer?.size ?: 0} bytes")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech - user finished speaking")
        }

        override fun onError(error: Int) {
            isListening = false
            val errorMessage = getErrorMessage(error)
            Log.e(TAG, "Speech recognition error: $errorMessage (code: $error)")
            updateState(VoiceState.ERROR)
            onError?.invoke("Speech recognition error: $errorMessage")
        }

        override fun onResults(results: Bundle?) {
            isListening = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val spokenText = matches?.firstOrNull()?.trim() ?: ""

            if (spokenText.isBlank()) {
                Log.w(TAG, "onResults: No speech detected (empty result)")
                updateState(VoiceState.ERROR)
                onError?.invoke("No speech detected. Please try again.")
                return
            }

            Log.d(TAG, "Speech recognized: '$spokenText'")
            Log.d(TAG, "Processing speech with Ollama LLM...")

            // Process speech with Ollama
            processSpeechWithOllama(spokenText)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            Log.d(TAG, "onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.d(TAG, "onEvent: eventType=$eventType")
        }
    }

    /**
     * Send spoken text to Ollama LLM and get response
     * Runs on IO dispatcher to avoid blocking main thread
     * Saves conversation turn for context in next question
     */
    private fun processSpeechWithOllama(spokenText: String) {
        updateState(VoiceState.PROCESSING)

        val startTime = System.currentTimeMillis()

        coroutineScope.launch {
            try {
                // Step 1: Build hospital context prompt (includes previous conversation)
                val doctors = withContext(Dispatchers.Main) {
                    // Try to get doctors from a shared source or pass them in
                    // For now, using empty list or we should update this class to accept doctors
                    emptyList<com.example.alliswelltemi.data.Doctor>()
                }
                
                val hospitalContextPrompt = RagContextBuilder.buildOllamaPrompt(
                    query = spokenText,
                    doctors = doctors,
                    historyContext = conversationContext.getContextString()
                )
                Log.d(TAG, "Built hospital context prompt (${hospitalContextPrompt.length} chars)")

                updateState(VoiceState.THINKING)

                // Step 2: Call Ollama LLM on IO dispatcher
                val ollamaResponse = withContext(Dispatchers.IO) {
                    Log.d(TAG, "Sending prompt to Ollama LLM...")
                    try {
                        OllamaClient.api.generate(
                            OllamaRequest(
                                model = "llama3:8b",
                                prompt = hospitalContextPrompt,
                                stream = false,
                                temperature = 0.7
                            )
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Ollama API error: ${e.message}", e)
                        throw e
                    }
                }

                val processingTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Ollama response received in ${processingTime}ms")
                Log.d(TAG, "Response length: ${ollamaResponse.response.length} chars")

                // Step 3: Speak response via Temi TTS
                val cleanedResponse = cleanResponseForSpeech(ollamaResponse.response)
                Log.d(TAG, "Cleaned response for TTS (${cleanedResponse.length} chars)")

                // Step 4: Save to conversation context for next turn
                conversationContext.addTurn(spokenText, cleanedResponse)
                Log.d(TAG, "Saved turn to conversation context (${conversationContext.getTurnCount()} total)")

                updateState(VoiceState.SPEAKING)
                speakResponse(cleanedResponse)

                // Step 5: Notify result ready
                val result = VoiceResult(
                    spokenText = spokenText,
                    llmResponse = cleanedResponse,
                    processingTimeMs = processingTime,
                    state = VoiceState.SPEAKING
                )
                onVoiceResultReady?.invoke(result)

                Log.d(TAG, "Voice pipeline completed successfully")

            } catch (e: Exception) {
                Log.e(TAG, "Error processing speech: ${e.message}", e)
                updateState(VoiceState.ERROR)

                val fallbackResponse = generateFallbackResponse(spokenText)
                Log.d(TAG, "Using fallback response: $fallbackResponse")

                // Save fallback response to context too
                conversationContext.addTurn(spokenText, fallbackResponse)

                speakResponse(fallbackResponse)
                onError?.invoke("Failed to process: ${e.message}")
            }
        }
    }

    /**
     * Build hospital-specific context prompt for Ollama
     * Includes previous conversation context to maintain conversation continuity
     */
    private fun buildHospitalContextPrompt(userInput: String): String {
        // Get previous conversation context
        val conversationContextStr = conversationContext.getContextString()

        // Build system prompt with conversation history
        val systemPrompt = """
            You are a helpful hospital assistant robot named Temi. You help patients navigate the hospital.
            
            Hospital Services:
            - OPD (Out-Patient Department): General consultations
            - Pharmacy: Medicine dispensary
            - Pathology Lab: Medical testing and diagnostics
            - Billing Counter: Payment and financial inquiries
            - ICU: Intensive care unit
            
            Guidelines:
            - Be friendly and professional
            - Keep responses brief (1-2 sentences max for speech)
            - Offer to help navigate or book appointments
            - If unsure, suggest visiting the information desk
            - Reference previous conversations if relevant
        """.trimIndent()

        // Add conversation history if available
        val contextWithHistory = if (conversationContextStr.isNotEmpty()) {
            """
            $systemPrompt
            
            $conversationContextStr
            
            Current Question: "$userInput"
            
            Respond as Temi hospital assistant. Reference previous context if relevant (2-3 sentences max):
            """.trimIndent()
        } else {
            """
            $systemPrompt
            
            Patient Query: "$userInput"
            
            Respond as Temi hospital assistant (2-3 sentences max):
            """.trimIndent()
        }

        Log.d(TAG, "Built prompt with ${conversationContext.getTurnCount()} previous turns")
        return contextWithHistory
    }

    /**
     * Clean LLM response for text-to-speech
     * Removes markdown, special characters, and formats for natural reading
     */
    private fun cleanResponseForSpeech(text: String): String {
        return text
            .replace(Regex("#+\\s+"), "")           // Remove markdown headers
            .replace(Regex("\\*\\*"), "")            // Remove bold markers
            .replace(Regex("\\*"), "")               // Remove italic markers
            .replace(Regex("\\[([^]]+)]\\([^)]+\\)"), "$1")  // Convert markdown links
            .replace(Regex("[{}\\[\\]_|`~^]"), "")  // Remove special chars
            .replace(Regex("\\n+"), ". ")            // Convert newlines to period + space
            .replace(Regex("\\s+"), " ")             // Normalize whitespace
            .replace(":", ". ")                       // Replace colons
            .trim()
            .take(500)  // Cap at 500 chars for reasonable TTS duration
    }

    /**
     * Speak response using Temi TTS
     */
    private fun speakResponse(text: String) {
        try {
            if (robot == null) {
                Log.w(TAG, "Robot not available for TTS")
                onError?.invoke("Robot not ready for speech output")
                return
            }

            if (text.isBlank()) {
                Log.w(TAG, "Skipping TTS - response text is blank")
                return
            }

            Log.d(TAG, "Speaking response: '$text' (${text.length} chars)")
            robot.speak(TtsRequest.create(text, isShowOnConversationLayer = false))
            updateState(VoiceState.IDLE)
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking response", e)
            onError?.invoke("TTS error: ${e.message}")
        }
    }

    /**
     * Generate fallback response when Ollama fails
     */
    private fun generateFallbackResponse(userInput: String): String {
        return when {
            userInput.contains("doctor", ignoreCase = true) ||
            userInput.contains("physician", ignoreCase = true) ||
            userInput.contains("specialist", ignoreCase = true) ->
                "I can help you find doctors. Please visit the doctors section to view available specialists."

            userInput.contains("pharmacy", ignoreCase = true) ||
            userInput.contains("medicine", ignoreCase = true) ->
                "Our pharmacy is located on the ground floor. How can I help navigate you there?"

            userInput.contains("lab", ignoreCase = true) ||
            userInput.contains("test", ignoreCase = true) ->
                "The pathology lab can be found in the diagnostic center. Would you like me to take you there?"

            userInput.contains("appointment", ignoreCase = true) ||
            userInput.contains("book", ignoreCase = true) ->
                "I can help you book an appointment. Please visit the appointment booking section."

            userInput.contains("navigate", ignoreCase = true) ||
            userInput.contains("where", ignoreCase = true) ||
            userInput.contains("take me", ignoreCase = true) ->
                "I can help you navigate. We have the OPD, pharmacy, pathology lab, billing counter, and ICU. Where would you like to go?"

            else ->
                "Sorry, I didn't quite understand that. Could you please repeat your request?"
        }
    }

    /**
     * Get user-friendly error message from SpeechRecognizer error code
     */
    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
            else -> "Unknown error (code: $errorCode)"
        }
    }

    /**
     * Get conversation context object (for UI integration or debugging)
     */
    fun getConversationContext(): ConversationContext = conversationContext

    /**
     * Clear conversation history (call when returning to main screen or on timeout)
     */
    fun clearConversationHistory() {
        conversationContext.clearHistory()
        Log.d(TAG, "Conversation history cleared")
    }

    /**
     * Get number of turns in current conversation
     */
    fun getConversationTurnCount(): Int = conversationContext.getTurnCount()

    /**
     * Get formatted conversation history for display
     */
    fun getFormattedConversationHistory(): String = conversationContext.getFormattedHistory()

    /**
     * Check if conversation session has expired
     */
    fun isConversationSessionExpired(): Boolean = conversationContext.isSessionExpired()

    /**
     * Clean up resources
     */
    fun release() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            Log.d(TAG, "VoiceInteractionManager released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing VoiceInteractionManager", e)
        }
    }

    companion object {
        private const val TAG = "VoiceInteraction"
    }
}

