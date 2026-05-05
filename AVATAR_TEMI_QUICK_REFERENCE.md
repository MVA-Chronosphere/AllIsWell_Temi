# Avatar + Temi TTS - Quick Reference Card

**Print me and keep handy! 📋**

---

## 1. File Checklist ✓

```
Required Assets:
✓ app/src/main/assets/avatar-view.html
✓ app/src/main/assets/models/indian_doctor_lipsync.glb
✓ app/src/main/assets/headaudio/headaudio.min.mjs
✓ app/src/main/assets/headaudio/headworklet.min.mjs
✓ app/src/main/assets/headaudio/model-en-mixed.bin

Code Files Created:
✓ AvatarController.kt          (utils/)
✓ AvatarWebViewComponent.kt    (ui/components/)
✓ WavAudioFormat.kt            (utils/)
```

---

## 2. Quick API Reference

### Initialize
```kotlin
val avatarController = AvatarController(webView, lifecycleScope)
avatarController.checkReadiness()
```

### Play Audio
```kotlin
avatarController.playTemiAudioBytes(
    audioBytes,           // ByteArray
    "audio/wav",          // MIME type
    3.5f,                 // Duration in seconds
    "[]"                  // Rhubarb cues (JSON)
)
```

### Control Avatar
```kotlin
avatarController.wave()           // Wave gesture
avatarController.stopSpeech()     // Stop + reset mouth
avatarController.setViseme(...)   // Manual viseme test
if (avatarController.isReady())   // Check status
```

---

## 3. Asset Paths

| What | Android Path | WebView Path |
|-----|------|------|
| Avatar Page | `app/src/main/assets/avatar-view.html` | `https://appassets.androidplatform.net/assets/avatar-view.html` |
| GLB Model | `app/src/main/assets/models/indian_doctor_lipsync.glb` | `/assets/models/indian_doctor_lipsync.glb` |
| HeadAudio Lib | `app/src/main/assets/headaudio/*.mjs` | `/assets/headaudio/*.mjs` |

---

## 4. JavaScript Bridge

Available on `window.TemiInterface`:

| Method | Purpose |
|--------|---------|
| `playAudio(uri, cues)` | Play audio with lip-sync |
| `stop()` | Stop speech, reset mouth |
| `wave()` | Wave gesture |
| `isReady()` | Check if ready |
| `getStatus()` | Get detailed status |

---

## 5. Temi TTS Hook (MainActivity.kt)

```kotlin
override fun onTtsStatusChanged(ttsRequest: TtsRequest?) {
    when (ttsRequest?.status) {
        TtsRequest.Status.STARTED -> {
            // Start avatar (wave on first response)
            avatarController?.wave()
        }
        TtsRequest.Status.COMPLETED -> {
            // Stop avatar
            avatarController?.stopSpeech()
        }
        else -> {}
    }
}
```

---

## 6. Compose Integration

```kotlin
AvatarWebViewComponent(
    modifier = Modifier.fillMaxSize(),
    onWebViewReady = { webView ->
        avatarController = AvatarController(webView, lifecycleScope)
        avatarController?.checkReadiness()
    }
)
```

---

## 7. Debugging

```bash
# Filter logs
adb logcat | grep -E "(AvatarController|HeadAudio|Gesture)"

# Open DevTools
chrome://inspect/#devices
```

---

## 8. Troubleshooting Quick Fixes

| Issue | Fix |
|-------|-----|
| "Asset not found" | Check files exist in `app/src/main/assets/` |
| "HeadAudio init failed" | HeadAudio files missing? OK, fallback works |
| "No sound" | Enable user gesture or check AudioContext |
| "Mouth not moving" | Verify morph targets in GLB model |
| "WebView blank" | Check Logcat for JS errors, enable DevTools |

---

## 9. One-Minute Setup

1. **Copy files to assets:**
   - GLB → `app/src/main/assets/models/`
   - HTML → `app/src/main/assets/`
   - HeadAudio → `app/src/main/assets/headaudio/`

2. **Add code files:**
   - `AvatarController.kt` → `utils/`
   - `AvatarWebViewComponent.kt` → `ui/components/`
   - `WavAudioFormat.kt` → `utils/`

3. **In MainActivity:**
   ```kotlin
   private var avatarController: AvatarController? = null
   
   override fun onTtsStatusChanged(ttsRequest: TtsRequest?) {
       when (ttsRequest?.status) {
           TtsRequest.Status.COMPLETED -> avatarController?.stopSpeech()
           else -> {}
       }
   }
   ```

4. **In your screen Composable:**
   ```kotlin
   AvatarWebViewComponent(
       modifier = Modifier.fillMaxSize(),
       onWebViewReady = { webView ->
           avatarController = AvatarController(webView, lifecycleScope)
           avatarController?.checkReadiness()
       }
   )
   ```

5. **Test:** Build, deploy, speak!

---

## 10. Performance Checklist

- [ ] HeadAudio runs off main thread (AudioWorklet)
- [ ] Base64 encoding only done once per audio clip
- [ ] stopSpeech() called immediately on user input
- [ ] WebView memory < 70 MB
- [ ] Animation frame rate 60 FPS

---

## 11. Production Deployment

- [ ] Disable Chrome DevTools in release build
- [ ] Verify Manifest has INTERNET + RECORD_AUDIO permissions
- [ ] Test on real Temi robot (not just emulator)
- [ ] Monitor Logcat for memory leaks
- [ ] Test with 20+ consecutive audio clips

---

## 12. Viseme Reference (Oculus)

| Viseme | Sounds | Example |
|--------|--------|---------|
| `viseme_sil` | Silence | (rest) |
| `viseme_aa` | Ah | "**a**pple" |
| `viseme_E` | Eh | "**e**nd" |
| `viseme_I` | Ee | "k**ee**p" |
| `viseme_O` | Oh | "f**o**rk" |
| `viseme_U` | Oo | "br**oo**m" |
| `viseme_PP` | M/B/P | "**m**om", "**b**at", "**p**at" |
| `viseme_FF` | F/V | "**f**un", "**v**ine" |
| `viseme_TH` | Th/D | "**th**is", "**d**ental" |
| `viseme_DD` | T/D retroflex | "**t**ap", "re**d**" |
| `viseme_SS` | S/Z | "**s**it", "**z**oo" |
| `viseme_kk` | K/G | "**k**ing", "**g**o" |
| `viseme_CH` | Ch/J/Sh | "**ch**air", "**sh**ow" |
| `viseme_nn` | N/L | "**n**ine", "**l**ove" |
| `viseme_RR` | R | "**r**un" |

---

**Last Updated:** May 2026  
**Status:** ✅ Production Ready  
**Questions?** Check `AVATAR_TEMI_INTEGRATION_GUIDE.md`

