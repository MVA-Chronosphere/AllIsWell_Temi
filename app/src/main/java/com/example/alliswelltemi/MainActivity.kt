package com.example.alliswelltemi

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alliswelltemi.ui.screens.TemiMainScreen
import com.example.alliswelltemi.ui.screens.NavigationScreen
import com.example.alliswelltemi.ui.screens.AppointmentBookingScreen
import com.example.alliswelltemi.ui.screens.DoctorsScreen
import com.example.alliswelltemi.ui.theme.TemiTheme
import com.example.alliswelltemi.viewmodel.NavigationViewModel
import com.example.alliswelltemi.viewmodel.AppointmentViewModel
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnRobotReadyListener

class MainActivity : AppCompatActivity(), OnRobotReadyListener {

    private var robot: Robot? = null
    private val isRobotReady = mutableStateOf(false)
    private val currentScreen = mutableStateOf("main")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up fullscreen and landscape
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize Temi Robot SDK
        Robot.getInstance().addOnRobotReadyListener(this)

<<<<<<< Updated upstream
        // Set Compose content
        setContent {
            TemiTheme(darkTheme = true) {
                when (currentScreen.value) {
                    "navigation" -> {
                        val navViewModel: NavigationViewModel = viewModel()
                        NavigationScreen(
                            robot = robot,
                            viewModel = navViewModel,
                            onBackPress = {
                                currentScreen.value = "main"
                            },
                            onNavigationComplete = { _ ->
                                // Optional: Do something after navigation completes
                                // For now, stay on navigation screen
=======
            doctorsViewModel = DoctorsViewModel(application)

            // Initialize orchestrator with empty list (will be updated when doctors load)
            orchestrator = SpeechOrchestrator(emptyList())

            lifecycleScope.launch {
                snapshotFlow { doctorsViewModel.doctors.value }.collectLatest { doctors ->
                    if (doctors.isNotEmpty()) {
                        // Update orchestrator with fresh doctor list
                        orchestrator = SpeechOrchestrator(doctors)

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastToastTime > 3000) {
                            android.widget.Toast.makeText(this@MainActivity, "✓ ${doctors.size} doctors loaded", android.widget.Toast.LENGTH_SHORT).show()
                            lastToastTime = currentTime
                        }
                    }
                }
            }

            setContent {
                val currentRobot = robotState.value
                val isConversationActive = conversationActiveState.value

                TemiTheme(darkTheme = true) {
                    when (currentScreen.value) {
                        "navigation" -> {
                            val navViewModel: NavigationViewModel = viewModel()
                            NavigationScreen(
                                robot = currentRobot,
                                viewModel = navViewModel,
                                isThinking = isGptProcessing,
                                isConversationActive = isConversationActive,
                                onBackPress = { currentScreen.value = "main" }
                            )
                        }
                        "appointment" -> {
                            AppointmentBookingScreen(
                                robot = currentRobot,
                                viewModel = appointmentViewModel,
                                doctorsViewModel = doctorsViewModel,
                                onBackPress = { currentScreen.value = "main" }
                            )
                        }
                        "doctors" -> {
                            DoctorsScreen(
                                robot = currentRobot,
                                viewModel = doctorsViewModel,
                                onBackPress = { currentScreen.value = "main" },
                                onSelectDoctor = { currentScreen.value = "appointment" }
                            )
                        }
                        else -> {
                            TemiMainScreen(
                                robot = currentRobot,
                                isThinking = isGptProcessing,
                                isConversationActive = isConversationActive,
                                onNavigate = { destination -> handleNavigation(destination) }
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiMain", "CRITICAL ERROR in onCreate(): ${e.message}", e)
        }
    }

    private var lastProcessedText = ""
    private var lastSafeSpeakMessage = ""
    private val isRobotSpeaking = AtomicBoolean(false)
    private val pendingTtsIds = Collections.synchronizedSet(mutableSetOf<UUID>())
    private var isGptProcessing by mutableStateOf(false)
    private var gptRequestStartTime: Long = 0L

     override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
         android.util.Log.d("VOICE_PIPELINE", "========== ASR RESULT RECEIVED ==========")
         android.util.Log.d("VOICE_PIPELINE", "Speech: '$asrResult'")
         android.util.Log.d("VOICE_PIPELINE", "Language: ${sttLanguage.name}")
         android.util.Log.i("VOICE_PIPELINE_FLOW", "✅ STEP 1: Speech captured by ASR")

         // STEP 1: Validate input
         if (asrResult.isBlank()) {
             android.util.Log.w("VOICE_PIPELINE", "⚠️ Empty ASR result, ignoring")
             return
         }

         robot?.finishConversation()

         // STEP 2: HARD BLOCK during active Ollama conversation
         if (isConversationActive) {
             android.util.Log.d("VOICE_PIPELINE", "❌ BLOCKED: Ollama conversation already active")
             return
         }

         // STEP 3: Race condition safety - ensure serial processing
         if (!isProcessingSpeech.compareAndSet(false, true)) {
             android.util.Log.d("VOICE_PIPELINE", "❌ BLOCKED: Already processing previous speech")
             return
         }

         // STEP 4: Process speech with Ollama EXCLUSIVELY
         try {
             android.util.Log.d("VOICE_PIPELINE", "✅ STEP 2: Starting manual speech processing with Ollama")
             processSpeech(asrResult)
         } catch (e: Exception) {
             android.util.Log.e("VOICE_PIPELINE", "❌ Error processing speech: ${e.message}", e)
             safeSpeak("An error occurred. Please try again.")
         } finally {
             isProcessingSpeech.set(false)
         }
     }

    override fun onNlpCompleted(nlpResult: NlpResult) {
        // ⚠️ CRITICAL: This should NEVER be called because we don't register NLP listener
        // If it IS called, we block it as a safety measure
        android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "========== TEMI NLP DETECTED ==========")
        android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "❌ BLOCKING Temi cloud NLP response!")
        android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "Action: ${nlpResult.action}")
        android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "Query: ${nlpResult.resolvedQuery}")
        android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "This response will NOT be used - using Ollama only")

        // Do NOT process this result - return immediately
        return
    }

    override fun onConversationStatusChanged(status: Int, text: String) {
        if (text.isNotBlank()) {
            robot?.cancelAllTtsRequests()
            robot?.finishConversation()
        }
    }

    override fun onConversationAttaches(isAttached: Boolean) {
        android.util.Log.d("TEMI_CLOUD_AI_BLOCK", "Conversation attached: $isAttached")

        // ========== BLOCK TEMI CONVERSATION LAYER ==========
        // Prevent Temi's default conversation UI from showing
        // Note: toggleConversationLayer() is not available in this SDK version
        // Blocking is achieved by not registering NLP listener
        if (isAttached) {
            android.util.Log.d("TEMI_CLOUD_AI_BLOCK", "Conversation layer attached (cannot be toggled in this SDK version)")
        }
    }

     /**
      * Call Ollama LLM with streaming support and exclusive conversation lock protection
      * CRITICAL: Ensures ONLY ONE Ollama conversation at a time
      * Treat as MUTEX LOCK - no parallel operations allowed
      */
     private fun callOllama(prompt: String) {
         // HARD LOCK: Prevents multiple Ollama calls
         if (isConversationActive) {
             android.util.Log.d("VOICE_PIPELINE", "BLOCKED: Duplicate conversation attempt - already active")
             return
         }

         isConversationActive = true
         conversationActiveState.value = true // Sync UI state

         android.util.Log.i("VOICE_PIPELINE", "========== STARTING OLLAMA CONVERSATION ==========")
         android.util.Log.d("VOICE_PIPELINE", "Streaming call START - isConversationActive = true")

         // Prevent duplicate calls
         if (isGptProcessing) {
             android.util.Log.d("VOICE_PIPELINE", "Skipping duplicate Ollama call (already processing)")
             isConversationActive = false // Release lock
             conversationActiveState.value = false
             return
         }

         isGptProcessing = true
         gptRequestStartTime = System.currentTimeMillis()

         // BLOCK inactivity timer during Ollama - prevents activity reset mid-conversation
         handler.removeCallbacks(inactivityRunnable)
         android.util.Log.d("VOICE_PIPELINE", "Inactivity timer BLOCKED during conversation")

        // Clean the prompt - remove excessive newlines and whitespace
        val cleanedPrompt = prompt
            .replace(Regex("\n{3,}"), "\n\n")  // Max 2 consecutive newlines
            .replace(Regex(" {2,}"), " ")       // Single spaces only
            .trim()

        android.util.Log.d("OLLAMA_FIX", "Cleaned prompt length: ${cleanedPrompt.length} chars (original: ${prompt.length})")
        android.util.Log.d("OLLAMA_FIX", "Calling Ollama.generateStreaming() now...")

        // Launch streaming coroutine
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val ollamaRequest = OllamaRequest(
                    model = "llama3:8b",
                    prompt = cleanedPrompt,
                    stream = true,
                    temperature = 0.7
                )

                android.util.Log.d("OLLAMA_FIX", "Ollama streaming request created")

                // Collect streaming response - WAIT for complete response before speaking
                val fullResponse = StringBuilder()
                OllamaClient.generateStreaming(ollamaRequest).collect { chunk ->
                    fullResponse.append(chunk)
                    // IMPORTANT: Do NOT speak streaming chunks immediately
                    // Buffer them and speak the complete response when done
                }

                val finalResponse = fullResponse.toString()
                val elapsedMs = System.currentTimeMillis() - gptRequestStartTime

                android.util.Log.d("OLLAMA_FIX", "========== OLLAMA RESPONSE RECEIVED ==========")
                android.util.Log.d("OLLAMA_FIX", "Response received after ${elapsedMs}ms")
                android.util.Log.d("OLLAMA_FIX", "Response length: ${finalResponse.length} chars")
                android.util.Log.d("OLLAMA_RESPONSE", "========== OLLAMA RESPONSE START ==========")
                android.util.Log.d("OLLAMA_RESPONSE", finalResponse)
                android.util.Log.d("OLLAMA_RESPONSE", "========== OLLAMA RESPONSE END ==========")

                // Step 4: Save to conversation context
                val lastQuestion = cleanedPrompt.substringAfter("User: ").substringBefore("\n\nIMPORTANT")
                conversationContext.addTurn(lastQuestion, finalResponse)
                android.util.Log.d("OLLAMA_FIX", "Saved turn to context. Total turns: ${conversationContext.getTurnCount()}")

                // RELEASE conversation lock AFTER streaming completes
                withContext(Dispatchers.Main) {
                    // Speak the COMPLETE response now that streaming is done
                    android.util.Log.d("OLLAMA_FIX", "Speaking complete response: ${finalResponse.length} chars")

                    // CRITICAL: Release conversation lock BEFORE calling safeSpeak()
                    // so safeSpeak() is not blocked by the isConversationActive check
                    isGptProcessing = false
                    isConversationActive = false
                    conversationActiveState.value = false
                    android.util.Log.d("OLLAMA_FIX", "Conversation lock RELEASED")

                    // NOW speak the response (will not be blocked)
                    safeSpeak(finalResponse)

                    // Restart inactivity timer after Ollama completes
                    handler.post(inactivityRunnable)
                    android.util.Log.d("OLLAMA_FIX", "Inactivity timer RESTARTED")
                }

            } catch (e: Exception) {
                android.util.Log.e("OLLAMA_FIX", "========== EXCEPTION in callOllama() ==========")
                android.util.Log.e("OLLAMA_FIX", "Exception: ${e.message}", e)

                // Generate fallback response
                val fallbackResponse = RagContextBuilder.generateFallbackResponse(cleanedPrompt, doctorsViewModel.doctors.value)
                android.util.Log.d("OLLAMA_FIX", "Fallback response: $fallbackResponse")

                withContext(Dispatchers.Main) {
                    safeSpeak(fallbackResponse)

                    // Release locks and restart timer
                    isGptProcessing = false
                    isConversationActive = false
                    conversationActiveState.value = false
                    handler.post(inactivityRunnable)
                }
            }
        }
    }

     /**
      * Speak the complete Ollama response
      */
     private fun processSpeech(text: String) {
         if (text.isBlank()) {
             android.util.Log.w("VOICE_PIPELINE", "processSpeech called with blank text - ignoring")
             return
         }

         // BLOCK INPUT DURING ACTIVE SESSION
         if (isConversationActive) {
             android.util.Log.d("VOICE_PIPELINE", "Input ignored (active conversation)")
             return
         }

         if (text == lastProcessedText) {
             android.util.Log.d("VOICE_PIPELINE", "processSpeech: Duplicate text skipped - '$text'")
             return
         }

         lastProcessedText = text
         android.util.Log.i("VOICE_PIPELINE_FLOW", "✅ STEP 3: Calling Ollama LLM")
         resetInactivityTimer()

        val doctors = doctorsViewModel.doctors.value
        if (doctors.isEmpty()) {
            android.util.Log.w("TemiSpeech", "processSpeech: Doctors list empty, waiting for data to load")
            safeSpeak("Doctors list is still loading. Please try again.")
            return
        }

        // Move all heavy work to background thread using coroutines
        lifecycleScope.launch {
            val perf_start = System.currentTimeMillis()

            try {
                // Step 1: Analyze intent on background thread
                val context = withContext(Dispatchers.Default) {
                    android.util.Log.d("PERF", "Orchestrator.analyze() starting on background thread")
                    orchestrator.analyze(text)
                }

                android.util.Log.d("TemiSpeech", "Intent: ${context.intent}, Confidence: ${context.confidence}, Doctor: ${context.doctor?.name}, Dept: ${context.department}")

                // Step 2: Handle DANCE intent (skip Ollama for dance)
                if (context.intent == SpeechOrchestrator.Intent.DANCE) {
                    withContext(Dispatchers.Main) {
                        android.util.Log.i("TemiSpeech", "🎉 DANCE REQUEST DETECTED! Performing dance...")
                        // Launch dance asynchronously
                        lifecycleScope.launch {
                            try {
                                val danceMove = context.danceMove ?: DanceService.DanceMove.SMOOTH_GROOVE
                                DanceService.performDance(robot, danceMove) {
                                    android.util.Log.d("TemiSpeech", "✅ Dance completed!")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("TemiSpeech", "Error during dance: ${e.message}", e)
                                safeSpeak("Oops! I had trouble dancing. Please try again.")
>>>>>>> Stashed changes
                            }
                        )
                    }
                    "appointment" -> {
                        val appointmentViewModel: AppointmentViewModel = viewModel()
                        AppointmentBookingScreen(
                            robot = robot,
                            viewModel = appointmentViewModel,
                            onBackPress = {
                                currentScreen.value = "main"
                            }
                        )
                    }
                    "doctors" -> {
                        val doctorsViewModel: DoctorsViewModel = viewModel()
                        DoctorsScreen(
                            robot = robot,
                            viewModel = doctorsViewModel,
                            onBackPress = {
                                currentScreen.value = "main"
                            },
                            onSelectDoctor = { doctor ->
                                // TODO: Handle doctor selection for booking
                                currentScreen.value = "appointment"
                            }
                        )
                    }
                    else -> {
                        TemiMainScreen(
                            robot = robot,
                            onNavigate = { destination ->
                                handleNavigation(destination)
                            }
                        )
                    }
                }
<<<<<<< Updated upstream
=======

                // Step 4: Build optimized Ollama prompt on background thread (SKIP FOR DANCE)
                val prompt = withContext(Dispatchers.Default) {
                    android.util.Log.d("PERF", "RagContextBuilder.buildOllamaPrompt() starting")
                    RagContextBuilder.buildOllamaPrompt(
                        query = text, 
                        doctors = doctors,
                        historyContext = conversationContext.getContextString()
                    )
                }

                val perf_time = System.currentTimeMillis() - perf_start
                android.util.Log.d("PERF", "Background processing completed in ${perf_time}ms")

                // Log full prompt for debugging (multi-line)
                android.util.Log.d("OLLAMA_PROMPT", "========== OLLAMA PROMPT START ==========")
                android.util.Log.d("OLLAMA_PROMPT", prompt)
                android.util.Log.d("OLLAMA_PROMPT", "========== OLLAMA PROMPT END (${prompt.length} chars) ==========")

                // Step 5: Call Ollama on main thread (must happen after all context prep)
                withContext(Dispatchers.Main) {
                    robot?.finishConversation()
                    callOllama(prompt)
                }

            } catch (e: Exception) {
                android.util.Log.e("TemiSpeech", "Error in background processing: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    safeSpeak("An error occurred while processing your request.")
                }
>>>>>>> Stashed changes
            }
        }
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            this.robot = Robot.getInstance()
            isRobotReady.value = true

<<<<<<< Updated upstream
            // Initialization message
            this.robot?.speak(
                TtsRequest.create(
                    speech = "Hello, I am Temi and I am ready to assist you",
                    isShowOnConversationLayer = false
                )
            )
=======
            // Register only the listeners needed for the custom voice pipeline:
            robot?.addAsrListener(this)                                   // Manual STT
            robot?.addTtsListener(this)                                   // Track speech status
            robot?.addConversationViewAttachesListener(this)              // Track UI state
            robot?.addOnConversationStatusChangedListener(this)           // Block Temi cloud responses
            // Do NOT add NLP listener - prevents Temi cloud AI and askQuestion()
            // Only Ollama/manual pipeline will process speech.
            // Enable kiosk mode and hide system UI overlays
            robot?.setKioskModeOn(true)
            robot?.hideTopBar()

            // Initialize voice interaction manager for ASR pipeline
            try {
                voiceInteractionManager = com.example.alliswelltemi.utils.VoiceInteractionManager(
                    context = this,
                    robot = robot,
                    coroutineScope = lifecycleScope
                )
                android.util.Log.d("TemiMain", "VoiceInteractionManager initialized successfully")
            } catch (e: Exception) {
                android.util.Log.e("TemiMain", "Failed to initialize VoiceInteractionManager: ${e.message}", e)
            }

            // Temi cloud AI and askQuestion() are fully disabled. Only custom pipeline is active.

            this.isRobotReady.value = true
>>>>>>> Stashed changes
        }
    }

    /**
     * Navigation handler for menu items
     * This can be extended to open different screens or trigger specific actions
     */
    private fun handleNavigation(destination: String) {
        // Implement navigation based on destination
        when (destination) {
            "navigation" -> {
                currentScreen.value = "navigation"
            }
            "appointment" -> {
                currentScreen.value = "appointment"
            }
            "doctors" -> {
                currentScreen.value = "doctors"
            }
            "feedback" -> {
                // TODO: Open feedback screen or show feedback form
                // This could be a bottom sheet, dialog, or new screen
            }
            "emergency" -> {
                // TODO: Trigger emergency alert system
                // You can integrate with hospital alert system here
            }
            "info" -> {
                // TODO: Open hospital information screen
            }
            "language" -> {
                // Language already handled in composable
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    override fun onResume() {
        super.onResume()
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }
}

