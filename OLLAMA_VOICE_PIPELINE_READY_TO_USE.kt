/**
 * OLLAMA_VOICE_PIPELINE_READY_TO_USE.kt
 *
 * This file contains ready-to-use code snippets for integrating the Ollama voice pipeline
 * Copy and paste the relevant sections into your MainActivity.kt
 *
 * DO NOT add this as a separate file - use it as a reference for code to copy
 */

// ============================================================================
// SNIPPET 1: Add to MainActivity class properties
// ============================================================================

private var voiceInteractionManager: VoiceInteractionManager? = null
private val currentVoiceState = mutableStateOf(VoiceState.IDLE)

// ============================================================================
// SNIPPET 2: Add to onCreate() - Initialize voice manager
// ============================================================================

// Place this AFTER Robot.getInstance().addOnRobotReadyListener
Robot.getInstance().addOnRobotReadyListener { isReady ->
    if (isReady) {
        val robot = robotState.value
        if (robot != null) {
            android.util.Log.d("TemiMain", "Robot ready - initializing voice manager")
            try {
                voiceInteractionManager = VoiceInteractionManager(
                    context = this,
                    robot = robot,
                    coroutineScope = lifecycleScope
                )

                // Handle state changes
                voiceInteractionManager?.setOnStateChanged { state ->
                    android.util.Log.d("VoiceUI", "State: $state")
                    currentVoiceState.value = state
                    // Update UI indicators here based on state
                }

                // Handle errors
                voiceInteractionManager?.setOnError { errorMessage ->
                    android.util.Log.e("VoiceError", errorMessage)
                    android.widget.Toast.makeText(
                        this,
                        errorMessage,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }

                // Handle results
                voiceInteractionManager?.setOnVoiceResultReady { result ->
                    android.util.Log.d("Voice", "User said: ${result.spokenText}")
                    android.util.Log.d("Voice", "Response: ${result.llmResponse}")
                    android.util.Log.d("Voice", "Time: ${result.processingTimeMs}ms")

                    // Optional: Navigate based on user input
                    handleVoiceIntent(result.spokenText)
                }

                android.util.Log.d("TemiMain", "Voice manager initialized")
            } catch (e: Exception) {
                android.util.Log.e("TemiMain", "Voice manager init failed: ${e.message}")
            }
        }
    }
}

// ============================================================================
// SNIPPET 3: Add helper function - Parse voice intent for navigation
// ============================================================================

private fun handleVoiceIntent(userInput: String) {
    val lowerInput = userInput.lowercase()

    when {
        lowerInput.contains("doctor") || lowerInput.contains("specialist") -> {
            android.util.Log.d("Voice", "Intent: FIND_DOCTOR")
            currentScreen.value = "doctors"
        }

        lowerInput.contains("appointment") || lowerInput.contains("book") -> {
            android.util.Log.d("Voice", "Intent: BOOK_APPOINTMENT")
            currentScreen.value = "appointment"
        }

        lowerInput.contains("navigate") || lowerInput.contains("pharmacy") ||
        lowerInput.contains("icu") || lowerInput.contains("lab") ||
        lowerInput.contains("pathology") -> {
            android.util.Log.d("Voice", "Intent: NAVIGATE")
            currentScreen.value = "navigation"
        }

        lowerInput.contains("feedback") || lowerInput.contains("rate") -> {
            android.util.Log.d("Voice", "Intent: FEEDBACK")
            currentScreen.value = "feedback"
        }
    }
}

// ============================================================================
// SNIPPET 4: Add function - Start listening on user action
// ============================================================================

private fun startVoiceListening() {
    android.util.Log.d("TemiMain", "Starting voice listening")
    resetInactivityTimer()
    voiceInteractionManager?.startListening()
}

// ============================================================================
// SNIPPET 5: Add function - Stop listening manually
// ============================================================================

private fun stopVoiceListening() {
    android.util.Log.d("TemiMain", "Stopping voice listening")
    voiceInteractionManager?.stopListening()
}

// ============================================================================
// SNIPPET 6: Modify onDestroy() - Cleanup voice manager
// ============================================================================

override fun onDestroy() {
    super.onDestroy()

    // ... existing cleanup code ...

    // Add this line
    voiceInteractionManager?.release()

    // ... rest of cleanup ...
}

// ============================================================================
// SNIPPET 7: OPTIONAL - Add UI button in TemiMainScreen composable
// ============================================================================

/*
In ui/screens/TemiMainScreen.kt, add a voice button to the composable:

@Composable
fun TemiMainScreen(
    robot: Robot? = null,
    isThinking: Boolean = false,
    isConversationActive: Boolean = false,
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... existing menu cards ...

        // Add voice button at bottom
        Button(
            onClick = {
                if (context is MainActivity) {
                    context.startVoiceListening()
                }
            },
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.neon_cyan)
            ),
            enabled = !isConversationActive && !isThinking
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Input",
                modifier = Modifier.size(50.dp),
                tint = colorResource(R.color.dark_bg)
            )
        }
    }
}
*/

// ============================================================================
// SNIPPET 8: ALTERNATIVE - Replace existing callGPT() with Ollama
// ============================================================================

/*
If you want to replace the existing robot.askQuestion() calls with direct Ollama,
modify the existing callGPT() function:

private fun callGPT(prompt: String) {
    // HARD LOCK: Prevents multiple GPT calls
    if (isConversationActive) {
        android.util.Log.d("GPT_FIX", "BLOCKED: Duplicate conversation attempt")
        return
    }

    isConversationActive = true
    conversationActiveState.value = true

    lifecycleScope.launch {
        try {
            // Build Ollama request
            val ollamaRequest = OllamaRequest(
                model = "llama3:8b",
                prompt = prompt,
                stream = false,
                temperature = 0.7
            )

            // Call Ollama on IO thread
            val response = withContext(Dispatchers.IO) {
                android.util.Log.d("Ollama", "Sending request...")
                OllamaClient.api.generate(ollamaRequest)
            }

            android.util.Log.d("Ollama", "Response: ${response.response}")

            // Speak the response
            safeSpeak(response.response)

            // Release lock
            isConversationActive = false
            conversationActiveState.value = false

        } catch (e: Exception) {
            android.util.Log.e("Ollama", "Error: ${e.message}", e)
            safeSpeak("Sorry, I couldn't process that request.")

            isConversationActive = false
            conversationActiveState.value = false
        }
    }
}
*/

// ============================================================================
// SNIPPET 9: Add imports to MainActivity
// ============================================================================

// Add these to the top of MainActivity.kt if not already present:

import com.example.alliswelltemi.network.OllamaClient
import com.example.alliswelltemi.network.OllamaRequest
import com.example.alliswelltemi.network.VoiceState
import com.example.alliswelltemi.utils.VoiceInteractionManager

// ============================================================================
// SNIPPET 10: Test - Quick verification in logcat
// ============================================================================

/*
After integrating and running the app, check logcat for these messages:

1. Voice manager initialized:
   adb logcat | grep "Voice manager initialized"

2. User speaks:
   adb logcat | grep "Speech recognized"

3. Sending to Ollama:
   adb logcat | grep "Sending prompt to Ollama"

4. Response received:
   adb logcat | grep "Ollama response received"

5. Speaking response:
   adb logcat | grep "Speaking response"

Example:
adb logcat | grep -E "VoiceInteraction|Voice manager|Speech recognized|Ollama"
*/

// ============================================================================
// SNIPPET 11: Minimum viable integration (if you want something quick)
// ============================================================================

/*
If you just want basic functionality without all the callbacks:

private var voiceManager: VoiceInteractionManager? = null

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Robot.getInstance().addOnRobotReadyListener { isReady ->
        if (isReady) {
            voiceManager = VoiceInteractionManager(this, robotState.value, lifecycleScope)
        }
    }
}

// User calls this function to start listening
fun startListening() {
    voiceManager?.startListening()
}

override fun onDestroy() {
    voiceManager?.release()
    super.onDestroy()
}

// That's it! The voice manager handles everything internally.
*/

// ============================================================================
// SNIPPET 12: Advanced - Custom hospital prompts
// ============================================================================

/*
You can customize the hospital context prompt in VoiceInteractionManager.kt
Find this function and modify:

private fun buildHospitalContextPrompt(userInput: String): String {
    return """
        You are a helpful hospital assistant robot named Temi.

        Hospital Services:
        - OPD (Out-Patient Department): General consultations
        - Pharmacy: Medicine dispensary
        - Pathology Lab: Medical testing
        - Billing Counter: Payment inquiries
        - ICU: Intensive care

        Guidelines:
        - Be friendly and professional
        - Keep responses brief (1-2 sentences for voice)
        - Suggest booking appointments when appropriate
        - If unsure, recommend visiting information desk

        Patient Query: "$userInput"

        Respond as Temi hospital assistant (2-3 sentences max):
    """.trimIndent()
}

// You can add more hospital-specific context:
- Doctor availability
- Operating hours
- Department specialties
- Common patient questions
*/

// ============================================================================
// SNIPPET 13: Testing the Ollama connection independently
// ============================================================================

/*
Test if Ollama is reachable from your app:

lifecycleScope.launch {
    try {
        val testResponse = withContext(Dispatchers.IO) {
            OllamaClient.api.generate(
                OllamaRequest(
                    prompt = "Say hello",
                    model = "llama3:8b"
                )
            )
        }
        Log.d("OllamaTest", "Success: ${testResponse.response}")
        Toast.makeText(this@MainActivity, "Ollama connected!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("OllamaTest", "Failed: ${e.message}")
        Toast.makeText(
            this@MainActivity,
            "Ollama connection failed: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}
*/

// ============================================================================
// SNIPPET 14: Configuration - Change Ollama server IP
// ============================================================================

/*
If your Ollama server is on a different IP, edit OllamaClient.kt:

object OllamaClient {

    // For Temi robot (default, change if needed):
    private const val BASE_URL = "http://192.168.137.1:11434/"

    // For Android emulator:
    // private const val BASE_URL = "http://10.0.2.2:11434/"

    // For custom IP (example):
    // private const val BASE_URL = "http://192.168.1.100:11434/"

    // For localhost (testing):
    // private const val BASE_URL = "http://localhost:11434/"

    // ... rest of code ...
}

Then rebuild: ./gradlew build
*/

// ============================================================================
// COMPLETE EXAMPLE: Full MainActivity snippet
// ============================================================================

/*
Here's a complete example showing how the pieces fit together:

class MainActivity : ComponentActivity(),
    Robot.AsrListener,
    Robot.NlpListener,
    // ... other listeners ...
{

    // Properties
    private val robotState = mutableStateOf<Robot?>(null)
    private val robot: Robot? get() = robotState.value
    private var voiceInteractionManager: VoiceInteractionManager? = null
    private val currentVoiceState = mutableStateOf(VoiceState.IDLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ... window setup ...

        Robot.getInstance().addOnRobotReadyListener { isReady ->
            if (isReady) {
                val robot = Robot.getInstance()
                robotState.value = robot

                // Initialize voice manager
                voiceInteractionManager = VoiceInteractionManager(
                    context = this,
                    robot = robot,
                    coroutineScope = lifecycleScope
                )

                voiceInteractionManager?.setOnError { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }

                voiceInteractionManager?.setOnStateChanged { state ->
                    currentVoiceState.value = state
                }
            }
        }

        setContent {
            // ... UI code ...
        }
    }

    fun startListening() {
        voiceInteractionManager?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceInteractionManager?.release()
    }
}
*/

// ============================================================================
// That's it! You now have a complete production-ready voice pipeline.
// ============================================================================

/*
Summary of what to do:

1. Add 4 properties to MainActivity class (Snippet 1)
2. Add voice manager initialization in onCreate (Snippet 2)
3. Add helper function for voice intent (Snippet 3)
4. Add startVoiceListening() function (Snippet 4)
5. Modify onDestroy() to release voice manager (Snippet 6)
6. Add imports (Snippet 9)
7. Optional: Add UI button (Snippet 7)
8. Build and test: ./gradlew build
9. Start Ollama: OLLAMA_HOST=0.0.0.0:11434 ollama serve
10. Run app and speak!

Questions? Check the documentation:
- OLLAMA_VOICE_PIPELINE_QUICK_REF.md
- OLLAMA_VOICE_PIPELINE.md
- VoiceInteractionManager.kt (source code comments)
*/

