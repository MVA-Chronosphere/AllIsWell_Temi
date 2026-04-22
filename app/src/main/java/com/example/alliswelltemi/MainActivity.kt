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
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.network.OllamaClient
import com.example.alliswelltemi.network.OllamaRequest
import com.example.alliswelltemi.network.VoiceState
import com.example.alliswelltemi.ui.screens.*
import com.example.alliswelltemi.ui.theme.TemiTheme
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

    // Expose conversation state to UI for guarding interactions
    private val conversationActiveState = mutableStateOf(false)

    // Production-grade voice pipeline
    private lateinit var orchestrator: SpeechOrchestrator
    private val isProcessingSpeech = AtomicBoolean(false)

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
                // NEVER reset activity during active GPT conversation
                if (currentScreen.value != "main" && !isRobotSpeaking.get() && !isConversationActive) {
                    android.util.Log.i("TemiLifecycle", "Inactivity timeout reached, returning to main screen")
                    currentScreen.value = "main"
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

    // GPT timeout safety
    private var gptTimeoutRunnable: Runnable? = null
    private val GPT_TIMEOUT_MS = 12000L // 12 second timeout for GPT responses

    override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
        android.util.Log.d("TemiSpeech", "ASR Result: '$asrResult' (language: ${sttLanguage?.name})")

        // HARD BLOCK: ASR during active GPT conversation - prevents interruptions
        if (isConversationActive) {
            android.util.Log.d("GPT_FIX", "BLOCKED ASR: conversation active")
            return
        }

        // Race condition safety: only process if not already processing
        if (!isProcessingSpeech.compareAndSet(false, true)) {
            android.util.Log.d("TemiSpeech", "Skipped duplicate ASR - already processing previous speech")
            return
        }

        try {
            processSpeech(asrResult)
        } finally {
            isProcessingSpeech.set(false)
        }
    }

    override fun onNlpCompleted(nlpResult: NlpResult) {
        val elapsedMs = System.currentTimeMillis() - gptRequestStartTime
        android.util.Log.d("TemiSpeech", "NLP Result (after ${elapsedMs}ms): action=${nlpResult.action}, query=${nlpResult.resolvedQuery}")
    }

    override fun onConversationStatusChanged(status: Int, text: String) {
        android.util.Log.d("GPT_FIX", "========== CONVERSATION STATUS CHANGED ==========")
        android.util.Log.d("GPT_FIX", "Status = $status")
        android.util.Log.d("GPT_FIX", "Text = '${if (text.isBlank()) "<empty>" else text}'")
        android.util.Log.d("GPT_FIX", "isConversationActive = $isConversationActive")

        // CRITICAL: BLOCK ALL Temi SDK responses - we use OLLAMA exclusively
        // Even if Temi has a response, we must block it and prevent TTS from speaking
        if (text.isNotBlank()) {
            android.util.Log.d("GPT_FIX", "========== BLOCKING TEMI SDK Q&A RESPONSE ==========")
            android.util.Log.d("GPT_FIX", "Blocked Temi Q&A Center response: '$text'")
            android.util.Log.d("GPT_FIX", "This should NOT be spoken - using OLLAMA only")

            // STOP any Temi SDK speech immediately
            // The Temi SDK might have already queued a TTS request for this response
            // We cancel pending TTS and mark as not speaking
            robot?.speak(TtsRequest.create("", false))  // Empty TTS to clear queue
            isRobotSpeaking.set(false)  // Mark as not speaking
            synchronized(pendingTtsIds) { pendingTtsIds.clear() }

            android.util.Log.d("GPT_FIX", "Temi SDK TTS queue cleared - waiting for OLLAMA response only")
        }

        return  // ✅ Always block all Temi SDK conversation responses
    }

    override fun onConversationAttaches(isAttached: Boolean) {
        android.util.Log.d("TemiSpeech", "Conversation attached: $isAttached")
    }

    /**
     * Call Ollama LLM with streaming support and exclusive conversation lock protection
     * CRITICAL: Ensures ONLY ONE Ollama conversation at a time
     * Treat as MUTEX LOCK - no parallel operations allowed
     */
    private fun callOllama(prompt: String) {
        // HARD LOCK: Prevents multiple Ollama calls
        if (isConversationActive) {
            android.util.Log.d("OLLAMA_FIX", "BLOCKED: Duplicate conversation attempt - already active")
            return
        }

        isConversationActive = true
        conversationActiveState.value = true // Sync UI state

        android.util.Log.d("OLLAMA_FIX", "========== STARTING OLLAMA CONVERSATION ==========")
        android.util.Log.d("OLLAMA_FIX", "Streaming call START - isConversationActive = true")

        // Prevent duplicate calls
        if (isGptProcessing) {
            android.util.Log.d("OLLAMA_FIX", "Skipping duplicate Ollama call (already processing)")
            isConversationActive = false // Release lock
            conversationActiveState.value = false
            return
        }

        isGptProcessing = true
        gptRequestStartTime = System.currentTimeMillis()

        // BLOCK inactivity timer during Ollama - prevents activity reset mid-conversation
        handler.removeCallbacks(inactivityRunnable)
        android.util.Log.d("OLLAMA_FIX", "Inactivity timer BLOCKED during conversation")

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
     * Speak streaming chunks in real-time for better UX
     */
    private fun speakStreamingChunk(chunk: String) {
        if (chunk.isBlank()) return

        try {
            // Clean chunk for TTS
            val cleanedChunk = chunk
                .replace(Regex("[\\r\\n]"), " ")
                .replace(Regex(" {2,}"), " ")
                .trim()

            if (cleanedChunk.isNotBlank()) {
                robot?.speak(TtsRequest.create(cleanedChunk, isShowOnConversationLayer = true))
            }
        } catch (e: Exception) {
            android.util.Log.w("OLLAMA_FIX", "Error speaking streaming chunk: ${e.message}")
        }
    }

    private fun processSpeech(text: String) {
        if (text.isBlank()) {
            android.util.Log.w("TemiSpeech", "processSpeech called with blank text - ignoring")
            return
        }

        // BLOCK INPUT DURING ACTIVE SESSION
        if (isConversationActive) {
            android.util.Log.d("OLLAMA_FIX", "Input ignored (active conversation)")
            return
        }

        if (text == lastProcessedText) {
            android.util.Log.d("TemiSpeech", "processSpeech: Duplicate text skipped - '$text'")
            return
        }

        lastProcessedText = text
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

                // Step 2: Handle navigation side effects on main thread
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

                        else -> {} // GENERAL - no special navigation
                    }
                }

                // Step 3: Build optimized Ollama prompt on background thread
                val prompt = withContext(Dispatchers.Default) {
                    android.util.Log.d("PERF", "RagContextBuilder.buildOllamaPrompt() starting")
                    RagContextBuilder.buildOllamaPrompt(text, doctors)
                }

                val perf_time = System.currentTimeMillis() - perf_start
                android.util.Log.d("PERF", "Background processing completed in ${perf_time}ms")

                // Log full prompt for debugging (multi-line)
                android.util.Log.d("OLLAMA_PROMPT", "========== OLLAMA PROMPT START ==========")
                android.util.Log.d("OLLAMA_PROMPT", prompt)
                android.util.Log.d("OLLAMA_PROMPT", "========== OLLAMA PROMPT END (${prompt.length} chars) ==========")

                // Step 4: Call Ollama on main thread (must happen after all context prep)
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

    private fun checkImmediateCommands(asrResult: String): Boolean {
        val trimmedResult = asrResult.trim().lowercase()

        // Navigation commands
        if (trimmedResult.contains("go to") || trimmedResult.contains("navigate to")) {
            val destination = trimmedResult.substringAfterLast("to ").trim()
            android.util.Log.i("TemiMain", "Navigating to: $destination")
            currentScreen.value = "navigation"
            return true
        }

        // Appointment booking command
        if (trimmedResult.contains("book appointment")) {
            android.util.Log.i("TemiMain", "Booking appointment")
            currentScreen.value = "appointment"
            return true
        }

        // Doctors list command
        if (trimmedResult.contains("show doctors") || trimmedResult.contains("list doctors")) {
            android.util.Log.i("TemiMain", "Showing doctors list")
            currentScreen.value = "doctors"
            return true
        }

        return false
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        synchronized(pendingTtsIds) {
            when (ttsRequest.status) {
                TtsRequest.Status.STARTED -> isRobotSpeaking.set(true)
                TtsRequest.Status.COMPLETED, TtsRequest.Status.CANCELED, TtsRequest.Status.ERROR -> {
                    pendingTtsIds.remove(ttsRequest.id)
                    if (pendingTtsIds.isEmpty()) {
                        isRobotSpeaking.set(false)
                        handler.removeCallbacksAndMessages("tts_safety")
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
                android.util.Log.d("OLLAMA_FIX", "BLOCKED safeSpeak: conversation active")
                return
            }

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

            val requests = chunks.map { TtsRequest.create(it, isShowOnConversationLayer = true) }
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
            robot?.addAsrListener(this)
            robot?.addNlpListener(this)
            robot?.addTtsListener(this)
            robot?.addConversationViewAttachesListener(this)

            // NOTE: NOT disabling Temi SDK's conversation system via setConversationMode()
            // because the method doesn't exist in Temi SDK 1.137.1
            // Instead, we manage our own isConversationActive flag and block overlapping requests
            android.util.Log.d("TemiMain", "Using custom conversation lock (isConversationActive) to manage Ollama")

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
        super.onDestroy()
        Robot.getInstance().removeOnRobotReadyListener(this)
        robot?.removeAsrListener(this)
        robot?.removeNlpListener(this)
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
