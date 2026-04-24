# 🔧 EXACT CODE CHANGES - Copy & Paste Ready

This document contains all the code changes needed to implement the 5 critical fixes.
**Read QUICK_FIX_IMPLEMENTATION_GUIDE.md first** for context.

---

## FIX #1: Dual-Listener Race Condition Guard

**File:** `MainActivity.kt`

**Location:** After line 138 (after `isRobotSpeaking` definition)

**Change:**

```kotlin
// ADD THIS NEW VARIABLE (after isRobotSpeaking)
private val isProcessingSpeech = AtomicBoolean(false)

// REPLACE THIS METHOD (lines 142-145)
override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
    android.util.Log.d("TemiSpeech", "ASR Result: $asrResult")
    processSpeech(asrResult)
}

// WITH THIS:
override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
    android.util.Log.d("TemiSpeech", "ASR Result: $asrResult")
    
    // Guard: Don't process if GPT is awaiting response or already processing
    if (isAwaitingGptResponse) {
        android.util.Log.d("TemiSpeech", "ASR ignored: waiting for GPT response")
        return
    }
    
    if (!isProcessingSpeech.compareAndSet(false, true)) {
        android.util.Log.d("TemiSpeech", "ASR ignored: already processing speech")
        return
    }
    
    try {
        processSpeech(asrResult)
    } finally {
        isProcessingSpeech.set(false)
    }
}
```

---

## FIX #2: Delete Dead Code

**File:** Delete entire file `utils/VoiceCommandParser.kt`

**Terminal Command:**
```bash
cd /Users/mva357/AndroidStudioProjects/AlliswellTemi
rm app/src/main/java/com/example/alliswelltemi/utils/VoiceCommandParser.kt
```

**Verification:**
```bash
# Should return nothing
find app -name "VoiceCommandParser*"
```

---

## FIX #3: Filter Prompt by Action Type

**File:** `MainActivity.kt`

**Location 1:** Replace `buildCombinedContext()` method (lines 353-373)

**Current Code:**
```kotlin
private fun buildCombinedContext(doctors: List<Doctor>): String {
    val sb = StringBuilder("=== HOSPITAL KNOWLEDGE BASE ===\n\n")
    
    sb.append("LOCATIONS:\n")
    LocationData.ALL_LOCATIONS.forEach { sb.append("- ${it.name}\n") }
    
    sb.append("\nDOCTORS:\n")
    if (doctors.isEmpty()) {
        sb.append("- No doctors currently in database.\n")
    } else {
        doctors.forEach { doctor ->
            val name = if (doctor.name.startsWith("Dr.", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
            sb.append("- $name: Dept: ${doctor.department}, Cabin: ${doctor.cabin}, Specialization: ${doctor.specialization}, Experience: ${doctor.yearsOfExperience} years.\n")
            if (doctor.aboutBio.isNotBlank()) {
                sb.append("  Bio: ${doctor.aboutBio}\n")
            }
        }
    }

    return sb.toString()
}
```

**Replace With:**
```kotlin
private fun buildCombinedContext(doctors: List<Doctor>, actionType: String = "general", targetName: String? = null): String {
    val sb = StringBuilder("=== HOSPITAL KNOWLEDGE BASE ===\n\n")
    
    sb.append("LOCATIONS:\n")
    LocationData.ALL_LOCATIONS.forEach { sb.append("- ${it.name}\n") }
    
    sb.append("\nDOCTORS:\n")
    
    // Filter doctors based on action type for efficiency
    val relevantDoctors = when (actionType) {
        "find_doctor" -> {
            if (targetName != null && targetName.isNotBlank()) {
                // If we have a target doctor name, include only matching doctors
                doctors.filter { doctor ->
                    doctor.name.contains(targetName, ignoreCase = true) ||
                    targetName.contains(doctor.name.replace("Dr.", "").trim(), ignoreCase = true)
                }
            } else {
                // No target specified: include all doctors
                doctors
            }
        }
        "navigate_to_doctor" -> {
            // Navigation doesn't need all doctor details, limit for brevity
            doctors.take(3)
        }
        else -> {
            // General query: include all doctors for context
            doctors
        }
    }
    
    if (relevantDoctors.isEmpty()) {
        sb.append("- No doctors currently in database.\n")
    } else {
        relevantDoctors.forEach { doctor ->
            val name = if (doctor.name.startsWith("Dr.", ignoreCase = true)) doctor.name else "Dr. ${doctor.name}"
            sb.append("- $name: Dept: ${doctor.department}, Cabin: ${doctor.cabin}, Specialization: ${doctor.specialization}, Experience: ${doctor.yearsOfExperience} years.\n")
            if (doctor.aboutBio.isNotBlank()) {
                sb.append("  Bio: ${doctor.aboutBio}\n")
            }
        }
    }

    return sb.toString()
}
```

**Location 2:** Update call in `provideGptResponse()` (line 271)

**Current Code:**
```kotlin
private fun provideGptResponse(actionType: String, userQuery: String, targetName: String?, doctors: List<Doctor>) {
    try {
        val context = buildCombinedContext(doctors)  // <-- THIS LINE
```

**Replace With:**
```kotlin
private fun provideGptResponse(actionType: String, userQuery: String, targetName: String?, doctors: List<Doctor>) {
    try {
        val context = buildCombinedContext(doctors, actionType, targetName)  // <-- UPDATED
```

---

## FIX #4: Add GPT Response Timeout

**File:** `MainActivity.kt`

**Location 1:** Add new variable (after line 140)

**Add:**
```kotlin
private var gptResponseTimeout: Runnable? = null
```

**Location 2:** Update `provideGptResponse()` method (lines 269-307)

**Current Code:**
```kotlin
private fun provideGptResponse(actionType: String, userQuery: String, targetName: String?, doctors: List<Doctor>) {
    try {
        val context = buildCombinedContext(doctors, actionType, targetName)
        
        // SYSTEM INSTRUCTION FOR GPT
        val systemInstruction = """
            You are Temi, the AI Robot Assistant at All Is Well Hospital.
            Use the provided HOSPITAL KNOWLEDGE BASE to answer user queries.
            
            RULES:
            1. Use ONLY the information in the KNOWLEDGE BASE.
            2. If the answer is not in the KNOWLEDGE BASE, say "I'm sorry, I don't have that information. I can help you find doctors, book appointments, or navigate the hospital."
            3. Be professional, warm, and concise (max 2-3 sentences).
            4. Today's date: ${java.text.SimpleDateFormat("yyyy-MM-dd").format(Date())}
        """.trimIndent()

        val prompt = """
            $systemInstruction
            
            $context
            
            USER QUERY: "$userQuery"
            CONTEXTUAL ACTION: $actionType ${if (targetName != null) "targeting $targetName" else ""}
            
            RESPONSE:
        """.trimIndent()

        android.util.Log.d("TemiGPT", "Sending Prompt to Temi GPT: $prompt")
        isAwaitingGptResponse = true
        
        // This triggers Temi's Enterprise ChatGPT if enabled in the console
        robot?.askQuestion(prompt)
        
    } catch (e: Exception) {
        android.util.Log.e("TemiGPT", "Error in GPT call", e)
        isAwaitingGptResponse = false
        safeSpeak("I'm sorry, I encountered an error. How can I help you?")
    }
}
```

**Replace With:**
```kotlin
private fun provideGptResponse(actionType: String, userQuery: String, targetName: String?, doctors: List<Doctor>) {
    try {
        // Cancel any previous timeout
        gptResponseTimeout?.let { handler.removeCallbacks(it) }
        
        val context = buildCombinedContext(doctors, actionType, targetName)
        
        // SYSTEM INSTRUCTION FOR GPT
        val systemInstruction = """
            You are Temi, the AI Robot Assistant at All Is Well Hospital.
            Use the provided HOSPITAL KNOWLEDGE BASE to answer user queries.
            
            RULES:
            1. Use ONLY the information in the KNOWLEDGE BASE.
            2. If the answer is not in the KNOWLEDGE BASE, say "I'm sorry, I don't have that information. I can help you find doctors, book appointments, or navigate the hospital."
            3. Be professional, warm, and concise (max 2-3 sentences).
            4. Today's date: ${java.text.SimpleDateFormat("yyyy-MM-dd").format(Date())}
        """.trimIndent()

        val prompt = """
            $systemInstruction
            
            $context
            
            USER QUERY: "$userQuery"
            CONTEXTUAL ACTION: $actionType ${if (targetName != null) "targeting $targetName" else ""}
            
            RESPONSE:
        """.trimIndent()

        android.util.Log.d("TemiGPT", "Sending Prompt to Temi GPT (size: ${prompt.length} chars)")
        isAwaitingGptResponse = true
        
        // Set timeout: If no response within 10 seconds, reset flag and give fallback
        gptResponseTimeout = Runnable {
            if (isAwaitingGptResponse) {
                android.util.Log.w("TemiGPT", "GPT response timeout after 10s")
                isAwaitingGptResponse = false
                safeSpeak("I'm taking longer than usual to think about that. Could you please try again?")
            }
        }
        handler.postDelayed(gptResponseTimeout!!, 10000)
        
        // This triggers Temi's Enterprise ChatGPT if enabled in the console
        robot?.askQuestion(prompt)
        
    } catch (e: Exception) {
        android.util.Log.e("TemiGPT", "Error in GPT call", e)
        isAwaitingGptResponse = false
        gptResponseTimeout?.let { handler.removeCallbacks(it) }
        safeSpeak("I'm sorry, I encountered an error. How can I help you?")
    }
}
```

**Location 3:** Update `onNlpCompleted()` method (lines 147-160)

**Current Code:**
```kotlin
override fun onNlpCompleted(nlpResult: NlpResult) {
    // Log results to debug GPT behavior
    android.util.Log.d("TemiSpeech", "NLP Result: action=${nlpResult.action}, query=${nlpResult.resolvedQuery}")
    
    if (isAwaitingGptResponse) {
        isAwaitingGptResponse = false
        // On some Temi versions, the GPT response text comes in 'resolvedQuery' field
        // if triggered via askQuestion.
        val text = nlpResult.resolvedQuery ?: ""
        if (text.isNotBlank()) {
            safeSpeak(text)
        }
    }
}
```

**Replace With:**
```kotlin
override fun onNlpCompleted(nlpResult: NlpResult) {
    // Log results to debug GPT behavior
    android.util.Log.d("TemiSpeech", "NLP Result: action=${nlpResult.action}, query=${nlpResult.resolvedQuery}")
    
    if (isAwaitingGptResponse) {
        isAwaitingGptResponse = false
        // Cancel timeout since we got a response
        gptResponseTimeout?.let { handler.removeCallbacks(it) }
        
        // On some Temi versions, the GPT response text comes in 'resolvedQuery' field
        // if triggered via askQuestion.
        val text = nlpResult.resolvedQuery ?: ""
        if (text.isNotBlank()) {
            safeSpeak(text)
        }
    }
}
```

---

## FIX #5: Create Centralized SpeechOrchestrator

**New File:** `utils/SpeechOrchestrator.kt`

**Create this new file:**

```kotlin
package com.example.alliswelltemi.utils

import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.data.LocationData
import android.util.Log

/**
 * Centralized orchestrator for speech intent classification and context building
 * Provides a single, testable, maintainable interface for speech processing
 */
object SpeechOrchestrator {
    private val tag = "SpeechOrchestrator"

    enum class Intent {
        FIND_DOCTOR,
        NAVIGATE_LOCATION,
        BOOK_APPOINTMENT,
        GENERAL_QUERY,
        UNKNOWN
    }

    data class SpeechContext(
        val intent: Intent,
        val targetName: String? = null,
        val utterance: String,
        val confidence: Float = 1f,
        val relevantDoctors: List<Doctor> = emptyList()
    )

    /**
     * Analyze user utterance and extract intent + target
     */
    fun analyzeUtterance(text: String, doctors: List<Doctor>): SpeechContext {
        if (text.isBlank()) {
            return SpeechContext(Intent.UNKNOWN, null, text, 0f)
        }

        val normalized = text.lowercase().trim()
        Log.d(tag, "Analyzing utterance: '$text' (normalized: '$normalized')")

        // Try to extract target (doctor or location)
        val targetDoctor = extractDoctor(normalized, doctors)
        val targetLocation = extractLocation(normalized)

        // Classify intent based on keywords
        val intent = when {
            normalized.contains("book") || normalized.contains("appointment") || normalized.contains("schedule") -> {
                Intent.BOOK_APPOINTMENT
            }
            normalized.contains("navigate") || normalized.contains("take me to") || normalized.contains("go to") -> {
                Intent.NAVIGATE_LOCATION
            }
            normalized.contains("find") || normalized.contains("doctor") || targetDoctor != null -> {
                Intent.FIND_DOCTOR
            }
            normalized.contains("show") || normalized.contains("list") -> {
                Intent.FIND_DOCTOR  // Assume they want to see doctors
            }
            else -> {
                Intent.GENERAL_QUERY
            }
        }

        // Build context
        val context = SpeechContext(
            intent = intent,
            targetName = targetDoctor?.name ?: targetLocation,
            utterance = text,
            confidence = if (targetDoctor != null || targetLocation != null) 1f else 0.7f,
            relevantDoctors = if (targetDoctor != null) listOf(targetDoctor) else emptyList()
        )

        Log.d(tag, "Classified: $context")
        return context
    }

    /**
     * Extract doctor from utterance using fuzzy matching
     */
    private fun extractDoctor(normalized: String, doctors: List<Doctor>): Doctor? {
        return doctors.find { doctor ->
            val doctorName = doctor.name.lowercase().replace("dr.", "").replace("dr ", "").trim()
            val nameTokens = doctorName.split(" ").filter { it.isNotEmpty() }

            // Exact match or partial match
            normalized.contains(doctorName) ||
            doctorName.contains(normalized) ||
            nameTokens.any { token ->
                (normalized.contains(token) || token.contains(normalized)) && token.length > 2
            }
        }
    }

    /**
     * Extract location from utterance
     */
    private fun extractLocation(normalized: String): String? {
        return LocationData.ALL_LOCATIONS.find { location ->
            normalized.contains(location.name.lowercase())
        }?.name
    }

    /**
     * Get human-readable intent name
     */
    fun getIntentName(intent: Intent): String {
        return when (intent) {
            Intent.FIND_DOCTOR -> "Find Doctor"
            Intent.NAVIGATE_LOCATION -> "Navigate"
            Intent.BOOK_APPOINTMENT -> "Book Appointment"
            Intent.GENERAL_QUERY -> "General Query"
            Intent.UNKNOWN -> "Unknown"
        }
    }
}
```

**Update MainActivity.kt:**

**Location 1:** Add import at top (after line 27)

```kotlin
import com.example.alliswelltemi.utils.SpeechOrchestrator
```

**Location 2:** Replace `processSpeech()` method (lines 238-267)

**Current Code:**
```kotlin
private fun processSpeech(text: String) {
    if (text.isBlank() || text == lastProcessedText) return
    lastProcessedText = text
    resetInactivityTimer()

    val lowerText = text.lowercase()
    val doctors = doctorsViewModel.doctors.value

    when {
        lowerText.contains("doctor") || lowerText.contains("find") -> {
            val doctor = doctors.find { lowerText.contains(it.name.lowercase()) }
            if (doctor != null) {
                currentScreen.value = "doctors"
                provideGptResponse("find_doctor", text, doctor.name, doctors)
            } else {
                provideGptResponse("general", text, null, doctors)
            }
        }
        lowerText.contains("navigate") || lowerText.contains("take me to") -> {
            val loc = LocationData.ALL_LOCATIONS.find { lowerText.contains(it.name.lowercase()) }
            if (loc != null) {
                provideGptResponse("navigate_to_doctor", text, loc.name, doctors)
                robot?.goTo(loc.name)
            } else {
                provideGptResponse("general", text, null, doctors)
            }
        }
        else -> provideGptResponse("general", text, null, doctors)
    }
}
```

**Replace With:**
```kotlin
private fun processSpeech(text: String) {
    if (text.isBlank() || text == lastProcessedText) return
    lastProcessedText = text
    resetInactivityTimer()

    val doctors = doctorsViewModel.doctors.value
    val context = SpeechOrchestrator.analyzeUtterance(text, doctors)

    android.util.Log.d("TemiSpeech", "Intent: ${context.intent.name}, Target: ${context.targetName}, Confidence: ${context.confidence}")

    when (context.intent) {
        SpeechOrchestrator.Intent.FIND_DOCTOR -> {
            currentScreen.value = "doctors"
            provideGptResponse("find_doctor", text, context.targetName, context.relevantDoctors.ifEmpty { doctors })
        }
        SpeechOrchestrator.Intent.NAVIGATE_LOCATION -> {
            provideGptResponse("navigate_to_doctor", text, context.targetName, doctors)
            robot?.goTo(context.targetName ?: "")
        }
        SpeechOrchestrator.Intent.BOOK_APPOINTMENT -> {
            currentScreen.value = "appointment"
            provideGptResponse("book_appointment", text, context.targetName, context.relevantDoctors.ifEmpty { doctors })
        }
        SpeechOrchestrator.Intent.GENERAL_QUERY -> {
            provideGptResponse("general", text, null, doctors)
        }
        SpeechOrchestrator.Intent.UNKNOWN -> {
            safeSpeak("I'm sorry, I didn't understand that. Could you say that again?")
        }
    }
}
```

---

## Summary of Changes

| File | Change | Type | Effort |
|------|--------|------|--------|
| MainActivity.kt | Add isProcessingSpeech guard | Code | 5 min |
| VoiceCommandParser.kt | Delete | Delete | 2 min |
| MainActivity.kt | Filter prompt by action type | Code | 15 min |
| MainActivity.kt | Add GPT timeout | Code | 10 min |
| SpeechOrchestrator.kt | Create new file | New | 30 min |
| MainActivity.kt | Update processSpeech to use orchestrator | Code | 10 min |

**Total Implementation Time:** ~75 minutes (1.25 hours)

---

## Testing Steps

### Step 1: Compile
```bash
cd /Users/mva357/AndroidStudioProjects/AlliswellTemi
./gradlew clean build
```

### Step 2: Check for Errors
```bash
# Should show 0 errors
./gradlew build 2>&1 | grep -i "error"
```

### Step 3: Deploy to Device
```bash
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk
```

### Step 4: Manual Testing
```bash
# Monitor logs
adb logcat | grep "TemiSpeech\|TemiGPT\|SpeechOrchestrator"

# Test voice input
# Say: "Find Dr. Sharma"
# Expected: Intent=FIND_DOCTOR, Target="Dr. Sharma"
```

### Step 5: Verify Fixes
- ✅ ASR not firing while processing
- ✅ Prompt size logged as < 2KB for filtered cases
- ✅ Timeout cancellation working
- ✅ Orchestrator correctly classifying intents

---

## Rollback Plan

If you need to revert:

```bash
# Restore from git
git checkout HEAD~1 -- app/src/main/java/com/example/alliswelltemi/MainActivity.kt
rm app/src/main/java/com/example/alliswelltemi/utils/SpeechOrchestrator.kt
```

---

**Success Criteria:** All 5 fixes implemented, no compile errors, manual testing passes

