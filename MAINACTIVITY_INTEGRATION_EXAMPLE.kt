package com.example.alliswelltemi

import android.util.Log
import android.speech.SpeechRecognizer
import com.example.alliswelltemi.network.VoiceState
import com.example.alliswelltemi.utils.VoiceInteractionManager
import com.robotemi.sdk.Robot

/**
 * MainActivityOllamaIntegration.kt
 *
 * This file shows how to integrate VoiceInteractionManager into MainActivity
 * This is a code reference file - copy the relevant sections into your MainActivity.kt
 *
 * DO NOT add this as a separate file - merge these snippets into MainActivity.kt
 */

/*
 * STEP 1: Add this property to MainActivity class
 * ================================================
 */
// private var voiceInteractionManager: VoiceInteractionManager? = null
// private val currentVoiceState = mutableStateOf(VoiceState.IDLE)

/*
 * STEP 2: Add this code to onCreate() after Robot.getInstance().addOnRobotReadyListener
 * ========================================================================================
 */
// Robot.getInstance().addOnRobotReadyListener { isReady ->
//     if (isReady) {
//         val robot = robotState.value
//         if (robot != null) {
//             android.util.Log.d("TemiMain", "Robot ready - initializing voice manager")
//             initializeVoiceManager(robot)
//         }
//     }
// }

/*
 * STEP 3: Add this function to MainActivity class
 * =================================================
 */
/*
private fun initializeVoiceManager(robot: Robot) {
    try {
        voiceInteractionManager = VoiceInteractionManager(
            context = this,
            robot = robot,
            coroutineScope = lifecycleScope
        )

        // Set up state change callback - update UI based on voice state
        voiceInteractionManager?.setOnStateChanged { state ->
            android.util.Log.d("VoiceUI", "Voice state changed to: $state")
            currentVoiceState.value = state

            // Here you can update UI to show:
            // - LISTENING: Show "Listening..." indicator and pulse animation
            // - PROCESSING: Show "Sending to server..." spinner
            // - THINKING: Show "Thinking..." with animated dots
            // - SPEAKING: Show "Speaking..." indicator
            // - ERROR: Show error message in red
        }

        // Set up error callback
        voiceInteractionManager?.setOnError { errorMessage ->
            android.util.Log.e("VoiceError", errorMessage)
            currentVoiceState.value = VoiceState.ERROR

            // Show error toast or snackbar to user
            android.widget.Toast.makeText(
                this,
                errorMessage,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        // Set up result callback
        voiceInteractionManager?.setOnVoiceResultReady { result ->
            android.util.Log.d("VoiceResult", "User: ${result.spokenText}")
            android.util.Log.d("VoiceResult", "Response: ${result.llmResponse}")
            android.util.Log.d("VoiceResult", "Processing: ${result.processingTimeMs}ms")

            // Parse the LLM response for navigation intent
            parseAndHandleVoiceResponse(result.spokenText, result.llmResponse)

            // Reset to IDLE after speaking completes
            handler.postDelayed({
                currentVoiceState.value = VoiceState.IDLE
            }, 2000)
        }

        android.util.Log.d("TemiMain", "Voice manager initialized successfully")
    } catch (e: Exception) {
        android.util.Log.e("TemiMain", "Failed to initialize voice manager: ${e.message}", e)
    }
}

private fun parseAndHandleVoiceResponse(userInput: String, llmResponse: String) {
    // Parse the spoken input and LLM response to determine action
    val lowerInput = userInput.lowercase()

    when {
        lowerInput.contains("doctor") || lowerInput.contains("specialist") ->
            currentScreen.value = "doctors"

        lowerInput.contains("appointment") || lowerInput.contains("book") ->
            currentScreen.value = "appointment"

        lowerInput.contains("navigate") || lowerInput.contains("pharmacy") ||
        lowerInput.contains("icu") || lowerInput.contains("lab") ->
            currentScreen.value = "navigation"

        lowerInput.contains("feedback") || lowerInput.contains("rate") ->
            currentScreen.value = "feedback"
    }
}

fun startListeningForVoice() {
    android.util.Log.d("TemiMain", "Starting voice listening")
    resetInactivityTimer()
    voiceInteractionManager?.startListening()
}

fun stopListeningForVoice() {
    android.util.Log.d("TemiMain", "Stopping voice listening")
    voiceInteractionManager?.stopListening()
}
*/

/*
 * STEP 4: Add this to onDestroy() before super.onDestroy()
 * ==========================================================
 */
// voiceInteractionManager?.release()

/*
 * STEP 5: Optional - Replace existing GPT call in processSpeech()
 * ================================================================
 *
 * BEFORE (using robot.askQuestion):
 * private fun processSpeech(text: String) {
 *     callGPT(prompt)  // Old approach
 * }
 *
 * AFTER (using Ollama):
 * private fun processSpeech(text: String) {
 *     // If you want to replace the existing GPT call completely:
 *     // Just call the voice manager directly
 *     voiceInteractionManager?.let {
 *         // The voice manager handles the entire pipeline
 *         // Just pass the spoken text
 *         // It will: record speech → convert to text → send to Ollama → speak response
 *     }
 * }
 *
 * OR - Keep existing flow but use Ollama instead of GPT
 *
 * You can modify the existing callGPT() to use Ollama:
 */

/*
private fun callGPT(prompt: String) {
    // Replace with Ollama call
    lifecycleScope.launch {
        try {
            val ollamaRequest = OllamaRequest(
                model = "llama3:8b",
                prompt = prompt,
                stream = false,
                temperature = 0.7
            )

            val response = withContext(Dispatchers.IO) {
                OllamaClient.api.generate(ollamaRequest)
            }

            // Speak the response
            robot?.speak(TtsRequest.create(response.response, false))

            // Reset conversation state
            isConversationActive = false
            conversationActiveState.value = false

        } catch (e: Exception) {
            android.util.Log.e("TemiMain", "Ollama error: ${e.message}", e)
            safeSpeak("Sorry, I couldn't process that. Please try again.")
        }
    }
}
*/

/*
 * STEP 6: Add voice button to TemiMainScreen composable
 * ========================================================
 *
 * In ui/screens/TemiMainScreen.kt, add a voice button:
 *
 * Button(
 *     onClick = { (context as? MainActivity)?.startListeningForVoice() },
 *     modifier = Modifier.size(100.dp),
 *     shape = CircleShape,
 *     colors = ButtonDefaults.buttonColors(
 *         containerColor = colorResource(R.color.neon_cyan)
 *     )
 * ) {
 *     Icon(
 *         imageVector = Icons.Default.Mic,
 *         contentDescription = "Voice",
 *         modifier = Modifier.size(50.dp),
 *         tint = colorResource(R.color.dark_bg)
 *     )
 * }
 */

/*
 * ============================================================================
 * QUICK MIGRATION GUIDE: FROM robot.askQuestion() TO OLLAMA VOICE PIPELINE
 * ============================================================================
 *
 * BEFORE (Old GPT approach):
 *
 *     private fun processSpeech(text: String) {
 *         callGPT(text)  // Uses robot.askQuestion() internally
 *     }
 *
 * AFTER (New Ollama approach):
 *
 *     // Option 1: Direct Ollama call (replaces entire flow)
 *     private fun processSpeech(text: String) {
 *         if (text.isBlank()) return
 *         lifecycleScope.launch {
 *             try {
 *                 val response = withContext(Dispatchers.IO) {
 *                     OllamaClient.api.generate(
 *                         OllamaRequest(
 *                             prompt = text,
 *                             model = "llama3:8b"
 *                         )
 *                     )
 *                 }
 *                 robot?.speak(TtsRequest.create(response.response, false))
 *             } catch (e: Exception) {
 *                 safeSpeak("Error processing request")
 *             }
 *         }
 *     }
 *
 *     // Option 2: Use VoiceInteractionManager (recommended - handles everything)
 *     private fun processSpeech(text: String) {
 *         voiceInteractionManager?.let {
 *             // Already integrated, just use the manager
 *             // It handles: speech recognition, LLM, TTS, all internally
 *         }
 *     }
 */

/*
 * ============================================================================
 * TROUBLESHOOTING
 * ============================================================================
 *
 * 1. "Speech recognition not available"
 *    → Device doesn't support SpeechRecognizer
 *    → Test on different device or emulator
 *
 * 2. "Network error" / "Connection timeout"
 *    → Ollama server not running
 *    → Start: OLLAMA_HOST=0.0.0.0:11434 ollama serve
 *    → Verify Temi is on same network
 *    → Check OllamaClient.kt BASE_URL
 *
 * 3. "No speech detected"
 *    → User didn't speak clearly
 *    → Check microphone permissions
 *    → Ask user to speak again
 *
 * 4. "SpeechRecognizer busy"
 *    → Already listening
 *    → Call stopListening() before startListening()
 *
 * 5. "Permission denied: RECORD_AUDIO"
 *    → Need runtime permission request (Android 6.0+)
 *    → Check AndroidManifest.xml has RECORD_AUDIO permission
 *    → Request permission in onCreate() using ActivityCompat.requestPermissions()
 */

/**
 * IMPORTANT:
 * This file is a REFERENCE showing how to integrate the voice pipeline.
 *
 * Copy the relevant code snippets into your actual MainActivity.kt
 * Do not add this file to the project - it's for documentation only.
 */

