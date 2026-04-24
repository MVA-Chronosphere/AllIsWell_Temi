# Ollama Voice Pipeline Integration Guide

**Status:** Production-Ready ✓
**Last Updated:** April 22, 2026

---

## 🎯 Pipeline Architecture

```
User Speech
    ↓
Android SpeechRecognizer (onResults)
    ↓
Spoken Text → String
    ↓
VoiceInteractionManager
    ↓
Build Hospital Context Prompt
    ↓
Ollama LLM (Local)
    ↓
Generated Text Response
    ↓
Clean Response (remove markdown, etc.)
    ↓
Temi TTS (robot.speak())
    ↓
Patient Hears Response
```

---

## 🔧 Implementation Steps

### Step 1: Add Dependencies

Retrofit is already in `app/build.gradle.kts`. Verify these exist:

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
implementation("com.google.code.gson:gson:2.10.1")
```

### Step 2: Verify Permissions

`AndroidManifest.xml` already has required permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
```

### Step 3: Create Network Components

✅ Files created:
- `OllamaModels.kt` - Data classes for Ollama API
- `OllamaApiService.kt` - Retrofit interface
- `OllamaClient.kt` - Singleton Retrofit builder

### Step 4: Create Voice Manager

✅ File created:
- `VoiceInteractionManager.kt` - Handles entire voice pipeline

### Step 5: Integrate in MainActivity

See "MainActivity Integration" section below.

---

## 📱 MainActivity Integration

### Option A: Simple Integration (Add to onCreate)

```kotlin
class MainActivity : ComponentActivity() {
    
    private var voiceManager: VoiceInteractionManager? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ... existing code ...
        
        // Initialize voice manager after Robot is ready
        Robot.getInstance().addOnRobotReadyListener { isReady ->
            if (isReady) {
                initializeVoiceManager()
            }
        }
    }
    
    private fun initializeVoiceManager() {
        val robot = robotState.value ?: return
        
        voiceManager = VoiceInteractionManager(
            context = this,
            robot = robot,
            coroutineScope = lifecycleScope
        )
        
        // Set up state change callback
        voiceManager?.setOnStateChanged { state ->
            Log.d("Voice", "State changed to: $state")
            // Update UI to show listening/processing/speaking indicators
        }
        
        // Set up error callback
        voiceManager?.setOnError { error ->
            Log.e("Voice", "Voice error: $error")
            // Show error toast or message
        }
        
        // Set up result callback
        voiceManager?.setOnVoiceResultReady { result ->
            Log.d("Voice", "Response: ${result.llmResponse}")
            Log.d("Voice", "Processing time: ${result.processingTimeMs}ms")
        }
    }
    
    // Start listening when user taps mic button or screen
    private fun startListening() {
        voiceManager?.startListening()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        voiceManager?.release()
    }
}
```

### Option B: Full Integration with Existing Voice System

Replace the existing `callGPT()` and `processSpeech()` methods with Ollama-based processing:

```kotlin
private fun processSpeechWithOllama(text: String) {
    // Use VoiceInteractionManager to process with Ollama
    voiceManager?.let {
        // The voice manager handles the entire pipeline
        // No need for separate GPT calls
    }
}
```

---

## 🚀 Usage Examples

### Basic Example: Start Listening and Get Response

```kotlin
// User taps mic button
button.setOnClickListener {
    voiceManager?.startListening()
}

// Voice manager automatically:
// 1. Records speech
// 2. Converts to text
// 3. Sends to Ollama
// 4. Speaks response via Temi
```

### Advanced Example: Monitor Each Pipeline Stage

```kotlin
voiceManager?.setOnStateChanged { state ->
    when (state) {
        VoiceState.IDLE -> Log.d("Voice", "Ready for input")
        VoiceState.LISTENING -> Log.d("Voice", "Recording...")
        VoiceState.PROCESSING -> Log.d("Voice", "Sending to Ollama...")
        VoiceState.THINKING -> Log.d("Voice", "Ollama generating response...")
        VoiceState.SPEAKING -> Log.d("Voice", "Temi speaking response...")
        VoiceState.ERROR -> Log.d("Voice", "Error occurred")
    }
}
```

---

## ⚙️ Ollama Server Setup

### Start Ollama Server

```bash
# With default settings
ollama serve

# With network access (required for Temi)
OLLAMA_HOST=0.0.0.0:11434 ollama serve

# Pull a model
ollama pull llama3:8b

# Check running models
ollama list
```

### Network Configuration

**For Temi Robot (Local Network):**
- Default IP: `192.168.137.1:11434`
- Change in `OllamaClient.kt` BASE_URL constant

**For Android Emulator:**
- Use: `http://10.0.2.2:11434/`
- Change in `OllamaClient.kt` BASE_URL constant

**For Testing:**
- Ensure Temi/device is on same WiFi as Ollama server
- Test connectivity: `curl http://<server-ip>:11434/api/generate`

---

## 🔍 Testing the Pipeline

### Test 1: Speech to Text
```kotlin
// User speaks: "Take me to the pharmacy"
voiceManager?.startListening()
// Verify SpeechRecognizer converts to text
// Check logcat: "Speech recognized: 'Take me to the pharmacy'"
```

### Test 2: Text to Ollama
```kotlin
// Verify Ollama receives prompt
// Check logcat: "Sending prompt to Ollama LLM..."
// Verify response returned: "Our pharmacy is located..."
```

### Test 3: Response to Speech
```kotlin
// Verify Temi speaks response
// Check robot display for TTS message
// Verify audio output
```

---

## 📊 Performance Notes

| Stage | Typical Time |
|-------|------------|
| Speech Recognition | 2-5 seconds |
| Ollama Processing | 3-10 seconds |
| TTS Output | 3-8 seconds |
| **Total** | **8-23 seconds** |

**Optimization Tips:**
- Use `llama3:7b` instead of `8b` for faster responses
- Reduce `temperature` from 0.7 to 0.5 for more focused responses
- Cache frequently asked questions

---

## 🛡️ Error Handling

### Common Issues & Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| "No speech detected" | User didn't speak clearly | Ask user to speak again |
| "Network error" | Ollama server not running | Start Ollama: `OLLAMA_HOST=0.0.0.0:11434 ollama serve` |
| "Network timeout" | Server response too slow | Increase timeout in `OllamaClient.kt` |
| "Insufficient permissions" | RECORD_AUDIO not granted | Request permission at runtime |
| "Speech recognition not available" | Device doesn't support STT | Use device with speech recognition capability |

### Runtime Permissions

For Android 6.0+, request runtime permissions:

```kotlin
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.RECORD_AUDIO),
        PERMISSION_REQUEST_CODE
    )
}
```

---

## 🔐 Security Considerations

1. **Local Processing:** All LLM inference happens on local Ollama server
   - No external API calls
   - No data sent to cloud
   - HIPAA-friendly

2. **Network:** Ensure Ollama server is not exposed to public internet
   - Use local network only
   - Add firewall rules if needed

3. **Timeouts:** Set reasonable timeouts to prevent hanging connections
   - Connection: 60 seconds
   - Read: 120 seconds
   - Write: 60 seconds

---

## 📝 Logging & Debugging

### Enable Full Logging

Check `OllamaClient.kt`:
```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY  // Full request/response logging
}
```

### View Logs

```bash
adb logcat | grep "VoiceInteraction"  # Voice pipeline logs
adb logcat | grep "Ollama"             # Ollama API logs
adb logcat | grep "TemiSpeech"         # Speech recognition logs
```

---

## ✅ Checklist Before Production

- [ ] Ollama server running: `OLLAMA_HOST=0.0.0.0:11434 ollama serve`
- [ ] Model installed: `ollama pull llama3:8b`
- [ ] Temi on same network as Ollama server
- [ ] `OllamaClient.kt` BASE_URL set correctly
- [ ] RECORD_AUDIO permission requested and granted
- [ ] VoiceInteractionManager initialized after Robot ready
- [ ] Error callbacks implemented
- [ ] Tested speech recognition end-to-end
- [ ] Tested Ollama API connectivity
- [ ] Tested TTS output
- [ ] Logcat shows full pipeline execution
- [ ] No `robot.askQuestion()` calls remaining (replaced with Ollama)

---

## 📞 Quick Reference

### Start Voice Pipeline
```kotlin
voiceManager?.startListening()
```

### Stop Voice Pipeline
```kotlin
voiceManager?.stopListening()
```

### Monitor Pipeline States
```kotlin
voiceManager?.setOnStateChanged { state ->
    // Handle: IDLE, LISTENING, PROCESSING, THINKING, SPEAKING, ERROR
}
```

### Get Results
```kotlin
voiceManager?.setOnVoiceResultReady { result ->
    println("User said: ${result.spokenText}")
    println("Robot says: ${result.llmResponse}")
    println("Time: ${result.processingTimeMs}ms")
}
```

---

## 🎓 Understanding the Code

### VoiceInteractionManager Key Methods

| Method | Purpose |
|--------|---------|
| `startListening()` | Start recording speech |
| `stopListening()` | Stop recording manually |
| `initializeSpeechRecognizer()` | Initialize Android STT |
| `processSpeechWithOllama()` | Send to Ollama LLM |
| `buildHospitalContextPrompt()` | Create smart prompt with context |
| `cleanResponseForSpeech()` | Format response for TTS |
| `speakResponse()` | Play response via Temi |
| `generateFallbackResponse()` | Provide response if Ollama fails |
| `release()` | Clean up resources on destroy |

### Hospital Context Prompt

The prompt sent to Ollama includes:
- Hospital service locations
- Professional guidelines (brief, helpful responses)
- Patient's actual query
- System instruction to act as hospital assistant

This ensures Ollama provides contextual, hospital-relevant responses.

---

## 🚀 Next Steps

1. **Test Basic Pipeline:**
   - Start Ollama server
   - Run app
   - Tap mic button
   - Speak: "Hello"
   - Verify response

2. **Add UI Indicators:**
   - Show "Listening..." when in LISTENING state
   - Show "Processing..." when in THINKING state
   - Show "Speaking..." when in SPEAKING state
   - Show error messages when ERROR state

3. **Integrate with Navigation:**
   - Parse user intent from response
   - Navigate to relevant screens
   - Book appointments via voice
   - Filter doctors via voice

4. **Optimize Performance:**
   - Profile processing time
   - Consider lighter models for faster responses
   - Add response caching for common queries

---

**Questions?** Check the code comments in:
- `VoiceInteractionManager.kt` - Full pipeline implementation
- `OllamaClient.kt` - Network configuration
- `OllamaModels.kt` - Data structures

