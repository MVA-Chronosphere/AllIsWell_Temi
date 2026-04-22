# Ollama Voice Pipeline - One-Page Cheat Sheet

**Print this page for quick reference!**

---

## 🎯 The Pipeline in 30 Seconds

```
User speaks → Speech to text → Send to Ollama → Get LLM response → Temi speaks
             (2-5 sec)          (3-10 sec)       (instant)        (3-8 sec)
                                                                   
Total time: 8-23 seconds, fully async, no main thread blocking
```

---

## 📁 Files Created

### Code (in `app/src/main/java/com/example/alliswelltemi/`)
```
network/OllamaModels.kt           60 lines  ← Data structures
network/OllamaApiService.kt       20 lines  ← Retrofit interface
network/OllamaClient.kt           50 lines  ← API client singleton
utils/VoiceInteractionManager.kt  420 lines ← Main voice pipeline
```

### Documentation (in project root)
```
OLLAMA_VOICE_PIPELINE_QUICK_REF.md           ← READ FIRST
OLLAMA_VOICE_PIPELINE_READY_TO_USE.kt        ← COPY FROM HERE
OLLAMA_VOICE_PIPELINE.md                     ← FULL GUIDE
OLLAMA_VOICE_PIPELINE_DEPLOYMENT.md          ← DEPLOYMENT
OLLAMA_VOICE_PIPELINE_DOCUMENTATION_INDEX.md ← NAVIGATION
```

---

## ⚡ Integration in 6 Steps

### Step 1: Add Properties
```kotlin
private var voiceInteractionManager: VoiceInteractionManager? = null
private val currentVoiceState = mutableStateOf(VoiceState.IDLE)
```

### Step 2: Initialize in onCreate()
```kotlin
Robot.getInstance().addOnRobotReadyListener { isReady ->
    if (isReady) {
        voiceInteractionManager = VoiceInteractionManager(
            this, robotState.value, lifecycleScope
        )
        voiceInteractionManager?.setOnError { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}
```

### Step 3: Start Listening
```kotlin
voiceInteractionManager?.startListening()
```

### Step 4: Handle Results (Optional)
```kotlin
voiceInteractionManager?.setOnVoiceResultReady { result ->
    Log.d("Voice", "User: ${result.spokenText}")
    Log.d("Voice", "Response: ${result.llmResponse}")
}
```

### Step 5: Handle Errors (Optional)
```kotlin
voiceInteractionManager?.setOnError { error ->
    Log.e("Voice", error)
}
```

### Step 6: Cleanup in onDestroy()
```kotlin
override fun onDestroy() {
    voiceInteractionManager?.release()
    super.onDestroy()
}
```

---

## 🔧 Configuration

### Ollama Server IP
Edit `OllamaClient.kt`:
```kotlin
// For Temi robot (default)
private const val BASE_URL = "http://192.168.137.1:11434/"

// For Android emulator
// private const val BASE_URL = "http://10.0.2.2:11434/"
```

### Ollama Model
Edit `VoiceInteractionManager.kt` in `processSpeechWithOllama()`:
```kotlin
OllamaRequest(
    model = "llama3:8b",        // Or llama3:7b for faster
    prompt = hospitalContextPrompt,
    stream = false,
    temperature = 0.7           // Adjust for creativity
)
```

---

## 🚀 Setup

### 1. Start Ollama
```bash
OLLAMA_HOST=0.0.0.0:11434 ollama serve
ollama pull llama3:8b
```

### 2. Verify Connection
```bash
curl -X POST http://192.168.137.1:11434/api/generate \
  -d '{"model":"llama3:8b","prompt":"Hello","stream":false}'
```

### 3. Build App
```bash
./gradlew build
adb install -r app/build/outputs/apk/debug/*.apk
```

### 4. Test
```bash
adb logcat | grep VoiceInteraction
# Tap mic, speak "Hello"
# Check for: "Speech recognized", "Ollama response", "Speaking"
```

---

## 🎤 Voice States

```
IDLE        → Ready for input
LISTENING   → Recording speech
PROCESSING  → Sending to Ollama
THINKING    → LLM generating
SPEAKING    → Temi speaking response
ERROR       → Something went wrong (fallback response provided)
```

### Monitor States
```kotlin
voiceInteractionManager?.setOnStateChanged { state ->
    when (state) {
        VoiceState.LISTENING -> showPulse()
        VoiceState.THINKING -> showSpinner()
        VoiceState.SPEAKING -> showSpeaking()
        VoiceState.ERROR -> showError()
        else -> hideIndicators()
    }
}
```

---

## 📊 Performance

| Stage | Time |
|-------|------|
| Speech Recognition | 2-5s |
| Ollama Processing | 3-10s |
| TTS Output | 3-8s |
| **TOTAL** | **8-23s** |

---

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| "Network error" | Start Ollama: `OLLAMA_HOST=0.0.0.0:11434 ollama serve` |
| "No speech detected" | Check RECORD_AUDIO permission, speak louder |
| "Robot not ready" | Initialize VoiceInteractionManager AFTER `onRobotReadyListener` |
| "Speech recognition not available" | Test on different device |
| "Permission denied" | Request RECORD_AUDIO permission at runtime |
| "Slow responses" | Use `llama3:7b` instead of `8b` |

---

## 🔐 Security Checklist

- ✅ All processing local (no cloud APIs)
- ✅ HIPAA-friendly
- ✅ No patient data sent externally
- ✅ Runs on private hospital network
- ✅ No API keys needed

---

## 📝 Key Classes

### VoiceInteractionManager
```kotlin
VoiceInteractionManager(context, robot, coroutineScope)

// Public API
.startListening()          // Start speech recognition
.stopListening()           // Stop manually
.setOnStateChanged(cb)     // Monitor state changes
.setOnVoiceResultReady(cb) // Get result with LLM response
.setOnError(cb)            // Handle errors
.release()                 // Cleanup
```

### Data Classes
```kotlin
OllamaRequest(model, prompt, stream, temperature)
OllamaResponse(model, response, done, ...)
VoiceState enum: IDLE, LISTENING, PROCESSING, THINKING, SPEAKING, ERROR
VoiceResult(spokenText, llmResponse, processingTimeMs)
```

---

## 💡 Tips & Tricks

### Tip 1: Custom Hospital Prompt
Edit `VoiceInteractionManager.kt` method `buildHospitalContextPrompt()` to add:
- Doctor availability
- Operating hours
- Department specialties
- Common questions

### Tip 2: Parse User Intent
Add voice navigation:
```kotlin
voiceInteractionManager?.setOnVoiceResultReady { result ->
    when {
        result.spokenText.contains("doctor") -> 
            currentScreen.value = "doctors"
        result.spokenText.contains("pharmacy") -> 
            currentScreen.value = "navigation"
        // ... more intents ...
    }
}
```

### Tip 3: Response Caching
Cache common responses to reduce Ollama calls:
```kotlin
val cache = mutableMapOf<String, String>()
if (prompt in cache) {
    return cache[prompt]!!
}
```

### Tip 4: Add UI Feedback
Show indicators for each state:
```kotlin
when (currentVoiceState.value) {
    VoiceState.LISTENING -> showMicPulse()
    VoiceState.THINKING -> showSpinner()
    VoiceState.SPEAKING -> showSpeaker()
}
```

---

## 📚 Documentation

```
START HERE
    ↓
OLLAMA_VOICE_PIPELINE_QUICK_REF.md (this sheet)
    ↓
OLLAMA_VOICE_PIPELINE_READY_TO_USE.kt (copy code)
    ↓
OLLAMA_VOICE_PIPELINE.md (full guide if needed)
```

---

## ✅ Pre-Flight Checklist

Before deploying:
- [ ] Ollama running
- [ ] Model installed: `ollama list`
- [ ] Network working: `curl` test passes
- [ ] Code integrated into MainActivity
- [ ] Build succeeds: `./gradlew build`
- [ ] APK installs: `adb install -r ...apk`
- [ ] Logcat shows no errors
- [ ] Voice pipeline tested end-to-end

---

## 🎯 What Happens When User Speaks

```
1. User taps mic
2. App shows "Listening..."
3. User says "Take me to pharmacy"
4. Speech recognized (2-5 sec)
5. App shows "Processing..."
6. Sent to Ollama with hospital context
7. LLM generates response (3-10 sec)
8. Response: "The pharmacy is on ground floor..."
9. App shows "Speaking..."
10. Temi speaks response (3-8 sec)
11. User hears answer
12. Ready for next command
```

---

## 🚀 You're Ready!

**Time to integrate: 30 minutes**
**Time to test: 5 minutes**
**Total: 35 minutes to working voice pipeline!**

Start with `OLLAMA_VOICE_PIPELINE_READY_TO_USE.kt` and copy code snippets.

---

**Questions?** Check `OLLAMA_VOICE_PIPELINE_DOCUMENTATION_INDEX.md` for navigation.

**Let's go! 🎤🤖**

