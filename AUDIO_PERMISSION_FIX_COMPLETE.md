# Audio Permission Fix - Android 12+ Runtime Permission Handling

## Problem Statement
The application was experiencing AudioRecord initialization failures on Android 12+ (API 31+) due to missing RECORD_AUDIO runtime permission validation:

```
E AudioRecord: createRecord returned error -1
E LipSyncManager: ❌ Failed to initialize AudioRecord: AudioRecord failed to initialize (state=0). 
                  Ensure RECORD_AUDIO permission is granted in app settings.
```

## Root Cause Analysis
1. **Permission Manifest vs Runtime:** Android 12+ requires BOTH manifest declarations AND runtime permission grants
2. **Race Condition:** The app was initiating voice listening before confirming runtime permission grant
3. **Async Permission Grant:** `ActivityResultContracts.RequestMultiplePermissions()` is asynchronous, but audio initialization was immediat
4. **LipSyncManager Missing Context:** Original code didn't have context to check permission status

## Solution Overview

### 1. **LipSyncManager.kt** - Added Runtime Permission Checking
**File:** `/app/src/main/java/com/example/alliswelltemi/utils/LipSyncManager.kt`

**Changes:**
- Added `Context` parameter to constructor (needed for permission checks)
- Added `hasRecordAudioPermission()` function to check if RECORD_AUDIO is granted
- Added explicit permission check BEFORE AudioRecord initialization
- Enhanced error messages with actionable user instructions

**Code:**
```kotlin
class LipSyncManager(
    private val context: Context,  // NEW: Added for permission checks
    private val coroutineScope: CoroutineScope,
    private val onMouthUpdate: (jawOpen: Float, mouthOpen: Float) -> Unit
) {
    private fun hasRecordAudioPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+: Check runtime permission
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 11 and below: Manifest permission is sufficient
            true
        }
    }

    private fun initializeAudioCapture() {
        try {
            // CRITICAL FIX: Check permission before attempting audio capture
            if (!hasRecordAudioPermission()) {
                throw SecurityException(
                    "RECORD_AUDIO permission not granted. Please enable microphone permission in app settings."
                )
            }
            // ... rest of initialization ...
        }
    }
}
```

**Benefits:**
- Prevents AudioRecord from attempting to initialize without permission
- Provides clear error messages for permission debugging
- Graceful failure with actionable user instructions
- Supports all Android versions (checks are conditional)

---

### 2. **MainActivity.kt** - Coordinated Permission Request & Voice Start

**File:** `/app/src/main/java/com/example/alliswelltemi/MainActivity.kt`

#### Changes to `permissionRequestLauncher`:
```kotlin
private val permissionRequestLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val allPermissionsGranted = permissions.values.all { it }
    if (allPermissionsGranted) {
        android.util.Log.d("MainActivity", "✅ All required permissions granted")
        // NEW: After permissions are granted, START voice listening
        handler.postDelayed({
            voiceInteractionManager?.startListening()
            android.util.Log.d("VOICE_PIPELINE", "Voice listening started after permissions granted")
        }, 500)
    } else {
        // NEW: Notify user about missing permissions
        val deniedPermissions = permissions.filter { !it.value }.keys.joinToString(", ")
        android.widget.Toast.makeText(this, "Missing permissions: $deniedPermissions\nVoice features may not work.", 
            android.widget.Toast.LENGTH_LONG).show()
    }
}
```

**Changes to `onRobotReady()` callback:**
```kotlin
override fun onRobotReady(isReady: Boolean) {
    if (isReady) {
        // ... existing robot setup ...
        
        try {
            voiceInteractionManager = VoiceInteractionManager(...)
            
            // NEW: Check permission BEFORE attempting to start listening
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+: Check if RECORD_AUDIO permission is granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                    == PackageManager.PERMISSION_GRANTED) {
                    // Permission already granted (e.g., from previous session)
                    handler.postDelayed({
                        voiceInteractionManager?.startListening()
                        android.util.Log.d("VOICE_PIPELINE", "Voice listening started (permission confirmed)")
                    }, 500)
                } else {
                    // Permission not yet granted, will be requested by requestNecessaryPermissions()
                    // VoiceInteractionManager will start once permissionRequestLauncher callback fires
                    android.util.Log.w("VOICE_PIPELINE", "Waiting for RECORD_AUDIO permission grant...")
                }
            } else {
                // Android 11 and below: Manifest permission is sufficient
                handler.postDelayed({
                    voiceInteractionManager?.startListening()
                    android.util.Log.d("VOICE_PIPELINE", "Voice listening started (pre-Android 12)")
                }, 500)
            }
        } catch (e: Exception) {
            android.util.Log.e("TemiMain", "Failed to initialize VoiceInteractionManager", e)
        }
        
        this.isRobotReady.value = true
    }
}
```

**Key Improvements:**
- **Conditional Check:** Only on Android 12+, pre-Android 12 manifests are sufficient
- **Two-Path Permission Handling:**
  1. If permission already granted (from previous session) → start listening immediately
  2. If not granted → wait for `requestNecessaryPermissions()` callback
- **Clear Logging:** Logs indicate which path is taken for debugging
- **Toast Feedback:** User is informed if permissions were denied

---

### 3. **VoiceInteractionManager.kt** - Permission Validation in `startListening()`

**File:** `/app/src/main/java/com/example/alliswelltemi/utils/VoiceInteractionManager.kt`

**New Imports:**
```kotlin
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
```

**Changes to `startListening()` function:**
```kotlin
fun startListening() {
    if (isListening) {
        Log.d(TAG, "Already listening - ignoring duplicate request")
        return
    }

    // NEW: CRITICAL: Check RECORD_AUDIO permission on Android 12+ before starting speech recognition
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            Log.e(
                TAG,
                "🔒 RECORD_AUDIO permission not granted on Android 12+. " +
                "Speech recognition will not work. User must grant microphone permission in app settings."
            )
            updateState(VoiceState.ERROR)
            onError?.invoke("Microphone permission required. Please enable in app settings.")
            return  // Exit early without attempting to initialize
        }
    }

    try {
        if (speechRecognizer == null) {
            initializeSpeechRecognizer()
        }
        
        isListening = true
        updateState(VoiceState.LISTENING)
        // ... rest of initialization ...
    } catch (e: Exception) {
        Log.e(TAG, "Error starting listening", e)
        isListening = false
        updateState(VoiceState.ERROR)
        onError?.invoke("Failed to start listening: ${e.message}")
    }
}
```

**Benefits:**
- **Defense in Depth:** Even if MainActivity's check passes, this validates again
- **Fail-Fast:** Prevents SpeechRecognizer.startListening() from silently failing
- **User Feedback:** Clear error callback sent to UI consumers
- **Graceful Degradation:** System doesn't crash, just disables voice features

---

## Permission Request Flow (Android 12+)

```
MainActivity.onCreate()
    ↓
requestNecessaryPermissions()
    ├─ Checks if permission already granted
    └─ If not, launches permissionRequestLauncher.launch()
         ↓
    Android System Shows Permission Dialog
         ↓
    User clicks "Allow" or "Deny"
         ↓
    permissionRequestLauncher callback fires
         ├─ If allowed: Calls voiceInteractionManager?.startListening()
         └─ If denied: Shows Toast, voice features disabled
```

**Alternatively (if permission already granted from previous session):**
```
MainActivity.onCreate()
    ↓
requestNecessaryPermissions()
    └─ Permission already granted (skips dialog)
         ↓
Robot.getInstance().addOnRobotReadyListener()
    ↓
onRobotReady() callback fires
    ├─ Checks ContextCompat.checkSelfPermission()
    └─ Permission confirmed, starts voiceInteractionManager?.startListening()
```

---

## Files Modified

1. **LipSyncManager.kt** (app/src/main/java/com/example/alliswelltemi/utils/)
   - Added Context parameter
   - Added hasRecordAudioPermission() function
   - Added permission validation in initializeAudioCapture()

2. **MainActivity.kt** (app/src/main/java/com/example/alliswelltemi/)
   - Updated permissionRequestLauncher callback to start voice listening
   - Updated onRobotReady() to check permissions before starting listening
   - Added conditional Android 12+ checks

3. **VoiceInteractionManager.kt** (app/src/main/java/com/example/alliswelltemi/utils/)
   - Added permission imports
   - Added permission validation in startListening()

---

## Testing Recommendations

### Test 1: Fresh Install (No Permissions Granted)
1. Uninstall app
2. Reinstall APK
3. Launch app
4. Should show permission dialog for RECORD_AUDIO
5. Grant permission
6. Voice interaction should work immediately

### Test 2: Permission Already Granted
1. Install app
2. Assume permission already granted
3. Launch app
4. Should NOT show permission dialog
5. Voice interaction should work immediately

### Test 3: Permission Denied
1. Install app
2. Deny permission when dialog appears
3. Voice features should be disabled
4. Toast should show: "Missing permissions: ... Voice features may not work."

### Test 4: Pre-Android 12 Devices
1. Test on Android 11 or below
2. Should NOT request runtime permission (manifest is sufficient)
3. Voice interaction should work immediately

### Test 5: Permission Revoked After Grant
1. Grant permission initially
2. Go to Settings > Apps > AlliswellTemi > Permissions > Microphone
3. Revoke permission
4. Relaunch app
5. Should trigger permission request again

---

## Logcat Diagnostic Messages

**Permission Already Granted:**
```
✅ All permissions already granted (from requestNecessaryPermissions)
Voice listening started (RECORD_AUDIO permission confirmed)
```

**Permission Request Dialog Shown:**
```
Requesting runtime permissions: [android.permission.RECORD_AUDIO]
Voice listening started after permissions granted (from permissionRequestLauncher callback)
```

**Permission Denied:**
```
⚠️ Some permissions were denied
🔒 RECORD_AUDIO permission not granted on Android 12+. Speech recognition will not work.
```

**Stale AudioRecord Error (Before Fix):**
```
(This error should no longer appear after the fix is deployed)
E AudioRecord: createRecord returned error -1
E LipSyncManager: ❌ Failed to initialize AudioRecord
```

---

## Backwards Compatibility

- ✅ Android 11 and below: Uses manifest permissions only (no runtime checks needed)
- ✅ Android 12+ without previous permission: Shows dialog as expected
- ✅ Android 12+ with previous permission: Skips dialog, works immediately
- ✅ All voice features gracefully degrade if permission is missing

---

## Related Configuration

**AndroidManifest.xml** (already includes required permissions):
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

**build.gradle.kts** (ensure target API 34+ for Android 12+ features):
```kotlin
compileSdk = 34
targetSdk = 34
minSdk = 26  // Supports Android 8+
```

---

## Summary

This fix implements **production-grade runtime permission handling** that:
1. **Prevents AudioRecord Crashes** on Android 12+ by validating permissions before initialization
2. **Coordinates Permission Grants** with voice listening startup
3. **Provides Clear User Feedback** if permissions are missing
4. **Maintains Backwards Compatibility** with older Android versions
5. **Implements Defense in Depth** with permission checks at multiple layers

The fix ensures the hospital kiosk application's voice interaction features work reliably across all supported Android versions while respecting privacy and security requirements.

