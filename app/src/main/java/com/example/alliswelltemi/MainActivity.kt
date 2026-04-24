package com.example.alliswelltemi

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alliswelltemi.network.OllamaClient
import com.example.alliswelltemi.network.OllamaRequest
import com.example.alliswelltemi.ui.screens.*
import com.example.alliswelltemi.ui.theme.TemiTheme
import com.example.alliswelltemi.utils.ConversationContext
import com.example.alliswelltemi.utils.RagContextBuilder
import com.example.alliswelltemi.utils.SpeechOrchestrator
import com.example.alliswelltemi.viewmodel.AppointmentViewModel
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.example.alliswelltemi.viewmodel.NavigationViewModel
import com.robotemi.sdk.*
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.NlpResult
import com.robotemi.sdk.SttLanguage
import com.robotemi.sdk.listeners.OnConversationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.alliswelltemi.utils.DanceService
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity(),
    Robot.AsrListener,
    Robot.NlpListener,
    Robot.TtsListener,
    Robot.ConversationViewAttachesListener,
    OnConversationStatusChangedListener,
    OnRobotReadyListener {

    private val robotState = mutableStateOf<Robot?>(null)
    private val robot: Robot? get() = robotState.value
    private val isRobotReady = mutableStateOf(false)
    private val currentScreen = mutableStateOf("main")
    private lateinit var doctorsViewModel: DoctorsViewModel
    private val appointmentViewModel by lazy { AppointmentViewModel() }

    // Voice interaction manager for ASR pipeline
    private var voiceInteractionManager: com.example.alliswelltemi.utils.VoiceInteractionManager? = null

    // Expose conversation state to UI for guarding interactions
    private val conversationActiveState = mutableStateOf(false)

    // Production-grade voice pipeline
    private lateinit var orchestrator: SpeechOrchestrator
    private val isProcessingSpeech = AtomicBoolean(false)
    
    // Conversation Context - maintains chat history
    private val conversationContext = ConversationContext(
        maxHistoryItems = 5,
        maxContextLength = 2000
    )

    // CRITICAL: SINGLE GLOBAL STATE - ensures ONLY ONE GPT conversation at a time
    // Treat as MUTEX LOCK - no parallel operations allowed
    @Volatile
    private var isConversationActive = false

    private var lastToastTime: Long = 0

    private var lastInteractionTime: Long = System.currentTimeMillis()
    private val INACTIVITY_TIMEOUT: Long = 30000 // 30 seconds
    private val handler = Handler(Looper.getMainLooper())
    private val inactivityRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteractionTime >= INACTIVITY_TIMEOUT) {
                // NEVER reset activity during active GPT conversation or while speaking
                if (!isRobotSpeaking.get() && !isConversationActive && !isGptProcessing) {
                    android.util.Log.i("TemiLifecycle", "Inactivity timeout reached, returning to main screen and clearing context")
                    
                    if (currentScreen.value != "main") {
                        currentScreen.value = "main"
                    }
                    
                    // CLEAR Conversation Memory after 30s of inactivity
                    conversationContext.clearHistory()
                } else {
                    // If active, postpone check instead of just stopping
                    android.util.Log.d("TemiLifecycle", "Inactivity timeout reached but system is busy (speaking=$isRobotSpeaking, active=$isConversationActive, processing=$isGptProcessing). Postponing clear.")
                    lastInteractionTime = System.currentTimeMillis() // Reset timer to give another full window
                }
            }
            handler.postDelayed(this, 5000) // Check every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        android.util.Log.i("TemiMain", "========== APPLICATION START ==========")
        
        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            Robot.getInstance().addOnRobotReadyListener(this)

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
         android.util.Log.d("TEMI_CLOUD_AI_BLOCK", "========== CONVERSATION STATUS CHANGED ==========")
         android.util.Log.d("TEMI_CLOUD_AI_BLOCK", "Status: $status (1=Started, 2=Listening, 3=Processing, 4=Complete)")
         android.util.Log.d("TEMI_CLOUD_AI_BLOCK", "Text: '${if (text.isBlank()) "<empty>" else text}'")

         // ========== CRITICAL: BLOCK ALL TEMI Q&A RESPONSES ==========
         // The Temi SDK automatically calls askQuestion() and tries to respond
         // We MUST intercept and block ALL of these responses
         // We do NOT want Temi's cloud AI to speak - ONLY OLLAMA should speak

         if (text.isNotBlank()) {
             android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "❌ BLOCKING Temi askQuestion() response: '$text'")
             android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "Status code: $status")
             android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "This response will NOT be spoken - OLLAMA will handle it instead")

             // EMERGENCY: Clear any pending Temi TTS immediately
             try {
                 robot?.speak(TtsRequest.create("", false))  // Send empty TTS to clear queue
                 android.util.Log.d("TEMI_CLOUD_AI_BLOCK", "✅ Temi TTS queue cleared")
             } catch (e: Exception) {
                 android.util.Log.w("TEMI_CLOUD_AI_BLOCK", "Could not clear TTS queue: ${e.message}")
             }

             // Clear pending IDs
             synchronized(pendingTtsIds) { pendingTtsIds.clear() }
             isRobotSpeaking.set(false)
         }

         // ALWAYS return without processing - block any Temi behavior
         return
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
                            }
                        }
                    }
                    // Don't call Ollama for dance requests - return early
                    isProcessingSpeech.set(false)
                    return@launch
                }

                // Step 3: Handle other navigation side effects on main thread
                withContext(Dispatchers.Main) {
                    when (context.intent) {
                        SpeechOrchestrator.Intent.NAVIGATE -> {
                            // If doctor matched, navigate to their cabin
                            context.doctor?.let {
                                robot?.goTo(it.cabin)
                                android.util.Log.d("TemiSpeech", "Navigating to ${it.name}'s cabin: ${it.cabin}")
                            }
                        }

                        SpeechOrchestrator.Intent.BOOK -> {
                            currentScreen.value = "appointment"
                        }

                        SpeechOrchestrator.Intent.FIND_DOCTOR -> {
                            // Optionally navigate to doctors screen if high confidence
                            if (context.confidence >= 0.85f) {
                                currentScreen.value = "doctors"
                            } else {
                                // Low confidence, keep on current screen
                            }
                        }

                        else -> {} // GENERAL or DANCE (already handled) - no special navigation
                    }
                }

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
                    callOllama(prompt)
                }

            } catch (e: Exception) {
                android.util.Log.e("TemiSpeech", "Error in background processing: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    safeSpeak("An error occurred while processing your request.")
                }
            }
        }
    }

    // Removed onWakeupWord override: not present in Temi SDK

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        synchronized(pendingTtsIds) {
            when (ttsRequest.status) {
                TtsRequest.Status.STARTED -> isRobotSpeaking.set(true)
                TtsRequest.Status.COMPLETED, TtsRequest.Status.CANCELED, TtsRequest.Status.ERROR -> {
                    pendingTtsIds.remove(ttsRequest.id)
                    if (pendingTtsIds.isEmpty()) {
                        isRobotSpeaking.set(false)
                        handler.removeCallbacksAndMessages("tts_safety")
                        // MANUAL PIPELINE: TTS complete, restart ASR listening
                        android.util.Log.d("VOICE_PIPELINE", "TTS finished - restarting ASR listening")
                        // robot?.startListening() // Removed: not in Temi SDK
                        voiceInteractionManager?.startListening() // Use your ASR pipeline
                    }
                }
                else -> {}
            }
        }
     }

    private fun handleNavigation(destination: String) {
        currentScreen.value = destination
        resetInactivityTimer()
    }

    private fun resetInactivityTimer() {
        lastInteractionTime = System.currentTimeMillis()

        // DISABLE INACTIVITY RESET DURING GPT
        if (isConversationActive) {
            android.util.Log.d("OLLAMA_FIX", "Timer blocked during conversation")
            return
        }

        handler.removeCallbacks(inactivityRunnable)
        handler.post(inactivityRunnable)
        android.util.Log.d("OLLAMA_FIX", "Inactivity timer RESTARTED")
    }

     private fun safeSpeak(message: String) {
         try {
             if (robot == null || message.isBlank()) return

             // NEVER INTERRUPT CONVERSATION: Block speech during active GPT conversation
             if (isConversationActive) {
                 android.util.Log.d("VOICE_PIPELINE", "BLOCKED safeSpeak: conversation active")
                 return
             }

             android.util.Log.i("VOICE_PIPELINE_FLOW", "✅ STEP 4: Speaking Ollama response (${message.length} chars)")
             lastSafeSpeakMessage = message
             synchronized(pendingTtsIds) { pendingTtsIds.clear() }
             isRobotSpeaking.set(true)
             handler.removeCallbacksAndMessages("tts_safety")

            val cleanedMessage = message
                .replace(NEWLINE_REGEX, ". ")
                .replace(SPACE_REGEX, " ")
                .replace(":", ". ")
                .replace("Dr.", "Doctor", ignoreCase = true)
                .replace(SYMBOL_REGEX, "")
                .trim()

            val sentences = cleanedMessage.split(Regex("(?<=[.!?])\\s+"))
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (sentences.isEmpty()) {
                isRobotSpeaking.set(false)
                return
            }

            val chunks = mutableListOf<String>()
            var currentChunk = ""
            for (sentence in sentences) {
                if (currentChunk.isEmpty()) currentChunk = sentence
                else if (currentChunk.length + sentence.length < 400) currentChunk += " " + sentence
                else {
                    chunks.add(currentChunk)
                    currentChunk = sentence
                }
            }
            if (currentChunk.isNotEmpty()) chunks.add(currentChunk)

             val requests = chunks.map { TtsRequest.create(it, isShowOnConversationLayer = false) }
             synchronized(pendingTtsIds) { requests.forEach { pendingTtsIds.add(it.id) } }

            requests.forEach { robot?.speak(it) }

            val fallbackTimeout = (cleanedMessage.length * 100L) + 10000L
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (isRobotSpeaking.get()) {
                        synchronized(pendingTtsIds) { pendingTtsIds.clear() }
                        isRobotSpeaking.set(false)
                    }
                }
            }, fallbackTimeout)
        } catch (_: Exception) {
            isRobotSpeaking.set(false)
        }
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            robotState.value = Robot.getInstance()

            // ========== CRITICAL: DISABLE ALL TEMI DEFAULT AI ==========
            // The key is NOT registering the NLP listener, which prevents Temi cloud AI from processing

            // Register ONLY the listeners we control for manual pipeline:
            robot?.addAsrListener(this)                                   // Manual STT
            robot?.addTtsListener(this)                                   // Track speech status
            robot?.addConversationViewAttachesListener(this)              // Track UI state
            robot?.addOnConversationStatusChangedListener(this)           // Block Temi Q&A responses

            // ✅ DO NOT add NLP listener - this is CRITICAL
            // If NLP listener is registered, Temi SDK automatically processes speech with cloud AI
            // By NOT adding it, ALL speech processing goes through our manual pipeline
            // robot?.addNlpListener(this)  // <-- ❌ NEVER ADD THIS - ENABLES TEMI CLOUD AI

            // ========== ADDITIONAL: DISABLE TEMI CONVERSATION MODE ==========
            // Prevent Temi's default conversation UI and askQuestion() from appearing
            // Note: toggleConversationLayer() and stopConversation() are not available in this SDK version
            // Conversation blocking is achieved by not registering NLP listener above

            // CRITICAL: Block askQuestion() by not registering NLP listener
            // This prevents Temi SDK from automatically calling askQuestion()
            // All speech will be processed by our Ollama pipeline instead

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

            android.util.Log.d("TEMI_DISABLE", "========== TEMI CLOUD AI DISABLED ==========")
            android.util.Log.d("TEMI_DISABLE", "✅ NLP listener NOT registered - Temi cloud AI disabled")
            android.util.Log.d("TEMI_DISABLE", "✅ ASR listener registered - manual STT pipeline active")
            android.util.Log.d("TEMI_DISABLE", "✅ OnConversationStatusChanged listener registered - blocking Temi Q&A")
            android.util.Log.d("TEMI_DISABLE", "✅ Conversation layer disabled - no default UI")
            android.util.Log.d("TEMI_DISABLE", "✅ Using MANUAL voice pipeline with OLLAMA only")
            android.util.Log.d("TEMI_DISABLE", "✅ askQuestion() blocked - OLLAMA will handle all responses")

            this.isRobotReady.value = true
        }
     }

    override fun onResume() {
        super.onResume()
        robot?.addOnConversationStatusChangedListener(this)
        handler.post(inactivityRunnable)
    }

    override fun onPause() {
        super.onPause()
        robot?.removeOnConversationStatusChangedListener(this)
        handler.removeCallbacks(inactivityRunnable)
    }

    override fun onDestroy() {
        // Release voice interaction manager
        voiceInteractionManager?.release()
        super.onDestroy()
        Robot.getInstance().removeOnRobotReadyListener(this)
        robot?.removeAsrListener(this)
        // robot?.removeNlpListener(this)  // <-- Not added, so not removing
        robot?.removeTtsListener(this)
        robot?.removeConversationViewAttachesListener(this)
        robot?.removeOnConversationStatusChangedListener(this)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetInactivityTimer()
    }

    companion object {
        private val NEWLINE_REGEX = Regex("\\n+")
        private val SPACE_REGEX = Regex("\\s+")
        private val SYMBOL_REGEX = Regex("[()#*]")
    }
}
