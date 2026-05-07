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
import com.example.alliswelltemi.network.OllamaOptions
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
import com.example.alliswelltemi.utils.speakWithLanguage
import com.example.alliswelltemi.utils.isHindi

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

    // LIPSYNC FIX 2: Viseme state for 3D avatar lip sync animation
    // Updated in real-time by TtsLipSyncManager callbacks, passed to TemiMainScreen
    private val currentViseme = mutableStateOf("viseme_sil")
    private val currentIntensity = mutableStateOf(0f)

    // Production-grade voice pipeline
    private lateinit var orchestrator: SpeechOrchestrator

    // LIPSYNC FIX 1: TTS-based lip sync manager (estimated visemes from text)
    private lateinit var ttsLipSyncManager: com.example.alliswelltemi.utils.TtsLipSyncManager

    // Conversation Context - maintains chat history
    private val conversationContext = ConversationContext(
        maxHistoryItems = 5,
        maxContextLength = 2000
    )

    // Maintain last question context for follow-up
    private var lastQuestionContext: String? = null

    // CRITICAL: SINGLE GLOBAL STATE - ensures ONLY ONE conversation at a time
    // Treat as MUTEX LOCK - no parallel operations allowed
    private val conversationLock = java.lang.Object()
    @Volatile
    private var isConversationActive = false
    // REMOVED: isGptProcessing (redundant, causes race conditions)

    private var lastToastTime: Long = 0

    private var lastInteractionTime: Long = System.currentTimeMillis()
    private val INACTIVITY_TIMEOUT: Long = 10 * 60 * 1000 // 10 minutes (was 5m)
    private val handler = Handler(Looper.getMainLooper())

    // Handler callback refs for safe cleanup (prevents memory leaks)
    private var safeSpeak_Runnable: Runnable? = null
    private var safeSpeakDuringStreaming_Runnable: Runnable? = null
    private val inactivityRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteractionTime >= INACTIVITY_TIMEOUT) {
                // NEVER reset activity during active conversation or while speaking
                if (!isRobotSpeaking.get() && !isConversationActive) {
                    android.util.Log.i("TemiLifecycle", "Inactivity timeout reached, returning to main screen and clearing context")
                    if (currentScreen.value != "main") {
                        currentScreen.value = "main"
                    }
                    // CLEAR Conversation Memory after 30s of inactivity
                    conversationContext.clearHistory()
                    conversationContext.resetSessionTimer()
                } else {
                    // If active, postpone check instead of just stopping
                    android.util.Log.d("TemiLifecycle", "Inactivity timeout reached but system is busy (speaking=${isRobotSpeaking.get()}, active=$isConversationActive). Postponing clear.")
                    lastInteractionTime = System.currentTimeMillis() // Reset timer to give another full window
                    conversationContext.resetSessionTimer()
                }
            }
            handler.postDelayed(this, 5000) // Check every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        android.util.Log.i("TemiMain", "========== APPLICATION START ==========")

        // NEW: Initialize Ollama config
        com.example.alliswelltemi.utils.OllamaConfig.init(this)
        val ollamaUrl = com.example.alliswelltemi.utils.OllamaConfig.getServerUrl()
        android.util.Log.d("TemiMain", "Ollama server URL: $ollamaUrl")
        // Eagerly initialize TTS manager to avoid speech delay after Ollama response
        // Removed TemiTTSManager initialization (now using Temi's built-in TTS only)

        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            Robot.getInstance().addOnRobotReadyListener(this)

            doctorsViewModel = DoctorsViewModel(application = application)

            // Initialize orchestrator with empty list and null robot (will be updated when doctors load and robot is ready)
            orchestrator = SpeechOrchestrator(emptyList(), null)

            // LIPSYNC FIX 1: Initialize TTS-based lip sync manager
            // This estimates visemes from text duration to approximate mouth movements
            ttsLipSyncManager = com.example.alliswelltemi.utils.TtsLipSyncManager(
                coroutineScope = lifecycleScope,
                onVisemeUpdate = { viseme, intensity ->
                    // LIPSYNC FIX 2: Forward viseme updates to UI state
                    // This drives the 3D avatar lip sync on the main screen
                    currentViseme.value = viseme
                    currentIntensity.value = intensity
                    android.util.Log.d("LIPSYNC_VISEME", "✓ Viseme: $viseme, intensity: $intensity")
                }
            )
            android.util.Log.d("TemiMain", "✓ TtsLipSyncManager initialized for lip sync")

            lifecycleScope.launch {
                snapshotFlow { doctorsViewModel.doctors.value }.collectLatest { doctors ->
                    if (doctors.isNotEmpty()) {
                        // Update orchestrator with fresh doctor list and current robot instance
                        orchestrator = SpeechOrchestrator(doctors, robot)

                        // CRITICAL: Inject dynamic doctor Q&As into knowledge base
                        // This synchronizes Strapi doctor data with the RAG knowledge base
                        com.example.alliswelltemi.data.HospitalKnowledgeBase.injectDoctorQAs(doctors)
                        android.util.Log.i("TemiMain", "✅ Knowledge base synchronized with ${doctors.size} doctors from Strapi")

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastToastTime > 3000) {
                            android.widget.Toast.makeText(this@MainActivity, "✓ ${doctors.size} doctors loaded & synced", android.widget.Toast.LENGTH_SHORT).show()
                            lastToastTime = currentTime
                        }
                    }
                }
            }

            setContent {
                val currentRobot = robotState.value
                val isConversationActive = conversationActiveState.value
                val viseme = currentViseme.value
                val intensity = currentIntensity.value

                TemiTheme(darkTheme = true) {
                    when (currentScreen.value) {
                        "navigation" -> {
                            val navViewModel: NavigationViewModel = viewModel()
                            NavigationScreen(
                                robot = currentRobot,
                                viewModel = navViewModel,
                                onBackPress = { currentScreen.value = "main" }
                            )
                        }
                        "appointment" -> {
                            AppointmentBookingScreen(
                                robot = currentRobot,
                                onBackPress = { currentScreen.value = "main" },
                                viewModel = appointmentViewModel,
                                doctorsViewModel = doctorsViewModel,
                                currentLanguage = "en"
                            )
                        }
                        "doctors" -> {
                            DoctorsScreen(
                                robot = currentRobot,
                                viewModel = doctorsViewModel,
                                onBackPress = { currentScreen.value = "main" },
                                onSelectDoctor = { /* Just navigate to doctor, don't go to appointment */ },
                                selectedDoctorId = selectedDoctorIdForScreen
                            )
                        }
                        "availableSoon" -> {
                            AvailableSoonScreen(
                                onBackPress = { currentScreen.value = "main" },
                                currentLanguage = "en",
                                featureName = "Appointment Booking"
                            )
                        }
                        "feedback" -> {
                            FeedbackScreen(
                                robot = currentRobot,
                                onBackPress = { currentScreen.value = "main" }
                            )
                        }
                        else -> {
                            TemiMainScreen(
                                robot = currentRobot,
                                onNavigate = { screen: String ->
                                    currentScreen.value = screen
                                },
                                currentViseme = viseme,
                                currentIntensity = intensity,
                                doctorsViewModel = doctorsViewModel
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiMain", "FATAL: Crash in onCreate: ${e.message}", e)
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
        // FIXED: Cancel all pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        safeSpeak_Runnable = null
        safeSpeakDuringStreaming_Runnable = null

        // LIPSYNC FIX: Release TtsLipSyncManager resources
        ttsLipSyncManager.release()

        voiceInteractionManager?.release()
        Robot.getInstance().removeOnRobotReadyListener(this)
        robot?.removeAsrListener(this)
        robot?.removeTtsListener(this)
        robot?.removeConversationViewAttachesListener(this)
        robot?.removeOnConversationStatusChangedListener(this)

        super.onDestroy()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionTime = System.currentTimeMillis()
        conversationContext.resetSessionTimer()
    }

    companion object {
        private const val NEWLINE_REGEX = "(\\r\\n|\\n|\\r)"
        private const val SPACE_REGEX = "\\s+"
        private const val SYMBOL_REGEX = "[*#_]"
    }

    // ASR Listener
    override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
        android.util.Log.i("VOICE_PIPELINE_FLOW", "✅ STEP 2: ASR Received '$asrResult' ($sttLanguage)")
        processSpeech(asrResult)
    }

    // NLP Listener (required for interface, but NOT registered to disable cloud AI)
    override fun onNlpCompleted(nlpResult: NlpResult) {
        android.util.Log.d("TEMI_DISABLE", "BLOCKED onNlpCompleted: cloud AI ignored")
    }

    // TTS Listener
    private val pendingTtsIds = mutableSetOf<UUID>()
    private val isRobotSpeaking = AtomicBoolean(false)

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        synchronized(pendingTtsIds) {
            when (ttsRequest.status) {
                TtsRequest.Status.STARTED -> {
                    isRobotSpeaking.set(true)
                    // LIPSYNC FIX 2: Log TTS start for monitoring
                    android.util.Log.d("TTS_LIFECYCLE", "✓ TTS started (${ttsRequest.id})")
                }
                TtsRequest.Status.COMPLETED, TtsRequest.Status.CANCELED, TtsRequest.Status.ERROR -> {
                    // LIPSYNC FIX 2: Stop lip sync when TTS completes
                    ttsLipSyncManager.stopLipSync()
                    android.util.Log.d("TTS_LIFECYCLE", "✓ TTS completed - lip sync stopped (${ttsRequest.id})")
                    
                    pendingTtsIds.remove(ttsRequest.id)
                    if (pendingTtsIds.isEmpty()) {
                        isRobotSpeaking.set(false)
                        handler.removeCallbacksAndMessages("tts_safety")
                        // CRITICAL FIX: Use restartListeningWithDelay for proper state management
                        android.util.Log.d("VOICE_PIPELINE", "TTS finished - restarting ASR listening with delay")
                        voiceInteractionManager?.restartListeningWithDelay()
                    } else {
                        // Still have pending TTS, don't mark as done speaking yet
                    }
                }
                else -> {}
            }
        }
    }

    // Conversation statuses
    override fun onConversationStatusChanged(status: Int, text: String) {
        android.util.Log.d("TEMI_DISABLE", "onConversationStatusChanged: status=$status, text='$text'")
        // If Temi tries to speak something through its default AI (status 1=talking), finish it
        if (status == 1) {
            android.util.Log.w("TEMI_DISABLE", "🛑 INTERCEPTED default Temi speech attempt! Killing conversation.")
            robot?.finishConversation()
        }
    }

    override fun onConversationAttaches(isAttached: Boolean) {
        android.util.Log.d("TEMI_DISABLE", "Conversation UI Attached: $isAttached")
    }

    private var isGptProcessing = false
    private var lastProcessedText = ""

    private fun callOllama(prompt: String) {
        // Atomic check-then-act using synchronized block
        synchronized(conversationLock) {
            if (isConversationActive) {
                android.util.Log.w("CONVERSATION_LOCK", "Conversation already active, dropping request")
                return
            }
            isConversationActive = true
        }

        conversationActiveState.value = true
        android.util.Log.i("OLLAMA_FIX", "========== OLLAMA REQUEST START ==========")
        android.util.Log.d("OLLAMA_FIX", "Conversation lock ACQUIRED")

        lifecycleScope.launch(Dispatchers.IO) {
            val gptRequestStartTime = System.currentTimeMillis()
            val cleanedPrompt = prompt.trim()

            try {
                // Step 2: Call Ollama with manual context injection
                val ollamaRequest = OllamaRequest(
                    model = "llama3:8b",
                    prompt = cleanedPrompt,
                    stream = true,
                    options = OllamaOptions(num_predict = 150)  // Limit to ~2-3 sentences for speed
                )
                android.util.Log.d("OLLAMA_FIX", "Ollama streaming request created")

                // Collect streaming response and speak COMPLETE response at once
                val fullResponse = StringBuilder()
                var firstChunkTime: Long? = null

                // PRODUCTION FIX: Cache disabled by default (cacheEnabled = false)
                // Enable only for specific queries if desired
                OllamaClient.generateStreaming(ollamaRequest, cacheEnabled = false).collect { chunk ->
                    if (firstChunkTime == null) {
                        firstChunkTime = System.currentTimeMillis()
                        val timeToFirstChunk = firstChunkTime!! - gptRequestStartTime
                        android.util.Log.d("OLLAMA_PERF", "⚡ First chunk received in ${timeToFirstChunk}ms")
                    }

                    fullResponse.append(chunk)
                }

                val finalResponse = fullResponse.toString().trim()
                val elapsedMs = System.currentTimeMillis() - gptRequestStartTime

                android.util.Log.d("OLLAMA_FIX", "========== OLLAMA RESPONSE RECEIVED ==========")
                android.util.Log.d("OLLAMA_FIX", "Response received after ${elapsedMs}ms")
                android.util.Log.d("OLLAMA_FIX", "Complete response (${finalResponse.length} chars): $finalResponse")

                // Step 4: Save to conversation context
                val lastQuestion = if (cleanedPrompt.contains("User: ")) {
                    cleanedPrompt.substringAfter("User: ").substringBefore("\n\nIMPORTANT")
                } else {
                    "Unknown question"
                }
                conversationContext.addTurn(lastQuestion, finalResponse)

                // RELEASE conversation lock and speak COMPLETE response
                withContext(Dispatchers.Main) {
                    synchronized(conversationLock) {
                        isConversationActive = false
                    }
                    conversationActiveState.value = false
                    android.util.Log.d("OLLAMA_FIX", "Conversation lock RELEASED")

                    // Speak the FULL response at once
                    android.util.Log.d("OLLAMA_PERF", "🔊 Speaking complete response (${finalResponse.length} chars)")
                    safeSpeak(finalResponse)
                    handler.post(inactivityRunnable)
                }

            } catch (e: Exception) {
                android.util.Log.e("OLLAMA_FIX", "Exception: ${e.message}", e)
                val fallbackResponse = RagContextBuilder.generateFallbackResponse(cleanedPrompt, doctorsViewModel.doctors.value)
                withContext(Dispatchers.Main) {
                    safeSpeak(fallbackResponse)
                    synchronized(conversationLock) {
                        isConversationActive = false
                    }
                    conversationActiveState.value = false
                    handler.post(inactivityRunnable)
                }
            }
        }
    }

    private fun processSpeech(text: String) {
        if (text.isBlank() || isConversationActive) return
        if (text == lastProcessedText) return

        lastProcessedText = text
        robot?.finishConversation()
        resetInactivityTimer()
        conversationContext.resetSessionTimer()

        // Intent detection
        val intentType = com.example.alliswelltemi.utils.TemiUtils.detectIntent(text)
        android.util.Log.d("INTENT_DETECTION", "Detected intent: $intentType for input: '$text'")

        // Maintain context for follow-up questions
        if (intentType == "QUESTION") {
            lastQuestionContext = text
        } else if (intentType == "COMMAND") {
            lastQuestionContext = null // Clear context on command
        } else if (intentType == "UNKNOWN" && lastQuestionContext != null) {
            // If input is ambiguous but we have a previous question, treat as follow-up
            android.util.Log.d("INTENT_DETECTION", "Treating as follow-up to: $lastQuestionContext")
        }

        // Check if doctors are currently loading OR if list is empty (fully loaded but no data)
        val doctors = doctorsViewModel.doctors.value
        val isLoadingDoctors = doctorsViewModel.isLoading.value

        if (isLoadingDoctors) {
            safeSpeak("Doctors list is still loading. Please wait a moment.")
            return
        }

        if (doctors.isEmpty()) {
            safeSpeak("Doctor information is currently unavailable. Please try again later.")
            return
        }

        lifecycleScope.launch {
            try {
                val context = withContext(Dispatchers.Default) {
                    orchestrator.analyze(text)
                }

                if (context.intent == SpeechOrchestrator.Intent.DANCE) {
                    withContext(Dispatchers.Main) {
                        lifecycleScope.launch {
                            try {
                                val danceMove = context.danceMove ?: DanceService.DanceMove.SMOOTH_GROOVE
                                // LIPSYNC FIX 5: Pass TtsLipSyncManager to DanceService
                                DanceService.performDance(
                                    robot = robot,
                                    danceMove = danceMove,
                                    language = "en",
                                    ttsLipSyncManager = ttsLipSyncManager
                                ) { }
                            } catch (e: Exception) {
                                safeSpeak("Oops! I had trouble dancing. Please try again.")
                            }
                        }
                    }
                    return@launch
                }

                var intentHandled = false
                withContext(Dispatchers.Main) {
                    when (context.intent) {
                        SpeechOrchestrator.Intent.NAVIGATE -> {
                            // LOCATION NAVIGATION: Priority 1 - Hospital location
                            if (context.location != null) {
                                val location = context.location
                                android.util.Log.d("NAVIGATE_DEBUG", "🎯 NAVIGATE Intent triggered!")
                                android.util.Log.d("NAVIGATE_DEBUG", "   Location found: ${location.name}")
                                android.util.Log.d("NAVIGATE_DEBUG", "   Location mapName: ${location.mapName}")
                                android.util.Log.d("NAVIGATE_DEBUG", "   Location nameHi: ${location.nameHi}")

                                try {
                                    // Use appropriate language for location name
                                    val detectedLanguage = if (isHindi(text)) "hi" else "en"
                                    val locationName = location.getNameInLanguage(detectedLanguage)

                                    // Try mapName first, then fall back to name
                                    // mapName is the Temi SDK location identifier
                                    // name is the human-readable location name
                                    val navigationTarget = if (location.mapName.isNotEmpty()) {
                                        location.mapName
                                    } else {
                                        location.name
                                    }

                                    android.util.Log.d("NAVIGATE_DEBUG", "   Navigation target: $navigationTarget")

                                    // Speak confirmation in appropriate language
                                    val confirmationText = if (detectedLanguage == "hi") {
                                        "ठीक है, आपको ${locationName} ले जा रहे हैं।"
                                    } else {
                                        "Sure, taking you to the ${locationName}."
                                    }

                                    android.util.Log.d("NAVIGATE_DEBUG", "   Speaking: $confirmationText")
                                    safeSpeak(confirmationText)

                                    // Log all available locations on robot FIRST
                                    val availableLocations = robot?.locations ?: emptyList()
                                    android.util.Log.i("LOCATION_NAV", "📍 Robot has ${availableLocations.size} saved locations:")
                                    availableLocations.forEach { robotLoc ->
                                        android.util.Log.i("LOCATION_NAV", "  - '$robotLoc'")
                                    }

                                    // IMPORTANT: Use the EXACT location name from robot's list
                                    // First, try to find exact match in robot's saved locations
                                    val robotLocationMatch = availableLocations.find { robotLoc ->
                                        robotLoc.equals(navigationTarget, ignoreCase = true) ||
                                        robotLoc.equals(location.name, ignoreCase = true) ||
                                        robotLoc.lowercase().replace(" ", "") == navigationTarget.lowercase().replace(" ", "")
                                    }

                                    android.util.Log.i("LOCATION_NAV", "✅ Navigating to: '$navigationTarget'")
                                    android.util.Log.d("NAVIGATE_DEBUG", "   Starting navigation attempts...")
                                    android.util.Log.d("NAVIGATE_DEBUG", "   Robot location match found: '${robotLocationMatch ?: "NONE"}'")

                                    // Try the robot's exact location name first if found
                                    val navigationAttempts = mutableListOf<String>()
                                    if (robotLocationMatch != null) {
                                        navigationAttempts.add(robotLocationMatch)  // EXACT name from robot
                                    }
                                    // Then try variations
                                    navigationAttempts.add(navigationTarget)  // "MAIN PHARMACY"
                                    navigationAttempts.add(navigationTarget.lowercase())  // "main pharmacy"
                                    navigationAttempts.add(navigationTarget.replace(" ", "_"))  // "MAIN_PHARMACY"
                                    navigationAttempts.add(location.id)  // "main_pharmacy"

                                    var navigated = false
                                    for (target in navigationAttempts) {
                                        android.util.Log.d("NAVIGATE_DEBUG", "   Attempting goTo('$target')")
                                        try {
                                            robot?.goTo(target)
                                            android.util.Log.d("NAVIGATE_DEBUG", "   ✅ goTo('$target') executed!")
                                            navigated = true
                                            break  // Stop if one succeeds
                                        } catch (navException: Exception) {
                                            android.util.Log.d("NAVIGATE_DEBUG", "   ⚠️ goTo('$target') error: ${navException.message}")
                                        }
                                    }

                                    if (!navigated) {
                                        android.util.Log.w("NAVIGATE_DEBUG", "   ⚠️ Navigation attempted but result unclear")
                                    }

                                    intentHandled = true
                                } catch (e: Exception) {
                                    android.util.Log.e("LOCATION_NAV", "❌ Navigation failed", e)
                                    android.util.Log.e("NAVIGATE_DEBUG", "❌ Exception: ${e.message}")
                                    safeSpeak("Sorry, I could not navigate to that location.")
                                }
                            }
                            // LOCATION NAVIGATION: Priority 2 - Doctor's cabin (fallback)
                            else if (context.doctor != null) {
                                context.doctor.let { doctor ->
                                    try {
                                        val detectedLanguage = if (isHindi(text)) "hi" else "en"
                                        val confirmationText = if (detectedLanguage == "hi") {
                                            "${doctor.name} के कैबिन ${doctor.cabin} में ले जा रहे हैं।"
                                        } else {
                                            "Taking you to ${doctor.name}'s cabin ${doctor.cabin}."
                                        }

                                        safeSpeak(confirmationText)
                                        android.util.Log.i("DOCTOR_NAV", "🏥 Navigating to doctor cabin: ${doctor.cabin}")
                                        robot?.goTo(doctor.cabin)
                                        intentHandled = true
                                    } catch (e: Exception) {
                                        android.util.Log.e("DOCTOR_NAV", "❌ Navigation failed", e)
                                        safeSpeak("Sorry, I could not navigate to that cabin.")
                                    }
                                }
                            }
                            else {
                                android.util.Log.d("NAVIGATE_INTENT", "⚠️ NAVIGATE intent detected but no location or doctor found")
                            }
                        }
                        SpeechOrchestrator.Intent.BOOK -> {
                            currentScreen.value = "appointment"
                            intentHandled = true
                        }
                        SpeechOrchestrator.Intent.FIND_DOCTOR -> {
                            if (context.confidence >= 0.85f) {
                                currentScreen.value = "doctors"
                                // We might still want Ollama to explain WHICH doctors if they asked "Which cardiologists do you have?"
                                // So we don't set intentHandled = true here necessarily, or we do if it's just "Show doctors"
                                if (context.doctor == null && context.department == null) {
                                    intentHandled = true
                                }
                            }
                        }
                        else -> {}
                    }
                    Unit
                }

                // If the intent was a direct action (Navigate/Book/Dance), don't call Ollama
                if (intentHandled) {
                    android.util.Log.d("PROCESS_SPEECH", "Intent handled via direct action, skipping Ollama.")
                    return@launch
                }

                val prompt = withContext(Dispatchers.Default) {
                    RagContextBuilder.buildOllamaPrompt(
                        query = text,
                        doctors = doctors,
                        historyContext = conversationContext.getContextString()
                        // Removed previousQuestion argument, as it does not exist in the function signature
                    )
                }

                withContext(Dispatchers.Main) {
                    callOllama(prompt)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { safeSpeak("An error occurred while processing your request.") }
            }
        }
    }

    private fun safeSpeak(message: String) {
         try {
             if (robot == null || message.isBlank() || isConversationActive) return

             // Detect language FIRST before cleaning
             val detectedLanguage = if (com.example.alliswelltemi.utils.isHindi(message)) "hi" else "en"

             // CRITICAL FIX: Standardize hospital name references
             // Replace any "AAMIS" hallucinations with correct hospital name
             var standardizedMessage = message

             // Check if AAMIS was present for logging
             if (standardizedMessage.contains(Regex("(?i)AAMIS"))) {
                 android.util.Log.w("HOSPITAL_NAME_FIX", "⚠️ Detected AAMIS hallucination in response, replacing with 'All Is Well Hospital'")
             }

             standardizedMessage = standardizedMessage
                 .replace(Regex("(?i)AAMIS"), "All Is Well Hospital")  // Exact match case-insensitive
                 .replace(Regex("(?i)AAMIS Hospital"), "All Is Well Hospital")  // With Hospital
                 .replace(Regex("(?i)A\\.?A\\.?M\\.?I\\.?S\\.?"), "All Is Well Hospital")  // Abbreviated forms

             // OPTIMIZED: Clean message differently based on language
             val cleanedMessage = if (detectedLanguage == "hi") {
                 // Hindi: Minimal punctuation changes, rely on TTS normalization
                 standardizedMessage
                     .replace(NEWLINE_REGEX, " ")         // Convert newlines to space
                     .replace(Regex("!+"), "")            // Remove exclamation marks completely
                     .replace(Regex("\\.{2,}"), "")       // Remove multiple periods
                     .replace(":", "")                     // Remove colons
                     .replace(";", "")                     // Remove semicolons
                     .replace(SPACE_REGEX, " ")           // Normalize whitespace
                     .replace("Dr.", "Doctor", ignoreCase = true)
                     .replace(SYMBOL_REGEX, "")
                     .trim()
             } else {
                 // English: Use comma replacement for smoother flow
                 standardizedMessage
                     .replace(NEWLINE_REGEX, " ")         // Convert newlines to space
                     .replace(Regex("[.!]+"), ",")        // Replace periods/exclamations with comma (less pause)
                     .replace(Regex(",\\s*,+"), ",")      // Remove duplicate commas
                     .replace(":", ",")                    // Replace colons with comma
                     .replace(";", ",")                    // Replace semicolons with comma
                     .replace(SPACE_REGEX, " ")           // Normalize whitespace
                     .replace("Dr.", "Doctor", ignoreCase = true)
                     .replace(SYMBOL_REGEX, "")
                     .replace(Regex(",\\s*$"), "")        // Remove trailing comma
                     .trim()
             }

            // For Hindi, don't split - speak as one chunk (TTS handles pauses)
            // For English, split on commas for better pacing
            val chunks = if (detectedLanguage == "hi") {
                listOf(cleanedMessage)  // Hindi: speak entire message at once
            } else {
                // English: split on commas and chunk intelligently
                val sentences = cleanedMessage.split(Regex("(?<=[,])\\s+"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                if (sentences.isEmpty()) return

                val result = mutableListOf<String>()
                var currentChunk = ""
                for (sentence in sentences) {
                    if (currentChunk.isEmpty()) currentChunk = sentence
                    else if (currentChunk.length + sentence.length < 400) currentChunk += " " + sentence
                    else {
                        result.add(currentChunk)
                        currentChunk = sentence
                    }
                }
                if (currentChunk.isNotEmpty()) result.add(currentChunk)
                result
            }

            if (chunks.isEmpty()) return

            isRobotSpeaking.set(true)

            // Use speakWithLanguage for multi-lingual TTS support (Temi built-in TTS)
            chunks.forEach { chunk ->
                speakWithLanguage(
                    text = chunk,
                    language = detectedLanguage,
                    robot = robot
                )

                // LIPSYNC FIX 3: Start lip sync with this chunk
                // TtsLipSyncManager estimates visemes based on text analysis
                ttsLipSyncManager.startLipSync(chunk)
                android.util.Log.d("LIPSYNC", "Started lip sync for chunk: ${chunk.take(50)}...")
            }

            // LIPSYNC FIX 3: Calculate accurate speech duration
            val charsPerSecond = if (detectedLanguage == "hi") 13f else 15f
            val estimatedSeconds = cleanedMessage.length / charsPerSecond
            val speechDurationMs = (estimatedSeconds * 1000).toLong() + 500

            // FIXED: Cancel previous callback, create new one
            safeSpeak_Runnable?.let { handler.removeCallbacks(it) }
            safeSpeak_Runnable = Runnable {
                if (isRobotSpeaking.get()) {
                    isRobotSpeaking.set(false)
                    ttsLipSyncManager.stopLipSync()  // Ensure lip sync stops
                }
            }
            handler.postDelayed(safeSpeak_Runnable!!, speechDurationMs)

        } catch (_: Exception) {
            isRobotSpeaking.set(false)
        }
    }

    /**
     * PERFORMANCE FIX: Speak during streaming without conversation lock blocking
     * Used for early streaming responses to provide instant feedback
     * OPTIMIZED: Reduced punctuation gaps for smoother TTS
     */
    private fun safeSpeakDuringStreaming(message: String) {
        try {
            if (robot == null || message.isBlank()) return

            // Detect language FIRST before cleaning
            val detectedLanguage = if (com.example.alliswelltemi.utils.isHindi(message)) "hi" else "en"

            // OPTIMIZED: Clean message differently based on language
            val cleanedMessage = if (detectedLanguage == "hi") {
                // Hindi: Minimal punctuation changes, rely on TTS normalization
                message
                    .replace(NEWLINE_REGEX, " ")         // Convert newlines to space
                    .replace(Regex("!+"), "")            // Remove exclamation marks completely
                    .replace(Regex("\\.{2,}"), "")       // Remove multiple periods
                    .replace(":", "")                     // Remove colons
                    .replace(";", "")                     // Remove semicolons
                    .replace(SPACE_REGEX, " ")           // Normalize whitespace
                    .replace("Dr.", "Doctor", ignoreCase = true)
                    .replace(SYMBOL_REGEX, "")
                    .trim()
            } else {
                // English: Use comma replacement for smoother flow
                message
                    .replace(NEWLINE_REGEX, " ")         // Convert newlines to space
                    .replace(Regex("[.!]+"), ",")        // Replace periods/exclamations with comma (less pause)
                    .replace(Regex(",\\s*,+"), ",")      // Remove duplicate commas
                    .replace(":", ",")                    // Replace colons with comma
                    .replace(";", ",")                    // Replace semicolons with comma
                    .replace(SPACE_REGEX, " ")           // Normalize whitespace
                    .replace("Dr.", "Doctor", ignoreCase = true)
                    .replace(SYMBOL_REGEX, "")
                    .replace(Regex(",\\s*$"), "")        // Remove trailing comma
                    .trim()
            }

            android.util.Log.d("OLLAMA_PERF", "💬 Speaking during stream: '$cleanedMessage'")

            isRobotSpeaking.set(true)

            // Use speakWithLanguage for multi-lingual TTS support (Temi built-in TTS)
            speakWithLanguage(
                text = cleanedMessage,
                language = detectedLanguage,
                robot = robot
            )

            // LIPSYNC FIX: Start lip sync during streaming response
            ttsLipSyncManager.startLipSync(cleanedMessage)
            android.util.Log.d("LIPSYNC", "Started lip sync for streaming: ${cleanedMessage.take(50)}...")

            // LIPSYNC FIX: Calculate accurate speech duration
            val charsPerSecond = if (detectedLanguage == "hi") 13f else 15f
            val estimatedSeconds = cleanedMessage.length / charsPerSecond
            val speechDurationMs = (estimatedSeconds * 1000).toLong() + 500

            // FIXED: Proper callback management
            safeSpeakDuringStreaming_Runnable?.let { handler.removeCallbacks(it) }
            safeSpeakDuringStreaming_Runnable = Runnable {
                if (isRobotSpeaking.get()) {
                    isRobotSpeaking.set(false)
                    ttsLipSyncManager.stopLipSync()  // Ensure lip sync stops
                }
            }
            handler.postDelayed(safeSpeakDuringStreaming_Runnable!!, speechDurationMs)

        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error speaking during streaming: ${e.message}", e)
            isRobotSpeaking.set(false)
        }
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            robotState.value = Robot.getInstance()
            robot?.addAsrListener(this)
            robot?.addTtsListener(this)
            robot?.addConversationViewAttachesListener(this)
            robot?.addOnConversationStatusChangedListener(this)
            robot?.setKioskModeOn(true)
            robot?.hideTopBar()

            // CRITICAL: Update orchestrator with robot instance for location validation
            orchestrator.setRobot(robot)
            android.util.Log.d("TemiMain", "✓ Orchestrator updated with robot instance for location validation")

            // DEBUG: List all available locations on the robot
            try {
                val availableLocations = robot?.locations ?: emptyList()
                android.util.Log.i("LOCATION_NAV", "========== ROBOT LOCATIONS ==========")
                android.util.Log.i("LOCATION_NAV", "Total locations on robot: ${availableLocations.size}")
                availableLocations.forEachIndexed { index, location ->
                    android.util.Log.i("LOCATION_NAV", "${index + 1}. '$location'")
                }
                android.util.Log.i("LOCATION_NAV", "=====================================")
            } catch (e: Exception) {
                android.util.Log.w("LOCATION_NAV", "Could not fetch robot locations: ${e.message}")
            }

            // Removed TemiTTSManager initialization and completion listener (now using Temi's built-in TTS only)

            // LOCATION NAVIGATION: Load Temi map for location-based navigation
            // This enables voice commands like "Take me to pharmacy" or "ले चलो फार्मेसी"
            try {
                robot?.tiltAngle(0)  // Reset tilt for map viewing if needed
                android.util.Log.i("MAP_NAVIGATION", "✅ Temi map ready for location-based navigation")
            } catch (e: Exception) {
                android.util.Log.e("MAP_NAVIGATION", "⚠️ Could not initialize map - location navigation may fail", e)
            }

            try {
                voiceInteractionManager = com.example.alliswelltemi.utils.VoiceInteractionManager(
                    context = this,
                    robot = robot,
                    coroutineScope = lifecycleScope,
                    language = "en"
                )

                // CRITICAL FIX: Start listening immediately when robot is ready
                // This allows user to say "hey temi" right away without delay
                handler.postDelayed({
                    voiceInteractionManager?.startListening()
                    android.util.Log.d("VOICE_PIPELINE", "Voice listening started after robot ready")
                }, 500)  // Small delay to ensure everything is initialized
            } catch (e: Exception) {
                android.util.Log.e("TemiMain", "Failed to initialize VoiceInteractionManager", e)
            }

            this.isRobotReady.value = true
        }
    }

    private fun resetInactivityTimer() {
        lastInteractionTime = System.currentTimeMillis()
        if (isConversationActive) return
        handler.removeCallbacks(inactivityRunnable)
        handler.post(inactivityRunnable)
    }

    private var selectedDoctorIdForScreen: String? = null
}