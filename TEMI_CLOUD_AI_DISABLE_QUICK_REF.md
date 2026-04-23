# QUICK REFERENCE: Temi Cloud AI Disable

## THE FIX IN 30 SECONDS

**Problem:** Temi was using its default cloud AI instead of Ollama.

**Solution:** 
1. ❌ **NEVER** add NLP listener in `onRobotReady()`
2. ✅ **ALWAYS** block `onNlpCompleted()` immediately
3. ✅ **ALWAYS** block `onConversationStatusChanged()` responses
4. ✅ Process all speech through custom Ollama pipeline

---

## Code Changes Made

### 1. onRobotReady() - Register ONLY Manual Listeners

```kotlin
override fun onRobotReady(isReady: Boolean) {
    if (isReady) {
        robotState.value = Robot.getInstance()
        
        // ✅ Register only manual listeners:
        robot?.addAsrListener(this)                                   
        robot?.addTtsListener(this)                                   
        robot?.addConversationViewAttachesListener(this)              
        robot?.addOnConversationStatusChangedListener(this)           
        
        // ❌ DO NOT ADD NLP LISTENER - This enables Temi cloud AI
        // robot?.addNlpListener(this)  
    }
}
```

**Why:** Without the NLP listener, Temi SDK cannot process speech with cloud AI. All processing goes through our manual pipeline.

---

### 2. onAsrResult() - Exclusive Ollama Pipeline

```kotlin
override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
    if (asrResult.isBlank()) return
    
    // Block if already processing
    if (isConversationActive) return
    
    // Ensure serial processing
    if (!isProcessingSpeech.compareAndSet(false, true)) return
    
    try {
        processSpeech(asrResult)  // Goes to Ollama ONLY
    } finally {
        isProcessingSpeech.set(false)
    }
}
```

**Why:** This forces every speech input through our custom `processSpeech()` method, which calls Ollama exclusively.

---

### 3. onNlpCompleted() - Safety Block

```kotlin
override fun onNlpCompleted(nlpResult: NlpResult) {
    // This should NEVER be called (NLP listener not registered)
    // But if it is, block it immediately
    android.util.Log.e("TEMI_CLOUD_AI_BLOCK", "Blocking Temi NLP: ${nlpResult.action}")
    return  // ← Do NOT process
}
```

**Why:** Even if NLP listener somehow gets registered, this prevents any response.

---

### 4. onConversationStatusChanged() - Block Temi Q&A

```kotlin
override fun onConversationStatusChanged(status: Int, text: String) {
    if (text.isNotBlank()) {
        // Clear any Temi TTS responses
        robot?.speak(TtsRequest.create("", false))
        synchronized(pendingTtsIds) { pendingTtsIds.clear() }
        isRobotSpeaking.set(false)
    }
    return  // ← Always block
}
```

**Why:** Temi SDK may generate Q&A responses. This clears them before they can be spoken.

---

## Data Flow

```
User speaks
    ↓
onAsrResult() - Manual STT capture
    ↓
processSpeech() - Our custom function
    ↓
Ollama LLM - Get response (EXCLUSIVE)
    ↓
safeSpeak() - Speak Ollama response
    ↓
onTtsStatusChanged() - Track speech

❌ Blocked paths:
   - onNlpCompleted() [NLP listener not registered]
   - onConversationStatusChanged() [responses cleared]
   - Temi default AI [no way to process]
```

---

## Testing

### ✅ Expected Behavior
- Say something → Only Ollama responds
- No Temi default voice responses
- Logcat shows: "MANUAL_PIPELINE" → "OLLAMA_FIX"

### ❌ If Temi Still Responds
1. Check that NLP listener NOT added in onRobotReady()
2. Check that onConversationStatusChanged() clears TTS queue
3. Search logcat for "TEMI_CLOUD_AI_BLOCK" entries

---

## Critical Rules

| ✅ DO | ❌ DON'T |
|------|---------|
| Register ASR listener | Add NLP listener |
| Block onNlpCompleted() | Call robot?.askQuestion() |
| Block onConversationStatusChanged() | Use robot?.startDefaultNlu() |
| Use isConversationActive lock | Enable "OK Temi" |
| Process through processSpeech() | Fall back to Temi AI |

---

## Debugging Checklist

```bash
# Check for Temi cloud AI blocks
adb logcat | grep "TEMI_CLOUD_AI_BLOCK"
# Expected: Empty or minimal entries

# Check for manual pipeline
adb logcat | grep "MANUAL_PIPELINE"
# Expected: "ASR RESULT RECEIVED" for each speech

# Check Ollama calls
adb logcat | grep "OLLAMA_FIX"
# Expected: "STARTING OLLAMA CONVERSATION"
```

---

## Files Modified

- **MainActivity.kt**
  - onRobotReady(): Register manual listeners only
  - onAsrResult(): Force Ollama pipeline
  - onNlpCompleted(): Safety block
  - onConversationStatusChanged(): Block Temi Q&A
  - Removed unused: gptTimeoutRunnable, GPT_TIMEOUT_MS, speakStreamingChunk(), checkImmediateCommands()
  - Cleaned unused imports

---

## Status: ✅ COMPLETE

All changes implemented and tested. Ready for production deployment.

**See:** TEMI_CLOUD_AI_DISABLE_COMPLETE.md for full documentation.

