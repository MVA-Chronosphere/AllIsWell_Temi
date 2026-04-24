# ⚡ QUICK REFERENCE: MANUAL VOICE PIPELINE (POST-CLOUD-AI-FIX)

**Last Updated:** April 23, 2026  
**Status:** ✅ PRODUCTION READY  

---

## 🎯 THE ONE RULE

**Temi is a hardware interface. Ollama is the AI.**

```
User → Temi Mic → ASR → onAsrResult() → Ollama → safeSpeak() → Temi Speaker
```

**NO Temi cloud AI at ANY step.**

---

## ✅ WHAT'S CHANGED

| Before | After |
|--------|-------|
| ❌ `robot?.askQuestion(...)` | ✅ Only ASR listening |
| ❌ `robot?.wakeup()` after TTS | ✅ ASR always active |
| ❌ `isShowOnConversationLayer = true` | ✅ `isShowOnConversationLayer = false` |
| ❌ NLP listener registered | ✅ NLP listener NOT registered |
| ❌ Temi cloud AI processing | ✅ Only Ollama processing |

---

## 📡 VOICE FLOW (4 STEPS)

```
┌─ STEP 1: User speaks
│  └─ onAsrResult(speech)
│     Log: ✅ STEP 1: Speech captured by ASR
│
├─ STEP 2: Validate & block duplicates
│  └─ isProcessingSpeech check
│     isConversationActive check
│
├─ STEP 3: Call Ollama
│  └─ processSpeech() → callOllama()
│     Log: ✅ STEP 3: Calling Ollama LLM
│
└─ STEP 4: Speak response
   └─ safeSpeak()
      Log: ✅ STEP 4: Speaking Ollama response
      isShowOnConversationLayer = FALSE
```

---

## 🚫 FORBIDDEN METHODS

**NEVER use these:**

```kotlin
❌ robot?.askQuestion("...")          // Activates cloud AI
❌ robot?.wakeup()                     // Resets ASR state
❌ isShowOnConversationLayer = true    // Shows cloud UI
❌ robot?.addNlpListener(this)         // Enables cloud processing
```

---

## ✅ REQUIRED METHODS

**ALWAYS use these:**

```kotlin
✅ onAsrResult()                       // ONLY entry point
✅ processSpeech()                     // Validate & process
✅ callOllama()                        // Call LLM
✅ safeSpeak()                         // Speak response
✅ isShowOnConversationLayer = false   // Disable Temi UI
```

---

## 🔍 DEBUGGING

### Monitor the Pipeline
```bash
adb logcat | grep "VOICE_PIPELINE"
```

### Expected Logs (for each query)
```
I/VOICE_PIPELINE_FLOW: ✅ STEP 1: Speech captured by ASR
I/VOICE_PIPELINE_FLOW: ✅ STEP 2: Starting manual speech processing
I/VOICE_PIPELINE_FLOW: ✅ STEP 3: Calling Ollama LLM
I/VOICE_PIPELINE_FLOW: ✅ STEP 4: Speaking Ollama response
D/VOICE_PIPELINE: TTS finished - ASR continues to listen
```

### If You See Cloud AI Logs
```
E/TEMI_CLOUD_AI_BLOCK: ❌ BLOCKING Temi cloud NLP response!
```
**Action:** Check code for forbidden methods (see above).

---

## 📝 CODE CHECKLIST

When adding new voice features:

- [ ] No `askQuestion()` calls
- [ ] No `wakeup()` calls
- [ ] All TTS requests have `isShowOnConversationLayer = false`
- [ ] Speech input goes through `onAsrResult()` only
- [ ] Voice state is managed with `isConversationActive` and `isProcessingSpeech`
- [ ] Ollama response is NOT interrupted
- [ ] Debug logs use `VOICE_PIPELINE` tag
- [ ] Tested on Temi robot (not just emulator)

---

## 🧪 QUICK TEST

```kotlin
// What you CAN do:
onVoiceClick = {
    Log.d("VOICE_PIPELINE", "Voice input initiated - ASR listening")
    // Let onAsrResult() handle it
}

// What you CANNOT do:
onVoiceClick = {
    robot?.askQuestion("What do you want?")  // ❌ NO!
}
```

---

## 📊 SYSTEM STATE VARIABLES

```kotlin
// Dual-lock system prevents duplicate conversations
@Volatile
var isConversationActive = false          // Mutex lock for Ollama
var isProcessingSpeech = AtomicBoolean()  // Race condition safety
var isGptProcessing = mutableStateOf()    // UI state tracking
```

**Never bypass these checks!**

---

## 🎓 REMEMBER

1. **ASR is always listening** (Temi hardware)
2. **Ollama is the only brain** (local LLM)
3. **User hears ONLY Ollama responses** (no cloud AI)
4. **Pipeline is serial** (one conversation at a time)
5. **No Temi cloud AI triggers** (askQuestion, wakeup, etc.)

---

## 🆘 IF TEMI IS ANSWERING ON ITS OWN

**Cloud AI has re-activated. Check:**

1. `grep "askQuestion" app/src/main/java/com/example/alliswelltemi/ui/screens/*.kt`
2. `grep "addNlpListener" app/src/main/java/com/example/alliswelltemi/MainActivity.kt`
3. `grep "wakeup()" app/src/main/java/com/example/alliswelltemi/MainActivity.kt`
4. `grep "isShowOnConversationLayer = true" app/src/main/java/com/example/alliswelltemi/**/*.kt`

If any of these appear, you've violated the rules. Fix immediately.

---

**Questions?** See `TEMI_CLOUD_AI_DISABLED_FINAL.md` for detailed documentation.

