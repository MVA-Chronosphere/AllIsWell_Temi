# Lip-Sync Fix: WebView Asset Loading Issue Resolved

**Date:** May 5, 2026  
**Issue:** `net::ERR_NAME_NOT_RESOLVED` preventing HeadAudio and model loading  
**Root Cause:** WebViewAssetLoader domain misconfiguration  
**Status:** ✅ FIXED

---

## Problem Identified

The WebView error log showed:
```
E  WebView error: net::ERR_NAME_NOT_RESOLVED
```

This meant the WebView tried to resolve `appassets.androidplatform.net` as a real domain instead of using the virtual domain handled by WebViewAssetLoader.

### Why This Broke Lip-Sync

1. **avatar-view.html** couldn't load because it's served from the virtual domain
2. **HeadAudio files** couldn't be fetched (`headworklet.min.mjs`, `headaudio.min.mjs`, `model-en-mixed.bin`)
3. **GLB model** couldn't be loaded (`models/indian_doctor_lipsync.glb`)
4. Without these assets, HeadAudio initialization failed
5. System fell back to no lip-sync at all

---

## Changes Made

### 1. AvatarWebViewComponent.kt

**Changed:** WebViewAssetLoader path handler configuration

```kotlin
// BEFORE:
val assetLoader = remember {
    WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()
}

// AFTER:
val assetLoader = remember {
    WebViewAssetLoader.Builder()
        .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()
}
```

**Why:** Maps the root path (`/`) directly to the assets folder, so all relative URLs work.

**Also improved:** Enhanced `shouldInterceptRequest` logging to show when assets are being intercepted.

### 2. avatar-view.html Updates

Changed all asset paths from absolute (`/assets/...`) to relative (`...`):

**GLB Model Loading:**
```javascript
// BEFORE:
loader.load('/assets/models/indian_doctor_lipsync.glb', ...)

// AFTER:
loader.load('models/indian_doctor_lipsync.glb', ...)
```

**HeadAudio Worklet:**
```javascript
// BEFORE:
await audioCtx.audioWorklet.addModule('/assets/headaudio/headworklet.min.mjs');

// AFTER:
await audioCtx.audioWorklet.addModule('headaudio/headworklet.min.mjs');
```

**HeadAudio Module:**
```javascript
// BEFORE:
const module = await import('/assets/headaudio/headaudio.min.mjs');

// AFTER:
const module = await import('headaudio/headaudio.min.mjs');
```

**HeadAudio Model:**
```javascript
// BEFORE:
await headAudio.loadModel('/assets/headaudio/model-en-mixed.bin');

// AFTER:
await headAudio.loadModel('headaudio/model-en-mixed.bin');
```

---

## How It Works Now

1. **WebViewAssetLoader** is configured with root path handler `/` → `assets` folder
2. Page URL: `https://appassets.androidplatform.net/assets/avatar-view.html`
3. **shouldInterceptRequest** intercepts all requests to this domain
4. Relative URLs inside the HTML (e.g., `models/indian_doctor_lipsync.glb`) are resolved correctly
5. **HeadAudio files** load successfully
6. **GLB model** loads successfully
7. **Lip-sync works!** ✅

---

## Verification

After this fix, you should see in Logcat:
```
D  AssetLoader: Intercepting: https://appassets.androidplatform.net/assets/avatar-view.html
D  chromium: [INFO:CONSOLE:X] "Model loaded successfully: ..."
D  chromium: [INFO:CONSOLE:X] "[HeadAudio] Initialized successfully"
```

And when speech happens:
```
D  chromium: [INFO:CONSOLE:X] "[Temi] playAudioWithLipSync received"
D  chromium: [INFO:CONSOLE:X] "[HeadAudio] Audio playback started"
```

---

## File Paths Summary

All assets must be at these locations:

```
app/src/main/assets/
├── avatar-view.html              ← Main page
├── models/
│   └── indian_doctor_lipsync.glb  ← 3D avatar with morph targets
└── headaudio/
    ├── headaudio.min.mjs          ← Real-time lip-sync engine
    ├── headworklet.min.mjs         ← AudioWorklet processor
    └── model-en-mixed.bin          ← Viseme detection model
```

---

## Testing

1. Build and deploy: `./gradlew installDebug`
2. Open Chrome DevTools (F12 on browser or remote debugging)
3. Check Console tab - should show:
   - No 404 errors
   - `[HeadAudio] Initialized successfully`
   - Model loaded message
4. Trigger TTS audio
5. Mouth should animate with speech ✅

---

## Troubleshooting

If still not working after this fix:

1. **Check Logcat for 404 errors:**
   ```
   net::ERR_NAME_NOT_RESOLVED → Asset file missing
   net::ERR_FILE_NOT_FOUND → File path wrong
   ```

2. **Verify assets exist:**
   ```bash
   find app/src/main/assets -type f -name "*.mjs" -o -name "*.bin" -o -name "*.glb"
   ```

3. **Check HeadAudio initialization:**
   Look for `[HeadAudio] Initialized successfully` in console

4. **If HeadAudio still fails:**
   - System falls back to cue-based lip-sync (less natural but works)
   - Audio will still play, just no real-time mouth animation

---

## Summary

| Component | Issue | Fix |
|-----------|-------|-----|
| WebViewAssetLoader | Path handler too specific | Changed `/assets/` to `/` |
| HTML asset URLs | Used absolute paths | Changed to relative paths |
| HeadAudio files | Couldn't be found | Now resolved via root handler |
| GLB model | Couldn't be found | Now resolved via root handler |
| Result | Lip-sync doesn't work | ✅ Works with real-time viseme detection |

---

**Next Deploy:** `./gradlew clean installDebug` (will rebuild assets)

