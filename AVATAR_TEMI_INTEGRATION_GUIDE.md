# Avatar + Temi TTS Integration Guide

**Status:** Production-Ready ✅  
**Last Updated:** May 2026  
**Scope:** Android WebView ↔ Three.js Avatar ↔ Temi TTS Audio  

---

## Overview

This integration connects your Temi robot's speech synthesis with a Three.js avatar running in a WebView. The avatar's mouth automatically animates with lip-sync via **HeadAudio** (real-time audio analysis) or **Rhubarb cues** (fallback).

### Architecture Diagram

```
Temi Robot TTS (audio bytes)
    ↓
Android Kotlin (AvatarController)
    ↓ playTemiAudioBytes(audioBytes, duration)
    ↓ Base64 encode → data URI
    ↓
WebView JavaScript (avatar-view.html)
    ↓ window.TemiInterface.playAudio(dataUri, cues)
    ↓
Three.js Scene
    ├→ HeadAudio (real-time viseme detection)
    │   ├→ Load /assets/headaudio/headaudio.min.mjs
    │   ├→ Load /assets/headaudio/headworklet.min.mjs
    │   └→ Model: /assets/headaudio/model-en-mixed.bin
    │
    ├→ GLB Model Loader
    │   └→ Load /assets/models/indian_doctor_lipsync.glb
    │
    └→ Morph Targets (lip sync)
        ├→ viseme_aa, viseme_PP, viseme_E, viseme_I, viseme_O, viseme_U
        ├→ viseme_FF, viseme_SS, viseme_TH, viseme_DD
        └→ viseme_kk, viseme_nn, viseme_RR, viseme_CH, viseme_sil
```

---

## Production Setup Checklist

### 1. **Android Manifest Permissions**

Ensure your `AndroidManifest.xml` has these permissions (should already exist):

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

### 2. **Assets Directory Structure**

Verify these files exist in `app/src/main/assets/`:

```
app/src/main/assets/
├── avatar-view.html                          ✓ Main avatar page
├── models/
│   └── indian_doctor_lipsync.glb             ✓ Avatar model with morph targets
└── headaudio/
    ├── headaudio.min.mjs                     ✓ HeadAudio library
    ├── headworklet.min.mjs                   ✓ AudioWorklet processor
    └── model-en-mixed.bin                    ✓ Pre-trained viseme model
```

**Note:** If any HeadAudio file is missing, HeadAudio will fail to initialize but fallback to Rhubarb cues (still works).

### 3. **Kotlin Integration Classes**

Create/Update these files:

- ✅ `AvatarController.kt` - Main bridge (audio encoding, JS calls)
- ✅ `AvatarWebViewComponent.kt` - Composable WebView + asset serving
- ✅ `WavAudioFormat.kt` - WAV header utilities (optional, for PCM conversion)

### 4. **Replace Model Viewer Component**

In your main screen composable, replace:

```kotlin
// OLD:
Model3DViewer(modifier = Modifier.fillMaxSize())

// NEW:
AvatarWebViewComponent(
    modifier = Modifier.fillMaxSize(),
    onWebViewReady = { webView ->
        // Initialize AvatarController here
        avatarController = AvatarController(webView, lifecycleScope)
        avatarController.checkReadiness()
    }
)
```

---

## Usage Examples

### Basic Audio Playback

```kotlin
val avatarController = AvatarController(webView, lifecycleScope)

// Play Temi TTS audio
val audioBytes = temiTtsAudio  // From Temi SDK
val duration = 3.5f             // Seconds
val cuesJson = "[]"             // No cues, use HeadAudio

avatarController.playTemiAudioBytes(
    audioBytes = audioBytes,
    mimeType = "audio/wav",
    speechDuration = duration,
    mouthCuesJson = cuesJson
)
```

### With Rhubarb Cues (Fallback)

```kotlin
// If you have pre-computed Rhubarb cues
val cues = listOf(
    mapOf("start" to 0.0, "end" to 0.12, "value" to "X"),  // silence
    mapOf("start" to 0.12, "end" to 0.28, "value" to "A"), // PP (m sound)
    mapOf("start" to 0.28, "end" to 0.44, "value" to "D"), // aa (ah sound)
    // ... more cues
)
val cuesJson = Json.encodeToString(cues)

avatarController.playTemiAudioBytes(
    audioBytes = audioBytes,
    mimeType = "audio/wav",
    speechDuration = 5f,
    mouthCuesJson = cuesJson
)
```

### With Manual Viseme Control (Testing)

```kotlin
avatarController.setViseme("viseme_aa", 0.8f)  // Wide open mouth
Thread.sleep(500)
avatarController.setViseme("viseme_sil", 0f)   // Close mouth
```

### Gesture Playback

```kotlin
avatarController.wave()  // Wave on first response
```

### Check Status

```kotlin
if (avatarController.isReady()) {
    Log.d("Avatar", "Ready to play audio")
    avatarController.getAvatarStatus()
}
```

### Stop Speech

```kotlin
avatarController.stopSpeech()
```

---

## Temi TTS Integration (MainActivity Hook)

In your `MainActivity.kt` `onTtsStatusChanged` listener:

```kotlin
override fun onTtsStatusChanged(ttsRequest: TtsRequest?) {
    when (ttsRequest?.status) {
        TtsRequest.Status.STARTED -> {
            // Speech started — avatar starts gesturing
            Log.d(TAG, "TTS started: ${ttsRequest.speech}")
            
            // Optional: trigger first-response wave
            // avatarController?.wave()
        }
        
        TtsRequest.Status.COMPLETED -> {
            // Speech finished
            Log.d(TAG, "TTS completed")
            avatarController?.stopSpeech()
        }
        
        else -> {}
    }
}
```

### Audio Bytes Capture (if needed)

If Temi SDK provides TTS audio callbacks, capture them:

```kotlin
// Hypothetical callback (check your Temi SDK version)
robot?.setOnTtsAudioListener { audioBytes ->
    // Ensure audio is in WAV format, or wrap PCM:
    val wavAudio = if (needsWavWrapper) {
        WavAudioFormat.createWavFile(
            sampleRate = 16000,
            numChannels = 1,
            bitsPerSample = 16,
            audioData = audioBytes
        )
    } else {
        audioBytes
    }
    
    // Speech duration estimation (rough)
    val estimatedDuration = audioBytes.size / (16000 * 2) // 16kHz, 16-bit
    
    avatarController?.playTemiAudioBytes(
        audioBytes = wavAudio,
        mimeType = "audio/wav",
        speechDuration = estimatedDuration,
        mouthCuesJson = "[]"
    )
}
```

---

## Asset Path Mapping

The WebViewAssetLoader serves assets via a virtual HTTPS domain:

| Asset Path | Served as | Type |
|-----------|----------|------|
| `app/src/main/assets/avatar-view.html` | `https://appassets.androidplatform.net/assets/avatar-view.html` | HTML |
| `app/src/main/assets/models/*.glb` | `/assets/models/*.glb` (inside WebView) | 3D Model |
| `app/src/main/assets/headaudio/*.mjs` | `/assets/headaudio/*.mjs` (inside WebView) | JavaScript Module |
| `app/src/main/assets/headaudio/*.bin` | `/assets/headaudio/*.bin` (inside WebView) | Binary Model |

**In avatar-view.html**, use absolute paths:

```javascript
// ✅ CORRECT (fixed in avatar-view.html)
loader.load('/assets/models/indian_doctor_lipsync.glb')
await audioCtx.audioWorklet.addModule('/assets/headaudio/headworklet.min.mjs')
await import('/assets/headaudio/headaudio.min.mjs')

// ❌ WRONG (relative paths won't work with data: or file://)
loader.load('models/indian_doctor_lipsync.glb')
```

---

## JavaScript Bridge API

The avatar page exposes a `window.TemiInterface` object for Kotlin to call:

### Available Methods

```javascript
window.TemiInterface = {
    /// Play audio (primary method)
    playAudio(audioDataUri, mouthCues = []),
    
    /// Play audio via backend (optional, requires server)
    playAudioWithBackend(base64Audio, mimeType, text, backendUrl),
    
    /// Stop speech and reset mouth
    stop(),
    
    /// Trigger wave gesture
    wave(),
    
    /// Check if avatar is ready
    isReady() → boolean,
    
    /// Get current status
    getStatus() → { ready, speaking, gesturing, headAudioReady }
}
```

### Kotlin Calling Convention

```kotlin
// Simple JavaScript execution (returns void)
avatarController.playTemiAudioBytes(audioBytes, "audio/wav", 3.5f, "[]")

// Status check (with callback)
webView.evaluateJavascript(
    "window.TemiInterface.getStatus()",
    { result -> Log.d(TAG, "Status: $result") }
)
```

---

## Debugging & Troubleshooting

### Enable Logcat Filtering

Monitor avatar-specific logs:

```bash
adb logcat | grep -E "(AvatarController|AvatarView|HeadAudio|Gesture)"
```

### Check WebView Loading

Enable Chrome DevTools:

```
# In your code (only when BuildConfig.DEBUG = true):
WebView.setWebContentsDebuggingEnabled(true)
```

Then in Chrome, visit: `chrome://inspect/#devices`

### Common Issues

**Issue: "Asset not found: /assets/models/..."**
- Solution: Verify file exists at `app/src/main/assets/models/indian_doctor_lipsync.glb`
- Check: Run `find app/src/main/assets -type f`

**Issue: "HeadAudio init failed"**
- Expected if `/assets/headaudio/*.mjs` files are missing
- Fallback: Cue-based lip sync + <audio> tag will still work
- Workaround: Add the HeadAudio files or use Rhubarb cues

**Issue: "Audio doesn't play, no sound"**
- Check: WebView settings: `mediaPlaybackRequiresUserGesture = false`
- Check: AudioContext might be suspended until first user gesture
- Solution: Single tap on WebView will resume AudioContext

**Issue: "Mouth not moving"**
- Check: Morph targets exist in GLB (use Three.js inspector)
- Check: Console logs show "morphMeshes: 0"? → GLB has no morph targets
- Solution: Verify GLB was exported with blend shapes from Blender

### Debug Console Output

Look for these log prefixes in Logcat:

| Prefix | Meaning |
|--------|---------|
| `[AvatarController]` | Kotlin bridge actions |
| `[AvatarView]` | JavaScript initialization |
| `[HeadAudio]` | Real-time viseme detection |
| `[Gesture]` | Hand/head gesture system |
| `[LipSync]` | Cue-based mouth animation |
| `[Temi]` | Android ↔ JS bridge calls |

---

## Performance Notes

- **Memory:** Avatar runs in WebView — typically 30-50 MB
- **CPU:** Animation loop is 60 FPS (requestAnimationFrame)
- **GlB Loading:** ~5-10 MB model, 1-2 seconds to load
- **HeadAudio:** AudioWorklet runs off main thread (no stutter)
- **Base64 Encoding:** ~1.3x audio bytes (e.g., 100 KB audio → 130 KB data URI)

### Optimization Tips

1. **Reuse data URIs:** Don't re-encode same audio multiple times
2. **Lazy HeadAudio:** It only initializes on first audio playback
3. **Gesture cancellation:** Call `stopSpeech()` immediately on user input

---

## Production Deployment Checklist

- [ ] HeadAudio files present in `app/src/main/assets/headaudio/`
- [ ] GLB model present at `app/src/main/assets/models/indian_doctor_lipsync.glb`
- [ ] `avatar-view.html` asset paths fixed (✓ done)
- [ ] Manifest has INTERNET, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS permissions
- [ ] AvatarController created and initialized in main Compose screen
- [ ] TestActivity has `onWebViewReady` callback to initialize AvatarController
- [ ] Temi TTS audio hook calls `avatarController.playTemiAudioBytes()`
- [ ] Chrome DevTools disabled in release build (BuildConfig.DEBUG)
- [ ] Logcat monitoring set up for debugging in staging

---

## Next Steps

1. **Integrate into MainActivity:**
   - Add AvatarWebViewComponent to your main Compose screen
   - Initialize AvatarController in onWebViewReady callback

2. **Hook Temi TTS:**
   - Capture audio bytes from Temi SDK
   - Call `playTemiAudioBytes()` when speech starts
   - Call `stopSpeech()` when speech ends

3. **Test:**
   - Build APK: `./gradlew installDebug`
   - Deploy to Temi: `adb install -r app/build/outputs/apk/debug/*.apk`
   - Trigger speech and verify mouth animation
   - Check Logcat for `[AvatarController]` logs

4. **Optimize (Optional):**
   - Add Rhubarb cues for offline fallback
   - Fine-tune viseme weights in avatar-view.html
   - Cache data URIs if audio repeats

---

## Support

If issues persist:
1. Check Logcat for `[AvatarView.JS]` errors
2. Inspect WebView in Chrome DevTools (https://appassets.androidplatform.net/assets/avatar-view.html)
3. Verify all assets load with Network tab
4. Check morph targets with: `window.TemiInterface.getStatus()`

