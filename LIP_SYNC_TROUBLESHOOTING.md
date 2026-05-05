# Lip-Sync Troubleshooting Guide

**Last Updated:** May 2026  
**Status:** Production Diagnostic Mode Enabled

---

## Quick Diagnosis Checklist

If lip-sync is not working, check these in order:

### 1. **Check Browser Console (Chrome DevTools)**

Open Console and trigger avatar audio. Look for these log messages:

```
[Temi] playAudioWithLipSync received
[Temi] Starting gesture, duration: X.XXs
[HeadAudio] Not initialized yet, initializing...
[HeadAudio] Initialized successfully
[HeadAudio] Fetching audio from: data:audio/wav;base64,...
[HeadAudio] Audio decoded: duration=X.XXs, channels=1
[HeadAudio] Audio playback started
```

### 2. **If you see HeadAudio errors:**

```
[HeadAudio] Failed to load headworklet: 404
→ Solution: Copy headaudio/*.mjs files to app/src/main/assets/headaudio/

[HeadAudio] Failed to load model: 404
→ Solution: Copy model-en-mixed.bin to app/src/main/assets/headaudio/

[HeadAudio] Audio decode failed: NotSupportedError
→ Solution: Ensure audio is WAV format (not MP3). Use WavAudioFormat.kt helper.
```

### 3. **If you see this instead:**

```
[Temi] HeadAudio failed, using cue-based fallback
[Temi] Starting cue-based lip sync with N cues
[Temi] Playing audio via fallback Audio element
```

**This is OK!** The system auto-falls back to cue-based lip-sync. Mouth should still animate but less naturally.

---

## Diagnosis Priority

| Priority | Check | What to Look For |
|----------|-------|-----------------|
| 1 | HeadAudio Files | Are `.mjs` and `.bin` files in assets/? |
| 2 | Audio Format | Is audio WAV or MP3? (WAV only for HeadAudio) |
| 3 | Morph Targets | Does GLB have viseme_* morph targets? |
| 4 | Console Logs | Are there JS errors or missing module errors? |
| 5 | AudioContext | Is AudioContext suspended? (needs user gesture) |

---

## Common Solutions

### Audio Not Playing at All

**Symptom:** Nothing happens when you send audio to avatar

**Diagnosis:**
```javascript
// In browser console, type:
window.TemiInterface.getStatus()
// Should show: {ready: true, headAudioReady: true, ...}
```

**Fixes:**
1. Click anywhere on the WebView to resume AudioContext
2. Check avatar is displaying (getStatus.ready should be true)
3. Verify audio data URI is valid: `data:audio/wav;base64,/IQAEA...`

### Mouth Moves But No Audio

**Symptom:** Mouth animates but you don't hear sound

**Causes:**
- Device muted
- Volume set to 0
- Audio output disabled in WebSettings

**Fix in AvatarWebViewComponent.kt:**
```kotlin
settings.apply {
    // ...existing code...
    mediaPlaybackRequiresUserGesture = false
}
```

### Mouth Doesn't Move (HeadAudio Failed)

**Symptom:** Audio plays but mouth stays still

**Check for in console:**
```
[Temi] HeadAudio failed, using cue-based fallback
```

**Solutions:**

1. **If HeadAudio files missing:**
   - Verify: `app/src/main/assets/headaudio/headaudio.min.mjs` exists
   - Verify: `app/src/main/assets/headaudio/headworklet.min.mjs` exists
   - Verify: `app/src/main/assets/headaudio/model-en-mixed.bin` exists

2. **If audio decode fails:**
   - Ensure audio is WAV format (16-bit PCM, 16kHz mono)
   - Use `WavAudioFormat.createWavFile()` to wrap raw PCM
   - In AvatarController.kt:
     ```kotlin
     val wavAudio = WavAudioFormat.createWavFile(
         sampleRate = 16000,
         numChannels = 1,
         bitsPerSample = 16,
         audioData = audioBytes
     )
     avatarController.playTemiAudioBytes(wavAudio, "audio/wav", duration)
     ```

3. **If morph targets missing:**
   - Check console for: `[AvatarView] morphMeshes: 0`
   - GLB file must have Oculus viseme blend shapes:
     - viseme_aa, viseme_E, viseme_I, viseme_O, viseme_U
     - viseme_PP, viseme_FF, viseme_SS, viseme_TH, viseme_DD
     - viseme_kk, viseme_nn, viseme_RR, viseme_CH, viseme_sil
   - Export GLB from Blender: Make sure blend shapes are included!

---

## Enhanced Logging for Debugging

The avatar-view.html now includes detailed diagnostic logs. Check these steps:

### Step 1: Open Chrome DevTools
```
Right-click on avatar → Inspect → Console tab
```

### Step 2: Look for HeadAudio initialization
```
[HeadAudio] Initialized successfully      ← Good!
                OR
[HeadAudio] Init FAILED - [error message] ← Problem found!
```

### Step 3: Trigger audio playback

Call from AvatarController:
```kotlin
avatarController.playTemiAudioBytes(audioBytes, "audio/wav", 3.5f, "[]")
```

### Step 4: Monitor console output

Expected sequence:
```
[Temi] playAudioWithLipSync received
[Temi] Starting gesture, duration: 3.50s
[HeadAudio] Fetching audio from: data:audio/wav;base64,...
[HeadAudio] Audio decoded: duration=3.50s, channels=1
[HeadAudio] Audio playback started
(audio plays + mouth animates)
[HeadAudio] Audio playback ended
```

Or fallback sequence (if HeadAudio unavailable):
```
[Temi] playAudioWithLipSync received
[Temi] Starting gesture, duration: 3.50s
[HeadAudio] Not ready (...), using fallback
[Temi] HeadAudio failed, using cue-based fallback
[Temi] Starting cue-based lip sync with 0 cues
[Temi] Playing audio via fallback Audio element
```

---

## Cue-Based Fallback (When HeadAudio Unavailable)

If HeadAudio initialization fails but you have Rhubarb cues:

```kotlin
// With cues for better fallback lip-sync:
val ruhubarbCues = """[
    {"start": 0.0, "end": 0.12, "value": "X"},
    {"start": 0.12, "end": 0.28, "value": "A"},
    ...
]"""

avatarController.playTemiAudioBytes(
    audioBytes = audioBytes,
    mimeType = "audio/wav",
    speechDuration = 3.5f,
    mouthCuesJson = rhubarbCues
)
```

---

## Deployment Checklist

Before deploying to production:

- [ ] HeadAudio files present in `app/src/main/assets/headaudio/`
  - [ ] headaudio.min.mjs
  - [ ] headworklet.min.mjs
  - [ ] model-en-mixed.bin
- [ ] GLB model at `app/src/main/assets/models/indian_doctor_lipsync.glb`
- [ ] GLB exported with viseme blend shapes
- [ ] Audio format: WAV (16-bit PCM, 16kHz mono)
- [ ] Tts audio wrapped in WAV header if raw PCM
- [ ] Chrome DevTools shows no 404 errors
- [ ] Console shows successful HeadAudio initialization
- [ ] Mouth animates when audio plays
- [ ] Fallback (cue-based) works if HeadAudio unavailable
- [ ] No javascript errors in console

---

## Testing Avatar Without Temi

Use the debug console in avatar-view.html (when loaded standalone):

```javascript
// In browser console:

// Test HeadAudio status
window.TemiInterface.getStatus()

// Force manual viseme (for testing)
window.setViseme('viseme_aa', 0.8)  // Open mouth
window.setViseme('viseme_PP', 0.6)  // Lips pressed

// Test audio with cues
window.TemiInterface.playAudio(
    'data:audio/wav;base64,UklGRi4A...',
    [{start: 0, end: 0.5, value: 'A'}, ...])
```

---

## Performance Notes

- **HeadAudio:** Runs in AudioWorklet (off main thread, no stutter)
- **Morph Targets:** Applied every frame (~60 FPS)
- **Memory:** ~40-60 MB for WebView + Three.js
- **Latency:** <50ms from audio start to mouth animation

---

## Next Steps

If lip-sync still doesn't work after checking above:

1. **Collect debug logs** from Chrome console
2. **Check HeadAudio initialization** for specific error
3. **Verify asset files** are in correct directories
4. **Test with fallback** (cue-based should work if assets missing)
5. **Check GLB file** has morph targets (Blender export settings)

Email logs + screenshot of console → support for further debugging.

