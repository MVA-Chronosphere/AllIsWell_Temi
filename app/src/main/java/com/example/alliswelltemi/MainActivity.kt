package com.example.alliswelltemi

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.ui.screens.TemiMainScreen
import com.example.alliswelltemi.ui.screens.NavigationScreen
import com.example.alliswelltemi.ui.screens.AppointmentBookingScreen
import com.example.alliswelltemi.ui.screens.DoctorsScreen
import com.example.alliswelltemi.ui.theme.TemiTheme
import com.example.alliswelltemi.viewmodel.NavigationViewModel
import com.example.alliswelltemi.viewmodel.AppointmentViewModel
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.example.alliswelltemi.utils.VoiceCommandParser
import com.example.alliswelltemi.utils.DoctorRAGService
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.SttLanguage
import com.robotemi.sdk.NlpResult
import com.robotemi.sdk.listeners.OnRobotReadyListener

import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity(), OnRobotReadyListener, Robot.AsrListener, Robot.NlpListener, Robot.TtsListener {

    private var robot: Robot? = null
    private val isRobotReady = mutableStateOf(false)
    private val currentScreen = mutableStateOf("main")
    private lateinit var doctorsViewModel: DoctorsViewModel
    private val appointmentViewModel = AppointmentViewModel()
    
    // Inactivity timer
    private var lastInteractionTime = System.currentTimeMillis()
    private val INACTIVITY_TIMEOUT = 30000L // 30 seconds
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val inactivityRunnable = object : Runnable {
        override fun run() {
            if (System.currentTimeMillis() - lastInteractionTime >= INACTIVITY_TIMEOUT) {
                // Check if robot is speaking or in an active flow
                val isBusy = isRobotSpeaking.get() || robot?.isReady == false || (currentScreen.value == "appointment" && appointmentViewModel.currentStep.value > 1 && appointmentViewModel.currentStep.value < 5)
                
                if (currentScreen.value != "main" && !isBusy) {
                    currentScreen.value = "main"
                    robot?.stopMovement()
                    robot?.speak(TtsRequest.create("Returning to home screen due to inactivity.", false))
                }
            }
            handler.postDelayed(this, 5000) // Check every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CRITICAL: Log app startup
        android.util.Log.i("TemiMain", "========== APPLICATION START ==========")
        android.util.Log.i("TemiMain", "onCreate() called")
        android.util.Log.d("TemiMain", "Process ID: ${android.os.Process.myPid()}")
        android.util.Log.d("TemiMain", "Thread: ${Thread.currentThread().name}")

        try {
            // Set up fullscreen and landscape
            android.util.Log.d("TemiMain", "Setting up fullscreen and landscape mode...")
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            android.util.Log.d("TemiMain", "Window flags set successfully")

            // Initialize Temi Robot SDK
            android.util.Log.d("TemiMain", "Initializing Temi Robot SDK...")
            Robot.getInstance().addOnRobotReadyListener(this)
            android.util.Log.d("TemiMain", "Robot ready listener added")

            // Initialize ViewModels
            android.util.Log.d("TemiMain", "Initializing DoctorsViewModel...")
            doctorsViewModel = DoctorsViewModel(application)
            android.util.Log.d("TemiMain", "DoctorsViewModel initialized")

            // Observe doctors loading to give feedback
            android.util.Log.d("TemiMain", "Setting up doctor loading observer...")
            lifecycleScope.launch {
                snapshotFlow { doctorsViewModel.doctors.value }.collectLatest { doctors ->
                    android.util.Log.d("TemiMain", "Doctor list updated: ${doctors.size} doctors")
                    if (doctors.isNotEmpty()) {
                        android.util.Log.i("TemiMain", "Loaded ${doctors.size} doctors from Strapi")
                        runOnUiThread {
                            android.widget.Toast.makeText(this@MainActivity, "Loaded ${doctors.size} doctors from Strapi", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            android.util.Log.d("TemiMain", "Doctor observer created")

            android.util.Log.d("TemiMain", "Fetching doctors from API...")
            doctorsViewModel.fetchDoctors()
            android.util.Log.d("TemiMain", "Doctor fetch initiated")

            // Set Compose content
            android.util.Log.d("TemiMain", "Setting Compose content...")
            setContent {
                TemiTheme(darkTheme = true) {
                    when (currentScreen.value) {
                        "navigation" -> {
                            android.util.Log.d("TemiMain", "Rendering: NavigationScreen")
                            val navViewModel: NavigationViewModel = viewModel()
                            NavigationScreen(
                                robot = robot,
                                viewModel = navViewModel,
                                onBackPress = {
                                    currentScreen.value = "main"
                                },
                                onNavigationComplete = { _ ->
                                }
                            )
                        }
                        "appointment" -> {
                            android.util.Log.d("TemiMain", "Rendering: AppointmentBookingScreen")
                            AppointmentBookingScreen(
                                robot = robot,
                                viewModel = appointmentViewModel,
                                doctorsViewModel = doctorsViewModel,
                                onBackPress = {
                                    currentScreen.value = "main"
                                }
                            )
                        }
                        "doctors" -> {
                            android.util.Log.d("TemiMain", "Rendering: DoctorsScreen")
                            DoctorsScreen(
                                robot = robot,
                                viewModel = doctorsViewModel,
                                onBackPress = {
                                    currentScreen.value = "main"
                                },
                                onSelectDoctor = { doctor ->
                                    currentScreen.value = "appointment"
                                }
                            )
                        }
                        else -> {
                            android.util.Log.d("TemiMain", "Rendering: TemiMainScreen (main)")
                            TemiMainScreen(
                                robot = robot,
                                onNavigate = { destination ->
                                    handleNavigation(destination)
                                }
                            )
                        }
                    }
                }
            }
            android.util.Log.i("TemiMain", "Compose content set successfully")
            android.util.Log.i("TemiMain", "========== onCreate() COMPLETE ==========")
        } catch (e: Exception) {
            android.util.Log.e("TemiMain", "CRITICAL ERROR in onCreate(): ${e.message}", e)
            android.util.Log.e("TemiMain", "Stack trace: ${android.util.Log.getStackTraceString(e)}")
            throw e
        }
    }

    private var lastProcessedText = ""
    private var lastSafeSpeakMessage = ""
    private val isRobotSpeaking = java.util.concurrent.atomic.AtomicBoolean(false)
    private val pendingTtsIds = mutableSetOf<java.util.UUID>()
    private val asrListeningDebounce = 1000L

    override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
        try {
            android.util.Log.i("TemiASR", "onAsrResult called")
            android.util.Log.d("TemiASR", "ASR Result: \"$asrResult\"")
            android.util.Log.d("TemiASR", "STT Language: $sttLanguage")
            
            val currentlySpeaking = isRobotSpeaking.get()
            android.util.Log.d("TemiASR", "Robot speaking state: $currentlySpeaking")

            if (asrResult.isEmpty()) {
                android.util.Log.w("TemiASR", "ASR result is empty")
                return
            }

            if (asrResult == lastProcessedText) {
                android.util.Log.d("TemiASR", "Skipping duplicate ASR result")
                return
            }

            // Heuristic to avoid robot hearing itself
            if (currentlySpeaking) {
                val normalizedAsr = VoiceCommandParser.normalizeQuery(asrResult)
                val normalizedCurrentSpeech = VoiceCommandParser.normalizeQuery(lastSafeSpeakMessage)
                
                if (normalizedAsr.length > 2 && (normalizedCurrentSpeech.contains(normalizedAsr) || normalizedAsr.contains(normalizedCurrentSpeech))) {
                    android.util.Log.d("TemiASR", "Ignoring ASR result (likely own speech): \"$asrResult\"")
                    return
                }
                android.util.Log.i("TemiASR", "Allowing ASR interruption: \"$asrResult\"")
            }

            android.util.Log.d("TemiASR", "Processing ASR result: \"$asrResult\"")
            processSpeech(asrResult)
            lastProcessedText = asrResult
        } catch (e: Exception) {
            android.util.Log.e("TemiASR", "Error in onAsrResult: ${e.message}", e)
        }
    }

    override fun onNlpCompleted(nlpResult: NlpResult) {
        try {
            android.util.Log.i("TemiNLP", "onNlpCompleted called")
            val query = nlpResult.resolvedQuery
            android.util.Log.d("TemiNLP", "NLP Resolved Query: \"$query\"")
            android.util.Log.d("TemiNLP", "NLP Action: ${nlpResult.action}")

            if (query.isNotEmpty() && query != lastProcessedText) {
                android.util.Log.d("TemiNLP", "Processing new NLP result (not duplicate)")
                processSpeech(query)
                lastProcessedText = query
            } else if (query.isEmpty()) {
                android.util.Log.w("TemiNLP", "NLP resolved query is empty")
            } else {
                android.util.Log.d("TemiNLP", "Skipping duplicate NLP result")
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiNLP", "Error in onNlpCompleted: ${e.message}", e)
        }
    }

    private fun safeSpeak(message: String) {
        try {
            if (robot == null) {
                android.util.Log.e("TemiSpeech", "Cannot speak: Robot not initialized")
                return
            }
            if (message.isBlank()) return

            lastSafeSpeakMessage = message

            // CRITICAL: Stop any ongoing speech and clear our tracking
            robot?.cancelAllTtsRequests()
            synchronized(pendingTtsIds) {
                pendingTtsIds.clear()
            }
            isRobotSpeaking.set(true)
            handler.removeCallbacksAndMessages("tts_safety")

            android.util.Log.v("TemiSpeech", "Original message: \"$message\"")
            
            // Clean the message: remove excessive newlines, colons, and normalize whitespace
            val cleanedMessage = message
                .replace(Regex("\\n+"), ". ")
                .replace(Regex("\\s+"), " ")
                .replace(":", ". ")
                .replace("Dr.", "Doctor", ignoreCase = true)
                .replace("Sr.", "Senior", ignoreCase = true)
                .replace("Jr.", "Junior", ignoreCase = true)
                .replace("Mr.", "Mister", ignoreCase = true)
                .replace("Ms.", "Miss", ignoreCase = true)
                .replace("Mrs.", "Missus", ignoreCase = true)
                .replace("M.D.", "MD", ignoreCase = true)
                .replace("M.B.B.S.", "MBBS", ignoreCase = true)
                .replace("B.D.S.", "BDS", ignoreCase = true)
                .replace("M.S.", "MS", ignoreCase = true)
                .replace(Regex("[()#*]"), "") // Remove markdown and parentheses
                .replace(Regex(" - "), ". ") // Replace bullet points with periods
                .trim()

            // Split into sentences for reliable delivery in chunks
            val sentences = cleanedMessage.split(Regex("(?<=[.!?])\\s+"))
                .map { it.trim() }
                .filter { it.isNotBlank() && it.length > 1 }

            if (sentences.isEmpty()) {
                android.util.Log.w("TemiSpeech", "No speakable sentences after cleaning")
                isRobotSpeaking.set(false)
                return
            }

            android.util.Log.i("TemiSpeech", "Queueing ${sentences.size} chunks for TTS")
            
            val requests = sentences.map { sentence ->
                TtsRequest.create(sentence, isShowOnConversationLayer = true)
            }

            synchronized(pendingTtsIds) {
                requests.forEach { pendingTtsIds.add(it.id) }
            }

            requests.forEach { request ->
                try {
                    robot?.speak(request)
                    android.util.Log.v("TemiSpeech", "Queued chunk: \"${request.speech}\"")
                } catch (e: Exception) {
                    android.util.Log.e("TemiSpeech", "Failed to speak chunk ${request.id}: ${e.message}")
                    synchronized(pendingTtsIds) {
                        pendingTtsIds.remove(request.id)
                    }
                }
            }

            if (requests.isEmpty()) {
                isRobotSpeaking.set(false)
                return
            }

            // Safety fallback: Reset flag if no status updates are received
            val fallbackTimeout = (cleanedMessage.length * 150L) + 15000L // 150ms per char + 15s buffer
            handler.postAtTime({
                if (isRobotSpeaking.get()) {
                    synchronized(pendingTtsIds) {
                        pendingTtsIds.clear()
                    }
                    isRobotSpeaking.set(false)
                    lastProcessedText = ""
                    android.util.Log.w("TemiSpeech", "TTS Safety Fallback triggered after ${fallbackTimeout}ms")
                }
            }, "tts_safety", android.os.SystemClock.uptimeMillis() + fallbackTimeout)
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech", "Error in safeSpeak: ${e.message}", e)
            isRobotSpeaking.set(false)
        }
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        android.util.Log.d("TemiSpeech", "TTS Status [${ttsRequest.id}]: ${ttsRequest.status}")
        
        synchronized(pendingTtsIds) {
            when (ttsRequest.status) {
                TtsRequest.Status.COMPLETED, TtsRequest.Status.CANCELED, TtsRequest.Status.ERROR -> {
                    val removed = pendingTtsIds.remove(ttsRequest.id)
                    if (removed) {
                        android.util.Log.d("TemiSpeech", "Tracked chunk finished (${ttsRequest.status}). Remaining: ${pendingTtsIds.size}")
                    } else {
                        android.util.Log.d("TemiSpeech", "Untracked chunk finished (${ttsRequest.status})")
                    }
                    
                    if (pendingTtsIds.isEmpty()) {
                        isRobotSpeaking.set(false)
                        lastProcessedText = ""
                        android.util.Log.i("TemiSpeech", "All TTS chunks finished, isRobotSpeaking = false")
                        handler.removeCallbacksAndMessages("tts_safety")
                    }
                }
                TtsRequest.Status.STARTED -> {
                    isRobotSpeaking.set(true)
                    android.util.Log.d("TemiSpeech", "Chunk started speaking: ${ttsRequest.id}")
                }
                else -> {
                    android.util.Log.v("TemiSpeech", "TTS Status update: ${ttsRequest.status} for ${ttsRequest.id}")
                }
            }
            Unit // Ensure synchronized block returns Unit
        }
    }

    private fun processSpeech(text: String) {
        try {
            resetInactivityTimer()
            val handlerStartTime = System.currentTimeMillis()
            android.util.Log.i("TemiSpeech", "========== VOICE INPUT START ==========")
            android.util.Log.i("TemiSpeech", "Raw input: \"$text\"")

            val normalizedSpeech = VoiceCommandParser.normalizeQuery(text)

            android.util.Log.i("TemiSpeech", "Normalized input: \"$normalizedSpeech\"")

            if (normalizedSpeech.isEmpty()) {
                android.util.Log.w("TemiSpeech", "Normalized speech is empty, returning")
                return
            }

            android.util.Log.d("TemiSpeech", "Processing query: $normalizedSpeech (original: $text)")

            // Guard: Only allow one handler per input
            var handlerInvoked = false

            // Global "Go Back" command
            if (normalizedSpeech == "go back" || normalizedSpeech == "back" || normalizedSpeech == "return") {
                android.util.Log.d("TemiSpeech", "Detected: Go Back command")
                handleGoBack()
                handlerInvoked = true
                android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (GO BACK) ==========")
                return
            }

            // Global "Help" command
            if (normalizedSpeech == "help" || normalizedSpeech == "what can you do" || normalizedSpeech == "support") {
                android.util.Log.d("TemiSpeech", "Detected: Help command")
                handleHelp()
                handlerInvoked = true
                android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (HELP) ==========")
                return
            }

            // Direct commands for screens
            if (normalizedSpeech.contains("book appointment") || normalizedSpeech.contains("make appointment")) {
                android.util.Log.d("TemiSpeech", "Detected: Book Appointment command")
                currentScreen.value = "appointment"
                appointmentViewModel.resetBooking()
                safeSpeak("Opening appointment booking. Please select a doctor.")
                handlerInvoked = true
                android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (BOOK APPOINTMENT) ==========")
                return
            }

            if (normalizedSpeech.contains("find doctor") || normalizedSpeech.contains("show doctors") || normalizedSpeech.contains("list of doctors")) {
                android.util.Log.d("TemiSpeech", "Detected: Find Doctor command")
                currentScreen.value = "doctors"
                safeSpeak("Showing our list of specialized doctors.")
                handlerInvoked = true
                android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (FIND DOCTOR) ==========")
                return
            }

            // --- ENHANCED RAG-BASED VOICE ROUTING ---
            val doctors = doctorsViewModel.doctors.value
            android.util.Log.d("TemiSpeech", "Loaded ${doctors.size} doctors from viewmodel")

            if (doctors.isEmpty()) {
                android.util.Log.w("TemiSpeech", "No doctors available in the system")
                safeSpeak("Doctor information is not available. Please try again later.")
                handlerInvoked = true
                android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (NO DOCTORS) ==========")
                return
            }

            // Use VoiceCommandParser for intent detection
            android.util.Log.d("TemiSpeech", "Starting voice command parsing...")
            val parsedCommand = VoiceCommandParser.parseCommand(text, doctors)
            android.util.Log.i("TemiSpeech", "Parsed command: ${VoiceCommandParser.summarizeCommand(parsedCommand)}")
            android.util.Log.d("TemiSpeech", "Command type: ${parsedCommand.type}, Target: ${parsedCommand.targetName}")

        if (!handlerInvoked) {
            when (parsedCommand.type) {
                    VoiceCommandParser.CommandType.FIND_DOCTOR -> {
                        android.util.Log.d("TemiSpeech", "Routing to FIND_DOCTOR handler with target: ${parsedCommand.targetName}")
                        handleFindDoctor(parsedCommand.targetName, doctors)
                        handlerInvoked = true
                        android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (FIND DOCTOR HANDLER) ==========")
                        return
                    }
                    VoiceCommandParser.CommandType.FILTER_DEPARTMENT -> {
                        android.util.Log.d("TemiSpeech", "Routing to FILTER_DEPARTMENT handler with department: ${parsedCommand.targetName}")
                        handleFilterDepartment(parsedCommand.targetName, doctors)
                        handlerInvoked = true
                        android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (FILTER DEPARTMENT) ==========")
                        return
                    }
                    VoiceCommandParser.CommandType.NAVIGATE_TO_DOCTOR -> {
                        android.util.Log.d("TemiSpeech", "Routing to NAVIGATE_TO_DOCTOR handler with target: ${parsedCommand.targetName}")
                        handleNavigateToDoctor(parsedCommand.targetName, doctors)
                        handlerInvoked = true
                        android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (NAVIGATE) ==========")
                        return
                    }
                    VoiceCommandParser.CommandType.BOOK_DOCTOR -> {
                        android.util.Log.d("TemiSpeech", "Routing to BOOK_DOCTOR handler with doctor: ${parsedCommand.targetName}")
                        handleBookDoctor(parsedCommand.targetName)
                        handlerInvoked = true
                        android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (BOOK) ==========")
                        return
                    }
                    VoiceCommandParser.CommandType.GET_INFO -> {
                        android.util.Log.d("TemiSpeech", "Routing to GET_INFO handler with target: ${parsedCommand.targetName}")
                        handleGetDoctorInfo(parsedCommand.targetName, doctors)
                        handlerInvoked = true
                        android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (GET INFO) ==========")
                        return
                    }
                    VoiceCommandParser.CommandType.UNKNOWN -> {
                        android.util.Log.d("TemiSpeech", "Routing to UNKNOWN/FALLBACK handler")
                        handleUnknownQuery(text, doctors)
                        handlerInvoked = true
                        android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (UNKNOWN) ==========")
                    }
                }
            }
            android.util.Log.d("TemiSpeech", "Handler execution time: ${System.currentTimeMillis() - handlerStartTime} ms")
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech", "Exception in processSpeech: ${e.message}", e)
            android.util.Log.e("TemiSpeech", "Stack trace: ${android.util.Log.getStackTraceString(e)}")
            safeSpeak("Sorry, I encountered an error. Please try again.")
            android.util.Log.i("TemiSpeech", "========== VOICE INPUT END (ERROR) ==========")
        }
    }

    /**
     * Handle "find doctor" voice command
     */
    private fun handleFindDoctor(doctorName: String?, doctors: List<Doctor>) {
        try {
            android.util.Log.d("TemiSpeech.FindDoctor", "Handler called with doctorName: $doctorName")

            if (doctorName == null) {
                android.util.Log.d("TemiSpeech.FindDoctor", "Doctor name is null, showing all doctors")
                currentScreen.value = "doctors"
                doctorsViewModel.filterByDepartment(null)
                safeSpeak("Showing our list of specialized doctors.")
                android.util.Log.d("TemiSpeech.FindDoctor", "Navigated to doctors screen showing all")
                return
            }

            android.util.Log.d("TemiSpeech.FindDoctor", "Searching for doctor: \"$doctorName\" among ${doctors.size} doctors")
            val doctor = doctors.find { it.name.lowercase().contains(doctorName.lowercase()) }

            if (doctor != null) {
                android.util.Log.i("TemiSpeech.FindDoctor", "Found doctor: ${doctor.name} (ID: ${doctor.id}, Department: ${doctor.department})")
                val response = DoctorRAGService.getResponseForDoctor(doctor, "general")
                android.util.Log.d("TemiSpeech.FindDoctor", "Generated response: \"$response\"")
                safeSpeak(response)
            } else {
                android.util.Log.w("TemiSpeech.FindDoctor", "Doctor \"$doctorName\" not found in database")
                safeSpeak("I couldn't find Dr. $doctorName. Please try again or say 'find doctor' to see our full list.")
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech.FindDoctor", "Error in handleFindDoctor: ${e.message}", e)
            safeSpeak("Sorry, there was an error finding that doctor.")
        }
    }

    /**
     * Handle department filter voice command
     */
    private fun handleFilterDepartment(deptName: String?, doctors: List<Doctor>) {
        try {
            android.util.Log.d("TemiSpeech.FilterDept", "Handler called with department: $deptName")

            if (deptName == null) {
                android.util.Log.w("TemiSpeech.FilterDept", "Department name is null")
                safeSpeak("Please specify a department.")
                return
            }

            android.util.Log.d("TemiSpeech.FilterDept", "Filtering for department: \"$deptName\"")
            val deptDoctors = doctors.filter { it.department.lowercase() == deptName.lowercase() }
            android.util.Log.i("TemiSpeech.FilterDept", "Found ${deptDoctors.size} doctors in $deptName department")

            deptDoctors.forEach { doc ->
                android.util.Log.d("TemiSpeech.FilterDept", "  - ${doc.name} (${doc.department})")
            }

            val response = DoctorRAGService.getResponseForDepartment(deptName, deptDoctors.size)
            android.util.Log.d("TemiSpeech.FilterDept", "Generated response: \"$response\"")

            safeSpeak(response)

            currentScreen.value = "doctors"
            doctorsViewModel.filterByDepartment(deptName)
            android.util.Log.d("TemiSpeech.FilterDept", "Navigated to doctors screen with $deptName filter")
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech.FilterDept", "Error in handleFilterDepartment: ${e.message}", e)
            safeSpeak("Sorry, there was an error filtering doctors.")
        }
    }

    /**
     * Handle navigation to doctor voice command
     */
    private fun handleNavigateToDoctor(target: String?, doctors: List<Doctor>) {
        try {
            android.util.Log.d("TemiSpeech.Navigate", "Handler called with target: $target")

            if (target == null) {
                android.util.Log.d("TemiSpeech.Navigate", "Target is null, opening navigation screen")
                currentScreen.value = "navigation"
                safeSpeak("Opening navigation. Please select a destination.")
                return
            }

            android.util.Log.d("TemiSpeech.Navigate", "Searching for doctor/location: \"$target\"")
            val doctor = doctors.find { it.name.lowercase().contains(target.lowercase()) }

            if (doctor != null) {
                val destination = doctor.cabin.ifBlank { doctor.name }
                android.util.Log.i("TemiSpeech.Navigate", "Found doctor: ${doctor.name}, navigating to cabin: $destination")
                safeSpeak("Navigating to Dr. ${doctor.name}'s cabin.")
                try {
                    android.util.Log.d("TemiSpeech.Navigate", "Calling robot.goTo($destination)")
                    robot?.goTo(destination)
                    android.util.Log.i("TemiSpeech.Navigate", "Navigation command sent successfully")
                } catch (e: Exception) {
                    android.util.Log.e("TemiSpeech.Navigate", "Error navigating to $destination: ${e.message}", e)
                }
            } else {
                android.util.Log.w("TemiSpeech.Navigate", "Target \"$target\" not found")
                safeSpeak("I couldn't find the destination. Please try again.")
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech.Navigate", "Error in handleNavigateToDoctor: ${e.message}", e)
            safeSpeak("Sorry, there was an error navigating.")
        }
    }

    /**
     * Handle book appointment voice command
     */
    private fun handleBookDoctor(doctorName: String?) {
        try {
            android.util.Log.d("TemiSpeech.Book", "Handler called with doctor: $doctorName")

            if (doctorName != null) {
                android.util.Log.d("TemiSpeech.Book", "Searching for doctor: \"$doctorName\"")
                val doctor = doctorsViewModel.doctors.value.find {
                    it.name.lowercase().contains(doctorName.lowercase())
                }

                if (doctor != null) {
                    android.util.Log.i("TemiSpeech.Book", "Found doctor: ${doctor.name}, setting for appointment")
                    appointmentViewModel.setSelectedDoctor(doctor)
                    safeSpeak("Great! Let's book an appointment with Dr. ${doctor.name}.")
                } else {
                    android.util.Log.w("TemiSpeech.Book", "Doctor \"$doctorName\" not found")
                    safeSpeak("I couldn't find Dr. $doctorName. Please try again.")
                    return
                }
            } else {
                android.util.Log.w("TemiSpeech.Book", "Doctor name is null")
                safeSpeak("Please specify a doctor's name for the appointment.")
                return
            }

            currentScreen.value = "appointment"
            appointmentViewModel.resetBooking()
            android.util.Log.d("TemiSpeech.Book", "Navigated to appointment screen")
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech.Book", "Error in handleBookDoctor: ${e.message}", e)
            safeSpeak("Sorry, there was an error booking the appointment.")
        }
    }

     /**
      * Handle get doctor info voice command
      */
     private fun handleGetDoctorInfo(target: String?, doctors: List<Doctor>) {
         try {
             android.util.Log.d("TemiSpeech.Info", "Handler called with target: $target")

             if (target.isNullOrBlank()) {
                 android.util.Log.w("TemiSpeech.Info", "Target is null or blank")
                 safeSpeak("Please specify a doctor or department.")
                 return
             }

             android.util.Log.d("TemiSpeech.Info", "Searching for: \"$target\"")
             val doctor = doctors.find { it.name.lowercase().contains(target.lowercase()) }

             if (doctor != null) {
                 android.util.Log.i("TemiSpeech.Info", "Found doctor: ${doctor.name}")
                 val response = DoctorRAGService.generateDetailedResponse(doctor)
                 android.util.Log.d("TemiSpeech.Info", "Generated detailed response with ${response.length} characters")
                 
                 safeSpeak(response)
             } else {
                 android.util.Log.w("TemiSpeech.Info", "Target \"$target\" not found in database")
                 safeSpeak("I couldn't find information about $target. Please try again or say 'find doctor' to see our full list.")
             }
         } catch (e: Exception) {
             android.util.Log.e("TemiSpeech.Info", "Error in handleGetDoctorInfo: ${e.message}", e)
             safeSpeak("Sorry, there was an error getting doctor information.")
         }
     }

     /**
      * Handle unknown query with fallback search
      */
     private fun handleUnknownQuery(text: String, doctors: List<Doctor>) {
         try {
             android.util.Log.d("TemiSpeech.Fallback", "Handler called for unknown query: \"$text\"")
             android.util.Log.d("TemiSpeech.Fallback", "No direct match found, trying fallback search...")
             android.util.Log.d("TemiSpeech.Fallback", "Available doctors: ${doctors.size}")
             doctors.take(5).forEach { doc ->
                 android.util.Log.d("TemiSpeech.Fallback", "  Doctor in system: ${doc.name} (${doc.department})")
             }

             val searchResults = doctorsViewModel.searchDoctors(text)
             android.util.Log.i("TemiSpeech.Fallback", "Search returned ${searchResults.size} results")

             searchResults.forEach { doc ->
                 android.util.Log.d("TemiSpeech.Fallback", "  - ${doc.name} (${doc.department})")
             }

             if (searchResults.isNotEmpty()) {
                 val response = DoctorRAGService.generateFallbackResponse(searchResults, text)
                 android.util.Log.d("TemiSpeech.Fallback", "Generated fallback response: \"$response\"")

                 safeSpeak(response)
                 return
             }

             // Final fallback with RAG knowledge base
             android.util.Log.w("TemiSpeech.Fallback", "No search results found, using final fallback")
             val response = "I'm sorry, I couldn't find a doctor or department matching \"$text\". You can say 'find doctor' to see our full list."
             safeSpeak(response)
          } catch (e: Exception) {
              android.util.Log.e("TemiSpeech.Fallback", "Error in handleUnknownQuery: ${e.message}", e)
              android.util.Log.e("TemiSpeech.Fallback", "Stack trace: ${android.util.Log.getStackTraceString(e)}")
              safeSpeak("I'm sorry, I couldn't process that request. Please try again or say 'find doctor'.")
         }
     }


    override fun onRobotReady(isReady: Boolean) {
        try {
            android.util.Log.i("TemiRobot", "========== ROBOT READY CALLBACK ==========")
            android.util.Log.i("TemiRobot", "Robot ready state: $isReady")

            if (isReady) {
                android.util.Log.d("TemiRobot", "Initializing robot instance...")
                this.robot = Robot.getInstance()
                android.util.Log.d("TemiRobot", "Robot instance obtained")

                android.util.Log.d("TemiRobot", "Adding ASR listener...")
                this.robot?.addAsrListener(this)
                android.util.Log.d("TemiRobot", "ASR listener added")

                android.util.Log.d("TemiRobot", "Adding NLP listener...")
                this.robot?.addNlpListener(this)
                android.util.Log.d("TemiRobot", "NLP listener added")

                android.util.Log.d("TemiRobot", "Adding TTS listener...")
                this.robot?.addTtsListener(this)
                android.util.Log.d("TemiRobot", "TTS listener added")


                isRobotReady.value = true
                android.util.Log.i("TemiRobot", "Robot initialization complete")

                android.util.Log.d("TemiRobot", "Sending startup speech...")
                safeSpeak("Hello, I am ready to assist you")
                android.util.Log.i("TemiRobot", "Startup speech sent")
            } else {
                android.util.Log.w("TemiRobot", "Robot is not ready yet")
                isRobotReady.value = false
            }
            android.util.Log.i("TemiRobot", "========== ROBOT READY COMPLETE ==========")
        } catch (e: Exception) {
            android.util.Log.e("TemiRobot", "Error in onRobotReady: ${e.message}", e)
        }
    }

    private fun handleNavigation(destination: String) {
        when (destination) {
            "navigation" -> currentScreen.value = "navigation"
            "appointment" -> currentScreen.value = "appointment"
            "doctors" -> currentScreen.value = "doctors"
        }
    }

    private fun handleGoBack() {
        try {
            android.util.Log.d("TemiSpeech.GoBack", "Handler called, current screen: ${currentScreen.value}")

            when (currentScreen.value) {
                "main" -> {
                    android.util.Log.i("TemiSpeech.GoBack", "Already on main screen")
                    safeSpeak("You are already on the home screen.")
                }
                "appointment" -> {
                    android.util.Log.d("TemiSpeech.GoBack", "On appointment screen, current step: ${appointmentViewModel.currentStep.value}")
                    if (appointmentViewModel.currentStep.value > 1) {
                        appointmentViewModel.goToPreviousStep()
                        android.util.Log.d("TemiSpeech.GoBack", "Going to previous appointment step")
                        safeSpeak("Going back to previous step.")
                    } else {
                        currentScreen.value = "main"
                        android.util.Log.d("TemiSpeech.GoBack", "Returning to main from appointment")
                        safeSpeak("Returning to home screen.")
                    }
                }
                else -> {
                    currentScreen.value = "main"
                    android.util.Log.d("TemiSpeech.GoBack", "Returning to main from ${currentScreen.value}")
                    safeSpeak("Returning to home screen.")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech.GoBack", "Error in handleGoBack: ${e.message}", e)
            safeSpeak("Sorry, there was an error.")
        }
    }

    private fun handleHelp() {
        try {
            android.util.Log.d("TemiSpeech.Help", "Handler called")
            val helpText = """I can help you with:
1. Booking an appointment. Just say 'book appointment'.
2. Finding a doctor. Say 'find doctor' or a doctor's name.
3. Navigating to different departments. Say 'take me to' followed by the location.
4. Checking your appointment status. Say 'check status' followed by your token.
How can I help you today?"""
            android.util.Log.d("TemiSpeech.Help", "Sending help text")
            safeSpeak(helpText)
        } catch (e: Exception) {
            android.util.Log.e("TemiSpeech.Help", "Error in handleHelp: ${e.message}", e)
            safeSpeak("Sorry, I couldn't provide help at this time.")
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.i("TemiLifecycle", "========== onResume ==========")
        try {
            robot?.addAsrListener(this)
            android.util.Log.d("TemiLifecycle", "ASR listener added")

            robot?.addNlpListener(this)
            android.util.Log.d("TemiLifecycle", "NLP listener added")

            robot?.addTtsListener(this)
            android.util.Log.d("TemiLifecycle", "TTS listener added")

            Robot.getInstance().addOnRobotReadyListener(this)
            android.util.Log.d("TemiLifecycle", "Robot ready listener added")

            resetInactivityTimer()
            android.util.Log.d("TemiLifecycle", "Inactivity timer reset")

            handler.post(inactivityRunnable)
            android.util.Log.d("TemiLifecycle", "Inactivity runnable posted")
        } catch (e: Exception) {
            android.util.Log.e("TemiLifecycle", "Error in onResume: ${e.message}", e)
        }
        android.util.Log.i("TemiLifecycle", "========== onResume complete ==========")
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.i("TemiLifecycle", "========== onPause ==========")
        try {
            robot?.removeAsrListener(this)
            android.util.Log.d("TemiLifecycle", "ASR listener removed")

            robot?.removeNlpListener(this)
            android.util.Log.d("TemiLifecycle", "NLP listener removed")

            robot?.removeTtsListener(this)
            android.util.Log.d("TemiLifecycle", "TTS listener removed")

            Robot.getInstance().removeOnRobotReadyListener(this)
            android.util.Log.d("TemiLifecycle", "Robot ready listener removed")

            handler.removeCallbacks(inactivityRunnable)
            android.util.Log.d("TemiLifecycle", "Inactivity runnable removed")
        } catch (e: Exception) {
            android.util.Log.e("TemiLifecycle", "Error in onPause: ${e.message}", e)
        }
        android.util.Log.i("TemiLifecycle", "========== onPause complete ==========")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        android.util.Log.v("TemiLifecycle", "User interaction detected")
        resetInactivityTimer()
    }

    private fun resetInactivityTimer() {
        lastInteractionTime = System.currentTimeMillis()
        android.util.Log.v("TemiLifecycle", "Inactivity timer reset to ${lastInteractionTime}")
    }
}
