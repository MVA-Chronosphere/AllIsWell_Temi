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

            doctorsViewModel = DoctorsViewModel(context = this@MainActivity)

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
                                onSelectDoctor = { currentScreen.value = "appointment" }
                            )
                        }
                        else -> {
                            TemiMainScreen(
                                robot = currentRobot,
                                onNavigate = { screen ->
                                    currentScreen.value = screen
                                }
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
        voiceInteractionManager?.release()
        super.onDestroy()
        Robot.getInstance().removeOnRobotReadyListener(this)
        robot?.removeAsrListener(this)
        robot?.removeTtsListener(this)
        robot?.removeConversationViewAttachesListener(this)
        robot?.removeOnConversationStatusChangedListener(this)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionTime = System.currentTimeMillis()
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
    private var lastSafeSpeakMessage = ""

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
                        voiceInteractionManager?.startListening() 
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
        if (isConversationActive || isGptProcessing) {
            android.util.Log.w("OLLAMA_FIX", "Blocked parallel Ollama call")
            return
        }

        // 1. ACQUIRE LOCK (Main Thread)
        isConversationActive = true
        isGptProcessing = true
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

                // PERFORMANCE FIX: Collect streaming response and speak IMMEDIATELY when we have enough
                val fullResponse = StringBuilder()
                var firstChunkTime: Long? = null
                var hasSpokenFirstPart = false
                val sentenceBuffer = StringBuilder()

                OllamaClient.generateStreaming(ollamaRequest).collect { chunk ->
                    if (firstChunkTime == null) {
                        firstChunkTime = System.currentTimeMillis()
                        val timeToFirstChunk = firstChunkTime!! - gptRequestStartTime
                        android.util.Log.d("OLLAMA_PERF", "⚡ First chunk received in ${timeToFirstChunk}ms")
                    }

                    fullResponse.append(chunk)
                    sentenceBuffer.append(chunk)

                    // Speak as soon as we have a complete sentence (ends with . ! ?)
                    if (!hasSpokenFirstPart && sentenceBuffer.length > 20) {
                        val text = sentenceBuffer.toString()
                        if (text.matches(Regex(".*[.!?]\\s*"))) {
                            hasSpokenFirstPart = true
                            val speakText = text.trim()
                            android.util.Log.d("OLLAMA_PERF", "🔊 Speaking first part early (${speakText.length} chars)")

                            // CRITICAL FIX: Temporarily allow speaking during conversation lock
                            withContext(Dispatchers.Main) {
                                safeSpeakDuringStreaming(speakText)
                            }
                            sentenceBuffer.clear()
                        }
                    }
                }

                val finalResponse = fullResponse.toString()
                val elapsedMs = System.currentTimeMillis() - gptRequestStartTime

                android.util.Log.d("OLLAMA_FIX", "========== OLLAMA RESPONSE RECEIVED ==========")
                android.util.Log.d("OLLAMA_FIX", "Response received after ${elapsedMs}ms")

                // Step 4: Save to conversation context
                val lastQuestion = if (cleanedPrompt.contains("User: ")) {
                    cleanedPrompt.substringAfter("User: ").substringBefore("\n\nIMPORTANT")
                } else {
                    "Unknown question"
                }
                conversationContext.addTurn(lastQuestion, finalResponse)

                // RELEASE conversation lock AFTER streaming completes
                withContext(Dispatchers.Main) {
                    isGptProcessing = false
                    isConversationActive = false
                    conversationActiveState.value = false
                    android.util.Log.d("OLLAMA_FIX", "Conversation lock RELEASED")

                    // Only speak if we haven't already started speaking
                    if (!hasSpokenFirstPart) {
                        safeSpeak(finalResponse)
                    }
                    handler.post(inactivityRunnable)
                }

            } catch (e: Exception) {
                android.util.Log.e("OLLAMA_FIX", "Exception: ${e.message}", e)
                val fallbackResponse = RagContextBuilder.generateFallbackResponse(cleanedPrompt, doctorsViewModel.doctors.value)
                withContext(Dispatchers.Main) {
                    safeSpeak(fallbackResponse)
                    isGptProcessing = false
                    isConversationActive = false
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

        val doctors = doctorsViewModel.doctors.value
        if (doctors.isEmpty()) {
            safeSpeak("Doctors list is still loading. Please try again.")
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
                                DanceService.performDance(
                                    robot = robot,
                                    danceMove = danceMove,
                                    context = this@MainActivity,
                                    language = "en"
                                ) { }
                            } catch (e: Exception) {
                                safeSpeak("Oops! I had trouble dancing. Please try again.")
                            }
                        }
                    }
                    isProcessingSpeech.set(false)
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    when (context.intent) {
                        SpeechOrchestrator.Intent.NAVIGATE -> {
                            context.doctor?.let { robot?.goTo(it.cabin) }
                        }
                        SpeechOrchestrator.Intent.BOOK -> { currentScreen.value = "appointment" }
                        SpeechOrchestrator.Intent.FIND_DOCTOR -> {
                            if (context.confidence >= 0.85f) {
                                currentScreen.value = "doctors"
                            } else {
                                // Do nothing or handle low confidence
                            }
                        }
                        else -> {}
                    }
                }

                val prompt = withContext(Dispatchers.Default) {
                    RagContextBuilder.buildOllamaPrompt(
                        query = text, 
                        doctors = doctors,
                        historyContext = conversationContext.getContextString()
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

            val cleanedMessage = message
                .replace(NEWLINE_REGEX, ". ")
                .replace(SPACE_REGEX, " ")
                .replace(":", ". ")
                .replace("Dr.", "Doctor", ignoreCase = true)
                .replace(SYMBOL_REGEX, "")
                .trim()

            // Detect language automatically for better TTS matching
            val detectedLanguage = if (com.example.alliswelltemi.utils.isHindi(cleanedMessage)) "hi" else "en"

            val sentences = cleanedMessage.split(Regex("(?<=[.!?])\\s+"))
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (sentences.isEmpty()) return

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

            isRobotSpeaking.set(true)
            
            // Use speakWithLanguage for multi-lingual TTS support (Google TTS for Hindi)
            chunks.forEach { chunk ->
                speakWithLanguage(
                    context = this@MainActivity,
                    text = chunk,
                    language = detectedLanguage,
                    robot = robot,
                    queueMode = android.speech.tts.TextToSpeech.QUEUE_ADD
                )
            }

            handler.postDelayed({
                if (isRobotSpeaking.get()) {
                    isRobotSpeaking.set(false)
                }
            }, (cleanedMessage.length * 100L) + 10000L)
        } catch (_: Exception) {
            isRobotSpeaking.set(false)
        }
    }

    /**
     * PERFORMANCE FIX: Speak during streaming without conversation lock blocking
     * Used for early streaming responses to provide instant feedback
     */
    private fun safeSpeakDuringStreaming(message: String) {
        try {
            if (robot == null || message.isBlank()) return

            val cleanedMessage = message
                .replace(NEWLINE_REGEX, ". ")
                .replace(SPACE_REGEX, " ")
                .replace(":", ". ")
                .replace("Dr.", "Doctor", ignoreCase = true)
                .replace(SYMBOL_REGEX, "")
                .trim()

            android.util.Log.d("OLLAMA_PERF", "💬 Speaking during stream: '$cleanedMessage'")

            // Detect language automatically for better TTS matching
            val detectedLanguage = if (com.example.alliswelltemi.utils.isHindi(cleanedMessage)) "hi" else "en"

            isRobotSpeaking.set(true)

            // Use speakWithLanguage for multi-lingual TTS support
            speakWithLanguage(
                context = this@MainActivity,
                text = cleanedMessage,
                language = detectedLanguage,
                robot = robot,
                queueMode = android.speech.tts.TextToSpeech.QUEUE_ADD
            )

            handler.postDelayed({
                if (isRobotSpeaking.get()) {
                    isRobotSpeaking.set(false)
                }
            }, (cleanedMessage.length * 100L) + 5000L)
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

            // Initialize TemiTTSManager with completion listener
            com.example.alliswelltemi.utils.TemiTTSManager.initialize(this)
            com.example.alliswelltemi.utils.TemiTTSManager.setOnCompletionListener {
                android.util.Log.d("VOICE_PIPELINE", "Hindi TTS finished - restarting ASR listening")
                isRobotSpeaking.set(false)
                voiceInteractionManager?.startListening()
            }

            try {
                voiceInteractionManager = com.example.alliswelltemi.utils.VoiceInteractionManager(
                    context = this,
                    robot = robot,
                    coroutineScope = lifecycleScope,
                    language = "en"
                )
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
}
