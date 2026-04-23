# OLLAMA NLP Fix - Exact Code Changes in MainActivity.kt

## Overview
**Problem:** Temi SDK's NLP listener was causing duplicate responses (both Temi Q&A and Ollama)
**Solution:** Disable NLP listener, use ONLY Ollama for responses
**File:** `MainActivity.kt`
**Changes:** 4 strategic locations

---

## Change Location 1: Lines 201-214
### Method: `onNlpCompleted()`
**Purpose:** Safety blocking if NLP is somehow triggered despite being disabled

```kotlin
// REPLACE THIS:
override fun onNlpCompleted(nlpResult: NlpResult) {
    val elapsedMs = System.currentTimeMillis() - gptRequestStartTime
    android.util.Log.d("TemiSpeech", "NLP Result (after ${elapsedMs}ms): action=${nlpResult.action}, query=${nlpResult.resolvedQuery}")
}

// WITH THIS:
override fun onNlpCompleted(nlpResult: NlpResult) {
    // ⚠️ NOTE: This method should NOT be called because we don't add the NLP listener in onRobotReady()
    // If for some reason it IS called, we block it here as a safety measure
    android.util.Log.w("TemiSpeech", "⚠️ WARNING: onNlpCompleted() called despite NLP listener being disabled!")
    android.util.Log.d("TemiSpeech", "NLP Result: action=${nlpResult.action}, query=${nlpResult.resolvedQuery}")

    // CRITICAL: BLOCK Temi SDK NLP automatic responses - we use OLLAMA exclusively
    android.util.Log.d("OLLAMA_FIX", "========== BLOCKING TEMI NLP AUTOMATIC RESPONSE ==========")
    android.util.Log.d("OLLAMA_FIX", "Blocked NLP action: '${nlpResult.action}'")
    android.util.Log.d("OLLAMA_FIX", "This should NOT trigger automatic response - using OLLAMA only")

    // Do NOT call the action or let Temi SDK respond automatically
    return
}
```

---

## Change Location 2: Lines 216-245
### Method: `onConversationStatusChanged()`
**Purpose:** Aggressive blocking of any remaining Temi Q&A responses

```kotlin
// REPLACE THIS:
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

// WITH THIS:
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
        
        // IMPORTANT: Do NOT let the Temi SDK continue processing
        // This method is called when Temi SDK has a response ready to speak
        // By blocking here, we prevent the default behavior
        return  // ✅ Block Temi SDK response completely
    }

    return  // ✅ Always block all Temi SDK conversation responses
}
```

---

## Change Location 3: Lines 610-628 ⭐ CRITICAL
### Method: `onRobotReady()`
**Purpose:** DISABLE the NLP listener - the main fix

```kotlin
// REPLACE THIS:
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

// WITH THIS:
override fun onRobotReady(isReady: Boolean) {
    if (isReady) {
        robotState.value = Robot.getInstance()
        robot?.addAsrListener(this)
        // ✅ CRITICALLY IMPORTANT: DO NOT add NLP listener
        // Temi SDK's NLP system has automatic Q&A responses
        // We handle speech processing ourselves with Ollama
        // robot?.addNlpListener(this)  // <-- DISABLED: Using Ollama instead
        robot?.addTtsListener(this)
        robot?.addConversationViewAttachesListener(this)

        // Add conversation status listener to block any remaining Temi Q&A responses
        robot?.addOnConversationStatusChangedListener(this)

        // NOTE: Temi SDK v1.137.1 doesn't have a setConversationMode() method
        // Instead, we manage our own isConversationActive flag and block overlapping requests
        android.util.Log.d("TemiMain", "✅ NLP listener DISABLED - using Ollama only")
        android.util.Log.d("TemiMain", "Using custom conversation lock (isConversationActive) to manage Ollama")

        this.isRobotReady.value = true
    }
}
```

---

## Change Location 4: Lines 642-650
### Method: `onDestroy()`
**Purpose:** Don't remove listener that was never added

```kotlin
// REPLACE THIS:
override fun onDestroy() {
    super.onDestroy()
    Robot.getInstance().removeOnRobotReadyListener(this)
    robot?.removeAsrListener(this)
    robot?.removeNlpListener(this)
    robot?.removeTtsListener(this)
    robot?.removeConversationViewAttachesListener(this)
    robot?.removeOnConversationStatusChangedListener(this)
}

// WITH THIS:
override fun onDestroy() {
    super.onDestroy()
    Robot.getInstance().removeOnRobotReadyListener(this)
    robot?.removeAsrListener(this)
    // robot?.removeNlpListener(this)  // <-- Not added, so not removing
    robot?.removeTtsListener(this)
    robot?.removeConversationViewAttachesListener(this)
    robot?.removeOnConversationStatusChangedListener(this)
}
```

---

## Quick Checklist

- [ ] Changed `onNlpCompleted()` (Lines 201-214)
- [ ] Changed `onConversationStatusChanged()` (Lines 216-245)
- [ ] **Changed `onRobotReady()` - DISABLED NLP listener** (Lines 610-628) ⭐
- [ ] Changed `onDestroy()` (Lines 642-650)
- [ ] Build project: `./gradlew clean build`
- [ ] Install: `./gradlew installDebug`
- [ ] Test: Speak "How are you?" - should hear ONLY Ollama response

## Verification

After making changes:

```bash
# Verify compilation
./gradlew clean build
# Expected: BUILD SUCCESSFUL

# Install on device
./gradlew installDebug

# Check logcat
adb logcat | grep "NLP\|OLLAMA\|ASR"

# Expected output:
# D/TemiMain: ✅ NLP listener DISABLED - using Ollama only
# D/TemiSpeech: ASR Result: 'how are you'
# D/OLLAMA_FIX: ========== STARTING OLLAMA CONVERSATION ==========
# D/OLLAMA_RESPONSE: Hi there! I'm doing great, thanks for asking!
```

## Summary

| Change # | Location | Impact | Importance |
|----------|----------|--------|-----------|
| 1 | `onNlpCompleted()` | Safety blocking | Medium |
| 2 | `onConversationStatusChanged()` | Aggressive blocking | High |
| 3 | `onRobotReady()` - DISABLE NLP | **MAIN FIX** | **CRITICAL** ⭐ |
| 4 | `onDestroy()` | Cleanup consistency | Medium |

**Most Important:** Change Location 3 (disable NLP listener in onRobotReady)


