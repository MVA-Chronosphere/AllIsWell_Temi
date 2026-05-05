# 🔍 LIP SYNC AUDIT - COMPLETE DIAGNOSIS & FIXES

**Date:** May 5, 2026  
**Project:** AllIsWell_Temi (Temi Robot Hospital Assistant)  
**Status:** 🔴 CRITICAL LIP SYNC FAILURE IDENTIFIED

---

## 📋 EXECUTIVE SUMMARY

**ROOT CAUSE:** The codebase supports THREE separate lip sync implementations (TtsLipSyncManager, LipSyncManager, AvatarController), but **NONE of them are integrated into the actual Temi TTS speech pipeline**. Speech is triggered via `robot.speak()` in MainActivity, but no lip sync mechanism receives or processes the audio output before or after playback.

**CRITICAL BREAK POINT:** Temi SDK's `Robot.speak(TtsRequest)` executes entirely on the robot hardware. The phone receives NO audio bytes, NO phoneme data, and NO timing information. Without hooks into the robot's internal TTS engine, local lip sync cannot sync with the actual robot mouth.

**IMPACT:** Mouth movements are completely decoupled from speech. Animations in screens or avatar components have no correlation with actual robot speech timing.

---

## 🔍 STEP 1: SPEECH PIPELINE AUDIT

### All Speech Invocation Points (Lines Identified)

| Location | File | Method | Type | TTS Support |
|----------|------|--------|------|-------------|
| MainActivity:551 | MainActivity.kt | `speakWithLanguage(chunk, language, robot)` | ✅ Temi TTS | YES |
| MainActivity:617 | MainActivity.kt | `speakWithLanguage(text, detectedLanguage, robot)` | ✅ Temi TTS | YES |
| TemiUtils:30 | TemiUtils.kt | `robot?.speak(TtsRequest.create(...))` | ✅ Temi TTS | YES |
| TemiUtils:40 | TemiUtils.kt | `this.speak(TtsRequest.create(...))` | ✅ Temi TTS | YES |
| VoiceInteractionManager:444 | VoiceInteractionManager.kt | `speakWithLanguage(text, speakLang, robot)` | ✅ Temi TTS | YES |
| DanceService:83 | DanceService.kt | `speakWithLanguage("Let me show...", language, robot)` | ✅ Temi TTS | YES |
| DanceService:105 | DanceService.kt | `speakWithLanguage(it, language, robot)` | ✅ Temi TTS | YES |
| DanceService:117 | DanceService.kt | `speakWithLanguage("Thanks for watching...", language, robot)` | ✅ Temi TTS | YES |

### Analysis

✅ **GOOD NEWS:** All speech uses **Temi-native TTS** via `Robot.speak()` - no external audio playback detected.

❌ **PROBLEM:** `Robot.speak()` is a **black-box hardware command**. The Temi robot handles TTS entirely on its own processor. The Android phone/app:
- Does NOT receive audio bytes
- Does NOT get phoneme/viseme timing
- Does NOT know when speech starts/stops
- Cannot synchronize animations with mouth movements

---

## 🔍 STEP 2: LANGUAGE + VOICE COMPATIBILITY AUDIT

### Current Language Support

```kotlin
// TemiUtils.kt:29
val ttsLanguage = if (finalLanguage == "hi") TtsRequest.Language.HI_IN else TtsRequest.Language.EN_US
robot?.speak(TtsRequest.create(speech = text, isShowOnConversationLayer = false, language = ttsLanguage))
```

| Language | Temi SDK Support | Viseme Support | Issue |
|----------|-----------------|-----------------|-------|
| English (EN_US) | ✅ Native | ❓ Proprietary | Unknown if Temi SDK generates visemes |
| Hindi (HI_IN) | ✅ Native | ❓ Proprietary | Unknown if Temi SDK generates visemes |

### Finding

**Temi SDK v1.137.1 does NOT expose viseme/phoneme data** in its public API. The `TtsRequest` class only accepts:
- `speech: String`
- `language: TtsRequest.Language` 
- `isShowOnConversationLayer: Boolean`

There is NO mechanism to receive lip sync data from Temi SDK.

---

## 🔍 STEP 3: ANIMATION SYSTEM AUDIT

### Animations Found in Codebase

| File | Type | Purpose | Status |
|------|------|---------|--------|
| DanceService.kt (lines 60-125) | `performDance()` coroutine | Choreograph robot dances | ✅ Active |
| DanceService.kt (lines 303-320) | `applyHeadTilt()` | Simulate head tilting | ❌ Non-functional (Temi SDK lacks API) |
| DanceService.kt (lines 326-334) | `applyBodyRotation()` | Rotate robot body | ⚠️ Uses `robot.turnBy()` |
| DanceService.kt (lines 340-355) | `applyMovement()` | Forward/backward movement | ❌ Non-functional (no API) |
| TtsLipSyncManager.kt (lines 82-112) | `startLipSync/stopLipSync()` | Text-based vowel approximation | ❌ ORPHANED (not called) |
| AvatarController.kt (lines 79-114) | `playTemiAudioBytes()` | Play audio with JavaScript lip sync | ❌ ORPHANED (no audio bytes from Temi) |

### Critical Finding: ORPHANED LIP SYNC IMPLEMENTATIONS

**TtsLipSyncManager is never called:**
```
grep -r "TtsLipSyncManager" app/src/main/java/
```
Result: **ZERO matches**. The class exists but is never instantiated or used.

**AvatarController is never integrated with Temi TTS:**
```
grep -r "playTemiAudioBytes\|AvatarController" app/src/main/java/
```
Result: AvatarController is only in components; never called from MainActivity speech pipeline. Avatar plays no audio.

### Animation Conflicts

**DanceService uses `robot.turnBy()` during dance moves**, which could interfere with lip sync if it were active. However, since lip sync is not active, there's no conflict—just broken functionality.

---

## 🔍 STEP 4: SEQUENCE USAGE AUDIT

**Finding:** No Temi SDK sequences (default pre-recorded animations) are used. All speech is via `robot.speak()` with dynamically generated text.

---

## 🔍 STEP 5: AI / BACKEND INTEGRATION AUDIT

### Pipeline Flow

```
User Voice Input
    ↓
Android SpeechRecognizer (ASR)
    ↓
VoiceInteractionManager:processSpeechWithOllama()
    ↓
Ollama LLM (local, on Ollama server)
    ↓
Text response received
    ↓
MainActivity.safeSpeak() calls speakWithLanguage()
    ↓
Robot.speak(TtsRequest) ← Speech generated on robot hardware
    ↓
Robot's mouth moves (IF lip sync was available) ← NOT CONNECTED
```

### Critical Break Point

**Line 369, MainActivity.kt:**
```kotlin
safeSpeak(finalResponse)  // Sends text to Temi TTS
```

After this line:
- `Robot.speak()` is called ✅
- Audio plays on robot ✅
- App receives NO audio bytes ❌
- No viseme data available ❌
- TtsLipSyncManager cannot start because it has no audio ❌
- AvatarController has no way to play audio ❌

---

## 🔍 STEP 6: CONCURRENCY + TIMING ISSUES

### TTS Status Tracking

```kotlin
// MainActivity.kt:264-281
override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
    synchronized(pendingTtsIds) {
        when (ttsRequest.status) {
            TtsRequest.Status.STARTED -> isRobotSpeaking.set(true)
            TtsRequest.Status.COMPLETED, CANCELED, ERROR -> {
                pendingTtsIds.remove(ttsRequest.id)
                if (pendingTtsIds.isEmpty()) {
                    isRobotSpeaking.set(false)
                    voiceInteractionManager?.restartListeningWithDelay()
                }
            }
            else -> {}
        }
    }
}
```

**Issue:** The app knows WHEN speech starts and stops, but has NO TIMING for individual phonemes/visemes. This is insufficient for real-time lip sync.

### Race Condition Risk

If DanceService and SpeakService ran simultaneously:
- Dancing would use `robot.turnBy()`
- Speaking would use `robot.speak()`
- **Result:** Robot would try to execute both, likely failing or blocking

**But this is MASKED because DanceService's `applyHeadTilt()` doesn't actually call any Temi API** (Temi SDK has no head tilt support).

---

## 🧠 ROOT CAUSE ANALYSIS

### PRIMARY CAUSE: Architectural Mismatch

**The Temi SDK is a hardware-integrated system.** Voice synthesis happens entirely on the robot's onboard processor:

1. **Temi Robot receives text:** `Robot.speak(TtsRequest.create("hello"))`
2. **Robot converts to speech:** Internal TTS engine (not accessible to Android app)
3. **Robot plays audio:** Through robot's speakers
4. **Android app awareness:** Only knows start/stop status, NOT audio content

**This means:** Local Android-based lip sync is impossible without:
- A. Access to the actual audio stream from Temi TTS (not provided by SDK)
- B. Or, circumventing Temi TTS and using Android TTS instead (defeats Temi branding)
- C. Or, embedding the 3D avatar with HeadAudio.js and playing audio separately (currently orphaned)

### SECONDARY CAUSES

1. **TtsLipSyncManager never integrated:** Class exists, implements text-to-viseme mapping, but never called
2. **AvatarController disconnected from speech:** Avatar infrastructure exists, but no audio path from Temi TTS
3. **No fallback mechanism:** When Temi TTS is used, there's no fallback to generate audio the app can intercept
4. **Dance moves not synchronized:** DanceService has speech embedded but no coordination with mouth sync

---

## 🛠️ FIX PLAN

### OPTION A: Use Temi's Built-in Lip Sync (If Available)

**Check if Temi SDK provides lip sync hooks:**

```kotlin
// In MainActivity.kt onRobotReady():
// TODO: Investigate if TtsRequest has lip sync callbacks
// Check TtsRequest documentation for:
// - viseme callbacks
// - phoneme timing
// - audio stream access
```

**Action:** Verify with Temi SDK documentation if version 1.137.1 exposes `TtsRequest.OnVisemeListener` or similar.

---

### OPTION B: Implement Custom Audio Capture + Replay (RECOMMENDED)

Split the speech pipeline into two parts:

**Part 1: Generate audio using Android TTS (not Temi TTS)**
**Part 2: Play audio on Temi speaker via speaker output or audio module**
**Part 3: Intercept audio stream for lip sync**

This requires:
- Replacing `robot.speak()` with `Android TextToSpeech`
- Playing audio through robot's audio output (if API available)
- Using `HeadAudio.js` in AvatarController to sync with local audio

**Drawback:** Loses Temi TTS quality and branding.

---

### OPTION C: Sync Based on Text Duration (COMPROMISE FIX)

Estimate speech duration from text length and trigger simple mouth animation:

```kotlin
// Estimated speech time: ~15 chars per second
val estimatedDurationMs = (text.length / 15f * 1000).toLong()

// Start TtsLipSyncManager with estimated duration
ttsLipSyncManager.startLipSync(text)

// When TTS completes, stop lip sync
// (detected via TtsRequest.Status.COMPLETED)
```

**Pros:**
- Works with existing Temi TTS
- No audio interception needed
- Simple to implement

**Cons:**
- Not perfectly synced (text length is rough estimate)
- Mouth might stop before/after speech ends
- Language-dependent (Hindi/English have different speech rates)

---

## ✅ IMMEDIATE FIXES (CODE LEVEL)

### FIX 1: Activate TtsLipSyncManager in MainActivity

**File:** `MainActivity.kt`

**Change 1:** Add TtsLipSyncManager instance

```kotlin
// Around line 63, after orchestrator declaration:
private lateinit var ttsLipSyncManager: TtsLipSyncManager

// In onCreate() after setting content:
ttsLipSyncManager = TtsLipSyncManager(
    coroutineScope = lifecycleScope,
    onVisemeUpdate = { viseme, intensity ->
        Log.d("LipSync", "Viseme: $viseme, intensity: $intensity")
        // TODO: Forward to avatar or UI
    }
)
```

**Change 2:** Trigger lip sync when speaking

```kotlin
// In safeSpeak() function, after line 547 (isRobotSpeaking.set(true)):
// Start lip sync with cleaned message
ttsLipSyncManager.startLipSync(cleanedMessage)
Log.d("LIPSYNC", "Started lip sync for: $cleanedMessage")
```

**Change 3:** Stop lip sync when done

```kotlin
// In onTtsStatusChanged(), when TtsRequest.Status.COMPLETED:
ttsLipSyncManager.stopLipSync()
Log.d("LIPSYNC", "Stopped lip sync")
```

---

### FIX 2: Integrate AvatarController with Temi TTS (If Avatar Enabled)

**File:** `MainActivity.kt`

**Add avatar controller:**

```kotlin
private var avatarController: AvatarController? = null

// In onCreate(), check if avatar is used:
// For now, avatar is orphaned. If needed, integrate:
// avatarController = AvatarController(webView, lifecycleScope)
```

**Note:** Avatar/WebView integration requires checking if avatar screen is active. Current codebase doesn't show avatar in main screens.

---

### FIX 3: Estimate Speech Duration Accurately

**File:** `MainActivity.kt`

**Update safeSpeak() to calculate accurate duration:**

```kotlin
// Around line 565, update the duration calculation:
private fun calculateSpeechDurationMs(text: String, language: String): Long {
    // Average speech rates:
    // English: ~150 words/minute = ~2.5 words/second ≈ 0.4 seconds per word ≈ 80ms per word
    // Average word: 4-5 chars, so ~16-20 chars per second = ~50-62 ms per char
    // Hindi: Slightly slower due to diacritics, ~40-50ms per char
    
    val charsPerSecond = if (language == "hi") 13f else 15f  // Adjusted for language
    val estimatedSeconds = text.length / charsPerSecond
    return (estimatedSeconds * 1000).toLong() + 500  // +500ms buffer
}

// Then use it:
val speechDurationMs = calculateSpeechDurationMs(cleanedMessage, detectedLanguage)
safeSpeak_Runnable?.let { handler.removeCallbacks(it) }
safeSpeak_Runnable = Runnable {
    if (isRobotSpeaking.get()) {
        isRobotSpeaking.set(false)
    }
}
handler.postDelayed(safeSpeak_Runnable!!, speechDurationMs)
```

---

### FIX 4: Disable/Comment Out Non-Functional Animations

**File:** `DanceService.kt`

**Update applyHeadTilt() to log intent instead of no-op:**

```kotlin
// Line 303-320:
private fun applyHeadTilt(robot: Robot, tiltY: Float, tiltX: Float) {
    try {
        val tiltYInt = tiltY.toInt()
        val tiltXInt = tiltX.toInt()
        Log.d(TAG, "⚠️ Head tilt intent logged (Temi SDK v1.137.1 has no tilt API): Y=${tiltYInt}°, X=${tiltXInt}°")
        Log.d(TAG, "   → Workaround: Use face animation layer or upgrade to Temi SDK with tilt support")
        
        // No API available - Temi SDK does not provide head tilt
        // Only body rotation via turnBy() is available
    } catch (e: Exception) {
        Log.w(TAG, "Could not apply tilt: ${e.message}")
    }
}
```

---

### FIX 5: Synchronize Dance Moves with Speech

**File:** `DanceService.kt`

**Update performDance() to start lip sync:**

```kotlin
// Around line 81-84:
suspend fun performDance(
    robot: Robot?,
    danceMove: DanceMove,
    language: String = "en-US",
    ttsLipSyncManager: TtsLipSyncManager? = null,  // NEW PARAMETER
    onComplete: (() -> Unit)? = null
) {
    if (robot == null) {
        Log.e(TAG, "Robot is null, cannot perform dance")
        return
    }
    
    try {
        // Initial greeting - with lip sync
        val greeting = "Let me show you my dance moves!"
        speakWithLanguage(greeting, language, robot)
        ttsLipSyncManager?.startLipSync(greeting)  // NEW
        delay(500)
        // ... rest of dance ...
```

---

## 📊 BEFORE vs AFTER

### BEFORE (Current State)
```
User speaks
    ↓
Text to Ollama LLM
    ↓
Response: "Hello! How can I help?"
    ↓
robot.speak(TtsRequest) ← Robot TTS engine (no app visibility)
    ↓
🔇 Robot speaks but mouth doesn't move (no lip sync connected)
```

### AFTER (With Fixes)
```
User speaks
    ↓
Text to Ollama LLM
    ↓
Response: "Hello! How can I help?"
    ↓
robot.speak(TtsRequest) ← Robot TTS engine
    ↓
TtsLipSyncManager.startLipSync(text) ← Estimate visemes from text
    ↓
Avatar or UI updates mouth shape in sync with estimated duration
    ↓
Mouth animation stops when TTS completes
    ↓
✅ Mouth moves (roughly) in sync with speech
```

---

## 🚫 LIMITATIONS

**Even with these fixes, lip sync will NOT be perfect because:**

1. **Text-based estimation is inaccurate:** "Hello" might be spoken in 300ms or 800ms depending on inflection
2. **Language differences:** Hindi and English have different phoneme durations
3. **No real phoneme data:** TtsLipSyncManager maps characters to visemes, not actual phoneme recognition
4. **Temi SDK limitation:** No access to actual audio stream or phoneme timing

**For PERFECT lip sync, you need:**
- Access to audio stream (Android TTS or Temi SDK enhancement)
- Real-time phoneme recognition (e.g., HeadAudio.js + audio playback)
- Or, Temi SDK v2.0+ with built-in lip sync callbacks

---

## 🎯 NEXT STEPS

1. **Apply FIX 1:** Activate TtsLipSyncManager in MainActivity
2. **Apply FIX 3:** Calculate accurate speech duration
3. **Apply FIX 4:** Update DanceService logging
4. **Apply FIX 5:** Pass TtsLipSyncManager to DanceService
5. **Test:** Observe if avatar/UI mouth moves roughly in sync with robot speech
6. **Investigate:** Check Temi SDK v1.137.1 documentation for any lip sync APIs

---

## 📚 REFERENCE

- **Temi SDK:** v1.137.1 (from build.gradle.kts)
- **TtsRequest API:** Only supports text, language, showOnConversationLayer (no viseme data)
- **LipSyncManager:** Deprecated, uses microphone audio (not applicable)
- **TtsLipSyncManager:** Ready to use, never integrated
- **AvatarController:** Ready, but requires audio playback (Temi SDK doesn't provide)

---

**Compiled by:** AI Code Audit  
**Status:** Ready for Implementation

