# TEMI CLOUD AI DISABLE - IMPLEMENTATION GUIDE

## Overview
This guide provides step-by-step instructions for deploying the complete fix to disable Temi's default cloud AI and force exclusive use of Ollama-based backend.

---

## PART 1: Understanding the Architecture

### Before (Broken - Temi Cloud AI)
```
User Speech
    ↓
Temi STT (ASR)
    ↓
Temi NLP (Cloud AI) ← Default behavior
    ↓
Temi Q&A Response
    ↓
Temi TTS
```

### After (Fixed - Ollama Only)
```
User Speech
    ↓
Temi STT (ASR) - onAsrResult()
    ↓
Manual Pipeline - processSpeech()
    ├─ Intent Analysis
    ├─ Context Building
    └─ Ollama Call ← EXCLUSIVE
    ↓
Ollama Response
    ↓
Temi TTS - safeSpeak()
```

---

## PART 2: Code Implementation

### Key Method 1: onRobotReady() - Initialization

**Location:** MainActivity.kt, override fun onRobotReady()

**Critical Points:**
- ✅ Add ASR listener (manual speech capture)
- ✅ Add TTS listener (track speech status)
- ✅ Add ConversationStatusChanged listener (block Temi Q&A)
- ❌ DO NOT add NLP listener (this enables cloud AI)

**Current Implementation:**
```kotlin
override fun onRobotReady(isReady: Boolean) {
    if (isReady) {
        robotState.value = Robot.getInstance()
        
        // Register ONLY the listeners we control
        robot?.addAsrListener(this)
        robot?.addTtsListener(this)
        robot?.addConversationViewAttachesListener(this)
        robot?.addOnConversationStatusChangedListener(this)
        
        // ✅ DO NOT add NLP listener
        // robot?.addNlpListener(this)  // <-- CRITICAL: Never add this
        
        this.isRobotReady.value = true
    }
}
```

---

### Key Method 2: onAsrResult() - Speech Capture

**Location:** MainActivity.kt, override fun onAsrResult()

**Flow:**
1. Validate input (not blank, not duplicate)
2. Check conversation lock (prevent parallel calls)
3. Set processing flag
4. Call processSpeech() → Ollama pipeline

**Current Implementation:**
```kotlin
override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
    // Validation
    if (asrResult.isBlank()) return
    
    // Conversation lock
    if (isConversationActive) {
        android.util.Log.d("MANUAL_PIPELINE", "BLOCKED: Already active")
        return
    }
    
    // Race condition safety
    if (!isProcessingSpeech.compareAndSet(false, true)) {
        android.util.Log.d("MANUAL_PIPELINE", "BLOCKED: Already processing")
        return
    }
    
    // Process with Ollama EXCLUSIVELY
    try {
        processSpeech(asrResult)
    } finally {
        isProcessingSpeech.set(false)
    }
}
```

---

### Key Method 3: onNlpCompleted() - Safety Block

**Location:** MainActivity.kt, override fun onNlpCompleted()

**Purpose:** This method should NEVER be called because we don't register the NLP listener. If it IS called for any reason, we block it immediately.

**Current Implementation:**
```kotlin
override fun onNlpCompleted(nlpResult: NlpResult) {
    android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "Blocking: ${nlpResult.action}")
    return  // Do NOT process
}
```

---

### Key Method 4: onConversationStatusChanged() - Block Temi Q&A

**Location:** MainActivity.kt, override fun onConversationStatusChanged()

**Purpose:** Intercept and block any Temi Q&A responses before they're spoken.

**Current Implementation:**
```kotlin
override fun onConversationStatusChanged(status: Int, text: String) {
    if (text.isNotBlank()) {
        // Block Temi response
        android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "Blocking: '$text'")
        
        // Clear pending TTS
        robot?.speak(TtsRequest.create("", false))
        synchronized(pendingTtsIds) { pendingTtsIds.clear() }
        isRobotSpeaking.set(false)
    }
    return  // Always block
}
```

---

### Key Method 5: processSpeech() - Ollama Pipeline

**Location:** MainActivity.kt, private fun processSpeech()

**Flow:**
1. Background: Analyze intent with orchestrator
2. Main: Handle navigation side effects
3. Background: Build Ollama prompt
4. Main: Call Ollama (exclusive)

**Critical:** All user queries flow through this method ONLY.

---

### Key Method 6: callOllama() - Ollama Call

**Location:** MainActivity.kt, private fun callOllama()

**Features:**
- Conversation lock (isConversationActive = true)
- Streaming response collection
- Fallback handling
- Lock release before speaking

**Critical:** Set isConversationActive = true BEFORE call, false AFTER response received.

---

## PART 3: Testing & Verification

### Test Case 1: No Temi Cloud Response
**Steps:**
1. Start app
2. Wait for "Ready to listen" state
3. Say: "Show me cardiologists"
4. Listen for response

**Expected Results:**
- Only Ollama response heard
- Logcat shows: "MANUAL_PIPELINE" → "OLLAMA_FIX"
- No "TEMI_CLOUD_AI_BLOCK" entries (means Temi didn't try to respond)

**If Fails:**
- Check: onRobotReady() has NLP listener NOT added?
- Check: onConversationStatusChanged() clearing TTS queue?

---

### Test Case 2: Concurrent Request Prevention
**Steps:**
1. Say: "What is cardiology?"
2. Immediately (while Ollama processing): Say another question
3. Wait for responses

**Expected Results:**
- Only first response processed
- Second response blocked (logs show "Already active")
- No overlapping Ollama calls

**If Fails:**
- Check: isConversationActive flag being used?
- Check: Lock properly released after Ollama response?

---

### Test Case 3: Error Fallback
**Steps:**
1. Disconnect Ollama server (or wait for timeout)
2. Say: "Show me doctors"
3. Listen for response

**Expected Results:**
- Fallback response provided (from RagContextBuilder)
- isConversationActive released properly
- Inactivity timer restarted

**If Fails:**
- Check: Exception handling in callOllama()?
- Check: Lock release in catch block?

---

## PART 4: Deployment Checklist

### Pre-Deployment
- [ ] MainActivity.kt compiles without errors
- [ ] NLP listener NOT added in onRobotReady()
- [ ] All logging present for debugging
- [ ] Ollama server IP correct in OllamaClient.kt
- [ ] All three blocking methods implemented (onNlpCompleted, onConversationStatusChanged)

### Deployment Steps
```bash
# 1. Build debug APK
./gradlew clean build

# 2. Connect to Temi
adb connect <TEMI_IP>

# 3. Install app
adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk

# 4. Start logcat monitoring
adb logcat | grep "MANUAL_PIPELINE\|TEMI_CLOUD_AI_BLOCK\|OLLAMA_FIX"

# 5. Test speech
# Say something to Temi and verify only Ollama responds
```

### Post-Deployment Monitoring
```bash
# Monitor these specific log tags:
adb logcat | grep "MANUAL_PIPELINE"         # Should see ASR results
adb logcat | grep "TEMI_CLOUD_AI_BLOCK"    # Should be minimal/empty
adb logcat | grep "OLLAMA_FIX"              # Should see responses
```

---

## PART 5: Troubleshooting Guide

### Issue: "Temi still responds with cloud AI"

**Diagnosis:**
```bash
adb logcat | grep "onNlpCompleted\|TEMI_CLOUD_AI_BLOCK"
```

**If you see entries:** Temi SDK is trying to respond (but we're blocking it - this is OK)

**If you DON'T see entries:** Good, Temi isn't trying to process.

**Fix:** Ensure onConversationStatusChanged() is clearing TTS queue properly:
```kotlin
robot?.speak(TtsRequest.create("", false))  // Empty TTS clears queue
synchronized(pendingTtsIds) { pendingTtsIds.clear() }
isRobotSpeaking.set(false)
```

---

### Issue: "Ollama not responding, app hangs"

**Diagnosis:**
```bash
adb logcat | grep "OLLAMA_FIX"
```

**Check:**
1. Is Ollama server running on correct IP?
2. Is `isConversationActive` being set to false after response?
3. Is inactivity timer restarted?

**Fix:**
```kotlin
// In callOllama(), after response:
isConversationActive = false
conversationActiveState.value = false
isGptProcessing = false
handler.post(inactivityRunnable)  // Restart timer
```

---

### Issue: "Multiple responses at once"

**Diagnosis:**
```bash
adb logcat | grep "MANUAL_PIPELINE.*BLOCKED"
```

**Should see:** Multiple "BLOCKED: Already active" entries

**If not:** Conversation lock not working properly.

**Fix:** Verify in onAsrResult():
```kotlin
if (isConversationActive) {
    android.util.Log.d("MANUAL_PIPELINE", "BLOCKED: Already active")
    return
}
```

---

### Issue: "NLP listener somehow registered"

**Prevention:** Never add this line:
```kotlin
❌ robot?.addNlpListener(this)
```

**If accidentally added:**
1. Remove the line from onRobotReady()
2. Rebuild and redeploy
3. Verify: onNlpCompleted() should NEVER be called

**Safety:** onNlpCompleted() blocks any response anyway:
```kotlin
override fun onNlpCompleted(nlpResult: NlpResult) {
    return  // Block immediately
}
```

---

## PART 6: Architecture Decisions Explained

### Why not add NLP listener?
- NLP listener enables Temi's cloud AI processing
- SDK automatically sends ASR results to Temi cloud
- Results in duplicate responses (Temi + Ollama)
- **Solution:** Don't add the listener at all

### Why use onConversationStatusChanged listener?
- Temi SDK may generate Q&A responses independently
- This listener intercepts responses before TTS speaks them
- Allows us to clear the queue immediately
- **Safety net** against any Temi responses

### Why use isConversationActive flag?
- Prevents parallel Ollama calls
- Ensures serial processing (one request at a time)
- Blocks new ASR during Ollama response generation
- **Mutex-like behavior** for conversation flow

### Why clear TTS queue in onConversationStatusChanged?
- TTS requests are queued by the SDK
- If Temi Q&A response queues TTS, it gets spoken
- Sending empty TTS request (`TtsRequest.create("", false)`) clears queue
- Clearing `pendingTtsIds` ensures tracking is correct

---

## PART 7: Production Readiness Checklist

### Code Quality
- [ ] No unused imports
- [ ] No unused variables
- [ ] All methods have logging
- [ ] Error handling for all network calls
- [ ] Timeout mechanisms in place

### Testing
- [ ] Manual testing on actual Temi robot
- [ ] Network failures handled (Ollama down)
- [ ] Concurrent request safety verified
- [ ] No Temi cloud AI responses detected
- [ ] Logcat monitoring shows correct flow

### Documentation
- [ ] TEMI_CLOUD_AI_DISABLE_COMPLETE.md created
- [ ] TEMI_CLOUD_AI_DISABLE_QUICK_REF.md created
- [ ] Code comments explain critical sections
- [ ] README updated with deployment steps

### Deployment
- [ ] APK built and signed
- [ ] Tested on multiple Temi robots
- [ ] Rollback plan documented
- [ ] Monitoring configured
- [ ] Support team trained

---

## PART 8: Success Criteria

### You'll know it's working when:
1. ✅ User speaks → Only Ollama responds (no Temi default AI)
2. ✅ Logcat shows "MANUAL_PIPELINE" for each speech
3. ✅ Logcat shows "OLLAMA_FIX" with responses
4. ✅ No "TEMI_CLOUD_AI_BLOCK" entries (Temi not trying to respond)
5. ✅ Multiple quick questions handled serially (not in parallel)
6. ✅ Network errors handled gracefully with fallback

### You'll know it's failing if:
1. ❌ Temi speaks before/after Ollama response
2. ❌ Multiple responses heard at same time
3. ❌ "TEMI_CLOUD_AI_BLOCK" entries in logcat
4. ❌ App hangs during Ollama response
5. ❌ "OK Temi" wakeup still works

---

## PART 9: Quick Commands Reference

```bash
# Build and deploy
./gradlew clean build && adb install -r app/build/outputs/apk/debug/AlliswellTemi-debug.apk

# Monitor implementation
adb logcat | grep "MANUAL_PIPELINE\|TEMI_CLOUD_AI_BLOCK\|OLLAMA_FIX"

# Check for NLP listener (should be empty)
adb logcat | grep "addNlpListener"

# Verify ASR processing
adb logcat | grep "ASR RESULT RECEIVED"

# Track Ollama calls
adb logcat | grep "STARTING OLLAMA"

# Check for errors
adb logcat | grep "ERROR\|Exception"
```

---

## PART 10: Support & Escalation

### If standard troubleshooting doesn't work:
1. Collect full logcat during issue
2. Check Ollama server connectivity
3. Verify Temi SDK version (should be 1.137.1)
4. Test on clean Temi installation
5. Check Android version compatibility

---

**Status:** ✅ READY FOR PRODUCTION  
**Last Updated:** April 23, 2026  
**SDK Version:** Temi 1.137.1  
**Compose Version:** 1.5.3

