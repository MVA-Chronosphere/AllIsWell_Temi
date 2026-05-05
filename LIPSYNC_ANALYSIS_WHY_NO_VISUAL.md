# LIP SYNC ANALYSIS: WHY MOUTH ISN'T MOVING

## Problem Diagnosis

**Current State:**
```
Ollama Response → TtsLipSyncManager generates visemes → Logged to logcat → NOWHERE
```

**Evidence from logs:**
```
14:58:13.328 TtsLipSyncManager: ✅ Starting TTS lip sync with visemes
14:58:13.329 LIPSYNC: Started lip sync for chunk
14:58:13.408 TtsLipSyncManager: 📊 Viseme animation: 168 chars, 11200ms duration
14:58:13.499 LIPSYNC_VISEME: Viseme: viseme_PP, intensity: 0.857
14:58:13.581 LIPSYNC_VISEME: Viseme: viseme_sil, intensity: 0.0
...
14:58:24.184 TtsLipSyncManager: 🛑 Stopping TTS lip sync
```

**The logs show:**
- ✅ Visemes ARE being generated (111 frames)
- ✅ Character-to-viseme mapping IS working
- ✅ Duration estimation IS accurate (11.2 seconds for 169 chars)
- ❌ **Viseme data is NEVER applied to any visual element**

---

## Root Cause: No Visual Target

The `onVisemeUpdate` callback defined in MainActivity does nothing useful:

```kotlin
// MainActivity.kt:141
onVisemeUpdate = { viseme, intensity ->
    android.util.Log.d("LIPSYNC_VISEME", "Viseme: $viseme, intensity: $intensity")
    // ← ONLY LOGS! No visual update!
}
```

### What Should Be Happening

```
TtsLipSyncManager generates viseme (e.g., "viseme_PP")
        ↓
onVisemeUpdate callback receives it
        ↓
Apply to visual target:
  Option A: 3D Avatar (WebView with Three.js)
  Option B: 2D Animated Avatar (Compose UI)
  Option C: Robot's face layer (if Temi SDK exposes it)
        ↓
Mouth shape changes
```

### What IS Happening

```
TtsLipSyncManager generates viseme
        ↓
onVisemeUpdate receives it
        ↓
Logs to logcat
        ↓
🛑 NOTHING VISUAL HAPPENS
```

---

## Visual Components Available

### 1. **AvatarWebViewComponent** (Exists but Disconnected)
- File: `AvatarWebViewComponent.kt`
- Has method: `setViseme(visemeName, weight)`
- Purpose: Apply visemes to Three.js 3D avatar
- Status: **NOT INTEGRATED** with TtsLipSyncManager

### 2. **TemiMainScreen** (UI-based, No Avatar)
- File: `TemiMainScreen.kt`
- Pure Compose UI (buttons, cards, menus)
- No 3D avatar rendering
- Status: **No visual lip sync target**

### 3. **Robot's Internal Face** (Hardware)
- Temi SDK controls this
- No public API for lip sync
- Status: **Not exposed by SDK v1.137.1**

---

## Solutions

### SOLUTION 1: Connect AvatarController (If Avatar Screen Exists)
**File:** `MainActivity.kt`

Check if avatar is being displayed, connect visemes to it:

```kotlin
private var avatarController: AvatarController? = null

// In onCreate():
// If avatar screen is active:
if (hasAvatarComponent) {
    avatarController = AvatarController(webView, lifecycleScope)
}

// In onVisemeUpdate callback:
onVisemeUpdate = { viseme, intensity ->
    avatarController?.setViseme(viseme, intensity)
    Log.d("LIPSYNC", "Applied: $viseme @ $intensity")
}
```

---

### SOLUTION 2: Create Animated Avatar UI in Compose
**Create:** `AvatarAnimationComponent.kt`

```kotlin
@Composable
fun AnimatedAvatarMouth(viseme: String, intensity: Float) {
    // Map viseme to Compose shape parameters
    when (viseme) {
        "viseme_sil" -> DrawNeutralMouth()  // Closed
        "viseme_aa", "viseme_O" -> DrawOpenMouth(intensity)  // Wide open
        "viseme_I", "viseme_E" -> DrawSmileMouth(intensity)  // Smile
        "viseme_PP", "viseme_U" -> DrawRoundedMouth(intensity)  // Round
        "viseme_FF" -> DrawFMouth(intensity)  // F shape
        else -> DrawNeutralMouth()
    }
}
```

Then connect in MainActivity:

```kotlin
onVisemeUpdate = { viseme, intensity ->
    mouthVisemeState.value = Pair(viseme, intensity)
}
```

---

### SOLUTION 3: Simple 2D Mouth Animation (Quick Fix)
**File:** `MainActivity.kt`

Add a simple visual mouth indicator synchronized with visemes:

```kotlin
// In setContent():
Box(modifier = Modifier.fillMaxSize()) {
    // Existing UI...
    
    // Lip Sync Indicator (bottom right corner)
    if (ttsLipSyncManager.isActive()) {
        LipSyncVisualizer(
            viseme = currentViseme.value,
            intensity = currentIntensity.value,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
```

---

## Why Logs Show Activity But Nothing Visual Happens

The logging is **correct**. The problem is earlier in the chain:

```
User speaks "what are you doing"
        ↓
Ollama generates: "I'm a helpful assistant for All Is Well Hospital..."
        ↓
MainActivity.safeSpeak() calls robot.speak(TtsRequest)
        ↓
✅ Robot speaks via hardware TTS (user hears it)
        ↓
TtsLipSyncManager.startLipSync() starts viseme estimation
        ✅ Generates: viseme_PP, viseme_E, viseme_I, etc.
        ✅ Logs each one to logcat
        ❌ **Never sends to visual component**
        ↓
Robot's mouth has no instruction to move
        ↓
🔴 **Mouth stays static even though speech is playing**
```

---

## Why Temi Robot's Mouth Isn't Moving

Temi SDK's `Robot.speak(TtsRequest)` **does NOT expose lip sync control**. The robot generates speech internally, but the Android app has:
- **NO access to audio bytes** (to analyze in real-time)
- **NO viseme callback** from Temi SDK
- **NO control over face layer** for mouth animation

### Temi Robot's Built-in Lip Sync

The physical Temi robot **does have its own lip sync**, but it likely:
1. Uses pre-recorded mouth animations keyed to common phrases
2. Or, uses basic phoneme detection on the robot's processor
3. Or, doesn't work at all (many users report no visible mouth movement)

**The app cannot control Temi's mouth directly** unless there's a hidden API.

---

## Recommended Fix: Hybrid Approach

### Step 1: Verify if Avatar screen is used
```bash
grep -n "AvatarWebViewComponent\|avatar-view.html" app/src/main/**/*.kt
```

If YES → Use SOLUTION 1 (connect to WebView avatar)  
If NO → Use SOLUTION 2 or 3 (create UI-based mouth)

### Step 2: Implement Visual Target
Choose the best option for your architecture:
- If using WebView avatar: **SOLUTION 1** (AvatarController integration)
- If Compose-only UI: **SOLUTION 2** (AnimatedMouthComponent)
- If emergency fix needed: **SOLUTION 3** (Simple indicator)

### Step 3: Connect Viseme Pipeline
```kotlin
// Instead of just logging:
onVisemeUpdate = { viseme, intensity ->
    // Send to visual component (not just logcat)
    visualComponent.updateMouth(viseme, intensity)
    Log.d("LIPSYNC", "Applied viseme to UI")
}
```

---

## Code Fix (Select One Option)

### Option A: If Avatar Exists (Use AvatarController)

```kotlin
// MainActivity.kt line 141
ttsLipSyncManager = TtsLipSyncManager(
    coroutineScope = lifecycleScope,
    onVisemeUpdate = { viseme, intensity ->
        // FIX: Apply to avatar instead of just logging
        avatarController?.setViseme(viseme, intensity)
        Log.d("LIPSYNC_VISEME", "Applied to avatar: $viseme @ $intensity")
    }
)
```

### Option B: If UI-Only (Create Mouth Component)

Create `MouthAnimationComponent.kt`:

```kotlin
@Composable
fun MouthShape(viseme: String, intensity: Float) {
    val mouthOpenAmount = when (viseme) {
        "viseme_sil" -> 0f  // Closed
        "viseme_aa", "viseme_O" -> 0.9f * intensity
        "viseme_I", "viseme_E" -> 0.7f * intensity
        "viseme_PP", "viseme_U" -> 0.6f * intensity
        else -> 0.3f * intensity
    }
    
    Canvas(modifier = Modifier.size(60.dp)) {
        // Draw mouth based on mouthOpenAmount
        drawOval(
            color = Color.Gray,
            size = Size(40f, mouthOpenAmount * 20f)
        )
    }
}
```

Then add to setContent():

```kotlin
if (currentViseme.value.isNotEmpty()) {
    MouthShape(currentViseme.value, currentIntensity.value)
}
```

---

## Summary

**Why mouth isn't moving:**
- Visemes ARE being generated ✅
- Visemes ARE NOT being sent to any visual component ❌
- `MainActivity.onVisemeUpdate` only logs data, doesn't apply it ❌

**Fix:**
- Connect `onVisemeUpdate` to AvatarController OR
- Create Compose-based mouth animation component OR
- Add simple visual feedback of lip sync state

**Estimated Time to Fix:** 30-60 minutes (depends on chosen approach)


